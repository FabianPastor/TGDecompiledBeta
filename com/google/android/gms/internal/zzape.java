package com.google.android.gms.internal;

import java.math.BigInteger;

public final class zzape
  extends zzaoy
{
  private static final Class<?>[] bow = { Integer.TYPE, Long.TYPE, Short.TYPE, Float.TYPE, Double.TYPE, Byte.TYPE, Boolean.TYPE, Character.TYPE, Integer.class, Long.class, Short.class, Float.class, Double.class, Byte.class, Boolean.class, Character.class };
  private Object value;
  
  public zzape(Boolean paramBoolean)
  {
    setValue(paramBoolean);
  }
  
  public zzape(Number paramNumber)
  {
    setValue(paramNumber);
  }
  
  zzape(Object paramObject)
  {
    setValue(paramObject);
  }
  
  public zzape(String paramString)
  {
    setValue(paramString);
  }
  
  private static boolean zza(zzape paramzzape)
  {
    if ((paramzzape.value instanceof Number))
    {
      paramzzape = (Number)paramzzape.value;
      return ((paramzzape instanceof BigInteger)) || ((paramzzape instanceof Long)) || ((paramzzape instanceof Integer)) || ((paramzzape instanceof Short)) || ((paramzzape instanceof Byte));
    }
    return false;
  }
  
  private static boolean zzcm(Object paramObject)
  {
    if ((paramObject instanceof String)) {
      return true;
    }
    paramObject = paramObject.getClass();
    Class[] arrayOfClass = bow;
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
  
  public Number aT()
  {
    if ((this.value instanceof String)) {
      return new zzapv((String)this.value);
    }
    return (Number)this.value;
  }
  
  public String aU()
  {
    if (be()) {
      return aT().toString();
    }
    if (bd()) {
      return bc().toString();
    }
    return (String)this.value;
  }
  
  Boolean bc()
  {
    return (Boolean)this.value;
  }
  
  public boolean bd()
  {
    return this.value instanceof Boolean;
  }
  
  public boolean be()
  {
    return this.value instanceof Number;
  }
  
  public boolean bf()
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
        paramObject = (zzape)paramObject;
        if (this.value != null) {
          break;
        }
      } while (((zzape)paramObject).value == null);
      return false;
      if ((!zza(this)) || (!zza((zzape)paramObject))) {
        break;
      }
    } while (aT().longValue() == ((zzape)paramObject).aT().longValue());
    return false;
    if (((this.value instanceof Number)) && ((((zzape)paramObject).value instanceof Number)))
    {
      double d1 = aT().doubleValue();
      double d2 = ((zzape)paramObject).aT().doubleValue();
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
    return this.value.equals(((zzape)paramObject).value);
  }
  
  public boolean getAsBoolean()
  {
    if (bd()) {
      return bc().booleanValue();
    }
    return Boolean.parseBoolean(aU());
  }
  
  public double getAsDouble()
  {
    if (be()) {
      return aT().doubleValue();
    }
    return Double.parseDouble(aU());
  }
  
  public int getAsInt()
  {
    if (be()) {
      return aT().intValue();
    }
    return Integer.parseInt(aU());
  }
  
  public long getAsLong()
  {
    if (be()) {
      return aT().longValue();
    }
    return Long.parseLong(aU());
  }
  
  public int hashCode()
  {
    if (this.value == null) {
      return 31;
    }
    long l;
    if (zza(this))
    {
      l = aT().longValue();
      return (int)(l ^ l >>> 32);
    }
    if ((this.value instanceof Number))
    {
      l = Double.doubleToLongBits(aT().doubleValue());
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
    if (((paramObject instanceof Number)) || (zzcm(paramObject))) {}
    for (boolean bool = true;; bool = false)
    {
      zzapq.zzbt(bool);
      this.value = paramObject;
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzape.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */