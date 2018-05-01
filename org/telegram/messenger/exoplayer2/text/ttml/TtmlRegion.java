package org.telegram.messenger.exoplayer2.text.ttml;

final class TtmlRegion
{
  public final String id;
  public final float line;
  public final int lineAnchor;
  public final int lineType;
  public final float position;
  public final float width;
  
  public TtmlRegion(String paramString)
  {
    this(paramString, Float.MIN_VALUE, Float.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Float.MIN_VALUE);
  }
  
  public TtmlRegion(String paramString, float paramFloat1, float paramFloat2, int paramInt1, int paramInt2, float paramFloat3)
  {
    this.id = paramString;
    this.position = paramFloat1;
    this.line = paramFloat2;
    this.lineType = paramInt1;
    this.lineAnchor = paramInt2;
    this.width = paramFloat3;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/text/ttml/TtmlRegion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */