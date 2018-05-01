package com.google.android.gms.internal;

import java.io.EOFException;
import java.io.IOException;
import java.io.Writer;

public final class zzapz
{
  public static Writer zza(Appendable paramAppendable)
  {
    if ((paramAppendable instanceof Writer)) {
      return (Writer)paramAppendable;
    }
    return new zza(paramAppendable, null);
  }
  
  public static void zzb(zzaoy paramzzaoy, zzaqr paramzzaqr)
    throws IOException
  {
    zzaqn.bqY.zza(paramzzaqr, paramzzaoy);
  }
  
  public static zzaoy zzh(zzaqp paramzzaqp)
    throws zzapc
  {
    int i = 1;
    try
    {
      paramzzaqp.bq();
      i = 0;
      paramzzaqp = (zzaoy)zzaqn.bqY.zzb(paramzzaqp);
      return paramzzaqp;
    }
    catch (EOFException paramzzaqp)
    {
      if (i != 0) {
        return zzapa.bou;
      }
      throw new zzaph(paramzzaqp);
    }
    catch (zzaqs paramzzaqp)
    {
      throw new zzaph(paramzzaqp);
    }
    catch (IOException paramzzaqp)
    {
      throw new zzaoz(paramzzaqp);
    }
    catch (NumberFormatException paramzzaqp)
    {
      throw new zzaph(paramzzaqp);
    }
  }
  
  private static final class zza
    extends Writer
  {
    private final zza bpA = new zza();
    private final Appendable bpz;
    
    private zza(Appendable paramAppendable)
    {
      this.bpz = paramAppendable;
    }
    
    public void close() {}
    
    public void flush() {}
    
    public void write(int paramInt)
      throws IOException
    {
      this.bpz.append((char)paramInt);
    }
    
    public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2)
      throws IOException
    {
      this.bpA.bpB = paramArrayOfChar;
      this.bpz.append(this.bpA, paramInt1, paramInt1 + paramInt2);
    }
    
    static class zza
      implements CharSequence
    {
      char[] bpB;
      
      public char charAt(int paramInt)
      {
        return this.bpB[paramInt];
      }
      
      public int length()
      {
        return this.bpB.length;
      }
      
      public CharSequence subSequence(int paramInt1, int paramInt2)
      {
        return new String(this.bpB, paramInt1, paramInt2 - paramInt1);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzapz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */