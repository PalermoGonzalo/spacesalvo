package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @OneToMany(mappedBy="game", fetch = FetchType.EAGER)
    // En el mappedBy hay que utilizar el nombre de la referencia utilizada a Game en la clase GamePlayers
    private Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy="game", fetch = FetchType.EAGER)
    private Set<Scores> scores;

    private LocalDateTime creationDate;

    public Game() {
        LocalDateTime dateTime = LocalDateTime.now(ZoneOffset.UTC);
        this.creationDate = dateTime;
        this.gamePlayers = new HashSet<>();
    }

    public Game(long hours) {
        LocalDateTime dateTime = LocalDateTime.now(ZoneOffset.UTC);
        this.creationDate = dateTime.plusHours(hours);
        this.gamePlayers = new HashSet<>();
    }

    public long getId(){
        return id;
    }

    public String getCreationDate() {
        return creationDate.toString().replace("T", " ");
    }

    @JsonIgnore
    public Set<GamePlayer> getGamePlayers(){
        return gamePlayers;
    }

    @JsonIgnore
    public Set<Scores> getScores(){
        return scores;
    }

    public Map<String, Object> getDto(){
        Map<String, Object> gameDto = new LinkedHashMap<>();
        gameDto.put("id", this.id);
        gameDto.put("created", this.creationDate);

        List<Map<String, Object>> gamePlayerDto =
                this.getGamePlayers()
                .stream()
                .map(gamePlayer -> gamePlayer.getDto())
                .collect(Collectors.toList());
        gameDto.put("gamePlayers", gamePlayerDto);
        return gameDto;
    }
}
