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

import mod.ymt.cmn.Coord3D;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;

/**
 * @author Yamato
 *
 */
public class BlockPyxis extends Block {
	private Icon[] icons = new Icon[6];
	
	public BlockPyxis(int blockId) {
		super(blockId, Material.rock);
		setHardness(0.5F);
		setStepSound(soundStoneFootstep);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F); // ハーフブロックサイズ
		setLightValue(1.0F); // 光源になるよ
		this.setCreativeTab(CreativeTabs.tabTransport); // 乗り物タブ
	}
	
	@Override
	public Icon getIcon(int side, int metadata) {
		switch (side) {
			case 0: // 底面
				return icons[0];
			case 1: // 上面
				return icons[2 + metadata % 4];
			default: // 側面
				return icons[1];
		}
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
		AirCraftCore core = AirCraftCore.getInstance();
		if (core.tryInteractServer(world)) {
			int metadata = world.getBlockMetadata(x, y, z);
			// ブロック削除
			world.setBlockToAir(x, y, z);
			// エンティティ生成
			world.spawnEntityInWorld(newPyxis(world, player, metadata, new Coord3D(x, y, z)));
		}
		return true;
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving player, ItemStack stack) {
		int metadata = getDirection(player);
		world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
	}
	
	@Override
	public void registerIcons(IconRegister ir) {
		icons = new Icon[]{
			ir.registerIcon("mod.ymt.air.py_bottom"),
			ir.registerIcon("mod.ymt.air.py_side"),
			ir.registerIcon("mod.ymt.air.py_top0"),
			ir.registerIcon("mod.ymt.air.py_top1"),
			ir.registerIcon("mod.ymt.air.py_top2"),
			ir.registerIcon("mod.ymt.air.py_top3"),
		};
	}
	
	protected Entity newPyxis(World world, EntityPlayer player, int metadata, Coord3D basePos) {
		Entity ent = new EntityPyxis(player.getEntityName(), world, blockID, metadata, basePos);
		ent.setPosition(basePos.x + 0.5, basePos.y, basePos.z + 0.5);
		return ent;
	}
	
	private static int getDirection(Entity ent) {
		return MathHelper.floor_double((ent.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
	}
}
