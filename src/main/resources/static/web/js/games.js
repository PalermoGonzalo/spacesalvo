var app = new Vue({
    el: "#salvoApp",
    data: {
        scores:"",
        showLogin:1,
        showLogout:0,
        form:{email:"", password:""},
        email:"",
        password:"",
        players: []
    },
     created() {
         this.schedule();
     },
     methods: {
             schedule: function() {
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
                console.log(this.form.email);
                console.log(this.form.password);
                $.post("/api/login", { username: this.form.email, password: this.form.password })
                    .done(function() {
                        console.log("logged in!");
                        app.showLogin = 0;
                        app.showLogout = 1;
                    });
             },
             logout: function(){
                 $.post("/api/logout")
                     .then(function() {
                         app.showLogin = 1;
                         app.showLogout = 0;
                         alert("Logged out");
                     });
             },
             checkExistUser: function(event){
                this.email = event.target.value;
             },
             checkExistPass: function(event){
                this.password = event.target.value;
             },
     },
     computed:{
     }
});