package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")

public class SalvoController {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private ShipRepository shipRepository;

    @RequestMapping("/games")
    public List<Object> findAllGames(){
        return gameRepository
                .findAll()
                .stream()
                .map(game -> game.getDto())
                .collect(Collectors.toList());
    }

    @RequestMapping("/players")
    public List<Object> findAllPlayers(){
        return playerRepository
                .findAll()
                .stream()
                .map(player -> player.getDto())
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
