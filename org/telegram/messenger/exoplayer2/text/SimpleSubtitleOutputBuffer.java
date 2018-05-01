package org.telegram.messenger.exoplayer2.text;

final class SimpleSubtitleOutputBuffer
  extends SubtitleOutputBuffer
{
  private final SimpleSubtitleDecoder owner;
  
  public SimpleSubtitleOutputBuffer(SimpleSubtitleDecoder paramSimpleSubtitleDecoder)
  {
    this.owner = paramSimpleSubtitleDecoder;
  }
  
  public final void release()
  {
    this.owner.releaseOutputBuffer(this);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/text/SimpleSubtitleOutputBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */