package tech.secretgarden.hardcore;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Hardcore extends JavaPlugin {

    public static HashMap<String, LocalDateTime> map = new HashMap<>();
    public static HashMap<String, String> stringMap = new HashMap<>();
    public static HashMap<String, LocalDateTime> updatedMap = new HashMap<>();
    public static HashMap<String, String> updatedStringMap = new HashMap<>();

    private final Data data = new Data();

    @Override
    public void onEnable() {
        System.out.println("Hardcore has loaded");
        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);

        //initial data file creation
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        data.readData(this);

        for (Map.Entry<String, String> entry : stringMap.entrySet()) {
            //coverts date format into LocalDateTime and puts it into map
            LocalDateTime time = LocalDateTime.parse(entry.getValue(), dateFormat);
            map.put(entry.getKey(), time);
        }

        for (Map.Entry<String, LocalDateTime> entry : map.entrySet()) {
            //Only stores entries that are after current time into new updated map
            if (entry.getValue().isAfter(LocalDateTime.now())) {
                updatedMap.put(entry.getKey(), entry.getValue());
            }
        }
        //stores updated map into a new file
        data.writeFile(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("Hardcore shutting down");
    }
}
