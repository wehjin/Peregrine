package com.rubyhuntersky.peregrine.ui


import android.os.Bundle
import android.view.*
import com.rubyhuntersky.peregrine.R
import com.rubyhuntersky.peregrine.exception.NotStoredException
import com.rubyhuntersky.peregrine.model.AllAccounts
import com.rubyhuntersky.peregrine.utility.toCurrencyDisplayString
import kotlinx.android.synthetic.main.fragment_net_value.*
import rx.Observable
import rx.Subscription
import rx.functions.Action1


class NetValueFragment : BaseFragment() {

    private val accountsListStream: Observable<AllAccounts>
        get() = baseActivity.allAccountsStream

    private var accountsListSubscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_net_value, container, false)
    }

    override fun onResume() {
        super.onResume()
        accountsListSubscription = accountsListStream.subscribe(
                Action1<AllAccounts> { allAccounts ->
                    val accountCount = (allAccounts?.accounts?.size ?: 0).toLong()
                    val netWorth = allAccounts?.netWorth
                    textview_net_worth.text = netWorth?.toCurrencyDisplayString() ?: getString(R.string.no_data)
                    textview_refresh_time.text = String.format("%d", accountCount)
                },
                Action1<Throwable> { throwable ->
                    if (throwable is NotStoredException) {
                        return@Action1
                    }
                    errorAction.call(throwable)
                }
        )
    }

    override fun onPause() {
        accountsListSubscription!!.unsubscribe()
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) = inflater.inflate(R.menu.net_value, menu)


    companion object {

        val TAG = NetValueFragment::class.java.simpleName
    }
}
