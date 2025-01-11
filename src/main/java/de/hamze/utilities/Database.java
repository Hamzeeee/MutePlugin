package de.hamze.utilities;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.hamze.main.MutePlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Database {
    private final String path = FileHandler.hikariConfigPath;
    private final HikariConfig config = new HikariConfig(path);
    private final HikariDataSource ds = new HikariDataSource(config);

    /**
     * <p>Check whether the player exists or not.</p>
     *
     * @param uuid The UUID of the player to check.
     * @return boolean
     *
     * <ul>
     *     <li><strong>True</strong> - Player exists.</li>
     *     <li><strong>False</strong> - Player doesn't exist.</li>
     * </ul>
     */
    public boolean playerExists(@NotNull UUID uuid) {
        try (Connection connection = getConnection(ds);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT UUID FROM muted_player WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * If the player doesn't exist in the database, this function will create a profile for that {@code player}.
     * @param uuid
     */
    public void createPlayer(@NotNull UUID uuid) {
        try (Connection connection = getConnection(ds);
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO muted_player(UUID, isMuted, date) VALUES (?, ?, ?)")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setBoolean(2, false);
            preparedStatement.setInt(3, 0);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * This method queries the database to check for muted players with the {@code UUID}.
     *
     * @param uuid
     * @return {@code boolean}
     * <ul>
     *     <li><strong>True</strong> - player is muted</li>
     *     <li><strong>False</strong> - player is NOT muted</li>
     * </ul>
     *
     * @throws SQLException throws an exception if something went wrong
     */
    public boolean isMuted(@NotNull UUID uuid) {
        try (Connection connection = getConnection(ds);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT isMuted FROM muted_player WHERE UUID = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("isMuted");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.err.println("Something went wrong!");
        return false;
    }


    public void mutePlayer(@NotNull UUID uuid, boolean mute) {
        try (Connection connection = getConnection(ds);
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE muted_player SET isMuted = ?, date = CURRENT_TIMESTAMP WHERE UUID = ?")) {
            preparedStatement.setBoolean(1, mute);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void getMutedPlayer() {
        try (Connection connection = getConnection(ds);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT UUID FROM muted_player WHERE isMuted = true")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                MutePlugin.getPlugin().muteListeners.add(UUID.fromString(resultSet.getString("UUID")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static Connection getConnection(HikariDataSource ds) {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
