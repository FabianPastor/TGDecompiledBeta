package org.telegram.messenger.exoplayer.hls;

import android.content.Context;
import android.text.TextUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer.chunk.Format;
import org.telegram.messenger.exoplayer.chunk.VideoFormatSelectorUtil;

public final class DefaultHlsTrackSelector
  implements HlsTrackSelector
{
  private static final int TYPE_AUDIO = 1;
  private static final int TYPE_DEFAULT = 0;
  private static final int TYPE_SUBTITLE = 2;
  private final Context context;
  private final int type;
  
  private DefaultHlsTrackSelector(Context paramContext, int paramInt)
  {
    this.context = paramContext;
    this.type = paramInt;
  }
  
  public static DefaultHlsTrackSelector newAudioInstance()
  {
    return new DefaultHlsTrackSelector(null, 1);
  }
  
  public static DefaultHlsTrackSelector newDefaultInstance(Context paramContext)
  {
    return new DefaultHlsTrackSelector(paramContext, 0);
  }
  
  public static DefaultHlsTrackSelector newSubtitleInstance()
  {
    return new DefaultHlsTrackSelector(null, 2);
  }
  
  private static boolean variantHasExplicitCodecWithPrefix(Variant paramVariant, String paramString)
  {
    paramVariant = paramVariant.format.codecs;
    if (TextUtils.isEmpty(paramVariant)) {}
    for (;;)
    {
      return false;
      paramVariant = paramVariant.split("(\\s*,\\s*)|(\\s*$)");
      int i = 0;
      while (i < paramVariant.length)
      {
        if (paramVariant[i].startsWith(paramString)) {
          return true;
        }
        i += 1;
      }
    }
  }
  
  public void selectTracks(HlsMasterPlaylist paramHlsMasterPlaylist, HlsTrackSelector.Output paramOutput)
    throws IOException
  {
    Object localObject1;
    if ((this.type == 1) || (this.type == 2)) {
      if (this.type == 1) {
        localObject1 = paramHlsMasterPlaylist.audios;
      }
    }
    while ((localObject1 != null) && (!((List)localObject1).isEmpty()))
    {
      int i = 0;
      for (;;)
      {
        if (i < ((List)localObject1).size())
        {
          paramOutput.fixedTrack(paramHlsMasterPlaylist, (Variant)((List)localObject1).get(i));
          i += 1;
          continue;
          localObject1 = paramHlsMasterPlaylist.subtitles;
          break;
          Object localObject2 = new ArrayList();
          localObject1 = VideoFormatSelectorUtil.selectVideoFormatsForDefaultDisplay(this.context, paramHlsMasterPlaylist.variants, null, false);
          i = 0;
          while (i < localObject1.length)
          {
            ((ArrayList)localObject2).add(paramHlsMasterPlaylist.variants.get(localObject1[i]));
            i += 1;
          }
          localObject1 = new ArrayList();
          ArrayList localArrayList = new ArrayList();
          i = 0;
          if (i < ((ArrayList)localObject2).size())
          {
            Variant localVariant = (Variant)((ArrayList)localObject2).get(i);
            if ((localVariant.format.height > 0) || (variantHasExplicitCodecWithPrefix(localVariant, "avc"))) {
              ((ArrayList)localObject1).add(localVariant);
            }
            for (;;)
            {
              i += 1;
              break;
              if (variantHasExplicitCodecWithPrefix(localVariant, "mp4a")) {
                localArrayList.add(localVariant);
              }
            }
          }
          if (!((ArrayList)localObject1).isEmpty()) {}
          for (;;)
          {
            if (((ArrayList)localObject1).size() > 1)
            {
              localObject2 = new Variant[((ArrayList)localObject1).size()];
              ((ArrayList)localObject1).toArray((Object[])localObject2);
              paramOutput.adaptiveTrack(paramHlsMasterPlaylist, (Variant[])localObject2);
            }
            i = 0;
            while (i < ((ArrayList)localObject1).size())
            {
              paramOutput.fixedTrack(paramHlsMasterPlaylist, (Variant)((ArrayList)localObject1).get(i));
              i += 1;
            }
            localObject1 = localObject2;
            if (localArrayList.size() < ((ArrayList)localObject2).size())
            {
              ((ArrayList)localObject2).removeAll(localArrayList);
              localObject1 = localObject2;
            }
          }
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/hls/DefaultHlsTrackSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */