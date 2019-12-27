package com.maoxin.apkshell.ipc.server;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import com.maoxin.apkshell.ipc.Person;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author lmx
 * Created by lmx on 2019/12/26.
 * binder本地对象，实现{@link PersonInterface}接口，声明具有server承诺给client的能力
 */
public abstract class PersonStub extends android.os.Binder implements PersonInterface {
    public static final String DESCRIPTOR = "com.maoxin.apkshell.ipc.server.PersonInterface";
    public static final int TRANSAVTION_addPerson = IBinder.FIRST_CALL_TRANSACTION;
    public static final int TRANSAVTION_getPerson = IBinder.FIRST_CALL_TRANSACTION + 1;

    public PersonStub() {
        this.attachInterface(this, DESCRIPTOR);
    }

    @Override
    public IBinder asBinder() {
        return this;
    }

    public static PersonInterface asInterfaceProxy(IBinder binder) {
        if (binder == null) return null;
        IInterface iin = binder.queryLocalInterface(DESCRIPTOR);
        if (iin instanceof PersonInterface) return (PersonInterface) iin;
        return new PersonProxy(binder);
    }

    @Override
    protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
        switch (code) {

            case INTERFACE_TRANSACTION:
                if (reply != null) {
                    reply.writeString(DESCRIPTOR);
                }
                return true;

            case TRANSAVTION_getPerson:
                if (reply != null) {
                    data.enforceInterface(DESCRIPTOR);
                    List<Person> result = this.getPersons();
                    reply.writeNoException();
                    reply.writeTypedList(result);
                }
                return true;

            case TRANSAVTION_addPerson:
                if (reply != null) {
                    data.enforceInterface(DESCRIPTOR);
                    Person arg0 = null;
                    if (data.readInt() != 0) {
                        arg0 = Person.CREATOR.createFromParcel(data);
                    }
                    this.addPerson(arg0);
                    reply.writeNoException();
                }
                return true;

        }
        return super.onTransact(code, data, reply, flags);
    }
}
