package canda.com.christmaslights;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class GreenwoodPL extends ActionBarActivity {

    static final ArrayList<String> houseListStreet = new ArrayList<String>();
    List<ParseObject> categories;
    AlertDialog levelDialog;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greenwood_pl);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "1YyWozFYPcEAEfP2XplZ9haCUGPUwIvRY9leIJyJ", "gKfEKAeFpTwoum3oAb9zb26jna6M6VBlGy6JJCWv");
        findViewById(R.id.houseListView);
        /*for(int i=1; i<=10; i++) {
            ParseObject house = new ParseObject("Houses");
            house.put("Street", "GreenwoodPl");
            house.put("Number", i);
            house.put("Rating", "None");
            house.saveInBackground();
        }*/
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                "Houses");
        query.orderByAscending("Street");
        query.orderByAscending("Number");
        try {
            categories = query.find();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
        populate();
    }

    public void populate() {
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, houseListStreet);
        arrayAdapter.clear();
        ListView lv = (ListView) findViewById(R.id.houseListView);
        lv.setAdapter(arrayAdapter);
        for(ParseObject a: categories) {
            int num = a.getInt("Number");
            int length = (int)(Math.log10(num)+1);
            String address =  a.getInt("Number") + " " + a.getString("Street");
            for(int i=0; i<8-length; i++) {
                address = address + "\t";
            }
            address = address + "Rating: " + a.getString("Rating");
            houseListStreet.add(address);
        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String item = ((TextView) view).getText().toString();
                String[] splitted = item.split("\\s+");
                String number = splitted[0];
                String street = splitted[1];
                int numberInt = Integer.parseInt(number);
                for (ParseObject a : categories) {
                    if (a.getString("Street").equals(street) && a.getInt("Number") == numberInt) {
                        String objID = a.getObjectId();
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Houses");
                        query.getInBackground(objID, new GetCallback<ParseObject>() {
                            public void done(ParseObject house, com.parse.ParseException e) {
                                if (e == null) {
                                    updateScore(house);
                                }
                            }
                        });

                    }
                }
            }
        });
    }

    public void updateScore(final ParseObject house) {
        final CharSequence[] items = {" Low "," Med/Low "," Med "," High ", " Ganj ", " None"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Rating");
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        house.put("Rating", "Low");
                        break;
                    case 1:
                        house.put("Rating", "Med/Low");
                        break;
                    case 2:
                        house.put("Rating", "Med");
                        break;
                    case 3:
                        house.put("Rating", "High");
                        break;
                    case 4:
                        house.put("Rating", "Ganj");
                        break;
                    case 5:
                        house.put("Rating", "None");
                        break;
                }
                levelDialog.dismiss();
                house.saveInBackground();
                refresh();
            }
        });
        levelDialog = builder.create();
        levelDialog.show();
        refresh();
    }


    public void refresh() {
        houseListStreet.clear();
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, houseListStreet);
        arrayAdapter.clear();
        ListView lv = (ListView) findViewById(R.id.houseListView);
        lv.setAdapter(arrayAdapter);
        for(ParseObject a: categories) {
            int num = a.getInt("Number");
            int length = (int)(Math.log10(num)+1);
            String address =  a.getInt("Number") + " " + a.getString("Street");
            for(int i=0; i<8-length; i++) {
                address = address + "\t";
            }
            address = address + "Rating: " + a.getString("Rating");
            houseListStreet.add(address);
        }
    }
    public void mapLaunch(View view) {
        Intent map = new Intent(this, MapsActivity.class);
        startActivity(map);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_greenwood_pl, menu);
        return true;
    }

    public void parseRetrieveAll(View view) throws ParseException {
        findViewById(R.id.houseListView);
        ParseQuery query = new ParseQuery("House");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> categories, com.parse.ParseException e) {
                if (e == null) {
                    for (int i = 0; i < categories.size(); i++) {
                        String c = categories.get(i).getString("Street");
                        houseListStreet.add(c);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        ListView lv = (ListView) findViewById(R.id.houseListView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, houseListStreet);
        lv.setAdapter(arrayAdapter);
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
        if (id == R.id.map_settings) {
            Intent map = new Intent(this, MapsActivity.class);
            startActivity(map);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
