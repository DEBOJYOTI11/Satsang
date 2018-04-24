package jeeryweb.satsang.Actvities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.View.OnClickListener;

import java.util.ArrayList;
import java.util.List;

import jeeryweb.satsang.Data.FileReader;
import jeeryweb.satsang.R;

public class SearchActivity extends AppCompatActivity {

    private Spinner spinnerZone,spinnerDistrict;
    private Button butt;
    FileReader fileReader;
    private final String Tag = SearchActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        fileReader = new FileReader();

        spinnerZone = (Spinner) findViewById(R.id.spinner_zone);
        spinnerDistrict = (Spinner) findViewById(R.id.spinner_district);
        butt = (Button)findViewById(R.id.btnSubmit);

        butt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(SearchActivity.this,
                        "OnClickListener : " +
                                "\nSpinner 1 : "+ String.valueOf(spinnerDistrict.getSelectedItem()) +
                                "\nSpinner 2 : "+ String.valueOf(spinnerZone.getSelectedItem()),
                        Toast.LENGTH_SHORT).show();

                displayPrayingTimeofSelectedValues(String.valueOf(spinnerZone.getSelectedItem()),String.valueOf(spinnerDistrict.getSelectedItem()));
            }

        });

        addDataToSpinnerZone();
    }
    public void addDataToSpinnerZone(){
        fileReader.read1(this);

        List<String> list = new ArrayList<String>();

        list = fileReader.getAllZones();

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerZone.setAdapter(dataAdapter);
        spinnerZone.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }
    public void addDataToSpinnerDistrict(String s){
        List<String> list = new ArrayList<String>();
        list = fileReader.getAllDistrictsOfAZone(s);
        if(list==null){

            Log.e(Tag, "List is null");
            return;
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistrict.setAdapter(dataAdapter);
        spinnerDistrict.setOnItemSelectedListener(new CustomOnItemSelectedListenerD());
    }
    public void displayPrayingTimeofSelectedValues(String zone, String District){

    }

    public class CustomOnItemSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            String selected = parent.getItemAtPosition(pos).toString();
            Toast.makeText(parent.getContext(),
                    "Selected Zone is  : " + selected,
                    Toast.LENGTH_SHORT).show();
            Log.e(Tag, "Selected is : "+selected);
            SearchActivity.this.addDataToSpinnerDistrict(selected);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }
    public class CustomOnItemSelectedListenerD implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            String selected = parent.getItemAtPosition(pos).toString();
            Toast.makeText(parent.getContext(),
                    "Selected district is : " + selected,
                    Toast.LENGTH_SHORT).show();
            Log.e(Tag, "Selected D is : "+selected);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }
}
