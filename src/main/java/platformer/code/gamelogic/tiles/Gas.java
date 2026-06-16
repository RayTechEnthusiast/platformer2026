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


		// Order: up, up-left, up-right, left, right, down, down-left, down-right
		int[] colChanges = {0, -1, 1, -1, 1, 0, -1, 1};
		int[] rowChanges = {-1, -1, -1, 0, 0, 1, 1, 1};

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