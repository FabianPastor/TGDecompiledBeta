package org.telegram.messenger.exoplayer.dash.mpd;

import android.net.Uri;
import org.telegram.messenger.exoplayer.chunk.Format;
import org.telegram.messenger.exoplayer.chunk.FormatWrapper;
import org.telegram.messenger.exoplayer.dash.DashSegmentIndex;

public abstract class Representation
  implements FormatWrapper
{
  private final String cacheKey;
  public final String contentId;
  public final Format format;
  private final RangedUri initializationUri;
  public final long presentationTimeOffsetUs;
  public final long revisionId;
  
  private Representation(String paramString1, long paramLong, Format paramFormat, SegmentBase paramSegmentBase, String paramString2)
  {
    this.contentId = paramString1;
    this.revisionId = paramLong;
    this.format = paramFormat;
    if (paramString2 != null) {}
    for (;;)
    {
      this.cacheKey = paramString2;
      this.initializationUri = paramSegmentBase.getInitialization(this);
      this.presentationTimeOffsetUs = paramSegmentBase.getPresentationTimeOffsetUs();
      return;
      paramString2 = paramString1 + "." + paramFormat.id + "." + paramLong;
    }
  }
  
  public static Representation newInstance(String paramString, long paramLong, Format paramFormat, SegmentBase paramSegmentBase)
  {
    return newInstance(paramString, paramLong, paramFormat, paramSegmentBase, null);
  }
  
  public static Representation newInstance(String paramString1, long paramLong, Format paramFormat, SegmentBase paramSegmentBase, String paramString2)
  {
    if ((paramSegmentBase instanceof SegmentBase.SingleSegmentBase)) {
      return new SingleSegmentRepresentation(paramString1, paramLong, paramFormat, (SegmentBase.SingleSegmentBase)paramSegmentBase, paramString2, -1L);
    }
    if ((paramSegmentBase instanceof SegmentBase.MultiSegmentBase)) {
      return new MultiSegmentRepresentation(paramString1, paramLong, paramFormat, (SegmentBase.MultiSegmentBase)paramSegmentBase, paramString2);
    }
    throw new IllegalArgumentException("segmentBase must be of type SingleSegmentBase or MultiSegmentBase");
  }
  
  public String getCacheKey()
  {
    return this.cacheKey;
  }
  
  public Format getFormat()
  {
    return this.format;
  }
  
  public abstract DashSegmentIndex getIndex();
  
  public abstract RangedUri getIndexUri();
  
  public RangedUri getInitializationUri()
  {
    return this.initializationUri;
  }
  
  public static class MultiSegmentRepresentation
    extends Representation
    implements DashSegmentIndex
  {
    private final SegmentBase.MultiSegmentBase segmentBase;
    
    public MultiSegmentRepresentation(String paramString1, long paramLong, Format paramFormat, SegmentBase.MultiSegmentBase paramMultiSegmentBase, String paramString2)
    {
      super(paramLong, paramFormat, paramMultiSegmentBase, paramString2, null);
      this.segmentBase = paramMultiSegmentBase;
    }
    
    public long getDurationUs(int paramInt, long paramLong)
    {
      return this.segmentBase.getSegmentDurationUs(paramInt, paramLong);
    }
    
    public int getFirstSegmentNum()
    {
      return this.segmentBase.getFirstSegmentNum();
    }
    
    public DashSegmentIndex getIndex()
    {
      return this;
    }
    
    public RangedUri getIndexUri()
    {
      return null;
    }
    
    public int getLastSegmentNum(long paramLong)
    {
      return this.segmentBase.getLastSegmentNum(paramLong);
    }
    
    public int getSegmentNum(long paramLong1, long paramLong2)
    {
      return this.segmentBase.getSegmentNum(paramLong1, paramLong2);
    }
    
    public RangedUri getSegmentUrl(int paramInt)
    {
      return this.segmentBase.getSegmentUrl(this, paramInt);
    }
    
    public long getTimeUs(int paramInt)
    {
      return this.segmentBase.getSegmentTimeUs(paramInt);
    }
    
    public boolean isExplicit()
    {
      return this.segmentBase.isExplicit();
    }
  }
  
  public static class SingleSegmentRepresentation
    extends Representation
  {
    public final long contentLength;
    private final RangedUri indexUri;
    private final DashSingleSegmentIndex segmentIndex;
    public final Uri uri;
    
    public SingleSegmentRepresentation(String paramString1, long paramLong1, Format paramFormat, SegmentBase.SingleSegmentBase paramSingleSegmentBase, String paramString2, long paramLong2)
    {
      super(paramLong1, paramFormat, paramSingleSegmentBase, paramString2, null);
      this.uri = Uri.parse(paramSingleSegmentBase.uri);
      this.indexUri = paramSingleSegmentBase.getIndex();
      this.contentLength = paramLong2;
      if (this.indexUri != null) {}
      for (paramString1 = null;; paramString1 = new DashSingleSegmentIndex(new RangedUri(paramSingleSegmentBase.uri, null, 0L, paramLong2)))
      {
        this.segmentIndex = paramString1;
        return;
      }
    }
    
    public static SingleSegmentRepresentation newInstance(String paramString1, long paramLong1, Format paramFormat, String paramString2, long paramLong2, long paramLong3, long paramLong4, long paramLong5, String paramString3, long paramLong6)
    {
      return new SingleSegmentRepresentation(paramString1, paramLong1, paramFormat, new SegmentBase.SingleSegmentBase(new RangedUri(paramString2, null, paramLong2, 1L + (paramLong3 - paramLong2)), 1L, 0L, paramString2, paramLong4, paramLong5 - paramLong4 + 1L), paramString3, paramLong6);
    }
    
    public DashSegmentIndex getIndex()
    {
      return this.segmentIndex;
    }
    
    public RangedUri getIndexUri()
    {
      return this.indexUri;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/dash/mpd/Representation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */