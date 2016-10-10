package org.telegram.messenger.exoplayer.dash;

import android.content.Context;
import java.io.IOException;
import java.util.List;
import org.telegram.messenger.exoplayer.chunk.VideoFormatSelectorUtil;
import org.telegram.messenger.exoplayer.dash.mpd.AdaptationSet;
import org.telegram.messenger.exoplayer.dash.mpd.MediaPresentationDescription;
import org.telegram.messenger.exoplayer.dash.mpd.Period;
import org.telegram.messenger.exoplayer.util.Util;

public final class DefaultDashTrackSelector
  implements DashTrackSelector
{
  private final int adaptationSetType;
  private final Context context;
  private final boolean filterProtectedHdContent;
  private final boolean filterVideoRepresentations;
  
  private DefaultDashTrackSelector(int paramInt, Context paramContext, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.adaptationSetType = paramInt;
    this.context = paramContext;
    this.filterVideoRepresentations = paramBoolean1;
    this.filterProtectedHdContent = paramBoolean2;
  }
  
  public static DefaultDashTrackSelector newAudioInstance()
  {
    return new DefaultDashTrackSelector(1, null, false, false);
  }
  
  public static DefaultDashTrackSelector newTextInstance()
  {
    return new DefaultDashTrackSelector(2, null, false, false);
  }
  
  public static DefaultDashTrackSelector newVideoInstance(Context paramContext, boolean paramBoolean1, boolean paramBoolean2)
  {
    return new DefaultDashTrackSelector(0, paramContext, paramBoolean1, paramBoolean2);
  }
  
  public void selectTracks(MediaPresentationDescription paramMediaPresentationDescription, int paramInt, DashTrackSelector.Output paramOutput)
    throws IOException
  {
    Period localPeriod = paramMediaPresentationDescription.getPeriod(paramInt);
    int i = 0;
    while (i < localPeriod.adaptationSets.size())
    {
      Object localObject = (AdaptationSet)localPeriod.adaptationSets.get(i);
      if (((AdaptationSet)localObject).type == this.adaptationSetType)
      {
        if (this.adaptationSetType == 0)
        {
          Context localContext;
          List localList;
          boolean bool;
          if (this.filterVideoRepresentations)
          {
            localContext = this.context;
            localList = ((AdaptationSet)localObject).representations;
            if ((this.filterProtectedHdContent) && (((AdaptationSet)localObject).hasContentProtection())) {
              bool = true;
            }
          }
          for (localObject = VideoFormatSelectorUtil.selectVideoFormatsForDefaultDisplay(localContext, localList, null, bool);; localObject = Util.firstIntegersArray(((AdaptationSet)localObject).representations.size()))
          {
            int k = localObject.length;
            if (k > 1) {
              paramOutput.adaptiveTrack(paramMediaPresentationDescription, paramInt, i, (int[])localObject);
            }
            j = 0;
            while (j < k)
            {
              paramOutput.fixedTrack(paramMediaPresentationDescription, paramInt, i, localObject[j]);
              j += 1;
            }
            bool = false;
            break;
          }
        }
        int j = 0;
        while (j < ((AdaptationSet)localObject).representations.size())
        {
          paramOutput.fixedTrack(paramMediaPresentationDescription, paramInt, i, j);
          j += 1;
        }
      }
      i += 1;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/dash/DefaultDashTrackSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */