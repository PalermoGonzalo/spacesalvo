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
		                    ships:"AIRCRAFT CARRIER",
                            location:[]
                        },{
                            ships:"BATTLESHIP",
                            location:[]
                        },{
                            ships:"SUBMARINE",
                            location:[]
                        },{
                            ships:"DESTROYER",
                            location:[]
                        },{
                            ships:"PATROL BOAT",
                            location:[]
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
				            if(ship.ships == tempShip.ships){
				                ship.location = [...ship.location, ...tempShip.location];
				            }
				        });
                    }
				}else{
				    console.log("Illegal position!");
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
			            if(unSaved.location.indexOf(position) != -1){
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
			    return response;
			 },
			 clone: function(obj){
                 return JSON.parse(JSON.stringify(obj));
             }
	 },
	computed:{
	   currentHeight: function(){
		   return window.innerHeight;
	   }
	}
});