package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    //@Column(name = "usuario")
    private String userName;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<Scores> scores;

    public Player() { }

    public Player(String user) {
        userName = user;
        this.gamePlayers = new HashSet<>();
    }

    public String getUserName() {
        return userName;
    }

    public Long getId(){return id;}

    @JsonIgnore
    public Set<GamePlayer> getGamePlayers(){return gamePlayers;}

    public Map<String, Object> getDto(){
        Map<String, Object> playerDto = new LinkedHashMap<>();
        playerDto.put("id", this.getId());
        playerDto.put("email", this.getUserName());
        return playerDto;
    }
}

