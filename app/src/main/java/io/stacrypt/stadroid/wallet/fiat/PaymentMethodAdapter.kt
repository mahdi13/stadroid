package io.stacrypt.stadroid.wallet.fiat

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.PaymentMethod
import kotlinx.android.synthetic.main.row_payment_gateway.view.*
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.layoutInflater

class PaymentMethodAdapter(val items: List<PaymentMethod>, val onSelected: (PaymentMethod) -> Unit) : BaseAdapter() {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val item = getItem(position)

        val view = parent!!.context.layoutInflater.inflate(R.layout.row_payment_gateway, parent, false)
        view.name.text = item.gateway
        view.icon.imageResource = when (item.fiatSymbol) {
            "IRR" -> R.drawable.shetab
            "TIRR" -> R.drawable.shetab
            else -> R.drawable.ic_launcher_foreground
        }

        view.setOnClickListener {
            view.check.isChecked = true
            onSelected.invoke(item)
        }

        return view
    }

    override fun getItem(position: Int): PaymentMethod = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = items.size
}