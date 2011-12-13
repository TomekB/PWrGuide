package pl.pwr.guide.exterior.model.overlays;

import java.util.ArrayList;

import pl.pwr.guide.exterior.views.BalloonItemizedOverlay;


import android.graphics.drawable.Drawable;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MyPositionOverlay extends BalloonItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> m_overlays = new ArrayList<OverlayItem>();
	
	private Location location;
	private String locationInfo;
	private MapView mapView;

	/** Get the position location */
	public Location getLocation() {
		return location;
	}

	/** Set the position location */
	public void setLocation(Location location) {
		this.location = location;
	}

	public String getLocationInfo() {
		return locationInfo;
	}

	public void setLocationInfo(String locationInfo) {
		this.locationInfo = locationInfo;
	}
	
	public MyPositionOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker), mapView);
		
		this.mapView = mapView;
		
		populate();
	}

	public void addOverlay() {
		
		GeoPoint point = new GeoPoint((int)(location.getLatitude()*1E6),(int)(location.getLongitude()*1E6));
		OverlayItem overlayItem = new OverlayItem(point,"My position",locationInfo);
	    m_overlays.add(overlayItem);
	    populate();
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		return m_overlays.get(i);
	}

	@Override
	public int size() {
		return m_overlays.size();
	}

	@Override
	protected boolean onBalloonTap(int index, OverlayItem item) {
				
		return true;
	}
}