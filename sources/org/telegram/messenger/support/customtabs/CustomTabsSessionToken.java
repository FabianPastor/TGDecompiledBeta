package org.telegram.messenger.support.customtabs;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.BundleCompat;
import org.telegram.messenger.support.customtabs.ICustomTabsCallback.Stub;

public class CustomTabsSessionToken {
    private static final String TAG = "CustomTabsSessionToken";
    private final CustomTabsCallback mCallback = new C18601();
    private final ICustomTabsCallback mCallbackBinder;

    /* renamed from: org.telegram.messenger.support.customtabs.CustomTabsSessionToken$1 */
    class C18601 extends CustomTabsCallback {
        C18601() {
        }

        public void onNavigationEvent(int r2, android.os.Bundle r3) {
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
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
            /*
            r1 = this;
            r0 = org.telegram.messenger.support.customtabs.CustomTabsSessionToken.this;	 Catch:{ RemoteException -> 0x000a }
            r0 = r0.mCallbackBinder;	 Catch:{ RemoteException -> 0x000a }
            r0.onNavigationEvent(r2, r3);	 Catch:{ RemoteException -> 0x000a }
            goto L_0x0011;
        L_0x000a:
            r2 = "CustomTabsSessionToken";
            r3 = "RemoteException during ICustomTabsCallback transaction";
            android.util.Log.e(r2, r3);
        L_0x0011:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.support.customtabs.CustomTabsSessionToken.1.onNavigationEvent(int, android.os.Bundle):void");
        }

        public void extraCallback(java.lang.String r2, android.os.Bundle r3) {
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
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
            /*
            r1 = this;
            r0 = org.telegram.messenger.support.customtabs.CustomTabsSessionToken.this;	 Catch:{ RemoteException -> 0x000a }
            r0 = r0.mCallbackBinder;	 Catch:{ RemoteException -> 0x000a }
            r0.extraCallback(r2, r3);	 Catch:{ RemoteException -> 0x000a }
            goto L_0x0011;
        L_0x000a:
            r2 = "CustomTabsSessionToken";
            r3 = "RemoteException during ICustomTabsCallback transaction";
            android.util.Log.e(r2, r3);
        L_0x0011:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.support.customtabs.CustomTabsSessionToken.1.extraCallback(java.lang.String, android.os.Bundle):void");
        }

        public void onMessageChannelReady(android.os.Bundle r2) {
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
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
            /*
            r1 = this;
            r0 = org.telegram.messenger.support.customtabs.CustomTabsSessionToken.this;	 Catch:{ RemoteException -> 0x000a }
            r0 = r0.mCallbackBinder;	 Catch:{ RemoteException -> 0x000a }
            r0.onMessageChannelReady(r2);	 Catch:{ RemoteException -> 0x000a }
            goto L_0x0011;
        L_0x000a:
            r2 = "CustomTabsSessionToken";
            r0 = "RemoteException during ICustomTabsCallback transaction";
            android.util.Log.e(r2, r0);
        L_0x0011:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.support.customtabs.CustomTabsSessionToken.1.onMessageChannelReady(android.os.Bundle):void");
        }

        public void onPostMessage(java.lang.String r2, android.os.Bundle r3) {
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
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
            /*
            r1 = this;
            r0 = org.telegram.messenger.support.customtabs.CustomTabsSessionToken.this;	 Catch:{ RemoteException -> 0x000a }
            r0 = r0.mCallbackBinder;	 Catch:{ RemoteException -> 0x000a }
            r0.onPostMessage(r2, r3);	 Catch:{ RemoteException -> 0x000a }
            goto L_0x0011;
        L_0x000a:
            r2 = "CustomTabsSessionToken";
            r3 = "RemoteException during ICustomTabsCallback transaction";
            android.util.Log.e(r2, r3);
        L_0x0011:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.support.customtabs.CustomTabsSessionToken.1.onPostMessage(java.lang.String, android.os.Bundle):void");
        }
    }

    static class DummyCallback extends Stub {
        public IBinder asBinder() {
            return this;
        }

        public void extraCallback(String str, Bundle bundle) {
        }

        public void onMessageChannelReady(Bundle bundle) {
        }

        public void onNavigationEvent(int i, Bundle bundle) {
        }

        public void onPostMessage(String str, Bundle bundle) {
        }

        DummyCallback() {
        }
    }

    public static CustomTabsSessionToken getSessionTokenFromIntent(Intent intent) {
        intent = BundleCompat.getBinder(intent.getExtras(), CustomTabsIntent.EXTRA_SESSION);
        if (intent == null) {
            return null;
        }
        return new CustomTabsSessionToken(Stub.asInterface(intent));
    }

    public static CustomTabsSessionToken createDummySessionTokenForTesting() {
        return new CustomTabsSessionToken(new DummyCallback());
    }

    CustomTabsSessionToken(ICustomTabsCallback iCustomTabsCallback) {
        this.mCallbackBinder = iCustomTabsCallback;
    }

    IBinder getCallbackBinder() {
        return this.mCallbackBinder.asBinder();
    }

    public int hashCode() {
        return getCallbackBinder().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof CustomTabsSessionToken) {
            return ((CustomTabsSessionToken) obj).getCallbackBinder().equals(this.mCallbackBinder.asBinder());
        }
        return null;
    }

    public CustomTabsCallback getCallback() {
        return this.mCallback;
    }

    public boolean isAssociatedWith(CustomTabsSession customTabsSession) {
        return customTabsSession.getBinder().equals(this.mCallbackBinder);
    }
}
