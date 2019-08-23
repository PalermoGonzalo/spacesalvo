package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

@org.springframework.web.bind.annotation.RestController

public class RestController {

    @Autowired
    private GameRepository repo;

    @RequestMapping("/game")
    /*
    public Game getCreationDate() {
        return repo.getCreationDate();
    }
    */

    @RequestMapping("/players")
}
