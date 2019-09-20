package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.persistence.ElementCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(
		PlayerRepository playerRepository,
		GameRepository gameRepository,
		ShipRepository shipRepository,
		SalvoRepository salvoRepository,
		ScoresRepository scoresRepository,
		GamePlayerRepository gamePlayerRepository) {
		return (args) -> {
			Player player1 = new Player("pedro@gmail.com");
			Player player2 = new Player("mateo@gmail.com");
			Player player3 = new Player("marcos@gmail.com");

			playerRepository.save(player1);
			playerRepository.save(player2);
			playerRepository.save(player3);

			Game game1 = new Game();
			Game game2 = new Game(1);
			Game game3 = new Game(2);

			gameRepository.save(game1);
			gameRepository.save(game2);
			gameRepository.save(game3);

			GamePlayer gp1 = new GamePlayer(player1, game1);
			GamePlayer gp2 = new GamePlayer(player2, game1);
			GamePlayer gp3 = new GamePlayer(player3, game2);
			GamePlayer gp4 = new GamePlayer(player2, game2);
			GamePlayer gp5 = new GamePlayer(player1, game3);
			GamePlayer gp6 = new GamePlayer(player3, game3);

			gamePlayerRepository.save(gp1);
			gamePlayerRepository.save(gp2);
			gamePlayerRepository.save(gp3);
			gamePlayerRepository.save(gp4);
			gamePlayerRepository.save(gp5);
			gamePlayerRepository.save(gp6);

			Ship sp1 = new Ship ("Destroyer", gp1, Arrays.asList("G5","G6","G7","G8"));
			Ship sp2 = new Ship ("Submarine", gp1, Arrays.asList("D1","D2"));
			Ship sp3 = new Ship ("Patrol Boat", gp1, Arrays.asList("C1","C2","C3"));
			Ship sp4 = new Ship ("Destroyer", gp2, Arrays.asList("C1","C2","C3","C4"));
			Ship sp5 = new Ship ("Submarine", gp2, Arrays.asList("A1","A2"));
			Ship sp6 = new Ship ("Patrol Boat", gp2, Arrays.asList("G1","G2","G3"));

			shipRepository.save(sp1);
			shipRepository.save(sp2);
			shipRepository.save(sp3);
			shipRepository.save(sp4);
			shipRepository.save(sp5);
			shipRepository.save(sp6);

			Salvo sv1 = new Salvo(1, gp1, Arrays.asList("A4","D8"));
			Salvo sv2 = new Salvo(2, gp1, Arrays.asList("I3","B1"));
			Salvo sv3 = new Salvo(3, gp1, Arrays.asList("G9","C8"));
			Salvo sv4 = new Salvo(1, gp2, Arrays.asList("G1","D9"));
			Salvo sv5 = new Salvo(2, gp2, Arrays.asList("I4","E5"));
			Salvo sv6 = new Salvo(3, gp2, Arrays.asList("E4","C2"));

			salvoRepository.save(sv1);
			salvoRepository.save(sv2);
			salvoRepository.save(sv3);
			salvoRepository.save(sv4);
			salvoRepository.save(sv5);
			salvoRepository.save(sv6);

			Scores sc1 = new Scores( player1, game1, 1);
			Scores sc2 = new Scores( player2, game1, 0);
			Scores sc3 = new Scores( player3, game2, 0);
			Scores sc4 = new Scores( player2, game2, 1);
			Scores sc5 = new Scores( player1, game3, 0.5);
			Scores sc6 = new Scores( player3, game3, 0.5);
		};
	}

}
