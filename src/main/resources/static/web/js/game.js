var app = new Vue({
    el: "#salvoApp",
    data: {
        grid: "ABCDEFGHIJ",
        selectedPlayer:"",
        games:"",
        viewer:"",
        ships:"",
        shipsLocations:"",
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
                         this.app.loadPlayers();
                         return myJson;
                     });
             },
             loadPlayers: function(){
                let that = this;
                that.games.gamePlayers.forEach(function(player){
                    if(player.id == that.selectedPlayer){
                        that.viewer = player.player;
                        that.ships = player.ships;
                        that.ships.forEach(function(ship){
                            that.shipsLocations = [...that.shipsLocations, ...ship.locations];
                        });
                    }else{
                        that.enemy = player.player;
                    }
                });
             },
             select: function(id){
                if( this.shipsLocations.indexOf(id) != -1 ){
                    return "blue";
                }
                return "white";
             }
     }
});