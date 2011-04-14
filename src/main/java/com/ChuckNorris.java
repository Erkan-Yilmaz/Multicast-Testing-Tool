package com;

/**
 * The one and only Chuck Norris class!
 *
 * @author Chuck Norris himself
 */
public class ChuckNorris {

    /**
     * Divides lhs by rhs. YES, THIS ONE WILL DIVIDE BY ZERO!
     * @param lhs left hand side of the operation
     * @param rhs right hand side of the operation. MAY BE ZERO!
     * @return quotient of lhs / rhs. Zero, if rhs is zero.
     */
    public static long div(long lhs, int rhs) {
        if(rhs != 0) return lhs/rhs;
        else         return 0;
    }

}
