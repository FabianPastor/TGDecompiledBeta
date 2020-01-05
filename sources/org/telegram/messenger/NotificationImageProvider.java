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
    public static final String AUTHORITY = "org.telegram.messenger.notification_image_provider";
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

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return null;
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }

    static {
        matcher.addURI("org.telegram.messenger.notification_image_provider", "msg_media_raw/#/*", 1);
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

    /* JADX WARNING: Missing exception handler attribute for start block: B:39:0x00d6 */
    /* JADX WARNING: Can't wrap try/catch for region: R(6:35|36|37|38|39|40) */
    public android.os.ParcelFileDescriptor openFile(android.net.Uri r10, java.lang.String r11) throws java.io.FileNotFoundException {
        /*
        r9 = this;
        r0 = "r";
        r11 = r0.equals(r11);
        if (r11 == 0) goto L_0x0104;
    L_0x0008:
        r11 = matcher;
        r11 = r11.match(r10);
        r0 = 1;
        if (r11 != r0) goto L_0x00fc;
    L_0x0011:
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
        org.telegram.messenger.ApplicationLoader.postInitApplication();
        r0 = android.net.Uri.fromFile(r1);
        r0 = org.telegram.messenger.AndroidUtilities.isInternalUri(r0);
        if (r0 != 0) goto L_0x00f3;
    L_0x0043:
        r0 = r1.exists();
        r2 = NUM; // 0x10000000 float:2.5243549E-29 double:1.32624737E-315;
        if (r0 == 0) goto L_0x0050;
    L_0x004b:
        r10 = android.os.ParcelFileDescriptor.open(r1, r2);
        return r10;
    L_0x0050:
        r0 = r9.fileStartTimes;
        r0 = r0.get(r11);
        r0 = (java.lang.Long) r0;
        if (r0 == 0) goto L_0x005f;
    L_0x005a:
        r3 = r0.longValue();
        goto L_0x0063;
    L_0x005f:
        r3 = java.lang.System.currentTimeMillis();
    L_0x0063:
        if (r0 != 0) goto L_0x006e;
    L_0x0065:
        r0 = r9.fileStartTimes;
        r5 = java.lang.Long.valueOf(r3);
        r0.put(r11, r5);
    L_0x006e:
        r0 = r1.exists();
        if (r0 != 0) goto L_0x00db;
    L_0x0074:
        r5 = java.lang.System.currentTimeMillis();
        r5 = r5 - r3;
        r7 = 3000; // 0xbb8 float:4.204E-42 double:1.482E-320;
        r0 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r0 < 0) goto L_0x00c7;
    L_0x007f:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x009c;
    L_0x0083:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "Waiting for ";
        r0.append(r1);
        r0.append(r11);
        r11 = " to download timed out";
        r0.append(r11);
        r11 = r0.toString();
        org.telegram.messenger.FileLog.w(r11);
    L_0x009c:
        r11 = android.text.TextUtils.isEmpty(r10);
        if (r11 != 0) goto L_0x00bf;
    L_0x00a2:
        r11 = new java.io.File;
        r11.<init>(r10);
        r10 = android.net.Uri.fromFile(r11);
        r10 = org.telegram.messenger.AndroidUtilities.isInternalUri(r10);
        if (r10 != 0) goto L_0x00b6;
    L_0x00b1:
        r10 = android.os.ParcelFileDescriptor.open(r11, r2);
        return r10;
    L_0x00b6:
        r10 = new java.lang.SecurityException;
        r11 = "trying to read internal file";
        r10.<init>(r11);
        throw r10;
    L_0x00bf:
        r10 = new java.io.FileNotFoundException;
        r11 = "Download timed out";
        r10.<init>(r11);
        throw r10;
    L_0x00c7:
        r0 = r9.sync;
        monitor-enter(r0);
        r5 = r9.waitingForFiles;	 Catch:{ all -> 0x00d8 }
        r5.add(r11);	 Catch:{ all -> 0x00d8 }
        r5 = r9.sync;	 Catch:{ InterruptedException -> 0x00d6 }
        r6 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r5.wait(r6);	 Catch:{ InterruptedException -> 0x00d6 }
    L_0x00d6:
        monitor-exit(r0);	 Catch:{ all -> 0x00d8 }
        goto L_0x006e;
    L_0x00d8:
        r10 = move-exception;
        monitor-exit(r0);	 Catch:{ all -> 0x00d8 }
        throw r10;
    L_0x00db:
        r10 = android.net.Uri.fromFile(r1);
        r10 = org.telegram.messenger.AndroidUtilities.isInternalUri(r10);
        if (r10 != 0) goto L_0x00ea;
    L_0x00e5:
        r10 = android.os.ParcelFileDescriptor.open(r1, r2);
        return r10;
    L_0x00ea:
        r10 = new java.lang.SecurityException;
        r11 = "trying to read internal file";
        r10.<init>(r11);
        throw r10;
    L_0x00f3:
        r10 = new java.lang.SecurityException;
        r11 = "trying to read internal file";
        r10.<init>(r11);
        throw r10;
    L_0x00fc:
        r10 = new java.io.FileNotFoundException;
        r11 = "Invalid URI";
        r10.<init>(r11);
        throw r10;
    L_0x0104:
        r10 = new java.lang.SecurityException;
        r11 = "Can only open files for read";
        r10.<init>(r11);
        goto L_0x010d;
    L_0x010c:
        throw r10;
    L_0x010d:
        goto L_0x010c;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationImageProvider.openFile(android.net.Uri, java.lang.String):android.os.ParcelFileDescriptor");
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
