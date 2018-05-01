package org.telegram.messenger.exoplayer2.upstream;

import java.io.IOException;

public final class DataSourceException
  extends IOException
{
  public static final int POSITION_OUT_OF_RANGE = 0;
  public final int reason;
  
  public DataSourceException(int paramInt)
  {
    this.reason = paramInt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/DataSourceException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */