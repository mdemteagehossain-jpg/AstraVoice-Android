package com.astra.voiceassistant;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;

public class FlashlightController {

    private final Context context;
    private final CameraManager cameraManager;
    private String cameraId;

    public FlashlightController(Context context) {
        this.context = context;
        this.cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        initCameraId();
    }

    private void initCameraId() {
        if (cameraManager != null) {
            try {
                for (String id : cameraManager.getCameraIdList()) {
                    CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(id);
                    Boolean hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                    Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                    if (hasFlash != null && hasFlash && facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                        this.cameraId = id;
                        break;
                    }
                }
            } catch (CameraAccessException e) {
                this.cameraId = null;
            }
        }
    }

    public boolean setTorchMode(boolean enable) {
        if (cameraManager != null && cameraId != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                cameraManager.setTorchMode(cameraId, enable);
                return true;
            } catch (CameraAccessException e) {
                return false;
            }
        }
        return false;
    }
}