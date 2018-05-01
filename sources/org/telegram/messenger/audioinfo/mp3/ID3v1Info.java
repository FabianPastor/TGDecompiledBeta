package org.telegram.messenger.audioinfo.mp3;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.telegram.messenger.audioinfo.AudioInfo;

public class ID3v1Info extends AudioInfo {
    public static boolean isID3v1StartPosition(InputStream inputStream) throws IOException {
        inputStream.mark(3);
        try {
            boolean z = inputStream.read() == 84 && inputStream.read() == 65 && inputStream.read() == 71;
            inputStream.reset();
            return z;
        } catch (Throwable th) {
            inputStream.reset();
        }
    }

    public ID3v1Info(java.io.InputStream r4) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r3 = this;
        r3.<init>();
        r0 = isID3v1StartPosition(r4);
        if (r0 == 0) goto L_0x0070;
    L_0x0009:
        r0 = "ID3";
        r3.brand = r0;
        r0 = "1.0";
        r3.version = r0;
        r0 = 128; // 0x80 float:1.794E-43 double:6.32E-322;
        r4 = r3.readBytes(r4, r0);
        r0 = 3;
        r1 = 30;
        r0 = r3.extractString(r4, r0, r1);
        r3.title = r0;
        r0 = 33;
        r0 = r3.extractString(r4, r0, r1);
        r3.artist = r0;
        r0 = 63;
        r0 = r3.extractString(r4, r0, r1);
        r3.album = r0;
        r0 = 93;
        r2 = 4;
        r0 = r3.extractString(r4, r0, r2);	 Catch:{ NumberFormatException -> 0x003e }
        r0 = java.lang.Short.parseShort(r0);	 Catch:{ NumberFormatException -> 0x003e }
        r3.year = r0;	 Catch:{ NumberFormatException -> 0x003e }
        goto L_0x0041;
    L_0x003e:
        r0 = 0;
        r3.year = r0;
    L_0x0041:
        r0 = 97;
        r0 = r3.extractString(r4, r0, r1);
        r3.comment = r0;
        r0 = 127; // 0x7f float:1.78E-43 double:6.27E-322;
        r0 = r4[r0];
        r0 = org.telegram.messenger.audioinfo.mp3.ID3v1Genre.getGenre(r0);
        if (r0 == 0) goto L_0x0059;
    L_0x0053:
        r0 = r0.getDescription();
        r3.genre = r0;
    L_0x0059:
        r0 = 125; // 0x7d float:1.75E-43 double:6.2E-322;
        r0 = r4[r0];
        if (r0 != 0) goto L_0x0070;
    L_0x005f:
        r0 = 126; // 0x7e float:1.77E-43 double:6.23E-322;
        r1 = r4[r0];
        if (r1 == 0) goto L_0x0070;
    L_0x0065:
        r1 = "1.1";
        r3.version = r1;
        r4 = r4[r0];
        r4 = r4 & 255;
        r4 = (short) r4;
        r3.track = r4;
    L_0x0070:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.audioinfo.mp3.ID3v1Info.<init>(java.io.InputStream):void");
    }

    byte[] readBytes(InputStream inputStream, int i) throws IOException {
        byte[] bArr = new byte[i];
        int i2 = 0;
        while (i2 < i) {
            int read = inputStream.read(bArr, i2, i - i2);
            if (read > 0) {
                i2 += read;
            } else {
                throw new EOFException();
            }
        }
        return bArr;
    }

    java.lang.String extractString(byte[] r3, int r4, int r5) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r2 = this;
        r0 = new java.lang.String;	 Catch:{ Exception -> 0x0014 }
        r1 = "ISO-8859-1";	 Catch:{ Exception -> 0x0014 }
        r0.<init>(r3, r4, r5, r1);	 Catch:{ Exception -> 0x0014 }
        r3 = 0;	 Catch:{ Exception -> 0x0014 }
        r4 = r0.indexOf(r3);	 Catch:{ Exception -> 0x0014 }
        if (r4 >= 0) goto L_0x000f;	 Catch:{ Exception -> 0x0014 }
    L_0x000e:
        goto L_0x0013;	 Catch:{ Exception -> 0x0014 }
    L_0x000f:
        r0 = r0.substring(r3, r4);	 Catch:{ Exception -> 0x0014 }
    L_0x0013:
        return r0;
    L_0x0014:
        r3 = "";
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.audioinfo.mp3.ID3v1Info.extractString(byte[], int, int):java.lang.String");
    }
}
