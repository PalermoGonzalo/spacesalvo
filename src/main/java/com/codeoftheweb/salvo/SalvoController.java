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





}
