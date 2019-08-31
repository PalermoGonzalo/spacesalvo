package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(
		PlayerRepository playerRepository,
		GameRepository gameRepository,
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
		};
	}

}
