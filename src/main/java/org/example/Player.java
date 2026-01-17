package org.example;
import java.util.Objects;

public class Player {
    private Integer id;
    private Integer age;
    private String name;
    private Integer goalNb;

    public Integer getGoalNb() {
        return goalNb;
    }

    public void setGoalNb(Integer goalNb) {
        this.goalNb = goalNb;
    }

    private PlayerPositionEnum position;
    private Team team;

    public String getTeamName() {
        return team == null ? null : team.getName();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlayerPositionEnum getPosition() {
        return position;
    }

    public void setPosition(PlayerPositionEnum position) {
        this.position = position;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(id, player.id) && Objects.equals(name, player.name) && position == player.position && Objects.equals(team, player.team);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, position, team);
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", age=" + age +
                ", name='" + name + '\'' +
                ", goalNb=" + goalNb +
                ", position=" + position +
                ", teamName=" + getTeamName() +
                '}';
    }
}