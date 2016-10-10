package org.telegram.messenger.exoplayer.hls;

import org.telegram.messenger.exoplayer.chunk.Format;
import org.telegram.messenger.exoplayer.chunk.MediaChunk;
import org.telegram.messenger.exoplayer.upstream.DataSource;
import org.telegram.messenger.exoplayer.upstream.DataSpec;

public final class TsChunk
  extends MediaChunk
{
  private long adjustedEndTimeUs;
  private int bytesLoaded;
  public final int discontinuitySequenceNumber;
  public final HlsExtractorWrapper extractorWrapper;
  private final boolean isEncrypted;
  private volatile boolean loadCanceled;
  
  public TsChunk(DataSource paramDataSource, DataSpec paramDataSpec, int paramInt1, Format paramFormat, long paramLong1, long paramLong2, int paramInt2, int paramInt3, HlsExtractorWrapper paramHlsExtractorWrapper, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    super(buildDataSource(paramDataSource, paramArrayOfByte1, paramArrayOfByte2), paramDataSpec, paramInt1, paramFormat, paramLong1, paramLong2, paramInt2);
    this.discontinuitySequenceNumber = paramInt3;
    this.extractorWrapper = paramHlsExtractorWrapper;
    this.isEncrypted = (this.dataSource instanceof Aes128DataSource);
    this.adjustedEndTimeUs = paramLong1;
  }
  
  private static DataSource buildDataSource(DataSource paramDataSource, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    if ((paramArrayOfByte1 == null) || (paramArrayOfByte2 == null)) {
      return paramDataSource;
    }
    return new Aes128DataSource(paramDataSource, paramArrayOfByte1, paramArrayOfByte2);
  }
  
  public long bytesLoaded()
  {
    return this.bytesLoaded;
  }
  
  public void cancelLoad()
  {
    this.loadCanceled = true;
  }
  
  public long getAdjustedEndTimeUs()
  {
    return this.adjustedEndTimeUs;
  }
  
  public boolean isLoadCanceled()
  {
    return this.loadCanceled;
  }
  
  /* Error */
  public void load()
    throws java.io.IOException, java.lang.InterruptedException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 35	org/telegram/messenger/exoplayer/hls/TsChunk:isEncrypted	Z
    //   4: ifeq +93 -> 97
    //   7: aload_0
    //   8: getfield 60	org/telegram/messenger/exoplayer/hls/TsChunk:dataSpec	Lorg/telegram/messenger/exoplayer/upstream/DataSpec;
    //   11: astore 4
    //   13: aload_0
    //   14: getfield 44	org/telegram/messenger/exoplayer/hls/TsChunk:bytesLoaded	I
    //   17: ifeq +75 -> 92
    //   20: iconst_1
    //   21: istore_1
    //   22: new 62	org/telegram/messenger/exoplayer/extractor/DefaultExtractorInput
    //   25: dup
    //   26: aload_0
    //   27: getfield 31	org/telegram/messenger/exoplayer/hls/TsChunk:dataSource	Lorg/telegram/messenger/exoplayer/upstream/DataSource;
    //   30: aload 4
    //   32: getfield 67	org/telegram/messenger/exoplayer/upstream/DataSpec:absoluteStreamPosition	J
    //   35: aload_0
    //   36: getfield 31	org/telegram/messenger/exoplayer/hls/TsChunk:dataSource	Lorg/telegram/messenger/exoplayer/upstream/DataSource;
    //   39: aload 4
    //   41: invokeinterface 73 2 0
    //   46: invokespecial 76	org/telegram/messenger/exoplayer/extractor/DefaultExtractorInput:<init>	(Lorg/telegram/messenger/exoplayer/upstream/DataSource;JJ)V
    //   49: astore 4
    //   51: iload_1
    //   52: ifeq +14 -> 66
    //   55: aload 4
    //   57: aload_0
    //   58: getfield 44	org/telegram/messenger/exoplayer/hls/TsChunk:bytesLoaded	I
    //   61: invokeinterface 82 2 0
    //   66: iconst_0
    //   67: istore_1
    //   68: iload_1
    //   69: ifne +46 -> 115
    //   72: aload_0
    //   73: getfield 48	org/telegram/messenger/exoplayer/hls/TsChunk:loadCanceled	Z
    //   76: ifne +39 -> 115
    //   79: aload_0
    //   80: getfield 27	org/telegram/messenger/exoplayer/hls/TsChunk:extractorWrapper	Lorg/telegram/messenger/exoplayer/hls/HlsExtractorWrapper;
    //   83: aload 4
    //   85: invokevirtual 88	org/telegram/messenger/exoplayer/hls/HlsExtractorWrapper:read	(Lorg/telegram/messenger/exoplayer/extractor/ExtractorInput;)I
    //   88: istore_1
    //   89: goto -21 -> 68
    //   92: iconst_0
    //   93: istore_1
    //   94: goto -72 -> 22
    //   97: aload_0
    //   98: getfield 60	org/telegram/messenger/exoplayer/hls/TsChunk:dataSpec	Lorg/telegram/messenger/exoplayer/upstream/DataSpec;
    //   101: aload_0
    //   102: getfield 44	org/telegram/messenger/exoplayer/hls/TsChunk:bytesLoaded	I
    //   105: invokestatic 94	org/telegram/messenger/exoplayer/util/Util:getRemainderDataSpec	(Lorg/telegram/messenger/exoplayer/upstream/DataSpec;I)Lorg/telegram/messenger/exoplayer/upstream/DataSpec;
    //   108: astore 4
    //   110: iconst_0
    //   111: istore_1
    //   112: goto -90 -> 22
    //   115: aload_0
    //   116: getfield 27	org/telegram/messenger/exoplayer/hls/TsChunk:extractorWrapper	Lorg/telegram/messenger/exoplayer/hls/HlsExtractorWrapper;
    //   119: invokevirtual 96	org/telegram/messenger/exoplayer/hls/HlsExtractorWrapper:getAdjustedEndTimeUs	()J
    //   122: lstore_2
    //   123: lload_2
    //   124: ldc2_w 97
    //   127: lcmp
    //   128: ifeq +8 -> 136
    //   131: aload_0
    //   132: lload_2
    //   133: putfield 37	org/telegram/messenger/exoplayer/hls/TsChunk:adjustedEndTimeUs	J
    //   136: aload_0
    //   137: aload 4
    //   139: invokeinterface 101 1 0
    //   144: aload_0
    //   145: getfield 60	org/telegram/messenger/exoplayer/hls/TsChunk:dataSpec	Lorg/telegram/messenger/exoplayer/upstream/DataSpec;
    //   148: getfield 67	org/telegram/messenger/exoplayer/upstream/DataSpec:absoluteStreamPosition	J
    //   151: lsub
    //   152: l2i
    //   153: putfield 44	org/telegram/messenger/exoplayer/hls/TsChunk:bytesLoaded	I
    //   156: aload_0
    //   157: getfield 31	org/telegram/messenger/exoplayer/hls/TsChunk:dataSource	Lorg/telegram/messenger/exoplayer/upstream/DataSource;
    //   160: invokeinterface 104 1 0
    //   165: return
    //   166: astore 5
    //   168: aload_0
    //   169: aload 4
    //   171: invokeinterface 101 1 0
    //   176: aload_0
    //   177: getfield 60	org/telegram/messenger/exoplayer/hls/TsChunk:dataSpec	Lorg/telegram/messenger/exoplayer/upstream/DataSpec;
    //   180: getfield 67	org/telegram/messenger/exoplayer/upstream/DataSpec:absoluteStreamPosition	J
    //   183: lsub
    //   184: l2i
    //   185: putfield 44	org/telegram/messenger/exoplayer/hls/TsChunk:bytesLoaded	I
    //   188: aload 5
    //   190: athrow
    //   191: astore 4
    //   193: aload_0
    //   194: getfield 31	org/telegram/messenger/exoplayer/hls/TsChunk:dataSource	Lorg/telegram/messenger/exoplayer/upstream/DataSource;
    //   197: invokeinterface 104 1 0
    //   202: aload 4
    //   204: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	205	0	this	TsChunk
    //   21	91	1	i	int
    //   122	11	2	l	long
    //   11	159	4	localObject1	Object
    //   191	12	4	localObject2	Object
    //   166	23	5	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   72	89	166	finally
    //   115	123	166	finally
    //   131	136	166	finally
    //   22	51	191	finally
    //   55	66	191	finally
    //   136	156	191	finally
    //   168	191	191	finally
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/hls/TsChunk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */