

<p align="center">
  <img width="500" height="500" src="https://user-images.githubusercontent.com/21172653/110108681-c5b8c680-7dac-11eb-8087-183c55f8b90b.png">
</p>

# Description

A work in progress java implementaion of the board game ["RoboRally"](https://en.wikipedia.org/wiki/RoboRally) using the [libgdx library](https://libgdx.com/).


# How to run
1. Install Java 13 or later. Java can be found at https://www.oracle.com/java/technologies/javase-downloads.html
2. Either download an IDE that supports java or use the command line to compile java files
3. Make sure to have installed Maven in your IDE or command line
4. Clone project using Git: This can be done using either HTTPS, SSH or GitHub CLI
	
	Using HTTPS, write in git bash (or your CLI of choice) command line 'git clone https://github.com/inf112-v21/Snokarane.git'
	
	Using SSH, write in git bash (or your CLI of choice) command line 'git clone git@github.com:inf112-v21/Snokarane.git'
	
	Using GitHub CLI, git bash (or your CLI of choice) write in command line 'gh repo clone inf112-v21/Snokarane'
	
	Alternatively, you can use your IDE's integrated VCS to log into your GitHub and clone from there.
	
	You can also download the source files by navigating to the lastest release tag.
5. If using an IDE (such as Intellij), import maven changes and add a java configuration from Java 13 or later. 
6. Run Main.java in your IDE, or compile it manually in the command line and run the resulting executable.
	
	To compile a Java program in the command line, use the command 'javac file-name',
	
	Then run 'java file-name' on the resulting file.
	
7. When running Main.java the application will appear on the screen, and from there you can use the application.


### 1. How to play

- Single player 
  - Select "Host" on the role menu
  - Select "Ok" to play when prompted about all players connected

- Multiplayer
  - Host - select "Host" on the role menu and wait for other players to connect. Press ok on the new prompt only when all players are connected. You can see this by waiting until you have a number of "recieved cards" in the consol log equal to the number of clients between each round.
  - Client - select "Client" on the role menu and connect to the host's IP adress
  - The UiB-owl with sparks on it is your character. 
  - As a host, you need to portforward port 54555 if you are playing multiplayer and you and the clients are on different connections. Guides to do this can be found online. If you are on the same internet connection as the clients, you don't need to portforward, but you need to find your local IP address. If you don't want to portforward, you can use a program such as Hamachi. 
  - To find your IP address, Google "What's my IP". This is only if you are on different connections than the clients.
  - To find your local IP address, for when  the client is on the same network, do the following:
  - On windows, hit Win+R and type cmd. Then write ipconfig. Your local IP address will be the IPv4 Address.
  - On Linux, open the terminal and type hostname -I. Your Local IP should show up
  - On macOS, if using wireless, type 'ipconfig getifaddr en1' in the terminal or 'ipconfig getifaddr en0' if you are on Ethernet.

### 2. Controls

Use keys 1-9 to select the corresponding card in your deck menu to choose your build. Wait for all players to complete their turn before you start the next turn. First player to reach all the flags wins.

# Manual tests

Manual tests can be found under ManualTests -> ManualTests.md


#  User stories
 
 User stories, acceptance criteria, and tasks can be found [here](https://docs.google.com/spreadsheets/d/1A_78OKM1BRXeeG4MR3e6AafYpPxnElqm3xPFjLozlGY/edit?usp=sharing)

# Class diagrams

Class diagramas can be found in the structure directory. Structure -> Class diagrams

# Acknowledgements 

A huge thank you to Olga Lohne for producing two pieces of original music for us to use in this project

# Known bugs

The host has to be the last actor to choose their 5 cards. Otherwise the program crashes.

All other players than you are seemingly turned the wrong direction. This doesn't affect gameplay that much as your position should always be correct.

You can't play the game using a M1 chip from Apple

