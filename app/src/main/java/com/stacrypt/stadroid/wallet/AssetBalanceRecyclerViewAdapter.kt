package com.stacrypt.stadroid.wallet

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.stacrypt.stadroid.data.Balance
import com.stacrypt.stadroid.R
import kotlinx.android.synthetic.main.fragment_asset_balance.view.*
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.imageURI
import java.lang.Exception

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
        holder.totalView.text = (item.available + item.freeze).toString()
        holder.availableView.text = item.available.toString()
        holder.freezeView.text = item.freeze.toString()
        try {
            holder.iconView.imageURI =
                Uri.parse("android.resource://com.stacrypt.stadroid/drawable/ic_${item.assetName}")
        } catch (e: Exception) {
            holder.iconView.imageResource =
                arrayOf(R.drawable.ic_btc, R.drawable.ic_eth, R.drawable.ic_ltc, R.drawable.ic_xrp).random()
        }   


//        with(holder.view) {
//            tag = item
//            setOnClickListener(mOnClickListener)
//        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nameView: TextView = view.name
        val depositView: Button = view.deposit
        val withdrawView: Button = view.withdraw
        val historyView: Button = view.history
        val totalView: TextView = view.available
        val availableView: TextView = view.total
        val freezeView: TextView = view.freeze
        val iconView: ImageView = view.icon

    }
}
