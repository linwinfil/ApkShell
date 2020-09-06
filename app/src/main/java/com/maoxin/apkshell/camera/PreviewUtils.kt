package com.maoxin.apkshell.camera

import android.graphics.Point
import android.util.Size
import android.view.Surface
import android.view.View

object PreviewUtils {
    fun isNaturalPortrait(view: View): Boolean {
        val display = view.display ?: return true
        val deviceSize = Point()
        display.getRealSize(deviceSize)
        val width = deviceSize.x
        val height = deviceSize.y
        val rotationDegrees = getRotationDegrees(view).toInt()
        return (rotationDegrees == 0 || rotationDegrees == 180) && width < height || (rotationDegrees == 90 || rotationDegrees == 270) && width >= height
    }

    fun getRotationDegrees(view: View): Int {
        val display = view.display ?: return 0
        val rotation = display.rotation
        return rotationDegreesFromSurfaceRotation(rotation)
    }

    fun rotationDegreesFromSurfaceRotation(rotationConstant: Int): Int {
        return when (rotationConstant) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> throw UnsupportedOperationException(
                    "Unsupported surface rotation constant: $rotationConstant")
        }
    }

    fun getPreviewSize(view: View, cameraSize: Size): Size {
        return if (isNaturalPortrait(view)) {
            Size(cameraSize.height, cameraSize.width)
        } else {
            Size(cameraSize.width, cameraSize.height)
        }
    }
}