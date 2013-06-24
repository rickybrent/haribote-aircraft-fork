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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import mod.ymt.cmn.Coord3D;
import mod.ymt.cmn.Utils;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.Block;
import net.minecraft.src.BlockHalfSlab;
import net.minecraft.src.BlockStairs;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Material;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.Vec3Pool;
import net.minecraft.src.World;

/**
 * @author Yamato
 *
 */
public class ImitationSpace implements IBlockAccess {
	private final World parent;
	
	/**
	 * 元座標から BlockData へのマップ
	 */
	private final Map<Coord3D, BlockData> allBlocks = new HashMap<Coord3D, BlockData>();
	/**
	 * 元座標から TileEntityData へのマップ
	 */
	private final Map<Coord3D, NBTTagCompound> allTileData = new HashMap<Coord3D, NBTTagCompound>();
	/**
	 * 表面にあるブロック
	 */
	private final List<BlockData> surfaceBlocks = new ArrayList<BlockData>();
	/**
	 * 準表面にあるブロック
	 */
	private final List<BlockData> semiSurfaceBlocks = new ArrayList<BlockData>();
	/**
	 * カスタムレンダリングするブロック
	 */
	private final List<BlockData> customRenderBlocks = new ArrayList<BlockData>();
	
	public ImitationSpace(World parent) {
		this.parent = parent;
	}
	
	public void addClientNonSurfaceBlocks(Collection<BlockData> blocks) {
		if (blocks.isEmpty()) {
			AirCraftCore.getInstance().debugPrint("ImitationSpace#addClientNonSurfaceBlocks empty data");
		}
		else {
			for (BlockData data: blocks) { // AllBlocks にのみ追加する
				setBlockData(data);
			}
		}
	}
	
	public void addClientSurfaceBlocks(Collection<BlockData> surfaces) {
		if (surfaces.isEmpty()) {
			AirCraftCore.getInstance().debugPrint("ImitationSpace#addClientSurfaceBlocks empty data");
		}
		else {
			for (BlockData data: surfaces) { // AllBlocks にも追加しておく
				setBlockData(data);
			}
			appendClientSurface(surfaces); // 表面を差し替え
		}
	}
	
	public void clear() {
		this.allBlocks.clear();
		this.surfaceBlocks.clear();
		this.semiSurfaceBlocks.clear();
		this.customRenderBlocks.clear();
	}
	
	public int countAllBlocks() {
		return allBlocks.size();
	}
	
	public int countSurfaceBlocks() {
		return surfaceBlocks.size();
	}
	
	@Override
	public boolean doesBlockHaveSolidTopSurface(int x, int y, int z) { // RenderBlocks で使用
		Block block = Utils.getBlock(getBlockId(x, y, z));
		if (block != null) {
			if (block.blockMaterial.isOpaque() && block.renderAsNormalBlock())
				return true;
			if (block instanceof BlockStairs)
				return (getBlockMetadata(x, y, z) & 4) == 4;
			if (block instanceof BlockHalfSlab)
				return (getBlockMetadata(x, y, z) & 8) == 8;
		}
		return false;
	}
	
	@Override
	public boolean extendedLevelsInChunkCache() { // 未使用
		throw new UnsupportedOperationException("method not implemented");
	}
	
	public Collection<BlockData> getAllBlocks() {
		return allBlocks.values();
	}
	
	public List<NBTTagCompound> getAllTileEntities() {
		return new ArrayList<NBTTagCompound>(allTileData.values());
	}
	
	@Override
	public BiomeGenBase getBiomeGenForCoords(int x, int z) { // RenderBlocks で使用
		return BiomeGenBase.plains;
	}
	
	@Override
	public int getBlockId(int x, int y, int z) { // RenderBlocks で使用
		BlockData data = getBlockData(x, y, z);
		if (data != null && data.block != null) {
			return data.block.blockID;
		}
		return 0;
	}
	
	@Override
	public Material getBlockMaterial(int x, int y, int z) { // RenderBlocks で使用
		BlockData data = getBlockData(x, y, z);
		if (data != null && data.block != null) {
			return data.block.blockMaterial;
		}
		return Material.air;
	}
	
	@Override
	public int getBlockMetadata(int x, int y, int z) { // RenderBlocks で使用
		BlockData data = getBlockData(x, y, z);
		if (data != null) {
			return data.metadata;
		}
		return 0;
	}
	
	@Override
	public TileEntity getBlockTileEntity(int x, int y, int z) { // 未使用
		throw new UnsupportedOperationException("method not implemented");
	}
	
	@Override
	public float getBrightness(int x, int y, int z, int var4) { // RenderBlocks で使用
		return 0;
	}
	
	public List<BlockData> getCustomRenderBlocks() {
		return customRenderBlocks;
	}
	
	@Override
	public int getHeight() { // RenderBlocks で使用
		return parent.getHeight();
	}
	
