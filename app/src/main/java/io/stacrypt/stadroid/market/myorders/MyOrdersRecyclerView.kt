package io.stacrypt.stadroid.market.myorders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.akexorcist.roundcornerprogressbar.common.BaseRoundCornerProgressBar
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.Order
import io.stacrypt.stadroid.ui.format10Digit
import io.stacrypt.stadroid.ui.percentFrom
import kotlinx.android.synthetic.main.fragment_my_orders.view.*
import org.jetbrains.anko.textColorResource
import java.text.SimpleDateFormat
import java.util.*

class MyOrdersRecyclerView(
    var items: List<Order>,
    val onCancelClicked: (Order) -> Unit,
    var onClickListener: (Order) -> Unit
) : RecyclerView.Adapter<MyOrdersRecyclerView.ViewHolder>() {

    private val sdfDate = SimpleDateFormat("dd-MM", Locale.ENGLISH)
    private val sdfTime = SimpleDateFormat("hh:mm", Locale.ENGLISH)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_my_orders, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.priceView.text = (item.price?.format10Digit() ?: "NA")
        holder.amountView.text = item.amount.format10Digit() + " " + item.market.split("_")[1]
        holder.filledView.text = (item.filledStock ?: 0.toBigDecimal()).format10Digit() + " filled"
        holder.valueView.text = "≃ " +
            (item.price?.let { (item.price * item.amount).format10Digit() + " " + item.market.split("_")[0] }
                ?: "NA")

        holder.progressView.progress = item.filledStock?.percentFrom(item.amount)?.toFloat() ?: 0f
        when {
            item.finishedAt != null -> holder.progressView.progressColor =
                holder.progressView.context.resources.getColor(R.color.real_blue)
            item.side.toLowerCase() == "buy" -> {
                holder.progressView.progressColor = holder.progressView.context.resources.getColor(R.color.real_green)
                holder.progressView.secondaryProgressColor =
                    holder.progressView.context.resources.getColor(R.color.real_green_trans)
                holder.amountView.textColorResource = R.color.real_green
            }
            item.side.toLowerCase() == "sell" -> {
                holder.progressView.progressColor = holder.progressView.context.resources.getColor(R.color.real_red)
                holder.progressView.secondaryProgressColor =
                    holder.progressView.context.resources.getColor(R.color.real_red_trans)
                holder.amountView.textColorResource = R.color.real_red
            }
            // TODO: What about cancelled ones?
        }

        holder.createdView.text = item.createdAt?.let { sdfDate.format(it) } ?: ""
        holder.modifiedView.text = item.modifiedAt?.let { "Last Update at " + sdfTime.format(it) } ?: ""

        holder.cancelView.setOnClickListener { onCancelClicked(item) }

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

        holder.mView.setOnClickListener { onClickListener(item) }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val priceView: TextView = mView.price
        val amountView: TextView = mView.amount
        val filledView: TextView = mView.filled
        val valueView: TextView = mView.value
        val progressView: BaseRoundCornerProgressBar = mView.progress
        //        val feeView: TextView = mView.fee
//        val statusView: TextView = mView.status
        val createdView: TextView = mView.created
        val modifiedView: TextView = mView.modified
        val cancelView: ImageButton = mView.cancel
//        val editView: TextView = mView.edit
//        val roleView: TextView = mView.role
//        val sideView: TextView = mView.side
    }
}
