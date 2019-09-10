package com.codeoftheweb.salvo;

//import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
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

    private long GamePlayer;

    @ElementCollection
    @Column(name="location")
    private List<String> location = new ArrayList<>();

    public Ship() { }

    public Ship(String shipType, long GamePlayer){
        this.shipType = shipType;
        this.GamePlayer = GamePlayer;
    }

    public long getGamePlayer() {
        return GamePlayer;
    }

    public String getShipType(){
        return shipType;
    }

}
