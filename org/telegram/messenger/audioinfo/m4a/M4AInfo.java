package org.telegram.messenger.audioinfo.m4a;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.audioinfo.mp3.ID3v1Genre;

public class M4AInfo
  extends AudioInfo
{
  private static final String ASCII = "ISO8859_1";
  static final Logger LOGGER = Logger.getLogger(M4AInfo.class.getName());
  private static final String UTF_8 = "UTF-8";
  private final Level debugLevel;
  private byte rating;
  private BigDecimal speed;
  private short tempo;
  private BigDecimal volume;
  
  public M4AInfo(InputStream paramInputStream)
    throws IOException
  {
    this(paramInputStream, Level.FINEST);
  }
  
  public M4AInfo(InputStream paramInputStream, Level paramLevel)
    throws IOException
  {
    this.debugLevel = paramLevel;
    paramInputStream = new MP4Input(paramInputStream);
    if (LOGGER.isLoggable(paramLevel)) {
      LOGGER.log(paramLevel, paramInputStream.toString());
    }
    ftyp(paramInputStream.nextChild("ftyp"));
    moov(paramInputStream.nextChildUpTo("moov"));
  }
  
  void data(MP4Atom paramMP4Atom)
    throws IOException
  {
    if (LOGGER.isLoggable(this.debugLevel)) {
      LOGGER.log(this.debugLevel, paramMP4Atom.toString());
    }
    paramMP4Atom.skip(4);
    paramMP4Atom.skip(4);
    Object localObject = paramMP4Atom.getParent().getType();
    int i = -1;
    switch (((String)localObject).hashCode())
    {
    default: 
      switch (i)
      {
      }
      break;
    }
    label1082:
    do
    {
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                return;
                if (!((String)localObject).equals("©alb")) {
                  break;
                }
                i = 0;
                break;
                if (!((String)localObject).equals("aART")) {
                  break;
                }
                i = 1;
                break;
                if (!((String)localObject).equals("©ART")) {
                  break;
                }
                i = 2;
                break;
                if (!((String)localObject).equals("©cmt")) {
                  break;
                }
                i = 3;
                break;
                if (!((String)localObject).equals("©com")) {
                  break;
                }
                i = 4;
                break;
                if (!((String)localObject).equals("©wrt")) {
                  break;
                }
                i = 5;
                break;
                if (!((String)localObject).equals("covr")) {
                  break;
                }
                i = 6;
                break;
                if (!((String)localObject).equals("cpil")) {
                  break;
                }
                i = 7;
                break;
                if (!((String)localObject).equals("cprt")) {
                  break;
                }
                i = 8;
                break;
                if (!((String)localObject).equals("©cpy")) {
                  break;
                }
                i = 9;
                break;
                if (!((String)localObject).equals("©day")) {
                  break;
                }
                i = 10;
                break;
                if (!((String)localObject).equals("disk")) {
                  break;
                }
                i = 11;
                break;
                if (!((String)localObject).equals("gnre")) {
                  break;
                }
                i = 12;
                break;
                if (!((String)localObject).equals("©gen")) {
                  break;
                }
                i = 13;
                break;
                if (!((String)localObject).equals("©grp")) {
                  break;
                }
                i = 14;
                break;
                if (!((String)localObject).equals("©lyr")) {
                  break;
                }
                i = 15;
                break;
                if (!((String)localObject).equals("©nam")) {
                  break;
                }
                i = 16;
                break;
                if (!((String)localObject).equals("rtng")) {
                  break;
                }
                i = 17;
                break;
                if (!((String)localObject).equals("tmpo")) {
                  break;
                }
                i = 18;
                break;
                if (!((String)localObject).equals("trkn")) {
                  break;
                }
                i = 19;
                break;
                this.album = paramMP4Atom.readString("UTF-8");
                return;
                this.albumArtist = paramMP4Atom.readString("UTF-8");
                return;
                this.artist = paramMP4Atom.readString("UTF-8");
                return;
                this.comment = paramMP4Atom.readString("UTF-8");
                return;
              } while ((this.composer != null) && (this.composer.trim().length() != 0));
              this.composer = paramMP4Atom.readString("UTF-8");
              return;
              for (;;)
              {
                try
                {
                  paramMP4Atom = paramMP4Atom.readBytes();
                  localObject = new BitmapFactory.Options();
                  ((BitmapFactory.Options)localObject).inJustDecodeBounds = true;
                  ((BitmapFactory.Options)localObject).inSampleSize = 1;
                  BitmapFactory.decodeByteArray(paramMP4Atom, 0, paramMP4Atom.length, (BitmapFactory.Options)localObject);
                  if ((((BitmapFactory.Options)localObject).outWidth > 800) || (((BitmapFactory.Options)localObject).outHeight > 800))
                  {
                    i = Math.max(((BitmapFactory.Options)localObject).outWidth, ((BitmapFactory.Options)localObject).outHeight);
                    if (i > 800)
                    {
                      ((BitmapFactory.Options)localObject).inSampleSize *= 2;
                      i /= 2;
                      continue;
                    }
                  }
                  ((BitmapFactory.Options)localObject).inJustDecodeBounds = false;
                  this.cover = BitmapFactory.decodeByteArray(paramMP4Atom, 0, paramMP4Atom.length, (BitmapFactory.Options)localObject);
                  if (this.cover == null) {
                    break;
                  }
                  float f = Math.max(this.cover.getWidth(), this.cover.getHeight()) / 120.0F;
                  if (f > 0.0F)
                  {
                    this.smallCover = Bitmap.createScaledBitmap(this.cover, (int)(this.cover.getWidth() / f), (int)(this.cover.getHeight() / f), true);
                    if (this.smallCover != null) {
                      break;
                    }
                    this.smallCover = this.cover;
                    return;
                  }
                }
                catch (Exception paramMP4Atom)
                {
                  paramMP4Atom.printStackTrace();
                  return;
                }
                this.smallCover = this.cover;
              }
              this.compilation = paramMP4Atom.readBoolean();
              return;
            } while ((this.copyright != null) && (this.copyright.trim().length() != 0));
            this.copyright = paramMP4Atom.readString("UTF-8");
            return;
            paramMP4Atom = paramMP4Atom.readString("UTF-8").trim();
          } while (paramMP4Atom.length() < 4);
          try
          {
            this.year = Short.valueOf(paramMP4Atom.substring(0, 4)).shortValue();
            return;
          }
          catch (NumberFormatException paramMP4Atom)
          {
            return;
          }
          paramMP4Atom.skip(2);
          this.disc = paramMP4Atom.readShort();
          this.discs = paramMP4Atom.readShort();
          return;
        } while ((this.genre != null) && (this.genre.trim().length() != 0));
        if (paramMP4Atom.getRemaining() != 2L) {
          break label1082;
        }
        paramMP4Atom = ID3v1Genre.getGenre(paramMP4Atom.readShort() - 1);
      } while (paramMP4Atom == null);
      this.genre = paramMP4Atom.getDescription();
      return;
      this.genre = paramMP4Atom.readString("UTF-8");
      return;
    } while ((this.genre != null) && (this.genre.trim().length() != 0));
    this.genre = paramMP4Atom.readString("UTF-8");
    return;
    this.grouping = paramMP4Atom.readString("UTF-8");
    return;
    this.lyrics = paramMP4Atom.readString("UTF-8");
    return;
    this.title = paramMP4Atom.readString("UTF-8");
    return;
    this.rating = paramMP4Atom.readByte();
    return;
    this.tempo = paramMP4Atom.readShort();
    return;
    paramMP4Atom.skip(2);
    this.track = paramMP4Atom.readShort();
    this.tracks = paramMP4Atom.readShort();
  }
  
  void ftyp(MP4Atom paramMP4Atom)
    throws IOException
  {
    if (LOGGER.isLoggable(this.debugLevel)) {
      LOGGER.log(this.debugLevel, paramMP4Atom.toString());
    }
    this.brand = paramMP4Atom.readString(4, "ISO8859_1").trim();
    if (this.brand.matches("M4V|MP4|mp42|isom")) {
      LOGGER.warning(paramMP4Atom.getPath() + ": brand=" + this.brand + " (experimental)");
    }
    for (;;)
    {
      this.version = String.valueOf(paramMP4Atom.readInt());
      return;
      if (!this.brand.matches("M4A|M4P")) {
        LOGGER.warning(paramMP4Atom.getPath() + ": brand=" + this.brand + " (expected M4A or M4P)");
      }
    }
  }
  
  public byte getRating()
  {
    return this.rating;
  }
  
  public BigDecimal getSpeed()
  {
    return this.speed;
  }
  
  public short getTempo()
  {
    return this.tempo;
  }
  
  public BigDecimal getVolume()
  {
    return this.volume;
  }
  
  void ilst(MP4Atom paramMP4Atom)
    throws IOException
  {
    if (LOGGER.isLoggable(this.debugLevel)) {
      LOGGER.log(this.debugLevel, paramMP4Atom.toString());
    }
    while (paramMP4Atom.hasMoreChildren())
    {
      MP4Atom localMP4Atom = paramMP4Atom.nextChild();
      if (LOGGER.isLoggable(this.debugLevel)) {
        LOGGER.log(this.debugLevel, localMP4Atom.toString());
      }
      if (localMP4Atom.getRemaining() == 0L)
      {
        if (LOGGER.isLoggable(this.debugLevel)) {
          LOGGER.log(this.debugLevel, localMP4Atom.getPath() + ": contains no value");
        }
      }
      else {
        data(localMP4Atom.nextChildUpTo("data"));
      }
    }
  }
  
  void mdhd(MP4Atom paramMP4Atom)
    throws IOException
  {
    if (LOGGER.isLoggable(this.debugLevel)) {
      LOGGER.log(this.debugLevel, paramMP4Atom.toString());
    }
    int j = paramMP4Atom.readByte();
    paramMP4Atom.skip(3);
    int i;
    long l;
    if (j == 1)
    {
      i = 16;
      paramMP4Atom.skip(i);
      i = paramMP4Atom.readInt();
      if (j != 1) {
        break label95;
      }
      l = paramMP4Atom.readLong();
      label66:
      if (this.duration != 0L) {
        break label105;
      }
      this.duration = (1000L * l / i);
    }
    label95:
    label105:
    while ((!LOGGER.isLoggable(this.debugLevel)) || (Math.abs(this.duration - 1000L * l / i) <= 2L))
    {
      return;
      i = 8;
      break;
      l = paramMP4Atom.readInt();
      break label66;
    }
    LOGGER.log(this.debugLevel, "mdhd: duration " + this.duration + " -> " + 1000L * l / i);
  }
  
  void mdia(MP4Atom paramMP4Atom)
    throws IOException
  {
    if (LOGGER.isLoggable(this.debugLevel)) {
      LOGGER.log(this.debugLevel, paramMP4Atom.toString());
    }
    mdhd(paramMP4Atom.nextChild("mdhd"));
  }
  
  void meta(MP4Atom paramMP4Atom)
    throws IOException
  {
    if (LOGGER.isLoggable(this.debugLevel)) {
      LOGGER.log(this.debugLevel, paramMP4Atom.toString());
    }
    paramMP4Atom.skip(4);
    while (paramMP4Atom.hasMoreChildren())
    {
      MP4Atom localMP4Atom = paramMP4Atom.nextChild();
      if ("ilst".equals(localMP4Atom.getType())) {
        ilst(localMP4Atom);
      }
    }
  }
  
  void moov(MP4Atom paramMP4Atom)
    throws IOException
  {
    if (LOGGER.isLoggable(this.debugLevel)) {
      LOGGER.log(this.debugLevel, paramMP4Atom.toString());
    }
    while (paramMP4Atom.hasMoreChildren())
    {
      MP4Atom localMP4Atom = paramMP4Atom.nextChild();
      String str = localMP4Atom.getType();
      int i = -1;
      switch (str.hashCode())
      {
      }
      for (;;)
      {
        switch (i)
        {
        default: 
          break;
        case 0: 
          mvhd(localMP4Atom);
          break;
          if (str.equals("mvhd"))
          {
            i = 0;
            continue;
            if (str.equals("trak"))
            {
              i = 1;
              continue;
              if (str.equals("udta")) {
                i = 2;
              }
            }
          }
          break;
        }
      }
      trak(localMP4Atom);
      continue;
      udta(localMP4Atom);
    }
  }
  
  void mvhd(MP4Atom paramMP4Atom)
    throws IOException
  {
    if (LOGGER.isLoggable(this.debugLevel)) {
      LOGGER.log(this.debugLevel, paramMP4Atom.toString());
    }
    int j = paramMP4Atom.readByte();
    paramMP4Atom.skip(3);
    int i;
    long l;
    if (j == 1)
    {
      i = 16;
      paramMP4Atom.skip(i);
      i = paramMP4Atom.readInt();
      if (j != 1) {
        break label111;
      }
      l = paramMP4Atom.readLong();
      label66:
      if (this.duration != 0L) {
        break label121;
      }
      this.duration = (1000L * l / i);
    }
    for (;;)
    {
      this.speed = paramMP4Atom.readIntegerFixedPoint();
      this.volume = paramMP4Atom.readShortFixedPoint();
      return;
      i = 8;
      break;
      label111:
      l = paramMP4Atom.readInt();
      break label66;
      label121:
      if ((LOGGER.isLoggable(this.debugLevel)) && (Math.abs(this.duration - 1000L * l / i) > 2L)) {
        LOGGER.log(this.debugLevel, "mvhd: duration " + this.duration + " -> " + 1000L * l / i);
      }
    }
  }
  
  void trak(MP4Atom paramMP4Atom)
    throws IOException
  {
    if (LOGGER.isLoggable(this.debugLevel)) {
      LOGGER.log(this.debugLevel, paramMP4Atom.toString());
    }
    mdia(paramMP4Atom.nextChildUpTo("mdia"));
  }
  
  void udta(MP4Atom paramMP4Atom)
    throws IOException
  {
    if (LOGGER.isLoggable(this.debugLevel)) {
      LOGGER.log(this.debugLevel, paramMP4Atom.toString());
    }
    while (paramMP4Atom.hasMoreChildren())
    {
      MP4Atom localMP4Atom = paramMP4Atom.nextChild();
      if ("meta".equals(localMP4Atom.getType())) {
        meta(localMP4Atom);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/audioinfo/m4a/M4AInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */