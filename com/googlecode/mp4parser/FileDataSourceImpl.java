package com.googlecode.mp4parser;

import android.support.v4.os.EnvironmentCompat;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.WritableByteChannel;

public class FileDataSourceImpl implements DataSource {
    FileChannel fc;
    String filename;

    public FileDataSourceImpl(File f) throws FileNotFoundException {
        this.fc = new FileInputStream(f).getChannel();
        this.filename = f.getName();
    }

    public FileDataSourceImpl(String f) throws FileNotFoundException {
        File file = new File(f);
        this.fc = new FileInputStream(file).getChannel();
        this.filename = file.getName();
    }

    public FileDataSourceImpl(FileChannel fc) {
        this.fc = fc;
        this.filename = EnvironmentCompat.MEDIA_UNKNOWN;
    }

    public FileDataSourceImpl(FileChannel fc, String filename) {
        this.fc = fc;
        this.filename = filename;
    }

    public int read(ByteBuffer byteBuffer) throws IOException {
        return this.fc.read(byteBuffer);
    }

    public long size() throws IOException {
        return this.fc.size();
    }

    public long position() throws IOException {
        return this.fc.position();
    }

    public void position(long nuPos) throws IOException {
        this.fc.position(nuPos);
    }

    public long transferTo(long startPosition, long count, WritableByteChannel sink) throws IOException {
        return this.fc.transferTo(startPosition, count, sink);
    }

    public ByteBuffer map(long startPosition, long size) throws IOException {
        return this.fc.map(MapMode.READ_ONLY, startPosition, size);
    }

    public void close() throws IOException {
        this.fc.close();
    }

    public String toString() {
        return this.filename;
    }
}
