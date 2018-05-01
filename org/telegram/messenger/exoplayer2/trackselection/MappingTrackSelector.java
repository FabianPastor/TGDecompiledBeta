package org.telegram.messenger.exoplayer2.trackselection;

import android.util.SparseArray;
import android.util.SparseBooleanArray;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.RendererCapabilities;
import org.telegram.messenger.exoplayer2.RendererConfiguration;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.util.Util;

public abstract class MappingTrackSelector
  extends TrackSelector
{
  private MappedTrackInfo currentMappedTrackInfo;
  private final SparseBooleanArray rendererDisabledFlags = new SparseBooleanArray();
  private final SparseArray<Map<TrackGroupArray, SelectionOverride>> selectionOverrides = new SparseArray();
  private int tunnelingAudioSessionId = 0;
  
  private boolean[] determineEnabledRenderers(RendererCapabilities[] paramArrayOfRendererCapabilities, TrackSelection[] paramArrayOfTrackSelection)
  {
    boolean[] arrayOfBoolean = new boolean[paramArrayOfTrackSelection.length];
    int i = 0;
    if (i < arrayOfBoolean.length)
    {
      if ((!this.rendererDisabledFlags.get(i)) && ((paramArrayOfRendererCapabilities[i].getTrackType() == 5) || (paramArrayOfTrackSelection[i] != null))) {}
      for (int j = 1;; j = 0)
      {
        arrayOfBoolean[i] = j;
        i++;
        break;
      }
    }
    return arrayOfBoolean;
  }
  
  private static int findRenderer(RendererCapabilities[] paramArrayOfRendererCapabilities, TrackGroup paramTrackGroup)
    throws ExoPlaybackException
  {
    int i = paramArrayOfRendererCapabilities.length;
    int j = 0;
    int k = 0;
    int m;
    label24:
    int i1;
    int i2;
    if (k < paramArrayOfRendererCapabilities.length)
    {
      RendererCapabilities localRendererCapabilities = paramArrayOfRendererCapabilities[k];
      m = 0;
      if (m < paramTrackGroup.length)
      {
        int n = localRendererCapabilities.supportsFormat(paramTrackGroup.getFormat(m)) & 0x7;
        i1 = j;
        i2 = i;
        if (n > j)
        {
          i = k;
          j = n;
          i1 = j;
          i2 = i;
          if (j != 4) {}
        }
      }
    }
    for (;;)
    {
      return i;
      m++;
      j = i1;
      i = i2;
      break label24;
      k++;
      break;
    }
  }
  
  private static int[] getFormatSupport(RendererCapabilities paramRendererCapabilities, TrackGroup paramTrackGroup)
    throws ExoPlaybackException
  {
    int[] arrayOfInt = new int[paramTrackGroup.length];
    for (int i = 0; i < paramTrackGroup.length; i++) {
      arrayOfInt[i] = paramRendererCapabilities.supportsFormat(paramTrackGroup.getFormat(i));
    }
    return arrayOfInt;
  }
  
  private static int[] getMixedMimeTypeAdaptationSupport(RendererCapabilities[] paramArrayOfRendererCapabilities)
    throws ExoPlaybackException
  {
    int[] arrayOfInt = new int[paramArrayOfRendererCapabilities.length];
    for (int i = 0; i < arrayOfInt.length; i++) {
      arrayOfInt[i] = paramArrayOfRendererCapabilities[i].supportsMixedMimeTypeAdaptation();
    }
    return arrayOfInt;
  }
  
  private static void maybeConfigureRenderersForTunneling(RendererCapabilities[] paramArrayOfRendererCapabilities, TrackGroupArray[] paramArrayOfTrackGroupArray, int[][][] paramArrayOfInt, RendererConfiguration[] paramArrayOfRendererConfiguration, TrackSelection[] paramArrayOfTrackSelection, int paramInt)
  {
    if (paramInt == 0) {}
    label119:
    label173:
    label187:
    label210:
    label214:
    for (;;)
    {
      return;
      int i = -1;
      int j = -1;
      int k = 1;
      int m = 0;
      int n = k;
      int i2;
      if (m < paramArrayOfRendererCapabilities.length)
      {
        int i1 = paramArrayOfRendererCapabilities[m].getTrackType();
        TrackSelection localTrackSelection = paramArrayOfTrackSelection[m];
        if (i1 != 1)
        {
          i2 = i;
          n = j;
          if (i1 != 2) {
            break label173;
          }
        }
        i2 = i;
        n = j;
        if (localTrackSelection == null) {
          break label173;
        }
        i2 = i;
        n = j;
        if (!rendererSupportsTunneling(paramArrayOfInt[m], paramArrayOfTrackGroupArray[m], localTrackSelection)) {
          break label173;
        }
        if (i1 != 1) {
          break label187;
        }
        if (i != -1) {
          n = 0;
        }
      }
      else
      {
        if ((i == -1) || (j == -1)) {
          break label210;
        }
      }
      for (m = 1;; m = 0)
      {
        if ((n & m) == 0) {
          break label214;
        }
        paramArrayOfRendererCapabilities = new RendererConfiguration(paramInt);
        paramArrayOfRendererConfiguration[i] = paramArrayOfRendererCapabilities;
        paramArrayOfRendererConfiguration[j] = paramArrayOfRendererCapabilities;
        break;
        i2 = m;
        n = j;
        for (;;)
        {
          m++;
          i = i2;
          j = n;
          break;
          if (j != -1)
          {
            n = 0;
            break label119;
          }
          n = m;
          i2 = i;
        }
      }
    }
  }
  
  private static boolean rendererSupportsTunneling(int[][] paramArrayOfInt, TrackGroupArray paramTrackGroupArray, TrackSelection paramTrackSelection)
  {
    boolean bool1 = false;
    boolean bool2;
    if (paramTrackSelection == null) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      int i = paramTrackGroupArray.indexOf(paramTrackSelection.getTrackGroup());
      for (int j = 0;; j++)
      {
        if (j >= paramTrackSelection.length()) {
          break label68;
        }
        bool2 = bool1;
        if ((paramArrayOfInt[i][paramTrackSelection.getIndexInTrackGroup(j)] & 0x20) != 32) {
          break;
        }
      }
      label68:
      bool2 = true;
    }
  }
  
  public final void clearSelectionOverride(int paramInt, TrackGroupArray paramTrackGroupArray)
  {
    Map localMap = (Map)this.selectionOverrides.get(paramInt);
    if ((localMap == null) || (!localMap.containsKey(paramTrackGroupArray))) {}
    for (;;)
    {
      return;
      localMap.remove(paramTrackGroupArray);
      if (localMap.isEmpty()) {
        this.selectionOverrides.remove(paramInt);
      }
      invalidate();
    }
  }
  
  public final void clearSelectionOverrides()
  {
    if (this.selectionOverrides.size() == 0) {}
    for (;;)
    {
      return;
      this.selectionOverrides.clear();
      invalidate();
    }
  }
  
  public final void clearSelectionOverrides(int paramInt)
  {
    Map localMap = (Map)this.selectionOverrides.get(paramInt);
    if ((localMap == null) || (localMap.isEmpty())) {}
    for (;;)
    {
      return;
      this.selectionOverrides.remove(paramInt);
      invalidate();
    }
  }
  
  public final MappedTrackInfo getCurrentMappedTrackInfo()
  {
    return this.currentMappedTrackInfo;
  }
  
  public final boolean getRendererDisabled(int paramInt)
  {
    return this.rendererDisabledFlags.get(paramInt);
  }
  
  public final SelectionOverride getSelectionOverride(int paramInt, TrackGroupArray paramTrackGroupArray)
  {
    Map localMap = (Map)this.selectionOverrides.get(paramInt);
    if (localMap != null) {}
    for (paramTrackGroupArray = (SelectionOverride)localMap.get(paramTrackGroupArray);; paramTrackGroupArray = null) {
      return paramTrackGroupArray;
    }
  }
  
  public final boolean hasSelectionOverride(int paramInt, TrackGroupArray paramTrackGroupArray)
  {
    Map localMap = (Map)this.selectionOverrides.get(paramInt);
    if ((localMap != null) && (localMap.containsKey(paramTrackGroupArray))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public final void onSelectionActivated(Object paramObject)
  {
    this.currentMappedTrackInfo = ((MappedTrackInfo)paramObject);
  }
  
  public final TrackSelectorResult selectTracks(RendererCapabilities[] paramArrayOfRendererCapabilities, TrackGroupArray paramTrackGroupArray)
    throws ExoPlaybackException
  {
    Object localObject1 = new int[paramArrayOfRendererCapabilities.length + 1];
    Object localObject2 = new TrackGroup[paramArrayOfRendererCapabilities.length + 1][];
    int[][][] arrayOfInt = new int[paramArrayOfRendererCapabilities.length + 1][][];
    for (int i = 0; i < localObject2.length; i++)
    {
      localObject2[i] = new TrackGroup[paramTrackGroupArray.length];
      arrayOfInt[i] = new int[paramTrackGroupArray.length][];
    }
    Object localObject3 = getMixedMimeTypeAdaptationSupport(paramArrayOfRendererCapabilities);
    i = 0;
    int j;
    Object localObject5;
    if (i < paramTrackGroupArray.length)
    {
      localObject4 = paramTrackGroupArray.get(i);
      j = findRenderer(paramArrayOfRendererCapabilities, (TrackGroup)localObject4);
      if (j == paramArrayOfRendererCapabilities.length) {}
      for (localObject5 = new int[((TrackGroup)localObject4).length];; localObject5 = getFormatSupport(paramArrayOfRendererCapabilities[j], (TrackGroup)localObject4))
      {
        int k = localObject1[j];
        localObject2[j][k] = localObject4;
        arrayOfInt[j][k] = localObject5;
        localObject1[j] += 1;
        i++;
        break;
      }
    }
    Object localObject4 = new TrackGroupArray[paramArrayOfRendererCapabilities.length];
    Object localObject6 = new int[paramArrayOfRendererCapabilities.length];
    for (i = 0; i < paramArrayOfRendererCapabilities.length; i++)
    {
      j = localObject1[i];
      localObject4[i] = new TrackGroupArray((TrackGroup[])Arrays.copyOf(localObject2[i], j));
      arrayOfInt[i] = ((int[][])Arrays.copyOf(arrayOfInt[i], j));
      localObject6[i] = paramArrayOfRendererCapabilities[i].getTrackType();
    }
    i = localObject1[paramArrayOfRendererCapabilities.length];
    localObject2 = new TrackGroupArray((TrackGroup[])Arrays.copyOf(localObject2[paramArrayOfRendererCapabilities.length], i));
    localObject1 = selectTracks(paramArrayOfRendererCapabilities, (TrackGroupArray[])localObject4, arrayOfInt);
    i = 0;
    if (i < paramArrayOfRendererCapabilities.length)
    {
      if (this.rendererDisabledFlags.get(i)) {
        localObject1[i] = null;
      }
      do
      {
        i++;
        break;
        localObject5 = localObject4[i];
      } while (!hasSelectionOverride(i, (TrackGroupArray)localObject5));
      localObject7 = (SelectionOverride)((Map)this.selectionOverrides.get(i)).get(localObject5);
      if (localObject7 == null) {}
      for (localObject5 = null;; localObject5 = ((SelectionOverride)localObject7).createTrackSelection((TrackGroupArray)localObject5))
      {
        localObject1[i] = localObject5;
        break;
      }
    }
    Object localObject7 = determineEnabledRenderers(paramArrayOfRendererCapabilities, (TrackSelection[])localObject1);
    localObject6 = new MappedTrackInfo((int[])localObject6, (TrackGroupArray[])localObject4, (int[])localObject3, arrayOfInt, (TrackGroupArray)localObject2);
    localObject3 = new RendererConfiguration[paramArrayOfRendererCapabilities.length];
    i = 0;
    if (i < paramArrayOfRendererCapabilities.length)
    {
      if (localObject7[i] != 0) {}
      for (localObject5 = RendererConfiguration.DEFAULT;; localObject5 = null)
      {
        localObject3[i] = localObject5;
        i++;
        break;
      }
    }
    maybeConfigureRenderersForTunneling(paramArrayOfRendererCapabilities, (TrackGroupArray[])localObject4, arrayOfInt, (RendererConfiguration[])localObject3, (TrackSelection[])localObject1, this.tunnelingAudioSessionId);
    return new TrackSelectorResult(paramTrackGroupArray, (boolean[])localObject7, new TrackSelectionArray((TrackSelection[])localObject1), localObject6, (RendererConfiguration[])localObject3);
  }
  
  protected abstract TrackSelection[] selectTracks(RendererCapabilities[] paramArrayOfRendererCapabilities, TrackGroupArray[] paramArrayOfTrackGroupArray, int[][][] paramArrayOfInt)
    throws ExoPlaybackException;
  
  public final void setRendererDisabled(int paramInt, boolean paramBoolean)
  {
    if (this.rendererDisabledFlags.get(paramInt) == paramBoolean) {}
    for (;;)
    {
      return;
      this.rendererDisabledFlags.put(paramInt, paramBoolean);
      invalidate();
    }
  }
  
  public final void setSelectionOverride(int paramInt, TrackGroupArray paramTrackGroupArray, SelectionOverride paramSelectionOverride)
  {
    Map localMap = (Map)this.selectionOverrides.get(paramInt);
    Object localObject = localMap;
    if (localMap == null)
    {
      localObject = new HashMap();
      this.selectionOverrides.put(paramInt, localObject);
    }
    if ((((Map)localObject).containsKey(paramTrackGroupArray)) && (Util.areEqual(((Map)localObject).get(paramTrackGroupArray), paramSelectionOverride))) {}
    for (;;)
    {
      return;
      ((Map)localObject).put(paramTrackGroupArray, paramSelectionOverride);
      invalidate();
    }
  }
  
  public void setTunnelingAudioSessionId(int paramInt)
  {
    if (this.tunnelingAudioSessionId != paramInt)
    {
      this.tunnelingAudioSessionId = paramInt;
      invalidate();
    }
  }
  
  public static final class MappedTrackInfo
  {
    public static final int RENDERER_SUPPORT_EXCEEDS_CAPABILITIES_TRACKS = 2;
    public static final int RENDERER_SUPPORT_NO_TRACKS = 0;
    public static final int RENDERER_SUPPORT_PLAYABLE_TRACKS = 3;
    public static final int RENDERER_SUPPORT_UNSUPPORTED_TRACKS = 1;
    private final int[][][] formatSupport;
    public final int length;
    private final int[] mixedMimeTypeAdaptiveSupport;
    private final int[] rendererTrackTypes;
    private final TrackGroupArray[] trackGroups;
    private final TrackGroupArray unassociatedTrackGroups;
    
    MappedTrackInfo(int[] paramArrayOfInt1, TrackGroupArray[] paramArrayOfTrackGroupArray, int[] paramArrayOfInt2, int[][][] paramArrayOfInt, TrackGroupArray paramTrackGroupArray)
    {
      this.rendererTrackTypes = paramArrayOfInt1;
      this.trackGroups = paramArrayOfTrackGroupArray;
      this.formatSupport = paramArrayOfInt;
      this.mixedMimeTypeAdaptiveSupport = paramArrayOfInt2;
      this.unassociatedTrackGroups = paramTrackGroupArray;
      this.length = paramArrayOfTrackGroupArray.length;
    }
    
    public int getAdaptiveSupport(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      int i = this.trackGroups[paramInt1].get(paramInt2).length;
      int[] arrayOfInt = new int[i];
      int j = 0;
      int k = 0;
      if (j < i)
      {
        int m = getTrackFormatSupport(paramInt1, paramInt2, j);
        if ((m != 4) && ((!paramBoolean) || (m != 3))) {
          break label97;
        }
        m = k + 1;
        arrayOfInt[k] = j;
        k = m;
      }
      label97:
      for (;;)
      {
        j++;
        break;
        return getAdaptiveSupport(paramInt1, paramInt2, Arrays.copyOf(arrayOfInt, k));
      }
    }
    
    public int getAdaptiveSupport(int paramInt1, int paramInt2, int[] paramArrayOfInt)
    {
      int i = 0;
      int j = 16;
      int k = 0;
      Object localObject = null;
      int m = 0;
      while (m < paramArrayOfInt.length)
      {
        int n = paramArrayOfInt[m];
        String str = this.trackGroups[paramInt1].get(paramInt2).getFormat(n).sampleMimeType;
        if (i == 0)
        {
          localObject = str;
          j = Math.min(j, this.formatSupport[paramInt1][paramInt2][m] & 0x18);
          m++;
          i++;
        }
        else
        {
          if (!Util.areEqual(localObject, str)) {}
          for (n = 1;; n = 0)
          {
            k |= n;
            break;
          }
        }
      }
      paramInt2 = j;
      if (k != 0) {
        paramInt2 = Math.min(j, this.mixedMimeTypeAdaptiveSupport[paramInt1]);
      }
      return paramInt2;
    }
    
    public int getRendererSupport(int paramInt)
    {
      int i = 0;
      int[][] arrayOfInt = this.formatSupport[paramInt];
      int j = 0;
      paramInt = i;
      int k;
      if (j < arrayOfInt.length)
      {
        i = 0;
        while (i < arrayOfInt[j].length) {
          switch (arrayOfInt[j][i] & 0x7)
          {
          default: 
            k = 1;
            paramInt = Math.max(paramInt, k);
            i++;
            break;
          case 4: 
            label67:
            paramInt = 3;
          }
        }
      }
      for (;;)
      {
        return paramInt;
        k = 2;
        break label67;
        j++;
        break;
      }
    }
    
    public int getTrackFormatSupport(int paramInt1, int paramInt2, int paramInt3)
    {
      return this.formatSupport[paramInt1][paramInt2][paramInt3] & 0x7;
    }
    
    public TrackGroupArray getTrackGroups(int paramInt)
    {
      return this.trackGroups[paramInt];
    }
    
    public int getTrackTypeRendererSupport(int paramInt)
    {
      int i = 0;
      int j = 0;
      while (j < this.length)
      {
        int k = i;
        if (this.rendererTrackTypes[j] == paramInt) {
          k = Math.max(i, getRendererSupport(j));
        }
        j++;
        i = k;
      }
      return i;
    }
    
    public TrackGroupArray getUnassociatedTrackGroups()
    {
      return this.unassociatedTrackGroups;
    }
  }
  
  public static final class SelectionOverride
  {
    public final TrackSelection.Factory factory;
    public final int groupIndex;
    public final int length;
    public final int[] tracks;
    
    public SelectionOverride(TrackSelection.Factory paramFactory, int paramInt, int... paramVarArgs)
    {
      this.factory = paramFactory;
      this.groupIndex = paramInt;
      this.tracks = paramVarArgs;
      this.length = paramVarArgs.length;
    }
    
    public boolean containsTrack(int paramInt)
    {
      boolean bool1 = false;
      int[] arrayOfInt = this.tracks;
      int i = arrayOfInt.length;
      for (int j = 0;; j++)
      {
        boolean bool2 = bool1;
        if (j < i)
        {
          if (arrayOfInt[j] == paramInt) {
            bool2 = true;
          }
        }
        else {
          return bool2;
        }
      }
    }
    
    public TrackSelection createTrackSelection(TrackGroupArray paramTrackGroupArray)
    {
      return this.factory.createTrackSelection(paramTrackGroupArray.get(this.groupIndex), this.tracks);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/trackselection/MappingTrackSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */