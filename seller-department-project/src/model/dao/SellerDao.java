package model.dao;

import java.util.List;

import model.entities.Department;
import model.entities.Seller;

public interface SellerDao {
	Seller insert(Seller obj);
	Seller update(Seller obj);
	boolean deleteById(Integer id);
	Seller findById(Integer id);
	List<Seller> findAll();
	List<Seller> findByDepartment(Department department);
}