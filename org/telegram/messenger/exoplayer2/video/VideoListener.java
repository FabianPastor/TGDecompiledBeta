package org.telegram.messenger.exoplayer2.video;

import android.graphics.SurfaceTexture;

public abstract interface VideoListener
{
  public abstract void onRenderedFirstFrame();
  
  public abstract boolean onSurfaceDestroyed(SurfaceTexture paramSurfaceTexture);
  
  public abstract void onSurfaceTextureUpdated(SurfaceTexture paramSurfaceTexture);
  
  public abstract void onVideoSizeChanged(int paramInt1, int paramInt2, int paramInt3, float paramFloat);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/video/VideoListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */