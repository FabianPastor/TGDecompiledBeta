package org.telegram.messenger.exoplayer.metadata.id3;

public final class ApicFrame
  extends Id3Frame
{
  public static final String ID = "APIC";
  public final String description;
  public final String mimeType;
  public final byte[] pictureData;
  public final int pictureType;
  
  public ApicFrame(String paramString1, String paramString2, int paramInt, byte[] paramArrayOfByte)
  {
    super("APIC");
    this.mimeType = paramString1;
    this.description = paramString2;
    this.pictureType = paramInt;
    this.pictureData = paramArrayOfByte;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/metadata/id3/ApicFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */