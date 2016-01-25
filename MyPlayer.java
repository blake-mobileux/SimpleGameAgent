/*Software Engineer Student: Blake Harrison
 *Project Name: Project 2
 *Course: CS38000 Artificial Intelligence
 *Professor: Doctor Wolf
 *Due: 11/08/2012
 *Project Description: This project uses the Min-Max algorithm with or without Alpha-Beta Pruning to implement an intelligent
 *agent to play the game Othello.
 */

package harrbc02;

import othello.*;

public class MyPlayer extends AIPlayer {

	private final boolean AB_SEARCH = true;// boolean states whether Alpha-Beta
											// Pruning should be used

	private int rootDepth = 5;// Depth of root node

	// evaluation scores
	private int boardEval;// variable used in computing evaluation scores

	private int[] move = { -1, -1 };// variable is used to store the current
									// move being assessed, both in traversing
									// the Min-Max decision tree and in
									// evaluation

	private double prevMin, prevMax;// Bookkeeping for the previous min value in
									// a node. Note: the value of this variable
									// is only set when minimax is initially
									// called and is only updated in the root
									// node

	/* Weight distributions for board pieces occupied */
	private int[][] boardWeight = { { 10, -3, 5, 2, 2, 5, -3, 10 },
			{ -3, -7, -1, -1, -1, -1, -7, -3 }, { 5, -1, 1, 1, 1, 1, -1, 5 },
			{ 2, -1, 1, 1, 1, 1, -1, 2 }, { 2, -1, 1, 1, 1, 1, -1, 2 },
			{ 5, -1, 1, 1, 1, 1, -1, 5 }, { -3, -7, -1, -1, -1, -1, -7, -3 },
			{ 10, -3, 5, 2, 2, 5, -3, 10 } };

	/**
	 * Return a name for this player
	 * 
	 * @return a String
	 */
	@Override
	public String getName() {
		return "The Engine that Could";
	}

	/**
	 * Given a Board, return a move in the array bestMove.
	 */
	@Override
	public void getNextMove(Board board, int[] bestMove)
			throws IllegalCellException, IllegalMoveException {

		long[] numNodesExplored = { 0 };

		minimax(board, rootDepth, AB_SEARCH, bestMove, numNodesExplored);
	}

	/*
	 * This method begins the Min-Max search either with or without Alpha-Beta
	 * Pruning
	 */
	@Override
	public double minimax(Board board, final int depthLimit,
			final boolean useAlphaBetaPruning, int[] bestMove,
			long[] numNodesExplored) {

		rootDepth = depthLimit;

		move[0] = -1;
		move[1] = -1;

		/*
		 * If the current player is black call maxIf the current player is white
		 * call minOtherwise, call empty player
		 */
		if (board.getPlayer() == Board.BLACK) {

			prevMax = Double.NEGATIVE_INFINITY;

			return max(board, depthLimit, useAlphaBetaPruning, bestMove,
					Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
					numNodesExplored);

		} else if (board.getPlayer() == Board.WHITE) {

			prevMin = Double.POSITIVE_INFINITY;

			return min(board, depthLimit, useAlphaBetaPruning, bestMove,
					Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
					numNodesExplored);

		} else {

			/* for finished game */
			numNodesExplored[0]++;
			return evaluate(board);

		}

	}

	/* Utility function returns the min minimax score */
	private double min(Board board, long depthLimit,
			final boolean useAlphaBetaPruning, int[] bestMove, double alpha,
			double beta, long[] numNodesExplored) {

		/* Update nodes explored */
		numNodesExplored[0]++;

		/* Evaluation test */
		if (board.getPlayer() == Board.EMPTY || depthLimit == 0) {

			return evaluate(board);

		}

		double min = Double.POSITIVE_INFINITY;

		/* Loop through all positions on the board */
		for (int c = 0; c < 8; c++) {
			for (int i = 0; i < 8; i++) {

				move[0] = c;
				move[1] = i;

				/* Check if a position is a legal move for the current player */
				if (board.isLegalMove(move)) {

					/* copy board */
					Board boardCopy = board.getClone();

					try {
						boardCopy.makeMove(move);
					} catch (IllegalMoveException e) {

						e.printStackTrace();

					}

					if (boardCopy.getPlayer() == Board.BLACK) {
						min = Math.min(
								max(boardCopy, depthLimit - 1,
										useAlphaBetaPruning, bestMove, alpha,
										beta, numNodesExplored), min);
					} else if (boardCopy.getPlayer() == Board.WHITE) {

						min = Math.min(
								min(boardCopy, depthLimit - 1,
										useAlphaBetaPruning, bestMove, alpha,
										beta, numNodesExplored), min);

					} else {

						min = Math.min(
								max(boardCopy, depthLimit - 1,
										useAlphaBetaPruning, bestMove, alpha,
										beta, numNodesExplored), min);

					}

					move[0] = c;
					move[1] = i;

					/*
					 * If the current node is the root node and the current min
					 * is the minimum value encountered thus farupdate bestMove
					 */
					if ((depthLimit == rootDepth) && min < prevMin) {

						prevMin = min;

						/* Update best move */
						bestMove[0] = move[0];
						bestMove[1] = move[1];

					}

					/* Option to do Alpha Beta Pruning */
					if (useAlphaBetaPruning) {

						if (min <= alpha) {

							return min;

						}

						if (min < beta) {

							beta = min;
						}

					}
				}
			}
		}
		return min;
	}

