/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.ucla.cs.sourcecodes;



import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.content.res.AssetManager;
import java.io.RandomAccessFile;

import com.androidbelieve.drawerwithswipetabs.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.BufferUnderflowException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.content.Intent.getIntent;

import com.googlecode.tesseract.android.TessBaseAPI;


public class CameraFragment extends Fragment  {



    protected static final String PHOTO_TAKEN = "photo_taken";
    // You should have the trained data file in assets folder
    // You can get them at:
    // http://code.google.com/p/tesseract-ocr/downloads/list
    public static final String lang = "eng";

    private Preview mPreview;

    Camera mCamera;
    int mNumberOfCameras;
    int mCurrentCamera;  // Camera ID currently chosen
    int mCameraCurrentlyLocked;  // Camera ID that's actually acquired
    public String recognizedText;

//    public static final String DATA_PATH = Environment
  //          .getExternalStorageDirectory().toString() + "/MyCameraApp/";
    public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString();



    // The first rear facing camera
    int mDefaultCameraId;
    private static String TAG = "CameraFragment.java";
    View mContentView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        createPath();
        // Create a container that will hold a SurfaceView for camera previews
         mPreview = new Preview(this.getActivity());




        // Find the total number of cameras available
        mNumberOfCameras = Camera.getNumberOfCameras();

