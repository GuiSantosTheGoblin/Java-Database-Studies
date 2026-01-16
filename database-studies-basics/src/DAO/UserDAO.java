package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import entities.User;
import connection.MySQLConnection;


public class UserDAO {
    private int userExists(User user, Connection conn) {
        // Returns:
        // -1: connection error or query error
        //  0: neither username nor email exist
        //  1: both username and email exist
        //  2: username exists
        //  3: email exists
        if (conn == null) return -1;
        
        String checkSql = "SELECT username, email FROM user_table WHERE username = ? OR email = ?";
        // prepareStatement(): Allows parameter binding using '?' and rotects against SQL Injection.
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, user.getUsername());
            checkStmt.setString(2, user.getEmail());
            
            // executeQuery(): Executes a SELECT statement and returns a table of data representing the result set, stored in a ResultSet object.
            try (ResultSet rs = checkStmt.executeQuery()) {
                // The ResultSet cursor starts before the first row of the table, so we call rs.next() to move to the first row.
                if (rs.next()) {
                    String foundUser = rs.getString("username");
                    String foundEmail = rs.getString("email");
                    
                    if (foundUser.equals(user.getUsername()) && foundEmail.equals(user.getEmail())) return 1;
                    if (foundUser.equals(user.getUsername())) return 2; 
                    if (foundEmail.equals(user.getEmail())) return 3;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return -1; 
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public void createUser(User user) {
        String sql = "INSERT INTO user_table (username, password, email) VALUES (?, ?, ?)";
        
        // Using try-with-resources to ensure connection is closed
        try (Connection conn = MySQLConnection.getConnection()) {
            if (conn == null) {
                System.out.println("Erro: Não foi possível conectar ao banco de dados.");
                return;
            }
                
            // Checking for existing username or email
            switch (this.userExists(user, conn)) {
                case -1:
                    System.out.println("Erro: Falha ao verificar existência do usuário.");
                    return;
                case 1:
                    System.out.println("Erro: Usuário já existe.");
                    return;
                case 2:
                    System.out.println("Erro: Nome de usuário já existe.");
                    return;
                case 3:
                    System.out.println("Erro: Email já existe.");
                    return;
                default:
                    break;
            }
            
            // Inserting new user
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, user.getUsername());
                pstmt.setString(2, user.getPassword());
                pstmt.setString(3, user.getEmail());
                pstmt.executeUpdate(); // executeUpdate(): Executes an SQL statement that may be an INSERT, UPDATE, or DELETE statement or an SQL statement that returns nothing, such as an SQL DDL statement.
                System.out.println("Usuário criado com sucesso!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}