package org.example;

public class Player {
    private final Integer id;
    private final  String name;
    private final int age;
    private final  PlayerPositionEnum position;
    private Team team;
    private Integer goalNb;

    public Player(Integer id, String name, int age, PlayerPositionEnum position, Team team, Integer goalNb) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.position = position;
        this.team = team;
        this.goalNb = goalNb;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public PlayerPositionEnum getPosition() { return position; }
    public Team getTeam() { return team; }
    public Integer getGoalNb() { return goalNb; }

    public void setTeam(Team team) { this.team = team; }
    public void setGoalNb(Integer goalNb) { this.goalNb = goalNb; }
}
