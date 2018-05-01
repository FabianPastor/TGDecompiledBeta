package org.telegram.messenger.exoplayer2.source.hls;

import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.source.chunk.Chunk;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;

final class HlsInitializationChunk
  extends Chunk
{
  private int bytesLoaded;
  public final Extractor extractor;
  public final Format format;
  private volatile boolean loadCanceled;
  
  public HlsInitializationChunk(DataSource paramDataSource, DataSpec paramDataSpec, int paramInt, Object paramObject, Extractor paramExtractor, Format paramFormat)
  {
    super(paramDataSource, paramDataSpec, 0, null, paramInt, paramObject, -9223372036854775807L, -9223372036854775807L);
    this.extractor = paramExtractor;
    this.format = paramFormat;
  }
  
  public long bytesLoaded()
  {
    return this.bytesLoaded;
  }
  
  public void cancelLoad()
  {
    this.loadCanceled = true;
  }
  
  public void init(HlsSampleStreamWrapper paramHlsSampleStreamWrapper)
  {
    this.extractor.init(paramHlsSampleStreamWrapper);
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
    //   1: getfield 49	org/telegram/messenger/exoplayer2/source/hls/HlsInitializationChunk:dataSpec	Lorg/telegram/messenger/exoplayer2/upstream/DataSpec;
    //   4: aload_0
    //   5: getfield 27	org/telegram/messenger/exoplayer2/source/hls/HlsInitializationChunk:bytesLoaded	I
    //   8: invokestatic 55	org/telegram/messenger/exoplayer2/util/Util:getRemainderDataSpec	(Lorg/telegram/messenger/exoplayer2/upstream/DataSpec;I)Lorg/telegram/messenger/exoplayer2/upstream/DataSpec;
    //   11: astore_2
    //   12: new 57	org/telegram/messenger/exoplayer2/extractor/DefaultExtractorInput
    //   15: dup
    //   16: aload_0
    //   17: getfield 61	org/telegram/messenger/exoplayer2/source/hls/HlsInitializationChunk:dataSource	Lorg/telegram/messenger/exoplayer2/upstream/DataSource;
    //   20: aload_2
    //   21: getfield 67	org/telegram/messenger/exoplayer2/upstream/DataSpec:absoluteStreamPosition	J
    //   24: aload_0
    //   25: getfield 61	org/telegram/messenger/exoplayer2/source/hls/HlsInitializationChunk:dataSource	Lorg/telegram/messenger/exoplayer2/upstream/DataSource;
    //   28: aload_2
    //   29: invokeinterface 73 2 0
    //   34: invokespecial 76	org/telegram/messenger/exoplayer2/extractor/DefaultExtractorInput:<init>	(Lorg/telegram/messenger/exoplayer2/upstream/DataSource;JJ)V
    //   37: astore_2
    //   38: iconst_0
    //   39: istore_1
    //   40: iload_1
    //   41: ifne +25 -> 66
    //   44: aload_0
    //   45: getfield 31	org/telegram/messenger/exoplayer2/source/hls/HlsInitializationChunk:loadCanceled	Z
    //   48: ifne +18 -> 66
    //   51: aload_0
    //   52: getfield 21	org/telegram/messenger/exoplayer2/source/hls/HlsInitializationChunk:extractor	Lorg/telegram/messenger/exoplayer2/extractor/Extractor;
    //   55: aload_2
    //   56: aconst_null
    //   57: invokeinterface 80 3 0
    //   62: istore_1
    //   63: goto -23 -> 40
    //   66: aload_0
    //   67: aload_2
    //   68: invokeinterface 85 1 0
    //   73: aload_0
    //   74: getfield 49	org/telegram/messenger/exoplayer2/source/hls/HlsInitializationChunk:dataSpec	Lorg/telegram/messenger/exoplayer2/upstream/DataSpec;
    //   77: getfield 67	org/telegram/messenger/exoplayer2/upstream/DataSpec:absoluteStreamPosition	J
    //   80: lsub
    //   81: l2i
    //   82: putfield 27	org/telegram/messenger/exoplayer2/source/hls/HlsInitializationChunk:bytesLoaded	I
    //   85: aload_0
    //   86: getfield 61	org/telegram/messenger/exoplayer2/source/hls/HlsInitializationChunk:dataSource	Lorg/telegram/messenger/exoplayer2/upstream/DataSource;
    //   89: invokeinterface 88 1 0
    //   94: return
    //   95: astore_3
    //   96: aload_0
    //   97: aload_2
    //   98: invokeinterface 85 1 0
    //   103: aload_0
    //   104: getfield 49	org/telegram/messenger/exoplayer2/source/hls/HlsInitializationChunk:dataSpec	Lorg/telegram/messenger/exoplayer2/upstream/DataSpec;
    //   107: getfield 67	org/telegram/messenger/exoplayer2/upstream/DataSpec:absoluteStreamPosition	J
    //   110: lsub
    //   111: l2i
    //   112: putfield 27	org/telegram/messenger/exoplayer2/source/hls/HlsInitializationChunk:bytesLoaded	I
    //   115: aload_3
    //   116: athrow
    //   117: astore_2
    //   118: aload_0
    //   119: getfield 61	org/telegram/messenger/exoplayer2/source/hls/HlsInitializationChunk:dataSource	Lorg/telegram/messenger/exoplayer2/upstream/DataSource;
    //   122: invokeinterface 88 1 0
    //   127: aload_2
    //   128: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	129	0	this	HlsInitializationChunk
    //   39	24	1	i	int
    //   11	87	2	localObject1	Object
    //   117	11	2	localObject2	Object
    //   95	21	3	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   44	63	95	finally
    //   12	38	117	finally
    //   66	85	117	finally
    //   96	117	117	finally
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/hls/HlsInitializationChunk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */