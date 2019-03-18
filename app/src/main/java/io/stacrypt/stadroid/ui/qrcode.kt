package io.stacrypt.stadroid.ui

import android.widget.ImageView
import net.glxn.qrgen.android.QRCode


fun ImageView.showQrCode(code: String) = setImageBitmap(QRCode.from(code).bitmap())
