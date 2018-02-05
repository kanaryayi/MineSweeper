import sun.applet.Main;

/******************************************************************************
   Compilation:  javac Minesweeper.java
   Execution:    java Minesweeper

	Bemerkungen:
	-Der Code ist größtenteils kommentiert, aber aus Zeitgründen nicht vollständig

	Dann noch viel Spass mit dem Spiel!

 ******************************************************************************/

public class Ms {

	public final static int SPOTCOUNTFORASIDE = 10;
	
	public static Output out;
	public static Selector r;
	public static AskUser asker;
	public static String startAnswer = "ja";
	public static void main (String [] args,Output outs,AskUser askers){
		asker = askers;
		out = outs;
		/*
		Kurze Erklärung zum Vorgehen hier:
		Es wird eine boolean Ausgabe von der Methode zur Spielausführung erwartet.
		Diese dient dazu festzustellen, ob das Spiel einmal zu Ende gespielt wurde.
		Eigentlich kann man diese Variable auslassen und einfach eine Endlosschleife machen,
		dies ist nur probehalbar implementiert.
		*/
		
		while(getInputForIntroductionToGame()){					
			
			StdDraw.clear();
				
			ausführung();																		//Methode für Spielvers. 2 wird aufgerufen
				
			System.out.println("Natürlich willst du nochmal spielen, ich sehe doch wie viel Spass dir das Spiel gemacht hat"); //Spassausgabe
		}
		
		startAnswer="ja";
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public static boolean ausführung(){
		
		boolean [][] bool = new boolean [SPOTCOUNTFORASIDE+1][SPOTCOUNTFORASIDE+1];						//2-dim boolean Array mit zusätzlicher Spalte und Zeile zur 																					//Überprüfung für Spielende
		boolean [][] mirrorBool = new boolean [SPOTCOUNTFORASIDE+1][SPOTCOUNTFORASIDE+1];											
		int [][] boar = new int [SPOTCOUNTFORASIDE][SPOTCOUNTFORASIDE];									//Minesweeper-Feld mit Größe random*random 
		int [][] mirrorBoar = new int [SPOTCOUNTFORASIDE][SPOTCOUNTFORASIDE];									//Minesweeper-Feld mit Größe random*random //zufälliges Feld mit Minen wird erstellt mit Größe random² und 																					//random-vielen Minen
		boolean isWon = false;
		boolean firstRoundforCurrentGame = true;
		int uncoveredSpotCount = 0; 																//Zählvariable//Breite/Höhe des Feldes
		int minen = 10;	
		int inputSpalte=-1;																//angeklickte Spalte
		int inputZeile=-1;												//Variable zum Rechnen
		double linien = 1.0/(SPOTCOUNTFORASIDE+1);													//Variable mit Abständen zwischen Linien als Wert
		double halb = linien/2;														//Variable mit Mitte von Abstand von 2 Linien als Wert
						
		drawBoard(linien, SPOTCOUNTFORASIDE);/* lines draw*/
		boar = makeRandomBoard(SPOTCOUNTFORASIDE,SPOTCOUNTFORASIDE,minen);		
		boarMirrorEqualize(boar, mirrorBoar, SPOTCOUNTFORASIDE);
		StdDraw.show(200);
		while(mirrorBool[SPOTCOUNTFORASIDE][SPOTCOUNTFORASIDE]==false && (SPOTCOUNTFORASIDE*SPOTCOUNTFORASIDE)-(minen+uncoveredSpotCount)>0){		//Solange keine Mine geöffnet wurde oder eine nicht Mine noch nicht 																					//geöffnet wurde 
			
			int[] inputs = getInputZeileNSpalte();
			uncoveredSpotCount=0;
			inputZeile = inputs[0];
			inputSpalte = inputs[1];
			if(firstRoundforCurrentGame==true && boar[inputZeile][inputSpalte]== 1){
				firstSpotisMine(boar, mirrorBoar, mirrorBool, inputZeile, inputSpalte);
			}
			uncover(bool,boar,inputZeile,inputSpalte);
			boolMirrorEqualize(mirrorBool, bool);
			StdDraw.clear();
			drawBoard(linien, SPOTCOUNTFORASIDE);
			uncoveredSpotCount = getUncoveredSpotCount(mirrorBoar, mirrorBool, halb, linien);
			if(mirrorBool[SPOTCOUNTFORASIDE][SPOTCOUNTFORASIDE]==true){
				isWon=false;
			}
			else if((SPOTCOUNTFORASIDE*SPOTCOUNTFORASIDE)-(minen+uncoveredSpotCount)==0){
				isWon=true;
			}
			firstRoundforCurrentGame=false;
			StdDraw.show(200);
		}
		StdDraw.clear();
		drawBoard(linien, SPOTCOUNTFORASIDE);
		showAllSpotsOnBoard(mirrorBoar, halb, linien);
		writeWonOrLose(isWon);
		StdDraw.show(12000);
		
		return true;
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static void uncover(boolean [][] uncoveredBoard, int [][] minesBoard, int zeile, int spalte) {
		int nearMinesCount = countMines(minesBoard,zeile,spalte);
		if(uncoveredBoard[zeile][spalte] == true){return;}					// This method has changed by me 
		uncoveredBoard[zeile][spalte]=true;													// less line less process and its taking less time 
																							// recursive method	
		if(isMine(minesBoard,zeile,spalte)==true){											// its looking all of near 8 spots recursively 
			uncoveredBoard[SPOTCOUNTFORASIDE][SPOTCOUNTFORASIDE]=true;						// and if the parameters show a edge or side spot
		}																					// it would give a exception that "outofarraybound" and would not enter recursion again
		else if(nearMinesCount == 0 ){														 		
			try{uncover(uncoveredBoard, minesBoard, zeile-1, spalte-1);}catch(Exception e){}
			try{uncover(uncoveredBoard, minesBoard, zeile-1, spalte);}catch(Exception e){}
			try{uncover(uncoveredBoard, minesBoard, zeile-1, spalte+1);}catch(Exception e){}
			try{uncover(uncoveredBoard, minesBoard, zeile, spalte-1);}catch(Exception e){}
			try{uncover(uncoveredBoard, minesBoard, zeile, spalte+1);}catch(Exception e){}
			try{uncover(uncoveredBoard, minesBoard, zeile+1, spalte-1);}catch(Exception e){}
			try{uncover(uncoveredBoard, minesBoard, zeile+1, spalte);}catch(Exception e){}
			try{uncover(uncoveredBoard, minesBoard, zeile+1, spalte+1);}catch(Exception e){}	
		}
	
	}
		
////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean isMine(int[][] board, int zeile, int spalte) {
		    return board[zeile][spalte] == 1;
	}
	     
	public static int countMines(int[][] board, int zeile, int spalte) {
		if (board[zeile][spalte] == 1) { return -1; }
		int height = board.length;
		int width = board[0].length;
		int nearMinesCount = 0;
		for (int i=zeile-1;i<=zeile+1;i++) {
			for (int j=spalte-1;j<=spalte+1;j++) {
				if (
					i < height && i >= 0 &&  // zeile ist gueltig
					j < width && j >= 0 &&   // spalte ist gueltig
					board[i][j] == 1         // es ist eine Mine
				) { nearMinesCount++; }
			}
		}
		return nearMinesCount;
	}
	public static int[][] makeRandomBoard(int height, int width, int mines){
    	int[][] board = new int[height][width];
    	height = asker.ask();
    	width = asker.ask();
    	board[height][width]=1;
    	for (int i =1; i<mines; i++) {
    	    int[] selectedPosition = selectRandomPosition(10,10);
    /* 28*/	if(board[selectedPosition[0]][selectedPosition[1]]==1){i--;}
    /*	 */ else{board[selectedPosition[0]][selectedPosition[1]]=1;
    		}
    	}
    	return board;
  	}
	public static int[] selectRandomPosition(int height, int width) {
		 int randomSpalte = StdRandom.uniform(0,width);
		 int randomZeile = StdRandom.uniform(0,height);
		 System.out.println(randomZeile+" "+randomSpalte);
		 return new int[]{randomZeile,randomSpalte};
	}
	
	public static void drawBoard(double linien , int spotCountOnASide){
		for(int p1=0;p1<spotCountOnASide+1;p1++){
			if(p1 != spotCountOnASide){
			
				/* 28*/	StdDraw.text(p1*linien+0.05,9.5/10,Integer.toString(p1));
						StdDraw.text(9.5/10,p1*linien+0.05,Integer.toString(spotCountOnASide-p1-1));
					}// draw lines								//for-schleife die das
			for(int p2=0;p2<spotCountOnASide+1;p2++){									//Raster für das Feld zeichnet
				StdDraw.line(linien*p2,linien*p2,linien*p2,linien*p1);		//Erklärung entfällt, da zu zeitaufwändig
				StdDraw.line(linien*p1,linien*p2,linien*p2,linien*p2);		//Erklärung entfällt, da zu zeitaufwändig
			}
		}
	}
	public static void boarMirrorEqualize(int[][] boar,int[][]mirrorBoar,int spotCountOnASide){
	
		for(int t=0; t<spotCountOnASide;t++){	 // boar mirror equlize                 		//Zeilen werden durchgegangen //boarmirrorBoar equalize
			for(int s=0;s<spotCountOnASide;s++){												//Spalten werden durchgegangen
				mirrorBoar[(spotCountOnASide-1)-t][(spotCountOnASide-1)-s]=boar[t][s];						//boar-Array wird umgedreht und als boar2 gespeichert
			}
		}
	}
	
	public static void boolMirrorEqualize(boolean[][] mirrorBool,boolean[][] bool){
		
		for(int t=0; t<SPOTCOUNTFORASIDE;t++){    // bool mirror equalize
			for(int s=0;s<SPOTCOUNTFORASIDE;s++){
				mirrorBool[(SPOTCOUNTFORASIDE-1)-t][(SPOTCOUNTFORASIDE-1)-s]=bool[t][s];
			}
		}
	}
	
	public static int[] getInputZeileNSpalte(){
		
		int inputZeile,inputSpalte;		//Zeile und Spalte soll zur Eingabe des Nutzers werden
		System.out.print("Zeile: ");
		inputZeile = getInputIntegersZeileNSpalte();
		System.out.print("Spalte: ");
		inputSpalte=getInputIntegersZeileNSpalte();	
	    
		return new int[]{inputZeile,inputSpalte};
	}
	public static void showAllSpotsOnBoard(int[][] mirrorBoar,double halb,double linien){
		for(int t=0; t<SPOTCOUNTFORASIDE;t++){   // show every number on the board
			
			for(int s=0;s<SPOTCOUNTFORASIDE;s++){
				
				String sp = Integer.toString(countMines(mirrorBoar,t,s));			//hier gucken ob boar oder boar2 eingesetzt werden müssen
				StdDraw.text(1-(halb+(linien*s)+0.09),halb+(linien*t),sp);
			}
		}
	}
	public static int getUncoveredSpotCount(int[][] mirrorBoar,boolean[][] mirrorBool,double halb,double linien){
		int uncoveredSpotCount = 0;
		for(int t=0; t<SPOTCOUNTFORASIDE;t++){ 
			for(int s=0;s<SPOTCOUNTFORASIDE;s++){
				String sp = Integer.toString(countMines(mirrorBoar,t,s));					//hier gucken ob boar oder boar2 eingesetzt werden müssen
				if(mirrorBool[t][s]==true){												//wird geprüft welche Felder schon geöffnet wurden
					if(Integer.parseInt(sp)==-1){
						StdDraw.show();
		    			StdDraw.text(1-(halb+(linien*s))-0.09,halb+(linien*t),"TOD");
						mirrorBool[SPOTCOUNTFORASIDE][SPOTCOUNTFORASIDE]=true;
					}
					else{
	 					StdDraw.text(1-(halb+(linien*s))-0.09,(halb+(linien*t)),sp);
	 				}
					uncoveredSpotCount++;
				}
			}
		}
		return uncoveredSpotCount;
	}
	public static void firstSpotisMine(int[][] boar,int[][] mirrorBoar,boolean [][] bool,int inputZeile, int inputSpalte){
		
		boar[inputZeile][inputSpalte] = 0;
		int[] position = selectRandomPosition(SPOTCOUNTFORASIDE, SPOTCOUNTFORASIDE);
		while(bool[position[0]][position[1]]==true){
			position = selectRandomPosition(SPOTCOUNTFORASIDE, SPOTCOUNTFORASIDE);
		}
		boar[position[0]][position[1]] = 1;
		boarMirrorEqualize(boar, mirrorBoar, SPOTCOUNTFORASIDE);
	}
	
	public static boolean getInputForIntroductionToGame(){
		
		boolean play = false;																			//boolean-Variable spass wird erzeugt und 																												//auf false gesetzt
		
																							//String-Variable s wird erzeugt und 																												//auf einen leeren String gesetzt
		
		System.out.println("Möchten Sie beginnen: Ja? Nein?");											//Ausgabe an den Nutzer
		
		
		
		
		
		
		//s wird auf die Eingabe des Nutzers gesetzt
		
		if(startAnswer.equals("Ja") || startAnswer.equals("J") ||startAnswer.equals("j") || startAnswer.equals("y") || startAnswer.equals("ja")||startAnswer.equals("JA")){		//Für bestimmte Eingaben wird das Spiel 																											//gestartet
			
			play = true;
			
			StdDraw.show(500);																			//Animation wird gestoppt
		}																								//
		else{																							//
			
			System.out.println("Warum klickst du auf das Spiel, wenn du es nicht spielen willst?");	
		}	
		startAnswer = "n";
		return play;
	}
	public static void writeWonOrLose(boolean isWon){
		
		if(isWon){															//aus vorherigen Zeilen ergibt sich: Wird aufgerufen, wenn alle nicht-Minen 																			//geöffnet wurden
			out.println("Victory!");									//Ausgabe	
		}
		else{																//Wenn auf eine Mine gedrückt wurde
			out.println("Verkackt!");								//Ausgabe
		}
	}
	public static int getInputIntegersZeileNSpalte(){
		int wrongÝnputRoundCount=0,inputInt;																 
		try{
			inputInt=asker.ask();
/*for*/		while(inputInt>=SPOTCOUNTFORASIDE || inputInt<0){												//zeile soll aber immernoch im Array liegen
/*type of*/		wrongÝnputRoundCount++;															//k wird um eins erhöht
/*input*/		if(wrongÝnputRoundCount>3){														//wenn der Nutzer 3 falsche Eingaben macht
					out.println("Sag mal kannst du nicht lesen?");		//Spassausgabe
				}
				if(inputInt<0){
					out.println("Bitte positive integer ! nicht char oder string!! wieder eingeben");
				}
				else{out.println("Zahl zu groß, bitte neue Zahl eingeben: ");}
					//Neue Eingabe wird erwartet
				inputInt=asker.ask();											//spalte wird zur Eingabe des Nutzers
			}
		}
		catch(Exception e){
			out.println("Bitte positive integer ! nicht char oder string!! wieder eingeben");
			inputInt = getInputIntegersZeileNSpalte();
		}
		return inputInt;
	}

/*////////////////////////////////////////////////////////////////////////////////////////////
Test- und Debugging Methoden
////////////////////////////////////////////////////////////////////////////////////////////*/

	public static void output(boolean [][] b){	
		int n = b.length;		
		for (int i=0;i<n;i++) {
			for (int ko=0;ko<n;ko++) {
				System.out.print(b[i][ko]+" ");
			}
			System.out.println();		
		}
	}
	public static void intoutput(int [][] b){	
		int n = b.length;		
		for (int i=0;i<n;i++) {
			for (int ko=0;ko<n;ko++) {
				System.out.print(b[i][ko]+" ");
			}
			System.out.println();		
		}
	}
}