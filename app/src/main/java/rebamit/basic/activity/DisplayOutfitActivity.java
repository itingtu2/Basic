package rebamit.basic.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import rebamit.basic.R;


/**
 * Activity to display the details of a particular outfit including pictures
 */
public class DisplayOutfitActivity extends AppCompatActivity {

    protected Button mCancelButton;
    protected String mPhotoPath;
    private ImageView mImageView;
    private String mTag, mDate;
    private TableLayout mOutfitTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_outfit);

        mCancelButton = (Button) findViewById(R.id.cancel_button);
        mImageView = (ImageView) findViewById(R.id.imageView_pic);
        mOutfitTable = (TableLayout) findViewById(R.id.outfit_table);

        Intent intent = getIntent();
        mTag = intent.getStringExtra("Tag");
        mDate = intent.getStringExtra("Date");
        mPhotoPath = intent.getStringExtra("Picture");

        displayOutfit();

        setPic(mPhotoPath);

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void setPic(String mPhotoPath) {

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap myBitmap = BitmapFactory.decodeFile(mPhotoPath, bmOptions);

        try {
            ExifInterface exif = new ExifInterface(mPhotoPath);
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

    private void displayOutfit() {
        DecimalFormat decFormat = new DecimalFormat("0.00");

        TableRow label = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        label.setLayoutParams(lp);
        label.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView tagLabel = new TextView(this);
        tagLabel.setLayoutParams(lp);
        tagLabel.setGravity(Gravity.CENTER_HORIZONTAL);
        tagLabel.setPadding(10, 0, 10, 0);
        tagLabel.setBackgroundColor(Color.parseColor("#006833"));
        tagLabel.setTextColor(Color.WHITE);
        tagLabel.setText("TAG");


        TextView dateLabel = new TextView(this);
        dateLabel.setLayoutParams(lp);
        dateLabel.setGravity(Gravity.CENTER_HORIZONTAL);
        dateLabel.setPadding(10, 0, 10, 0);
        dateLabel.setBackgroundColor(Color.parseColor("#006833"));
        dateLabel.setTextColor(Color.WHITE);
        dateLabel.setText("DATE");

        label.addView(tagLabel);
        label.addView(dateLabel);

        mOutfitTable.addView(label, 0);
        mOutfitTable.setColumnShrinkable(3, true);
        mOutfitTable.setColumnStretchable(3, true);

        TableRow contents = new TableRow(this);
        contents.setLayoutParams(lp);
        contents.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView tag = new TextView(this);
        tag.setLayoutParams(lp);
        tag.setPadding(10, 0, 10, 0);
        tag.setText(mTag);

        TextView date = new TextView(this);
        date.setLayoutParams(lp);
        date.setPadding(10, 0, 10, 0);
        date.setText(mDate);

        contents.addView(tag);
        contents.addView(date);

        mOutfitTable.addView(contents, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_outfit, menu);
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
}
