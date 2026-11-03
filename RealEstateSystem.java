import java.util.List;
import database.Database.UserDAO;
import database.Database.PropertyDAO;
import models.Models.Builder;
import models.Models.Customer;
import models.Models.Property;
import models.Models.User;
import utils.Utilities.InputUtil;
import utils.Utilities.RealEstateException;

public class RealEstateSystem {

    private static UserDAO userDAO = new UserDAO();
    private static PropertyDAO propertyDAO = new PropertyDAO();
    private static User loggedInUser = null;

    public static void main(String[] args) {
        System.out.println("================================================");
        System.out.println("   Welcome to Real Estate Management System");
        System.out.println("================================================");

        // Menu Driven Program
        boolean running = true;
        while (running) {
            if (loggedInUser == null) {
                running = showInitialMenu();
            } else {
                if (loggedInUser instanceof Builder) {
                    showBuilderMenu();
                } else if (loggedInUser instanceof Customer) {
                    showCustomerMenu();
                }
            }
        }
        System.out.println("Thank you for using the Real Estate System.");
    }

    private static boolean showInitialMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit Application");

        int choice = InputUtil.readInt("Enter your choice: ");

        switch (choice) {
            case 1:
                loginFlow();
                break;
            case 2:
                registerFlow();
                break;
            case 3:
                return false;
            default:
                System.out.println("Invalid choice.");
        }
        return true;
    }

    // Login Method
    private static void loginFlow() {
        System.out.println("\n--- Login ---");
        String name = InputUtil.readString("Name: ");
        String password = InputUtil.readString("Password: ");

        loggedInUser = userDAO.loginUser(name, password);

        if (loggedInUser != null) {
            System.out.println("Login successful! Welcome, " + loggedInUser.getUsername());

        } else {
            System.out.println("Invalid credentials.");
        }
    }

    // Register Method
    private static void registerFlow() {
        System.out.println("\n--- Registration ---");
        String name = InputUtil.readString("Full Name: ");
        String email = InputUtil.readString("Email: ");
        String password = InputUtil.readString("Choose a Password: ");

        // Validation: Phone number must contain only numeric digits
        String phone = "";
        boolean validPhone = false;
        while (!validPhone) {
            phone = InputUtil.readString("Phone Number: ");
            // Excepti9on Handling For Invalid Phone Numbers
            try {
                if (phone.isEmpty()) {
                    throw new NumberFormatException("Phone number cannot be empty.");
                }
                Long.parseLong(phone); // will throw if non-numeric characters are present
                validPhone = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid phone number! Please enter numeric digits only. (" + e.getMessage() + ")");
            }
        }

        // Validation: Role choice must be strictly 1 or 2
        int roleChoice = 0;
        while (roleChoice != 1 && roleChoice != 2) {
            System.out.println("Select Role:");
            System.out.println("1. CUSTOMER");
            System.out.println("2. BUILDER");
            roleChoice = InputUtil.readInt("Choice (1 or 2): ");
            if (roleChoice != 1 && roleChoice != 2) {
                System.out.println("Invalid choice! Please enter 1 for CUSTOMER or 2 for BUILDER.");
            }
        }

        String role = (roleChoice == 2) ? "BUILDER" : "CUSTOMER";

        if (userDAO.registerUser(name, email, password, phone, role)) {
            System.out.println("Registration successful! You can now log in.");
        } else {
            System.out.println("Registration failed. Try a different name or email.");
        }
    }

    // Menu Driven Program for Builder
    private static void showBuilderMenu() {
        System.out.println("\n--- Builder Menu ---");
        System.out.println("1. Add Property");
        System.out.println("2. View My Properties");
        System.out.println("3. View All Properties");
        System.out.println("4. Update My Property");
        System.out.println("5. Delete My Property");
        System.out.println("6. Logout");

        int choice = InputUtil.readInt("Enter your choice: ");
        switch (choice) {
            case 1:
                addPropertyFlow();
                break;
            case 2:
                viewPropertiesByBuilder();
                break;
            case 3:
                viewAllProperties(false);
                break;
            case 4:
                updatePropertyFlow();
                break;
            case 5:
                deletePropertyFlow();
                break;
            case 6:
                loggedInUser = null;
                System.out.println("Logged out.");
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    // Menu Driven Program for Customers
    private static void showCustomerMenu() {
        System.out.println("\n--- Customer Menu ---");
        System.out.println("1. View Available Properties");
        System.out.println("2. Search Properties");
        System.out.println("3. Buy Property");
        System.out.println("4. Logout");

        int choice = InputUtil.readInt("Enter your choice: ");
        switch (choice) {
            case 1:
                viewAllProperties(true);
                break;
            case 2:
                searchPropertiesFlow();
                break;
            case 3:
                buyPropertyFlow();
                break;
            case 4:
                loggedInUser = null;
                System.out.println("Logged out.");
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void addPropertyFlow() {
        System.out.println("\n--- Add Property ---");
        String title = InputUtil.readString("Title: ");
        String city = InputUtil.readString("City: ");
        int bhk = InputUtil.readInt("BHKs: ");
        double price = InputUtil.readDouble("Price: ");

        Builder currentBuilder = new Builder();
        currentBuilder.setId(loggedInUser.getId());
        currentBuilder.setUsername(loggedInUser.getUsername());

        Property newProp = new Property(0, title, city, bhk, price, "AVAILABLE", currentBuilder);

        if (propertyDAO.addProperty(newProp)) {
            System.out.println("Property added successfully!");
        } else {
            System.out.println("Failed to add property.");
        }
    }

    private static void viewPropertiesByBuilder() {
        System.out.println("\n--- My Properties ---");
        displayPropertyList(propertyDAO.getPropertiesByBuilder(loggedInUser.getId()));
    }

    private static void viewAllProperties(boolean onlyAvailable) {
        System.out.println("\n--- All Properties ---");
        displayPropertyList(propertyDAO.getAllProperties(onlyAvailable));
    }

    private static void updatePropertyFlow() {
        System.out.println("\n--- Update Property ---");
        viewPropertiesByBuilder();
        int propId = InputUtil.readInt("Enter Property ID to update (0 to cancel): ");
        if (propId == 0)
            return;

        String title = InputUtil.readString("New Title: ");
        String city = InputUtil.readString("New City: ");
        int bhk = InputUtil.readInt("New BHKs: ");
        double price = InputUtil.readDouble("New Price: ");
        String status = InputUtil.readString("New Status (AVAILABLE/SOLD): ").toUpperCase();

        Builder currentBuilder = new Builder();
        currentBuilder.setId(loggedInUser.getId());

        Property updatedProp = new Property(propId, title, city, bhk, price, status, currentBuilder);

        if (propertyDAO.updateProperty(updatedProp)) {
            System.out.println("Property updated successfully.");
        } else {
            System.out.println("Failed to update property.");
        }
    }

    private static void deletePropertyFlow() {
        System.out.println("\n--- Delete Property ---");
        int propId = InputUtil.readInt("Enter Property ID to delete: ");
        if (propertyDAO.deleteProperty(propId, loggedInUser.getId())) {
            System.out.println("Property deleted successfully.");
        } else {
            System.out.println("Failed to delete property.");
        }
    }

    private static void searchPropertiesFlow() {
        System.out.println("\n--- Search Properties ---");
        String city = InputUtil.readString("City: ");

        double maxPrice = InputUtil.readDouble("Max Price: ");
        int minBhk = InputUtil.readInt("Min BHK: ");
        List<Property> props = propertyDAO.searchProperties(city, maxPrice, minBhk);

        displayPropertyList(props);
    }

    private static void buyPropertyFlow() {
        System.out.println("\n--- Buy Property ---");
        int propId = InputUtil.readInt("Enter Property ID to purchase: ");

        try {
            if (propertyDAO.buyProperty(propId)) {
                System.out.println("Congratulations " + loggedInUser.getUsername() + "! Purchase Complete.");
            }
        } catch (RealEstateException e) {
            System.err.println("Purchase Request Failed!");
            System.err.println("Reason: " + e.getMessage());
        }
    }

    private static void displayPropertyList(List<Property> properties) {
        if (properties.isEmpty()) {
            System.out.println("No properties found.");
        } else {
            for (Property p : properties) {
                System.out.println(p);
            }
        }
    }
}