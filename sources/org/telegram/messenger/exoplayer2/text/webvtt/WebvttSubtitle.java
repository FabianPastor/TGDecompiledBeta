package org.telegram.messenger.exoplayer2.text.webvtt;

import android.text.SpannableStringBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.text.Cue;
import org.telegram.messenger.exoplayer2.text.Subtitle;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

final class WebvttSubtitle implements Subtitle {
    private final long[] cueTimesUs = new long[(2 * this.numCues)];
    private final List<WebvttCue> cues;
    private final int numCues;
    private final long[] sortedCueTimesUs;

    public WebvttSubtitle(List<WebvttCue> list) {
        this.cues = list;
        this.numCues = list.size();
        for (int i = 0; i < this.numCues; i++) {
            WebvttCue webvttCue = (WebvttCue) list.get(i);
            int i2 = i * 2;
            this.cueTimesUs[i2] = webvttCue.startTime;
            this.cueTimesUs[i2 + 1] = webvttCue.endTime;
        }
        this.sortedCueTimesUs = Arrays.copyOf(this.cueTimesUs, this.cueTimesUs.length);
        Arrays.sort(this.sortedCueTimesUs);
    }

    public int getNextEventTimeIndex(long j) {
        j = Util.binarySearchCeil(this.sortedCueTimesUs, j, false, false);
        return j < this.sortedCueTimesUs.length ? j : -1;
    }

    public int getEventTimeCount() {
        return this.sortedCueTimesUs.length;
    }

    public long getEventTime(int i) {
        boolean z = false;
        Assertions.checkArgument(i >= 0);
        if (i < this.sortedCueTimesUs.length) {
            z = true;
        }
        Assertions.checkArgument(z);
        return this.sortedCueTimesUs[i];
    }

    public List<Cue> getCues(long j) {
        CharSequence charSequence = null;
        List list = null;
        WebvttCue webvttCue = list;
        for (int i = 0; i < this.numCues; i++) {
            int i2 = i * 2;
            if (this.cueTimesUs[i2] <= j && j < this.cueTimesUs[i2 + 1]) {
                if (list == null) {
                    list = new ArrayList();
                }
                WebvttCue webvttCue2 = (WebvttCue) this.cues.get(i);
                if (!webvttCue2.isNormalCue()) {
                    list.add(webvttCue2);
                } else if (webvttCue == null) {
                    webvttCue = webvttCue2;
                } else if (charSequence == null) {
                    charSequence = new SpannableStringBuilder();
                    charSequence.append(webvttCue.text).append("\n").append(webvttCue2.text);
                } else {
                    charSequence.append("\n").append(webvttCue2.text);
                }
            }
        }
        if (charSequence != null) {
            list.add(new WebvttCue(charSequence));
        } else if (webvttCue != null) {
            list.add(webvttCue);
        }
        if (list != null) {
            return list;
        }
        return Collections.emptyList();
    }
}
