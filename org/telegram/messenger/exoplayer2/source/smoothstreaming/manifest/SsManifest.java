package org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest;

import android.net.Uri;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.UriUtil;
import org.telegram.messenger.exoplayer2.util.Util;

public class SsManifest
{
  public static final int UNSET_LOOKAHEAD = -1;
  public final long durationUs;
  public final long dvrWindowLengthUs;
  public final boolean isLive;
  public final int lookAheadCount;
  public final int majorVersion;
  public final int minorVersion;
  public final ProtectionElement protectionElement;
  public final StreamElement[] streamElements;
  
  private SsManifest(int paramInt1, int paramInt2, long paramLong1, long paramLong2, int paramInt3, boolean paramBoolean, ProtectionElement paramProtectionElement, StreamElement[] paramArrayOfStreamElement)
  {
    this.majorVersion = paramInt1;
    this.minorVersion = paramInt2;
    this.durationUs = paramLong1;
    this.dvrWindowLengthUs = paramLong2;
    this.lookAheadCount = paramInt3;
    this.isLive = paramBoolean;
    this.protectionElement = paramProtectionElement;
    this.streamElements = paramArrayOfStreamElement;
  }
  
  public SsManifest(int paramInt1, int paramInt2, long paramLong1, long paramLong2, long paramLong3, int paramInt3, boolean paramBoolean, ProtectionElement paramProtectionElement, StreamElement[] paramArrayOfStreamElement) {}
  
  public final SsManifest copy(List<TrackKey> paramList)
  {
    LinkedList localLinkedList = new LinkedList(paramList);
    Collections.sort(localLinkedList);
    paramList = null;
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    for (int i = 0; i < localLinkedList.size(); i++)
    {
      TrackKey localTrackKey = (TrackKey)localLinkedList.get(i);
      StreamElement localStreamElement = this.streamElements[localTrackKey.streamElementIndex];
      if ((localStreamElement != paramList) && (paramList != null))
      {
        localArrayList1.add(paramList.copy((Format[])localArrayList2.toArray(new Format[0])));
        localArrayList2.clear();
      }
      paramList = localStreamElement;
      localArrayList2.add(localStreamElement.formats[localTrackKey.trackIndex]);
    }
    if (paramList != null) {
      localArrayList1.add(paramList.copy((Format[])localArrayList2.toArray(new Format[0])));
    }
    paramList = (StreamElement[])localArrayList1.toArray(new StreamElement[0]);
    return new SsManifest(this.majorVersion, this.minorVersion, this.durationUs, this.dvrWindowLengthUs, this.lookAheadCount, this.isLive, this.protectionElement, paramList);
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
    private static final String URL_PLACEHOLDER_BITRATE_1 = "{bitrate}";
    private static final String URL_PLACEHOLDER_BITRATE_2 = "{Bitrate}";
    private static final String URL_PLACEHOLDER_START_TIME_1 = "{start time}";
    private static final String URL_PLACEHOLDER_START_TIME_2 = "{start_time}";
    private final String baseUri;
    public final int chunkCount;
    private final List<Long> chunkStartTimes;
    private final long[] chunkStartTimesUs;
    private final String chunkTemplate;
    public final int displayHeight;
    public final int displayWidth;
    public final Format[] formats;
    public final String language;
    private final long lastChunkDurationUs;
    public final int maxHeight;
    public final int maxWidth;
    public final String name;
    public final String subType;
    public final long timescale;
    public final int type;
    
    public StreamElement(String paramString1, String paramString2, int paramInt1, String paramString3, long paramLong1, String paramString4, int paramInt2, int paramInt3, int paramInt4, int paramInt5, String paramString5, Format[] paramArrayOfFormat, List<Long> paramList, long paramLong2)
    {
      this(paramString1, paramString2, paramInt1, paramString3, paramLong1, paramString4, paramInt2, paramInt3, paramInt4, paramInt5, paramString5, paramArrayOfFormat, paramList, Util.scaleLargeTimestamps(paramList, 1000000L, paramLong1), Util.scaleLargeTimestamp(paramLong2, 1000000L, paramLong1));
    }
    
    private StreamElement(String paramString1, String paramString2, int paramInt1, String paramString3, long paramLong1, String paramString4, int paramInt2, int paramInt3, int paramInt4, int paramInt5, String paramString5, Format[] paramArrayOfFormat, List<Long> paramList, long[] paramArrayOfLong, long paramLong2)
    {
      this.baseUri = paramString1;
      this.chunkTemplate = paramString2;
      this.type = paramInt1;
      this.subType = paramString3;
      this.timescale = paramLong1;
      this.name = paramString4;
      this.maxWidth = paramInt2;
      this.maxHeight = paramInt3;
      this.displayWidth = paramInt4;
      this.displayHeight = paramInt5;
      this.language = paramString5;
      this.formats = paramArrayOfFormat;
      this.chunkStartTimes = paramList;
      this.chunkStartTimesUs = paramArrayOfLong;
      this.lastChunkDurationUs = paramLong2;
      this.chunkCount = paramList.size();
    }
    
    public Uri buildRequestUri(int paramInt1, int paramInt2)
    {
      boolean bool1 = true;
      if (this.formats != null)
      {
        bool2 = true;
        Assertions.checkState(bool2);
        if (this.chunkStartTimes == null) {
          break label135;
        }
        bool2 = true;
        label27:
        Assertions.checkState(bool2);
        if (paramInt2 >= this.chunkStartTimes.size()) {
          break label141;
        }
      }
      label135:
      label141:
      for (boolean bool2 = bool1;; bool2 = false)
      {
        Assertions.checkState(bool2);
        String str1 = Integer.toString(this.formats[paramInt1].bitrate);
        String str2 = ((Long)this.chunkStartTimes.get(paramInt2)).toString();
        str2 = this.chunkTemplate.replace("{bitrate}", str1).replace("{Bitrate}", str1).replace("{start time}", str2).replace("{start_time}", str2);
        return UriUtil.resolveToUri(this.baseUri, str2);
        bool2 = false;
        break;
        bool2 = false;
        break label27;
      }
    }
    
    public StreamElement copy(Format[] paramArrayOfFormat)
    {
      return new StreamElement(this.baseUri, this.chunkTemplate, this.type, this.subType, this.timescale, this.name, this.maxWidth, this.maxHeight, this.displayWidth, this.displayHeight, this.language, paramArrayOfFormat, this.chunkStartTimes, this.chunkStartTimesUs, this.lastChunkDurationUs);
    }
    
    public long getChunkDurationUs(int paramInt)
    {
      if (paramInt == this.chunkCount - 1) {}
      for (long l = this.lastChunkDurationUs;; l = this.chunkStartTimesUs[(paramInt + 1)] - this.chunkStartTimesUs[paramInt]) {
        return l;
      }
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
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/smoothstreaming/manifest/SsManifest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */