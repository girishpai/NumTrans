package com.udacity.mlndcapstone.numtrans;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by girishpai on 11/4/16.
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
        return bitmap;

    }




}
