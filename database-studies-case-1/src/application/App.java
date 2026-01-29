package application;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import DAO.DepartmentDAO;
import DAO.SellerDAO;
import connection.DBIntegrityException;
import connection.MySQLConnection;
import connection.MySQLException;
import entities.Department;
import entities.Seller;
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
                    if (sellerMenu(sc, conn) == MenuAction.EXIT) {
                        return MenuAction.EXIT;
                    }
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
                8. Update by Name
                9. Delete by ID
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

    public static MenuAction depDeleteById(Scanner sc, Connection conn) throws MySQLException, DBIntegrityException {
        int departmentId;
        Department department;
        int sellerCount;
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

            sellerCount = DepartmentDAO.sellerCount(conn, department.getName());
            if (sellerCount > 0) {
                System.out.println("Department '" + department.getName() + "' cannot be removed while there are still sellers attributed to it.");
                System.out.println("Number of sellers in '" + department.getName() + "' = " + sellerCount);
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
    
    public static MenuAction depDeleteByName(Scanner sc, Connection conn) throws MySQLException, DBIntegrityException {
        String departmentName;
        Department department;
        int sellerCount;
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

            sellerCount = DepartmentDAO.sellerCount(conn, department.getName());
            if (sellerCount > 0) {
                System.out.println("Department '" + department.getName() + "' cannot be removed while there are still sellers attributed to it.");
                System.out.println("Number of sellers in '" + department.getName() + "' = " + sellerCount);
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

    public static MenuAction sellerMenu(Scanner sc, Connection conn) throws MySQLException {
        String menuOptions = """

                ================ SELLER MENU ================
                1. Search seller by ID
                2. Search seller by Name
                3. Show all sellers
                4. Insert new seller
                5. Update by ID
                6. Delete by ID
                7. Delete by Name

                B. Back to Main Menu
                Q. Quit
                """;

        while (true) {
            System.out.println(menuOptions);
            System.out.print("Select an option: ");
            String choice = sc.nextLine();

            try {
                switch (choice) {
                    case "1":
                        if (selFindById(sc, conn) == MenuAction.EXIT) {
                            return MenuAction.EXIT;
                        }
                        break;
                    case "2":
                        if (selFindByName(sc, conn) == MenuAction.EXIT) {
                            return MenuAction.EXIT;
                        }
                        break;
                    case "3":
                        if (selShowAll(sc, conn) == MenuAction.EXIT) {
                            return MenuAction.EXIT;
                        }
                        break;
                    case "4":
                        if (selInsert(sc, conn) == MenuAction.EXIT) {
                            return MenuAction.EXIT;
                        }
                        break;
                    case "5":
                        if (selUpdateById(sc, conn) == MenuAction.EXIT) {
                            return MenuAction.EXIT;
                        }
                        break;
                    case "6":
                        if (selDeleteById(sc, conn) == MenuAction.EXIT) {
                            return MenuAction.EXIT;
                        }
                        break;
                    case "7":
                        if (selDeleteByName(sc, conn) == MenuAction.EXIT) {
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
            } catch (DBIntegrityException e) {
                System.out.println("Error: " + e.getMessage());
            }
            System.out.println();
        }
    }

    public static MenuAction selFindById(Scanner sc, Connection conn) throws MySQLException {
        int sellerId;
        Seller seller;
        String input;
        MenuAction action;

        String menuOptions = """

            ========== FIND SELLER BY ID ==========
            B. Back to Seller Menu
            Q. Quit
            """;

        while (true) {
            System.out.println(menuOptions);

            System.out.print("Inform the seller's ID: ");
            input = sc.nextLine();

            action = bOrQCheck(input);
            if (action != MenuAction.CONTINUE) {
                return action;
            }

            try {
                sellerId = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid ID format. Please enter a valid number.");
                continue;
            }

            if (sellerId < 1) {
                System.out.println("Seller ID must be greater than 0.");
                continue;
            }

            seller = SellerDAO.findById(conn, sellerId);
            if (seller == null) {
                System.out.println("Seller with ID = " + sellerId + " not found.");
            } else {
                System.out.println("\n" + seller);
            }
            System.out.println();
        }
    }

    public static MenuAction selFindByName(Scanner sc, Connection conn) throws MySQLException {
        String sellerName;
        Seller seller;
        String input;
        MenuAction action;

        String menuOptions = """

            ========== FIND SELLER BY NAME ==========
            B. Back to Seller Menu
            Q. Quit
            """;

        while (true) {
            System.out.println(menuOptions);

            System.out.print("Inform the seller's name: ");
            input = sc.nextLine();

            if (input.trim().isEmpty()) {
                System.out.println("Seller name cannot be empty. Please try again.");
                continue;
            }

            action = bOrQCheck(input);
            if (action != MenuAction.CONTINUE) {
                return action;
            }

            sellerName = fixName(input);
            seller = SellerDAO.findByName(conn, sellerName);
            if (seller == null) {
                System.out.println("Seller named '" + sellerName + "' not found.");
            } else {
                System.out.println("\n" + seller);
            }
            System.out.println();
        }
    }

    public static MenuAction selShowAll(Scanner sc, Connection conn) throws MySQLException {
        MenuAction action;

        System.out.println("\n========== SHOWING ALL SELLERS ==========");

        List<Seller> sellers = SellerDAO.showAll(conn);
        if (sellers.isEmpty()) {
            System.out.println("No sellers found.");
        } else {
            for (Seller seller : sellers) {
                System.out.println(seller);
            }
        }

        action = bOrQMenuSel(sc);
        if (action == MenuAction.INVALID) {
            System.out.println("Invalid option. Returning to Seller Menu.");
            return MenuAction.BACK;
        }
        return action;
    }

    public static MenuAction selInsert(Scanner sc, Connection conn) throws MySQLException {
        String sellerName;
        String email;
        String birthDateStr;
        String baseSalaryStr;
        String departmentNameStr;
        Seller seller;
        Department department;
        String input;
        MenuAction action;

        String menuOptions = """

            ========== INSERT NEW SELLER ==========
            B. Back to Seller Menu
            Q. Quit
            """;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        while (true) {
            System.out.println(menuOptions);

            System.out.print("Inform the seller's name: ");
            input = sc.nextLine();

            if (input.trim().isEmpty()) {
                System.out.println("Seller name cannot be empty. Please try again.");
                continue;
            }

            action = bOrQCheck(input);
            if (action != MenuAction.CONTINUE) {
                return action;
            }

            sellerName = fixName(input);

            System.out.print("Inform the seller's email: ");
            email = sc.nextLine();

            if (email.trim().isEmpty()) {
                System.out.println("Seller email cannot be empty. Please try again.");
                continue;
            }

            System.out.print("Inform the seller's birth date (dd/MM/yyyy): ");
            birthDateStr = sc.nextLine();

            java.util.Date birthDate;
            try {
                birthDate = dateFormat.parse(birthDateStr);
            } catch (java.text.ParseException e) {
                System.out.println("Invalid date format. Please use dd/MM/yyyy.");
                continue;
            }

            System.out.print("Inform the seller's base salary: ");
            baseSalaryStr = sc.nextLine();

            BigDecimal baseSalary;
            try {
                baseSalary = new BigDecimal(baseSalaryStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid salary format. Please enter a valid number.");
                continue;
            }

            System.out.print("Inform the department name: ");
            departmentNameStr = sc.nextLine();

            if (departmentNameStr.trim().isEmpty()) {
                System.out.println("Department name cannot be empty.");
                continue;
            }

            departmentNameStr = fixName(departmentNameStr);
            department = DepartmentDAO.findByName(conn, departmentNameStr);

            if (department == null) {
                System.out.println("Department '" + departmentNameStr + "' not found.");
                continue;
            }

            if (SellerDAO.findByName(conn, sellerName) != null) {
                System.out.println("Seller '" + sellerName + "' already exists.");
                continue;
            }

            seller = SellerDAO.insert(conn, new Seller(sellerName, email, birthDate, baseSalary, department));
            System.out.println("Seller '" + seller.getName() + "' inserted with id = " + seller.getId() + ".");
            System.out.println();
        }
    }

    public static MenuAction selUpdateById(Scanner sc, Connection conn) throws MySQLException {
        int sellerId;
        String sellerName;
        String email;
        String birthDateStr;
        String baseSalaryStr;
        String departmentNameStr;
        Seller seller;
        Department department;
        String input;
        MenuAction action;

        String menuOptions = """

            ========== UPDATE SELLER BY ID ==========
            B. Back to Seller Menu
            Q. Quit
            """;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        while (true) {
            System.out.println(menuOptions);
            System.out.print("Enter the seller's ID to update: ");
            input = sc.nextLine();

            action = bOrQCheck(input);
            if (action != MenuAction.CONTINUE) {
                return action;
            }

            try {
                sellerId = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid ID format. Please enter a valid number.");
                continue;
            }

            if (sellerId < 1) {
                System.out.println("Seller ID must be greater than 0.");
                continue;
            }

            seller = SellerDAO.findById(conn, sellerId);
            if (seller == null) {
                System.out.println("Seller with ID = " + sellerId + " not found.");
                continue;
            }

            System.out.println("\nSeller with ID = " + seller.getId() + " found.");
            System.out.println("Current information: " + seller);

            System.out.print("\nEnter new name (or press Enter to keep current): ");
            input = sc.nextLine();
            sellerName = input.isEmpty() ? seller.getName() : fixName(input);

            System.out.print("Enter new email (or press Enter to keep current): ");
            email = sc.nextLine();
            email = email.isEmpty() ? seller.getEmail() : email;

            System.out.print("Enter new birth date dd/MM/yyyy (or press Enter to keep current): ");
            birthDateStr = sc.nextLine();
            java.util.Date birthDate = seller.getBirthDate();
            if (!birthDateStr.isEmpty()) {
                try {
                    birthDate = dateFormat.parse(birthDateStr);
                } catch (java.text.ParseException e) {
                    System.out.println("Invalid date format. Keeping current date.");
                }
            }

            System.out.print("Enter new base salary (or press Enter to keep current): ");
            baseSalaryStr = sc.nextLine();
            BigDecimal baseSalary = seller.getBaseSalary();
            if (!baseSalaryStr.isEmpty()) {
                try {
                    baseSalary = new BigDecimal(baseSalaryStr);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid salary format. Keeping current salary.");
                }
            }

            System.out.print("Enter new department name (or press Enter to keep current): ");
            departmentNameStr = sc.nextLine();
            department = seller.getDepartment();
            if (!departmentNameStr.isEmpty()) {
                departmentNameStr = fixName(departmentNameStr);
                Department newDep = DepartmentDAO.findByName(conn, departmentNameStr);
                if (newDep == null) {
                    System.out.println("Department '" + departmentNameStr + "' not found. Keeping current department.");
                } else {
                    department = newDep;
                }
            }

            Seller updatedSeller = SellerDAO.updateById(conn, sellerId, 
                new Seller(sellerId, sellerName, email, birthDate, baseSalary, department));
            System.out.println("\nSeller with ID = " + updatedSeller.getId() + " successfully updated.");
            System.out.println();
        }
    }

    public static MenuAction selDeleteById(Scanner sc, Connection conn) throws MySQLException, DBIntegrityException {
        int sellerId;
        Seller seller;
        String input;
        MenuAction action;

        String menuOptions = """

            ========== DELETE SELLER BY ID ==========
            B. Back to Seller Menu
            Q. Quit
            """;

        while (true) {
            System.out.println(menuOptions);
            System.out.print("Enter the seller's ID to delete: ");
            input = sc.nextLine();

            action = bOrQCheck(input);
            if (action != MenuAction.CONTINUE) {
                return action;
            }

            try {
                sellerId = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid ID format. Please enter a valid number.");
                continue;
            }

            if (sellerId < 1) {
                System.out.println("Seller ID must be greater than 0.");
                continue;
            }

            seller = SellerDAO.findById(conn, sellerId);
            if (seller == null) {
                System.out.println("Seller with ID = " + sellerId + " not found.");
                continue;
            }

            System.out.println("\nSeller with ID = " + seller.getId() + " found.");
            System.out.println("Information: " + seller);
            System.out.print("Are you sure you want to delete this seller? (Y/N): ");
            input = sc.nextLine();

            if (input.equalsIgnoreCase("N") || input.equalsIgnoreCase("NO")) {
                System.out.println("Deletion cancelled.");
                continue;
            } else if (!input.equalsIgnoreCase("Y") && !input.equalsIgnoreCase("YES")) {
                System.out.println("Invalid option. Deletion cancelled.");
                continue;
            }

            boolean deleted = SellerDAO.deleteById(conn, sellerId);
            if (deleted) {
                System.out.println("\nSeller with ID = " + seller.getId() + " successfully deleted.");
            } else {
                System.out.println("Failed to delete seller.");
            }
            System.out.println();
        }
    }

    public static MenuAction selDeleteByName(Scanner sc, Connection conn) throws MySQLException, DBIntegrityException {
        String sellerName;
        Seller seller;
        String input;
        MenuAction action;

        String menuOptions = """

            ========== DELETE SELLER BY NAME ==========
            B. Back to Seller Menu
            Q. Quit
            """;

        while (true) {
            System.out.println(menuOptions);
            System.out.print("Enter the seller's name to delete: ");
            input = sc.nextLine();

            action = bOrQCheck(input);
            if (action != MenuAction.CONTINUE) {
                return action;
            }

            if (input.trim().isEmpty()) {
                System.out.println("Seller name cannot be empty. Please try again.");
                continue;
            }

            sellerName = fixName(input);
            seller = SellerDAO.findByName(conn, sellerName);
            if (seller == null) {
                System.out.println("Seller named '" + sellerName + "' not found.");
                continue;
            }

            System.out.println("\nSeller named '" + seller.getName() + "' found.");
            System.out.println("Information: " + seller);
            System.out.print("Are you sure you want to delete this seller? (Y/N): ");
            input = sc.nextLine();

            if (input.equalsIgnoreCase("N") || input.equalsIgnoreCase("NO")) {
                System.out.println("Deletion cancelled.");
                continue;
            } else if (!input.equalsIgnoreCase("Y") && !input.equalsIgnoreCase("YES")) {
                System.out.println("Invalid option. Deletion cancelled.");
                continue;
            }

            boolean deleted = SellerDAO.deleteByName(conn, sellerName);
            if (deleted) {
                System.out.println("\nSeller named '" + seller.getName() + "' successfully deleted.");
            } else {
                System.out.println("Failed to delete seller.");
            }
            System.out.println();
        }
    }
}