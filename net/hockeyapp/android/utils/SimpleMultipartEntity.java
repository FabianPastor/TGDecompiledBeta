package net.hockeyapp.android.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

public class SimpleMultipartEntity {
    private static final char[] BOUNDARY_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private String mBoundary;
    private boolean mIsSetFirst = false;
    private boolean mIsSetLast = false;
    private OutputStream mOut;
    private File mTempFile;

    public SimpleMultipartEntity(File tempFile) {
        this.mTempFile = tempFile;
        try {
            this.mOut = new FileOutputStream(this.mTempFile);
        } catch (Throwable e) {
            HockeyLog.error("Failed to open temp file", e);
        }
        StringBuilder buffer = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < 30; i++) {
            buffer.append(BOUNDARY_CHARS[rand.nextInt(BOUNDARY_CHARS.length)]);
        }
        this.mBoundary = buffer.toString();
    }

    public String getBoundary() {
        return this.mBoundary;
    }

    public void writeFirstBoundaryIfNeeds() throws IOException {
        if (!this.mIsSetFirst) {
            this.mOut.write(("--" + this.mBoundary + "\r\n").getBytes());
        }
        this.mIsSetFirst = true;
    }

    public void writeLastBoundaryIfNeeds() {
        if (!this.mIsSetLast) {
            try {
                this.mOut.write(("\r\n--" + this.mBoundary + "--\r\n").getBytes());
                this.mOut.flush();
                this.mOut.close();
                this.mOut = null;
            } catch (Throwable e) {
                HockeyLog.error("Failed to close temp file", e);
            }
            this.mIsSetLast = true;
        }
    }

    public void addPart(String key, String value) throws IOException {
        writeFirstBoundaryIfNeeds();
        this.mOut.write(("Content-Disposition: form-data; name=\"" + key + "\"\r\n").getBytes());
        this.mOut.write("Content-Type: text/plain; charset=UTF-8\r\n".getBytes());
        this.mOut.write("Content-Transfer-Encoding: 8bit\r\n\r\n".getBytes());
        this.mOut.write(value.getBytes());
        this.mOut.write(("\r\n--" + this.mBoundary + "\r\n").getBytes());
    }

    public void addPart(String key, String fileName, InputStream fin, boolean lastFile) throws IOException {
        addPart(key, fileName, fin, "application/octet-stream", lastFile);
    }

    public void addPart(String key, String fileName, InputStream fin, String type, boolean lastFile) throws IOException {
        writeFirstBoundaryIfNeeds();
        try {
            type = "Content-Type: " + type + "\r\n";
            this.mOut.write(("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + fileName + "\"\r\n").getBytes());
            this.mOut.write(type.getBytes());
            this.mOut.write("Content-Transfer-Encoding: binary\r\n\r\n".getBytes());
            byte[] tmp = new byte[4096];
            while (true) {
                int l = fin.read(tmp);
                if (l == -1) {
                    break;
                }
                this.mOut.write(tmp, 0, l);
            }
            this.mOut.flush();
            if (lastFile) {
                writeLastBoundaryIfNeeds();
            } else {
                this.mOut.write(("\r\n--" + this.mBoundary + "\r\n").getBytes());
            }
        } finally {
            try {
                fin.close();
            } catch (IOException e) {
            }
        }
    }

    public long getContentLength() {
        writeLastBoundaryIfNeeds();
        return this.mTempFile.length();
    }

    public void writeTo(OutputStream out) throws IOException {
        writeLastBoundaryIfNeeds();
        FileInputStream fileInputStream = new FileInputStream(this.mTempFile);
        BufferedOutputStream outputStream = new BufferedOutputStream(out);
        byte[] tmp = new byte[4096];
        while (true) {
            int l = fileInputStream.read(tmp);
            if (l != -1) {
                outputStream.write(tmp, 0, l);
            } else {
                fileInputStream.close();
                outputStream.flush();
                outputStream.close();
                this.mTempFile.delete();
                this.mTempFile = null;
                return;
            }
        }
    }
}
