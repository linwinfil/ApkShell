package com.maoxin.apkshell;

import android.media.MediaMetadataRetriever;
import android.support.annotation.FloatRange;

import com.maoxin.apkshell.audio.FileUtils;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author lmx
 * Created by lmx on 2018-03-27.
 */

public class Mp4ParseUtils
{
    public static final String SOUN = "soun";
    public static final String VIDE = "vide";

    /**
     * 对MP4文件集合进行追加合并(按照顺序拼接)
     *
     * @param mp4PathList
     * @param outPutPath
     * @throws IOException
     */
    public static void appendMp4List(List<String> mp4PathList, String outPutPath) throws Throwable
    {
        List<Movie> mp4MovieList = new ArrayList<>();
        for (String mp4Path : mp4PathList)
        {
            if (FileUtils.isFileExists(mp4Path))
            {
                mp4MovieList.add(MovieCreator.build(mp4Path));
            }
        }

        LinkedList<Track> audioTracks = new LinkedList<>();
        LinkedList<Track> videoTracks = new LinkedList<>();
        for (Movie mp4Movie : mp4MovieList)
        {
            for (Track inMovieTrack : mp4Movie.getTracks())
            {
                if (SOUN.equals(inMovieTrack.getHandler()))
                {
                    audioTracks.add(inMovieTrack);
                }
                if (VIDE.equals(inMovieTrack.getHandler()))
                {
                    videoTracks.add(inMovieTrack);
                }
            }
        }
        Movie resultMovie = new Movie();
        if (!audioTracks.isEmpty())
        {
            resultMovie.addTrack(new AppendTrack((Track[]) audioTracks.toArray(new Track[audioTracks.size()])));
        }
        if (!videoTracks.isEmpty())
        {
            resultMovie.addTrack(new AppendTrack((Track[]) videoTracks.toArray(new Track[videoTracks.size()])));
        }
        Container outContainer = new DefaultMp4Builder().build(resultMovie);
        FileChannel fileChannel = new RandomAccessFile(outPutPath, "rw").getChannel();
        outContainer.writeContainer(fileChannel);
        fileChannel.close();
    }

    /**
     * 抽取MP4文件集中视频轨道进行追加合并
     *
     * @param mp4PathList
     * @param outPutPath
     * @throws Throwable
     */
    public static void extractVideo(List<String> mp4PathList, String outPutPath) throws Throwable
    {
        List<Movie> mp4MovieList = new ArrayList<>();
        for (String mp4Path : mp4PathList)
        {
            if (FileUtils.isFileExists(mp4Path))
            {
                mp4MovieList.add(MovieCreator.build(mp4Path));
            }
        }

        LinkedList<Track> videoTracks = new LinkedList<>();
        for (Movie mp4Movie : mp4MovieList)
        {
            for (Track inMovieTrack : mp4Movie.getTracks())
            {
                if (VIDE.equals(inMovieTrack.getHandler()))
                {
                    videoTracks.add(inMovieTrack);
                }
            }
        }
        Movie resultMovie = new Movie();
        if (!videoTracks.isEmpty())
        {
            resultMovie.addTrack(new AppendTrack((Track[]) videoTracks.toArray(new Track[videoTracks.size()])));
        }
        Container outContainer = new DefaultMp4Builder().build(resultMovie);
        FileChannel fileChannel = new RandomAccessFile(outPutPath, "rw").getChannel();
        outContainer.writeContainer(fileChannel);
        fileChannel.close();
    }

    /**
     * 抽取MP4文件集中音频轨道进行追加合并
     *
     * @param mp4PathList
     * @param outPutPath
     * @throws Throwable
     */
    public static void extractAudio(List<String> mp4PathList, String outPutPath) throws Throwable
    {
        List<Movie> mp4MovieList = new ArrayList<>();
        for (String mp4Path : mp4PathList)
        {
            if (FileUtils.isFileExists(mp4Path))
            {
                mp4MovieList.add(MovieCreator.build(mp4Path));
            }
        }

        LinkedList<Track> audioTracks = new LinkedList<>();
        for (Movie mp4Movie : mp4MovieList)
        {
            for (Track inMovieTrack : mp4Movie.getTracks())
            {
                if (SOUN.equals(inMovieTrack.getHandler()))
                {
                    audioTracks.add(inMovieTrack);
                }
            }
        }
        Movie resultMovie = new Movie();
        if (!audioTracks.isEmpty())
        {
            resultMovie.addTrack(new AppendTrack((Track[]) audioTracks.toArray(new Track[audioTracks.size()])));
        }
        Container outContainer = new DefaultMp4Builder().build(resultMovie);
        FileChannel fileChannel = new RandomAccessFile(outPutPath, "rw").getChannel();
        outContainer.writeContainer(fileChannel);
        fileChannel.close();
    }

