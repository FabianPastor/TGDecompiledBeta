package org.webrtc;

import android.content.Context;
import android.os.SystemClock;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class FileVideoCapturer implements VideoCapturer {
    private static final String TAG = "FileVideoCapturer";
    private CapturerObserver capturerObserver;
    private final TimerTask tickTask = new TimerTask() {
        public void run() {
            FileVideoCapturer.this.tick();
        }
    };
    private final Timer timer = new Timer();
    private final VideoReader videoReader;

    private interface VideoReader {
        void close();

        VideoFrame getNextFrame();
    }

    private static class VideoReaderY4M implements VideoReader {
        private static final int FRAME_DELIMETER_LENGTH = ("FRAME".length() + 1);
        private static final String TAG = "VideoReaderY4M";
        private static final String Y4M_FRAME_DELIMETER = "FRAME";
        private final int frameHeight;
        private final int frameWidth;
        private final RandomAccessFile mediaFile;
        private final FileChannel mediaFileChannel;
        private final long videoStart;

        public VideoReaderY4M(String file) throws IOException {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            this.mediaFile = randomAccessFile;
            this.mediaFileChannel = randomAccessFile.getChannel();
            StringBuilder builder = new StringBuilder();
            while (true) {
                int c = this.mediaFile.read();
                if (c == -1) {
                    throw new RuntimeException("Found end of file before end of header for file: " + file);
                } else if (c == 10) {
                    this.videoStart = this.mediaFileChannel.position();
                    int w = 0;
                    int h = 0;
                    String colorSpace = "";
                    for (String tok : builder.toString().split("[ ]")) {
                        switch (tok.charAt(0)) {
                            case 'C':
                                colorSpace = tok.substring(1);
                                break;
                            case 'H':
                                h = Integer.parseInt(tok.substring(1));
                                break;
                            case 'W':
                                w = Integer.parseInt(tok.substring(1));
                                break;
                        }
                    }
                    Logging.d("VideoReaderY4M", "Color space: " + colorSpace);
                    if (!colorSpace.equals("420") && !colorSpace.equals("420mpeg2")) {
                        throw new IllegalArgumentException("Does not support any other color space than I420 or I420mpeg2");
                    } else if (w % 2 == 1 || h % 2 == 1) {
                        throw new IllegalArgumentException("Does not support odd width or height");
                    } else {
                        this.frameWidth = w;
                        this.frameHeight = h;
                        Logging.d("VideoReaderY4M", "frame dim: (" + w + ", " + h + ")");
                        return;
                    }
                } else {
                    builder.append((char) c);
                }
            }
        }

        public VideoFrame getNextFrame() {
            long captureTimeNs = TimeUnit.MILLISECONDS.toNanos(SystemClock.elapsedRealtime());
            JavaI420Buffer buffer = JavaI420Buffer.allocate(this.frameWidth, this.frameHeight);
            ByteBuffer dataY = buffer.getDataY();
            ByteBuffer dataU = buffer.getDataU();
            ByteBuffer dataV = buffer.getDataV();
            int i = this.frameHeight;
            int chromaHeight = (i + 1) / 2;
            int strideY = i * buffer.getStrideY();
            int strideU = buffer.getStrideU() * chromaHeight;
            int strideV = buffer.getStrideV() * chromaHeight;
            try {
                int i2 = FRAME_DELIMETER_LENGTH;
                ByteBuffer frameDelim = ByteBuffer.allocate(i2);
                if (this.mediaFileChannel.read(frameDelim) < i2) {
                    this.mediaFileChannel.position(this.videoStart);
                    if (this.mediaFileChannel.read(frameDelim) < i2) {
                        throw new RuntimeException("Error looping video");
                    }
                }
                String frameDelimStr = new String(frameDelim.array(), Charset.forName("US-ASCII"));
                if (frameDelimStr.equals("FRAME\n")) {
                    this.mediaFileChannel.read(dataY);
                    this.mediaFileChannel.read(dataU);
                    this.mediaFileChannel.read(dataV);
                    return new VideoFrame(buffer, 0, captureTimeNs);
                }
                throw new RuntimeException("Frames should be delimited by FRAME plus newline, found delimter was: '" + frameDelimStr + "'");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void close() {
            try {
                this.mediaFile.close();
            } catch (IOException e) {
                Logging.e("VideoReaderY4M", "Problem closing file", e);
            }
        }
    }

    public FileVideoCapturer(String inputFile) throws IOException {
        try {
            this.videoReader = new VideoReaderY4M(inputFile);
        } catch (IOException e) {
            Logging.d("FileVideoCapturer", "Could not open video file: " + inputFile);
            throw e;
        }
    }

    public void tick() {
        VideoFrame videoFrame = this.videoReader.getNextFrame();
        this.capturerObserver.onFrameCaptured(videoFrame);
        videoFrame.release();
    }

    public void initialize(SurfaceTextureHelper surfaceTextureHelper, Context applicationContext, CapturerObserver capturerObserver2) {
        this.capturerObserver = capturerObserver2;
    }

    public void startCapture(int width, int height, int framerate) {
        this.timer.schedule(this.tickTask, 0, (long) (1000 / framerate));
    }

    public void stopCapture() throws InterruptedException {
        this.timer.cancel();
    }

    public void changeCaptureFormat(int width, int height, int framerate) {
    }

    public void dispose() {
        this.videoReader.close();
    }

    public boolean isScreencast() {
        return false;
    }
}
