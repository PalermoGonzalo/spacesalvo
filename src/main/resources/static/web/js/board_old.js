var app = new Vue({
    el: "#salvoApp",
    data: {
        grid: "ABCDEFGHIJ",
        selectedPlayer:"",
        games:"",
        viewer:"",
        shipsType:{
            type:["AIRCRAFT CARRIER","BATTLESHIP","SUBMARINE","DESTROYER","PATROL BOAT"],
            size:[5,4,3,3,2],
            imageHeight:50,
        },
        myBoard:{
            ships:[],
            location:[],
            rotation:[],
            hits:[],
            water:[]
        },
        ships:[],
        //ships:"",
        shipsLocations:"",
        localSalvo:"",
        enemySalvo:"",
        enemy:"",
        dragPosition:"",
        dragElement:"",
        dragElementRotation: 0,
        horizontal: true,
        vertical: false
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
                             myJson.ships.forEach(function(ship){
                                let objShip = {
                                    ship: ship.shipType,
                                    position : [...ship.locations],
                                    rotation : 0 };
                                this.app.ships.push(objShip);
                                this.app.shipsLocations = [...this.app.shipsLocations, ...ship.locations];
                             });
                             this.app.loadPlayers();
                         }
                         return myJson;
                     });
             },
             returnHome: function(){
             window.location.href = '/web/games.html';
             },
             logout: function(){
                  $.post("/api/logout")
                      .then(function() {
                          alert("Logged out");
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
                let ret = "transparent";
                if(this.enemySalvo){
                    this.enemySalvo.forEach(function(enemySalvos){
                        if(enemySalvos.indexOf(id) != -1){
                            ret = "blue";
                        }
                    });
                }
                if(this.shipsLocations){
                    if( this.shipsLocations.indexOf(id) != -1){
                        if(ret == "blue"){
                            ret = "red";
                        }else{
                            ret = "black";
                        }
                    }
                }
                return ret;
             },
             myShoots: function(id){
                 let ret = "transparent";
                 if(this.localSalvo){
                     this.localSalvo.forEach(function(localSalvos){
                         if(localSalvos.indexOf(id) != -1){
                             ret = "blue";
                         }
                     });
                 }
                 return ret;
             },
             drop: function(ev){
                let count = this.shipsType.size[this.shipsType.type.indexOf(this.dragElement)];
                let column = this.grid.indexOf(this.dragPosition.substring(0, 1));
                let row = parseInt(this.dragPosition.substring(1, 2));
                let tempShip = {ships:[], location:[]};
                let validation = 0;
                for(index = 0; index < count; index++){
                    tempShip.ships.push(this.dragElement);
                    if(this.dragElementRotation > 0){
                        if(row + index >= 10){validation = 1;}
                        tempShip.location.push(this.grid.charAt(column) + (row + index));
                    }else{
                        if(column + index >= 10){validation = 1;}
                        tempShip.location.push(this.grid.charAt(column + index)+row);
                    }
                }
                if(validation == 0){
                    this.myBoard.ships = [...this.myBoard.ships, ...tempShip.ships];
                    this.myBoard.location = [...this.myBoard.location, ...tempShip.location];
                }
                console.log(tempShip);
             },
             dragover: function(ev){
                 this.dragPosition = ev.target.id;
             },
             setDragElement: function(shipType){
                this.dragElement = shipType;
             },
             checkPosition: function(id){
                if(this.myBoard.location.indexOf(id) != -1){
                    return true;
                }
                return false;
             },
             cellImage: function(id){
                let response = "";
                if(this.myBoard.location.indexOf(id) != -1){
                    response = this.myBoard.ships[this.myBoard.location.indexOf(id)];
                }
                return response;
             },
             setVertical: function(){
                this.horizontal = !this.horizontal;
                this.vertical = !this.vertical;
                if(this.dragElementRotation == 0){
                    this.dragElementRotation= 90;
                }else{
                    this.dragElementRotation = 0;
                }
                console.log("Horizontal: " + this.horizontal);
                console.log("Vertical: " + this.vertical);
             }
      }
});