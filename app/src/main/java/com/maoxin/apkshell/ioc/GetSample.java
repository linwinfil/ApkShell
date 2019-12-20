package com.maoxin.apkshell.ioc;

import javax.inject.Inject;

/**
 * @author lmx
 * Created by lmx on 2019/12/17.
 */
public class GetSample {

    @Inject SampleSet.SampleA sampleA;
    @Inject SampleSet.SampleB sampleB;

    @Inject
    public GetSample() {
    }
}
