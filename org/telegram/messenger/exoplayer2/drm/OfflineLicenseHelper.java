package org.telegram.messenger.exoplayer2.drm;

import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Pair;
import java.io.IOException;
import java.util.HashMap;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.drm.DefaultDrmSessionManager.EventListener;
import org.telegram.messenger.exoplayer2.drm.DrmSession.DrmSessionException;
import org.telegram.messenger.exoplayer2.source.dash.DashUtil;
import org.telegram.messenger.exoplayer2.source.dash.manifest.AdaptationSet;
import org.telegram.messenger.exoplayer2.source.dash.manifest.DashManifest;
import org.telegram.messenger.exoplayer2.source.dash.manifest.Period;
import org.telegram.messenger.exoplayer2.source.dash.manifest.Representation;
import org.telegram.messenger.exoplayer2.upstream.HttpDataSource;
import org.telegram.messenger.exoplayer2.upstream.HttpDataSource.Factory;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class OfflineLicenseHelper<T extends ExoMediaCrypto> {
    private final ConditionVariable conditionVariable;
    private final DefaultDrmSessionManager<T> drmSessionManager;
    private final HandlerThread handlerThread = new HandlerThread("OfflineLicenseHelper");

    public static OfflineLicenseHelper<FrameworkMediaCrypto> newWidevineInstance(String licenseUrl, Factory httpDataSourceFactory) throws UnsupportedDrmException {
        return newWidevineInstance(new HttpMediaDrmCallback(licenseUrl, httpDataSourceFactory), null);
    }

    public static OfflineLicenseHelper<FrameworkMediaCrypto> newWidevineInstance(MediaDrmCallback callback, HashMap<String, String> optionalKeyRequestParameters) throws UnsupportedDrmException {
        return new OfflineLicenseHelper(FrameworkMediaDrm.newInstance(C.WIDEVINE_UUID), callback, optionalKeyRequestParameters);
    }

    public OfflineLicenseHelper(ExoMediaDrm<T> mediaDrm, MediaDrmCallback callback, HashMap<String, String> optionalKeyRequestParameters) {
        this.handlerThread.start();
        this.conditionVariable = new ConditionVariable();
        EventListener eventListener = new EventListener() {
            public void onDrmKeysLoaded() {
                OfflineLicenseHelper.this.conditionVariable.open();
            }

            public void onDrmSessionManagerError(Exception e) {
                OfflineLicenseHelper.this.conditionVariable.open();
            }

            public void onDrmKeysRestored() {
                OfflineLicenseHelper.this.conditionVariable.open();
            }

            public void onDrmKeysRemoved() {
                OfflineLicenseHelper.this.conditionVariable.open();
            }
        };
        this.drmSessionManager = new DefaultDrmSessionManager(C.WIDEVINE_UUID, mediaDrm, callback, optionalKeyRequestParameters, new Handler(this.handlerThread.getLooper()), eventListener);
    }

    public void releaseResources() {
        this.handlerThread.quit();
    }

    public byte[] download(HttpDataSource dataSource, String manifestUriString) throws IOException, InterruptedException, DrmSessionException {
        return download(dataSource, DashUtil.loadManifest(dataSource, manifestUriString));
    }

    public byte[] download(HttpDataSource dataSource, DashManifest dashManifest) throws IOException, InterruptedException, DrmSessionException {
        if (dashManifest.getPeriodCount() < 1) {
            return null;
        }
        Period period = dashManifest.getPeriod(0);
        int adaptationSetIndex = period.getAdaptationSetIndex(2);
        if (adaptationSetIndex == -1) {
            adaptationSetIndex = period.getAdaptationSetIndex(1);
            if (adaptationSetIndex == -1) {
                return null;
            }
        }
        AdaptationSet adaptationSet = (AdaptationSet) period.adaptationSets.get(adaptationSetIndex);
        if (adaptationSet.representations.isEmpty()) {
            return null;
        }
        Representation representation = (Representation) adaptationSet.representations.get(0);
        DrmInitData drmInitData = representation.format.drmInitData;
        if (drmInitData == null) {
            Format sampleFormat = DashUtil.loadSampleFormat(dataSource, representation);
            if (sampleFormat != null) {
                drmInitData = sampleFormat.drmInitData;
            }
            if (drmInitData == null) {
                return null;
            }
        }
        blockingKeyRequest(2, null, drmInitData);
        return this.drmSessionManager.getOfflineLicenseKeySetId();
    }

    public byte[] renew(byte[] offlineLicenseKeySetId) throws DrmSessionException {
        Assertions.checkNotNull(offlineLicenseKeySetId);
        blockingKeyRequest(2, offlineLicenseKeySetId, null);
        return this.drmSessionManager.getOfflineLicenseKeySetId();
    }

    public void release(byte[] offlineLicenseKeySetId) throws DrmSessionException {
        Assertions.checkNotNull(offlineLicenseKeySetId);
        blockingKeyRequest(3, offlineLicenseKeySetId, null);
    }

    public Pair<Long, Long> getLicenseDurationRemainingSec(byte[] offlineLicenseKeySetId) throws DrmSessionException {
        Assertions.checkNotNull(offlineLicenseKeySetId);
        DrmSession<T> session = openBlockingKeyRequest(1, offlineLicenseKeySetId, null);
        Pair<Long, Long> licenseDurationRemainingSec = WidevineUtil.getLicenseDurationRemainingSec(this.drmSessionManager);
        this.drmSessionManager.releaseSession(session);
        return licenseDurationRemainingSec;
    }

    private void blockingKeyRequest(int licenseMode, byte[] offlineLicenseKeySetId, DrmInitData drmInitData) throws DrmSessionException {
        DrmSession<T> session = openBlockingKeyRequest(licenseMode, offlineLicenseKeySetId, drmInitData);
        DrmSessionException error = session.getError();
        if (error != null) {
            throw error;
        }
        this.drmSessionManager.releaseSession(session);
    }

    private DrmSession<T> openBlockingKeyRequest(int licenseMode, byte[] offlineLicenseKeySetId, DrmInitData drmInitData) {
        this.drmSessionManager.setMode(licenseMode, offlineLicenseKeySetId);
        this.conditionVariable.close();
        DrmSession<T> session = this.drmSessionManager.acquireSession(this.handlerThread.getLooper(), drmInitData);
        this.conditionVariable.block();
        return session;
    }
}
