package tech.secretgarden.hardcore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

public class EventListener implements Listener {

    private final Data data = new Data();
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
            if (Hardcore.updatedMap.containsKey(uuid)) {
                //uuid is in hashmap
                for (Map.Entry<String, LocalDateTime> entry : Hardcore.updatedMap.entrySet()) {

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
            String uuid = player.getUniqueId().toString();
            LocalDateTime time = LocalDateTime.now().plusHours(24);
            Hardcore.updatedMap.put(uuid, time);

            data.writeFile(plugin);

            player.getInventory().clear();
        }
    }

    @EventHandler
    private void respawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        if (player.getWorld().getName().equalsIgnoreCase("hardcore")) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.teleport(spawn);
                System.out.println("teleporting");
            } , 1);
        }
    }
}
