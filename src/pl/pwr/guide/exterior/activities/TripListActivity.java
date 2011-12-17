package pl.pwr.guide.exterior.activities;

import java.util.ArrayList;
import java.util.List;

import pl.pwr.guide.R;
import pl.pwr.guide.exterior.model.Trip;
import pl.pwr.guide.exterior.providers.PoiProvider;
import pl.pwr.guide.exterior.providers.TripProvider;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class TripListActivity extends Activity {

	private static List<Trip> tripList = new ArrayList<Trip>();
	
	private ImageView addTripButton;
	private ListView tripsListView;
	
	private Trip selectedTrip;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.trip_list);
		
		findViews();
		setListeners();
		
		tripsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> _av, View _v, int _index,
					long _id) {
				selectedTrip = tripList.get(_index);
				//Intent intent = new Intent(getBaseContext(), NewSubject.class);
				//intent.putExtra("subject", selectedSubject);
				//startActivityForResult(intent, ADD_ACTION);
			}
		});
		
		tripList.add(new Trip(1,"Test","2,3"));
		tripList.add(new Trip(2,"Test2","3,4,5"));
		addTripToDB(tripList);
		
		//loadTripsFromProvider();
				
		tripsListView.setAdapter(new ArrayAdapter<String>(this, R.layout.trip_item, loadTripsFromProvider()));
	}
	
	private void findViews() {
		addTripButton = (ImageView) this.findViewById(R.id.addTripButton);
		tripsListView = (ListView) this.findViewById(R.id.tripsListView);

	}

	private void setListeners() {
		addTripButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				//Intent intent = new Intent(getBaseContext(), NewSubject.class);
				//startActivityFocrResult(intent, ADD_ACTION);
				
			}
		});
	}
	
	private String[] loadTripsFromProvider() {

		tripList.clear();
		ContentResolver cr = getContentResolver();

		Cursor c = cr.query(TripProvider.CONTENT_URI, null, null, null, null);

		if (c.moveToFirst()) {
			do {
				Trip trip = new Trip();
				trip.setId(c.getLong(TripProvider.ID_COLUMN));
				trip.setName(c.getString(TripProvider.NAME_COLUMN));
				trip.setPoiList(c.getString(TripProvider.POIS_COLUMN));
				
				tripList.add(trip);
			} while (c.moveToNext());
		}
		c.close();
		
		String[] trips = new String[tripList.size()];
		for (int i=0;i<tripList.size();i++){
			
			trips[i] = tripList.get(i).getName();
		}
		
		return trips;
	}
	
	private boolean addTripToDB(List<Trip> tripList) {
		ContentResolver cr = getContentResolver();

		for (Trip trip : tripList) {
			String w = TripProvider.KEY_ID + " = " + trip.getId();

			Cursor c = cr.query(TripProvider.CONTENT_URI, null, w, null, null);
			int dbCount = c.getCount();
			c.close();

			if (dbCount == 0) {
				ContentValues values = new ContentValues();

				values.put(TripProvider.KEY_ID, trip.getId());
				values.put(TripProvider.KEY_NAME, trip.getName());
				values.put(TripProvider.KEY_POIS, trip.getPoiList());

				cr.insert(TripProvider.CONTENT_URI, values);
			}
		}
		return false;
	}
}
