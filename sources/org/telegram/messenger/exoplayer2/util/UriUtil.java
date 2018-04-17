package org.telegram.messenger.exoplayer2.util;

import android.net.Uri;
import android.text.TextUtils;

public final class UriUtil {
    private static final int FRAGMENT = 3;
    private static final int INDEX_COUNT = 4;
    private static final int PATH = 1;
    private static final int QUERY = 2;
    private static final int SCHEME_COLON = 0;

    private UriUtil() {
    }

    public static Uri resolveToUri(String baseUri, String referenceUri) {
        return Uri.parse(resolve(baseUri, referenceUri));
    }

    public static String resolve(String baseUri, String referenceUri) {
        StringBuilder uri = new StringBuilder();
        baseUri = baseUri == null ? TtmlNode.ANONYMOUS_REGION_ID : baseUri;
        referenceUri = referenceUri == null ? TtmlNode.ANONYMOUS_REGION_ID : referenceUri;
        int[] refIndices = getUriIndices(referenceUri);
        if (refIndices[0] != -1) {
            uri.append(referenceUri);
            removeDotSegments(uri, refIndices[1], refIndices[2]);
            return uri.toString();
        }
        int[] baseIndices = getUriIndices(baseUri);
        if (refIndices[3] == 0) {
            uri.append(baseUri, 0, baseIndices[3]);
            uri.append(referenceUri);
            return uri.toString();
        } else if (refIndices[2] == 0) {
            uri.append(baseUri, 0, baseIndices[2]);
            uri.append(referenceUri);
            return uri.toString();
        } else if (refIndices[1] != 0) {
            baseLimit = baseIndices[0] + 1;
            uri.append(baseUri, 0, baseLimit);
            uri.append(referenceUri);
            return removeDotSegments(uri, refIndices[1] + baseLimit, refIndices[2] + baseLimit);
        } else if (referenceUri.charAt(refIndices[1]) == '/') {
            uri.append(baseUri, 0, baseIndices[1]);
            uri.append(referenceUri);
            return removeDotSegments(uri, baseIndices[1], baseIndices[1] + refIndices[2]);
        } else if (baseIndices[0] + 2 >= baseIndices[1] || baseIndices[1] != baseIndices[2]) {
            int lastSlashIndex = baseUri.lastIndexOf(47, baseIndices[2] - 1);
            baseLimit = lastSlashIndex == -1 ? baseIndices[1] : lastSlashIndex + 1;
            uri.append(baseUri, 0, baseLimit);
            uri.append(referenceUri);
            return removeDotSegments(uri, baseIndices[1], refIndices[2] + baseLimit);
        } else {
            uri.append(baseUri, 0, baseIndices[1]);
            uri.append('/');
            uri.append(referenceUri);
            return removeDotSegments(uri, baseIndices[1], (baseIndices[1] + refIndices[2]) + 1);
        }
    }

    private static String removeDotSegments(StringBuilder uri, int offset, int limit) {
        if (offset >= limit) {
            return uri.toString();
        }
        if (uri.charAt(offset) == '/') {
            offset++;
        }
        int segmentStart = offset;
        int limit2 = limit;
        limit = offset;
        while (limit <= limit2) {
            int nextSegmentStart;
            if (limit == limit2) {
                nextSegmentStart = limit;
            } else if (uri.charAt(limit) == '/') {
                nextSegmentStart = limit + 1;
            } else {
                limit++;
            }
            if (limit == segmentStart + 1 && uri.charAt(segmentStart) == '.') {
                uri.delete(segmentStart, nextSegmentStart);
                limit2 -= nextSegmentStart - segmentStart;
                limit = segmentStart;
            } else if (limit == segmentStart + 2 && uri.charAt(segmentStart) == '.' && uri.charAt(segmentStart + 1) == '.') {
                int prevSegmentStart = uri.lastIndexOf("/", segmentStart - 2) + 1;
                int removeFrom = prevSegmentStart > offset ? prevSegmentStart : offset;
                uri.delete(removeFrom, nextSegmentStart);
                limit2 -= nextSegmentStart - removeFrom;
                segmentStart = prevSegmentStart;
                limit = prevSegmentStart;
            } else {
                limit++;
                segmentStart = limit;
            }
        }
        return uri.toString();
    }

    private static int[] getUriIndices(String uriString) {
        int[] indices = new int[4];
        if (TextUtils.isEmpty(uriString)) {
            indices[0] = -1;
            return indices;
        }
        int pathIndex;
        int length = uriString.length();
        int fragmentIndex = uriString.indexOf(35);
        if (fragmentIndex == -1) {
            fragmentIndex = length;
        }
        int queryIndex = uriString.indexOf(63);
        if (queryIndex == -1 || queryIndex > fragmentIndex) {
            queryIndex = fragmentIndex;
        }
        int schemeIndexLimit = uriString.indexOf(47);
        if (schemeIndexLimit == -1 || schemeIndexLimit > queryIndex) {
            schemeIndexLimit = queryIndex;
        }
        int schemeIndex = uriString.indexOf(58);
        if (schemeIndex > schemeIndexLimit) {
            schemeIndex = -1;
        }
        boolean hasAuthority = schemeIndex + 2 < queryIndex && uriString.charAt(schemeIndex + 1) == '/' && uriString.charAt(schemeIndex + 2) == '/';
        if (hasAuthority) {
            pathIndex = uriString.indexOf(47, schemeIndex + 3);
            if (pathIndex == -1 || pathIndex > queryIndex) {
                pathIndex = queryIndex;
            }
        } else {
            pathIndex = schemeIndex + 1;
        }
        int pathIndex2 = pathIndex;
        indices[0] = schemeIndex;
        indices[1] = pathIndex2;
        indices[2] = queryIndex;
        indices[3] = fragmentIndex;
        return indices;
    }
}
