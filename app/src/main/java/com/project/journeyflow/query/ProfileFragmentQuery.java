package com.project.journeyflow.query;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.journeyflow.database.User;

import java.io.File;

public class ProfileFragmentQuery extends Query{
    private final Context context;
    public ProfileFragmentQuery(Context context) {
        super(context);
        this.context = context;
    }

    public void displayNameAndPicture(ImageView imageView, TextView textViewName) {
        User user = fetchUserData();

        if (user != null) {
            textViewName.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));

            File appDirectory = new File(context.getExternalFilesDir(null), "JourneyFlow");
            Log.d("DirectoryPath", "App Directory: " + appDirectory.getAbsolutePath());

            if (appDirectory.exists() && appDirectory.isDirectory()) {
                File profileImage = new File(appDirectory, "profile_picture.jpg");

                if (profileImage.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(profileImage.getAbsolutePath());
                    imageView.setImageBitmap(bitmap);
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
}
