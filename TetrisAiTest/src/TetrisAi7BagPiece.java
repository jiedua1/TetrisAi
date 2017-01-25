import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Project created by: Mr Ruth .. Niles North High School .. Skokie, IL ..
 */

public class TetrisAi7BagPiece extends JPanel implements ActionListener, KeyListener {

  private int frameCount; //keeps track of current frame			
  private Timer timer;// handles animation
  private int delay; //miliseconds between each action
  private static Image offScreenBuffer;// needed for double buffering graphics
  private Graphics offScreenGraphics;// needed for double buffering graphics
  
  private int numCols = 10;
  private int numRows = 20; //rows in tetris game
  private int grid[][] = new int[numRows + 3][14]; //array that stores the piece data
  private ArrayList<Integer> pieceArray = new ArrayList<Integer>();
  
  private Piece curPiece = new Piece(1); //piece that player can move
  private Piece ghostPiece = new Piece(1); //piece at bottom of board
  private Piece nextPiece = new Piece(1); //piece that will appear next, it is in the upper right box
  private AI ai; //an ai object
  private Font font; //the font of text
  private int linesCleared[] = new int[5]; //stores the data on single line and multi line clears
  private int score = 0; //keeps track of lines cleared
  private int pieceCount = 0; //keeaaps track of number of pieces played, does not go down
  private Random rand; //the RNG
  public static final int size = 24;  
  /** size of tiles in backgrond grid, in pixels */
  private boolean aiOn = false; //whether or not ai is on
  private boolean keyReady = true; //whether or not the next key is ready to be pressed, glitched atm
  private boolean gameOver = true; //returns true when game lost, false otherwise
  public boolean drawOn = true;
  private boolean sevenBagOn = false; //determines whether to use 7 bag piece generation or completely pseudorandom piece gen.
  private int searchDepth = 1; //can be set to 1 or 2, depending on whether to use 2nd piece or not
  
  /** 
	* Initializes the game and all the necessary variables
	*/
  public synchronized void init() {
	  rand = new Random();
	  init(rand.nextLong());
  }
  
