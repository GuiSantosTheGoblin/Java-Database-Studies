package DAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connection.MySQLException;
import entities.Department;
import entities.Seller;

public class SellerDAO {
    public static Seller findById(Connection conn, Integer id) throws MySQLException {     
        String sql = """
            SELECT 
                s.Id AS SellerId,
                s.Name AS SellerName,
                s.Email,
                s.BirthDate,
                s.BaseSalary,
                d.Id AS DepId,
                d.Name AS DepName
            FROM seller s
            JOIN department d ON s.DepartmentId = d.Id
            WHERE s.Id = ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                Department department = new Department(
                    rs.getInt("DepId"),
                    rs.getString("DepName")
                );

                Seller seller = new Seller(
                    rs.getInt("SellerId"),
                    rs.getString("SellerName"),
                    rs.getString("Email"),
                    rs.getDate("BirthDate"),
                    rs.getBigDecimal("BaseSalary"),
                    department
                );

                return seller;
            } catch (SQLException e) {
                throw new MySQLException("Could not execute the query.", e);
            }

        } catch (SQLException e) {
            throw new MySQLException("Could not prepare the statement.", e);
        }
    }
}