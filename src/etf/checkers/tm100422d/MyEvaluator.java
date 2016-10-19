package etf.checkers.tm100422d;

import etf.checkers.Utils;

public class MyEvaluator {

	public int evaluate(int[] bs, int side) {
		
		int a = Utils.scoreBoardState(bs);
		if (Utils.isForcedJump(bs, side))
			a++;
		return a;
	}

}
