package org.telegram.messenger.exoplayer2.source.dash.manifest;

public final class UtcTimingElement
{
  public final String schemeIdUri;
  public final String value;
  
  public UtcTimingElement(String paramString1, String paramString2)
  {
    this.schemeIdUri = paramString1;
    this.value = paramString2;
  }
  
  public String toString()
  {
    return this.schemeIdUri + ", " + this.value;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/dash/manifest/UtcTimingElement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */