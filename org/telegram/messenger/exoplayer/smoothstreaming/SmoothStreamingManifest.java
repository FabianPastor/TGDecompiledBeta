package org.telegram.messenger.exoplayer.smoothstreaming;

import android.net.Uri;
import java.util.List;
import java.util.UUID;
import org.telegram.messenger.exoplayer.chunk.Format;
import org.telegram.messenger.exoplayer.chunk.FormatWrapper;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.UriUtil;
import org.telegram.messenger.exoplayer.util.Util;

public class SmoothStreamingManifest
{
  public final long durationUs;
  public final long dvrWindowLengthUs;
  public final boolean isLive;
  public final int lookAheadCount;
  public final int majorVersion;
  public final int minorVersion;
  public final ProtectionElement protectionElement;
  public final StreamElement[] streamElements;
  
  public SmoothStreamingManifest(int paramInt1, int paramInt2, long paramLong1, long paramLong2, long paramLong3, int paramInt3, boolean paramBoolean, ProtectionElement paramProtectionElement, StreamElement[] paramArrayOfStreamElement)
  {
    this.majorVersion = paramInt1;
    this.minorVersion = paramInt2;
    this.lookAheadCount = paramInt3;
    this.isLive = paramBoolean;
    this.protectionElement = paramProtectionElement;
    this.streamElements = paramArrayOfStreamElement;
    if (paramLong3 == 0L)
    {
      paramLong3 = -1L;
      this.dvrWindowLengthUs = paramLong3;
      if (paramLong2 != 0L) {
        break label87;
      }
    }
    label87:
    for (paramLong1 = -1L;; paramLong1 = Util.scaleLargeTimestamp(paramLong2, 1000000L, paramLong1))
    {
      this.durationUs = paramLong1;
      return;
      paramLong3 = Util.scaleLargeTimestamp(paramLong3, 1000000L, paramLong1);
      break;
    }
  }
  
  public static class ProtectionElement
  {
    public final byte[] data;
    public final UUID uuid;
    
    public ProtectionElement(UUID paramUUID, byte[] paramArrayOfByte)
    {
      this.uuid = paramUUID;
      this.data = paramArrayOfByte;
    }
  }
  
  public static class StreamElement
  {
    public static final int TYPE_AUDIO = 0;
    public static final int TYPE_TEXT = 2;
    public static final int TYPE_UNKNOWN = -1;
    public static final int TYPE_VIDEO = 1;
    private static final String URL_PLACEHOLDER_BITRATE = "{bitrate}";
    private static final String URL_PLACEHOLDER_START_TIME = "{start time}";
    private final String baseUri;
    public final int chunkCount;
    private final List<Long> chunkStartTimes;
    private final long[] chunkStartTimesUs;
    private final String chunkTemplate;
    public final int displayHeight;
    public final int displayWidth;
    public final String language;
    private final long lastChunkDurationUs;
    public final int maxHeight;
    public final int maxWidth;
    public final String name;
    public final int qualityLevels;
    public final String subType;
    public final long timescale;
    public final SmoothStreamingManifest.TrackElement[] tracks;
    public final int type;
    
    public StreamElement(String paramString1, String paramString2, int paramInt1, String paramString3, long paramLong1, String paramString4, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, String paramString5, SmoothStreamingManifest.TrackElement[] paramArrayOfTrackElement, List<Long> paramList, long paramLong2)
    {
      this.baseUri = paramString1;
      this.chunkTemplate = paramString2;
      this.type = paramInt1;
      this.subType = paramString3;
      this.timescale = paramLong1;
      this.name = paramString4;
      this.qualityLevels = paramInt2;
      this.maxWidth = paramInt3;
      this.maxHeight = paramInt4;
      this.displayWidth = paramInt5;
      this.displayHeight = paramInt6;
      this.language = paramString5;
      this.tracks = paramArrayOfTrackElement;
      this.chunkCount = paramList.size();
      this.chunkStartTimes = paramList;
      this.lastChunkDurationUs = Util.scaleLargeTimestamp(paramLong2, 1000000L, paramLong1);
      this.chunkStartTimesUs = Util.scaleLargeTimestamps(paramList, 1000000L, paramLong1);
    }
    
    public Uri buildRequestUri(int paramInt1, int paramInt2)
    {
      boolean bool2 = true;
      if (this.tracks != null)
      {
        bool1 = true;
        Assertions.checkState(bool1);
        if (this.chunkStartTimes == null) {
          break label111;
        }
        bool1 = true;
        label25:
        Assertions.checkState(bool1);
        if (paramInt2 >= this.chunkStartTimes.size()) {
          break label116;
        }
      }
      label111:
      label116:
      for (boolean bool1 = bool2;; bool1 = false)
      {
        Assertions.checkState(bool1);
        String str = this.chunkTemplate.replace("{bitrate}", Integer.toString(this.tracks[paramInt1].format.bitrate)).replace("{start time}", ((Long)this.chunkStartTimes.get(paramInt2)).toString());
        return UriUtil.resolveToUri(this.baseUri, str);
        bool1 = false;
        break;
        bool1 = false;
        break label25;
      }
    }
    
    public long getChunkDurationUs(int paramInt)
    {
      if (paramInt == this.chunkCount - 1) {
        return this.lastChunkDurationUs;
      }
      return this.chunkStartTimesUs[(paramInt + 1)] - this.chunkStartTimesUs[paramInt];
    }
    
    public int getChunkIndex(long paramLong)
    {
      return Util.binarySearchFloor(this.chunkStartTimesUs, paramLong, true, true);
    }
    
    public long getStartTimeUs(int paramInt)
    {
      return this.chunkStartTimesUs[paramInt];
    }
  }
  
  public static class TrackElement
    implements FormatWrapper
  {
    public final byte[][] csd;
    public final Format format;
    
    public TrackElement(int paramInt1, int paramInt2, String paramString1, byte[][] paramArrayOfByte, int paramInt3, int paramInt4, int paramInt5, int paramInt6, String paramString2)
    {
      this.csd = paramArrayOfByte;
      this.format = new Format(String.valueOf(paramInt1), paramString1, paramInt3, paramInt4, -1.0F, paramInt6, paramInt5, paramInt2, paramString2);
    }
    
    public Format getFormat()
    {
      return this.format;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/smoothstreaming/SmoothStreamingManifest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */