package org.telegram.messenger.exoplayer2.source.ads;

import android.net.Uri;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.C;

public final class AdPlaybackState {
    public final int[] adCounts;
    public final int adGroupCount;
    public final long[] adGroupTimesUs;
    public long adResumePositionUs;
    public final Uri[][] adUris;
    public final int[] adsLoadedCounts;
    public final int[] adsPlayedCounts;
    public long contentDurationUs;

    public AdPlaybackState(long[] adGroupTimesUs) {
        this.adGroupTimesUs = adGroupTimesUs;
        this.adGroupCount = adGroupTimesUs.length;
        this.adsPlayedCounts = new int[this.adGroupCount];
        this.adCounts = new int[this.adGroupCount];
        Arrays.fill(this.adCounts, -1);
        this.adUris = new Uri[this.adGroupCount][];
        Arrays.fill(this.adUris, new Uri[0]);
        this.adsLoadedCounts = new int[adGroupTimesUs.length];
        this.contentDurationUs = C.TIME_UNSET;
    }

    private AdPlaybackState(long[] adGroupTimesUs, int[] adCounts, int[] adsLoadedCounts, int[] adsPlayedCounts, Uri[][] adUris, long contentDurationUs, long adResumePositionUs) {
        this.adGroupTimesUs = adGroupTimesUs;
        this.adCounts = adCounts;
        this.adsLoadedCounts = adsLoadedCounts;
        this.adsPlayedCounts = adsPlayedCounts;
        this.adUris = adUris;
        this.contentDurationUs = contentDurationUs;
        this.adResumePositionUs = adResumePositionUs;
        this.adGroupCount = adGroupTimesUs.length;
    }

    public AdPlaybackState copy() {
        Uri[][] adUris = new Uri[this.adGroupTimesUs.length][];
        for (int i = 0; i < this.adUris.length; i++) {
            adUris[i] = (Uri[]) Arrays.copyOf(this.adUris[i], this.adUris[i].length);
        }
        return new AdPlaybackState(Arrays.copyOf(this.adGroupTimesUs, this.adGroupCount), Arrays.copyOf(this.adCounts, this.adGroupCount), Arrays.copyOf(this.adsLoadedCounts, this.adGroupCount), Arrays.copyOf(this.adsPlayedCounts, this.adGroupCount), adUris, this.contentDurationUs, this.adResumePositionUs);
    }

    public void setAdCount(int adGroupIndex, int adCount) {
        this.adCounts[adGroupIndex] = adCount;
    }

    public void addAdUri(int adGroupIndex, Uri uri) {
        int adIndexInAdGroup = this.adUris[adGroupIndex].length;
        this.adUris[adGroupIndex] = (Uri[]) Arrays.copyOf(this.adUris[adGroupIndex], adIndexInAdGroup + 1);
        this.adUris[adGroupIndex][adIndexInAdGroup] = uri;
        int[] iArr = this.adsLoadedCounts;
        iArr[adGroupIndex] = iArr[adGroupIndex] + 1;
    }

    public void playedAd(int adGroupIndex) {
        this.adResumePositionUs = 0;
        int[] iArr = this.adsPlayedCounts;
        iArr[adGroupIndex] = iArr[adGroupIndex] + 1;
    }

    public void playedAdGroup(int adGroupIndex) {
        this.adResumePositionUs = 0;
        if (this.adCounts[adGroupIndex] == -1) {
            this.adCounts[adGroupIndex] = 0;
        }
        this.adsPlayedCounts[adGroupIndex] = this.adCounts[adGroupIndex];
    }

    public void setAdResumePositionUs(long adResumePositionUs) {
        this.adResumePositionUs = adResumePositionUs;
    }
}
