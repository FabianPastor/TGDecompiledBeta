package org.telegram.messenger.exoplayer2.trackselection;

import org.telegram.messenger.exoplayer2.RendererConfiguration;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.util.Util;

public final class TrackSelectorResult
{
  public final TrackGroupArray groups;
  public final Object info;
  public final RendererConfiguration[] rendererConfigurations;
  public final boolean[] renderersEnabled;
  public final TrackSelectionArray selections;
  
  public TrackSelectorResult(TrackGroupArray paramTrackGroupArray, boolean[] paramArrayOfBoolean, TrackSelectionArray paramTrackSelectionArray, Object paramObject, RendererConfiguration[] paramArrayOfRendererConfiguration)
  {
    this.groups = paramTrackGroupArray;
    this.renderersEnabled = paramArrayOfBoolean;
    this.selections = paramTrackSelectionArray;
    this.info = paramObject;
    this.rendererConfigurations = paramArrayOfRendererConfiguration;
  }
  
  public boolean isEquivalent(TrackSelectorResult paramTrackSelectorResult)
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    if (paramTrackSelectorResult != null)
    {
      if (paramTrackSelectorResult.selections.length == this.selections.length) {
        break label29;
      }
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      label29:
      for (int i = 0;; i++)
      {
        if (i >= this.selections.length) {
          break label62;
        }
        bool2 = bool1;
        if (!isEquivalent(paramTrackSelectorResult, i)) {
          break;
        }
      }
      label62:
      bool2 = true;
    }
  }
  
  public boolean isEquivalent(TrackSelectorResult paramTrackSelectorResult, int paramInt)
  {
    boolean bool1 = false;
    boolean bool2;
    if (paramTrackSelectorResult == null) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      bool2 = bool1;
      if (this.renderersEnabled[paramInt] == paramTrackSelectorResult.renderersEnabled[paramInt])
      {
        bool2 = bool1;
        if (Util.areEqual(this.selections.get(paramInt), paramTrackSelectorResult.selections.get(paramInt)))
        {
          bool2 = bool1;
          if (Util.areEqual(this.rendererConfigurations[paramInt], paramTrackSelectorResult.rendererConfigurations[paramInt])) {
            bool2 = true;
          }
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/trackselection/TrackSelectorResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */