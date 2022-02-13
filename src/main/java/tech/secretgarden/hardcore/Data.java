package tech.secretgarden.hardcore;

import com.google.gson.Gson;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Data {

    public void writeFile(Plugin plugin) {
        File file = new File(plugin.getDataFolder(), "cooldown.json");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        try {
            if (!file.exists()) {
                file.createNewFile();
                System.out.println("created file");
            }
            Gson gson = new Gson();
            Writer writer = new FileWriter(file, false);
            for (Map.Entry<String, LocalDateTime> entry : Hardcore.updatedMap.entrySet()) {
                String timeString = entry.getValue().format(dateFormat);
                Hardcore.updatedStringMap.put(entry.getKey(), timeString);
            }
            gson.toJson(Hardcore.updatedStringMap, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readData(Plugin plugin) {
        File file = new File(plugin.getDataFolder(), "cooldown.json");
        try {
            plugin.getDataFolder().mkdir();
            if (!file.exists()) {
                file.createNewFile();
                System.out.println("created file");
            }

            Gson gson = new Gson();
            Reader reader = new FileReader(file);
            HashMap<String, String> readData = gson.fromJson(reader, HashMap.class);
            if (readData != null) {
                //reads file and puts everything into stringMap
                Hardcore.stringMap.putAll(readData);
            }
            reader.close();
            file.delete();
            System.out.println("deleted file");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
