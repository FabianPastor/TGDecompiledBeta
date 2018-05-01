package org.telegram.ui.ActionBar;

import android.content.Context;

public class DarkAlertDialog
  extends AlertDialog
{
  public DarkAlertDialog(Context paramContext, int paramInt)
  {
    super(paramContext, paramInt);
  }
  
  protected int getThemeColor(String paramString)
  {
    int i = -1;
    int j;
    switch (paramString.hashCode())
    {
    default: 
      j = -1;
      label50:
      switch (j)
      {
      }
      break;
    }
    for (i = super.getThemeColor(paramString);; i = -14277082)
    {
      return i;
      if (!paramString.equals("dialogBackground")) {
        break;
      }
      j = 0;
      break label50;
      if (!paramString.equals("dialogTextBlack")) {
        break;
      }
      j = 1;
      break label50;
      if (!paramString.equals("dialogButton")) {
        break;
      }
      j = 2;
      break label50;
      if (!paramString.equals("dialogScrollGlow")) {
        break;
      }
      j = 3;
      break label50;
    }
  }
  
  public static class Builder
    extends AlertDialog.Builder
  {
    public Builder(Context paramContext)
    {
      super();
    }
    
    public Builder(Context paramContext, int paramInt)
    {
      super();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ActionBar/DarkAlertDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */