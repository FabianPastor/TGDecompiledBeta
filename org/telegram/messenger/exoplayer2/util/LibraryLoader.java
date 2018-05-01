package org.telegram.messenger.exoplayer2.util;

public final class LibraryLoader
{
  private boolean isAvailable;
  private boolean loadAttempted;
  private String[] nativeLibraries;
  
  public LibraryLoader(String... paramVarArgs)
  {
    this.nativeLibraries = paramVarArgs;
  }
  
  public boolean isAvailable()
  {
    for (;;)
    {
      String[] arrayOfString;
      int i;
      int j;
      try
      {
        if (this.loadAttempted)
        {
          bool = this.isAvailable;
          return bool;
        }
        this.loadAttempted = true;
      }
      finally {}
      try
      {
        arrayOfString = this.nativeLibraries;
        i = arrayOfString.length;
        j = 0;
        if (j < i)
        {
          System.loadLibrary(arrayOfString[j]);
          j++;
          continue;
        }
        this.isAvailable = true;
      }
      catch (UnsatisfiedLinkError localUnsatisfiedLinkError)
      {
        continue;
      }
      boolean bool = this.isAvailable;
    }
  }
  
  /* Error */
  public void setLibraries(String... paramVarArgs)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 22	org/telegram/messenger/exoplayer2/util/LibraryLoader:loadAttempted	Z
    //   6: ifne +19 -> 25
    //   9: iconst_1
    //   10: istore_2
    //   11: iload_2
    //   12: ldc 33
    //   14: invokestatic 39	org/telegram/messenger/exoplayer2/util/Assertions:checkState	(ZLjava/lang/Object;)V
    //   17: aload_0
    //   18: aload_1
    //   19: putfield 16	org/telegram/messenger/exoplayer2/util/LibraryLoader:nativeLibraries	[Ljava/lang/String;
    //   22: aload_0
    //   23: monitorexit
    //   24: return
    //   25: iconst_0
    //   26: istore_2
    //   27: goto -16 -> 11
    //   30: astore_1
    //   31: aload_0
    //   32: monitorexit
    //   33: aload_1
    //   34: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	35	0	this	LibraryLoader
    //   0	35	1	paramVarArgs	String[]
    //   10	17	2	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   2	9	30	finally
    //   11	22	30	finally
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/util/LibraryLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */