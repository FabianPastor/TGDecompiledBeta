package com.google.android.gms.internal;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public abstract class zzaoh
{
  public Number aQ()
  {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }
  
  public String aR()
  {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }
  
  public boolean aS()
  {
    return this instanceof zzaoe;
  }
  
  public boolean aT()
  {
    return this instanceof zzaok;
  }
  
  public boolean aU()
  {
    return this instanceof zzaon;
  }
  
  public boolean aV()
  {
    return this instanceof zzaoj;
  }
  
  public zzaok aW()
  {
    if (aT()) {
      return (zzaok)this;
    }
    String str = String.valueOf(this);
    throw new IllegalStateException(String.valueOf(str).length() + 19 + "Not a JSON Object: " + str);
  }
  
  public zzaoe aX()
  {
    if (aS()) {
      return (zzaoe)this;
    }
    throw new IllegalStateException("This is not a JSON Array.");
  }
  
  public zzaon aY()
  {
    if (aU()) {
      return (zzaon)this;
    }
    throw new IllegalStateException("This is not a JSON Primitive.");
  }
  
  Boolean aZ()
  {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }
  
  public boolean getAsBoolean()
  {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }
  
  public double getAsDouble()
  {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }
  
  public int getAsInt()
  {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }
  
  public long getAsLong()
  {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }
  
  public String toString()
  {
    try
    {
      Object localObject = new StringWriter();
      zzaqa localzzaqa = new zzaqa((Writer)localObject);
      localzzaqa.setLenient(true);
      zzapi.zzb(this, localzzaqa);
      localObject = ((StringWriter)localObject).toString();
      return (String)localObject;
    }
    catch (IOException localIOException)
    {
      throw new AssertionError(localIOException);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaoh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */