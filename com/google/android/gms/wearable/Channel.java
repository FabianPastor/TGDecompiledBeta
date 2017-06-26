package com.google.android.gms.wearable;

import android.net.Uri;
import android.os.Parcelable;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Releasable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.ChannelApi.ChannelListener;
import java.io.InputStream;
import java.io.OutputStream;

public interface Channel extends Parcelable {

    public interface GetInputStreamResult extends Releasable, Result {
        InputStream getInputStream();
    }

    public interface GetOutputStreamResult extends Releasable, Result {
        OutputStream getOutputStream();
    }

    PendingResult<Status> addListener(GoogleApiClient googleApiClient, ChannelListener channelListener);

    PendingResult<Status> close(GoogleApiClient googleApiClient);

    PendingResult<Status> close(GoogleApiClient googleApiClient, int i);

    PendingResult<GetInputStreamResult> getInputStream(GoogleApiClient googleApiClient);

    String getNodeId();

    PendingResult<GetOutputStreamResult> getOutputStream(GoogleApiClient googleApiClient);

    String getPath();

    PendingResult<Status> receiveFile(GoogleApiClient googleApiClient, Uri uri, boolean z);

    PendingResult<Status> removeListener(GoogleApiClient googleApiClient, ChannelListener channelListener);

    PendingResult<Status> sendFile(GoogleApiClient googleApiClient, Uri uri);

    PendingResult<Status> sendFile(GoogleApiClient googleApiClient, Uri uri, long j, long j2);
}