    public static void extractVideoAndAudio(List<String> mp4PathList, String videoOutPath, String audioOutPath) throws Throwable
    {
        List<Movie> mp4MovieList = new ArrayList<>();
        for (String mp4Path : mp4PathList)
        {
            if (FileUtils.isFileExists(mp4Path))
            {
                mp4MovieList.add(MovieCreator.build(mp4Path));
            }
        }

        LinkedList<Track> audioTracks = new LinkedList<>();
        LinkedList<Track> videoTracks = new LinkedList<>();
        for (Movie mp4Movie : mp4MovieList)
        {
            for (Track inMovieTrack : mp4Movie.getTracks())
            {
                String handler = inMovieTrack.getHandler();
                if (SOUN.equals(handler))
                {
                    audioTracks.add(inMovieTrack);
                }
                else if (VIDE.equals(handler))
                {
                    videoTracks.add(inMovieTrack);
                }
            }
        }

        //audio
        Movie resultMovie = new Movie();
        if (!audioTracks.isEmpty())
        {
            resultMovie.addTrack(new AppendTrack((Track[]) audioTracks.toArray(new Track[audioTracks.size()])));
        }
        Container outContainer = new DefaultMp4Builder().build(resultMovie);
        FileChannel fileChannel = new RandomAccessFile(audioOutPath, "rw").getChannel();
        outContainer.writeContainer(fileChannel);
        fileChannel.close();

        //video
        resultMovie = new Movie();
        if (!videoTracks.isEmpty())
        {
            resultMovie.addTrack(new AppendTrack((Track[]) videoTracks.toArray(new Track[videoTracks.size()])));
        }
        outContainer = new DefaultMp4Builder().build(resultMovie);
        fileChannel = new RandomAccessFile(videoOutPath, "rw").getChannel();
        outContainer.writeContainer(fileChannel);
        fileChannel.close();
    }

    public static float getVideoFrameRate(String path) throws Throwable
    {
        if (!FileUtils.isFileCanRead(path)) return 0F;
        float duration = getDurationIso(path);
        if (duration == 0F) return 0F;
        Movie movie = MovieCreator.build(path);
        int totalSamples = 0;
        for (Track track : movie.getTracks())
        {
            if (VIDE.equals(track.getHandler()))
            {
                totalSamples += track.getSamples().size();
            }
        }
        return totalSamples / duration;
    }


    public static float getDurationIso(String path)
    {
        if (!FileUtils.isFileCanRead(path)) return 0F;
        try
        {
            IsoFile isoFile = isoFile = new IsoFile(path);
            float lengthInSeconds = (float)
                    isoFile.getMovieBox().getMovieHeaderBox().getDuration() /
                    isoFile.getMovieBox().getMovieHeaderBox().getTimescale();
            isoFile.close();
            return lengthInSeconds;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return 0F;
    }

    public static boolean setVolume(String path, String outPath, @FloatRange(from = 0.0f, to = 1.0f) float volume) throws Throwable
    {
        if (!FileUtils.isFileCanRead(path)) return false;

        Movie build = MovieCreator.build(path);
        LinkedList<Track> audioTracks = new LinkedList<>();
        LinkedList<Track> videoTracks = new LinkedList<>();
        for (Track track : build.getTracks())
        {
            String handler = track.getHandler();
            if (SOUN.equals(handler))
            {
                track.getTrackMetaData().setVolume(volume);
                audioTracks.add(track);
            }
            else if (VIDE.equals(handler))
            {
                videoTracks.add(track);
            }
        }

        Movie resultMovie = new Movie();
        if (!audioTracks.isEmpty())
        {
            resultMovie.addTrack(new AppendTrack((Track[]) audioTracks.toArray(new Track[audioTracks.size()])));
        }
        if (!videoTracks.isEmpty())
        {
            resultMovie.addTrack(new AppendTrack((Track[]) videoTracks.toArray(new Track[videoTracks.size()])));
        }
        Container outContainer = new DefaultMp4Builder().build(resultMovie);
        FileChannel fileChannel = new RandomAccessFile(outPath, "rw").getChannel();
        outContainer.writeContainer(fileChannel);
        fileChannel.close();
        return true;
    }


    /**
     * 使用{@link MediaMetadataRetriever}解码时间
     *
     * @param path
     * @return
     */
    public static long getDuration(String path)
    {
        if (!FileUtils.isFileExists(path)) return 0L;
        MediaMetadataRetriever mm = new MediaMetadataRetriever();
        try
        {
            mm.setDataSource(path);
            String duration = mm.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            return Long.parseLong(duration);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        finally
        {
            mm.release();
        }
        return 0L;
    }
}
