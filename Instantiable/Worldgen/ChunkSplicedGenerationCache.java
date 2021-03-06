/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Worldgen;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public class ChunkSplicedGenerationCache {

	private final HashMap<ChunkCoordIntPair, HashMap<Coordinate, BlockPlace>> data = new HashMap();

	public ChunkSplicedGenerationCache() {

	}

	public void setBlock(int x, int y, int z, Block b) {
		this.place(x, y, z, new SetBlock(b));
	}

	public void setBlock(int x, int y, int z, Block b, int meta) {
		this.place(x, y, z, new SetBlock(b, meta));
	}

	public void setTileEntity(int x, int y, int z, Block b, int meta, TileCallback call) {
		this.place(x, y, z, new TileSet(call, b, meta));
	}

	public void place(int x, int y, int z, BlockPlace sb) {
		ChunkCoordIntPair key = this.getKey(x, z);
		HashMap<Coordinate, BlockPlace> map = data.get(key);
		if (map == null) {
			map = new HashMap();
			data.put(key, map);
		}
		x = x%16;
		z = z%16;
		if (x < 0) {
			x += 16;
			if (x%16 == 0)
				x += 16;
		}
		if (z < 0) {
			z += 16;
			if (z%16 == 0)
				z += 16;
		}
		map.put(new Coordinate(x, y, z), sb);
	}

	public void generate(World world, int chunkX, int chunkZ) {
		this.generate(world, new ChunkCoordIntPair(chunkX, chunkZ));
	}

	public void generate(World world, ChunkCoordIntPair cp) {
		this.doGenerate(world, cp);
		data.remove(cp);
	}

	public void generateAll(World world) {
		for (ChunkCoordIntPair cp : data.keySet()) {
			this.doGenerate(world, cp);
		}
		data.clear();
	}

	private void doGenerate(World world, ChunkCoordIntPair cp) {
		HashMap<Coordinate, BlockPlace> map = data.get(cp);
		if (map != null) {
			//ReikaJavaLibrary.pConsole("To generate: "+map);
			for (Coordinate c : map.keySet()) {
				BlockPlace bp = map.get(c);
				int x = (cp.chunkXPos << 4)+c.xCoord;
				int y = c.yCoord;
				int z = (cp.chunkZPos << 4)+c.zCoord;
				bp.place(world, x, y, z);
			}
		}
	}

	public void clear() {
		data.clear();
	}

	@Override
	public String toString() {
		return data.toString();
	}

	private static ChunkCoordIntPair getKey(int x, int z) {
		return new ChunkCoordIntPair(x >> 4, z >> 4);
	}

	public static interface BlockPlace {

		public void place(World world, int x, int y, int z);

	}

	static class SetBlock implements BlockPlace {

		private final Block block;
		private final int metadata;

		SetBlock(Block b) {
			this(b, 0);
		}

		SetBlock(Block b, int m) {
			block = b;
			metadata = m;
		}

		@Override
		public void place(World world, int x, int y, int z) {
			world.setBlock(x, y, z, block, metadata, 3);
			if (block.getLightValue(world, x, y, z) > 0) {
				world.markBlockForUpdate(x, y, z);
				world.func_147479_m(x, y, z);
			}
		}

	}

	static class TileSet extends SetBlock {

		private final TileCallback callback;

		TileSet(TileCallback c, Block b, int m) {
			super(b, m);
			callback = c;
		}

		@Override
		public void place(World world, int x, int y, int z) {
			super.place(world, x, y, z);
			callback.onTilePlaced(world, x, y, z, world.getTileEntity(x, y, z));
		}

	}

	public static interface TileCallback {

		public void onTilePlaced(World world, int x, int y, int z, TileEntity te);

	}

	public static final class RelayCache extends ChunkSplicedGenerationCache {

		private final World world;

		public RelayCache(World world) {
			this.world = world;
		}

		@Override
		public void place(int x, int y, int z, BlockPlace sb) {
			sb.place(world, x, y, z);
		}

	}

}
