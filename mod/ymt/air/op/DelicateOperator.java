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

import java.util.Set;
import mod.ymt.air.Materializer;
import mod.ymt.air.ScanTime;
import net.minecraft.src.Block;

/**
 * @author Yamato
 *
 */
public class DelicateOperator extends AbstractOperator {
	@Override
	protected void addMoveableBlockIds(Set<Integer> result) {
		result.add(Block.sapling.blockID); // 苗木
		result.add(Block.tallGrass.blockID);
		result.add(Block.deadBush.blockID);
		result.add(Block.plantYellow.blockID);
		result.add(Block.plantRed.blockID);
		result.add(Block.mushroomBrown.blockID);
		result.add(Block.mushroomRed.blockID);
		result.add(Block.fire.blockID);
		result.add(Block.redstoneWire.blockID);
		result.add(Block.crops.blockID); // 小麦
		result.add(Block.pressurePlateStone.blockID); // 石の感圧板
		result.add(Block.pressurePlatePlanks.blockID); // 木の感圧板
		result.add(Block.snow.blockID);
		result.add(Block.cactus.blockID);
		result.add(Block.reed.blockID); // サトウキビ
		result.add(Block.portal.blockID); // ポータル移動可能
		result.add(Block.cake.blockID);
		result.add(Block.melonStem.blockID); // スイカの苗
		result.add(Block.pumpkinStem.blockID); // かぼちゃの苗
		result.add(Block.waterlily.blockID); // 蓮の葉
		result.add(Block.netherStalk.blockID); // ネザーいぼ
		result.add(Block.dragonEgg.blockID);
		result.add(Block.carrot.blockID);
		result.add(Block.potato.blockID);
		result.add(Block.tripWire.blockID); // トリップワイヤー
		result.add(Block.flowerPot.blockID); // 植木鉢
	}
	
	@Override
	protected ScanTime getScanTime(int blockID) {
		return blockID == Block.redstoneWire.blockID ? ScanTime.RedstoneWire : ScanTime.Delicate;
	}
	
	@Override
	protected boolean setRealBlockWithRotation(Materializer owner, int blockId, int metadata, int x, int y, int z, int rotate) {
		return setRealBlock(owner, blockId, metadata, x, y, z);
	}
}
