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
    
    public LinkedList<Node> bfSearch ( int x, int y ) {
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
            if ( CS.children == null ) 
                CS.generateChildren();
            //System.out.println(CS.children == null);

            // filter invalid children of CS
            for ( int i=0; i<CS.children.size(); i++ ) {
                Node child = CS.children.get(i);
                if ( DE.contains(child) 
                  || SL.contains(child)
                  || NSL.contains(child) ) {
                    // remove if it is in DE SL or NSL
                    CS.children.remove(i);
                } else if ( env.isObstacle(child.x, child.y) ) {
                    // remove if will collide with an obstacle
                    System.out.println(""+env.isObstacle(child.x, child.y)+child);
                    CS.children.remove(i);
                    DE.addLast(child);
                } else if ( this.isSelfCollision(child) ) {
                    // remove if will cause snake head to collide with body
                    CS.children.remove(i);
                    System.out.println("self collision detect");
                }
            }
            

            if ( CS.children.isEmpty() ) {
                while ( !SL.isEmpty() && CS.equals(SL.peek()) ) {
                    DE.addLast(CS);    
                    SL.pop();
                    CS = NSL.pop();
                    //CS = NSL.peek();

                }
                SL.push(CS);
            } else {
                for ( Node child : CS.children ) {
                    if ( !DE.contains(child) 
                      && !SL.contains(child)
                      && !NSL.contains(child) 
                      && isFeasible(CS, child) ) {
                        NSL.addLast(child);
                    }
                }
                CS = NSL.peek();
                SL.push(CS);
            }
        }
        return null;
    }

    public LinkedList<Node> dfSearch ( int x, int y ) {
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
            
            /*System.out.println("CS: "+CS.x+", "+CS.y);
            System.out.println("SL: "+SL);
            System.out.println("NSL: "+NSL);
            System.out.println("DE: "+DE);
            */
            //try { Thread.sleep(300); } catch ( Exception e ) { ;; }
            
            if ( CS.children == null ) 
                CS.generateChildren();
            //System.out.println(CS.children == null);
            
            // for case that game just started
            Node temp = CS;
            boolean new_graph = false;
            for ( int i=0; i<Board.SNAKE_LENGTH; i++ ) {
                if ( temp.parent == null ) {
                    new_graph = true;
                    break;
                }
                temp = temp.parent;
            }

            // filter invalid children of CS
            Node[] children = new Node[CS.children.size()];
            for ( int i=0; i<CS.children.size(); i++ ) 
                children[i] = CS.children.get(i);

            for ( int i=0; i<CS.children.size(); i++ ) { 
                Node child = CS.children.get(i);
                if ( DE.contains(child) 
                  || SL.contains(child)
                  || NSL.contains(child) ) {
                    // remove if it is in DE SL or NSL
                    children[i] = null;
                } else if ( env.isObstacle(child.x, child.y) ) {
                    // remove if will collide with an obstacle
                    System.out.println("obstacle found at "+child);
                    children[i] = null;
                    DE.push(child);
                } else if ( new_graph ) {
                    System.out.println("NEW GRAPH");
                    if ( env.isSnake(child.x, child.y) ) {
                        children[i] = null;
                        System.out.println("SELF COLLISION");
                    }
                } else if ( this.isSelfCollision(child) ) {
                    // remove if will cause snake head to collide with body
                    children[i] = null;
                    System.out.println("self collision detected");
                } 
            }
            
            CS.children = new ArrayList<Node>(4);
            for ( int i=0; i<children.length; i++ ) {
                if ( children[i] != null )
                    CS.children.add(children[i]);
            }

            if ( CS.children.isEmpty() ) {
                // backtrack
                while ( !SL.isEmpty() && CS.equals(SL.peek()) ) {
                    DE.push(CS);    
                    SL.pop();
                    CS = NSL.pop();
                    CS = NSL.peek();
                }
                SL.push(CS);
            } else {
                // add children to fringe
                for ( Node child : CS.children ) {
                    if ( !DE.contains(child) 
                      && !SL.contains(child)
                      && !NSL.contains(child) 
                      && isFeasible(CS, child) ) {
                        NSL.push(child);
                    }
                }
                CS = NSL.peek();
                SL.push(CS);
            }
        }
        return null;
    }

    private boolean isFeasible( Node CS, Node next ) {
        if ( next.x != CS.x && next.y != CS.y ) 
            return false;
        if ( CS.equals(next) ) 
            return false;
        else 
            return !isSelfCollision(next) 
                && !env.isObstacle(next.x, next.y);
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
