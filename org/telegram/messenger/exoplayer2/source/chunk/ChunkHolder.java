package org.telegram.messenger.exoplayer2.source.chunk;

public final class ChunkHolder
{
  public Chunk chunk;
  public boolean endOfStream;
  
  public void clear()
  {
    this.chunk = null;
    this.endOfStream = false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/chunk/ChunkHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */