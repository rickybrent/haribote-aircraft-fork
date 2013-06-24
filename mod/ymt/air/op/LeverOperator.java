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
import net.minecraft.src.Block;

/**
 * @author Yamato
 *
 */
public class LeverOperator extends AbstractRotationOperator {
	public LeverOperator() {
		for (int metadata = 0; metadata < rotation.length; metadata++) {
			// 1 ¨ 3 ¨ 2 ¨ 4
			// 5 ¨ 6
			// 0 ¨ 7
			int d = metadata & 7;
			switch (d) {
				case 1:
					d = 3;
					break;
				case 3:
					d = 2;
					break;
				case 2:
					d = 4;
					break;
				case 4:
					d = 1;
					break;
				case 5:
					d = 6;
					break;
				case 6:
					d = 5;
					break;
				case 0:
					d = 7;
					break;
				case 7:
					d = 0;
					break;
			}
			rotation[metadata] = (metadata & 8) | (d & 7);
		}
	}
	
	@Override
	protected void addMoveableBlockIds(Set<Integer> result) {
		result.add(Block.lever.blockID);
	}
	
}
