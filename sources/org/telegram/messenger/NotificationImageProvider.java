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
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;

public class NotificationImageProvider extends ContentProvider implements NotificationCenterDelegate {
    public static final String AUTHORITY = "org.telegram.messenger.beta.notification_image_provider";
    private static final UriMatcher matcher = new UriMatcher(-1);
    private HashMap<String, Long> fileStartTimes = new HashMap();
    private final Object sync = new Object();
    private HashSet<String> waitingForFiles = new HashSet();

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

    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        if ("r".equals(mode)) {
            switch (matcher.match(uri)) {
                case 1:
                    List<String> path = uri.getPathSegments();
                    int account = Integer.parseInt((String) path.get(1));
                    String name = (String) path.get(2);
                    String finalPath = uri.getQueryParameter("final_path");
                    String fallbackPath = uri.getQueryParameter("fallback");
                    File finalFile = new File(finalPath);
                    if (finalFile.exists()) {
                        FileLog.d(finalFile + " already exists");
                        return ParcelFileDescriptor.open(finalFile, NUM);
                    }
                    Long _startTime = (Long) this.fileStartTimes.get(name);
                    long startTime = _startTime != null ? _startTime.longValue() : System.currentTimeMillis();
                    if (_startTime == null) {
                        this.fileStartTimes.put(name, Long.valueOf(startTime));
                    }
                    while (!finalFile.exists()) {
                        if (System.currentTimeMillis() - startTime >= 3000) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.w("Waiting for " + name + " to download timed out");
                            }
                            if (!TextUtils.isEmpty(fallbackPath)) {
                                return ParcelFileDescriptor.open(new File(fallbackPath), NUM);
                            }
                            throw new FileNotFoundException("Download timed out");
                        }
                        synchronized (this.sync) {
                            this.waitingForFiles.add(name);
                            try {
                                this.sync.wait(1000);
                            } catch (InterruptedException e) {
                            }
                        }
                    }
                    return ParcelFileDescriptor.open(finalFile, NUM);
                default:
                    throw new FileNotFoundException("Invalid URI");
            }
        }
        throw new SecurityException("Can only open files for read");
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.fileDidLoad) {
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
