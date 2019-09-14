var app = new Vue({
    el: "#salvoApp",
    data: {
        route:"",
        games:""
    },
     created() {
        let uri = window.location.search.substring(1);
        let params = new URLSearchParams(uri);
        route=params.get("gp");
        this.schedule();
     },
     methods: {
             schedule: function() {
                 //var url = new URL("localhost:8080/api/game_view/" + route);
                  //   params = {"1"}
                 //Object.keys(params).forEach(key => url.searchParams.append(key, params[key]))
                 //console.log(url);
                 fetch("localhost:8080/api/game_view/1")
                     .then(function(response) {
                         return response.json();
                     })
                     .then(function(myJson) {
                         this.app.games = myJson;
                         //console.log(this.app.games);
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