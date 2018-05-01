package org.telegram.messenger.exoplayer2.source.chunk;

import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;

public class ContainerMediaChunk
  extends BaseMediaChunk
{
  private volatile int bytesLoaded;
  private final int chunkCount;
  private final ChunkExtractorWrapper extractorWrapper;
  private volatile boolean loadCanceled;
  private volatile boolean loadCompleted;
  private final long sampleOffsetUs;
  
  public ContainerMediaChunk(DataSource paramDataSource, DataSpec paramDataSpec, Format paramFormat, int paramInt1, Object paramObject, long paramLong1, long paramLong2, int paramInt2, int paramInt3, long paramLong3, ChunkExtractorWrapper paramChunkExtractorWrapper)
  {
    super(paramDataSource, paramDataSpec, paramFormat, paramInt1, paramObject, paramLong1, paramLong2, paramInt2);
    this.chunkCount = paramInt3;
    this.sampleOffsetUs = paramLong3;
    this.extractorWrapper = paramChunkExtractorWrapper;
  }
  
  public final long bytesLoaded()
  {
    return this.bytesLoaded;
  }
  
  public final void cancelLoad()
  {
    this.loadCanceled = true;
  }
  
  public int getNextChunkIndex()
  {
    return this.chunkIndex + this.chunkCount;
  }
  
  public final boolean isLoadCanceled()
  {
    return this.loadCanceled;
  }
  
  public boolean isLoadCompleted()
  {
    return this.loadCompleted;
  }
  
  /* Error */
  public final void load()
    throws java.io.IOException, java.lang.InterruptedException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 52	org/telegram/messenger/exoplayer2/source/chunk/ContainerMediaChunk:dataSpec	Lorg/telegram/messenger/exoplayer2/upstream/DataSpec;
    //   4: aload_0
    //   5: getfield 29	org/telegram/messenger/exoplayer2/source/chunk/ContainerMediaChunk:bytesLoaded	I
    //   8: i2l
    //   9: invokevirtual 58	org/telegram/messenger/exoplayer2/upstream/DataSpec:subrange	(J)Lorg/telegram/messenger/exoplayer2/upstream/DataSpec;
    //   12: astore_1
    //   13: new 60	org/telegram/messenger/exoplayer2/extractor/DefaultExtractorInput
    //   16: astore_2
    //   17: aload_2
    //   18: aload_0
    //   19: getfield 64	org/telegram/messenger/exoplayer2/source/chunk/ContainerMediaChunk:dataSource	Lorg/telegram/messenger/exoplayer2/upstream/DataSource;
    //   22: aload_1
    //   23: getfield 67	org/telegram/messenger/exoplayer2/upstream/DataSpec:absoluteStreamPosition	J
    //   26: aload_0
    //   27: getfield 64	org/telegram/messenger/exoplayer2/source/chunk/ContainerMediaChunk:dataSource	Lorg/telegram/messenger/exoplayer2/upstream/DataSource;
    //   30: aload_1
    //   31: invokeinterface 73 2 0
    //   36: invokespecial 76	org/telegram/messenger/exoplayer2/extractor/DefaultExtractorInput:<init>	(Lorg/telegram/messenger/exoplayer2/upstream/DataSource;JJ)V
    //   39: aload_0
    //   40: getfield 29	org/telegram/messenger/exoplayer2/source/chunk/ContainerMediaChunk:bytesLoaded	I
    //   43: ifne +24 -> 67
    //   46: aload_0
    //   47: invokevirtual 80	org/telegram/messenger/exoplayer2/source/chunk/ContainerMediaChunk:getOutput	()Lorg/telegram/messenger/exoplayer2/source/chunk/BaseMediaChunkOutput;
    //   50: astore_1
    //   51: aload_1
    //   52: aload_0
    //   53: getfield 23	org/telegram/messenger/exoplayer2/source/chunk/ContainerMediaChunk:sampleOffsetUs	J
    //   56: invokevirtual 86	org/telegram/messenger/exoplayer2/source/chunk/BaseMediaChunkOutput:setSampleOffsetUs	(J)V
    //   59: aload_0
    //   60: getfield 25	org/telegram/messenger/exoplayer2/source/chunk/ContainerMediaChunk:extractorWrapper	Lorg/telegram/messenger/exoplayer2/source/chunk/ChunkExtractorWrapper;
    //   63: aload_1
    //   64: invokevirtual 92	org/telegram/messenger/exoplayer2/source/chunk/ChunkExtractorWrapper:init	(Lorg/telegram/messenger/exoplayer2/source/chunk/ChunkExtractorWrapper$TrackOutputProvider;)V
    //   67: aload_0
    //   68: getfield 25	org/telegram/messenger/exoplayer2/source/chunk/ContainerMediaChunk:extractorWrapper	Lorg/telegram/messenger/exoplayer2/source/chunk/ChunkExtractorWrapper;
    //   71: getfield 96	org/telegram/messenger/exoplayer2/source/chunk/ChunkExtractorWrapper:extractor	Lorg/telegram/messenger/exoplayer2/extractor/Extractor;
    //   74: astore_1
    //   75: iconst_0
    //   76: istore_3
    //   77: iload_3
    //   78: ifne +22 -> 100
    //   81: aload_0
    //   82: getfield 33	org/telegram/messenger/exoplayer2/source/chunk/ContainerMediaChunk:loadCanceled	Z
    //   85: ifne +15 -> 100
    //   88: aload_1
    //   89: aload_2
    //   90: aconst_null
    //   91: invokeinterface 102 3 0
    //   96: istore_3
    //   97: goto -20 -> 77
    //   100: iload_3
    //   101: iconst_1
    //   102: if_icmpeq +43 -> 145
    //   105: iconst_1
    //   106: istore 4
    //   108: iload 4
    //   110: invokestatic 108	org/telegram/messenger/exoplayer2/util/Assertions:checkState	(Z)V
    //   113: aload_0
    //   114: aload_2
    //   115: invokeinterface 113 1 0
    //   120: aload_0
    //   121: getfield 52	org/telegram/messenger/exoplayer2/source/chunk/ContainerMediaChunk:dataSpec	Lorg/telegram/messenger/exoplayer2/upstream/DataSpec;
    //   124: getfield 67	org/telegram/messenger/exoplayer2/upstream/DataSpec:absoluteStreamPosition	J
    //   127: lsub
    //   128: l2i
    //   129: putfield 29	org/telegram/messenger/exoplayer2/source/chunk/ContainerMediaChunk:bytesLoaded	I
    //   132: aload_0
    //   133: getfield 64	org/telegram/messenger/exoplayer2/source/chunk/ContainerMediaChunk:dataSource	Lorg/telegram/messenger/exoplayer2/upstream/DataSource;
    //   136: invokestatic 119	org/telegram/messenger/exoplayer2/util/Util:closeQuietly	(Lorg/telegram/messenger/exoplayer2/upstream/DataSource;)V
    //   139: aload_0
    //   140: iconst_1
    //   141: putfield 43	org/telegram/messenger/exoplayer2/source/chunk/ContainerMediaChunk:loadCompleted	Z
    //   144: return
    //   145: iconst_0
    //   146: istore 4
    //   148: goto -40 -> 108
    //   151: astore_1
    //   152: aload_0
    //   153: aload_2
    //   154: invokeinterface 113 1 0
    //   159: aload_0
    //   160: getfield 52	org/telegram/messenger/exoplayer2/source/chunk/ContainerMediaChunk:dataSpec	Lorg/telegram/messenger/exoplayer2/upstream/DataSpec;
    //   163: getfield 67	org/telegram/messenger/exoplayer2/upstream/DataSpec:absoluteStreamPosition	J
    //   166: lsub
    //   167: l2i
    //   168: putfield 29	org/telegram/messenger/exoplayer2/source/chunk/ContainerMediaChunk:bytesLoaded	I
    //   171: aload_1
    //   172: athrow
    //   173: astore_2
    //   174: aload_0
    //   175: getfield 64	org/telegram/messenger/exoplayer2/source/chunk/ContainerMediaChunk:dataSource	Lorg/telegram/messenger/exoplayer2/upstream/DataSource;
    //   178: invokestatic 119	org/telegram/messenger/exoplayer2/util/Util:closeQuietly	(Lorg/telegram/messenger/exoplayer2/upstream/DataSource;)V
    //   181: aload_2
    //   182: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	183	0	this	ContainerMediaChunk
    //   12	77	1	localObject1	Object
    //   151	21	1	localObject2	Object
    //   16	138	2	localDefaultExtractorInput	org.telegram.messenger.exoplayer2.extractor.DefaultExtractorInput
    //   173	9	2	localObject3	Object
    //   76	27	3	i	int
    //   106	41	4	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   67	75	151	finally
    //   81	97	151	finally
    //   108	113	151	finally
    //   13	67	173	finally
    //   113	132	173	finally
    //   152	173	173	finally
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/chunk/ContainerMediaChunk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */