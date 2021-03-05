# Description

A work in progress java implementaion of the board game ["RoboRally"](https://en.wikipedia.org/wiki/RoboRally) using the [libgdx library](https://libgdx.com/).


# How to run
0. Install Java 13 or later.
1. Make sure to have installed Maven
2. Clone project using Git.
3. Run Main.java using an IDE such as IntelliJ (recommended) or Eclipse. Alternativly compile and run using your preferred method.

### 1. How to play

- Single player 
  - Select "Host" on the role menu
  - Select "Ok" to play when prompted about all players connected

- Multiplayer
  - Host - select "Host" on the role menu and wait for other players to connect. Press ok on the new prompt only when all players are connected. You can see this by waiting until you have a number of "recieved cards" in the consol log equal to the number of clients between each round.
  - Client - select "Client" on the role menu and connect to the host's IP adress
  - The UiB-owl with sparks on it is your character. 

### 2. Controls

Use keys 1-9 to select the corresponding card in your deck menu to choose your build. Wait for all players to complete their turn before you start the next turn. First player to reach all the flags wins.

# Manual tests

GUI test:
1. Start the game in single player mode. 
2. If graphics winodw appears the test is succsesful.

Network test:
1. Launch a host
2. Connect to a host IP to test the networking


Card dealt test:
1. Start the game in either single or mutiplayer mode (if using muliplayer make sure there is a host!)
2. If cards appear on screen (as text) the test has passed.


Card sequence test:
1. Start the game in either single or mutiplayer mode (if using muliplayer make sure there is a host!)
2. Select a sequence of cards and keep a note of which cards you've selected as well as the order.
3. If the player ends up the same place position in the GUI as predicted by going over your notes, the test has passed.


Player is oriented in correct direction test:
1. Start the game in either single or mutiplayer mode (if using muliplayer make sure there is a host!)
2. Select cards that change direction of player if available (you have to select a total of five cards, all cards do not have to be rotaional cards, but at least one has to), if no rotainal cards exist; exit the game and start again from step 1. While selecting cards, make a note of what direction the player is supposed to be oriented.
3 After all cards have been selected, check if the Graphical representaion of your player corresponds with your notes, if it does then the test has passed. 

Winning test:
1. Start the game in either single or mutiplayer mode (if using muliplayer make sure there is a host!)
2. Select a sequence of cards so that you progress towards a flag
3. Repeat step 2 until you land on a flag
4. Repeat step 2, but this time towards the flag opposite flag than last time
5. When you visit the second flag, the game should close and the consol should show the ID of the player along with a congratulation message. In the host's case, it is -230230


#  User stories
 
 User stories, acceptance criteria, and tasks can be found [here](https://docs.google.com/spreadsheets/d/1A_78OKM1BRXeeG4MR3e6AafYpPxnElqm3xPFjLozlGY/edit?usp=sharing)

# Known bugs

The host has to be the last actor to choose their 5 cards. Otherwise the program crashes.

All other players than you are seemingly turned the wrong direction. This doesn't affect gameplay that much as your position should always be correct.

# Known issues

If a player disconnects during a session, the whole game has to be restarted.

The players are by default named after the order in which they connect to the host ("1", "2", "3",..) and the host is named "-032032"
