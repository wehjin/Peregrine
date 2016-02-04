package com.rubyhuntersky.peregrine;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BuyProgramUnitTest {
    @Test
    public void sharesCount_isTotalDividedByPrice() throws Exception {
        final BigDecimal total = BigDecimal.valueOf(100);
        final AssetPrice assetPrice = new AssetPrice("asset1", BigDecimal.valueOf(25));
        final List<AssetPrice> prices = Collections.singletonList(assetPrice);
        final BuyProgram buyProgram = new BuyProgram(total, prices, 0);
        assertEquals(4, buyProgram.getSharesCount().intValue());
    }
}