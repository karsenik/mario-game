//Your Name: Esra Korun
//Student ID: 2024400165

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        final int width = 800;
        final int height = 800;

        StdDraw.setCanvasSize(width, height);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
        StdDraw.setTitle("Super Mario Bros.");
        StdDraw.enableDoubleBuffering();

        int[][] obstacles = readFile("data/obstacles.txt");
        int[][] pipes     = readFile("data/pipes.txt");
        int[][] portals   = readFile("data/portals.txt");
        int[][] coins     = readFile("data/coins.txt");

        String clue0 = "Go to the exit pipe!";
        String clue1 = "Do not mess with mushrooms!";
        String clue2 = "Do not forget to collect all the coins!";

        Enemy enemy0 = new Enemy(340, 580, 40, 1, 260, 420); //new Enemy(x, y, size, direction, x1, x2)
        Enemy enemy1 = new Enemy(440, 300, 40, 1, 380, 500); //new Enemy(x, y, size, direction, x1, x2)

        Map map = new Map(obstacles, pipes, portals);
        ArrayList<Level> levels = new ArrayList<>();

        // level 1
        levels.add(new Level(1, new ArrayList<>(), clue0, new int[][]{}));

        // level 2
        ArrayList<Enemy> enemies2 = new ArrayList<>();
        enemies2.add(enemy0);
        enemies2.add(enemy1);
        levels.add(new Level(2, enemies2, clue1, new int[][]{}));

        // level 3
        levels.add(new Level(3, enemies2, clue2, coins));

        // level 4
        levels.add(new Level(4, enemies2, "Watch out for fireballs!", coins));

        // game start
        Game game = new Game(levels, map);
        game.run();


        Color skyColor = new Color(146, 144, 255);
        Color mainMenuColor0 = new Color(153, 78, 0);
        Color mainMenuColor1 = new Color(100, 50, 0);
        Color mainMenuColor2 = new Color(255, 204, 197);
        Color marioDarkBlue = new Color(32, 56, 136);
        Color marioRed = new Color(228, 0, 18);
        Color marioGold = new Color(255, 183, 0);
        Color marioLightBlue = new Color(107, 140, 255);

    }

    private static int[][] readFile(String path) {
        ArrayList<int[]> list = new ArrayList<>();
        try {
            Scanner sc = new Scanner(new File(path));
            while (sc.hasNextLine()) {
                String[] parts = sc.nextLine().split(",");
                list.add(new int[]{
                        Integer.parseInt(parts[0].trim()),
                        Integer.parseInt(parts[1].trim()),
                        Integer.parseInt(parts[2].trim()),
                        Integer.parseInt(parts[3].trim())
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list.toArray(new int[0][]);
    }
}
