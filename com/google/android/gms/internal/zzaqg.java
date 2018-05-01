package com.google.android.gms.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public final class zzaqg
  extends zzaqr
{
  private static final Writer bpO = new Writer()
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
  private static final zzape bpP = new zzape("closed");
  private final List<zzaoy> bpN = new ArrayList();
  private String bpQ;
  private zzaoy bpR = zzapa.bou;
  
  public zzaqg()
  {
    super(bpO);
  }
  
  private zzaoy bv()
  {
    return (zzaoy)this.bpN.get(this.bpN.size() - 1);
  }
  
  private void zzd(zzaoy paramzzaoy)
  {
    if (this.bpQ != null)
    {
      if ((!paramzzaoy.aY()) || (bN())) {
        ((zzapb)bv()).zza(this.bpQ, paramzzaoy);
      }
      this.bpQ = null;
      return;
    }
    if (this.bpN.isEmpty())
    {
      this.bpR = paramzzaoy;
      return;
    }
    zzaoy localzzaoy = bv();
    if ((localzzaoy instanceof zzaov))
    {
      ((zzaov)localzzaoy).zzc(paramzzaoy);
      return;
    }
    throw new IllegalStateException();
  }
  
  public zzaqr bA()
    throws IOException
  {
    zzd(zzapa.bou);
    return this;
  }
  
  public zzaoy bu()
  {
    if (!this.bpN.isEmpty())
    {
      String str = String.valueOf(this.bpN);
      throw new IllegalStateException(String.valueOf(str).length() + 34 + "Expected one JSON element but was " + str);
    }
    return this.bpR;
  }
  
  public zzaqr bw()
    throws IOException
  {
    zzaov localzzaov = new zzaov();
    zzd(localzzaov);
    this.bpN.add(localzzaov);
    return this;
  }
  
  public zzaqr bx()
    throws IOException
  {
    if ((this.bpN.isEmpty()) || (this.bpQ != null)) {
      throw new IllegalStateException();
    }
    if ((bv() instanceof zzaov))
    {
      this.bpN.remove(this.bpN.size() - 1);
      return this;
    }
    throw new IllegalStateException();
  }
  
  public zzaqr by()
    throws IOException
  {
    zzapb localzzapb = new zzapb();
    zzd(localzzapb);
    this.bpN.add(localzzapb);
    return this;
  }
  
  public zzaqr bz()
    throws IOException
  {
    if ((this.bpN.isEmpty()) || (this.bpQ != null)) {
      throw new IllegalStateException();
    }
    if ((bv() instanceof zzapb))
    {
      this.bpN.remove(this.bpN.size() - 1);
      return this;
    }
    throw new IllegalStateException();
  }
  
  public void close()
    throws IOException
  {
    if (!this.bpN.isEmpty()) {
      throw new IOException("Incomplete document");
    }
    this.bpN.add(bpP);
  }
  
  public void flush()
    throws IOException
  {}
  
  public zzaqr zza(Number paramNumber)
    throws IOException
  {
    if (paramNumber == null) {
      return bA();
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
    zzd(new zzape(paramNumber));
    return this;
  }
  
  public zzaqr zzcs(long paramLong)
    throws IOException
  {
    zzd(new zzape(Long.valueOf(paramLong)));
    return this;
  }
  
  public zzaqr zzdh(boolean paramBoolean)
    throws IOException
  {
    zzd(new zzape(Boolean.valueOf(paramBoolean)));
    return this;
  }
  
  public zzaqr zzus(String paramString)
    throws IOException
  {
    if ((this.bpN.isEmpty()) || (this.bpQ != null)) {
      throw new IllegalStateException();
    }
    if ((bv() instanceof zzapb))
    {
      this.bpQ = paramString;
      return this;
    }
    throw new IllegalStateException();
  }
  
  public zzaqr zzut(String paramString)
    throws IOException
  {
    if (paramString == null) {
      return bA();
    }
    zzd(new zzape(paramString));
    return this;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaqg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */