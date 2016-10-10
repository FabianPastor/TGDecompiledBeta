package com.google.android.gms.common.server.response;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.internal.zzab.zza;
import com.google.android.gms.common.internal.zzac;
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
    if (paramField.zzavq() == 11)
    {
      paramStringBuilder.append(((FastJsonResponse)paramField.zzavz().cast(paramObject)).toString());
      return;
    }
    if (paramField.zzavq() == 7)
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
    Map localMap = zzavs();
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
        switch (localField.zzavr())
        {
        default: 
          if (localField.zzavv()) {
            zza(localStringBuilder, localField, (ArrayList)localObject);
          }
          break;
        case 8: 
          localStringBuilder.append("\"").append(zzc.zzp((byte[])localObject)).append("\"");
          break;
        case 9: 
          localStringBuilder.append("\"").append(zzc.zzq((byte[])localObject)).append("\"");
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
    if (paramField.zzavr() == 11)
    {
      if (paramField.zzavw()) {
        return zzid(paramField.zzavx());
      }
      return zzic(paramField.zzavx());
    }
    return zzib(paramField.zzavx());
  }
  
  public abstract Map<String, Field<?, ?>> zzavs();
  
  public HashMap<String, Object> zzavt()
  {
    return null;
  }
  
  public HashMap<String, Object> zzavu()
  {
    return null;
  }
  
  protected Object zzb(Field paramField)
  {
    String str = paramField.zzavx();
    if (paramField.zzavz() != null)
    {
      boolean bool;
      if (zzia(paramField.zzavx()) == null)
      {
        bool = true;
        zzac.zza(bool, "Concrete field shouldn't be value object: %s", new Object[] { paramField.zzavx() });
        if (!paramField.zzavw()) {
          break label73;
        }
      }
      label73:
      for (paramField = zzavu();; paramField = zzavt())
      {
        if (paramField == null) {
          break label81;
        }
        return paramField.get(str);
        bool = false;
        break;
      }
      try
      {
        label81:
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
    return zzia(paramField.zzavx());
  }
  
  protected abstract Object zzia(String paramString);
  
  protected abstract boolean zzib(String paramString);
  
  protected boolean zzic(String paramString)
  {
    throw new UnsupportedOperationException("Concrete types not supported");
  }
  
  protected boolean zzid(String paramString)
  {
    throw new UnsupportedOperationException("Concrete type arrays not supported");
  }
  
  public static class Field<I, O>
    extends AbstractSafeParcelable
  {
    public static final zza CREATOR = new zza();
    protected final String DA;
    private FieldMappingDictionary DB;
    private FastJsonResponse.zza<I, O> DC;
    protected final int Dt;
    protected final boolean Du;
    protected final int Dv;
    protected final boolean Dw;
    protected final String Dx;
    protected final int Dy;
    protected final Class<? extends FastJsonResponse> Dz;
    private final int mVersionCode;
    
    Field(int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, boolean paramBoolean2, String paramString1, int paramInt4, String paramString2, ConverterWrapper paramConverterWrapper)
    {
      this.mVersionCode = paramInt1;
      this.Dt = paramInt2;
      this.Du = paramBoolean1;
      this.Dv = paramInt3;
      this.Dw = paramBoolean2;
      this.Dx = paramString1;
      this.Dy = paramInt4;
      if (paramString2 == null) {
        this.Dz = null;
      }
      for (this.DA = null; paramConverterWrapper == null; this.DA = paramString2)
      {
        this.DC = null;
        return;
        this.Dz = SafeParcelResponse.class;
      }
      this.DC = paramConverterWrapper.zzavo();
    }
    
    protected Field(int paramInt1, boolean paramBoolean1, int paramInt2, boolean paramBoolean2, String paramString, int paramInt3, Class<? extends FastJsonResponse> paramClass, FastJsonResponse.zza<I, O> paramzza)
    {
      this.mVersionCode = 1;
      this.Dt = paramInt1;
      this.Du = paramBoolean1;
      this.Dv = paramInt2;
      this.Dw = paramBoolean2;
      this.Dx = paramString;
      this.Dy = paramInt3;
      this.Dz = paramClass;
      if (paramClass == null) {}
      for (this.DA = null;; this.DA = paramClass.getCanonicalName())
      {
        this.DC = paramzza;
        return;
      }
    }
    
    public static Field zza(String paramString, int paramInt, FastJsonResponse.zza<?, ?> paramzza, boolean paramBoolean)
    {
      return new Field(paramzza.zzavq(), paramBoolean, paramzza.zzavr(), false, paramString, paramInt, null, paramzza);
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
      return (I)this.DC.convertBack(paramO);
    }
    
    public int getVersionCode()
    {
      return this.mVersionCode;
    }
    
    public String toString()
    {
      zzab.zza localzza = zzab.zzx(this).zzg("versionCode", Integer.valueOf(this.mVersionCode)).zzg("typeIn", Integer.valueOf(this.Dt)).zzg("typeInArray", Boolean.valueOf(this.Du)).zzg("typeOut", Integer.valueOf(this.Dv)).zzg("typeOutArray", Boolean.valueOf(this.Dw)).zzg("outputFieldName", this.Dx).zzg("safeParcelFieldId", Integer.valueOf(this.Dy)).zzg("concreteTypeName", zzawa());
      Class localClass = zzavz();
      if (localClass != null) {
        localzza.zzg("concreteType.class", localClass.getCanonicalName());
      }
      if (this.DC != null) {
        localzza.zzg("converterName", this.DC.getClass().getCanonicalName());
      }
      return localzza.toString();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      zza localzza = CREATOR;
      zza.zza(this, paramParcel, paramInt);
    }
    
    public void zza(FieldMappingDictionary paramFieldMappingDictionary)
    {
      this.DB = paramFieldMappingDictionary;
    }
    
    public int zzavq()
    {
      return this.Dt;
    }
    
    public int zzavr()
    {
      return this.Dv;
    }
    
    public boolean zzavv()
    {
      return this.Du;
    }
    
    public boolean zzavw()
    {
      return this.Dw;
    }
    
    public String zzavx()
    {
      return this.Dx;
    }
    
    public int zzavy()
    {
      return this.Dy;
    }
    
    public Class<? extends FastJsonResponse> zzavz()
    {
      return this.Dz;
    }
    
    String zzawa()
    {
      if (this.DA == null) {
        return null;
      }
      return this.DA;
    }
    
    public boolean zzawb()
    {
      return this.DC != null;
    }
    
    ConverterWrapper zzawc()
    {
      if (this.DC == null) {
        return null;
      }
      return ConverterWrapper.zza(this.DC);
    }
    
    public Map<String, Field<?, ?>> zzawd()
    {
      zzac.zzy(this.DA);
      zzac.zzy(this.DB);
      return this.DB.zzie(this.DA);
    }
  }
  
  public static abstract interface zza<I, O>
  {
    public abstract I convertBack(O paramO);
    
    public abstract int zzavq();
    
    public abstract int zzavr();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/server/response/FastJsonResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */