package platformer.code.gamelogic.tiles;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import platformer.code.gameengine.hitbox.RectHitbox;
import platformer.code.gameengine.maths.Vector2D;
import platformer.code.gamelogic.level.Level;

public class Tile {

	protected Vector2D position;
	protected int size;
	protected RectHitbox hitbox;
	protected BufferedImage image;
	protected boolean solid;
	protected Level level;
	
	public Tile(float x, float y, int size, BufferedImage image, boolean solid, Level level) {
		this.position = new Vector2D(x * size, y * size);
		this.size = size;
		this.image = image;
		this.solid = solid;
		this.level = level;
	}
	
	public void update(float tslf) {}
	
	public void draw(Graphics g) {
		if (image != null)
			g.drawImage(image, (int) position.x, (int) position.y, size, size, null);
		
		if (hitbox != null)
			hitbox.draw(g);		
	}
	
	public boolean isSolid() {
		return solid;
	}
	
	public RectHitbox getHitbox() {
		return hitbox;
	}
	
	public float getX() {
		return position.x;
	}
	
	public float getY() {
		return position.y;
	}
	
	public int getRow() {
		return (int)(position.y / size);
	}
	
	public int getCol() {
		return (int)(position.x / size);
	}
	
	public int getSize() {
		return size;
	}

	// precondition: image is a valid BufferedImage or null
	// postcondition: this tile's image is changed to the given image
	public void setImage(BufferedImage image) {
		this.image = image;
	}
}