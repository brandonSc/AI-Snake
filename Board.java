import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.util.LinkedList;

public class Board extends JPanel implements ActionListener 
{
    public final static int WIDTH_PX = 400; 
    public final static int HEIGHT_PX = 400;     
    public final static int CELL_SIZE = 10;   
    public final static int WIDTH = 
        (int)(WIDTH_PX/CELL_SIZE);
    public final static int HEIGHT = 
        (int)(HEIGHT_PX/CELL_SIZE);
    public final static int DELAY = 50;       // frequency of frame update
    public final static int SNAKE_LENGTH = 5; // length of snake
    public final static int NUM_OBSTACLES = 3;// #of impassable blocks

    private final int x[] = new int[SNAKE_LENGTH];
    private final int y[] = new int[SNAKE_LENGTH];

    private int[] obstacles_x;
    private int[] obstacles_y;
    private int food_x;
    private int food_y;
    private int score;

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;
    private boolean ai_controlled = false;

    private Timer timer;
    private Image body;
    private Image food;
    private Image head;
    private Image obstacle;
    private Font smallFont;
    private Font largeFont;

    private LinkedList<Node> solution;

    public Board ( boolean isAI ) {
        this.ai_controlled = isAI;
        addKeyListener(new TAdapter());
        setBackground(Color.LIGHT_GRAY);
        setFocusable(true);

        this.obstacles_x = new int[NUM_OBSTACLES];
        this.obstacles_y = new int[NUM_OBSTACLES];

        setPreferredSize(new Dimension(WIDTH_PX, HEIGHT_PX));
        loadImages();
        loadFonts();
        initGame();
    }
    
    public void randomize(){

        String direction = "";
        double r = Math.random();
        if ( r < 0.25 ) {
            direction = "up";
        } else if ( r < 0.5 ) {
            direction = "down";
        } else if ( r < 0.75 ) { 
            direction = "right";
        } else { 
            direction = "left";
        }
        move(direction);
    }

    public void turnRight(){
        if ( !leftDirection ) {
            rightDirection = true;
            upDirection = false;
            downDirection = false;
        }
    }
    public void turnLeft(){
        if ( !rightDirection ) {
            leftDirection = true;
            upDirection = false;
            downDirection = false;
        } 
    }
    public void turnUp(){
        if ( !downDirection ) {
            upDirection = true;
            rightDirection = false;
            leftDirection = false;
        }
    }
    public void turnDown(){
        if ( !upDirection ) {
            downDirection = true;
            rightDirection = false;
            leftDirection = false;
        }
    }

    /*
    public int getHeadX(){
        return (int)(WIDTH_PX/CELL_SIZE);
    }

    public int getHeadY(){
        return (int)(HEIGHT_PX/CELL_SIZE);
    }

    public int getFoodX(){
        return (int)(food_x/CELL_SIZE);
    }

    public int getFoodY(){
        return (int)(food_y/CELL_SIZE);
    }

    public int[] getObstacesX(){
        int[] a = new int[obstacles_x.length];
        for ( int i=0; i<obstacles_x.length; i++ ) {
            a[i] = (int)(obstacles_x[i]/CELL_SIZE);
        }
        return a;
    }

    public int[] getObstacesY(){    
        int[] a = new int[obstacles_y.length];
        for ( int i=0; i<obstacles_y.length; i++ ) {
            a[i] = (int)(obstacles_y[i]/CELL_SIZE);
        }
        return a;
    }
    */

    public boolean isCollisoin ( int x, int y ) {
        return isObstacle(x,y) || isSnake(x,y);
    }

    public boolean isFood ( int x, int y ) {
        return ( x == (int)(food_x/CELL_SIZE) &&
                 y == (int)(food_y/CELL_SIZE));
    }

    public boolean isObstacle ( int x, int y ) {
        for ( int i=0; i<NUM_OBSTACLES; i++ ) {
            if ( x == (int)(obstacles_x[i]/CELL_SIZE)
              && y == (int)(obstacles_y[i]/CELL_SIZE) ) {
                return true;
            } 
        }
        return false;
    }

    public boolean isSnake ( int x, int y ) { 
        for ( int i=1; i<SNAKE_LENGTH; i++ ) {
            if ( x == (int)(this.x[i]/CELL_SIZE)
              && y == (int)(this.y[i]/CELL_SIZE) ) {
                return true;
            }
        }
        return false;
    }

    private void loadImages(){
        ImageIcon iid = new ImageIcon("body.png");
        body = iid.getImage();

        ImageIcon iia = new ImageIcon("food.png");
        food = iia.getImage();

        ImageIcon iih = new ImageIcon("head.png");
        head = iih.getImage();

        ImageIcon iio = new ImageIcon("obstacle.png");
        obstacle = iio.getImage();
    }

    private void loadFonts(){
        largeFont = new Font("Monospaced", Font.BOLD, 16);
        smallFont = new Font("Monospaced", Font.BOLD, 12);
    }

