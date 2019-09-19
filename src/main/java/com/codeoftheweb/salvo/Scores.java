package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
public class Scores {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="fk_player")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="fk_game")
    private Game game;

    private int score;

    private LocalDateTime finishDate;

    public Scores(Player player, Game game) {
        LocalDateTime dateTime = LocalDateTime.now(ZoneOffset.UTC);
        this.finishDate = dateTime;
        this.game = game;
        this.player = player;
    }
/*
    public Scores(long hours) {
        LocalDateTime dateTime = LocalDateTime.now(ZoneOffset.UTC);
        this.creationDate = dateTime.plusHours(hours);
        this.gamePlayers = new HashSet<>();
    }
*/
    public long getId(){
        return id;
    }

    public String getFinishDate() {
        return finishDate.toString().replace("T", " ");
    }

    @JsonIgnore
    public Game getGames(){return game;}

    @JsonIgnore
    public Player getPlayers(){return player;}
/*
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
*/
}
