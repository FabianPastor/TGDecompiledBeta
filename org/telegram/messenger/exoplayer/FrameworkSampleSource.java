package org.telegram.messenger.exoplayer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaExtractor;
import android.net.Uri;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.telegram.messenger.exoplayer.drm.DrmInitData;
import org.telegram.messenger.exoplayer.drm.DrmInitData.Mapped;
import org.telegram.messenger.exoplayer.drm.DrmInitData.SchemeInitData;
import org.telegram.messenger.exoplayer.extractor.mp4.PsshAtomUtil;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.Util;

@Deprecated
@TargetApi(16)
public final class FrameworkSampleSource
  implements SampleSource, SampleSource.SampleSourceReader
{
  private static final int ALLOWED_FLAGS_MASK = 3;
  private static final int TRACK_STATE_DISABLED = 0;
  private static final int TRACK_STATE_ENABLED = 1;
  private static final int TRACK_STATE_FORMAT_SENT = 2;
  private final Context context;
  private MediaExtractor extractor;
  private final FileDescriptor fileDescriptor;
  private final long fileDescriptorLength;
  private final long fileDescriptorOffset;
  private final Map<String, String> headers;
  private long lastSeekPositionUs;
  private boolean[] pendingDiscontinuities;
  private long pendingSeekPositionUs;
  private IOException preparationError;
  private boolean prepared;
  private int remainingReleaseCount;
  private MediaFormat[] trackFormats;
  private int[] trackStates;
  private final Uri uri;
  
  public FrameworkSampleSource(Context paramContext, Uri paramUri, Map<String, String> paramMap)
  {
    if (Util.SDK_INT >= 16) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.context = ((Context)Assertions.checkNotNull(paramContext));
      this.uri = ((Uri)Assertions.checkNotNull(paramUri));
      this.headers = paramMap;
      this.fileDescriptor = null;
      this.fileDescriptorOffset = 0L;
      this.fileDescriptorLength = 0L;
      return;
    }
  }
  
  public FrameworkSampleSource(FileDescriptor paramFileDescriptor, long paramLong1, long paramLong2)
  {
    if (Util.SDK_INT >= 16) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.fileDescriptor = ((FileDescriptor)Assertions.checkNotNull(paramFileDescriptor));
      this.fileDescriptorOffset = paramLong1;
      this.fileDescriptorLength = paramLong2;
      this.context = null;
      this.uri = null;
      this.headers = null;
      return;
    }
  }
  
  @SuppressLint({"InlinedApi"})
  private static MediaFormat createMediaFormat(android.media.MediaFormat paramMediaFormat)
  {
    Object localObject = paramMediaFormat.getString("mime");
    String str = getOptionalStringV16(paramMediaFormat, "language");
    int j = getOptionalIntegerV16(paramMediaFormat, "max-input-size");
    int k = getOptionalIntegerV16(paramMediaFormat, "width");
    int m = getOptionalIntegerV16(paramMediaFormat, "height");
    int n = getOptionalIntegerV16(paramMediaFormat, "rotation-degrees");
    int i1 = getOptionalIntegerV16(paramMediaFormat, "channel-count");
    int i2 = getOptionalIntegerV16(paramMediaFormat, "sample-rate");
    int i3 = getOptionalIntegerV16(paramMediaFormat, "encoder-delay");
    int i4 = getOptionalIntegerV16(paramMediaFormat, "encoder-padding");
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (paramMediaFormat.containsKey("csd-" + i))
    {
      ByteBuffer localByteBuffer = paramMediaFormat.getByteBuffer("csd-" + i);
      byte[] arrayOfByte = new byte[localByteBuffer.limit()];
      localByteBuffer.get(arrayOfByte);
      localArrayList.add(arrayOfByte);
      localByteBuffer.flip();
      i += 1;
    }
    long l;
    if (paramMediaFormat.containsKey("durationUs"))
    {
      l = paramMediaFormat.getLong("durationUs");
      if (!"audio/raw".equals(localObject)) {
        break label266;
      }
    }
    label266:
    for (i = 2;; i = -1)
    {
      localObject = new MediaFormat(null, (String)localObject, -1, j, l, k, m, n, -1.0F, i1, i2, str, Long.MAX_VALUE, localArrayList, false, -1, -1, i, i3, i4);
      ((MediaFormat)localObject).setFrameworkFormatV16(paramMediaFormat);
      return (MediaFormat)localObject;
      l = -1L;
      break;
    }
  }
  
  @TargetApi(18)
  private DrmInitData getDrmInitDataV18()
  {
    Map localMap = this.extractor.getPsshInfo();
    Object localObject;
    if ((localMap == null) || (localMap.isEmpty()))
    {
      localObject = null;
      return (DrmInitData)localObject;
    }
    DrmInitData.Mapped localMapped = new DrmInitData.Mapped();
    Iterator localIterator = localMap.keySet().iterator();
    for (;;)
    {
      localObject = localMapped;
      if (!localIterator.hasNext()) {
        break;
      }
      localObject = (UUID)localIterator.next();
      localMapped.put((UUID)localObject, new DrmInitData.SchemeInitData("video/mp4", PsshAtomUtil.buildPsshAtom((UUID)localObject, (byte[])localMap.get(localObject))));
    }
  }
  
  @TargetApi(16)
  private static final int getOptionalIntegerV16(android.media.MediaFormat paramMediaFormat, String paramString)
  {
    if (paramMediaFormat.containsKey(paramString)) {
      return paramMediaFormat.getInteger(paramString);
    }
    return -1;
  }
  
  @TargetApi(16)
  private static final String getOptionalStringV16(android.media.MediaFormat paramMediaFormat, String paramString)
  {
    if (paramMediaFormat.containsKey(paramString)) {
      return paramMediaFormat.getString(paramString);
    }
    return null;
  }
  
  private void seekToUsInternal(long paramLong, boolean paramBoolean)
  {
    if ((paramBoolean) || (this.pendingSeekPositionUs != paramLong))
    {
      this.lastSeekPositionUs = paramLong;
      this.pendingSeekPositionUs = paramLong;
      this.extractor.seekTo(paramLong, 0);
      int i = 0;
      while (i < this.trackStates.length)
      {
        if (this.trackStates[i] != 0) {
          this.pendingDiscontinuities[i] = true;
        }
        i += 1;
      }
    }
  }
  
  public boolean continueBuffering(int paramInt, long paramLong)
  {
    return true;
  }
  
  public void disable(int paramInt)
  {
    Assertions.checkState(this.prepared);
    if (this.trackStates[paramInt] != 0) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.extractor.unselectTrack(paramInt);
      this.pendingDiscontinuities[paramInt] = false;
      this.trackStates[paramInt] = 0;
      return;
    }
  }
  
  public void enable(int paramInt, long paramLong)
  {
    boolean bool2 = true;
    Assertions.checkState(this.prepared);
    if (this.trackStates[paramInt] == 0)
    {
      bool1 = true;
      Assertions.checkState(bool1);
      this.trackStates[paramInt] = 1;
      this.extractor.selectTrack(paramInt);
      if (paramLong == 0L) {
        break label66;
      }
    }
    label66:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      seekToUsInternal(paramLong, bool1);
      return;
      bool1 = false;
      break;
    }
  }
  
  public long getBufferedPositionUs()
  {
    Assertions.checkState(this.prepared);
    long l1 = this.extractor.getCachedDuration();
    if (l1 == -1L) {
      return -1L;
    }
    long l2 = this.extractor.getSampleTime();
    if (l2 == -1L) {
      return -3L;
    }
    return l2 + l1;
  }
  
  public MediaFormat getFormat(int paramInt)
  {
    Assertions.checkState(this.prepared);
    return this.trackFormats[paramInt];
  }
  
  public int getTrackCount()
  {
    Assertions.checkState(this.prepared);
    return this.trackStates.length;
  }
  
  public void maybeThrowError()
    throws IOException
  {
    if (this.preparationError != null) {
      throw this.preparationError;
    }
  }
  
  public boolean prepare(long paramLong)
  {
    if (!this.prepared)
    {
      if (this.preparationError != null) {
        return false;
      }
      this.extractor = new MediaExtractor();
      try
      {
        if (this.context != null) {
          this.extractor.setDataSource(this.context, this.uri, this.headers);
        }
        for (;;)
        {
          this.trackStates = new int[this.extractor.getTrackCount()];
          this.pendingDiscontinuities = new boolean[this.trackStates.length];
          this.trackFormats = new MediaFormat[this.trackStates.length];
          int i = 0;
          while (i < this.trackStates.length)
          {
            this.trackFormats[i] = createMediaFormat(this.extractor.getTrackFormat(i));
            i += 1;
          }
          this.extractor.setDataSource(this.fileDescriptor, this.fileDescriptorOffset, this.fileDescriptorLength);
        }
        this.prepared = true;
      }
      catch (IOException localIOException)
      {
        this.preparationError = localIOException;
        return false;
      }
    }
    return true;
  }
  
  public int readData(int paramInt, long paramLong, MediaFormatHolder paramMediaFormatHolder, SampleHolder paramSampleHolder)
  {
    Assertions.checkState(this.prepared);
    if (this.trackStates[paramInt] != 0) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      if (this.pendingDiscontinuities[paramInt] == 0) {
        break;
      }
      return -2;
    }
    if (this.trackStates[paramInt] != 2)
    {
      paramMediaFormatHolder.format = this.trackFormats[paramInt];
      if (Util.SDK_INT >= 18) {}
      for (paramSampleHolder = getDrmInitDataV18();; paramSampleHolder = null)
      {
        paramMediaFormatHolder.drmInitData = paramSampleHolder;
        this.trackStates[paramInt] = 2;
        return -4;
      }
    }
    int i = this.extractor.getSampleTrackIndex();
    if (i == paramInt)
    {
      if (paramSampleHolder.data != null)
      {
        paramInt = paramSampleHolder.data.position();
        paramSampleHolder.size = this.extractor.readSampleData(paramSampleHolder.data, paramInt);
        paramSampleHolder.data.position(paramSampleHolder.size + paramInt);
      }
      for (;;)
      {
        paramSampleHolder.timeUs = this.extractor.getSampleTime();
        paramSampleHolder.flags = (this.extractor.getSampleFlags() & 0x3);
        if (paramSampleHolder.isEncrypted()) {
          paramSampleHolder.cryptoInfo.setFromExtractorV16(this.extractor);
        }
        this.pendingSeekPositionUs = -1L;
        this.extractor.advance();
        return -3;
        paramSampleHolder.size = 0;
      }
    }
    if (i < 0) {}
    for (paramInt = -1;; paramInt = -2) {
      return paramInt;
    }
  }
  
  public long readDiscontinuity(int paramInt)
  {
    if (this.pendingDiscontinuities[paramInt] != 0)
    {
      this.pendingDiscontinuities[paramInt] = false;
      return this.lastSeekPositionUs;
    }
    return Long.MIN_VALUE;
  }
  
  public SampleSource.SampleSourceReader register()
  {
    this.remainingReleaseCount += 1;
    return this;
  }
  
  public void release()
  {
    if (this.remainingReleaseCount > 0) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      int i = this.remainingReleaseCount - 1;
      this.remainingReleaseCount = i;
      if ((i == 0) && (this.extractor != null))
      {
        this.extractor.release();
        this.extractor = null;
      }
      return;
    }
  }
  
  public void seekToUs(long paramLong)
  {
    Assertions.checkState(this.prepared);
    seekToUsInternal(paramLong, false);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/FrameworkSampleSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */