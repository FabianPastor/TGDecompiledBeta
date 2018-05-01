package org.telegram.messenger.exoplayer2.upstream;

import android.net.Uri;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class DataSpec {
    public static final int FLAG_ALLOW_CACHING_UNKNOWN_LENGTH = 2;
    public static final int FLAG_ALLOW_GZIP = 1;
    public final long absoluteStreamPosition;
    public final int flags;
    public final String key;
    public final long length;
    public final long position;
    public final byte[] postBody;
    public final Uri uri;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Flags {
    }

    public DataSpec(Uri uri) {
        this(uri, 0);
    }

    public DataSpec(Uri uri, int i) {
        this(uri, 0, -1, null, i);
    }

    public DataSpec(Uri uri, long j, long j2, String str) {
        this(uri, j, j, j2, str, 0);
    }

    public DataSpec(Uri uri, long j, long j2, String str, int i) {
        this(uri, j, j, j2, str, i);
    }

    public DataSpec(Uri uri, long j, long j2, long j3, String str, int i) {
        this(uri, null, j, j2, j3, str, i);
    }

    public DataSpec(Uri uri, byte[] bArr, long j, long j2, long j3, String str, int i) {
        boolean z = false;
        Assertions.checkArgument(j >= 0);
        Assertions.checkArgument(j2 >= 0);
        if (j3 > 0 || j3 == -1) {
            z = true;
        }
        Assertions.checkArgument(z);
        this.uri = uri;
        this.postBody = bArr;
        this.absoluteStreamPosition = j;
        this.position = j2;
        this.length = j3;
        this.key = str;
        this.flags = i;
    }

    public boolean isFlagSet(int i) {
        return (this.flags & i) == i;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DataSpec[");
        stringBuilder.append(this.uri);
        stringBuilder.append(", ");
        stringBuilder.append(Arrays.toString(this.postBody));
        stringBuilder.append(", ");
        stringBuilder.append(this.absoluteStreamPosition);
        stringBuilder.append(", ");
        stringBuilder.append(this.position);
        stringBuilder.append(", ");
        stringBuilder.append(this.length);
        stringBuilder.append(", ");
        stringBuilder.append(this.key);
        stringBuilder.append(", ");
        stringBuilder.append(this.flags);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public DataSpec subrange(long j) {
        long j2 = -1;
        if (this.length != -1) {
            j2 = this.length - j;
        }
        return subrange(j, j2);
    }

    public DataSpec subrange(long j, long j2) {
        DataSpec dataSpec = this;
        if (j == 0 && dataSpec.length == j2) {
            return dataSpec;
        }
        return new DataSpec(dataSpec.uri, dataSpec.postBody, dataSpec.absoluteStreamPosition + j, dataSpec.position + j, j2, dataSpec.key, dataSpec.flags);
    }
}
