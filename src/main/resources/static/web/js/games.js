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
                            this.app.players.push(score.player);
                         });
                         return myJson;
                     });
             }
     },
     computed:{
     }
});