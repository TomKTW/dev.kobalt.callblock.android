package dev.kobalt.callblock.main

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import dev.kobalt.callblock.base.BaseActivity
import dev.kobalt.callblock.databinding.MainBinding

/** Main activity of application. */
class MainActivity : BaseActivity<MainBinding>() {

    private val callPermissionsRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            Log.d("Permissions", "All granted")
        } else {
            Log.d("Permissions", "Some denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        callPermissionsRequest.launch(
            arrayOf(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CALL_LOG
            )
        )
    }

}