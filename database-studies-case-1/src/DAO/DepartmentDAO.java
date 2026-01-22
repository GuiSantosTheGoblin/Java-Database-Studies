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

        String sql = "SELECT * FROM department WHERE Name = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    Department department = new Department(
                        rs.getInt("Id"),
                        rs.getString("Name")
                    );
                    return department;
                }
                return null;
            } catch (SQLException e) {
                throw new MySQLException("Could not execute the query.", e);
            }
        } catch (SQLException e) {
            throw new MySQLException("Could not prepare the statement.", e);
        }
    }

    public static Department findById(Connection conn, Integer id) throws MySQLException {
        // Returns a Department object if found by id, otherwise returns null.

        String sql = "SELECT * FROM department WHERE Id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    Department department = new Department(
                        rs.getInt("Id"),
                        rs.getString("Name")
                    );
                    return department;
                }
                return null;
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

        String sql = "SELECT * FROM department";

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
        // Returns null if a department with the same name already exists.

        if (department == null) {
            throw new MySQLException("Department cannot be null.");
        }

        if (department.getName() == null || department.getName().trim().isEmpty()) {
            throw new MySQLException("Department name cannot be null or empty.");
        }

        if (findByName(conn, department.getName()) != null) {
            return null;
        }

        String sql = "INSERT INTO department (Name) VALUES (?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){
            stmt.setString(1, department.getName());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new MySQLException("Inserting department failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    department.setId(generatedKeys.getInt(1));
                } else {
                    throw new MySQLException("Inserting department failed, no ID obtained.");
                }
            }

            return department;
        } catch (SQLException e) {
            throw new MySQLException("Could not prepare or execute the statement.", e);
        }
    }
    
    public static int sellerCount(Connection conn, String departmentName) throws MySQLException {
        // Returns the number of sellers in a given department.
        // Returns -1 if department doesn't exist

        String sql = """
        SELECT COUNT(seller.Id) as SellerCount
        FROM department
        LEFT JOIN seller ON seller.DepartmentId = department.Id
        WHERE department.Name = ?
        GROUP BY department.Id
        """;
                
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, departmentName);
            try (ResultSet rs = stmt.executeQuery()){
                if (!rs.next()){
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
        SELECT department.Name, COUNT(seller.Id) as SellerCount
        FROM department
        LEFT JOIN seller ON seller.DepartmentId = department.Id
        GROUP BY department.Id
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

        if (findById(conn, id) == null) {
            throw new MySQLException("There is no department with id = " + id + ".");
        }
        
        String sql = "UPDATE department SET Name = ? WHERE Id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, newName);
            stmt.setInt(2, id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new MySQLException("Updating department failed, no rows affected.");
            }

            return new Department(id, newName);
        } catch (SQLException e) {
            throw new MySQLException("Could not prepare or execute the statement.", e);
        }
    }

    public static Department updateByName(Connection conn, String oldName, String newName) throws MySQLException {
        // Updates the department's name based on its name.

        Department department = findByName(conn, oldName);
        if (department == null) {
            throw new MySQLException("There is no department with name = '" + oldName + "'.");
        }

        String sql = "UPDATE department SET Name = ? WHERE Name = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, newName);
            stmt.setString(2, oldName);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new MySQLException("Updating department failed, no rows affected.");
            }

            department.setName(newName);
            return department;
        } catch (SQLException e) {
            throw new MySQLException("Could not prepare or execute the statement.", e);
        }
    }

    public static boolean deleteById(Connection conn, int id) throws MySQLException {
        // Deletes a department based on its id number and returns a boolean indicating success.

        String sql = "DELETE FROM department WHERE Id = ?";

        if (findById(conn, id) == null){
            throw new MySQLException("There is no department with id = " + id + "."); 
        }

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

        if (findByName(conn, name) == null){
            throw new MySQLException("There is no department with name = '" + name + "'."); 
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