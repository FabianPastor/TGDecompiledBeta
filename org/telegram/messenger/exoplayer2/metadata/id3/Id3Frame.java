package org.telegram.messenger.exoplayer2.metadata.id3;

import org.telegram.messenger.exoplayer2.metadata.Metadata.Entry;
import org.telegram.messenger.exoplayer2.util.Assertions;

public abstract class Id3Frame
  implements Metadata.Entry
{
  public final String id;
  
  public Id3Frame(String paramString)
  {
    this.id = ((String)Assertions.checkNotNull(paramString));
  }
  
  public int describeContents()
  {
    return 0;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/metadata/id3/Id3Frame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */