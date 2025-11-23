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
            checkLine();
            board.addScore(1);
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

    public void updateWithSpeed(int customSpeed) {
        if(collision) {
            // fill the color for board
            for(int row = 0; row < coords.length;row++){
                for(int col = 0 ; col < coords[0].length;col++){
                    if (coords[row][col] != 0){
                        board.getBoard()[y + row][x + col] = color;
                    }
                }
            }
            checkLine();
            board.addScore(1);
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

        // Use delayTimeForMovement which changes with speedUp/speedDown
        int currentDelay = (delayTimeForMovement == fast) ? fast : customSpeed;

        if(System.currentTimeMillis() - beginTime > currentDelay) {
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

    private void checkLine() {
        int bottomLine = board.getBoard().length - 1;
        int linesCleared = 0;

        for(int topLine = board.getBoard().length - 1; topLine > 0; topLine --) {
            int count = 0;
            for (int col = 0; col < board.getBoard()[0].length; col++) {
                if (board.getBoard()[topLine][col] != null) {
                    count++;
                }
                board.getBoard()[bottomLine][col] = board.getBoard()[topLine][col];
            }
            //check if line clear to add 10 score
            if (count == board.getBoard()[0].length) {
                board.addScore(10);
                linesCleared++;
            }
            if(count < board.getBoard() [0].length) {
                bottomLine--;
            }
        }

        // Notify board of lines cleared
        if (linesCleared > 0) {
            board.onLinesCleared(linesCleared);
        }
    }

    public void rotateShape() {
        int[][] rotatedShape = transposeMatrix(coords);
        reverseRows(rotatedShape);
        //check for right side and bottom
        if((x + rotatedShape[0].length > Board.BOARD_WIDTH) || (y + rotatedShape.length > 20)) {
            return;
        }

        // Check for collision with other shapes before retated
        for(int row = 0; row < rotatedShape.length; row++) {
            for(int col = 0; col < rotatedShape[row].length; col++) {
                if(rotatedShape[row][col] != 0) {
                    if(board.getBoard()[y + row][x + col] != null) {
                        return;
                    }
                }
            }
        }
        coords = rotatedShape;
    }

    private int[][] transposeMatrix(int[][] matrix) {
        int[][] temp = new int[matrix[0].length][matrix.length];
        for(int row = 0; row < matrix.length; row++) {
            for(int col = 0; col < matrix[0].length; col++) {
                temp[col][row] = matrix[row][col];
            }
        }
        return temp;
    }

    private void reverseRows(int[][] matrix) {
        int middle = matrix.length / 2;
        for(int row = 0; row < middle; row++) {
            int[] temp = matrix[row];
            matrix[row] = matrix[matrix.length - row - 1];
            matrix[matrix.length - row - 1] = temp;
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

    public int[][] getCoords() {
        return coords;
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

    public int getY() {return y; }

    public int getX() {return x; }

    public Color getColor() {
        return color;
    }

}