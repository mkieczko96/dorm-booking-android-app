package com.booker.model.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class User implements Parcelable {

    @Expose
    private Long id;

    @Expose
    private String firstName;

    @Expose
    private String lastName;

    @Expose
    private Long room;

    @Expose
    private String username;

    @Expose
    private String password; //BCrypt hash of password

    @Expose
    private Long createdOn; // unix timestamp

    @Expose
    private Long lastModifiedOn; // unix timestamp

    @Expose
    private Long expiresOn; //unix timestamp

    @Expose
    private Boolean accountNonExpired;

    @Expose
    private Long incorrectLoginAttempts;

    @Expose
    private Boolean accountNonLocked;

    @Expose
    private Long credentialsUpdatedOn;

    @Expose
    private Boolean credentialsNonExpired;

    @Expose
    private Boolean enabled;

    @Expose
    private List<Role> roles;

    public String getDisplayName() {
        return firstName + " " + lastName;
    }

    public User(Parcel in) {
        id = in.readLong();
        firstName = in.readString();
        lastName = in.readString();
        room = in.readLong();
        username = in.readString();
        password = in.readString();
        createdOn = in.readLong();
        lastModifiedOn = in.readLong();
        expiresOn = in.readLong();
        accountNonExpired = in.readBoolean();
        incorrectLoginAttempts = in.readLong();
        accountNonLocked = in.readBoolean();
        credentialsUpdatedOn = in.readLong();
        credentialsNonExpired = in.readBoolean();
        enabled = in.readBoolean();
        in.readList(roles, Role.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeLong(room);
        parcel.writeString(username);
        parcel.writeString(password);
        parcel.writeLong(createdOn);
        parcel.writeLong(lastModifiedOn);
        parcel.writeLong(expiresOn);
        parcel.writeBoolean(accountNonExpired);
        parcel.writeLong(incorrectLoginAttempts);
        parcel.writeBoolean(accountNonLocked);
        parcel.writeLong(credentialsUpdatedOn);
        parcel.writeBoolean(credentialsNonExpired);
        parcel.writeBoolean(enabled);
        parcel.writeList(roles);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
