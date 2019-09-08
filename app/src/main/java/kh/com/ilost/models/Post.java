package kh.com.ilost.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daly on 3/24/2018.
 */

public class Post {
    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPtitle() {
        return ptitle;
    }

    public void setPtitle(String ptitle) {
        this.ptitle = ptitle;
    }

    public String getPtype() {
        return ptype;
    }

    public void setPtype(String ptype) {
        this.ptype = ptype;
    }

    public String getPdate() {
        return pdate;
    }

    public void setPdate(String pdate) {
        this.pdate = pdate;
    }

    public String getPuid() {
        return puid;
    }

    public void setPuid(String puid) {
        this.puid = puid;
    }

    public String getPtimefrom() {
        return ptimefrom;
    }

    public void setPtimefrom(String ptimefrom) {
        this.ptimefrom = ptimefrom;
    }

    public String getPtimeto() {
        return ptimeto;
    }

    public void setPtimeto(String ptimeto) {
        this.ptimeto = ptimeto;
    }

    public String getPcat() {
        return pcat;
    }

    public void setPcat(String pcat) {
        this.pcat = pcat;
    }

    public String getPlocation() {
        return plocation;
    }

    public void setPlocation(String plocation) {
        this.plocation = plocation;
    }
    private String pid;
    private String ptitle;
    private String ptype;
    private String pdate;
    private String puid;
    private String ptimefrom;
    private String ptimeto;
    private String pcat;
    private String plocation;


    private String purl;

    public Post() {
    }

    public String getPurl() {
        return purl;
    }

    public void setPurl(String purl) {
        this.purl = purl;
    }
}
