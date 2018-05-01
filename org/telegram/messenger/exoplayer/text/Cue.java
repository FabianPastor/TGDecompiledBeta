package org.telegram.messenger.exoplayer.text;

import android.text.Layout.Alignment;

public class Cue
{
  public static final int ANCHOR_TYPE_END = 2;
  public static final int ANCHOR_TYPE_MIDDLE = 1;
  public static final int ANCHOR_TYPE_START = 0;
  public static final float DIMEN_UNSET = Float.MIN_VALUE;
  public static final int LINE_TYPE_FRACTION = 0;
  public static final int LINE_TYPE_NUMBER = 1;
  public static final int TYPE_UNSET = Integer.MIN_VALUE;
  public final float line;
  public final int lineAnchor;
  public final int lineType;
  public final float position;
  public final int positionAnchor;
  public final float size;
  public final CharSequence text;
  public final Layout.Alignment textAlignment;
  
  public Cue()
  {
    this(null);
  }
  
  public Cue(CharSequence paramCharSequence)
  {
    this(paramCharSequence, null, Float.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Float.MIN_VALUE, Integer.MIN_VALUE, Float.MIN_VALUE);
  }
  
  public Cue(CharSequence paramCharSequence, Layout.Alignment paramAlignment, float paramFloat1, int paramInt1, int paramInt2, float paramFloat2, int paramInt3, float paramFloat3)
  {
    this.text = paramCharSequence;
    this.textAlignment = paramAlignment;
    this.line = paramFloat1;
    this.lineType = paramInt1;
    this.lineAnchor = paramInt2;
    this.position = paramFloat2;
    this.positionAnchor = paramInt3;
    this.size = paramFloat3;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/text/Cue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */