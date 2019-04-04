package io.stacrypt.stadroid.profile.verification

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel;
import io.stacrypt.stadroid.data.Country
import io.stacrypt.stadroid.data.UserRepository
import java.util.*

class EvidenceFormViewModel : ViewModel() {

    val firstName = MutableLiveData<String>()
    val lastName = MutableLiveData<String>()

    val gender = MutableLiveData<String>()
    val address = MutableLiveData<String>()

    val countries = UserRepository.getCountries()
    val selectedCountryId = MutableLiveData<Int>()
    val states = switchMap(selectedCountryId) {
        UserRepository.getStates(it)
    }
    val selectedStateId = MutableLiveData<Int>()
    val cities = switchMap(selectedStateId) {
        UserRepository.getCities(it)
    }
    val selectedCityId = MutableLiveData<Int>()

    val birthday = MutableLiveData<Date>()

}
