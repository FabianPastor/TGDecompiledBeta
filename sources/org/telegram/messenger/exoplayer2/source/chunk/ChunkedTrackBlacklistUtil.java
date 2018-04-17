package org.telegram.messenger.exoplayer2.source.chunk;

import android.util.Log;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException;

public final class ChunkedTrackBlacklistUtil {
    public static final long DEFAULT_TRACK_BLACKLIST_MS = 60000;
    private static final String TAG = "ChunkedTrackBlacklist";

    public static boolean maybeBlacklistTrack(TrackSelection trackSelection, int trackSelectionIndex, Exception e) {
        return maybeBlacklistTrack(trackSelection, trackSelectionIndex, e, DEFAULT_TRACK_BLACKLIST_MS);
    }

    public static boolean maybeBlacklistTrack(TrackSelection trackSelection, int trackSelectionIndex, Exception e, long blacklistDurationMs) {
        if (!shouldBlacklist(e)) {
            return false;
        }
        boolean blacklisted = trackSelection.blacklist(trackSelectionIndex, blacklistDurationMs);
        int responseCode = ((InvalidResponseCodeException) e).responseCode;
        String str;
        StringBuilder stringBuilder;
        if (blacklisted) {
            str = TAG;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Blacklisted: duration=");
            stringBuilder.append(blacklistDurationMs);
            stringBuilder.append(", responseCode=");
            stringBuilder.append(responseCode);
            stringBuilder.append(", format=");
            stringBuilder.append(trackSelection.getFormat(trackSelectionIndex));
            Log.w(str, stringBuilder.toString());
        } else {
            str = TAG;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Blacklisting failed (cannot blacklist last enabled track): responseCode=");
            stringBuilder.append(responseCode);
            stringBuilder.append(", format=");
            stringBuilder.append(trackSelection.getFormat(trackSelectionIndex));
            Log.w(str, stringBuilder.toString());
        }
        return blacklisted;
    }

    public static boolean shouldBlacklist(Exception e) {
        boolean z = false;
        if (!(e instanceof InvalidResponseCodeException)) {
            return false;
        }
        int responseCode = ((InvalidResponseCodeException) e).responseCode;
        if (responseCode != 404) {
            if (responseCode != 410) {
                return z;
            }
        }
        z = true;
        return z;
    }

    private ChunkedTrackBlacklistUtil() {
    }
}
