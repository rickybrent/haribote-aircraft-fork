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
package mod.ymt.air.op;

import java.util.HashSet;
import java.util.Set;
import mod.ymt.air.AirCraftCore;
import mod.ymt.air.BlockData;
import mod.ymt.air.Materializer;
import mod.ymt.air.Operator;
import mod.ymt.air.ScanTime;
import mod.ymt.cmn.Coord3D;
import mod.ymt.cmn.Utils;
import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.MaterialLogic;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.RenderEngine;
import net.minecraft.src.RenderManager;
import net.minecraft.src.Tessellator;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

/**
 * @author Yamato
 *
 */
public abstract class AbstractOperator implements Operator {

	@Override
	public boolean hasSpecialRender() {
		return false;
	}

	@Override
	public boolean putBlocksToWorld(Materializer owner, ScanTime time, BlockData data, Coord3D target, int rotate) {
		if (time == getScanTime(data.block.blockID)) {
			setRealBlock(owner, data, target, rotate);
			return true;
		}
		return false;
	}

	@Override
	public void register(AirCraftCore core) {
		Set<Integer> moveable = new HashSet<Integer>();
		addMoveableBlockIds(moveable);
		for (int blockId: moveable) {
			core.setBlockOperator(blockId, this);
		}
	}

	@Override
	public boolean removeBlockFromWorld(Materializer owner, ScanTime time, Coord3D pos, Coord3D base) {
		int id = owner.world.getBlockId(pos.x, pos.y, pos.z);
		if (time == getScanTime(id)) {
			// 削除
			pickImitationBlock(owner, id, owner.world.getBlockMetadata(pos.x, pos.y, pos.z), pos, base);
			return true;
		}
		return false;
	}

	@Override
	public void renderBlock(RenderBlocks render, BlockData data) {
		Coord3D absPos = data.absPos;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		tessellator.setTranslation(-absPos.x - 0.5, -absPos.y, -absPos.z - 0.5);
		render.renderBlockByRenderType(data.block, absPos.x, absPos.y, absPos.z);
		tessellator.setTranslation(0, 0, 0);
		tessellator.draw();
	}

	@Override
	public void renderBlockSpecial(RenderManager manager, RenderBlocks render, BlockData data) {
		;
	}

	protected abstract void addMoveableBlockIds(Set<Integer> result);

	protected boolean canPlaceBlockAt(World world, int x, int y, int z, int blockId, int metadata) {
		return true;
	}

	protected void dropBlockAsItem(World world, Coord3D target, int blockId, int metadata) {
		if (Utils.isServerSide(world)) {
			Block block = Utils.getBlock(blockId);
			if (block != null) {
				block.dropBlockAsItem(world, target.x, target.y, target.z, metadata, 0);
			}
		}
	}

	protected ScanTime getScanTime(int blockId) {
		return ScanTime.Normal;
	}

	protected boolean isReplaceable(int oldBlockId, int newBlockId) {
		if (oldBlockId == Block.bedrock.blockID) // 受け側が bedrock は再配置不可能
			return false;
		Block oldBlock = Utils.getBlock(oldBlockId);
		if (oldBlock == null || oldBlock.blockMaterial.isReplaceable()) // 受け側が null または Replaceable なら再配置可能
			return true;
		Block newBlock = Utils.getBlock(newBlockId);
		if (newBlock == null || newBlock.blockMaterial.isReplaceable()) // 置く側が null または Replaceable なら再配置不可能
			return false;
		if (newBlock.blockMaterial instanceof MaterialLogic) // 置く側が回路ならば再配置不可能
			return false;
		return true;
	}

	protected void onCancelSetRealBlock(Materializer owner, BlockData data, Coord3D target) {
		// 置こうとしているブロックをアイテム化
		dropBlockAsItem(owner.world, target, data.block.blockID, data.metadata);
	}

