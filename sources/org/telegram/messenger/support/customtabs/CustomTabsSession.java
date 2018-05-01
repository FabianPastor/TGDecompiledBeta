package org.telegram.messenger.support.customtabs;

import android.content.ComponentName;
import android.os.IBinder;

public final class CustomTabsSession {
    private static final String TAG = "CustomTabsSession";
    private final ICustomTabsCallback mCallback;
    private final ComponentName mComponentName;
    private final Object mLock = new Object();
    private final ICustomTabsService mService;

    public static CustomTabsSession createDummySessionForTesting(ComponentName componentName) {
        return new CustomTabsSession(null, new DummyCallback(), componentName);
    }

    CustomTabsSession(ICustomTabsService iCustomTabsService, ICustomTabsCallback iCustomTabsCallback, ComponentName componentName) {
        this.mService = iCustomTabsService;
        this.mCallback = iCustomTabsCallback;
        this.mComponentName = componentName;
    }

    public boolean mayLaunchUrl(android.net.Uri r3, android.os.Bundle r4, java.util.List<android.os.Bundle> r5) {
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
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r2 = this;
        r0 = r2.mService;	 Catch:{ RemoteException -> 0x0009 }
        r1 = r2.mCallback;	 Catch:{ RemoteException -> 0x0009 }
        r3 = r0.mayLaunchUrl(r1, r3, r4, r5);	 Catch:{ RemoteException -> 0x0009 }
        return r3;
    L_0x0009:
        r3 = 0;
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.support.customtabs.CustomTabsSession.mayLaunchUrl(android.net.Uri, android.os.Bundle, java.util.List):boolean");
    }

    public boolean setActionButton(android.graphics.Bitmap r3, java.lang.String r4) {
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
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r2 = this;
        r0 = new android.os.Bundle;
        r0.<init>();
        r1 = "android.support.customtabs.customaction.ICON";
        r0.putParcelable(r1, r3);
        r3 = "android.support.customtabs.customaction.DESCRIPTION";
        r0.putString(r3, r4);
        r3 = new android.os.Bundle;
        r3.<init>();
        r4 = "android.support.customtabs.extra.ACTION_BUTTON_BUNDLE";
        r3.putBundle(r4, r0);
        r4 = r2.mService;	 Catch:{ RemoteException -> 0x0022 }
        r0 = r2.mCallback;	 Catch:{ RemoteException -> 0x0022 }
        r3 = r4.updateVisuals(r0, r3);	 Catch:{ RemoteException -> 0x0022 }
        return r3;
    L_0x0022:
        r3 = 0;
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.support.customtabs.CustomTabsSession.setActionButton(android.graphics.Bitmap, java.lang.String):boolean");
    }

    public boolean setSecondaryToolbarViews(android.widget.RemoteViews r3, int[] r4, android.app.PendingIntent r5) {
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
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r2 = this;
        r0 = new android.os.Bundle;
        r0.<init>();
        r1 = "android.support.customtabs.extra.EXTRA_REMOTEVIEWS";
        r0.putParcelable(r1, r3);
        r3 = "android.support.customtabs.extra.EXTRA_REMOTEVIEWS_VIEW_IDS";
        r0.putIntArray(r3, r4);
        r3 = "android.support.customtabs.extra.EXTRA_REMOTEVIEWS_PENDINGINTENT";
        r0.putParcelable(r3, r5);
        r3 = r2.mService;	 Catch:{ RemoteException -> 0x001d }
        r4 = r2.mCallback;	 Catch:{ RemoteException -> 0x001d }
        r3 = r3.updateVisuals(r4, r0);	 Catch:{ RemoteException -> 0x001d }
        return r3;
    L_0x001d:
        r3 = 0;
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.support.customtabs.CustomTabsSession.setSecondaryToolbarViews(android.widget.RemoteViews, int[], android.app.PendingIntent):boolean");
    }

    @java.lang.Deprecated
    public boolean setToolbarItem(int r3, android.graphics.Bitmap r4, java.lang.String r5) {
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
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r2 = this;
        r0 = new android.os.Bundle;
        r0.<init>();
        r1 = "android.support.customtabs.customaction.ID";
        r0.putInt(r1, r3);
        r3 = "android.support.customtabs.customaction.ICON";
        r0.putParcelable(r3, r4);
        r3 = "android.support.customtabs.customaction.DESCRIPTION";
        r0.putString(r3, r5);
        r3 = new android.os.Bundle;
        r3.<init>();
        r4 = "android.support.customtabs.extra.ACTION_BUTTON_BUNDLE";
        r3.putBundle(r4, r0);
        r4 = r2.mService;	 Catch:{ RemoteException -> 0x0027 }
        r5 = r2.mCallback;	 Catch:{ RemoteException -> 0x0027 }
        r3 = r4.updateVisuals(r5, r3);	 Catch:{ RemoteException -> 0x0027 }
        return r3;
    L_0x0027:
        r3 = 0;
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.support.customtabs.CustomTabsSession.setToolbarItem(int, android.graphics.Bitmap, java.lang.String):boolean");
    }

    public boolean requestPostMessageChannel(android.net.Uri r3) {
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
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r2 = this;
        r0 = r2.mService;	 Catch:{ RemoteException -> 0x0009 }
        r1 = r2.mCallback;	 Catch:{ RemoteException -> 0x0009 }
        r3 = r0.requestPostMessageChannel(r1, r3);	 Catch:{ RemoteException -> 0x0009 }
        return r3;
    L_0x0009:
        r3 = 0;
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.support.customtabs.CustomTabsSession.requestPostMessageChannel(android.net.Uri):boolean");
    }

    public int postMessage(java.lang.String r4, android.os.Bundle r5) {
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
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r3 = this;
        r0 = r3.mLock;
        monitor-enter(r0);
        r1 = r3.mService;	 Catch:{ RemoteException -> 0x000f }
        r2 = r3.mCallback;	 Catch:{ RemoteException -> 0x000f }
        r4 = r1.postMessage(r2, r4, r5);	 Catch:{ RemoteException -> 0x000f }
        monitor-exit(r0);	 Catch:{ all -> 0x000d }
        return r4;	 Catch:{ all -> 0x000d }
    L_0x000d:
        r4 = move-exception;	 Catch:{ all -> 0x000d }
        goto L_0x0012;	 Catch:{ all -> 0x000d }
    L_0x000f:
        r4 = -2;	 Catch:{ all -> 0x000d }
        monitor-exit(r0);	 Catch:{ all -> 0x000d }
        return r4;	 Catch:{ all -> 0x000d }
    L_0x0012:
        monitor-exit(r0);	 Catch:{ all -> 0x000d }
        throw r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.support.customtabs.CustomTabsSession.postMessage(java.lang.String, android.os.Bundle):int");
    }

    IBinder getBinder() {
        return this.mCallback.asBinder();
    }

    ComponentName getComponentName() {
        return this.mComponentName;
    }
}
