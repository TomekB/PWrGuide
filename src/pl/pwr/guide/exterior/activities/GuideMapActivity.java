package pl.pwr.guide.exterior.activities;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pl.pwr.guide.R;
import pl.pwr.guide.exterior.model.Image;
import pl.pwr.guide.exterior.model.Poi;
import pl.pwr.guide.exterior.model.overlays.DirectionPathOverlay;
import pl.pwr.guide.exterior.model.overlays.MyPositionOverlay;
import pl.pwr.guide.exterior.model.overlays.PoiOverlay;
import pl.pwr.guide.exterior.providers.PoiProvider;
import pl.pwr.guide.exterior.utils.SaxFeedParser;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class GuideMapActivity extends MapActivity {

	private MapController mapController;
	private MyPositionOverlay positionOverlay;
	// Define the new menu item identifiers
	static final private int MENU_DOWNLOAD_DATA = Menu.FIRST;
	static final private int MENU_PREFERENCES = Menu.FIRST + 1;
	static final private int MENU_TRIPS = Menu.FIRST + 2;
	static final private int SHOW_PREFERENCES = 1;
	
	private boolean isMapView = false;
	private int zoomRatio = 18;
	private MapView myMapView;
	private ProgressDialog progressDialog;
	
	private static List<Poi> poisList = new ArrayList<Poi>();
	private static List<Overlay> overlays = new ArrayList<Overlay>();
	
	private static final int NETWORK_DISABLED_DIALOG = 1;
	private static final int DIALOG_DOWNLOAD_PROGRESS = 2;
	
	private static final double WROCLAW_LAT = 51.110;
	private static final double WROCLAW_LON = 17.030;
	
	private SharedPreferences prefs;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.map);
		
		prefs = getSharedPreferences(Preferences.USER_PREFERENCE, Activity.MODE_PRIVATE);;

		myMapView = (MapView) findViewById(R.id.myMapView);
		mapController = myMapView.getController();

		// Check whether is connected to network
		if (!isConnectedToNetwork()) {
			showDialog(NETWORK_DISABLED_DIALOG);
		}
						
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		String provider = selectBestProvider(locationManager);
		Location location = locationManager.getLastKnownLocation(provider);
		
		// If there is no last known location, set location = Wroc³aw
		if (location == null){
			
			location = new Location(provider);
			location.setLatitude(WROCLAW_LAT);
			location.setLongitude(WROCLAW_LON);
		}
		
	    updateFromPreferences();
		// Add the MyPositionOverlay
		overlays = myMapView.getOverlays();
		positionOverlay = new MyPositionOverlay(getResources().getDrawable(R.drawable.marker2), myMapView);
		updateWithNewLocation(location);
		positionOverlay.addOverlay();
		overlays.add(positionOverlay);
		// Add the PoiOverlay
		drawPoiOverlays();
		
		//Refreshes current location
		locationManager.requestLocationUpdates(provider, 2000, 10,
				locationListener);	
	}
		
	@Override
	public Dialog onCreateDialog(int id) {
		switch (id) {
		case (NETWORK_DISABLED_DIALOG):
			
			AlertDialog.Builder ndDialog = new AlertDialog.Builder(this);
			ndDialog.setMessage(getText(R.string.wifi_activate_dialogbox_question));
			ndDialog.setCancelable(true);
			ndDialog.setIcon(R.drawable.ic_dialog_alert);
			ndDialog.setTitle(R.string.wifi_activate_dialogbox_title);
			ndDialog.setPositiveButton(getText(R.string.positive_answer),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							startActivityForResult(
									new Intent(
											android.provider.Settings.ACTION_WIFI_SETTINGS),
									0);
						}
					});
			ndDialog.setNegativeButton(getText(R.string.negative_answer),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			return ndDialog.create();
		case DIALOG_DOWNLOAD_PROGRESS:
			
			progressDialog = new ProgressDialog(this);
			progressDialog
					.setMessage(getText(R.string.download_progress));
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(true);
			progressDialog.show();
			return progressDialog;
		default:
			return null;
		}
	}

	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		public void onProviderDisabled(String provider) {
			updateWithNewLocation(null);
		}

		public void onProviderEnabled(String provider) {
			//TODO ?
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			//TODO ?
		}
	};

	/** Update the map with a new location */
	private void updateWithNewLocation(Location location) {

		String addressString = "No address found";

		if (location != null) {
			// Update my location marker
			positionOverlay.setLocation(location);

			// Update the map location.
			Double geoLat = location.getLatitude() * 1E6;
			Double geoLng = location.getLongitude() * 1E6;
			GeoPoint point = new GeoPoint(geoLat.intValue(), geoLng.intValue());

			mapController.animateTo(point);

			double lat = location.getLatitude();
			double lng = location.getLongitude();
			//BigDecimal latitude = new BigDecimal(lat).setScale(2,BigDecimal.ROUND_HALF_UP);
			//BigDecimal longitude = new BigDecimal(lng).setScale(2,BigDecimal.ROUND_HALF_UP);

			Geocoder gc = new Geocoder(this, Locale.getDefault());
			try {
				List<Address> addresses = gc.getFromLocation(lat, lng, 1);
				StringBuilder sb = new StringBuilder();
				if (addresses.size() > 0) {
					Address address = addresses.get(0);

					for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
						sb.append(address.getAddressLine(i)).append("\n");
					String[] addressConstraints = new String[3];
					addressConstraints[0] = address.getLocality();
					addressConstraints[1] = address.getPostalCode();
					addressConstraints[2] = address.getCountryName();
					for (int i = 0; i < addressConstraints.length; i++)
						if (addressConstraints[i] != null)
							sb.append(addressConstraints[i]).append("\n");
				}
				addressString = sb.toString();
			} catch (IOException e) {
			}
		} else {
			addressString = "No location found";
		}
		positionOverlay.setLocationInfo(addressString);
	}
	
	private void drawPoiOverlays(){
		
		PoiOverlay itemizedOverlay = new PoiOverlay(getResources().getDrawable(R.drawable.marker), myMapView);		
		loadPoisFromProvider();
		
		for(Poi poi : poisList){
			
			Log.d("POI", poi.toString());
			itemizedOverlay.addOverlay(poi);
		}
		overlays.add(itemizedOverlay);
		
		List<GeoPoint> geoPoints =  itemizedOverlay.getGeoPoints();
		
		overlays.add(new DirectionPathOverlay(geoPoints.get(0),geoPoints.get(6)));
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// Create and add new menu items.
		MenuItem itemDownloadData = menu.add(0, MENU_DOWNLOAD_DATA, Menu.NONE ,R.string.download);
		MenuItem itemPrefs = menu.add(0, MENU_PREFERENCES, Menu.NONE,
				R.string.menu_preferences);
		MenuItem itemTrip= menu.add(0, MENU_TRIPS, Menu.NONE,
				R.string.menu_trips);

		// Assign icons
		itemDownloadData.setIcon(R.drawable.ic_menu_refresh);
		itemPrefs.setIcon(R.drawable.ic_menu_preferences);
		itemTrip.setIcon(R.drawable.ic_menu_preferences);
		return true;
	}

	/** Process the options menu item selection */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case (MENU_DOWNLOAD_DATA): {

			if(isConnectedToNetwork()) {
				showDialog(DIALOG_DOWNLOAD_PROGRESS);
				getData();
			}
			else {
				showDialog(NETWORK_DISABLED_DIALOG);
			}
			return true;
		}
		case (MENU_PREFERENCES): {
			Intent i = new Intent(this, Preferences.class);
			startActivityForResult(i, SHOW_PREFERENCES);
			return true;
		}
		case (MENU_TRIPS): {
			Intent intent = new Intent(this, TripListActivity.class);
			startActivity(intent);
			return true;
		}
		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == SHOW_PREFERENCES)
			if (resultCode == Activity.RESULT_OK) {
				updateFromPreferences();
			}
	}

	/** Update preference variables based on saved preferences */
	private void updateFromPreferences() {
		
		int defaultZoom = prefs.getInt(Preferences.PREF_ZOOM, 0);
		if (defaultZoom < 0){
			defaultZoom = 0;
		}

		isMapView = prefs.getBoolean(Preferences.PREF_VIEW, true);

		Resources r = getResources();
		int[] zoomValues = r.getIntArray(R.array.zoom_values);		
		zoomRatio = zoomValues[defaultZoom];
		
		mapController.setZoom(zoomRatio);
		myMapView.setSatellite(!isMapView);
		myMapView.invalidate();
	}

	private String selectBestProvider(LocationManager locationManager) {
		
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		return locationManager.getBestProvider(criteria, true);
	}
	
	private boolean isConnectedToNetwork() {
		
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
				|| cm.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED)
			return true;
		return false;
	}
	
	private void loadPoisFromProvider() {

		poisList.clear();
		ContentResolver cr = getContentResolver();

		Cursor c = cr.query(PoiProvider.CONTENT_URI, null, null, null, null);

		if (c.moveToFirst()) {
			do {
				Poi poi = new Poi();
				poi.setId(c.getLong(PoiProvider.ID_COLUMN));
				poi.setName(c.getString(PoiProvider.NAME_COLUMN));
				poi.setLatitude(c.getDouble(PoiProvider.LAT_COLUMN));
				poi.setLongitude(c.getDouble(PoiProvider.LON_COLUMN));
				poi.setShortDescription(c.getString(PoiProvider.SHORT_COLUMN));
				poi.setDescription(c.getString(PoiProvider.DESC_COLUMN));
				poi.setLink(c.getString(PoiProvider.LINK_COLUMN));
				//Category category = c.getInt(PoiProvider.CATEGORY_COLUMN);
				// TODO fill that fields
				ArrayList<Image> images;
				
				poisList.add(poi);
				
			} while (c.moveToNext());
		}
		c.close();
	}	

	private void getData() {
		
		Thread updateThread = new Thread(null, backgroundDownload ,"updateData");
		updateThread.start();
	}
		
	private Runnable backgroundDownload = new Runnable() {
		@Override
		public void run() {
			getAndParseData();
		}
	};
		
	private void getAndParseData() {
			
		URL url;
		try {
			String feed = getString(R.string.provider_url);
			url = new URL(feed);

			URLConnection connection;
			connection = url.openConnection();

			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoOutput(true);

			int responseCode = httpConnection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {

				int xmlVersion = prefs.getInt(Preferences.XML_VERSION, 1);
				SaxFeedParser parser = new SaxFeedParser(feed, xmlVersion);
				int  serverXmlVersion = parser.parse();
				if (xmlVersion == serverXmlVersion) {
					
					Editor editor = prefs.edit();
					editor.putInt(Preferences.XML_VERSION, serverXmlVersion);
					editor.commit();
					addPoisToDB(parser.getPois());
				}

			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			progressDialog.dismiss();
		}
	}

	private boolean addPoisToDB(List<Poi> poisList) {
		ContentResolver cr = getContentResolver();

		for (Poi poi : poisList) {
			String w = PoiProvider.KEY_ID + " = " + poi.getId();

			Cursor c = cr.query(PoiProvider.CONTENT_URI, null, w, null, null);
			int dbCount = c.getCount();
			c.close();

			if (dbCount == 0) {
				ContentValues values = new ContentValues();

				values.put(PoiProvider.KEY_ID, poi.getId());
				values.put(PoiProvider.KEY_NAME, poi.getName());
				values.put(PoiProvider.KEY_LAT, poi.getLatitude());
				values.put(PoiProvider.KEY_LON, poi.getLongitude());
				values.put(PoiProvider.KEY_SHORT, poi.getShortDescription());
				//values.put(PoiProvider.KEY_DESC, poi.getDescription());
				//values.put(PoiProvider.KEY_LINK, poi.getLink());

				cr.insert(PoiProvider.CONTENT_URI, values);
			}
		}
		return false;
	}
}
