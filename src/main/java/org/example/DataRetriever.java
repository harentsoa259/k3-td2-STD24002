package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    private final DBConnection db;

    public DataRetriever(DBConnection db) {
        this.db = db;
    }

    public Team findTeamById(Integer id) throws SQLException {
        String sql = """
            SELECT t.id tid, t.name tname, t.continent,
                   p.id pid, p.name pname, p.age, p.position, p.goal_nb
            FROM team t
            LEFT JOIN player p ON t.id = p.id_team
            WHERE t.id = ?
        """;

        Team team = null;
        try (Connection c = db.getDBConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                if (team == null) {
                    team = new Team(rs.getInt("tid"), rs.getString("tname"),
                            ContinentEnum.valueOf(rs.getString("continent")));
                }

                if (rs.getObject("pid") != null) {
                    Player p = new Player(
                            rs.getInt("pid"),
                            rs.getString("pname"),
                            rs.getInt("age"),
                            PlayerPositionEnum.valueOf(rs.getString("position")),
                            team,
                            (Integer) rs.getObject("goal_nb") // récupérer goal_nb
                    );
                    team.addPlayer(p);
                }
            }
        }
        return team;
    }

    // Sauvegarder une équipe (assigner les joueurs)
    public Team saveTeam(Team team) throws SQLException {
        if (team.getId() == null) return null;

        try (Connection c = db.getDBConnection()) {
            c.setAutoCommit(false);

            try (PreparedStatement ps = c.prepareStatement("UPDATE player SET id_team=NULL WHERE id_team=?")) {
                ps.setInt(1, team.getId());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = c.prepareStatement("UPDATE player SET id_team=? WHERE id=?")) {
                for (Player p : team.getPlayers()) {
                    ps.setInt(1, team.getId());
                    ps.setInt(2, p.getId());
                    ps.executeUpdate();
                }
            }

            c.commit();
        }

        return team;
    }
}
