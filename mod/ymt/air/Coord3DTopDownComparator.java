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

import java.util.Comparator;
import mod.ymt.cmn.Coord3D;
import mod.ymt.cmn.Utils;

/**
 * @author Yamato
 *
 */
class Coord3DTopDownComparator implements Comparator<Coord3D> {
	@Override
	public int compare(Coord3D o1, Coord3D o2) {
		int result = 0;
		if (result == 0)
			result = Utils.compare(o2.y, o1.y);
		if (result == 0)
			result = Utils.compare(o2.x, o1.x);
		if (result == 0)
			result = Utils.compare(o2.z, o1.z);
		return result;
	}
}
