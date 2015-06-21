package rebamit.basic.activity;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

import rebamit.basic.R;
import rebamit.basic.fragment.DatePickerDialogFragment;
import rebamit.basic.object.Outfit;
import rebamit.basic.repo.OutfitRepo;


public class SearchOutfitActivity extends AppCompatActivity {

    public static ArrayList<Outfit> outfitList = new ArrayList<>();
    public static int dateField = 1;
    protected String mActivityTitle;
    private Button searchButton;
    private EditText field1, field2;
    private RadioButton type;
    private RadioGroup type_group;

    /**
     * On create
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_outfit);

        mActivityTitle = getTitle().toString();

        searchButton = (Button) findViewById(R.id.search_outfit_button);
        field1 = (EditText) findViewById(R.id.new_field1);
        field2 = (EditText) findViewById(R.id.new_field2);
        type_group = (RadioGroup) findViewById(R.id.radio_type);

        field2.setVisibility(View.GONE);

        field1.setHint("Tag");

        type_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(field1.getWindowToken(), 0);
                field1.setInputType(InputType.TYPE_CLASS_TEXT);
                field2.setInputType(InputType.TYPE_CLASS_TEXT);
                switch (radioButton.getText().toString()) {
                    case "Tag":
                        field1.setVisibility(View.VISIBLE);
                        field2.setVisibility(View.GONE);
                        field1.setText("");
                        field2.setText("");
                        field1.setHint("Tag");
                        searchButton.requestFocus();

                        field1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                // do nothing
                            }
                        });

                        field2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                // do nothing
                            }
                        });
                        break;

                    case "Date":
                        field1.setVisibility(View.VISIBLE);
                        field2.setVisibility(View.VISIBLE);
                        field1.setText("");
                        field2.setText("");
                        field1.setHint("Start Date");
                        field2.setHint("End Date");
                        searchButton.requestFocus();

                        field1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (hasFocus) {
                                    dateField = 1;
                                    showDatePickerFragment(v);
                                    searchButton.requestFocus();
                                }
                            }
                        });

                        field2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (hasFocus) {
                                    dateField = 2;
                                    showDatePickerFragment(v);
                                    searchButton.requestFocus();
                                }
                            }
                        });
                        break;
                    default:
                        break;
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {

            /**
             * On click, creates a new outfit and adds it to database
             */
            @Override
            public void onClick(View v) {
                OutfitRepo repo = new OutfitRepo(SearchOutfitActivity.this);

                String field1_text = field1.getText().toString();
                String field2_text = field2.getText().toString();

                int selectedId = type_group.getCheckedRadioButtonId();
                type = (RadioButton) findViewById(selectedId);

                if (type.getText().toString().equals("Tag")) {
                    if (field1_text.equals("")) {
                        Toast.makeText(SearchOutfitActivity.this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
                    } else {
                        outfitList = repo.getOutfitByTag(field1_text);
                    }
                } else if (type.getText().toString().equals("Date")) {
                    if (field1_text.equals("") || field2_text.equals("")) {
                        Toast.makeText(SearchOutfitActivity.this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
                    } else {
                        outfitList = repo.getOutfitByDate(field1_text, field2_text);
                    }
                }

                if (outfitList.size() == 0) {
                    Toast.makeText(SearchOutfitActivity.this, "No Results", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DisplayResultsActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public void showDatePickerFragment(View v) {
        DialogFragment newFragment = new DatePickerDialogFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_outfit, menu);
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
