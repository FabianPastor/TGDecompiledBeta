package org.telegram.messenger.exoplayer2.source.chunk;

import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;

public final class InitializationChunk
  extends Chunk
{
  private volatile int bytesLoaded;
  private final ChunkExtractorWrapper extractorWrapper;
  private volatile boolean loadCanceled;
  
  public InitializationChunk(DataSource paramDataSource, DataSpec paramDataSpec, Format paramFormat, int paramInt, Object paramObject, ChunkExtractorWrapper paramChunkExtractorWrapper)
  {
    super(paramDataSource, paramDataSpec, 2, paramFormat, paramInt, paramObject, -9223372036854775807L, -9223372036854775807L);
    this.extractorWrapper = paramChunkExtractorWrapper;
  }
  
  public long bytesLoaded()
  {
    return this.bytesLoaded;
  }
  
  public void cancelLoad()
  {
    this.loadCanceled = true;
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
    //   1: getfield 38	org/telegram/messenger/exoplayer2/source/chunk/InitializationChunk:dataSpec	Lorg/telegram/messenger/exoplayer2/upstream/DataSpec;
    //   4: aload_0
    //   5: getfield 23	org/telegram/messenger/exoplayer2/source/chunk/InitializationChunk:bytesLoaded	I
    //   8: i2l
    //   9: invokevirtual 44	org/telegram/messenger/exoplayer2/upstream/DataSpec:subrange	(J)Lorg/telegram/messenger/exoplayer2/upstream/DataSpec;
    //   12: astore_1
    //   13: new 46	org/telegram/messenger/exoplayer2/extractor/DefaultExtractorInput
    //   16: astore_2
    //   17: aload_2
    //   18: aload_0
    //   19: getfield 50	org/telegram/messenger/exoplayer2/source/chunk/InitializationChunk:dataSource	Lorg/telegram/messenger/exoplayer2/upstream/DataSource;
    //   22: aload_1
    //   23: getfield 54	org/telegram/messenger/exoplayer2/upstream/DataSpec:absoluteStreamPosition	J
    //   26: aload_0
    //   27: getfield 50	org/telegram/messenger/exoplayer2/source/chunk/InitializationChunk:dataSource	Lorg/telegram/messenger/exoplayer2/upstream/DataSource;
    //   30: aload_1
    //   31: invokeinterface 60 2 0
    //   36: invokespecial 63	org/telegram/messenger/exoplayer2/extractor/DefaultExtractorInput:<init>	(Lorg/telegram/messenger/exoplayer2/upstream/DataSource;JJ)V
    //   39: aload_0
    //   40: getfield 23	org/telegram/messenger/exoplayer2/source/chunk/InitializationChunk:bytesLoaded	I
    //   43: ifne +11 -> 54
    //   46: aload_0
    //   47: getfield 19	org/telegram/messenger/exoplayer2/source/chunk/InitializationChunk:extractorWrapper	Lorg/telegram/messenger/exoplayer2/source/chunk/ChunkExtractorWrapper;
    //   50: aconst_null
    //   51: invokevirtual 69	org/telegram/messenger/exoplayer2/source/chunk/ChunkExtractorWrapper:init	(Lorg/telegram/messenger/exoplayer2/source/chunk/ChunkExtractorWrapper$TrackOutputProvider;)V
    //   54: aload_0
    //   55: getfield 19	org/telegram/messenger/exoplayer2/source/chunk/InitializationChunk:extractorWrapper	Lorg/telegram/messenger/exoplayer2/source/chunk/ChunkExtractorWrapper;
    //   58: getfield 73	org/telegram/messenger/exoplayer2/source/chunk/ChunkExtractorWrapper:extractor	Lorg/telegram/messenger/exoplayer2/extractor/Extractor;
    //   61: astore_1
    //   62: iconst_0
    //   63: istore_3
    //   64: iload_3
    //   65: ifne +22 -> 87
    //   68: aload_0
    //   69: getfield 27	org/telegram/messenger/exoplayer2/source/chunk/InitializationChunk:loadCanceled	Z
    //   72: ifne +15 -> 87
    //   75: aload_1
    //   76: aload_2
    //   77: aconst_null
    //   78: invokeinterface 79 3 0
    //   83: istore_3
    //   84: goto -20 -> 64
    //   87: iload_3
    //   88: iconst_1
    //   89: if_icmpeq +38 -> 127
    //   92: iconst_1
    //   93: istore 4
    //   95: iload 4
    //   97: invokestatic 85	org/telegram/messenger/exoplayer2/util/Assertions:checkState	(Z)V
    //   100: aload_0
    //   101: aload_2
    //   102: invokeinterface 90 1 0
    //   107: aload_0
    //   108: getfield 38	org/telegram/messenger/exoplayer2/source/chunk/InitializationChunk:dataSpec	Lorg/telegram/messenger/exoplayer2/upstream/DataSpec;
    //   111: getfield 54	org/telegram/messenger/exoplayer2/upstream/DataSpec:absoluteStreamPosition	J
    //   114: lsub
    //   115: l2i
    //   116: putfield 23	org/telegram/messenger/exoplayer2/source/chunk/InitializationChunk:bytesLoaded	I
    //   119: aload_0
    //   120: getfield 50	org/telegram/messenger/exoplayer2/source/chunk/InitializationChunk:dataSource	Lorg/telegram/messenger/exoplayer2/upstream/DataSource;
    //   123: invokestatic 96	org/telegram/messenger/exoplayer2/util/Util:closeQuietly	(Lorg/telegram/messenger/exoplayer2/upstream/DataSource;)V
    //   126: return
    //   127: iconst_0
    //   128: istore 4
    //   130: goto -35 -> 95
    //   133: astore_1
    //   134: aload_0
    //   135: aload_2
    //   136: invokeinterface 90 1 0
    //   141: aload_0
    //   142: getfield 38	org/telegram/messenger/exoplayer2/source/chunk/InitializationChunk:dataSpec	Lorg/telegram/messenger/exoplayer2/upstream/DataSpec;
    //   145: getfield 54	org/telegram/messenger/exoplayer2/upstream/DataSpec:absoluteStreamPosition	J
    //   148: lsub
    //   149: l2i
    //   150: putfield 23	org/telegram/messenger/exoplayer2/source/chunk/InitializationChunk:bytesLoaded	I
    //   153: aload_1
    //   154: athrow
    //   155: astore_2
    //   156: aload_0
    //   157: getfield 50	org/telegram/messenger/exoplayer2/source/chunk/InitializationChunk:dataSource	Lorg/telegram/messenger/exoplayer2/upstream/DataSource;
    //   160: invokestatic 96	org/telegram/messenger/exoplayer2/util/Util:closeQuietly	(Lorg/telegram/messenger/exoplayer2/upstream/DataSource;)V
    //   163: aload_2
    //   164: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	165	0	this	InitializationChunk
    //   12	64	1	localObject1	Object
    //   133	21	1	localObject2	Object
    //   16	120	2	localDefaultExtractorInput	org.telegram.messenger.exoplayer2.extractor.DefaultExtractorInput
    //   155	9	2	localObject3	Object
    //   63	27	3	i	int
    //   93	36	4	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   54	62	133	finally
    //   68	84	133	finally
    //   95	100	133	finally
    //   13	54	155	finally
    //   100	119	155	finally
    //   134	155	155	finally
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/chunk/InitializationChunk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */