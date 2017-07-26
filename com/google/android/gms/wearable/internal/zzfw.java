package com.google.android.gms.wearable.internal;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzj;
import com.google.android.gms.common.internal.zzq;
import com.google.android.gms.common.internal.zzz;
import com.google.android.gms.common.zze;
import com.google.android.gms.internal.zzbaz;
import com.google.android.gms.internal.zzbdw;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.CapabilityApi.CapabilityListener;
import com.google.android.gms.wearable.ChannelApi.ChannelListener;
import com.google.android.gms.wearable.DataApi.DataItemResult;
import com.google.android.gms.wearable.DataApi.DataListener;
import com.google.android.gms.wearable.DataApi.GetFdForAssetResult;
import com.google.android.gms.wearable.MessageApi.MessageListener;
import com.google.android.gms.wearable.NodeApi.NodeListener;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.WearableStatusCodes;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public final class zzfw extends zzz<zzdn> {
    private final zzdp<Object> zzbTg;
    private final zzdp<Object> zzbTh;
    private final zzdp<ChannelListener> zzbTi;
    private final zzdp<DataListener> zzbTj;
    private final zzdp<MessageListener> zzbTk;
    private final zzdp<NodeListener> zzbTl;
    private final zzdp<Object> zzbTm;
    private final zzdp<CapabilityListener> zzbTn;
    private final zzgh zzbTo;
    private final ExecutorService zzbrV;

    public zzfw(Context context, Looper looper, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener, zzq com_google_android_gms_common_internal_zzq) {
        this(context, looper, connectionCallbacks, onConnectionFailedListener, com_google_android_gms_common_internal_zzq, Executors.newCachedThreadPool(), zzgh.zzbz(context));
    }

    private zzfw(Context context, Looper looper, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener, zzq com_google_android_gms_common_internal_zzq, ExecutorService executorService, zzgh com_google_android_gms_wearable_internal_zzgh) {
        super(context, looper, 14, com_google_android_gms_common_internal_zzq, connectionCallbacks, onConnectionFailedListener);
        this.zzbTg = new zzdp();
        this.zzbTh = new zzdp();
        this.zzbTi = new zzdp();
        this.zzbTj = new zzdp();
        this.zzbTk = new zzdp();
        this.zzbTl = new zzdp();
        this.zzbTm = new zzdp();
        this.zzbTn = new zzdp();
        this.zzbrV = (ExecutorService) zzbo.zzu(executorService);
        this.zzbTo = com_google_android_gms_wearable_internal_zzgh;
    }

    protected final void zza(int i, IBinder iBinder, Bundle bundle, int i2) {
        if (Log.isLoggable("WearableClient", 2)) {
            Log.d("WearableClient", "onPostInitHandler: statusCode " + i);
        }
        if (i == 0) {
            this.zzbTg.zzam(iBinder);
            this.zzbTh.zzam(iBinder);
            this.zzbTi.zzam(iBinder);
            this.zzbTj.zzam(iBinder);
            this.zzbTk.zzam(iBinder);
            this.zzbTl.zzam(iBinder);
            this.zzbTm.zzam(iBinder);
            this.zzbTn.zzam(iBinder);
        }
        super.zza(i, iBinder, bundle, i2);
    }

    public final void zza(@NonNull zzj com_google_android_gms_common_internal_zzj) {
        int i = 0;
        if (!zzpe()) {
            try {
                Bundle bundle = getContext().getPackageManager().getApplicationInfo("com.google.android.wearable.app.cn", 128).metaData;
                if (bundle != null) {
                    i = bundle.getInt("com.google.android.wearable.api.version", 0);
                }
                if (i < zze.GOOGLE_PLAY_SERVICES_VERSION_CODE) {
                    Log.w("WearableClient", "Android Wear out of date. Requires API version " + zze.GOOGLE_PLAY_SERVICES_VERSION_CODE + " but found " + i);
                    Context context = getContext();
                    Context context2 = getContext();
                    Intent intent = new Intent("com.google.android.wearable.app.cn.UPDATE_ANDROID_WEAR").setPackage("com.google.android.wearable.app.cn");
                    if (context2.getPackageManager().resolveActivity(intent, 65536) == null) {
                        intent = new Intent("android.intent.action.VIEW", Uri.parse("market://details").buildUpon().appendQueryParameter(TtmlNode.ATTR_ID, "com.google.android.wearable.app.cn").build());
                    }
                    zza(com_google_android_gms_common_internal_zzj, 6, PendingIntent.getActivity(context, 0, intent, 0));
                    return;
                }
            } catch (NameNotFoundException e) {
                zza(com_google_android_gms_common_internal_zzj, 16, null);
                return;
            }
        }
        super.zza(com_google_android_gms_common_internal_zzj);
    }

    public final void zza(zzbaz<GetFdForAssetResult> com_google_android_gms_internal_zzbaz_com_google_android_gms_wearable_DataApi_GetFdForAssetResult, Asset asset) throws RemoteException {
        ((zzdn) zzrf()).zza(new zzfn(com_google_android_gms_internal_zzbaz_com_google_android_gms_wearable_DataApi_GetFdForAssetResult), asset);
    }

    public final void zza(zzbaz<Status> com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, CapabilityListener capabilityListener) throws RemoteException {
        this.zzbTn.zza(this, com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, capabilityListener);
    }

    public final void zza(zzbaz<Status> com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, CapabilityListener capabilityListener, zzbdw<CapabilityListener> com_google_android_gms_internal_zzbdw_com_google_android_gms_wearable_CapabilityApi_CapabilityListener, IntentFilter[] intentFilterArr) throws RemoteException {
        this.zzbTn.zza(this, com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, capabilityListener, zzga.zze(com_google_android_gms_internal_zzbdw_com_google_android_gms_wearable_CapabilityApi_CapabilityListener, intentFilterArr));
    }

    public final void zza(zzbaz<Status> com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, ChannelListener channelListener, zzbdw<ChannelListener> com_google_android_gms_internal_zzbdw_com_google_android_gms_wearable_ChannelApi_ChannelListener, String str, IntentFilter[] intentFilterArr) throws RemoteException {
        if (str == null) {
            this.zzbTi.zza(this, com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, channelListener, zzga.zzd(com_google_android_gms_internal_zzbdw_com_google_android_gms_wearable_ChannelApi_ChannelListener, intentFilterArr));
            return;
        }
        this.zzbTi.zza(this, com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, new zzeu(str, channelListener), zzga.zza(com_google_android_gms_internal_zzbdw_com_google_android_gms_wearable_ChannelApi_ChannelListener, str, intentFilterArr));
    }

    public final void zza(zzbaz<Status> com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, ChannelListener channelListener, String str) throws RemoteException {
        if (str == null) {
            this.zzbTi.zza(this, com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, channelListener);
            return;
        }
        this.zzbTi.zza(this, com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, new zzeu(str, channelListener));
    }

    public final void zza(zzbaz<Status> com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, DataListener dataListener) throws RemoteException {
        this.zzbTj.zza(this, com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, dataListener);
    }

    public final void zza(zzbaz<Status> com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, DataListener dataListener, zzbdw<DataListener> com_google_android_gms_internal_zzbdw_com_google_android_gms_wearable_DataApi_DataListener, IntentFilter[] intentFilterArr) throws RemoteException {
        this.zzbTj.zza(this, com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, dataListener, zzga.zza(com_google_android_gms_internal_zzbdw_com_google_android_gms_wearable_DataApi_DataListener, intentFilterArr));
    }

    public final void zza(zzbaz<Status> com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, MessageListener messageListener) throws RemoteException {
        this.zzbTk.zza(this, com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, messageListener);
    }

    public final void zza(zzbaz<Status> com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, MessageListener messageListener, zzbdw<MessageListener> com_google_android_gms_internal_zzbdw_com_google_android_gms_wearable_MessageApi_MessageListener, IntentFilter[] intentFilterArr) throws RemoteException {
        this.zzbTk.zza(this, com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, messageListener, zzga.zzb(com_google_android_gms_internal_zzbdw_com_google_android_gms_wearable_MessageApi_MessageListener, intentFilterArr));
    }

    public final void zza(zzbaz<Status> com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, NodeListener nodeListener) throws RemoteException {
        this.zzbTl.zza(this, com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, nodeListener);
    }

    public final void zza(zzbaz<Status> com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, NodeListener nodeListener, zzbdw<NodeListener> com_google_android_gms_internal_zzbdw_com_google_android_gms_wearable_NodeApi_NodeListener, IntentFilter[] intentFilterArr) throws RemoteException {
        this.zzbTl.zza(this, com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, nodeListener, zzga.zzc(com_google_android_gms_internal_zzbdw_com_google_android_gms_wearable_NodeApi_NodeListener, intentFilterArr));
    }

    public final void zza(zzbaz<DataItemResult> com_google_android_gms_internal_zzbaz_com_google_android_gms_wearable_DataApi_DataItemResult, PutDataRequest putDataRequest) throws RemoteException {
        for (Entry value : putDataRequest.getAssets().entrySet()) {
            Asset asset = (Asset) value.getValue();
            if (asset.getData() == null && asset.getDigest() == null && asset.getFd() == null && asset.getUri() == null) {
                String valueOf = String.valueOf(putDataRequest.getUri());
                String valueOf2 = String.valueOf(asset);
                throw new IllegalArgumentException(new StringBuilder((String.valueOf(valueOf).length() + 33) + String.valueOf(valueOf2).length()).append("Put for ").append(valueOf).append(" contains invalid asset: ").append(valueOf2).toString());
            }
        }
        PutDataRequest zzt = PutDataRequest.zzt(putDataRequest.getUri());
        zzt.setData(putDataRequest.getData());
        if (putDataRequest.isUrgent()) {
            zzt.setUrgent();
        }
        List arrayList = new ArrayList();
        for (Entry value2 : putDataRequest.getAssets().entrySet()) {
            Asset asset2 = (Asset) value2.getValue();
            if (asset2.getData() != null) {
                try {
                    ParcelFileDescriptor[] createPipe = ParcelFileDescriptor.createPipe();
                    if (Log.isLoggable("WearableClient", 3)) {
                        String valueOf3 = String.valueOf(asset2);
                        String valueOf4 = String.valueOf(createPipe[0]);
                        String valueOf5 = String.valueOf(createPipe[1]);
                        Log.d("WearableClient", new StringBuilder(((String.valueOf(valueOf3).length() + 61) + String.valueOf(valueOf4).length()) + String.valueOf(valueOf5).length()).append("processAssets: replacing data with FD in asset: ").append(valueOf3).append(" read:").append(valueOf4).append(" write:").append(valueOf5).toString());
                    }
                    zzt.putAsset((String) value2.getKey(), Asset.createFromFd(createPipe[0]));
                    Runnable futureTask = new FutureTask(new zzfx(this, createPipe[1], asset2.getData()));
                    arrayList.add(futureTask);
                    this.zzbrV.submit(futureTask);
                } catch (Throwable e) {
                    valueOf = String.valueOf(putDataRequest);
                    throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 60).append("Unable to create ParcelFileDescriptor for asset in request: ").append(valueOf).toString(), e);
                }
            } else if (asset2.getUri() != null) {
                try {
                    zzt.putAsset((String) value2.getKey(), Asset.createFromFd(getContext().getContentResolver().openFileDescriptor(asset2.getUri(), "r")));
                } catch (FileNotFoundException e2) {
                    new zzfr(com_google_android_gms_internal_zzbaz_com_google_android_gms_wearable_DataApi_DataItemResult, arrayList).zza(new zzem(WearableStatusCodes.ASSET_UNAVAILABLE, null));
                    String valueOf6 = String.valueOf(asset2.getUri());
                    Log.w("WearableClient", new StringBuilder(String.valueOf(valueOf6).length() + 28).append("Couldn't resolve asset URI: ").append(valueOf6).toString());
                    return;
                }
            } else {
                zzt.putAsset((String) value2.getKey(), asset2);
            }
        }
        ((zzdn) zzrf()).zza(new zzfr(com_google_android_gms_internal_zzbaz_com_google_android_gms_wearable_DataApi_DataItemResult, arrayList), zzt);
    }

    public final void zza(zzbaz<Status> com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, String str, Uri uri, long j, long j2) {
        try {
            ExecutorService executorService = this.zzbrV;
            zzbo.zzu(com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status);
            zzbo.zzu(str);
            zzbo.zzu(uri);
            zzbo.zzb(j >= 0, "startOffset is negative: %s", Long.valueOf(j));
            zzbo.zzb(j2 >= -1, "invalid length: %s", Long.valueOf(j2));
            executorService.execute(new zzfz(this, uri, com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, str, j, j2));
        } catch (RuntimeException e) {
            com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status.zzr(new Status(8));
            throw e;
        }
    }

    public final void zza(zzbaz<Status> com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, String str, Uri uri, boolean z) {
        try {
            ExecutorService executorService = this.zzbrV;
            zzbo.zzu(com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status);
            zzbo.zzu(str);
            zzbo.zzu(uri);
            executorService.execute(new zzfy(this, uri, com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status, z, str));
        } catch (RuntimeException e) {
            com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status.zzr(new Status(8));
            throw e;
        }
    }

    protected final /* synthetic */ IInterface zzd(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.wearable.internal.IWearableService");
        return queryLocalInterface instanceof zzdn ? (zzdn) queryLocalInterface : new zzdo(iBinder);
    }

    protected final String zzdb() {
        return "com.google.android.gms.wearable.BIND";
    }

    protected final String zzdc() {
        return "com.google.android.gms.wearable.internal.IWearableService";
    }

    public final boolean zzpe() {
        return !this.zzbTo.zzgm("com.google.android.wearable.app.cn");
    }

    protected final String zzqZ() {
        return this.zzbTo.zzgm("com.google.android.wearable.app.cn") ? "com.google.android.wearable.app.cn" : "com.google.android.gms";
    }
}
