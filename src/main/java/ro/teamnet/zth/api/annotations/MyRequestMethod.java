package ro.teamnet.zth.api.annotations;


import java.lang.annotation.*;

/**
 * Created by Oana.Mihai on 7/11/2016.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestMethod {
    String methodType() default "GET";

    String urlPath();
}
