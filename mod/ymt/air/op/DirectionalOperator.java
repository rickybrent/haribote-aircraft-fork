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
import mod.ymt.air.AirCraftCore;
import net.minecraft.src.Block;

/**
 * @author Yamato
 *
 */
public class DirectionalOperator extends AbstractRotationOperator {
	public DirectionalOperator() {
		super(3, 0, 1, 2, 3);
	}
	
	@Override
	protected void addMoveableBlockIds(Set<Integer> result) {
		result.add(AirCraftCore.getInstance().getBlockIdPyxis()); // ‚±‚Ì Operator ‚ÅˆÚ“®
		result.add(Block.cocoaPlant.blockID);
		result.add(Block.fenceGate.blockID);
		result.add(Block.pumpkin.blockID);
		result.add(Block.pumpkinLantern.blockID);
	}
}
