package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="fk_game")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="fk_player")
    private Player player;

    private LocalDateTime joinDate;

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    private Set<Ship> Ships = new HashSet<>();

    //private Date joinDate;
    public GamePlayer(){}

    public GamePlayer(Player player, Game game) {
        LocalDateTime dateTime = LocalDateTime.now(ZoneOffset.UTC);
        this.joinDate = dateTime;
        this.game = game;
        this.player = player;
    }

    public long getId(){
        return id;
    }

    public Player getPlayer(){
        return player;
    }

    public Game getGame(){
        return game;
    }

    public String getJoinDate(){
        return joinDate.toString();
    }

    //public Date getJoinDate(){return joinDate;}
    public Map<String, Object> getDto(){
        Map<String, Object> gamePlayerDto = new LinkedHashMap<>();
        gamePlayerDto.put("id", this.getId());
        gamePlayerDto.put("player", player.getDto());
        return gamePlayerDto;
    }
}
