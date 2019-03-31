import android.content.Context
import android.content.DialogInterface
import androidx.fragment.app.Fragment
import org.jetbrains.anko.AlertBuilder
import org.jetbrains.anko.alert

//package io.stacrypt.stadroid.ui
//
//import android.content.Context
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.DialogFragment
//import io.stacrypt.stadroid.R
//
//enum class MessageState { LOADING, WARNING, INFO, ERROR }
//
//class InteractiveDialog : DialogFragment() {
//
//    var title: String? = null
//    val softPromises = ArrayList<String>()
//    val hardPromises = ArrayList<String>()
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
//        inflater.inflate(R.layout.dialog_intractive_container, container, false)
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
////        view.soft.addView(
////        AlertDialog
////        activity.apply {
////            val name = editText()
////        ViewManager
////            verticalLayout {
////                val name = editText()
////                button("Say Hello") {
////                    onClick { toast("Hello, ${name.text}!") }
////                }
////            }
////        }
//
////        )
//    }
//}
//
//fun Context.interactiveDialog(builder: InteractiveDialog.() -> Unit): InteractiveDialog =
//    InteractiveDialog().apply(builder)
//
//fun InteractiveDialog.softPromise(builder: () -> String) {
//    softPromises.add(builder.invoke())
//}
//
//fun InteractiveDialog.hardPromise(builder: () -> String) {
//    hardPromises.add(builder.invoke())
//}
//
//fun InteractiveDialog.title(builder: () -> String) {
//    title = builder.invoke()
//}


interface ConditionAlertBuilder<out D : DialogInterface> : AlertBuilder<D> {
    val conditions: ArrayList<String>
}

//fun Context.alert(init: AlertBuilder<DialogInterface>.() -> Unit): AlertBuilder<DialogInterface> =
//    AndroidAlertBui
//lder(this).apply { init() }

//fun Fragment.conditionalAlert(init: ConditionAlertBuilder<DialogInterface>.() -> Unit) = requireActivity().alert {
//
//}.apply(init)
