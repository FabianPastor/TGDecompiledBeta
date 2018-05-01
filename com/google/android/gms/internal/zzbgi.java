package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zzc;
import com.google.android.gms.common.util.zzo;
import com.google.android.gms.common.util.zzp;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class zzbgi
{
  protected static <O, I> I zza(zzbgj<I, O> paramzzbgj, Object paramObject)
  {
    Object localObject = paramObject;
    if (zzbgj.zzc(paramzzbgj) != null) {
      localObject = paramzzbgj.convertBack(paramObject);
    }
    return (I)localObject;
  }
  
  private static void zza(StringBuilder paramStringBuilder, zzbgj paramzzbgj, Object paramObject)
  {
    if (paramzzbgj.zzaIH == 11)
    {
      paramStringBuilder.append(((zzbgi)paramzzbgj.zzaIN.cast(paramObject)).toString());
      return;
    }
    if (paramzzbgj.zzaIH == 7)
    {
      paramStringBuilder.append("\"");
      paramStringBuilder.append(zzo.zzcK((String)paramObject));
      paramStringBuilder.append("\"");
      return;
    }
    paramStringBuilder.append(paramObject);
  }
  
  private static void zza(StringBuilder paramStringBuilder, zzbgj paramzzbgj, ArrayList<Object> paramArrayList)
  {
    paramStringBuilder.append("[");
    int i = 0;
    int j = paramArrayList.size();
    while (i < j)
    {
      if (i > 0) {
        paramStringBuilder.append(",");
      }
      Object localObject = paramArrayList.get(i);
      if (localObject != null) {
        zza(paramStringBuilder, paramzzbgj, localObject);
      }
      i += 1;
    }
    paramStringBuilder.append("]");
  }
  
  public String toString()
  {
    Map localMap = zzrL();
    StringBuilder localStringBuilder = new StringBuilder(100);
    Iterator localIterator = localMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      zzbgj localzzbgj = (zzbgj)localMap.get(str);
      if (zza(localzzbgj))
      {
        Object localObject = zza(localzzbgj, zzb(localzzbgj));
        if (localStringBuilder.length() == 0) {
          localStringBuilder.append("{");
        }
        for (;;)
        {
          localStringBuilder.append("\"").append(str).append("\":");
          if (localObject != null) {
            break label138;
          }
          localStringBuilder.append("null");
          break;
          localStringBuilder.append(",");
        }
        label138:
        switch (localzzbgj.zzaIJ)
        {
        default: 
          if (localzzbgj.zzaII) {
            zza(localStringBuilder, localzzbgj, (ArrayList)localObject);
          }
          break;
        case 8: 
          localStringBuilder.append("\"").append(zzc.encode((byte[])localObject)).append("\"");
          break;
        case 9: 
          localStringBuilder.append("\"").append(zzc.zzg((byte[])localObject)).append("\"");
          break;
        case 10: 
          zzp.zza(localStringBuilder, (HashMap)localObject);
          continue;
          zza(localStringBuilder, localzzbgj, localObject);
        }
      }
    }
    if (localStringBuilder.length() > 0) {
      localStringBuilder.append("}");
    }
    for (;;)
    {
      return localStringBuilder.toString();
      localStringBuilder.append("{}");
    }
  }
  
  protected boolean zza(zzbgj paramzzbgj)
  {
    if (paramzzbgj.zzaIJ == 11)
    {
      if (paramzzbgj.zzaIK)
      {
        paramzzbgj = paramzzbgj.zzaIL;
        throw new UnsupportedOperationException("Concrete type arrays not supported");
      }
      paramzzbgj = paramzzbgj.zzaIL;
      throw new UnsupportedOperationException("Concrete types not supported");
    }
    return zzcI(paramzzbgj.zzaIL);
  }
  
  protected Object zzb(zzbgj paramzzbgj)
  {
    String str = paramzzbgj.zzaIL;
    if (paramzzbgj.zzaIN != null)
    {
      zzcH(paramzzbgj.zzaIL);
      zzbo.zza(true, "Concrete field shouldn't be value object: %s", new Object[] { paramzzbgj.zzaIL });
      boolean bool = paramzzbgj.zzaIK;
      try
      {
        char c = Character.toUpperCase(str.charAt(0));
        paramzzbgj = String.valueOf(str.substring(1));
        paramzzbgj = String.valueOf(paramzzbgj).length() + 4 + "get" + c + paramzzbgj;
        paramzzbgj = getClass().getMethod(paramzzbgj, new Class[0]).invoke(this, new Object[0]);
        return paramzzbgj;
      }
      catch (Exception paramzzbgj)
      {
        throw new RuntimeException(paramzzbgj);
      }
    }
    return zzcH(paramzzbgj.zzaIL);
  }
  
  protected abstract Object zzcH(String paramString);
  
  protected abstract boolean zzcI(String paramString);
  
  public abstract Map<String, zzbgj<?, ?>> zzrL();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbgi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */