package tech.secretgarden.hardcore;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Hardcore extends JavaPlugin {

    public static HashMap<String, LocalDateTime> map = new HashMap<>();
    public static HashMap<String, String> stringMap = new HashMap<>();
    public HashMap<String, LocalDateTime> updatedMap = new HashMap<>();
    public HashMap<String, String> updatedStringMap = new HashMap<>();

    @Override
    public void onEnable() {
        System.out.println("Hardcore has loaded");
        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);

        //initial data file creation
        File file = new File(getDataFolder(), "cooldown.json");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        try {
            getDataFolder().mkdir();
            if (!file.exists()) {
                file.createNewFile();
                System.out.println("created file");
            }

            Gson gson = new Gson();
            Reader reader = new FileReader(file);
            HashMap<String, String> readData = gson.fromJson(reader, HashMap.class);
            if (readData != null) {
                stringMap.putAll(readData);
            }
            reader.close();
            file.delete();
            System.out.println("deleted file");

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, String> entry : stringMap.entrySet()) {
            LocalDateTime time = LocalDateTime.parse(entry.getValue(), dateFormat);
            map.put(entry.getKey(), time);
        }

        for (Map.Entry<String, LocalDateTime> entry : map.entrySet()) {
            if (entry.getValue().isAfter(LocalDateTime.now())) {
                updatedMap.put(entry.getKey(), entry.getValue());
            }
        }

        /*
        Iterator<HashMap.Entry<String, LocalDateTime>> iterator = map.entrySet().iterator();
        HashMap.Entry<String, LocalDateTime> entry = iterator.next();
        if (entry.getValue().isBefore(LocalDateTime.now())) {
            iterator.remove();
        }

         */
        //map.entrySet().removeIf(entry -> entry.getValue().isBefore(LocalDateTime.now()));

        writeFile();

        //loads data into stringMap
        /*
        try {
            Gson gson = new Gson();
            Reader reader = new FileReader(file);
            HashMap<String, String> readData = gson.fromJson(reader, HashMap.class);
            if (readData != null) {
                stringMap.putAll(readData);
                for (Map.Entry<String, String> entry : stringMap.entrySet()) {
                    LocalDateTime time = LocalDateTime.parse(entry.getValue(), dateFormat);
                    map.put(entry.getKey(), time);
                }
                map.entrySet().removeIf(entry -> entry.getValue().isBefore(LocalDateTime.now()));
            }
            file.delete();
            System.out.println("deleted file");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        try {
            if (!file.exists()) {
                file.createNewFile();
                System.out.println("created file");
            }
            final Gson gson = new Gson();
            Writer writer = new FileWriter(file, false);
            for (Map.Entry<String, LocalDateTime> entry : Hardcore.map.entrySet()) {
                String timeString = entry.getValue().format(dateFormat);
                Hardcore.stringMap.put(entry.getKey(), timeString);
            }
            gson.toJson(Hardcore.stringMap, writer);
            writer.flush();
            writer.close();

        } catch (IOException x) {
            x.printStackTrace();
        }

         */
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("Hardcore shutting down");
    }

    private void writeFile() {
        Bukkit.getScheduler().runTaskLater(this, () -> {
            File file = new File(getDataFolder(), "cooldown.json");
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            try {
                if (!file.exists()) {
                    file.createNewFile();
                    System.out.println("created file");
                }
                Gson gson = new Gson();
                Writer writer = new FileWriter(file, false);
                for (Map.Entry<String, LocalDateTime> entry : updatedMap.entrySet()) {
                    String timeString = entry.getValue().format(dateFormat);
                    updatedStringMap.put(entry.getKey(), timeString);
                }
                gson.toJson(updatedStringMap, writer);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 2);
    }
}
