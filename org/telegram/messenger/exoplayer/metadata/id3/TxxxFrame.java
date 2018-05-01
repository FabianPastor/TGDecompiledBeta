package org.telegram.messenger.exoplayer.metadata.id3;

public final class TxxxFrame
  extends Id3Frame
{
  public static final String ID = "TXXX";
  public final String description;
  public final String value;
  
  public TxxxFrame(String paramString1, String paramString2)
  {
    super("TXXX");
    this.description = paramString1;
    this.value = paramString2;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/metadata/id3/TxxxFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */