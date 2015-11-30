package com.rubyhuntersky.peregrine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wehjin
 * @since 11/30/15.
 */

public class PortfolioAssets {
    private final List<Asset> assets;

    public PortfolioAssets(List<AccountAssets> accountAssetsList) {
        final List<Asset> assets = new ArrayList<>();
        if (accountAssetsList != null) {
            for (AccountAssets accountAssets : accountAssetsList) {
                assets.addAll(accountAssets.toAssetList());
            }
        }
        this.assets = assets;
    }

    public List<Asset> getAssets() {
        return assets;
    }
}
