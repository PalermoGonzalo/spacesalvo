package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    //@Column(name = "usuario")
    private String userName;
    private String email;
    private String password;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<Scores> scores;

    public Player() { }

    public Player(String user, String email, String password) {
        this.userName = user;
        this.email = email;
        this.password = password;
        this.gamePlayers = new HashSet<>();
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() { return email; }

    public String getPassword() { return password; }

    public Long getId(){return id;}

    @JsonIgnore
    public Set<Scores> getScores(){return scores;}

    @JsonIgnore
    public Set<GamePlayer> getGamePlayers(){return gamePlayers;}

    public Map<String, Object> getDto(){
        Map<String, Object> playerDto = new LinkedHashMap<>();
        playerDto.put("id", this.getId());
        playerDto.put("email", this.getUserName());
        //playerDto.put("wins", this.scores.stream().filter(score -> score.getScore() == 1).count());
        return playerDto;
    }
}

