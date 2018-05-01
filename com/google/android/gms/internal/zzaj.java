package com.google.android.gms.internal;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

final class zzaj
  extends FilterInputStream
{
  private int zzaz = 0;
  
  private zzaj(InputStream paramInputStream)
  {
    super(paramInputStream);
  }
  
  public final int read()
    throws IOException
  {
    int i = super.read();
    if (i != -1) {
      this.zzaz += 1;
    }
    return i;
  }
  
  public final int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    paramInt1 = super.read(paramArrayOfByte, paramInt1, paramInt2);
    if (paramInt1 != -1) {
      this.zzaz += paramInt1;
    }
    return paramInt1;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */