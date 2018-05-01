package org.telegram.messenger.audioinfo;

import android.graphics.Bitmap;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import org.telegram.messenger.audioinfo.m4a.M4AInfo;
import org.telegram.messenger.audioinfo.mp3.MP3Info;

public abstract class AudioInfo
{
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
  protected String version;
  protected short year;
  
  public static AudioInfo getAudioInfo(File paramFile)
  {
    for (;;)
    {
      try
      {
        byte[] arrayOfByte = new byte[12];
        localObject = new java/io/RandomAccessFile;
        ((RandomAccessFile)localObject).<init>(paramFile, "r");
        ((RandomAccessFile)localObject).readFully(arrayOfByte, 0, 8);
        ((RandomAccessFile)localObject).close();
        localObject = new java/io/BufferedInputStream;
        FileInputStream localFileInputStream = new java/io/FileInputStream;
        localFileInputStream.<init>(paramFile);
        ((BufferedInputStream)localObject).<init>(localFileInputStream);
        if ((arrayOfByte[4] != 102) || (arrayOfByte[5] != 116) || (arrayOfByte[6] != 121) || (arrayOfByte[7] != 112)) {
          continue;
        }
        paramFile = new org/telegram/messenger/audioinfo/m4a/M4AInfo;
        paramFile.<init>((InputStream)localObject);
      }
      catch (Exception paramFile)
      {
        Object localObject;
        paramFile = null;
        continue;
        paramFile = null;
        continue;
      }
      return paramFile;
      if (!paramFile.getAbsolutePath().endsWith("mp3")) {
        continue;
      }
      paramFile = new MP3Info((InputStream)localObject, paramFile.length());
    }
  }
  
  public String getAlbum()
  {
    return this.album;
  }
  
  public String getAlbumArtist()
  {
    return this.albumArtist;
  }
  
  public String getArtist()
  {
    return this.artist;
  }
  
  public String getBrand()
  {
    return this.brand;
  }
  
  public String getComment()
  {
    return this.comment;
  }
  
  public String getComposer()
  {
    return this.composer;
  }
  
  public String getCopyright()
  {
    return this.copyright;
  }
  
  public Bitmap getCover()
  {
    return this.cover;
  }
  
  public short getDisc()
  {
    return this.disc;
  }
  
  public short getDiscs()
  {
    return this.discs;
  }
  
  public long getDuration()
  {
    return this.duration;
  }
  
  public String getGenre()
  {
    return this.genre;
  }
  
  public String getGrouping()
  {
    return this.grouping;
  }
  
  public String getLyrics()
  {
    return this.lyrics;
  }
  
  public Bitmap getSmallCover()
  {
    return this.smallCover;
  }
  
  public String getTitle()
  {
    return this.title;
  }
  
  public short getTrack()
  {
    return this.track;
  }
  
  public short getTracks()
  {
    return this.tracks;
  }
  
  public String getVersion()
  {
    return this.version;
  }
  
  public short getYear()
  {
    return this.year;
  }
  
  public boolean isCompilation()
  {
    return this.compilation;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/audioinfo/AudioInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */