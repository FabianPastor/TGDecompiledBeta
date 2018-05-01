package org.telegram.messenger.exoplayer.metadata.id3;

public final class GeobFrame
  extends Id3Frame
{
  public static final String ID = "GEOB";
  public final byte[] data;
  public final String description;
  public final String filename;
  public final String mimeType;
  
  public GeobFrame(String paramString1, String paramString2, String paramString3, byte[] paramArrayOfByte)
  {
    super("GEOB");
    this.mimeType = paramString1;
    this.filename = paramString2;
    this.description = paramString3;
    this.data = paramArrayOfByte;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/metadata/id3/GeobFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */