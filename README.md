#AntWorld
### A project for CS 351, Fall 2014.
Program Version: 1.0 <br>
Client Version: clientPack_2014_9_10 <br>
TeamName: Toothachegrass <br>
NestName: YELLOW_MEADOW <br>

------------------------------------------------------------------


####Authors
J. Jake Nichol  jjaken@unm.edu <br>
Troy Squillaci   zivia@unm.edu

###Project Description
AntWorld is a multiplayer game in which players control a nest of ants.  Nests are distributed across a map that is populated with several different types of food.  Each type of food is used to birth a new ant of that type (ex. speed food births speed ants).<br> <br>
Players must write an AI control for the ant nest.  Ants must venture out into the world to collect food and bring it back to the nest.  The ants may encounter agressive ants from other nests, and must act accordingly.  

###Our Solution
Upon exiting the nest, ants begin a psuedo-random walk to find food.  Upon finding food, the nearest ants, up to four, will gravitate to the food.  Ants will pick up the food, plot course for the nest, and set off.  Once at the nest, ants will enter to drop the food, and then exit to restart the search.  If any ant's health drops below 1/4 it's total health, the ant will immediately go to the nest, ant rest there until fully healed.  Additionally, we have a set number of ants collecting water, from the nearest lake, and bringin it back to the nest.  
\* We have also implemented AStar pathfinding and an avoidance algorithm for when ants are in range of enemies.  See **Notable Features** below for more information on these implementations.

###How to use
This program must be run with an up to date version of Joel Castellanos' AntWorld server and client pack.
  * The server version is maintained by Joel Castellanos and can be assumed to be equivalent to the most current client pack.
  * The current client version is at the top of this file.


1. Package the java project into an executable jar.
2. From the terminal, run:


    ```
    $ java -jar <path_to_jar_file> <appropriate_server_name>
    ```


3. A JFrame will appear with a map of the world.  The ants are painted at their locations as well as food and enemy ants.  Our ants are color coded to show status as follows:
  * Orange - Searching for food
  * Teal - Approaching discovered food
  * Green - Carrying food back to nest
  * Black - Injured (15 health remaining)
  * Blue - Enemy ant
  * White - State is unknown (not seen to occur)


4. Rectangles are drawn over our nest.  
  * The larger white one indicates the area in which our ants are allowed to roam.  This keeps them from getting too close to enemy territory while still having plenty of food accessible.  
  * The Smaller red rectangles are areas in which our ants cannot enter.  These were found to have little food, and too many enemies.


5. Buttons on the JFrame are as follows:
  * Return To Nest - Returns all ants back to the nest.  Upon arrival, ants resume activity.
  * Force Random Walk - Forces all ants to walk randomly for a short time.


6. A table of our ants displays the following information:
  * ID - Each ant's ID
  * Grid X - Ant's x location
  * Grid Y - Ant's y location
  * Alive - If ant is alive
  * Type - Ant type
  * Carry Type - Carried items' type
  * Carry Units - Number of units carried
  * Health - Ant's ant
  * Underground - If the ant is underground


###Notable Features
* AStar is multithreaded on 12 threads.
  * The graph is constructed as the path is found.
* To avoid enemy ants, we draw a rectangle around every friendly ant.  If enemy ants enter this rectangle, our ant will calculate their average location and begin walking int he opposite direction.  This leads to very few deaths from brainless bots.  This only fails when the enemy ants surround our ant.
* An unimplemented group structure is included in antworld.group.  This structure was designed to have the ants form into groups which will hold travel and battle formations when appropriate.  These groups had different types, similar to ant types, which dictate their main function.  
  * Due to time constraints higher priorities, this feature was never fully implemented and eventually abandoned.
