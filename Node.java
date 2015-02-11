import java.util.ArrayList;

public class Node
{
    Node parent; 
    ArrayList<Node> children; 
    boolean visited; 
    int x, y;

    public Node ( Node parent, int x, int y ) {
        this.parent = parent;
        this.children = new ArrayList<Node>(4);
        this.visited = false;
        setState(x,y);
    }

    public void setState ( int x, int y ) {
        this.x = x;
        this.y = y;
    }

    public void addChild ( Node child ) {
        this.children.add(child);
    }

    public void addChildren ( Node[] children ) {
        for ( Node n : children ) 
            this.children.add(n);
    }

    public ArrayList<Node> getChildren() {
        return this.children; 
    }

    @Override 
    public boolean equals ( Object other ) {
        Node n = (Node)other;
        return n.x == this.x && n.y == this.y;
    }

    public void generateChildren() {
        if ( x-1 > 0 )
            addChild(new Node(this,x-1,y));
        if ( x+1 < Board.WIDTH ) 
            addChild(new Node(this,x+1,y));
        if ( y-1 > 0 )
            addChild(new Node(this,x,y-1));
        if ( y+1 < Board.HEIGHT ) 
            addChild(new Node(this,x,y+1));
    }
}
