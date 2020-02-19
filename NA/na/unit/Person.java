package unit;

import paint.ImageFrame;

public interface Person {
	public abstract String personalName();
	public abstract ImageFrame personalIcon();
	
	public static final Person ASIDE = new Person() {
		@Override
		public String personalName() {
			return "(旁白)";
		}
		@Override
		public ImageFrame personalIcon() {
			return ImageFrame.create("picture/personalIcon/CHI_N1.png");
		}
	};
}
