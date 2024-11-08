package com.project.journeyflow.profile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;

import java.io.InputStream;

public class ProfileImage {

    /*
    private Bitmap getResizedBitmap(Uri uri, int targetWidth, int targetHeight) throws Exception {
        InputStream input = getContentResolver().openInputStream(uri);

        // First, decode with inJustDecodeBounds=true to get the dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(input, null, options);
        input.close();

        // Calculate inSampleSize to reduce memory usage
        options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight);
        options.inJustDecodeBounds = false;

        // Decode with inSampleSize set
        input = getContentResolver().openInputStream(uri);
        Bitmap resizedBitmap = BitmapFactory.decodeStream(input, null, options);
        input.close();

        return resizedBitmap;
    }

    // Method to calculate the appropriate sample size
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    // Method to create a circular bitmap
    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        float radius = size / 2f;
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);

        canvas.drawCircle(radius, radius, radius, paint);
        return output;
    }

     */

}
