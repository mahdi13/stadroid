package io.stacrypt.stadroid.market

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.stemeraldApiClient
import io.stacrypt.stadroid.ui.format
import io.stacrypt.stadroid.ui.format10Digit
import kotlinx.android.synthetic.main.new_order_fragment.view.*
import kotlinx.coroutines.*
import org.jetbrains.anko.customView
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import java.lang.Exception
import java.math.BigDecimal

class NewOrderFragment : Fragment() {

    private lateinit var viewModel: MarketViewModel

    private var priceTick = "0.1".toBigDecimal()
    private var amountTick = "0.1".toBigDecimal()

    private var autoIncreaseDecreaseJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.new_order_fragment, container, false)

        viewModel = ViewModelProviders.of(activity!!).get(MarketViewModel::class.java)

        viewModel.last.observe(viewLifecycleOwner, Observer {
            if (it?.price == null) return@Observer
            if (viewModel.newOrderPrice.value == null || viewModel.newOrderPrice.value == BigDecimal("0"))
                viewModel.newOrderPrice.postValue(it.price)

            if (viewModel.newOrderType.value == "market")
                rootView?.price?.setText(it.price.format10Digit())
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
            if (viewModel.newOrderType.value == "limit" && it != null)
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

            viewModel.newOrderPrice.postValue(viewModel.last.value?.price)
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

        listOf(rootView.buy, rootView.sell).forEach {
            it.setOnClickListener { v ->
                // TODO: Verify the value with min/max and user's balance

                alert {
                    ctx.setTheme(R.style.AlertDialogCustom)
                    title = "Review your order"

                    val newOrderAmount = viewModel.newOrderAmount.value?.format(viewModel.baseCurrency.value!!)!!
                    val newOrderPrice = viewModel.newOrderPrice.value?.format(viewModel.quoteCurrency.value!!)!!
                    val newOrderType = viewModel.newOrderType.value!!
                    val newOrderEstimateFee = "NA" // FIXME Calculate fee
                    val newOrderSide = if (v.id == R.id.buy) "buy" else "sell"
                    val newOrderMarketName = viewModel.marketName.value!!
                    customView {
                        verticalLayout {
                            textView("Action: $newOrderSide") {
                                gravity = Gravity.CENTER
                            }
                            textView("Order Type: $newOrderType") {
                                gravity = Gravity.CENTER
                            }
                            textView("Estimated fee: $newOrderEstimateFee ${viewModel.quoteCurrency.value!!.symbol}") {
                                gravity = Gravity.CENTER
                            }
                            textView("Amount: $newOrderAmount ${viewModel.baseCurrency.value!!.symbol}") {
                                gravity = Gravity.CENTER
                            }
                            if (newOrderType == "limit")
                                textView("Price: $newOrderPrice ${viewModel.quoteCurrency.value!!.symbol}") {
                                    gravity = Gravity.CENTER
                                }
                        }
                    }
                    positiveButton("Let's do it!") {
                        val progressDialog =
                            indeterminateProgressDialog("Wait", "Putting your order...").apply { show() }
                        GlobalScope.launch(Dispatchers.Main) {
                            val request = if (newOrderType == "limit")
                                stemeraldApiClient.putLimitOrder(
                                    amount = newOrderAmount,
                                    price = newOrderPrice,
                                    marketName = newOrderMarketName,
                                    side = newOrderSide
                                )
                            else
                                stemeraldApiClient.putMarketOrder(
                                    amount = newOrderAmount,
                                    marketName = newOrderMarketName,
                                    side = newOrderSide
                                )

                            try {
                                val newOrder = request.await()
                                toast("Your order has been submitted!")
                            } catch (e: Exception) {
                                e.printStackTrace()
                                toast(R.string.problem_occurred_toast)
                            } finally {
                                progressDialog.dismiss()
                            }
                        }
                    }
                    negativeButton("Cancel") {}
                }.show()
            }
        }

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
            //            if (viewModel.newOrderType.value == "market") "≃ ${removePrefix("≃")}"
            if (viewModel.newOrderType.value == "market") (viewModel.last.value?.price?.format10Digit()
                ?: "NA")
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

