var app = new Vue({
    el: "#mainApp",
    data: {
        games:""
    },
     created() {
         this.schedule();
         this.handleResize();
         window.addEventListener('resize', this.handleResize);
     },
     methods: {
             schedule: function() {
                 //let that = this;
                 fetch('/api/games')
                     .then(function(response) {
                         return response.json();
                     })
                     .then(function(myJson) {
                         this.app.games = myJson.Schedule;
                         return myJson;
                     });
             }
     }
});
/*
$('document').ready(function(){
    $.get('/api/games')
    .done((games) => {createdGameList(games);})
    .fail((jqXHR, status) => {console.error(status);});
});

function createdGameList(games){
    let gameList = $('#game-list');

    games.forEach(game => {
        gameList.append(
            `<li>
                  $(game.created);
                  $(game.gamePlayers[0].player.email);
                  $(game.gamePlayers[1].player.email);
             </li>`
        );
    });
}
*/