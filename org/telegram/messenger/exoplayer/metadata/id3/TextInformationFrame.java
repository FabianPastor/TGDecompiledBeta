package org.telegram.messenger.exoplayer.metadata.id3;

public final class TextInformationFrame
  extends Id3Frame
{
  public final String description;
  
  public TextInformationFrame(String paramString1, String paramString2)
  {
    super(paramString1);
    this.description = paramString2;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/metadata/id3/TextInformationFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */