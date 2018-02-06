package net.hockeyapp.android.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.Queue;
import net.hockeyapp.android.Constants;
import net.hockeyapp.android.objects.FeedbackAttachment;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.ImageUtils;
import net.hockeyapp.android.views.AttachmentView;

public class AttachmentDownloader {
    private final Handler downloadHandler;
    private boolean downloadRunning;
    private Queue<DownloadJob> queue;

    private static class AttachmentDownloaderHolder {
        static final AttachmentDownloader INSTANCE = new AttachmentDownloader();
    }

    private static class DownloadHandler extends Handler {
        private final AttachmentDownloader downloader;

        DownloadHandler(AttachmentDownloader downloader) {
            this.downloader = downloader;
        }

        public void handleMessage(Message msg) {
            final DownloadJob retryCandidate = (DownloadJob) this.downloader.queue.poll();
            if (!retryCandidate.isSuccess() && retryCandidate.consumeRetry()) {
                postDelayed(new Runnable() {
                    public void run() {
                        DownloadHandler.this.downloader.queue.add(retryCandidate);
                        DownloadHandler.this.downloader.downloadNext();
                    }
                }, 3000);
            }
            this.downloader.downloadRunning = false;
            this.downloader.downloadNext();
        }
    }

    private static class DownloadJob {
        private final AttachmentView attachmentView;
        private final FeedbackAttachment feedbackAttachment;
        private int remainingRetries;
        private boolean success;

        private DownloadJob(FeedbackAttachment feedbackAttachment, AttachmentView attachmentView) {
            this.feedbackAttachment = feedbackAttachment;
            this.attachmentView = attachmentView;
            this.success = false;
            this.remainingRetries = 2;
        }

        FeedbackAttachment getFeedbackAttachment() {
            return this.feedbackAttachment;
        }

        AttachmentView getAttachmentView() {
            return this.attachmentView;
        }

        boolean isSuccess() {
            return this.success;
        }

        void setSuccess(boolean success) {
            this.success = success;
        }

        boolean hasRetry() {
            return this.remainingRetries > 0;
        }

        boolean consumeRetry() {
            int i = this.remainingRetries - 1;
            this.remainingRetries = i;
            return i >= 0;
        }
    }

    @SuppressLint({"StaticFieldLeak"})
    private static class DownloadTask extends AsyncTask<Void, Integer, Boolean> {
        private Bitmap bitmap = null;
        private int bitmapOrientation = 1;
        private final Context context;
        private final DownloadJob downloadJob;
        private final Handler handler;

        DownloadTask(DownloadJob downloadJob, Handler handler) {
            this.downloadJob = downloadJob;
            this.handler = handler;
            this.context = downloadJob.getAttachmentView().getContext();
        }

        protected void onPreExecute() {
        }

        protected Boolean doInBackground(Void... args) {
            FeedbackAttachment attachment = this.downloadJob.getFeedbackAttachment();
            File file = new File(Constants.getHockeyAppStorageDir(this.context), attachment.getCacheId());
            if (file.exists()) {
                HockeyLog.error("Cached...");
                loadImageThumbnail(file);
                return Boolean.valueOf(true);
            }
            HockeyLog.error("Downloading...");
            boolean success = downloadAttachment(attachment.getUrl(), file);
            if (success) {
                loadImageThumbnail(file);
            }
            return Boolean.valueOf(success);
        }

        protected void onProgressUpdate(Integer... values) {
        }

        protected void onPostExecute(Boolean success) {
            AttachmentView attachmentView = this.downloadJob.getAttachmentView();
            this.downloadJob.setSuccess(success.booleanValue());
            if (success.booleanValue()) {
                attachmentView.setImage(this.bitmap, this.bitmapOrientation);
            } else if (!this.downloadJob.hasRetry()) {
                attachmentView.signalImageLoadingError();
            }
            this.handler.sendEmptyMessage(0);
        }

        private void loadImageThumbnail(File file) {
            try {
                AttachmentView attachmentView = this.downloadJob.getAttachmentView();
                this.bitmapOrientation = ImageUtils.determineOrientation(file);
                this.bitmap = ImageUtils.decodeSampledBitmap(file, this.bitmapOrientation == 0 ? attachmentView.getWidthLandscape() : attachmentView.getWidthPortrait(), this.bitmapOrientation == 0 ? attachmentView.getMaxHeightLandscape() : attachmentView.getMaxHeightPortrait());
            } catch (Throwable e) {
                HockeyLog.error("Failed to load image thumbnail", e);
                this.bitmap = null;
            }
        }

