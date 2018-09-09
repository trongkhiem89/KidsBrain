package com.kid.brain.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by khiemnt on 12/20/16.
 */

public class BitmapUtils {
    private final static BitmapUtils bitmapUtil = new BitmapUtils();
    public final static BitmapUtils getInstance(){
        return bitmapUtil;
    }

    public byte[] convertBitmapToByte(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public Bitmap compressBytesToBitmap(byte[] bytes){
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

}
