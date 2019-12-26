package com.maoxin.apkshell.ipc.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.maoxin.apkshell.ipc.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.Nullable;

/**
 * @author lmx
 * Created by lmx on 2019/12/26.
 */
public class RemoteService extends Service {

    private List<Person> persons = new ArrayList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        Person person = new Person();
        person.setUid(UUID.randomUUID().toString());
        person.setName("chuck");
        System.out.println(person.toString());
        persons.add(person);
    }

    private PersonStub stub = new PersonStub() {
        @Override
        public List<com.maoxin.apkshell.ipc.Person> getPersons() throws RemoteException {
            synchronized (this) {
                return persons;
            }
        }

        @Override
        public void addPerson(com.maoxin.apkshell.ipc.Person person) throws RemoteException {
            synchronized (this) {
                persons.add(person);
                System.out.println("remote:addPerson" + person.toString());
            }
        }
    };
}
