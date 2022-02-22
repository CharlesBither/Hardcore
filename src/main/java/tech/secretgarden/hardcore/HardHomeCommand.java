package tech.secretgarden.hardcore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HardHomeCommand implements CommandExecutor {

    Database database = new Database();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String uuid = player.getUniqueId().toString();
            World hardcore = Bukkit.getWorld("Hardcore");
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("home")) {
                    try (Connection connection = database.getPool().getConnection();
                         PreparedStatement statement = connection.prepareStatement("SELECT * FROM home WHERE uuid = ?")) {
                        statement.setString(1, uuid);
                        ResultSet rs = statement.executeQuery();
                        if (rs.next()) {
                            int x = rs.getInt("x");
                            int y = rs.getInt("y");
                            int z = rs.getInt("z");
                            Location home = new Location(hardcore, x, y, z);
                            player.teleport(home);
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "You do not have a home. Set one with /hc sethome");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                if (args[0].equalsIgnoreCase("sethome")) {
                    //Setting home location
                    if (player.getWorld().getName().equalsIgnoreCase("Hardcore")) {
                        String worldName = "Hardcore";
                        Location location = player.getLocation();
                        int x = location.getBlockX();
                        int y = location.getBlockY();
                        int z = location.getBlockZ();
                        //looks for previous entry in db, if there is one, it will be deleted.
                        try (Connection connection = database.getPool().getConnection();
                             PreparedStatement statement = connection.prepareStatement("SELECT * FROM home", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                            ResultSet rs = statement.executeQuery();
                            while(rs.next()) {
                                rs.deleteRow();
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        //creates new entry in db
                        try (Connection connection = database.getPool().getConnection();
                        PreparedStatement statement = connection.prepareStatement("INSERT INTO home (uuid, x, y, z, world) VALUES (?,?,?,?,?)")) {
                            statement.setString(1, uuid);
                            statement.setInt(2, x);
                            statement.setInt(3, y);
                            statement.setInt(4, z);
                            statement.setString(5, worldName);
                            statement.executeUpdate();
                            player.sendMessage(ChatColor.GREEN + "Successfully added home!");
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You can only set 1 home in the Hardcore world");
                    }
                }
            }
        }
        return false;
    }
}
