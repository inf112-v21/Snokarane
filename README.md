# Description

A work in progress java implementaion of the board game ["RoboRally"](https://en.wikipedia.org/wiki/RoboRally) using the [libgdx library](https://libgdx.com/).


# How to run
0. Install Java 13 or later.
1. Clone project using Git.
2. Run Main.java using an IDE such as IntelliJ (recommended) or Eclipse. Alternativly compile and run using your preferred method.
3. Choose host or client
3a. Host: wait for connected players
3b. Client: connect to ip of host



# Brukarhistorier
 
### Historie #1: 
Som speler, ynskjer eg ei grafisk framstilling av brettet,  slik at eg kan sjå området eg speler på 
 
Akseptansekrav: 
Når ein køyrer Java-programmet, skal det vise seg eit nytt vindauge med eit visuelt rutenett fylt med grafiske element, basert på kartets tmx-fil.
 
 
### Historie #2: 
Som speler, ynskjer eg å kunne sjå brikkene på brettet, slik at eg veit kor eg og dei andre spelarane er.
 
Akseptansekrav: 
Brikkene som representerer spelarane er synlege på rutenettet i det grafiske brukargrensesnittet.
 
### Historie #3: 
Som spelar, ynskjer eg å kunne flytte brikkene, slik at eg kan ha framgang i spillet.
 
Akseptansekrav:  
Når man trykker på en piltast, så skal spiller-koordinatene flytte seg i tilsvarande retning som piltasten, og denne endringa skal også visast det grafiske brukergrensesnittet.
 
### Historie #4: 
Som spelar, ynskjer eg at det er flagg på brettet, slik at eg kan vitje flagga ved å flytte brikka til dei. 
 
Akseptansekrav: 
Rutenettet i det grafiske brukargrensesnittet inneheld fleire ruter med flagg på ulike stader. Det skal vere ei flagg-klasse for å skilje mellom forskjellege flagg. 
 
### Historie #5: 
Som spelar, ynskjer eg at flaggvitjing blir registrert, slik at eg kan oppnå vinn-tilstand. 
 
Akseptansekrav: 
Når ein spelar beveger seg på et flagg, blir dette registrert i ein spelar-klasse. Når ein speler har vitja alle flagga, vinn denne spelaren.



