# Connect4
This is a connect 4 game using [MinMax with alpha-beta pruning](https://www.youtube.com/watch?v=l-hh51ncgDI) implemented in java with java swing. Before you run it make sure to download and import the [org.json](https://repo1.maven.org/maven2/org/json/json/20230227/json-20230227.jar) library.

![image](https://github.com/KonstantinosGalanis/Connect4/assets/147558588/8b7b6249-a1ef-41c6-bbf9-664b07dd3a6a)

# Functionality
The main menu conatains the following features:

*[New Game]: This menu is where you start a new game. By selecting it, it appears
a list of difficulty levels that the player can choose from. These levels are displayed with
in the JMenuItems format and are as follows:

        1. Trivial: The AI ​​module looks at one move in depth.
        2. Medium: The AI ​​module looks three moves deep.
        3. Hard: The AI ​​module looks five moves deep.

*[1st Player]: A two-option menu appears via menu radio button with the options “AI” and
"You". The AI ​​option is defaulted at program startup and gives the first
movement of each new game to the AI ​​module, while the You option to the application user. The choice
“AI” or “You” has no effect on the current game and is about starting the next one
game that will be played through the “New Game” menu.

*History: The game canvas "disappears" from the window and a scrollable one appears
list (via JList) of previously completed games. For every game he has
completed, the following information is displayed:

        1. start date and time
        2. the difficulty level of the AI ​​module
        3. if the AI ​​module or the user won

*Help: The menu is disabled.