	protected boolean pickImitationBlock(Materializer owner, int blockId, int metadata, Coord3D pos, Coord3D base) {
		Block block = Utils.getBlock(blockId);
		if (block == null) {
			return false;
		}
		// 消去する前にタイルエンティティ回収
		NBTTagCompound tileData = block instanceof BlockContainer ? readFromTileEntity(owner, blockId, metadata, pos) : null;
		// 消去
		setRealBlock(owner, 0, 0, pos.x, pos.y, pos.z);
		// 追加
		owner.setImitationBlock(new BlockData(block, metadata, pos.subtract(base), pos));
		if (tileData != null) {
			owner.setImitationTileEntity(tileData);
		}
		return true;
	}

	protected NBTTagCompound readFromTileEntity(Materializer owner, int blockId, int metadata, Coord3D pos) {
		World world = owner.world;
		TileEntity tile = world.getBlockTileEntity(pos.x, pos.y, pos.z);
		if (tile != null) {
			NBTTagCompound tag = new NBTTagCompound("value");
			tile.writeToNBT(tag);
			return tag;
		}
		return null;
	}

	protected boolean setRealBlock(Materializer owner, BlockData data, Coord3D target, int rotate) {
		World world = owner.world;
		int x = target.x;
		int y = target.y;
		int z = target.z;
		int oldBlockId = world.getBlockId(x, y, z);
		if (!isReplaceable(oldBlockId, data.block.blockID)) { // 配置できる？
			// キャンセル
			onCancelSetRealBlock(owner, data, target);
			// 置かなかったので false
			return false;
		}
		// 今あるブロックをアイテム化
		dropBlockAsItem(world, target, oldBlockId, world.getBlockMetadata(x, y, z));
		// ブロック配置
		if (!setRealBlockWithRotation(owner, data.block.blockID, data.metadata, x, y, z, rotate)) { // 置けた？
			// キャンセル
			onCancelSetRealBlock(owner, data, target);
			// 置けなかったので false
			return false;
		}
		// タイルエンティティ転写
		writeToTileEntity(owner, data, target, rotate);
		// 置いたら true
		return true;
	}

	protected boolean setRealBlock(Materializer owner, int blockId, int metadata, int x, int y, int z) {
		if (canPlaceBlockAt(owner.world, x, y, z, blockId, metadata)) {
			return owner.setRealBlock(x, y, z, blockId, metadata);
		}
		return false;
	}

	protected abstract boolean setRealBlockWithRotation(Materializer owner, int blockId, int metadata, int x, int y, int z, int rotate);

	protected void writeToTileEntity(Materializer owner, BlockData data, Coord3D target, int rotate) {
		if (data.block instanceof BlockContainer) {
			NBTTagCompound tag = owner.getImitationTileEntity(data.absPos);
			if (tag == null) {
				AirCraftCore.getInstance().debugPrint("writeToTileEntity not found TileEntity at ImitationSpace %s", target);
				return;
			}
			String id = tag.getString("id");
			// NBTTagCompound 転写
			TileEntity realTile = owner.world.getBlockTileEntity(target.x, target.y, target.z);
			if (realTile == null) {
				AirCraftCore.getInstance().debugPrint("writeToTileEntity not found TileEntity at %s", target);
				return;
			}
			NBTTagCompound tag2 = new NBTTagCompound();
			realTile.writeToNBT(tag2);
			String id2 = tag2.getString("id");
			if (!id.equals(id2)) {
				AirCraftCore.getInstance().debugPrint("writeToTileEntity unmatch TileEntity at %s (%s - %s)", target, id, id2);
				return;
			}
			tag.setInteger("x", target.x);
			tag.setInteger("y", target.y);
			tag.setInteger("z", target.z);
			realTile.readFromNBT(tag); // 転写
		}
	}

	protected static void loadTexture(RenderManager manager, String path) {
		RenderEngine engine = manager.renderEngine;
		engine.bindTexture(path);
	}
}
