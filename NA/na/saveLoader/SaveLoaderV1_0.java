package saveLoader;

import java.io.BufferedReader;
import java.io.IOException;

import core.GHQ;
import engine.NAGame;
import paint.ImageFrame;
import physics.stage.GHQStage;
import preset.structure.Structure;
import preset.structure.Terrain;
import preset.structure.Tile;
import preset.unit.Unit;
import preset.vegetation.Vegetation;
import structure.NATile;
import structure.NATileEnum;
import unit.Boss_1;
import unit.HumanGuard;
import unit.HumanGuard2;
import unit.NAUnit;
import unit.Player;

public class SaveLoaderV1_0 extends SaveLoader {
	final StringBuilder sb = new StringBuilder();
	private StringBuilder appendLine(String str) {
		return sb.append(str).append("\r\n");
	}
	private StringBuilder appendLine(Object value) {
		return appendLine(value.toString());
	}
	@Override
	public String save() {
		//saveVersion output
		appendLine("SaveLoaderV1_0");
		//outputStages
		final GHQStage stage = GHQ.stage();
		//outputUnits
		checkControllingUnit: {
			for(Unit ver : stage.units) {
				if(ver instanceof Player) {
					appendLine(stage.units.size() - 1);
					break checkControllingUnit;
				}
			}
			appendLine(stage.units.size());
		}
		for(Unit ver : stage.units) {
			final NAUnit unit = (NAUnit)ver;
			if(unit instanceof Player)
				continue;
			appendLine(unit.getClass().getName());
			appendLine(unit.point().intX());
			appendLine(unit.point().intY());
		}
		//outputStructures
		appendLine(stage.structures.size());
		for(Structure ver : stage.structures) {
			appendLine(ver.getClass().getName());
			if(ver instanceof Tile) {
				final Tile tile = (Tile)ver;
				appendLine(tile.point().intX());
				appendLine(tile.point().intY());
				appendLine(tile.xTiles());
				appendLine(tile.yTiles());
			}else if(ver instanceof Terrain) {
				//TODO: terrain save process
			}
		}
		//outputVegetations
		appendLine(stage.vegetations.size());
		for(Vegetation ver : stage.vegetations) {
			appendLine(ver.getClass().getName());
			appendLine(((ImageFrame)ver.getDotPaint()).originURLStr());
			appendLine(ver.point().intX());
			appendLine(ver.point().intY());
		}
		return sb.toString();
	}
	private boolean nameMatch(String name, Class<?> cls) {
		return name.equals(cls.getName());
	}
	private int readIntLine(BufferedReader br) throws IOException {
		return Integer.valueOf(br.readLine());
	}
	@Override
	public GHQStage load(BufferedReader br) throws IOException {
		//skipVersionInfo
		//bufferedReader.readLine();
		//units
		final GHQStage stage = new GHQStage(NAGame.STAGE_W, NAGame.STAGE_H);
		int unitSize = Integer.valueOf(br.readLine());
		for(int i = 0;i < unitSize; ++i) {
			final String name = br.readLine();
			final int x = Integer.valueOf(br.readLine());
			final int y = Integer.valueOf(br.readLine());
			if(nameMatch(name, HumanGuard.class))
				stage.addUnit(Unit.initialSpawn(new HumanGuard(), x, y));
			else if(nameMatch(name, HumanGuard2.class))
				stage.addUnit(Unit.initialSpawn(new HumanGuard2(), x, y));
			else if(nameMatch(name, Boss_1.class))
				stage.addUnit(Unit.initialSpawn(new Boss_1(), x, y));
			else if(nameMatch(name, Player.class))
				stage.addUnit(Unit.initialSpawn(new Player(), x, y));
		}
		//structures
		int structureSize = Integer.valueOf(br.readLine());
		for(int i = 0;i < structureSize; ++i) {
			final String name = br.readLine();
			if(nameMatch(name, Tile.class)) {
				stage.addStructure(new NATile(NATileEnum.WOOD, readIntLine(br), readIntLine(br), readIntLine(br), readIntLine(br)));
			}else if(nameMatch(name, Terrain.class)) {
				//TODO: terrain load process
			}
		}
		//Vegetation
		int vegetationSize = Integer.valueOf(br.readLine());
		for(int i = 0;i < vegetationSize; ++i) {
			final String name = br.readLine();
			if(nameMatch(name, Vegetation.class)) {
				stage.addVegetation(new Vegetation(ImageFrame.create(br.readLine()), readIntLine(br), readIntLine(br)));
			}
		}
		return stage;
	}
}
