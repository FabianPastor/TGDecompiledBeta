package org.telegram.messenger.audioinfo;

import android.graphics.Bitmap;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import org.telegram.messenger.audioinfo.m4a.M4AInfo;
import org.telegram.messenger.audioinfo.mp3.MP3Info;
/* loaded from: classes.dex */
public abstract class AudioInfo {
    protected String album;
    protected String albumArtist;
    protected String artist;
    protected String brand;
    protected String comment;
    protected boolean compilation;
    protected String composer;
    protected String copyright;
    protected Bitmap cover;
    protected short disc;
    protected short discs;
    protected long duration;
    protected String genre;
    protected String grouping;
    protected String lyrics;
    protected Bitmap smallCover;
    protected String title;
    protected short track;
    protected short tracks;
    protected short year;

    public long getDuration() {
        return this.duration;
    }

    public String getTitle() {
        return this.title;
    }

    public String getArtist() {
        return this.artist;
    }

    public String getAlbumArtist() {
        return this.albumArtist;
    }

    public String getAlbum() {
        return this.album;
    }

    public short getYear() {
        return this.year;
    }

    public String getGenre() {
        return this.genre;
    }

    public String getComment() {
        return this.comment;
    }

    public short getTrack() {
        return this.track;
    }

    public short getTracks() {
        return this.tracks;
    }

    public short getDisc() {
        return this.disc;
    }

    public short getDiscs() {
        return this.discs;
    }

    public String getCopyright() {
        return this.copyright;
    }

    public String getComposer() {
        return this.composer;
    }

    public String getGrouping() {
        return this.grouping;
    }

    public boolean isCompilation() {
        return this.compilation;
    }

    public String getLyrics() {
        return this.lyrics;
    }

    public Bitmap getCover() {
        return this.cover;
    }

    public Bitmap getSmallCover() {
        return this.smallCover;
    }

    public static AudioInfo getAudioInfo(File file) {
        byte[] bArr;
        BufferedInputStream bufferedInputStream;
        try {
            bArr = new byte[12];
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            randomAccessFile.readFully(bArr, 0, 8);
            randomAccessFile.close();
            bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        } catch (Exception unused) {
        }
        if (bArr[4] == 102 && bArr[5] == 116 && bArr[6] == 121 && bArr[7] == 112) {
            return new M4AInfo(bufferedInputStream);
        }
        if (file.getAbsolutePath().endsWith("mp3")) {
            return new MP3Info(bufferedInputStream, file.length());
        }
        return null;
    }
}
