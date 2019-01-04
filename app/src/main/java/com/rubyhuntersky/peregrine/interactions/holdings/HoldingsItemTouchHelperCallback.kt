package com.rubyhuntersky.peregrine.interactions.holdings

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.rubyhuntersky.peregrine.data.Databook
import kotlinx.android.synthetic.main.listitem_offlineholding.view.*

class HoldingsItemTouchHelperCallback(private val databook: Databook) : ItemTouchHelper.Callback() {

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
}