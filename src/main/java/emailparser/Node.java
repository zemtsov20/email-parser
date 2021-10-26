package emailparser;

public class Node {
    private String url;
    private int depth;

    public Node(String url, int depth){
        this.url = url;
        this.depth = depth;
    }

    public String getUrl() {
        return url;
    }

    public int getDepth() {
        return depth;
    }

}
