package com.rubyhuntersky.peregrine.exception;

import android.content.Context;

import com.rubyhuntersky.peregrine.oauth.OauthAppToken;
import com.rubyhuntersky.peregrine.model.Storage;

/**
 * @author wehjin
 * @since 11/21/15.
 */

public class ProductionStorage extends Storage {
    public ProductionStorage(Context context, OauthAppToken oauthAppToken) {
        super(context, "prod", oauthAppToken);
    }
}
