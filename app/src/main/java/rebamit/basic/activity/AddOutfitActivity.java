package rebamit.basic.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rebamit.basic.R;
import rebamit.basic.object.Outfit;
import rebamit.basic.picture.AlbumStorageDirFactory;
import rebamit.basic.picture.BaseAlbumDirFactory;
import rebamit.basic.picture.FroyoAlbumDirFactory;
import rebamit.basic.repo.OutfitRepo;

/**
 * Activity to add outfits to the database including taking pictures and scanning them
 */
public class AddOutfitActivity extends AppCompatActivity {

    private static final int ACTION_TAKE_PHOTO = 1;
    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private Button saveButton;
    private Button cancelButton;
    private Button picButton;
    private EditText tags;
    private OutfitRepo repo = new OutfitRepo(AddOutfitActivity.this);
    private Outfit o = new Outfit();
    private ImageView mImageView;
    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath;
    Button.OnClickListener mTakePicOnClickListener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakePictureIntent(ACTION_TAKE_PHOTO);
                }
            };
    private String mPhotoPath = null;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    /**
     * Indicates whether the specified action can be used as an intent. This
     * method queries the package manager for installed packages that can
     * respond to an intent with the specified action. If no suitable package is
     * found, this method returns false.
     * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
     *
     * @param context The application's environment.
     * @param action  The Intent action to check for availability.
     * @return True if an Intent with the specified action can be sent and
     * responded to, false otherwise.
     */
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * On create, links xml inputs to variables
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_outfit);
        saveButton = (Button) findViewById(R.id.save_outfit);
        cancelButton = (Button) findViewById(R.id.cancel_outfit);
        tags = (EditText) findViewById(R.id.new_tags);
        mImageView = (ImageView) findViewById(R.id.imageView_pic);
        mImageBitmap = null;

        picButton = (Button) findViewById(R.id.pic_button);
        setBtnListenerOrDisable(
                picButton,
                mTakePicOnClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }

        saveButton.setOnClickListener(new View.OnClickListener() {

            /**
             * On click, creates a new outfit and adds it to database
             */
            @Override
            public void onClick(View v) {
                if (mPhotoPath == null)
                    Toast.makeText(AddOutfitActivity.this, "Please take a picture", Toast.LENGTH_SHORT).show();
                else {
                    o.tag = tags.getText().toString();
                    o.picture = mPhotoPath;
                    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
                    try {
                        o.date = sdf.parse(sdf.format(new Date()));
                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }
                    repo.insert(o);
                    Toast.makeText(AddOutfitActivity.this, "Outfit Added!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        /**
         * Redirects to main activity
         */
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Method for saving the picture taken
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTION_TAKE_PHOTO: {
                if (resultCode == RESULT_OK) {
                    if (mCurrentPhotoPath != null) {
                        setPic();
                        galleryAddPic();
                        mPhotoPath = mCurrentPhotoPath;
                        mCurrentPhotoPath = null;
                    }
                }
            }
        }
    }

    // Some lifecycle callbacks so that the image can survive orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
        outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
        mImageView.setImageBitmap(mImageBitmap);
        mImageView.setVisibility(
                savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ?
                        ImageView.VISIBLE : ImageView.INVISIBLE
        );
    }

    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.album_name);
    }

    /**
     * Get the directory for saving the file
     */
    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());
            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("Camera", "failed to create directory");
                        return null;
                    }
                }
            }
        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    /**
     * Method for saving the image file
     * @return the image saved as a File
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        return File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    /**
     * scales the photo down
     */
    private void setPic() {

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap myBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        try {
            ExifInterface exif = new ExifInterface(mCurrentPhotoPath);
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
        }
        catch (Exception e) {

        }
        ImageView img = (ImageView) findViewById(R.id.imageView_pic);
        img.setImageBitmap(myBitmap);

    }

    /**
     * Adds the new picture to the phones gallery
     */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    /**
     * Function that opens the camera and saves the image into a file
     * @param actionCode
     */
    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File f;
        try {
            f = setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            mCurrentPhotoPath = null;
        }

        startActivityForResult(takePictureIntent, actionCode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_outfit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Checks whether intent is available and disables listener if it is not
     * @param btn btn
     * @param onClickListener onClickListener
     * @param intentName intentName
     */
    private void setBtnListenerOrDisable(Button btn, Button.OnClickListener onClickListener, String intentName) {
        if (isIntentAvailable(this, intentName)) {
            btn.setOnClickListener(onClickListener);
        } else {
            btn.setText(getText(R.string.cannot).toString() + " " + btn.getText());
            btn.setClickable(false);
        }

    }
}
