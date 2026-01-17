package org.example;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Team {
    private Integer id;
    private String name;
    private ContinentEnum continent;
    private List<Player> players;


    public Team(Integer id, String name, ContinentEnum continent, List<Player> players) {
        this.id = id;
        this.name = name;
        this.continent = continent;
        this.players = players;
    }

    public Integer getPlayersCount() {
        throw new RuntimeException("Not supported yet.");
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ContinentEnum getContinent() {
        return continent;
    }

    public void setContinent(ContinentEnum continent) {
        this.continent = continent;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        if (players == null) {
            players = new ArrayList<>();
        }
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setTeam(this);
        }
        this.players = players;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(id, team.id) && Objects.equals(name, team.name) && continent == team.continent && Objects.equals(players, team.players);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, continent, players);
    }


    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", continent=" + continent +
                ", players=" + players +
                '}';
    }

    public Integer getPlayerGoals() {
        int goals = 0;
        for (int i = 0; i < players.size(); i++) {
            Integer goal = players.get(i).getGoalNb();
            if(goal == null) {
                throw new RuntimeException("Erreur ...");
            }
            goals = goals + players.get(i).getGoalNb();
        }
        return goals;
    }
}
