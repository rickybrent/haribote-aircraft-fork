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

import mod.ymt.cmn.Utils;
import net.minecraft.src.MathHelper;

/**
 * @author Yamato
 *
 */
public class AirCraftMoveHandler {
	public static final byte PROC_STOP = 0;
	public static final byte PROC_FORWARD = 1;
	public static final byte PROC_BACKWARD = 2;
	public static final byte PROC_TURN_RIGHT = 3;
	public static final byte PROC_TURN_LEFT = 4;
	public static final byte PROC_UP = 5;
	public static final byte PROC_DOWN = 6;
	public static final byte PROC_RIGHT = 7;
	public static final byte PROC_LEFT = 8;
	public static final byte PROC_TERMINATE = 9;
	
	public final EntityCraftCore owner;
	public final int keepTime;
	
	public String playerName = null;
	private boolean craftMoving = false;
	private int speedForward = 0;
	private int forward = 0;
	private int upSlide = 0;
	private int rightSlide = 0;
	private int rightTurn = 0;
	private int movingCount = 0;
	
	public AirCraftMoveHandler(EntityCraftCore owner, String playerName, int keepTime) {
		this.owner = owner;
		this.playerName = playerName;
		this.keepTime = 0 < keepTime ? keepTime : 20 * 60;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public boolean isCraftMoving() {
		return craftMoving;
	}
	
	public void onTick() {
		craftMoving = false;
		owner.motionX = owner.motionY = owner.motionZ = 0;
		float motionYaw = 0;
		
		if (owner.isDead) {
			return;
		}
		if (forward != 0) {
			double speed = speedForward * 0.1;
			float angle = -(owner.rotationYaw + owner.getDirectionOffset() * 90) * (float) Math.PI / 180.0F;
			owner.motionX = speed * MathHelper.sin(angle);
			owner.motionZ = speed * MathHelper.cos(angle);
			craftMoving = true;
		}
		if (rightSlide != 0) {
			double speed = rightSlide * 0.05;
			float angle = -(owner.rotationYaw + owner.getDirectionOffset() * 90 + 90) * (float) Math.PI / 180.0F;
			owner.motionX += speed * MathHelper.sin(angle);
			owner.motionZ += speed * MathHelper.cos(angle);
			craftMoving = true;
		}
		if (upSlide != 0) {
			owner.motionY = 0.08 * upSlide;
			craftMoving = true;
		}
		if (rightTurn != 0) {
			motionYaw = 0.2f * rightTurn;
			craftMoving = true;
		}
		
		if (craftMoving) {
			// ˆÚ“®
			owner.moveCraft(owner.motionX, owner.motionY, owner.motionZ, motionYaw, 0);
			if (keepTime < ++movingCount) {
				stopAllServerAndClient();
			}
		}
	}
	
	public void process(String sender, byte type) {
		if (owner.isDead) {
			return;
		}
		keepOnProcess();
		if (sender == null || sender.equals(this.playerName)) {
			switch (type) {
				case PROC_TERMINATE:
					processStop();
					owner.terminate();
					break;
				case PROC_STOP:
					if (craftMoving) {
						processStop();
					}
					else {
						processStop();
						owner.adjustPositionAndRotation();
					}
					break;
				case PROC_FORWARD:
					setForward(forward + 1);
					break;
				case PROC_BACKWARD:
					setForward(forward - 1);
					break;
				case PROC_TURN_RIGHT:
					setRightTurn(rightTurn + 1);
					break;
				case PROC_TURN_LEFT:
					setRightTurn(rightTurn - 1);
					break;
				case PROC_UP:
					setUpSlide(upSlide + 1);
					break;
				case PROC_DOWN:
					setUpSlide(upSlide - 1);
					break;
				case PROC_RIGHT:
					setRightSlide(rightSlide + 1);
					break;
				case PROC_LEFT:
					setRightSlide(rightSlide - 1);
					break;
				default:
					AirCraftCore.getInstance().debugPrint("AirCraftMoveHandler#process unknown proc[ %s ]", type);
					break;
			}
		}
	}
	
	public void processStop() {
		owner.stopImmediately();
		setForward(0);
		setRightSlide(0);
		setRightTurn(0);
		setUpSlide(0);
		keepOnProcess();
	}
	
	public void setPlayerName(String name) {
		this.playerName = name;
	}
	
	public void stopAllServerAndClient() {
		AirCraftCore core = AirCraftCore.getInstance();
		if (Utils.isServerSide(owner.worldObj)) {
			core.net.sendMoveStopToServerAndClients(playerName);
		}
	}
	
	private void keepOnProcess() {
		movingCount = 0;
	}
	
	protected int clip(int value, int min, int max) {
		if (value < min)
			return min;
		if (max < value)
			return max;
		return value;
	}
	
	protected void setForward(int value) {
		this.forward = clip(value, -1, 4);
		if (forward <= 0) {
			this.speedForward = forward;
		}
		else {
			this.speedForward = MathHelper.floor_double(Math.pow(2, forward - 1));
		}
	}
	
	protected void setRightSlide(int value) {
		this.rightSlide = clip(value, -3, 3);
	}
	
	protected void setRightTurn(int value) {
		this.rightTurn = clip(value, -4, 4);
	}
	
	protected void setUpSlide(int value) {
		this.upSlide = clip(value, -4, 4);
	}
}
