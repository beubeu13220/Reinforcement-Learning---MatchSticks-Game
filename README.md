# Reinforcement Learning 

## Application : matchsticks game 

### Game Rules 

* There are N matchsticks in the game. Two players play to the game. 
* A player in his or her turn can pick 1,..., J matchsticks. 
* The player who picks the **last matchstick loses the game**.
* N and J are chosen at the beginning of the game 


### How play and training the player 

To play you must run the jar file with the below command 
```console
java -jar reinforcementLearning-0.1.jar
```

**What happen ??**

- The first running step is to define the game and players
    - By default we define a game with **12 stick** and three actions, we can **pick 1,2 or 3 sticks** 
    - We initiate a random player and a human player (used after)
    - Two trainable players
- Second step, we train the two trainable players, one against the others, 10 000 times
- Then, one trainable player play against the random player 100 times 
- Our **knowledgeable player won +/- 96 % of the games** 
- After all, after the game initiate step and the training step, let's play !!

Below, a terminal output example with the percentage success of the trainable player and one game
between the intelligent player and a Human.

Note : At each step we can see the state of game (the remaining sticks)

```console
success percentage of robot player against random player : 0.96 %
||||||||||||
Play an action in the permitted action List(1, 2, 3)
> 2
Le player humanPlayer play the action : 2
||||||||||
Le player Player1 play the action : 1
|||||||||
Play an action in the permitted action List(1, 2, 3)
> 3
Le player humanPlayer play the action : 3
||||||
Le player Player1 play the action : 2
||||
Play an action in the permitted action List(1, 2, 3)
> 3
Le player humanPlayer play the action : 3
|
Le player Player1 play the action : 1
Le player Player1 loose the game
```


### Training method 

Before to play against a knowledgeable player, we must train it.
To do that, we used the reinforcement learning, especially the value function method. 
```math
V(s_{t}) = V(s_{t}) + \alpha * ( V(s_{t+1}) - V(s_{t}) )
where \alpha : Learning rate 
```

**How does it work**  
To use the value function, we define a *Reward* at the end of the game. Like 1 and -1 according to the final state. 
Then, at each game we update the value function at state t by the state at t + 1. 
For example, a player take actions 2,3,3 so we update his function value like :
```math
V(12) = V(12) + 0.001 * ( V(9) - V(12) )
The player starts the game at state 12, and pick 2 sticks. 
The state t + 1 isn't 10, but 9 because we must take the other player action to know the next state.

V(9) = V(9) + 0.001 * ( V(4) - V(9) )
The player picks 3 and the opponent picks 2, so the next state is 4

V(4) = V(4) + 0.001 * ( 1 - V(4) )
The last player action is to pick 3 sticks, so the opponent loose the game and the next state is the reward 1.
If the player had lost the game the next state value would have been -1. 
```
Note : At the beginning, the value function returns 0 for all states 

To train our players, we repeat the game 10 000 times between two knowledgeable players. At each game we improve the value 
function, and the players learn which states are good or not.
Also, we used the  \(\epsilon\) - greddy approach during the training step. 
At the beginning we used the value 0.99 as epsilon, so 99 % of actions are chosen randomly. Then, step by step,
at every 10 games, we decrease the epsilon value with a factor 0.99. 
So at the end, we almost play with the train value function. We play only 5% of actions randomly, to keep a little part
of exploration. 

Below, value function example of an intelligent player : 

| State    |     Value Function   
| :------- | :-------------: |
| 1        |     *-0.9923682501783281*      |
|         |          |
| 2        |   0.078039139908678          |
| 3        |     0.9292136060255751       |
| 4        |     0.167610796204391        |
|         |          |
| 5        |   *-0.8950600831910127*        |
|         |          |
| 6        |     0.05847639341421234      |
| 7        |     0.11244477461253402      |
| 8        |     0.8106200237881187       |
|         |          |
| 9        |     *-0.7677909736532793*      |
|         |          |
| 10       |     0.03730295796086518      |
| 11       |     0.03731668558845933      |
| 12       |     0.6530421665664732       |

The negative steps are the best state where we have to play the other player.
So according to the trainable player the best strategy is to pick 3 at each step. 

Note : The matchstick game is deterministic, so when we start the game with the good strategy we are sure to win the game.

### Compile Step 

You can modify some property in the Play object, especially in the Main :
* Number of sticks in the game
* The authorized number of sticks picked at each step 
* Epsilon values
* Iteration during training, and during game against random player 
* The order when human plays against robot player
* ... 

*To compile* 
```console
clean assembly 
```
When you compile, you also run the tests in the test/scala directory 


### Next version optimization

- We could add parameters to choose N stick and J actions when we launch the jar
- We will able to play (Human Player) more than one time again a trainable player
- Start the game between Human Player and Trainable Player with a random order 
- We should add an option to ensure the human player play against the trainable player
- Adding much comments on the code 
- Improve the tests quality 

### Note : Q-Learning vs Value Function 

>Les deux approches n'ont pas de sens à être vraiment confronté directement car elle n'ont pas vraiment les même utilisé en renforcement. v(s) te donne l'espérance de récompense sachant que tu est à l'état s. Alors que Q(s, a) te donne l'espérance de récompense sachant que tu est à l'état s et que tu prend l'action a. Donc si tu veux utiliser v(s) pour prendre des décisions (comme je l'ai fait avec les stick (renforcement #4) tu dois obligatoirement être capable de connaitre tout les états st+1 pour choisir l'état qui te donne la meilleur espérance et donc tu dois savoir quelles actions te ménera vers quelle état a t+1. Hors tu n'a generalement pas acces à ces informations.
C'est pour ça que le Q-learning est intéressant, tu n'a pas besoin de savoir que quand tu es à l'état s et que tu prend l'action a tu fini à l'état st+1, tu peux simplement calculer l'espérance de Q(s, a) et choisir l'espérance parmis toutes tes actions possible qui te donne le meilleur résultat.
Cela dit, la value function à toujours son utilisé à d'autre moment dans le renforcement