#AntWorld
### A project for CS 351, Fall 2014.
Version 1.0
------------------------------------------------------------------


####Authors
J. Jake Nichol  jjaken@unm.edu <br>
Troy Sqillaci   zivia@unm.edu

###Project Description
AntWorld is a multiplayer game in which players control a nest of ants.  Nests are distributed across a map that is populated with several different types of food.  Each type of food si used to birth a new ant of that type (ex. speed food births speed ants).<br> <br>
Players must write an AI control for the ant nest.  Ants must venture out into the world to collect food and bring it back to the nest.  The ants may encounter agressive ants from other nests, and must act accordingly.  

###Our Solution
Upon exiting the nest, ants begin a psuedo-random walk to find food.  Upon finding food, the nearest ants, up to four, will gravitate to the food.  Ants will pick up the food, plot course for the nest, and set off.  Once at the nest, ants will enter to drop the food, and then exit to restart the search.  If any ant's health drops below 1/4 it's total health, the ant will immediately go to the nest, ant rest there until fully healed.  

###How to use
1. These are instructions for using the program
  * Describe instruction
2. .
3. .
4. ...
5. 
###Notable Features
* ...
