package com.google.android.gms.common.images;

public final class Size
{
  private final int zzrW;
  private final int zzrX;
  
  public Size(int paramInt1, int paramInt2)
  {
    this.zzrW = paramInt1;
    this.zzrX = paramInt2;
  }
  
  public static Size parseSize(String paramString)
    throws NumberFormatException
  {
    if (paramString == null) {
      throw new IllegalArgumentException("string must not be null");
    }
    int j = paramString.indexOf('*');
    int i = j;
    if (j < 0) {
      i = paramString.indexOf('x');
    }
    if (i < 0) {
      throw zzcy(paramString);
    }
    try
    {
      Size localSize = new Size(Integer.parseInt(paramString.substring(0, i)), Integer.parseInt(paramString.substring(i + 1)));
      return localSize;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw zzcy(paramString);
    }
  }
  
  private static NumberFormatException zzcy(String paramString)
  {
    throw new NumberFormatException(String.valueOf(paramString).length() + 16 + "Invalid Size: \"" + paramString + "\"");
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == null) {}
    do
    {
      do
      {
        return false;
        if (this == paramObject) {
          return true;
        }
      } while (!(paramObject instanceof Size));
      paramObject = (Size)paramObject;
    } while ((this.zzrW != ((Size)paramObject).zzrW) || (this.zzrX != ((Size)paramObject).zzrX));
    return true;
  }
  
  public final int getHeight()
  {
    return this.zzrX;
  }
  
  public final int getWidth()
  {
    return this.zzrW;
  }
  
  public final int hashCode()
  {
    return this.zzrX ^ (this.zzrW << 16 | this.zzrW >>> 16);
  }
  
  public final String toString()
  {
    int i = this.zzrW;
    int j = this.zzrX;
    return 23 + i + "x" + j;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/images/Size.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */