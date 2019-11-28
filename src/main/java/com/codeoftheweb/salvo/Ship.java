package com.codeoftheweb.salvo;

//import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import java.util.*;
import javax.persistence.*;

//import java.util.HashSet;
//import java.util.Set;

@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String shipType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="fk_gamePlayer")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name="locations")
    private List<String> locations = new ArrayList<String>();

    public Ship() { }

    public Ship(String shipType,  List<String> locations){
        this.shipType = shipType;
        this.locations = locations;
    }

    public Ship(String shipType, GamePlayer GamePlayer, List<String> locations){
        this.shipType = shipType;
        this.gamePlayer = GamePlayer;
        this.locations = locations;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer){
        this.gamePlayer = gamePlayer;
    }

    public String getShipType(){
        return shipType;
    }

    public long getId(){
        return this.id;
    }

    public List<String> getLocations(){
        return this.locations;
    }

    public Map<String, Object> getDto(){
        Map<String, Object> shipDto = new LinkedHashMap<>();
        shipDto.put("id", this.getId());
        shipDto.put("shipType", this.getShipType());
        shipDto.put("locations", this.getLocations());
        return shipDto;
    }

}