        private boolean downloadAttachment(String url, File file) {
            Throwable e;
            boolean z;
            Throwable th;
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) createConnection(new URL(url));
                connection.connect();
                int lengthOfFile = connection.getContentLength();
                String status = connection.getHeaderField("Status");
                if (status == null || status.startsWith("200")) {
                    OutputStream output2;
                    InputStream input2 = new BufferedInputStream(connection.getInputStream());
                    try {
                        output2 = new FileOutputStream(file);
                    } catch (IOException e2) {
                        e = e2;
                        input = input2;
                        try {
                            HockeyLog.error("Failed to download attachment to " + file, e);
                            z = false;
                            if (output != null) {
                                try {
                                    output.close();
                                } catch (IOException e3) {
                                    if (connection != null) {
                                        connection.disconnect();
                                    }
                                    return z;
                                }
                            }
                            if (input != null) {
                                input.close();
                            }
                            if (connection != null) {
                                connection.disconnect();
                            }
                            return z;
                        } catch (Throwable th2) {
                            th = th2;
                            if (output != null) {
                                try {
                                    output.close();
                                } catch (IOException e4) {
                                    if (connection != null) {
                                        connection.disconnect();
                                    }
                                    throw th;
                                }
                            }
                            if (input != null) {
                                input.close();
                            }
                            if (connection != null) {
                                connection.disconnect();
                            }
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        input = input2;
                        if (output != null) {
                            output.close();
                        }
                        if (input != null) {
                            input.close();
                        }
                        if (connection != null) {
                            connection.disconnect();
                        }
                        throw th;
                    }
                    try {
                        byte[] data = new byte[1024];
                        long total = 0;
                        while (true) {
                            int count = input2.read(data);
                            if (count == -1) {
                                break;
                            }
                            total += (long) count;
                            publishProgress(new Integer[]{Integer.valueOf((int) ((100 * total) / ((long) lengthOfFile)))});
                            output2.write(data, 0, count);
                        }
                        output2.flush();
                        z = total > 0;
                        if (output2 != null) {
                            try {
                                output2.close();
                            } catch (IOException e5) {
                            }
                        }
                        if (input2 != null) {
                            input2.close();
                        }
                        if (connection != null) {
                            connection.disconnect();
                        }
                        output = output2;
                        input = input2;
                    } catch (IOException e6) {
                        e = e6;
                        output = output2;
                        input = input2;
                        HockeyLog.error("Failed to download attachment to " + file, e);
                        z = false;
                        if (output != null) {
                            output.close();
                        }
                        if (input != null) {
                            input.close();
                        }
                        if (connection != null) {
                            connection.disconnect();
                        }
                        return z;
                    } catch (Throwable th4) {
                        th = th4;
                        output = output2;
                        input = input2;
                        if (output != null) {
                            output.close();
                        }
                        if (input != null) {
                            input.close();
                        }
                        if (connection != null) {
                            connection.disconnect();
                        }
                        throw th;
                    }
                    return z;
                }
                z = false;
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e7) {
                    }
                }
                if (input != null) {
                    input.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
                return z;
            } catch (IOException e8) {
                e = e8;
                HockeyLog.error("Failed to download attachment to " + file, e);
                z = false;
                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
                return z;
            }
        }

        private URLConnection createConnection(URL url) throws IOException {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", "HockeySDK/Android 5.0.4");
            connection.setInstanceFollowRedirects(true);
            return connection;
        }
    }

    public static AttachmentDownloader getInstance() {
        return AttachmentDownloaderHolder.INSTANCE;
    }

    private AttachmentDownloader() {
        this.downloadHandler = new DownloadHandler(this);
        this.queue = new LinkedList();
        this.downloadRunning = false;
    }

    public void download(FeedbackAttachment feedbackAttachment, AttachmentView attachmentView) {
        this.queue.add(new DownloadJob(feedbackAttachment, attachmentView));
        downloadNext();
    }

    private void downloadNext() {
        if (!this.downloadRunning) {
            DownloadJob downloadJob = (DownloadJob) this.queue.peek();
            if (downloadJob != null) {
                this.downloadRunning = true;
                AsyncTaskUtils.execute(new DownloadTask(downloadJob, this.downloadHandler));
            }
        }
    }
}
