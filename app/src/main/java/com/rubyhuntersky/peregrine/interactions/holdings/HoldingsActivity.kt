package com.rubyhuntersky.peregrine.interactions.holdings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.rubyhuntersky.peregrine.R
import kotlinx.android.synthetic.main.activity_holdings.*

class HoldingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_holdings)
        setSupportActionBar(toolbar)
    }

    override fun onStart() {
        super.onStart()
        loadingTextView.visibility = View.VISIBLE
        addHoldingFrameLayout.visibility = View.GONE
        addHoldingButton.setOnClickListener {
            addHoldingFrameLayout.visibility = View.GONE
        }
    }
}
