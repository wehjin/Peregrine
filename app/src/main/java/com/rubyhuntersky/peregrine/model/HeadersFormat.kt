package com.rubyhuntersky.peregrine.model

import android.util.Pair
import java.net.URLDecoder

object HeadersFormat {

    @JvmStatic
    fun format(headers: List<Pair<String, String>>) = headers.joinToString(
            prefix = "{ ",
            transform = { "${it.first}=${URLDecoder.decode(it.second, "UTF-8")}" },
            postfix = " }"
    )
}