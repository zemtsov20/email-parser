package emailparser;

public record Node(String url, int depth) {

    public String getUrl() {
        return url;
    }

    public int getDepth() {
        return depth;
    }

}
