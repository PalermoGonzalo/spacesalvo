var app = new Vue({
    el: "#salvoApp",
    data: {
        grid: "ABCDEFGHIJ",
        selectedPlayer:"",
        games:"",
        viewer:"",
        ships:"",
        shipsLocations:"",
        localSalvo:"",
        enemySalvo:"",
        enemy:""
    },
     created() {
        let uri = window.location.search.substring(1);
        let params = new URLSearchParams(uri);
        this.selectedPlayer = params.get("gp");
        this.loadGames();
     },
     methods: {
             loadGames: function() {
                 var url = new URL("http://localhost:8080/api/game_view/" + this.selectedPlayer);
                 fetch(url)
                     .then(function(response) {
                         return response.json();
                     })
                     .then(function(myJson) {
                        if(myJson.error){
                            alert(myJson.error);
                        }else{
                             this.app.games = myJson;
                             this.app.ships = myJson.ships;
                             this.app.ships.forEach(function(ship){
                                 this.app.shipsLocations = [...this.app.shipsLocations, ...ship.locations];
                             });
                             this.app.loadPlayers();
                         }
                         return myJson;
                     });
             },
             loadPlayers: function(){
                let that = this;
                that.games.gamePlayers.forEach(function(player){
                    if(player.id == that.selectedPlayer){
                        that.viewer = player;
                        that.viewer.salvo.forEach(function(localSalvo){
                            that.localSalvo = [...that.localSalvo, ...localSalvo.locations];
                         });
                    }else{
                        that.enemy = player;
                        that.enemy.salvo.forEach(function(localSalvo){
                           that.enemySalvo = [...that.enemySalvo, ...localSalvo.locations];
                        });
                    }
                });
             },
             select: function(id){
                let ret = "white";
                this.enemySalvo.forEach(function(enemySalvos){
                    if(enemySalvos.indexOf(id) != -1){
                        ret = "blue";
                    }
                });
                if( this.shipsLocations.indexOf(id) != -1){
                    if(ret == "blue"){
                        ret = "red";
                    }else{
                        ret = "black";
                    }
                }

                return ret;
             },
             myShoots: function(id){
                 let ret = "";
                 if(!this.localSalvo){
                     this.localSalvo.forEach(function(localSalvos){
                         if(localSalvos.indexOf(id) != -1){
                             ret = "blue";
                         }
                     });
                     /*
                     if( this.shipsLocations.indexOf(id) != -1){
                         if(ret == "blue"){
                             ret = "red";
                         }else{
                             ret = "black";
                         }
                     }*/
                     if( ret == ""){
                         return "white";
                     }else{
                         return ret;
                     }
                 }
              }
     }
});