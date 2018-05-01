package org.telegram.messenger.exoplayer2.trackselection;

import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.RendererCapabilities;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.upstream.BandwidthMeter;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

public class DefaultTrackSelector
  extends MappingTrackSelector
{
  private static final float FRACTION_TO_CONSIDER_FULLSCREEN = 0.98F;
  private static final int[] NO_TRACKS = new int[0];
  private static final int WITHIN_RENDERER_CAPABILITIES_BONUS = 1000;
  private final TrackSelection.Factory adaptiveTrackSelectionFactory;
  private final AtomicReference<Parameters> paramsReference;
  
  public DefaultTrackSelector()
  {
    this((TrackSelection.Factory)null);
  }
  
  public DefaultTrackSelector(TrackSelection.Factory paramFactory)
  {
    this.adaptiveTrackSelectionFactory = paramFactory;
    this.paramsReference = new AtomicReference(Parameters.DEFAULT);
  }
  
  public DefaultTrackSelector(BandwidthMeter paramBandwidthMeter)
  {
    this(new AdaptiveTrackSelection.Factory(paramBandwidthMeter));
  }
  
  private static int compareFormatValues(int paramInt1, int paramInt2)
  {
    int i = -1;
    if (paramInt1 == -1)
    {
      paramInt1 = i;
      if (paramInt2 == -1) {
        paramInt1 = 0;
      }
    }
    for (;;)
    {
      return paramInt1;
      if (paramInt2 == -1) {
        paramInt1 = 1;
      } else {
        paramInt1 -= paramInt2;
      }
    }
  }
  
  private static int compareInts(int paramInt1, int paramInt2)
  {
    if (paramInt1 > paramInt2) {
      paramInt1 = 1;
    }
    for (;;)
    {
      return paramInt1;
      if (paramInt2 > paramInt1) {
        paramInt1 = -1;
      } else {
        paramInt1 = 0;
      }
    }
  }
  
  private static void filterAdaptiveVideoTrackCountForMimeType(TrackGroup paramTrackGroup, int[] paramArrayOfInt, int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, List<Integer> paramList)
  {
    for (int i = paramList.size() - 1; i >= 0; i--)
    {
      int j = ((Integer)paramList.get(i)).intValue();
      if (!isSupportedAdaptiveVideoTrack(paramTrackGroup.getFormat(j), paramString, paramArrayOfInt[j], paramInt1, paramInt2, paramInt3, paramInt4)) {
        paramList.remove(i);
      }
    }
  }
  
  protected static boolean formatHasLanguage(Format paramFormat, String paramString)
  {
    if ((paramString != null) && (TextUtils.equals(paramString, Util.normalizeLanguageCode(paramFormat.language)))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  protected static boolean formatHasNoLanguage(Format paramFormat)
  {
    if ((TextUtils.isEmpty(paramFormat.language)) || (formatHasLanguage(paramFormat, "und"))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private static int getAdaptiveAudioTrackCount(TrackGroup paramTrackGroup, int[] paramArrayOfInt, AudioConfigurationTuple paramAudioConfigurationTuple)
  {
    int i = 0;
    int j = 0;
    while (j < paramTrackGroup.length)
    {
      int k = i;
      if (isSupportedAdaptiveAudioTrack(paramTrackGroup.getFormat(j), paramArrayOfInt[j], paramAudioConfigurationTuple)) {
        k = i + 1;
      }
      j++;
      i = k;
    }
    return i;
  }
  
  private static int[] getAdaptiveAudioTracks(TrackGroup paramTrackGroup, int[] paramArrayOfInt, boolean paramBoolean)
  {
    int i = 0;
    Object localObject1 = null;
    HashSet localHashSet = new HashSet();
    int j = 0;
    int k;
    Object localObject3;
    if (j < paramTrackGroup.length)
    {
      localObject2 = paramTrackGroup.getFormat(j);
      k = ((Format)localObject2).channelCount;
      int m = ((Format)localObject2).sampleRate;
      if (paramBoolean) {}
      for (localObject2 = null;; localObject2 = ((Format)localObject2).sampleMimeType)
      {
        localObject3 = new AudioConfigurationTuple(k, m, (String)localObject2);
        localObject2 = localObject1;
        k = i;
        if (localHashSet.add(localObject3))
        {
          m = getAdaptiveAudioTrackCount(paramTrackGroup, paramArrayOfInt, (AudioConfigurationTuple)localObject3);
          localObject2 = localObject1;
          k = i;
          if (m > i)
          {
            localObject2 = localObject3;
            k = m;
          }
        }
        j++;
        localObject1 = localObject2;
        i = k;
        break;
      }
    }
    if (i > 1)
    {
      localObject3 = new int[i];
      i = 0;
      k = 0;
      for (;;)
      {
        localObject2 = localObject3;
        if (k >= paramTrackGroup.length) {
          break;
        }
        j = i;
        if (isSupportedAdaptiveAudioTrack(paramTrackGroup.getFormat(k), paramArrayOfInt[k], (AudioConfigurationTuple)localObject1))
        {
          localObject3[i] = k;
          j = i + 1;
        }
        k++;
        i = j;
      }
    }
    Object localObject2 = NO_TRACKS;
    return (int[])localObject2;
  }
  
  private static int getAdaptiveVideoTrackCountForMimeType(TrackGroup paramTrackGroup, int[] paramArrayOfInt, int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, List<Integer> paramList)
  {
    int i = 0;
    int j = 0;
    while (j < paramList.size())
    {
      int k = ((Integer)paramList.get(j)).intValue();
      int m = i;
      if (isSupportedAdaptiveVideoTrack(paramTrackGroup.getFormat(k), paramString, paramArrayOfInt[k], paramInt1, paramInt2, paramInt3, paramInt4)) {
        m = i + 1;
      }
      j++;
      i = m;
    }
    return i;
  }
  
  private static int[] getAdaptiveVideoTracksForGroup(TrackGroup paramTrackGroup, int[] paramArrayOfInt, boolean paramBoolean1, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean2)
  {
    if (paramTrackGroup.length < 2) {
      paramTrackGroup = NO_TRACKS;
    }
    for (;;)
    {
      return paramTrackGroup;
      List localList = getViewportFilteredTrackIndices(paramTrackGroup, paramInt5, paramInt6, paramBoolean2);
      if (localList.size() < 2)
      {
        paramTrackGroup = NO_TRACKS;
      }
      else
      {
        Object localObject1 = null;
        Object localObject2 = null;
        if (!paramBoolean1)
        {
          HashSet localHashSet = new HashSet();
          paramInt5 = 0;
          int i = 0;
          for (;;)
          {
            localObject1 = localObject2;
            if (i >= localList.size()) {
              break;
            }
            String str = paramTrackGroup.getFormat(((Integer)localList.get(i)).intValue()).sampleMimeType;
            localObject1 = localObject2;
            paramInt6 = paramInt5;
            if (localHashSet.add(str))
            {
              int j = getAdaptiveVideoTrackCountForMimeType(paramTrackGroup, paramArrayOfInt, paramInt1, str, paramInt2, paramInt3, paramInt4, localList);
              localObject1 = localObject2;
              paramInt6 = paramInt5;
              if (j > paramInt5)
              {
                localObject1 = str;
                paramInt6 = j;
              }
            }
            i++;
            localObject2 = localObject1;
            paramInt5 = paramInt6;
          }
        }
        filterAdaptiveVideoTrackCountForMimeType(paramTrackGroup, paramArrayOfInt, paramInt1, (String)localObject1, paramInt2, paramInt3, paramInt4, localList);
        if (localList.size() < 2) {
          paramTrackGroup = NO_TRACKS;
        } else {
          paramTrackGroup = Util.toArray(localList);
        }
      }
    }
  }
  
  private static Point getMaxVideoSizeInViewport(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = 1;
    int j = paramInt1;
    int k = paramInt2;
    int m;
    if (paramBoolean)
    {
      if (paramInt3 <= paramInt4) {
        break label81;
      }
      m = 1;
      if (paramInt1 <= paramInt2) {
        break label87;
      }
      label27:
      j = paramInt1;
      k = paramInt2;
      if (m != i)
      {
        k = paramInt1;
        j = paramInt2;
      }
    }
    if (paramInt3 * k >= paramInt4 * j) {}
    for (Point localPoint = new Point(j, Util.ceilDivide(j * paramInt4, paramInt3));; localPoint = new Point(Util.ceilDivide(k * paramInt3, paramInt4), k))
    {
      return localPoint;
      label81:
      m = 0;
      break;
      label87:
      i = 0;
      break label27;
    }
  }
  
  private static List<Integer> getViewportFilteredTrackIndices(TrackGroup paramTrackGroup, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList(paramTrackGroup.length);
    for (int i = 0; i < paramTrackGroup.length; i++) {
      localArrayList.add(Integer.valueOf(i));
    }
    if ((paramInt1 == Integer.MAX_VALUE) || (paramInt2 == Integer.MAX_VALUE)) {}
    for (;;)
    {
      return localArrayList;
      i = Integer.MAX_VALUE;
      int j = 0;
      while (j < paramTrackGroup.length)
      {
        Format localFormat = paramTrackGroup.getFormat(j);
        int k = i;
        if (localFormat.width > 0)
        {
          k = i;
          if (localFormat.height > 0)
          {
            Point localPoint = getMaxVideoSizeInViewport(paramBoolean, paramInt1, paramInt2, localFormat.width, localFormat.height);
            int m = localFormat.width * localFormat.height;
            k = i;
            if (localFormat.width >= (int)(localPoint.x * 0.98F))
            {
              k = i;
              if (localFormat.height >= (int)(localPoint.y * 0.98F))
              {
                k = i;
                if (m < i) {
                  k = m;
                }
              }
            }
          }
        }
        j++;
        i = k;
      }
      if (i != Integer.MAX_VALUE) {
        for (paramInt1 = localArrayList.size() - 1; paramInt1 >= 0; paramInt1--)
        {
          paramInt2 = paramTrackGroup.getFormat(((Integer)localArrayList.get(paramInt1)).intValue()).getPixelCount();
          if ((paramInt2 == -1) || (paramInt2 > i)) {
            localArrayList.remove(paramInt1);
          }
        }
      }
    }
  }
  
  protected static boolean isSupported(int paramInt, boolean paramBoolean)
  {
    paramInt &= 0x7;
    if ((paramInt == 4) || ((paramBoolean) && (paramInt == 3))) {}
    for (paramBoolean = true;; paramBoolean = false) {
      return paramBoolean;
    }
  }
  
  private static boolean isSupportedAdaptiveAudioTrack(Format paramFormat, int paramInt, AudioConfigurationTuple paramAudioConfigurationTuple)
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    if (isSupported(paramInt, false))
    {
      bool2 = bool1;
      if (paramFormat.channelCount == paramAudioConfigurationTuple.channelCount)
      {
        bool2 = bool1;
        if (paramFormat.sampleRate == paramAudioConfigurationTuple.sampleRate) {
          if (paramAudioConfigurationTuple.mimeType != null)
          {
            bool2 = bool1;
            if (!TextUtils.equals(paramAudioConfigurationTuple.mimeType, paramFormat.sampleMimeType)) {}
          }
          else
          {
            bool2 = true;
          }
        }
      }
    }
    return bool2;
  }
  
  private static boolean isSupportedAdaptiveVideoTrack(Format paramFormat, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    if (isSupported(paramInt1, false))
    {
      bool2 = bool1;
      if ((paramInt1 & paramInt2) != 0) {
        if (paramString != null)
        {
          bool2 = bool1;
          if (!Util.areEqual(paramFormat.sampleMimeType, paramString)) {}
        }
        else if (paramFormat.width != -1)
        {
          bool2 = bool1;
          if (paramFormat.width > paramInt3) {}
        }
        else if (paramFormat.height != -1)
        {
          bool2 = bool1;
          if (paramFormat.height > paramInt4) {}
        }
        else if (paramFormat.bitrate != -1)
        {
          bool2 = bool1;
          if (paramFormat.bitrate > paramInt5) {}
        }
        else
        {
          bool2 = true;
        }
      }
    }
    return bool2;
  }
  
  private static TrackSelection selectAdaptiveVideoTrack(RendererCapabilities paramRendererCapabilities, TrackGroupArray paramTrackGroupArray, int[][] paramArrayOfInt, Parameters paramParameters, TrackSelection.Factory paramFactory)
    throws ExoPlaybackException
  {
    int i;
    boolean bool;
    label33:
    int j;
    label36:
    int[] arrayOfInt;
    if (paramParameters.allowNonSeamlessAdaptiveness)
    {
      i = 24;
      if ((!paramParameters.allowMixedMimeAdaptiveness) || ((paramRendererCapabilities.supportsMixedMimeTypeAdaptation() & i) == 0)) {
        break label116;
      }
      bool = true;
      j = 0;
      if (j >= paramTrackGroupArray.length) {
        break label128;
      }
      paramRendererCapabilities = paramTrackGroupArray.get(j);
      arrayOfInt = getAdaptiveVideoTracksForGroup(paramRendererCapabilities, paramArrayOfInt[j], bool, i, paramParameters.maxVideoWidth, paramParameters.maxVideoHeight, paramParameters.maxVideoBitrate, paramParameters.viewportWidth, paramParameters.viewportHeight, paramParameters.viewportOrientationMayChange);
      if (arrayOfInt.length <= 0) {
        break label122;
      }
    }
    label116:
    label122:
    label128:
    for (paramRendererCapabilities = paramFactory.createTrackSelection(paramRendererCapabilities, arrayOfInt);; paramRendererCapabilities = null)
    {
      return paramRendererCapabilities;
      i = 16;
      break;
      bool = false;
      break label33;
      j++;
      break label36;
    }
  }
  
  private static TrackSelection selectFixedVideoTrack(TrackGroupArray paramTrackGroupArray, int[][] paramArrayOfInt, Parameters paramParameters)
  {
    Object localObject1 = null;
    int i = 0;
    int j = 0;
    int k = -1;
    int m = -1;
    for (int n = 0; n < paramTrackGroupArray.length; n++)
    {
      TrackGroup localTrackGroup = paramTrackGroupArray.get(n);
      List localList = getViewportFilteredTrackIndices(localTrackGroup, paramParameters.viewportWidth, paramParameters.viewportHeight, paramParameters.viewportOrientationMayChange);
      int[] arrayOfInt = paramArrayOfInt[n];
      int i1 = 0;
      if (i1 < localTrackGroup.length)
      {
        int i2 = k;
        Object localObject2 = localObject1;
        int i3 = m;
        int i4 = i;
        int i5 = j;
        Format localFormat;
        if (isSupported(arrayOfInt[i1], paramParameters.exceedRendererCapabilitiesIfNecessary))
        {
          localFormat = localTrackGroup.getFormat(i1);
          if ((!localList.contains(Integer.valueOf(i1))) || ((localFormat.width != -1) && (localFormat.width > paramParameters.maxVideoWidth)) || ((localFormat.height != -1) && (localFormat.height > paramParameters.maxVideoHeight)) || ((localFormat.bitrate != -1) && (localFormat.bitrate > paramParameters.maxVideoBitrate))) {
            break label252;
          }
        }
        label252:
        for (i2 = 1;; i2 = 0)
        {
          if ((i2 != 0) || (paramParameters.exceedVideoConstraintsIfNecessary)) {
            break label258;
          }
          i5 = j;
          i4 = i;
          i3 = m;
          localObject2 = localObject1;
          i2 = k;
          i1++;
          k = i2;
          localObject1 = localObject2;
          m = i3;
          i = i4;
          j = i5;
          break;
        }
        label258:
        label266:
        boolean bool;
        int i7;
        if (i2 != 0)
        {
          i6 = 2;
          bool = isSupported(arrayOfInt[i1], false);
          i7 = i6;
          if (bool) {
            i7 = i6 + 1000;
          }
          if (i7 <= j) {
            break label393;
          }
          i6 = 1;
          label304:
          if (i7 == j)
          {
            if (!paramParameters.forceLowestBitrate) {
              break label405;
            }
            if (compareFormatValues(localFormat.bitrate, k) >= 0) {
              break label399;
            }
          }
        }
        label393:
        label399:
        for (int i6 = 1;; i6 = 0)
        {
          i2 = k;
          localObject2 = localObject1;
          i3 = m;
          i4 = i;
          i5 = j;
          if (i6 == 0) {
            break;
          }
          localObject2 = localTrackGroup;
          i4 = i1;
          i2 = localFormat.bitrate;
          i3 = localFormat.getPixelCount();
          i5 = i7;
          break;
          i6 = 1;
          break label266;
          i6 = 0;
          break label304;
        }
        label405:
        i6 = localFormat.getPixelCount();
        if (i6 != m)
        {
          i6 = compareFormatValues(i6, m);
          label428:
          if ((!bool) || (i2 == 0)) {
            break label470;
          }
          if (i6 <= 0) {
            break label464;
          }
          i6 = 1;
        }
        for (;;)
        {
          break;
          i6 = compareFormatValues(localFormat.bitrate, k);
          break label428;
          label464:
          i6 = 0;
          continue;
          label470:
          if (i6 < 0) {
            i6 = 1;
          } else {
            i6 = 0;
          }
        }
      }
    }
    if (localObject1 == null) {}
    for (paramTrackGroupArray = null;; paramTrackGroupArray = new FixedTrackSelection((TrackGroup)localObject1, i)) {
      return paramTrackGroupArray;
    }
  }
  
  public Parameters getParameters()
  {
    return (Parameters)this.paramsReference.get();
  }
  
  protected TrackSelection selectAudioTrack(TrackGroupArray paramTrackGroupArray, int[][] paramArrayOfInt, Parameters paramParameters, TrackSelection.Factory paramFactory)
    throws ExoPlaybackException
  {
    int i = -1;
    int j = -1;
    Object localObject1 = null;
    for (int k = 0; k < paramTrackGroupArray.length; k++)
    {
      TrackGroup localTrackGroup = paramTrackGroupArray.get(k);
      int[] arrayOfInt = paramArrayOfInt[k];
      int m = 0;
      while (m < localTrackGroup.length)
      {
        int n = j;
        int i1 = i;
        Object localObject2 = localObject1;
        if (isSupported(arrayOfInt[m], paramParameters.exceedRendererCapabilitiesIfNecessary))
        {
          AudioTrackScore localAudioTrackScore = new AudioTrackScore(localTrackGroup.getFormat(m), paramParameters, arrayOfInt[m]);
          if (localObject1 != null)
          {
            n = j;
            i1 = i;
            localObject2 = localObject1;
            if (localAudioTrackScore.compareTo((AudioTrackScore)localObject1) <= 0) {}
          }
          else
          {
            n = k;
            i1 = m;
            localObject2 = localAudioTrackScore;
          }
        }
        m++;
        j = n;
        i = i1;
        localObject1 = localObject2;
      }
    }
    if (j == -1) {
      paramTrackGroupArray = null;
    }
    for (;;)
    {
      return paramTrackGroupArray;
      paramTrackGroupArray = paramTrackGroupArray.get(j);
      if ((!paramParameters.forceLowestBitrate) && (paramFactory != null))
      {
        paramArrayOfInt = getAdaptiveAudioTracks(paramTrackGroupArray, paramArrayOfInt[j], paramParameters.allowMixedMimeAdaptiveness);
        if (paramArrayOfInt.length > 0)
        {
          paramTrackGroupArray = paramFactory.createTrackSelection(paramTrackGroupArray, paramArrayOfInt);
          continue;
        }
      }
      paramTrackGroupArray = new FixedTrackSelection(paramTrackGroupArray, i);
    }
  }
  
  protected TrackSelection selectOtherTrack(int paramInt, TrackGroupArray paramTrackGroupArray, int[][] paramArrayOfInt, Parameters paramParameters)
    throws ExoPlaybackException
  {
    Object localObject1 = null;
    int i = 0;
    int j = 0;
    for (int k = 0; k < paramTrackGroupArray.length; k++)
    {
      TrackGroup localTrackGroup = paramTrackGroupArray.get(k);
      int[] arrayOfInt = paramArrayOfInt[k];
      paramInt = 0;
      if (paramInt < localTrackGroup.length)
      {
        Object localObject2 = localObject1;
        int m = i;
        int n = j;
        int i1;
        if (isSupported(arrayOfInt[paramInt], paramParameters.exceedRendererCapabilitiesIfNecessary))
        {
          if ((localTrackGroup.getFormat(paramInt).selectionFlags & 0x1) == 0) {
            break label169;
          }
          i1 = 1;
          label90:
          if (i1 == 0) {
            break label175;
          }
        }
        label169:
        label175:
        for (n = 2;; n = 1)
        {
          i1 = n;
          if (isSupported(arrayOfInt[paramInt], false)) {
            i1 = n + 1000;
          }
          localObject2 = localObject1;
          m = i;
          n = j;
          if (i1 > j)
          {
            localObject2 = localTrackGroup;
            m = paramInt;
            n = i1;
          }
          paramInt++;
          localObject1 = localObject2;
          i = m;
          j = n;
          break;
          i1 = 0;
          break label90;
        }
      }
    }
    if (localObject1 == null) {}
    for (paramTrackGroupArray = null;; paramTrackGroupArray = new FixedTrackSelection((TrackGroup)localObject1, i)) {
      return paramTrackGroupArray;
    }
  }
  
  protected TrackSelection selectTextTrack(TrackGroupArray paramTrackGroupArray, int[][] paramArrayOfInt, Parameters paramParameters)
    throws ExoPlaybackException
  {
    Object localObject1 = null;
    int i = 0;
    int j = 0;
    for (int k = 0; k < paramTrackGroupArray.length; k++)
    {
      TrackGroup localTrackGroup = paramTrackGroupArray.get(k);
      int[] arrayOfInt = paramArrayOfInt[k];
      int m = 0;
      if (m < localTrackGroup.length)
      {
        Object localObject2 = localObject1;
        int n = i;
        int i1 = j;
        Format localFormat;
        int i2;
        if (isSupported(arrayOfInt[m], paramParameters.exceedRendererCapabilitiesIfNecessary))
        {
          localFormat = localTrackGroup.getFormat(m);
          i2 = localFormat.selectionFlags & (paramParameters.disabledTextTrackSelectionFlags ^ 0xFFFFFFFF);
          if ((i2 & 0x1) == 0) {
            break label246;
          }
          i1 = 1;
          label108:
          if ((i2 & 0x2) == 0) {
            break label252;
          }
          i2 = 1;
          label118:
          boolean bool = formatHasLanguage(localFormat, paramParameters.preferredTextLanguage);
          if ((!bool) && ((!paramParameters.selectUndeterminedTextLanguage) || (!formatHasNoLanguage(localFormat)))) {
            break label282;
          }
          if (i1 == 0) {
            break label258;
          }
          i1 = 8;
          label158:
          if (!bool) {
            break label276;
          }
          i2 = 1;
          label166:
          i1 += i2;
        }
        for (;;)
        {
          label173:
          i2 = i1;
          if (isSupported(arrayOfInt[m], false)) {
            i2 = i1 + 1000;
          }
          localObject2 = localObject1;
          n = i;
          i1 = j;
          if (i2 > j)
          {
            localObject2 = localTrackGroup;
            n = m;
            i1 = i2;
          }
          label246:
          label252:
          label258:
          label276:
          label282:
          do
          {
            m++;
            localObject1 = localObject2;
            i = n;
            j = i1;
            break;
            i1 = 0;
            break label108;
            i2 = 0;
            break label118;
            if (i2 == 0)
            {
              i1 = 6;
              break label158;
            }
            i1 = 4;
            break label158;
            i2 = 0;
            break label166;
            if (i1 != 0)
            {
              i1 = 3;
              break label173;
            }
            localObject2 = localObject1;
            n = i;
            i1 = j;
          } while (i2 == 0);
          if (formatHasLanguage(localFormat, paramParameters.preferredAudioLanguage)) {
            i1 = 2;
          } else {
            i1 = 1;
          }
        }
      }
    }
    if (localObject1 == null) {}
    for (paramTrackGroupArray = null;; paramTrackGroupArray = new FixedTrackSelection((TrackGroup)localObject1, i)) {
      return paramTrackGroupArray;
    }
  }
  
  protected TrackSelection[] selectTracks(RendererCapabilities[] paramArrayOfRendererCapabilities, TrackGroupArray[] paramArrayOfTrackGroupArray, int[][][] paramArrayOfInt)
    throws ExoPlaybackException
  {
    int i = paramArrayOfRendererCapabilities.length;
    TrackSelection[] arrayOfTrackSelection = new TrackSelection[i];
    Parameters localParameters = (Parameters)this.paramsReference.get();
    int j = 0;
    int k = 0;
    int m = 0;
    int i1;
    if (m < i)
    {
      n = j;
      i1 = k;
      if (2 == paramArrayOfRendererCapabilities[m].getTrackType())
      {
        i1 = k;
        if (k == 0)
        {
          arrayOfTrackSelection[m] = selectVideoTrack(paramArrayOfRendererCapabilities[m], paramArrayOfTrackGroupArray[m], paramArrayOfInt[m], localParameters, this.adaptiveTrackSelectionFactory);
          if (arrayOfTrackSelection[m] == null) {
            break label141;
          }
          i1 = 1;
        }
        label107:
        if (paramArrayOfTrackGroupArray[m].length <= 0) {
          break label147;
        }
      }
      label141:
      label147:
      for (k = 1;; k = 0)
      {
        n = j | k;
        m++;
        j = n;
        k = i1;
        break;
        i1 = 0;
        break label107;
      }
    }
    int n = 0;
    m = 0;
    k = 0;
    if (k < i)
    {
      int i2 = n;
      i1 = m;
      switch (paramArrayOfRendererCapabilities[k].getTrackType())
      {
      default: 
        arrayOfTrackSelection[k] = selectOtherTrack(paramArrayOfRendererCapabilities[k].getTrackType(), paramArrayOfTrackGroupArray[k], paramArrayOfInt[k], localParameters);
        i1 = m;
        i2 = n;
      }
      label295:
      label343:
      do
      {
        do
        {
          k++;
          n = i2;
          m = i1;
          break;
          i2 = n;
          i1 = m;
        } while (n != 0);
        TrackGroupArray localTrackGroupArray = paramArrayOfTrackGroupArray[k];
        int[][] arrayOfInt = paramArrayOfInt[k];
        TrackSelection.Factory localFactory;
        if (j != 0)
        {
          localFactory = null;
          arrayOfTrackSelection[k] = selectAudioTrack(localTrackGroupArray, arrayOfInt, localParameters, localFactory);
          if (arrayOfTrackSelection[k] == null) {
            break label343;
          }
        }
        for (i1 = 1;; i1 = 0)
        {
          i2 = i1;
          i1 = m;
          break;
          localFactory = this.adaptiveTrackSelectionFactory;
          break label295;
        }
        i2 = n;
        i1 = m;
      } while (m != 0);
      arrayOfTrackSelection[k] = selectTextTrack(paramArrayOfTrackGroupArray[k], paramArrayOfInt[k], localParameters);
      if (arrayOfTrackSelection[k] != null) {}
      for (i1 = 1;; i1 = 0)
      {
        i2 = n;
        break;
      }
    }
    return arrayOfTrackSelection;
  }
  
  protected TrackSelection selectVideoTrack(RendererCapabilities paramRendererCapabilities, TrackGroupArray paramTrackGroupArray, int[][] paramArrayOfInt, Parameters paramParameters, TrackSelection.Factory paramFactory)
    throws ExoPlaybackException
  {
    Object localObject1 = null;
    Object localObject2 = localObject1;
    if (!paramParameters.forceLowestBitrate)
    {
      localObject2 = localObject1;
      if (paramFactory != null) {
        localObject2 = selectAdaptiveVideoTrack(paramRendererCapabilities, paramTrackGroupArray, paramArrayOfInt, paramParameters, paramFactory);
      }
    }
    paramRendererCapabilities = (RendererCapabilities)localObject2;
    if (localObject2 == null) {
      paramRendererCapabilities = selectFixedVideoTrack(paramTrackGroupArray, paramArrayOfInt, paramParameters);
    }
    return paramRendererCapabilities;
  }
  
  public void setParameters(Parameters paramParameters)
  {
    Assertions.checkNotNull(paramParameters);
    if (!((Parameters)this.paramsReference.getAndSet(paramParameters)).equals(paramParameters)) {
      invalidate();
    }
  }
  
  private static final class AudioConfigurationTuple
  {
    public final int channelCount;
    public final String mimeType;
    public final int sampleRate;
    
    public AudioConfigurationTuple(int paramInt1, int paramInt2, String paramString)
    {
      this.channelCount = paramInt1;
      this.sampleRate = paramInt2;
      this.mimeType = paramString;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (this == paramObject) {}
      for (;;)
      {
        return bool;
        if ((paramObject == null) || (getClass() != paramObject.getClass()))
        {
          bool = false;
        }
        else
        {
          paramObject = (AudioConfigurationTuple)paramObject;
          if ((this.channelCount != ((AudioConfigurationTuple)paramObject).channelCount) || (this.sampleRate != ((AudioConfigurationTuple)paramObject).sampleRate) || (!TextUtils.equals(this.mimeType, ((AudioConfigurationTuple)paramObject).mimeType))) {
            bool = false;
          }
        }
      }
    }
    
    public int hashCode()
    {
      int i = this.channelCount;
      int j = this.sampleRate;
      if (this.mimeType != null) {}
      for (int k = this.mimeType.hashCode();; k = 0) {
        return (i * 31 + j) * 31 + k;
      }
    }
  }
  
  private static final class AudioTrackScore
    implements Comparable<AudioTrackScore>
  {
    private final int bitrate;
    private final int channelCount;
    private final int defaultSelectionFlagScore;
    private final int matchLanguageScore;
    private final DefaultTrackSelector.Parameters parameters;
    private final int sampleRate;
    private final int withinRendererCapabilitiesScore;
    
    public AudioTrackScore(Format paramFormat, DefaultTrackSelector.Parameters paramParameters, int paramInt)
    {
      this.parameters = paramParameters;
      if (DefaultTrackSelector.isSupported(paramInt, false))
      {
        paramInt = 1;
        this.withinRendererCapabilitiesScore = paramInt;
        if (!DefaultTrackSelector.formatHasLanguage(paramFormat, paramParameters.preferredAudioLanguage)) {
          break label92;
        }
        paramInt = 1;
        label40:
        this.matchLanguageScore = paramInt;
        if ((paramFormat.selectionFlags & 0x1) == 0) {
          break label97;
        }
      }
      label92:
      label97:
      for (paramInt = i;; paramInt = 0)
      {
        this.defaultSelectionFlagScore = paramInt;
        this.channelCount = paramFormat.channelCount;
        this.sampleRate = paramFormat.sampleRate;
        this.bitrate = paramFormat.bitrate;
        return;
        paramInt = 0;
        break;
        paramInt = 0;
        break label40;
      }
    }
    
    public int compareTo(AudioTrackScore paramAudioTrackScore)
    {
      int i = 1;
      if (this.withinRendererCapabilitiesScore != paramAudioTrackScore.withinRendererCapabilitiesScore) {
        i = DefaultTrackSelector.compareInts(this.withinRendererCapabilitiesScore, paramAudioTrackScore.withinRendererCapabilitiesScore);
      }
      for (;;)
      {
        return i;
        if (this.matchLanguageScore != paramAudioTrackScore.matchLanguageScore)
        {
          i = DefaultTrackSelector.compareInts(this.matchLanguageScore, paramAudioTrackScore.matchLanguageScore);
        }
        else if (this.defaultSelectionFlagScore != paramAudioTrackScore.defaultSelectionFlagScore)
        {
          i = DefaultTrackSelector.compareInts(this.defaultSelectionFlagScore, paramAudioTrackScore.defaultSelectionFlagScore);
        }
        else if (this.parameters.forceLowestBitrate)
        {
          i = DefaultTrackSelector.compareInts(paramAudioTrackScore.bitrate, this.bitrate);
        }
        else
        {
          if (this.withinRendererCapabilitiesScore == 1) {}
          for (;;)
          {
            if (this.channelCount == paramAudioTrackScore.channelCount) {
              break label145;
            }
            i = DefaultTrackSelector.compareInts(this.channelCount, paramAudioTrackScore.channelCount) * i;
            break;
            i = -1;
          }
          label145:
          if (this.sampleRate != paramAudioTrackScore.sampleRate) {
            i = DefaultTrackSelector.compareInts(this.sampleRate, paramAudioTrackScore.sampleRate) * i;
          } else {
            i = DefaultTrackSelector.compareInts(this.bitrate, paramAudioTrackScore.bitrate) * i;
          }
        }
      }
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (this == paramObject) {}
      for (;;)
      {
        return bool;
        if ((paramObject == null) || (getClass() != paramObject.getClass()))
        {
          bool = false;
        }
        else
        {
          paramObject = (AudioTrackScore)paramObject;
          if ((this.withinRendererCapabilitiesScore != ((AudioTrackScore)paramObject).withinRendererCapabilitiesScore) || (this.matchLanguageScore != ((AudioTrackScore)paramObject).matchLanguageScore) || (this.defaultSelectionFlagScore != ((AudioTrackScore)paramObject).defaultSelectionFlagScore) || (this.channelCount != ((AudioTrackScore)paramObject).channelCount) || (this.sampleRate != ((AudioTrackScore)paramObject).sampleRate) || (this.bitrate != ((AudioTrackScore)paramObject).bitrate)) {
            bool = false;
          }
        }
      }
    }
    
    public int hashCode()
    {
      return ((((this.withinRendererCapabilitiesScore * 31 + this.matchLanguageScore) * 31 + this.defaultSelectionFlagScore) * 31 + this.channelCount) * 31 + this.sampleRate) * 31 + this.bitrate;
    }
  }
  
  public static final class Parameters
  {
    public static final Parameters DEFAULT = new Parameters();
    public final boolean allowMixedMimeAdaptiveness;
    public final boolean allowNonSeamlessAdaptiveness;
    public final int disabledTextTrackSelectionFlags;
    public final boolean exceedRendererCapabilitiesIfNecessary;
    public final boolean exceedVideoConstraintsIfNecessary;
    public final boolean forceLowestBitrate;
    public final int maxVideoBitrate;
    public final int maxVideoHeight;
    public final int maxVideoWidth;
    public final String preferredAudioLanguage;
    public final String preferredTextLanguage;
    public final boolean selectUndeterminedTextLanguage;
    public final int viewportHeight;
    public final boolean viewportOrientationMayChange;
    public final int viewportWidth;
    
    private Parameters()
    {
      this(null, null, false, 0, false, false, true, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true, Integer.MAX_VALUE, Integer.MAX_VALUE, true);
    }
    
    private Parameters(String paramString1, String paramString2, boolean paramBoolean1, int paramInt1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean5, boolean paramBoolean6, int paramInt5, int paramInt6, boolean paramBoolean7)
    {
      this.preferredAudioLanguage = Util.normalizeLanguageCode(paramString1);
      this.preferredTextLanguage = Util.normalizeLanguageCode(paramString2);
      this.selectUndeterminedTextLanguage = paramBoolean1;
      this.disabledTextTrackSelectionFlags = paramInt1;
      this.forceLowestBitrate = paramBoolean2;
      this.allowMixedMimeAdaptiveness = paramBoolean3;
      this.allowNonSeamlessAdaptiveness = paramBoolean4;
      this.maxVideoWidth = paramInt2;
      this.maxVideoHeight = paramInt3;
      this.maxVideoBitrate = paramInt4;
      this.exceedVideoConstraintsIfNecessary = paramBoolean5;
      this.exceedRendererCapabilitiesIfNecessary = paramBoolean6;
      this.viewportWidth = paramInt5;
      this.viewportHeight = paramInt6;
      this.viewportOrientationMayChange = paramBoolean7;
    }
    
    public DefaultTrackSelector.ParametersBuilder buildUpon()
    {
      return new DefaultTrackSelector.ParametersBuilder(this, null);
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (this == paramObject) {}
      for (;;)
      {
        return bool;
        if ((paramObject == null) || (getClass() != paramObject.getClass()))
        {
          bool = false;
        }
        else
        {
          paramObject = (Parameters)paramObject;
          if ((this.selectUndeterminedTextLanguage != ((Parameters)paramObject).selectUndeterminedTextLanguage) || (this.disabledTextTrackSelectionFlags != ((Parameters)paramObject).disabledTextTrackSelectionFlags) || (this.forceLowestBitrate != ((Parameters)paramObject).forceLowestBitrate) || (this.allowMixedMimeAdaptiveness != ((Parameters)paramObject).allowMixedMimeAdaptiveness) || (this.allowNonSeamlessAdaptiveness != ((Parameters)paramObject).allowNonSeamlessAdaptiveness) || (this.maxVideoWidth != ((Parameters)paramObject).maxVideoWidth) || (this.maxVideoHeight != ((Parameters)paramObject).maxVideoHeight) || (this.exceedVideoConstraintsIfNecessary != ((Parameters)paramObject).exceedVideoConstraintsIfNecessary) || (this.exceedRendererCapabilitiesIfNecessary != ((Parameters)paramObject).exceedRendererCapabilitiesIfNecessary) || (this.viewportOrientationMayChange != ((Parameters)paramObject).viewportOrientationMayChange) || (this.viewportWidth != ((Parameters)paramObject).viewportWidth) || (this.viewportHeight != ((Parameters)paramObject).viewportHeight) || (this.maxVideoBitrate != ((Parameters)paramObject).maxVideoBitrate) || (!TextUtils.equals(this.preferredAudioLanguage, ((Parameters)paramObject).preferredAudioLanguage)) || (!TextUtils.equals(this.preferredTextLanguage, ((Parameters)paramObject).preferredTextLanguage))) {
            bool = false;
          }
        }
      }
    }
    
    public int hashCode()
    {
      int i = 1;
      int j;
      int k;
      int m;
      label26:
      int n;
      label36:
      int i1;
      label46:
      int i2;
      int i3;
      int i4;
      label68:
      int i5;
      if (this.selectUndeterminedTextLanguage)
      {
        j = 1;
        k = this.disabledTextTrackSelectionFlags;
        if (!this.forceLowestBitrate) {
          break label190;
        }
        m = 1;
        if (!this.allowMixedMimeAdaptiveness) {
          break label196;
        }
        n = 1;
        if (!this.allowNonSeamlessAdaptiveness) {
          break label202;
        }
        i1 = 1;
        i2 = this.maxVideoWidth;
        i3 = this.maxVideoHeight;
        if (!this.exceedVideoConstraintsIfNecessary) {
          break label208;
        }
        i4 = 1;
        if (!this.exceedRendererCapabilitiesIfNecessary) {
          break label214;
        }
        i5 = 1;
        label78:
        if (!this.viewportOrientationMayChange) {
          break label220;
        }
      }
      for (;;)
      {
        return (((((((((((((j * 31 + k) * 31 + m) * 31 + n) * 31 + i1) * 31 + i2) * 31 + i3) * 31 + i4) * 31 + i5) * 31 + i) * 31 + this.viewportWidth) * 31 + this.viewportHeight) * 31 + this.maxVideoBitrate) * 31 + this.preferredAudioLanguage.hashCode()) * 31 + this.preferredTextLanguage.hashCode();
        j = 0;
        break;
        label190:
        m = 0;
        break label26;
        label196:
        n = 0;
        break label36;
        label202:
        i1 = 0;
        break label46;
        label208:
        i4 = 0;
        break label68;
        label214:
        i5 = 0;
        break label78;
        label220:
        i = 0;
      }
    }
  }
  
  public static final class ParametersBuilder
  {
    private boolean allowMixedMimeAdaptiveness;
    private boolean allowNonSeamlessAdaptiveness;
    private int disabledTextTrackSelectionFlags;
    private boolean exceedRendererCapabilitiesIfNecessary;
    private boolean exceedVideoConstraintsIfNecessary;
    private boolean forceLowestBitrate;
    private int maxVideoBitrate;
    private int maxVideoHeight;
    private int maxVideoWidth;
    private String preferredAudioLanguage;
    private String preferredTextLanguage;
    private boolean selectUndeterminedTextLanguage;
    private int viewportHeight;
    private boolean viewportOrientationMayChange;
    private int viewportWidth;
    
    public ParametersBuilder()
    {
      this(DefaultTrackSelector.Parameters.DEFAULT);
    }
    
    private ParametersBuilder(DefaultTrackSelector.Parameters paramParameters)
    {
      this.preferredAudioLanguage = paramParameters.preferredAudioLanguage;
      this.preferredTextLanguage = paramParameters.preferredTextLanguage;
      this.selectUndeterminedTextLanguage = paramParameters.selectUndeterminedTextLanguage;
      this.disabledTextTrackSelectionFlags = paramParameters.disabledTextTrackSelectionFlags;
      this.forceLowestBitrate = paramParameters.forceLowestBitrate;
      this.allowMixedMimeAdaptiveness = paramParameters.allowMixedMimeAdaptiveness;
      this.allowNonSeamlessAdaptiveness = paramParameters.allowNonSeamlessAdaptiveness;
      this.maxVideoWidth = paramParameters.maxVideoWidth;
      this.maxVideoHeight = paramParameters.maxVideoHeight;
      this.maxVideoBitrate = paramParameters.maxVideoBitrate;
      this.exceedVideoConstraintsIfNecessary = paramParameters.exceedVideoConstraintsIfNecessary;
      this.exceedRendererCapabilitiesIfNecessary = paramParameters.exceedRendererCapabilitiesIfNecessary;
      this.viewportWidth = paramParameters.viewportWidth;
      this.viewportHeight = paramParameters.viewportHeight;
      this.viewportOrientationMayChange = paramParameters.viewportOrientationMayChange;
    }
    
    public DefaultTrackSelector.Parameters build()
    {
      return new DefaultTrackSelector.Parameters(this.preferredAudioLanguage, this.preferredTextLanguage, this.selectUndeterminedTextLanguage, this.disabledTextTrackSelectionFlags, this.forceLowestBitrate, this.allowMixedMimeAdaptiveness, this.allowNonSeamlessAdaptiveness, this.maxVideoWidth, this.maxVideoHeight, this.maxVideoBitrate, this.exceedVideoConstraintsIfNecessary, this.exceedRendererCapabilitiesIfNecessary, this.viewportWidth, this.viewportHeight, this.viewportOrientationMayChange, null);
    }
    
    public ParametersBuilder clearVideoSizeConstraints()
    {
      return setMaxVideoSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    public ParametersBuilder clearViewportSizeConstraints()
    {
      return setViewportSize(Integer.MAX_VALUE, Integer.MAX_VALUE, true);
    }
    
    public ParametersBuilder setAllowMixedMimeAdaptiveness(boolean paramBoolean)
    {
      this.allowMixedMimeAdaptiveness = paramBoolean;
      return this;
    }
    
    public ParametersBuilder setAllowNonSeamlessAdaptiveness(boolean paramBoolean)
    {
      this.allowNonSeamlessAdaptiveness = paramBoolean;
      return this;
    }
    
    public ParametersBuilder setDisabledTextTrackSelectionFlags(int paramInt)
    {
      this.disabledTextTrackSelectionFlags = paramInt;
      return this;
    }
    
    public ParametersBuilder setExceedRendererCapabilitiesIfNecessary(boolean paramBoolean)
    {
      this.exceedRendererCapabilitiesIfNecessary = paramBoolean;
      return this;
    }
    
    public ParametersBuilder setExceedVideoConstraintsIfNecessary(boolean paramBoolean)
    {
      this.exceedVideoConstraintsIfNecessary = paramBoolean;
      return this;
    }
    
    public ParametersBuilder setForceLowestBitrate(boolean paramBoolean)
    {
      this.forceLowestBitrate = paramBoolean;
      return this;
    }
    
    public ParametersBuilder setMaxVideoBitrate(int paramInt)
    {
      this.maxVideoBitrate = paramInt;
      return this;
    }
    
    public ParametersBuilder setMaxVideoSize(int paramInt1, int paramInt2)
    {
      this.maxVideoWidth = paramInt1;
      this.maxVideoHeight = paramInt2;
      return this;
    }
    
    public ParametersBuilder setMaxVideoSizeSd()
    {
      return setMaxVideoSize(1279, 719);
    }
    
    public ParametersBuilder setPreferredAudioLanguage(String paramString)
    {
      this.preferredAudioLanguage = paramString;
      return this;
    }
    
    public ParametersBuilder setPreferredTextLanguage(String paramString)
    {
      this.preferredTextLanguage = paramString;
      return this;
    }
    
    public ParametersBuilder setSelectUndeterminedTextLanguage(boolean paramBoolean)
    {
      this.selectUndeterminedTextLanguage = paramBoolean;
      return this;
    }
    
    public ParametersBuilder setViewportSize(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      this.viewportWidth = paramInt1;
      this.viewportHeight = paramInt2;
      this.viewportOrientationMayChange = paramBoolean;
      return this;
    }
    
    public ParametersBuilder setViewportSizeToPhysicalDisplaySize(Context paramContext, boolean paramBoolean)
    {
      paramContext = Util.getPhysicalDisplaySize(paramContext);
      return setViewportSize(paramContext.x, paramContext.y, paramBoolean);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/trackselection/DefaultTrackSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */