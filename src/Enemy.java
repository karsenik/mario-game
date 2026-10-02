//Your Name
//Student ID

public class Enemy {
    private int x, y, size;
    private int x1, x2;
    private int speed = 4;
    private int direction;
    private int frameCount = 0;


    public Enemy(int x, int y, int size, int direction, int x1, int x2){
        this.x = x; this.y = y; this.size= size; this.direction = direction;
        this.x1 = x1; this.x2 = x2;
    }
    public int getX(){ return x;}
    public int getY(){ return y;}
    public int getSize(){ return size;}
    public void move(){
        x += direction*speed;
        if(x >= x2) direction = -1;
        if(x <= x1) direction = 1;
    }
    public void draw(){
        String sprite = "assets/mushroom.png";
        StdDraw.picture(x, y, sprite, size, size);
    }
    public double[] shoot(int currentFrame){
        frameCount++;
        if(frameCount % 60 == 0){
            return new double[]{this.x, this.y + 20, 8.0};
        }
        return null;
    }
}
