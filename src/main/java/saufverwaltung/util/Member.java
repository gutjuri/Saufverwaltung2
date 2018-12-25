package saufverwaltung.util;

public class Member {
    private String name;
    private int guthaben;
    private int alk;
    private int antalk;
    private boolean visible;

    public Member(String name, int guthaben, int alk, int antalk, boolean visible) {
        super();
        this.name = name;
        this.guthaben = guthaben;
        this.alk = alk;
        this.antalk = antalk;
        this.visible = visible;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGuthabenFormatted() {
        int euros = Math.abs(guthaben) / 100;
        int cents = Math.abs(guthaben) % 100;

        return (guthaben < 0 ? "-" : "") + euros + "," + (cents < 10 ? "0" : "") + cents;
    }

    public int getGuthaben() {
        return this.guthaben;
    }

    public void setGuthaben(int guthaben) {
        this.guthaben = guthaben;
    }

    public int getAlk() {
        return alk;
    }

    public void setAlk(int alk) {
        this.alk = alk;
    }

    public int getAntalk() {
        return antalk;
    }

    public void setAntalk(int antalk) {
        this.antalk = antalk;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void toggleVisible() {
        this.visible = !this.isVisible();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
