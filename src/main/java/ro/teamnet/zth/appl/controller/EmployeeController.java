package ro.teamnet.zth.appl.controller;

import ro.teamnet.zth.api.annotations.*;
import ro.teamnet.zth.appl.domain.Employee;
import ro.teamnet.zth.appl.service.EmployeeService;

import java.util.List;

@MyController(urlPath = "/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @InjectService
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }


    @MyRequestMethod(urlPath = "/all")
    public List<Employee> getAllEmployees() {
        return employeeService.findAllEmployees();
    }

    @MyRequestMethod(urlPath = "/one")
    public Employee getOneEmployee(@MyRequestParam(name = "id") Long employeeId) {
        return employeeService.findOneEmployee(employeeId);
    }

    @MyRequestMethod(urlPath = "/one", methodType = "DELETE")
    public Boolean deleteOneEmployee(@MyRequestParam(name = "id") Long employeeId) {
        return employeeService.delete(employeeId);
    }

    @MyRequestMethod(urlPath = "/create", methodType = "POST")
    public Employee saveEmployee(@MyRequestObject Employee employee) {
        return employeeService.save(employee);
    }

    @MyRequestMethod(urlPath = "/edit", methodType = "PUT")
    public Employee updateEmployee(@MyRequestObject Employee employee) {
        return employeeService.update(employee);
    }

}
