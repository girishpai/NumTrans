package com.udacity.mlndcapstone.numtrans;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by girishpai on 11/4/16.
 * Utils class to perform file handling
 */
public class IOUtils {

    private static final String TAG = "IOUtils";

    public void saveImage(Bitmap bm, String imageName) {
        FileOutputStream outStream;
        String extStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File file = new File(extStorageDirectory, imageName);

        if (true) {

            try {
                outStream = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.flush();
                outStream.close();
                Log.d(TAG, "Adding Image : " + imageName);
                //photoNames.add(imageName);
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found : " + file.getAbsolutePath());
            } catch (IOException e) {
                Log.d(TAG, "IO Exception");
                e.printStackTrace();
            }
        }
    }

    public void deleteImage(String imageName) {
        String extStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File file = new File(extStorageDirectory, imageName);
        if (file.exists()) {
            file.delete();
        }
    }

    public File createImageFile(String imageFilename)  {
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        Log.d(TAG,"Storage Directory : " + storageDirectory.toString());
        File image = new File(storageDirectory, imageFilename);
        return image;
    }

    public Bitmap getSavedImage(String imageName) {
        String extStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File temp = new File(extStorageDirectory,imageName);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        String fileName = temp.getAbsolutePath();
        Bitmap bitmap = BitmapFactory.decodeFile(fileName, options);

        return getResizedBitmap(bitmap,640,480);

    }

    // Resizes a bitmap to new size.
    public static Bitmap getResizedBitmap(Bitmap image, int newHeight, int newWidth) {
        int width = image.getWidth();
        int height = image.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }

    //Depending on phone, the photo taken on the camera might be in landscape or portrait.
    // This method converts the photo into portrait and returns a reduced size image to take care
    // of low memory situations.
    public Bitmap getCameraPhoto(String filename) {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        String extStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File temp = new File(extStorageDirectory,filename);
        String file = temp.getAbsolutePath();
        BitmapFactory.decodeFile(file, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(file, opts);
        bm = getResizedBitmap(bm,2500,2500);
        bounds.outHeight = 2500;
        bounds.outWidth = 2500;
        try {
            ExifInterface exif = new ExifInterface(file);


            String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;

            int rotationAngle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

            Matrix matrix = new Matrix();
            matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
            return rotatedBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }


}
