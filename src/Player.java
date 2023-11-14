import java.util.ArrayList;

public class Player {
    private final ArrayList<Item> inventory;

    private Integer health = 100;

    private boolean hasEquipped;

    public boolean isHasEquipped() {
        return hasEquipped;
    }

    public void setHasEquipped(boolean hasEquipped) {
        this.hasEquipped = hasEquipped;
    }

    public Player() {
        this.inventory = new ArrayList<>();
    }


    @Override
    public String toString() {
        return "Player{" +
                "inventory=" + inventory +
                ", health=" + health +
                '}';
    }

    public Player(ArrayList<Item> inventory, Integer health) {
        this.inventory = inventory;
        this.health = health;
    }

    public void addItemToInventory(Item item) {
        inventory.add(item);
    }

    public void removeItemFromInventory(Item item) {
        inventory.remove(item);
    }

    public Integer getHealth() {
        return health;
    }

    public void setHealth(Integer health) {
        this.health = health;
    }

    public ArrayList<Item> getInventory() {
        return inventory;
    }

    public void displayInventory() {
        if (inventory.isEmpty()) {
            System.out.println("Your inventory is empty.");
        } else {
            System.out.println("Items in your inventory:");
            for (Item item : inventory) {
                System.out.println(item.getName());
            }
        }
    }
}

