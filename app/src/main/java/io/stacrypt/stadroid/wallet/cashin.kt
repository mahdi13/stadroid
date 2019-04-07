package io.stacrypt.stadroid.wallet

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.BankCard
import io.stacrypt.stadroid.profile.ProfileSettingActivity
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.ACTION_CHOOSE
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.ARG_ACTION
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.ARG_LAUNCH_MODE
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.ARG_TARGET
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.LAUNCH_MODE_DIALOG
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.RESULT_CHOOSE
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_ADD_BANK_CARD
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_BANK_CARDS
import io.stacrypt.stadroid.profile.banking.BankCardPagedAdapter
import io.stacrypt.stadroid.profile.banking.BankingRepository
import io.stacrypt.stadroid.wallet.balance.BalanceDetailActivity.Companion.ARG_ASSET
import io.stacrypt.stadroid.wallet.data.WalletRepository
import io.stacrypt.stadroid.wallet.fiat.PaymentGatewayAdapter
import kotlinx.android.synthetic.main.frgment_cashin.view.*
import org.jetbrains.anko.customView
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.longToast
import org.jetbrains.anko.support.v4.selector
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.verticalLayout

class CashinViewModel : ViewModel() {

    val fiatSymbol = MutableLiveData<String>()

    val currency = Transformations.switchMap(fiatSymbol) { WalletRepository.getCurrency(it) }
    val paymentGateways = Transformations.switchMap(fiatSymbol) { WalletRepository.getPaymentGateways(it) }
    val bankCards = Transformations.switchMap(fiatSymbol) {
        BankingRepository.getBankCards(it).pagedList
    }
}

class CashinFragment : Fragment() {
    lateinit var viewModel: CashinViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.frgment_cashin, container, false)

        viewModel = ViewModelProviders.of(this).get(CashinViewModel::class.java)

        // FIXME: Empty observer to LiveData switchMaps work fine
        viewModel.currency.observe(viewLifecycleOwner, Observer { })
        viewModel.paymentGateways.observe(viewLifecycleOwner, Observer { items ->
            view?.payment_gateways?.adapter =
                PaymentGatewayAdapter(items.filter { it.fiatSymbol == viewModel.fiatSymbol.value })
        })
        viewModel.fiatSymbol.postValue(arguments?.getString(ARG_ASSET)!!)

        // val adapter = BankCardPagedAdapter()
        // rootView.cards.adapter = adapter
        //
        // viewModel.bankCards.observe(this, Observer<PagedList<BankCard>> {
        //     adapter.submitList(it)
        // })
        // rootView.cards.layoutManager = LinearLayoutManager(activity)

        // rootView.add.setOnClickListener {
        //     startActivity<ProfileSettingActivity>(
        //         ProfileSettingActivity.ARG_TARGET to TARGET_ADD_BANK_CARD,
        //         ProfileSettingActivity.ARG_LAUNCH_MODE to LAUNCH_MODE_DIALOG
        //     )
        // }

        rootView.choose_card.setOnClickListener {
            startActivityForResult(Intent(context, ProfileSettingActivity::class.java).apply {
                putExtra(ARG_TARGET, TARGET_BANK_CARDS)
                putExtra(ARG_ACTION, ACTION_CHOOSE)
                putExtra(ARG_LAUNCH_MODE, LAUNCH_MODE_DIALOG)
            }, 0)
            // val cards = ArrayList<BankCard>().apply {
            //     BankingRepository.getBankCards(viewModel.fiatSymbol.value).pagedList.observe(
            //         viewLifecycleOwner,
            //         Observer { list ->
            //             list.iterator().forEach { card -> this.add(card) }
            //             if (this.size > 0) list.loadAround(this.size - 1)
            //         })
            // }
            // // selector("Choose a card", cards.map { it.pan }) { dialogInterface: DialogInterface, i: Int ->
            //     longToast("${i} was selected")
            //     // ctx.setTheme(R.style.AlertDialogCustom)
            //     // customView {
            //     //     verticalLayout {}.apply {
            //     //         this.addView(RecyclerView(context!!).apply {
            //     //             adapter = BankCardPagedAdapter()
            //     //             viewModel.bankCards.observe(viewLifecycleOwner, Observer<PagedList<BankCard>> {
            //     //                 (adapter as BankCardPagedAdapter).submitList(it)
            //     //             })
            //     //             this.layoutManager = LinearLayoutManager(activity)
            //     //         }, 0)
            //     //     }
            //     // }
            // }.apply {
            //
            // }
        }


        return rootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data?.getStringExtra(RESULT_CHOOSE) != null) {
            val card = Gson().fromJson(data.getStringExtra(RESULT_CHOOSE), BankCard::class.java)
            if (card != null) {
                longToast("You slected ${card.pan}")
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}