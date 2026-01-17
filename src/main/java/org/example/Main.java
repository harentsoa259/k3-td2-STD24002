package org.example;
public class Main {
    public static void main(String[] args) {

        DataRetriever dataRetriever = new DataRetriever();

        Team team = dataRetriever.findTeamById(1);

        System.out.println(team);
        System.out.println(team.getPlayerGoals());
    }
}