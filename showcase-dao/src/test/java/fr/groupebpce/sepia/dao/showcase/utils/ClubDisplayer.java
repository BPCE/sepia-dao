package fr.groupebpce.sepia.dao.showcase.utils;

import fr.groupebpce.sepia.dao.showcase.entity.Club;
import fr.groupebpce.sepia.dao.showcase.entity.Player;

import java.util.List;

public class ClubDisplayer {

    public static void display(Club club) {
        System.out.printf("%-20s (%3s)%n", club.getName(), club.getCountry().getCode());
    }

    public static void display(List<Club> clubs) {
        clubs.forEach(ClubDisplayer::display);
    }

    public static void displayWithStadium(Club club) {
        System.out.printf("%-20s (%3s) %5s %-22s (%5s) %n",
                club.getName(),
                club.getCountry().getCode(),
                "",
                club.getStadium().getName(),
                club.getStadium().getCapacity());
    }

    public static void displayWithStadium(List<Club> clubs) {
        clubs.forEach(ClubDisplayer::displayWithStadium);
    }

    public static void displayWithStadiumAndPlayers(List<Club> clubs) {
        clubs.forEach(ClubDisplayer::displayWithStadiumAndPlayers);
    }

    public static void displayWithPlayers(List<Club> clubs) {
        clubs.forEach(ClubDisplayer::displayWithPlayers);
    }

    public static void displayWithStadiumAndPlayers(Club club) {
        displayWithStadium(club);
        displayPlayers(club.getPlayers());
    }

    private static void displayWithPlayers(Club club) {
        display(club);
        displayPlayers(club.getPlayers());
    }

    private static void displayPlayers(List<Player> players) {
        players.forEach(ClubDisplayer::displayPlayer);
    }

    private static void displayPlayer(Player player) {
        System.out.printf(" - %s (%s) %n",
                player.getName(),
                player.getCountry().getCode()
        );
    }
}
