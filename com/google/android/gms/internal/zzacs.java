package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzaa.zza;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zzc;
import com.google.android.gms.common.util.zzq;
import com.google.android.gms.common.util.zzr;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class zzacs
{
  private void zza(StringBuilder paramStringBuilder, zza paramzza, Object paramObject)
  {
    if (paramzza.zzys() == 11)
    {
      paramStringBuilder.append(((zzacs)paramzza.zzyy().cast(paramObject)).toString());
      return;
    }
    if (paramzza.zzys() == 7)
    {
      paramStringBuilder.append("\"");
      paramStringBuilder.append(zzq.zzdy((String)paramObject));
      paramStringBuilder.append("\"");
      return;
    }
    paramStringBuilder.append(paramObject);
  }
  
  private void zza(StringBuilder paramStringBuilder, zza paramzza, ArrayList<Object> paramArrayList)
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
        zza(paramStringBuilder, paramzza, localObject);
      }
      i += 1;
    }
    paramStringBuilder.append("]");
  }
  
  public String toString()
  {
    Map localMap = zzyr();
    StringBuilder localStringBuilder = new StringBuilder(100);
    Iterator localIterator = localMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      zza localzza = (zza)localMap.get(str);
      if (zza(localzza))
      {
        Object localObject = zza(localzza, zzb(localzza));
        if (localStringBuilder.length() == 0) {
          localStringBuilder.append("{");
        }
        for (;;)
        {
          localStringBuilder.append("\"").append(str).append("\":");
          if (localObject != null) {
            break label139;
          }
          localStringBuilder.append("null");
          break;
          localStringBuilder.append(",");
        }
        label139:
        switch (localzza.zzyu())
        {
        default: 
          if (localzza.zzyt()) {
            zza(localStringBuilder, localzza, (ArrayList)localObject);
          }
          break;
        case 8: 
          localStringBuilder.append("\"").append(zzc.zzq((byte[])localObject)).append("\"");
          break;
        case 9: 
          localStringBuilder.append("\"").append(zzc.zzr((byte[])localObject)).append("\"");
          break;
        case 10: 
          zzr.zza(localStringBuilder, (HashMap)localObject);
          continue;
          zza(localStringBuilder, localzza, localObject);
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
  
  protected <O, I> I zza(zza<I, O> paramzza, Object paramObject)
  {
    Object localObject = paramObject;
    if (zza.zzc(paramzza) != null) {
      localObject = paramzza.convertBack(paramObject);
    }
    return (I)localObject;
  }
  
  protected boolean zza(zza paramzza)
  {
    if (paramzza.zzyu() == 11)
    {
      if (paramzza.zzyv()) {
        return zzdv(paramzza.zzyw());
      }
      return zzdu(paramzza.zzyw());
    }
    return zzdt(paramzza.zzyw());
  }
  
  protected Object zzb(zza paramzza)
  {
    String str = paramzza.zzyw();
    if (paramzza.zzyy() != null)
    {
      zzds(paramzza.zzyw());
      zzac.zza(true, "Concrete field shouldn't be value object: %s", new Object[] { paramzza.zzyw() });
      paramzza.zzyv();
      try
      {
        char c = Character.toUpperCase(str.charAt(0));
        paramzza = String.valueOf(str.substring(1));
        paramzza = String.valueOf(paramzza).length() + 4 + "get" + c + paramzza;
        paramzza = getClass().getMethod(paramzza, new Class[0]).invoke(this, new Object[0]);
        return paramzza;
      }
      catch (Exception paramzza)
      {
        throw new RuntimeException(paramzza);
      }
    }
    return zzds(paramzza.zzyw());
  }
  
  protected abstract Object zzds(String paramString);
  
  protected abstract boolean zzdt(String paramString);
  
  protected boolean zzdu(String paramString)
  {
    throw new UnsupportedOperationException("Concrete types not supported");
  }
  
  protected boolean zzdv(String paramString)
  {
    throw new UnsupportedOperationException("Concrete type arrays not supported");
  }
  
  public abstract Map<String, zza<?, ?>> zzyr();
  
  public static class zza<I, O>
    extends zza
  {
    public static final zzacu CREATOR = new zzacu();
    protected final int zzaGX;
    protected final boolean zzaGY;
    protected final int zzaGZ;
    protected final boolean zzaHa;
    protected final String zzaHb;
    protected final int zzaHc;
    protected final Class<? extends zzacs> zzaHd;
    protected final String zzaHe;
    private zzacw zzaHf;
    private zzacs.zzb<I, O> zzaHg;
    private final int zzaiI;
    
    zza(int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, boolean paramBoolean2, String paramString1, int paramInt4, String paramString2, zzacn paramzzacn)
    {
      this.zzaiI = paramInt1;
      this.zzaGX = paramInt2;
      this.zzaGY = paramBoolean1;
      this.zzaGZ = paramInt3;
      this.zzaHa = paramBoolean2;
      this.zzaHb = paramString1;
      this.zzaHc = paramInt4;
      if (paramString2 == null) {
        this.zzaHd = null;
      }
      for (this.zzaHe = null; paramzzacn == null; this.zzaHe = paramString2)
      {
        this.zzaHg = null;
        return;
        this.zzaHd = zzacz.class;
      }
      this.zzaHg = paramzzacn.zzyp();
    }
    
    protected zza(int paramInt1, boolean paramBoolean1, int paramInt2, boolean paramBoolean2, String paramString, int paramInt3, Class<? extends zzacs> paramClass, zzacs.zzb<I, O> paramzzb)
    {
      this.zzaiI = 1;
      this.zzaGX = paramInt1;
      this.zzaGY = paramBoolean1;
      this.zzaGZ = paramInt2;
      this.zzaHa = paramBoolean2;
      this.zzaHb = paramString;
      this.zzaHc = paramInt3;
      this.zzaHd = paramClass;
      if (paramClass == null) {}
      for (this.zzaHe = null;; this.zzaHe = paramClass.getCanonicalName())
      {
        this.zzaHg = paramzzb;
        return;
      }
    }
    
    public static zza zza(String paramString, int paramInt, zzacs.zzb<?, ?> paramzzb, boolean paramBoolean)
    {
      return new zza(7, paramBoolean, 0, false, paramString, paramInt, null, paramzzb);
    }
    
    public static <T extends zzacs> zza<T, T> zza(String paramString, int paramInt, Class<T> paramClass)
    {
      return new zza(11, false, 11, false, paramString, paramInt, paramClass, null);
    }
    
    public static <T extends zzacs> zza<ArrayList<T>, ArrayList<T>> zzb(String paramString, int paramInt, Class<T> paramClass)
    {
      return new zza(11, true, 11, true, paramString, paramInt, paramClass, null);
    }
    
    public static zza<Integer, Integer> zzk(String paramString, int paramInt)
    {
      return new zza(0, false, 0, false, paramString, paramInt, null, null);
    }
    
    public static zza<Boolean, Boolean> zzl(String paramString, int paramInt)
    {
      return new zza(6, false, 6, false, paramString, paramInt, null, null);
    }
    
    public static zza<String, String> zzm(String paramString, int paramInt)
    {
      return new zza(7, false, 7, false, paramString, paramInt, null, null);
    }
    
    public I convertBack(O paramO)
    {
      return (I)this.zzaHg.convertBack(paramO);
    }
    
    public int getVersionCode()
    {
      return this.zzaiI;
    }
    
    public String toString()
    {
      zzaa.zza localzza = zzaa.zzv(this).zzg("versionCode", Integer.valueOf(this.zzaiI)).zzg("typeIn", Integer.valueOf(this.zzaGX)).zzg("typeInArray", Boolean.valueOf(this.zzaGY)).zzg("typeOut", Integer.valueOf(this.zzaGZ)).zzg("typeOutArray", Boolean.valueOf(this.zzaHa)).zzg("outputFieldName", this.zzaHb).zzg("safeParcelFieldId", Integer.valueOf(this.zzaHc)).zzg("concreteTypeName", zzyz());
      Class localClass = zzyy();
      if (localClass != null) {
        localzza.zzg("concreteType.class", localClass.getCanonicalName());
      }
      if (this.zzaHg != null) {
        localzza.zzg("converterName", this.zzaHg.getClass().getCanonicalName());
      }
      return localzza.toString();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      zzacu.zza(this, paramParcel, paramInt);
    }
    
    public void zza(zzacw paramzzacw)
    {
      this.zzaHf = paramzzacw;
    }
    
    public boolean zzyA()
    {
      return this.zzaHg != null;
    }
    
    zzacn zzyB()
    {
      if (this.zzaHg == null) {
        return null;
      }
      return zzacn.zza(this.zzaHg);
    }
    
    public Map<String, zza<?, ?>> zzyC()
    {
      zzac.zzw(this.zzaHe);
      zzac.zzw(this.zzaHf);
      return this.zzaHf.zzdw(this.zzaHe);
    }
    
    public int zzys()
    {
      return this.zzaGX;
    }
    
    public boolean zzyt()
    {
      return this.zzaGY;
    }
    
    public int zzyu()
    {
      return this.zzaGZ;
    }
    
    public boolean zzyv()
    {
      return this.zzaHa;
    }
    
    public String zzyw()
    {
      return this.zzaHb;
    }
    
    public int zzyx()
    {
      return this.zzaHc;
    }
    
    public Class<? extends zzacs> zzyy()
    {
      return this.zzaHd;
    }
    
    String zzyz()
    {
      if (this.zzaHe == null) {
        return null;
      }
      return this.zzaHe;
    }
  }
  
  public static abstract interface zzb<I, O>
  {
    public abstract I convertBack(O paramO);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzacs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */