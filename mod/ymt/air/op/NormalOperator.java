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
import net.minecraft.src.Block;

/**
 * @author Yamato
 *
 */
public class NormalOperator extends AbstractOperator {
	@Override
	protected void addMoveableBlockIds(Set<Integer> result) {
		result.add(Block.stone.blockID);
		result.add(Block.grass.blockID);
		result.add(Block.dirt.blockID);
		result.add(Block.cobblestone.blockID);
		result.add(Block.planks.blockID); // 木材
		result.add(Block.sand.blockID);
		result.add(Block.gravel.blockID);
		result.add(Block.oreGold.blockID);
		result.add(Block.oreIron.blockID);
		result.add(Block.oreCoal.blockID);
		result.add(Block.leaves.blockID);
		result.add(Block.sponge.blockID);
		result.add(Block.glass.blockID);
		result.add(Block.oreLapis.blockID);
		result.add(Block.blockLapis.blockID);
		result.add(Block.sandStone.blockID);
		result.add(Block.web.blockID);
		result.add(Block.cloth.blockID);
		result.add(Block.blockGold.blockID);
		result.add(Block.blockSteel.blockID);
		result.add(Block.stoneDoubleSlab.blockID); // 石のハーフブロック二段重ね
		result.add(Block.stoneSingleSlab.blockID); // 石のハーフブロック
		result.add(Block.brick.blockID); // レンガ
		result.add(Block.tnt.blockID);
		result.add(Block.bookShelf.blockID);
		result.add(Block.cobblestoneMossy.blockID); // 苔石
		result.add(Block.obsidian.blockID); // 黒曜石動かすと、ネザーポータルが破綻するよね
		result.add(Block.oreDiamond.blockID);
		result.add(Block.blockDiamond.blockID);
		result.add(Block.tilledField.blockID); // 農地
		result.add(Block.oreRedstone.blockID);
		result.add(Block.oreRedstoneGlowing.blockID);
		result.add(Block.ice.blockID);
		result.add(Block.blockSnow.blockID);
		result.add(Block.blockClay.blockID);
		result.add(Block.fence.blockID);
		result.add(Block.netherrack.blockID); // ネザーラック
		result.add(Block.slowSand.blockID); // ソウルサンド
		result.add(Block.glowStone.blockID);
		result.add(Block.silverfish.blockID); // シルバーフィッシュ入りブロック
		result.add(Block.stoneBrick.blockID); // 石レンガ
		result.add(Block.mushroomCapRed.blockID); // 赤いキノコブロック
		result.add(Block.mushroomCapBrown.blockID); // 茶色のキノコブロック
		result.add(Block.fenceIron.blockID); // 鉄フェンス
		result.add(Block.thinGlass.blockID); // 板ガラス
		result.add(Block.melon.blockID); // すいか
		result.add(Block.mycelium.blockID); // 菌糸ブロック
		result.add(Block.netherBrick.blockID); // ネザーレンガ
		result.add(Block.netherFence.blockID); // ネザーレンガフェンス
		result.add(Block.whiteStone.blockID); // エンドストーン
		result.add(Block.redstoneLampIdle.blockID); // レッドストーンランプ(消灯)
		result.add(Block.redstoneLampActive.blockID); // レッドストーンランプ(点灯)
		result.add(Block.woodDoubleSlab.blockID); // 木のハーフブロック二段重ね
		result.add(Block.woodSingleSlab.blockID); // 木のハーフブロック
		result.add(Block.oreEmerald.blockID);
		result.add(Block.blockEmerald.blockID);
		result.add(Block.cobblestoneWall.blockID); // 丸石フェンス
		result.add(Block.workbench.blockID); // 作業台は NormalOperator で移動可能
		result.add(Block.cauldron.blockID); // 大釜
		
		// AbstractOperator が TileEntity に対応したので、BlockContainer も少しだけ動かせる
		result.add(Block.music.blockID); // ノートブロック
		result.add(Block.beacon.blockID); // ビーコン
		result.add(Block.enchantmentTable.blockID); // エンチャ台
		result.add(Block.commandBlock.blockID); // コマンドブロック
	}
	
	@Override
	protected boolean setRealBlockWithRotation(Materializer owner, int blockId, int metadata, int x, int y, int z, int rotate) {
		return setRealBlock(owner, blockId, metadata, x, y, z);
	}
}
