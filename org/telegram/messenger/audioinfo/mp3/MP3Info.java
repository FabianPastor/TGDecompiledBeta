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
  
  public MP3Info(InputStream paramInputStream, long paramLong, Level paramLevel)
    throws IOException, ID3v2Exception, MP3Exception
  {
    this.brand = "MP3";
    this.version = "0";
    MP3Input localMP3Input = new MP3Input(paramInputStream);
    Object localObject;
    if (ID3v2Info.isID3v2StartPosition(localMP3Input))
    {
      localObject = new ID3v2Info(localMP3Input, paramLevel);
      this.album = ((ID3v2Info)localObject).getAlbum();
      this.albumArtist = ((ID3v2Info)localObject).getAlbumArtist();
      this.artist = ((ID3v2Info)localObject).getArtist();
      this.comment = ((ID3v2Info)localObject).getComment();
      this.cover = ((ID3v2Info)localObject).getCover();
      this.smallCover = ((ID3v2Info)localObject).getSmallCover();
      this.compilation = ((ID3v2Info)localObject).isCompilation();
      this.composer = ((ID3v2Info)localObject).getComposer();
      this.copyright = ((ID3v2Info)localObject).getCopyright();
      this.disc = ((ID3v2Info)localObject).getDisc();
      this.discs = ((ID3v2Info)localObject).getDiscs();
      this.duration = ((ID3v2Info)localObject).getDuration();
      this.genre = ((ID3v2Info)localObject).getGenre();
      this.grouping = ((ID3v2Info)localObject).getGrouping();
      this.lyrics = ((ID3v2Info)localObject).getLyrics();
      this.title = ((ID3v2Info)localObject).getTitle();
      this.track = ((ID3v2Info)localObject).getTrack();
      this.tracks = ((ID3v2Info)localObject).getTracks();
      this.year = ((ID3v2Info)localObject).getYear();
    }
    if ((this.duration <= 0L) || (this.duration >= 3600000L)) {}
    try
    {
      localObject = new org/telegram/messenger/audioinfo/mp3/MP3Info$1;
      ((1)localObject).<init>(this, paramLong);
      this.duration = calculateDuration(localMP3Input, paramLong, (StopReadCondition)localObject);
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
      if (i > 0)
      {
        paramLong = localMP3Frame.getHeader().getTotalDuration(localMP3Frame.getSize() * i);
        return paramLong;
      }
      i = 1;
      long l1 = paramMP3Input.getPosition();
      long l2 = localMP3Frame.getSize();
      long l3 = localMP3Frame.getSize();
      int j = localMP3Frame.getHeader().getBitrate();
      long l4 = j;
      int k = 0;
      int m = 10000 / localMP3Frame.getHeader().getDuration();
      for (;;)
      {
        if ((i == m) && (k == 0) && (paramLong > 0L))
        {
          paramLong = localMP3Frame.getHeader().getTotalDuration(paramLong - (l1 - l2));
          break;
        }
        localMP3Frame = readNextFrame(paramMP3Input, paramStopReadCondition, localMP3Frame);
        if (localMP3Frame == null)
        {
          paramLong = 1000L * l3 * i * 8L / l4;
          break;
        }
        int n = localMP3Frame.getHeader().getBitrate();
        if (n != j) {
          k = 1;
        }
        l4 += n;
        l3 += localMP3Frame.getSize();
        i++;
      }
    }
    throw new MP3Exception("No audio frame");
  }
  
  MP3Frame readFirstFrame(MP3Input paramMP3Input, StopReadCondition paramStopReadCondition)
    throws IOException
  {
    int i = 0;
    int j;
    if (paramStopReadCondition.stopRead(paramMP3Input))
    {
      j = -1;
      if (j != -1)
      {
        if ((i != 255) || ((j & 0xE0) != 224)) {
          break label366;
        }
        paramMP3Input.mark(2);
        if (!paramStopReadCondition.stopRead(paramMP3Input)) {
          break label77;
        }
      }
    }
    label62:
    Object localObject1;
    label65:
    label77:
    for (i = -1;; i = paramMP3Input.read())
    {
      if (i != -1) {
        break label85;
      }
      localObject1 = null;
      return (MP3Frame)localObject1;
      j = paramMP3Input.read();
      break;
    }
    label85:
    int k;
    label98:
    Object localObject2;
    if (paramStopReadCondition.stopRead(paramMP3Input))
    {
      k = -1;
      if (k == -1) {
        break label392;
      }
      localObject2 = null;
    }
    try
    {
      localObject1 = new org/telegram/messenger/audioinfo/mp3/MP3Frame$Header;
      ((MP3Frame.Header)localObject1).<init>(j, i, k);
      localObject2 = localObject1;
    }
    catch (MP3Exception localMP3Exception1)
    {
      byte[] arrayOfByte;
      label366:
      label392:
      label398:
      label407:
      label415:
      label424:
      label433:
      for (;;) {}
    }
    if (localObject2 != null)
    {
      paramMP3Input.reset();
      paramMP3Input.mark(((MP3Frame.Header)localObject2).getFrameSize() + 2);
      arrayOfByte = new byte[((MP3Frame.Header)localObject2).getFrameSize()];
      arrayOfByte[0] = ((byte)-1);
      arrayOfByte[1] = ((byte)(byte)j);
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
            break label398;
          }
          k = -1;
          if (!paramStopReadCondition.stopRead(paramMP3Input)) {
            break label407;
          }
          i = -1;
          localObject1 = localMP3Frame;
          if (k == -1) {
            break label65;
          }
          localObject1 = localMP3Frame;
          if (i == -1) {
            break label65;
          }
          if ((k == 255) && ((i & 0xFE) == (j & 0xFE)))
          {
            if (!paramStopReadCondition.stopRead(paramMP3Input)) {
              break label415;
            }
            k = -1;
            if (!paramStopReadCondition.stopRead(paramMP3Input)) {
              break label424;
            }
            m = -1;
            localObject1 = localMP3Frame;
            if (k == -1) {
              break label65;
            }
            localObject1 = localMP3Frame;
            if (m == -1) {
              break label65;
            }
            try
            {
              localObject1 = new org/telegram/messenger/audioinfo/mp3/MP3Frame$Header;
              ((MP3Frame.Header)localObject1).<init>(i, k, m);
              if (((MP3Frame.Header)localObject1).isCompatible((MP3Frame.Header)localObject2))
              {
                paramMP3Input.reset();
                paramMP3Input.skipFully(arrayOfByte.length - 2);
                localObject1 = localMP3Frame;
              }
            }
            catch (MP3Exception localMP3Exception2) {}
          }
        }
        paramMP3Input.reset();
        i = j;
        if (!paramStopReadCondition.stopRead(paramMP3Input)) {
          break label433;
        }
        j = -1;
      }
      catch (EOFException paramMP3Input) {}
      k = paramMP3Input.read();
      break label98;
      break label62;
      break label62;
      k = paramMP3Input.read();
      continue;
      i = paramMP3Input.read();
      continue;
      k = paramMP3Input.read();
      continue;
      int m = paramMP3Input.read();
      continue;
      j = paramMP3Input.read();
    }
  }
  
  MP3Frame readNextFrame(MP3Input paramMP3Input, StopReadCondition paramStopReadCondition, MP3Frame paramMP3Frame)
    throws IOException
  {
    MP3Frame.Header localHeader = paramMP3Frame.getHeader();
    paramMP3Input.mark(4);
    int i;
    int j;
    if (paramStopReadCondition.stopRead(paramMP3Input))
    {
      i = -1;
      if (!paramStopReadCondition.stopRead(paramMP3Input)) {
        break label62;
      }
      j = -1;
      label37:
      if ((i != -1) && (j != -1)) {
        break label71;
      }
      paramMP3Input = null;
    }
    for (;;)
    {
      return paramMP3Input;
      i = paramMP3Input.read();
      break;
      label62:
      j = paramMP3Input.read();
      break label37;
      label71:
      int k;
      label104:
      int m;
      if ((i == 255) && ((j & 0xE0) == 224))
      {
        if (paramStopReadCondition.stopRead(paramMP3Input))
        {
          k = -1;
          if (!paramStopReadCondition.stopRead(paramMP3Input)) {
            break label143;
          }
        }
        label143:
        for (m = -1;; m = paramMP3Input.read())
        {
          if ((k != -1) && (m != -1)) {
            break label152;
          }
          paramMP3Input = null;
          break;
          k = paramMP3Input.read();
          break label104;
        }
        label152:
        paramStopReadCondition = null;
      }
      try
      {
        paramMP3Frame = new org/telegram/messenger/audioinfo/mp3/MP3Frame$Header;
        paramMP3Frame.<init>(j, k, m);
        paramStopReadCondition = paramMP3Frame;
      }
      catch (MP3Exception paramMP3Frame)
      {
        for (;;) {}
      }
      if ((paramStopReadCondition != null) && (paramStopReadCondition.isCompatible(localHeader)))
      {
        paramMP3Frame = new byte[paramStopReadCondition.getFrameSize()];
        paramMP3Frame[0] = ((byte)(byte)i);
        paramMP3Frame[1] = ((byte)(byte)j);
        paramMP3Frame[2] = ((byte)(byte)k);
        paramMP3Frame[3] = ((byte)(byte)m);
        try
        {
          paramMP3Input.readFully(paramMP3Frame, 4, paramMP3Frame.length - 4);
          paramMP3Input = new MP3Frame(paramStopReadCondition, paramMP3Frame);
        }
        catch (EOFException paramMP3Input)
        {
          paramMP3Input = null;
        }
      }
      else
      {
        paramMP3Input.reset();
        paramMP3Input = null;
      }
    }
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