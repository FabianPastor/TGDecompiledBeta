package com.google.android.gms.common.util;

public final class zzl
{
  public static String zza(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0) || (paramInt2 <= 0) || (paramInt2 > paramArrayOfByte.length)) {
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder((paramInt2 + 16 - 1) / 16 * 57);
    int i = 0;
    int j = paramInt2;
    paramInt1 = 0;
    if (j > 0)
    {
      if (paramInt1 == 0) {
        if (paramInt2 < 65536) {
          localStringBuilder.append(String.format("%04X:", new Object[] { Integer.valueOf(i) }));
        }
      }
      for (;;)
      {
        localStringBuilder.append(String.format(" %02X", new Object[] { Integer.valueOf(paramArrayOfByte[i] & 0xFF) }));
        j -= 1;
        paramInt1 += 1;
        if ((paramInt1 == 16) || (j == 0))
        {
          localStringBuilder.append('\n');
          paramInt1 = 0;
        }
        i += 1;
        break;
        localStringBuilder.append(String.format("%08X:", new Object[] { Integer.valueOf(i) }));
        continue;
        if (paramInt1 == 8) {
          localStringBuilder.append(" -");
        }
      }
    }
    return localStringBuilder.toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/zzl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */