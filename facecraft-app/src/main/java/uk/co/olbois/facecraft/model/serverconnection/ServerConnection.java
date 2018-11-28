package uk.co.olbois.facecraft.model.serverconnection;

import android.os.Parcel;
import android.os.Parcelable;

import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.sqlite.Identifiable;

public class ServerConnection implements Identifiable<Long>, Parcelable {

    private Long id;
    private String host;
    private int port = 25565;
    private Long userId;

    // TODO delete these, they will eventually be queried from the server
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
        this.id = parcel.readLong();
        this.host = parcel.readString();
        this.port = parcel.readInt();
        this.userId = parcel.readLong();
        this.userCount = parcel.readInt();
        this.role = Role.values()[parcel.readInt()];
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(host);
        dest.writeInt(port);
        dest.writeLong(userId);
        dest.writeInt(userCount);
        dest.writeInt(role.ordinal());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public ServerConnection(){

    }

    public ServerConnection(Long id, String host, int port, int userCount, Role role, Long userId) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.userCount = userCount;
        this.role = role;
        this.userId = userId;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public enum Role {
        MEMBER, ADMIN, OWNER
    }

}
