package com.project.journeyflow.query;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.journeyflow.database.User;

import java.io.File;
import java.io.InputStream;

public class ProfileFragmentQuery extends Query{
    private final Context context;
    public ProfileFragmentQuery(Context context) {
        super(context);
        this.context = context;
    }

    public void displayNameAndPicture(ImageView imageView, TextView textViewName, TextView textViewUsername) {
        User user = fetchUserData();

        if (user != null) {
            textViewName.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
            textViewUsername.setText(String.format(user.getUsername()));

            File appDirectory = new File(context.getExternalFilesDir(null), "JourneyFlow");
            Log.d("DirectoryPath", "App Directory: " + appDirectory.getAbsolutePath());

            if (appDirectory.exists() && appDirectory.isDirectory()) {
                File profileImage = new File(appDirectory, "profile_picture.jpg");

                if (profileImage.exists()) {
                    try {
                        Bitmap resizedBitmap = getResizedBitmap(Uri.fromFile(profileImage), 300, 300); // Adjust size as needed
                        Bitmap circularBitmap = getCircularBitmap(resizedBitmap);
                        imageView.setImageBitmap(circularBitmap);
                    } catch (Exception e) {
                        Log.e("ERROR SHOW PROFILE IMAGE", e.toString());
                        Toast.makeText(context, "Image resize problem", Toast.LENGTH_LONG).show();
                    }

                    /*
                    Bitmap bitmap = BitmapFactory.decodeFile(profileImage.getAbsolutePath());
                    imageView.setImageBitmap(bitmap);

                     */
                } else {
                    // Handle case where the image does not exist
                    Log.e("FileCheck", "File does not exist: " + profileImage.getAbsolutePath());
                    imageView.setImageResource(android.R.drawable.ic_dialog_alert);
                }
            } else {
                Log.e("DirectoryCheck", "Directory does not exist: " + appDirectory.getAbsolutePath());
                // Handle directory does not exist
                Toast.makeText(context, "Cant find image", Toast.LENGTH_LONG).show();
                imageView.setImageResource(android.R.drawable.ic_dialog_alert);
            }
        }
        else {
            Toast.makeText(context, "Error retrieving profile picture and name", Toast.LENGTH_LONG).show();
        }
    }

    // Method to resize the selected image
    protected Bitmap getResizedBitmap(Uri uri, int targetWidth, int targetHeight) throws Exception {
        InputStream input = context.getContentResolver().openInputStream(uri);

        // First, decode with inJustDecodeBounds=true to get the dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(input, null, options);
        input.close();

        // Calculate inSampleSize to reduce memory usage
        options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight);
        options.inJustDecodeBounds = false;

        // Decode with inSampleSize set
        input = context.getContentResolver().openInputStream(uri);
        Bitmap resizedBitmap = BitmapFactory.decodeStream(input, null, options);
        input.close();

        return resizedBitmap;
    }

    // Method to calculate the appropriate sample size
    protected int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
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
    protected Bitmap getCircularBitmap(Bitmap bitmap) {
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
}
