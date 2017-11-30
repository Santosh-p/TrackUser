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

package sveltoz.icaretrackerapp.Camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import sveltoz.icaretrackerapp.Service.MyFirebaseMessagingService;
import sveltoz.icaretrackerapp.Service.DemoCamService;

import static sveltoz.icaretrackerapp.Constants.Constants.appendLog;

/**
 * Created by Keval on 10-Nov-16.
 * This surface view works as the fake preview for the camera.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

@SuppressWarnings("ALL")
@SuppressLint("ViewConstructor")
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static CameraCallbacks mHiddenCameraActivity;
    static String formattedDate;
    private static SurfaceHolder mHolder;
    private static Camera mCamera;
    public static String imageFileName;
    public static Context context;
    private static CameraConfig mCameraConfig;

    private static boolean safeToTakePicture = false;

    public CameraPreview(@NonNull Context context, CameraCallbacks cameraCallbacks) {
        super(context);
        context = context;
        mHiddenCameraActivity = cameraCallbacks;

        //Set surface holder
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }


    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
//            mCamera = null;
//            mCamera = Camera.open();

            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            // left blank for now
            mHiddenCameraActivity.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED);
            e.printStackTrace();
            appendLog(context, "1 CameraPreview " + e.toString()+date);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        safeToTakePicture = false;

        if (DemoCamService.cameraType.equals("front")) {
            Camera.Parameters parameters = mCamera.getParameters();
            List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
            Camera.Size cs = sizes.get(0);
            parameters.set("orientation", "portrait");
            parameters.setRotation(270);
            parameters.setPreviewSize(cs.width, cs.height);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
            safeToTakePicture = true;

        } else {
            Camera.Parameters params = mCamera.getParameters();
            params.set("jpeg-quality", 40);
            params.set("rotation", 90);
            params.set("orientation", "portrait");
            mCamera.setParameters(params);
            safeToTakePicture = true;
            mCamera.startPreview();

        }


        mCamera.startPreview();
        safeToTakePicture = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Call stopPreview() to stop updating the preview surface.
        if (mCamera != null) {
            safeToTakePicture = false;
            mCamera.stopPreview();
        }
    }

    /**
     * Initialize the camera and start the preview of the camera.
     *
     * @param cameraConfig camera config builder.
     */
    public static void startCameraInternal(@NonNull CameraConfig cameraConfig) {
        mCameraConfig = cameraConfig;

        if (safeCameraOpen(mCameraConfig.getFacing())) {
            if (mCamera != null) {
                //requestLayout();

                try {
                    mCamera.setPreviewDisplay(mHolder);
                    mCamera.startPreview();

                    safeToTakePicture = true;
                } catch (IOException e) {
                    DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                    String date = df.format(Calendar.getInstance().getTime());
                    e.printStackTrace();

                    mHiddenCameraActivity.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED);
                    e.printStackTrace();
                    appendLog(context, "2 CameraPreview " + e.toString()+date);
                }
            }
        } else {
            mHiddenCameraActivity.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED);
        }
    }

    private static boolean safeCameraOpen(int id) {
        boolean qOpened = false;

        try {
            stopPreviewAndFreeCamera();

            mCamera = Camera.open(id);
            qOpened = (mCamera != null);
        } catch (Exception e) {
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            Log.e("CameraPreview", "failed to open Camera");
            e.printStackTrace();
            appendLog(context, "3 CameraPreview " + e.toString()+date);
        }

        return qOpened;
    }

    public boolean isSafeToTakePictureInternal() {
        return safeToTakePicture;
    }

    public static void takePictureInternal() {
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                FileOutputStream fos = null;
                try {
                    Calendar c = Calendar.getInstance();
                    System.out.println("Current time => " + c.getTime());

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    formattedDate = df.format(c.getTime());
                    formattedDate = formattedDate.replace(":", ".");
                    formattedDate = formattedDate.replace(" ", "x");
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AudioRecorder/" + "img" + "x"
                            + MyFirebaseMessagingService.userid + "x" + formattedDate + "x" + MyFirebaseMessagingService.responceVal1   //IMG_214515184113123.png
                            + (CameraConfig.mImageFormat == CameraImageFormat.FORMAT_JPEG ? ".jpeg" : ".png"));

                    String imageName = file.toString();
                    String[] arr = imageName.split("/");

                    imageFileName = arr[arr.length - 1];

                    fos = new FileOutputStream(file);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 250, 250, false);
                    bitmap.compress(mCameraConfig.getImageFormat() == ImageFormat.JPEG
                                    ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.PNG,
                            100, fos);
                    mHiddenCameraActivity.onImageCapture(mCameraConfig.getImageFile());
                } catch (FileNotFoundException e) {
                    DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                    String date = df.format(Calendar.getInstance().getTime());
                    e.printStackTrace();
                    appendLog(context, "4 CameraPreview " + e.toString()+date);
                } finally {
                    try {
                        if (fos != null) fos.close();
                    } catch (IOException e) {
                        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                        String date = df.format(Calendar.getInstance().getTime());
                        e.printStackTrace();
                        appendLog(context, "5 CameraPreview " + e.toString()+date);
                    }
                    MyFirebaseMessagingService.SendImageFileToServer();
                }

            }

        });
    }

    /**
     * When this function returns, mCamera will be null.
     */
    public static void stopPreviewAndFreeCamera() {
        safeToTakePicture = false;

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
}