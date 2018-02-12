package com.adnonstop.exoplayer;

import com.google.android.exoplayer2.upstream.DataSource;

/**
 * @author lmx
 *         Created by lmx on 2018-02-07.
 */

public interface IDataSourceFactory
{
    //源数据工厂接口
    DataSource.Factory getDataSourceFactory();
}
