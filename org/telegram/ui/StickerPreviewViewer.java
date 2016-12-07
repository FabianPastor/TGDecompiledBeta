package org.telegram.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer.DefaultLoadControl;
import org.telegram.messenger.volley.DefaultRetryPolicy;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.StickerCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class StickerPreviewViewer {
    @SuppressLint({"StaticFieldLeak"})
    private static volatile StickerPreviewViewer Instance = null;
    private ColorDrawable backgroundDrawable = new ColorDrawable(NUM);
    private ImageReceiver centerImage = new ImageReceiver();
    private FrameLayoutDrawer containerView;
    private InputStickerSet currentSet;
    private Document currentSticker;
    private View currentStickerPreviewCell;
    private StickerPreviewViewerDelegate delegate;
    private boolean isVisible = false;
    private int keyboardHeight = AndroidUtilities.dp(200.0f);
    private long lastUpdateTime;
    private Runnable openStickerPreviewRunnable;
    private Activity parentActivity;
    private float showProgress;
    private Runnable showSheetRunnable = new Runnable() {
        public void run() {
            if (StickerPreviewViewer.this.parentActivity != null && StickerPreviewViewer.this.currentSet != null) {
                Builder builder = new Builder(StickerPreviewViewer.this.parentActivity);
                builder.setItems(new CharSequence[]{LocaleController.getString("SendStickerPreview", R.string.SendStickerPreview), LocaleController.formatString("ViewPackPreview", R.string.ViewPackPreview, new Object[0])}, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (StickerPreviewViewer.this.parentActivity != null && StickerPreviewViewer.this.delegate != null) {
                            if (which == 0) {
                                StickerPreviewViewer.this.delegate.sentSticker(StickerPreviewViewer.this.currentSticker);
                            } else if (which == 1) {
                                StickerPreviewViewer.this.delegate.openSet(StickerPreviewViewer.this.currentSet);
                            }
                        }
                    }
                });
                StickerPreviewViewer.this.visibleDialog = builder.create();
                StickerPreviewViewer.this.visibleDialog.setOnDismissListener(new OnDismissListener() {
                    public void onDismiss(DialogInterface dialog) {
                        StickerPreviewViewer.this.visibleDialog = null;
                        StickerPreviewViewer.this.close();
                    }
                });
                StickerPreviewViewer.this.visibleDialog.show();
            }
        }
    };
    private int startX;
    private int startY;
    private Dialog visibleDialog;
    private LayoutParams windowLayoutParams;
    private FrameLayout windowView;

    private class FrameLayoutDrawer extends FrameLayout {
        public FrameLayoutDrawer(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        protected void onDraw(Canvas canvas) {
            StickerPreviewViewer.getInstance().onDraw(canvas);
        }
    }

    public interface StickerPreviewViewerDelegate {
        void openSet(InputStickerSet inputStickerSet);

        void sentSticker(Document document);
    }

    public static StickerPreviewViewer getInstance() {
        StickerPreviewViewer localInstance = Instance;
        if (localInstance == null) {
            synchronized (PhotoViewer.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        StickerPreviewViewer localInstance2 = new StickerPreviewViewer();
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

    public void reset() {
        if (this.openStickerPreviewRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.openStickerPreviewRunnable);
            this.openStickerPreviewRunnable = null;
        }
        if (this.currentStickerPreviewCell != null) {
            if (this.currentStickerPreviewCell instanceof StickerEmojiCell) {
                ((StickerEmojiCell) this.currentStickerPreviewCell).setScaled(false);
            } else if (this.currentStickerPreviewCell instanceof StickerCell) {
                ((StickerCell) this.currentStickerPreviewCell).setScaled(false);
            } else if (this.currentStickerPreviewCell instanceof ContextLinkCell) {
                ((ContextLinkCell) this.currentStickerPreviewCell).setScaled(false);
            }
            this.currentStickerPreviewCell = null;
        }
    }

    public boolean onTouch(MotionEvent event, View listView, int height, Object listener, StickerPreviewViewerDelegate stickerPreviewViewerDelegate) {
        this.delegate = stickerPreviewViewerDelegate;
        if (this.openStickerPreviewRunnable != null || isVisible()) {
            if (event.getAction() == 1 || event.getAction() == 3 || event.getAction() == 6) {
                final View view = listView;
                final Object obj = listener;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (view instanceof AbsListView) {
                            ((AbsListView) view).setOnItemClickListener((OnItemClickListener) obj);
                        } else if (view instanceof RecyclerListView) {
                            ((RecyclerListView) view).setOnItemClickListener((RecyclerListView.OnItemClickListener) obj);
                        }
                    }
                }, 150);
                if (this.openStickerPreviewRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(this.openStickerPreviewRunnable);
                    this.openStickerPreviewRunnable = null;
                } else if (isVisible()) {
                    close();
                    if (this.currentStickerPreviewCell != null) {
                        if (this.currentStickerPreviewCell instanceof StickerEmojiCell) {
                            ((StickerEmojiCell) this.currentStickerPreviewCell).setScaled(false);
                        } else if (this.currentStickerPreviewCell instanceof StickerCell) {
                            ((StickerCell) this.currentStickerPreviewCell).setScaled(false);
                        } else if (this.currentStickerPreviewCell instanceof ContextLinkCell) {
                            ((ContextLinkCell) this.currentStickerPreviewCell).setScaled(false);
                        }
                        this.currentStickerPreviewCell = null;
                    }
                }
            } else if (event.getAction() != 0) {
                if (isVisible()) {
                    if (event.getAction() == 2) {
                        int x = (int) event.getX();
                        int y = (int) event.getY();
                        int count = 0;
                        if (listView instanceof AbsListView) {
                            count = ((AbsListView) listView).getChildCount();
                        } else if (listView instanceof RecyclerListView) {
                            count = ((RecyclerListView) listView).getChildCount();
                        }
                        int a = 0;
                        while (a < count) {
                            View view2 = null;
                            if (listView instanceof AbsListView) {
                                view2 = ((AbsListView) listView).getChildAt(a);
                            } else if (listView instanceof RecyclerListView) {
                                view2 = ((RecyclerListView) listView).getChildAt(a);
                            }
                            if (view2 == null) {
                                return false;
                            }
                            int top = view2.getTop();
                            int bottom = view2.getBottom();
                            int left = view2.getLeft();
                            int right = view2.getRight();
                            if (top > y || bottom < y || left > x || right < x) {
                                a++;
                            } else {
                                boolean ok = false;
                                if (view2 instanceof StickerEmojiCell) {
                                    ok = true;
                                } else if (view2 instanceof StickerCell) {
                                    ok = true;
                                } else if (view2 instanceof ContextLinkCell) {
                                    ok = ((ContextLinkCell) view2).isSticker();
                                }
                                if (ok && view2 != this.currentStickerPreviewCell) {
                                    if (this.currentStickerPreviewCell instanceof StickerEmojiCell) {
                                        ((StickerEmojiCell) this.currentStickerPreviewCell).setScaled(false);
                                    } else if (this.currentStickerPreviewCell instanceof StickerCell) {
                                        ((StickerCell) this.currentStickerPreviewCell).setScaled(false);
                                    } else if (this.currentStickerPreviewCell instanceof ContextLinkCell) {
                                        ((ContextLinkCell) this.currentStickerPreviewCell).setScaled(false);
                                    }
                                    this.currentStickerPreviewCell = view2;
                                    setKeyboardHeight(height);
                                    if (this.currentStickerPreviewCell instanceof StickerEmojiCell) {
                                        open(((StickerEmojiCell) this.currentStickerPreviewCell).getSticker(), ((StickerEmojiCell) this.currentStickerPreviewCell).isRecent());
                                        ((StickerEmojiCell) this.currentStickerPreviewCell).setScaled(true);
                                    } else if (this.currentStickerPreviewCell instanceof StickerCell) {
                                        open(((StickerCell) this.currentStickerPreviewCell).getSticker(), false);
                                        ((StickerCell) this.currentStickerPreviewCell).setScaled(true);
                                    } else if (this.currentStickerPreviewCell instanceof ContextLinkCell) {
                                        open(((ContextLinkCell) this.currentStickerPreviewCell).getDocument(), false);
                                        ((ContextLinkCell) this.currentStickerPreviewCell).setScaled(true);
                                    }
                                    return true;
                                }
                            }
                        }
                    }
                    return true;
                } else if (this.openStickerPreviewRunnable != null) {
                    if (event.getAction() != 2) {
                        AndroidUtilities.cancelRunOnUIThread(this.openStickerPreviewRunnable);
                        this.openStickerPreviewRunnable = null;
                    } else if (Math.hypot((double) (((float) this.startX) - event.getX()), (double) (((float) this.startY) - event.getY())) > ((double) AndroidUtilities.dp(10.0f))) {
                        AndroidUtilities.cancelRunOnUIThread(this.openStickerPreviewRunnable);
                        this.openStickerPreviewRunnable = null;
                    }
                }
            }
        }
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent event, View listView, int height, StickerPreviewViewerDelegate stickerPreviewViewerDelegate) {
        this.delegate = stickerPreviewViewerDelegate;
        if (event.getAction() == 0) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            int count = 0;
            if (listView instanceof AbsListView) {
                count = ((AbsListView) listView).getChildCount();
            } else if (listView instanceof RecyclerListView) {
                count = ((RecyclerListView) listView).getChildCount();
            }
            int a = 0;
            while (a < count) {
                View view = null;
                if (listView instanceof AbsListView) {
                    view = ((AbsListView) listView).getChildAt(a);
                } else if (listView instanceof RecyclerListView) {
                    view = ((RecyclerListView) listView).getChildAt(a);
                }
                if (view == null) {
                    return false;
                }
                int top = view.getTop();
                int bottom = view.getBottom();
                int left = view.getLeft();
                int right = view.getRight();
                if (top > y || bottom < y || left > x || right < x) {
                    a++;
                } else {
                    boolean ok = false;
                    if (view instanceof StickerEmojiCell) {
                        ok = ((StickerEmojiCell) view).showingBitmap();
                    } else if (view instanceof StickerCell) {
                        ok = ((StickerCell) view).showingBitmap();
                    } else if (view instanceof ContextLinkCell) {
                        ContextLinkCell cell = (ContextLinkCell) view;
                        ok = cell.isSticker() && cell.showingBitmap();
                    }
                    if (!ok) {
                        return false;
                    }
                    this.startX = x;
                    this.startY = y;
                    this.currentStickerPreviewCell = view;
                    final View view2 = listView;
                    final int i = height;
                    this.openStickerPreviewRunnable = new Runnable() {
                        public void run() {
                            if (StickerPreviewViewer.this.openStickerPreviewRunnable != null) {
                                if (view2 instanceof AbsListView) {
                                    ((AbsListView) view2).setOnItemClickListener(null);
                                    ((AbsListView) view2).requestDisallowInterceptTouchEvent(true);
                                } else if (view2 instanceof RecyclerListView) {
                                    ((RecyclerListView) view2).setOnItemClickListener(null);
                                    ((RecyclerListView) view2).requestDisallowInterceptTouchEvent(true);
                                }
                                StickerPreviewViewer.this.openStickerPreviewRunnable = null;
                                StickerPreviewViewer.this.setParentActivity((Activity) view2.getContext());
                                StickerPreviewViewer.this.setKeyboardHeight(i);
                                if (StickerPreviewViewer.this.currentStickerPreviewCell instanceof StickerEmojiCell) {
                                    StickerPreviewViewer.this.open(((StickerEmojiCell) StickerPreviewViewer.this.currentStickerPreviewCell).getSticker(), ((StickerEmojiCell) StickerPreviewViewer.this.currentStickerPreviewCell).isRecent());
                                    ((StickerEmojiCell) StickerPreviewViewer.this.currentStickerPreviewCell).setScaled(true);
                                } else if (StickerPreviewViewer.this.currentStickerPreviewCell instanceof StickerCell) {
                                    StickerPreviewViewer.this.open(((StickerCell) StickerPreviewViewer.this.currentStickerPreviewCell).getSticker(), false);
                                    ((StickerCell) StickerPreviewViewer.this.currentStickerPreviewCell).setScaled(true);
                                } else if (StickerPreviewViewer.this.currentStickerPreviewCell instanceof ContextLinkCell) {
                                    StickerPreviewViewer.this.open(((ContextLinkCell) StickerPreviewViewer.this.currentStickerPreviewCell).getDocument(), false);
                                    ((ContextLinkCell) StickerPreviewViewer.this.currentStickerPreviewCell).setScaled(true);
                                }
                            }
                        }
                    };
                    AndroidUtilities.runOnUIThread(this.openStickerPreviewRunnable, 200);
                    return true;
                }
            }
        }
        return false;
    }

    public void setDelegate(StickerPreviewViewerDelegate stickerPreviewViewerDelegate) {
        this.delegate = stickerPreviewViewerDelegate;
    }

    public void setParentActivity(Activity activity) {
        if (this.parentActivity != activity) {
            this.parentActivity = activity;
            this.windowView = new FrameLayout(activity);
            this.windowView.setFocusable(true);
            this.windowView.setFocusableInTouchMode(true);
            if (VERSION.SDK_INT >= 23) {
                this.windowView.setFitsSystemWindows(true);
            }
            this.containerView = new FrameLayoutDrawer(activity);
            this.containerView.setFocusable(false);
            this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
            this.containerView.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == 1 || event.getAction() == 6 || event.getAction() == 3) {
                        StickerPreviewViewer.this.close();
                    }
                    return true;
                }
            });
            this.windowLayoutParams = new LayoutParams();
            this.windowLayoutParams.height = -1;
            this.windowLayoutParams.format = -3;
            this.windowLayoutParams.width = -1;
            this.windowLayoutParams.gravity = 48;
            this.windowLayoutParams.type = 99;
            if (VERSION.SDK_INT >= 21) {
                this.windowLayoutParams.flags = -NUM;
            } else {
                this.windowLayoutParams.flags = 8;
            }
            this.centerImage.setAspectFit(true);
            this.centerImage.setInvalidateAll(true);
            this.centerImage.setParentView(this.containerView);
        }
    }

    public void setKeyboardHeight(int height) {
        this.keyboardHeight = height;
    }

    public void open(Document sticker, boolean isRecent) {
        if (this.parentActivity != null && sticker != null) {
            InputStickerSet newSet = null;
            if (isRecent) {
                for (int a = 0; a < sticker.attributes.size(); a++) {
                    DocumentAttribute attribute = (DocumentAttribute) sticker.attributes.get(a);
                    if ((attribute instanceof TL_documentAttributeSticker) && attribute.stickerset != null) {
                        newSet = attribute.stickerset;
                        break;
                    }
                }
                if (!(newSet == null || this.currentSet == newSet)) {
                    try {
                        if (this.visibleDialog != null) {
                            this.visibleDialog.setOnDismissListener(null);
                            this.visibleDialog.dismiss();
                        }
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                    AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
                    AndroidUtilities.runOnUIThread(this.showSheetRunnable, 2000);
                }
            }
            this.currentSet = newSet;
            this.centerImage.setImage((TLObject) sticker, null, sticker.thumb.location, null, "webp", true);
            this.currentSticker = sticker;
            this.containerView.invalidate();
            if (!this.isVisible) {
                AndroidUtilities.lockOrientation(this.parentActivity);
                try {
                    if (this.windowView.getParent() != null) {
                        ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
                    }
                } catch (Throwable e2) {
                    FileLog.e("tmessages", e2);
                }
                ((WindowManager) this.parentActivity.getSystemService("window")).addView(this.windowView, this.windowLayoutParams);
                this.isVisible = true;
                this.showProgress = 0.0f;
                this.lastUpdateTime = System.currentTimeMillis();
            }
        }
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void close() {
        if (this.parentActivity != null && this.visibleDialog == null) {
            AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
            this.showProgress = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            this.lastUpdateTime = System.currentTimeMillis();
            this.containerView.invalidate();
            try {
                if (this.visibleDialog != null) {
                    this.visibleDialog.dismiss();
                    this.visibleDialog = null;
                }
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
            this.currentSticker = null;
            this.currentSet = null;
            this.delegate = null;
            this.isVisible = false;
        }
    }

    public void destroy() {
        this.isVisible = false;
        this.delegate = null;
        this.currentSticker = null;
        this.currentSet = null;
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
        }
        if (this.parentActivity != null && this.windowView != null) {
            try {
                if (this.windowView.getParent() != null) {
                    ((WindowManager) this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
                }
                this.windowView = null;
            } catch (Throwable e2) {
                FileLog.e("tmessages", e2);
            }
            Instance = null;
        }
    }

    @SuppressLint({"DrawAllocation"})
    private void onDraw(Canvas canvas) {
        if (this.containerView != null && this.backgroundDrawable != null) {
            this.backgroundDrawable.setAlpha((int) (BitmapDescriptorFactory.HUE_CYAN * this.showProgress));
            this.backgroundDrawable.setBounds(0, 0, this.containerView.getWidth(), this.containerView.getHeight());
            this.backgroundDrawable.draw(canvas);
            canvas.save();
            int size = (int) (((float) Math.min(this.containerView.getWidth(), this.containerView.getHeight())) / 1.8f);
            canvas.translate((float) (this.containerView.getWidth() / 2), (float) Math.max((size / 2) + AndroidUtilities.statusBarHeight, (this.containerView.getHeight() - this.keyboardHeight) / 2));
            if (this.centerImage.getBitmap() != null) {
                size = (int) (((float) size) * ((DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD * this.showProgress) / DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD));
                this.centerImage.setAlpha(this.showProgress);
                this.centerImage.setImageCoords((-size) / 2, (-size) / 2, size, size);
                this.centerImage.draw(canvas);
            }
            canvas.restore();
            long newTime;
            long dt;
            if (this.isVisible) {
                if (this.showProgress != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                    newTime = System.currentTimeMillis();
                    dt = newTime - this.lastUpdateTime;
                    this.lastUpdateTime = newTime;
                    this.showProgress += ((float) dt) / BitmapDescriptorFactory.HUE_GREEN;
                    this.containerView.invalidate();
                    if (this.showProgress > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                        this.showProgress = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                    }
                }
            } else if (this.showProgress != 0.0f) {
                newTime = System.currentTimeMillis();
                dt = newTime - this.lastUpdateTime;
                this.lastUpdateTime = newTime;
                this.showProgress -= ((float) dt) / BitmapDescriptorFactory.HUE_GREEN;
                this.containerView.invalidate();
                if (this.showProgress < 0.0f) {
                    this.showProgress = 0.0f;
                }
                if (this.showProgress == 0.0f) {
                    AndroidUtilities.unlockOrientation(this.parentActivity);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            StickerPreviewViewer.this.centerImage.setImageBitmap((Bitmap) null);
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
        }
    }
}
