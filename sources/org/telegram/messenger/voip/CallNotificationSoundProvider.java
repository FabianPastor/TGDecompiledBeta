package org.telegram.messenger.voip;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.ParcelFileDescriptor.AutoCloseOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
        } else if (ApplicationLoader.applicationContext == null) {
            throw new FileNotFoundException("Unexpected application state");
        } else {
            VoIPBaseService srv = VoIPBaseService.getSharedInstance();
            if (srv != null) {
                srv.startRingtoneAndVibration();
            }
            try {
                ParcelFileDescriptor[] pipe = ParcelFileDescriptor.createPipe();
                AutoCloseOutputStream outputStream = new AutoCloseOutputStream(pipe[1]);
                outputStream.write(new byte[]{(byte) 82, (byte) 73, (byte) 70, (byte) 70, (byte) 41, (byte) 0, (byte) 0, (byte) 0, (byte) 87, (byte) 65, (byte) 86, (byte) 69, (byte) 102, (byte) 109, (byte) 116, (byte) 32, (byte) 16, (byte) 0, (byte) 0, (byte) 0, (byte) 1, (byte) 0, (byte) 1, (byte) 0, (byte) 68, (byte) -84, (byte) 0, (byte) 0, (byte) 16, (byte) -79, (byte) 2, (byte) 0, (byte) 2, (byte) 0, (byte) 16, (byte) 0, (byte) 100, (byte) 97, (byte) 116, (byte) 97, (byte) 10, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0});
                outputStream.close();
                return pipe[0];
            } catch (IOException x) {
                throw new FileNotFoundException(x.getMessage());
            }
        }
    }
}
