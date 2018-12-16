package uk.co.olbois.facecraft.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import uk.co.olbois.facecraft.model.serverconnection.ServerConnection;
import uk.co.olbois.facecraft.sqlite.Identifiable;

public class SampleUser implements Identifiable<Long>, Parcelable {

    private Long id;
    private String username;
    private String password;

    private String deviceToken;
    private String url;
    private String serversOwnedUrl;
    private String serversPartOfUrl;
    private ServerConnection.Role role = ServerConnection.Role.OTHER;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<SampleUser>() {

        @Override
        public SampleUser createFromParcel(Parcel source) {
            return new SampleUser(source);
        }

        @Override
        public SampleUser[] newArray(int size) {
            return new SampleUser[size];
        }
    };

    private SampleUser(Parcel parcel){
        this.id = parcel.readLong();
        this.username = parcel.readString();
        this.password = parcel.readString();
        this.deviceToken = parcel.readString();
        this.role = ServerConnection.Role.values()[parcel.readInt()];
        this.url = parcel.readString();
        this.serversOwnedUrl = parcel.readString();
        this.serversPartOfUrl = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(deviceToken);
        dest.writeInt(role.ordinal());
        dest.writeString(url);
        dest.writeString(serversOwnedUrl);
        dest.writeString(serversPartOfUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public SampleUser() {
    }

    public SampleUser(Long id, String username, String password) {

        this.id = id;
        this.username = username;
        this.password = password;
    }

    public Long getId() {

        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getServersOwnedUrl() {
        return serversOwnedUrl;
    }

    public void setServersOwnedUrl(String serversOwnedUrl) {
        this.serversOwnedUrl = serversOwnedUrl;
    }

    public String getServersPartOfUrl() {
        return serversPartOfUrl;
    }

    public void setServersPartOfUrl(String serversPartOfUrl) {
        this.serversPartOfUrl = serversPartOfUrl;
    }

    public ServerConnection.Role getRole() {
        return role;
    }

    public void setRole(ServerConnection.Role role) {
        this.role = role;
    }


    public String format(){
        Gson gson = new GsonBuilder()
                .create();

        return gson.toJson(this);
    }

    public static SampleUser parse(String json){
        Gson gson = new GsonBuilder()
                .create();

        SampleUser s = gson.fromJson(json, SampleUser.class);

        JsonElement ele = gson.fromJson(json, JsonElement.class);
        JsonObject jsonAsObj = ele.getAsJsonObject();
        JsonElement links = jsonAsObj.get("_links");
        String url = links.getAsJsonObject().get("self").getAsJsonObject().get("href").getAsString();
        s.setUrl(url);
        s.setServersOwnedUrl(links.getAsJsonObject().get("serversOwned").getAsJsonObject().get("href").getAsString());
        s.setServersPartOfUrl(links.getAsJsonObject().get("serversPartOf").getAsJsonObject().get("href").getAsString());
        String[] arr = url.split("/");

        s.setId(Long.parseLong(arr[arr.length-1]));

        return s;
    }

    public static SampleUser[] parseArray(String json){
        Gson gson = new GsonBuilder()
                .create();

        JsonElement ele = gson.fromJson(json, JsonElement.class);
        JsonArray usersFromJson = ele.getAsJsonObject().get("_embedded").getAsJsonObject().get("users").getAsJsonArray();

        SampleUser[] users = new SampleUser[usersFromJson.size()];
        int count = 0;
        for(JsonElement user : usersFromJson){
            users[count++] = parse(user.getAsJsonObject().toString());
        }

        return users;
    }

}
