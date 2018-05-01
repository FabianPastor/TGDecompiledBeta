package org.telegram.messenger.camera;

public final class Size
{
  public final int mHeight;
  public final int mWidth;
  
  public Size(int paramInt1, int paramInt2)
  {
    this.mWidth = paramInt1;
    this.mHeight = paramInt2;
  }
  
  private static NumberFormatException invalidSize(String paramString)
  {
    throw new NumberFormatException("Invalid Size: \"" + paramString + "\"");
  }
  
  public static Size parseSize(String paramString)
    throws NumberFormatException
  {
    int i = paramString.indexOf('*');
    int j = i;
    if (i < 0) {
      j = paramString.indexOf('x');
    }
    if (j < 0) {
      throw invalidSize(paramString);
    }
    try
    {
      Size localSize = new Size(Integer.parseInt(paramString.substring(0, j)), Integer.parseInt(paramString.substring(j + 1)));
      return localSize;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw invalidSize(paramString);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool1 = true;
    boolean bool2 = false;
    if (paramObject == null) {}
    do
    {
      for (;;)
      {
        return bool2;
        if (this != paramObject) {
          break;
        }
        bool2 = true;
      }
    } while (!(paramObject instanceof Size));
    paramObject = (Size)paramObject;
    if ((this.mWidth == ((Size)paramObject).mWidth) && (this.mHeight == ((Size)paramObject).mHeight)) {}
    for (bool2 = bool1;; bool2 = false) {
      break;
    }
  }
  
  public int getHeight()
  {
    return this.mHeight;
  }
  
  public int getWidth()
  {
    return this.mWidth;
  }
  
  public int hashCode()
  {
    return this.mHeight ^ (this.mWidth << 16 | this.mWidth >>> 16);
  }
  
  public String toString()
  {
    return this.mWidth + "x" + this.mHeight;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/camera/Size.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */