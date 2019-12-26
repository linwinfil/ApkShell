package com.maoxin.apkshell.ipc.server;

import android.os.IInterface;
import android.os.RemoteException;

import com.maoxin.apkshell.ipc.Person;

import java.util.List;

/**
 * @author lmx
 * Created by lmx on 2019/12/26.
 * 定义{@link RemoteService}具有什么操作
 */
public interface PersonInterface extends IInterface {
    List<Person> getPersons() throws RemoteException;

    void addPerson(Person person) throws RemoteException;
}
