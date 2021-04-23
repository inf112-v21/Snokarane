# Manual Tests

### GUI test:
1. Start the game in single player mode.
2. Chose host in the first prompt, then press enter in the next one.
3. If graphics winodw appears the test is succsesful.

### Network test:
1. Launch a host
2. Open a different computer.
3. Find the hosts IP, using the guide above under "How to play"
4. Connect to the host IP to test the networking
5. If the host prints a connection, along with an ID and an IP address, the connection was successfull
6. The same should happen on the clients PC

### Card dealt test:
1. Start the game in either single or mutiplayer mode (if using muliplayer make sure there is a host!)
2. If cards appear on screen the test has passed.

### Card sequence test:
1. Start the game in either single or mutiplayer mode (if using muliplayer make sure there is a host!)
2. Select a sequence of cards and keep a note of which cards you've selected as well as the order.
3. If the player ends up the same place position in the GUI as predicted by going over your notes, the test has passed.

### Player is oriented in correct direction test:
1. Start the game in either single or mutiplayer mode (if using muliplayer make sure there is a host!)
2. Select cards that change direction of player if available (you have to select a total of five cards, all cards do not have to be rotaional cards, but at least one has to), if no rotainal cards exist; exit the game and start again from step 1. While selecting cards, make a note of what direction the player is supposed to be oriented.
   3 After all cards have been selected, check if the Graphical representaion of your player corresponds with your notes, if it does then the test has passed.

### Winning test:
1. Start the game in either single or mutiplayer mode (if using muliplayer make sure there is a host!)
2. Select a sequence of cards so that you progress towards a flag
3. Repeat step 2 until you land on a flag
4. Repeat step 2, but this time for a different flag. Repeat until all flags have been landed on.
5. When you visit the second flag, the game should close and the console should show the ID of the player along with a congratulation message.
6. If the game prints this message, the test has passed.

### Chat test:
1. Start the game as host
2. Start another client instance of the game as client on the same host pc
3. Join on client via "localhost" or empty IP address
4. Click "ok" on the all players ready prompt when the client is connected
5. Send a message in the chat in both the client and host instance, if both messages appear
   on both screens, the chat is receiving messages correctly

### Chat commands test:
1. Start the game as host
2. When the chat has loaded (the welcome message displays in the chat), enter /h to
   see available commands
3. If the commands pop up, the chat is correctly parsing the message as a command
4. Write </c set-color r> in the chat (without the '<>' symbols)
5. If the chat changes color to red, the set-color command is working correctly
6. Write <c set-name name> in the chat (without the '<>' symbols)
7. If the name at the beggining of the message changes to name, the set-name command
   is working correctly
8. Write <c font-scale 1> in the chat (without the '<>' symbols)
9. If the chat font has increased in size (default is 0.8), the font-scale command
   is working correctly


### CharacterCustomisationTest:
1. Start the game as usual.
2. Click on the "Customize" button.
3. Change color values as desiered using the sliders or textboxes next to them (values above 255 are set to 255, blank values is set to 0).
4. Take a screenshot (using your prefered method) or a take a picture of your screen using a camera (tips: your phones camra is easily accasible if you have a modern smartphone!)
   3.a Make sure to include the character preview iamge in the picture!

   3.b Save the picture somewhere you can easily find it and view it.

5. Click the save button.
6. Start a new game session.
   6.a Navigate back to the main menu by pressing the "back" button or by closing and realunching the application.

   6.b From the main click on the "host/join game" button.

   6.c Click on the "host game" button

   6.d A promt with the message "All players connected" should appear. press the "OK" button on the prompot.

7. Have a look at your charcter in game and compare with the screenshot/picture you took earlier.

   7.a if the playercharacter in the game matches the sceenshot/picture you took then the test has passed, if not the test has failed.

8. NOTE that If using the camera method keep in mind that your camra might warp the colors somewhat, if this is the case, use your best judgment to determine if the 		test has passed or not.



### Custom character over network test:
1. Follow the CharacterCustomisationTest up to stage 5 on two sepperate devices.
2. Host a Game on one of the devices, and connect with the other device.
3. The "Waiting for connections screen" on both devices should now show a preview of the custom characters on all devices.
4. Start the game on the host device.
5. Compare the pictures on both devices using the same method as described in "CharacterCustomisationTest"
6. If the customisation for each character appears the same as the way they were customisatised on their respective device;
   the test has passed, otherwise the test has failed



### Main menu music test:
1. Make sure your audio device is configured correctly on your device
2. Start the application as normal
3. If you can hear music the test has passed



### In game music test:
1. Make sure your audio device is configured correctly on your device
2. Start the application as normal
3. Host a game by clicking on "Host/Join game" -> "Host Game" -> "Start Game"
4. If you can hear music, then the test has passed


### Power down test:
1. Host a game with a minimum of 2 players
2. Make sure one player powers down on the first turn, by clicking the power down button, while the rest do not power down
3. If the player that powered down does not recieve cards, but the rest do, and the turn progresses after all the non-powered down players sent their cards, the test has passed 
