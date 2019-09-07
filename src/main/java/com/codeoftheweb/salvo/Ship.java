package com.codeoftheweb.salvo;

//import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

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
    private long GamePlayer;

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
