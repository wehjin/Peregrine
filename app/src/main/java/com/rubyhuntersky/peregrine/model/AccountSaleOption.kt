package com.rubyhuntersky.peregrine.model

import android.os.Parcel
import com.rubyhuntersky.peregrine.utility.DefaultParcelable
import com.rubyhuntersky.peregrine.utility.DefaultParcelable.Companion.generateCreator
import com.rubyhuntersky.peregrine.utility.read
import com.rubyhuntersky.peregrine.utility.write
import java.math.BigDecimal

/**
 * @author Jeffrey Yu
 * @since 3/11/17.
 */

data class AccountSaleOption(val accountId: String,
                             val accountTitle: String,
                             val assetName: String,
                             val sharesAvailable: BigDecimal) : DefaultParcelable {

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.write(accountId, accountTitle, assetName, sharesAvailable)
    }

    companion object {
        @Suppress("unused")
        @JvmStatic val CREATOR = generateCreator {
            AccountSaleOption(it.read(), it.read(), it.read(), it.read())
        }
    }
}
