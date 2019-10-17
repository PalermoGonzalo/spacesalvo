package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")

public class SalvoController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private GamePlayerRepository gamePlayerRepository;
    @Autowired
    private ShipRepository shipRepository;
    @Autowired
    private ScoresRepository scoresRepository;

    @RequestMapping("/games")
    public ResponseEntity<Map<String, Object>> games(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            response.put("player", "null");
        } else {
            Player player = playerRepository.findByEmail(authentication.getName());
            response.put("player", player.getDto());
        }

        List<Map<String, Object>> gamesdto = gameRepository.findAll().stream().map(game -> game.getDto()).collect(Collectors.toList());
        response.put("games", gamesdto);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.ACCEPTED);
    }

    /*
    public List<Object> findAllGames(){
        return gameRepository
                .findAll()
                .stream()
                .map(game -> game.getDto())
                .collect(Collectors.toList());
    }
    */
    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
            @RequestParam String username,
            @RequestParam String password) {

        if (username.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByEmail(username) !=  null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player(username, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    /*
    public List<Object> findAllPlayers(){
        return playerRepository
                .findAll()
                .stream()
                .map(player -> player.getDto())
                .collect(Collectors.toList());
    }
    */

    @RequestMapping("/game_view/{id}")
    public Map<String, Object> getGame(@PathVariable("id") long id){
        GamePlayer gamePlayer = gamePlayerRepository.getOne(id);

        Map<String, Object> dto = gamePlayer.getGame().getDto();
        dto.put("ships", gamePlayer.getShips().stream().map(ship -> ship.getDto()));
        return dto;
    }

    @RequestMapping("/scores")
    public List<Object> findAllScores(){
        return scoresRepository
                .findAll()
                .stream()
                .map(scores -> scores.getDto())
                .collect(Collectors.toList());
    }

    @RequestMapping("/ships")
    public List<Object> findAllShips(){
        return shipRepository
                .findAll()
                .stream()
                .map(ship -> ship.getDto())
                .collect(Collectors.toList());
    }
    /*
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
            @RequestParam String userName,
            @RequestParam String email, @RequestParam String password) {

        if (userName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByEmail(email) !=  null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player(userName, email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    */
}
