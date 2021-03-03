package com.hunter.mymediaplayer;

import android.os.Parcel;
import android.os.Parcelable;

public class Track implements Parcelable  {
    private String title;
    private String artist;
    private int image;
    private int audio;

    public Track(String title, String artist, int image, int audio) {
        this.title = title;
        this.artist = artist;
        this.image = image;
        this.audio = audio;
    }

    protected Track(Parcel in) {
        title = in.readString();
        artist = in.readString();
        image = in.readInt();
        audio = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeInt(image);
        dest.writeInt(audio);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

    public int getAudio() {
        return audio;
    }

    public void setAudio(int audio) {
        this.audio = audio;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "name='" + title + '\'' +
                ", officeName='" + artist + '\'' +
                '}';
    }

}
