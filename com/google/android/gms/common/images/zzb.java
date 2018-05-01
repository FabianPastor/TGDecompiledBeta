package com.google.android.gms.common.images;

import android.net.Uri;
import com.google.android.gms.common.internal.zzbe;
import java.util.Arrays;

final class zzb
{
  public final Uri uri;
  
  public zzb(Uri paramUri)
  {
    this.uri = paramUri;
  }
  
  public final boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof zzb)) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    return zzbe.equal(((zzb)paramObject).uri, this.uri);
  }
  
  public final int hashCode()
  {
    return Arrays.hashCode(new Object[] { this.uri });
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/images/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */