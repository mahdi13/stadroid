package io.stacrypt.stadroid.market

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.Order
import kotlinx.android.synthetic.main.fragment_my_active_orders.view.*
import kotlinx.android.synthetic.main.fragment_my_active_orders_list.view.*
import java.text.SimpleDateFormat

class MyActiveOrdersFragment : Fragment() {

    private lateinit var viewModel: MarketViewModel
    private lateinit var adapter: MyActiveOrdersRecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_my_active_orders_list, container, false)
        val recyclerView = rootView.list

        adapter = MyActiveOrdersRecyclerView(ArrayList())
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(MarketViewModel::class.java)
        viewModel.myActiveOrders.observe(this,
            Observer<List<Order>> { mines ->
                adapter.items = mines
                adapter.notifyDataSetChanged()
            })

    }
}

class MyActiveOrdersRecyclerView(
    var items: List<Order>
) : RecyclerView.Adapter<MyActiveOrdersRecyclerView.ViewHolder>() {

    private val sdf = SimpleDateFormat("dd-MM hh:mm")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_my_active_orders, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.priceView.text = item.price.toString()
        holder.amountView.text = item.amount.toString()
//        holder.feeView.text = item.fee
//        holder.dateView.text = sdf.format(item.time)
//
//        holder.statusView.text = "Active"
//        holder.statusView.textColorResource = R.color.greenLight
//
//        if (item.side == "1") {
//            holder.sideView.text = "Bye"
//            holder.sideView.textColorResource = R.color.greenLight
//        } else {
//            holder.sideView.text = "Sell"
//            holder.sideView.textColorResource = R.color.redLight
//        }
//
//        if (item.role == "1") {
//            holder.roleView.text = "Maker"
//        } else {
//            holder.roleView.text = "Taker"
//        }

    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val priceView: TextView = mView.price
        val amountView: TextView = mView.amount
//        val feeView: TextView = mView.fee
//        val statusView: TextView = mView.status
//        val dateView: TextView = mView.date
//        val editView: TextView = mView.edit
//        val roleView: TextView = mView.role
//        val sideView: TextView = mView.side
    }
}
