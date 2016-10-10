package com.google.android.gms.common.images;

public final class Size
{
  private final int zzajw;
  private final int zzajx;
  
  public Size(int paramInt1, int paramInt2)
  {
    this.zzajw = paramInt1;
    this.zzajx = paramInt2;
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
      throw zzhp(paramString);
    }
    try
    {
      Size localSize = new Size(Integer.parseInt(paramString.substring(0, i)), Integer.parseInt(paramString.substring(i + 1)));
      return localSize;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw zzhp(paramString);
    }
  }
  
  private static NumberFormatException zzhp(String paramString)
  {
    throw new NumberFormatException(String.valueOf(paramString).length() + 16 + "Invalid Size: \"" + paramString + "\"");
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (paramObject == null) {}
    do
    {
      return false;
      if (this == paramObject) {
        return true;
      }
    } while (!(paramObject instanceof Size));
    paramObject = (Size)paramObject;
    if ((this.zzajw == ((Size)paramObject).zzajw) && (this.zzajx == ((Size)paramObject).zzajx)) {}
    for (;;)
    {
      return bool;
      bool = false;
    }
  }
  
  public int getHeight()
  {
    return this.zzajx;
  }
  
  public int getWidth()
  {
    return this.zzajw;
  }
  
  public int hashCode()
  {
    return this.zzajx ^ (this.zzajw << 16 | this.zzajw >>> 16);
  }
  
  public String toString()
  {
    int i = this.zzajw;
    int j = this.zzajx;
    return 23 + i + "x" + j;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/images/Size.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */