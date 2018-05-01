package com.google.android.gms.common.server.response;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzz;
import com.google.android.gms.common.internal.zzz.zza;
import com.google.android.gms.common.server.converter.ConverterWrapper;
import com.google.android.gms.common.util.zzc;
import com.google.android.gms.common.util.zzp;
import com.google.android.gms.common.util.zzq;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class FastJsonResponse
{
  private void zza(StringBuilder paramStringBuilder, Field paramField, Object paramObject)
  {
    if (paramField.zzaxa() == 11)
    {
      paramStringBuilder.append(((FastJsonResponse)paramField.zzaxg().cast(paramObject)).toString());
      return;
    }
    if (paramField.zzaxa() == 7)
    {
      paramStringBuilder.append("\"");
      paramStringBuilder.append(zzp.zzii((String)paramObject));
      paramStringBuilder.append("\"");
      return;
    }
    paramStringBuilder.append(paramObject);
  }
  
  private void zza(StringBuilder paramStringBuilder, Field paramField, ArrayList<Object> paramArrayList)
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
        zza(paramStringBuilder, paramField, localObject);
      }
      i += 1;
    }
    paramStringBuilder.append("]");
  }
  
  public String toString()
  {
    Map localMap = zzawz();
    StringBuilder localStringBuilder = new StringBuilder(100);
    Iterator localIterator = localMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      Field localField = (Field)localMap.get(str);
      if (zza(localField))
      {
        Object localObject = zza(localField, zzb(localField));
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
        switch (localField.zzaxc())
        {
        default: 
          if (localField.zzaxb()) {
            zza(localStringBuilder, localField, (ArrayList)localObject);
          }
          break;
        case 8: 
          localStringBuilder.append("\"").append(zzc.zzq((byte[])localObject)).append("\"");
          break;
        case 9: 
          localStringBuilder.append("\"").append(zzc.zzr((byte[])localObject)).append("\"");
          break;
        case 10: 
          zzq.zza(localStringBuilder, (HashMap)localObject);
          continue;
          zza(localStringBuilder, localField, localObject);
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
  
  protected <O, I> I zza(Field<I, O> paramField, Object paramObject)
  {
    Object localObject = paramObject;
    if (Field.zzc(paramField) != null) {
      localObject = paramField.convertBack(paramObject);
    }
    return (I)localObject;
  }
  
  protected boolean zza(Field paramField)
  {
    if (paramField.zzaxc() == 11)
    {
      if (paramField.zzaxd()) {
        return zzif(paramField.zzaxe());
      }
      return zzie(paramField.zzaxe());
    }
    return zzid(paramField.zzaxe());
  }
  
  public abstract Map<String, Field<?, ?>> zzawz();
  
  protected Object zzb(Field paramField)
  {
    String str = paramField.zzaxe();
    if (paramField.zzaxg() != null)
    {
      if (zzic(paramField.zzaxe()) == null) {}
      for (boolean bool = true;; bool = false)
      {
        zzaa.zza(bool, "Concrete field shouldn't be value object: %s", new Object[] { paramField.zzaxe() });
        if (paramField.zzaxd()) {}
        try
        {
          char c = Character.toUpperCase(str.charAt(0));
          paramField = String.valueOf(str.substring(1));
          paramField = String.valueOf(paramField).length() + 4 + "get" + c + paramField;
          paramField = getClass().getMethod(paramField, new Class[0]).invoke(this, new Object[0]);
          return paramField;
        }
        catch (Exception paramField)
        {
          throw new RuntimeException(paramField);
        }
      }
    }
    return zzic(paramField.zzaxe());
  }
  
  protected abstract Object zzic(String paramString);
  
  protected abstract boolean zzid(String paramString);
  
  protected boolean zzie(String paramString)
  {
    throw new UnsupportedOperationException("Concrete types not supported");
  }
  
  protected boolean zzif(String paramString)
  {
    throw new UnsupportedOperationException("Concrete type arrays not supported");
  }
  
  public static class Field<I, O>
    extends AbstractSafeParcelable
  {
    public static final zza CREATOR = new zza();
    protected final int Fg;
    protected final boolean Fh;
    protected final int Fi;
    protected final boolean Fj;
    protected final String Fk;
    protected final int Fl;
    protected final Class<? extends FastJsonResponse> Fm;
    protected final String Fn;
    private FieldMappingDictionary Fo;
    private FastJsonResponse.zza<I, O> Fp;
    private final int mVersionCode;
    
    Field(int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, boolean paramBoolean2, String paramString1, int paramInt4, String paramString2, ConverterWrapper paramConverterWrapper)
    {
      this.mVersionCode = paramInt1;
      this.Fg = paramInt2;
      this.Fh = paramBoolean1;
      this.Fi = paramInt3;
      this.Fj = paramBoolean2;
      this.Fk = paramString1;
      this.Fl = paramInt4;
      if (paramString2 == null) {
        this.Fm = null;
      }
      for (this.Fn = null; paramConverterWrapper == null; this.Fn = paramString2)
      {
        this.Fp = null;
        return;
        this.Fm = SafeParcelResponse.class;
      }
      this.Fp = paramConverterWrapper.zzawx();
    }
    
    protected Field(int paramInt1, boolean paramBoolean1, int paramInt2, boolean paramBoolean2, String paramString, int paramInt3, Class<? extends FastJsonResponse> paramClass, FastJsonResponse.zza<I, O> paramzza)
    {
      this.mVersionCode = 1;
      this.Fg = paramInt1;
      this.Fh = paramBoolean1;
      this.Fi = paramInt2;
      this.Fj = paramBoolean2;
      this.Fk = paramString;
      this.Fl = paramInt3;
      this.Fm = paramClass;
      if (paramClass == null) {}
      for (this.Fn = null;; this.Fn = paramClass.getCanonicalName())
      {
        this.Fp = paramzza;
        return;
      }
    }
    
    public static Field zza(String paramString, int paramInt, FastJsonResponse.zza<?, ?> paramzza, boolean paramBoolean)
    {
      return new Field(7, paramBoolean, 0, false, paramString, paramInt, null, paramzza);
    }
    
    public static <T extends FastJsonResponse> Field<T, T> zza(String paramString, int paramInt, Class<T> paramClass)
    {
      return new Field(11, false, 11, false, paramString, paramInt, paramClass, null);
    }
    
    public static <T extends FastJsonResponse> Field<ArrayList<T>, ArrayList<T>> zzb(String paramString, int paramInt, Class<T> paramClass)
    {
      return new Field(11, true, 11, true, paramString, paramInt, paramClass, null);
    }
    
    public static Field<Integer, Integer> zzk(String paramString, int paramInt)
    {
      return new Field(0, false, 0, false, paramString, paramInt, null, null);
    }
    
    public static Field<Boolean, Boolean> zzl(String paramString, int paramInt)
    {
      return new Field(6, false, 6, false, paramString, paramInt, null, null);
    }
    
    public static Field<String, String> zzm(String paramString, int paramInt)
    {
      return new Field(7, false, 7, false, paramString, paramInt, null, null);
    }
    
    public I convertBack(O paramO)
    {
      return (I)this.Fp.convertBack(paramO);
    }
    
    public int getVersionCode()
    {
      return this.mVersionCode;
    }
    
    public String toString()
    {
      zzz.zza localzza = zzz.zzx(this).zzg("versionCode", Integer.valueOf(this.mVersionCode)).zzg("typeIn", Integer.valueOf(this.Fg)).zzg("typeInArray", Boolean.valueOf(this.Fh)).zzg("typeOut", Integer.valueOf(this.Fi)).zzg("typeOutArray", Boolean.valueOf(this.Fj)).zzg("outputFieldName", this.Fk).zzg("safeParcelFieldId", Integer.valueOf(this.Fl)).zzg("concreteTypeName", zzaxh());
      Class localClass = zzaxg();
      if (localClass != null) {
        localzza.zzg("concreteType.class", localClass.getCanonicalName());
      }
      if (this.Fp != null) {
        localzza.zzg("converterName", this.Fp.getClass().getCanonicalName());
      }
      return localzza.toString();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      zza.zza(this, paramParcel, paramInt);
    }
    
    public void zza(FieldMappingDictionary paramFieldMappingDictionary)
    {
      this.Fo = paramFieldMappingDictionary;
    }
    
    public int zzaxa()
    {
      return this.Fg;
    }
    
    public boolean zzaxb()
    {
      return this.Fh;
    }
    
    public int zzaxc()
    {
      return this.Fi;
    }
    
    public boolean zzaxd()
    {
      return this.Fj;
    }
    
    public String zzaxe()
    {
      return this.Fk;
    }
    
    public int zzaxf()
    {
      return this.Fl;
    }
    
    public Class<? extends FastJsonResponse> zzaxg()
    {
      return this.Fm;
    }
    
    String zzaxh()
    {
      if (this.Fn == null) {
        return null;
      }
      return this.Fn;
    }
    
    public boolean zzaxi()
    {
      return this.Fp != null;
    }
    
    ConverterWrapper zzaxj()
    {
      if (this.Fp == null) {
        return null;
      }
      return ConverterWrapper.zza(this.Fp);
    }
    
    public Map<String, Field<?, ?>> zzaxk()
    {
      zzaa.zzy(this.Fn);
      zzaa.zzy(this.Fo);
      return this.Fo.zzig(this.Fn);
    }
  }
  
  public static abstract interface zza<I, O>
  {
    public abstract I convertBack(O paramO);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/server/response/FastJsonResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */