// IWorker.aidl
package com.maoxin.apkshell;

// Declare any non-default types here with import statements

interface IWorker {

    void onEditCode(String something, long times);

    long onCommitCode(String tag, int value);
}
