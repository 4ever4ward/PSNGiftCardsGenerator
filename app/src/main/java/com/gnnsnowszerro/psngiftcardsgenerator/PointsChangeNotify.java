package com.gnnsnowszerro.psngiftcardsgenerator;

/**
 * Created by Alexandr on 19/06/2017.
 */

public interface PointsChangeNotify {
    /**
     * Currency points remaining balance changing notification,
     * this call-back will proceed in UI thread, can interactive directly with UI
     * @param pointsBalance
     *     current currency points remaining balance
     */
    public void onPointBalanceChange(int pointsBalance);
}
