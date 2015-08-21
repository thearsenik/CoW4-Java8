# Code Of War 4 SDK for Java 8
This is a simple SDK for Code Of War 4 written with Java 8.

## Installation
Clone this repository or download as zip file. 

Once the source code is on you local drive, there is 2 solutions: 
- integrating it as an IntelliJ module (IntelliJ module is under CoW4-Java/CoW4-Java.iml)
- adding sources and GSon library to your project.

## Using SDK
This SDK is very simple to use. Here are 2 steps to follow.
### Connecting to server
First instanciate a `SocketManager` and then call `connectToServer` method.
```java
new SocketManager().connectToServer(
    "localhost", // server domain or ip
    8127, // port
    "My_troll_IA", // IA name
    "https://mydomain.com/my-avatar.png", // Avatar image url
    CharacterSkin.BARBARIAN, // Character skin
    Main::executeTurn, // Function to call or lambda function
    new StaticGameWorld()); // The game world
```
There are 2 types of game worlds:
- `StaticGameWorld`: In this class the labyrinth data is hard coded. This gives your IA more time to process its orders. (might not work perfectly)
- `DynamicGameWorld`: This class parses game data every turn.

When connected to server this SDK starts an infinite loop that listen server messages.

### Managing turns
Once a turn message is read from server socket, the function you passed as parameter to `connectToServer` is called. Here is a simple implementation:

```java
public static List<Order> executeTurn(GameWorld world) {
    List<Order> orders = new ArrayList<>();
    Cell cell = world.getMyIA().getCell();
    Cell[][] labyrinth = world.getLabyrinth();
    int myIaColumn = cell.getColumn();
    int myIaLine = cell.getLine();
    Order order = null;
    if (cell.canLeft()) {
        order = new MoveOrder(labyrinth[myIaLine][myIaColumn - 1].getId());
    } else if (cell.canRight()) {
        order = new MoveOrder(labyrinth[myIaLine][myIaColumn + 1].getId());
    } else if (cell.canTop()) {
        order = new MoveOrder(labyrinth[myIaLine - 1][myIaColumn].getId());
    } else if (cell.canBottom()) {
        order = new MoveOrder(labyrinth[myIaLine + 1][myIaColumn].getId());
    }
    orders.add(order);
    return orders;
}
```

As you can see, `world` contains all data you need to take your decisions. Your function should return an order. There are 3 types of orders:
- `MoveOrder` : Your character will move to a `Cell`
- `PickUpOrder` : If you are above an item this order pick up this item.
- `UseItemOrder` : If you have an item, you can use it.
