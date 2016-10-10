package com.google.android.gms.internal;

import java.math.BigInteger;

public final class zzaon
  extends zzaoh
{
  private static final Class<?>[] blf = { Integer.TYPE, Long.TYPE, Short.TYPE, Float.TYPE, Double.TYPE, Byte.TYPE, Boolean.TYPE, Character.TYPE, Integer.class, Long.class, Short.class, Float.class, Double.class, Byte.class, Boolean.class, Character.class };
  private Object value;
  
  public zzaon(Boolean paramBoolean)
  {
    setValue(paramBoolean);
  }
  
  public zzaon(Number paramNumber)
  {
    setValue(paramNumber);
  }
  
  zzaon(Object paramObject)
  {
    setValue(paramObject);
  }
  
  public zzaon(String paramString)
  {
    setValue(paramString);
  }
  
  private static boolean zza(zzaon paramzzaon)
  {
    if ((paramzzaon.value instanceof Number))
    {
      paramzzaon = (Number)paramzzaon.value;
      return ((paramzzaon instanceof BigInteger)) || ((paramzzaon instanceof Long)) || ((paramzzaon instanceof Integer)) || ((paramzzaon instanceof Short)) || ((paramzzaon instanceof Byte));
    }
    return false;
  }
  
  private static boolean zzcn(Object paramObject)
  {
    if ((paramObject instanceof String)) {
      return true;
    }
    paramObject = paramObject.getClass();
    Class[] arrayOfClass = blf;
    int j = arrayOfClass.length;
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        break label45;
      }
      if (arrayOfClass[i].isAssignableFrom((Class)paramObject)) {
        break;
      }
      i += 1;
    }
    label45:
    return false;
  }
  
  public Number aQ()
  {
    if ((this.value instanceof String)) {
      return new zzape((String)this.value);
    }
    return (Number)this.value;
  }
  
  public String aR()
  {
    if (bb()) {
      return aQ().toString();
    }
    if (ba()) {
      return aZ().toString();
    }
    return (String)this.value;
  }
  
  Boolean aZ()
  {
    return (Boolean)this.value;
  }
  
  public boolean ba()
  {
    return this.value instanceof Boolean;
  }
  
  public boolean bb()
  {
    return this.value instanceof Number;
  }
  
  public boolean bc()
  {
    return this.value instanceof String;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if (this == paramObject) {}
    do
    {
      do
      {
        return true;
        if ((paramObject == null) || (getClass() != paramObject.getClass())) {
          return false;
        }
        paramObject = (zzaon)paramObject;
        if (this.value != null) {
          break;
        }
      } while (((zzaon)paramObject).value == null);
      return false;
      if ((!zza(this)) || (!zza((zzaon)paramObject))) {
        break;
      }
    } while (aQ().longValue() == ((zzaon)paramObject).aQ().longValue());
    return false;
    if (((this.value instanceof Number)) && ((((zzaon)paramObject).value instanceof Number)))
    {
      double d1 = aQ().doubleValue();
      double d2 = ((zzaon)paramObject).aQ().doubleValue();
      boolean bool1;
      if (d1 != d2)
      {
        bool1 = bool2;
        if (Double.isNaN(d1))
        {
          bool1 = bool2;
          if (!Double.isNaN(d2)) {}
        }
      }
      else
      {
        bool1 = true;
      }
      return bool1;
    }
    return this.value.equals(((zzaon)paramObject).value);
  }
  
  public boolean getAsBoolean()
  {
    if (ba()) {
      return aZ().booleanValue();
    }
    return Boolean.parseBoolean(aR());
  }
  
  public double getAsDouble()
  {
    if (bb()) {
      return aQ().doubleValue();
    }
    return Double.parseDouble(aR());
  }
  
  public int getAsInt()
  {
    if (bb()) {
      return aQ().intValue();
    }
    return Integer.parseInt(aR());
  }
  
  public long getAsLong()
  {
    if (bb()) {
      return aQ().longValue();
    }
    return Long.parseLong(aR());
  }
  
  public int hashCode()
  {
    if (this.value == null) {
      return 31;
    }
    long l;
    if (zza(this))
    {
      l = aQ().longValue();
      return (int)(l ^ l >>> 32);
    }
    if ((this.value instanceof Number))
    {
      l = Double.doubleToLongBits(aQ().doubleValue());
      return (int)(l ^ l >>> 32);
    }
    return this.value.hashCode();
  }
  
  void setValue(Object paramObject)
  {
    if ((paramObject instanceof Character))
    {
      this.value = String.valueOf(((Character)paramObject).charValue());
      return;
    }
    if (((paramObject instanceof Number)) || (zzcn(paramObject))) {}
    for (boolean bool = true;; bool = false)
    {
      zzaoz.zzbs(bool);
      this.value = paramObject;
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaon.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */