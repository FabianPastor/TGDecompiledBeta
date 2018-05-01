package org.telegram.messenger.exoplayer2.source.dash.manifest;

import android.net.Uri;
import org.telegram.messenger.exoplayer2.util.UriUtil;

public final class RangedUri
{
  private int hashCode;
  public final long length;
  private final String referenceUri;
  public final long start;
  
  public RangedUri(String paramString, long paramLong1, long paramLong2)
  {
    String str = paramString;
    if (paramString == null) {
      str = "";
    }
    this.referenceUri = str;
    this.start = paramLong1;
    this.length = paramLong2;
  }
  
  public RangedUri attemptMerge(RangedUri paramRangedUri, String paramString)
  {
    Object localObject1 = null;
    long l1 = -1L;
    String str = resolveUriString(paramString);
    Object localObject2 = localObject1;
    if (paramRangedUri != null)
    {
      if (str.equals(paramRangedUri.resolveUriString(paramString))) {
        break label40;
      }
      localObject2 = localObject1;
    }
    label40:
    do
    {
      do
      {
        return (RangedUri)localObject2;
        if ((this.length != -1L) && (this.start + this.length == paramRangedUri.start))
        {
          l2 = this.start;
          if (paramRangedUri.length == -1L) {}
          for (;;)
          {
            localObject2 = new RangedUri(str, l2, l1);
            break;
            l1 = this.length + paramRangedUri.length;
          }
        }
        localObject2 = localObject1;
      } while (paramRangedUri.length == -1L);
      localObject2 = localObject1;
    } while (paramRangedUri.start + paramRangedUri.length != this.start);
    long l2 = paramRangedUri.start;
    if (this.length == -1L) {}
    for (;;)
    {
      localObject2 = new RangedUri(str, l2, l1);
      break;
      l1 = paramRangedUri.length + this.length;
    }
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this == paramObject) {}
    for (;;)
    {
      return bool;
      if ((paramObject == null) || (getClass() != paramObject.getClass()))
      {
        bool = false;
      }
      else
      {
        paramObject = (RangedUri)paramObject;
        if ((this.start != ((RangedUri)paramObject).start) || (this.length != ((RangedUri)paramObject).length) || (!this.referenceUri.equals(((RangedUri)paramObject).referenceUri))) {
          bool = false;
        }
      }
    }
  }
  
  public int hashCode()
  {
    if (this.hashCode == 0) {
      this.hashCode = ((((int)this.start + 527) * 31 + (int)this.length) * 31 + this.referenceUri.hashCode());
    }
    return this.hashCode;
  }
  
  public Uri resolveUri(String paramString)
  {
    return UriUtil.resolveToUri(paramString, this.referenceUri);
  }
  
  public String resolveUriString(String paramString)
  {
    return UriUtil.resolve(paramString, this.referenceUri);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/dash/manifest/RangedUri.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */