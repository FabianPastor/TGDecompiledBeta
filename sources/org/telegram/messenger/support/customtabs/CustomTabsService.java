package org.telegram.messenger.support.customtabs;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.support.v4.util.ArrayMap;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.support.customtabs.ICustomTabsService.Stub;

public abstract class CustomTabsService extends Service {
    public static final String ACTION_CUSTOM_TABS_CONNECTION = "android.support.customtabs.action.CustomTabsService";
    public static final String KEY_URL = "android.support.customtabs.otherurls.URL";
    public static final int RESULT_FAILURE_DISALLOWED = -1;
    public static final int RESULT_FAILURE_MESSAGING_ERROR = -3;
    public static final int RESULT_FAILURE_REMOTE_ERROR = -2;
    public static final int RESULT_SUCCESS = 0;
    private Stub mBinder = new C23201();
    private final Map<IBinder, DeathRecipient> mDeathRecipientMap = new ArrayMap();

    @Retention(RetentionPolicy.SOURCE)
    public @interface Result {
    }

    /* renamed from: org.telegram.messenger.support.customtabs.CustomTabsService$1 */
    class C23201 extends Stub {
        C23201() {
        }

        public boolean warmup(long j) {
            return CustomTabsService.this.warmup(j);
        }

        public boolean newSession(org.telegram.messenger.support.customtabs.ICustomTabsCallback r6) {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
            /*
            r5 = this;
            r0 = new org.telegram.messenger.support.customtabs.CustomTabsSessionToken;
            r0.<init>(r6);
            r1 = 0;
            r2 = new org.telegram.messenger.support.customtabs.CustomTabsService$1$1;	 Catch:{ RemoteException -> 0x0031 }
            r2.<init>(r0);	 Catch:{ RemoteException -> 0x0031 }
            r3 = org.telegram.messenger.support.customtabs.CustomTabsService.this;	 Catch:{ RemoteException -> 0x0031 }
            r3 = r3.mDeathRecipientMap;	 Catch:{ RemoteException -> 0x0031 }
            monitor-enter(r3);	 Catch:{ RemoteException -> 0x0031 }
            r4 = r6.asBinder();	 Catch:{ all -> 0x002e }
            r4.linkToDeath(r2, r1);	 Catch:{ all -> 0x002e }
            r4 = org.telegram.messenger.support.customtabs.CustomTabsService.this;	 Catch:{ all -> 0x002e }
            r4 = r4.mDeathRecipientMap;	 Catch:{ all -> 0x002e }
            r6 = r6.asBinder();	 Catch:{ all -> 0x002e }
            r4.put(r6, r2);	 Catch:{ all -> 0x002e }
            monitor-exit(r3);	 Catch:{ all -> 0x002e }
            r6 = org.telegram.messenger.support.customtabs.CustomTabsService.this;	 Catch:{ RemoteException -> 0x0031 }
            r6 = r6.newSession(r0);	 Catch:{ RemoteException -> 0x0031 }
            return r6;
        L_0x002e:
            r6 = move-exception;
            monitor-exit(r3);	 Catch:{ all -> 0x002e }
            throw r6;	 Catch:{ RemoteException -> 0x0031 }
        L_0x0031:
            return r1;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.support.customtabs.CustomTabsService.1.newSession(org.telegram.messenger.support.customtabs.ICustomTabsCallback):boolean");
        }

        public boolean mayLaunchUrl(ICustomTabsCallback iCustomTabsCallback, Uri uri, Bundle bundle, List<Bundle> list) {
            return CustomTabsService.this.mayLaunchUrl(new CustomTabsSessionToken(iCustomTabsCallback), uri, bundle, list);
        }

        public Bundle extraCommand(String str, Bundle bundle) {
            return CustomTabsService.this.extraCommand(str, bundle);
        }

        public boolean updateVisuals(ICustomTabsCallback iCustomTabsCallback, Bundle bundle) {
            return CustomTabsService.this.updateVisuals(new CustomTabsSessionToken(iCustomTabsCallback), bundle);
        }

        public boolean requestPostMessageChannel(ICustomTabsCallback iCustomTabsCallback, Uri uri) {
            return CustomTabsService.this.requestPostMessageChannel(new CustomTabsSessionToken(iCustomTabsCallback), uri);
        }

        public int postMessage(ICustomTabsCallback iCustomTabsCallback, String str, Bundle bundle) {
            return CustomTabsService.this.postMessage(new CustomTabsSessionToken(iCustomTabsCallback), str, bundle);
        }
    }

    protected abstract Bundle extraCommand(String str, Bundle bundle);

    protected abstract boolean mayLaunchUrl(CustomTabsSessionToken customTabsSessionToken, Uri uri, Bundle bundle, List<Bundle> list);

    protected abstract boolean newSession(CustomTabsSessionToken customTabsSessionToken);

    protected abstract int postMessage(CustomTabsSessionToken customTabsSessionToken, String str, Bundle bundle);

    protected abstract boolean requestPostMessageChannel(CustomTabsSessionToken customTabsSessionToken, Uri uri);

    protected abstract boolean updateVisuals(CustomTabsSessionToken customTabsSessionToken, Bundle bundle);

    protected abstract boolean warmup(long j);

    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    protected boolean cleanUpSession(org.telegram.messenger.support.customtabs.CustomTabsSessionToken r4) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r3 = this;
        r0 = 0;
        r1 = r3.mDeathRecipientMap;	 Catch:{ NoSuchElementException -> 0x001e }
        monitor-enter(r1);	 Catch:{ NoSuchElementException -> 0x001e }
        r4 = r4.getCallbackBinder();	 Catch:{ all -> 0x001b }
        r2 = r3.mDeathRecipientMap;	 Catch:{ all -> 0x001b }
        r2 = r2.get(r4);	 Catch:{ all -> 0x001b }
        r2 = (android.os.IBinder.DeathRecipient) r2;	 Catch:{ all -> 0x001b }
        r4.unlinkToDeath(r2, r0);	 Catch:{ all -> 0x001b }
        r2 = r3.mDeathRecipientMap;	 Catch:{ all -> 0x001b }
        r2.remove(r4);	 Catch:{ all -> 0x001b }
        monitor-exit(r1);	 Catch:{ all -> 0x001b }
        r4 = 1;	 Catch:{ all -> 0x001b }
        return r4;	 Catch:{ all -> 0x001b }
    L_0x001b:
        r4 = move-exception;	 Catch:{ all -> 0x001b }
        monitor-exit(r1);	 Catch:{ all -> 0x001b }
        throw r4;	 Catch:{ NoSuchElementException -> 0x001e }
    L_0x001e:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.support.customtabs.CustomTabsService.cleanUpSession(org.telegram.messenger.support.customtabs.CustomTabsSessionToken):boolean");
    }
}
