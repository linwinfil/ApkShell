package com.maoxin.apkshell.ipc.server;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import com.maoxin.apkshell.ipc.Person;

import java.util.List;

/**
 * @author lmx
 * Created by lmx on 2019/12/26.
 */
public class PersonProxy implements PersonInterface {

    private static final String DESCRIPTOR = PersonStub.DESCRIPTOR;
    private IBinder remote;

    public PersonProxy(IBinder binder) {
        remote = binder;
    }

    @Override
    public List<Person> getPersons() throws RemoteException {

        Parcel data = Parcel.obtain();
        Parcel replay = Parcel.obtain();
        List<Person> result;

        try {
            data.writeInterfaceToken(DESCRIPTOR);
            remote.transact(PersonStub.TRANSAVTION_getPerson, data, replay, 0);
            replay.readException();
            result = replay.createTypedArrayList(Person.CREATOR);
        }
        finally {
            replay.recycle();
            data.recycle();
        }
        return result;
    }

    @Override
    public void addPerson(Person person) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel replay = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            if (person != null) {
                data.writeInt(1);
                person.writeToParcel(data, 0);
            }
            else {
                data.writeInt(0);
            }
            remote.transact(PersonStub.TRANSAVTION_addPerson, data, replay, 0);
            replay.readException();
        }
        finally {
            replay.recycle();
            data.recycle();
        }
    }

    @Override
    public IBinder asBinder() {
        return remote;
    }
}
