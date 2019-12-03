package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;


import javax.persistence.*;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Entity
public class Salvo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private int turn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="fk_gamePlayer")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name="salvoLocations")
    private List<String> cell = new ArrayList<>();

    public Salvo() { }

    public Salvo(int turn, List<String> locations){
        this.turn = turn;
        this.cell = locations;
    }

    public Salvo(int turn, GamePlayer GamePlayer, List<String> locations){
        this.turn = turn;
        this.gamePlayer = GamePlayer;
        this.cell = locations;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public long getTurn(){
        return turn;
    }

    public long getId(){
        return this.id;
    }

    public List<String> getLocations(){
        return this.cell;
    }

    public void setGamePlayer(GamePlayer gamePlayer){
        this.gamePlayer = gamePlayer;
    }

    public List<String> getHits(List<String> salvoes, Set<Ship> ships){
        List<String> shipsLocations = new ArrayList<>();
        ships.forEach(ship -> shipsLocations.addAll(ship.getLocations()));

        return salvoes.stream().filter(shot -> shipsLocations.stream().anyMatch(location -> location.equals(shot))).collect(Collectors.toList());
    }

    public List<Ship> getSunks(Set<Salvo> shots, Set<Ship> ships){
        List<String> allShots = new ArrayList<>();
        shots.forEach(shot -> allShots.addAll(shot.getLocations()));
        return ships.stream().filter(ship -> allShots.containsAll(ship.getLocations())).collect(Collectors.toList());
    }

    public Map<String, Object> getDto(){
        Map<String, Object> salvoDto = new LinkedHashMap<>();
        salvoDto.put("id", this.getId());
        salvoDto.put("turn", this.getTurn());
        salvoDto.put("locations", this.getLocations());
        GamePlayer playerOpponent = this.getGamePlayer().getOpponent();

        if(playerOpponent != null){
            Set<Ship> opponentShips = playerOpponent.getShips();
            salvoDto.put("hits", this.getHits(this.getLocations(), opponentShips));
            Set<Salvo> shots = this.getGamePlayer().getSalvo().stream().filter(salvo -> salvo.getTurn() <= this.getTurn()).collect(Collectors.toSet());
            salvoDto.put("Sunk", this.getSunks(shots, opponentShips).stream().map(Ship::getDto));
        }
        return salvoDto;
    }
}
