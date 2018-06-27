package org.telegram.messenger.exoplayer2.upstream.cache;

import android.net.Uri;

final class ContentMetadataInternal {
    private static final String METADATA_NAME_CONTENT_LENGTH = "exo_len";
    private static final String METADATA_NAME_REDIRECTED_URI = "exo_redir";
    private static final String PREFIX = "exo_";

    ContentMetadataInternal() {
    }

    public static long getContentLength(ContentMetadata contentMetadata) {
        return contentMetadata.get(METADATA_NAME_CONTENT_LENGTH, -1);
    }

    public static void setContentLength(ContentMetadataMutations mutations, long length) {
        mutations.set(METADATA_NAME_CONTENT_LENGTH, length);
    }

    public static void removeContentLength(ContentMetadataMutations mutations) {
        mutations.remove(METADATA_NAME_CONTENT_LENGTH);
    }

    public static Uri getRedirectedUri(ContentMetadata contentMetadata) {
        String redirectedUri = contentMetadata.get(METADATA_NAME_REDIRECTED_URI, (String) null);
        if (redirectedUri == null) {
            return null;
        }
        return Uri.parse(redirectedUri);
    }

    public static void setRedirectedUri(ContentMetadataMutations mutations, Uri uri) {
        mutations.set(METADATA_NAME_REDIRECTED_URI, uri.toString());
    }

    public static void removeRedirectedUri(ContentMetadataMutations mutations) {
        mutations.remove(METADATA_NAME_REDIRECTED_URI);
    }
}
