package org.telegram.messenger.exoplayer2.extractor.ts;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class DefaultStreamReaderFactory
  implements ElementaryStreamReader.Factory
{
  public static final int FLAG_ALLOW_NON_IDR_KEYFRAMES = 1;
  public static final int FLAG_DETECT_ACCESS_UNITS = 8;
  public static final int FLAG_IGNORE_AAC_STREAM = 2;
  public static final int FLAG_IGNORE_H264_STREAM = 4;
  private final int flags;
  
  public DefaultStreamReaderFactory()
  {
    this(0);
  }
  
  public DefaultStreamReaderFactory(int paramInt)
  {
    this.flags = paramInt;
  }
  
  public ElementaryStreamReader createStreamReader(int paramInt, ElementaryStreamReader.EsInfo paramEsInfo)
  {
    boolean bool2 = true;
    switch (paramInt)
    {
    default: 
    case 3: 
    case 4: 
    case 15: 
    case 129: 
    case 135: 
    case 130: 
    case 138: 
    case 2: 
    case 27: 
      do
      {
        do
        {
          return null;
          return new MpegAudioReader(paramEsInfo.language);
        } while ((this.flags & 0x2) != 0);
        return new AdtsReader(false, paramEsInfo.language);
        return new Ac3Reader(paramEsInfo.language);
        return new DtsReader(paramEsInfo.language);
        return new H262Reader();
      } while ((this.flags & 0x4) != 0);
      boolean bool1;
      if ((this.flags & 0x1) != 0)
      {
        bool1 = true;
        if ((this.flags & 0x8) == 0) {
          break label218;
        }
      }
      for (;;)
      {
        return new H264Reader(bool1, bool2);
        bool1 = false;
        break;
        bool2 = false;
      }
    case 36: 
      label218:
      return new H265Reader();
    }
    return new Id3Reader();
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Flags {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/ts/DefaultStreamReaderFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */