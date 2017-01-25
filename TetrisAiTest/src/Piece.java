import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;

import javax.imageio.ImageIO;
import static java.awt.Color.*;

public class Piece {
	int id; // a number from 1 to 7
	int row, col; // position of upper left corner of block
	int rotation = 0;
	int[][] block; // 4x4 array to hold the id of the
	private boolean isGhost = false; // allows there to be an outline for where
										// the block will go
	private int alpha = 255;
	private boolean fading = false;
	
	public static Color[] colors = { BLACK, YELLOW, CYAN, ORANGE, BLUE, RED,
			GREEN, MAGENTA, WHITE, WHITE }; // color of the blocks

	public Piece(int X) {
		/**
		 * Stores the different pieces of the Tetris game by putting an X in a
		 * 2d array. Each X represents a spot where an actual block will appear.
		 * 
		 * @param X
		 */
		isGhost = false;
		id = X;
		row = -1;
		col = 5;
		if (X == 1) {
			block = new int[][] { // O .. id 1

			{ 0, 0, 0, 0 },

			{ 0, X, X, 0},

			{ 0, X, X, 0 },

			{ 0, 0, 0, 0 } };
		} else if (X == 2) {
			block = new int[][] { // I .. id 2
			{ 0, 0, 0, 0 },

			{ X, X, X, X },

			{ 0, 0, 0, 0 },

			{ 0, 0, 0, 0 } };
		} else if (X == 3) {
			block = new int[][] { // L .. id 3
			{ 0, 0, 0, 0 },

			{ X, X, X, 0 },

			{ X, 0, 0, 0 },

			{ 0, 0, 0, 0 } };
		} else if (X == 4) {
			block = new int[][] { // _L .. id 4
			{ 0, 0, 0, 0 },

			{ X, X, X, 0 },

			{ 0, 0, X, 0 },

			{ 0, 0, 0, 0 } };
		} else if (X == 5) {
			block = new int[][] { // Z .. id 5
			{ 0, 0, 0, 0 },

			{ X, X, 0, 0 },

			{ 0, X, X, 0 },

			{ 0, 0, 0, 0 } };
		} else if (X == 6) {
			block = new int[][] { // _Z .. id 6
			{ 0, 0, 0, 0 },

			{ 0, X, X, 0 },

			{ X, X, 0, 0 },

			{ 0, 0, 0, 0 } };
		} else if (X == 7) {
			block = new int[][] { // T .. id 7
			{ 0, 0, 0, 0 },

			{ X, X, X, 0 },

			{ 0, X, 0, 0 },

			{ 0, 0, 0, 0 } };
		}
	}
	public void toggleFade() {
		fading = !fading;
	}
	
