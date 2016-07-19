package ro.teamnet.zth.appl.service;

import ro.teamnet.zth.api.annotations.MyService;
import ro.teamnet.zth.appl.dao.DepartmentDao;
import ro.teamnet.zth.appl.domain.Department;

import java.util.List;

/**
 * Created by Oana.Mihai on 7/15/2016.
 */
@MyService
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentDao departmentDao = new DepartmentDao();

    @Override
    public List<Department> findAll() {
        return departmentDao.findAllDepartments();
    }

    @Override
    public Department findOne(Long departmentId) {
        return departmentDao.findDepartmentById(departmentId);
    }
}
