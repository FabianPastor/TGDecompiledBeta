package org.telegram.messenger.voip;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import java.io.FileNotFoundException;
import org.telegram.messenger.ApplicationLoader;

public class CallNotificationSoundProvider extends ContentProvider {
    public boolean onCreate() {
        return true;
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

    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        if (!"r".equals(mode)) {
            throw new SecurityException("Unexpected file mode " + mode);
        } else if (ApplicationLoader.applicationContext != null) {
            try {
                VoIPService srv = VoIPService.getSharedInstance();
                if (srv != null) {
                    srv.startRingtoneAndVibration();
                }
                ParcelFileDescriptor[] pipe = ParcelFileDescriptor.createPipe();
                ParcelFileDescriptor.AutoCloseOutputStream outputStream = new ParcelFileDescriptor.AutoCloseOutputStream(pipe[1]);
                outputStream.write(new byte[]{82, 73, 70, 70, 41, 0, 0, 0, 87, 65, 86, 69, 102, 109, 116, 32, 16, 0, 0, 0, 1, 0, 1, 0, 68, -84, 0, 0, 16, -79, 2, 0, 2, 0, 16, 0, 100, 97, 116, 97, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
                outputStream.close();
                return pipe[0];
            } catch (Exception e) {
                throw new FileNotFoundException(e.getMessage());
            }
        } else {
            throw new FileNotFoundException("Unexpected application state");
        }
    }
}
