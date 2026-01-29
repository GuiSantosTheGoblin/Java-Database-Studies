package DAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connection.DBIntegrityException;
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

    public static Seller findByName(Connection conn, String name) throws MySQLException {
        // Returns a Seller object if found by name, otherwise returns null.

        if (name == null || name.trim().isEmpty()) {
            throw new MySQLException("Seller's name cannot be null or empty.");
        }

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
            WHERE s.Name = ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);

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

    public static List<Seller> showAll(Connection conn) throws MySQLException {
        // Returns a list of all sellers in the database.

        List<Seller> sellers = new ArrayList<>();

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
            ORDER BY s.Name
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
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

                    sellers.add(seller);
                }

                return sellers;
            } catch (SQLException e) {
                throw new MySQLException("Could not execute the query.", e);
            }

        } catch (SQLException e) {
            throw new MySQLException("Could not prepare the statement.", e);
        }
    }

    public static Seller insert(Connection conn, Seller seller) throws MySQLException {
        // Inserts a new seller into the database and returns the seller with the generated id.

        if (seller == null) {
            throw new MySQLException("Seller cannot be null.");
        }

        if (seller.getName() == null || seller.getName().trim().isEmpty()) {
            throw new MySQLException("Seller's name cannot be null or empty.");
        }

        if (seller.getEmail() == null || seller.getEmail().trim().isEmpty()) {
            throw new MySQLException("Seller's email cannot be null or empty.");
        }

        if (seller.getDepartment() == null || seller.getDepartment().getId() == null || seller.getDepartment().getId() < 1) {
            throw new MySQLException("Seller must be associated with a valid department.");
        }

        String sql = """
            INSERT 
            INTO seller (
                Name, 
                Email, 
                BirthDate, 
                BaseSalary, 
                DepartmentId) 
            VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, seller.getName());
            ps.setString(2, seller.getEmail());
            ps.setDate(3, new java.sql.Date(seller.getBirthDate().getTime()));
            ps.setBigDecimal(4, seller.getBaseSalary());
            ps.setInt(5, seller.getDepartment().getId());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Seller(
                        keys.getInt(1),
                        seller.getName(),
                        seller.getEmail(),
                        seller.getBirthDate(),
                        seller.getBaseSalary(),
                        seller.getDepartment()
                    );
                }
                throw new MySQLException("No ID generated for seller.");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062 || "23000".equals(e.getSQLState())) {
                throw new MySQLException("Seller already exists with this email.", e);
            }
            throw new MySQLException("Could not insert seller.", e);
        }
    }

    public static Seller updateById(Connection conn, int id, Seller seller) throws MySQLException {
        // Updates a seller's information based on its id number.

        if (seller == null) {
            throw new MySQLException("Seller cannot be null.");
        }

        if (id < 1) {
            throw new MySQLException("Invalid seller id.");
        }

        if (seller.getName() == null || seller.getName().trim().isEmpty()) {
            throw new MySQLException("Seller's name cannot be null or empty.");
        }

        if (seller.getEmail() == null || seller.getEmail().trim().isEmpty()) {
            throw new MySQLException("Seller's email cannot be null or empty.");
        }

        if (seller.getDepartment() == null || seller.getDepartment().getId() == null || seller.getDepartment().getId() < 1) {
            throw new MySQLException("Seller must be associated with a valid department.");
        }

        String sql = """
            UPDATE seller 
            SET 
                Name = ?, 
                Email = ?, 
                BirthDate = ?, 
                BaseSalary = ?, 
                DepartmentId = ? 
            WHERE Id = ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, seller.getName());
            ps.setString(2, seller.getEmail());
            ps.setDate(3, new java.sql.Date(seller.getBirthDate().getTime()));
            ps.setBigDecimal(4, seller.getBaseSalary());
            ps.setInt(5, seller.getDepartment().getId());
            ps.setInt(6, id);

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new MySQLException("Seller not found.");
            }

            return new Seller(
                id,
                seller.getName(),
                seller.getEmail(),
                seller.getBirthDate(),
                seller.getBaseSalary(),
                seller.getDepartment()
            );
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062 || "23000".equals(e.getSQLState())) {
                throw new MySQLException("Seller already exists with this email.", e);
            }
            throw new MySQLException("Could not update seller.", e);
        }
    }

    public static boolean deleteById(Connection conn, int id) throws MySQLException, DBIntegrityException {
        // Deletes a seller based on its id number and returns a boolean indicating success.

        if (id < 1) {
            throw new MySQLException("Invalid seller id.");
        }

        String sql = "DELETE FROM seller WHERE Id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451 || "23000".equals(e.getSQLState())) {
                throw new DBIntegrityException("Cannot delete seller: it is associated with one or more records.");
            }

            throw new MySQLException("Could not delete seller.", e);
        }
    }

    public static boolean deleteByName(Connection conn, String name) throws MySQLException, DBIntegrityException {
        // Deletes a seller based on its name.

        if (name == null || name.trim().isEmpty()) {
            throw new MySQLException("Seller's name cannot be null or empty.");
        }

        String sql = "DELETE FROM seller WHERE Name = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451 || "23000".equals(e.getSQLState())) {
                throw new DBIntegrityException("Cannot delete seller: it is associated with one or more records.");
            }
            throw new MySQLException("Could not delete seller.", e);
        }
    }
}