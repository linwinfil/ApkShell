package com.maoxin.apkshell.ipc.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.maoxin.apkshell.IWorker;

import androidx.annotation.Nullable;

/**
 * @author lmx
 * Created by lmx on 2019/12/27.
 */
public class WorkerService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    private IWorker.Stub stub = new IWorker.Stub() {
        @Override
        public void onEditCode(String something, long times) throws RemoteException {
            System.out.println("onEditCode:" + something + ", at " + times);
        }

        @Override
        public long onCommitCode(String tag, int value) throws RemoteException {
            System.out.println("onCommitCode:" + tag + ", value " + value);
            return System.currentTimeMillis();
        }
    };
}
