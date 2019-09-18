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
                 var url = new URL("http://localhost:8080/api/game_view/1");
                 fetch(url)
                     .then(function(response) {
                         return response.json();
                     })
                     .then(function(myJson) {
                         this.app.games = myJson;
                         this.app.ships = myJson.ships;
                         this.app.ships.forEach(function(ship){
                             this.app.shipsLocations = [...this.app.shipsLocations, ...ship.locations];
                         });
                         this.app.loadPlayers();
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
                let ret = "";
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
                if( ret == ""){
                    return "white";
                }else{
                    return ret;
                }
             },
             myShoots: function(id){
                 let ret = "";
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
});