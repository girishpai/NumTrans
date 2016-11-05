package com.udacity.mlndcapstone.numtrans;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "NumTrans";
    private DigitDetector mDetector = new DigitDetector();
    private ImageProcessor mImgProcessor = new ImageProcessor();
    private IOUtils mIOUtils = new IOUtils();
    ArrayList<String> mPhotoNames = new ArrayList<String>(50);
    int mPhotoNum = 0;

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

        Bitmap origImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.photo);
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.photo);
        savePhoto(bitmap,"photo.jpg");
        Mat imgToProcess = mImgProcessor.preProcessImage(bitmap);
        Utils.matToBitmap(imgToProcess,bitmap);
        savePhoto(bitmap,"photo_preprocess.jpg");
        Mat boundImage = mImgProcessor.segmentAndDetect(imgToProcess,origImage,mDetector);
        Utils.matToBitmap(boundImage,bitmap);
        savePhoto(bitmap,"photo_bound.jpg");

        mResultText = (TextView) findViewById(R.id.text_result);
        mPhoto = (ImageView) findViewById(R.id.photo);
        mNextButton = (Button) findViewById(R.id.next_button);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch(mPhotoNum) {
                    case 0 : resultText = "Original Photo";
                            break;
                    case 1 : resultText = "Processed Photo";
                            break;
                    case 2 : resultText = "Bounding Boxes";
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


}
