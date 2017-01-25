import java.util.Arrays;
import java.util.Random;

public class AI {
	
  Random rand;
  int left = 4; //moves piece left
  int right = 6; //moves piece right
  int rotate = 2; // rotates piece
  int slam = 0; //slams piece down
  int down = 8; // moves piece down
  
  int numRows = 20;
  int numCols = 10; // 2 is for the offset, 10 actual
  
  int bestCol = -1;
  int bestRotate = -1;
  int maxHeight = 0;
  
  //average 10500 lines 1 deep
  private double holeP = -5.2;
  private double heightP = -0.5;
  private double lineB = 1;
  private double valleyP = -1;
  private double blockadeP = 0;
  
  public boolean spectatorMode = false; //whether to go down or slam immediately
  
  
  public int makeMove(int grid[][], Piece curPiece, Piece nextPiece, int searchDepth) {
	    int moveNum = rand.nextInt(4) * 2 + 2;
	    if(searchDepth == 2) {
	    	findBestPosition(grid, curPiece, nextPiece); //sets bestCol and bestRotate\ put nextPiece for good AI
	    } else {
	    	findBestPosition(grid, curPiece);
	    }
	    	
	    if(curPiece.rotation<bestRotate) {
	    	moveNum = 2;
	    }
	    else if(curPiece.col<bestCol) {
	    	moveNum = right;
	    }
	    else if(curPiece.col>bestCol) {
	    	moveNum = left;
	    }
	    // has to check 2 squares down because sometimes piece moves down twice, due
	    //to it updating every 30 timer ticks or so automatically
	    else if(curPiece.canMoveDown(grid,2) && spectatorMode) {
	    	moveNum = down;
	    }
	    else {
	    	moveNum = slam;
	    	bestCol = -1; //resets the bestCol so therefore makeMove will reset findPosition
	    	bestRotate = -1; //resets the bestCol so therefore makeMove will reset findPosition
	    }
	    return moveNum;
  }
  public AI() {
    rand = new Random(29);// you may choose your own seed
  }
  public AI(int r, int c) {
	    rand = new Random(29);// you may choose your own seed
	    numRows = r;
	    numCols=c;
  }

  public void setEvalFunction(double holeP, double heightP, double lineB,
		  double valleyP, double blockadeP) {
	  this.holeP = holeP;
	  this.heightP = heightP;
	  this.lineB = lineB;
	  this.valleyP = valleyP;
	  this.blockadeP = blockadeP;
  }
  
    
    
//    while (curPiece.canMoveLeft(grid))
//    	curPiece.moveLeft();
//    evalBoard(grid);


    // replace this 'lamest of the lame' AI's with your own code

