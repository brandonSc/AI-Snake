import java.util.LinkedList;
import java.util.ArrayList;

public class Search
{
    private LinkedList<Node> closed;
    private LinkedList<Node> solution;
    private Board env;

    public Search ( Board env ) {
        this.closed = new LinkedList<Node>();
        this.solution = new LinkedList<Node>();
        this.env = env;
    }

    public boolean search ( Node start ) { 
        Node S = start;
        if ( closed.contains(S) ) 
            return false;
        if ( env.isFood(S.x, S.y) ) 
            return true;
       // while (  
       //
       return false;
    }

    public LinkedList<Node> backtrackSearch ( int x, int y ) {
        LinkedList<Node> SL  = new LinkedList<Node>();
        LinkedList<Node> NSL = new LinkedList<Node>();
        LinkedList<Node> DE  = new LinkedList<Node>();

        Node CS = new Node(null, x, y);
        SL.push(CS);
        NSL.push(CS);

        while ( !NSL.isEmpty() ) {
            // check if goal is reached
            if ( env.isFood(CS.x, CS.y) ) 
                return SL;
            
            //System.out.println(CS.x+", "+CS.y);
            CS.generateChildren();
            ArrayList<Node> children = CS.children;

            // filter invalid children of CS
            for ( int i=0; i<children.size(); i++ ) {
                Node child = children.get(i);
                /*if ( DE.contains(child) 
                  || SL.contains(child)
                  || NSL.contains(child) ) {
                    // remove if it is in DE SL or NSL
                    children.remove(i);
                } else*/ if ( env.isObstacle(child.x, child.y) ) {
                    // remove if will collide with an obstacle
                    children.remove(i);
                } else if ( this.isSelfCollision(child) ) {
                    // remove if will cause snake head to collide with body
                    children.remove(i);
                }
            }

            if ( children.isEmpty() ) {
                while ( !SL.isEmpty() && CS.equals(SL.peek()) ) {
                    DE.push(CS);    
                    SL.pop();
                    NSL.pop();
                    CS = NSL.peek();
                }
                SL.push(CS);
            } else {
                for ( Node child : children ) {
                    if ( !DE.contains(child) 
                      && !SL.contains(child)
                      && !NSL.contains(child) ) {
                        NSL.push(child);
                    }
                }
                CS = NSL.peek();
                SL.push(CS);
            }
        }
        return null;
    }

    private boolean isSelfCollision ( Node CS ) { 
        Node n = CS.parent;
        for ( int i=1; (i<Board.SNAKE_LENGTH && n != null); i++ ) { 
            if ( n.x == CS.x && n.y == CS.y )
                return true;
            n = n.parent;
        }
        return false;
    }
}
