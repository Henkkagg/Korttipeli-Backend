# Korttipeli Backend

Tämä on Ktor-backend fullstack projektiini. Frontendin esittely täällä: https://github.com/Henkkagg/Korttipeli-Android

Korttipeli on verkon kautta toimiva moninpeli. Peliin rekisteröidyttyään pelaaja voi luoda omia kortteja, sekä lisätä muita käyttäjiä kavereikseen. Sekä omista, että kavereiden korteista pystyy kokoamaan pakan, jolla kaverit sitten voivat pelata keskenään.

## Keskeisimmät ominaisuudet

* Käyttäjien rekisteröinti ja sisäänkirjautuminen
  * Salasanojen hashaus argon2id - algoritmillä
  * Sessiotietojen varmennus Json web tokenilla
* Datan (ml. kuvat) varastointi MongoDB:ssä
* Websocket-yhteydellä toimiva lyhyen viiveen moninpeli
    * Kaikki pelaajat saavat ilmoituksen, jos joku pelaajista käyttää pelin kulkuun vaikuttavan erikoiskortin
    * Vuorossa oleva pelaajan nostaessa uuden kortin, se näytetään samanaikaisesti myös muille
* Kaverijärjestelmä
  * Käyttäjän luoma sisältö (kortit ja pakat) jaetaan vain käyttäjän kavereille
  * Toisensa kavereiksi lisänneet käyttäjät voivat hyödyntää toistensa korttejaan luodessaan pakkoja
* Tietoliikenteen optimointi
    * Käyttäjien pyytäessä ajantasaisia korttitietoja, ne toimitetaan vain muuttuneilta osin. Esim. jos käyttäjällä on cachattuna kortti, josta on vain teksti vaihtunut, niin ei lähetetä myös kortin kuvaa uudelleen
    * Kortin kuvan vastaanotto "taustalla" kesken kortin luomisen, jolloin lopullinen tallennus-interaktio nopeutuu käyttäjän näkökulmasta