    private void initGame(){
        score = 0;
        for( int i=0; i<SNAKE_LENGTH; i++ ){
            x[i] = 50 - i * CELL_SIZE;
            y[i] = 50;
        }

        placeFood();
        placeObstacles();

        if ( !ai_controlled ) { 
            timer = new Timer(DELAY, this);
            timer.start();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        if( inGame ){
            g.drawImage(food, food_x, food_y, this);

            for ( int i=0; i<NUM_OBSTACLES; i++ ) {
                g.drawImage(
                        obstacle, 
                        obstacles_x[i], 
                        obstacles_y[i],
                        this);
            }

            for (int i=0; i<SNAKE_LENGTH; i++ ){
                if( i == 0 ){
                    g.drawImage(head, x[i], y[i], this);
                } else {
                    g.drawImage(body, x[i], y[i], this);
                }
            }
            Toolkit.getDefaultToolkit().sync();
        } else {
            gameOver(g);
        }
        drawHUD(g);
    }

    private void gameOver(Graphics g) {

            String msg = "Game Over";
            FontMetrics metr = getFontMetrics(largeFont);

            g.setColor(Color.BLACK);
            g.setFont(largeFont);
            g.drawString(msg, (WIDTH_PX - metr.stringWidth(msg)) / 2, HEIGHT_PX / 2);
        }

        private void checkFood(){
            // check if snake head is at same location as food
            if( (x[0] == food_x) && (y[0] == food_y) ){
                // re locate the food and obstacles
                placeFood();
                placeObstacles();
                score++;
            }
        }

        private void checkObstacles(){
            for ( int i=0; i<NUM_OBSTACLES; i++ ) {
                if ( (x[0] == obstacles_x[i]) 
                  && (y[0] == obstacles_y[i]) ) {
                    inGame = false;
                }
            }
        }

        private void move(){
            for( int i=SNAKE_LENGTH-1; i>0; --i ){
                x[i] = x[(i-1)];
                y[i] = y[(i-1)];
            }

            if( leftDirection ){
                x[0] -= CELL_SIZE;
            }

            if( rightDirection ){
                x[0] += CELL_SIZE;
            }

            if( upDirection ){
                y[0] -= CELL_SIZE;
            }

            if( downDirection ){
                y[0] += CELL_SIZE;
            }
        }

        private void checkCollision(){
            for( int i=SNAKE_LENGTH-1; i>0; --i ){
                // check case that snake has collided with itself
                if( (i > 1) && (x[0] == x[i]) && (y[0] == y[i]) ){
                    inGame = false;
                }
            }

            // check for collision with wall
            if( y[0] >= HEIGHT_PX ){
                inGame = false;
            }

            if( y[0] < 0 ){
                inGame = false;
            }

            if( x[0] >= WIDTH_PX ){
                inGame = false;
            }

            if( x[0] < 0 ){
                inGame = false;
            }

            if( !inGame /*&& !ai_controlled*/ ){
                timer.stop();
            }
        }

        private void drawHUD( Graphics g ){
            String msg = "Score: [ "+score+" ]";
            FontMetrics metr = getFontMetrics(smallFont);
            g.setColor(Color.BLACK);
            g.setFont(smallFont);
            g.drawString(msg, 
                    (WIDTH_PX - metr.stringWidth(msg)) 
                    / 2, HEIGHT_PX-30);
        }

        /**
         * place NUM_OBSTACLES randomly,
         * but not on the food!
         */
        private void placeObstacles(){
            for ( int i=0; i<NUM_OBSTACLES; i++ ) {
                int r = food_x;
                while ( r == food_x ) {
                    r = (int)(Math.random()
                            *(((WIDTH_PX-CELL_SIZE)/CELL_SIZE)+1));
                    obstacles_x[i] = r*CELL_SIZE;
                }
                r = food_y;
                while ( r == food_y ) { 
                    r = (int)(Math.random()
                            *(((WIDTH_PX-CELL_SIZE)/CELL_SIZE)+1));
                    obstacles_y[i] = r*CELL_SIZE;
                }
            }
        }

        /**
         * Place a piece of food randomly
         * somewhere within the area of the board
         */
        private void placeFood(){
            int r = (int)(Math.random()
                    *(((WIDTH_PX-CELL_SIZE)/CELL_SIZE)+1));
            food_x = r*CELL_SIZE;

            r = (int) (Math.random()
                    *(((HEIGHT_PX-CELL_SIZE)/CELL_SIZE)+1));
            food_y = r*CELL_SIZE;

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if ( inGame ) {
                if ( solution == null ) {
                    Search search = new Search(this);
                    solution = search.backtrackSearch(x[0], y[0]);
                    solution.removeLast(); // same as head x,y
                }

                Node n = solution.removeLast();
                String nextDir = directionDifference(n.x, n.y);
                move(nextDir);

                checkFood();
                checkObstacles();
                checkCollision();
                move();
            }

        repaint();
    }

    public String directionDifference ( int x, int y ) {
        String str = "err";
        
        if ( this.x[0] == x && this.y[0] == y-1 )
            return "down";
        else if ( this.x[0] == x-1 && this.y[0] == y ) 
            return "right";
        else if ( this.x[0] == x && this.y[0] == y+1 ) 
            return "up";
        else if ( this.x[0] == x+1 && this.y[0] == y )
            return "left";
        else if ( this.x[0] == x && this.y[0] == y ) 
            return "same";
        return str;
    }

    public void move ( String direction ) { 
        if ( direction.equals("up") ) { 
            turnUp();
        } else if ( direction.equals("down") ) {
            turnDown();
        } else if ( direction.equals("right") ) {
            turnRight();
        } else if ( direction.equals("left") ) {
            turnLeft();
        } else {
            System.err.println("unrecognized direction: "
                + "'"+direction+"' @Board.move(String)");
            System.exit(-1);
        }
    }

    private class TAdapter extends KeyAdapter {
        private long timeOfLastEvent = 0;

        @Override
        public void keyPressed(KeyEvent e) {

            long currTime = System.currentTimeMillis();

            if( currTime - timeOfLastEvent < 10 )
                return;

            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
            
            timeOfLastEvent = System.currentTimeMillis();
        }
    }
}
