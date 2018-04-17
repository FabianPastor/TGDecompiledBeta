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
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.FileLocation;
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
    private static TextPaint textPaint;
    private ColorDrawable backgroundDrawable = new ColorDrawable(NUM);
    private ImageReceiver centerImage = new ImageReceiver();
    private FrameLayoutDrawer containerView;
    private int currentAccount;
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
    private Runnable showSheetRunnable = new C17041();
    private int startX;
    private int startY;
    private StaticLayout stickerEmojiLayout;
    private Dialog visibleDialog;
    private LayoutParams windowLayoutParams;
    private FrameLayout windowView;

    /* renamed from: org.telegram.ui.StickerPreviewViewer$1 */
    class C17041 implements Runnable {

        /* renamed from: org.telegram.ui.StickerPreviewViewer$1$2 */
        class C17032 implements OnDismissListener {
            C17032() {
            }

            public void onDismiss(DialogInterface dialog) {
                StickerPreviewViewer.this.visibleDialog = null;
                StickerPreviewViewer.this.close();
            }
        }

        C17041() {
        }

        public void run() {
            if (StickerPreviewViewer.this.parentActivity != null) {
                if (StickerPreviewViewer.this.currentSet != null) {
                    int i;
                    final boolean inFavs = DataQuery.getInstance(StickerPreviewViewer.this.currentAccount).isStickerInFavorites(StickerPreviewViewer.this.currentSticker);
                    Builder builder = new Builder(StickerPreviewViewer.this.parentActivity);
                    ArrayList<CharSequence> items = new ArrayList();
                    final ArrayList<Integer> actions = new ArrayList();
                    ArrayList<Integer> icons = new ArrayList();
                    if (StickerPreviewViewer.this.delegate != null) {
                        if (StickerPreviewViewer.this.delegate.needSend()) {
                            items.add(LocaleController.getString("SendStickerPreview", R.string.SendStickerPreview));
                            icons.add(Integer.valueOf(R.drawable.stickers_send));
                            actions.add(Integer.valueOf(0));
                        }
                        items.add(LocaleController.formatString("ViewPackPreview", R.string.ViewPackPreview, new Object[0]));
                        icons.add(Integer.valueOf(R.drawable.stickers_pack));
                        actions.add(Integer.valueOf(1));
                    }
                    if (!MessageObject.isMaskDocument(StickerPreviewViewer.this.currentSticker) && (inFavs || DataQuery.getInstance(StickerPreviewViewer.this.currentAccount).canAddStickerToFavorites())) {
                        String str;
                        if (inFavs) {
                            str = "DeleteFromFavorites";
                            i = R.string.DeleteFromFavorites;
                        } else {
                            str = "AddToFavorites";
                            i = R.string.AddToFavorites;
                        }
                        items.add(LocaleController.getString(str, i));
                        icons.add(Integer.valueOf(inFavs ? R.drawable.stickers_unfavorite : R.drawable.stickers_favorite));
                        actions.add(Integer.valueOf(2));
                    }
                    if (!items.isEmpty()) {
                        int[] ic = new int[icons.size()];
                        for (i = 0; i < icons.size(); i++) {
                            ic[i] = ((Integer) icons.get(i)).intValue();
                        }
                        builder.setItems((CharSequence[]) items.toArray(new CharSequence[items.size()]), ic, new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (StickerPreviewViewer.this.parentActivity != null) {
                                    if (((Integer) actions.get(which)).intValue() == 0) {
                                        if (StickerPreviewViewer.this.delegate != null) {
                                            StickerPreviewViewer.this.delegate.sendSticker(StickerPreviewViewer.this.currentSticker);
                                        }
                                    } else if (((Integer) actions.get(which)).intValue() == 1) {
                                        if (StickerPreviewViewer.this.delegate != null) {
                                            StickerPreviewViewer.this.delegate.openSet(StickerPreviewViewer.this.currentSet);
                                        }
                                    } else if (((Integer) actions.get(which)).intValue() == 2) {
                                        DataQuery.getInstance(StickerPreviewViewer.this.currentAccount).addRecentSticker(2, StickerPreviewViewer.this.currentSticker, (int) (System.currentTimeMillis() / 1000), inFavs);
                                    }
                                }
                            }
                        });
                        StickerPreviewViewer.this.visibleDialog = builder.create();
                        StickerPreviewViewer.this.visibleDialog.setOnDismissListener(new C17032());
                        StickerPreviewViewer.this.visibleDialog.show();
                        StickerPreviewViewer.this.containerView.performHapticFeedback(0);
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.StickerPreviewViewer$4 */
    class C17074 implements OnTouchListener {
        C17074() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == 1 || event.getAction() == 6 || event.getAction() == 3) {
                StickerPreviewViewer.this.close();
            }
            return true;
        }
    }

    /* renamed from: org.telegram.ui.StickerPreviewViewer$5 */
    class C17085 implements Runnable {
        C17085() {
        }

        public void run() {
            StickerPreviewViewer.this.centerImage.setImageBitmap((Bitmap) null);
        }
    }

    private class FrameLayoutDrawer extends FrameLayout {
        public FrameLayoutDrawer(Context context) {
            super(context);
            setWillNotDraw(null);
        }

        protected void onDraw(Canvas canvas) {
            StickerPreviewViewer.this.onDraw(canvas);
        }
    }

    public interface StickerPreviewViewerDelegate {
        boolean needSend();

        void openSet(InputStickerSet inputStickerSet);

        void sendSticker(Document document);
    }

    public static StickerPreviewViewer getInstance() {
        StickerPreviewViewer localInstance = Instance;
        if (localInstance == null) {
            synchronized (PhotoViewer.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    StickerPreviewViewer stickerPreviewViewer = new StickerPreviewViewer();
                    localInstance = stickerPreviewViewer;
                    Instance = stickerPreviewViewer;
                }
            }
        }
        return localInstance;
    }

    public static boolean hasInstance() {
        return Instance != null;
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
        int i;
        boolean z;
        Object obj;
        final View view = listView;
        this.delegate = stickerPreviewViewerDelegate;
        boolean z2 = false;
        if (this.openStickerPreviewRunnable == null) {
            if (isVisible()) {
            }
            i = height;
            z = false;
            obj = listener;
            return z;
        }
        if (!(event.getAction() == 1 || event.getAction() == 3)) {
            if (event.getAction() != 6) {
                if (event.getAction() != 0) {
                    if (isVisible()) {
                        if (event.getAction() == 2) {
                            int x = (int) event.getX();
                            int y = (int) event.getY();
                            int count = 0;
                            if (view instanceof AbsListView) {
                                count = ((AbsListView) view).getChildCount();
                            } else if (view instanceof RecyclerListView) {
                                count = ((RecyclerListView) view).getChildCount();
                            }
                            int a = 0;
                            while (a < count) {
                                View view2 = null;
                                if (view instanceof AbsListView) {
                                    view2 = ((AbsListView) view).getChildAt(a);
                                } else if (view instanceof RecyclerListView) {
                                    view2 = ((RecyclerListView) view).getChildAt(a);
                                }
                                if (view2 == null) {
                                    return z2;
                                }
                                int top = view2.getTop();
                                int bottom = view2.getBottom();
                                int left = view2.getLeft();
                                int right = view2.getRight();
                                if (top <= y && bottom >= y && left <= x) {
                                    if (right >= x) {
                                        boolean ok = false;
                                        if (view2 instanceof StickerEmojiCell) {
                                            ok = true;
                                        } else if (view2 instanceof StickerCell) {
                                            ok = true;
                                        } else if (view2 instanceof ContextLinkCell) {
                                            ok = ((ContextLinkCell) view2).isSticker();
                                        }
                                        if (ok) {
                                            if (view2 != r0.currentStickerPreviewCell) {
                                                if (r0.currentStickerPreviewCell instanceof StickerEmojiCell) {
                                                    ((StickerEmojiCell) r0.currentStickerPreviewCell).setScaled(z2);
                                                } else if (r0.currentStickerPreviewCell instanceof StickerCell) {
                                                    ((StickerCell) r0.currentStickerPreviewCell).setScaled(z2);
                                                } else if (r0.currentStickerPreviewCell instanceof ContextLinkCell) {
                                                    ((ContextLinkCell) r0.currentStickerPreviewCell).setScaled(z2);
                                                }
                                                r0.currentStickerPreviewCell = view2;
                                                setKeyboardHeight(height);
                                                if (r0.currentStickerPreviewCell instanceof StickerEmojiCell) {
                                                    open(((StickerEmojiCell) r0.currentStickerPreviewCell).getSticker(), ((StickerEmojiCell) r0.currentStickerPreviewCell).isRecent());
                                                    z = true;
                                                    ((StickerEmojiCell) r0.currentStickerPreviewCell).setScaled(true);
                                                } else if (r0.currentStickerPreviewCell instanceof StickerCell) {
                                                    open(((StickerCell) r0.currentStickerPreviewCell).getSticker(), false);
                                                    z = true;
                                                    ((StickerCell) r0.currentStickerPreviewCell).setScaled(true);
                                                } else if (r0.currentStickerPreviewCell instanceof ContextLinkCell) {
                                                    open(((ContextLinkCell) r0.currentStickerPreviewCell).getDocument(), false);
                                                    z = true;
                                                    ((ContextLinkCell) r0.currentStickerPreviewCell).setScaled(true);
                                                } else {
                                                    z = true;
                                                }
                                                return z;
                                            }
                                        }
                                    }
                                }
                                i = height;
                                a++;
                                z2 = false;
                            }
                        }
                        i = height;
                        return true;
                    }
                    i = height;
                    if (r0.openStickerPreviewRunnable != null) {
                        if (event.getAction() != 2) {
                            AndroidUtilities.cancelRunOnUIThread(r0.openStickerPreviewRunnable);
                            r0.openStickerPreviewRunnable = null;
                        } else if (Math.hypot((double) (((float) r0.startX) - event.getX()), (double) (((float) r0.startY) - event.getY())) > ((double) AndroidUtilities.dp(10.0f))) {
                            AndroidUtilities.cancelRunOnUIThread(r0.openStickerPreviewRunnable);
                            r0.openStickerPreviewRunnable = null;
                        }
                    }
                    obj = listener;
                    z = false;
                    return z;
                }
                i = height;
                z = false;
                obj = listener;
                return z;
            }
        }
        i = height;
        obj = listener;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (view instanceof AbsListView) {
                    ((AbsListView) view).setOnItemClickListener((OnItemClickListener) obj);
                } else if (view instanceof RecyclerListView) {
                    ((RecyclerListView) view).setOnItemClickListener((RecyclerListView.OnItemClickListener) obj);
                }
            }
        }, 150);
        if (r0.openStickerPreviewRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(r0.openStickerPreviewRunnable);
            r0.openStickerPreviewRunnable = null;
        } else if (isVisible()) {
            close();
            if (r0.currentStickerPreviewCell != null) {
                if (r0.currentStickerPreviewCell instanceof StickerEmojiCell) {
                    z = false;
                    ((StickerEmojiCell) r0.currentStickerPreviewCell).setScaled(false);
                } else {
                    z = false;
                    if (r0.currentStickerPreviewCell instanceof StickerCell) {
                        ((StickerCell) r0.currentStickerPreviewCell).setScaled(false);
                    } else if (r0.currentStickerPreviewCell instanceof ContextLinkCell) {
                        ((ContextLinkCell) r0.currentStickerPreviewCell).setScaled(false);
                    }
                }
                r0.currentStickerPreviewCell = null;
                return z;
            }
        }
        z = false;
        return z;
    }

    public boolean onInterceptTouchEvent(MotionEvent event, View listView, int height, StickerPreviewViewerDelegate stickerPreviewViewerDelegate) {
        final int i;
        View view = listView;
        this.delegate = stickerPreviewViewerDelegate;
        if (event.getAction() == 0) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            int count = 0;
            if (view instanceof AbsListView) {
                count = ((AbsListView) view).getChildCount();
            } else if (view instanceof RecyclerListView) {
                count = ((RecyclerListView) view).getChildCount();
            }
            int a = 0;
            while (a < count) {
                View view2 = null;
                if (view instanceof AbsListView) {
                    view2 = ((AbsListView) view).getChildAt(a);
                } else if (view instanceof RecyclerListView) {
                    view2 = ((RecyclerListView) view).getChildAt(a);
                }
                if (view2 == null) {
                    return false;
                }
                int top = view2.getTop();
                int bottom = view2.getBottom();
                int left = view2.getLeft();
                int right = view2.getRight();
                if (top <= y && bottom >= y && left <= x) {
                    if (right >= x) {
                        boolean ok = false;
                        if (view2 instanceof StickerEmojiCell) {
                            ok = ((StickerEmojiCell) view2).showingBitmap();
                        } else if (view2 instanceof StickerCell) {
                            ok = ((StickerCell) view2).showingBitmap();
                        } else if (view2 instanceof ContextLinkCell) {
                            ContextLinkCell cell = (ContextLinkCell) view2;
                            boolean z = cell.isSticker() && cell.showingBitmap();
                            ok = z;
                        }
                        if (!ok) {
                            return false;
                        }
                        r0.startX = x;
                        r0.startY = y;
                        r0.currentStickerPreviewCell = view2;
                        i = height;
                        r0.openStickerPreviewRunnable = new Runnable() {
                            public void run() {
                                if (StickerPreviewViewer.this.openStickerPreviewRunnable != null) {
                                    if (view instanceof AbsListView) {
                                        ((AbsListView) view).setOnItemClickListener(null);
                                        ((AbsListView) view).requestDisallowInterceptTouchEvent(true);
                                    } else if (view instanceof RecyclerListView) {
                                        ((RecyclerListView) view).setOnItemClickListener((RecyclerListView.OnItemClickListener) null);
                                        ((RecyclerListView) view).requestDisallowInterceptTouchEvent(true);
                                    }
                                    StickerPreviewViewer.this.openStickerPreviewRunnable = null;
                                    StickerPreviewViewer.this.setParentActivity((Activity) view.getContext());
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
                        AndroidUtilities.runOnUIThread(r0.openStickerPreviewRunnable, 200);
                        return true;
                    }
                }
                i = height;
                a++;
                StickerPreviewViewer stickerPreviewViewer = this;
                view = listView;
            }
        }
        i = height;
        return false;
    }

    public void setDelegate(StickerPreviewViewerDelegate stickerPreviewViewerDelegate) {
        this.delegate = stickerPreviewViewerDelegate;
    }

    public void setParentActivity(Activity activity) {
        this.currentAccount = UserConfig.selectedAccount;
        this.centerImage.setCurrentAccount(this.currentAccount);
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
            this.containerView.setOnTouchListener(new C17074());
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
        TLObject tLObject = sticker;
        if (this.parentActivity != null) {
            if (tLObject != null) {
                if (textPaint == null) {
                    textPaint = new TextPaint(1);
                    textPaint.setTextSize((float) AndroidUtilities.dp(24.0f));
                }
                InputStickerSet newSet = null;
                for (int a = 0; a < tLObject.attributes.size(); a++) {
                    DocumentAttribute attribute = (DocumentAttribute) tLObject.attributes.get(a);
                    if ((attribute instanceof TL_documentAttributeSticker) && attribute.stickerset != null) {
                        newSet = attribute.stickerset;
                        break;
                    }
                }
                InputStickerSet newSet2 = newSet;
                if (newSet2 != null) {
                    try {
                        if (r1.visibleDialog != null) {
                            r1.visibleDialog.setOnDismissListener(null);
                            r1.visibleDialog.dismiss();
                            r1.visibleDialog = null;
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                    AndroidUtilities.cancelRunOnUIThread(r1.showSheetRunnable);
                    AndroidUtilities.runOnUIThread(r1.showSheetRunnable, 1300);
                }
                r1.currentSet = newSet2;
                ImageReceiver imageReceiver = r1.centerImage;
                FileLocation fileLocation = (tLObject == null || tLObject.thumb == null) ? null : tLObject.thumb.location;
                imageReceiver.setImage(tLObject, null, fileLocation, null, "webp", 1);
                r1.stickerEmojiLayout = null;
                for (int a2 = 0; a2 < tLObject.attributes.size(); a2++) {
                    DocumentAttribute attribute2 = (DocumentAttribute) tLObject.attributes.get(a2);
                    if ((attribute2 instanceof TL_documentAttributeSticker) && !TextUtils.isEmpty(attribute2.alt)) {
                        r1.stickerEmojiLayout = new StaticLayout(Emoji.replaceEmoji(attribute2.alt, textPaint.getFontMetricsInt(), AndroidUtilities.dp(24.0f), false), textPaint, AndroidUtilities.dp(100.0f), Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                        break;
                    }
                }
                r1.currentSticker = tLObject;
                r1.containerView.invalidate();
                if (!r1.isVisible) {
                    AndroidUtilities.lockOrientation(r1.parentActivity);
                    try {
                        if (r1.windowView.getParent() != null) {
                            ((WindowManager) r1.parentActivity.getSystemService("window")).removeView(r1.windowView);
                        }
                    } catch (Throwable e2) {
                        FileLog.m3e(e2);
                    }
                    ((WindowManager) r1.parentActivity.getSystemService("window")).addView(r1.windowView, r1.windowLayoutParams);
                    r1.isVisible = true;
                    r1.showProgress = 0.0f;
                    r1.lastUpdateTime = System.currentTimeMillis();
                }
            }
        }
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void close() {
        if (this.parentActivity != null) {
            if (this.visibleDialog == null) {
                AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
                this.showProgress = 1.0f;
                this.lastUpdateTime = System.currentTimeMillis();
                this.containerView.invalidate();
                try {
                    if (this.visibleDialog != null) {
                        this.visibleDialog.dismiss();
                        this.visibleDialog = null;
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                this.currentSticker = null;
                this.currentSet = null;
                this.delegate = null;
                this.isVisible = false;
            }
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
            FileLog.m3e(e);
        }
        if (this.parentActivity != null) {
            if (this.windowView != null) {
                try {
                    if (this.windowView.getParent() != null) {
                        ((WindowManager) this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
                    }
                    this.windowView = null;
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
                Instance = null;
            }
        }
    }

    @SuppressLint({"DrawAllocation"})
    private void onDraw(Canvas canvas) {
        if (this.containerView != null) {
            if (this.backgroundDrawable != null) {
                this.backgroundDrawable.setAlpha((int) (180.0f * this.showProgress));
                int i = 0;
                this.backgroundDrawable.setBounds(0, 0, this.containerView.getWidth(), this.containerView.getHeight());
                this.backgroundDrawable.draw(canvas);
                canvas.save();
                int size = (int) (((float) Math.min(this.containerView.getWidth(), this.containerView.getHeight())) / NUM);
                float width = (float) (this.containerView.getWidth() / 2);
                int i2 = (size / 2) + AndroidUtilities.statusBarHeight;
                if (this.stickerEmojiLayout != null) {
                    i = AndroidUtilities.dp(40.0f);
                }
                canvas.translate(width, (float) Math.max(i2 + i, (this.containerView.getHeight() - this.keyboardHeight) / 2));
                if (this.centerImage.getBitmap() != null) {
                    size = (int) (((float) size) * ((this.showProgress * 0.8f) / 0.8f));
                    this.centerImage.setAlpha(this.showProgress);
                    this.centerImage.setImageCoords((-size) / 2, (-size) / 2, size, size);
                    this.centerImage.draw(canvas);
                }
                if (this.stickerEmojiLayout != null) {
                    canvas.translate((float) (-AndroidUtilities.dp(50.0f)), (float) (((-this.centerImage.getImageHeight()) / 2) - AndroidUtilities.dp(30.0f)));
                    this.stickerEmojiLayout.draw(canvas);
                }
                canvas.restore();
                long newTime;
                long dt;
                if (this.isVisible) {
                    if (this.showProgress != 1.0f) {
                        newTime = System.currentTimeMillis();
                        dt = newTime - this.lastUpdateTime;
                        this.lastUpdateTime = newTime;
                        this.showProgress += ((float) dt) / 120.0f;
                        this.containerView.invalidate();
                        if (this.showProgress > 1.0f) {
                            this.showProgress = 1.0f;
                        }
                    }
                } else if (this.showProgress != 0.0f) {
                    newTime = System.currentTimeMillis();
                    dt = newTime - this.lastUpdateTime;
                    this.lastUpdateTime = newTime;
                    this.showProgress -= ((float) dt) / 120.0f;
                    this.containerView.invalidate();
                    if (this.showProgress < 0.0f) {
                        this.showProgress = 0.0f;
                    }
                    if (this.showProgress == 0.0f) {
                        AndroidUtilities.unlockOrientation(this.parentActivity);
                        AndroidUtilities.runOnUIThread(new C17085());
                        try {
                            if (this.windowView.getParent() != null) {
                                ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
                            }
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                }
            }
        }
    }
}
