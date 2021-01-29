package com.booker.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class CurrentUser implements Parcelable {
    private long id;
    private String displayName;
    private long room;

    protected CurrentUser(Parcel in) {
        id = in.readLong();
        displayName = in.readString();
        room = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(displayName);
        parcel.writeLong(room);
    }

    public static final Creator<CurrentUser> CREATOR = new Creator<CurrentUser>() {
        @Override
        public CurrentUser createFromParcel(Parcel in) {
            return new CurrentUser(in);
        }

        @Override
        public CurrentUser[] newArray(int size) {
            return new CurrentUser[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
