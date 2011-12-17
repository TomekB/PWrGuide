package pl.pwr.guide.exterior.model;

/***
 * Stub of Trip class.
 * 
 */
public class Trip {

	private long id;
	private String name;
	private String poiList;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPoiList() {
		return poiList;
	}
	public void setPoiList(String poiList) {
		this.poiList = poiList;
	}
	public Trip(){
		
	}
	
	public Trip(long id, String name, String poiList) {
		super();
		this.id = id;
		this.name = name;
		this.poiList = poiList;
	}
	
	
}
