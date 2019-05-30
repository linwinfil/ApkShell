package com.maoxin.apkshell.fragment

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.maoxin.apkshell.R


private val PERMISSIONLIST = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.RECORD_AUDIO
)
private const val PERMISSION_REQUEST_CODE = 0x000001

class PermissionFragment : Fragment() {

    val cameraxFramentNaviAction = NavOptions.Builder().setPopUpTo(R.id.permissionFragment, true).build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!checkPermission()) {
            //没有权限，申请权限
            requestPermissions(PERMISSIONLIST, PERMISSION_REQUEST_CODE)
        } else {
            //有权限，打开CameraXFragment
            openCameraXFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_permission, container, false)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCameraXFragment()
            } else {
                Toast.makeText(context, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun openCameraXFragment() {
        val findNavController = Navigation.findNavController(requireActivity(), R.id.fragment_container)
        findNavController.navigate(R.id.action_permissionFragment_to_cameraXFragment, null, cameraxFramentNaviAction)
    }

    private fun checkPermission(): Boolean {
        for (permission in PERMISSIONLIST) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
}
