package com.google.android.gms.internal;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public final class zzapo
  extends zzapy
{
  private static final Reader bmu = new Reader()
  {
    public void close()
      throws IOException
    {
      throw new AssertionError();
    }
    
    public int read(char[] paramAnonymousArrayOfChar, int paramAnonymousInt1, int paramAnonymousInt2)
      throws IOException
    {
      throw new AssertionError();
    }
  };
  private static final Object bmv = new Object();
  private final List<Object> bmw = new ArrayList();
  
  public zzapo(zzaoh paramzzaoh)
  {
    super(bmu);
    this.bmw.add(paramzzaoh);
  }
  
  private Object bo()
  {
    return this.bmw.get(this.bmw.size() - 1);
  }
  
  private Object bp()
  {
    return this.bmw.remove(this.bmw.size() - 1);
  }
  
  private void zza(zzapz paramzzapz)
    throws IOException
  {
    if (bn() != paramzzapz)
    {
      paramzzapz = String.valueOf(paramzzapz);
      String str = String.valueOf(bn());
      throw new IllegalStateException(String.valueOf(paramzzapz).length() + 18 + String.valueOf(str).length() + "Expected " + paramzzapz + " but was " + str);
    }
  }
  
  public void beginArray()
    throws IOException
  {
    zza(zzapz.bok);
    zzaoe localzzaoe = (zzaoe)bo();
    this.bmw.add(localzzaoe.iterator());
  }
  
  public void beginObject()
    throws IOException
  {
    zza(zzapz.bom);
    zzaok localzzaok = (zzaok)bo();
    this.bmw.add(localzzaok.entrySet().iterator());
  }
  
  public zzapz bn()
    throws IOException
  {
    if (this.bmw.isEmpty()) {
      return zzapz.bot;
    }
    Object localObject = bo();
    if ((localObject instanceof Iterator))
    {
      boolean bool = this.bmw.get(this.bmw.size() - 2) instanceof zzaok;
      localObject = (Iterator)localObject;
      if (((Iterator)localObject).hasNext())
      {
        if (bool) {
          return zzapz.boo;
        }
        this.bmw.add(((Iterator)localObject).next());
        return bn();
      }
      if (bool) {
        return zzapz.bon;
      }
      return zzapz.bol;
    }
    if ((localObject instanceof zzaok)) {
      return zzapz.bom;
    }
    if ((localObject instanceof zzaoe)) {
      return zzapz.bok;
    }
    if ((localObject instanceof zzaon))
    {
      localObject = (zzaon)localObject;
      if (((zzaon)localObject).bc()) {
        return zzapz.bop;
      }
      if (((zzaon)localObject).ba()) {
        return zzapz.bor;
      }
      if (((zzaon)localObject).bb()) {
        return zzapz.boq;
      }
      throw new AssertionError();
    }
    if ((localObject instanceof zzaoj)) {
      return zzapz.bos;
    }
    if (localObject == bmv) {
      throw new IllegalStateException("JsonReader is closed");
    }
    throw new AssertionError();
  }
  
  public void bq()
    throws IOException
  {
    zza(zzapz.boo);
    Map.Entry localEntry = (Map.Entry)((Iterator)bo()).next();
    this.bmw.add(localEntry.getValue());
    this.bmw.add(new zzaon((String)localEntry.getKey()));
  }
  
  public void close()
    throws IOException
  {
    this.bmw.clear();
    this.bmw.add(bmv);
  }
  
  public void endArray()
    throws IOException
  {
    zza(zzapz.bol);
    bp();
    bp();
  }
  
  public void endObject()
    throws IOException
  {
    zza(zzapz.bon);
    bp();
    bp();
  }
  
  public boolean hasNext()
    throws IOException
  {
    zzapz localzzapz = bn();
    return (localzzapz != zzapz.bon) && (localzzapz != zzapz.bol);
  }
  
  public boolean nextBoolean()
    throws IOException
  {
    zza(zzapz.bor);
    return ((zzaon)bp()).getAsBoolean();
  }
  
  public double nextDouble()
    throws IOException
  {
    Object localObject = bn();
    if ((localObject != zzapz.boq) && (localObject != zzapz.bop))
    {
      String str = String.valueOf(zzapz.boq);
      localObject = String.valueOf(localObject);
      throw new IllegalStateException(String.valueOf(str).length() + 18 + String.valueOf(localObject).length() + "Expected " + str + " but was " + (String)localObject);
    }
    double d = ((zzaon)bo()).getAsDouble();
    if ((!isLenient()) && ((Double.isNaN(d)) || (Double.isInfinite(d)))) {
      throw new NumberFormatException(57 + "JSON forbids NaN and infinities: " + d);
    }
    bp();
    return d;
  }
  
  public int nextInt()
    throws IOException
  {
    Object localObject = bn();
    if ((localObject != zzapz.boq) && (localObject != zzapz.bop))
    {
      String str = String.valueOf(zzapz.boq);
      localObject = String.valueOf(localObject);
      throw new IllegalStateException(String.valueOf(str).length() + 18 + String.valueOf(localObject).length() + "Expected " + str + " but was " + (String)localObject);
    }
    int i = ((zzaon)bo()).getAsInt();
    bp();
    return i;
  }
  
  public long nextLong()
    throws IOException
  {
    Object localObject = bn();
    if ((localObject != zzapz.boq) && (localObject != zzapz.bop))
    {
      String str = String.valueOf(zzapz.boq);
      localObject = String.valueOf(localObject);
      throw new IllegalStateException(String.valueOf(str).length() + 18 + String.valueOf(localObject).length() + "Expected " + str + " but was " + (String)localObject);
    }
    long l = ((zzaon)bo()).getAsLong();
    bp();
    return l;
  }
  
  public String nextName()
    throws IOException
  {
    zza(zzapz.boo);
    Map.Entry localEntry = (Map.Entry)((Iterator)bo()).next();
    this.bmw.add(localEntry.getValue());
    return (String)localEntry.getKey();
  }
  
  public void nextNull()
    throws IOException
  {
    zza(zzapz.bos);
    bp();
  }
  
  public String nextString()
    throws IOException
  {
    Object localObject = bn();
    if ((localObject != zzapz.bop) && (localObject != zzapz.boq))
    {
      String str = String.valueOf(zzapz.bop);
      localObject = String.valueOf(localObject);
      throw new IllegalStateException(String.valueOf(str).length() + 18 + String.valueOf(localObject).length() + "Expected " + str + " but was " + (String)localObject);
    }
    return ((zzaon)bp()).aR();
  }
  
  public void skipValue()
    throws IOException
  {
    if (bn() == zzapz.boo)
    {
      nextName();
      return;
    }
    bp();
  }
  
  public String toString()
  {
    return getClass().getSimpleName();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzapo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */