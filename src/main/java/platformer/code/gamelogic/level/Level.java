package platformer.code.gamelogic.level;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import platformer.code.gameengine.PhysicsObject;
import platformer.code.gameengine.graphics.Camera;
import platformer.code.gameengine.loaders.Mapdata;
import platformer.code.gameengine.loaders.Tileset;
import platformer.code.gamelogic.GameResources;
import platformer.code.gamelogic.Main;
import platformer.code.gamelogic.enemies.Enemy;
import platformer.code.gamelogic.player.Player;
import platformer.code.gamelogic.tiledMap.Map;
import platformer.code.gamelogic.tiles.Flag;
import platformer.code.gamelogic.tiles.Flower;
import platformer.code.gamelogic.tiles.Gas;
import platformer.code.gamelogic.tiles.SolidTile;
import platformer.code.gamelogic.tiles.Spikes;
import platformer.code.gamelogic.tiles.Tile;
import platformer.code.gamelogic.tiles.Water;

public class Level {

	private LevelData leveldata;
	private Map map;
	private Enemy[] enemies;
	public static Player player;
	private Camera camera;

	private boolean active;
	private boolean playerDead;
	private boolean playerWin;

	private ArrayList<Enemy> enemiesList = new ArrayList<>();
	private ArrayList<Flower> flowers = new ArrayList<>();

	private List<PlayerDieListener> dieListeners = new ArrayList<>();
	private List<PlayerWinListener> winListeners = new ArrayList<>();

	private Mapdata mapdata;
	private int width;
	private int height;
	private int tileSize;
	private Tileset tileset;
	public static float GRAVITY = 70;

	private long waterTimer = 0;
	private String statusText = "";

	public Level(LevelData leveldata) {
		this.leveldata = leveldata;
		mapdata = leveldata.getMapdata();
		width = mapdata.getWidth();
		height = mapdata.getHeight();
		tileSize = mapdata.getTileSize();
		restartLevel();
	}

	public LevelData getLevelData() {
		return leveldata;
	}

