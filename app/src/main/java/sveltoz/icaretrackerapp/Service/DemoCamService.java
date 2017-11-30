/*
 * Copyright 2016 Keval Patel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sveltoz.icaretrackerapp.Service;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import sveltoz.icaretrackerapp.Camera.CameraConfig;
import sveltoz.icaretrackerapp.Camera.CameraError;
import sveltoz.icaretrackerapp.Camera.CameraFacing;
import sveltoz.icaretrackerapp.Camera.CameraImageFormat;
import sveltoz.icaretrackerapp.Camera.CameraResolution;
import sveltoz.icaretrackerapp.Camera.HiddenCameraUtils;

import static sveltoz.icaretrackerapp.Service.MyFirebaseMessagingService.camType;

/**
 * Created by Keval on 11-Nov-16.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class DemoCamService extends HiddenCameraService {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static String cameraType;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            if (camType.equals("front")) {
                cameraType = "front";

                if (HiddenCameraUtils.canOverDrawOtherApps(this)) {
                    CameraConfig cameraConfig = new CameraConfig()
                            .getBuilder(this)
                            .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
                            .setCameraResolution(CameraResolution.MEDIUM_RESOLUTION)
                            .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                            .build();

                    startCamera(cameraConfig);

                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                takePicture();
                            } catch (Exception e) {
                                Log.d("InputStream", e.toString());
                            }
                        }
                    }, 2000);
                } else {
                    //Open settings to grant permission for "Draw other apps".
                    HiddenCameraUtils.openDrawOverPermissionSetting(this);
                }
            } else {
                cameraType = "back";
                if (HiddenCameraUtils.canOverDrawOtherApps(this)) {
                    CameraConfig cameraConfig = new CameraConfig()
                            .getBuilder(this)
                            .setCameraFacing(CameraFacing.REAR_FACING_CAMERA)
                            .setCameraResolution(CameraResolution.LOW_RESOLUTION)
                            .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                            .build();

                    startCamera(cameraConfig);

                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            takePicture();
                        }
                    }, 2000);
                } else {
                    //Open settings to grant permission for "Draw other apps".
                    HiddenCameraUtils.openDrawOverPermissionSetting(this);
                }
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onImageCapture(@NonNull File imageFile) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

        Log.d("Image capture", imageFile.length() + "");
        stopSelf();
    }

    @Override
    public void onCameraError(@CameraError.CameraErrorCodes int errorCode) {
        switch (errorCode) {
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
                //Camera open failed. Probably because another application
                //is using the camera
                Toast.makeText(this, "Cannot open camera.", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
                //camera permission is not available
                //Ask for the camra permission before initializing it.
                Toast.makeText(this, "Camera permission not available.", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION:
                //Display information dialog to the user with steps to grant "Draw over other app"
                //permission for the app.
                HiddenCameraUtils.openDrawOverPermissionSetting(this);
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
                Toast.makeText(this, "Your device does not have front camera.", Toast.LENGTH_LONG).show();
                break;
        }
        stopSelf();
    }
}