  public void startAi() { //needed to reset the best position function
	  bestCol = -1;
	  bestRotate = -1;
  }
  public int findMaxRow(int[][] grid) {
	  int stoppingRow = 0; //last row
	  for (int curRow = numRows; curRow > 0; curRow--) {
		  boolean rowEmpty = true;
		  for(int j = 2; j<numCols+2; j++) {
			  if(grid[curRow][j] !=0) {
				  rowEmpty = false;
				  break;
			  }
		  }
		  if(rowEmpty) {
			  stoppingRow = curRow;
			  break;
		  }	  
	  }
	  if(numRows-stoppingRow>maxHeight) {
		  maxHeight = numRows-stoppingRow;
	  }
	  return numRows-stoppingRow;
  }
  //performs a 2 deep search using curPiece AND nextPiece, does this a bit differently.
  public void findBestPosition(int[][] grid, Piece curPiece, Piece nextPiece) {
	  double bestBestEval = -10000; //best position so far
	  int bestBestCol = 5;
	  int bestBestRotate = 0;
	  if(bestCol == -1 || bestRotate == -1) {
		  findMaxRow(grid);
		  int rotateLimit;
		  //iterate through every rotation and possible position
		  switch (curPiece.id) //more efficient
		  //for certain pieces, only checks 2 rotates or 1. Sets up "rotateLimit"
		  {
		  case 1:
			  rotateLimit = 1;
			  break;
		  case 2:
			  rotateLimit = 2;
			  break;
		  case 5:
			  rotateLimit = 2;
			  break;
		  case 6:
			  rotateLimit = 2; 
			  break;
		  default:
			  rotateLimit = 4;
			  break;
		  }
		  for(int rotation = 0; rotation <rotateLimit; rotation++) {
			  Piece piece = new Piece(curPiece);
			  for(int r = 0; r<rotation; r++) {
				  piece.rotate();
			  }
			  while(piece.canMoveLeft(grid)) {
				  piece.moveLeft();
			  }
			  while(piece.canMoveRight(grid)) {
				  Piece ghostPiece = new Piece(piece, grid); 
				  int[][] tempGrid = new int[grid.length][];
				  for(int r = 0; r<grid.length; r++) {
					  tempGrid[r] = grid[r].clone();
				  }
				  ghostPiece.addToGrid(tempGrid);
				  Piece tempNext = new Piece(nextPiece);
				  tempNext.moveDown();
				  tempNext.moveDown();
				  bestCol = -1; 
				  //Resets bestCol so that the findBestPos actually works
				  bestRotate = -1;
				 //Resets bestRotate so that the findBestPos actually works
				  double bestScore = findBestPosition(tempGrid, tempNext); //"score" of the tempboard, higher is better
				  if(bestScore > bestBestEval) { 
					  bestBestEval = bestScore;
					  bestBestCol = piece.col;
					  bestBestRotate = rotation;
//					  System.out.println(bestBestCol);
//					  System.out.println(bestBestRotate);
//					  debugging code
				  }  
				  piece.moveRight();
			  } 
			  //block to move it an extra time right, since code deosn't cover
			  Piece ghostPiece = new Piece(piece, grid); 
			  int[][] tempGrid = new int[grid.length][];
			  for(int r = 0; r<grid.length; r++) {
				  tempGrid[r] = grid[r].clone();
			  }
			  ghostPiece.addToGrid(tempGrid);
			  removeCompleteRows(tempGrid);
			  Piece tempNext = new Piece(nextPiece);
			  tempNext.row += 2;
			  bestCol = -1; //resets bestCol so that findBestPosition actually executes
			  bestRotate = -1; // same thing
			  double bestScore = findBestPosition(tempGrid, tempNext); //"score" of the tempboard, higher is better
			  if(bestScore > bestBestEval) { 
				  bestBestEval = bestScore;
				  bestBestCol = ghostPiece.col;
				  bestBestRotate = rotation;
			  }  
			  piece.moveRight();
			  //extra time right
		  }	  
		  bestCol = bestBestCol;
		  //System.out.println(bestBestCol);
		  bestRotate = bestBestRotate;
		  //System.out.println(bestBestRotate);
	  }
  }
  public void removeCompleteRows(int[][] grid) {
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
	        // shift down
	        for (int i = curRow; i > 0; i--) {
	          for (int j = 2; j < numCols + 2; j++) {
	            grid[i][j] = grid[i - 1][j];
	          }
	        }
	        curRow++;// stay at current index
	      }
	    }
  }
	  
  //returns modifies bestRotate and bestCol to the specified amount
  public double findBestPosition(int[][] grid, Piece curPiece) {
	  //only do the method if the bestCol is not set or bestRotate is not set
	  double bestEval = -10000; //best position so far
	  if(bestCol == -1 || bestRotate == -1) {
		  bestCol = 5; //default in piece
		  bestRotate = 0;
		  
		  //makes code efficient, checks less rotations
		  int rotateLimit = 4;
		  switch(curPiece.id)
		  {
		  case 1:
			  rotateLimit = 1;
			  break;
		  case 2:
			  rotateLimit = 2;
			  break;
		  case 5:
			  rotateLimit = 2;
			  break;
		  case 6:
			  rotateLimit = 2; 
			  break;
		  default:
			  rotateLimit = 4;
			  break;
		  }
		  //iterate through every rotation and possible position
		  for(int rotation = 0; rotation <rotateLimit; rotation++) {
			  Piece piece = new Piece(curPiece);
			  for(int r = 0; r<rotation; r++) {
				  piece.rotate();
			  }
			  while(piece.canMoveLeft(grid)) {
				  piece.moveLeft();
			  }
			  while(piece.canMoveRight(grid)) {
				  Piece ghostPiece = new Piece(piece, grid); 
				  int[][] tempGrid = new int[grid.length][];
				  for(int r = 0; r<grid.length; r++) {
					  tempGrid[r] = grid[r].clone();
				  }
				  ghostPiece.addToGrid(tempGrid);
				  double curScore = evalBoard(tempGrid); //"score" of the tempboard, higher is better
				  if(curScore > bestEval) { 
					  bestEval = curScore;
					  bestCol = ghostPiece.col;
					  bestRotate = rotation;
				  }  
				  piece.moveRight();
			  } 
			  //block to move it an extra time right, since code deosn't cover
			  Piece ghostPiece = new Piece(piece, grid); 
			  int[][] tempGrid = new int[grid.length][];
			  for(int r = 0; r<grid.length; r++) {
				  tempGrid[r] = grid[r].clone();
			  }
			  ghostPiece.addToGrid(tempGrid);
			  double curScore = evalBoard(tempGrid); //"score" of the tempboard, higher is better
			  if(curScore > bestEval) { 
				  bestEval = curScore;
				  bestCol = ghostPiece.col;
				  bestRotate = rotation;
			  }  
			  //extra time right
			  
		  }	  
	  }
	  //System.out.println(bestEval);
	  return bestEval;
 }
  public double evalBoard(int grid[][]) {
	  return evalBoard(grid, holeP, heightP, lineB, valleyP, blockadeP);
  }
  
  public double evalBoard(int grid[][], double holeP, double heightP, double lineB, double valleyP, double blockadeP) {
	  double score = 0;
	  
	  //bestSoFar -5, -0.35, 2.6, -1 8000+ for seed 139. Averages 2000+ lines. 
	  //interesting: making linebonus negative.
	  //making lineBonus 0 is pretty interesting
	  
	  int stoppingRow = 0;
	  //each hole, surrounded by something on the top, will incur this penalty.
	  double holePenalty = holeP; //default -5
	  //Every block that has height H will incur a heightPenalty*H penalty to score
	  double heightPenalty = heightP; //default -0.35
	  //bonus per line
	  double lineBonus = lineB; //default 2.6
	  double valleyPenalty = valleyP; //default -1
	  
	  double blockadePenalty = blockadeP; //default -0.12
	  
	  //alternate values, up are default
//	  holePenalty = -4;
//	  valleyPenalty = -1.1;
	  //    *** ** 
	  //    *** **
	  //    *** **   this gets a penalty of 0.5*3 since valley of 3 deep.
	  //    *** **
	  //    ******
	 
	  
	  //this code is for efficiency, it sets the stoppingpoint to the first 
	  //empty row it finds. row stoppingrow is empty!
	  for (int curRow = numRows; curRow > 0; curRow--) {
		  boolean rowEmpty = true;
		  for(int j = 2; j<numCols+2; j++) {
			  if(grid[curRow][j] !=0) {
				  rowEmpty = false;
				  break;
			  }
		  }
		  if(rowEmpty) {
			  stoppingRow = curRow;
			  break;
		  }	  
	  }
	//code for calculating height penalty. Iterates across column, then down rows
	   for (int curRow = numRows; curRow > stoppingRow; curRow--) {
	        for (int j = 2; j < numCols+2; j++) {
	        	if(grid[curRow][j]!=0) {
	        		score += heightPenalty * (numRows-curRow); //this is because curRow 20 = row 0, curRow 19 = row 1, etc.
	        	}
	        }
	   }
	 //code for calculating linebonus
	   for (int curRow = numRows; curRow > stoppingRow; curRow--) {
	        // check if i-th row is full
		   	boolean rowFull = true;
	        for (int j = 2; j < numCols + 2; j++) {
	          if (grid[curRow][j] == 0) {
	        	rowFull = false;
	            break;
	          }
	        }
	        if(rowFull)  {
	        	score+=lineBonus;
	        }
	   }
	  //clear the full lines before calculating holes
	   removeCompleteRows(grid);
	   for(int col = 2; col<numCols + 2; col++) {
		   
		   for(int curRow = stoppingRow; curRow<=numRows; curRow++) {
			   if(grid[curRow][col] != 0){
				   curRow++;
				   while (curRow <= numRows) {
					   if(grid[curRow][col]==0) {score+=holePenalty;}
					   curRow++;
				   }
			   }
		   }
	   }
	   //code for calculating holes
	   
	   
	   //code for blockades
	   for(int col = 2; col<numCols + 2; col++) {
		   for(int curRow = numRows; curRow>=stoppingRow; curRow--) {
			   if(grid[curRow][col] == 0){
				   curRow--;
				   while (curRow >= stoppingRow) {
					   if(grid[curRow][col]!=0) score+=blockadePenalty;
					   curRow--;
				   }
			   }
		   }
	   }
//	   if(stoppingRow<numCols) { //emergency mode, when the board reaches past halfway, add punishment extra for height above that
//		   for (int curRow = 12; curRow > stoppingRow; curRow--) {
//		        for (int j = 2; j < 12; j++) {
//		        	if(grid[curRow][j]!=0) {
//		        		score += 0.4 * heightPenalty * (20-curRow); //this is because curRow 20 = row 0, curRow 19 = row 1, etc.
//		        	}
//		        }
//		   }
//	   }
//	   if(stoppingRow<9) { //PANIC MODE, when the board reaches past halfway, add punishment extra for height above that
//		   for (int curRow = 9; curRow > stoppingRow; curRow--) {
//		        for (int j = 2; j < 12; j++) {
//		        	if(grid[curRow][j]!=0) {
//		        		score += 0.4 * heightPenalty * (20-curRow); //this is because curRow 20 = row 0, curRow 19 = row 1, etc.
//		        	}
//		        }
//		   }
//	   }
	   if(stoppingRow<=6) { //PANIC MODE, when the board reaches past halfway, add punishment extra for height above that
		   for (int curRow = 6; curRow >= stoppingRow; curRow--) {
		        for (int j = 2; j < numCols + 2; j++) {
		        	if(grid[curRow][j]!=0) {
		        		score += 10 * heightPenalty * (6-curRow); //this is because curRow 20 = row 0, curRow 19 = row 1, etc.
		        	}
		        }
		   }
	   } //EMERGENCY
	   

	   //code for calculating valley penalties
	   for (int curRow = numRows; curRow > stoppingRow; curRow--) {
	        for (int j = 2; j < numCols + 2; j++) {
	        	if(grid[curRow][j]==0) {
	        		if(grid[curRow][j-1]!=0 && grid[curRow][j+1]!=0) {
	        			if(grid[curRow+1][j]==0) //empty space below too
	        				score += valleyPenalty*3; // supervallies
	        			else
	        				score += valleyPenalty;
	        		}
	        	}
	        }
	   }
		return score;
	}
}