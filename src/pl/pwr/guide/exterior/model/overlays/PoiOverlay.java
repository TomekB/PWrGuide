package pl.pwr.guide.exterior.model.overlays;

import java.util.ArrayList;

import pl.pwr.guide.exterior.model.Poi;
import pl.pwr.guide.exterior.views.BalloonItemizedOverlay;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class PoiOverlay extends BalloonItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> m_overlays = new ArrayList<OverlayItem>();
	private ArrayList<GeoPoint> geoPoints = new ArrayList<GeoPoint>();

	public PoiOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker), mapView);

		populate();
	}

	public void addOverlay(Poi poi) {

		GeoPoint geoPoint = new GeoPoint((int) (poi.getLatitude() * 1E6), (int) (poi.getLongitude() * 1E6));
		OverlayItem overlayItem = new OverlayItem(geoPoint, poi.getName(), poi.getShortDescription());
		m_overlays.add(overlayItem);
		geoPoints.add(geoPoint);
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

	public ArrayList<GeoPoint> getGeoPoints() {
		return geoPoints;
	}

	@Override
	protected boolean onBalloonTap(int index, OverlayItem item) {

		//TODO TO POI DETAILS
		return true;
	}
}
