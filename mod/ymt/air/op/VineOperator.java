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
import mod.ymt.air.ScanTime;
import net.minecraft.src.Block;

/**
 * @author Yamato
 *
 */
public class VineOperator extends AbstractRotationOperator {
	public VineOperator() {
		for (int metadata = 0; metadata < rotation.length; metadata++) {
			int d = 0;
			if ((metadata & 1) != 0) // 南→西
				d |= 2;
			if ((metadata & 2) != 0) // 西→北
				d |= 4;
			if ((metadata & 4) != 0) // 北→東
				d |= 8;
			if ((metadata & 8) != 0) // 東→南
				d |= 1;
			rotation[metadata] = d;
		}
	}
	
	@Override
	protected void addMoveableBlockIds(Set<Integer> result) {
		result.add(Block.vine.blockID);
	}
	
	@Override
	protected ScanTime getScanTime(int blockId) {
		return ScanTime.Delicate;
	}
}
