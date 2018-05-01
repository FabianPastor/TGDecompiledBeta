package org.telegram.messenger.exoplayer2.source.hls;

import java.io.IOException;

public final class SampleQueueMappingException
  extends IOException
{
  public SampleQueueMappingException(String paramString)
  {
    super("Unable to bind a sample queue to TrackGroup with mime type " + paramString + ".");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/hls/SampleQueueMappingException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */