package org.telegram.messenger.exoplayer.smoothstreaming;

import android.content.Context;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.telegram.messenger.exoplayer.chunk.VideoFormatSelectorUtil;
import org.telegram.messenger.exoplayer.util.Util;

public final class DefaultSmoothStreamingTrackSelector
  implements SmoothStreamingTrackSelector
{
  private final Context context;
  private final boolean filterProtectedHdContent;
  private final boolean filterVideoRepresentations;
  private final int streamElementType;
  
  private DefaultSmoothStreamingTrackSelector(int paramInt, Context paramContext, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.context = paramContext;
    this.streamElementType = paramInt;
    this.filterVideoRepresentations = paramBoolean1;
    this.filterProtectedHdContent = paramBoolean2;
  }
  
  public static DefaultSmoothStreamingTrackSelector newAudioInstance()
  {
    return new DefaultSmoothStreamingTrackSelector(0, null, false, false);
  }
  
  public static DefaultSmoothStreamingTrackSelector newTextInstance()
  {
    return new DefaultSmoothStreamingTrackSelector(2, null, false, false);
  }
  
  public static DefaultSmoothStreamingTrackSelector newVideoInstance(Context paramContext, boolean paramBoolean1, boolean paramBoolean2)
  {
    return new DefaultSmoothStreamingTrackSelector(1, paramContext, paramBoolean1, paramBoolean2);
  }
  
  public void selectTracks(SmoothStreamingManifest paramSmoothStreamingManifest, SmoothStreamingTrackSelector.Output paramOutput)
    throws IOException
  {
    int i = 0;
    while (i < paramSmoothStreamingManifest.streamElements.length)
    {
      Object localObject2 = paramSmoothStreamingManifest.streamElements[i].tracks;
      if (paramSmoothStreamingManifest.streamElements[i].type == this.streamElementType)
      {
        if (this.streamElementType == 1)
        {
          boolean bool;
          if (this.filterVideoRepresentations)
          {
            localObject1 = this.context;
            localObject2 = Arrays.asList((Object[])localObject2);
            if ((this.filterProtectedHdContent) && (paramSmoothStreamingManifest.protectionElement != null)) {
              bool = true;
            }
          }
          for (Object localObject1 = VideoFormatSelectorUtil.selectVideoFormatsForDefaultDisplay((Context)localObject1, (List)localObject2, null, bool);; localObject1 = Util.firstIntegersArray(localObject2.length))
          {
            int k = localObject1.length;
            if (k > 1) {
              paramOutput.adaptiveTrack(paramSmoothStreamingManifest, i, (int[])localObject1);
            }
            j = 0;
            while (j < k)
            {
              paramOutput.fixedTrack(paramSmoothStreamingManifest, i, localObject1[j]);
              j += 1;
            }
            bool = false;
            break;
          }
        }
        int j = 0;
        while (j < localObject2.length)
        {
          paramOutput.fixedTrack(paramSmoothStreamingManifest, i, j);
          j += 1;
        }
      }
      i += 1;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/smoothstreaming/DefaultSmoothStreamingTrackSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */