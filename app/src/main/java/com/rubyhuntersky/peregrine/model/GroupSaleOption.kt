package com.rubyhuntersky.peregrine.model

import android.os.Parcel
import com.rubyhuntersky.peregrine.utility.DefaultParcelable
import com.rubyhuntersky.peregrine.utility.DefaultParcelable.Companion.generateCreator
import com.rubyhuntersky.peregrine.utility.read
import com.rubyhuntersky.peregrine.utility.write
import java.math.BigDecimal

data class GroupSaleOption(val assetName: String = "-",
                           val assetPrice: BigDecimal = BigDecimal.ONE,
                           val accountSaleOptions: List<AccountSaleOption>) : DefaultParcelable {

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.write(assetName, assetPrice, accountSaleOptions)
    }

    companion object {
        @Suppress("unused")
        @JvmStatic val CREATOR = generateCreator {
            GroupSaleOption(it.read(), it.read(), it.read())
        }
    }
}
