package com.rubyhuntersky.peregrine.model

import android.os.Parcel
import com.rubyhuntersky.peregrine.utility.DefaultParcelable
import com.rubyhuntersky.peregrine.utility.DefaultParcelable.Companion.generateCreator
import com.rubyhuntersky.peregrine.utility.read
import com.rubyhuntersky.peregrine.utility.toLabelAndCurrencyDisplayString
import com.rubyhuntersky.peregrine.utility.write
import java.math.BigDecimal

data class AssetNamePrice
@JvmOverloads constructor(val name: String = "-", val price: BigDecimal = BigDecimal.ONE) : DefaultParcelable {

    fun toDisplayString(): String = price.toLabelAndCurrencyDisplayString(name)

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.write(name, price)
    }

    companion object {
        @Suppress("unused")
        @JvmStatic val CREATOR = generateCreator { AssetNamePrice(it.read(), it.read()) }
    }
}
