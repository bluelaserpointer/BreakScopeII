package saveLoader;

import java.io.BufferedReader;
import java.io.IOException;

import stage.GHQStage;

public abstract class SaveLoader {
	public abstract String save();
	public abstract GHQStage load(BufferedReader bufferedReader) throws IOException;
}
