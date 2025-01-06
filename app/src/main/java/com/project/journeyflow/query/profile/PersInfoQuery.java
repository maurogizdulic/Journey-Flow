package com.project.journeyflow.query.profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import com.project.journeyflow.database.User;
import com.project.journeyflow.query.ProfileFragmentQuery;

import java.io.File;
import java.util.Date;

import io.realm.Realm;

public class PersInfoQuery extends ProfileFragmentQuery {

    private final Context context;
    public PersInfoQuery(Context context) {
        super(context);
        this.context = context;
    }

    public void displayProfilePicture(ImageButton imageButton){
        User user = fetchUserData();

        if (user != null) {

            File appDirectory = new File(context.getExternalFilesDir(null), "JourneyFlow");
            Log.d("DirectoryPath", "App Directory: " + appDirectory.getAbsolutePath());

            if (appDirectory.exists() && appDirectory.isDirectory()) {
                File profileImage = new File(appDirectory, "profile_picture.jpg");

                if (profileImage.exists()) {
                    try {
                        Bitmap resizedBitmap = getResizedBitmap(Uri.fromFile(profileImage), 300, 300); // Adjust size as needed
                        Bitmap circularBitmap = getCircularBitmap(resizedBitmap);
                        imageButton.setImageBitmap(circularBitmap);
                    } catch (Exception e) {
                        Log.e("ERROR SHOW PROFILE IMAGE", e.toString());
                        Toast.makeText(context, "Image resize problem", Toast.LENGTH_LONG).show();
                    }

                } else {
                    // Handle case where the image does not exist
                    Log.e("FileCheck", "File does not exist: " + profileImage.getAbsolutePath());
                    imageButton.setImageResource(android.R.drawable.ic_dialog_alert);
                }
            } else {
                Log.e("DirectoryCheck", "Directory does not exist: " + appDirectory.getAbsolutePath());
                // Handle directory does not exist
                Toast.makeText(context, "Cant find image", Toast.LENGTH_LONG).show();
                imageButton.setImageResource(android.R.drawable.ic_dialog_alert);
            }
        }
        else {
            Toast.makeText(context, "Error retrieving profile picture and name", Toast.LENGTH_LONG).show();
        }
    }

    public User getPersonalInfo() {
        return fetchUserData();
    }

    public void updatePersonalInformation(String firstName, String lastName, String username, String gender, String weight, String height, Date dateOfBirth) {
        if (weight.length() > 3) {
            weight = weight.substring(0, weight.length() - 3);
        }

        if (height.length() > 3) {
            height = height.substring(0, height.length() - 3);
        }

        User userData = fetchUserData();
        Realm realm = initializeRealm();

        String finalWeight = weight;
        String finalHeight = height;
        realm.executeTransaction(realm1 -> {
            User user = realm1.where(User.class).equalTo("id", userData.getId()).findFirst();

            if(user != null) {
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setUsername(username);
                user.setGender(gender);
                user.setWeight(Double.parseDouble(finalWeight));
                user.setHeight(Double.parseDouble(finalHeight));
                user.setDateOfBirth(dateOfBirth);
            }
        });

    }

}
