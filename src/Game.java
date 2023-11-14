import java.io.InputStreamReader;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Game {
    private static final HashMap<Integer, Room> roomsMap = new HashMap();
    private static int currentRoomId;
    private static Player player1 = new Player(); // Create an instance of the Player class


    public Game() {
    }

    public static void main(String[] args) {

        try {
            loadRoomsFromFile("room.txt");
//            displayRooms();
            playGame();
        } catch (IOException var2) {
            System.out.println("Error reading room data file: " + var2.getMessage());
        }

    }

    private static void loadRoomsFromFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.equals("----")) { // Detect the start of a new room entry
                int roomId = Integer.parseInt(reader.readLine()); // Read room ID
                String roomName = reader.readLine(); // Read room name
                String roomDescription = reader.readLine(); // Read room description
                reader.readLine(); // Skip the "false" line
                reader.readLine(); // Skip the "false" line

                String directionLine = reader.readLine(); // Read room directions
                HashMap<String, Integer> directions = new HashMap<>();// Stores directions and IDs in a hashmap

                String[] elements = directionLine.split(",\\s*");// Splits the directionLine into an array of
                // strings based on a comma-- separates the directions and their associated room IDs.


                for (String element : elements) { // Loop processes each element in the elements array, which represents a direction and a room ID:
                    String[] keyValue = element.split(":\\s*"); //Splits each element into a key (the direction) and a value (the room ID).


                    if (keyValue.length == 2) {
                        String key = keyValue[0].trim();
                        int value = Integer.parseInt(keyValue[1].trim());
                        directions.put(key, value);
                    }
                }

                boolean visited = Boolean.parseBoolean(reader.readLine()); // Read and assign the visited value
                Puzzle puzzle = null;
                ArrayList<Item> listOfItems = new ArrayList<>();
                reader.readLine();
                String itemsLine = reader.readLine(); // Read the item line

                if (!itemsLine.equals("")) {
                    String type = reader.readLine();
                    String itemDesc = reader.readLine();
                    Item item = new Item(itemsLine, itemDesc, type);
                    listOfItems.add(item);
                    String puzzleName = reader.readLine();

                    // Check if puzzleName is empty
                    if (!puzzleName.isEmpty()) {
                        String puzzleDescription = reader.readLine();
                        String puzzleAnswer = reader.readLine();

                        // Check if attemptsAllowed line is not empty before parsing it
                        String attemptsAllowedString = reader.readLine();
                        int attemptsAllowed = 0;
                        if (!attemptsAllowedString.isEmpty()) {
                            attemptsAllowed = Integer.parseInt(attemptsAllowedString);
                        }

                        // Create a Puzzle object
                        puzzle = new Puzzle(puzzleName, puzzleDescription, puzzleAnswer, attemptsAllowed);
                    } else {
                        // Set puzzle to null if puzzleName is empty
                        puzzle = null;
                    }
                }

// Now you have `puzzle` set to `null` if puzzleName is empty.


                Room room = new Room(roomId, roomName, roomDescription, directions, listOfItems, puzzle);
                room.setMonster(MonsterImpl.readFromFile());
                room.setVisited(visited);



                roomsMap.put(roomId, room); // Add the room to the map
//                System.out.println(room);
            }
        }

        reader.close();
    }


    private static void playGame() throws IOException {
        currentRoomId = 1;
        Room currentRoom = roomsMap.get(currentRoomId);
        Scanner input = new Scanner(System.in);
        System.out.println("Welcome to the Adventure Game!");
        System.out.println("You are currently in the " + currentRoom.getName());
        System.out.println(currentRoom.getDescription());
        boolean isRunning = true;

        while (isRunning) {

            Puzzle puzzle = currentRoom.getPuzzle();
            if (puzzle != null && !currentRoom.isPuzzleSolved()) {
                System.out.println("You have encountered a puzzle!");
                System.out.println(puzzle.getDescription());

                // Check if the puzzle is already solved
                while (puzzle.getRemainingAttempts() > 0) {
                    System.out.println("Remaining attempts: " + puzzle.getRemainingAttempts());
                    System.out.println("Try to solve the puzzle. Enter your answer:");
                    String playerAnswer = input.nextLine();

                    if (puzzle.checkAnswer(playerAnswer)) {
                        System.out.println("Congratulations! You solved the puzzle.");
                        currentRoom.setPuzzleSolved(true); // Mark the puzzle as solved for this room
                        break;
                    } else {
                        System.out.println("Wrong answer. Try again.");
                    }
                }

                if (puzzle.getRemainingAttempts() == 0) {
                    System.out.println("Sorry, you have failed the puzzle. Better luck next time!");
                    puzzle.setRemainingAttempts(puzzle.getAttemptsAllowed());
                }
            }



            System.out.println("Enter a command (N, S, E, W, explore, quit):"); // Add "explore" as a command
            String command = input.nextLine().toLowerCase();

            // Check if the current room has a puzzle


            switch (command) {
                case "n":
                case "s":
                case "e":
                case "w":
                    currentRoom = move(command);

                    break;
                case "explore":
                    // Display the items in the room when the "explore" command is used
                    ArrayList<Item> itemsInRoom = currentRoom.getItems();
                    if (!itemsInRoom.isEmpty()) {
                        System.out.println("Items in the room:");
                        for (Item item : itemsInRoom) {
                            System.out.println(item);
                        }
                    } else {
                        System.out.println("There are no items in this room.");
                    }
                    System.out.println(itemsInRoom);

                    break;
                case "pickup":
                    if (!currentRoom.getItems().isEmpty()) {
                        pickup(currentRoom.getItems());
                        System.out.println(currentRoom);
                    }
                    break;
                case "inventory":
                    player1.displayInventory();
                    break;
                case "drop":
                    if (!player1.getInventory().isEmpty()) {
                        player1.displayInventory();
                        System.out.println("Enter the name of the item you want to drop:");
                        String itemName = input.nextLine();
                        boolean itemFound = false;

                        for (Item item : player1.getInventory()) {
                            if (item.getName().equalsIgnoreCase(itemName)) {
                                player1.removeItemFromInventory(item);
                                currentRoom.getItems().add(item);
                                itemFound = true;
                                System.out.println("You dropped: " + item.getName());
                                break;
                            }
                        }

                        if (!itemFound) {
                            System.out.println("Item not found in your inventory.");
                        }
                    } else {
                        System.out.println("Your inventory is empty.");
                    }
                    break;
                case "inspect":
                    player1.displayInventory();
                    System.out.println("Enter the name of the item you want to inspect (or type 'room' to inspect the current room):");
                    String itemNameToInspect = input.nextLine();
                    boolean itemFound = false;

                    // Check if the item is in the player's inventory
                    for (Item item : player1.getInventory()) {
                        if (item.getName().equalsIgnoreCase(itemNameToInspect)) {
                            System.out.println(item.getDescription());
                            itemFound = true;
                            break;
                        }
                    }

                    // If the item is not in the player's inventory, check if it's in the current room
                    if (!itemFound) {
                        for (Item item : currentRoom.getItems()) {
                            if (item.getName().equalsIgnoreCase(itemNameToInspect)) {
                                System.out.println(item.getDescription());
                                itemFound = true;
                                break;
                            }
                        }
                    }

                    // If the item is not found in both inventory and room, inform the player
                    if (!itemFound) {
                        System.out.println("Item not found.");
                    }
                    break;
                case "ignore":
                    handleIgnore();
                    break;
                case "equip":
                    player1.displayInventory();
                    System.out.println("Enter what item you want to equip");
                    String equipedItem = input.nextLine();

                    for(Item item : player1.getInventory()){
                        System.out.println(item);
                        if(item.getName().equalsIgnoreCase(equipedItem) && item.getType().equalsIgnoreCase("weapon")){
                            System.out.println(item.getName() + " is equipped: attack is +10");
                            player1.setHasEquipped(true);
                        }else{
                            System.out.println("can't equip "+ item.getName());
                        }
                    }
                    break;
                case "unequip":
                    System.out.println("You have unequipped an item");
                    player1.setHasEquipped(false);
                    break;
                case "heal":
                    player1.displayInventory();
                    System.out.println("Enter what item you want to heal");
                    String heal = input.nextLine();

                    for(Item item : player1.getInventory()){
                        System.out.println(item);
                        if(item.getName().equalsIgnoreCase(heal) && item.getType().equalsIgnoreCase("food")){
                            player1.setHealth(player1.getHealth()+10);
                            System.out.println(item.getName() + " has heal you 10 health points: "+ player1.getHealth());
                        }
                    }
                    break;
                case "quit":
                    System.out.println("Thank you for playing!");
                    terminateGame(input, !isRunning);
                    return;

                default:
                    System.out.println("Invalid command. Please try again.");


            }
            Monster goblin = MonsterImpl.readFromFile();
            if (currentRoomId == goblin.getRoomId() && goblin.isAlive()) {
                fightMonster();
                break;
            }
        }
    }


    private static void terminateGame(Scanner input, boolean game) {
        if (game == false) {
            input.close();
        }
    }

    private static Player pickup(ArrayList<Item> items) {
        if (!items.isEmpty()) {
            Item itemToPickUp = items.get(0); // Get the first item in the ArrayList
            player1.addItemToInventory(itemToPickUp); // Add the item to the player's inventory
            items.remove(itemToPickUp); // Remove the item from the room's items list
            System.out.println("You picked up: " + itemToPickUp.getName());
        } else {
            System.out.println("There are no items in this room.");
        }
        return player1;
    }


    private static Room move(String direction) {
        Room currentRoom = roomsMap.get(currentRoomId);
        int nextRoomId = 0;
        boolean validMove = false;
        switch (direction) {
            case "n":
                if (currentRoom.getDirections().containsKey("North")) {
                    nextRoomId = currentRoom.getDirections().get("North");
                    validMove = true;
                }
                break;
            case "s":
                if (currentRoom.getDirections().containsKey("South")) {
                    nextRoomId = currentRoom.getDirections().get("South");
                    validMove = true;
                }
                break;
            case "e":
                if (currentRoom.getDirections().containsKey("East")) {
                    nextRoomId = currentRoom.getDirections().get("East");
                    validMove = true;
                }
                break;
            case "w":
                if (currentRoom.getDirections().containsKey("West")) {
                    nextRoomId = currentRoom.getDirections().get("West");
                    validMove = true;
                }
                break;
            default:
                System.out.println("Invalid direction. Please try again.");
        }

        if (validMove) {
            Room nextRoom = roomsMap.get(nextRoomId);
            if (nextRoom != null) {
                if (nextRoom.isVisited()) {
                    System.out.println("You have returned to the " + nextRoom.getName() + ".");
                }

                currentRoomId = nextRoomId;
                currentRoom = nextRoom;
            } else {
                System.out.println("Invalid exit in that direction. Please choose another direction.");
            }
        } else {
            System.out.println("There is no exit in that direction. Please choose another direction.");
        }

        currentRoom.setVisited(true);
        System.out.println("You are now in the " + currentRoom.getName());
        System.out.println(currentRoom.getDescription());


        return currentRoom;
    }

    private static void fightMonster() throws IOException {
        Scanner scanner = new Scanner(System.in);

        while (true) { // Start an infinite loop
            System.out.println("There's a goblin in here!");
            System.out.println("Would you like to fight this monster or ignore?");
            System.out.println("Enter 'Fight' to fight or 'Ignore' to ignore");

            String input = scanner.nextLine().trim(); // Read user input and trim whitespace

            if ("Fight".equalsIgnoreCase(input)) {
                System.out.println("You have chosen to fight!");
                handleFight();
                break; // Break the loop if valid input is provided
            } else if ("Ignore".equalsIgnoreCase(input)) {
                System.out.println("You have chosen to ignore the monster.");
                handleIgnore();
                break; // Break the loop if valid input is provided
            } else {
                System.out.println("Invalid input. Please enter 'Fight' or 'Ignore'.");
                // Do not read a new line here; just loop back and prompt again
            }
        }
    }


    private static void handleIgnore() throws IOException {
        Scanner input = new Scanner(System.in);
        System.out.println("You are ignoring the monster.");
        System.out.println("Which direction would you like to move? (n, s, e, w)");

        String direction = input.nextLine().toLowerCase();
        Room nextRoom = move(direction); // This calls the move method.
        if (nextRoom != null) {
            System.out.println("Moving to: " + nextRoom.getName());
            move(String.valueOf(input));
            currentRoomId = nextRoom.getId();
            playGame();
        } else {
            System.out.println("You can't move in that direction.");
        }
    }

    private static void handleFight() throws IOException {
        Integer playerHealth = player1.getHealth();
        Monster monster = MonsterImpl.readFromFile(); // Consider caching this instead of reading from file each time
        Integer monsterHealth = monster.getHealth();
        Integer monsterAttack = monster.getAttackDamage();
        //Integer playerWeaponDamage = player1.getEquippedWeaponDamage();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input;

        System.out.println("A goblin blocks your path!");
        System.out.println("Enter 'Attack' to attack with your weapon, 'Heal' to use a healing item, or 'Run' to escape.");

        while (playerHealth > 0 && monsterHealth > 0) {
            System.out.print("Your action: ");
            input = reader.readLine();

            if ("attack".equalsIgnoreCase(input)) {

                int min = 10;
                int max = 30;
                //Integer playerAttack = (int) (Math.random() * (max - min + 1)) + min + playerWeaponDamage;
                int playerAttack;
                if(player1.isHasEquipped() == true){
                    playerAttack = (int) (Math.random() * (max - min + 1)) + min + 10;
                }else{
                    playerAttack = (int) (Math.random() * (max - min + 1)) + min;
                }
                System.out.println("You attacked for " + playerAttack + " damage.");
                monsterHealth -= playerAttack;

                if (monsterHealth <= 0) {
                    System.out.println("The goblin is defeated!");
                    monster.setAlive(false);

                    // Add logic here to continue the game after defeating the goblin
                    System.out.println("Which direction would you like to move? (n, s, e, w)");
                    Scanner scanner = new Scanner(System.in); // Use existing Scanner or BufferedReader
                    String direction = scanner.nextLine().toLowerCase();
                    Room nextRoom = move(direction); // This calls the move method.
                    if (nextRoom != null) {
                        System.out.println("Moving to: " + nextRoom.getName());
                        currentRoomId = nextRoom.getId();
                        playGame(); // Continue the game
                    } else {
                        System.out.println("You can't move in that direction.");
                    }
                    break;

                }

                // Goblin's turn to attack
                System.out.println("The goblin attacks you for " + monsterAttack + " damage.");
                playerHealth -= monsterAttack;
                if (playerHealth <= 0) {
                    System.out.println("You have been defeated by the goblin.");
                    break;
                }

                System.out.println("Your health: " + playerHealth);
                System.out.println("Goblin's health: " + monsterHealth);



            }

        }

    }
}