        // Find the ID of the rear-facing ("default") camera
        CameraInfo cameraInfo = new CameraInfo();
        for (int i = 0; i < mNumberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                mCurrentCamera = mDefaultCameraId = i;
            }
        }
        setHasOptionsMenu(mNumberOfCameras > 1);




    }



    public void onInitiateCapture() {

        Log.d(TAG,"onInitiateCapture");

        if (mCamera != null) {
            Log.d(TAG,"onInitiateCapture, mCamera not null");
            if(Preview.safeToTakePicture) {
                Log.d(TAG,"onInitiateCapture - Save to take pic");
                mCamera.takePicture(null, null, mPicture);
                Preview.safeToTakePicture = false;

            }
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Add an up arrow to the "home" button, indicating that the button will go "up"
        // one activity in the app's Activity heirarchy.
        // Calls to getActionBar() aren't guaranteed to return the ActionBar when called
        // from within the Fragment's onCreate method, because the Window's decor hasn't been
        // initialized yet.  Either call for the ActionBar reference in Activity.onCreate()
        // (after the setContentView(...) call), or in the Fragment's onActivityCreated method.




        // Activity activity = this.getActivity();
        // ActionBar actionBar = activity.getActionBar();
        // actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(PHOTO_TAKEN, Preview.safeToTakePicture);
    }




    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {


            TextView tessStatus =  (TextView) getActivity().findViewById(R.id.tess_status);

            tessStatus.setVisibility(View.VISIBLE);
            tessStatus.setText("Please Wait \n Reading data  ...");

            File pictureFile = getOutputMediaFile();

            //Force update the directory
            makeFileDiscoverable(pictureFile,getActivity().getApplicationContext());







            if (pictureFile == null) {
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);


                fos.close();
            } catch (FileNotFoundException e) {

            } catch (IOException e) {
            }
            String _path = pictureFile.getPath();

            Log.d(TAG, "PATH " + _path);
            Preview.safeToTakePicture = true;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;

            Bitmap bitmap = BitmapFactory.decodeFile(_path, options);


            try {
                ExifInterface exif = new ExifInterface(_path);
                int exifOrientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);

                Log.v(TAG, "Orient: " + exifOrientation);

                int rotate = 0;

                switch (exifOrientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotate = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotate = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotate = 270;
                        break;
                }

                Log.v(TAG, "Rotation: " + rotate);

                if (rotate != 0) {

                    // Getting width & height of the given image.
                    int w = bitmap.getWidth();
                    int h = bitmap.getHeight();

                    Log.d(TAG,"bitmp width " + Integer.toString(w));
                    Log.d(TAG,"bitmp height " + Integer.toString(h));

                    // Setting pre rotate
                    Matrix mtx = new Matrix();
                    mtx.preRotate(rotate);

                    // Rotating Bitmap
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
                }



                // Convert to ARGB_8888, required by tess
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);


            } catch (IOException e) {
                Log.e(TAG, "Couldn't correct orientation: " + e.toString());
            }



            Log.v(TAG, "Before baseApi");

            TessBaseAPI baseApi = new TessBaseAPI();
            baseApi.setDebug(true);
            Log.v(TAG, "Before baseApi init");
            baseApi.init(DATA_PATH, lang);

            Log.v(TAG, "after  init");
            baseApi.setImage(bitmap);
            Log.v(TAG, "setImage");
            recognizedText = baseApi.getUTF8Text();

            baseApi.end();
            // You now have the text in recognizedText var, you can do anything with it.
            // We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
            // so that garbage doesn't make it to the display.

            Log.v(TAG, "OCRED TEXT: " + recognizedText);

            if ( lang.equalsIgnoreCase("eng") ) {
                recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
            }

            recognizedText = recognizedText.trim();



            if ( recognizedText.length() != 0 ) {
                //_field.setText(_field.getText().toString().length() == 0 ? recognizedText : _field.getText() + " " + recognizedText);
                //_field.setSelection(_field.getText().toString().length());

                Log.d(TAG, "Read Text is " + recognizedText);
            }

            // Cycle done.
            tessStatus.setText("Done");
            tessStatus.setVisibility(View.GONE);

            /*
            if (pictureFile == null) {
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {

            } catch (IOException e) {
            }*/


        }
    };



    private static File getOutputMediaFile() {
        //File mediaStorageDir = new File(
        //        Environment
        //                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        //        "MyCameraApp");

        //File mediaStorageDir = new File(
       //         Environment
       //                 .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
       //         "tessdata");

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),"tessdata");


        //    public static final String DATA_PATH = Environment
        //          .getExternalStorageDirectory().toString() + "/MyCameraApp/";


        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Log.d(TAG, "onCreateView");

        setHasOptionsMenu(true);





        return mPreview;
    }


    @Override
    public void onResume() {
        super.onResume();

        // Use mCurrentCamera to select the camera desired to safely restore
        // the fragment after the camera has been changed

        Log.d(TAG, "onResume()");
        mCamera = Camera.open(mCurrentCamera);
        mCameraCurrentlyLocked = mCurrentCamera;
        mPreview.setCamera(mCamera);



    }

    @Override
    public void onPause() {
        super.onPause();

        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        if (mCamera != null) {
            mPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        if (mNumberOfCameras > 1) {
            // Inflate our menu which can gather user input for switching camera
            inflater.inflate(R.menu.camera_menu, menu);
        } else {
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    public void makeFileDiscoverable(File file, Context context){
        MediaScannerConnection.scanFile(context, new String[]{file.getPath()}, null, null);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(file)));
    }


    public void createPath() {
        String[] paths = new String[] { DATA_PATH, DATA_PATH + "/tessdata/" };


        for (String path : paths) {
            Log.d(TAG, "paths: " + path);
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                    return;
                } else {
                    Log.v(TAG, "Created directory " + path + " on sdcard");
                }
            }
        }

        // lang.traineddata file with the app (in assets folder)
        // You can get them at:
        // http://code.google.com/p/tesseract-ocr/downloads/list
        // This area needs work and optimization
        if (!(new File(DATA_PATH + "/tessdata/" + lang + ".traineddata")).exists()) {
            try {

                //check on this  - peter bankole
             AssetManager assetManager = getActivity().getAssets();
                InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");



                //GZIPInputStream gin = new GZIPInputStream(in);
                OutputStream out = new FileOutputStream(DATA_PATH
                        + "/tessdata/" + lang + ".traineddata");



                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                //while ((lenf = gin.read(buff)) > 0) {
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                //gin.close();
                out.close();

                Log.v(TAG, "Copied " + lang + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
            }
        }


    }



        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_switch_cam:
                // Release this camera -> mCameraCurrentlyLocked
                if (mCamera != null) {
                    mCamera.stopPreview();
                    mPreview.setCamera(null);
                    mCamera.release();
                    mCamera = null;
                }


                // Acquire the next camera and request Preview to reconfigure
                // parameters.
                mCurrentCamera = (mCameraCurrentlyLocked + 1) % mNumberOfCameras;
                mCamera = Camera.open(mCurrentCamera);
                mCameraCurrentlyLocked = mCurrentCamera;
                mPreview.switchCamera(mCamera);

                // Start the preview
                mCamera.startPreview();
                return true;
            case android.R.id.home:
                Intent intent = new Intent(this.getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
