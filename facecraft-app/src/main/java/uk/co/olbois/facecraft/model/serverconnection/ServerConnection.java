package uk.co.olbois.facecraft.model.serverconnection;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.sqlite.Identifiable;

public class ServerConnection implements Identifiable<String>, Parcelable {

    private String id;
    private String name;

    private String password;
    private String url;
    private String members;
    private String owners;

    private int userCount;
    private Role role = Role.MEMBER;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<ServerConnection>() {

        @Override
        public ServerConnection createFromParcel(Parcel source) {
            return new ServerConnection(source);
        }

        @Override
        public ServerConnection[] newArray(int size) {
            return new ServerConnection[size];
        }
    };

    private ServerConnection(Parcel parcel){
        this.id = parcel.readString();
        this.name = parcel.readString();
        this.userCount = parcel.readInt();
        this.role = Role.values()[parcel.readInt()];
        this.url = parcel.readString();
        this.members = parcel.readString();
        this.owners = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeInt(userCount);
        dest.writeInt(role.ordinal());
        dest.writeString(url);
        dest.writeString(members);
        dest.writeString(owners);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public ServerConnection(){

    }

    public ServerConnection(String id, String host, int userCount, Role role) {
        this.id = id;
        this.name = host;
        this.userCount = userCount;
        this.role = role;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public String getOwners() {
        return owners;
    }

    public void setOwners(String owners) {
        this.owners = owners;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public enum Role {
        MEMBER, OWNER, OTHER
    }


    public String format(){
        Gson gson = new GsonBuilder()
                .create();

        return gson.toJson(this);
    }

    public static ServerConnection parse(String json){
        Gson gson = new GsonBuilder()
                .create();

        ServerConnection sc = gson.fromJson(json, ServerConnection.class);

        JsonElement ele = gson.fromJson(json, JsonElement.class);
        JsonObject jsonAsObj = ele.getAsJsonObject();
        JsonElement links = jsonAsObj.get("_links");
        String url = links.getAsJsonObject().get("self").getAsJsonObject().get("href").getAsString();
        sc.setUrl(url);
        sc.setMembers(links.getAsJsonObject().get("members").getAsJsonObject().get("href").getAsString());
        sc.setOwners(links.getAsJsonObject().get("owners").getAsJsonObject().get("href").getAsString());
        String[] arr = url.split("/");

        sc.setId(arr[arr.length-1]);

        return sc;
    }

    public static ServerConnection[] parseArray(String json){
        Gson gson = new GsonBuilder()
                .create();

        JsonElement ele = gson.fromJson(json, JsonElement.class);
        JsonArray serversFromJson = ele.getAsJsonObject().get("_embedded").getAsJsonObject().get("servers").getAsJsonArray();

        ServerConnection[] servers = new ServerConnection[serversFromJson.size()];
        int count = 0;
        for(JsonElement server : serversFromJson){
            servers[count++] = parse(server.getAsJsonObject().toString());
        }

        return servers;
    }
}
