package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

import connection.MySQLConnection;

public class App {
    public static void main(String[] args) throws Exception {
        try (var conn = MySQLConnection.getConnection()) {
            int howManySellers = 0;
            String department;
            String sql;
            
            try (Scanner sc = new Scanner(System.in)){
                System.out.print("Inform the name of the depatment: ");
                department = sc.nextLine();
            }

            sql = """
                SELECT COUNT(seller.Id)
                FROM department
                LEFT JOIN seller ON seller.DepartmentId = department.Id
                WHERE department.Name = ?
                GROUP BY department.Id
                """;
                
            try (PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setString(1, department);
                try (ResultSet rs = stmt.executeQuery()){
                    if (!rs.next()){
                        System.out.println("Error: There is no '" + department + "' department.");
                        return;
                    }
                     
                    howManySellers = rs.getInt(1);
                }
            }
                
            if (howManySellers == 1) {
                System.out.println("There is " + howManySellers + " seller in the '" + department + "' department.");
            } else if (howManySellers == 0) {
                System.out.println("There are no sellers in the '" + department + "' department");
            } else {
                System.out.println("There are " + howManySellers + " sellers in the '" + department + "' department.");
            }
        }
    }
}