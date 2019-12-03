package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
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
    @Autowired
    private SalvoRepository salvoRepository;

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

    @RequestMapping(path = "/games/players/{idGamePlayer}/ships", method =  RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getShips(@PathVariable("idGamePlayer") long id, Authentication authentication){
        Map<String, Object> response = new HashMap<>();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            response.put("error", "You must be log to access this game!");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.UNAUTHORIZED);
        }else {
            GamePlayer gamePlayer = gamePlayerRepository.findById(id).orElse(null);
            Player player = playerRepository.findByEmail(authentication.getName());
            if ( gamePlayer == null ){
                response.put("error", "Not such game!");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
            if (gamePlayer.getPlayer().getId() != player.getId()){
                response.put("error", "You are not allowed to see this player information!");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FORBIDDEN);
            }
            response.put("Ships", gamePlayer.getShips().stream().map(sp -> sp.getDto()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        }
    }

    @RequestMapping(path = "/games/players/{idGamePlayer}/ships", method =  RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> setShips(@PathVariable("idGamePlayer") long id, Authentication authentication, @RequestBody List<Ship> ships){
        Map<String, Object> response = new HashMap<>();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            response.put("error", "You must be log to access this game!");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.UNAUTHORIZED);
        }else {
            GamePlayer gamePlayer = gamePlayerRepository.findById(id).orElse(null);
            Player player = playerRepository.findByEmail(authentication.getName());
            if ( gamePlayer == null ){
                response.put("error", "Not such game!");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
            if (gamePlayer.getPlayer().getId() != player.getId()){
                response.put("error", "You are not allowed to see this player information!");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FORBIDDEN);
            }
            if (gamePlayer.getShips().size() > 0) {
                response.put("error", "You have already set your's ships!");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FORBIDDEN);
            }
            if (ships == null || ships.size() != 5) {
                response.put("error", "You must add 5 ships!");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FORBIDDEN);
            }
            if (ships.stream().anyMatch(ship -> this.isOutOfRange(ship))) {
                response.put("error", "Some of your's ships is out of range!");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FORBIDDEN);
            }else if(ships.stream().anyMatch(ship -> this.isNotConsecutive(ship))){
                response.put("error", "Some of your's ships are consecutive!");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FORBIDDEN);
            }else if(this.areOverlapped(ships)){
                response.put("error", "Some of your's ships are overlapped!");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FORBIDDEN);
            }else{
                ships.forEach(ship -> {
                    ship.setGamePlayer(gamePlayer);
                    shipRepository.save(ship);
                });
                //gamePlayerRepository.save(gamePlayer);
                response.put("status", "Ships placed!");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
            }

        }
    }

    private boolean isOutOfRange(Ship ship){
        for(String cell : ship.getLocations()){
            if(!(cell instanceof String) || cell.length() < 2) {
                return true;
            }
            char y = cell.substring(0,1).charAt(0);
            Integer x;
            try{
                x = Integer.parseInt(cell.substring(1));
            }catch(NumberFormatException e){
                x = 99;
            }
            if(x < 1 || x > 10 || y < 'A' || y > 'J'){
                return true;
            }
        }
        return false;
    }

    private boolean isNotConsecutive(Ship ship){
        List<String> cells = ship.getLocations();
        boolean isVertical = cells.get(0).charAt(0) != cells.get(1).charAt(0);
        for(int i = 0; i < cells.size(); i++){
            if(i < cells.size() -1){
                if(isVertical){
                    char yChar = cells.get(i).substring(0,1).charAt(0);
                    char compareChar = cells.get(i + 1).substring(0,1).charAt(0);
                    if(compareChar - yChar != 1){
                        return true;
                    }
                }else{
                    Integer xInt = Integer.parseInt(cells.get(i).substring(1));
                    Integer compareInt = Integer.parseInt(cells.get(i + 1).substring(1));
                    if(compareInt - xInt != 1){
                        return true;
                    }
                }
            }
            for(int j = i + 1; j < cells.size(); j++){
                if(isVertical){
                    if(!cells.get(i).substring(1).equals(cells.get(j).substring(1))){
                        return true;
                    }
                }else{
                    if(!cells.get(i).substring(0,1).equals(cells.get(j).substring(0,1))){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean areOverlapped(List<Ship> ships){
        List<String> allCells = new ArrayList<>();
        ships.forEach(ship -> allCells.addAll(ship.getLocations()));
        for(int i = 0; i < allCells.size(); i++){
            for(int j = i + 1; j < allCells.size(); j++){
                if(allCells.get(i).equals(allCells.get(j))){
                    return true;
                }
            }
        }
        return false;
    }

    @RequestMapping(path="/games/players/{id}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addSalvoes(Authentication authentication, @PathVariable long id, @RequestBody List<String> shots){
        Map<String, Object> response = new HashMap<>();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            response.put("error", "You must be log to join this game!");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.UNAUTHORIZED);
        }else{
            GamePlayer gamePlayer = gamePlayerRepository.findById(id).orElse(null);
            Player player = playerRepository.findByEmail(authentication.getName());
            if ( gamePlayer == null ){
                response.put("error", "Not such game!");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
            if (gamePlayer.getPlayer().getId() != player.getId()){
                response.put("error", "You are not allowed to see this player information!");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FORBIDDEN);
            }
            if(shots.size() != 5){
                response.put("error", "Wrong number of shots!");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FORBIDDEN);
            }else{
                int turn = gamePlayer.getSalvo().size() + 1;
                Salvo salvo = new Salvo(turn, shots);
                salvo.setGamePlayer(gamePlayer);
                salvoRepository.save(salvo);
                response.put("success", "Salvo added!");
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
        }
    }

    @RequestMapping(path="/games/players/{id}/salvoes", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getSalvoes(Authentication authentication, @PathVariable long id){
        Map<String, Object> response = new HashMap<>();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            response.put("error", "You must be log to access this game!");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.UNAUTHORIZED);
        }else {
            GamePlayer gamePlayer = gamePlayerRepository.findById(id).orElse(null);
            Player player = playerRepository.findByEmail(authentication.getName());
            if ( gamePlayer == null ){
                response.put("error", "Not such game!");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
            if (gamePlayer.getPlayer().getId() != player.getId()){
                response.put("error", "You are not allowed to see this player information!");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FORBIDDEN);
            }
            response.put("Salvoes", gamePlayer.getSalvo().stream().map(sp -> sp.getDto()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
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
            response.put("error", "You must be log to access this game!");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.UNAUTHORIZED);
        }else {
            GamePlayer gamePlayer = gamePlayerRepository.getOne(id);
            Player player = playerRepository.findByEmail(authentication.getName());
            if (gamePlayer.getPlayer().getId() != player.getId()) {
                response.put("error", "Usted no pertenece a este juego");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FORBIDDEN);
            } else {
                //GamePlayer gamePlayer = gamePlayerRepository.getOne(id);
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
