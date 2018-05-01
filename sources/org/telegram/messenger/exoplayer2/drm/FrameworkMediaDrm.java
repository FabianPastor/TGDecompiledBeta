package org.telegram.messenger.exoplayer2.drm;

import android.annotation.TargetApi;
import android.media.DeniedByServerException;
import android.media.MediaCrypto;
import android.media.MediaCryptoException;
import android.media.MediaDrm;
import android.media.MediaDrm.KeyStatus;
import android.media.MediaDrmException;
import android.media.NotProvisionedException;
import android.media.UnsupportedSchemeException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.drm.ExoMediaDrm.DefaultKeyRequest;
import org.telegram.messenger.exoplayer2.drm.ExoMediaDrm.DefaultKeyStatus;
import org.telegram.messenger.exoplayer2.drm.ExoMediaDrm.DefaultProvisionRequest;
import org.telegram.messenger.exoplayer2.drm.ExoMediaDrm.KeyRequest;
import org.telegram.messenger.exoplayer2.drm.ExoMediaDrm.OnEventListener;
import org.telegram.messenger.exoplayer2.drm.ExoMediaDrm.OnKeyStatusChangeListener;
import org.telegram.messenger.exoplayer2.drm.ExoMediaDrm.ProvisionRequest;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

@TargetApi(23)
public final class FrameworkMediaDrm implements ExoMediaDrm<FrameworkMediaCrypto> {
    private final MediaDrm mediaDrm;
    private final UUID uuid;

    public static FrameworkMediaDrm newInstance(UUID uuid) throws UnsupportedDrmException {
        try {
            return new FrameworkMediaDrm(uuid);
        } catch (UUID uuid2) {
            throw new UnsupportedDrmException(1, uuid2);
        } catch (UUID uuid22) {
            throw new UnsupportedDrmException(2, uuid22);
        }
    }

    private FrameworkMediaDrm(UUID uuid) throws UnsupportedSchemeException {
        Assertions.checkNotNull(uuid);
        Assertions.checkArgument(C0542C.COMMON_PSSH_UUID.equals(uuid) ^ 1, "Use C.CLEARKEY_UUID instead");
        if (Util.SDK_INT < 27 && C0542C.CLEARKEY_UUID.equals(uuid)) {
            uuid = C0542C.COMMON_PSSH_UUID;
        }
        this.uuid = uuid;
        this.mediaDrm = new MediaDrm(uuid);
    }

    public void setOnEventListener(final OnEventListener<? super FrameworkMediaCrypto> onEventListener) {
        this.mediaDrm.setOnEventListener(onEventListener == null ? null : new MediaDrm.OnEventListener() {
            public void onEvent(MediaDrm mediaDrm, byte[] bArr, int i, int i2, byte[] bArr2) {
                onEventListener.onEvent(FrameworkMediaDrm.this, bArr, i, i2, bArr2);
            }
        });
    }

    public void setOnKeyStatusChangeListener(final OnKeyStatusChangeListener<? super FrameworkMediaCrypto> onKeyStatusChangeListener) {
        if (Util.SDK_INT < 23) {
            throw new UnsupportedOperationException();
        }
        this.mediaDrm.setOnKeyStatusChangeListener(onKeyStatusChangeListener == null ? null : new MediaDrm.OnKeyStatusChangeListener() {
            public void onKeyStatusChange(MediaDrm mediaDrm, byte[] bArr, List<KeyStatus> list, boolean z) {
                mediaDrm = new ArrayList();
                for (KeyStatus keyStatus : list) {
                    mediaDrm.add(new DefaultKeyStatus(keyStatus.getStatusCode(), keyStatus.getKeyId()));
                }
                onKeyStatusChangeListener.onKeyStatusChange(FrameworkMediaDrm.this, bArr, mediaDrm, z);
            }
        }, null);
    }

    public byte[] openSession() throws MediaDrmException {
        return this.mediaDrm.openSession();
    }

    public void closeSession(byte[] bArr) {
        this.mediaDrm.closeSession(bArr);
    }

    public KeyRequest getKeyRequest(byte[] bArr, byte[] bArr2, String str, int i, HashMap<String, String> hashMap) throws NotProvisionedException {
        bArr = this.mediaDrm.getKeyRequest(bArr, bArr2, str, i, hashMap);
        return new DefaultKeyRequest(bArr.getData(), bArr.getDefaultUrl());
    }

    public byte[] provideKeyResponse(byte[] bArr, byte[] bArr2) throws NotProvisionedException, DeniedByServerException {
        return this.mediaDrm.provideKeyResponse(bArr, bArr2);
    }

    public ProvisionRequest getProvisionRequest() {
        MediaDrm.ProvisionRequest provisionRequest = this.mediaDrm.getProvisionRequest();
        return new DefaultProvisionRequest(provisionRequest.getData(), provisionRequest.getDefaultUrl());
    }

    public void provideProvisionResponse(byte[] bArr) throws DeniedByServerException {
        this.mediaDrm.provideProvisionResponse(bArr);
    }

    public Map<String, String> queryKeyStatus(byte[] bArr) {
        return this.mediaDrm.queryKeyStatus(bArr);
    }

    public void release() {
        this.mediaDrm.release();
    }

    public void restoreKeys(byte[] bArr, byte[] bArr2) {
        this.mediaDrm.restoreKeys(bArr, bArr2);
    }

    public String getPropertyString(String str) {
        return this.mediaDrm.getPropertyString(str);
    }

    public byte[] getPropertyByteArray(String str) {
        return this.mediaDrm.getPropertyByteArray(str);
    }

    public void setPropertyString(String str, String str2) {
        this.mediaDrm.setPropertyString(str, str2);
    }

    public void setPropertyByteArray(String str, byte[] bArr) {
        this.mediaDrm.setPropertyByteArray(str, bArr);
    }

    public FrameworkMediaCrypto createMediaCrypto(byte[] bArr) throws MediaCryptoException {
        boolean z = Util.SDK_INT < 21 && C0542C.WIDEVINE_UUID.equals(this.uuid) && "L3".equals(getPropertyString("securityLevel"));
        return new FrameworkMediaCrypto(new MediaCrypto(this.uuid, bArr), z);
    }
}
