/**
 * Copyright 2013 Yamato
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mod.ymt.air;

import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import mod.ymt.cmn.Coord3D;
import mod.ymt.cmn.Utils;
import net.minecraft.src.Block;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;

/**
 * @author Yamato
 *
 */
public class Materializer {
	private final AirCraftCore core = AirCraftCore.getInstance();

	private final int blocklimit;

	public final World world;
	public final ImitationSpace space;
	private final Set<Integer> moveable;

	public Materializer(World world, ImitationSpace space, int blocklimit, Set<Integer> moveable) {
		this.world = world;
		this.blocklimit = blocklimit;
		this.space = space;
		this.moveable = moveable;
	}

	public void addCoreBlock(int x, int y, int z, int blockId, int metadata) {
		BlockData data = BlockData.valueOf(blockId, metadata, Coord3D.ZERO, new Coord3D(x, y, z));
		if (data != null) { // ありえないはずだけど
			space.setBlockData(data);
		}
	}

	public NBTTagCompound getImitationTileEntity(Coord3D absPos) {
		return space.getTileEntityData(absPos);
	}

	public boolean putBlocks(int x, int y, int z, int rotate) {
		// y 座標の下から配置していく
		List<BlockData> allBlocks = new LinkedList<BlockData>(space.getAllBlocks());
		Collections.sort(allBlocks, new BlockDataBottomUpComparator());

		// 一番上と一番下が、ワールド内に収まっていることを確認する
		if (!allBlocks.isEmpty()) {
			int minRelY = Integer.MAX_VALUE, maxRelY = Integer.MIN_VALUE;
			for (BlockData data: allBlocks) {
				if (data.relPos.y < minRelY)
					minRelY = data.relPos.y;
				if (maxRelY < data.relPos.y)
					maxRelY = data.relPos.y;
			}
			if (y + minRelY < 0) { // 最下部が y < 0
				return false;
			}
			if (world.getHeight() <= y + maxRelY + 1) { // 最上部が MaxHeight <= y
				return false;
			}
		}

		// 順に配置
		processPutBlocks(ScanTime.Normal, allBlocks, x, y, z, rotate);
		processPutBlocks(ScanTime.Delicate, allBlocks, x, y, z, rotate);
		processPutBlocks(ScanTime.RedstoneWire, allBlocks, x, y, z, rotate);
		processPutBlocks(ScanTime.RedstoneOutput, allBlocks, x, y, z, rotate);

		return true;
	}

	public boolean removeBlocks(int x, int y, int z) {
		core.debugPrint("remove start!");

		// 探索
		Coord3D base = new Coord3D(x, y, z);
		List<Coord3D> allPoints = new LinkedList<Coord3D>(traceBlock(base)); // 絶対座標系
		core.debugPrint("get %s blocks traced", allPoints.size());

		// y 座標の上から削除していく
		Collections.sort(allPoints, new Coord3DTopDownComparator());

		// 引っぺがす
		processRemoveBlocks(ScanTime.RedstoneOutput, allPoints, base);
		processRemoveBlocks(ScanTime.RedstoneWire, allPoints, base);
		processRemoveBlocks(ScanTime.Delicate, allPoints, base);
		processRemoveBlocks(ScanTime.Normal, allPoints, base);

		core.debugPrint("remove %s blocks", space.countAllBlocks());

		// 表面計算
		space.updateServerSurface();
		core.debugPrint("surface %s blocks", space.countSurfaceBlocks());

		return true;
	}

	public void setImitationBlock(BlockData block) {
		space.setBlockData(block);
	}

	public void setImitationTileEntity(NBTTagCompound tag) {
		space.setTileEntityData(tag);
	}

	public boolean setRealBlock(int x, int y, int z, int blockId, int metadata) {
		if (Utils.isServerSide(world)) {
			world.setBlock(x, y, z, blockId, metadata, 2);
			return true;
		}
		return false;
	}

	private boolean isSurface(World world, Set<Coord3D> all, Coord3D pos) {
		for (Coord3D np: pos.getNeighbor()) {
			if (!all.contains(np)) {
				return true;
			}
			Block block = Utils.getBlock(world.getBlockId(np.x, np.y, np.z));
			if (block == null || !block.isOpaqueCube()) {
				return true;
			}
		}
		return false;
	}

	private void processPutBlocks(ScanTime time, List<BlockData> allBlocks, int x, int y, int z, int rotate) {
		Iterator<BlockData> iter = allBlocks.iterator();
		while (iter.hasNext()) {
			BlockData data = iter.next();
			Operator op = core.getBlockOperator(data.block.blockID);
			Coord3D pos = data.relPos.rotate(rotate).move(x, y, z);
			// 配置
			if (op.putBlocksToWorld(this, time, data, pos, rotate)) {
				// 配置したら allBlocks から除外
				iter.remove();
			}
		}
	}

	private void processRemoveBlocks(ScanTime time, List<Coord3D> allPoints, Coord3D base) {
		Iterator<Coord3D> iter = allPoints.iterator();
		while (iter.hasNext()) {
			Coord3D pos = iter.next();
			int blockId = world.getBlockId(pos.x, pos.y, pos.z);
			if (0 < blockId) {
				// 削除
				if (core.getBlockOperator(blockId).removeBlockFromWorld(this, time, pos, base)) {
					// 削除したら allPoints から除外
					iter.remove();
				}
			}
		}
	}

	protected boolean isMoveable(int blockId) {
		if (blockId == Block.bedrock.blockID)
			return false;
		return moveable.contains(blockId);
	}

	protected Set<Coord3D> traceBlock(Coord3D base) {
		int surfaceCount = 1; // コアブロックがすでに1個追加されてるはずなので1スタート
		Set<Coord3D> result = new HashSet<Coord3D>();
		Deque<Coord3D> nextPos = new LinkedList<Coord3D>(Arrays.asList(base.getNeighbor()));
		// 探索
		for (Coord3D pos = nextPos.poll(); pos != null && surfaceCount < blocklimit; pos = nextPos.poll()) {
			surfaceCount += traceBlock(result, pos, nextPos);
		}
		return result;
	}

	protected int traceBlock(Set<Coord3D> allblock, Coord3D pos, Deque<Coord3D> nextPos) {
		if (pos.y < 0) {
			return 0;
		}
		int blockId = world.getBlockId(pos.x, pos.y, pos.z);
		if (blockId == 0 || !isMoveable(blockId)) {
			return 0;
		}
		if (!allblock.add(pos)) { // 追加
			return 0;
		}
		int ret = 1; // 表面ブロック数
		for (Coord3D np: pos.getNeighbor()) {
			if (allblock.contains(np)) {
				// 既に探索済みならば表面計算
				if (!isSurface(world, allblock, np)) {
					ret--; // 表面でないならば表面ブロック数を -1
				}
			}
			else {
				// 未探索ならば次回追加
				nextPos.addLast(np);
			}
		}
		return ret;
	}
}
