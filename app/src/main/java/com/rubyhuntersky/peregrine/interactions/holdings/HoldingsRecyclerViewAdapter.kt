package com.rubyhuntersky.peregrine.interactions.holdings

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rubyhuntersky.peregrine.R
import com.rubyhuntersky.peregrine.data.OfflineLot
import kotlinx.android.synthetic.main.listitem_offlineholding.view.*

class HoldingsRecyclerViewAdapter : RecyclerView.Adapter<HoldingsRecyclerViewAdapter.HoldingViewHolder>() {

    class HoldingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var lots = emptyList<OfflineLot>()

    fun setLots(lots: List<OfflineLot>) {
        this.lots = lots
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = lots.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoldingViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.listitem_offlineholding, parent, false)
        return HoldingViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: HoldingViewHolder, position: Int) {
        val lot = lots[position]
        viewHolder.itemView.tag = position
        viewHolder.itemView.line1TextView.text = lot.symbol.toString()
        viewHolder.itemView.line2TextView.text = "${lot.shareCount} shares"
        viewHolder.itemView.nearLinearLayout.translationX = 0f
    }
}