package gllc.ravore.app.Messaging;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import gllc.ravore.app.Automation.RotateBitmap;
import gllc.ravore.app.Interfaces.StartCamera;
import gllc.ravore.app.MyApplication;
import gllc.ravore.app.Objects.Bracelet;
import gllc.ravore.app.R;

/**
 * Created by bhangoo on 3/31/2016.
 */
public class LoadProfilePhoto {

    AlertDialog.Builder alertadd;
    AlertDialog.Builder alertadd2;
    StartCamera startCamera;

    public LoadProfilePhoto(ImageView giverImage, ImageView receiverImage, boolean amIGiver, Bracelet bracelet, Context context, Context alertDialogContext, StartCamera startCamera){

        alertadd = new AlertDialog.Builder(alertDialogContext);
        alertadd2 = new AlertDialog.Builder(alertDialogContext);
        this.startCamera = startCamera;

        if (amIGiver){
            loadLocalPath(giverImage, bracelet);
            loadOtherPersonAndSetListener(receiverImage, bracelet, "receiver", context);
        }
        else {
            loadLocalPath(receiverImage, bracelet);
            loadOtherPersonAndSetListener(giverImage, bracelet, "giver", context);
        }
    }

    public void loadLocalPath(ImageView imageView, Bracelet bracelet){

        if (MyApplication.file.getFile().exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(MyApplication.file.getPath());
            imageView.setImageBitmap(RotateBitmap.RotateBitmap(myBitmap));
        }

            /*
            Bitmap myBitmap = BitmapFactory.decodeFile(MyApplication.file.getPath());
            try {
                ExifInterface exif = new ExifInterface(MyApplication.file.getPath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                }
                else if (orientation == 3) {
                    matrix.postRotate(180);
                }
                else if (orientation == 8) {
                    matrix.postRotate(270);
                }
                myBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true); // rotating bitmap
                imageView.setImageBitmap(myBitmap);
            }
            catch (Exception e) {
                Log.i("MyActivity", "Exception trying to load bitmap: " + e.getMessage());
            }*/
        

        else {imageView.setImageResource(R.drawable.anon);}

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = { "View Photo","Take Photo", "Choose from Library", "Delete Photo", "Cancel" };
                alertadd2.setTitle(" Photo Options!");
                alertadd2.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        startCamera.StartCamera(items[item].toString());
                    }
                });
                alertadd2.show();
            }
        });
    }

    public void loadOtherPersonAndSetListener(ImageView imageView, final Bracelet bracelet, String giverReceiver, final Context context){
        String userId;
        if (giverReceiver.equals("receiver")){userId = bracelet.getReceiverId();}
        else {userId = bracelet.getGiverId();}

        for (int i = 0; i < MyApplication.allAnon.size(); i++) {
            if (MyApplication.allAnon.get(i).getUserId().equals(userId)) {
                String url = MyApplication.cloudinary.url().format("jpg")
                        .generate("v" + MyApplication.allAnon.get(i).getUrlVersion() + "/" + userId);
                Picasso.with(context).load(url).placeholder(R.drawable.anon).into(imageView);
            }
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater factory = LayoutInflater.from(context);
                View view = factory.inflate(R.layout.full_photo, null);
                ImageView fullImageView = (ImageView) view.findViewById(R.id.fullPhotoImageview);
                alertadd.setView(view);

                String userId;

                if (MyApplication.currentUserIsGiver) {
                    userId = bracelet.getReceiverId();
                } else {
                    userId = bracelet.getGiverId();
                }


                for (int i = 0; i < MyApplication.allAnon.size(); i++) {
                    if (MyApplication.allAnon.get(i).getUserId().equals(userId)) {
                        Picasso.with(context).load(MyApplication.allAnon.get(i).getFullPhotoUrl()).placeholder(R.drawable.placeholder).into(fullImageView);
                    }
                }

                alertadd.setNeutralButton("OK!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int sumthin) {

                    }
                });

                alertadd.show();
            }
        });
    }
/*
    public void startTheCamera(Context context){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                context.startActivityForResult(takePictureIntent, MyApplication.REQUEST_CAMERA);
            }
        }
    }*/
}
