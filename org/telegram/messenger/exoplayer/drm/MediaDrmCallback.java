package org.telegram.messenger.exoplayer.drm;

import android.annotation.TargetApi;
import java.util.UUID;
import org.telegram.messenger.exoplayer.drm.ExoMediaDrm.KeyRequest;
import org.telegram.messenger.exoplayer.drm.ExoMediaDrm.ProvisionRequest;

@TargetApi(18)
public interface MediaDrmCallback {
    byte[] executeKeyRequest(UUID uuid, KeyRequest keyRequest) throws Exception;

    byte[] executeProvisionRequest(UUID uuid, ProvisionRequest provisionRequest) throws Exception;
}
