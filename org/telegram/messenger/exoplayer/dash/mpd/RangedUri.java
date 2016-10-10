package org.telegram.messenger.exoplayer.dash.mpd;

import android.net.Uri;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.UriUtil;

public final class RangedUri
{
  private final String baseUri;
  private int hashCode;
  public final long length;
  private final String referenceUri;
  public final long start;
  
  public RangedUri(String paramString1, String paramString2, long paramLong1, long paramLong2)
  {
    if ((paramString1 != null) || (paramString2 != null)) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      this.baseUri = paramString1;
      this.referenceUri = paramString2;
      this.start = paramLong1;
      this.length = paramLong2;
      return;
    }
  }
  
  public RangedUri attemptMerge(RangedUri paramRangedUri)
  {
    long l1 = -1L;
    if ((paramRangedUri == null) || (!getUriString().equals(paramRangedUri.getUriString()))) {}
    do
    {
      return null;
      if ((this.length != -1L) && (this.start + this.length == paramRangedUri.start))
      {
        str1 = this.baseUri;
        str2 = this.referenceUri;
        l2 = this.start;
        if (paramRangedUri.length == -1L) {}
        for (;;)
        {
          return new RangedUri(str1, str2, l2, l1);
          l1 = this.length + paramRangedUri.length;
        }
      }
    } while ((paramRangedUri.length == -1L) || (paramRangedUri.start + paramRangedUri.length != this.start));
    String str1 = this.baseUri;
    String str2 = this.referenceUri;
    long l2 = paramRangedUri.start;
    if (this.length == -1L) {}
    for (;;)
    {
      return new RangedUri(str1, str2, l2, l1);
      l1 = paramRangedUri.length + this.length;
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      paramObject = (RangedUri)paramObject;
    } while ((this.start == ((RangedUri)paramObject).start) && (this.length == ((RangedUri)paramObject).length) && (getUriString().equals(((RangedUri)paramObject).getUriString())));
    return false;
  }
  
  public Uri getUri()
  {
    return UriUtil.resolveToUri(this.baseUri, this.referenceUri);
  }
  
  public String getUriString()
  {
    return UriUtil.resolve(this.baseUri, this.referenceUri);
  }
  
  public int hashCode()
  {
    if (this.hashCode == 0) {
      this.hashCode = ((((int)this.start + 527) * 31 + (int)this.length) * 31 + getUriString().hashCode());
    }
    return this.hashCode;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/dash/mpd/RangedUri.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */