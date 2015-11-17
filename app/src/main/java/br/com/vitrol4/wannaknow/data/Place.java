package br.com.vitrol4.wannaknow.data;

import java.util.Date;

/**
 * Created by vitrol4 on 14/11/15.
 */
public class Place {

    private String title;
    private String description;
    private Date created;

    public Place(String title, String description, Date created) {
        this.title = title;
        this.description = description;
        this.created = created;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

}
