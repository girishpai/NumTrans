package com.udacity.mlndcapstone.numtrans;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "NumTrans";
    private DigitDetector mDetector = new DigitDetector();
    private ImageProcessor mImgProcessor = new ImageProcessor();
    private IOUtils mIOUtils = new IOUtils();
    ArrayList<String> mPhotoNames = new ArrayList<String>(50);
    private final int ACTIVITY_START_CAMERA_APP = 0;
    int mPhotoNum = 0;
    private String mImageFileLocation;

    //View Variables
    ImageView mPhoto;
    Button mNextButton;
    TextView mResultText;
    String resultText;


    //Initialize open cv
    static {
        if(OpenCVLoader.initDebug()) {
            Log.d(TAG,"OpenCV Successfully Loaded");
        }
        else {
            Log.d(TAG,"OpenCV Load Not Successfully");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean ret = mDetector.setup(this);
        if (!ret) {
            Log.i(TAG, "Detector setup failed");
            return;
        }


        //Uncomment this section to locally test openCV / detector interface without calling camera

        /*
        Bitmap origImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.photo2);
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.photo2);

        Mat imgToProcess = mImgProcessor.preProcessImage(bitmap);
        Bitmap.createScaledBitmap(bitmap,imgToProcess.width(),imgToProcess.height(),false);
        Bitmap.createScaledBitmap(origImage,imgToProcess.width(),imgToProcess.height(),false);
        savePhoto(bitmap,"photo.jpg");
        Utils.matToBitmap(imgToProcess,bitmap);
        savePhoto(bitmap,"photo_preprocess.jpg");
        Mat boundImage = mImgProcessor.segmentAndDetect(imgToProcess,origImage,mDetector);
        Utils.matToBitmap(boundImage,bitmap);
        savePhoto(bitmap,"photo_bound.jpg");
        */





        mResultText = (TextView) findViewById(R.id.text_result);
        mPhoto = (ImageView) findViewById(R.id.photo);
        mNextButton = (Button) findViewById(R.id.next_button);

        //Next button in the app to slide between original photo,pre-processed photo and
        //result photo with boxes around digits
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch(mPhotoNum) {
                    case 0 : resultText = "Original Photo";
                            break;
                    case 1 : resultText = "Processed Photo";
                            break;
                    case 2 : resultText = mImgProcessor.getResultText();
                            break;
                }
                mPhoto.setImageBitmap(mIOUtils.getSavedImage(mPhotoNames.get(mPhotoNum)));
                mResultText.setText(resultText);
                mPhotoNum += 1;
                mPhotoNum = mPhotoNum % mPhotoNames.size();
            }

        });
    }

    public void savePhoto(Bitmap bm, String photoName) {
        mPhotoNames.add(photoName);
        mIOUtils.saveImage(bm,photoName);
    }

    //Once the photo is clicked inside the Camera App it comes to this task
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == ACTIVITY_START_CAMERA_APP && resultCode == RESULT_OK) {

            //Get the photo clicked by the camera - reduced size to ensure does not make
            //the app crash in low memory situation - was a BUG initially which was not caught
            // when app was run on high memory Nexus 6p phone (64 GB). When app was re-run on Samsung
            // S5 with 32 GB it crashed dues to insufficient memory.
            Bitmap bitmap = mIOUtils.getCameraPhoto("photo.jpg");
            Bitmap origImage = mIOUtils.getCameraPhoto("photo.jpg");

            //Preproces the image to remove noise, amplify the region of interest with the number
            //by making it bright white and make the background completely black.
            Mat imgToProcess = mImgProcessor.preProcessImage(bitmap);

            //Scale down the bitmap based on the processing height and width (640 x 480)
            Bitmap.createScaledBitmap(bitmap,imgToProcess.width(),imgToProcess.height(),false);
            Bitmap.createScaledBitmap(origImage,imgToProcess.width(),imgToProcess.height(),false);

            //Convert to bitmap and save the photo to display in the app.
            Utils.matToBitmap(imgToProcess.clone(),bitmap);
            savePhoto(bitmap,"photo_preprocess.jpg");

            //Pass the preprocessed image to perform segmentation and recogntion. This also
            //overlays rectangular boxes on the segmented digits.
            Mat boundImage = mImgProcessor.segmentAndRecognize(imgToProcess,origImage,mDetector);
            Utils.matToBitmap(boundImage.clone(),bitmap);
            savePhoto(bitmap,"photo_bound.jpg");



        }
    }

    //This is the task called when the take Photo button is pressed.
    //Transfers control to Camera Application of the phone
    public void takePhoto(View view) {

        Intent callCameraApplicationIntent = new Intent();
        callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        photoFile = mIOUtils.createImageFile("photo.jpg");
        mImageFileLocation = photoFile.getAbsolutePath();
        mPhotoNames.add(photoFile.getName());
        Log.d(TAG,"Image name : " + photoFile.getName());
        callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        startActivityForResult(callCameraApplicationIntent,ACTIVITY_START_CAMERA_APP);
    }




}
