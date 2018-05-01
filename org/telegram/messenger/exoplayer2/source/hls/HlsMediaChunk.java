package org.telegram.messenger.exoplayer2.source.hls;

import android.util.Pair;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.metadata.id3.Id3Decoder;
import org.telegram.messenger.exoplayer2.metadata.id3.PrivFrame;
import org.telegram.messenger.exoplayer2.source.chunk.MediaChunk;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.TimestampAdjuster;

final class HlsMediaChunk
  extends MediaChunk
{
  private static final String PRIV_TIMESTAMP_FRAME_OWNER = "com.apple.streaming.transportStreamTimestamp";
  private static final AtomicInteger uidSource = new AtomicInteger();
  private int bytesLoaded;
  public final int discontinuitySequenceNumber;
  private final Extractor extractor;
  public final HlsMasterPlaylist.HlsUrl hlsUrl;
  private final ParsableByteArray id3Data;
  private final Id3Decoder id3Decoder;
  private boolean id3TimestampPeeked;
  private final DataSource initDataSource;
  private final DataSpec initDataSpec;
  private boolean initLoadCompleted;
  private int initSegmentBytesLoaded;
  private final boolean isEncrypted;
  private final boolean isMasterTimestampSource;
  private final boolean isPackedAudioExtractor;
  private volatile boolean loadCanceled;
  private volatile boolean loadCompleted;
  private HlsSampleStreamWrapper output;
  private final boolean reusingExtractor;
  private final boolean shouldSpliceIn;
  private final TimestampAdjuster timestampAdjuster;
  public final int uid;
  
  public HlsMediaChunk(HlsExtractorFactory paramHlsExtractorFactory, DataSource paramDataSource, DataSpec paramDataSpec1, DataSpec paramDataSpec2, HlsMasterPlaylist.HlsUrl paramHlsUrl, List<Format> paramList, int paramInt1, Object paramObject, long paramLong1, long paramLong2, int paramInt2, int paramInt3, boolean paramBoolean, TimestampAdjuster paramTimestampAdjuster, HlsMediaChunk paramHlsMediaChunk, DrmInitData paramDrmInitData, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    super(buildDataSource(paramDataSource, paramArrayOfByte1, paramArrayOfByte2), paramDataSpec1, paramHlsUrl.format, paramInt1, paramObject, paramLong1, paramLong2, paramInt2);
    this.discontinuitySequenceNumber = paramInt3;
    this.initDataSpec = paramDataSpec2;
    this.hlsUrl = paramHlsUrl;
    this.isMasterTimestampSource = paramBoolean;
    this.timestampAdjuster = paramTimestampAdjuster;
    this.isEncrypted = (this.dataSource instanceof Aes128DataSource);
    paramObject = null;
    if (paramHlsMediaChunk != null) {
      if (paramHlsMediaChunk.hlsUrl != paramHlsUrl)
      {
        paramBoolean = true;
        this.shouldSpliceIn = paramBoolean;
        if ((paramHlsMediaChunk.discontinuitySequenceNumber == paramInt3) && (!this.shouldSpliceIn)) {
          break label263;
        }
        paramHlsUrl = null;
        label116:
        paramHlsExtractorFactory = paramHlsExtractorFactory.createExtractor(paramHlsUrl, paramDataSpec1.uri, this.trackFormat, paramList, paramDrmInitData, paramTimestampAdjuster);
        this.extractor = ((Extractor)paramHlsExtractorFactory.first);
        this.isPackedAudioExtractor = ((Boolean)paramHlsExtractorFactory.second).booleanValue();
        if (this.extractor != paramHlsUrl) {
          break label285;
        }
        paramBoolean = true;
        label176:
        this.reusingExtractor = paramBoolean;
        if ((!this.reusingExtractor) || (paramDataSpec2 == null)) {
          break label291;
        }
        paramBoolean = true;
        label197:
        this.initLoadCompleted = paramBoolean;
        if (!this.isPackedAudioExtractor) {
          break label324;
        }
        if ((paramHlsMediaChunk == null) || (paramHlsMediaChunk.id3Data == null)) {
          break label297;
        }
        this.id3Decoder = paramHlsMediaChunk.id3Decoder;
        this.id3Data = paramHlsMediaChunk.id3Data;
      }
    }
    for (;;)
    {
      this.initDataSource = paramDataSource;
      this.uid = uidSource.getAndIncrement();
      return;
      paramBoolean = false;
      break;
      label263:
      paramHlsUrl = paramHlsMediaChunk.extractor;
      break label116;
      this.shouldSpliceIn = false;
      paramHlsUrl = (HlsMasterPlaylist.HlsUrl)paramObject;
      break label116;
      label285:
      paramBoolean = false;
      break label176;
      label291:
      paramBoolean = false;
      break label197;
      label297:
      this.id3Decoder = new Id3Decoder();
      this.id3Data = new ParsableByteArray(10);
      continue;
      label324:
      this.id3Decoder = null;
      this.id3Data = null;
    }
  }
  
  private static DataSource buildDataSource(DataSource paramDataSource, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    Object localObject = paramDataSource;
    if (paramArrayOfByte1 != null) {
      localObject = new Aes128DataSource(paramDataSource, paramArrayOfByte1, paramArrayOfByte2);
    }
    return (DataSource)localObject;
  }
  
  /* Error */
  private void loadMedia()
    throws IOException, InterruptedException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 82	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:isEncrypted	Z
    //   4: ifeq +161 -> 165
    //   7: aload_0
    //   8: getfield 157	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:dataSpec	Lorg/telegram/messenger/exoplayer2/upstream/DataSpec;
    //   11: astore_1
    //   12: aload_0
    //   13: getfield 159	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:bytesLoaded	I
    //   16: ifeq +144 -> 160
    //   19: iconst_1
    //   20: istore_2
    //   21: aload_0
    //   22: getfield 73	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:isMasterTimestampSource	Z
    //   25: ifne +158 -> 183
    //   28: aload_0
    //   29: getfield 75	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:timestampAdjuster	Lorg/telegram/messenger/exoplayer2/util/TimestampAdjuster;
    //   32: invokevirtual 164	org/telegram/messenger/exoplayer2/util/TimestampAdjuster:waitUntilInitialized	()V
    //   35: new 166	org/telegram/messenger/exoplayer2/extractor/DefaultExtractorInput
    //   38: astore_3
    //   39: aload_3
    //   40: aload_0
    //   41: getfield 78	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:dataSource	Lorg/telegram/messenger/exoplayer2/upstream/DataSource;
    //   44: aload_1
    //   45: getfield 170	org/telegram/messenger/exoplayer2/upstream/DataSpec:absoluteStreamPosition	J
    //   48: aload_0
    //   49: getfield 78	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:dataSource	Lorg/telegram/messenger/exoplayer2/upstream/DataSource;
    //   52: aload_1
    //   53: invokeinterface 176 2 0
    //   58: invokespecial 179	org/telegram/messenger/exoplayer2/extractor/DefaultExtractorInput:<init>	(Lorg/telegram/messenger/exoplayer2/upstream/DataSource;JJ)V
    //   61: aload_0
    //   62: getfield 120	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:isPackedAudioExtractor	Z
    //   65: ifeq +53 -> 118
    //   68: aload_0
    //   69: getfield 181	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:id3TimestampPeeked	Z
    //   72: ifne +46 -> 118
    //   75: aload_0
    //   76: aload_3
    //   77: invokespecial 185	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:peekId3PrivTimestamp	(Lorg/telegram/messenger/exoplayer2/extractor/ExtractorInput;)J
    //   80: lstore 4
    //   82: aload_0
    //   83: iconst_1
    //   84: putfield 181	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:id3TimestampPeeked	Z
    //   87: aload_0
    //   88: getfield 187	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:output	Lorg/telegram/messenger/exoplayer2/source/hls/HlsSampleStreamWrapper;
    //   91: astore_1
    //   92: lload 4
    //   94: ldc2_w 188
    //   97: lcmp
    //   98: ifeq +113 -> 211
    //   101: aload_0
    //   102: getfield 75	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:timestampAdjuster	Lorg/telegram/messenger/exoplayer2/util/TimestampAdjuster;
    //   105: lload 4
    //   107: invokevirtual 193	org/telegram/messenger/exoplayer2/util/TimestampAdjuster:adjustTsTimestamp	(J)J
    //   110: lstore 4
    //   112: aload_1
    //   113: lload 4
    //   115: invokevirtual 199	org/telegram/messenger/exoplayer2/source/hls/HlsSampleStreamWrapper:setSampleOffsetUs	(J)V
    //   118: iload_2
    //   119: ifeq +13 -> 132
    //   122: aload_3
    //   123: aload_0
    //   124: getfield 159	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:bytesLoaded	I
    //   127: invokeinterface 204 2 0
    //   132: iconst_0
    //   133: istore_2
    //   134: iload_2
    //   135: ifne +85 -> 220
    //   138: aload_0
    //   139: getfield 206	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:loadCanceled	Z
    //   142: ifne +78 -> 220
    //   145: aload_0
    //   146: getfield 109	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:extractor	Lorg/telegram/messenger/exoplayer2/extractor/Extractor;
    //   149: aload_3
    //   150: aconst_null
    //   151: invokeinterface 210 3 0
    //   156: istore_2
    //   157: goto -23 -> 134
    //   160: iconst_0
    //   161: istore_2
    //   162: goto -141 -> 21
    //   165: aload_0
    //   166: getfield 157	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:dataSpec	Lorg/telegram/messenger/exoplayer2/upstream/DataSpec;
    //   169: aload_0
    //   170: getfield 159	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:bytesLoaded	I
    //   173: i2l
    //   174: invokevirtual 214	org/telegram/messenger/exoplayer2/upstream/DataSpec:subrange	(J)Lorg/telegram/messenger/exoplayer2/upstream/DataSpec;
    //   177: astore_1
    //   178: iconst_0
    //   179: istore_2
    //   180: goto -159 -> 21
    //   183: aload_0
    //   184: getfield 75	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:timestampAdjuster	Lorg/telegram/messenger/exoplayer2/util/TimestampAdjuster;
    //   187: invokevirtual 218	org/telegram/messenger/exoplayer2/util/TimestampAdjuster:getFirstSampleTimestampUs	()J
    //   190: ldc2_w 219
    //   193: lcmp
    //   194: ifne -159 -> 35
    //   197: aload_0
    //   198: getfield 75	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:timestampAdjuster	Lorg/telegram/messenger/exoplayer2/util/TimestampAdjuster;
    //   201: aload_0
    //   202: getfield 223	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:startTimeUs	J
    //   205: invokevirtual 226	org/telegram/messenger/exoplayer2/util/TimestampAdjuster:setFirstSampleTimestampUs	(J)V
    //   208: goto -173 -> 35
    //   211: aload_0
    //   212: getfield 223	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:startTimeUs	J
    //   215: lstore 4
    //   217: goto -105 -> 112
    //   220: aload_0
    //   221: aload_3
    //   222: invokeinterface 229 1 0
    //   227: aload_0
    //   228: getfield 157	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:dataSpec	Lorg/telegram/messenger/exoplayer2/upstream/DataSpec;
    //   231: getfield 170	org/telegram/messenger/exoplayer2/upstream/DataSpec:absoluteStreamPosition	J
    //   234: lsub
    //   235: l2i
    //   236: putfield 159	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:bytesLoaded	I
    //   239: aload_0
    //   240: getfield 78	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:dataSource	Lorg/telegram/messenger/exoplayer2/upstream/DataSource;
    //   243: invokestatic 235	org/telegram/messenger/exoplayer2/util/Util:closeQuietly	(Lorg/telegram/messenger/exoplayer2/upstream/DataSource;)V
    //   246: aload_0
    //   247: iconst_1
    //   248: putfield 237	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:loadCompleted	Z
    //   251: return
    //   252: astore_1
    //   253: aload_0
    //   254: aload_3
    //   255: invokeinterface 229 1 0
    //   260: aload_0
    //   261: getfield 157	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:dataSpec	Lorg/telegram/messenger/exoplayer2/upstream/DataSpec;
    //   264: getfield 170	org/telegram/messenger/exoplayer2/upstream/DataSpec:absoluteStreamPosition	J
    //   267: lsub
    //   268: l2i
    //   269: putfield 159	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:bytesLoaded	I
    //   272: aload_1
    //   273: athrow
    //   274: astore_1
    //   275: aload_0
    //   276: getfield 78	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:dataSource	Lorg/telegram/messenger/exoplayer2/upstream/DataSource;
    //   279: invokestatic 235	org/telegram/messenger/exoplayer2/util/Util:closeQuietly	(Lorg/telegram/messenger/exoplayer2/upstream/DataSource;)V
    //   282: aload_1
    //   283: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	284	0	this	HlsMediaChunk
    //   11	167	1	localObject1	Object
    //   252	21	1	localObject2	Object
    //   274	9	1	localObject3	Object
    //   20	160	2	i	int
    //   38	217	3	localDefaultExtractorInput	org.telegram.messenger.exoplayer2.extractor.DefaultExtractorInput
    //   80	136	4	l	long
    // Exception table:
    //   from	to	target	type
    //   138	157	252	finally
    //   35	92	274	finally
    //   101	112	274	finally
    //   112	118	274	finally
    //   122	132	274	finally
    //   211	217	274	finally
    //   220	239	274	finally
    //   253	274	274	finally
  }
  
  /* Error */
  private void maybeLoadInitData()
    throws IOException, InterruptedException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 124	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:initLoadCompleted	Z
    //   4: ifne +10 -> 14
    //   7: aload_0
    //   8: getfield 69	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:initDataSpec	Lorg/telegram/messenger/exoplayer2/upstream/DataSpec;
    //   11: ifnonnull +4 -> 15
    //   14: return
    //   15: aload_0
    //   16: getfield 69	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:initDataSpec	Lorg/telegram/messenger/exoplayer2/upstream/DataSpec;
    //   19: aload_0
    //   20: getfield 241	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:initSegmentBytesLoaded	I
    //   23: i2l
    //   24: invokevirtual 214	org/telegram/messenger/exoplayer2/upstream/DataSpec:subrange	(J)Lorg/telegram/messenger/exoplayer2/upstream/DataSpec;
    //   27: astore_1
    //   28: new 166	org/telegram/messenger/exoplayer2/extractor/DefaultExtractorInput
    //   31: astore_2
    //   32: aload_2
    //   33: aload_0
    //   34: getfield 130	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:initDataSource	Lorg/telegram/messenger/exoplayer2/upstream/DataSource;
    //   37: aload_1
    //   38: getfield 170	org/telegram/messenger/exoplayer2/upstream/DataSpec:absoluteStreamPosition	J
    //   41: aload_0
    //   42: getfield 130	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:initDataSource	Lorg/telegram/messenger/exoplayer2/upstream/DataSource;
    //   45: aload_1
    //   46: invokeinterface 176 2 0
    //   51: invokespecial 179	org/telegram/messenger/exoplayer2/extractor/DefaultExtractorInput:<init>	(Lorg/telegram/messenger/exoplayer2/upstream/DataSource;JJ)V
    //   54: iconst_0
    //   55: istore_3
    //   56: iload_3
    //   57: ifne +25 -> 82
    //   60: aload_0
    //   61: getfield 206	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:loadCanceled	Z
    //   64: ifne +18 -> 82
    //   67: aload_0
    //   68: getfield 109	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:extractor	Lorg/telegram/messenger/exoplayer2/extractor/Extractor;
    //   71: aload_2
    //   72: aconst_null
    //   73: invokeinterface 210 3 0
    //   78: istore_3
    //   79: goto -23 -> 56
    //   82: aload_0
    //   83: aload_2
    //   84: invokeinterface 229 1 0
    //   89: aload_0
    //   90: getfield 69	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:initDataSpec	Lorg/telegram/messenger/exoplayer2/upstream/DataSpec;
    //   93: getfield 170	org/telegram/messenger/exoplayer2/upstream/DataSpec:absoluteStreamPosition	J
    //   96: lsub
    //   97: l2i
    //   98: putfield 241	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:initSegmentBytesLoaded	I
    //   101: aload_0
    //   102: getfield 78	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:dataSource	Lorg/telegram/messenger/exoplayer2/upstream/DataSource;
    //   105: invokestatic 235	org/telegram/messenger/exoplayer2/util/Util:closeQuietly	(Lorg/telegram/messenger/exoplayer2/upstream/DataSource;)V
    //   108: aload_0
    //   109: iconst_1
    //   110: putfield 124	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:initLoadCompleted	Z
    //   113: goto -99 -> 14
    //   116: astore_1
    //   117: aload_0
    //   118: aload_2
    //   119: invokeinterface 229 1 0
    //   124: aload_0
    //   125: getfield 69	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:initDataSpec	Lorg/telegram/messenger/exoplayer2/upstream/DataSpec;
    //   128: getfield 170	org/telegram/messenger/exoplayer2/upstream/DataSpec:absoluteStreamPosition	J
    //   131: lsub
    //   132: l2i
    //   133: putfield 241	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:initSegmentBytesLoaded	I
    //   136: aload_1
    //   137: athrow
    //   138: astore_2
    //   139: aload_0
    //   140: getfield 78	org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk:dataSource	Lorg/telegram/messenger/exoplayer2/upstream/DataSource;
    //   143: invokestatic 235	org/telegram/messenger/exoplayer2/util/Util:closeQuietly	(Lorg/telegram/messenger/exoplayer2/upstream/DataSource;)V
    //   146: aload_2
    //   147: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	148	0	this	HlsMediaChunk
    //   27	19	1	localDataSpec	DataSpec
    //   116	21	1	localObject1	Object
    //   31	88	2	localDefaultExtractorInput	org.telegram.messenger.exoplayer2.extractor.DefaultExtractorInput
    //   138	9	2	localObject2	Object
    //   55	24	3	i	int
    // Exception table:
    //   from	to	target	type
    //   60	79	116	finally
    //   28	54	138	finally
    //   82	101	138	finally
    //   117	138	138	finally
  }
  
  private long peekId3PrivTimestamp(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    paramExtractorInput.resetPeekPosition();
    long l;
    if (!paramExtractorInput.peekFully(this.id3Data.data, 0, 10, true)) {
      l = -9223372036854775807L;
    }
    for (;;)
    {
      return l;
      this.id3Data.reset(10);
      if (this.id3Data.readUnsignedInt24() != Id3Decoder.ID3_TAG)
      {
        l = -9223372036854775807L;
      }
      else
      {
        this.id3Data.skipBytes(3);
        int i = this.id3Data.readSynchSafeInt();
        int j = i + 10;
        Object localObject;
        if (j > this.id3Data.capacity())
        {
          localObject = this.id3Data.data;
          this.id3Data.reset(j);
          System.arraycopy(localObject, 0, this.id3Data.data, 0, 10);
        }
        if (!paramExtractorInput.peekFully(this.id3Data.data, 10, i, true))
        {
          l = -9223372036854775807L;
        }
        else
        {
          paramExtractorInput = this.id3Decoder.decode(this.id3Data.data, i);
          if (paramExtractorInput == null)
          {
            l = -9223372036854775807L;
          }
          else
          {
            j = paramExtractorInput.length();
            for (i = 0;; i++)
            {
              if (i >= j) {
                break label288;
              }
              localObject = paramExtractorInput.get(i);
              if ((localObject instanceof PrivFrame))
              {
                localObject = (PrivFrame)localObject;
                if ("com.apple.streaming.transportStreamTimestamp".equals(((PrivFrame)localObject).owner))
                {
                  System.arraycopy(((PrivFrame)localObject).privateData, 0, this.id3Data.data, 0, 8);
                  this.id3Data.reset(8);
                  l = this.id3Data.readLong() & 0x1FFFFFFFF;
                  break;
                }
              }
            }
            label288:
            l = -9223372036854775807L;
          }
        }
      }
    }
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
    this.output = paramHlsSampleStreamWrapper;
    paramHlsSampleStreamWrapper.init(this.uid, this.shouldSpliceIn, this.reusingExtractor);
    if (!this.reusingExtractor) {
      this.extractor.init(paramHlsSampleStreamWrapper);
    }
  }
  
  public boolean isLoadCanceled()
  {
    return this.loadCanceled;
  }
  
  public boolean isLoadCompleted()
  {
    return this.loadCompleted;
  }
  
  public void load()
    throws IOException, InterruptedException
  {
    maybeLoadInitData();
    if (!this.loadCanceled) {
      loadMedia();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/hls/HlsMediaChunk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */