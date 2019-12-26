package com.maoxin.apkshell.ipc;

import dagger.Component;

/**
 * @author lmx
 * Created by lmx on 2019/12/17.
 */
@Component
public interface SampleComponent {
    void injectSampleA(SampleSet.SampleA sampleA);

    void injectSampleB(SampleSet.SampleB sampleB);
}
