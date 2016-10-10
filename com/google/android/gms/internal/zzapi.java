package com.google.android.gms.internal;

import java.io.EOFException;
import java.io.IOException;
import java.io.Writer;

public final class zzapi
{
  public static Writer zza(Appendable paramAppendable)
  {
    if ((paramAppendable instanceof Writer)) {
      return (Writer)paramAppendable;
    }
    return new zza(paramAppendable, null);
  }
  
  public static void zzb(zzaoh paramzzaoh, zzaqa paramzzaqa)
    throws IOException
  {
    zzapw.bnH.zza(paramzzaqa, paramzzaoh);
  }
  
  public static zzaoh zzh(zzapy paramzzapy)
    throws zzaol
  {
    int i = 1;
    try
    {
      paramzzapy.bn();
      i = 0;
      paramzzapy = (zzaoh)zzapw.bnH.zzb(paramzzapy);
      return paramzzapy;
    }
    catch (EOFException paramzzapy)
    {
      if (i != 0) {
        return zzaoj.bld;
      }
      throw new zzaoq(paramzzapy);
    }
    catch (zzaqb paramzzapy)
    {
      throw new zzaoq(paramzzapy);
    }
    catch (IOException paramzzapy)
    {
      throw new zzaoi(paramzzapy);
    }
    catch (NumberFormatException paramzzapy)
    {
      throw new zzaoq(paramzzapy);
    }
  }
  
  private static final class zza
    extends Writer
  {
    private final Appendable bmi;
    private final zza bmj = new zza();
    
    private zza(Appendable paramAppendable)
    {
      this.bmi = paramAppendable;
    }
    
    public void close() {}
    
    public void flush() {}
    
    public void write(int paramInt)
      throws IOException
    {
      this.bmi.append((char)paramInt);
    }
    
    public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2)
      throws IOException
    {
      this.bmj.bmk = paramArrayOfChar;
      this.bmi.append(this.bmj, paramInt1, paramInt1 + paramInt2);
    }
    
    static class zza
      implements CharSequence
    {
      char[] bmk;
      
      public char charAt(int paramInt)
      {
        return this.bmk[paramInt];
      }
      
      public int length()
      {
        return this.bmk.length;
      }
      
      public CharSequence subSequence(int paramInt1, int paramInt2)
      {
        return new String(this.bmk, paramInt1, paramInt2 - paramInt1);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzapi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */