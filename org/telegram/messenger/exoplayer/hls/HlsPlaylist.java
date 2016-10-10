package org.telegram.messenger.exoplayer.hls;

public abstract class HlsPlaylist
{
  public static final int TYPE_MASTER = 0;
  public static final int TYPE_MEDIA = 1;
  public final String baseUri;
  public final int type;
  
  protected HlsPlaylist(String paramString, int paramInt)
  {
    this.baseUri = paramString;
    this.type = paramInt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/hls/HlsPlaylist.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */