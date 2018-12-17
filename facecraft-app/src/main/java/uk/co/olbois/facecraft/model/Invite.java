package uk.co.olbois.facecraft.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Invite {

    private Long id;
    private String url;
    private String invited_by;
    private String invited_user_id;
    private String server;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInvited_by() {
        return invited_by;
    }

    public void setInvited_by(String invited_by) {
        this.invited_by = invited_by;
    }

    public String getInvited_user_id() {
        return invited_user_id;
    }

    public void setInvited_user_id(String invited_user_id) {
        this.invited_user_id = invited_user_id;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server_id) {
        this.server = server_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String format(){
        Gson gson = new GsonBuilder()
                .create();

        return gson.toJson(this);
    }

    public static Invite parse(String json){
        Gson gson = new GsonBuilder()
                .create();

        Invite i = gson.fromJson(json, Invite.class);

        JsonElement ele = gson.fromJson(json, JsonElement.class);
        JsonObject jsonAsObj = ele.getAsJsonObject();
        JsonElement links = jsonAsObj.get("_links");
        String url = links.getAsJsonObject().get("self").getAsJsonObject().get("href").getAsString();
        i.setUrl(url);
        i.setInvited_by(links.getAsJsonObject().get("invited_by").getAsJsonObject().get("href").getAsString());
        i.setInvited_user_id(links.getAsJsonObject().get("invited_user_id").getAsJsonObject().get("href").getAsString());
        i.setServer(links.getAsJsonObject().get("server").getAsJsonObject().get("href").getAsString());
        String[] arr = url.split("/");

        i.setId(Long.parseLong(arr[arr.length-1]));

        return i;
    }

    public static Invite[] parseArray(String json){
        Gson gson = new GsonBuilder()
                .create();

        JsonElement ele = gson.fromJson(json, JsonElement.class);
        JsonArray invitesFromJson = ele.getAsJsonObject().get("_embedded").getAsJsonObject().get("invites").getAsJsonArray();

        Invite[] invites = new Invite[invitesFromJson.size()];
        int count = 0;
        for(JsonElement invite : invitesFromJson){
            invites[count++] = parse(invite.getAsJsonObject().toString());
        }

        return invites;
    }
}
