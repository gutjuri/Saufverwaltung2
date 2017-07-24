

public class Member {
	private String name;
	private double guthaben;
	private int alk;
	private int antalk;
	private boolean visible;

	public Member(String name, double guthaben, int alk, int antalk, boolean visible) {
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

	public double getGuthaben() {
		return guthaben;
	}

	public void setGuthaben(double guthaben) {
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
}
