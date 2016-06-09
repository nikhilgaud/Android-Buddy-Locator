package kj.buddylocator;

/**
 * Created by Kapil on 9/28/2014.
 */
public class ListViewItemWhitelist {
    public String name;
    public String data;
    public String id;
    public String lattitude;
    public String longitude;
    public String whitelist;

    public ListViewItemWhitelist(String name, String data, String id, String lattitude, String longitude, String whitelist){
        this.name = name;
        this.data = data;
        this.id = id;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.whitelist = whitelist;
    }

}