package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private LocalDateTime creationDate;

    @OneToMany(mappedBy="game", fetch = FetchType.EAGER)
    Set<Game> game;

    public Game() {
        LocalDateTime dateTime = LocalDateTime.now(ZoneOffset.UTC);
        this.creationDate = dateTime;
    }

    public String getCreationDate() {
        return creationDate.toString();
    }

    public String toString() {
        return creationDate.toString().replace('T', ' ');
    }
}
