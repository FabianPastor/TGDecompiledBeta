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
import org.telegram.messenger.C0446R;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
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
    private static volatile StickerPreviewViewer Instance;
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

            public void onDismiss(DialogInterface dialogInterface) {
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
                    final boolean isStickerInFavorites = DataQuery.getInstance(StickerPreviewViewer.this.currentAccount).isStickerInFavorites(StickerPreviewViewer.this.currentSticker);
                    Builder builder = new Builder(StickerPreviewViewer.this.parentActivity);
                    ArrayList arrayList = new ArrayList();
                    final ArrayList arrayList2 = new ArrayList();
                    ArrayList arrayList3 = new ArrayList();
                    if (StickerPreviewViewer.this.delegate != null) {
                        if (StickerPreviewViewer.this.delegate.needSend()) {
                            arrayList.add(LocaleController.getString("SendStickerPreview", C0446R.string.SendStickerPreview));
                            arrayList3.add(Integer.valueOf(C0446R.drawable.stickers_send));
                            arrayList2.add(Integer.valueOf(0));
                        }
                        arrayList.add(LocaleController.formatString("ViewPackPreview", C0446R.string.ViewPackPreview, new Object[0]));
                        arrayList3.add(Integer.valueOf(C0446R.drawable.stickers_pack));
                        arrayList2.add(Integer.valueOf(1));
                    }
                    if (!MessageObject.isMaskDocument(StickerPreviewViewer.this.currentSticker) && (isStickerInFavorites || DataQuery.getInstance(StickerPreviewViewer.this.currentAccount).canAddStickerToFavorites())) {
                        String str;
                        if (isStickerInFavorites) {
                            str = "DeleteFromFavorites";
                            i = C0446R.string.DeleteFromFavorites;
                        } else {
                            str = "AddToFavorites";
                            i = C0446R.string.AddToFavorites;
                        }
                        arrayList.add(LocaleController.getString(str, i));
                        arrayList3.add(Integer.valueOf(isStickerInFavorites ? C0446R.drawable.stickers_unfavorite : C0446R.drawable.stickers_favorite));
                        arrayList2.add(Integer.valueOf(2));
                    }
                    if (!arrayList.isEmpty()) {
                        int[] iArr = new int[arrayList3.size()];
                        for (i = 0; i < arrayList3.size(); i++) {
                            iArr[i] = ((Integer) arrayList3.get(i)).intValue();
                        }
                        builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), iArr, new OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (StickerPreviewViewer.this.parentActivity != null) {
                                    if (((Integer) arrayList2.get(i)).intValue() == null) {
                                        if (StickerPreviewViewer.this.delegate != null) {
                                            StickerPreviewViewer.this.delegate.sendSticker(StickerPreviewViewer.this.currentSticker);
                                        }
                                    } else if (((Integer) arrayList2.get(i)).intValue() == 1) {
                                        if (StickerPreviewViewer.this.delegate != null) {
                                            StickerPreviewViewer.this.delegate.openSet(StickerPreviewViewer.this.currentSet);
                                        }
                                    } else if (((Integer) arrayList2.get(i)).intValue() == 2) {
                                        DataQuery.getInstance(StickerPreviewViewer.this.currentAccount).addRecentSticker(2, StickerPreviewViewer.this.currentSticker, (int) (System.currentTimeMillis() / 1000), isStickerInFavorites);
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

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == 1 || motionEvent.getAction() == 6 || motionEvent.getAction() == 3) {
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
        StickerPreviewViewer stickerPreviewViewer = Instance;
        if (stickerPreviewViewer == null) {
            synchronized (PhotoViewer.class) {
                stickerPreviewViewer = Instance;
                if (stickerPreviewViewer == null) {
                    stickerPreviewViewer = new StickerPreviewViewer();
                    Instance = stickerPreviewViewer;
                }
            }
        }
        return stickerPreviewViewer;
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

    public boolean onTouch(MotionEvent motionEvent, final View view, int i, final Object obj, StickerPreviewViewerDelegate stickerPreviewViewerDelegate) {
        this.delegate = stickerPreviewViewerDelegate;
        if (!(this.openStickerPreviewRunnable == null && isVisible() == null)) {
            if (!(motionEvent.getAction() == 1 || motionEvent.getAction() == 3)) {
                if (motionEvent.getAction() != 6) {
                    if (motionEvent.getAction() != null) {
                        if (isVisible() != null) {
                            if (motionEvent.getAction() == 2) {
                                obj = (int) motionEvent.getX();
                                motionEvent = (int) motionEvent.getY();
                                stickerPreviewViewerDelegate = view instanceof AbsListView;
                                int childCount = stickerPreviewViewerDelegate != null ? ((AbsListView) view).getChildCount() : view instanceof RecyclerListView ? ((RecyclerListView) view).getChildCount() : 0;
                                int i2 = 0;
                                while (i2 < childCount) {
                                    View childAt = stickerPreviewViewerDelegate != null ? ((AbsListView) view).getChildAt(i2) : view instanceof RecyclerListView ? ((RecyclerListView) view).getChildAt(i2) : null;
                                    if (childAt == null) {
                                        return false;
                                    }
                                    int top = childAt.getTop();
                                    int bottom = childAt.getBottom();
                                    int left = childAt.getLeft();
                                    int right = childAt.getRight();
                                    if (top <= motionEvent && bottom >= motionEvent && left <= obj) {
                                        if (right >= obj) {
                                            if ((childAt instanceof StickerEmojiCell) == null) {
                                                if ((childAt instanceof StickerCell) == null) {
                                                    motionEvent = (childAt instanceof ContextLinkCell) != null ? ((ContextLinkCell) childAt).isSticker() : null;
                                                    if (motionEvent != null) {
                                                        if (childAt == this.currentStickerPreviewCell) {
                                                            if ((this.currentStickerPreviewCell instanceof StickerEmojiCell) != null) {
                                                                ((StickerEmojiCell) this.currentStickerPreviewCell).setScaled(false);
                                                            } else if ((this.currentStickerPreviewCell instanceof StickerCell) != null) {
                                                                ((StickerCell) this.currentStickerPreviewCell).setScaled(false);
                                                            } else if ((this.currentStickerPreviewCell instanceof ContextLinkCell) != null) {
                                                                ((ContextLinkCell) this.currentStickerPreviewCell).setScaled(false);
                                                            }
                                                            this.currentStickerPreviewCell = childAt;
                                                            setKeyboardHeight(i);
                                                            if ((this.currentStickerPreviewCell instanceof StickerEmojiCell) != null) {
                                                                open(((StickerEmojiCell) this.currentStickerPreviewCell).getSticker(), ((StickerEmojiCell) this.currentStickerPreviewCell).isRecent());
                                                                ((StickerEmojiCell) this.currentStickerPreviewCell).setScaled(true);
                                                            } else if ((this.currentStickerPreviewCell instanceof StickerCell) != null) {
                                                                open(((StickerCell) this.currentStickerPreviewCell).getSticker(), false);
                                                                ((StickerCell) this.currentStickerPreviewCell).setScaled(true);
                                                            } else if ((this.currentStickerPreviewCell instanceof ContextLinkCell) != null) {
                                                                open(((ContextLinkCell) this.currentStickerPreviewCell).getDocument(), false);
                                                                ((ContextLinkCell) this.currentStickerPreviewCell).setScaled(true);
                                                            }
                                                            return true;
                                                        }
                                                    }
                                                }
                                            }
                                            motionEvent = 1;
                                            if (motionEvent != null) {
                                                if (childAt == this.currentStickerPreviewCell) {
                                                    if ((this.currentStickerPreviewCell instanceof StickerEmojiCell) != null) {
                                                        ((StickerEmojiCell) this.currentStickerPreviewCell).setScaled(false);
                                                    } else if ((this.currentStickerPreviewCell instanceof StickerCell) != null) {
                                                        ((StickerCell) this.currentStickerPreviewCell).setScaled(false);
                                                    } else if ((this.currentStickerPreviewCell instanceof ContextLinkCell) != null) {
                                                        ((ContextLinkCell) this.currentStickerPreviewCell).setScaled(false);
                                                    }
                                                    this.currentStickerPreviewCell = childAt;
                                                    setKeyboardHeight(i);
                                                    if ((this.currentStickerPreviewCell instanceof StickerEmojiCell) != null) {
                                                        open(((StickerEmojiCell) this.currentStickerPreviewCell).getSticker(), ((StickerEmojiCell) this.currentStickerPreviewCell).isRecent());
                                                        ((StickerEmojiCell) this.currentStickerPreviewCell).setScaled(true);
                                                    } else if ((this.currentStickerPreviewCell instanceof StickerCell) != null) {
                                                        open(((StickerCell) this.currentStickerPreviewCell).getSticker(), false);
                                                        ((StickerCell) this.currentStickerPreviewCell).setScaled(true);
                                                    } else if ((this.currentStickerPreviewCell instanceof ContextLinkCell) != null) {
                                                        open(((ContextLinkCell) this.currentStickerPreviewCell).getDocument(), false);
                                                        ((ContextLinkCell) this.currentStickerPreviewCell).setScaled(true);
                                                    }
                                                    return true;
                                                }
                                            }
                                        }
                                    }
                                    i2++;
                                }
                            }
                            return true;
                        } else if (this.openStickerPreviewRunnable != null) {
                            if (motionEvent.getAction() != 2) {
                                AndroidUtilities.cancelRunOnUIThread(this.openStickerPreviewRunnable);
                                this.openStickerPreviewRunnable = null;
                            } else if (Math.hypot((double) (((float) this.startX) - motionEvent.getX()), (double) (((float) this.startY) - motionEvent.getY())) > ((double) AndroidUtilities.dp(NUM))) {
                                AndroidUtilities.cancelRunOnUIThread(this.openStickerPreviewRunnable);
                                this.openStickerPreviewRunnable = null;
                            }
                        }
                    }
                }
            }
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
            } else if (isVisible() != null) {
                close();
                if (this.currentStickerPreviewCell != null) {
                    if ((this.currentStickerPreviewCell instanceof StickerEmojiCell) != null) {
                        ((StickerEmojiCell) this.currentStickerPreviewCell).setScaled(false);
                    } else if ((this.currentStickerPreviewCell instanceof StickerCell) != null) {
                        ((StickerCell) this.currentStickerPreviewCell).setScaled(false);
                    } else if ((this.currentStickerPreviewCell instanceof ContextLinkCell) != null) {
                        ((ContextLinkCell) this.currentStickerPreviewCell).setScaled(false);
                    }
                    this.currentStickerPreviewCell = null;
                }
            }
        }
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent, final View view, final int i, StickerPreviewViewerDelegate stickerPreviewViewerDelegate) {
        this.delegate = stickerPreviewViewerDelegate;
        if (motionEvent.getAction() == null) {
            stickerPreviewViewerDelegate = (int) motionEvent.getX();
            motionEvent = (int) motionEvent.getY();
            boolean z = view instanceof AbsListView;
            int childCount = z ? ((AbsListView) view).getChildCount() : view instanceof RecyclerListView ? ((RecyclerListView) view).getChildCount() : 0;
            for (int i2 = 0; i2 < childCount; i2++) {
                View view2 = null;
                if (z) {
                    view2 = ((AbsListView) view).getChildAt(i2);
                } else if (view instanceof RecyclerListView) {
                    view2 = ((RecyclerListView) view).getChildAt(i2);
                }
                if (view2 == null) {
                    return false;
                }
                int top = view2.getTop();
                int bottom = view2.getBottom();
                int left = view2.getLeft();
                int right = view2.getRight();
                if (top <= motionEvent && bottom >= motionEvent && left <= stickerPreviewViewerDelegate) {
                    if (right >= stickerPreviewViewerDelegate) {
                        if (view2 instanceof StickerEmojiCell) {
                            z = ((StickerEmojiCell) view2).showingBitmap();
                        } else if (view2 instanceof StickerCell) {
                            z = ((StickerCell) view2).showingBitmap();
                        } else {
                            if (view2 instanceof ContextLinkCell) {
                                ContextLinkCell contextLinkCell = (ContextLinkCell) view2;
                                if (contextLinkCell.isSticker() && contextLinkCell.showingBitmap()) {
                                    z = true;
                                }
                            }
                            z = false;
                        }
                        if (!z) {
                            return false;
                        }
                        this.startX = stickerPreviewViewerDelegate;
                        this.startY = motionEvent;
                        this.currentStickerPreviewCell = view2;
                        this.openStickerPreviewRunnable = new Runnable() {
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
                        AndroidUtilities.runOnUIThread(this.openStickerPreviewRunnable, 200);
                        return true;
                    }
                }
            }
        }
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

    public void setKeyboardHeight(int i) {
        this.keyboardHeight = i;
    }

    public void open(Document document, boolean z) {
        TLObject tLObject = document;
        if (this.parentActivity != null) {
            if (tLObject != null) {
                int i;
                DocumentAttribute documentAttribute;
                InputStickerSet inputStickerSet;
                if (textPaint == null) {
                    textPaint = new TextPaint(1);
                    textPaint.setTextSize((float) AndroidUtilities.dp(24.0f));
                }
                for (i = 0; i < tLObject.attributes.size(); i++) {
                    documentAttribute = (DocumentAttribute) tLObject.attributes.get(i);
                    if ((documentAttribute instanceof TL_documentAttributeSticker) && documentAttribute.stickerset != null) {
                        inputStickerSet = documentAttribute.stickerset;
                        break;
                    }
                }
                inputStickerSet = null;
                if (inputStickerSet != null) {
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
                r1.currentSet = inputStickerSet;
                ImageReceiver imageReceiver = r1.centerImage;
                FileLocation fileLocation = (tLObject == null || tLObject.thumb == null) ? null : tLObject.thumb.location;
                imageReceiver.setImage(tLObject, null, fileLocation, null, "webp", 1);
                r1.stickerEmojiLayout = null;
                for (i = 0; i < tLObject.attributes.size(); i++) {
                    documentAttribute = (DocumentAttribute) tLObject.attributes.get(i);
                    if ((documentAttribute instanceof TL_documentAttributeSticker) && !TextUtils.isEmpty(documentAttribute.alt)) {
                        r1.stickerEmojiLayout = new StaticLayout(Emoji.replaceEmoji(documentAttribute.alt, textPaint.getFontMetricsInt(), AndroidUtilities.dp(24.0f), false), textPaint, AndroidUtilities.dp(100.0f), Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
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
                int min = (int) (((float) Math.min(this.containerView.getWidth(), this.containerView.getHeight())) / 1.8f);
                float width = (float) (this.containerView.getWidth() / 2);
                int i2 = (min / 2) + AndroidUtilities.statusBarHeight;
                if (this.stickerEmojiLayout != null) {
                    i = AndroidUtilities.dp(40.0f);
                }
                canvas.translate(width, (float) Math.max(i2 + i, (this.containerView.getHeight() - this.keyboardHeight) / 2));
                if (this.centerImage.getBitmap() != null) {
                    min = (int) (((float) min) * ((this.showProgress * 0.8f) / 0.8f));
                    this.centerImage.setAlpha(this.showProgress);
                    i2 = (-min) / 2;
                    this.centerImage.setImageCoords(i2, i2, min, min);
                    this.centerImage.draw(canvas);
                }
                if (this.stickerEmojiLayout != null) {
                    canvas.translate((float) (-AndroidUtilities.dp(50.0f)), (float) (((-this.centerImage.getImageHeight()) / 2) - AndroidUtilities.dp(30.0f)));
                    this.stickerEmojiLayout.draw(canvas);
                }
                canvas.restore();
                long currentTimeMillis;
                long j;
                if (this.isVisible != null) {
                    if (this.showProgress != NUM) {
                        currentTimeMillis = System.currentTimeMillis();
                        j = currentTimeMillis - this.lastUpdateTime;
                        this.lastUpdateTime = currentTimeMillis;
                        this.showProgress += ((float) j) / 120.0f;
                        this.containerView.invalidate();
                        if (this.showProgress > NUM) {
                            this.showProgress = 1.0f;
                        }
                    }
                } else if (this.showProgress != null) {
                    currentTimeMillis = System.currentTimeMillis();
                    j = currentTimeMillis - this.lastUpdateTime;
                    this.lastUpdateTime = currentTimeMillis;
                    this.showProgress -= ((float) j) / 120.0f;
                    this.containerView.invalidate();
                    if (this.showProgress < null) {
                        this.showProgress = 0.0f;
                    }
                    if (this.showProgress == null) {
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
