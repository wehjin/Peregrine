package com.rubyhuntersky.peregrine.exception;

import android.content.Context;

import com.rubyhuntersky.peregrine.Storage;

/**
 * @author wehjin
 * @since 11/21/15.
 */

public class ProductionStorage extends Storage {
    public ProductionStorage(Context context) {
        super(context, "prod");
    }
}
