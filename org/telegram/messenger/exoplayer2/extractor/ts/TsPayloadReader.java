package org.telegram.messenger.exoplayer2.extractor.ts;

import android.util.SparseArray;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.TimestampAdjuster;

public abstract interface TsPayloadReader
{
  public abstract void consume(ParsableByteArray paramParsableByteArray, boolean paramBoolean)
    throws ParserException;
  
  public abstract void init(TimestampAdjuster paramTimestampAdjuster, ExtractorOutput paramExtractorOutput, TrackIdGenerator paramTrackIdGenerator);
  
  public abstract void seek();
  
  public static final class DvbSubtitleInfo
  {
    public final byte[] initializationData;
    public final String language;
    public final int type;
    
    public DvbSubtitleInfo(String paramString, int paramInt, byte[] paramArrayOfByte)
    {
      this.language = paramString;
      this.type = paramInt;
      this.initializationData = paramArrayOfByte;
    }
  }
  
  public static final class EsInfo
  {
    public final byte[] descriptorBytes;
    public final List<TsPayloadReader.DvbSubtitleInfo> dvbSubtitleInfos;
    public final String language;
    public final int streamType;
    
    public EsInfo(int paramInt, String paramString, List<TsPayloadReader.DvbSubtitleInfo> paramList, byte[] paramArrayOfByte)
    {
      this.streamType = paramInt;
      this.language = paramString;
      if (paramList == null) {}
      for (paramString = Collections.emptyList();; paramString = Collections.unmodifiableList(paramList))
      {
        this.dvbSubtitleInfos = paramString;
        this.descriptorBytes = paramArrayOfByte;
        return;
      }
    }
  }
  
  public static abstract interface Factory
  {
    public abstract SparseArray<TsPayloadReader> createInitialPayloadReaders();
    
    public abstract TsPayloadReader createPayloadReader(int paramInt, TsPayloadReader.EsInfo paramEsInfo);
  }
  
  public static final class TrackIdGenerator
  {
    private static final int ID_UNSET = Integer.MIN_VALUE;
    private final int firstTrackId;
    private String formatId;
    private final String formatIdPrefix;
    private int trackId;
    private final int trackIdIncrement;
    
    public TrackIdGenerator(int paramInt1, int paramInt2)
    {
      this(Integer.MIN_VALUE, paramInt1, paramInt2);
    }
    
    public TrackIdGenerator(int paramInt1, int paramInt2, int paramInt3)
    {
      if (paramInt1 != Integer.MIN_VALUE) {}
      for (String str = paramInt1 + "/";; str = "")
      {
        this.formatIdPrefix = str;
        this.firstTrackId = paramInt2;
        this.trackIdIncrement = paramInt3;
        this.trackId = Integer.MIN_VALUE;
        return;
      }
    }
    
    private void maybeThrowUninitializedError()
    {
      if (this.trackId == Integer.MIN_VALUE) {
        throw new IllegalStateException("generateNewId() must be called before retrieving ids.");
      }
    }
    
    public void generateNewId()
    {
      if (this.trackId == Integer.MIN_VALUE) {}
      for (int i = this.firstTrackId;; i = this.trackId + this.trackIdIncrement)
      {
        this.trackId = i;
        this.formatId = (this.formatIdPrefix + this.trackId);
        return;
      }
    }
    
    public String getFormatId()
    {
      maybeThrowUninitializedError();
      return this.formatId;
    }
    
    public int getTrackId()
    {
      maybeThrowUninitializedError();
      return this.trackId;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/ts/TsPayloadReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */