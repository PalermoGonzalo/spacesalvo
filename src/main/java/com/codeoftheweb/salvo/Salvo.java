package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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


    public Map<String, Object> getDto(){
        Map<String, Object> salvoDto = new LinkedHashMap<>();
        salvoDto.put("id", this.getId());
        salvoDto.put("turn", this.getTurn());
        salvoDto.put("locations", this.getLocations());
        return salvoDto;
    }
}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/**").hasAuthority("ADMIN")
                .antMatchers("/**").hasAuthority("USER")
                .and()
                .formLogin();

        http.formLogin()
                .usernameParameter("name")
                .passwordParameter("pwd")
                .loginPage("/login");

        http.logout().logoutUrl("/logout");
    }
}