package org.telegram.messenger.exoplayer2.util;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class RepeatModeUtil
{
  public static final int REPEAT_TOGGLE_MODE_ALL = 2;
  public static final int REPEAT_TOGGLE_MODE_NONE = 0;
  public static final int REPEAT_TOGGLE_MODE_ONE = 1;
  
  public static int getNextRepeatMode(int paramInt1, int paramInt2)
  {
    int i = 1;
    if (i <= 2)
    {
      int j = (paramInt1 + i) % 3;
      if (isRepeatModeEnabled(j, paramInt2)) {
        paramInt1 = j;
      }
    }
    for (;;)
    {
      return paramInt1;
      i++;
      break;
    }
  }
  
  public static boolean isRepeatModeEnabled(int paramInt1, int paramInt2)
  {
    boolean bool1 = true;
    boolean bool2 = bool1;
    switch (paramInt1)
    {
    default: 
      bool2 = false;
    }
    for (;;)
    {
      return bool2;
      bool2 = bool1;
      if ((paramInt2 & 0x1) == 0)
      {
        bool2 = false;
        continue;
        bool2 = bool1;
        if ((paramInt2 & 0x2) == 0) {
          bool2 = false;
        }
      }
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface RepeatToggleModes {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/util/RepeatModeUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */