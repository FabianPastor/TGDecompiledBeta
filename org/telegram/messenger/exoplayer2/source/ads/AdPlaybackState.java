package org.telegram.messenger.exoplayer2.source.ads;

import android.net.Uri;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class AdPlaybackState {
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

    public static final class AdGroup {
        public final int count;
        public final long[] durationsUs;
        public final int nextAdIndexToPlay;
        public final int[] states;
        public final Uri[] uris;

        public AdGroup() {
            this(-1, new int[0], new Uri[0], new long[0]);
        }

        private AdGroup(int count, int[] states, Uri[] uris, long[] durationsUs) {
            Assertions.checkArgument(states.length == uris.length);
            this.count = count;
            this.states = states;
            this.uris = uris;
            this.durationsUs = durationsUs;
            int nextAdIndexToPlay = 0;
            while (nextAdIndexToPlay < states.length && states[nextAdIndexToPlay] != 0 && states[nextAdIndexToPlay] != 1) {
                nextAdIndexToPlay++;
            }
            this.nextAdIndexToPlay = nextAdIndexToPlay;
        }

        public AdGroup withAdCount(int count) {
            boolean z = this.count == -1 && this.states.length <= count;
            Assertions.checkArgument(z);
            return new AdGroup(count, copyStatesWithSpaceForAdCount(this.states, count), (Uri[]) Arrays.copyOf(this.uris, count), copyDurationsUsWithSpaceForAdCount(this.durationsUs, count));
        }

        public AdGroup withAdUri(Uri uri, int index) {
            boolean z;
            long[] durationsUs;
            boolean z2 = false;
            if (this.count == -1 || index < this.count) {
                z = true;
            } else {
                z = false;
            }
            Assertions.checkArgument(z);
            int[] states = copyStatesWithSpaceForAdCount(this.states, index + 1);
            if (states[index] == 0) {
                z2 = true;
            }
            Assertions.checkArgument(z2);
            if (this.durationsUs.length == states.length) {
                durationsUs = this.durationsUs;
            } else {
                durationsUs = copyDurationsUsWithSpaceForAdCount(this.durationsUs, states.length);
            }
            Uri[] uris = (Uri[]) Arrays.copyOf(this.uris, states.length);
            uris[index] = uri;
            states[index] = 1;
            return new AdGroup(this.count, states, uris, durationsUs);
        }

        public AdGroup withAdState(int state, int index) {
            boolean z;
            long[] durationsUs;
            Uri[] uris;
            boolean z2 = false;
            if (this.count == -1 || index < this.count) {
                z = true;
            } else {
                z = false;
            }
            Assertions.checkArgument(z);
            int[] states = copyStatesWithSpaceForAdCount(this.states, index + 1);
            if (states[index] == 0 || states[index] == 1) {
                z2 = true;
            }
            Assertions.checkArgument(z2);
            if (this.durationsUs.length == states.length) {
                durationsUs = this.durationsUs;
            } else {
                durationsUs = copyDurationsUsWithSpaceForAdCount(this.durationsUs, states.length);
            }
            if (this.uris.length == states.length) {
                uris = this.uris;
            } else {
                uris = (Uri[]) Arrays.copyOf(this.uris, states.length);
            }
            states[index] = state;
            return new AdGroup(this.count, states, uris, durationsUs);
        }

        public AdGroup withAdDurationsUs(long[] durationsUs) {
            boolean z = this.count == -1 || durationsUs.length <= this.uris.length;
            Assertions.checkArgument(z);
            if (durationsUs.length < this.uris.length) {
                durationsUs = copyDurationsUsWithSpaceForAdCount(durationsUs, this.uris.length);
            }
            return new AdGroup(this.count, this.states, this.uris, durationsUs);
        }

        public AdGroup withAllAdsSkipped() {
            if (this.count == -1) {
                return new AdGroup(0, new int[0], new Uri[0], new long[0]);
            }
            int count = this.states.length;
            int[] states = Arrays.copyOf(this.states, count);
            int i = 0;
            while (i < count) {
                if (states[i] == 1 || states[i] == 0) {
                    states[i] = 2;
                }
                i++;
            }
            return new AdGroup(count, states, this.uris, this.durationsUs);
        }

        private static int[] copyStatesWithSpaceForAdCount(int[] states, int count) {
            int oldStateCount = states.length;
            int newStateCount = Math.max(count, oldStateCount);
            states = Arrays.copyOf(states, newStateCount);
            Arrays.fill(states, oldStateCount, newStateCount, 0);
            return states;
        }

        private static long[] copyDurationsUsWithSpaceForAdCount(long[] durationsUs, int count) {
            int oldDurationsUsCount = durationsUs.length;
            int newDurationsUsCount = Math.max(count, oldDurationsUsCount);
            durationsUs = Arrays.copyOf(durationsUs, newDurationsUsCount);
            Arrays.fill(durationsUs, oldDurationsUsCount, newDurationsUsCount, C.TIME_UNSET);
            return durationsUs;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface AdState {
    }

    public AdPlaybackState(long[] adGroupTimesUs) {
        int count = adGroupTimesUs.length;
        this.adGroupCount = count;
        this.adGroupTimesUs = Arrays.copyOf(adGroupTimesUs, count);
        this.adGroups = new AdGroup[count];
        for (int i = 0; i < count; i++) {
            this.adGroups[i] = new AdGroup();
        }
        this.adResumePositionUs = 0;
        this.contentDurationUs = C.TIME_UNSET;
    }

    private AdPlaybackState(long[] adGroupTimesUs, AdGroup[] adGroups, long adResumePositionUs, long contentDurationUs) {
        this.adGroupCount = adGroups.length;
        this.adGroupTimesUs = adGroupTimesUs;
        this.adGroups = adGroups;
        this.adResumePositionUs = adResumePositionUs;
        this.contentDurationUs = contentDurationUs;
    }

    public AdPlaybackState withAdCount(int adGroupIndex, int adCount) {
        Assertions.checkArgument(adCount > 0);
        if (this.adGroups[adGroupIndex].count == adCount) {
            return this;
        }
        AdGroup[] adGroups = (AdGroup[]) Arrays.copyOf(this.adGroups, this.adGroups.length);
        adGroups[adGroupIndex] = this.adGroups[adGroupIndex].withAdCount(adCount);
        return new AdPlaybackState(this.adGroupTimesUs, adGroups, this.adResumePositionUs, this.contentDurationUs);
    }

    public AdPlaybackState withAdUri(int adGroupIndex, int adIndexInAdGroup, Uri uri) {
        AdGroup[] adGroups = (AdGroup[]) Arrays.copyOf(this.adGroups, this.adGroups.length);
        adGroups[adGroupIndex] = adGroups[adGroupIndex].withAdUri(uri, adIndexInAdGroup);
        return new AdPlaybackState(this.adGroupTimesUs, adGroups, this.adResumePositionUs, this.contentDurationUs);
    }

    public AdPlaybackState withPlayedAd(int adGroupIndex, int adIndexInAdGroup) {
        AdGroup[] adGroups = (AdGroup[]) Arrays.copyOf(this.adGroups, this.adGroups.length);
        adGroups[adGroupIndex] = adGroups[adGroupIndex].withAdState(3, adIndexInAdGroup);
        return new AdPlaybackState(this.adGroupTimesUs, adGroups, this.adResumePositionUs, this.contentDurationUs);
    }

    public AdPlaybackState withAdLoadError(int adGroupIndex, int adIndexInAdGroup) {
        AdGroup[] adGroups = (AdGroup[]) Arrays.copyOf(this.adGroups, this.adGroups.length);
        adGroups[adGroupIndex] = adGroups[adGroupIndex].withAdState(4, adIndexInAdGroup);
        return new AdPlaybackState(this.adGroupTimesUs, adGroups, this.adResumePositionUs, this.contentDurationUs);
    }

    public AdPlaybackState withSkippedAdGroup(int adGroupIndex) {
        AdGroup[] adGroups = (AdGroup[]) Arrays.copyOf(this.adGroups, this.adGroups.length);
        adGroups[adGroupIndex] = adGroups[adGroupIndex].withAllAdsSkipped();
        return new AdPlaybackState(this.adGroupTimesUs, adGroups, this.adResumePositionUs, this.contentDurationUs);
    }

    public AdPlaybackState withAdDurationsUs(long[][] adDurationUs) {
        AdGroup[] adGroups = (AdGroup[]) Arrays.copyOf(this.adGroups, this.adGroups.length);
        for (int adGroupIndex = 0; adGroupIndex < this.adGroupCount; adGroupIndex++) {
            adGroups[adGroupIndex] = adGroups[adGroupIndex].withAdDurationsUs(adDurationUs[adGroupIndex]);
        }
        return new AdPlaybackState(this.adGroupTimesUs, adGroups, this.adResumePositionUs, this.contentDurationUs);
    }

    public AdPlaybackState withAdResumePositionUs(long adResumePositionUs) {
        if (this.adResumePositionUs == adResumePositionUs) {
            return this;
        }
        return new AdPlaybackState(this.adGroupTimesUs, this.adGroups, adResumePositionUs, this.contentDurationUs);
    }

    public AdPlaybackState withContentDurationUs(long contentDurationUs) {
        return this.contentDurationUs == contentDurationUs ? this : new AdPlaybackState(this.adGroupTimesUs, this.adGroups, this.adResumePositionUs, contentDurationUs);
    }
}
