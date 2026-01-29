package model.dao.impl;

import java.util.List;

import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.DBException;

public class SellerDaoJDBC implements SellerDao {

    @Override
    public Seller insert(Seller obj) throws DBException {
        // TODO Auto-generated method stub
    }

    @Override
    public Seller update(Seller obj) throws DBException {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean deleteById(Integer id) throws DBException {
        // TODO Auto-generated method stub
    }

    @Override
    public Seller findById(Integer id) throws DBException {
        // TODO Auto-generated method stub
    }

    @Override
    public List<Seller> findAll() throws DBException {
        // TODO Auto-generated method stub
    }

    @Override
    public List<Seller> findByDepartment(Department department) throws DBException {
        // TODO Auto-generated method stub
    }
}
