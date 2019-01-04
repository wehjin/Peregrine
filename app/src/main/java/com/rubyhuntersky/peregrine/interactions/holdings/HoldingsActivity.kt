package com.rubyhuntersky.peregrine.interactions.holdings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.rubyhuntersky.peregrine.R
import kotlinx.android.synthetic.main.activity_holdings.*
import rx.Subscription

class HoldingsActivity : AppCompatActivity() {

    private val reactor = HoldingsReactor()
    private var reactorStates: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_holdings)
        setSupportActionBar(toolbar)
    }

    override fun onStart() {
        super.onStart()
        reactorStates = reactor.states.subscribe { state ->
            when (state) {
                is HoldingsReactor.State.Loading -> {
                    setVisibleView(loadingTextView)
                }
                is HoldingsReactor.State.Empty -> {
                    setVisibleView(addHoldingFrameLayout)
                    addHoldingButton.setOnClickListener {
                    }
                }
            }
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
