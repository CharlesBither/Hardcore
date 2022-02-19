package tech.secretgarden.hardcore;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Hardcore extends JavaPlugin {

    private final Database database = new Database();
    public static ArrayList<String> configList = new ArrayList<>();

    public ArrayList<String> getList() {
        configList.add(getConfig().getString("HOST"));
        configList.add(getConfig().getString("PORT"));
        configList.add(getConfig().getString("DATABASE"));
        configList.add(getConfig().getString("USERNAME"));
        configList.add(getConfig().getString("PASSWORD"));
        return configList;
    }

    @Override
    public void onEnable() {
        System.out.println("Hardcore has loaded");
        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        if (getConfig().getString("HOST") != null) {
            try {
                getList();
                Database.connect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try (Connection connection = database.getPool().getConnection();
                 PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS cooldown (" +
                         "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                         "uuid VARCHAR(36), " +
                         "timestamp TIMESTAMP NOT NULL);")) {
                statement.executeUpdate();

            } catch (SQLException x) {
                x.printStackTrace();
            }
            try (Connection connection = database.getPool().getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT id, timestamp FROM cooldown", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    Timestamp timestamp = rs.getTimestamp("timestamp");
                    LocalDateTime end = timestamp.toLocalDateTime();
                    if (end.isBefore(LocalDateTime.now())) {
                        rs.deleteRow();
                    }
                }

            } catch (SQLException x) {
                x.printStackTrace();
            }
        }
        System.out.println("Connected to database = " + Database.isConnected());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("Hardcore shutting down");
        Database.disconnect();
    }
}
