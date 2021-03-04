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

- start the game to test if the grapchics appear
- connect to a host IP to test the networking
- start the game to test if you are dealt cards
- select a sequence of cards to test if the player ends up on the correct tile
- select a sequence of cards to test if the player ends up in the correct direction



#  User stories
 
 User stories, acceptance criteria, and tasks can be found [here](https://docs.google.com/spreadsheets/d/1A_78OKM1BRXeeG4MR3e6AafYpPxnElqm3xPFjLozlGY/edit?usp=sharing)

# Known bugs

The host has to be the last actor to choose their 5 cards. Otherwise the program crashes.

All other players than you are seemingly turned the wrong direction. This doesn't affect gameplay that much as your position should always be correct.

# Known issues

If a player disconnects during a session, the whole game has to be restarted.

The players are by default named after the order in which they connect to the host ("1", "2", "3",..) and the host is named "-032032"
