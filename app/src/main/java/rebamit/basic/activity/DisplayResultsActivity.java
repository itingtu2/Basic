package rebamit.basic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import rebamit.basic.R;
import rebamit.basic.object.Outfit;


public class DisplayResultsActivity extends AppCompatActivity {

    protected Button mCancelButton;
    protected ListView mListViewOutfit;
    protected SimpleAdapter mAdapterOutfit;
    protected ArrayList<HashMap<String, String>> outfitListItem = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_results);

        mCancelButton = (Button) findViewById(R.id.cancel_button);
        mListViewOutfit = (ListView) findViewById(R.id.outfit_list_view);

        mListViewOutfit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Outfit outfit = SearchOutfitActivity.outfitList.get(position);
                Intent intent = new Intent(getApplicationContext(), DisplayOutfitActivity.class);
                intent.putExtra("Tag", outfit.tag);
                intent.putExtra("Date", new SimpleDateFormat("MM-dd-yyyy").format(outfit.date));
                intent.putExtra("Picture", outfit.picture);
                startActivity(intent);
            }
        });

        for (int i = 0; i < SearchOutfitActivity.outfitList.size(); i++) {
            Outfit outfit = SearchOutfitActivity.outfitList.get(i);
            HashMap<String, String> item = new HashMap<>();
            item.put("line1", outfit.tag);
            item.put("line2", new SimpleDateFormat("MM-dd-yyyy").format(outfit.date));
            outfitListItem.add(item);
        }
        mAdapterOutfit = new SimpleAdapter(DisplayResultsActivity.this, outfitListItem,
                android.R.layout.simple_list_item_2, new String[]{"line1", "line2"},
                new int[]{android.R.id.text1, android.R.id.text2});
        mListViewOutfit.setAdapter(mAdapterOutfit);

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_results, menu);
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
