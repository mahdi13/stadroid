package io.stacrypt.stadroid

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.progressBar

class LoadingFragmentUi<T> : AnkoComponent<T> {
    override fun createView(ui: AnkoContext<T>) = with(ui) {
        frameLayout {
            foregroundGravity = Gravity.CENTER
            progressBar {
//                updateLayoutParams<FrameLayout.LayoutParams> {
//                    width = FrameLayout.LayoutParams.WRAP_CONTENT
//                    height = FrameLayout.LayoutParams.WRAP_CONTENT
//                }
            }
        }
    }
}


class LoadingFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        LoadingFragmentUi<Fragment>().createView(AnkoContext.create(context!!, this))
}