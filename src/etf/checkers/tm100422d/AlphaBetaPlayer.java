
/* Don't forget to change this line to etf.checkers.<username> */
package etf.checkers.tm100422d;

import java.util.*;

import etf.checkers.*;

/*
 * This is a skeleton for an alpha beta checkers player. Please copy this file
 * into your own directory, i.e. src/<username>/, and change the package 
 * declaration at the top to read
 *     package etf.checkers.<username>;
 * , where <username> is your eStudent login.
 * @Author: DD
 */
/** This is a skeleton for an alpha beta checkers player. */
public class AlphaBetaPlayer extends CheckersPlayer implements GradedCheckersPlayer
{
    /** The number of pruned subtrees for the most recent deepening iteration. */
    protected int pruneCount;
    protected Evaluator sbe;
    
    protected Stack<Integer> revertStack;
    protected int [] bs;
    protected int LastPrunedNodeScore;
    protected Move bestMove;
    protected int intBestMove;

    public AlphaBetaPlayer(String name, int side)
    { 
        super(name, side);
        // Use SimpleEvaluator to score terminal nodes
        sbe = new SimpleEvaluator();
        pruneCount=0;
    }

    public void calculateMove(int[] bs)
    {
        /* Remember to stop expanding after reaching depthLimit */
        /* Also, remember to count the number of pruned subtrees. */
    	this.bs=bs.clone();
    	int a = 0;
    	 
    	for (int dl=2; dl<=getDepthLimit(0); dl++) 
    		 a = alphabeta(Utils.getAllPossibleMoves(bs, side), -1000, 1000, 1, side, 0, dl);
    	 
    	 if (a>intBestMove)
    		 bestMove=chosenMove;
    	 
    	 if (Utils.verbose) {
    		 System.out.println(bestMove.toString() + ", " + intBestMove);
    		 System.out.println(getPruneCount());
    		 System.out.println(getLastPrunedNodeScore());    		 
    	 }
    	 
    }

    public int getPruneCount()
    {
        return pruneCount;
    }
    

	@Override
	public int getLastPrunedNodeScore() {
		// TODO Auto-generated method stub
		return LastPrunedNodeScore;
	}
	
	public int alphabeta(List<Move> moveList, int alpha, int beta, int max, int side, int t, int dl) {
		
		if (moveList.size() == 0 ||  max==dl) { 
			int ret = sbe.evaluate(bs);
			//System.out.println(ret);
			if (side==0)
				return ret;
			else return -ret;
		}	
		int najboljaVrednost;
		if (max % 2 == 1)  //maks je ako daje ostatak
			 najboljaVrednost = -1000;
		else  najboljaVrednost = 1000;
		for (int i=0; i < moveList.size(); i++) {
			Move move = moveList.get(i);
			revertStack = Utils.execute(bs, move);
			int trVrednost = alphabeta(Utils.getAllPossibleMoves(bs, Utils.otherSide(side)), alpha, beta, max+1, Utils.otherSide(side), t+1, dl);
			
			if (max%2 == 1 && trVrednost>najboljaVrednost) {
				najboljaVrednost = trVrednost;
				if (t==0)
					setMove(move);
				if (najboljaVrednost>=beta){
					//if (Utils.verbose)
						//System.out.println(getPruneCount());
					pruneCount=0;
					for (int j=i+1; j < moveList.size();) {
						moveList.remove(j);
						pruneCount++;
					}
					Utils.revert(bs, revertStack);
					LastPrunedNodeScore=najboljaVrednost;
					return najboljaVrednost;
				}
				alpha = alpha>=najboljaVrednost ? alpha : najboljaVrednost;
			}
			else if (max%2==0 && trVrednost < najboljaVrednost){
				najboljaVrednost = trVrednost;
				if (najboljaVrednost<=alpha){
					//if (Utils.verbose)
						//System.out.println(getPruneCount());
					pruneCount=0;
					for (int j=i+1; j < moveList.size();) {
						moveList.remove(j);
						pruneCount++;
					}
					Utils.revert(bs, revertStack);
					LastPrunedNodeScore=najboljaVrednost;
					return najboljaVrednost;
				}
				beta = beta <= najboljaVrednost ? beta : najboljaVrednost;
			}
			Utils.revert(bs, revertStack);	
		}
		return najboljaVrednost;
		
	}
	
}
