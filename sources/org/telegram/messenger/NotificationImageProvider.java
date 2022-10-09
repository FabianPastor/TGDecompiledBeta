package org.telegram.messenger;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.telegram.messenger.NotificationCenter;
/* loaded from: classes.dex */
public class NotificationImageProvider extends ContentProvider implements NotificationCenter.NotificationCenterDelegate {
    private static String authority;
    private static UriMatcher matcher;
    private HashSet<String> waitingForFiles = new HashSet<>();
    private final Object sync = new Object();
    private HashMap<String, Long> fileStartTimes = new HashMap<>();

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return null;
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return null;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }

    public static String getAuthority() {
        if (authority == null) {
            authority = ApplicationLoader.getApplicationId() + ".notification_image_provider";
        }
        return authority;
    }

    private static UriMatcher getUriMatcher() {
        if (matcher == null) {
            UriMatcher uriMatcher = new UriMatcher(-1);
            matcher = uriMatcher;
            uriMatcher.addURI(getAuthority(), "msg_media_raw/#/*", 1);
        }
        return matcher;
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        for (int i = 0; i < UserConfig.getActivatedAccountsCount(); i++) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.fileLoaded);
        }
        return true;
    }

    @Override // android.content.ContentProvider
    public void shutdown() {
        for (int i = 0; i < UserConfig.getActivatedAccountsCount(); i++) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.fileLoaded);
        }
    }

    @Override // android.content.ContentProvider
    public String[] getStreamTypes(Uri uri, String str) {
        if (str.startsWith("*/") || str.startsWith("image/")) {
            return new String[]{"image/jpeg", "image/png", "image/webp"};
        }
        return null;
    }

    @Override // android.content.ContentProvider
    public ParcelFileDescriptor openFile(Uri uri, String str) throws FileNotFoundException {
        if (!"r".equals(str)) {
            throw new SecurityException("Can only open files for read");
        }
        if (getUriMatcher().match(uri) == 1) {
            List<String> pathSegments = uri.getPathSegments();
            Integer.parseInt(pathSegments.get(1));
            String str2 = pathSegments.get(2);
            String queryParameter = uri.getQueryParameter("final_path");
            String queryParameter2 = uri.getQueryParameter("fallback");
            File file = new File(queryParameter);
            ApplicationLoader.postInitApplication();
            if (AndroidUtilities.isInternalUri(Uri.fromFile(file))) {
                throw new SecurityException("trying to read internal file");
            }
            if (!file.exists()) {
                Long l = this.fileStartTimes.get(str2);
                long longValue = l != null ? l.longValue() : System.currentTimeMillis();
                if (l == null) {
                    this.fileStartTimes.put(str2, Long.valueOf(longValue));
                }
                while (!file.exists()) {
                    if (System.currentTimeMillis() - longValue >= 3000) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.w("Waiting for " + str2 + " to download timed out");
                        }
                        if (TextUtils.isEmpty(queryParameter2)) {
                            throw new FileNotFoundException("Download timed out");
                        }
                        File file2 = new File(queryParameter2);
                        if (AndroidUtilities.isInternalUri(Uri.fromFile(file2))) {
                            throw new SecurityException("trying to read internal file");
                        }
                        return ParcelFileDescriptor.open(file2, NUM);
                    }
                    synchronized (this.sync) {
                        this.waitingForFiles.add(str2);
                        try {
                            this.sync.wait(1000L);
                        } catch (InterruptedException unused) {
                        }
                    }
                }
                if (AndroidUtilities.isInternalUri(Uri.fromFile(file))) {
                    throw new SecurityException("trying to read internal file");
                }
            }
            return ParcelFileDescriptor.open(file, NUM);
        }
        throw new FileNotFoundException("Invalid URI");
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.fileLoaded) {
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
