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

import mod.ymt.air.Materializer;

/**
 * @author Yamato
 *
 */
public abstract class AbstractRotationOperator extends AbstractOperator {
	protected final int[] rotation = new int[16];

	protected AbstractRotationOperator() {
		for (int i = 0; i < rotation.length; i++) {
			rotation[i] = i; // デフォルト
		}
	}

	protected AbstractRotationOperator(int a, int b, int c, int d) {
		this();
		a &= 15;
		b &= 15;
		c &= 15;
		d &= 15;
		rotation[a] = b;
		rotation[b] = c;
		rotation[c] = d;
		rotation[d] = a;
	}

	protected int getRotatedMetadata(int metadata, int rotate) {
		for (int i = 0; i < rotate; i++) {
			if (0 <= metadata && metadata < rotation.length) {
				metadata = rotation[metadata];
			}
		}
		return metadata;
	}

	@Override
	protected boolean setRealBlockWithRotation(Materializer owner, int blockId, int metadata, int x, int y, int z, int rotate) {
		return setRealBlock(owner, blockId, getRotatedMetadata(metadata, rotate), x, y, z);
	}
}
