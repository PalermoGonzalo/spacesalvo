package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @OneToMany(mappedBy="game", fetch = FetchType.EAGER)
    // En el mappedBy hay que utilizar el nombre de la referencia utilizada a Game en la clase GamePlayers
    private Set<GamePlayer> gamePlayers;

    private LocalDateTime creationDate;

    public Game() {
        LocalDateTime dateTime = LocalDateTime.now(ZoneOffset.UTC);
        this.creationDate = dateTime;
        this.gamePlayers = new HashSet<>();
    }

    public long getId(){
        return id;
    }

    public String getCreationDate() {
        return creationDate.toString();
    }

    @JsonIgnore
    public Set<GamePlayer> getGamePlayer(){
        return gamePlayers;
    }


}
