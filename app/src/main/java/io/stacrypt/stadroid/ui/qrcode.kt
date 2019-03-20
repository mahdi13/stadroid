package io.stacrypt.stadroid.ui

import android.view.View
import android.widget.ImageView
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.glxn.qrgen.android.QRCode
import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import android.R.attr.data
import android.graphics.Color
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter




fun ImageView.showQrCode(code: String) = apply {
//    setLayerType(View.LAYER_TYPE_NON, null)
}.apply {
    GlobalScope.launch(Dispatchers.Main){
//        invalidate()
//        delay(1000)
//        setImageBitmap(
//            QRCode.from(code)
//                .withColor(0xFFFF0000.toInt(), 0xFFFFFFAA.toInt())
//                .withErrorCorrection(ErrorCorrectionLevel.L)
//                .bitmap()
//        )
//        invalidate()
//        delay(1000)
//        invalidate()
        val writer = QRCodeWriter()
        // String finaldata = Uri.encode(data, "utf-8");
        val width = 250
        val height = 250
        val bm = writer
            .encode(code, BarcodeFormat.QR_CODE, width, height)
        val ImageBitmap = Bitmap.createBitmap(
            width, height,
            Bitmap.Config.ARGB_8888
        )

        for (i in 0 until width) {// width
            for (j in 0 until height) {// height
                ImageBitmap.setPixel(
                    i, j, if (bm.get(i, j))
                        Color.BLACK
                    else
                        Color.WHITE
                )
            }
        }

        setImageBitmap(ImageBitmap)

    }
}.apply { invalidate() }
