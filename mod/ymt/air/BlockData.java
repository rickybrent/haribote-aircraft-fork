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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import mod.ymt.cmn.Coord3D;
import mod.ymt.cmn.Utils;
import net.minecraft.src.Block;

/**
 * @author Yamato
 *
 */
public class BlockData {
	public final Block block;
	public final int metadata;
	public final Coord3D relPos;
	public final Coord3D absPos;
	
	public BlockData(Block block, int metadata, Coord3D relPos, Coord3D absPos) {
		if (block == null || relPos == null || absPos == null)
			throw new NullPointerException("argument must not null");
		this.block = block;
		this.metadata = metadata;
		this.relPos = relPos;
		this.absPos = absPos;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlockData other = (BlockData) obj;
		if (block.blockID != other.block.blockID)
			return false;
		if (metadata != other.metadata)
			return false;
		if (!relPos.equals(other.relPos))
			return false;
		if (!absPos.equals(other.absPos))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + block.blockID;
		result = prime * result + metadata;
		result = prime * result + relPos.hashCode();
		result = prime * result + absPos.hashCode();
		return result;
	}
	
	@Override
	public String toString() {
		return "BlockData [block=" + block + ", metadata=" + metadata + ", relPos=" + relPos + ", absPos=" + absPos + "]";
	}
	
	public void write(DataOutput output) throws IOException {
		int blocks = pack(block.blockID, metadata);
		if ((blocks & 0xFF00) != 0xFF00 && isByte(relPos.x) && isByte(relPos.y) && isByte(relPos.z)) {
			output.writeShort(blocks); // 2
			output.writeByte(relPos.x); // + 1
			output.writeByte(relPos.y); // + 1
			output.writeByte(relPos.z); // + 1
			// = 5
		}
		else {
			output.writeByte(-1); // 1
			output.writeShort(blocks); // + 2
			output.writeInt(relPos.x); // + 4
			output.writeInt(relPos.y); // + 4
			output.writeInt(relPos.z); // + 4
			// = 15
			// AirCraftCore.getInstance().debugPrint("write large %s", this);
		}
		// 書き込みサイズが変わると Packet250CustomPayload 用の分割数が変わってくるので注意
	}
	
	public static int pack(int blockId, int metadata) {
		return (blockId << 4) | (metadata & 15);
	}
	
	public static BlockData read(DataInput input, Coord3D base) throws IOException {
		byte first = input.readByte();
		if (first == -1) {
			int block = input.readUnsignedShort();
			Coord3D relPos = new Coord3D(input.readInt(), input.readInt(), input.readInt());
			return valueOf(unpackBlockId(block), unpackMetadata(block), relPos, relPos.add(base));
		}
		else {
			int block = 0xFFFF & ((first << 8) | input.readUnsignedByte());
			Coord3D relPos = new Coord3D(input.readByte(), input.readByte(), input.readByte());
			return valueOf(unpackBlockId(block), unpackMetadata(block), relPos, relPos.add(base));
		}
	}
	
	public static int unpackBlockId(int block) {
		return block >>> 4;
	}
	
	public static int unpackMetadata(int block) {
		return block & 15;
	}
	
	public static BlockData valueOf(int blockId, int metadata, Coord3D relPos, Coord3D absPos) {
		Block block = Utils.getBlock(blockId);
		if (block != null) {
			return new BlockData(block, metadata, relPos, absPos);
		}
		return null;
	}
	
	private static boolean isByte(int value) {
		return ((byte) value) == value;
	}
}
