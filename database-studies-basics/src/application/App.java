package application;

import entities.User;
import DAO.UserDAO;

public class App {
    public static void main(String[] args) throws Exception {
        UserDAO userDAO = new UserDAO();

        User newUser = new User("peter_pan", "dsggfdgfdg", "peterp@example.com");
        userDAO.createUser(newUser);
    }
}