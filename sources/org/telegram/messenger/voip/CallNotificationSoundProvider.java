package org.telegram.messenger.voip;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import java.io.FileNotFoundException;
import org.telegram.messenger.ApplicationLoader;
/* loaded from: classes.dex */
public class CallNotificationSoundProvider extends ContentProvider {
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
    public boolean onCreate() {
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return null;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public ParcelFileDescriptor openFile(Uri uri, String str) throws FileNotFoundException {
        if (!"r".equals(str)) {
            throw new SecurityException("Unexpected file mode " + str);
        } else if (ApplicationLoader.applicationContext == null) {
            throw new FileNotFoundException("Unexpected application state");
        } else {
            try {
                VoIPService sharedInstance = VoIPService.getSharedInstance();
                if (sharedInstance != null) {
                    sharedInstance.startRingtoneAndVibration();
                }
                ParcelFileDescriptor[] createPipe = ParcelFileDescriptor.createPipe();
                ParcelFileDescriptor.AutoCloseOutputStream autoCloseOutputStream = new ParcelFileDescriptor.AutoCloseOutputStream(createPipe[1]);
                autoCloseOutputStream.write(new byte[]{82, 73, 70, 70, 41, 0, 0, 0, 87, 65, 86, 69, 102, 109, 116, 32, 16, 0, 0, 0, 1, 0, 1, 0, 68, -84, 0, 0, 16, -79, 2, 0, 2, 0, 16, 0, 100, 97, 116, 97, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
                autoCloseOutputStream.close();
                return createPipe[0];
            } catch (Exception e) {
                throw new FileNotFoundException(e.getMessage());
            }
        }
    }
}
