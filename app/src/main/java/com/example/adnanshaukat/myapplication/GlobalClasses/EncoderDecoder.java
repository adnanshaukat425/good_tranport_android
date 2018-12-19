package com.example.adnanshaukat.myapplication.GlobalClasses;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.adnanshaukat.myapplication.R;

/**
 * Created by AdnanShaukat on 18/12/2018.
 */

public class EncoderDecoder {

    public static Bitmap getDecodeImage(String encodedImage){
        if (encodedImage.isEmpty()) {
            return null;
        } else {
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            return decodedByte;
        }
    }
}
