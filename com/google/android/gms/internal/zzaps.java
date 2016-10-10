package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class zzaps
  implements zzaou
{
  private final zzapb bkM;
  private final zzapc bkV;
  private final zzaoa bkX;
  
  public zzaps(zzapb paramzzapb, zzaoa paramzzaoa, zzapc paramzzapc)
  {
    this.bkM = paramzzapb;
    this.bkX = paramzzaoa;
    this.bkV = paramzzapc;
  }
  
  private zzaot<?> zza(zzaob paramzzaob, Field paramField, zzapx<?> paramzzapx)
  {
    paramField = (zzaov)paramField.getAnnotation(zzaov.class);
    if (paramField != null)
    {
      paramField = zzapn.zza(this.bkM, paramzzaob, paramzzapx, paramField);
      if (paramField != null) {
        return paramField;
      }
    }
    return paramzzaob.zza(paramzzapx);
  }
  
  private zzb zza(final zzaob paramzzaob, final Field paramField, String paramString, final zzapx<?> paramzzapx, boolean paramBoolean1, boolean paramBoolean2)
  {
    new zzb(paramString, paramBoolean1, paramBoolean2)
    {
      final zzaot<?> bmG = zzaps.zza(zzaps.this, paramzzaob, paramField, paramzzapx);
      
      void zza(zzapy paramAnonymouszzapy, Object paramAnonymousObject)
        throws IOException, IllegalAccessException
      {
        paramAnonymouszzapy = this.bmG.zzb(paramAnonymouszzapy);
        if ((paramAnonymouszzapy != null) || (!this.bmK)) {
          paramField.set(paramAnonymousObject, paramAnonymouszzapy);
        }
      }
      
      void zza(zzaqa paramAnonymouszzaqa, Object paramAnonymousObject)
        throws IOException, IllegalAccessException
      {
        paramAnonymousObject = paramField.get(paramAnonymousObject);
        new zzapv(paramzzaob, this.bmG, paramzzapx.bz()).zza(paramAnonymouszzaqa, paramAnonymousObject);
      }
      
      public boolean zzct(Object paramAnonymousObject)
        throws IOException, IllegalAccessException
      {
        if (!this.bmN) {}
        while (paramField.get(paramAnonymousObject) == paramAnonymousObject) {
          return false;
        }
        return true;
      }
    };
  }
  
  static List<String> zza(zzaoa paramzzaoa, Field paramField)
  {
    zzaow localzzaow = (zzaow)paramField.getAnnotation(zzaow.class);
    LinkedList localLinkedList = new LinkedList();
    if (localzzaow == null) {
      localLinkedList.add(paramzzaoa.zzc(paramField));
    }
    for (;;)
    {
      return localLinkedList;
      localLinkedList.add(localzzaow.value());
      paramzzaoa = localzzaow.be();
      int j = paramzzaoa.length;
      int i = 0;
      while (i < j)
      {
        localLinkedList.add(paramzzaoa[i]);
        i += 1;
      }
    }
  }
  
  private Map<String, zzb> zza(zzaob paramzzaob, zzapx<?> paramzzapx, Class<?> paramClass)
  {
    LinkedHashMap localLinkedHashMap = new LinkedHashMap();
    if (paramClass.isInterface()) {
      return localLinkedHashMap;
    }
    Type localType1 = paramzzapx.bz();
    Object localObject1 = paramClass;
    paramClass = paramzzapx;
    label94:
    int j;
    if (localObject1 != Object.class)
    {
      Field[] arrayOfField = ((Class)localObject1).getDeclaredFields();
      int k = arrayOfField.length;
      int i = 0;
      for (;;)
      {
        if (i < k)
        {
          Field localField = arrayOfField[i];
          boolean bool1 = zza(localField, true);
          boolean bool2 = zza(localField, false);
          if ((!bool1) && (!bool2))
          {
            i += 1;
          }
          else
          {
            localField.setAccessible(true);
            Type localType2 = zzapa.zza(paramClass.bz(), (Class)localObject1, localField.getGenericType());
            List localList = zzd(localField);
            paramzzapx = null;
            j = 0;
            label138:
            if (j < localList.size())
            {
              Object localObject2 = (String)localList.get(j);
              if (j != 0) {
                bool1 = false;
              }
              localObject2 = (zzb)localLinkedHashMap.put(localObject2, zza(paramzzaob, localField, (String)localObject2, zzapx.zzl(localType2), bool1, bool2));
              if (paramzzapx != null) {
                break label314;
              }
              paramzzapx = (zzapx<?>)localObject2;
            }
          }
        }
      }
    }
    label314:
    for (;;)
    {
      j += 1;
      break label138;
      if (paramzzapx == null) {
        break label94;
      }
      paramzzaob = String.valueOf(localType1);
      paramzzapx = paramzzapx.name;
      throw new IllegalArgumentException(String.valueOf(paramzzaob).length() + 37 + String.valueOf(paramzzapx).length() + paramzzaob + " declares multiple JSON fields named " + paramzzapx);
      paramClass = zzapx.zzl(zzapa.zza(paramClass.bz(), (Class)localObject1, ((Class)localObject1).getGenericSuperclass()));
      localObject1 = paramClass.by();
      break;
      return localLinkedHashMap;
    }
  }
  
  static boolean zza(Field paramField, boolean paramBoolean, zzapc paramzzapc)
  {
    return (!paramzzapc.zza(paramField.getType(), paramBoolean)) && (!paramzzapc.zza(paramField, paramBoolean));
  }
  
  private List<String> zzd(Field paramField)
  {
    return zza(this.bkX, paramField);
  }
  
  public <T> zzaot<T> zza(zzaob paramzzaob, zzapx<T> paramzzapx)
  {
    Class localClass = paramzzapx.by();
    if (!Object.class.isAssignableFrom(localClass)) {
      return null;
    }
    return new zza(this.bkM.zzb(paramzzapx), zza(paramzzaob, paramzzapx, localClass), null);
  }
  
  public boolean zza(Field paramField, boolean paramBoolean)
  {
    return zza(paramField, paramBoolean, this.bkV);
  }
  
  public static final class zza<T>
    extends zzaot<T>
  {
    private final Map<String, zzaps.zzb> bmM;
    private final zzapg<T> bmt;
    
    private zza(zzapg<T> paramzzapg, Map<String, zzaps.zzb> paramMap)
    {
      this.bmt = paramzzapg;
      this.bmM = paramMap;
    }
    
    public void zza(zzaqa paramzzaqa, T paramT)
      throws IOException
    {
      if (paramT == null)
      {
        paramzzaqa.bx();
        return;
      }
      paramzzaqa.bv();
      try
      {
        Iterator localIterator = this.bmM.values().iterator();
        while (localIterator.hasNext())
        {
          zzaps.zzb localzzb = (zzaps.zzb)localIterator.next();
          if (localzzb.zzct(paramT))
          {
            paramzzaqa.zzus(localzzb.name);
            localzzb.zza(paramzzaqa, paramT);
          }
        }
        paramzzaqa.bw();
      }
      catch (IllegalAccessException paramzzaqa)
      {
        throw new AssertionError();
      }
    }
    
    public T zzb(zzapy paramzzapy)
      throws IOException
    {
      if (paramzzapy.bn() == zzapz.bos)
      {
        paramzzapy.nextNull();
        return null;
      }
      Object localObject1 = this.bmt.bg();
      try
      {
        paramzzapy.beginObject();
        for (;;)
        {
          if (!paramzzapy.hasNext()) {
            break label103;
          }
          localObject2 = paramzzapy.nextName();
          localObject2 = (zzaps.zzb)this.bmM.get(localObject2);
          if ((localObject2 != null) && (((zzaps.zzb)localObject2).bmO)) {
            break;
          }
          paramzzapy.skipValue();
        }
      }
      catch (IllegalStateException paramzzapy)
      {
        for (;;)
        {
          Object localObject2;
          throw new zzaoq(paramzzapy);
          ((zzaps.zzb)localObject2).zza(paramzzapy, localObject1);
        }
      }
      catch (IllegalAccessException paramzzapy)
      {
        throw new AssertionError(paramzzapy);
      }
      label103:
      paramzzapy.endObject();
      return (T)localObject1;
    }
  }
  
  static abstract class zzb
  {
    final boolean bmN;
    final boolean bmO;
    final String name;
    
    protected zzb(String paramString, boolean paramBoolean1, boolean paramBoolean2)
    {
      this.name = paramString;
      this.bmN = paramBoolean1;
      this.bmO = paramBoolean2;
    }
    
    abstract void zza(zzapy paramzzapy, Object paramObject)
      throws IOException, IllegalAccessException;
    
    abstract void zza(zzaqa paramzzaqa, Object paramObject)
      throws IOException, IllegalAccessException;
    
    abstract boolean zzct(Object paramObject)
      throws IOException, IllegalAccessException;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaps.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */