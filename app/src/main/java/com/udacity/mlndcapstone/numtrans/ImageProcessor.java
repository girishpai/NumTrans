package com.udacity.mlndcapstone.numtrans;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by girishpai on 11/4/16.
 * This is the main class which does all the pre-processing and segmentation.
 * Heavily used Computer Vision / Image Processing concepts like Edge Detection,
 * Contour Finding, Filtering etc.
 * Also calls the detector class to perform recognition of the segmented digits
 */
public class ImageProcessor {

    IOUtils mIOUtils = new IOUtils();
    private static final String TAG = "ImageProcessor";
    ArrayList<RoiObject> mRoiImages = new ArrayList<RoiObject>(50);
    //StringBuilder mResultDigits;
    String mResultText;
    NumberToWordConv mNumConverter = new NumberToWordConv();


    // Amplify the region of interest by making the number bright and
    // background black. This removes noise due to shadows / insufficient lighting.
    public Mat preProcessImage(Bitmap image) {
        Size sz = new Size(640, 480);
        ArrayList<Rect> rects;
        Rect rect;
        int top,bottom,left,right;
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap imageToDisplay = Bitmap.createBitmap(640,480,conf);
        Mat origImageMatrix = new Mat(image.getWidth(), image.getHeight(), CvType.CV_8UC3);
        Mat tempImageMat = new Mat(image.getWidth(),image.getHeight(),CvType.CV_8UC1,new Scalar(0));
        Utils.bitmapToMat(image,origImageMatrix);

        Mat imgToProcess = new Mat (image.getWidth(), image.getHeight(), CvType.CV_8UC1);
        Mat imgToProcessCanny = new Mat (image.getWidth(), image.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(image,imgToProcess);

        //Resize image to manageable size
        Imgproc.resize(imgToProcess, imgToProcess, sz,0,0,Imgproc.INTER_NEAREST);
        Imgproc.resize(origImageMatrix, origImageMatrix, sz,0,0,Imgproc.INTER_NEAREST);
        Imgproc.resize(tempImageMat, tempImageMat, sz,0,0,Imgproc.INTER_NEAREST);
        Imgproc.cvtColor(imgToProcess, imgToProcess, Imgproc.COLOR_BGR2GRAY);


        //Remove noise using Gaussian filter
        http://docs.opencv.org/2.4/doc/tutorials/imgproc/gausian_median_blur_bilateral_filter/gausian_median_blur_bilateral_filter.html
        Imgproc.GaussianBlur(imgToProcess,imgToProcess,new Size(3,3),0);


        Mat imgGrayInv = new Mat(sz,CvType.CV_8UC1,new Scalar(255.0));

        //Invert the brightness - make lighter pixel dark and vice versa.
        Core.subtract(imgGrayInv,imgToProcess,imgGrayInv);
        Imgproc.Canny(imgToProcess,imgToProcessCanny,13,39,3,false);

        rects = this.boundingBox(imgToProcessCanny);
        Log.d(TAG,"Length of rects : " + rects.size());

        if (rects.size() != 0) {
            rect = rects.get(0);
            top = rect.y;
            bottom = rect.y + rect.height;
            left = rect.x;
            right = rect.x + rect.height;
            for (int i = 1; i < rects.size(); i++) {
                rect = rects.get(i);
                if (rect.y < top) {
                    top = rect.y;
                }
                if (rect.y + rect.height > bottom) {
                    bottom = rect.y + rect.height;
                }
                if (rect.x < left) {
                    left = rect.x;
                }
                if (rect.x + rect.width > right) {
                    right = rect.x + rect.width;
                }
            }

            // Using the algorithm in the following paper to get the region of interest
            //http://web.stanford.edu/class/cs231m/projects/final-report-yang-pu.pdf

            Mat aux = tempImageMat.colRange(left, right).rowRange(top, bottom);
            MatOfDouble matMean = new MatOfDouble();
            MatOfDouble matStd = new MatOfDouble();
            Double mean;
            Double std;
            Mat roiImage = imgGrayInv.submat(top, bottom, left, right).clone();
            Core.meanStdDev(roiImage, matMean, matStd);
            mean = matMean.toArray()[0];
            std = matStd.toArray()[0];
            Imgproc.threshold(roiImage, roiImage,mean + std,255, Imgproc.THRESH_BINARY);
            roiImage.copyTo(aux);
        }

        sz = new Size(image.getWidth(),image.getHeight());
        Imgproc.resize(tempImageMat,tempImageMat,sz);
        return tempImageMat;
    }

    public ArrayList<Rect> boundingBox(Mat imgToProcess) {
        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        ArrayList<Rect> rects = new ArrayList<>(50);
        Mat hierarchy = new Mat();
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf type
        Imgproc.findContours(imgToProcess, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {

            double contourArea = Imgproc.contourArea(contours.get(contourIdx));

            MatOfPoint2f approxCurve = new MatOfPoint2f();
            MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(contourIdx).toArray());
            //Processing on mMOP2f1 which is in type MatOfPoint2f
            double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

            //Convert back to MatOfPoint
            MatOfPoint points = new MatOfPoint(approxCurve.toArray());

            // Get bounding rect of contour
            Rect rect = Imgproc.boundingRect(points);
            Log.d(TAG, "Rect Height :" + rect.height);
            //Log.d(TAG, "Rect Width :" + rect.width);
            if (rect.height > 5) {
                rects.add(rect);
            }


        }
        filterRectangles(rects);
        return rects;
    }



    public Mat segmentAndRecognize(Mat imgToProcess,Bitmap origImage,DigitDetector digitDetector) {
        StringBuilder ResultDigits  = new StringBuilder("");
        mRoiImages.clear();
        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Mat origImageMatrix = new Mat(origImage.getWidth(), origImage.getHeight(), CvType.CV_8UC3);
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Mat roiImage;
        Utils.bitmapToMat(origImage, origImageMatrix);
        Imgproc.findContours(imgToProcess, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {

            double contourArea = Imgproc.contourArea(contours.get(contourIdx));

            //In case of white board / surface having a lot of small dark spots
            // Use this to filter out very small spots.
            //if (contourArea < 500.0) {
            //    continue;
            //}
            Log.d(TAG,"CountourAra = " + contourArea);

            MatOfPoint2f approxCurve = new MatOfPoint2f();
            MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(contourIdx).toArray());

            //Processing on mMOP2f1 which is in type MatOfPoint2f
            double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

            //Convert back to MatOfPoint
            MatOfPoint points = new MatOfPoint(approxCurve.toArray());

            // Get bounding rect of contour
            Rect rect = Imgproc.boundingRect(points);
            Imgproc.rectangle(origImageMatrix, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0, 255), 3);
            if ((rect.y + rect.height > origImageMatrix.rows()) || (rect.x + rect.width > origImageMatrix.cols())) {
                continue;
            }


            MatOfDouble matMean = new MatOfDouble();
            MatOfDouble matStd = new MatOfDouble();
            Double mean;
            Double std;

            roiImage = imgToProcess.submat(rect.y,rect.y + rect.height ,rect.x,rect.x + rect.width );
            int xCord = rect.x;
            Core.copyMakeBorder(roiImage, roiImage, 100, 100, 100, 100, Core.BORDER_ISOLATED);

            Mat resizeimage = new Mat();
            Size sz = new Size(28, 28);
            Imgproc.resize(roiImage, roiImage, sz);


            Core.meanStdDev(roiImage, matMean, matStd);
            mean = matMean.toArray()[0];
            std = matStd.toArray()[0];

            Imgproc.threshold(roiImage, roiImage, mean, 255, Imgproc.THRESH_BINARY_INV);
            Bitmap tempImage = Bitmap.createBitmap(roiImage.cols(), roiImage.rows(), conf);
            Utils.matToBitmap(roiImage, tempImage);
            RoiObject roiObject = new RoiObject(xCord,tempImage);
            mRoiImages.add(roiObject);

            //Uncomment only for debugging
            //mIOUtils.saveImage(tempImage,"roi.jpg");
        }


        // To read the digits from left to right - sort as per the X coordinates.
        Collections.sort(mRoiImages);

        //Set the max number of digits to read to 9 (arbitrarily chosen)
        int max = (mRoiImages.size() > 9) ? 9 : mRoiImages.size();
        for (int i = 0; i < max; i++) {
            RoiObject roi = mRoiImages.get(i);
            int [] pixels = getPixelData(roi.bmp);

            //Call the native detectDigit method. This is where the tensorflow graph is invoked.
            // The graph was created using python code (as part of the training).
            int digit = digitDetector.detectDigit(pixels);

            Log.i(TAG, "digit =" + digit);

            ResultDigits.append("" + digit);
        }

        int number = Integer.parseInt(ResultDigits.toString());
        Log.i(TAG,"Number = :" + number);
        mResultText = mNumConverter.numberToWords(number);

        return origImageMatrix;
    }

    public String getResultText() {
        return this.mResultText;
    }

    // Converting the bitmap into pixel array which is sent to the detector (Native code).
    private int [] getPixelData(Bitmap tempImage) {
        Log.d(TAG,"Image Size : " + tempImage.getWidth() + " , " + tempImage.getHeight());
        int [] pixels = new int[tempImage.getWidth() * tempImage.getHeight()];
        tempImage.getPixels(pixels, 0, tempImage.getWidth(), 0, 0, tempImage.getWidth(),tempImage.getHeight());
        int[] retPixels = new int[pixels.length];
        for (int i = 0; i < pixels.length; ++i) {
            // Set 0 for white and 255 for black pixel
            int pix = pixels[i];
            pix = pix & 0xff;
            int b = pix & 0xff;
            retPixels[i] = 0xff - b;
        }
        return retPixels;

    }

    private ArrayList<Rect> filterRectangles(ArrayList<Rect> rects) {
        double sum = 0.0;
        double mean = 0.0;
        for (int i = 0; i < rects.size(); i++) {
            sum += rects.get(i).height;
        }

        mean = sum / rects.size();

        Log.d(TAG, "Mean = " + mean);

        for (int i = 0; i < rects.size(); i++) {
            if (rects.get(i).height < (mean - 5.0)) {
                rects.remove(i);
            }
        }
        return rects;
    }
}
