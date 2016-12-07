package org.telegram.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.volley.DefaultRetryPolicy;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.PhotoSize;

public class SecretPhotoViewer implements NotificationCenterDelegate {
    private static volatile SecretPhotoViewer Instance = null;
    private ImageReceiver centerImage = new ImageReceiver();
    private FrameLayoutDrawer containerView;
    private MessageObject currentMessageObject = null;
    private boolean isVisible = false;
    private Activity parentActivity;
    private SecretDeleteTimer secretDeleteTimer;
    private LayoutParams windowLayoutParams;
    private FrameLayout windowView;

    private class FrameLayoutDrawer extends FrameLayout {
        public FrameLayoutDrawer(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        protected void onDraw(Canvas canvas) {
            SecretPhotoViewer.getInstance().onDraw(canvas);
        }
    }

    private class SecretDeleteTimer extends FrameLayout {
        private String currentInfoString;
        private Paint deleteProgressPaint;
        private RectF deleteProgressRect = new RectF();
        private Drawable drawable = null;
        private StaticLayout infoLayout = null;
        private TextPaint infoPaint = null;
        private int infoWidth;

        public SecretDeleteTimer(Context context) {
            super(context);
            setWillNotDraw(false);
            this.infoPaint = new TextPaint(1);
            this.infoPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
            this.infoPaint.setColor(-1);
            this.deleteProgressPaint = new Paint(1);
            this.deleteProgressPaint.setColor(-1644826);
            this.drawable = getResources().getDrawable(R.drawable.circle1);
        }

        private void updateSecretTimeText() {
            if (SecretPhotoViewer.this.currentMessageObject != null) {
                String str = SecretPhotoViewer.this.currentMessageObject.getSecretTimeString();
                if (str == null) {
                    return;
                }
                if (this.currentInfoString == null || !this.currentInfoString.equals(str)) {
                    this.currentInfoString = str;
                    this.infoWidth = (int) Math.ceil((double) this.infoPaint.measureText(this.currentInfoString));
                    this.infoLayout = new StaticLayout(TextUtils.ellipsize(this.currentInfoString, this.infoPaint, (float) this.infoWidth, TruncateAt.END), this.infoPaint, this.infoWidth, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                    invalidate();
                }
            }
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            this.deleteProgressRect.set((float) (getMeasuredWidth() - AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE)), (float) AndroidUtilities.dp(2.0f), (float) (getMeasuredWidth() - AndroidUtilities.dp(2.0f)), (float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE));
        }

        protected void onDraw(Canvas canvas) {
            if (SecretPhotoViewer.this.currentMessageObject != null && SecretPhotoViewer.this.currentMessageObject.messageOwner.destroyTime != 0) {
                if (this.drawable != null) {
                    this.drawable.setBounds(getMeasuredWidth() - AndroidUtilities.dp(32.0f), 0, getMeasuredWidth(), AndroidUtilities.dp(32.0f));
                    this.drawable.draw(canvas);
                }
                float progress = ((float) Math.max(0, (((long) SecretPhotoViewer.this.currentMessageObject.messageOwner.destroyTime) * 1000) - (System.currentTimeMillis() + ((long) (ConnectionsManager.getInstance().getTimeDifference() * 1000))))) / (((float) SecretPhotoViewer.this.currentMessageObject.messageOwner.ttl) * 1000.0f);
                canvas.drawArc(this.deleteProgressRect, -90.0f, -360.0f * progress, true, this.deleteProgressPaint);
                if (progress != 0.0f) {
                    int offset = AndroidUtilities.dp(2.0f);
                    invalidate(((int) this.deleteProgressRect.left) - offset, ((int) this.deleteProgressRect.top) - offset, ((int) this.deleteProgressRect.right) + (offset * 2), ((int) this.deleteProgressRect.bottom) + (offset * 2));
                }
                updateSecretTimeText();
                if (this.infoLayout != null) {
                    canvas.save();
                    canvas.translate((float) ((getMeasuredWidth() - AndroidUtilities.dp(38.0f)) - this.infoWidth), (float) AndroidUtilities.dp(7.0f));
                    this.infoLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }
    }

    public static SecretPhotoViewer getInstance() {
        SecretPhotoViewer localInstance = Instance;
        if (localInstance == null) {
            synchronized (PhotoViewer.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        SecretPhotoViewer localInstance2 = new SecretPhotoViewer();
                        try {
                            Instance = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            localInstance = localInstance2;
                            throw th2;
                        }
                    }
                } catch (Throwable th3) {
                    th2 = th3;
                    throw th2;
                }
            }
        }
        return localInstance;
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.messagesDeleted) {
            if (this.currentMessageObject != null && ((Integer) args[1]).intValue() == 0 && args[0].contains(Integer.valueOf(this.currentMessageObject.getId()))) {
                closePhoto();
            }
        } else if (id == NotificationCenter.didCreatedNewDeleteTask && this.currentMessageObject != null && this.secretDeleteTimer != null) {
            SparseArray<ArrayList<Integer>> mids = args[0];
            for (int i = 0; i < mids.size(); i++) {
                int key = mids.keyAt(i);
                Iterator it = ((ArrayList) mids.get(key)).iterator();
                while (it.hasNext()) {
                    if (this.currentMessageObject.getId() == ((Integer) it.next()).intValue()) {
                        this.currentMessageObject.messageOwner.destroyTime = key;
                        this.secretDeleteTimer.invalidate();
                        return;
                    }
                }
            }
        }
    }

