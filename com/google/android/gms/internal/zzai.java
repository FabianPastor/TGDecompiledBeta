package com.google.android.gms.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

final class zzai
{
  public String key;
  public long size;
  public String zza;
  public long zzb;
  public long zzc;
  public long zzd;
  public long zze;
  public Map<String, String> zzf;
  
  private zzai() {}
  
  public zzai(String paramString, zzc paramzzc)
  {
    this.key = paramString;
    this.size = paramzzc.data.length;
    this.zza = paramzzc.zza;
    this.zzb = paramzzc.zzb;
    this.zzc = paramzzc.zzc;
    this.zzd = paramzzc.zzd;
    this.zze = paramzzc.zze;
    this.zzf = paramzzc.zzf;
  }
  
  public static zzai zzf(InputStream paramInputStream)
    throws IOException
  {
    zzai localzzai = new zzai();
    if (zzag.zzb(paramInputStream) != 538247942) {
      throw new IOException();
    }
    localzzai.key = zzag.zzd(paramInputStream);
    localzzai.zza = zzag.zzd(paramInputStream);
    if (localzzai.zza.equals("")) {
      localzzai.zza = null;
    }
    localzzai.zzb = zzag.zzc(paramInputStream);
    localzzai.zzc = zzag.zzc(paramInputStream);
    localzzai.zzd = zzag.zzc(paramInputStream);
    localzzai.zze = zzag.zzc(paramInputStream);
    localzzai.zzf = zzag.zze(paramInputStream);
    return localzzai;
  }
  
  public final boolean zza(OutputStream paramOutputStream)
  {
    for (;;)
    {
      try
      {
        zzag.zza(paramOutputStream, 538247942);
        zzag.zza(paramOutputStream, this.key);
        if (this.zza == null)
        {
          localObject = "";
          zzag.zza(paramOutputStream, (String)localObject);
          zzag.zza(paramOutputStream, this.zzb);
          zzag.zza(paramOutputStream, this.zzc);
          zzag.zza(paramOutputStream, this.zzd);
          zzag.zza(paramOutputStream, this.zze);
          localObject = this.zzf;
          if (localObject == null) {
            break;
          }
          zzag.zza(paramOutputStream, ((Map)localObject).size());
          localObject = ((Map)localObject).entrySet().iterator();
          if (!((Iterator)localObject).hasNext()) {
            break label172;
          }
          Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
          zzag.zza(paramOutputStream, (String)localEntry.getKey());
          zzag.zza(paramOutputStream, (String)localEntry.getValue());
          continue;
        }
        Object localObject = this.zza;
      }
      catch (IOException paramOutputStream)
      {
        zzab.zzb("%s", new Object[] { paramOutputStream.toString() });
        return false;
      }
    }
    zzag.zza(paramOutputStream, 0);
    label172:
    paramOutputStream.flush();
    return true;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzai.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */