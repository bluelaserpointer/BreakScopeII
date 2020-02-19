package talent;

import paint.ImageFrame;
import unit.NAUnit;

public class AllUp extends Talent {
	public AllUp(NAUnit owner) {
		super("AllUP", ImageFrame.create("picture/loadLine/crossed.png"), owner);
		doAllUp(5);
	}
	private void doAllUp(int amount) {
		((NAUnit)owner).POW_FIXED.setValue(((NAUnit)owner).POW_FIXED.doubleValue() + amount);
		((NAUnit)owner).AGI_FIXED.setValue(((NAUnit)owner).AGI_FIXED.doubleValue() + amount);
		((NAUnit)owner).INT_FIXED.setValue(((NAUnit)owner).INT_FIXED.doubleValue() + amount);
	}
	public void levelUp() {
		switch(++nowLevel) {
		case 2:
			doAllUp(1);
			break;
		case 3:
		case 4:
		case 5:
			doAllUp(2);
			break;
		default:
			nowLevel = 5;
			break;
		}
	};
	@Override
	public String description() {
		return "身体素质强化\n" +
				"通过对自己使用的消耗道具数量升级：10/20/40/80\n" +
				"使得主角的三项主要属性增加：5/6/8/10/12\n" +
				"注：该增幅为白字增幅\n" +
				"当异能升级为五级时，额外获得能力，使得三项主要资源（红绿蓝条）与韧性的自我恢复速度增加一倍\n" + 
				"身体素质强化\n" +
				"通过对自己使用的消耗道具数量升级：10/20/40/80\n" +
				"使得主角的三项主要属性增加：5/6/8/10/12\n" +
				"注：该增幅为白字增幅\n" +
				"当异能升级为五级时，额外获得能力，使得三项主要资源（红绿蓝条）与韧性的自我恢复速度增加一倍";
	}

}
