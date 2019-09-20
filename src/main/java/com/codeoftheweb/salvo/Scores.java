package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private double score;

    private LocalDateTime finishDate;

    public Scores(){};

    public Scores(Game game){
        this.game = game;
    }

    public Scores(Player player, Game game, double score) {
        LocalDateTime dateTime = LocalDateTime.now(ZoneOffset.UTC);
        this.finishDate = dateTime;
        this.game = game;
        this.player = player;
        this.score = score;
    }

    public long getId(){
        return id;
    }

    public double getScore(){ return score; }

    public String getFinishDate() {
        return finishDate.toString().replace("T", " ");
    }

    @JsonIgnore
    public Game getGames(){return game;}

    @JsonIgnore
    public Player getPlayers(){return player;}

    public Map<String, Object> getDto(){
        Map<String, Object> scoresDto = new LinkedHashMap<>();
        scoresDto.put("id", this.getId());
        scoresDto.put("score", this.getScore());
        //scoresDto.put("finishDate", this.getFinishDate());
        return scoresDto;
    }
}
