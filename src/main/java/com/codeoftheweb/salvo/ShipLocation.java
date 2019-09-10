package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.util.HashSet;
import java.util.Set;



@Entity
public class ShipLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String location;

    public ShipLocation(){}

    public ShipLocation(String location){
        this.location = location;
    }

    public String getLocation(){
        return this.location;
    }
}
