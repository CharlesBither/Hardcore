package tech.secretgarden.hardcore;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class EventListener implements Listener {

    private final Hardcore plugin;
    public EventListener(Hardcore instance) {
        this.plugin = instance;
    }

    Location spawn = Bukkit.getWorld("world").getSpawnLocation();

    @EventHandler
    private void teleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        String uuid = player.getUniqueId().toString();
        if (e.getTo().getWorld().getName().equalsIgnoreCase("hardcore")) {
            if (Hardcore.map.containsKey(uuid)) {
                //uuid is in hashmap
                for (Map.Entry<String, LocalDateTime> entry : Hardcore.map.entrySet()) {

                    if (entry.getKey().equals(uuid)) {
                        LocalDateTime now = LocalDateTime.now();
                        LocalDateTime end = entry.getValue();

                        if (entry.getValue().isAfter(LocalDateTime.now())) {
                            Duration duration = Duration.between(now, end);
                            int hours = duration.toHoursPart();
                            int minutes = duration.toMinutesPart();
                            int seconds = duration.toSecondsPart();

                            String hoursString = Integer.toString(hours);
                            String minutesString = Integer.toString(minutes);
                            String secondsString = Integer.toString(seconds);

                            e.setCancelled(true);
                            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You must wait " +
                                    hoursString + " hours, " +
                                    minutesString + " minutes, " +
                                    secondsString + " seconds before entering again!");
                        }
                    }
                }
            }
        }

    }

    @EventHandler
    private void death(PlayerDeathEvent e) {
        Player player = e.getEntity();
        if (player.getWorld().getName().equalsIgnoreCase("hardcore")) {
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String uuid = player.getUniqueId().toString();
            LocalDateTime time = LocalDateTime.now().plusHours(24);
            String timeString = LocalDateTime.now().plusHours(24).format(dateFormat);
            Hardcore.map.put(uuid, time);
            try {

                File file = new File(plugin.getDataFolder(), "cooldown.json");
                final Gson gson = new Gson();
                Writer writer = new FileWriter(file, false);
                for (Map.Entry<String, LocalDateTime> entry : Hardcore.map.entrySet()) {
                    Hardcore.stringMap.put(entry.getKey(), timeString);
                }
                player.getInventory().clear();
                gson.toJson(Hardcore.stringMap, writer);
                writer.flush();
                writer.close();

            } catch (IOException x) {
                x.printStackTrace();
            }
        }
    }

    @EventHandler
    private void respawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        if (player.getWorld().getName().equalsIgnoreCase("hardcore")) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.teleport(spawn);
                System.out.println("teleporting");
            } , 2);
        }
    }
}
