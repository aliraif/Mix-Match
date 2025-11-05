package tetris;

import java.awt.*;

public class Shape {
    private Color color;

    private int x = 4, y = 0;
    private int normal = 600;
    private int fast = 50;
    private int delayTimeForMovement = normal;
    private long beginTime;
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private static final int BLOCK_SIZE = 30;

    private int deltaX = 0;
    private boolean collision = false;
    private int[][] coords;
    private Board board;

    public Shape(int[][] coords,Board board ,Color color){
        this.coords = coords;
        this.board = board;
        this.color = color;
    }

    public int[][] getPattern() {
        return coords;
    }

    public void setX(int x){
        this.x = x;
    }

    public void setY(int y){
        this.y = y;
    }

    public void reset(){
        this.x = 4;
        this.y = 0;
        collision = false;
    }

    public void update() {
        if(collision) {
            // fill the color for board
            for(int row = 0; row < coords.length;row++){
                for(int col = 0 ; col < coords[0].length;col++){
                    if (coords[row][col] != 0){
                        board.getBoard()[y + row][x + col] = color;
                    }
                }
            }
            // set current shape
            board.setCurrentShape();
            return;
        }


        // check moving horizontal
        boolean moveX = true;
        if(!(x + deltaX + coords[0].length > 10) && !(x + deltaX < 0)) {
            // horizontal collision logic
            for(int row =0; row < coords.length; row++){
                for(int col = 0;col < coords[row].length; col++ ){
                    if(coords[row][col]!= 0) {
                        if (board.getBoard()[y + row][x + deltaX + col] != null) {
                            moveX = false;
                        }
                    }
                }
            }
            if(moveX) {
                x += deltaX;
            }
        }
        deltaX = 0;

        if(System.currentTimeMillis() - beginTime > delayTimeForMovement) {
            // vertical movement
            if(!(y + 1 + coords.length > BOARD_HEIGHT)) {
                // vertical collision logic
                for(int row =0; row < coords.length;row++){
                    for(int col = 0;col < coords[row].length; col++ ){
                        if(coords[row][col]!= 0){
                            if(board.getBoard()[y+1+row][x+deltaX+col] != null){
                                collision = true;
                            }
                        }
                    }
                }
                if(!collision){
                    y++;
                }

            } else {
                collision = true;
            }
            beginTime = System.currentTimeMillis();
        }
    }

    public void render(Graphics g){
        //draw the shape
        for (int row = 0; row < coords.length; row++) {
            for (int col = 0; col < coords[row].length; col++) {
                if (coords[row][col] != 0) {
                    g.setColor(color);
                    g.fillRect(col * BLOCK_SIZE + x * BLOCK_SIZE, row * BLOCK_SIZE + y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE );
                }
            }
        }
    }

    public void speedUp(){
        delayTimeForMovement = fast;
    }

    public void speedDown(){
        delayTimeForMovement = normal;
    }

    public void moveRight(){
        deltaX = 1;
    }

    public void moveLeft(){
        deltaX = -1;
    }

}