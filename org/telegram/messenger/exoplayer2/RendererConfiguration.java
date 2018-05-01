package org.telegram.messenger.exoplayer2;

public final class RendererConfiguration
{
  public static final RendererConfiguration DEFAULT = new RendererConfiguration(0);
  public final int tunnelingAudioSessionId;
  
  public RendererConfiguration(int paramInt)
  {
    this.tunnelingAudioSessionId = paramInt;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this == paramObject) {}
    for (;;)
    {
      return bool;
      if ((paramObject == null) || (getClass() != paramObject.getClass()))
      {
        bool = false;
      }
      else
      {
        paramObject = (RendererConfiguration)paramObject;
        if (this.tunnelingAudioSessionId != ((RendererConfiguration)paramObject).tunnelingAudioSessionId) {
          bool = false;
        }
      }
    }
  }
  
  public int hashCode()
  {
    return this.tunnelingAudioSessionId;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/RendererConfiguration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */