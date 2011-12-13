package pl.pwr.guide.exterior.activities;

import pl.pwr.guide.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.Button;

public class Preferences extends PreferenceActivity {

	private CheckBoxPreference mapView;
	private ListPreference zoomSpinner;

	private Button okButton;
	private Button cancelButton;

	private SharedPreferences prefs;

	public static final String USER_PREFERENCE = "USER_PREFERENCES";

	public static final String PREF_ZOOM = "PREF_ZOOM";
	public static final String PREF_VIEW = "PREF_VIEW";
	public static final String XML_VERSION = "XML_VERSION";

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		prefs = getSharedPreferences(USER_PREFERENCE, Activity.MODE_PRIVATE);
		addPreferencesFromResource(R.xml.preferences);
		setContentView(R.layout.preferences);
		
		findViews();
		zoomSpinner.setNegativeButtonText(getText(R.string.cancel));
		setListeners();

		updateUIFromPreferences();
	}

	private void findViews() {
		mapView = (CheckBoxPreference) findPreference("map_view");
		zoomSpinner = (ListPreference) findPreference("zoom_spinner");

		okButton = (Button) findViewById(R.id.okButton);
		cancelButton = (Button) findViewById(R.id.cancelButton);
	}

	private void setListeners() {
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				mapView.setPersistent(false);
				zoomSpinner.setPersistent(false);
				Preferences.this.setResult(RESULT_CANCELED);
				finish();
			}
		});
		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				savePreferences();
				Preferences.this.setResult(RESULT_OK);
				finish();
			}
		});
	}

	private void savePreferences() {
		boolean isMapView = mapView.isChecked();
		int zoomIndex = zoomSpinner.findIndexOfValue(zoomSpinner.getValue());

		Editor editor = prefs.edit();
		editor.putInt(PREF_ZOOM, zoomIndex);
		editor.putBoolean(PREF_VIEW, isMapView);
		editor.commit();
	}

	private void updateUIFromPreferences() {

		zoomSpinner.setValueIndex(prefs.getInt(PREF_ZOOM, 4));
		mapView.setChecked(prefs.getBoolean(PREF_VIEW, true));
	}
}