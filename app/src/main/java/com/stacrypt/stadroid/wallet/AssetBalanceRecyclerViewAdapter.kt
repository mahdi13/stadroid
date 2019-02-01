package com.stacrypt.stadroid.wallet

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.stacrypt.stadroid.data.Balance
import com.stacrypt.stadroid.R
import kotlinx.android.synthetic.main.fragment_asset_balance.view.*

class AssetBalanceRecyclerViewAdapter(
    var items: List<Balance>
) : RecyclerView.Adapter<AssetBalanceRecyclerViewAdapter.ViewHolder>() {

//    private val mOnClickListener: View.OnClickListener

//    init {
//        mOnClickListener = View.OnClickListener { v ->
//            val item = v.tag as DummyItem
    // Notify the active callbacks interface (the activity, if the fragment is attached to
    // one) that an item has been selected.
//            mListener?.onListFragmentInteraction(item)
//        }
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_asset_balance, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.nameView.text = item.assetName
        holder.availableView.text = item.available.toString()
        holder.freezeView.text = item.freeze.toString()

//        with(holder.view) {
//            tag = item
//            setOnClickListener(mOnClickListener)
//        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nameView: TextView = view.name
        val availableView: TextView = view.available
        val freezeView: TextView = view.freeze

    }
}
