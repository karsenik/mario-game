//Your Name
//Student ID

import java.awt.*;
import java.util.PrimitiveIterator;

public class Map {
    private int[][] obstacles;
    private int[][] pipes;
    private int[][] portals;
    private int[][] coins;

    private boolean[] coinsCollected;
    private boolean justTeleported = false;

    public Map(int[][] obstacles, int[][] pipes, int[][] portals){
        this.obstacles = obstacles; this.pipes = pipes; this.portals = portals;
    }

    public void setCoins(int[][] coins){
        this.coins = coins;
        this.coinsCollected = new boolean[coins.length];
    }
    public boolean isOnGround(int x, int y, double halfSize, double speedY) {
        if (speedY > 0) return false;
        return checkCollision(x, (int)(y - halfSize - 1), 1, obstacles)
                || checkCollision(x, (int)(y - halfSize - 1), 1, pipes)
                || checkCollision(x, (int)(y - halfSize - 1), 1, portals)
                || y - halfSize <= 1;
    }

    public boolean isOnCeiling(int x, int y, double halfSize) {
        return checkCollision(x, (int)(y + halfSize + 1), 1, obstacles)
                || checkCollision(x, (int)(y + halfSize + 1), 1, pipes);
    }

    public int collectCoin(Mario mario){
        int collected = 0;
        for(int i = 0; i < coins.length; i++){
            if(coinsCollected[i]) continue;
            int dx = Math.abs(mario.x - coins[i][0]);
            int dy = Math.abs(mario.y - coins[i][1]);
            if(dx < mario.getSize() / 2.0 + coins[i][2]
                && dy < mario.getSize() / 2.0 + coins[i][3]){
                coinsCollected[i] = true;
                collected++;
            }
        }
        return collected;
    }

    public boolean allCoinsCollected(){
        for(boolean c: coinsCollected) {
            if(!c) return false;
        }
        return true;
    }

    public void handleTeleport(Mario mario, boolean sPressed){
        if (portals == null || portals.length < 2) return;

        int[] upperPortal = portals[0];
        int[] lowerPortal = portals[2];

        double hs = mario.getSize() / 2.0;
        if (!checkCollision(mario.x, mario.y, hs, portals)) {
            this.justTeleported = false;
        }

        if (this.justTeleported) return;

        if (checkCollision(mario.x, mario.y, hs, new int[][]{upperPortal})) {
            mario.x = lowerPortal[0];
            mario.y = lowerPortal[1] + lowerPortal[3] + (int)hs + 15;
            mario.speedY = 15;
            this.justTeleported = true;
            return;
        }

        if (sPressed && checkCollision(mario.x, mario.y, hs, new int[][]{lowerPortal})) {
            mario.x = upperPortal[0];
            mario.y = upperPortal[1] - upperPortal[3] - (int)hs - 15;
            mario.speedY = -10;
            this.justTeleported = true;
        }
    }

    public boolean isAtExit(Mario mario) {
        if (pipes == null || pipes.length < 4) return false;

        for (int i = 2; i < pipes.length; i++) {
            int[] p = pipes[i];
            boolean inside = Math.abs(mario.x - p[0]) < mario.getSize()/2.0 + p[2]
                    && Math.abs(mario.y - p[1]) < mario.getSize()/2.0 + p[3];
            if (inside) return true;
        }
        return false;
    }

    private boolean checkCollision(double nextX, double nextY, double playerHalfSize, int[][] obstacles){
        for(int[] obs: obstacles){
            if(Math.abs(nextX - obs[0]) < playerHalfSize + obs[2]
            && Math.abs(nextY - obs[1]) < playerHalfSize + obs[3]) return true;
        }
        return false;
    }

    public boolean isInsideObstacle(Mario mario, int x, int y, double halfSize, boolean spawning, boolean sPressed) {
        if (checkCollision(x, y, halfSize, obstacles)) return true;

        if (!spawning && checkCollision(x, y, halfSize, pipes)) {
            if (isAtExit(mario)) {
                return false;
            }
            return true;
        }

        if (portals != null && portals.length >= 4) {
            int[] lowerPlatform = portals[3];

            if (checkCollision(x, y, halfSize, new int[][]{lowerPlatform})) {
                if (sPressed) return false;
                return true;
            }

            for (int i = 0; i <= 2; i += 2) {
                int[] p = portals[i];
                if (checkCollision(x, y, halfSize, new int[][]{p})) {
                    boolean atCenter = Math.abs(x - p[0]) < p[2] * 0.4;
                    if (!atCenter) return true;
                }
            }
        }
        return false;
    }


    public void draw(){
        StdDraw.setPenColor(new Color(146, 144, 255));
        StdDraw.filledRectangle(400, 400, 400, 400);

        for(int[] o : obstacles) {
            StdDraw.picture(o[0], o[1], "assets/block.png", o[2]*2, o[3]*2);
        }

        StdDraw.setPenColor(new Color(255, 183, 0));
        for(int[] p : pipes){
            StdDraw.filledRectangle(p[0], p[1], p[2], p[3]);
        }

        StdDraw.setPenColor(new Color(228, 0, 18));
        for(int[] p : portals){
            StdDraw.filledRectangle(p[0], p[1], p[2], p[3]);
        }

        if(coins != null){
            for(int i = 0; i < coins.length; i++){
                if(!coinsCollected[i]){
                    StdDraw.picture(coins[i][0], coins[i][1], "assets/coin.png", coins[i][2]*2, coins[i][3]*2);
                }
            }
        }
    }

    public int[][] getPipes(){
        return pipes;
    }
    public boolean checkPipeCollision(int x, int y, double halfSize) {
        return checkCollision(x, y, halfSize, pipes);
    }

}
