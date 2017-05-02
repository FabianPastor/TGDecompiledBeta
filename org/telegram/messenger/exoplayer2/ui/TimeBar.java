package org.telegram.messenger.exoplayer2.ui;

import android.support.annotation.Nullable;

public interface TimeBar {

    public interface OnScrubListener {
        void onScrubMove(TimeBar timeBar, long j);

        void onScrubStart(TimeBar timeBar);

        void onScrubStop(TimeBar timeBar, long j, boolean z);
    }

    void setAdBreakTimesMs(@Nullable long[] jArr, int i);

    void setBufferedPosition(long j);

    void setDuration(long j);

    void setEnabled(boolean z);

    void setKeyCountIncrement(int i);

    void setKeyTimeIncrement(long j);

    void setListener(OnScrubListener onScrubListener);

    void setPosition(long j);
}
