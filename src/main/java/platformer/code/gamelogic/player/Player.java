package platformer.code.gamelogic.player;

import java.awt.Color;
import java.awt.Graphics;

import platformer.code.gameengine.PhysicsObject;
import platformer.code.gameengine.graphics.MyGraphics;
import platformer.code.gameengine.hitbox.RectHitbox;
import platformer.code.gamelogic.Main;
import platformer.code.gamelogic.level.Level;
import platformer.code.gamelogic.tiles.Tile;

public class Player extends PhysicsObject {
	public float walkSpeed = 400;
	public float jumpPower = 1350;
	public float speedMultiplier = 1;
	public boolean gasFloating = false;
	public float gasLiftSpeed = 200;

	private int jumpsLeft = 2;
	private boolean jumpWasDown = false;

	public Player(float x, float y, Level level) {
		super(x, y, level.getLevelData().getTileSize(), level.getLevelData().getTileSize(), level);
		int offset = (int)(level.getLevelData().getTileSize() * 0.1);
		this.hitbox = new RectHitbox(this, offset, offset, width - offset, height - offset);
	}

	@Override
	public void update(float tslf) {
		super.update(tslf);
		
		movementVector.x = 0;

		if (PlayerInput.isLeftKeyDown()) {
			movementVector.x = -walkSpeed * speedMultiplier;
		}

		if (PlayerInput.isRightKeyDown()) {
			movementVector.x = walkSpeed * speedMultiplier;
		}

		if (gasFloating) {
			movementVector.y = -gasLiftSpeed;
		}

		if (collisionMatrix[BOT] != null) {
			jumpsLeft = 2;
		}

		boolean jumpDown = PlayerInput.isJumpKeyDown();

		if (jumpDown && !jumpWasDown && jumpsLeft > 0) {
			movementVector.y = -jumpPower;
			jumpsLeft--;
		}

		jumpWasDown = jumpDown;
		gasFloating = false;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.YELLOW);
		MyGraphics.fillRectWithOutline(g, (int)getX(), (int)getY(), width, height);
		
		if (Main.DEBUGGING) {
			for (int i = 0; i < closestMatrix.length; i++) {
				Tile t = closestMatrix[i];
				if (t != null) {
					g.setColor(Color.RED);
					g.drawRect((int)t.getX(), (int)t.getY(), t.getSize(), t.getSize());
				}
			}
		}
		
		hitbox.draw(g);
	}
}