package com.maoxin.apkshell.ipc;

/**
 * @author lmx
 * Created by lmx on 2019/12/17.
 */
public class SampleSet {
    public static class SampleA {
        public SampleA() {
            DaggerSampleComponent.create().injectSampleA(this);
        }
    }

    public static class SampleB {
        public SampleB() {
            DaggerSampleComponent.create().injectSampleB(this);
        }
    }

    public static class SampleC {

    }
}
