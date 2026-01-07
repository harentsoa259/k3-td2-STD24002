package org.example;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private final  Integer id;
    private final  String name;
    private final ContinentEnum continent;
    private final List<Player> players = new ArrayList<>();

    public Team(Integer id, String name, ContinentEnum continent) {
        this.id = id;
        this.name = name;
        this.continent = continent;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public ContinentEnum getContinent() { return continent; }
    public List<Player> getPlayers() { return players; }

    public void addPlayer(Player p) {
        players.add(p);
        p.setTeam(this);
    }

    public int getPlayersGoals() {
        int total = 0;
        for (Player p : players) {
            if (p.getGoalNb() == null) {
                throw new RuntimeException("Le nombre de buts d'un joueur est encore inconnu: " + p.getName());
            }
            total += p.getGoalNb();
        }
        return total;
    }

}
