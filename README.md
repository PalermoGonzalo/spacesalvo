BATALLA NAVAL (SALVO)
=======

## Tecnologias

Programa realizado en Java 8, utilizando el framework Spring Boot 2.2.1 en back-end. 

Para el Front-end se utilizo Bootstrap 4.0.0 y el framework Vue.js.

Como base de datos se utilizo H2 de forma no persistente (tipo SQL).

## Descripción del Juego

El juego cuenta con una landing page que es un tablero de posiciones donde se muestran los puntajes de los jugadores. (1 punto por juego ganado, 0.5 por empatado y 0 por pedridos)

Solo se permite jugar estando registrado y logueado. Para ello cuenta con un registro simple donde se solicita un email y contraseña.

Una vez logueado, se muestran ademas del tablero de posiciones los juegos activos y la posibilidad de crear un juego nuevo. El juego no es en tiempo real, sino por turnos, por lo tanto se permite jugar varios juegos al mismo tiempo.

Cuando se empieza el juego se solicita colocar los barcos propios en posicion, mediante un sistema de drag & drop. Una vez realizado esto, automaticamente se guardan las posiciones y se espera a que el contricante este listo.

Inicialmente se permiten 5 disparos por turno, que iran disminuyendo de a 1 a medida que se vayan hundiendo los barcos.