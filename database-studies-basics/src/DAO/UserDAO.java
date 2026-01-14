package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import entities.User;
import connection.MySQLConnection;


public class UserDAO {
    private int userExists(User user, Connection conn) {
        if (conn == null) return -1; // connection error
        
         String checkSql = "SELECT username, email FROM user_table WHERE username = ? OR email = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, user.getUsername());
            checkStmt.setString(2, user.getEmail());
            
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    String foundUser = rs.getString("username");
                    String foundEmail = rs.getString("email");
                    
                    if (foundUser.equals(user.getUsername()) && foundEmail.equals(user.getEmail())) return 1; // Username and Email exist
                    if (foundUser.equals(user.getUsername())) return 2; // Username exists
                    if (foundEmail.equals(user.getEmail())) return 3; // Email exists
                }
            } catch (Exception e) {
                e.printStackTrace();
                return -1; // Error during result set processing
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Error during statement preparation/execution
        }
        return 0; // Neither exists
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
                pstmt.executeUpdate();
                System.out.println("Usuário criado com sucesso!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}