package unit;

public class Researcher extends NAUnit{

	public Researcher(int charaSize) {
		super(charaSize);
	}

	@Override
	public boolean interact(NAUnit unit) {
		return true;
	}
	@Override
	public UnitGroup unitGroup() {
		// TODO Auto-generated method stub
		return null;
	}
}
