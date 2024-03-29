var app = new Vue({
	el: "#salvoApp",
	data: {
		selectedPlayer:"",
		grid: "ABCDEFGHIJ",
		shipsType:{
			type:["AIRCRAFT CARRIER","BATTLESHIP","SUBMARINE","DESTROYER","PATROL BOAT"],
			size:[5,4,3,3,2],
			imageHeight:40,
		},
		gameData: "",
		player:"",
		enemy:"",
		btnText: "Vertical",
		gameState: "",
		myBoard:{
			ships:[],
			location:[],
			rotation:[],
			hits:[],
			water:[]
		},
		salvoes: [],
		newSalvo: [],
		hits: [],
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
		this.startInterval();
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
									 that.salvoes = player.salvo;
								 }else{
									 that.enemy = player.player;
									 that.hits = player.salvo;
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
							 if(myJson.gameState == "WAIT"){
							    this.app.gameState = "Waiting opponent shots...";
							 }else if(myJson.gameState == "FIRE"){
							    this.app.gameState = "Your turn to fire!";
							 }
							 this.app.gameData = myJson;
						 }
						 return myJson;
					 });
			 },
			 saveShips: function(){
			    let that = this;
			    $.ajax({
                  url:"/api/games/players/" + this.selectedPlayer + "/ships",
                  type:"POST",
                  data:JSON.stringify(this.unSaveShips),
                  contentType:"application/json",
                  dataType:"json",
                  success: function(response){
                        //location.reload();
                        that.loadGames();
                  }
                });
			 },
             fire: function(){
                let that = this;
                if(this.newSalvo.length == 5){
                    $.ajax({
                       url:"/api/games/players/" + this.selectedPlayer + "/salvoes",
                       type:"POST",
                       data:JSON.stringify(this.newSalvo),
                       contentType:"application/json",
                       dataType:"json",
                       success: function(response){
                             that.loadGames();
                             //location.reload();
                       }
                    });
                }else{
                    alert("shots incomplete!");
                }
             },
			 returnHome: function(){
			    window.location.href = '/web/games.html';
			 },
			 logout: function(){
				  $.post("/api/logout")
					  .then(function() {
					      //this.app.returnHome;
						  alert("Logged out");
					  });
			  },
			 drop: function(ev){
				let count = this.shipsType.size[this.shipsType.type.indexOf(this.dragElement.shipType)];
				let column = this.grid.indexOf(this.dragElement.position.substring(0, 1));
				let row = parseInt(this.dragElement.position.substring(1));
				illegalPos = false;

				let tempShip = {ships:"", location:[]};
				tempShip.ships = this.dragElement.shipType;

				for(index = 0; index < count; index++){
					if(this.dragElement.rotation > 0){
						if(this.innerGrid((row-1), index)){
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
				if(this.shipsLocated == 0){
				    //if(confirm("Do you want to save yours ships?")){
				        this.saveShips();
				    //}
				}
			 },
			 // Validacion barcos dentro de grilla
			 innerGrid: function(index, pos){
			    if(pos + index < 10){
			        return true;
			    }else{
			        return false;
			    }
			 },
			 // Validacion barcos superpuestos
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
                        response = true;
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
                //if(this.ships.ship && !this.ships.ship.includes(shipType)){ response = false;}
                //if(this.unSaveShips.shipType && !this.unSaveShips.shipType.includes(shipType)){ response = false;};
                this.ships.forEach(function(ship){
                    if(ship.ship == shipType){
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
             },
             setSalvo: function(id){
                let that = this;
                let block = false;
                that.salvoes.forEach(function(turn){
                    if(turn.locations.indexOf(id) != -1){
                        block = true;
                    }
                });
                if(!block){
                    if(that.newSalvo.indexOf(id) == -1 && that.newSalvo.length < 5){
                        that.newSalvo.push(id);
                    }else if(that.newSalvo.indexOf(id) != -1){
                        that.newSalvo.splice(that.newSalvo.indexOf(id), 1);
                    }
                }
             },
             cellContentSalvoes: function(id){
                let that = this;
                let response = "";
                if(this.newSalvo.includes(id)){response = "MISSILE";}
                this.gameData.gamePlayers.forEach(function(gp){
                    if(gp.id == that.player.id){
                        gp.salvo.forEach(function(sv){
                            if(sv.hits.includes(id)){
                                response = "EXPLOSION";
                            }else if(sv.locations.includes(id)){
                                response = "MISS-EXPLOSION";
                            }
                        });
                    }
                });
                return response;
             },
             hitNumber: function(turn, player){
                let that = this;
                let hitNumb = 0;
                if(player == "Me"){
                    that.salvoes.forEach(function(salvoTurn){
                        if(salvoTurn.turn == turn){
                            hitNumb = salvoTurn.hits.length;
                        }
                    });
                }else{
                    that.hits.forEach(function(salvoTurn){
                        if(salvoTurn.turn == turn){
                            hitNumb = salvoTurn.hits.length;
                        }
                    });
                }
                return hitNumb;
             },
             sunkNumber: function(turn, player){
                let that = this;
                 let sunkNumb = false;
                 if(player == "Me"){
                     that.salvoes.forEach(function(salvoTurn){
                         if(salvoTurn.turn == turn){
                             sunkNumb = true;
                         }
                     });
                 }else{
                     that.hits.forEach(function(salvoTurn){
                         if(salvoTurn.turn == turn){
                             sunkNumb = true;
                         }
                     });
                 }
                 return sunkNumb;
             },
             getSunked: function(turn, player){
                let that = this;
                let sunked = [];
                if(player == "Me"){
                     that.salvoes.forEach(function(salvoTurn){
                         if(salvoTurn.turn == turn){
                            for(i = 0; i < salvoTurn.Sunk.length; i++){
                                sunked.push(salvoTurn.Sunk[i].shipType);
                              }
                         }
                     });
                 }else{
                     that.hits.forEach(function(salvoTurn){
                          if(salvoTurn.turn == turn){
                              for(i = 0; i < salvoTurn.Sunk.length; i++){
                                sunked.push(salvoTurn.Sunk.shipType);
                              }
                          }
                      });
                 }
                 return sunked;
             },
             getShipClass: function(id){
                let response = "";
                let that = this;

                this.gameData.gamePlayers.forEach(function(gp){
                    if(gp.id != that.player.id){
                        gp.salvo.forEach(function(sv){
                            if(sv.hits.includes(id)){
                                response = "EXPLOSION";
                            }else if(sv.locations.includes(id)){
                                response = "MISS-EXPLOSION";
                            }
                        });
                    }
                });

                if(this.gameData.ships.length != 0 && response == ""){
                    this.gameData.ships.forEach(function(ship){
                        if(ship.locations.includes(id)){
                            let shipType = ship.shipType.replace(/\s/g, "-");
                            response = shipType + "-" + that.getRotation(ship.locations) + ship.locations.indexOf(id);
                        }
                    });
                } else {
                    this.unSaveShips.forEach(function(ship){
                        if(ship.locations.includes(id)){
                            let shipType = ship.shipType.replace(/\s/g, "-");
                            response = shipType + "-" + that.getRotation(ship.locations) + ship.locations.indexOf(id);
                        }
                    });
                }
                return response;
             },
             getRotation: function(locations){
                if(locations[0].substring(0, 1) == locations[1].substring(0, 1)){
                    return "V";
                }else{
                    return "H";
                }
             },
             startInterval: function () {
                let that = this;
                  setInterval(() => {
                    if(that.gameData.gameState == "WAIT_OPPONENT" || that.gameData.gameState == "WAIT_OPPONENT_SHIPS" || that.gameData.gameState == "WAIT"){
                        that.loadGames();
                    }
                  }, 5000);
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
	                count++;
	            }
	       });
	       return count;
	   },
	   shipAvailable: function(){
	       let that = this;
	       this.shipsType.type.forEach(function(ship){
	            if(that.loadShip(ship) != false){
	                return true;
	            }
	       });
	       return false;
	   },
	   enableFire: function(){
	       return false;
	   },
	   maxTurn: function(){
	        let maxTurni = 0;
	        this.salvoes.forEach(function(salvo){
	            if(salvo.turn > maxTurni){
	                maxTurni = salvo.turn;
	            }
	        });
	        this.hits.forEach(function(salvo){
	            if(salvo.turn > maxTurni){
	                maxTurni = salvo.turn;
	            }
	        });
	        return maxTurni;
	   }
	}
});