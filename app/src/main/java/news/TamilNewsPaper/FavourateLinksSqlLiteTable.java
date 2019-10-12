package news.TamilNewsPaper;

/**
 * Created by Elcot on 12/14/2016.
 */

public class FavourateLinksSqlLiteTable {
    int id;
    String linkUrl;
    String linkName;
    String actualTag;
    String isLite;

    public FavourateLinksSqlLiteTable(int id, String linkUrl, String linkName, String isLite, String actualTag) {
        this.id = id;
        this.linkUrl = linkUrl;
        this.linkName = linkName;
        this.actualTag = actualTag;
        this.isLite = isLite;
    }

    public FavourateLinksSqlLiteTable(int id, String linkUrl, String linkName, String actualTag) {
        this.id = id;
        this.linkUrl = linkUrl;
        this.linkName = linkName;
        this.actualTag = actualTag;
    }

    public FavourateLinksSqlLiteTable(String linkName, String linkUrl) {
        this.linkUrl = linkUrl;
        this.linkName = linkName;
    }

    public FavourateLinksSqlLiteTable(int id, String linkName, String linkUrl) {
        this.id = id;
        this.linkUrl = linkUrl;
        this.linkName = linkName;
    }

    public FavourateLinksSqlLiteTable(String linkName, String linkUrl, String isLite, String actualTag) {
        this.id = id;
        this.linkUrl = linkUrl;
        this.linkName = linkName;
        this.isLite = isLite;
    }
    public String getActualTag() {
        return actualTag;
    }


    public void setActualTag(String actualTag) {
        this.actualTag = actualTag;
    }

    public FavourateLinksSqlLiteTable() {
    }



    public int getId() {
        return id;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getIsLite() {
        return isLite;
    }

    public void setIsLite(String isLite) {
        this.isLite = isLite;
    }
}