  //exercise for fun
  public void shiftBoard(int grid[][]) {
	  for(int r = 0; r<numRows+1; r++) {
			int curPos = 3;
			for(int c = 2; c<numCols+2; c++) {
				if(grid[r][c]==0) {
					curPos = c+1;
					while(curPos<numCols+2) {
						if(grid[r][curPos]!=0) {
							grid[r][c]=grid[r][curPos];
							grid[r][curPos]=0;
							c++;
						}
						curPos++;
					}
				}
				if(curPos > numCols) {
					break;
				}
			}
		}

  }
  public synchronized void init(long n) {
	 
	
    offScreenBuffer = createImage(getWidth(), getHeight());// should be 1016x736
    offScreenGraphics = offScreenBuffer.getGraphics();
    delay = 0;
    timer = new Timer(delay, this);

    grid = new int[numRows + 3][numCols + 4];
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[0].length; j++) {
        if (j == 0 || j == 1 || j == numCols+2 || j == numCols+3 || i == numRows + 1
            || i == numRows + 2)
          grid[i][j] = 9;
      }
    }
    font = new Font("Lucida Console", Font.BOLD, 18);
    linesCleared = new int[5];
    rand = new Random(n); // default 139
    
    if(sevenBagOn) {
    	pieceArray.clear();
    	for(int i=1; i<7; i++) {
    		pieceArray.add(i);
    	}
    	Collections.shuffle(pieceArray);
    	curPiece = new Piece(pieceArray.remove(0));
        nextPiece = new Piece(pieceArray.remove(0));
    } else {
    	curPiece = new Piece(rand.nextInt(7) + 1);
    	nextPiece = new Piece(rand.nextInt(7) + 1);
    }
    curPiece.moveDown();
    curPiece.moveDown();
    ghostPiece = new Piece(curPiece, grid); 
    ai = new AI(numRows, numCols);
    frameCount = 1;
    setGameOver(false);
    offScreenGraphics.clearRect(0, 0, 800, 600);
    // timer.setInitialDelay(2000);
    timer.start();
    repaint();
  }
  /**
   * creates a new piece and adds it to the grid for the player to control.
   * This method also modifies the relevant variables like pieceCount, ghostPiece, nextPiece,
   * and updates them. This methods moves the piece down two spots so that it will not 
   * go out of the board. This method also checks game over when called.
   */
  public void addPieceToGrid() {
    curPiece.addToGrid(grid);
    removeCompleteRows();
    pieceCount++;
    curPiece = nextPiece;
    ghostPiece = new Piece(curPiece, grid);
    if(sevenBagOn) {
	    if(pieceArray.size()==0) {
	    	for(int i=1; i<=7; i++) {
	    		pieceArray.add(i);
	    	}
	    	Collections.shuffle(pieceArray);
	    }
	    nextPiece = new Piece(pieceArray.remove(0));
    } else {
    	nextPiece = new Piece(1+rand.nextInt(7));
    }
    
    
    if (curPiece.canMoveDown(grid)) {
      curPiece.moveDown();
      if (curPiece.canMoveDown(grid)) {
        curPiece.moveDown();
      } else {
        setGameOver(true);
        repaint();
        timer.stop();
      }
    } else {
      setGameOver(true);
      repaint();
      timer.stop();
    }
  }
  
  /** 
	 * When a row(s) is completely filled, this method iterates through that row of the grid
	 * to remove every piece of the row. It shifts all the rows above the n removed row(s) down n spots. 
	 */
  public void removeCompleteRows() {
    int rowsRemoved = 0;
    for (int curRow = numRows; curRow > 0; curRow--) {
      // check if i-th row is full
      boolean rowFull = true;
      for (int j = 2; j < numCols+2; j++) {
        if (grid[curRow][j] == 0) {
          rowFull = false;
          break;
        }
      }
      if (rowFull) {
        rowsRemoved++;
        // shift down
        for (int i = curRow; i > 0; i--) {
          for (int j = 2; j < numCols+2; j++) {
            grid[i][j] = grid[i - 1][j];
          }
        }
        curRow++;// stay at current index
      }
    }
    if (rowsRemoved > 0) {
      linesCleared[rowsRemoved]++;
      linesCleared[0]++;
    }
  }

  /** 
	 * Regulates the action flow of the game. Every 20 frames, this method moves the current
	 * piece down if it can, and if not, a new piece is created. Also, this method
	 * checks if the ai is on and if so, tells the ai to perform it.
	 */
  public void actionPerformed(ActionEvent e) {
    if (frameCount % 20 == 0) {
      if (curPiece.canMoveDown(grid)) {
        curPiece.moveDown();
      } else {
        addPieceToGrid();
      }
    }

    if (aiOn && !gameOver) {
		int move = ai.makeMove(grid, curPiece, nextPiece, searchDepth);
		attemptMove(move);
    }

    removeCompleteRows();
    frameCount++;// update the frameCount
    if(drawOn) repaint();
    if(drawOn || linesCleared[0] % 1000==0) repaint();// needed to refresh the animation
  }
  
  /** 
	 * Displays all the necessary text in the side and draws all 
	 * the pieces(curpiece, ghost, next). Also, this method displays game over when player loses.
	 */
  public synchronized void draw(Graphics2D g) {
    if (g == null || offScreenGraphics == null)
      return;
    g.clearRect(0, 0, 800, 600);
    drawGrid(g);
    curPiece.draw(g, 0, 0);
    ghostPiece.draw(g, 0, 0);
    int leftMargin = 400;
    g.setFont(font);
    g.setColor(Color.DARK_GRAY);
    g.drawString("NEXT PIECE", leftMargin, 30);
    nextPiece.draw(g, 300, 50);
    g.drawString("press R   .. restart", leftMargin, 195);
    if (aiOn)
      g.drawString("press A   .. ai    :ON", leftMargin, 220);
    else
      g.drawString("press A   .. ai    :OFF", leftMargin, 220);
    g.drawString("press W/S .. speed :" + delay, leftMargin, 245);
    g.drawString("piece count: " + pieceCount, leftMargin, 300);
    g.drawString("1 lines    : " + linesCleared[1], leftMargin, 350);
    g.drawString("2 lines    : " + linesCleared[2], leftMargin, 380);
    g.drawString("3 lines    : " + linesCleared[3], leftMargin, 410);
    g.drawString("4 lines    : " + linesCleared[4], leftMargin, 440);
    g.drawString("--------------", leftMargin, 470);
    g.drawString("total lines: " + linesCleared[0], leftMargin, 500);
    g.drawString(Double.toString(ai.evalBoard(grid)), leftMargin, 100);
    g.drawString(Integer.toString(ai.maxHeight), leftMargin, 130);
    
    if (isGameOver()) {
      g.drawString("  GAME OVER !!  ", leftMargin + 200, 550);
    }
  }
  
  /**
   * Fills grid with black tiles
   */
  public synchronized void drawGrid(Graphics2D g) {
    if (g == null || offScreenGraphics == null)
      return;
    int greyVal = 0;
    int roundness = 8;
    g.setColor(Color.BLACK);
    g.fillRect(2 * size + 2, size, 10 * size - 1, size * numRows + 1);
    for (int i = 1; i < grid.length; i++) {
      for (int j = 0; j < grid[0].length; j++) {
        if (grid[i][j] != 0) {
          g.setColor(Piece.colors[grid[i][j]]);
          g.fillRoundRect(j * size, i * size, size, size, roundness, roundness);
          g.setColor(Color.BLACK);
          g.drawRoundRect(j * size, i * size, size, size, roundness, roundness);
        } else {
          g.setColor(Color.BLACK);
          g.fillRect(j * size, i * size, size, size);
        }
      }
    }
    // g.drawRect(2 * size - 1, size, 10 * size + 2, size * numRows + 1);
  }

  /**
   * int move .. 4 left .. 6 right .. 2 up .. 8 down .. 0 slam
   * trys to move or rotate the piece, checks if it can move or not)
	* Also sets the ghost piece position if the piece moves/rotates
   */
  public void attemptMove(int move) {
    if (move == 4) {
      if (curPiece.canMoveLeft(grid)) {
        curPiece.moveLeft();
        ghostPiece = new Piece(curPiece, grid);
      }
    } else if (move == 6) {
      if (curPiece.canMoveRight(grid)) {
        curPiece.moveRight();
        ghostPiece = new Piece(curPiece, grid);
      }
    } else if (move == 2) {
      if (curPiece.canRotate(grid)) {
        curPiece.rotate();
        ghostPiece = new Piece(curPiece, grid);
      }
    } else if (move == 8) {
      if (curPiece.canMoveDown(grid)) {
        curPiece.moveDown();
      } else {
        addPieceToGrid();
      }
    } else if (move == 0) {
      while (curPiece.canMoveDown(grid)) {
        curPiece.moveDown();
      }
      addPieceToGrid();
    }

  }

  @Override
  /**
   * This method encompasses all the keystroke actions for the game, including
   * changing the delay, switching the ai on, checking if the key is ready,
   * and calls the attemptmove method if keys are pressed
   */
  public void keyPressed(KeyEvent e) {
	  
    int keyCode = e.getKeyCode();
    // System.out.println("in keyTyped!!" + keyCode);
    if (!keyReady)
      return;
    keyReady = false;
    // toggle ai
    if (keyCode == KeyEvent.VK_A) {
      aiOn = !aiOn;
      if(aiOn) ai.startAi();
    }
    if (keyCode == KeyEvent.VK_W) {
      if (delay > 0) {
        delay--;
        timer.setDelay(delay);
      }
    }
    if(keyCode == KeyEvent.VK_U) {
    	if(curPiece.canReflect(grid)) {
    		curPiece.reflectPiece();
    	}
    }
    if (keyCode == KeyEvent.VK_S) {
      delay++;
      timer.setDelay(delay);
    }
    if (keyCode == KeyEvent.VK_Y) {
    	drawOn=!drawOn;
      }
    
    if (keyCode == KeyEvent.VK_T){
    	ai.spectatorMode = !ai.spectatorMode;
    }
    if(keyCode == KeyEvent.VK_P) {
    	curPiece.toggleFade();
    }
    if (!aiOn && !isGameOver()) {
      if (keyCode == KeyEvent.VK_LEFT) {
        attemptMove(4);
      } else if (keyCode == KeyEvent.VK_RIGHT) {
        attemptMove(6);
      } else if (keyCode == KeyEvent.VK_UP) {
        attemptMove(2);
      } else if (keyCode == KeyEvent.VK_DOWN) {
        attemptMove(8);
      } else if (keyCode == KeyEvent.VK_SPACE) {
        attemptMove(0);
      } else if (keyCode == KeyEvent.VK_NUMPAD5) {
    	  shiftBoard(grid);
      }
    }
    if (keyCode == KeyEvent.VK_R && isGameOver() == true) {
      init();
    }
    if(drawOn) repaint();
  }

  @Override
  public void keyReleased(KeyEvent e) {
	  /**
	   * this method is self explaining, so only 50% of its space will be this block comment
	   */
	int keyCode = e.getKeyCode();
    keyReady = true;
  }

  // !!!!!!!! DO NOT MODIFY CODE BELOW THIS LINE !!!!!!!!
  // !!!!!!!! DO NOT MODIFY CODE BELOW THIS LINE !!!!!!!!
  @Override
  public void keyTyped(KeyEvent e) {
	  /**
	   * leave empty .. needed for KeyListener interface
	   */
    // leave empty .. needed for KeyListener interface
  }

  /**
   * Called automatically after a repaint request<br>
   * THIS METHOD NEED NOT BE MODIFIED! ..
   */
  public synchronized void paint(Graphics g) {
	  /** 
	   * the standard paint method that updates the screen. Synchronized to avoid a race condition
	   */
		draw((Graphics2D) offScreenGraphics);
		   g.drawImage(offScreenBuffer, 0, 0, this);
  }
  
  public int linesCleared() {
	  return linesCleared[0];
  }
  
  public boolean isGameOver() {
		return gameOver;
	}
	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}
	public boolean isAiOn() {
		return aiOn;
	}
	public void setAi(boolean what) {
		aiOn=what;
	}
	
	 public void setEvalFunc(double holeP, double heightP, double lineB,
			  double valleyP, double blockadeP) {
		  ai.setEvalFunction(holeP, heightP, lineB, valleyP, blockadeP);
	  }

  /**
   * main() is needed to initialize the window.<br>
   * THIS METHOD NEED NOT BE MODIFIED! .. <br>
   * you should write all necessary initialization code in initRound()
   */
  public static void main(String[] args) {
    JFrame window = new JFrame("Tetris .. AI .. Machine Learning");
    window.setBounds(100, 100, 800, 600);
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setResizable(false);

    TetrisAi7BagPiece tetris = new TetrisAi7BagPiece();
    window.getContentPane().add(tetris);
    window.setBackground(new Color(240, 240, 240));
    window.setVisible(true);
    tetris.init();
    window.addKeyListener(tetris);
  }
  
  
}// end class TetrisAI
