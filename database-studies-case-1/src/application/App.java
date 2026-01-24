package application;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import DAO.DepartmentDAO;
import connection.MySQLConnection;
import connection.MySQLException;
import entities.Department;
import util.MenuAction;

public class App {
    public static void main(String[] args) throws Exception {
        try (
            Scanner sc = new Scanner(System.in);
            Connection conn = MySQLConnection.getConnection()
        ) {
            System.out.println("Company Management System 0.0.1");
            MenuAction action = mainMenu(sc, conn);
            if (action == MenuAction.EXIT) {
                System.out.println("\nClosing the application...");
            }
        } catch (MySQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static String fixName(String name) {
        StringBuilder fixedName = new StringBuilder();
        String[] parts = name.split(" ");

        for (String part : parts) {
            switch (part.length()) {
                case 0:
                    break;
                case 1:
                    part = part.toUpperCase();
                    fixedName.append(part);
                    break;            
                default:
                    part = part.toUpperCase().charAt(0) + part.substring(1).toLowerCase();
                    fixedName.append(part);
                    break;
            }
            fixedName.append(" ");
        }

        fixedName.deleteCharAt(fixedName.length() - 1); // Remove last space
        return fixedName.toString();
    }

    public static MenuAction bOrQCheck(String input) {
        switch (input.toUpperCase()) {
            case "B":
                return MenuAction.BACK;
            case "Q":
                return MenuAction.EXIT;
            default:
                return MenuAction.CONTINUE;
        }
    }

    public static MenuAction bOrQMenuDep(Scanner sc) {
        String menuOptions = """

            ===========================================
            B. Back to Department Menu
            Q. Quit
            """;

        while (true) {
            System.out.println(menuOptions);
            System.out.print("Select an option: ");
            String choice = sc.nextLine();

            switch (choice.toUpperCase()) {
                case "B":
                    return MenuAction.BACK;
                case "Q":
                    return MenuAction.EXIT;
                default:
                    return MenuAction.INVALID;
            }
        }
    }

    public static MenuAction bOrQMenuSel(Scanner sc) {
        String menuOptions = """

            ===========================================
            B. Back to Seller Menu
            Q. Quit
            """;

        while (true) {
            System.out.println(menuOptions);
            System.out.print("Select an option: ");
            String choice = sc.nextLine();

            switch (choice.toUpperCase()) {
                case "B":
                    return MenuAction.BACK;
                case "Q":
                    return MenuAction.EXIT;
                default:
                    return MenuAction.INVALID;
            }
        }
    }

    public static MenuAction mainMenu(Scanner sc, Connection conn) throws MySQLException {
        String menuOptions = """

                ================= MAIN MENU =================
                1. Department Menu
                2. Seller Menu

                Q. Quit
                """;

        while (true) {
            System.out.println(menuOptions);
            System.out.print("Select an option: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    if (departmentMenu(sc, conn) == MenuAction.EXIT) {
                        return MenuAction.EXIT;
                    }
                    break;
                case "2":
                    // sellerMenu(sc, conn); // Implement sellerMenu similarly
                    System.out.println("Seller Menu is under construction.");
                    break;
                case "q":
                case "Q":
                    return MenuAction.EXIT;
                default:
                    System.out.println("Invalid option. Please try again.");
            }            
        }
    }

    public static MenuAction departmentMenu(Scanner sc, Connection conn) throws MySQLException {
        String menuOptions = """

                =============== DEPARTMENT MENU ===============
                1. Search department by ID
                2. Search department by Name
                3. Show all departments
                4. Count sellers in given department
                5. Count all sellers in the company by department
                6. Insert new department
                7. Update by ID
                9. Update by Name
                8. Delete by ID
                10. Delete by Name

                B. Back to Main Menu
                Q. Quit
                """;

        while (true) {
            System.out.println(menuOptions);
            System.out.print("Select an option: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    if (depFindById(sc, conn) == MenuAction.EXIT) {
                        return MenuAction.EXIT;
                    }
                    break;
                case "2":
                    if (depFindByName(sc, conn) == MenuAction.EXIT) {
                        return MenuAction.EXIT;
                    }
                    break;
                case "3":
                    if (depShowAll(sc, conn) == MenuAction.EXIT) {
                        return MenuAction.EXIT;
                    }
                    break;
                case "4":
                    if (depSellerCount(sc, conn) == MenuAction.EXIT) {
                        return MenuAction.EXIT;
                    }
                    break;
                case "5":
                    if (depSellerCountAll(sc, conn) == MenuAction.EXIT) {
                        return MenuAction.EXIT;
                    }
                    break;
                case "6":
                    if (depInsert(sc, conn) == MenuAction.EXIT) {
                        return MenuAction.EXIT;
                    }
                    break;
                case "7":
                    if (depUpdateById(sc, conn) == MenuAction.EXIT) {
                        return MenuAction.EXIT;
                    }
                    break;
                case "8":
                    if (depUpdateByName(sc, conn) == MenuAction.EXIT) {
                        return MenuAction.EXIT;
                    }
                    break;
                case "9":
                    if (depDeleteById(sc, conn) == MenuAction.EXIT) {
                        return MenuAction.EXIT;
                    }
                    break;
                case "10":
                    if (depDeleteByName(sc, conn) == MenuAction.EXIT) {
                        return MenuAction.EXIT;
                    }
                    break;
                case "b":
                case "B":
                    return MenuAction.BACK;
                case "q":
                case "Q":
                    return MenuAction.EXIT;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
            System.out.println();
        }
    }

    public static MenuAction depFindById (Scanner sc, Connection conn) throws MySQLException {
        int departmentId;
        Department department;
        String input;
        MenuAction action;

        String menuOptions = """

            ========= FIND DEPARTMENT BY ID =========
            B. Back to Department Menu
            Q. Quit
            """;

        while (true) {
            System.out.println(menuOptions);

            System.out.print("Inform the department's ID: ");
            input = sc.nextLine();

            action = bOrQCheck(input);
            if (action != MenuAction.CONTINUE) {
                return action;
            }

            try {
                departmentId = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid ID format. Please enter a valid integer.");
                continue;
            }

            if (departmentId < 1){
                System.out.println("ID cannot be less then 1. Please try again.");
                continue;
            }

            department = DepartmentDAO.findById(conn, departmentId);
            if (department == null) {
                System.out.println("There is no department with ID = " + departmentId + ".");
            } else {
                System.out.println("Department with ID = " + departmentId + " found, name = '" + department.getName() + "'.");
            }
            System.out.println();
        }
    }

    public static MenuAction depFindByName (Scanner sc, Connection conn) throws MySQLException {
        String departmentName;
        Department department;
        String input;
        MenuAction action;

        String menuOptions = """

            ======== FIND DEPARTMENT BY NAME ========
            B. Back to Department Menu
            Q. Quit
            """;

        while (true) {
            System.out.println(menuOptions);

            System.out.print("Inform the department's name: ");
            input = sc.nextLine();

            if (input.trim().isEmpty()){
                System.out.println("Department name cannot be empty. Please try again.");
                continue;
            }

            action = bOrQCheck(input);            
            if (action != MenuAction.CONTINUE) {
                return action;
            }
            
            departmentName = fixName(input);
            department = DepartmentDAO.findByName(conn, departmentName);
            if (department == null) {
                System.out.println("There is no '" + departmentName + "' department.");
            } else {
                System.out.println("Department '" + department.getName() + "' found, ID = " + department.getId() + ".");
            }
            System.out.println();
        }
    }

    public static MenuAction depShowAll(Scanner sc, Connection conn) throws MySQLException {
        MenuAction action;

        System.out.println("\n========= SHOWING ALL DEPARTMENTS =========");

        List<Department> departments = DepartmentDAO.showAll(conn);
        if (departments.isEmpty()) {
            System.out.println("No departments found.");
        } else {            
            for (Department department : departments) {
                System.out.println("ID = " + department.getId() + ", Name = '" + department.getName() + "'");
            }
        }

        action = bOrQMenuDep(sc);
        if (action == MenuAction.INVALID) {
            System.out.println("Invalid option. Returning to Department Menu.");
            return MenuAction.BACK;
        }
        return action;
    }

    public static MenuAction depSellerCount(Scanner sc, Connection conn) throws MySQLException {
        String departmentName;
        int sellerCount;
        String input;
        MenuAction action;

        String menuOptions = """

            ========= DEPARTMENT SELLER COUNT =========
            B. Back to Department Menu
            Q. Quit
            """;

        while (true) {
            System.out.println(menuOptions);

            System.out.print("Inform the department's name: ");
            input = sc.nextLine();

            if (input.trim().isEmpty()){
                System.out.println("Department name cannot be empty. Please try again.");
                continue;
            }
            
            action = bOrQCheck(input);
            if (action != MenuAction.CONTINUE) {
                return action;
            }
            
            departmentName = fixName(input);
            sellerCount = DepartmentDAO.sellerCount(conn, departmentName);
            switch (sellerCount){
                case -1:
                    System.out.println("There is no '" + departmentName + "' department.");
                    break;
                case 0:
                    System.out.println("There are no sellers in the '" + departmentName + "' department");
                    break;
                case 1:
                    System.out.println("There is " + sellerCount + " seller in the '" + departmentName + "' department.");
                    break;
                default:
                    System.out.println("There are " + sellerCount + " sellers in the '" + departmentName + "' department.");
                break;
            }
        }
    }

    public static MenuAction depSellerCountAll(Scanner sc, Connection conn) throws MySQLException {
        MenuAction action;

        System.out.println("\n======== SELLER COUNT BY DEPARTMENT ========");
        
        Map<String, Integer> result = DepartmentDAO.sellerCountAll(conn);
        if (result.isEmpty()) {
            System.out.println("No departments found.");
        } else {
            for (Map.Entry<String, Integer> entry : result.entrySet()) {
                System.out.println("'" + entry.getKey() + "', Seller Count = " + entry.getValue());
            }
        }

        action = bOrQMenuDep(sc);
        if (action == MenuAction.INVALID) {
            System.out.println("Invalid option. Returning to Department Menu.");
            return MenuAction.BACK;
        }
        return action;
    }

    public static MenuAction depInsert(Scanner sc, Connection conn) throws MySQLException {
        String departmentName;
        Department department;
        String input;
        MenuAction action;

        String menuOptions = """

            =========== INSERT NEW DEPARTMENT ===========
            B. Back to Department Menu
            Q. Quit
            """;

        while (true) {
            System.out.println(menuOptions);

            System.out.print("Inform the name of the department: ");
            input = sc.nextLine();

            if (input.trim().isEmpty()) {
                System.out.println("Department name cannot be empty. Please try again.");
                continue;
            }

            action = bOrQCheck(input);
            if (action != MenuAction.CONTINUE) {
                return action;
            }
            
            departmentName = fixName(input);

            if (DepartmentDAO.findByName(conn, departmentName) != null){
                System.out.println("Department '" + departmentName + "' already exists.");
                continue;
            }
            
            department = DepartmentDAO.insert(conn, new Department(departmentName));
            System.out.println("Department '" + department.getName() + "' inserted with id = " + department.getId() + "'.");
            System.out.println();
        }
    }

    public static MenuAction depUpdateById(Scanner sc, Connection conn) throws MySQLException {
        int departmentId;
        String oldName;
        String newName;
        Department department;
        Department compDep;
        String input;
        MenuAction action;

        String menuOptions = """

            ========= UPDATE DEPARTMENT BY ID =========
            B. Back to Department Menu
            Q. Quit
            """;

        while (true) {
            System.out.println(menuOptions);
            System.out.print("Enter the department's ID to update: ");
            input = sc.nextLine();

            action = bOrQCheck(input);
            if (action != MenuAction.CONTINUE) {    
                return action;
            }

            try {
                departmentId = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid ID format. Please enter a valid integer.");
                continue;
            }

            if (departmentId < 1){
                System.out.println("ID cannot be less then 1. Please try again.");
                continue;
            }

            department = DepartmentDAO.findById(conn, departmentId);
            if (department == null) {
                System.out.println("Department with ID = " + departmentId + " not found.");
                continue;
            }

            oldName = department.getName();
            System.out.println("\nCurrent name of the department with ID = " + department.getId() + " is '" + department.getName() + "'.");
            System.out.print("Are you sure you want to update it? (Y/N): ");
            input = sc.nextLine();

            if (input.equalsIgnoreCase("N") || input.equalsIgnoreCase("NO")) {
                System.out.println("Update cancelled.");
                continue;
            } else if (!input.equalsIgnoreCase("Y") && !input.equalsIgnoreCase("YES")) {
                System.out.println("Invalid option. Update cancelled.");
                continue;
            }

            System.out.print("\nEnter the new name for the department: ");
            input = sc.nextLine();

            if (input.trim().isEmpty()) {
                System.out.println("Department name cannot be empty. Please try again.");
                continue;
            }

            action = bOrQCheck(input);
            if (action != MenuAction.CONTINUE) {
                return action;
            }

            newName = fixName(input);

            compDep = DepartmentDAO.findByName(conn, newName);
            if (compDep != null) {
                System.out.println("There is already a department by the name of '" + compDep.getName() + "' with the id = " + compDep.getId() + ".");
                continue;
            }

            department = DepartmentDAO.updateById(conn, departmentId, newName);
            System.out.println("Department's name successfully updated from '" + oldName + "' to '" + department.getName() + "'.");
        }
    }

    public static MenuAction depUpdateByName(Scanner sc, Connection conn) throws MySQLException {
        String oldName;
        String newName;
        Department department;
        Department compDep;
        String input;
        MenuAction action;

        String menuOptions = """

            ========= UPDATE DEPARTMENT BY NAME =========
            B. Back to Department Menu
            Q. Quit
            """;

        while (true) {
            System.out.println(menuOptions);
            System.out.print("Enter the department's name to update: ");
            input = sc.nextLine();

            if (input.trim().isEmpty()){
                System.out.println("Department name cannot be empty. Please try again.");
                continue;
            }

            action = bOrQCheck(input);
            if (action != MenuAction.CONTINUE) {    
                return action;
            }

            oldName = fixName(input);
            department = DepartmentDAO.findByName(conn, oldName);
            if (department == null) {
                System.out.println("Department with name '" + oldName + "' not found.");
                continue;
            }

            System.out.println("\nCurrent name of the department with ID = " + department.getId() + " is '" + department.getName() + "'.");
            System.out.print("Are you sure you want to update it? (Y/N): ");
            input = sc.nextLine();

            if (input.equalsIgnoreCase("N") || input.equalsIgnoreCase("NO")) {
                System.out.println("Update cancelled.");
                continue;
            } else if (!input.equalsIgnoreCase("Y") && !input.equalsIgnoreCase("YES")) {
                System.out.println("Invalid option. Update cancelled.");
                continue;
            }

            System.out.print("\nEnter the new name for the department: ");
            input = sc.nextLine();

            if (input.trim().isEmpty()) {
                System.out.println("Department name cannot be empty. Please try again.");
                continue;
            }

            action = bOrQCheck(input);
            if (action != MenuAction.CONTINUE) {
                return action;
            }

            newName = fixName(input);
            
            compDep = DepartmentDAO.findByName(conn, newName);
            if (compDep != null) {
                System.out.println("There is already a department by the name of '" + compDep.getName() + "' with the id = " + compDep.getId() + ".");
                continue;
            }

            department = DepartmentDAO.updateByName(conn, department, newName);
            System.out.println("Department's name successfully updated from '" + oldName + "' to '" + department.getName() + "'.");
        }
    }

    public static MenuAction depDeleteById(Scanner sc, Connection conn) throws MySQLException {
        int departmentId;
        Department department;
        String input;
        MenuAction action;

        String menuOptions = """

            ========= DELETE DEPARTMENT BY ID =========
            B. Back to Department Menu
            Q. Quit
            """;

        while (true) {
            System.out.println(menuOptions);
            System.out.print("Enter the department's ID to delete: ");
            input = sc.nextLine();

            action = bOrQCheck(input);
            if (action != MenuAction.CONTINUE) {    
                return action;
            }

            try {
                departmentId = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid ID format. Please enter a valid integer.");
                continue;
            }

            if (departmentId < 1){
                System.out.println("ID cannot be less then 1. Please try again.");
                continue;
            }

            department = DepartmentDAO.findById(conn, departmentId);
            if (department == null) {
                System.out.println("Department with ID = " + departmentId + " not found.");
                continue;
            }

            System.out.println("\nDepartment with ID = " + departmentId + " named '" + department.getName() + "' found.");
            System.out.print("Are you sure you want to delete it? (Y/N): ");
            input = sc.nextLine();
            if (input.equalsIgnoreCase("N") || input.equalsIgnoreCase("NO")) {
                System.out.println("Deletion cancelled.");
                continue;
            } else if (!input.equalsIgnoreCase("Y") && !input.equalsIgnoreCase("YES")) {
                System.out.println("Invalid option. Deletion cancelled.");
                continue;
            }

            boolean deleted = DepartmentDAO.deleteById(conn, departmentId);
            if (deleted) {
                System.out.println("\nDepartment with ID = " + department.getId() + " named '" + department.getName() + "' successfully deleted.");
            } else {
                throw new MySQLException("Failed to delete department.");
            }
        }
    }
    
    public static MenuAction depDeleteByName(Scanner sc, Connection conn) throws MySQLException {
        String departmentName;
        Department department;
        String input;
        MenuAction action;

        String menuOptions = """

            ======== DELETE DEPARTMENT BY NAME ========
            B. Back to Department Menu
            Q. Quit
            """;

        while (true) {
            System.out.println(menuOptions);
            System.out.print("Enter the department's name to delete: ");
            input = sc.nextLine();

            action = bOrQCheck(input);
            if (action != MenuAction.CONTINUE) {
                return action;
            }

            if (input.trim().isEmpty()){
                System.out.println("Department name cannot be empty. Please try again.");
                continue;
            }

            departmentName = fixName(input);
            department = DepartmentDAO.findByName(conn, departmentName);
            if (department == null) {
                System.out.println("Department named '" + departmentName + "' not found.");
                continue;
            }

            System.out.println("\nDepartment with ID = " + department.getId() + " named '" + department.getName() + "' found.");
            System.out.print("Are you sure you want to delete it? (Y/N): ");
            input = sc.nextLine();
            if (input.equalsIgnoreCase("N") || input.equalsIgnoreCase("NO")) {
                System.out.println("Deletion cancelled.");
                continue;
            } else if (!input.equalsIgnoreCase("Y") && !input.equalsIgnoreCase("YES")) {
                System.out.println("Invalid option. Deletion cancelled.");
                continue;
            }

            boolean deleted = DepartmentDAO.deleteByName(conn, departmentName);
            if (deleted) {
                System.out.println("\nDepartment with ID = " + department.getId() + " named '" + department.getName() + "' successfully deleted.");
            } else {
                throw new MySQLException("Failed to delete department.");
            }
        }
    }
}