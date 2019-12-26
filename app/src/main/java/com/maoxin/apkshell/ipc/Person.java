package com.maoxin.apkshell.ipc;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author lmx
 * Created by lmx on 2019/12/26.
 */
public class Person implements Parcelable {

    private String name;
    private String uid;

    public Person() {
    }

    protected Person(Parcel in) {
        name = in.readString();
        uid = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(uid);
    }

    @Override
    public String toString() {
        return "Person{" + "name='" + name + '\'' + ", uid=" + uid + '}';
    }
}
