package org.telegram.messenger.exoplayer2.extractor.ts;

import android.util.SparseArray;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class DefaultTsPayloadReaderFactory
  implements TsPayloadReader.Factory
{
  private static final int DESCRIPTOR_TAG_CAPTION_SERVICE = 134;
  public static final int FLAG_ALLOW_NON_IDR_KEYFRAMES = 1;
  public static final int FLAG_DETECT_ACCESS_UNITS = 8;
  public static final int FLAG_IGNORE_AAC_STREAM = 2;
  public static final int FLAG_IGNORE_H264_STREAM = 4;
  public static final int FLAG_IGNORE_SPLICE_INFO_STREAM = 16;
  public static final int FLAG_OVERRIDE_CAPTION_DESCRIPTORS = 32;
  private final List<Format> closedCaptionFormats;
  private final int flags;
  
  public DefaultTsPayloadReaderFactory()
  {
    this(0);
  }
  
  public DefaultTsPayloadReaderFactory(int paramInt)
  {
    this(paramInt, Collections.emptyList());
  }
  
  public DefaultTsPayloadReaderFactory(int paramInt, List<Format> paramList)
  {
    this.flags = paramInt;
    Object localObject = paramList;
    if (!isSet(32))
    {
      localObject = paramList;
      if (paramList.isEmpty()) {
        localObject = Collections.singletonList(Format.createTextSampleFormat(null, "application/cea-608", 0, null));
      }
    }
    this.closedCaptionFormats = ((List)localObject);
  }
  
  private SeiReader buildSeiReader(TsPayloadReader.EsInfo paramEsInfo)
  {
    if (isSet(32)) {}
    for (paramEsInfo = new SeiReader(this.closedCaptionFormats);; paramEsInfo = new SeiReader(paramEsInfo))
    {
      return paramEsInfo;
      ParsableByteArray localParsableByteArray = new ParsableByteArray(paramEsInfo.descriptorBytes);
      paramEsInfo = this.closedCaptionFormats;
      while (localParsableByteArray.bytesLeft() > 0)
      {
        int i = localParsableByteArray.readUnsignedByte();
        int j = localParsableByteArray.readUnsignedByte();
        int k = localParsableByteArray.getPosition();
        if (i == 134)
        {
          ArrayList localArrayList = new ArrayList();
          int m = localParsableByteArray.readUnsignedByte();
          i = 0;
          paramEsInfo = localArrayList;
          if (i < (m & 0x1F))
          {
            String str = localParsableByteArray.readString(3);
            int n = localParsableByteArray.readUnsignedByte();
            if ((n & 0x80) != 0)
            {
              i1 = 1;
              label125:
              if (i1 == 0) {
                break label178;
              }
              paramEsInfo = "application/cea-708";
            }
            for (int i1 = n & 0x3F;; i1 = 1)
            {
              localArrayList.add(Format.createTextSampleFormat(null, paramEsInfo, null, -1, 0, str, i1, null));
              localParsableByteArray.skipBytes(2);
              i++;
              break;
              i1 = 0;
              break label125;
              label178:
              paramEsInfo = "application/cea-608";
            }
          }
        }
        localParsableByteArray.setPosition(k + j);
      }
    }
  }
  
  private boolean isSet(int paramInt)
  {
    if ((this.flags & paramInt) != 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public SparseArray<TsPayloadReader> createInitialPayloadReaders()
  {
    return new SparseArray();
  }
  
  public TsPayloadReader createPayloadReader(int paramInt, TsPayloadReader.EsInfo paramEsInfo)
  {
    Object localObject = null;
    switch (paramInt)
    {
    }
    for (;;)
    {
      return (TsPayloadReader)localObject;
      localObject = new PesReader(new MpegAudioReader(paramEsInfo.language));
      continue;
      if (!isSet(2))
      {
        localObject = new PesReader(new AdtsReader(false, paramEsInfo.language));
        continue;
        if (!isSet(2))
        {
          localObject = new PesReader(new LatmReader(paramEsInfo.language));
          continue;
          localObject = new PesReader(new Ac3Reader(paramEsInfo.language));
          continue;
          localObject = new PesReader(new DtsReader(paramEsInfo.language));
          continue;
          localObject = new PesReader(new H262Reader());
          continue;
          if (!isSet(4))
          {
            localObject = new PesReader(new H264Reader(buildSeiReader(paramEsInfo), isSet(1), isSet(8)));
            continue;
            localObject = new PesReader(new H265Reader(buildSeiReader(paramEsInfo)));
            continue;
            if (!isSet(16))
            {
              localObject = new SectionReader(new SpliceInfoSectionReader());
              continue;
              localObject = new PesReader(new Id3Reader());
              continue;
              localObject = new PesReader(new DvbSubtitleReader(paramEsInfo.dvbSubtitleInfos));
            }
          }
        }
      }
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Flags {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/ts/DefaultTsPayloadReaderFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */