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
    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    public boolean onCreate() {
        return true;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return null;
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }

    public ParcelFileDescriptor openFile(Uri uri, String str) throws FileNotFoundException {
        if (!"r".equals(str)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unexpected file mode ");
            stringBuilder.append(str);
            throw new SecurityException(stringBuilder.toString());
        } else if (ApplicationLoader.applicationContext != null) {
            VoIPBaseService sharedInstance = VoIPBaseService.getSharedInstance();
            if (sharedInstance != null) {
                sharedInstance.startRingtoneAndVibration();
            }
            try {
                ParcelFileDescriptor[] createPipe = ParcelFileDescriptor.createPipe();
                AutoCloseOutputStream autoCloseOutputStream = new AutoCloseOutputStream(createPipe[1]);
                autoCloseOutputStream.write(new byte[]{(byte) 82, (byte) 73, (byte) 70, (byte) 70, (byte) 41, (byte) 0, (byte) 0, (byte) 0, (byte) 87, (byte) 65, (byte) 86, (byte) 69, (byte) 102, (byte) 109, (byte) 116, (byte) 32, (byte) 16, (byte) 0, (byte) 0, (byte) 0, (byte) 1, (byte) 0, (byte) 1, (byte) 0, (byte) 68, (byte) -84, (byte) 0, (byte) 0, (byte) 16, (byte) -79, (byte) 2, (byte) 0, (byte) 2, (byte) 0, (byte) 16, (byte) 0, (byte) 100, (byte) 97, (byte) 116, (byte) 97, (byte) 10, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0});
                autoCloseOutputStream.close();
                return createPipe[0];
            } catch (IOException e) {
                throw new FileNotFoundException(e.getMessage());
            }
        } else {
            throw new FileNotFoundException("Unexpected application state");
        }
    }
}
