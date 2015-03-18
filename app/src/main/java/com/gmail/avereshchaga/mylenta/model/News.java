package com.gmail.avereshchaga.mylenta.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.gmail.avereshchaga.mylenta.utils.Utils;

/**
 * Created by Alex on 15.03.2015.
 */
public class News implements Comparable<News>, Parcelable {
    private String enclosure;
    private String title;
    private String pubDate;
    private String description;
    private String author;

    public News() {
    }

    private News(Parcel in) {
        enclosure = in.readString();
        title = in.readString();
        pubDate = in.readString();
        description = in.readString();
        author = in.readString();
    }

    public void setEnclosure(String enclosure) {
        this.enclosure = enclosure;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEnclosure() {
        return enclosure;
    }

    public String getTitle() {
        return title;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public int compareTo(News another) {
        return Utils.parseDate(another.pubDate).compareTo(Utils.parseDate(pubDate));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        if (enclosure != null) {
            result = prime * result + enclosure.length();
        }

        if (title != null) {
            result = prime * result + title.length();
        }
        if (pubDate != null) {
            result = prime * result + pubDate.length();
        }
        if (description != null) {
            result = prime * result + description.length();
        }
        if (author != null) {
            result = prime * result + author.length();
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        News other = (News) obj;
        if ((this.enclosure == null) ? (other.enclosure != null) : !this.enclosure.equals(other.enclosure)) {
            return false;
        }
        if ((this.title == null) ? (other.title != null) : !this.title.equals(other.title)) {
            return false;
        }
        if ((this.pubDate == null) ? (other.pubDate != null) : !this.pubDate.equals(other.pubDate)) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if ((this.author == null) ? (other.author != null) : !this.author.equals(other.author)) {
            return false;
        }
        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(enclosure);
        dest.writeString(title);
        dest.writeString(pubDate);
        dest.writeString(description);
        dest.writeString(author);
    }

    public static final Parcelable.Creator<News> CREATOR = new Parcelable.Creator<News>() {
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        public News[] newArray(int size) {
            return new News[size];
        }
    };
}
