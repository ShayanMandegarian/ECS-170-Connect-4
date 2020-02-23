import java.util.*;

public class alphabeta_914100795 extends AIModule
{
	int player;
	int opponent;
	int maxDepth;
	int bestMoveSeen;
	int[] order = {3,4,2,5,1,6,0};

  public class Scores
	{
		int myThrees;
		int theirThrees;
		int myWin;
		int theirWin;

		public Scores()
		{
			myThrees = 0;
			theirThrees = 0;
			myWin = 0;
			theirWin = 0;
		}

		public String printScores()
		{
			return "myThrees: " + this.myThrees + ", theirThrees: " + this.theirThrees + ", myWin: " + this.myWin + ", theirWin: " + this.theirWin;
		}
	}

	public void getNextMove(final GameStateModule game)
	{
        player = game.getActivePlayer();
        opponent = (game.getActivePlayer() == 1?2:1);
				maxDepth = 0;
		//begin recursion
		while(!terminate){
			minimax(game, 0, player, player, opponent, Integer.MIN_VALUE, Integer.MAX_VALUE);

      	if(!terminate) {
					chosenMove = bestMoveSeen;
				}
				maxDepth++;
		}

		if(game.canMakeMove(chosenMove))
			game.makeMove(chosenMove);
	}

	private int minimax(final GameStateModule state, int depth, int playerID, int player, int opponent, int alpha, int beta) {
        if (terminate) {
					if (playerID == player) {
            return Integer.MAX_VALUE;
					}
					if (playerID == opponent) {
						return Integer.MIN_VALUE;
					}
				}
        if (depth == maxDepth) {
            return eval(state, playerID, player, opponent);
        }
        depth++;
        int value = 0;
        //max's turn
        if(playerID == player){
						for (int i: order) {
                if(state.canMakeMove(i)) {
                    state.makeMove(i);
                    value = minimax(state, depth, opponent, player, opponent, alpha, beta);
                    state.unMakeMove();
                    if (value > alpha) {
                        alpha = value;
                        if (depth == 1) { //top of recursion, make our move choice
                            bestMoveSeen = i;
                        }
                    }
										if (beta <= alpha) {
											break;
                		}
								}
            }
            return alpha;
        }

        else { //min's turn
						for (int i: order) {
                if(state.canMakeMove(i)) {
                    state.makeMove(i);
                    value = minimax(state, depth, player, player, opponent, alpha, beta);
                    state.unMakeMove();
										if (value < beta) {
											beta = value;
										}
										if (beta <= alpha) {
											break;
                		}
								}
            }
            return beta;
        }
    }

		private void checkDown(final GameStateModule state, Scores scores, int x, int y, int id) {
			int currColor = state.getAt(x,y);
			int i;

			for (i = 0; i < 4; i++) {
				if ((y - i) < 0) {
					break;
				}
				if (state.getAt(x, y-i) != currColor) {
					break;
				}
			}
			if (i < 3) {
				return;
			}
			else if (i == 3 && state.getHeightAt(x) <= state.getHeight() - 3) {
				if (currColor == id) {
					scores.myThrees += 1;
				}
				else {
					scores.theirThrees += 1;
				}
				return;
			}
			else if (i == 4 && state.canMakeMove(x)) {
				if (currColor == id) {
					scores.myWin += 1;
				}
				else {
					scores.theirWin += 1;
				}
				return;
			}
		}
		private void checkSide(final GameStateModule state, Scores scores, int x, int y, int id) {
			int currColor = state.getAt(x,y);
			int currLeft = currColor;
			int currRight = currColor;
			if (currColor == 0) {
				currLeft = state.getAt(x-1,y);
				currRight = state.getAt(x+1,y);
			}
			int i; // how many in a row to the right of x,y are of currColor
			int j;
			int k = 0; // how many in a row to the left of x,y are of currColor
			int m = 0;
			// check to the right
			for (i = 1; i < 4; i++) {
				if ((x + i+1) > state.getWidth()) {
					break;
				}
				if (state.getAt(x+i, y) != currRight && state.getAt(x+i, y) != 0) {
					break;
				}
				m++;
			}
			// check to the left
			for (j = -3; j < 0; j++) {
				if ((x + j) < 0) {
					continue;
				}
				if (state.getAt(x+j, y) != currLeft && state.getAt(x+j, y) != 0) {
					break;
				}
				k++;
			}

			// check to the left scores
			if (k == 2 && x > 2) { // 3 in a row...
				if (state.getAt(x-3, y) == 0 || state.getAt(x,y) == 0) { // ... w/ open spot
					if (currLeft == id) {
						scores.myThrees += 1;
					}
					else {
						scores.theirThrees += 1;
					}
				}
			}
			if (k == 3 && x > 3) { // 4 in a row...
				if (state.getAt(x, y) == 0 || state.getAt(x-1,y) == 0 || state.getAt(x-2,y) == 0 ||
				    state.getAt(x-3,y) == 0 || state.getAt(x-4,y) == 0) { // ... w/ open spot
					if (currLeft == id) {
						scores.myWin += 1;
					}
					else {
						scores.theirWin += 1;
					}
				}
			}

			// check to the right scores
			if (m == 2 && x < state.getWidth() - 3) { // 3 in a row not at the end...
				if (state.getAt(x+3, y) == 0 || state.getAt(x,y) == 0) { // ... w/ open spot
					if (currRight == id) {
						scores.myThrees += 1;
					}
					else {
						scores.theirThrees += 1;
					}
				}
			}
			if (m == 3 && state.canMakeMove(x+4)) { // 4 in a row not at the end
				if (state.getAt(x, y) == 0 || state.getAt(x+1,y) == 0 || state.getAt(x+2,y) == 0 ||
				    state.getAt(x+3,y) == 0 || state.getAt(x+4,y) == 0) { // ... w/ open spot
					if (currRight == id) {
						scores.myWin += 1;
					}
					else {
						scores.theirWin += 1;
					}
				}
			}

			// check middle wins
			if (((m == 2 && k == 1) || (m == 1 && k == 2)) && state.getAt(x,y) == 0) {
				if (currRight == id && currLeft == id) {
					scores.myWin += 1;
				}
				else {
					scores.theirWin += 1;
				}
			}
		}

