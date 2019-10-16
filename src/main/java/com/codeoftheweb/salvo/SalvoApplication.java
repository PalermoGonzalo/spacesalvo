package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.persistence.ElementCollection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder(){
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
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
			Player player1 = new Player("pedro","pedro@gmail.com",  passwordEncoder().encode("ASD123"));
			Player player2 = new Player("mateo","mateo@gmail.com", passwordEncoder().encode("DSA321"));
			Player player3 = new Player("marcos","marcos@gmail.com", passwordEncoder().encode("POW987"));

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

			scoresRepository.save(sc1);
			scoresRepository.save(sc2);
			scoresRepository.save(sc3);
			scoresRepository.save(sc4);
			scoresRepository.save(sc5);
			scoresRepository.save(sc6);
		};
	}

}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {
	@Autowired
	PlayerRepository playerRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userName-> {
			Player player = playerRepository.findByEmail(userName);
			if (player != null){
				return new User(player.getEmail(), player.getPassword(), AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + userName);
			}
		});
	}
}


@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
                .antMatchers("/web/games.html", "/web/js/games.js", "/api/scores", "/web/login.html").permitAll()
				.antMatchers("/rest/**").hasAuthority("ADMIN")
				.antMatchers("/**").hasAuthority("USER")
				//.anyRequest().permitAll()
                .and()
                .formLogin()
				.usernameParameter("username")
                .passwordParameter("password")
                .loginPage("/api/login")
                .successHandler((req, res, auth) -> clearAuthenticationAttributes(req))
				.failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
				.and()
                .logout()
                .logoutUrl("/api/logout")
				.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                .and()
                .csrf()
                .disable();
	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}
}