    public void setParentActivity(Activity activity) {
        if (this.parentActivity != activity) {
            this.parentActivity = activity;
            this.windowView = new FrameLayout(activity);
            this.windowView.setBackgroundColor(-16777216);
            this.windowView.setFocusable(true);
            this.windowView.setFocusableInTouchMode(true);
            if (VERSION.SDK_INT >= 23) {
                this.windowView.setFitsSystemWindows(true);
            }
            this.containerView = new FrameLayoutDrawer(activity);
            this.containerView.setFocusable(false);
            this.windowView.addView(this.containerView);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.containerView.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -1;
            layoutParams.gravity = 51;
            this.containerView.setLayoutParams(layoutParams);
            this.containerView.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == 1 || event.getAction() == 6 || event.getAction() == 3) {
                        SecretPhotoViewer.this.closePhoto();
                    }
                    return true;
                }
            });
            this.secretDeleteTimer = new SecretDeleteTimer(activity);
            this.containerView.addView(this.secretDeleteTimer);
            layoutParams = (FrameLayout.LayoutParams) this.secretDeleteTimer.getLayoutParams();
            layoutParams.gravity = 53;
            layoutParams.width = AndroidUtilities.dp(100.0f);
            layoutParams.height = AndroidUtilities.dp(32.0f);
            layoutParams.rightMargin = AndroidUtilities.dp(19.0f);
            layoutParams.topMargin = AndroidUtilities.dp(19.0f);
            this.secretDeleteTimer.setLayoutParams(layoutParams);
            this.windowLayoutParams = new LayoutParams();
            this.windowLayoutParams.height = -1;
            this.windowLayoutParams.format = -3;
            this.windowLayoutParams.width = -1;
            this.windowLayoutParams.gravity = 48;
            this.windowLayoutParams.type = 99;
            this.windowLayoutParams.flags = 8;
            this.centerImage.setParentView(this.containerView);
        }
    }

    public void openPhoto(MessageObject messageObject) {
        if (this.parentActivity != null && messageObject != null && messageObject.messageOwner.media != null && messageObject.messageOwner.media.photo != null) {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesDeleted);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didCreatedNewDeleteTask);
            PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
            int size = sizeFull.size;
            if (size == 0) {
                size = -1;
            }
            Drawable drawable = ImageLoader.getInstance().getImageFromMemory(sizeFull.location, null, null);
            if (drawable == null) {
                File file = FileLoader.getPathToAttach(sizeFull);
                Bitmap bitmap = null;
                Options options = null;
                if (VERSION.SDK_INT < 21) {
                    options = new Options();
                    options.inDither = true;
                    options.inPreferredConfig = Config.ARGB_8888;
                    options.inPurgeable = true;
                    options.inSampleSize = 1;
                    options.inMutable = true;
                }
                try {
                    bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                }
                if (bitmap != null) {
                    drawable = new BitmapDrawable(bitmap);
                    ImageLoader.getInstance().putImageToCache(drawable, sizeFull.location.volume_id + "_" + sizeFull.location.local_id);
                }
            }
            if (drawable != null) {
                this.centerImage.setImageBitmap(drawable);
            } else {
                this.centerImage.setImage(sizeFull.location, null, null, size, null, false);
            }
            this.currentMessageObject = messageObject;
            AndroidUtilities.lockOrientation(this.parentActivity);
            try {
                if (this.windowView.getParent() != null) {
                    ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
                }
            } catch (Throwable e2) {
                FileLog.e("tmessages", e2);
            }
            ((WindowManager) this.parentActivity.getSystemService("window")).addView(this.windowView, this.windowLayoutParams);
            this.secretDeleteTimer.invalidate();
            this.isVisible = true;
        }
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void closePhoto() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didCreatedNewDeleteTask);
        if (this.parentActivity != null) {
            this.currentMessageObject = null;
            this.isVisible = false;
            AndroidUtilities.unlockOrientation(this.parentActivity);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    SecretPhotoViewer.this.centerImage.setImageBitmap((Bitmap) null);
                }
            });
            try {
                if (this.windowView.getParent() != null) {
                    ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
                }
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
        }
    }

    public void destroyPhotoViewer() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didCreatedNewDeleteTask);
        this.isVisible = false;
        this.currentMessageObject = null;
        if (this.parentActivity != null && this.windowView != null) {
            try {
                if (this.windowView.getParent() != null) {
                    ((WindowManager) this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
                }
                this.windowView = null;
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
            Instance = null;
        }
    }

    private void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate((float) (this.containerView.getWidth() / 2), (float) (this.containerView.getHeight() / 2));
        Bitmap bitmap = this.centerImage.getBitmap();
        if (bitmap != null) {
            float scale;
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            float scaleX = ((float) this.containerView.getWidth()) / ((float) bitmapWidth);
            float scaleY = ((float) this.containerView.getHeight()) / ((float) bitmapHeight);
            if (scaleX > scaleY) {
                scale = scaleY;
            } else {
                scale = scaleX;
            }
            int width = (int) (((float) bitmapWidth) * scale);
            int height = (int) (((float) bitmapHeight) * scale);
            this.centerImage.setImageCoords((-width) / 2, (-height) / 2, width, height);
            this.centerImage.draw(canvas);
        }
        canvas.restore();
    }
}
