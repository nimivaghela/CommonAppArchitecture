package com.app.structure.ui

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.app.structure.R
import com.app.structure.model.UserHolder
import com.app.structure.utils.PermissionCallback
import com.app.structure.utils.PermissionHelper
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private val mUserHolder: UserHolder by inject()

    //This is used to check runtime permissions and show dialog based on that permission
    private var permissionHelper: PermissionHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // init Permission helper with list of permissions required and request code
        permissionHelper = PermissionHelper(
            this, arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), 1001
        )

        txtEmail.setOnClickListener {
            //call this only when you want to ask permission
            permissionHelper?.requestPermissions(object : PermissionCallback {

                //this execute when all permissions are granted
                override fun onPermissionGranted() {
                    Toast.makeText(this@MainActivity, "Permission granted", Toast.LENGTH_LONG)
                        .show()
                }

                //this will execute when all permissions are denied
                override fun onPermissionDenied() {
                    Toast.makeText(this@MainActivity, "Permission denied", Toast.LENGTH_LONG).show()
                }

                //this will execute when permission denied by system and this will move user to setting screen
                override fun onPermissionDeniedBySystem() {
                    Toast.makeText(
                        this@MainActivity,
                        "Permission denied by system",
                        Toast.LENGTH_LONG
                    ).show()

                    permissionHelper?.openSettings()
                }

                // this will execute when some of permission is granted, in that case you can move user to
                // settings screen by calling openSetting with proper message for permissions which are not granted
                override fun onIndividualPermissionGranted(grantedPermission: Array<String>) {
                    Toast.makeText(
                        this@MainActivity,
                        "Permission individual granted",
                        Toast.LENGTH_LONG
                    ).show()
                }

                //this will execute when permission are not added in Manifest but asking for runtime
                override fun onPermissionNotFoundInManifest() {
                    Toast.makeText(this@MainActivity, "Permission not found", Toast.LENGTH_LONG)
                        .show()
                }
            })
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //you need to add default result into helper class to manage results and get result in helper callback
        permissionHelper?.onRequestPermissionResult(requestCode, permissions, grantResults)
    }
}