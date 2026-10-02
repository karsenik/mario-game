//Your Name
//Student ID

public class Mario {
    private final int spawnX;
    private final int spawnY;
    private final int size = 40;

    public int x, y;
    public double speedY = 0;
    public double gravity = -0.5;
    private int deathCount = 0;
    private boolean isDead = false;
    public int frame = 0;
    public boolean onGround = false;
    public boolean spawning = true;

    private boolean facingRight = true;
    private boolean wWasPressed = false;
    private String currentSprite = "assets/standRight.png";

    public Mario(int spawnX, int spawnY) {
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.x = spawnX;
        this.y = spawnY;
    }

    public int getSize() { return size; }
    public int getDeathCount() { return deathCount; }
    public boolean isDead() { return isDead; }

    public void handleInput(int frame, boolean onGround, Map map) {
        if (isDead) return;
        int prevX = x;

        if (StdDraw.isKeyPressed('A')) {
            facingRight = false;
            x -= 6;
        }

        if (StdDraw.isKeyPressed('D')) {
            facingRight = true;
            x += 6;
        }

        boolean wNow = StdDraw.isKeyPressed('W');
        if (wNow && !wWasPressed && onGround) jump();
        wWasPressed = wNow;

        boolean isMoving = (x != prevX);
        updateSprite(frame, onGround, isMoving);
    }
    public void updateSprite(int frame, boolean onGround, boolean isMoving){
        if (isDead) {
            currentSprite = "assets/dead.png";
            return;
        }

        if (!onGround) {
            currentSprite = facingRight ? "assets/jumpRight.png" : "assets/jumpLeft.png";
            return;
        }

        if (isMoving) {
            int walkFrame = (frame / 8) % 2;
            if (facingRight) {
                currentSprite = walkFrame == 0 ? "assets/walkRight1.png" : "assets/walkRight2.png";
            } else {
                currentSprite = walkFrame == 0 ? "assets/walkLeft1.png" : "assets/walkLeft2.png";
            }
            return;
        }

        currentSprite = facingRight ? "assets/standRight.png" : "assets/standLeft.png";
    }

    public void jump() { speedY = 12; }

    public boolean checkEnemyCollision(Enemy enemy) {
        int dx = Math.abs(x - enemy.getX());
        int dy = Math.abs(y - enemy.getY());
        return dx < (size + enemy.getSize()) / 2.0
                && dy < (size + enemy.getSize()) / 2.0;
    }

    public void die() {
        if (isDead) return;
        isDead = true;
        deathCount++;
        speedY = 8;
        currentSprite = "assets/dead.png";
    }

    public void respawn() {
        x = spawnX;
        y = spawnY -80;
        speedY = 0;
        isDead = false;
        onGround = false;
        currentSprite = "assets/standRight.png";
    }

    public void applyGravity() {
        speedY += gravity;
        y += (int) speedY;
    }

    public void draw() {
        StdDraw.picture(x, y, currentSprite, size, size);
    }
}