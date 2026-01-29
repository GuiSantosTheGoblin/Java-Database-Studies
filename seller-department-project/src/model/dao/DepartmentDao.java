package model.dao;

import java.util.List;

import model.entities.Department;

public interface DepartmentDao {
	Department insert(Department obj);
	Department update(Department obj);
	boolean deleteById(Integer id);
	Department findById(Integer id);
	List<Department> findAll();
}