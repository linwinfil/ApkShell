package com.maoxin.app.server

import android.os.Build
import android.os.CancellationSignal
import android.service.autofill.*
import androidx.annotation.RequiresApi

/** @author lmx
 * Created by lmx on 2020/3/5.
 */
@RequiresApi(Build.VERSION_CODES.O)
class MyAutofillServer : AutofillService() {
    override fun onFillRequest(request: FillRequest, cancellationSignal: CancellationSignal, callback: FillCallback) {
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {

    }
}