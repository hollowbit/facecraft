package uk.co.olbois.facecraft.model;

import android.os.Parcel;
import android.os.Parcelable;

import uk.co.olbois.facecraft.sqlite.Identifiable;

public class SampleUser implements Identifiable<Long>, Parcelable {

    private Long id;
    private String username;
    private String password;

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
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(username);
        dest.writeString(password);
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

}
