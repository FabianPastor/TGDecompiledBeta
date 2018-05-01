package org.telegram.messenger.exoplayer2.text.cea;

import android.text.Layout.Alignment;
import org.telegram.messenger.exoplayer2.text.Cue;

final class Cea708Cue
  extends Cue
  implements Comparable<Cea708Cue>
{
  public static final int PRIORITY_UNSET = -1;
  public final int priority;
  
  public Cea708Cue(CharSequence paramCharSequence, Layout.Alignment paramAlignment, float paramFloat1, int paramInt1, int paramInt2, float paramFloat2, int paramInt3, float paramFloat3, boolean paramBoolean, int paramInt4, int paramInt5)
  {
    super(paramCharSequence, paramAlignment, paramFloat1, paramInt1, paramInt2, paramFloat2, paramInt3, paramFloat3, paramBoolean, paramInt4);
    this.priority = paramInt5;
  }
  
  public int compareTo(Cea708Cue paramCea708Cue)
  {
    int i;
    if (paramCea708Cue.priority < this.priority) {
      i = -1;
    }
    for (;;)
    {
      return i;
      if (paramCea708Cue.priority > this.priority) {
        i = 1;
      } else {
        i = 0;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/text/cea/Cea708Cue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */