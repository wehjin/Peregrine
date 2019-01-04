package com.rubyhuntersky.peregrine.interactions.holdings

import android.graphics.Canvas
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import com.rubyhuntersky.peregrine.R
import com.rubyhuntersky.peregrine.data.Databook
import com.rubyhuntersky.peregrine.interactions.newholding.NewHoldingCatalyst
import kotlinx.android.synthetic.main.activity_holdings.*
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
        holdingsRecyclerView.adapter = HoldingsRecyclerViewAdapter()

        ItemTouchHelper(object : ItemTouchHelper.Callback() {

            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                return makeMovementFlags(0, ItemTouchHelper.START)
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.itemView.tag as Int
                val newPortfolio = databook.portfolio.removeOfflineLot(position)
                databook.portfolio = newPortfolio
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                viewHolder.itemView.nearLinearLayout.translationX = dX
            }
        }).attachToRecyclerView(holdingsRecyclerView)
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
                (adapter as HoldingsRecyclerViewAdapter).setLots(state.offlineInventory.lots)
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
