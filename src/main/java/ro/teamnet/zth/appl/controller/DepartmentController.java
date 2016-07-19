package ro.teamnet.zth.appl.controller;

import ro.teamnet.zth.api.annotations.InjectService;
import ro.teamnet.zth.api.annotations.MyController;
import ro.teamnet.zth.api.annotations.MyRequestMethod;
import ro.teamnet.zth.api.annotations.MyRequestParam;
import ro.teamnet.zth.appl.domain.Department;
import ro.teamnet.zth.appl.service.DepartmentService;

import java.util.List;

@MyController(urlPath = "/departments")
public class DepartmentController {

    public final DepartmentService departmentService;

    @InjectService
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @MyRequestMethod(urlPath = "/all")
    public List<Department> getAllDepartments() {
        return departmentService.findAll();
    }

    @MyRequestMethod(urlPath = "/one")
    public Department getOneDepartment(@MyRequestParam(name = "id") Long departmentId) {
        return departmentService.findOne(departmentId);
    }
}
