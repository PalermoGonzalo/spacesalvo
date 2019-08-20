package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="id")
    private long gameId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="id")
    private long playerId;
    private LocalDateTime joinDate;

    public GamePlayer() {
        LocalDateTime dateTime = LocalDateTime.now(ZoneOffset.UTC);
        this.joinDate = dateTime;
    }

    public Long getId() { return id; }

    public String getJoinDate() {
        return joinDate;
    }

    public Long getPlayerId() { return playerId; }

    public Long getGameId() { return gameId; }


}
