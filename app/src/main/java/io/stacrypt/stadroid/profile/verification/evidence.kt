package io.stacrypt.stadroid.profile.verification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.Bindable
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModelProviders
import io.stacrypt.stadroid.data.UserRepository
import io.stacrypt.stadroid.wallet.ObservableViewModel
import java.util.*
import android.widget.DatePicker
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.InverseBindingAdapter
import androidx.lifecycle.LiveData
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.Country
import io.stacrypt.stadroid.databinding.EvidenceFormFragmentBinding
import io.stacrypt.stadroid.databinding.WithdrawFragmentBinding
import io.stacrypt.stadroid.ui.SpinnerExtensions
import io.stacrypt.stadroid.ui.SpinnerExtensions.setSpinnerEntries
import io.stacrypt.stadroid.ui.SpinnerExtensions.setSpinnerItemSelectedListener
import io.stacrypt.stadroid.ui.SpinnerExtensions.setSpinnerValue
import kotlinx.android.synthetic.main.evidence_form_fragment.view.*



class EvidenceFormViewModel : ObservableViewModel() {

    @Bindable
    val firstName = MutableLiveData<String>()
    @Bindable
    val lastName = MutableLiveData<String>()

    @Bindable
    val selectedGenderPosition = MutableLiveData<Int>()
    @Bindable
    val address = MutableLiveData<String>()

    @Bindable
    val idNumber = MutableLiveData<String>()

    @Bindable
    val countries = UserRepository.getCountries()
    @Bindable
    val selectedCountryPosition = MutableLiveData<Int>()
    @Bindable
    val states = Transformations.switchMap(selectedCountryPosition) {
        UserRepository.getStates(countries.value?.get(it)?.id ?: -1)
    }
    @Bindable
    val selectedStatePosition = MutableLiveData<Int>()
    @Bindable
    val cities = Transformations.switchMap(selectedStatePosition) {
        UserRepository.getCities(states.value?.get(it)?.id ?: -1)
    }
    @Bindable
    val selectedCityPosition = MutableLiveData<Int>()

    @Bindable
    val birthdayDay = MutableLiveData<Int>()

    @Bindable
    val birthdayMoth = MutableLiveData<Int>()

    @Bindable
    val birthdayYear = MutableLiveData<Int>()

//    companion object {
//        @JvmStatic
//        @BindingAdapter("entries")
//        fun Spinner.setEntries(entries: List<Any>?) {
//            setSpinnerEntries(entries)
//        }
//
//        @JvmStatic
//        @BindingAdapter("onItemSelected")
//        fun Spinner.setItemSelectedListener(itemSelectedListener: SpinnerExtensions.ItemSelectedListener?) {
//            setSpinnerItemSelectedListener(itemSelectedListener)
//        }
//
//        @JvmStatic
//        @BindingAdapter("newValue")
//        fun Spinner.setNewValue(newValue: Any?) {
//            setSpinnerValue(newValue)
//        }
//
//
//        @BindingAdapter(
//            value = ["android:year", "android:month", "android:day", "android:onDateChanged"],
//            requireAll = false
//        )
//        @JvmStatic
//        fun DatePicker.setBirthday(
//            year: Int, month: Int, day: Int, listener: DatePicker.OnDateChangedListener
//        ) {
//            Log.d("salam", year.toString())
//            init(
//                if (year == 0) year else year,
//                if (month == 0) month else month,
//                if (day == 0) day else dayOfMonth,
//                listener
//            )
//        }
//
//
//    }

//    @BindingAdapter("countryNames")
//    fun Spinner.setCountries(countries: List<Country>) {
//        setSpinnerEntries(countries.mapNames())
//    }

//    @Bindable
//    val birthday = MutableLiveData<Date>()


}


class EvidenceFormFragment : Fragment() {

    private lateinit var viewModel: EvidenceFormViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProviders.of(this).get(EvidenceFormViewModel::class.java)

        val binding = DataBindingUtil.inflate<EvidenceFormFragmentBinding>(
            inflater, R.layout.evidence_form_fragment, container, false
        )
        val view = binding.root
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.states.observe(viewLifecycleOwner, androidx.lifecycle.Observer { })

        return view

    }

}
