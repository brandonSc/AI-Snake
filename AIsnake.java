import java.awt.EventQueue;
import javax.swing.JFrame;


public class AIsnake extends JFrame
{
    private Board board;

    public AIsnake() {
        this.board = new Board(true);
        add(board);

        setResizable(false);
        pack();

        setTitle("AIsnake");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        System.out.println(board.isObstacle(3,3));


    }

    public void randomize(){
        while ( true ) {
            try { Thread.sleep(500); } catch ( Exception e ) { ;; }
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
            board.move(direction);
        }
    }


    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {                
                AIsnake ex = new AIsnake();
                ex.setVisible(true);
                //ex.randomize();
            }
        });
    }
}

