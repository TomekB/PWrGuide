package pl.pwr.guide.exterior.model;

import java.io.Serializable;
import java.util.List;

/***
 * Class for points of interest.
 * @author Pawel Krawczyk
 *
 */
public class Poi implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String name;
	private double latitude;
	private double longitude;
	private String shortDescription;
	private String description;
	private String link;
	private Category category;
	private List<Image> images;
	
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
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getShortDescription() {
		return shortDescription;
	}
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public List<Image> getImages() {
		return images;
	}
	public void setImages(List<Image> images) {
		this.images = images;
	}
		
	@Override
	public String toString() {
		return "Poi [id=" + id + ", name=" + name + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", shortDescription="
				+ shortDescription + ", description=" + description + ", link="
				+ link + ", category=" + category + ", images=" + images
				+  "]";
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		if ((long) getId() == ((Poi) obj).getId()) {
			return true;
		}
		return false;
	}
}