	/* Utility function returns the max minimax score */
	private double max(Board board, long depthLimit,
			final boolean useAlphaBetaPruning, int[] bestMove, double alpha,
			double beta, long[] numNodesExplored) {

		/* Update nodes explored */
		numNodesExplored[0]++;

		/* Evaluation test */
		if (board.getPlayer() == Board.EMPTY || depthLimit == 0) {

			return evaluate(board);

		}

		double max = Double.NEGATIVE_INFINITY;

		/* Loop through all positions on the board */
		for (int c = 0; c < 8; c++) {
			for (int i = 0; i < 8; i++) {

				move[0] = c;
				move[1] = i;

				/* Check if a position is a legal move for the current player */
				if (board.isLegalMove(move)) {

					/* copy board */
					Board boardCopy = board.getClone();

					try {
						boardCopy.makeMove(move);
					} catch (IllegalMoveException e) {

						e.printStackTrace();

					}

					if (boardCopy.getPlayer() == Board.WHITE) {
						max = Math.max(
								min(boardCopy, depthLimit - 1,
										useAlphaBetaPruning, bestMove, alpha,
										beta, numNodesExplored), max);
					} else if (boardCopy.getPlayer() == Board.BLACK) {

						max = Math.max(
								max(boardCopy, depthLimit - 1,
										useAlphaBetaPruning, bestMove, alpha,
										beta, numNodesExplored), max);
					} else {

						max = Math.max(
								min(boardCopy, depthLimit - 1,
										useAlphaBetaPruning, bestMove, alpha,
										beta, numNodesExplored), max);

					}

					move[0] = c;
					move[1] = i;

					/*
					 * If the current node is the root node and the current min
					 * is the minimum value encountered thus farupdate bestMove
					 */
					if ((depthLimit == rootDepth) && max > prevMax) {

						prevMax = max;

						/* Update best move */
						bestMove[0] = move[0];
						bestMove[1] = move[1];

					}

					/* Option to do Alpha Beta Pruning */
					if (useAlphaBetaPruning) {

						if (max >= beta) {

							return max;

						}

						if (max > alpha) {

							alpha = max;
						}
					}
				}
			}
		}
		return max;
	}

	/*
	 * This function takes in a Board object as a parameter in its method
	 * signature and returns an evaluation scorefor the current state of the
	 * Board object
	 */
	@Override
	public double evaluate(Board board) {

		if (board.countCells(Board.EMPTY) <= 8) {
			return board.countCells(Board.BLACK)
					- board.countCells(Board.WHITE);
		} else {
			boardEval = 0;

			int result = 0;

			try {

				/*
				 * assign values to boardEval depending on the cells that black
				 * or white occupy values for white correspond to negative
				 * values values for black correspond to positive values
				 * different cells get different values
				 */
				for (int c = 0; c < 8; c++) {
					for (int i = 0; i < 8; i++) {

						move[0] = c;
						move[1] = i;
						result = board.getCell(move);

						if (result == Board.BLACK) {

							boardEval += boardWeight[c][i];

						} else if (result == Board.WHITE) {

							boardEval -= boardWeight[c][i];
						}
					}
				}

			} catch (IllegalCellException e) {

				e.printStackTrace();

			}
		}
		return boardEval;
	}

	/**
	 * This should return a string to indicate the versions of this player that
	 * are available for playing opponents. There should be one line for each
	 * version. Each line should contain the version number, then a space, then
	 * the number of features for that version, then a space, then the number of
	 * hidden units for the neural network of that version.
	 */
	@Override
	public String getAvailableVersions() {

		return null;
	}

	/**
	 * An alternative to the evaluate function above This function can assume
	 * that loadNN has been called first
	 */
	@Override
	public double nnEvaluate(Board board) {

		return -1;
	}

	/**
	 * This should return the number of features that this player passes into
	 * the neural network
	 */
	@Override
	public int getNumFeatures() {

		return -1;
	}

	/**
	 * This fills in featureVals with the feature values for the given board
	 */
	@Override
	public void getFeatureValues(Board b, double[] featureVals) {

	}

}