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
    public static final String AUTHORITY = "org.telegram.messenger.beta.notification_image_provider";
    private static final UriMatcher matcher;
    private HashMap<String, Long> fileStartTimes = new HashMap<>();
    private final Object sync = new Object();
    private HashSet<String> waitingForFiles = new HashSet<>();

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        matcher = uriMatcher;
        uriMatcher.addURI("org.telegram.messenger.beta.notification_image_provider", "msg_media_raw/#/*", 1);
    }

    public boolean onCreate() {
        for (int i = 0; i < UserConfig.getActivatedAccountsCount(); i++) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.fileLoaded);
        }
        return true;
    }

    public void shutdown() {
        for (int i = 0; i < UserConfig.getActivatedAccountsCount(); i++) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.fileLoaded);
        }
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    public String[] getStreamTypes(Uri uri, String mimeTypeFilter) {
        if (!mimeTypeFilter.startsWith("*/") && !mimeTypeFilter.startsWith("image/")) {
            return null;
        }
        return new String[]{"image/jpeg", "image/png", "image/webp"};
    }

    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00e1, code lost:
        r11 = r15;
        r10 = NUM;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.os.ParcelFileDescriptor openFile(android.net.Uri r19, java.lang.String r20) throws java.io.FileNotFoundException {
        /*
            r18 = this;
            r1 = r18
            r2 = r19
            java.lang.String r0 = "r"
            r3 = r20
            boolean r0 = r0.equals(r3)
            if (r0 == 0) goto L_0x0116
            android.content.UriMatcher r0 = matcher
            int r0 = r0.match(r2)
            r4 = 1
            if (r0 != r4) goto L_0x010e
            java.util.List r5 = r19.getPathSegments()
            java.lang.Object r0 = r5.get(r4)
            java.lang.String r0 = (java.lang.String) r0
            int r4 = java.lang.Integer.parseInt(r0)
            r0 = 2
            java.lang.Object r0 = r5.get(r0)
            r6 = r0
            java.lang.String r6 = (java.lang.String) r6
            java.lang.String r0 = "final_path"
            java.lang.String r7 = r2.getQueryParameter(r0)
            java.lang.String r0 = "fallback"
            java.lang.String r8 = r2.getQueryParameter(r0)
            java.io.File r0 = new java.io.File
            r0.<init>(r7)
            r9 = r0
            org.telegram.messenger.ApplicationLoader.postInitApplication()
            android.net.Uri r0 = android.net.Uri.fromFile(r9)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r0)
            if (r0 != 0) goto L_0x0106
            boolean r0 = r9.exists()
            r10 = 268435456(0x10000000, float:2.5243549E-29)
            if (r0 != 0) goto L_0x00ff
            java.util.HashMap<java.lang.String, java.lang.Long> r0 = r1.fileStartTimes
            java.lang.Object r0 = r0.get(r6)
            r11 = r0
            java.lang.Long r11 = (java.lang.Long) r11
            if (r11 == 0) goto L_0x0064
            long r12 = r11.longValue()
            goto L_0x0068
        L_0x0064:
            long r12 = java.lang.System.currentTimeMillis()
        L_0x0068:
            if (r11 != 0) goto L_0x0073
            java.util.HashMap<java.lang.String, java.lang.Long> r0 = r1.fileStartTimes
            java.lang.Long r14 = java.lang.Long.valueOf(r12)
            r0.put(r6, r14)
        L_0x0073:
            boolean r0 = r9.exists()
            if (r0 != 0) goto L_0x00eb
            long r14 = java.lang.System.currentTimeMillis()
            long r14 = r14 - r12
            r16 = 3000(0xbb8, double:1.482E-320)
            int r0 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r0 < 0) goto L_0x00cb
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x00a1
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r14 = "Waiting for "
            r0.append(r14)
            r0.append(r6)
            java.lang.String r14 = " to download timed out"
            r0.append(r14)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
        L_0x00a1:
            boolean r0 = android.text.TextUtils.isEmpty(r8)
            if (r0 != 0) goto L_0x00c3
            java.io.File r0 = new java.io.File
            r0.<init>(r8)
            android.net.Uri r14 = android.net.Uri.fromFile(r0)
            boolean r14 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r14)
            if (r14 != 0) goto L_0x00bb
            android.os.ParcelFileDescriptor r10 = android.os.ParcelFileDescriptor.open(r0, r10)
            return r10
        L_0x00bb:
            java.lang.SecurityException r10 = new java.lang.SecurityException
            java.lang.String r14 = "trying to read internal file"
            r10.<init>(r14)
            throw r10
        L_0x00c3:
            java.io.FileNotFoundException r0 = new java.io.FileNotFoundException
            java.lang.String r10 = "Download timed out"
            r0.<init>(r10)
            throw r0
        L_0x00cb:
            java.lang.Object r14 = r1.sync
            monitor-enter(r14)
            java.util.HashSet<java.lang.String> r0 = r1.waitingForFiles     // Catch:{ all -> 0x00e5 }
            r0.add(r6)     // Catch:{ all -> 0x00e5 }
            java.lang.Object r0 = r1.sync     // Catch:{ InterruptedException -> 0x00de }
            r15 = r11
            r10 = 1000(0x3e8, double:4.94E-321)
            r0.wait(r10)     // Catch:{ InterruptedException -> 0x00dc }
            goto L_0x00e0
        L_0x00dc:
            r0 = move-exception
            goto L_0x00e0
        L_0x00de:
            r0 = move-exception
            r15 = r11
        L_0x00e0:
            monitor-exit(r14)     // Catch:{ all -> 0x00e9 }
            r11 = r15
            r10 = 268435456(0x10000000, float:2.5243549E-29)
            goto L_0x0073
        L_0x00e5:
            r0 = move-exception
            r15 = r11
        L_0x00e7:
            monitor-exit(r14)     // Catch:{ all -> 0x00e9 }
            throw r0
        L_0x00e9:
            r0 = move-exception
            goto L_0x00e7
        L_0x00eb:
            r15 = r11
            android.net.Uri r0 = android.net.Uri.fromFile(r9)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r0)
            if (r0 != 0) goto L_0x00f7
            goto L_0x00ff
        L_0x00f7:
            java.lang.SecurityException r0 = new java.lang.SecurityException
            java.lang.String r10 = "trying to read internal file"
            r0.<init>(r10)
            throw r0
        L_0x00ff:
            r10 = 268435456(0x10000000, float:2.5243549E-29)
            android.os.ParcelFileDescriptor r0 = android.os.ParcelFileDescriptor.open(r9, r10)
            return r0
        L_0x0106:
            java.lang.SecurityException r0 = new java.lang.SecurityException
            java.lang.String r10 = "trying to read internal file"
            r0.<init>(r10)
            throw r0
        L_0x010e:
            java.io.FileNotFoundException r0 = new java.io.FileNotFoundException
            java.lang.String r4 = "Invalid URI"
            r0.<init>(r4)
            throw r0
        L_0x0116:
            java.lang.SecurityException r0 = new java.lang.SecurityException
            java.lang.String r4 = "Can only open files for read"
            r0.<init>(r4)
            goto L_0x011f
        L_0x011e:
            throw r0
        L_0x011f:
            goto L_0x011e
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationImageProvider.openFile(android.net.Uri, java.lang.String):android.os.ParcelFileDescriptor");
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.fileLoaded) {
            synchronized (this.sync) {
                String name = args[0];
                if (this.waitingForFiles.remove(name)) {
                    this.fileStartTimes.remove(name);
                    this.sync.notifyAll();
                }
            }
        }
    }
}
