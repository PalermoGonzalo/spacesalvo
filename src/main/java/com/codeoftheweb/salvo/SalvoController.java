package com.codeoftheweb.salvo;

import org.omg.CORBA.OBJ_ADAPTER;
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

    @RequestMapping(path = "/games", method = RequestMethod.GET)
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

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> newGame(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            response.put("gameId", "null");
        } else {
            Player player = playerRepository.findByEmail(authentication.getName());
            Game newGame = gameRepository.save(new Game());
            GamePlayer newGamePlayer = gamePlayerRepository.save(new GamePlayer(player, newGame));
            response.put("gamePlayerId", newGamePlayer.getId());
        }
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @RequestMapping(path = "/games/{id}/players", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getGamePlayers(@PathVariable("id") long id, Authentication authentication){
        Map<String, Object> response = new HashMap<>();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            response.put("error", "You must be log first!");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.UNAUTHORIZED);
        }else{
            Game game = gameRepository.findById(id).orElse(null);
            Map<String, Object> gameDto = new HashMap<>();
            response.put("id", game.getId());
            response.put("created", game.getCreationDate());
            response.put("Players", game.getGamePlayers().stream().map(gp -> gp.getSimpleDto() ));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        }
    }
/*
    @RequestMapping(path = "/games/{id}/players", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getGamePlayers(@PathVariable("id") long id, Authentication authentication){
        Map<String, Object> response = new HashMap<>();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            response.put("error", "You must be log first!");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.UNAUTHORIZED);
        }else{
            Game game = gameRepository.findById(id).orElse(null);
            Map<String, Object> gameDto = new HashMap<>();
            response.put("id", game.getId());
            response.put("created", game.getCreationDate());
            response.put("Players", game.getGamePlayers().stream().map(gp -> getPlayerDto(gp) ));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        }
    }

    private Map<String, Object> getPlayerDto(GamePlayer gp) {
        Map<String, Object> dto = gp.getPlayer().getDto();
        dto.put("gpid", gp.getId());
        return dto;
    }
*/
    @RequestMapping(path = "/games/{id}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable("id") long id, Authentication authentication){
        Map<String, Object> response = new HashMap<>();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            response.put("error", "You must be log to join this game!");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.UNAUTHORIZED);
        }else{
            Game game = gameRepository.findById(id).orElse(null);
            if(game == null) {
                response.put("error", "Not such game");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }

            if(game.getGamePlayers().size() > 1){
                response.put("error", "Game is full!");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FORBIDDEN);
            }

            Player player = playerRepository.findByEmail(authentication.getName());
            if(game.getGamePlayers().stream().anyMatch(gp -> gp.getPlayer().getId() == player.getId())){
                response.put("error", "You are already joined to this game!");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FORBIDDEN);
            }

            GamePlayer newGamePlayer = gamePlayerRepository.save(new GamePlayer(player, game));
            response.put("gpId", newGamePlayer.getId());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
        }
    }

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

    @RequestMapping(path = "/game_view/{id}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getGame(@PathVariable("id") long id, Authentication authentication){
        Map<String, Object> response = new HashMap<>();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            response.put("error", "Debe estar logueado para acceder");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.UNAUTHORIZED);
        }else {
            Player player = playerRepository.findByEmail(authentication.getName());
            if (id != player.getId()) {
                response.put("error", "Usted no pertenece a este juego");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FORBIDDEN);
            } else {
                GamePlayer gamePlayer = gamePlayerRepository.getOne(id);

                response = gamePlayer.getGame().getDto();
                response.put("ships", gamePlayer.getShips().stream().map(ship -> ship.getDto()));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.ACCEPTED);
            }
        }
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
}
