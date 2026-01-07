package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    private final DBConnection db;

    public DataRetriever(DBConnection db) {
        this.db = db;
    }

    // ======================== FIND TEAM BY ID ========================
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

    // ======================== FIND PLAYERS WITH PAGINATION ========================
    public List<Player> findPlayers(int page, int size) throws SQLException {
        int offset = (page - 1) * size;
        String sql = """
            SELECT p.*, t.id tid, t.name tname, t.continent
            FROM player p
            LEFT JOIN team t ON p.id_team = t.id
            ORDER BY p.id
            LIMIT ? OFFSET ?
        """;

        List<Player> players = new ArrayList<>();
        try (Connection c = db.getDBConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, size);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Team team = rs.getObject("tid") == null ? null :
                        new Team(rs.getInt("tid"), rs.getString("tname"),
                                ContinentEnum.valueOf(rs.getString("continent")));

                Player p = new Player(rs.getInt("id"), rs.getString("name"), rs.getInt("age"),
                        PlayerPositionEnum.valueOf(rs.getString("position")), team,
                        (Integer) rs.getObject("goal_nb"));
                players.add(p);
            }
        }
        return players;
    }

    // ======================== CREATE PLAYERS ========================
    public List<Player> createPlayers(List<Player> players) throws SQLException {
        String insert = "INSERT INTO player(name, age, position, id_team, goal_nb) VALUES (?,?,?,?,?)";
        List<Player> created = new ArrayList<>();

        try (Connection c = db.getDBConnection()) {
            c.setAutoCommit(false);

            for (Player p : players) {
                try (PreparedStatement check = c.prepareStatement("SELECT COUNT(*) FROM player WHERE name = ?")) {
                    check.setString(1, p.getName());
                    ResultSet rs = check.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new RuntimeException("Le joueur " + p.getName() + " existe déjà !");
                    }
                }

                try (PreparedStatement ps = c.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, p.getName());
                    ps.setInt(2, p.getAge());
                    ps.setString(3, p.getPosition().name());
                    if (p.getTeam() != null) {
                        ps.setInt(4, p.getTeam().getId());
                    } else {
                        ps.setNull(4, Types.INTEGER);
                    }
                    if (p.getGoalNb() != null) {
                        ps.setInt(5, p.getGoalNb());
                    } else {
                        ps.setNull(5, Types.INTEGER);
                    }
                    ps.executeUpdate();

                    ResultSet keys = ps.getGeneratedKeys();
                    if (keys.next()) {
                        p = new Player(keys.getInt(1), p.getName(), p.getAge(), p.getPosition(), p.getTeam(), p.getGoalNb());
                        created.add(p);
                    }
                }
            }

            c.commit();
        }
        return created;
    }

    // ======================== SAVE TEAM ========================
    public void saveTeam(Team team) throws SQLException {
        if (team.getId() == null) return;

        try (Connection c = db.getDBConnection()) {
            c.setAutoCommit(false);

            // Détacher tous les joueurs existants
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
    }

    // ======================== FIND TEAMS BY PLAYER NAME ========================
    public List<Team> findTeamsByPlayerName(String name) throws SQLException {
        String sql = "SELECT DISTINCT t.* FROM team t JOIN player p ON t.id=p.id_team WHERE LOWER(p.name) LIKE ?";
        List<Team> teams = new ArrayList<>();

        try (Connection c = db.getDBConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + name.toLowerCase() + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                teams.add(new Team(rs.getInt("id"), rs.getString("name"),
                        ContinentEnum.valueOf(rs.getString("continent"))));
            }
        }
        return teams;
    }

    // ======================== FIND PLAYERS BY CRITERIA ========================
    public List<Player> findPlayersByCriteria(String playerName, PlayerPositionEnum position,
                                              String teamName, ContinentEnum continent,
                                              int page, int size) throws SQLException {
        StringBuilder sql = new StringBuilder("""
            SELECT p.*, t.id tid, t.name tname, t.continent
            FROM player p
            LEFT JOIN team t ON p.id_team=t.id
            WHERE 1=1
        """);
        List<Object> params = new ArrayList<>();

        if (playerName != null) { sql.append(" AND LOWER(p.name) LIKE ?"); params.add("%" + playerName.toLowerCase() + "%"); }
        if (position != null) { sql.append(" AND p.position::text = ?"); params.add(position.name()); } // CAST ENUM -> text
        if (teamName != null) { sql.append(" AND LOWER(t.name) LIKE ?"); params.add("%" + teamName.toLowerCase() + "%"); }
        if (continent != null) { sql.append(" AND t.continent = ?"); params.add(continent.name()); }

        sql.append(" ORDER BY p.id LIMIT ? OFFSET ?");
        params.add(size);
        params.add((page - 1) * size);

        List<Player> players = new ArrayList<>();
        try (Connection c = db.getDBConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Team team = rs.getObject("tid") == null ? null :
                        new Team(rs.getInt("tid"), rs.getString("tname"),
                                ContinentEnum.valueOf(rs.getString("continent")));
                players.add(new Player(rs.getInt("id"), rs.getString("name"), rs.getInt("age"),
                        PlayerPositionEnum.valueOf(rs.getString("position")), team,
                        (Integer) rs.getObject("goal_nb")));
            }
        }
        return players;
    }
}