		private void checkDiagFront(final GameStateModule state, Scores scores, int x, int y, int id) {
			int currColor = state.getAt(x,y);
			// check "front slash" diagonal
			int currFrontUp = -1;
			int currFrontDown = -1;
			if (x < state.getWidth() && y < state.getHeight()) {
				currFrontUp = state.getAt(x+1,y+1);
			}
			if (x > 0 && y > 0) {
				currFrontDown = state.getAt(x-1, y-1);
			}
				// up
				int i;
				for (i = 0; i < 4; i++) {
					if ((x + i > state.getWidth()) || (y + i > state.getHeight())) {
						break;
					}
					if (state.getAt(x+i,y+i) != currFrontUp) { //|| state.getAt(x+i,y+i) != 0) {
						break;
					}
				}
				// down
				int j;
				for (j = 0; j < 4; j++) {
					if ((x - j < 0) || (y - j < 0)) {
						break;
					}
					if (state.getAt(x-j,y-j) != currFrontDown) { //|| state.getAt(x+i,y+i) != 0) {
						break;
					}
				}
				if (currColor != 0) {
					i++;
				}
				if (i + j > 2) {
					if (currColor == 0) {
						if (i == 0) {
							if (currFrontDown == id) {
								scores.myThrees += 1;
							}
							else {
								scores.theirThrees += 1;
							}
						}
						if (j == 0) {
							if (currFrontUp == id) {
								scores.myThrees += 1;
							}
							else {
								scores.theirThrees += 1;
							}
						}
					}
					if (currColor == id) {
						scores.myThrees += 1;
					}
					else {
						scores.theirThrees += 1;
					}
				}
				if (i + j > 3) {

					if (currColor == 0) {
						if (i == 0) {
							if (currFrontDown == id) {
								scores.myWin += 1;
							}
							else {
								scores.theirWin += 1;
							}
						}
						if (j == 0) {
							if (currFrontUp == id) {
								scores.myWin += 1;
							}
							else {
								scores.theirWin += 1;
							}
						}
					if (currColor == id) {
						scores.myWin += 1;
					}
					else {
						scores.theirWin += 1;
					}
				}
			}
		}
		private void checkDiagBack(final GameStateModule state, Scores scores, int x, int y, int id) {

			// check "back slash" diagonal
			int currColor = state.getAt(x,y);
			int currBackUp = -1;
			int currBackDown = -1;
			if (x < 0 && y < 0) {
				currBackUp = state.getAt(x-1,y+1);
			}
			if (x > 0 && y > 0) {
				currBackDown = state.getAt(x+1, y-1);
			}
				// up
				int k;
				for (k = 0; k < 4; k++) {
					if ((x - k < 0 || (y + k > state.getHeight()))) {
						break;
					}
					if (state.getAt(x-k,y+k) != currBackUp) { //|| state.getAt(x+i,y+i) != 0) {
						break;
					}
				}
				// down
				int p;
				for (p = 0; p < 4; p++) {
					if ((x - p < 0) || (y - p < 0)) {
						break;
					}
					if (state.getAt(x-p,y-p) != currBackDown) { //|| state.getAt(x+i,y+i) != 0) {
						break;
					}
				}
				if (currColor != 0) {
					k++;
				}
				if (k + p > 2) {
					if (currColor == 0) {
						if (k == 0) {
							if (currBackDown == id) {
								scores.myThrees += 1;
							}
							else {
								scores.theirThrees += 1;
							}
						}
						if (p == 0) {
							if (currBackUp == id) {
								scores.myThrees += 1;
							}
							else {
								scores.theirThrees += 1;
							}
						}
					}
					if (currColor == id) {
						scores.myThrees += 1;
					}
					else {
						scores.theirThrees += 1;
					}
				}
				if (k + p > 3) {
					if (currColor == 0) {
						if (k == 0) {
							if (currBackDown == id) {
								scores.myWin += 1;
							}
							else {
								scores.theirWin += 1;
							}
						}
						if (p == 0) {
							if (currBackUp == id) {
								scores.myWin += 1;
							}
							else {
								scores.theirWin += 1;
							}
						}
					if (currColor == id) {
						scores.myWin += 1;
					}
					else {
						scores.theirWin += 1;
					}
				}
			}

		}

		private int eval(final GameStateModule state, int playerID, int player, int opponent) {
			Scores scores = new Scores();
			for(int i = 0; i < state.getWidth(); i++) {
				for (int j = 0; j < state.getHeight(); j++) {

					if (state.isGameOver()) {
						if (state.getWinner() == player) {
							scores.myWin += 90000;
						}
						else {
							scores.theirWin += 90000;
						}
					}
					if (terminate) {
						break;
					}
					checkDown(state, scores, i, j, player);
					checkSide(state, scores, i, j, player);
					checkDiagFront(state, scores, i, j, player);
					checkDiagBack(state, scores, i, j, player);

				}
			}

			return (scores.myThrees - (2 * scores.theirThrees) + (10 * (scores.myWin - (2 * scores.theirWin))));
		}


}
