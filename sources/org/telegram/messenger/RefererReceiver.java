package org.telegram.messenger;

import android.content.BroadcastReceiver;

public class RefererReceiver extends BroadcastReceiver {
    public void onReceive(android.content.Context r2, android.content.Intent r3) {
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
        r1 = this;
        r2 = org.telegram.messenger.UserConfig.selectedAccount;	 Catch:{ Exception -> 0x0013 }
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);	 Catch:{ Exception -> 0x0013 }
        r3 = r3.getExtras();	 Catch:{ Exception -> 0x0013 }
        r0 = "referrer";	 Catch:{ Exception -> 0x0013 }
        r3 = r3.getString(r0);	 Catch:{ Exception -> 0x0013 }
        r2.setReferer(r3);	 Catch:{ Exception -> 0x0013 }
    L_0x0013:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.RefererReceiver.onReceive(android.content.Context, android.content.Intent):void");
    }
}
