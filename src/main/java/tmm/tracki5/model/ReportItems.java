package tmm.tracki5.model;

/**
 * Created by Arun on 03/03/16.
 */
public class ReportItems {

    private String name;
    private String desc;
    private int photo;

    public ReportItems(String name, String desc, int photo) {
        this.name = name;
        this.photo = photo;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }
}