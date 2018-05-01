package org.telegram.messenger.exoplayer2.source.ads;

import android.net.Uri;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class AdPlaybackState
{
  public static final int AD_STATE_AVAILABLE = 1;
  public static final int AD_STATE_ERROR = 4;
  public static final int AD_STATE_PLAYED = 3;
  public static final int AD_STATE_SKIPPED = 2;
  public static final int AD_STATE_UNAVAILABLE = 0;
  public static final AdPlaybackState NONE = new AdPlaybackState(new long[0]);
  public final int adGroupCount;
  public final long[] adGroupTimesUs;
  public final AdGroup[] adGroups;
  public final long adResumePositionUs;
  public final long contentDurationUs;
  
  public AdPlaybackState(long[] paramArrayOfLong)
  {
    int i = paramArrayOfLong.length;
    this.adGroupCount = i;
    this.adGroupTimesUs = Arrays.copyOf(paramArrayOfLong, i);
    this.adGroups = new AdGroup[i];
    for (int j = 0; j < i; j++) {
      this.adGroups[j] = new AdGroup();
    }
    this.adResumePositionUs = 0L;
    this.contentDurationUs = -9223372036854775807L;
  }
  
  private AdPlaybackState(long[] paramArrayOfLong, AdGroup[] paramArrayOfAdGroup, long paramLong1, long paramLong2)
  {
    this.adGroupCount = paramArrayOfAdGroup.length;
    this.adGroupTimesUs = paramArrayOfLong;
    this.adGroups = paramArrayOfAdGroup;
    this.adResumePositionUs = paramLong1;
    this.contentDurationUs = paramLong2;
  }
  
  public AdPlaybackState withAdCount(int paramInt1, int paramInt2)
  {
    boolean bool;
    if (paramInt2 > 0)
    {
      bool = true;
      Assertions.checkArgument(bool);
      if (this.adGroups[paramInt1].count != paramInt2) {
        break label34;
      }
    }
    for (Object localObject = this;; localObject = new AdPlaybackState(this.adGroupTimesUs, (AdGroup[])localObject, this.adResumePositionUs, this.contentDurationUs))
    {
      return (AdPlaybackState)localObject;
      bool = false;
      break;
      label34:
      localObject = (AdGroup[])Arrays.copyOf(this.adGroups, this.adGroups.length);
      localObject[paramInt1] = this.adGroups[paramInt1].withAdCount(paramInt2);
    }
  }
  
  public AdPlaybackState withAdDurationsUs(long[][] paramArrayOfLong)
  {
    AdGroup[] arrayOfAdGroup = (AdGroup[])Arrays.copyOf(this.adGroups, this.adGroups.length);
    for (int i = 0; i < this.adGroupCount; i++) {
      arrayOfAdGroup[i] = arrayOfAdGroup[i].withAdDurationsUs(paramArrayOfLong[i]);
    }
    return new AdPlaybackState(this.adGroupTimesUs, arrayOfAdGroup, this.adResumePositionUs, this.contentDurationUs);
  }
  
  public AdPlaybackState withAdLoadError(int paramInt1, int paramInt2)
  {
    AdGroup[] arrayOfAdGroup = (AdGroup[])Arrays.copyOf(this.adGroups, this.adGroups.length);
    arrayOfAdGroup[paramInt1] = arrayOfAdGroup[paramInt1].withAdState(4, paramInt2);
    return new AdPlaybackState(this.adGroupTimesUs, arrayOfAdGroup, this.adResumePositionUs, this.contentDurationUs);
  }
  
  public AdPlaybackState withAdResumePositionUs(long paramLong)
  {
    if (this.adResumePositionUs == paramLong) {}
    for (AdPlaybackState localAdPlaybackState = this;; localAdPlaybackState = new AdPlaybackState(this.adGroupTimesUs, this.adGroups, paramLong, this.contentDurationUs)) {
      return localAdPlaybackState;
    }
  }
  
  public AdPlaybackState withAdUri(int paramInt1, int paramInt2, Uri paramUri)
  {
    AdGroup[] arrayOfAdGroup = (AdGroup[])Arrays.copyOf(this.adGroups, this.adGroups.length);
    arrayOfAdGroup[paramInt1] = arrayOfAdGroup[paramInt1].withAdUri(paramUri, paramInt2);
    return new AdPlaybackState(this.adGroupTimesUs, arrayOfAdGroup, this.adResumePositionUs, this.contentDurationUs);
  }
  
  public AdPlaybackState withContentDurationUs(long paramLong)
  {
    if (this.contentDurationUs == paramLong) {}
    for (AdPlaybackState localAdPlaybackState = this;; localAdPlaybackState = new AdPlaybackState(this.adGroupTimesUs, this.adGroups, this.adResumePositionUs, paramLong)) {
      return localAdPlaybackState;
    }
  }
  
  public AdPlaybackState withPlayedAd(int paramInt1, int paramInt2)
  {
    AdGroup[] arrayOfAdGroup = (AdGroup[])Arrays.copyOf(this.adGroups, this.adGroups.length);
    arrayOfAdGroup[paramInt1] = arrayOfAdGroup[paramInt1].withAdState(3, paramInt2);
    return new AdPlaybackState(this.adGroupTimesUs, arrayOfAdGroup, this.adResumePositionUs, this.contentDurationUs);
  }
  
  public AdPlaybackState withSkippedAdGroup(int paramInt)
  {
    AdGroup[] arrayOfAdGroup = (AdGroup[])Arrays.copyOf(this.adGroups, this.adGroups.length);
    arrayOfAdGroup[paramInt] = arrayOfAdGroup[paramInt].withAllAdsSkipped();
    return new AdPlaybackState(this.adGroupTimesUs, arrayOfAdGroup, this.adResumePositionUs, this.contentDurationUs);
  }
  
  public static final class AdGroup
  {
    public final int count;
    public final long[] durationsUs;
    public final int nextAdIndexToPlay;
    public final int[] states;
    public final Uri[] uris;
    
    public AdGroup()
    {
      this(-1, new int[0], new Uri[0], new long[0]);
    }
    
    private AdGroup(int paramInt, int[] paramArrayOfInt, Uri[] paramArrayOfUri, long[] paramArrayOfLong)
    {
      boolean bool;
      if (paramArrayOfInt.length == paramArrayOfUri.length)
      {
        bool = true;
        Assertions.checkArgument(bool);
        this.count = paramInt;
        this.states = paramArrayOfInt;
        this.uris = paramArrayOfUri;
        this.durationsUs = paramArrayOfLong;
      }
      for (paramInt = 0;; paramInt++) {
        if ((paramInt >= paramArrayOfInt.length) || (paramArrayOfInt[paramInt] == 0) || (paramArrayOfInt[paramInt] == 1))
        {
          this.nextAdIndexToPlay = paramInt;
          return;
          bool = false;
          break;
        }
      }
    }
    
    private static long[] copyDurationsUsWithSpaceForAdCount(long[] paramArrayOfLong, int paramInt)
    {
      int i = paramArrayOfLong.length;
      paramInt = Math.max(paramInt, i);
      paramArrayOfLong = Arrays.copyOf(paramArrayOfLong, paramInt);
      Arrays.fill(paramArrayOfLong, i, paramInt, -9223372036854775807L);
      return paramArrayOfLong;
    }
    
    private static int[] copyStatesWithSpaceForAdCount(int[] paramArrayOfInt, int paramInt)
    {
      int i = paramArrayOfInt.length;
      paramInt = Math.max(paramInt, i);
      paramArrayOfInt = Arrays.copyOf(paramArrayOfInt, paramInt);
      Arrays.fill(paramArrayOfInt, i, paramInt, 0);
      return paramArrayOfInt;
    }
    
    public AdGroup withAdCount(int paramInt)
    {
      if ((this.count == -1) && (this.states.length <= paramInt)) {}
      for (boolean bool = true;; bool = false)
      {
        Assertions.checkArgument(bool);
        int[] arrayOfInt = copyStatesWithSpaceForAdCount(this.states, paramInt);
        long[] arrayOfLong = copyDurationsUsWithSpaceForAdCount(this.durationsUs, paramInt);
        return new AdGroup(paramInt, arrayOfInt, (Uri[])Arrays.copyOf(this.uris, paramInt), arrayOfLong);
      }
    }
    
    public AdGroup withAdDurationsUs(long[] paramArrayOfLong)
    {
      if ((this.count == -1) || (paramArrayOfLong.length <= this.uris.length)) {}
      for (boolean bool = true;; bool = false)
      {
        Assertions.checkArgument(bool);
        long[] arrayOfLong = paramArrayOfLong;
        if (paramArrayOfLong.length < this.uris.length) {
          arrayOfLong = copyDurationsUsWithSpaceForAdCount(paramArrayOfLong, this.uris.length);
        }
        return new AdGroup(this.count, this.states, this.uris, arrayOfLong);
      }
    }
    
    public AdGroup withAdState(int paramInt1, int paramInt2)
    {
      boolean bool1 = false;
      boolean bool2;
      int[] arrayOfInt;
      long[] arrayOfLong;
      if ((this.count == -1) || (paramInt2 < this.count))
      {
        bool2 = true;
        Assertions.checkArgument(bool2);
        arrayOfInt = copyStatesWithSpaceForAdCount(this.states, paramInt2 + 1);
        if (arrayOfInt[paramInt2] != 0)
        {
          bool2 = bool1;
          if (arrayOfInt[paramInt2] != 1) {}
        }
        else
        {
          bool2 = true;
        }
        Assertions.checkArgument(bool2);
        if (this.durationsUs.length != arrayOfInt.length) {
          break label127;
        }
        arrayOfLong = this.durationsUs;
        label81:
        if (this.uris.length != arrayOfInt.length) {
          break label142;
        }
      }
      label127:
      label142:
      for (Uri[] arrayOfUri = this.uris;; arrayOfUri = (Uri[])Arrays.copyOf(this.uris, arrayOfInt.length))
      {
        arrayOfInt[paramInt2] = paramInt1;
        return new AdGroup(this.count, arrayOfInt, arrayOfUri, arrayOfLong);
        bool2 = false;
        break;
        arrayOfLong = copyDurationsUsWithSpaceForAdCount(this.durationsUs, arrayOfInt.length);
        break label81;
      }
    }
    
    public AdGroup withAdUri(Uri paramUri, int paramInt)
    {
      boolean bool1 = false;
      boolean bool2;
      int[] arrayOfInt;
      if ((this.count == -1) || (paramInt < this.count))
      {
        bool2 = true;
        Assertions.checkArgument(bool2);
        arrayOfInt = copyStatesWithSpaceForAdCount(this.states, paramInt + 1);
        bool2 = bool1;
        if (arrayOfInt[paramInt] == 0) {
          bool2 = true;
        }
        Assertions.checkArgument(bool2);
        if (this.durationsUs.length != arrayOfInt.length) {
          break label122;
        }
      }
      label122:
      for (long[] arrayOfLong = this.durationsUs;; arrayOfLong = copyDurationsUsWithSpaceForAdCount(this.durationsUs, arrayOfInt.length))
      {
        Uri[] arrayOfUri = (Uri[])Arrays.copyOf(this.uris, arrayOfInt.length);
        arrayOfUri[paramInt] = paramUri;
        arrayOfInt[paramInt] = 1;
        return new AdGroup(this.count, arrayOfInt, arrayOfUri, arrayOfLong);
        bool2 = false;
        break;
      }
    }
    
    public AdGroup withAllAdsSkipped()
    {
      if (this.count == -1) {}
      int i;
      for (Object localObject = new AdGroup(0, new int[0], new Uri[0], new long[0]);; localObject = new AdGroup(i, (int[])localObject, this.uris, this.durationsUs))
      {
        return (AdGroup)localObject;
        i = this.states.length;
        localObject = Arrays.copyOf(this.states, i);
        for (int j = 0; j < i; j++) {
          if ((localObject[j] == 1) || (localObject[j] == 0)) {
            localObject[j] = 2;
          }
        }
      }
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface AdState {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/ads/AdPlaybackState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */