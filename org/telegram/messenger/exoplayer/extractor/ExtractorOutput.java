package org.telegram.messenger.exoplayer.extractor;

import org.telegram.messenger.exoplayer.drm.DrmInitData;

public abstract interface ExtractorOutput
{
  public abstract void drmInitData(DrmInitData paramDrmInitData);
  
  public abstract void endTracks();
  
  public abstract void seekMap(SeekMap paramSeekMap);
  
  public abstract TrackOutput track(int paramInt);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/ExtractorOutput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */