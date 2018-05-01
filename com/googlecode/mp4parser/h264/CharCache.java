package com.googlecode.mp4parser.h264;

public class CharCache
{
  private char[] cache;
  private int pos;
  
  public CharCache(int paramInt)
  {
    this.cache = new char[paramInt];
  }
  
  public void append(char paramChar)
  {
    if (this.pos < this.cache.length - 1)
    {
      this.cache[this.pos] = paramChar;
      this.pos += 1;
    }
  }
  
  public void append(String paramString)
  {
    paramString = paramString.toCharArray();
    int i = this.cache.length - this.pos;
    if (paramString.length < i) {
      i = paramString.length;
    }
    for (;;)
    {
      System.arraycopy(paramString, 0, this.cache, this.pos, i);
      this.pos += i;
      return;
    }
  }
  
  public void clear()
  {
    this.pos = 0;
  }
  
  public int length()
  {
    return this.pos;
  }
  
  public String toString()
  {
    return new String(this.cache, 0, this.pos);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/h264/CharCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */