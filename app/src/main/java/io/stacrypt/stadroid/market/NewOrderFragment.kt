package io.stacrypt.stadroid.market

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.ui.format10Digit
import kotlinx.android.synthetic.main.new_order_fragment.view.*
import kotlinx.coroutines.*
import java.math.BigDecimal

class NewOrderFragment : Fragment() {

    private lateinit var viewModel: MarketViewModel

    private var priceTick = "0.1".toBigDecimal()
    private var amountTick = "0.1".toBigDecimal()

    private var autoIncreaseDecreaseJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.new_order_fragment, container, false)

        viewModel = ViewModelProviders.of(activity!!).get(MarketViewModel::class.java)

        viewModel.last.observe(viewLifecycleOwner, Observer {
            if (it?.price == null) return@Observer
            if (viewModel.newOrderType.value == "market" || viewModel.newOrderPrice.value == BigDecimal("0"))
                viewModel.newOrderPrice.postValue(it.price)
        })

        viewModel.quoteCurrency.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            priceTick = BigDecimal("10").scaleByPowerOfTen(-3 - it.normalizationScale)
        })

        viewModel.baseCurrency.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            amountTick = BigDecimal("10").scaleByPowerOfTen(-3 - it.normalizationScale)
        })

        viewModel.newOrderPrice.observe(viewLifecycleOwner, Observer {
            rootView.price.setText(it.format10Digit())
        })

        viewModel.newOrderAmount.observe(viewLifecycleOwner, Observer {
            rootView.amount.setText(it.format10Digit())
        })

        viewModel.newOrderType.observe(viewLifecycleOwner, Observer {
            listOf(rootView.price, rootView.price_up, rootView.price_down).forEach { v ->
                v.isEnabled = it != "market"
//                v.visibility = if (it != "market") View.VISIBLE else View.GONE
            }

            if (it == "market") viewModel.newOrderPrice.postValue(viewModel.last.value?.price)
//            if (it == "limit") rootView.price.setText(viewModel.newOrderPrice.value?.format10Digit())
//            else rootView.price.setText(viewModel.last.value?.price?.format10Digit())

        })

        rootView.price_up.setOnClickListener {
            viewModel.newOrderPrice.value = viewModel.newOrderPrice.value?.plus(priceTick)
        }

        listOf(
            rootView.price_up,
            rootView.price_down,
            rootView.amount_up,
            rootView.amount_down
        ).forEach {
            it.setOnLongClickListener { v ->
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                autoIncreaseDecreaseJob = GlobalScope.launch(Dispatchers.IO) {
                    while (true) {
                        when (v.id) {
                            R.id.price_up -> viewModel.newOrderPrice.postValue(
                                viewModel.newOrderPrice.value?.plus(
                                    priceTick
                                )
                            )
                            R.id.price_down -> viewModel.newOrderPrice.postValue(
                                viewModel.newOrderPrice.value?.minus(
                                    priceTick
                                )?.max(0.toBigDecimal())
                            )
                            R.id.amount_up -> viewModel.newOrderAmount.postValue(
                                viewModel.newOrderAmount.value?.plus(
                                    amountTick
                                )
                            )
                            R.id.amount_down -> viewModel.newOrderAmount.postValue(
                                viewModel.newOrderAmount.value?.minus(
                                    amountTick
                                )?.max(0.toBigDecimal())
                            )
                        }

                        delay(100)
                    }
                }
                true
            }
            it.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP)
                    autoIncreaseDecreaseJob?.takeIf { j -> j.isActive }?.cancel()
                false
            }
        }

        rootView.amount_up.setOnClickListener {
            viewModel.newOrderAmount.value = viewModel.newOrderAmount.value?.plus(amountTick)
        }

        rootView.price_down.setOnClickListener {
            viewModel.newOrderPrice.value = viewModel.newOrderPrice.value?.minus(priceTick)?.max(0.toBigDecimal())
        }

        rootView.amount_down.setOnClickListener {
            viewModel.newOrderAmount.value = viewModel.newOrderAmount.value?.minus(amountTick)?.max(0.toBigDecimal())
        }

        rootView.market_order.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) viewModel.newOrderType.value = "market"
            else viewModel.newOrderType.value = "limit"
        }

        rootView.price.addTextChangedListener(priceTextWatcher)
        rootView.amount.addTextChangedListener(amountTextWatcher)

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    private val priceTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val fixedString = buildFixedString(s)
            if (s?.toString() != fixedString?.toString()) s?.replace(0, s.length, fixedString)
        }

        private fun buildFixedString(s: Editable?): CharSequence? = s?.trim()?.run {
            if (viewModel.newOrderType.value == "market") "≃ ${removePrefix("≃")}"
            else this
        }


        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }

    private val amountTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val fixedString = buildFixedString(s)
            if (s?.toString() != fixedString?.toString()) s?.replace(0, s.length, fixedString)
        }

        private fun buildFixedString(s: Editable?): CharSequence? = s?.trim()


        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

}


