var app = new Vue({
    el: "#salvoApp",
    data: {
        scores:"",
        showLogin:1,
        register:0,
        form:{email:"", password:""},
        email:"",
        password:"",
        showModal: false,
        games: [],
        players: []
    },
     created() {
         this.loadScores();
     },
     methods: {
             loadScores: function() {
                 //let that = this;
                 fetch('/api/scores')
                     .then(function(response) {
                         return response.json();
                     })
                     .then(function(myJson) {
                         this.app.scores = myJson;
                         console.log(this.app.scores);
                         myJson.forEach(function(score){
                            let newPlayer = 1;
                            this.app.players.forEach(function(player){
                                if(player.player == score.player){
                                    console.log("old Player");
                                    if(score.score == 1){
                                        player['win'] += 1;
                                        player['totalScore'] += 1;
                                    }else if(score.score != 0){
                                        player['ties'] += 1;
                                        player['totalScore'] += 0.5;
                                    }else{
                                        player['lost'] += 1;
                                    }
                                    newPlayer = 0;
                                }
                            });
                            if(newPlayer == 1){
                                console.log("new player");
                                let playerStats = [];
                                playerStats['player'] = score.player;
                                playerStats['win'] = 0;
                                playerStats['ties'] = 0;
                                playerStats['lost'] = 0;
                                playerStats['totalScore'] = 0;
                                if(score.score == 1){
                                    playerStats['win'] = 1;
                                    playerStats['totalScore'] = 1;
                                }else if(score.score != 0){
                                    playerStats['ties'] = 1;
                                    playerStats['totalScore'] = 0.5;
                                }else{
                                    playerStats['lost'] = 1;
                                }
                                this.app.players.push(playerStats);
                            }
                         });
                     return myJson;
                     });
                 },
             login: function(){
                let that = this;
                if(this.form.email == "" || this.form.password == ""){
                    alert("Username or password missed!");
                    return;
                }
                $.post("/api/login", { username: this.form.email, password: this.form.password })
                    .done(function() {
                        //console.log("logged in!");
                        that.loadGames();
                        that.showLogin = 0;
                    })
                    .fail(function(){
                        alert("Username or password error!");
                    });
             },
             loadGames: function(){
                fetch('/api/games')
                  .then(function(response) {
                      return response.json();
                  })
                  .then(function(myJson) {
                      console.log(myJson);
                      this.app.games = myJson;
                      return myJson;
                  });
             },
             registration: function(){
                if(this.form.email == "" || this.form.password == ""){
                    alert("Username or password missed!");
                    return;
                }
                 $.post("/api/players", { username: this.form.email, password: this.form.password })
                     .done(function() {
                         alert("you have register succesfully!")
                         app.form.email = "";
                         app.form.password = "";
                         app.register = 0;
                     })
                     .fail(function(){
                        alert("User already exists!");
                     });
             },
             logout: function(){
                 $.post("/api/logout")
                     .then(function() {
                         app.showLogin = 1;
                         alert("Logged out");
                     });
             },
             checkExistUser: function(event){
                this.email = event.target.value;
             },
             checkExistPass: function(event){
                this.password = event.target.value;
             },
             setRegistration: function(){
                this.register = 1;
             }
     },
     computed:{
        currentHeight: function(){
            return window.innerHeight;
        }
     }
});