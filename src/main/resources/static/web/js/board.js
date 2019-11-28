var app = new Vue({
	el: "#salvoApp",
	data: {
		selectedPlayer:"",
		grid: "ABCDEFGHIJ",
		shipsType:{
			type:["AIRCRAFT CARRIER","BATTLESHIP","SUBMARINE","DESTROYER","PATROL BOAT"],
			size:[5,4,3,3,2],
			imageHeight:50,
		},
		player:"",
		enemy:"",
		btnText: "Vertical",
		myBoard:{
			ships:[],
			location:[],
			rotation:[],
			hits:[],
			water:[]
		},
		ships:[],
		shipsLocations:"",
		dragElement:{
		    position: "",
		    shipType: "",
		    rotation: ""
		},
		unSaveShips: [
		                {
		                    shipType:"AIRCRAFT CARRIER",
                            locations:[]
                        },{
                            shipType:"BATTLESHIP",
                            locations:[]
                        },{
                            shipType:"SUBMARINE",
                            locations:[]
                        },{
                            shipType:"DESTROYER",
                            locations:[]
                        },{
                            shipType:"PATROL BOAT",
                            locations:[]
                        }
                     ]
	},
	 created() {
		let uri = window.location.search.substring(1);
		let params = new URLSearchParams(uri);
		this.selectedPlayer = params.get("gp");
		this.loadGames();
	 },
	 methods: {
			 loadGames: function() {
				 let that = this;
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
							 // Cargamos los jugadores
							 myJson.gamePlayers.forEach(function(player){
								 if(player.id == that.selectedPlayer){
									 that.player = player.player;
								 }else{
									 that.enemy = player.player;
								 }
							 });
							 // Cargamos los barcos si tuviera
							 myJson.ships.forEach(function(ship){
								 let objShip = {
									 ship: ship.shipType,
									 position : [...ship.locations],
									 rotation : 0
									 };
								 this.app.ships.push(objShip);
								 this.app.shipsLocations = [...this.app.shipsLocations, ...ship.locations];
							 });
						 }
						 return myJson;
					 });
			 },
			 saveShips: function(){
			    console.log("Saving ships...");
			    $.ajax({
                  url:"/api/games/players/" + this.selectedPlayer + "/ships",
                  type:"POST",
                  data:JSON.stringify(this.unSaveShips),
                  contentType:"application/json",
                  dataType:"json",
                  success: function(response){
                        response.forEach(function (data){
                         if(data == "status"){
                             console.log("OK");
                         }else{
                            console.log("error");
                            console.log(response);
                         }
                     });
                  }
                });
                /*
                 $.post("/api/games/players/" + this.selectedPlayer + "/ships", JSON.stringify(this.unSaveShips) )
                     .done(function(response) {

                     })
                     .fail(function(){
                         alert("Username or password error!");
                     });
                     */
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
			 drop: function(ev){
				let count = this.shipsType.size[this.shipsType.type.indexOf(this.dragElement.shipType)];
				let column = this.grid.indexOf(this.dragElement.position.substring(0, 1));
				let row = parseInt(this.dragElement.position.substring(1, 2));
				illegalPos = false;

				let tempShip = {ships:"", location:[]};
				tempShip.ships = this.dragElement.shipType;

				for(index = 0; index < count; index++){
					if(this.dragElement.rotation > 0){
						if(this.innerGrid(row, index)){
						    tempShip.location.push(this.grid.charAt(column) + (row + index));
						}else{
						    illegalPos = true;
						}
					}else{
						if(this.innerGrid(column, index)){
						    tempShip.location.push(this.grid.charAt(column + index)+row);
						}else{
						    illegalPos = true;
						}
					}
				}
				if(!illegalPos){
				    if(this.validation(tempShip) == 0){
				        this.unSaveShips.forEach(function(ship){
				            if(ship.shipType == tempShip.ships){
				                ship.locations = [...ship.locations, ...tempShip.location];
				            }
				        });
                    }
				}else{
				    console.log("Illegal position!");
				}
                console.log(this.shipsLocated);
				if(this.shipsLocated == 0){
				    this.saveShips();
				}
			 },
			 innerGrid: function(index, pos){
			    if(pos + index < 10){
			        return true;
			    }else{
			        return false;
			    }
			 },
			 validation: function(ship){
			    let response = 0;
			    this.unSaveShips.forEach(function(unSaved){
			        // Valido que no se superpongan los barcos
			        ship.location.forEach(function(position){
			            if(unSaved.locations.indexOf(position) != -1){
			                response = 1;
			                console.log("The ship overlaps with others!");
			            }
			        });
			    });
			    return response;
			 },
			 dragover: function(ev){
				 this.dragElement.position = ev.target.id;
			 },
			 setDragElement: function(shipType){
				this.dragElement.shipType = shipType;
			 },
			 checkPosition: function(id){
			    let response = false;
			    this.ships.forEach(function(ship){
                    if(ship.position.indexOf(id) != -1){
                        //response = true;
                    }
                });
                return response;
			 },
			 cellImage: function(id){
				let response = "";
				if(this.myBoard.location.indexOf(id) != -1){
					response = this.myBoard.ships[this.myBoard.location.indexOf(id)];
				}
				return response;
			 },
			 rotate: function(){
			    console.log(this.dragElement.rotation);
				if(this.dragElement.rotation == 0){
				    this.dragElement.rotation = 90;
				    this.btnText = "Horizontal";
				}else{
				    this.dragElement.rotation = 0;
                    this.btnText = "Vertical";
				}
			 },
			 cellContent: function(id){
			    let response = false;
			    this.ships.forEach(function(ship){
			        if(ship.position.indexOf(id) != -1){
			            response = ship.ship;
			        }
			    });
			    this.unSaveShips.forEach(function(ship){
			        if(ship.locations.indexOf(id) != -1){
                        response = ship.shipType;
                    }
			    });
			    return response;
			 },
			 clone: function(obj){
                 return JSON.parse(JSON.stringify(obj));
             },
             loadShip: function(shipType){
                let response = true;
                this.ships.forEach(function(ship){
                    if(ship.ships == shipType){
                        if(ship.position.length != 0){
                            response = false;
                        }
                    }
                });
                this.unSaveShips.forEach(function(ship){
                    if(ship.shipType == shipType){
                        if(ship.locations.length != 0){
                             response = false;
                         }
                    }
                });
                return response;
             }
	 },
	computed:{
	   currentHeight: function(){
		   return window.innerHeight;
	   },
	   shipsLocated: function(){
	       let count = 0;
	       this.unSaveShips.forEach(function(ship){
	            if(ship.locations.length == 0){
	                console.log(count);
	                count++;
	            }
	       });
	       return count;
	   }
	}
});