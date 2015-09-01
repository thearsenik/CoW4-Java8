# Code Of War 4 SDK for Java 8
This is a simple SDK for Code Of War 4 written with Java 8.

## Installation
Clone this repository or download as zip file. 

Once the source code is on you local drive, there are 2 solutions: 
- integrating it as an IntelliJ module (IntelliJ module is under CoW4-Java/CoW4-Java.iml)
- adding sources and GSon library to your project.

## Using SDK
**CAUTION: The `getShortestPath` method in `GameWorld` class is very simple and very slow. You must implement your own one.**

This SDK is very simple to use. Here are 2 steps to follow.
### Connecting to server
First instanciate a `SocketManager` and then call `connectToServer` method.
```java
new SocketManager().connectToServer(
    "localhost", // server domain or ip
    8127, // port
    "My_troll_AI", // AI name
    "https://mydomain.com/my-avatar.png", // Avatar image url
    CharacterSkin.BARBARIAN, // Character skin
    Main::executeTurn, // Function to call or lambda function
    new StaticGameWorld()); // The game world
```
There are 2 types of game worlds:
- `StaticGameWorld`: In this class the labyrinth data is hard coded. This gives your AI more time to process its orders. (might not work perfectly)
- `DynamicGameWorld`: This class parses game data every turn.

When connected to server this SDK starts an infinite loop that listen server messages.

### Managing turns
Once a turn message is read from server socket, the function you passed as parameter to `connectToServer` is called. Here is a simple implementation:

```java
public static List<Order> executeTurn(GameWorld world) {
    List<Order> orders = new ArrayList<>();
    Cell cell = world.getMyAI().getCell();
    Cell[][] labyrinth = world.getLabyrinth();
    int myAiColumn = cell.getColumn();
    int myAiLine = cell.getLine();
    Order order = null;
    if (cell.canLeft()) {
        order = new MoveOrder(labyrinth[myAiLine][myAiColumn - 1].getId());
    } else if (cell.canRight()) {
        order = new MoveOrder(labyrinth[myAiLine][myAiColumn + 1].getId());
    } else if (cell.canTop()) {
        order = new MoveOrder(labyrinth[myAiLine - 1][myAiColumn].getId());
    } else if (cell.canBottom()) {
        order = new MoveOrder(labyrinth[myAiLine + 1][myAiColumn].getId());
    }
    orders.add(order);
    return orders;
}
```

As you can see, `world` contains all data you need to take your decisions. Your function should return an order. There are 3 types of orders:
- `MoveOrder` : Your character will move to a `Cell`
- `PickUpOrder` : If you are above an item this order pick up this item.
- `UseItemOrder` : If you have an item, you can use it.

## Tools
`tests` package has 2 classes:
- `PerformanceTests` : Run this class as an application to measure `getShortestPath` processing time
- `CellPathTester` : Run this class as an application to launch a GUI drawing your paths. Use Shift + Click to place the starting point and Alt + Click to place the destination point.
