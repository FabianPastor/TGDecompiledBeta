package org.telegram.messenger.audioinfo.m4a;

import java.io.IOException;
import java.io.InputStream;
import org.telegram.messenger.audioinfo.util.PositionInputStream;

public final class MP4Input
  extends MP4Box<PositionInputStream>
{
  public MP4Input(InputStream paramInputStream)
  {
    super(new PositionInputStream(paramInputStream), null, "");
  }
  
  public MP4Atom nextChildUpTo(String paramString)
    throws IOException
  {
    MP4Atom localMP4Atom;
    do
    {
      localMP4Atom = nextChild();
    } while (!localMP4Atom.getType().matches(paramString));
    return localMP4Atom;
  }
  
  public String toString()
  {
    return "mp4[pos=" + getPosition() + "]";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/audioinfo/m4a/MP4Input.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */