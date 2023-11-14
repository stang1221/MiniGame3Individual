import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import java.util.List;

public class MonsterImpl {

    public static Monster readFromFile() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("Monster.txt"));
        String name = lines.size() > 0 ? lines.get(0) : "";
        String desc = lines.size() > 1 ? lines.get(1) : "";
        int attackDamage = lines.size() > 2 ? Integer.parseInt(lines.get(2)) : 0;
        int health = lines.size() > 3 ? Integer.parseInt(lines.get(3).replaceAll("-", "").trim()) : 0;
        int roomId = Integer.parseInt(lines.size() > 4 ? lines.get(4) : "");


        Monster goblin = new Monster();
        goblin.setName(name);
        goblin.setDescription(desc);
        goblin.setAttackDamage(attackDamage);
        goblin.setHealth(health);
        goblin.setRoomId(roomId);

        return goblin;
    }
}
