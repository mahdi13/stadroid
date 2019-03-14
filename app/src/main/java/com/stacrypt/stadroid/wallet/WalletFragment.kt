package com.stacrypt.stadroid.wallet

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import kotlinx.android.synthetic.main.fragment_wallet.*
import android.view.animation.DecelerateInterpolator
import android.animation.ObjectAnimator
import android.widget.ProgressBar
import com.stacrypt.stadroid.R


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [WalletFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [WalletFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class WalletFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wallet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        if (childFragmentManager.fragments.size == 0) {
//            val ft = childFragmentManager.beginTransaction()
//            ft.replace(R.id.asset_balance_container, LoadingFragment())
//            ft.commit()

        childFragmentManager.beginTransaction().replace(R.id.asset_balance_container, AssetBalanceFragment())
            .commitNow()

//        view.apsv_presentation.shadowColor = Color.argb(200, 0, 0, 0)
//        view.apsv_presentation.animationDuration = 1000
//        view.apsv_presentation.sweepAngle = 270F
//
////        val colors = IntArray(MODEL_COUNT)
////        val bgColors = IntArray(MODEL_COUNT)
////        for (i in 0 until MODEL_COUNT) {
////            colors[i] = Color.parseColor(stringColors[i])
////            bgColors[i] = Color.parseColor(stringBgColors[i])
////        }
//
//        val models = ArrayList<ArcProgressStackView.Model>()
//        models.add(ArcProgressStackView.Model("STRATEGY", 0.7F, Color.BLUE, Color.RED))
////        models.add(Model("STRATEGY", 1, bgColors[0], colors[0]))
////        models.add(ArcProgressStackView.Model("DESIGN", 1, bgColors[1], colors[1]))
////        models.add(Model("DEVELOPMENT", 1, bgColors[2], colors[2]))
////        models.add(Model("QA", 1, bgColors[3], colors[3]))
//        view.apsv_presentation.models = models
//
//
//        val valueAnimator = ValueAnimator.ofFloat(1.0f, 105.0f)
//        valueAnimator.duration = 800
//        valueAnimator.startDelay = 200
//        valueAnimator.repeatMode = ValueAnimator.RESTART
//        valueAnimator.repeatCount = 1
//        valueAnimator.addListener(object : AnimatorListenerAdapter() {
//            override fun onAnimationEnd(animation: Animator) {
//                animation.removeListener(this)
//                animation.addListener(this)
//
//                for (model in apsv_presentation.models) model.setProgress(0.8F)
//                apsv_presentation.animateProgress()
//            }
//
//            override fun onAnimationRepeat(animation: Animator) {
//            }
//        })
//        valueAnimator.addUpdateListener { animation ->
//            apsv_presentation.getModels().get(Math.min(0, 0))
//                .setProgress(animation.animatedValue as Float)
//            apsv_presentation.postInvalidate()
//        }
//
//        apsv_presentation.setOnClickListener(View.OnClickListener {
//            if (valueAnimator.isRunning) return@OnClickListener
//            if (apsv_presentation.getProgressAnimator().isRunning()) return@OnClickListener
//            valueAnimator.start()
//        })

//        fun animateIndicator(indicator: ProgressBar, toVal: Int) = ObjectAnimator.ofInt(
//            indicator, "progress", 0, toVal
//        ).apply {
//            duration = 1_000 // in milliseconds
//            interpolator = DecelerateInterpolator()
//            start()
//
//        }
//
//        value_indicator.suffixText = "$"
//        animateIndicator(value_indicator, 100)
//        animateIndicator(crypto_indicator, 60)
//        animateIndicator(blocked_indicator, 40)


//        val ft = childFragmentManager.beginTransaction()
//        ft.replace(R.id.asset_balance_container, AssetBalanceFragment())
//        ft.commitNow()
//        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WalletFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WalletFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
