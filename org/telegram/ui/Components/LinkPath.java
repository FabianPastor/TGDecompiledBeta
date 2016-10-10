package org.telegram.ui.Components;

import android.graphics.Path;
import android.graphics.Path.Direction;
import android.text.StaticLayout;

public class LinkPath
  extends Path
{
  private StaticLayout currentLayout;
  private int currentLine;
  private float heightOffset;
  private float lastTop = -1.0F;
  
  public void addRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, Path.Direction paramDirection)
  {
    float f3 = paramFloat2 + this.heightOffset;
    float f4 = this.heightOffset;
    if (this.lastTop == -1.0F) {
      this.lastTop = f3;
    }
    float f2;
    float f1;
    for (;;)
    {
      f2 = this.currentLayout.getLineRight(this.currentLine);
      f1 = this.currentLayout.getLineLeft(this.currentLine);
      if (paramFloat1 < f2) {
        break;
      }
      return;
      if (this.lastTop != f3)
      {
        this.lastTop = f3;
        this.currentLine += 1;
      }
    }
    paramFloat2 = paramFloat3;
    if (paramFloat3 > f2) {
      paramFloat2 = f2;
    }
    paramFloat3 = paramFloat1;
    if (paramFloat1 < f1) {
      paramFloat3 = f1;
    }
    super.addRect(paramFloat3, f3, paramFloat2, paramFloat4 + f4, paramDirection);
  }
  
  public void setCurrentLayout(StaticLayout paramStaticLayout, int paramInt, float paramFloat)
  {
    this.currentLayout = paramStaticLayout;
    this.currentLine = paramStaticLayout.getLineForOffset(paramInt);
    this.lastTop = -1.0F;
    this.heightOffset = paramFloat;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/LinkPath.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */