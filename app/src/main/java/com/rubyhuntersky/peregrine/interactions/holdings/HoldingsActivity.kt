package com.rubyhuntersky.peregrine.interactions.holdings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.rubyhuntersky.peregrine.R
import com.rubyhuntersky.peregrine.interactions.newholding.NewHoldingCatalyst
import kotlinx.android.synthetic.main.activity_holdings.*
import rx.Subscription

class HoldingsActivity : AppCompatActivity() {

    private val reactor = HoldingsReactor(NewHoldingCatalyst(this))
    private var reactorStates: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_holdings)
        setSupportActionBar(toolbar)
    }

    override fun onStart() {
        super.onStart()
        reactorStates = reactor.states.subscribe { state ->
            val visibleView = when (state) {
                is HoldingsReactor.State.Loading -> {
                    loadingTextView
                }
                is HoldingsReactor.State.Empty -> {
                    addHoldingButton.visibility = View.VISIBLE
                    addHoldingButton.setOnClickListener {
                        reactor.perform(HoldingsReactor.Action.AddHolding)
                    }
                    addHoldingFrameLayout
                }
            }

            setVisibleView(visibleView)
        }
    }

    private fun setVisibleView(visibleView: View) {
        listOf(loadingTextView, addHoldingFrameLayout)
                .forEach { view ->
                    view.visibility = if (view == visibleView) View.VISIBLE else View.GONE
                }
    }

    override fun onStop() {
        reactorStates?.unsubscribe()
        super.onStop()
    }
}
