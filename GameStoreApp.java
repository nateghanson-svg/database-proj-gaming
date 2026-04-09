
import java.sql.*;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.Random;

public class GameStoreApp {
    static Scanner scan = new Scanner(System.in);
    static Random rand = new Random(); 
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:sqlite:C:\\Users\\nateg\\OneDrive\\Attachments\\CS_1103\\CS_1103_FinalProj\\gaming_database.db"
            );
            
            System.out.println("Connected to database successfully!");
            Statement stmt = conn.createStatement();
            
            programStart(conn);

            
            conn.close();
            
        } catch (SQLException e) {
            System.out.println("Connection failed");
            e.printStackTrace();
        }
        
    }
    public static void programStart(Connection conn)
    {
            
        System.out.println("Are you an employee or a client?");
        String userRole = scan.nextLine();
            
            if (userRole.equalsIgnoreCase("employee"))
            {
                EmpMenu(conn);
            }
            else if (userRole.equalsIgnoreCase("client"))
            {
                ClientMenu(conn);
            }
            else
            {
                System.out.println("Please enter either \"employee\" or \"client\"");
                programStart(conn);
            }
    }
    public static void EmpMenu(Connection conn)
    {
        
        while (true) {
        try {
            System.out.println("Select an option:\n1. Restock Game\n2. View Games\n3. Exit");
            int empOption = scan.nextInt();
            scan.nextLine();

            switch (empOption) {
                case 1 -> RestockScreen(conn);
                case 2 -> EmpGameScreen(conn);
                case 3 -> {
                    System.out.println("Exiting employee menu...");
                    return;
                }
                default -> System.out.println("Please enter 1, 2, or 3.");
            }

        } catch (InputMismatchException e) {
            System.out.println("Please enter a valid number.");
            scan.nextLine(); 
        }
    }
    }
    public static void ClientMenu(Connection conn)
    {
         int clientId = -1;

    
    while (true) {
        System.out.println("Are you a returning client? (yes/no)");
        String answer = scan.nextLine().trim().toLowerCase();

        if (answer.equals("yes")) {
            System.out.println("Please enter your client ID:");
            try {
                clientId = scan.nextInt();
                scan.nextLine(); // consume newline
                break;
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number for client ID.");
                scan.nextLine(); // clear invalid input
            }
        } else if (answer.equals("no")) {
            try {
                clientId = getUniqueClientId(conn);
                System.out.println("Your new client ID is: " + clientId);
                break;
            } catch (SQLException e) {
                System.out.println("Error generating client ID. Please try again.");
                e.printStackTrace();
            }
        } else {
            System.out.println("Please type 'yes' or 'no'.");
        }
    }

    // Main client menu loop
    while (true) {
        System.out.println("Select an option:\n1. Purchase Game\n2. View Available Games\n3. View your previous transactions\n4. Exit");
        try {
            int choice = scan.nextInt();
            scan.nextLine(); // consume newline

            switch (choice) {
                case 1 -> PurchaseScreen(conn, clientId);
                case 2 -> AvailableScreen(conn);
                case 3 -> TransactionsScreen(conn, clientId);
                case 4 -> {
                    System.out.println("Exiting client menu...");
                    return;
                }
                default -> System.out.println("Please enter a number between 1 and 4.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Please enter a valid number.");
            scan.nextLine(); // clear invalid input
        }
    }
    }
    public static void RestockScreen(Connection conn)
    {
        while (true) { 
        try {
            System.out.print("Enter the ID of a game to restock: ");
            int restockId = scan.nextInt();
            scan.nextLine(); 

            System.out.print("Enter quantity to add: ");
            int quantity = scan.nextInt();
            scan.nextLine(); 

            String sql = "UPDATE Game_Platform SET in_stock = in_stock + ? WHERE game_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, quantity);
                pstmt.setInt(2, restockId);
                int rows = pstmt.executeUpdate();

                if (rows > 0) {
                    System.out.println("Restocked successfully!");
                } else {
                    System.out.println("Game ID not found. Try again.");
                }
            }

            break; 

        } catch (InputMismatchException e) {
            System.out.println("Please enter a valid number.");
            scan.nextLine(); 
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            break;
        }
        }
    }
    public static void EmpGameScreen(Connection conn)
    {
        
    String sql = """
        SELECT g.game_id, g.title, g.release_year, c.name AS company, 
               p.name AS platform, gp.copies_sold, gp.in_stock
        FROM Games g
        JOIN Companies c ON g.company_id = c.company_id
        JOIN Game_Platform gp ON g.game_id = gp.game_id
        JOIN Platforms p ON gp.platform_id = p.platform_id
        ORDER BY g.game_id, gp.platform_id;
    """;

    try (Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        System.out.printf("%-3s %-30s %-5s %-15s %-15s %-12s %-8s%n",
                          "ID", "Title", "Year", "Company", "Platform", "Copies Sold", "In Stock");
        System.out.println("--------------------------------------------------------------------------------------");

        while (rs.next()) {
            System.out.printf("%-3d %-30s %-5d %-15s %-15s %-12d %-8d%n",
                    rs.getInt("game_id"),
                    rs.getString("title"),
                    rs.getInt("release_year"),
                    rs.getString("company"),
                    rs.getString("platform"),
                    rs.getInt("copies_sold"),
                    rs.getInt("in_stock"));
        }

    } catch (SQLException e) {
        System.out.println("Error retrieving games: " + e.getMessage());
    }

    System.out.println(); 
    }
    
    public static int getUniqueClientId(Connection conn) throws SQLException {
        int clientId;
        boolean unique = false;

        while (!unique) {
            clientId = rand.nextInt(100) + 1; 

            String query = "SELECT COUNT(*) AS count FROM Transactions WHERE client_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, clientId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next() && rs.getInt("count") == 0) {
                    unique = true;
                    return clientId;
                }
            }
        }
        return -1; 
    }
    public static void PurchaseScreen(Connection conn, int clientId)
    {
         try {
        // Show available games with stock
        System.out.println("Available Games:");
        String sqlGames = """
            SELECT g.game_id, g.title, p.platform_id, p.name AS platform, gp.in_stock
            FROM Games g
            JOIN Game_Platform gp ON g.game_id = gp.game_id
            JOIN Platforms p ON gp.platform_id = p.platform_id
            WHERE gp.in_stock > 0
            ORDER BY g.game_id, gp.platform_id;
        """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlGames)) {
            System.out.printf("%-3s %-30s %-15s %-8s%n", "ID", "Title", "Platform", "In Stock");
            System.out.println("----------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-3d %-30s %-15s %-8d%n",
                        rs.getInt("game_id"),
                        rs.getString("title"),
                        rs.getString("platform"),
                        rs.getInt("in_stock"));
            }
        }

        // Ask client which game and platform to purchase
        System.out.println("Enter the Game ID you want to purchase:");
        int gameId = scan.nextInt();
        scan.nextLine();

        System.out.println("Enter the Platform ID you want to purchase on:");
        int platformId = scan.nextInt();
        scan.nextLine();

        System.out.println("Enter quantity to purchase:");
        int quantity = scan.nextInt();
        scan.nextLine();

        // Check stock
        String stockSql = "SELECT in_stock FROM Game_Platform WHERE game_id = ? AND platform_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(stockSql)) {
            pstmt.setInt(1, gameId);
            pstmt.setInt(2, platformId);
            ResultSet rsStock = pstmt.executeQuery();
            if (rsStock.next()) {
                int inStock = rsStock.getInt("in_stock");
                if (quantity > inStock) {
                    System.out.println("Not enough stock. Only " + inStock + " available.");
                    return;
                }
            } else {
                System.out.println("Game or platform not found.");
                return;
            }
        }

        // Deduct stock
        String updateSql = "UPDATE Game_Platform SET in_stock = in_stock - ? WHERE game_id = ? AND platform_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, gameId);
            pstmt.setInt(3, platformId);
            pstmt.executeUpdate();
        }

        // Insert transaction
        String insertSql = "INSERT INTO Transactions (game_id, platform_id, quantity, transaction_type, client_id) VALUES (?, ?, ?, 'purchase', ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setInt(1, gameId);
            pstmt.setInt(2, platformId);
            pstmt.setInt(3, quantity);
            pstmt.setInt(4, clientId);
            pstmt.executeUpdate();
        }

        System.out.println("Purchase successful!");
    } catch (SQLException e) {
        System.out.println("Database error: " + e.getMessage());
        e.printStackTrace();
    } catch (InputMismatchException e) {
        System.out.println("Invalid input. Please try again.");
        scan.nextLine(); // clear invalid input
    }
    }
    public static void AvailableScreen(Connection conn)
    {
        String sql = """
        SELECT g.game_id, g.title, g.release_year, c.name AS company, 
               p.name AS platform, gp.in_stock
        FROM Games g
        JOIN Companies c ON g.company_id = c.company_id
        JOIN Game_Platform gp ON g.game_id = gp.game_id
        JOIN Platforms p ON gp.platform_id = p.platform_id
        WHERE gp.in_stock > 0
        ORDER BY g.game_id, gp.platform_id;
    """;

    try (Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        System.out.printf("%-3s %-30s %-5s %-15s %-15s %-8s%n",
                          "ID", "Title", "Year", "Company", "Platform", "In Stock");
        System.out.println("---------------------------------------------------------------------");

        while (rs.next()) {
            System.out.printf("%-3d %-30s %-5d %-15s %-15s %-8d%n",
                    rs.getInt("game_id"),
                    rs.getString("title"),
                    rs.getInt("release_year"),
                    rs.getString("company"),
                    rs.getString("platform"),
                    rs.getInt("in_stock"));
        }

    } catch (SQLException e) {
        System.out.println("Error retrieving available games: " + e.getMessage());
    }

    System.out.println();
    }
    public static void TransactionsScreen(Connection conn, int clientId)
    {
        String sql = """
        SELECT t.transaction_id, g.title, p.name AS platform, t.quantity, t.transaction_type, t.transaction_date
        FROM Transactions t
        JOIN Games g ON t.game_id = g.game_id
        JOIN Platforms p ON t.platform_id = p.platform_id
        WHERE t.client_id = ?
        ORDER BY t.transaction_date DESC;
    """;

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, clientId);
        ResultSet rs = pstmt.executeQuery();

        System.out.printf("%-3s %-30s %-15s %-8s %-10s %-20s%n",
                "ID", "Title", "Platform", "Qty", "Type", "Date");
        System.out.println("--------------------------------------------------------------------------");

        while (rs.next()) {
            System.out.printf("%-3d %-30s %-15s %-8d %-10s %-20s%n",
                    rs.getInt("transaction_id"),
                    rs.getString("title"),
                    rs.getString("platform"),
                    rs.getInt("quantity"),
                    rs.getString("transaction_type"),
                    rs.getString("transaction_date"));
        }

    } catch (SQLException e) {
        System.out.println("Error retrieving transactions: " + e.getMessage());
    }
    }
}
