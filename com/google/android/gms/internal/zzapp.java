package com.google.android.gms.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public final class zzapp
  extends zzaqa
{
  private static final Writer bmx = new Writer()
  {
    public void close()
      throws IOException
    {
      throw new AssertionError();
    }
    
    public void flush()
      throws IOException
    {
      throw new AssertionError();
    }
    
    public void write(char[] paramAnonymousArrayOfChar, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      throw new AssertionError();
    }
  };
  private static final zzaon bmy = new zzaon("closed");
  private zzaoh bmA = zzaoj.bld;
  private final List<zzaoh> bmw = new ArrayList();
  private String bmz;
  
  public zzapp()
  {
    super(bmx);
  }
  
  private zzaoh bs()
  {
    return (zzaoh)this.bmw.get(this.bmw.size() - 1);
  }
  
  private void zzd(zzaoh paramzzaoh)
  {
    if (this.bmz != null)
    {
      if ((!paramzzaoh.aV()) || (bK())) {
        ((zzaok)bs()).zza(this.bmz, paramzzaoh);
      }
      this.bmz = null;
      return;
    }
    if (this.bmw.isEmpty())
    {
      this.bmA = paramzzaoh;
      return;
    }
    zzaoh localzzaoh = bs();
    if ((localzzaoh instanceof zzaoe))
    {
      ((zzaoe)localzzaoh).zzc(paramzzaoh);
      return;
    }
    throw new IllegalStateException();
  }
  
  public zzaoh br()
  {
    if (!this.bmw.isEmpty())
    {
      String str = String.valueOf(this.bmw);
      throw new IllegalStateException(String.valueOf(str).length() + 34 + "Expected one JSON element but was " + str);
    }
    return this.bmA;
  }
  
  public zzaqa bt()
    throws IOException
  {
    zzaoe localzzaoe = new zzaoe();
    zzd(localzzaoe);
    this.bmw.add(localzzaoe);
    return this;
  }
  
  public zzaqa bu()
    throws IOException
  {
    if ((this.bmw.isEmpty()) || (this.bmz != null)) {
      throw new IllegalStateException();
    }
    if ((bs() instanceof zzaoe))
    {
      this.bmw.remove(this.bmw.size() - 1);
      return this;
    }
    throw new IllegalStateException();
  }
  
  public zzaqa bv()
    throws IOException
  {
    zzaok localzzaok = new zzaok();
    zzd(localzzaok);
    this.bmw.add(localzzaok);
    return this;
  }
  
  public zzaqa bw()
    throws IOException
  {
    if ((this.bmw.isEmpty()) || (this.bmz != null)) {
      throw new IllegalStateException();
    }
    if ((bs() instanceof zzaok))
    {
      this.bmw.remove(this.bmw.size() - 1);
      return this;
    }
    throw new IllegalStateException();
  }
  
  public zzaqa bx()
    throws IOException
  {
    zzd(zzaoj.bld);
    return this;
  }
  
  public void close()
    throws IOException
  {
    if (!this.bmw.isEmpty()) {
      throw new IOException("Incomplete document");
    }
    this.bmw.add(bmy);
  }
  
  public void flush()
    throws IOException
  {}
  
  public zzaqa zza(Number paramNumber)
    throws IOException
  {
    if (paramNumber == null) {
      return bx();
    }
    if (!isLenient())
    {
      double d = paramNumber.doubleValue();
      if ((Double.isNaN(d)) || (Double.isInfinite(d)))
      {
        paramNumber = String.valueOf(paramNumber);
        throw new IllegalArgumentException(String.valueOf(paramNumber).length() + 33 + "JSON forbids NaN and infinities: " + paramNumber);
      }
    }
    zzd(new zzaon(paramNumber));
    return this;
  }
  
  public zzaqa zzcu(long paramLong)
    throws IOException
  {
    zzd(new zzaon(Long.valueOf(paramLong)));
    return this;
  }
  
  public zzaqa zzdf(boolean paramBoolean)
    throws IOException
  {
    zzd(new zzaon(Boolean.valueOf(paramBoolean)));
    return this;
  }
  
  public zzaqa zzus(String paramString)
    throws IOException
  {
    if ((this.bmw.isEmpty()) || (this.bmz != null)) {
      throw new IllegalStateException();
    }
    if ((bs() instanceof zzaok))
    {
      this.bmz = paramString;
      return this;
    }
    throw new IllegalStateException();
  }
  
  public zzaqa zzut(String paramString)
    throws IOException
  {
    if (paramString == null) {
      return bx();
    }
    zzd(new zzaon(paramString));
    return this;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzapp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */