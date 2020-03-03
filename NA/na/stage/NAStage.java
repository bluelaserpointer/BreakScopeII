package stage;

public class NAStage extends GHQStage {
	
	public static final int SEEN_CELL_SIZE = 25;
	private final GridBitSet seenMark;
	private final GridBitSet enemySeenMark;
	private final GridBitSet playerSeenMark;
	
	public NAStage(int width, int height) {
		super(width, height);
		seenMark = new GridBitSet(this, SEEN_CELL_SIZE);
		enemySeenMark = new GridBitSet(this, SEEN_CELL_SIZE);
		playerSeenMark = new GridBitSet(this, SEEN_CELL_SIZE);
	}
	public static NAStage generate(GHQStage sample) {
		NAStage stage = new NAStage(sample.width(), sample.height());
		stage.units.addAll(sample.units);
		stage.bullets.addAll(sample.bullets);
		stage.units.addAll(sample.units);
		return stage;
	}
	
	//information
	public GridPainter gridPainter() {
		return seenMark;
	}
	public GridBitSet seenMark() {
		return seenMark;
	}
	public GridBitSet enemySeenMark() {
		return enemySeenMark;
	}
	public GridBitSet playerSeenMark() {
		return playerSeenMark;
	}
}
