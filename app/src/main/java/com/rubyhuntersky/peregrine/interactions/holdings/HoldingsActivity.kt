package com.rubyhuntersky.peregrine.interactions.holdings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.rubyhuntersky.peregrine.R
import com.rubyhuntersky.peregrine.data.Databook
import com.rubyhuntersky.peregrine.data.OfflineLot
import com.rubyhuntersky.peregrine.interactions.newholding.NewHoldingCatalyst
import kotlinx.android.synthetic.main.activity_holdings.*
import kotlinx.android.synthetic.main.activity_holdings.view.*
import rx.Subscription

class HoldingsActivity : AppCompatActivity() {

    private val databook = Databook()
    private val newHoldingCatalyst = NewHoldingCatalyst(this, databook)
    private val reactor = HoldingsReactor(databook, newHoldingCatalyst).apply { start() }
    private var reactorSubscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_holdings)
        setSupportActionBar(toolbar)
    }

    override fun onStart() {
        super.onStart()
        reactorSubscription = reactor.states.subscribe { state -> render(state) }
    }

    private fun render(state: HoldingsReactor.State) {
        val visibleView = when (state) {
            is HoldingsReactor.State.Loading -> loadingTextView
            is HoldingsReactor.State.Empty -> {
                addHoldingFrameLayout.apply {
                    addHoldingButton.setOnClickListener {
                        reactor.perform(HoldingsReactor.Action.AddHolding)
                    }
                }
            }
            is HoldingsReactor.State.Loaded -> holdingsListView.apply {
                adapter = object : BaseAdapter() {

                    private val holdings = state.offlineInventory.lots
                    override fun getCount(): Int = holdings.size
                    override fun getItem(position: Int): OfflineLot = holdings[position]
                    override fun getItemId(position: Int): Long = position.toLong()

                    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                        val view = convertView ?: View.inflate(context, android.R.layout.simple_list_item_1, null)
                        return view.apply {
                            val holding = getItem(position)
                            findViewById<TextView>(android.R.id.text1).text = holding.toString()
                        }
                    }
                }
            }
        }
        setVisibleView(visibleView)
    }

    private fun setVisibleView(visibleView: View) {
        listOf(loadingTextView, addHoldingFrameLayout, holdingsListView)
                .forEach { view ->
                    view.visibility = if (view == visibleView) View.VISIBLE else View.GONE
                }
    }

    override fun onStop() {
        reactorSubscription?.unsubscribe()
        super.onStop()
    }
}
