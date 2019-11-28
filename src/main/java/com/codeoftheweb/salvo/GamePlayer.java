package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

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
    private Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Salvo> salvo = new HashSet<>();

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

    public Set<Ship> getShips(){ return ships;}

    public void setShip(Ship ship){
        this.ships.add(ship);
        ship.setGamePlayer(this);
    }

    public Set<Salvo> getSalvo(){ return salvo;}

    public void setSalvo(Salvo salvo){
        this.salvo.add(salvo);
        salvo.setGamePlayer(this);
    }

    public Map<String, Object> getDto(){
        Map<String, Object> gamePlayerDto = new LinkedHashMap<>();
        gamePlayerDto.put("id", this.getId());
        gamePlayerDto.put("player", player.getDto());
        List<Map<String, Object>> salvoDto =
                this.getSalvo()
                        .stream()
                        .map(salvo -> salvo.getDto())
                        .collect(Collectors.toList());
        gamePlayerDto.put("salvo", salvoDto);
        //Scores scores = new Scores(this.getGame());
        //List<Map<String, Object>> scoreDto =
        //        Collections.singletonList(scores.getDto());
        //gamePlayerDto.put("score", scores.getDto());
        return gamePlayerDto;
    }

    public Map<String, Object> getSimpleDto(){
        Map<String, Object> gamePlayerDto = new LinkedHashMap<>();
        gamePlayerDto.put("gpid", this.getId());
        gamePlayerDto.put("id", player.getId());
        gamePlayerDto.put("user", player.getEmail());
        return gamePlayerDto;
    }
}
