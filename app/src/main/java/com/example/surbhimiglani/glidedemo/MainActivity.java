package com.example.surbhimiglani.glidedemo;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    ImageView profileImageView;
    final private static int REQUEST_CODE_GALLERY = 1;
    final private static int REQUEST_IMAGE_CAPTURE = 2;
    Button loadButton, takeButton;
    Uri selectedImage2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadButton=(Button) findViewById(R.id.button);
        takeButton=(Button) findViewById(R.id.button2);
        profileImageView=(ImageView)findViewById(R.id.imageView);

        takeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}
                        , REQUEST_IMAGE_CAPTURE
                );

            }


        });

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_GALLERY
                    );
                } else {
                    Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickIntent.setType("image/*");

                /* Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                   chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent}); */
                    startActivityForResult(Intent.createChooser(pickIntent, "SELECT"), REQUEST_CODE_GALLERY);
                }


            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    String fileName = "abc.jpg";

                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, fileName);
                    selectedImage2 = getContentResolver()
                            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    values);

                  /*  File file = new File(Environment.getExternalStorageDirectory(),
                            "file" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                    selectedImage = Uri.fromFile(file);
                 */

                    takePictureIntent
                            .putExtra(MediaStore.EXTRA_OUTPUT, selectedImage2);
                    takePictureIntent.putExtra("return-data", true);

                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            } else {
                Toast.makeText(getApplicationContext(), "You dont have permission to access the camera", Toast.LENGTH_SHORT).show();
            }

        } else
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(pickIntent, "SELECT"), REQUEST_CODE_GALLERY);

             /*   Intent gallery_Intent = new Intent(getApplicationContext(), GalleryUtil.class);
                startActivityForResult(gallery_Intent, REQUEST_CODE_GALLERY);   */

            } else {
                Toast.makeText(getApplicationContext(), "You dont have permission to access the gallery", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_GALLERY && data != null) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Sample.jpg");
                Glide.with(getApplicationContext()).load(picturePath).centerCrop().into(profileImageView);

            }
            else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor =
                        managedQuery(selectedImage2, projection, null,
                                null, null);
                int column_index_data = cursor.getColumnIndexOrThrow(
                        MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String picturePath = cursor.getString(column_index_data);
                Glide.with(getApplicationContext()).load(selectedImage2).centerCrop().into(profileImageView);
            }
        }
    }
}
