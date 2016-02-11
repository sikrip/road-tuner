package sikrip.roaddyno.web.chartmodel;

public class ChartModel {
	private String type = "serial";
	private String categoryField = "category";
	private Integer startDuration = 1;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCategoryField() {
		return categoryField;
	}

	public void setCategoryField(String categoryField) {
		this.categoryField = categoryField;
	}

	public Integer getStartDuration() {
		return startDuration;
	}

	public void setStartDuration(Integer startDuration) {
		this.startDuration = startDuration;
	}
}
