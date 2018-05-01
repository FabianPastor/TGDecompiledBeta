package org.telegram.messenger.support.customtabs;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import org.telegram.messenger.support.customtabs.ICustomTabsCallback.Stub;

public abstract class PostMessageServiceConnection implements ServiceConnection {
    private final Object mLock = new Object();
    private IPostMessageService mService;
    private final ICustomTabsCallback mSessionBinder;

    public void onPostMessageServiceConnected() {
    }

    public void onPostMessageServiceDisconnected() {
    }

    public PostMessageServiceConnection(CustomTabsSessionToken customTabsSessionToken) {
        this.mSessionBinder = Stub.asInterface(customTabsSessionToken.getCallbackBinder());
    }

    public boolean bindSessionToPostMessageService(Context context, String str) {
        Intent intent = new Intent();
        intent.setClassName(str, PostMessageService.class.getName());
        return context.bindService(intent, this, 1);
    }

    public void unbindFromContext(Context context) {
        context.unbindService(this);
    }

    public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        this.mService = IPostMessageService.Stub.asInterface(iBinder);
        onPostMessageServiceConnected();
    }

    public final void onServiceDisconnected(ComponentName componentName) {
        this.mService = null;
        onPostMessageServiceDisconnected();
    }

    public final boolean notifyMessageChannelReady(android.os.Bundle r5) {
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
        r4 = this;
        r0 = r4.mService;
        r1 = 0;
        if (r0 != 0) goto L_0x0006;
    L_0x0005:
        return r1;
    L_0x0006:
        r0 = r4.mLock;
        monitor-enter(r0);
        r2 = r4.mService;	 Catch:{ RemoteException -> 0x0015 }
        r3 = r4.mSessionBinder;	 Catch:{ RemoteException -> 0x0015 }
        r2.onMessageChannelReady(r3, r5);	 Catch:{ RemoteException -> 0x0015 }
        monitor-exit(r0);	 Catch:{ all -> 0x0013 }
        r5 = 1;	 Catch:{ all -> 0x0013 }
        return r5;	 Catch:{ all -> 0x0013 }
    L_0x0013:
        r5 = move-exception;	 Catch:{ all -> 0x0013 }
        goto L_0x0017;	 Catch:{ all -> 0x0013 }
    L_0x0015:
        monitor-exit(r0);	 Catch:{ all -> 0x0013 }
        return r1;	 Catch:{ all -> 0x0013 }
    L_0x0017:
        monitor-exit(r0);	 Catch:{ all -> 0x0013 }
        throw r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.support.customtabs.PostMessageServiceConnection.notifyMessageChannelReady(android.os.Bundle):boolean");
    }

    public final boolean postMessage(java.lang.String r5, android.os.Bundle r6) {
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
        r4 = this;
        r0 = r4.mService;
        r1 = 0;
        if (r0 != 0) goto L_0x0006;
    L_0x0005:
        return r1;
    L_0x0006:
        r0 = r4.mLock;
        monitor-enter(r0);
        r2 = r4.mService;	 Catch:{ RemoteException -> 0x0015 }
        r3 = r4.mSessionBinder;	 Catch:{ RemoteException -> 0x0015 }
        r2.onPostMessage(r3, r5, r6);	 Catch:{ RemoteException -> 0x0015 }
        monitor-exit(r0);	 Catch:{ all -> 0x0013 }
        r5 = 1;	 Catch:{ all -> 0x0013 }
        return r5;	 Catch:{ all -> 0x0013 }
    L_0x0013:
        r5 = move-exception;	 Catch:{ all -> 0x0013 }
        goto L_0x0017;	 Catch:{ all -> 0x0013 }
    L_0x0015:
        monitor-exit(r0);	 Catch:{ all -> 0x0013 }
        return r1;	 Catch:{ all -> 0x0013 }
    L_0x0017:
        monitor-exit(r0);	 Catch:{ all -> 0x0013 }
        throw r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.support.customtabs.PostMessageServiceConnection.postMessage(java.lang.String, android.os.Bundle):boolean");
    }
}
