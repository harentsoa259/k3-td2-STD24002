package org.example;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    Team findTeamById(Integer id) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    """
                            select team.id, team.name, continent
                            from team
                            where team.id = ?""");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Team team = new Team();
                Integer teamId = resultSet.getInt("id");
                team.setId(teamId);
                team.setName(resultSet.getString("name"));
                team.setContinent(ContinentEnum.valueOf(resultSet.getString("continent")));
                team.setPlayers(findPlayersByIdTeam(teamId));
                dbConnection.close(connection);
                return team;
            }
            dbConnection.close(connection);
            throw new RuntimeException("Not found team(id=" + id + ")");
        } catch (SQLException e) {
            dbConnection.close(connection);
            throw new RuntimeException(e);
        }
    }

    private List<Player> findPlayersByIdTeam(Integer idTeam) {
        List<Player> players = new ArrayList<>();
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    """
                            select id, name, age, position, goal_nb from player where team_id = ?
                            """);
            preparedStatement.setInt(1, idTeam);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Player player = new Player();
                player.setId(resultSet.getInt("id"));
                player.setName(resultSet.getString("name"));
                player.setAge(resultSet.getInt("age"));
                player.setPosition(PlayerPositionEnum.valueOf(resultSet.getString("position")));
                player.setGoalNb(resultSet.getObject("goal_nb") == null ? null : resultSet.getInt("goal_nb"));
                players.add(player);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        dbConnection.close(connection);
        return players;
    }

    List<Player> findPlayers(int page, int size) {
        throw new RuntimeException("Not supported");
    }

    List<Player> createPlayers(List<Player> newPlayers) {
        throw new RuntimeException("Not supported");
    }

    Team saveTeam(Team teamToSave) {
        throw new RuntimeException("Not supported");
    }

    List<Team> findTeamsByPlayerName(String playerName) {
        throw new RuntimeException("Not supported");
    }

    List<Player> findPlayersByCriteria(String playerName,
                                       PlayerPositionEnum position, String teamName,
                                       ContinentEnum continent, int page, int size) {
        throw new RuntimeException("Not supported");
    }
}