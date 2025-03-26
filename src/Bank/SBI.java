package Bank;

import java.sql.*;
import java.util.Scanner;

public class SBI {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice;
        
        try {
            Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/banking_system", 
                "root", 
                "Sathvika@123#"
            );

            System.out.println("----------Welcome to SBI Bank!!----------");
            System.out.print("\nEnter Account Holder name: ");
            String name = sc.nextLine();
            
            System.out.print("Enter Account No (9 digits): ");
            long accountNumber = sc.nextLong();
            sc.nextLine(); 
            
            System.out.println("\nLogged in successfully!!");
            
            BankAccount account = new BankAccount(connection, name, accountNumber);
            
            do {
                System.out.println("\nMenu:");
                System.out.println("1. My Account");
                System.out.println("2. Withdraw Amount");
                System.out.println("3. Deposit Amount");
                System.out.println("4. Transaction History");
                System.out.println("5. Exit");
                
                System.out.print("\nEnter your choice: ");
                choice = sc.nextInt();
                sc.nextLine(); // Consume newline
                
                switch(choice) {
                    case 1:
                        account.displayAccountDetails();
                        //System.out.println("\nThank you for being a SBI family member!!");
                        break;
                    case 2:
                        account.withdraw();
                        System.out.println("\nThank you, have a great day!!");
                        break;
                    case 3:
                        account.deposit();
                        System.out.println("\nThank you, have a great day!!");
                        break;
                    case 4:
                        account.viewTransactionHistory();
                        break;
                    case 5:
                        account.exit();
                        System.out.println("\nThank you for using SBI banking");
                        break;
                    default:
                        System.out.println("Invalid choice");
                }
            } while(choice != 5);
            
            connection.close();
        } catch(SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        } finally {
            sc.close();
        }
    }
}

class BankAccount {
    private Connection connection;
    private String name;
    private long accountNumber;
    private double balance;
    private Scanner sc;
    
    public BankAccount(Connection connection, String name, long accountNumber) {
        this.connection = connection;
        this.name = name;
        this.accountNumber = accountNumber;
        this.sc = new Scanner(System.in);
        this.balance = getAccountBalanceFromDB();
    }
    
    private double getAccountBalanceFromDB() {
        try {
            String sql = "SELECT balance FROM accounts WHERE account_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setLong(1, accountNumber);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getDouble("balance");
                }
            }
        } catch(SQLException e) {
            System.err.println("Error fetching balance: " + e.getMessage());
        }
        return 0.0;
    }
    
    public void displayAccountDetails() {
        System.out.println("\nAccount Details:");
        System.out.println("-----------------------------");
        System.out.println("Account Holder: " + name);
        System.out.println("Account Number: " + accountNumber);
        System.out.printf("Available Balance: Rs. %.2f%n", balance);
        System.out.println("-----------------------------");
    }
    
    public void withdraw() {
        System.out.print("\nEnter amount to withdraw: ");
        try {
            double amount = sc.nextDouble();
            sc.nextLine(); 
            
            if (amount <= 0) {
                System.out.println("Amount must be positive!");
                return;
            }
            
            if (amount > balance) {
                System.out.println("Insufficient balance!");
                return;
            }
            
            String sql = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setDouble(1, amount);
                stmt.setLong(2, accountNumber);
                int rows = stmt.executeUpdate();
                
                if (rows > 0) {
                    balance -= amount;
                    recordTransaction("Withdrawal", amount);
                    System.out.printf("Rs. %.2f withdrawn successfully.%n", amount);
                    System.out.printf("New balance: Rs. %.2f%n", balance);
                }
            }
        } catch(SQLException e) {
            System.err.println("Withdrawal error: " + e.getMessage());
        }
    }
    
    public void deposit() {
        System.out.print("\nEnter amount to deposit: ");
        try {
            double amount = sc.nextDouble();
            sc.nextLine(); // Consume newline
            
            if (amount <= 0) {
                System.out.println("Amount must be positive!");
                return;
            }
            
            String sql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setDouble(1, amount);
                stmt.setLong(2, accountNumber);
                int rows = stmt.executeUpdate();
                
                if (rows > 0) {
                    balance += amount;
                    recordTransaction("Deposit", amount);
                    System.out.printf("Rs. %.2f deposited successfully.%n", amount);
                    System.out.printf("New balance: Rs. %.2f%n", balance);
                }
            }
        } catch(SQLException e) {
            System.err.println("Deposit error: " + e.getMessage());
        }
    }
    
    public void viewTransactionHistory() {
        try {
            String sql = "SELECT transaction_type, amount, transaction_date FROM transactions " +
                         "WHERE account_id = ? ORDER BY transaction_date DESC";
            
            System.out.println("\nTransaction History:");
            System.out.println("----------------------------------------");
            System.out.printf("%-12s %-10s %-20s%n", "Type", "Amount", "Date");
            System.out.println("----------------------------------------");
            
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setLong(1, accountNumber);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    System.out.printf("%-12s %-10.2f %-20s%n",
                                    rs.getString("transaction_type"),
                                    rs.getDouble("amount"),
                                    rs.getTimestamp("transaction_date"));
                }
            }
            
            System.out.println("----------------------------------------");
            
        } catch(SQLException e) {
            System.err.println("Error fetching transactions: " + e.getMessage());
        }
    }
    
    private void recordTransaction(String type, double amount) throws SQLException {
        String sql = "INSERT INTO transactions (account_id, transaction_type, amount, balance_after, description) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, accountNumber);
            stmt.setString(2, type);
            stmt.setDouble(3, amount);
            stmt.setDouble(4, balance); 
            stmt.setString(5, type + " transaction"); 
            stmt.executeUpdate();
        }
    }
    
    public void exit() {
        System.out.println("\nLogging out...");
    }
}