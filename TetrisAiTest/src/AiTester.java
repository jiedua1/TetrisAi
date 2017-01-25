import java.awt.Color;
import java.util.Scanner;

import javax.swing.JFrame;

public class AiTester {
	private static TetrisAi7BagPiece game;
	private AI testAi;
	private static int numTrials;
	static TetrisAi7BagPiece tetris = new TetrisAi7BagPiece();
	
	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Please type in number of repetitions to run");
		numTrials = sc.nextInt();
		
		JFrame window = new JFrame("Tetris .. AI .. Machine Learning");
	    window.setBounds(100, 100, 800, 600);
	    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    window.setResizable(false);

	    window.getContentPane().add(tetris);
	    window.setBackground(new Color(240, 240, 240));
	    window.setVisible(true);
	    tetris.init();
	    tetris.setAi(true);
		tetris.drawOn=false;
	    window.addKeyListener(tetris);
	                             
//hole height line valley blockade
//	    for(double lineB = 0.6; lineB <= 3.0; lineB +=0.4) {
//	    	for(double vPenalty = -0.55; vPenalty >= -1; vPenalty -=0.15 ) {
//	    	testEvalFunc(-5, -0.35, lineB, vPenalty, -0.09);
//	    	testEvalFunc(-5, -0.5, lineB, vPenalty, -0.09);
//	    }
//	 }

	    testEvalFunc(-5.2, -0.5, 1, -1, 0);
	    for(double i = 0; i<5; i +=0.2) {
	    	testEvalFunc(-5.2, -0.5, i, -1, 0);
	    }
	    
	    //for(double i=-2.5; i>-7; i-=0.3) {
	    	//testEvalFunc(i, -0.5, 1, -1, 0);
	    //}
	    
	    defaultTest();
	    System.out.println("done");
		
	}
	public static void testEvalFunc(double holeP, double heightP, double lineB,
			  double valleyP, double blockadeP) {
		  int seed = 462; //used to pick random random seeds
		  
		  tetris.init(seed);
	      tetris.setAi(true);
		  tetris.drawOn=false;
		  tetris.setEvalFunc(holeP, heightP, lineB, valleyP, blockadeP);
		  System.out.println();
		  int sumLines = 0;
		  int i = 1;
		  while(i<=numTrials) {
		    	if(tetris.isGameOver()) {
		    		System.out.print(tetris.linesCleared()+ " ");
		    		sumLines+=tetris.linesCleared();
		    		tetris.init(seed+i);
		    	    tetris.setAi(true);
		    		tetris.drawOn=false;
		    		tetris.setEvalFunc(holeP, heightP, lineB, valleyP, blockadeP);
		    		i++;
				}
		    }
		  System.out.println();
		  System.out.println("Parameters: "+"holeP"+holeP + " heightP"+heightP+
				  " lineB:" + lineB + " valleyP:"+valleyP + " blockadeP" + blockadeP);
		  System.out.println("Average lines cleared: "+sumLines/numTrials +"  ||||||||||||||||||||||||||||||||||");
	  }
	
	public static void defaultTest() {
		//testEvalFunc(-5, -0.35, 2.6, -1, -0.12);
		testEvalFunc(-5.2, -0.5, 1, -1, 0);
	  }
	
}
/* 
Parameters: holeP-5.0 heightP-0.5 lineB:0.6 valleyP:-0.7000000000000001 blockadeP-0.09
Average lines cleared: 3330  ||||||||||||||||||||||||||||||||||
Parameters: holeP-5.0 heightP-0.35 lineB:0.6 valleyP:-0.8500000000000001 blockadeP-0.09
Average lines cleared: 3438  ||||||||||||||||||||||||||||||||||
Parameters: holeP-5.0 heightP-0.5 lineB:0.6 valleyP:-1.0 blockadeP-0.09
Average lines cleared: 3497  ||||||||||||||||||||||||||||||||||
Parameters: holeP-5.0 heightP-0.5 lineB:1.0 valleyP:-0.7000000000000001 blockadeP-0.09
Average lines cleared: 3405  ||||||||||||||||||||||||||||||||||
Parameters: holeP-5.0 heightP-0.35 lineB:1.0 valleyP:-0.8500000000000001 blockadeP-0.09
Average lines cleared: 3535  ||||||||||||||||||||||||||||||||||
Parameters: holeP-5.0 heightP-0.5 lineB:1.4 valleyP:-0.7000000000000001 blockadeP-0.09
Average lines cleared: 3055  ||||||||||||||||||||||||||||||||||
Parameters: holeP-5.0 heightP-0.35 lineB:1.4 valleyP:-0.8500000000000001 blockadeP-0.09
Average lines cleared: 3438  ||||||||||||||||||||||||||||||||||
Parameters: holeP-5.0 heightP-0.5 lineB:1.4 valleyP:-1.0 blockadeP-0.09
Average lines cleared: 4165  ||||||||||||||||||||||||||||||||||
Parameters: holeP-5.0 heightP-0.35 lineB:1.7999999999999998 valleyP:-0.8500000000000001 blockadeP-0.09
Average lines cleared: 3558  ||||||||||||||||||||||||||||||||||
Parameters: holeP-5.0 heightP-0.5 lineB:1.7999999999999998 valleyP:-1.0 blockadeP-0.09
Average lines cleared: 3571  ||||||||||||||||||||||||||||||||||
Parameters: holeP-5.0 heightP-0.35 lineB:2.1999999999999997 valleyP:-0.8500000000000001 blockadeP-0.09
Average lines cleared: 3421  ||||||||||||||||||||||||||||||||||
Parameters: holeP-5.0 heightP-0.5 lineB:2.1999999999999997 valleyP:-1.0 blockadeP-0.09
Average lines cleared: 3571  ||||||||||||||||||||||||||||||||||
Parameters: holeP-5.0 heightP-0.5 lineB:2.5999999999999996 valleyP:-0.7000000000000001 blockadeP-0.09
Average lines cleared: 3330  ||||||||||||||||||||||||||||||||||
Parameters: holeP-5.0 heightP-0.35 lineB:2.5999999999999996 valleyP:-0.8500000000000001 blockadeP-0.09
Average lines cleared: 3438  ||||||||||||||||||||||||||||||||||
Parameters: holeP-5.0 heightP-0.5 lineB:2.5999999999999996 valleyP:-1.0 blockadeP-0.09
Average lines cleared: 3571  ||||||||||||||||||||||||||||||||||
Parameters: holeP-5.0 heightP-0.35 lineB:2.9999999999999996 valleyP:-0.8500000000000001 blockadeP-0.09
Average lines cleared: 3438  ||||||||||||||||||||||||||||||||||
Parameters: holeP-5.0 heightP-0.5 lineB:2.9999999999999996 valleyP:-1.0 blockadeP-0.09
Average lines cleared: 3624  ||||||||||||||||||||||||||||||||||


*/