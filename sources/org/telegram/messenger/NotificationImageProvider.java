package org.telegram.messenger;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import java.util.HashMap;
import java.util.HashSet;
import org.telegram.messenger.NotificationCenter;

public class NotificationImageProvider extends ContentProvider implements NotificationCenter.NotificationCenterDelegate {
    public static final String AUTHORITY = "org.telegram.messenger.notification_image_provider";
    private static final UriMatcher matcher;
    private HashMap<String, Long> fileStartTimes = new HashMap<>();
    private final Object sync = new Object();
    private HashSet<String> waitingForFiles = new HashSet<>();

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
        UriMatcher uriMatcher = new UriMatcher(-1);
        matcher = uriMatcher;
        uriMatcher.addURI("org.telegram.messenger.notification_image_provider", "msg_media_raw/#/*", 1);
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

    /* JADX WARNING: Can't wrap try/catch for region: R(6:33|34|35|36|37|38) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:37:0x00d0 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.os.ParcelFileDescriptor openFile(android.net.Uri r10, java.lang.String r11) throws java.io.FileNotFoundException {
        /*
            r9 = this;
            java.lang.String r0 = "r"
            boolean r11 = r0.equals(r11)
            if (r11 == 0) goto L_0x00fd
            android.content.UriMatcher r11 = matcher
            int r11 = r11.match(r10)
            r0 = 1
            if (r11 != r0) goto L_0x00f5
            java.util.List r11 = r10.getPathSegments()
            java.lang.Object r0 = r11.get(r0)
            java.lang.String r0 = (java.lang.String) r0
            java.lang.Integer.parseInt(r0)
            r0 = 2
            java.lang.Object r11 = r11.get(r0)
            java.lang.String r11 = (java.lang.String) r11
            java.lang.String r0 = "final_path"
            java.lang.String r0 = r10.getQueryParameter(r0)
            java.lang.String r1 = "fallback"
            java.lang.String r10 = r10.getQueryParameter(r1)
            java.io.File r1 = new java.io.File
            r1.<init>(r0)
            org.telegram.messenger.ApplicationLoader.postInitApplication()
            android.net.Uri r0 = android.net.Uri.fromFile(r1)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r0)
            if (r0 != 0) goto L_0x00ed
            boolean r0 = r1.exists()
            r2 = 268435456(0x10000000, float:2.5243549E-29)
            if (r0 != 0) goto L_0x00e8
            java.util.HashMap<java.lang.String, java.lang.Long> r0 = r9.fileStartTimes
            java.lang.Object r0 = r0.get(r11)
            java.lang.Long r0 = (java.lang.Long) r0
            if (r0 == 0) goto L_0x005a
            long r3 = r0.longValue()
            goto L_0x005e
        L_0x005a:
            long r3 = java.lang.System.currentTimeMillis()
        L_0x005e:
            if (r0 != 0) goto L_0x0069
            java.util.HashMap<java.lang.String, java.lang.Long> r0 = r9.fileStartTimes
            java.lang.Long r5 = java.lang.Long.valueOf(r3)
            r0.put(r11, r5)
        L_0x0069:
            boolean r0 = r1.exists()
            if (r0 != 0) goto L_0x00d5
            long r5 = java.lang.System.currentTimeMillis()
            long r5 = r5 - r3
            r7 = 3000(0xbb8, double:1.482E-320)
            int r0 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r0 < 0) goto L_0x00c1
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0097
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Waiting for "
            r0.append(r1)
            r0.append(r11)
            java.lang.String r11 = " to download timed out"
            r0.append(r11)
            java.lang.String r11 = r0.toString()
            org.telegram.messenger.FileLog.w(r11)
        L_0x0097:
            boolean r11 = android.text.TextUtils.isEmpty(r10)
            if (r11 != 0) goto L_0x00b9
            java.io.File r11 = new java.io.File
            r11.<init>(r10)
            android.net.Uri r10 = android.net.Uri.fromFile(r11)
            boolean r10 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r10)
            if (r10 != 0) goto L_0x00b1
            android.os.ParcelFileDescriptor r10 = android.os.ParcelFileDescriptor.open(r11, r2)
            return r10
        L_0x00b1:
            java.lang.SecurityException r10 = new java.lang.SecurityException
            java.lang.String r11 = "trying to read internal file"
            r10.<init>(r11)
            throw r10
        L_0x00b9:
            java.io.FileNotFoundException r10 = new java.io.FileNotFoundException
            java.lang.String r11 = "Download timed out"
            r10.<init>(r11)
            throw r10
        L_0x00c1:
            java.lang.Object r0 = r9.sync
            monitor-enter(r0)
            java.util.HashSet<java.lang.String> r5 = r9.waitingForFiles     // Catch:{ all -> 0x00d2 }
            r5.add(r11)     // Catch:{ all -> 0x00d2 }
            java.lang.Object r5 = r9.sync     // Catch:{ InterruptedException -> 0x00d0 }
            r6 = 1000(0x3e8, double:4.94E-321)
            r5.wait(r6)     // Catch:{ InterruptedException -> 0x00d0 }
        L_0x00d0:
            monitor-exit(r0)     // Catch:{ all -> 0x00d2 }
            goto L_0x0069
        L_0x00d2:
            r10 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00d2 }
            throw r10
        L_0x00d5:
            android.net.Uri r10 = android.net.Uri.fromFile(r1)
            boolean r10 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r10)
            if (r10 != 0) goto L_0x00e0
            goto L_0x00e8
        L_0x00e0:
            java.lang.SecurityException r10 = new java.lang.SecurityException
            java.lang.String r11 = "trying to read internal file"
            r10.<init>(r11)
            throw r10
        L_0x00e8:
            android.os.ParcelFileDescriptor r10 = android.os.ParcelFileDescriptor.open(r1, r2)
            return r10
        L_0x00ed:
            java.lang.SecurityException r10 = new java.lang.SecurityException
            java.lang.String r11 = "trying to read internal file"
            r10.<init>(r11)
            throw r10
        L_0x00f5:
            java.io.FileNotFoundException r10 = new java.io.FileNotFoundException
            java.lang.String r11 = "Invalid URI"
            r10.<init>(r11)
            throw r10
        L_0x00fd:
            java.lang.SecurityException r10 = new java.lang.SecurityException
            java.lang.String r11 = "Can only open files for read"
            r10.<init>(r11)
            goto L_0x0106
        L_0x0105:
            throw r10
        L_0x0106:
            goto L_0x0105
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationImageProvider.openFile(android.net.Uri, java.lang.String):android.os.ParcelFileDescriptor");
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.fileDidLoad) {
            synchronized (this.sync) {
                String str = objArr[0];
                if (this.waitingForFiles.remove(str)) {
                    this.fileStartTimes.remove(str);
                    this.sync.notifyAll();
                }
            }
        }
    }
}
