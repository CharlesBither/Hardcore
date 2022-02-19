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

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class EventListener implements Listener {

    private final Database database = new Database();
    private final Hardcore plugin;
    public EventListener(Hardcore instance) {
        this.plugin = instance;
    }

    Location spawn = Bukkit.getWorld("world").getSpawnLocation();

    @EventHandler
    private void teleport(PlayerTeleportEvent e) {
        LocalDateTime now = LocalDateTime.now();
        Player player = e.getPlayer();
        String uuid = player.getUniqueId().toString();
        if (e.getTo().getWorld().getName().equalsIgnoreCase("hardcore") ||
                e.getTo().getWorld().getName().equalsIgnoreCase("hardcore_nether") ||
                e.getTo().getWorld().getName().equalsIgnoreCase("hardcore_the_end")) {

            try (Connection connection = database.getPool().getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT timestamp FROM cooldown WHERE uuid = ?")) {
                statement.setString(1, uuid);
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    Timestamp result = rs.getTimestamp("timestamp");
                    LocalDateTime end = result.toLocalDateTime();
                    if (end.isAfter(now)) {
                        e.setCancelled(true);
                        Duration duration = Duration.between(now, end);
                        String hour = Integer.toString(duration.toHoursPart());
                        String minute = Integer.toString(duration.toMinutesPart());
                        String second = Integer.toString(duration.toSecondsPart());
                        player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You must wait " +
                                hour + " hours, " +
                                minute + " minutes, " +
                                second + " seconds before entering again!");
                    }
                }
            } catch (SQLException x) {
                x.printStackTrace();
            }
        }
    }

    @EventHandler
    private void death(PlayerDeathEvent e) {
        Player player = e.getEntity();
        if (player.getWorld().getName().equalsIgnoreCase("hardcore") ||
                player.getWorld().getName().equalsIgnoreCase("hardcore_nether") ||
                player.getWorld().getName().equalsIgnoreCase("hardcore_the_end")) {
            String uuid = player.getUniqueId().toString();
            LocalDateTime time = LocalDateTime.now().plusHours(24);
            Timestamp timestamp = Timestamp.valueOf(time);

            try (Connection connection = database.getPool().getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO cooldown (uuid, timestamp) VALUES (?, ?);")) {
                statement.setString(1, uuid);
                statement.setTimestamp(2, timestamp);
                statement.executeUpdate();
            } catch (SQLException x) {
                x.printStackTrace();
            }
            player.getInventory().clear();
        }
    }

    @EventHandler
    private void respawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        if (player.getWorld().getName().equalsIgnoreCase("hardcore") ||
                player.getWorld().getName().equalsIgnoreCase("hardcore_nether") ||
                player.getWorld().getName().equalsIgnoreCase("hardcore_the_end")) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.teleport(spawn);
                System.out.println("teleporting");
            } , 1);
        }
    }
}
