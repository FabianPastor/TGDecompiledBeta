package org.telegram.messenger.audioinfo.mp3;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.messenger.audioinfo.AudioInfo;

public class MP3Info
  extends AudioInfo
{
  static final Logger LOGGER = Logger.getLogger(MP3Info.class.getName());
  
  public MP3Info(InputStream paramInputStream, long paramLong)
    throws IOException, ID3v2Exception, MP3Exception
  {
    this(paramInputStream, paramLong, Level.FINEST);
  }
  
  public MP3Info(InputStream paramInputStream, final long paramLong, Level paramLevel)
    throws IOException, ID3v2Exception, MP3Exception
  {
    this.brand = "MP3";
    this.version = "0";
    MP3Input localMP3Input = new MP3Input(paramInputStream);
    if (ID3v2Info.isID3v2StartPosition(localMP3Input))
    {
      ID3v2Info localID3v2Info = new ID3v2Info(localMP3Input, paramLevel);
      this.album = localID3v2Info.getAlbum();
      this.albumArtist = localID3v2Info.getAlbumArtist();
      this.artist = localID3v2Info.getArtist();
      this.comment = localID3v2Info.getComment();
      this.cover = localID3v2Info.getCover();
      this.smallCover = localID3v2Info.getSmallCover();
      this.compilation = localID3v2Info.isCompilation();
      this.composer = localID3v2Info.getComposer();
      this.copyright = localID3v2Info.getCopyright();
      this.disc = localID3v2Info.getDisc();
      this.discs = localID3v2Info.getDiscs();
      this.duration = localID3v2Info.getDuration();
      this.genre = localID3v2Info.getGenre();
      this.grouping = localID3v2Info.getGrouping();
      this.lyrics = localID3v2Info.getLyrics();
      this.title = localID3v2Info.getTitle();
      this.track = localID3v2Info.getTrack();
      this.tracks = localID3v2Info.getTracks();
      this.year = localID3v2Info.getYear();
    }
    if ((this.duration <= 0L) || (this.duration >= 3600000L)) {}
    try
    {
      this.duration = calculateDuration(localMP3Input, paramLong, new StopReadCondition()
      {
        final long stopPosition = paramLong - 128L;
        
        public boolean stopRead(MP3Input paramAnonymousMP3Input)
          throws IOException
        {
          return (paramAnonymousMP3Input.getPosition() == this.stopPosition) && (ID3v1Info.isID3v1StartPosition(paramAnonymousMP3Input));
        }
      });
      if (((this.title == null) || (this.album == null) || (this.artist == null)) && (localMP3Input.getPosition() <= paramLong - 128L))
      {
        localMP3Input.skipFully(paramLong - 128L - localMP3Input.getPosition());
        if (ID3v1Info.isID3v1StartPosition(paramInputStream))
        {
          paramInputStream = new ID3v1Info(paramInputStream);
          if (this.album == null) {
            this.album = paramInputStream.getAlbum();
          }
          if (this.artist == null) {
            this.artist = paramInputStream.getArtist();
          }
          if (this.comment == null) {
            this.comment = paramInputStream.getComment();
          }
          if (this.genre == null) {
            this.genre = paramInputStream.getGenre();
          }
          if (this.title == null) {
            this.title = paramInputStream.getTitle();
          }
          if (this.track == 0) {
            this.track = paramInputStream.getTrack();
          }
          if (this.year == 0) {
            this.year = paramInputStream.getYear();
          }
        }
      }
      return;
    }
    catch (MP3Exception localMP3Exception)
    {
      for (;;)
      {
        if (LOGGER.isLoggable(paramLevel)) {
          LOGGER.log(paramLevel, "Could not determine MP3 duration", localMP3Exception);
        }
      }
    }
  }
  
  long calculateDuration(MP3Input paramMP3Input, long paramLong, StopReadCondition paramStopReadCondition)
    throws IOException, MP3Exception
  {
    MP3Frame localMP3Frame = readFirstFrame(paramMP3Input, paramStopReadCondition);
    if (localMP3Frame != null)
    {
      int i = localMP3Frame.getNumberOfFrames();
      if (i > 0) {
        return localMP3Frame.getHeader().getTotalDuration(localMP3Frame.getSize() * i);
      }
      i = 1;
      long l3 = paramMP3Input.getPosition();
      long l4 = localMP3Frame.getSize();
      long l1 = localMP3Frame.getSize();
      int k = localMP3Frame.getHeader().getBitrate();
      long l2 = k;
      int j = 0;
      int m = 10000 / localMP3Frame.getHeader().getDuration();
      for (;;)
      {
        if ((i == m) && (j == 0) && (paramLong > 0L)) {
          return localMP3Frame.getHeader().getTotalDuration(paramLong - (l3 - l4));
        }
        localMP3Frame = readNextFrame(paramMP3Input, paramStopReadCondition, localMP3Frame);
        if (localMP3Frame == null) {
          return 1000L * l1 * i * 8L / l2;
        }
        int n = localMP3Frame.getHeader().getBitrate();
        if (n != k) {
          j = 1;
        }
        l2 += n;
        l1 += localMP3Frame.getSize();
        i += 1;
      }
    }
    throw new MP3Exception("No audio frame");
  }
  
  MP3Frame readFirstFrame(MP3Input paramMP3Input, StopReadCondition paramStopReadCondition)
    throws IOException
  {
    int j = 0;
    int i;
    if (paramStopReadCondition.stopRead(paramMP3Input))
    {
      i = -1;
      if (i != -1)
      {
        if ((j != 255) || ((i & 0xE0) != 224)) {
          break label357;
        }
        paramMP3Input.mark(2);
        if (!paramStopReadCondition.stopRead(paramMP3Input)) {
          break label77;
        }
      }
    }
    label63:
    Object localObject1;
    label66:
    label77:
    for (j = -1;; j = paramMP3Input.read())
    {
      if (j != -1) {
        break label86;
      }
      localObject1 = null;
      return (MP3Frame)localObject1;
      i = paramMP3Input.read();
      break;
    }
    label86:
    int k;
    label99:
    Object localObject2;
    if (paramStopReadCondition.stopRead(paramMP3Input))
    {
      k = -1;
      if (k == -1) {
        break label382;
      }
      localObject2 = null;
    }
    try
    {
      localObject1 = new MP3Frame.Header(i, j, k);
      localObject2 = localObject1;
    }
    catch (MP3Exception localMP3Exception2)
    {
      byte[] arrayOfByte;
      label357:
      label382:
      label388:
      label397:
      label406:
      label415:
      label424:
      for (;;) {}
    }
    if (localObject2 != null)
    {
      paramMP3Input.reset();
      paramMP3Input.mark(((MP3Frame.Header)localObject2).getFrameSize() + 2);
      arrayOfByte = new byte[((MP3Frame.Header)localObject2).getFrameSize()];
      arrayOfByte[0] = -1;
      arrayOfByte[1] = ((byte)i);
    }
    for (;;)
    {
      try
      {
        paramMP3Input.readFully(arrayOfByte, 2, arrayOfByte.length - 2);
        MP3Frame localMP3Frame = new MP3Frame((MP3Frame.Header)localObject2, arrayOfByte);
        if (!localMP3Frame.isChecksumError())
        {
          if (!paramStopReadCondition.stopRead(paramMP3Input)) {
            break label388;
          }
          k = -1;
          if (!paramStopReadCondition.stopRead(paramMP3Input)) {
            break label397;
          }
          j = -1;
          localObject1 = localMP3Frame;
          if (k == -1) {
            break label66;
          }
          localObject1 = localMP3Frame;
          if (j == -1) {
            break label66;
          }
          if ((k == 255) && ((j & 0xFE) == (i & 0xFE)))
          {
            if (!paramStopReadCondition.stopRead(paramMP3Input)) {
              break label406;
            }
            k = -1;
            if (!paramStopReadCondition.stopRead(paramMP3Input)) {
              break label415;
            }
            m = -1;
            localObject1 = localMP3Frame;
            if (k == -1) {
              break label66;
            }
            localObject1 = localMP3Frame;
            if (m == -1) {
              break label66;
            }
            try
            {
              if (new MP3Frame.Header(j, k, m).isCompatible((MP3Frame.Header)localObject2))
              {
                paramMP3Input.reset();
                paramMP3Input.skipFully(arrayOfByte.length - 2);
                return localMP3Frame;
              }
            }
            catch (MP3Exception localMP3Exception1) {}
          }
        }
        paramMP3Input.reset();
        j = i;
        if (!paramStopReadCondition.stopRead(paramMP3Input)) {
          break label424;
        }
        i = -1;
      }
      catch (EOFException paramMP3Input) {}
      k = paramMP3Input.read();
      break label99;
      break label63;
      break label63;
      k = paramMP3Input.read();
      continue;
      j = paramMP3Input.read();
      continue;
      k = paramMP3Input.read();
      continue;
      int m = paramMP3Input.read();
      continue;
      i = paramMP3Input.read();
    }
  }
  
  MP3Frame readNextFrame(MP3Input paramMP3Input, StopReadCondition paramStopReadCondition, MP3Frame paramMP3Frame)
    throws IOException
  {
    MP3Frame.Header localHeader = paramMP3Frame.getHeader();
    paramMP3Input.mark(4);
    int i;
    if (paramStopReadCondition.stopRead(paramMP3Input))
    {
      i = -1;
      if (!paramStopReadCondition.stopRead(paramMP3Input)) {
        break label60;
      }
    }
    label60:
    for (int j = -1;; j = paramMP3Input.read())
    {
      if ((i != -1) && (j != -1)) {
        break label69;
      }
      return null;
      i = paramMP3Input.read();
      break;
    }
    label69:
    int k;
    int m;
    if ((i == 255) && ((j & 0xE0) == 224))
    {
      if (paramStopReadCondition.stopRead(paramMP3Input))
      {
        k = -1;
        if (!paramStopReadCondition.stopRead(paramMP3Input)) {
          break label138;
        }
      }
      label138:
      for (m = -1;; m = paramMP3Input.read())
      {
        if ((k != -1) && (m != -1)) {
          break label147;
        }
        return null;
        k = paramMP3Input.read();
        break;
      }
      label147:
      paramStopReadCondition = null;
    }
    try
    {
      paramMP3Frame = new MP3Frame.Header(j, k, m);
      paramStopReadCondition = paramMP3Frame;
    }
    catch (MP3Exception paramMP3Frame)
    {
      for (;;) {}
    }
    if ((paramStopReadCondition != null) && (paramStopReadCondition.isCompatible(localHeader)))
    {
      paramMP3Frame = new byte[paramStopReadCondition.getFrameSize()];
      paramMP3Frame[0] = ((byte)i);
      paramMP3Frame[1] = ((byte)j);
      paramMP3Frame[2] = ((byte)k);
      paramMP3Frame[3] = ((byte)m);
      try
      {
        paramMP3Input.readFully(paramMP3Frame, 4, paramMP3Frame.length - 4);
        return new MP3Frame(paramStopReadCondition, paramMP3Frame);
      }
      catch (EOFException paramMP3Input)
      {
        return null;
      }
    }
    paramMP3Input.reset();
    return null;
  }
  
  static abstract interface StopReadCondition
  {
    public abstract boolean stopRead(MP3Input paramMP3Input)
      throws IOException;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/audioinfo/mp3/MP3Info.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */