package io.stacrypt.stadroid.market.myorders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.Order
import io.stacrypt.stadroid.data.stemeraldApiClient
import io.stacrypt.stadroid.market.MarketViewModel
import kotlinx.android.synthetic.main.fragment_my_orders_list.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast

class MyOrdersFragment : Fragment() {

    private lateinit var viewModel: MarketViewModel
    private lateinit var adapter: MyOrdersRecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_my_orders_list, container, false)
        val recyclerView = rootView.list

        adapter = MyOrdersRecyclerView(ArrayList()) { o ->
            alert {
                title = "You are going to cancel one of your pending orders!"
                ctx.setTheme(R.style.AlertDialogCustom)
                message =
                    "Are you sure you want to cancel you ${o.type} ${o.side} order" +
                            " with amount ${o.amount}${if (o.price != null) " and price ${o.price}" else ""}"
                positiveButton("Let's do it") {
                    GlobalScope.launch(Dispatchers.Main) {
                        try {
                            stemeraldApiClient.cancelOrder(marketName = o.market, orderId = o.id).await()
                            toast("Cancelled successfully!")
                        } catch (e: Exception) {
                            toast(R.string.problem_occurred_toast)
                            e.printStackTrace()
                        }
                        adapter.notifyItemRemoved(adapter.items.indexOf(o))
                    }
                }
                negativeButton(buttonTextResource = R.string.cancel) {}
            }.show()
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(MarketViewModel::class.java)
        viewModel.myOrders.observe(this,
            Observer<List<Order>> { mines ->
                adapter.items = mines
                adapter.notifyDataSetChanged()
            })
    }
}
