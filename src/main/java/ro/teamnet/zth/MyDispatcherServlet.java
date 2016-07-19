package ro.teamnet.zth;

import org.codehaus.jackson.map.ObjectMapper;
import ro.teamnet.zth.api.annotations.*;
import ro.teamnet.zth.fmk.AnnotationScanUtils;
import ro.teamnet.zth.fmk.MethodAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class MyDispatcherServlet extends HttpServlet {

    private final Map<String, MethodAttributes> allowedMethods = new HashMap<>();

    private final Set<Class> allServiceImplementations = new HashSet<>();

    @Override
    public void init() throws ServletException {
        try {
            loadControllers(AnnotationScanUtils.getClasses("ro.teamnet.zth.appl.controller"));
            loadServices(AnnotationScanUtils.getClasses("ro.teamnet.zth.appl.service"));
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    private void loadControllers(Iterable<Class> classes) {
        for (Class aClass : classes) {
            if (aClass.isAnnotationPresent(MyController.class)) {
                MyController controllerAnnotation = (MyController) aClass.getAnnotation(MyController.class);
                String controllerUrlPath = controllerAnnotation.urlPath();
                Method[] controllerMethods = aClass.getMethods();
                for (Method method : controllerMethods) {
                    if (method.isAnnotationPresent(MyRequestMethod.class)) {
                        MyRequestMethod annotation = method.getAnnotation(MyRequestMethod.class);
                        String methodUrlPath = annotation.urlPath();
                        String methodType = annotation.methodType();

                        MethodAttributes methodAttributes = new MethodAttributes();
                        methodAttributes.setControllerClass(aClass.getName());
                        methodAttributes.setMethodName(method.getName());
                        methodAttributes.setMethodType(methodType);
                        methodAttributes.setParameterTypes(method.getParameterTypes());

                        String urlPath = controllerUrlPath + methodUrlPath;

                        final String key = createUniqueKeyForRequest(methodType, urlPath);

                        allowedMethods.put(key, methodAttributes);

                        System.out.println(key + " : " + methodAttributes);
                    }
                }
            }
        }
    }

    /**
     * Metoda creeaza o cheie unica prin care se vor identifica request-urile venite din browser.
     * Se va tine cont atat de url, cat si de metoda HTTP din request.
     *
     * @param methodType - tipul metodei HTTP
     * @param urlPath    - path-ul din request
     * @return o cheie unica pentru a identifica request-ul
     */
    private String createUniqueKeyForRequest(String methodType, String urlPath) {
        return methodType + " " + urlPath;
    }


    private void loadServices(Iterable<Class> serviceClasses) {
        for (Class serviceClass : serviceClasses) {
            if (serviceClass.isAnnotationPresent(MyService.class)) {
                allServiceImplementations.add(serviceClass);
            }
        }
    }

    private Object getServiceImplementation(Class serviceInterface) throws IllegalAccessException, InstantiationException {
        for (Class serviceImplementation : allServiceImplementations) {
            if (serviceInterface.isAssignableFrom(serviceImplementation))
                return serviceImplementation.newInstance();
        }
        throw new RuntimeException("No implementation found for service interface " + serviceInterface);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatchReply(req, resp, "GET");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatchReply(req, resp, "POST");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatchReply(req, resp, "DELETE");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatchReply(req, resp, "PUT");
    }

    private void dispatchReply(HttpServletRequest req, HttpServletResponse resp, String methodType) {
        try {
            Object resultToDisplay = dispatch(req, methodType);
            reply(resp, resultToDisplay);
        } catch (Exception e) {
            try {
                sendExceptionError(e, resp);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private Object dispatch(HttpServletRequest req, String methodType) throws Exception {
        if (req.getPathInfo() == null) {
            blockRequest();
        }
        // Analizam request-ul si compunem cheia unica dupa care vom cauta informatii in registrul de controllere.
        // Tinem cont atat de path, cat si de metoda HTTP.
        final String uniqueKeyForRequest = createUniqueKeyForRequest(methodType, req.getPathInfo());

        MethodAttributes methodAttributes = allowedMethods.get(uniqueKeyForRequest);

        if (methodAttributes == null) {
            blockRequest();
        }

        // Obtinem clasa controllerului si construim o instanta. Daca in controller se injecteaza un service,
        // construim o instanta pentru acel service cu implementarea disponibila.
        Class<?> controllerClass = Class.forName(methodAttributes.getControllerClass());
        Constructor<?>[] constructors = controllerClass.getConstructors();
        Object controllerInstance = null;
        for (Constructor<?> constructor : constructors) {
            Class<?>[] constructorParameterTypes = constructor.getParameterTypes();
            if (constructorParameterTypes.length == 0) {
                controllerInstance = controllerClass.newInstance();
                break;
            }
            if (constructor.isAnnotationPresent(InjectService.class) && constructorParameterTypes.length == 1) {
                Class<?> serviceInterface = constructorParameterTypes[0];
                Object serviceImplementation = getServiceImplementation(serviceInterface);
                controllerInstance = constructor.newInstance(serviceImplementation);
                break;
            }
        }

        if (controllerInstance == null) {
            blockRequest();
        }

        // Obtinem metoda ce va trebui invocata.
        Method method = controllerClass.getMethod(methodAttributes.getMethodName(), methodAttributes.getParameterTypes());

        // Pregatim parametrii necesari pentru invocarea metodei.
        List<Object> methodParamsValues = new ArrayList<>();
        // Obtinem o descriere a parametrilor asteptati de metoda.
        final Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            Class<?> parameterType = parameter.getType();
            if (parameter.isAnnotationPresent(MyRequestParam.class)) {
                // Extragem parametrii transmisi in url pe baza numelui specificat in adnotarea MyRequestParam.
                MyRequestParam annotation = parameter.getAnnotation(MyRequestParam.class);
                String requestParameterStringValue = req.getParameter(annotation.name());
                // Convertim string-ul citit din url intr-un obiect de tipul asteptat de metoda din controller.
                Object requestParameterObject = parameterType.equals(String.class) ? requestParameterStringValue
                        : new ObjectMapper().readValue(requestParameterStringValue, parameterType);
                methodParamsValues.add(requestParameterObject);
            } else if (parameter.isAnnotationPresent(MyRequestObject.class)) {
                // Construim parametrii transmisi in body-ul request-ului HTTP.
                BufferedReader requestBodyReader = req.getReader();
                // Convertim body-ul requestului intr-un obiect de tipul asteptat de metoda din controller.
                Object requestBodyObject = new ObjectMapper().readValue(requestBodyReader, parameterType);
                methodParamsValues.add(requestBodyObject);
            }
        }
        // Invocam metoda cu parametrii extrasi din request.
        return method.invoke(controllerInstance, methodParamsValues.toArray());
    }

    private void blockRequest() {
        throw new RuntimeException("Request not allowed!");
    }

    private void reply(HttpServletResponse resp, Object responseToDisplay) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        final String responseAsString = objectMapper.writeValueAsString(responseToDisplay);
        resp.getWriter().print(responseAsString);
    }

    private void sendExceptionError(Exception e, HttpServletResponse resp) throws IOException {
        resp.getWriter().print(e.getMessage());
    }
}
