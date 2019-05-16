package org.telegram.messenger;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import java.util.HashMap;
import java.util.HashSet;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;

public class NotificationImageProvider extends ContentProvider implements NotificationCenterDelegate {
    public static final String AUTHORITY = "org.telegram.messenger.beta.notification_image_provider";
    private static final UriMatcher matcher = new UriMatcher(-1);
    private HashMap<String, Long> fileStartTimes = new HashMap();
    private final Object sync = new Object();
    private HashSet<String> waitingForFiles = new HashSet();

    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:45:0x00e4 in {7, 10, 11, 13, 20, 24, 26, 32, 35, 38, 40, 42, 44} preds:[]
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:242)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:42)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    public android.os.ParcelFileDescriptor openFile(android.net.Uri r10, java.lang.String r11) throws java.io.FileNotFoundException {
        /*
        r9 = this;
        r0 = "r";
        r11 = r0.equals(r11);
        if (r11 == 0) goto L_0x00dc;
        r11 = matcher;
        r11 = r11.match(r10);
        r0 = 1;
        if (r11 != r0) goto L_0x00d4;
        r11 = r10.getPathSegments();
        r0 = r11.get(r0);
        r0 = (java.lang.String) r0;
        java.lang.Integer.parseInt(r0);
        r0 = 2;
        r11 = r11.get(r0);
        r11 = (java.lang.String) r11;
        r0 = "final_path";
        r0 = r10.getQueryParameter(r0);
        r1 = "fallback";
        r10 = r10.getQueryParameter(r1);
        r1 = new java.io.File;
        r1.<init>(r0);
        r0 = r1.exists();
        r2 = NUM; // 0x10000000 float:2.5243549E-29 double:1.32624737E-315;
        if (r0 == 0) goto L_0x0057;
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r10.append(r1);
        r11 = " already exists";
        r10.append(r11);
        r10 = r10.toString();
        org.telegram.messenger.FileLog.d(r10);
        r10 = android.os.ParcelFileDescriptor.open(r1, r2);
        return r10;
        r0 = r9.fileStartTimes;
        r0 = r0.get(r11);
        r0 = (java.lang.Long) r0;
        if (r0 == 0) goto L_0x0066;
        r3 = r0.longValue();
        goto L_0x006a;
        r3 = java.lang.System.currentTimeMillis();
        if (r0 != 0) goto L_0x0075;
        r0 = r9.fileStartTimes;
        r5 = java.lang.Long.valueOf(r3);
        r0.put(r11, r5);
        r0 = r1.exists();
        if (r0 != 0) goto L_0x00cf;
        r5 = java.lang.System.currentTimeMillis();
        r5 = r5 - r3;
        r7 = 3000; // 0xbb8 float:4.204E-42 double:1.482E-320;
        r0 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r0 < 0) goto L_0x00bb;
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x00a3;
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "Waiting for ";
        r0.append(r1);
        r0.append(r11);
        r11 = " to download timed out";
        r0.append(r11);
        r11 = r0.toString();
        org.telegram.messenger.FileLog.w(r11);
        r11 = android.text.TextUtils.isEmpty(r10);
        if (r11 != 0) goto L_0x00b3;
        r11 = new java.io.File;
        r11.<init>(r10);
        r10 = android.os.ParcelFileDescriptor.open(r11, r2);
        return r10;
        r10 = new java.io.FileNotFoundException;
        r11 = "Download timed out";
        r10.<init>(r11);
        throw r10;
        r0 = r9.sync;
        monitor-enter(r0);
        r5 = r9.waitingForFiles;	 Catch:{ all -> 0x00cc }
        r5.add(r11);	 Catch:{ all -> 0x00cc }
        r5 = r9.sync;	 Catch:{ InterruptedException -> 0x00ca }
        r6 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;	 Catch:{ InterruptedException -> 0x00ca }
        r5.wait(r6);	 Catch:{ InterruptedException -> 0x00ca }
    L_0x00ca:
        monitor-exit(r0);	 Catch:{ all -> 0x00cc }
        goto L_0x0075;	 Catch:{ all -> 0x00cc }
        r10 = move-exception;	 Catch:{ all -> 0x00cc }
        monitor-exit(r0);	 Catch:{ all -> 0x00cc }
        throw r10;
        r10 = android.os.ParcelFileDescriptor.open(r1, r2);
        return r10;
        r10 = new java.io.FileNotFoundException;
        r11 = "Invalid URI";
        r10.<init>(r11);
        throw r10;
        r10 = new java.lang.SecurityException;
        r11 = "Can only open files for read";
        r10.<init>(r11);
        throw r10;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationImageProvider.openFile(android.net.Uri, java.lang.String):android.os.ParcelFileDescriptor");
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return null;
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }

    static {
        matcher.addURI("org.telegram.messenger.beta.notification_image_provider", "msg_media_raw/#/*", 1);
    }

    public boolean onCreate() {
        for (int i = 0; i < UserConfig.getActivatedAccountsCount(); i++) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.fileDidLoad);
        }
        return true;
    }

    public void shutdown() {
        for (int i = 0; i < UserConfig.getActivatedAccountsCount(); i++) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.fileDidLoad);
        }
    }

    public String[] getStreamTypes(Uri uri, String str) {
        if (!str.startsWith("*/") && !str.startsWith("image/")) {
            return null;
        }
        return new String[]{"image/jpeg", "image/png", "image/webp"};
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.fileDidLoad) {
            synchronized (this.sync) {
                String str = (String) objArr[0];
                if (this.waitingForFiles.remove(str)) {
                    this.fileStartTimes.remove(str);
                    this.sync.notifyAll();
                }
            }
        }
    }
}
