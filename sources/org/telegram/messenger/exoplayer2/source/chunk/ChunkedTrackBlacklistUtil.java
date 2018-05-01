package org.telegram.messenger.exoplayer2.source.chunk;

import android.util.Log;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException;

public final class ChunkedTrackBlacklistUtil {
    public static final long DEFAULT_TRACK_BLACKLIST_MS = 60000;
    private static final String TAG = "ChunkedTrackBlacklist";

    public static boolean maybeBlacklistTrack(TrackSelection trackSelection, int i, Exception exception) {
        return maybeBlacklistTrack(trackSelection, i, exception, DEFAULT_TRACK_BLACKLIST_MS);
    }

    public static boolean maybeBlacklistTrack(TrackSelection trackSelection, int i, Exception exception, long j) {
        if (!shouldBlacklist(exception)) {
            return null;
        }
        boolean blacklist = trackSelection.blacklist(i, j);
        exception = ((InvalidResponseCodeException) exception).responseCode;
        if (blacklist) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Blacklisted: duration=");
            stringBuilder.append(j);
            stringBuilder.append(", responseCode=");
            stringBuilder.append(exception);
            stringBuilder.append(", format=");
            stringBuilder.append(trackSelection.getFormat(i));
            Log.w(str, stringBuilder.toString());
        } else {
            j = TAG;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Blacklisting failed (cannot blacklist last enabled track): responseCode=");
            stringBuilder2.append(exception);
            stringBuilder2.append(", format=");
            stringBuilder2.append(trackSelection.getFormat(i));
            Log.w(j, stringBuilder2.toString());
        }
        return blacklist;
    }

    public static boolean shouldBlacklist(Exception exception) {
        boolean z = false;
        if (!(exception instanceof InvalidResponseCodeException)) {
            return false;
        }
        exception = ((InvalidResponseCodeException) exception).responseCode;
        if (exception == 404 || exception == 410) {
            z = true;
        }
        return z;
    }

    private ChunkedTrackBlacklistUtil() {
    }
}
