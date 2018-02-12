package com.adnonstop.exoplayer;

/**
 * @author lmx
 *         Created by lmx on 2018-02-09.
 */

public class DefaultMediaSource implements IMediaSource
{
    private String mUri;

    @Override
    public String getVideoUri()
    {
        return mUri;
    }

    @Override
    public void setVideoUri(String path)
    {
        this.mUri = path;
    }

}
