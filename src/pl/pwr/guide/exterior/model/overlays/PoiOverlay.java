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
	private MapView mapView;

	public PoiOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker), mapView);

		this.mapView = mapView;

		populate();
	}

	public void addOverlay(Poi poi) {

		GeoPoint point = new GeoPoint((int) (poi.getLatitude() * 1E6),
				(int) (poi.getLongitude() * 1E6));
		OverlayItem overlayItem = new OverlayItem(point, poi.getName(),
				poi.getShortDescription());
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

		//TODO TO POI DETAILS
		return true;
	}
}
