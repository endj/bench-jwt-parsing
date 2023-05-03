package bench;

public class Payload {
    private int sub;
    private String name;
    private int iat;

    public Payload(int sub, String name, int iat) {
        this.sub = sub;
        this.name = name;
        this.iat = iat;
    }

    public Payload() {
    }

    public int getSub() {
        return sub;
    }

    public String getName() {
        return name;
    }

    public int getIat() {
        return iat;
    }
}
