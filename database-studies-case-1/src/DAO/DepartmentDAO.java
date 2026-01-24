package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import connection.MySQLException;
import entities.Department;

public class DepartmentDAO {

    public static Department findByName(Connection conn, String name) throws MySQLException {
        // Returns a Department object if found by name, otherwise returns null.

        if (name == null || name.trim().isEmpty()) {
            throw new MySQLException("Department's name cannot be null or empty.");
        }
        
        String sql = """
            SELECT 
                Id, 
                Name 
            FROM department 
            WHERE Name = ?;
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()){
                if (!rs.next()){                    
                    return null;
                }
                
                Department department = new Department(
                        rs.getInt("Id"),
                        rs.getString("Name")
                );

                return department;
            } catch (SQLException e) {
                throw new MySQLException("Could not execute the query.", e);
            }
        } catch (SQLException e) {
            throw new MySQLException("Could not prepare the statement.", e);
        }
    }

    public static Department findById(Connection conn, Integer id) throws MySQLException {
        // Returns a Department object if found by id, otherwise returns null.
 
        if (id < 1) {
            throw new MySQLException("Invalid department id.");
        }

        String sql = """
            SELECT 
                Id, 
                Name 
            FROM department 
            WHERE Id = ?;
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()){
                if (!rs.next()){                    
                    return null;
                }
                
                Department department = new Department(
                        rs.getInt("Id"),
                        rs.getString("Name")
                );

                return department;
            } catch (SQLException e) {
                throw new MySQLException("Could not execute the query.", e);
            }
        } catch (SQLException e) {
            throw new MySQLException("Could not prepare the statement.", e);
        }
    }

    public static List<Department> showAll(Connection conn) throws MySQLException {
        // Returns a list of all departments in the database.

        List<Department> departments = new ArrayList<>();

        String sql = """
            SELECT 
                Id, 
                Name 
            FROM department 
            WHERE Id = ?";
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            try (ResultSet rs = stmt.executeQuery()){
                while (rs.next()) {
                    departments.add(new Department(
                        rs.getInt("Id"),
                        rs.getString("Name")
                    ));
                }

                return departments;
            } catch (SQLException e) {
                throw new MySQLException("Could not execute the query.", e);
            }
        } catch (SQLException e) {
            throw new MySQLException("Could not prepare the statement.", e);
        }
    }

    public static Department insert(Connection conn, Department department) throws MySQLException {
        // Inserts a new department into the database and returns the department with the generated id.

        if (department == null) {
            throw new MySQLException("Department cannot be null.");
        }

        if (department.getName() == null || department.getName().trim().isEmpty()) {
            throw new MySQLException("Department's name cannot be null or empty.");
        }

        String sql = "INSERT INTO department (Name) VALUES (?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, department.getName());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Department(keys.getInt(1), department.getName());
                }
                throw new MySQLException("No ID generated for department.");
            } 
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062 || "23000".equals(e.getSQLState())) {
                throw new MySQLException("Department already exists with this name.", e);
            }
            throw new MySQLException("Could not insert department.", e);
        }
    }
    
    public static int sellerCount(Connection conn, String departmentName) throws MySQLException {
        // Returns the number of sellers in a given department.
        // Returns -1 if department doesn't exist

        if (departmentName == null || departmentName.trim().isEmpty()){
            throw new MySQLException("Department name cannot be null or empty.");
        }

        String sql = """
            SELECT 
                COUNT(s.Id) AS SellerCount
            FROM department AS d
            LEFT JOIN seller AS s
            ON s.DepartmentId = d.Id
            WHERE d.Name = ?
            GROUP BY d.Id
            """;
                
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, departmentName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return -1;
                }
            
                return rs.getInt("SellerCount");
            } catch (SQLException e) {
                throw new MySQLException("Could not execute the query.", e);
            }
        } catch (SQLException e) {
            throw new MySQLException("Could not prepare the statement.", e);
        }
    }

    public static Map<String, Integer> sellerCountAll(Connection conn) throws MySQLException {
        // Returns a TreeMap of all departments and their respective seller counts.

        Map<String, Integer> result = new TreeMap<>();

        String sql = """
            SELECT 
                d.Name, 
                COUNT(s.Id) as SellerCount
            FROM department AS d
            LEFT JOIN seller AS s
            ON s.DepartmentId = d.Id
            GROUP BY d.Id
            """;
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            try (ResultSet rs = stmt.executeQuery()){
                while (rs.next()) {
                    String name = rs.getString("Name");
                    int count = rs.getInt("SellerCount");
                    result.put(name, count);
                }

                return result;
            } catch (SQLException e) {
                throw new MySQLException("Could not excecute the query.", e);
            }
        } catch (SQLException e) {
            throw new MySQLException("Could not prepare the statement.", e);
        } 
    } 

    public static Department updateById(Connection conn, int id, String newName) throws MySQLException {
        // Updates the department's name based on its id number.

        if (newName == null || newName.trim().isEmpty()) {
            throw new MySQLException("Department's name cannot be null or empty.");
        }

        if (id < 1) {
            throw new MySQLException("Invalid department id.");
        }

        String sql = "UPDATE department SET Name = ? WHERE Id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newName);
            stmt.setInt(2, id);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new MySQLException("Department not found.");
            }

            return new Department(id, newName);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062 || "23000".equals(e.getSQLState())) {
                throw new MySQLException("Department already exists with this name.", e);
            }
            throw new MySQLException("Could not update department.", e);
        }
    }

    public static Department updateByName(Connection conn, Department department, String newName) throws MySQLException {
        // Updates the department's name based on its name.
        
        if (department == null) {
            throw new MySQLException("Department cannot be null.");
        }

        if (department.getId() == null) {
            throw new MySQLException("Department's ID cannot be null.");
        }

        if (newName == null || newName.trim().isEmpty()) {
            throw new MySQLException("Department's name cannot be null or empty.");
        }

        String sql = "UPDATE department SET Name = ? WHERE Id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newName);
            stmt.setInt(2, department.getId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new MySQLException("Department not found.");
            }

            return new Department(department.getId(), newName);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062 || "23000".equals(e.getSQLState())) {
                throw new MySQLException("Department already exists with this name.", e);
            }
            throw new MySQLException("Could not update department.", e);
        }
    }

    public static boolean deleteById(Connection conn, int id) throws MySQLException {
        // Deletes a department based on its id number and returns a boolean indicating success.

        if (id < 1) {
            throw new MySQLException("Invalid department id.");
        }

        String sql = "DELETE FROM department WHERE Id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            return true;
        } catch (SQLException e) {
            throw new MySQLException("Could not prepare or execute the statement.", e);
        }
    }

    public static boolean deleteByName(Connection conn, String name) throws MySQLException {
        // Deletes a department based on its name.

        if (name == null || name.trim().isEmpty()) {
            throw new MySQLException("Department's name cannot be null or empty.");
        }

        String sql = "DELETE FROM department WHERE Name = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, name);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            return true;
        } catch (SQLException e) {
            throw new MySQLException("Could not prepare or execute the statement.", e);
        }
    }
}