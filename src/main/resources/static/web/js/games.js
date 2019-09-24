var app = new Vue({
    el: "#salvoApp",
    data: {
        scores:"",
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
                 }
     },
     computed:{
     }
});