package com.rubyhuntersky.peregrine.interactions.holdings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rubyhuntersky.peregrine.R
import com.rubyhuntersky.peregrine.data.Databook
import com.rubyhuntersky.peregrine.interactions.newholding.NewHoldingCatalyst
import kotlinx.android.synthetic.main.activity_holdings.*
import kotlinx.android.synthetic.main.activity_holdings.view.*
import kotlinx.android.synthetic.main.listitem_offlineholding.view.*
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
        holdingsRecyclerView.layoutManager = LinearLayoutManager(this)
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

            is HoldingsReactor.State.Loaded -> holdingsRecyclerView.apply {

                class HoldingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

                adapter = object : RecyclerView.Adapter<HoldingViewHolder>() {
                    private val lots = state.offlineInventory.lots

                    override fun getItemCount(): Int = lots.size

                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoldingViewHolder {
                        val itemView = LayoutInflater.from(context).inflate(R.layout.listitem_offlineholding, parent, false)
                        return HoldingViewHolder(itemView)
                    }

                    override fun onBindViewHolder(viewHolder: HoldingViewHolder, position: Int) {
                        val lot = lots[position]
                        viewHolder.itemView.line1TextView.text = lot.symbol.toString()
                        viewHolder.itemView.line2TextView.text = "${lot.shareCount} shares"
                    }
                }
            }
        }
        setVisibleView(visibleView)
    }

    private fun setVisibleView(visibleView: View) {
        listOf(loadingTextView, addHoldingFrameLayout, holdingsRecyclerView)
                .forEach { view ->
                    view.visibility = if (view == visibleView) View.VISIBLE else View.GONE
                }

        if (visibleView == holdingsRecyclerView) {
            plusFab.setOnClickListener {
                reactor.perform(HoldingsReactor.Action.AddHolding)
            }
            plusFab.show()
        } else {
            plusFab.hide()
        }
    }

    override fun onStop() {
        reactorSubscription?.unsubscribe()
        super.onStop()
    }
}
