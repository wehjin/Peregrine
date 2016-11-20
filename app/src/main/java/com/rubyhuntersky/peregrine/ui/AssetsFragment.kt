package com.rubyhuntersky.peregrine.ui

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.*
import android.widget.*
import com.rubyhuntersky.peregrine.R
import com.rubyhuntersky.peregrine.model.Asset
import com.rubyhuntersky.peregrine.model.Assignments
import com.rubyhuntersky.peregrine.model.PartitionList
import com.rubyhuntersky.peregrine.model.PortfolioAssets
import rx.Observable
import rx.functions.Action1

class AssetsFragment : BaseFragment() {

    data class ViewsData(val partitionList: PartitionList, val portfolioAssets: PortfolioAssets, val assignments: Assignments)

    private var textView: TextView? = null
    private var listView: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_assets, container, false)
        textView = view.findViewById(R.id.text) as TextView
        listView = view.findViewById(R.id.list) as ListView
        return view
    }

    override fun onResume() {
        super.onResume()
        Observable.combineLatest(baseActivity.partitionListStream, baseActivity.portfolioAssetsStream, storage.streamAssignments(), ::ViewsData)
                .subscribe(Action1<ViewsData> { viewsData -> updateViews(viewsData) }, errorAction)
    }

    override fun getErrorAction(): Action1<Throwable> {
        return Action1 { throwable ->
            Log.e(TAG, "Error", throwable)
            showText(throwable.message ?: "")
        }
    }

    private fun updateViews(viewsData: ViewsData) {
        val partitionList = viewsData.partitionList
        val assignments = viewsData.assignments
        val assets = viewsData.portfolioAssets.assets
        if (assets.isEmpty()) {
            showText("No data")
        } else {
            listView!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> showAssignmentDialog(assets[position], partitionList, assignments) }
            showList(getAssetsBaseAdapter(activity, viewsData.partitionList, assets, viewsData.assignments))
        }
    }

    private fun showAssignmentDialog(asset: Asset, partitionList: PartitionList,
                                     assignments: Assignments) {
        val startingIndex = 1 + getPartitionIndex(asset, partitionList, assignments)
        val endingIndex = intArrayOf(startingIndex)
        AlertDialog.Builder(activity)
                .setTitle("Assign Group")
                .setSingleChoiceItems(partitionList.toNamesArray("None"), startingIndex
                ) { dialog, which -> endingIndex[0] = which }
                .setNegativeButton("Cancel") { dialog, which -> }
                .setPositiveButton("Assign", DialogInterface.OnClickListener { dialog, which ->
                    val nextIndex = endingIndex[0]
                    if (nextIndex == startingIndex) {
                        return@OnClickListener
                    }
                    val symbol = asset.symbol
                    val partitions = partitionList.partitions
                    val nextAssignments = if (nextIndex == 0)
                        assignments.erasePartitionId(symbol)
                    else
                        assignments.setPartitionId(symbol, partitions[nextIndex - 1].id)
                    storage.writeAssignments(nextAssignments)
                }).show()
    }

    private fun showList(adapter: ListAdapter) {
        listView!!.adapter = adapter
        listView!!.visibility = View.VISIBLE
        textView!!.visibility = View.GONE
    }

    private fun showText(message: String) {
        textView!!.text = message
        textView!!.visibility = View.VISIBLE
        listView!!.visibility = View.GONE
    }

    private fun getAssetsBaseAdapter(context: Context, partitionList: PartitionList,
                                     assets: List<Asset>, assignments: Assignments): BaseAdapter {
        return object : BaseAdapter() {

            override fun getCount(): Int {
                return assets.size
            }

            override fun getItem(position: Int): Any {
                return assets[position]
            }

            override fun getItemId(position: Int): Long {
                return assets.hashCode().toLong()
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: View.inflate(context, R.layout.cell_asset, null)
                val asset = assets[position]
                val startText = view.findViewById(R.id.startText) as TextView
                val startDetailText = view.findViewById(R.id.startDetailText) as TextView
                val endText = view.findViewById(R.id.endText) as TextView
                startText.text = asset.symbol

                val partitionName = getPartitionName(asset, partitionList, assignments)
                val detailColorRes = if (partitionName == null) R.color.colorAccent else R.color.darkTextSecondary
                startDetailText.setTextColor(ContextCompat.getColor(activity, detailColorRes))
                startDetailText.text = partitionName ?: getString(R.string.unassigned)

                endText.text = UiHelper.getCurrencyDisplayString(asset.marketValue)
                return view
            }
        }
    }

    private fun getPartitionName(asset: Asset, partitionList: PartitionList, assignments: Assignments): String? {
        val partitionId = assignments.getPartitionId(asset.symbol)
        return partitionList.getName(partitionId)
    }

    private fun getPartitionIndex(asset: Asset, partitionList: PartitionList, assignments: Assignments): Int {
        return partitionList.getIndex(assignments.getPartitionId(asset.symbol))
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.assets, menu)
    }


}
