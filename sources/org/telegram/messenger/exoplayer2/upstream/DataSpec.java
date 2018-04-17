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

    public DataSpec(Uri uri, int flags) {
        this(uri, 0, -1, null, flags);
    }

    public DataSpec(Uri uri, long absoluteStreamPosition, long length, String key) {
        this(uri, absoluteStreamPosition, absoluteStreamPosition, length, key, 0);
    }

    public DataSpec(Uri uri, long absoluteStreamPosition, long length, String key, int flags) {
        this(uri, absoluteStreamPosition, absoluteStreamPosition, length, key, flags);
    }

    public DataSpec(Uri uri, long absoluteStreamPosition, long position, long length, String key, int flags) {
        this(uri, null, absoluteStreamPosition, position, length, key, flags);
    }

    public DataSpec(Uri uri, byte[] postBody, long absoluteStreamPosition, long position, long length, String key, int flags) {
        boolean z = false;
        Assertions.checkArgument(absoluteStreamPosition >= 0);
        Assertions.checkArgument(position >= 0);
        if (length <= 0) {
            if (length != -1) {
                Assertions.checkArgument(z);
                this.uri = uri;
                this.postBody = postBody;
                this.absoluteStreamPosition = absoluteStreamPosition;
                this.position = position;
                this.length = length;
                this.key = key;
                this.flags = flags;
            }
        }
        z = true;
        Assertions.checkArgument(z);
        this.uri = uri;
        this.postBody = postBody;
        this.absoluteStreamPosition = absoluteStreamPosition;
        this.position = position;
        this.length = length;
        this.key = key;
        this.flags = flags;
    }

    public boolean isFlagSet(int flag) {
        return (this.flags & flag) == flag;
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

    public DataSpec subrange(long offset) {
        long j = -1;
        if (this.length != -1) {
            j = this.length - offset;
        }
        return subrange(offset, j);
    }

    public DataSpec subrange(long offset, long length) {
        DataSpec dataSpec = this;
        if (offset == 0 && dataSpec.length == length) {
            return dataSpec;
        }
        return new DataSpec(dataSpec.uri, dataSpec.postBody, dataSpec.absoluteStreamPosition + offset, dataSpec.position + offset, length, dataSpec.key, dataSpec.flags);
    }
}
