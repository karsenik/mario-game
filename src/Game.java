//Your Name
//Student ID

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Game {
    private ArrayList<Level> levels;
    private Map map;
    private int width = 800;
    private int height = 800;
    private int pauseDuration = 25;
    private boolean followCamera = false;
    private boolean cWasPressed = false;
    private ArrayList<double[]> bullets = new ArrayList<>();

    public Game(ArrayList<Level> levels, Map map) {
        this.levels = levels;
        this.map = map;
    }

    public void run() {
        Mario mario = new Mario(100, 140);
        startScreen(mario);

        while (StdDraw.isKeyPressed(' ')) StdDraw.pause(10);

        long startTime = System.currentTimeMillis();
        int currentLevelIdx = 0;

        while (currentLevelIdx < levels.size()) {
            Level level = levels.get(currentLevelIdx);
            map.setCoins(level.getCoins());
            ArrayList<Enemy> enemies = level.getEnemies();

            int[] startPipe = map.getPipes()[0];
            mario = new Mario(startPipe[0], startPipe[1]-startPipe[3]);
            mario.spawning = true;

            boolean levelComplete = false;
            int frame = 0;

            while (!levelComplete) {

                updateCamera(mario);

                map.draw();

                double halfSize = mario.getSize() / 2.0;
                boolean onGround = map.isOnGround(mario.x, mario.y, mario.getSize() / 2.0, mario.speedY);

                // death
                if (mario.isDead()) {
                    mario.speedY += mario.gravity;
                    mario.y += (int) mario.speedY;

                    if (mario.y < -50) mario.respawn();

                    for (Enemy e : enemies) e.draw();
                    mario.draw();
                    drawHUD(mario, level, startTime);
                    StdDraw.show();
                    StdDraw.pause(pauseDuration);
                    continue;
                }

                boolean sPressed = StdDraw.isKeyPressed('S');
                int prevX = mario.x;
                mario.handleInput(frame, onGround, map);

                if (onGround && mario.speedY < 0) {
                    mario.speedY = 0;
                }

                if (map.isInsideObstacle(mario, mario.x, mario.y, halfSize, mario.spawning, sPressed)) {
                    mario.x = prevX;
                }

                mario.speedY += mario.gravity;
                mario.y += (int) mario.speedY;

                // spawn
                if (mario.spawning) {
                    mario.speedY += mario.gravity;
                    mario.y += (int) mario.speedY;

                    if (map.isOnGround(mario.x, mario.y, halfSize, mario.speedY)) {
                        mario.speedY = 0;
                        mario.spawning = false;
                    }

                    boolean outsidePipe = !map.checkPipeCollision(mario.x, mario.y, halfSize);
                    if (outsidePipe) mario.spawning = false;

                    mario.draw();
                    StdDraw.show();
                    StdDraw.pause(pauseDuration);
                    continue;
                }


                // vertical collision
                if (map.isInsideObstacle(mario, mario.x, mario.y, halfSize, mario.spawning, sPressed)) {
                    if (mario.speedY < 0) {
                        while (map.isInsideObstacle(mario, mario.x, mario.y, halfSize, mario.spawning, sPressed)) {
                            mario.y++;
                        }
                    } else {
                        while (map.isInsideObstacle(mario, mario.x, mario.y, halfSize, mario.spawning, sPressed)) {
                            mario.y--;
                        }
                    }
                    mario.speedY = 0;
                }

                map.handleTeleport(mario, sPressed);

                if (mario.x - halfSize < 0) mario.x = (int) halfSize;
                if (mario.x + halfSize > width) mario.x = (int)(width - halfSize);
                if (mario.y - halfSize < 0) {
                    mario.y = (int) halfSize;
                    mario.speedY = 0;
                }

                // restart
                if (StdDraw.isKeyPressed('R')) {
                    currentLevelIdx = 0;
                    startTime = System.currentTimeMillis();
                    break;
                }

                // enemies
                for (Enemy e : enemies) {
                    e.move();
                    e.draw();

                    if(level.getId() == 4){
                        double[] newShot = e.shoot(frame);
                        if(newShot != null) bullets.add(newShot);
                    }
                    if (mario.checkEnemyCollision(e)) mario.die();
                }

                //bullet
                for(int i = 0; i < bullets.size(); i++){
                    double[] b = bullets.get(i);
                    b[1] += b[2];

                    StdDraw.picture(b[0], b[1], "assets/bullet.png", 20, 20);

                    double dx = Math.abs(mario.x - b[0]);
                    double dy = Math.abs(mario.y - b[1]);
                    if(dx < halfSize + 10 && dy < halfSize + 10){
                        mario.die();
                        bullets.remove(i);
                        i--;
                        continue;
                    }

                    if(b[1] > height){
                        bullets.remove(i);
                        i--;
                    }
                }

                map.handleTeleport(mario, StdDraw.isKeyPressed('S'));
                map.collectCoin(mario);
                mario.draw();
                drawHUD(mario, level, startTime);

                if (!mario.spawning && map.isAtExit(mario)) {
                    if (level.getId() >= 3){
                        if(map.allCoinsCollected()){
                            levelComplete = true;
                        }
                    }else{
                        levelComplete = true;
                    }
                }

                StdDraw.show();
                StdDraw.pause(pauseDuration);
                frame++;
            }

            bullets.clear();

            if (!StdDraw.isKeyPressed('R')) {
                levelBanner(currentLevelIdx + 1, level, enemies, mario, getTime(startTime));
                currentLevelIdx++;
            }
        }
        while (StdDraw.isKeyPressed(' ')) StdDraw.pause(10);

        int result = endScreen(getTime(startTime), mario.getDeathCount());

        if (result == 1) {
            run();
        } else {
            System.exit(0);
        }
    }

    private void startScreen(Mario mario) {
        boolean spacePressed = false;
        int frame = 0;

        while (true) {
            boolean spaceNow = StdDraw.isKeyPressed(' ');
            if (spaceNow && !spacePressed) break;
            spacePressed = spaceNow;

            StdDraw.setPenColor(new Color(146, 144, 255));
            StdDraw.filledRectangle(400, 400, 400, 400);

            StdDraw.setPenColor(new Color(100, 50, 0));
            StdDraw.filledRectangle(400, 460, 242, 72);
            StdDraw.setPenColor(new Color(139, 69, 19));
            StdDraw.filledRectangle(400, 460, 240, 70);
            StdDraw.setPenColor(new Color(255, 204, 197));
            StdDraw.setFont(new Font("Arial", Font.BOLD, 28));
            StdDraw.text(400, 480, "SUPER");
            StdDraw.text(400, 425, "MARIO BROS.");

            StdDraw.setFont(new Font("Arial", Font.BOLD, 16));
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.text(400, 360, "Press [Space] to start");
            StdDraw.setFont(new Font("Arial", Font.PLAIN, 16));
            StdDraw.text(400, 330, "Move: [A] [D] [W]");
            int fps = 1000 / pauseDuration;
            StdDraw.text(400, 300, "FPS: ~" + fps + "   Adjust: [←] [→]");

            if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT)) pauseDuration++;
            if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT) && pauseDuration > 8) pauseDuration--;

            mario.handleInput(frame, mario.onGround, map);
            mario.applyGravity();

            if (mario.y <= 140) {
                mario.y = 140;
                mario.speedY = 0;
                mario.onGround = true;
            } else {
                mario.onGround = false;
            }
            if (mario.x < 15) mario.x = 15;
            if (mario.x > width - 15) mario.x = width - 15;

            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 20; col++) {
                    StdDraw.picture(20 + col * 40, 20 + row * 40,
                            "assets/block.png", 40, 40);
                }
            }

            mario.draw();
            StdDraw.show();
            StdDraw.pause(pauseDuration);
            frame++;
        }

        this.pauseDuration = pauseDuration;
    }

    private long levelBanner(int id, Level currentLevel, ArrayList<Enemy> currentEnemies,
                             Mario mario, String elapsedTime) {
        while (StdDraw.isKeyPressed(' ')) StdDraw.pause(10);
        boolean spacePressed = false;

        while (true) {
            boolean spaceNow = StdDraw.isKeyPressed(' ');
            if (spaceNow && !spacePressed) break;
            spacePressed = spaceNow;

            StdDraw.setPenColor(new Color(32, 56, 136));
            StdDraw.filledRectangle(400, 400, 400, 100);

            StdDraw.setPenColor(StdDraw.YELLOW);
            StdDraw.setFont(new Font("Arial", Font.BOLD, 32));
            StdDraw.text(400, 420, "Level " + id + " Complete!");

            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.setFont(new Font("Arial", Font.PLAIN, 16));
            StdDraw.text(400, 380, "Press [Space] to Continue");

            StdDraw.show();
            StdDraw.pause(pauseDuration);
        }

        return System.currentTimeMillis();
    }

    private int endScreen(String elapsedTime, int deathCount) {
        int selected = 0;
        boolean upWasPressed = false;
        boolean downWasPressed = false;
        boolean spaceWasPressed = false;

        while (true) {
            boolean upNow    = StdDraw.isKeyPressed(KeyEvent.VK_UP);
            boolean downNow  = StdDraw.isKeyPressed(KeyEvent.VK_DOWN);
            boolean spaceNow = StdDraw.isKeyPressed(' ');

            if (upNow && !upWasPressed)     selected = (selected + 1) % 2;
            if (downNow && !downWasPressed) selected = (selected + 1) % 2;
            if (spaceNow && !spaceWasPressed) break;

            upWasPressed    = upNow;
            downWasPressed  = downNow;
            spaceWasPressed = spaceNow;


            StdDraw.setPenColor(new Color(30, 40, 110));
            StdDraw.filledRectangle(400, 400, 400, 400);

            StdDraw.setPenColor(new Color(255, 183, 0));
            StdDraw.setFont(new Font("Arial", Font.BOLD, 52));
            StdDraw.text(400, 620, "You Won!");

            StdDraw.setFont(new Font("Arial", Font.BOLD, 28));

            if (selected == 0) {
                StdDraw.setPenColor(new Color(255, 183, 0));
                StdDraw.text(400, 420, "> Exit <");
            } else {
                StdDraw.setPenColor(new Color(150, 160, 220)); // soluk
                StdDraw.text(400, 420, "Exit");
            }

            if (selected == 1) {
                StdDraw.setPenColor(new Color(255, 183, 0));
                StdDraw.text(400, 360, "> Restart <");
            } else {
                StdDraw.setPenColor(new Color(150, 160, 220));
                StdDraw.text(400, 360, "Restart");
            }

            StdDraw.setFont(new Font("Arial", Font.BOLD, 16));
            StdDraw.setPenColor(new Color(200, 200, 255));
            StdDraw.text(400, 220, "Time: " + elapsedTime);
            StdDraw.text(400, 190, "Deaths: " + deathCount);

            StdDraw.show();
            StdDraw.pause(pauseDuration);
        }

        return selected;
    }

    private void updateCamera(Mario mario) {
        // C toggle
        boolean cNow = StdDraw.isKeyPressed('C');
        if (cNow && !cWasPressed) followCamera = !followCamera;
        cWasPressed = cNow;

        if (!followCamera) {
            StdDraw.setXscale(0, width);
            StdDraw.setYscale(0, height);
        } else {
            int viewW = width / 2;
            int viewH = height / 2;

            double camX = mario.x;
            double camY = mario.y;

            camX = Math.max(viewW / 2.0,     camX);
            camX = Math.min(width - viewW / 2.0,  camX);
            camY = Math.max(viewH / 2.0,     camY);
            camY = Math.min(height - viewH / 2.0, camY);

            StdDraw.setXscale(camX - viewW / 2.0, camX + viewW / 2.0);
            StdDraw.setYscale(camY - viewH / 2.0, camY + viewH / 2.0);
        }
    }

    private void drawHUD(Mario mario, Level level, long startTime) {
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);

        StdDraw.setPenColor(new Color(32, 56, 136));
        StdDraw.filledRectangle(400, 80, 400, 80);
        StdDraw.setFont(new Font("Arial", Font.BOLD, 14));

        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.textLeft(10, 120, "Move: [A] [D] [W]");
        StdDraw.textLeft(10, 80, "Restart: [R]");
        StdDraw.textLeft(10, 40, "Camera: [C]");

        StdDraw.setPenColor(StdDraw.YELLOW);
        StdDraw.text(400, 80, level.getClue());

        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.textRight(790, 120, "Level " + level.getId());
        StdDraw.textRight(790, 80, getTime(startTime));
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.textRight(790, 40, "Deaths: " + mario.getDeathCount());

        if(followCamera){
            updateCamera(mario);
        }
    }

    private String getTime(long startingTime) {
        long ms = System.currentTimeMillis() - startingTime;
        return String.format("%02d:%02d:%02d",
                ms / 60000, (ms % 60000) / 1000, (ms % 1000) / 10);
    }
}
