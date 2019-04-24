package io.stacrypt.stadroid.market

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.stemeraldApiClient
import io.stacrypt.stadroid.data.verboseLocalizedMessage
import io.stacrypt.stadroid.ext.calculateEstimatedFee
import io.stacrypt.stadroid.ui.format
import io.stacrypt.stadroid.ui.format10Digit
import io.stacrypt.stadroid.ui.formatForJson
import kotlinx.android.synthetic.main.new_order_fragment.view.*
import kotlinx.coroutines.*
import org.jetbrains.anko.customView
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.longToast
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import retrofit2.HttpException
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import android.app.Activity
import android.content.Context
import io.stacrypt.stadroid.ui.formatExactFinancial
import io.stacrypt.stadroid.util.NumberTextWatcherForThousand
import org.jetbrains.anko.design.snackbar
import java.math.RoundingMode

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

        viewModel.quoteBalance.observe(viewLifecycleOwner, Observer {})
        viewModel.baseBalance.observe(viewLifecycleOwner, Observer {})

        viewModel.newOrderPrice.observe(viewLifecycleOwner, Observer {
            if (viewModel.newOrderType.value == "limit" && it != null && viewModel.quoteCurrency.value != null)
                rootView.price.setText(it.format(viewModel.quoteCurrency.value!!))
        })

        rootView.price.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) syncEdittextValues()
        }

        viewModel.newOrderAmount.observe(viewLifecycleOwner, Observer {
            if (viewModel.baseCurrency.value != null)
                rootView.amount.setText((it ?: BigDecimal.ZERO).format(viewModel.baseCurrency.value!!))
        })

        rootView.amount.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) syncEdittextValues()
        }

        viewModel.newOrderType.observe(viewLifecycleOwner, Observer {
            listOf(rootView.price, rootView.price_up, rootView.price_down).forEach { v ->
                v.isEnabled = it != "market"
//                v.visibility = if (it != "market") View.VISIBLE else View.GONE
            }

            viewModel.newOrderPrice.postValue(viewModel.last.value?.price)

            if (viewModel.quoteCurrency.value != null)
                rootView.price.setText(viewModel.last.value?.price?.format(viewModel.quoteCurrency.value!!))
//            if (it == "limit") rootView.price.setText(viewModel.newOrderPrice.value?.format10Digit())
//            else rootView.price.setText(viewModel.last.value?.price?.format10Digit())
        })

        listOf(
            rootView.price_up,
            rootView.price_down,
            rootView.amount_up,
            rootView.amount_down
        ).forEach {
            it.setOnLongClickListener { v ->
                syncEdittextValues()
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

        // listOf(rootView.price, rootView.amount).forEach { view ->
        //     view.setOnEditorActionListener { v, actionId, event ->
        //         if (listOf(EditorInfo.IME_ACTION_DONE).contains(actionId)) {
        //             // hideKeyboardFrom(context!!, view)
        //             // (getSystemService(
        //             //     context!!,
        //             //     InputMethodService::class.java
        //             // ) as InputMethodManager?)?.hideSoftInputFromWindow(
        //             //     this@NewOrderFragment.activity?.currentFocus?.windowToken,
        //             //     0
        //             // )
        //             false
        //         } else {
        //             true
        //         }
        //     }
        // }

        rootView.price_up.setOnClickListener {
            syncEdittextValues()
            viewModel.newOrderPrice.value = viewModel.newOrderPrice.value?.plus(priceTick)
        }

        rootView.amount_up.setOnClickListener {
            syncEdittextValues()
            viewModel.newOrderAmount.value = viewModel.newOrderAmount.value?.plus(amountTick)
        }

        rootView.price_down.setOnClickListener {
            syncEdittextValues()
            viewModel.newOrderPrice.value = viewModel.newOrderPrice.value?.minus(priceTick)?.max(0.toBigDecimal())
        }

        rootView.amount_down.setOnClickListener {
            syncEdittextValues()
            viewModel.newOrderAmount.value = viewModel.newOrderAmount.value?.minus(amountTick)?.max(0.toBigDecimal())
        }

        rootView.market_order.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) viewModel.newOrderType.value = "market"
            else viewModel.newOrderType.value = "limit"
        }

        rootView.price.addTextChangedListener(NumberTextWatcherForThousand(rootView.price))
        rootView.amount.addTextChangedListener(NumberTextWatcherForThousand(rootView.amount))

        listOf(rootView.buy, rootView.sell).forEach {
            it.setOnClickListener { v ->
                // TODO: Verify the value with min/max and user's balance

                syncEdittextValues()

                val newOrderAmount = viewModel.newOrderAmount.value
                val newOrderPrice = viewModel.newOrderPrice.value
                val newOrderType = viewModel.newOrderType.value!!
                val newOrderSide = if (v.id == R.id.buy) "buy" else "sell"
                val newOrderMarketName = viewModel.marketName

                if (validateNewOrder(
                        type = newOrderType,
                        amount = newOrderAmount,
                        price = newOrderPrice,
                        side = newOrderSide
                    )
                ) alert {
                    title = "Review your order"

                    val newOrderEstimateFee = viewModel.market.value?.calculateEstimatedFee(
                        type = newOrderType,
                        side = newOrderSide,
                        amount = newOrderAmount!!,
                        price = newOrderPrice!!,
                        lastPrice = viewModel.last.value!!
                    )

                    customView {
                        verticalLayout {
                            textView("Action: $newOrderSide") {
                                gravity = Gravity.CENTER
                            }
                            textView("Order Type: $newOrderType") {
                                gravity = Gravity.CENTER
                            }
                            textView("Estimated fee: ${newOrderEstimateFee?.format(viewModel.quoteCurrency.value!!)!!} ${viewModel.quoteCurrency.value!!.symbol}") {
                                gravity = Gravity.CENTER
                            }
                            textView("Amount: ${newOrderAmount?.format(viewModel.baseCurrency.value!!)!!} ${viewModel.baseCurrency.value!!.symbol}") {
                                gravity = Gravity.CENTER
                            }
                            if (newOrderType == "limit")
                                textView("Price: ${newOrderPrice?.format(viewModel.quoteCurrency.value!!)!!} ${viewModel.quoteCurrency.value!!.symbol}") {
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
                                    amount = newOrderAmount?.formatForJson(viewModel.baseCurrency.value!!)!!,
                                    price = newOrderPrice?.formatForJson(viewModel.quoteCurrency.value!!)!!,
                                    marketName = newOrderMarketName,
                                    side = newOrderSide
                                )
                            else
                                stemeraldApiClient.putMarketOrder(
                                    amount = newOrderAmount?.formatForJson(viewModel.baseCurrency.value!!)!!,
                                    marketName = newOrderMarketName,
                                    side = newOrderSide
                                )

                            try {
                                val newOrder = request.await()
                                view?.snackbar("Your order has been submitted!")
                            } catch (e: HttpException) {
                                e.printStackTrace()
                                view?.snackbar(e.verboseLocalizedMessage())
                            } catch (e: Exception) {
                                e.printStackTrace()
                                view?.snackbar(R.string.problem_occurred_toast)
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

    private fun validateNewOrder(type: String, amount: BigDecimal?, price: BigDecimal?, side: String): Boolean {
        var error: String? = null

        val market = viewModel.market.value!!

        val minAmount = when (side) {
            "buy" -> market.buyAmountMin
            "sell" -> market.sellAmountMin
            else -> throw IllegalArgumentException("Bad side value: $side")
        }

        val maxAmount = when (side) {
            "buy" -> market.buyAmountMax
            "sell" -> market.sellAmountMax
            else -> throw IllegalArgumentException("Bad side value: $side")
        }

        if (amount == null) error = "Amount not valid"
        else if (type == "limit" && (price == null || price <= BigDecimal.ZERO)) error = "Price not valid"
        else if (amount < minAmount) error = "Amount too low. Min is: ${minAmount.format10Digit()}"
        else if (maxAmount > BigDecimal.ZERO && amount > maxAmount) error =
            "Amount too high. Max is: ${maxAmount.format10Digit()}"
        else if (side == "buy" && viewModel.quoteBalance.value != null && amount.times(if (type == "market") viewModel.last.value!!.price else price!!) > viewModel.quoteBalance.value?.available)
            error =
                "You balance is not enough. You have ${viewModel.quoteBalance.value!!.available.format10Digit()} ${viewModel.quoteCurrency.value?.symbol} available"
        else if (side == "sell" && viewModel.baseBalance.value != null && amount > viewModel.baseBalance.value?.available)
            error =
                "You balance is not enough. You have ${viewModel.baseBalance.value!!.available.format10Digit()} ${viewModel.baseCurrency.value?.symbol} available"
        else if (side == "sell" && type == "limit" && viewModel.last.value != null && price!!.divide(
                viewModel.last.value!!.price,
                2,
                RoundingMode.HALF_UP
            ) <= BigDecimal(
                "0.1"
            )
        )

            error = "You price is too far from the market price, please review it. "
        else if (side == "buy" && type == "limit" && viewModel.last.value != null && price!!.divide(
                viewModel.last.value!!.price,
                2,
                RoundingMode.HALF_UP
            ) >= BigDecimal(
                "10"
            )
        )

            error = "You price is too far from the market price, please review it. "
        else return true

        view?.snackbar(error)

        return false
    }

    // private val priceTextWatcher = object : TextWatcher {
    //     override fun afterTextChanged(s: Editable?) {
    //         view?.price?.removeTextChangedListener(this)
    //
    //         val fixedString = buildFixedString(s)
    //         if (s?.toString() != fixedString?.toString()) {
    //             s?.replace(0, s.length, fixedString)
    //         }
    //
    //         view?.price?.addTextChangedListener(this)
    //     }
    //
    //     private fun buildFixedString(s: Editable?): CharSequence? = s?.trim()?.run {
    //         //            if (viewModel.newOrderType.value == "market") "≃ ${removePrefix("≃")}"
    //         if (viewModel.newOrderType.value == "market") (viewModel.last.value?.price?.format10Digit()
    //             ?: "NA")
    //         else this
    //     }
    //
    //     override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    //     }
    //
    //     override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    //     }
    // }
    //
    // private val amountTextWatcher = object : TextWatcher {
    //     override fun afterTextChanged(s: Editable?) {
    //         view?.amount?.removeTextChangedListener(this)
    //
    //         val fixedString = buildFixedString(s)
    //         if (s?.toString() != fixedString?.toString()) {
    //             s?.replace(0, s.length, fixedString)
    //         }
    //
    //         view?.amount?.addTextChangedListener(this)
    //     }
    //
    //     private fun buildFixedString(s: Editable?): CharSequence? = s?.trim()
    //
    //     override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    //     }
    //
    //     override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    //     }
    // }

    private fun syncEdittextValues() {
        viewModel.newOrderPrice.value = view?.price?.text?.toString()?.replace(",", "")?.toBigDecimalOrNull()
        viewModel.newOrderAmount.value = view?.amount?.text?.toString()?.replace(",", "")?.toBigDecimalOrNull()
    }
}

fun hideKeyboardFrom(context: Context, view: View) {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}
