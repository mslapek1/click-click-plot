package model.plot;

public class Aes {

    private Integer x;
    private Integer y;
    private Integer size;

    public Aes(Integer x, Integer y, Integer size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }


    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public Integer getSize() { return size; }
}
