import java.util.ArrayList;

public class Node
{
    Node parent; 
    ArrayList<Node> children; 
    boolean visited; 
    int x, y;

    public Node ( Node parent, int x, int y ) {
        this.parent = parent;
        this.children = null;
        this.visited = false;
        setState(x,y);
    }

    public void setState ( int x, int y ) {
        this.x = x;
        this.y = y;
    }

    @Override 
    public boolean equals ( Object other ) {
        if ( other == null ) return false;
        Node n = (Node)other;
        return (n.x == this.x && n.y == this.y);
    }

    @Override 
    public int hashCode() { 
        return -100*this.x+20*this.y;
    }

    public void generateChildren() {
        this.children = new ArrayList<Node>(4);

        if ( x-1 >= 0 )
            children.add(new Node(this,x-1,y));
        if ( y-1 >= 0 )
            children.add(new Node(this,x,y-1));
        if ( x+1 < Board.WIDTH ) 
            children.add(new Node(this,x+1,y));
        if ( y+1 < Board.HEIGHT ) 
            children.add(new Node(this,x,y+1));
    }

    @Override
    public String toString() {
        return "("+x+","+y+")";
    }
}

