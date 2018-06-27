package org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest;

import android.net.Uri;
import org.telegram.messenger.exoplayer2.util.Util;

public final class SsUtil {
    public static Uri fixManifestUri(Uri manifestUri) {
        return Util.toLowerInvariant(manifestUri.getLastPathSegment()).matches("manifest(\\(.+\\))?") ? manifestUri : Uri.withAppendedPath(manifestUri, "Manifest");
    }

    private SsUtil() {
    }
}
