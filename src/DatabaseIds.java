import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

public class DatabaseIds {
    String starId;
    int genreId;
    HashMap<String, Integer> genreTable;

    String dbtype = "mysql";
    String dbname = "MySQLReadOnly";
    String username = "mytestuser";
    String password = "My6$Password";

    public DatabaseIds() {
        this.starId = null;
        this.genreId = 23;
        this.genreTable = new HashMap<>();
        initializeGenreTable();
    }

    public String getNextStarId() {
        if (this.starId == null) {
            try (Connection conn = DriverManager.getConnection("jdbc:" + dbtype + ":///" + dbname + "?autoReconnect=true&useSSL=false", username, password);
                 PreparedStatement stmt = conn.prepareStatement("SELECT CONCAT('nm', LPAD(MAX(CAST(SUBSTRING(id, 3) AS UNSIGNED)) + 1, 7, '0')) AS next_star_id FROM stars");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    this.starId = rs.getString("next_star_id");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            long numericPart = Long.parseLong(this.starId.substring(2)) + 1;
            this.starId = "nm" + String.format("%07d", numericPart);
        }
        return this.starId;
    }

    public int getNextGenreId() {
        this.genreId = this.genreId + 1;
        return this.genreId;
    }

    public String getStarID(String starName) {
        String starID = null;

        // SQL query to retrieve the star ID based on the star name
        String query = "SELECT id FROM stars WHERE name = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:" + dbtype + ":///" + dbname + "?autoReconnect=true&useSSL=false", username, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the parameter for the star name
            stmt.setString(1, starName);

            // Execute the query
            ResultSet rs = stmt.executeQuery();

            // If the result set has a row, retrieve the star ID
            if (rs.next()) {
                starID = rs.getString("id");
            }
            else {
                starID = getNextStarId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return starID;
    }

    private void initializeGenreTable() {
        // Populate genre hashtable with the provided values in ascending order
        genreTable.put("Action", 1);
        genreTable.put("Adult", 2);
        genreTable.put("Adventure", 3);
        genreTable.put("Animation", 4);
        genreTable.put("Biography", 5);
        genreTable.put("Comedy", 6);
        genreTable.put("Crime", 7);
        genreTable.put("Documentary", 8);
        genreTable.put("Drama", 9);
        genreTable.put("Family", 10);
        genreTable.put("Fantasy", 11);
        genreTable.put("History", 12);
        genreTable.put("Horror", 13);
        genreTable.put("Music", 14);
        genreTable.put("Musical", 15);
        genreTable.put("Mystery", 16);
        genreTable.put("Reality-TV", 17);
        genreTable.put("Romance", 18);
        genreTable.put("Sci-Fi", 19);
        genreTable.put("Sport", 20);
        genreTable.put("Thriller", 21);
        genreTable.put("War", 22);
        genreTable.put("Western", 23);
    }

}