	@Override
	public float getLightBrightness(int x, int y, int z) { // RenderBlocks で使用
		return parent.provider.lightBrightnessTable[15]; // TODO 15をもっと賢く
	}
	
	@Override
	public int getLightBrightnessForSkyBlocks(int x, int y, int z, int limit) { // RenderBlocks で使用
		int skyBrightness = 15;
		int blockBrightness = 13;
		return skyBrightness << 20 | blockBrightness << 4;
	}
	
	public List<BlockData> getSemiSurfaceBlocks() {
		return semiSurfaceBlocks;
	}
	
	public List<BlockData> getSurfaceBlocks() {
		return surfaceBlocks;
	}
	
	public NBTTagCompound getTileEntityData(Coord3D absPos) {
		return allTileData.get(absPos);
	}
	
	@Override
	public Vec3Pool getWorldVec3Pool() { // RenderBlocks で使用
		return parent.getWorldVec3Pool();
	}
	
	@Override
	public boolean isAirBlock(int x, int y, int z) { // RenderBlocks で使用
		return this.getBlockId(x, y, z) == 0;
	}
	
	@Override
	public boolean isBlockNormalCube(int x, int y, int z) { // RenderBlocks で使用
		return Block.isNormalCube(this.getBlockId(x, y, z));
	}
	
	@Override
	public boolean isBlockOpaqueCube(int x, int y, int z) { // RenderBlocks で使用
		BlockData data = getBlockData(x, y, z);
		if (data != null && data.block != null) {
			return data.block.isOpaqueCube();
		}
		return false;
	}
	
	@Override
	public boolean isBlockProvidingPowerTo(int x, int y, int z, int direction) { // 未使用
		BlockData data = getBlockData(x, y, z);
		if (data != null && data.block != null) {
			return data.block.isProvidingStrongPower(this, x, y, z, direction);
		}
		return false;
	}
	
	public void setBlockData(BlockData data) {
		if (data != null) {
			allBlocks.put(data.absPos, data);
		}
	}
	
	public void setServerAllBlocks(Collection<BlockData> all) {
		clear();
		if (all.isEmpty()) {
			AirCraftCore.getInstance().debugPrint("ImitationSpace#setServerAllBlocks empty data");
		}
		else {
			for (BlockData data: all) { // AllBlocks に追加
				setBlockData(data);
			}
			updateServerSurface(); // Surface 更新
		}
	}
	
	public void setTileEntityData(NBTTagCompound tag) {
		int x = tag.getInteger("x");
		int y = tag.getInteger("y");
		int z = tag.getInteger("z");
		allTileData.put(new Coord3D(x, y, z), tag);
	}
	
	public void updateServerSurface() {
		// 表面計算
		surfaceBlocks.clear();
		for (Entry<Coord3D, BlockData> ent: allBlocks.entrySet()) {
			if (isSurface(allBlocks, ent.getKey())) {
				surfaceBlocks.add(ent.getValue());
			}
		}
		
		// 準表面計算
		Map<Coord3D, BlockData> _all = new HashMap<Coord3D, BlockData>(allBlocks);
		for (BlockData surface: surfaceBlocks) {
			_all.remove(surface.absPos);
		}
		semiSurfaceBlocks.clear();
		for (BlockData block: _all.values()) {
			if (isSurface(_all, block.absPos)) {
				semiSurfaceBlocks.add(block);
			}
		}
	}
	
	protected void appendClientSurface(Collection<BlockData> surfaces) {
		AirCraftCore core = AirCraftCore.getInstance();
		// いま追加されていないブロックのみを追加する
		Set<BlockData> unAppendedSurfaces = new HashSet<BlockData>(surfaces);
		unAppendedSurfaces.removeAll(surfaceBlocks);
		if (unAppendedSurfaces.isEmpty() == false) {
			// SurfaceBlocks 追加
			surfaceBlocks.addAll(unAppendedSurfaces);
			// カスタムレンダラ追加
			for (BlockData data: unAppendedSurfaces) {
				if (core.getBlockOperator(data.block.blockID).hasSpecialRender()) {
					customRenderBlocks.add(data);
				}
			}
			// 並び替え
			Collections.sort(surfaceBlocks, new BlockDataRenderingComparator());
			// 並び替え
			Collections.sort(customRenderBlocks, new BlockDataRenderingComparator());
		}
	}
	
	protected BlockData getBlockData(int x, int y, int z) {
		return allBlocks.get(new Coord3D(x, y, z));
	}
	
	private static boolean isSurface(Map<Coord3D, BlockData> blocks, Coord3D here) {
		for (Coord3D np: here.getNeighbor()) {
			BlockData data = blocks.get(np);
			if (data == null || !data.block.isOpaqueCube()) {
				return true;
			}
		}
		return false;
	}
}