	public int[][] getReflectedBlock() {
		int[][] pieceStorage = new int[4][4];
		for(int r = 0; r<4; r++) {
			for(int c = 0; c<3; c++) {
				pieceStorage[r][c]=block[r][2-c];
			}
		}
		return pieceStorage;
	}
	public boolean canReflect(int grid[][]) {
		int[][] newBlock = getReflectedBlock();
		if(id == 1 || id == 2 || id == 7) {
			return true;
		} else {
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					if (newBlock[i][j] != 0 && grid[i + row][j + col] != 0) {
						return false;
					}
				}
			}
		}
		return true;
	}
	public void reflectPiece() {
		if(id!=2 && id!= 1 && id!=7) block = getReflectedBlock(); //prevents line cut
	}
	public Piece(Piece other, int grid[][]) {
		/**
		 * constructor for ghost piece
		 * 
		 */
		isGhost = true;
		id = other.id;
		row = other.row;
		col = other.col;
		block = new int[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				block[i][j] = other.block[i][j];
			}
		}
		while (canMoveDown(grid)) {
			moveDown();
		}
	}
	
	/**
	 * copy constructor for ghost piece
	 * 
	 */
	public Piece(Piece other) {
		isGhost = false;
		id = other.id;
		row = other.row;
		col = other.col;
		block = new int[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				block[i][j] = other.block[i][j];
			}
		}
	}
	public boolean canMoveDown(int grid[][]) {
		/**
		 * Checks if the block can move down anymore. If not, returns false.
		 * 
		 * @param grid
		 * @return
		 */
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (block[i][j] != 0 && grid[i + row + 1][j + col] != 0) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean canMoveDown(int grid[][], int squares) {
		/**
		 * Checks if the block can move down anymore. If not, returns false.
		 * 
		 * @param grid
		 * @return
		 */
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (block[i][j] != 0 && grid[i + row + squares][j + col] != 0) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean canRotate(int grid[][]) {
		/**
		 * Checks if the block can rotate without colliding with the walls,
		 * ground, or any other block. If it collides, it returns false.
		 * 
		 * @param grid
		 * @return
		 */
		if (row < 1) {
			return false;
		}
		int rotatedBlock[][] = getRotatedBlock();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (rotatedBlock[i][j] != 0 && grid[i + row][j + col] != 0) {
					return false;
				}
			}
		}
		return true;
	}

	public void rotate() {
		/**
		 * Calls the getRotatedBlock function.
		 */
		block = getRotatedBlock();
		rotation++;
	}

	// id: 1 is O .. 2 is I .. 3 is L .. 4 is _L .. 5 is Z .. 6 is _Z .. 7 is T
	public int[][] getRotatedBlock() {
		/**
		 * returns an array holding a rotated block
		 */
		if (id == 1) {// if O piece .. do nothing
			return block;
		}
		int rotatedBlock[][] = new int[4][4];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				rotatedBlock[2 - j][i] = block[i][j];
			}
		}
		if (id == 2) { // if I piece
			if (block[3][1] == 2) {
				rotatedBlock[1][3] = 2;
			} else {
				rotatedBlock[3][1] = 2;
			}
		}
		return rotatedBlock;
	}

	public void moveDown() {
		/**
		 * Moves the block down.
		 */
		row++;
	}

	public boolean canMoveLeft(int grid[][]) {
		/**
		 * Checks to see if the block can move lift. If it cannot, returns
		 * false.
		 * 
		 * @param grid
		 * @return
		 */
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (block[i][j] != 0 && grid[i + row][j + col - 1] != 0) {
					return false;
				}
			}
		}
		return true;
	}

	public void moveLeft() {
		/**
		 * Moves the block left.
		 */
		col--;
	}

	public boolean canMoveRight(int grid[][]) {
		/**
		 * Checks to see if the block can move right. If not, returns false.
		 * 
		 * @param grid
		 * @return
		 */
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (block[i][j] != 0 && grid[i + row][j + col + 1] != 0) {
					return false;
				}
			}
		}
		return true;
	}

	public void moveRight() {
		/**
		 * Moves the block right.
		 */
		col++;
	}

	/**
	 * Checks to see if there is a block present when the block collides with
	 * the ground or a block. If there is no block, the new block fills the spot
	 * with the id .
	 * 
	 * @param grid
	 */
	public void addToGrid(int grid[][]) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (block[i][j] != 0) {
					grid[i + row][j + col] = block[i][j];
				}
			}
		}
	}

	/**
	 * Draws the block pieces then fills them with color. Also draws the ghost
	 * blocks.
	 * 
	 * @param g
	 * @param x
	 * @param y
	 */
	public void draw(Graphics2D g, int x, int y) {
		int size = TetrisAI.size;
		int roundness = 8;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (block[i][j] != 0) {
					// g.setColor(new Color(greyVal, greyVal, greyVal));
					if (isGhost) {
						g.setColor(GRAY);
						g.drawRoundRect((j + col) * size + x + 1, (i + row)
								* size + y + 1, size - 2, size - 2, roundness,
								roundness);
					} else {
						Color cColor = colors[id];
						g.setColor(colors[id]);
						g.setColor(new Color(cColor.getRed(), cColor.getGreen(), cColor.getBlue(), alpha));
						g.fillRoundRect((j + col) * size + x, (i + row) * size
								+ y, size, size, roundness, roundness);
						g.setColor(BLACK);
						g.drawRoundRect((j + col) * size + x, (i + row) * size
								+ y, size, size, roundness, roundness);

					}
				}
			}
		}
		g.setColor(BLACK);
	}
}
	// end class Piece



