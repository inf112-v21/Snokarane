# Description

A work in progress java implementaion of the board game ["RoboRally"](https://en.wikipedia.org/wiki/RoboRally) using the [libgdx library](https://libgdx.com/).


# How to run
0. Install Java 13 or later.
1. Clone project using Git.
2. Run Main.java using an IDE such as IntelliJ (recommended) or Eclipse. Alternativly compile and run using your preferred method.
3. Choose host or client.
* 3a. Host: wait for connected players.
* 3b. Client: connect to ip of host.



#  User stories
 
 User stories, acceptance criteria, and tasks can be found [here](https://docs.google.com/spreadsheets/d/1A_78OKM1BRXeeG4MR3e6AafYpPxnElqm3xPFjLozlGY/edit?usp=sharing)

# Known bugs

The host has to be the last actor to choose their 5 cards. Otherwise the program crashes.

All other players than you are seemingly turned the wrong direction. This doesn't affect gameplay that much as your position should always be correct.

If a player disconnects during a session, the whole game has to be restarted.
