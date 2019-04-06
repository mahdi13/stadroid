package io.stacrypt.stadroid.profile.verification

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Bindable
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModelProviders
import io.stacrypt.stadroid.data.UserRepository
import io.stacrypt.stadroid.wallet.ObservableViewModel
import java.util.*
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.Gender
import io.stacrypt.stadroid.data.stemeraldApiClient
import io.stacrypt.stadroid.databinding.EvidenceFormFragmentBinding
import kotlinx.android.synthetic.main.evidence_form_fragment.*
import kotlinx.android.synthetic.main.evidence_form_fragment.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File
import okhttp3.RequestBody
import okhttp3.MediaType
import org.jetbrains.anko.support.v4.longToast
import org.jetbrains.anko.support.v4.toast

const val PICK_IMAGE_ID1 = 201
const val PICK_IMAGE_ID2 = 202

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

    val idCardPath = MutableLiveData<String>()

    val idCardSecondaryPath = MutableLiveData<String>()

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
        inflater: LayoutInflater,
        container: ViewGroup?,
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

        view.id_card1.setOnClickListener {
            ImagePicker.with(this) //  Initialize ImagePicker with activity or fragment context
                .setToolbarColor("#212121")         //  Toolbar color
                .setStatusBarColor("#000000")       //  StatusBar color (works with SDK >= 21  )
                .setToolbarTextColor("#FFFFFF")     //  Toolbar text color (Title and Done button)
                .setToolbarIconColor("#FFFFFF")     //  Toolbar icon color (Back and Camera button)
                .setProgressBarColor("#4CAF50")     //  ProgressBar color
                .setBackgroundColor("#212121")      //  Background color
                .setCameraOnly(false)               //  Camera mode
                .setMultipleMode(false) //  Select multiple images or single image
                .setFolderMode(true)                //  Folder mode
                .setShowCamera(true)                //  Show camera button
                .setFolderTitle("Albums")           //  Folder title (works with FolderMode = true)
                .setImageTitle("Galleries")         //  Image title (works with FolderMode = false)
                .setDoneTitle("Done")               //  Done button title
                .setLimitMessage("You have reached selection limit") // Selection limit message
                .setMaxSize(1)                     //  Max images can be selected
                .setSavePath("ImagePicker")         //  Image capture folder name
//                .setSelectedImages(images)          //  Selected images
                .setAlwaysShowDoneButton(true)      //  Set always show done button in multiple mode
                .setRequestCode(PICK_IMAGE_ID1)                //  Set request code, default Config.RC_PICK_IMAGES
                .setKeepScreenOn(true)              //  Keep screen on when selecting images
                .start()                          //  Start ImagePicker
        }

        view.id_card2.setOnClickListener {
            ImagePicker.with(this) //  Initialize ImagePicker with activity or fragment context
                .setToolbarColor("#212121") //  Toolbar color
                .setStatusBarColor("#000000") //  StatusBar color (works with SDK >= 21  )
                .setToolbarTextColor("#FFFFFF") //  Toolbar text color (Title and Done button)
                .setToolbarIconColor("#FFFFFF") //  Toolbar icon color (Back and Camera button)
                .setProgressBarColor("#4CAF50") //  ProgressBar color
                .setBackgroundColor("#212121") //  Background color

                // FIXME: It should be enabled, but it's disable now because of an unknown bug
                .setCameraOnly(false) //  Camera mode

                .setMultipleMode(false) //  Select multiple images or single image
                .setFolderMode(true) //  Folder mode
                .setShowCamera(true) //  Show camera button
                .setFolderTitle("Albums") //  Folder title (works with FolderMode = true)
                .setImageTitle("Galleries") //  Image title (works with FolderMode = false)
                .setDoneTitle("Done") //  Done button title
                .setLimitMessage("You have reached selection limit") // Selection limit message
                .setMaxSize(1) //  Max images can be selected
                .setSavePath("ImagePicker") //  Image capture folder name
//                .setSelectedImages(images)          //  Selected images
                .setAlwaysShowDoneButton(true) //  Set always show done button in multiple mode
                .setRequestCode(PICK_IMAGE_ID2) //  Set request code, default Config.RC_PICK_IMAGES
                .setKeepScreenOn(true) //  Keep screen on when selecting images
                .start() //  Start ImagePicker
        }

        view.submit.setOnClickListener {

            val idFile = File(viewModel.idCardPath.value)
            val idFileReqBody = RequestBody.create(MediaType.parse("image/*"), idFile)
            val idPart = MultipartBody.Part.createFormData("idCard", idFile.getName(), idFileReqBody)

            val idSecondaryFile = File(viewModel.idCardPath.value)
            val idSecondaryFileReqBody = RequestBody.create(MediaType.parse("image/*"), idSecondaryFile)
            val idPartSecondary =
                MultipartBody.Part.createFormData("idCardSecondary", idSecondaryFile.getName(), idSecondaryFileReqBody)

            GlobalScope.launch(Dispatchers.Main) {

                try {
                    stemeraldApiClient.submitMyEvidences(
                        firstName = viewModel.firstName.value!!,
                        lastName = viewModel.lastName.value!!,
                        address = viewModel.address.value!!,
                        birthday = Calendar.getInstance(Locale.ENGLISH).apply {
                            set(
                                viewModel.birthdayYear.value!!,
                                viewModel.birthdayMoth.value!!,
                                viewModel.birthdayDay.value!!
                            )
                        }.time,
                        cityId = viewModel.cities.value!!.get(viewModel.selectedCityPosition.value!!).id,
                        gender = Gender.valueOf(resources.getStringArray(R.array.genders).get(viewModel.selectedGenderPosition.value!!)),
                        nationalCode = viewModel.idNumber.value!!,
                        idCard = idPart,
                        idCardSecondary = idPartSecondary
                    ).await()

                    longToast("Successfully uploaded. You will be fully verified in a few hours...")
                } catch (e: Exception) {
                    e.printStackTrace()
                    toast(R.string.problem_occurred_toast)
                }
            }

        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (listOf(
                PICK_IMAGE_ID1,
                PICK_IMAGE_ID2
            ).contains(requestCode) && resultCode == Activity.RESULT_OK && data != null
        ) {
            data.getParcelableArrayListExtra<Parcelable>(Config.EXTRA_IMAGES)?.get(0)?.run { this as Image? }
                ?.path?.let { path ->
                val file = File(path)

                if (!file.exists()) {
                    toast("File ${file.path} not exists")
                } else {
                    when (requestCode) {
                        PICK_IMAGE_ID1 -> {
                            Glide.with(this).load(file).into(id_card1_image)
                            viewModel.idCardPath.postValue(path)
                        }
                        PICK_IMAGE_ID2 -> {
                            Glide.with(this).load(file).into(id_card2_image)
                            viewModel.idCardSecondaryPath.postValue(path)
                        }
                    }

                    toast("Image selected successfully")
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
