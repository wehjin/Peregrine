package com.rubyhuntersky.peregrine.ui

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.rubyhuntersky.peregrine.R
import com.rubyhuntersky.peregrine.model.Asset
import com.rubyhuntersky.peregrine.model.Assignments
import com.rubyhuntersky.peregrine.model.PartitionList
import com.rubyhuntersky.peregrine.model.PortfolioAssets
import rx.Observable
import rx.Observable.combineLatest
import rx.functions.Action1
import java.math.BigDecimal

class AssetsFragment : BaseFragment() {

    data class ViewsData(
            val partitionList: PartitionList,
            val portfolioAssets: PortfolioAssets,
            val assignments: Assignments
    )

    private lateinit var textView: TextView
    private lateinit var listView: ListView
    private val partitionLists: Observable<PartitionList> get() = baseActivity.partitionListStream
    private val portfolioAssets: Observable<PortfolioAssets> get() = baseActivity.portfolioAssetsStream

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
        combineLatest(partitionLists, portfolioAssets, storage.streamAssignments(), ::ViewsData)
                .subscribe(Action1 { updateViews(it) }, errorAction)
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
            listView.adapter = MyListAdapter(activity, viewsData.partitionList, assets, viewsData.assignments)
            listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> showAssignmentDialog(assets[position], partitionList, assignments) }
            listView.visibility = View.VISIBLE
            textView.visibility = View.GONE
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

    private fun getPartitionIndex(asset: Asset, partitionList: PartitionList, assignments: Assignments): Int {
        return partitionList.getIndex(assignments.getPartitionId(asset.symbol))
    }

    private fun showText(message: String) {
        textView.text = message
        textView.visibility = View.VISIBLE
        listView.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.assets, menu)
    }

    class MyListAdapter(val activity: Activity, val partitions: PartitionList, assets: List<Asset>, val assignments: Assignments) : BaseAdapter() {

        data class ItemModel(val symbol: String, val marketValue: BigDecimal, val groupName: String?)

        val List<Asset>.totalMarketValue: BigDecimal get() = map { it.marketValue!! }.reduce { a, b -> a + b }
        val String.toGroupName: String? get() = partitions.getName(assignments.getPartitionId(this))
        val BigDecimal.asCurrencyDisplayString: String get() = UiHelper.getCurrencyDisplayString(this)
        val ItemModel.groupTextColor: Int get() = ContextCompat.getColor(activity, if (groupName == null) R.color.colorAccent else R.color.darkTextSecondary)

        val itemModels: List<ItemModel> by lazy {
            assets.groupBy { it.symbol }
                    .map { ItemModel(it.key, it.value.totalMarketValue, it.key.toGroupName) }
                    .sortedBy { it.symbol }
        }

        override fun getCount(): Int = itemModels.size
        override fun getItem(position: Int): Any = itemModels[position]
        override fun getItemId(position: Int): Long = itemModels.hashCode().toLong()
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: activity.layoutInflater.inflate(R.layout.cell_asset, parent, false)
            val itemModel = itemModels[position]
            val startText = view.findViewById(R.id.startText) as TextView
            val startDetailText = view.findViewById(R.id.startDetailText) as TextView
            val endText = view.findViewById(R.id.endText) as TextView
            startText.text = itemModel.symbol
            startDetailText.text = itemModel.groupName ?: activity.getString(R.string.unassigned)
            startDetailText.setTextColor(itemModel.groupTextColor)
            endText.text = itemModel.marketValue.asCurrencyDisplayString
            return view
        }
    }
}
