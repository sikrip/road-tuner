package sikrip.roaddyno.web.data;

public class Graph {

    private String balloonText =  "[[title]] of [[category]]:[[value]]";
    private String bullet =  "round";
    private String id = "AmGraph-1";
    private String title = "graph 1";
    private String type = "smoothedLine";
    private String valueField =  "column-1";

    public Graph() {
    }

    public Graph(String balloonText, String bullet, String id, String title, String type, String valueField) {
        this.balloonText = balloonText;
        this.bullet = bullet;
        this.id = id;
        this.title = title;
        this.type = type;
        this.valueField = valueField;
    }

    public String getBalloonText() {
        return balloonText;
    }

    public void setBalloonText(String balloonText) {
        this.balloonText = balloonText;
    }

    public String getBullet() {
        return bullet;
    }

    public void setBullet(String bullet) {
        this.bullet = bullet;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValueField() {
        return valueField;
    }

    public void setValueField(String valueField) {
        this.valueField = valueField;
    }
}