	public void restartLevel() {
		enemiesList.clear();
		flowers.clear();

		int[][] values = mapdata.getValues();
		Tile[][] tiles = new Tile[width][height];

		for (int x = 0; x < width; x++) {
			int xPosition = x;
			for (int y = 0; y < height; y++) {
				int yPosition = y;

				tileset = GameResources.tileset;

				tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this);
				if (values[x][y] == 0)
					tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this);
				else if (values[x][y] == 1)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid"), this);
				else if (values[x][y] == 2)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_DOWNWARDS, this);
				else if (values[x][y] == 3)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_UPWARDS, this);
				else if (values[x][y] == 4)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_LEFTWARDS, this);
				else if (values[x][y] == 5)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_RIGHTWARDS, this);
				else if (values[x][y] == 6)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Dirt"), this);
				else if (values[x][y] == 7)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Grass"), this);
				else if (values[x][y] == 8)
					enemiesList.add(new Enemy(xPosition * tileSize, yPosition * tileSize, this));
				else if (values[x][y] == 9)
					tiles[x][y] = new Flag(xPosition, yPosition, tileSize, tileset.getImage("Flag"), this);
				else if (values[x][y] == 10) {
					tiles[x][y] = new Flower(xPosition, yPosition, tileSize, tileset.getImage("Flower1"), this, 1);
					flowers.add((Flower) tiles[x][y]);
				}
				else if (values[x][y] == 11) {
					tiles[x][y] = new Flower(xPosition, yPosition, tileSize, tileset.getImage("Flower2"), this, 2);
					flowers.add((Flower) tiles[x][y]);
				}
				else if (values[x][y] == 12)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_down"), this);
				else if (values[x][y] == 13)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_up"), this);
				else if (values[x][y] == 14)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_middle"), this);
				else if (values[x][y] == 15)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasOne"), this, 1);
				else if (values[x][y] == 16)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasTwo"), this, 2);
				else if (values[x][y] == 17)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasThree"), this, 3);
				else if (values[x][y] == 18)
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Falling_water"), this, 0);
				else if (values[x][y] == 19)
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Full_water"), this, 3);
				else if (values[x][y] == 20)
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Half_water"), this, 2);
				else if (values[x][y] == 21)
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Quarter_water"), this, 1);
			}
		}

		enemies = new Enemy[enemiesList.size()];
		map = new Map(width, height, tileSize, tiles);
		camera = new Camera(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT, 0, map.getFullWidth(), map.getFullHeight());

		for (int i = 0; i < enemiesList.size(); i++) {
			enemies[i] = new Enemy(enemiesList.get(i).getX(), enemiesList.get(i).getY(), this);
		}

		player = new Player(leveldata.getPlayerX() * map.getTileSize(), leveldata.getPlayerY() * map.getTileSize(), this);
		camera.setFocusedObject(player);

		active = true;
		playerDead = false;
		playerWin = false;
		waterTimer = 0;
		statusText = "";
	}

	public void onPlayerDeath() {
		active = false;
		playerDead = true;
		throwPlayerDieEvent();
	}

	public void onPlayerWin() {
		active = false;
		playerWin = true;
		throwPlayerWinEvent();
	}

	public void update(float tslf) {
		if (active) {
			updateFluidEffects();

			player.update(tslf);

			if (map.getFullHeight() + 100 < player.getY())
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.BOT] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.TOP] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.LEF] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.RIG] instanceof Spikes)
				onPlayerDeath();

			for (int i = 0; i < flowers.size(); i++) {
				if (flowers.get(i).getHitbox().isIntersecting(player.getHitbox())) {
					if (flowers.get(i).getType() == 1)
						water(flowers.get(i).getCol(), flowers.get(i).getRow(), map, 3);
					else
						addGas(flowers.get(i).getCol(), flowers.get(i).getRow(), map, 20, new ArrayList<Gas>());
					flowers.remove(i);
					i--;
				}
			}

			updateFluidEffects();

			for (int i = 0; i < enemies.length; i++) {
				enemies[i].update(tslf);
				if (player.getHitbox().isIntersecting(enemies[i].getHitbox())) {
					onPlayerDeath();
				}
			}

			map.update(tslf);
			camera.update(tslf);
		}
	}

	// precondition: player and map have been created
	// postcondition: water and gas effects are applied to the player
	private void updateFluidEffects() {
		boolean touchingWater = playerTouchingWater();
		boolean touchingGas = playerTouchingGas();

		statusText = "";

		if (touchingWater) {
			if (waterTimer == 0) {
				waterTimer = System.currentTimeMillis();
			}

			float secondsInWater = (System.currentTimeMillis() - waterTimer) / 1000.0f;
			float newSpeedMultiplier = 1.0f - secondsInWater * 0.18f;

			if (newSpeedMultiplier < 0.35f) {
				newSpeedMultiplier = 0.35f;
			}

			player.speedMultiplier = newSpeedMultiplier;
			statusText = "Water speed: " + (int)(newSpeedMultiplier * 100) + "%";
		}
		else {
			waterTimer = 0;
			player.speedMultiplier = 1;
		}

		if (touchingGas) {
			player.gasFloating = true;

			if (statusText.equals("")) {
				statusText = "Gas: floating";
			}
			else {
				statusText += "   Gas: floating";
			}
		}
		else {
			player.gasFloating = false;
		}
	}

	// precondition: player and map have been created
	// postcondition: returns true if the player is touching water
	private boolean playerTouchingWater() {
		for (int col = 0; col < map.getWidth(); col++) {
			for (int row = 0; row < map.getHeight(); row++) {
				Tile tile = map.getTiles()[col][row];

				if (tile instanceof Water && playerOverlapsTile(tile)) {
					return true;
				}
			}
		}

		return false;
	}

	// precondition: player and map have been created
	// postcondition: returns true if the player is touching gas
	private boolean playerTouchingGas() {
		for (int col = 0; col < map.getWidth(); col++) {
			for (int row = 0; row < map.getHeight(); row++) {
				Tile tile = map.getTiles()[col][row];

				if (tile instanceof Gas && playerOverlapsTile(tile)) {
					return true;
				}
			}
		}

		return false;
	}

	// precondition: tile is a valid map tile
	// postcondition: returns true if the player rectangle overlaps the tile rectangle
	private boolean playerOverlapsTile(Tile tile) {
		return player.getX() < tile.getX() + tile.getSize()
			&& player.getX() + tileSize > tile.getX()
			&& player.getY() < tile.getY() + tile.getSize()
			&& player.getY() + tileSize > tile.getY();
	}

	// precondition: col and row are valid locations where water should start flowing
	// postcondition: water is added to the map recursively following gravity and sideways flow rules
	private void water(int col, int row, Map map, int fullness) {
		Tile[][] tiles = map.getTiles();

		if (col < 0 || col >= tiles.length || row < 0 || row >= tiles[col].length) {
			return;
		}

		if (tiles[col][row] instanceof Water || tiles[col][row].isSolid()) {
			return;
		}

		boolean isFallingWater = fullness == 0;

		// If water reaches the bottom edge, it should fall off the map.
		// It should not spread left/right as if the map edge were a solid floor.
		if (row + 1 >= tiles[col].length) {
			addWaterTile(col, row, map, fullness);
			return;
		}

		// If the tile below is open, water flows downward.
		// The current tile only looks like Falling_water if it was already part of a falling stream.
		if (!tiles[col][row + 1].isSolid() && !(tiles[col][row + 1] instanceof Water)) {
			addWaterTile(col, row, map, fullness);
			water(col, row + 1, map, 0);
			return;
		}

		// Once falling water hits a real surface, it becomes full water and can spread sideways.
		if (isFallingWater) {
			fullness = 3;
		}

		addWaterTile(col, row, map, fullness);

		int nextFullness = fullness - 1;
		if (nextFullness < 1) {
			nextFullness = 1;
		}

		if (col + 1 < tiles.length && !tiles[col + 1][row].isSolid() && !(tiles[col + 1][row] instanceof Water)) {
			water(col + 1, row, map, nextFullness);
		}

		if (col - 1 >= 0 && !tiles[col - 1][row].isSolid() && !(tiles[col - 1][row] instanceof Water)) {
			water(col - 1, row, map, nextFullness);
		}
	}

	// precondition: col and row are valid map positions and fullness is 0, 1, 2, or 3
	// postcondition: adds a water tile with the correct image for its fullness
	private void addWaterTile(int col, int row, Map map, int fullness) {
		Water w = new Water(col, row, tileSize, tileset.getImage(getWaterImageName(fullness)), this, fullness);
		map.addTile(col, row, w);
	}

	// precondition: fullness is 0, 1, 2, or 3
	// postcondition: returns the correct water image name for the given fullness
	private String getWaterImageName(int fullness) {
		if (fullness == 0) {
			return "Falling_water";
		}
		else if (fullness == 2) {
			return "Half_water";
		}
		else if (fullness == 1) {
			return "Quarter_water";
		}
		else {
			return "Full_water";
		}
	}

	// precondition: col and row are valid locations where gas should start spreading
	// postcondition: gas is added iteratively until the required number of squares is filled or there is no room
	private void addGas(int col, int row, Map map, int numSquaresToFill, ArrayList<Gas> placedThisRound) {
		Tile[][] tiles = map.getTiles();

		if (numSquaresToFill <= 0) {
			return;
		}

		if (col < 0 || col >= tiles.length || row < 0 || row >= tiles[col].length) {
			return;
		}

		if (tiles[col][row].isSolid() || tiles[col][row] instanceof Gas) {
			return;
		}

		placedThisRound.clear();

		Gas startGas = new Gas(col, row, tileSize, tileset.getImage("GasOne"), this, 0);
		map.addTile(col, row, startGas);
		placedThisRound.add(startGas);
		numSquaresToFill--;

		for (int i = 0; i < placedThisRound.size(); i++) {
			if (numSquaresToFill <= 0) {
				return;
			}

			Gas currentGas = placedThisRound.get(i);
			int currentCol = currentGas.getCol();
			int currentRow = currentGas.getRow();

			int[] colChanges = {0, -1, 1, 0};
			int[] rowChanges = {-1, 0, 0, 1};

			for (int j = 0; j < colChanges.length; j++) {
				int newCol = currentCol + colChanges[j];
				int newRow = currentRow + rowChanges[j];

				if (newCol >= 0 && newCol < tiles.length && newRow >= 0 && newRow < tiles[newCol].length) {
					if (!tiles[newCol][newRow].isSolid() && !(tiles[newCol][newRow] instanceof Gas)) {
						Gas newGas = new Gas(newCol, newRow, tileSize, tileset.getImage("GasOne"), this, 0);
						map.addTile(newCol, newRow, newGas);
						placedThisRound.add(newGas);
						numSquaresToFill--;

						if (numSquaresToFill <= 0) {
							return;
						}
					}
				}
			}
		}
	}

	public void draw(Graphics g) {
		g.translate((int) -camera.getX(), (int) -camera.getY());

		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				Tile tile = map.getTiles()[x][y];
				if (tile == null)
					continue;

				if (tile instanceof Gas) {
					int adjacencyCount = 0;

					for (int i = -1; i < 2; i++) {
						for (int j = -1; j < 2; j++) {
							if (j != 0 || i != 0) {
								if ((x + i) >= 0 && (x + i) < map.getTiles().length && (y + j) >= 0 && (y + j) < map.getTiles()[x + i].length) {
									if (map.getTiles()[x + i][y + j] instanceof Gas) {
										adjacencyCount++;
									}
								}
							}
						}
					}

					if (adjacencyCount == 8) {
						((Gas) tile).setIntensity(2);
						tile.setImage(tileset.getImage("GasThree"));
					}
					else if (adjacencyCount > 5) {
						((Gas) tile).setIntensity(1);
						tile.setImage(tileset.getImage("GasTwo"));
					}
					else {
						((Gas) tile).setIntensity(0);
						tile.setImage(tileset.getImage("GasOne"));
					}
				}

				if (camera.isVisibleOnCamera(tile.getX(), tile.getY(), tile.getSize(), tile.getSize()))
					tile.draw(g);
			}
		}

		if (!statusText.equals("")) {
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.drawString(statusText, (int)player.getX(), (int)player.getY() - 20);
		}

		for (int i = 0; i < enemies.length; i++) {
			enemies[i].draw(g);
		}

		player.draw(g);

		if (Camera.SHOW_CAMERA)
			camera.draw(g);

		g.translate((int) +camera.getX(), (int) +camera.getY());
	}

	public void throwPlayerDieEvent() {
		for (PlayerDieListener playerDieListener : dieListeners) {
			playerDieListener.onPlayerDeath();
		}
	}

	public void addPlayerDieListener(PlayerDieListener listener) {
		dieListeners.add(listener);
	}

	public void throwPlayerWinEvent() {
		for (PlayerWinListener playerWinListener : winListeners) {
			playerWinListener.onPlayerWin();
		}
	}

	public void addPlayerWinListener(PlayerWinListener listener) {
		winListeners.add(listener);
	}

	public boolean isActive() {
		return active;
	}

	public boolean isPlayerDead() {
		return playerDead;
	}

	public boolean isPlayerWin() {
		return playerWin;
	}

	public Map getMap() {
		return map;
	}

	public Player getPlayer() {
		return player;
	}
}