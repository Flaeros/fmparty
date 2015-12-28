package ru.fmparty.utils;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

public class ImageHelper {

    private static final String TAG = "FlashMob ImageHelper";

    public static String getPath(Uri uri, Activity activity) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String imagePath = cursor.getString(column_index);

        return imagePath;
    }

    public static void selectImageFromGallery(Activity activity){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        activity.startActivityForResult(photoPickerIntent, 1);
    }

    public static String onActivityResultHelp(Intent data, Activity activity, ImageView imageView) {
        Uri selectedImage = data.getData();
        String filePath = getPath(selectedImage, activity);

        Log.d(TAG, "filePath = " + filePath);
        String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
        imageView.setImageURI(selectedImage);

        try
        {
            if (file_extn.equals("img") || file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("gif") || file_extn.equals("png")) {
                //FINE
            } else {
                //NOT IN REQUIRED FORMAT
                Log.d(TAG, " Wrong format. file_extn = " + file_extn);
                filePath = null;
            }
        } catch (
                Exception e
                )

        { //FileNotFoundException
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return filePath;
    }

}
