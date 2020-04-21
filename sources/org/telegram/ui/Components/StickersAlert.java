package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Property;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FileRefController;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.tgnet.TLRPC$TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputPhoto;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC$TL_inputStickeredMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_messages_getAttachedStickers;
import org.telegram.tgnet.TLRPC$TL_messages_getStickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_installStickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSetInstallResultArchive;
import org.telegram.tgnet.TLRPC$Vector;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.FeaturedStickerSetInfoCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.ContentPreviewViewer;

public class StickersAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public GridAdapter adapter;
    /* access modifiers changed from: private */
    public boolean clearsInputField;
    /* access modifiers changed from: private */
    public StickersAlertDelegate delegate;
    /* access modifiers changed from: private */
    public FrameLayout emptyView;
    /* access modifiers changed from: private */
    public RecyclerListView gridView;
    /* access modifiers changed from: private */
    public boolean ignoreLayout;
    private TLRPC$InputStickerSet inputStickerSet;
    private StickersAlertInstallDelegate installDelegate;
    /* access modifiers changed from: private */
    public int itemSize;
    /* access modifiers changed from: private */
    public GridLayoutManager layoutManager;
    private ActionBarMenuItem optionsButton;
    private Activity parentActivity;
    /* access modifiers changed from: private */
    public BaseFragment parentFragment;
    private TextView pickerBottomLayout;
    /* access modifiers changed from: private */
    public ContentPreviewViewer.ContentPreviewViewerDelegate previewDelegate = new ContentPreviewViewer.ContentPreviewViewerDelegate() {
        public /* synthetic */ void gifAddedOrDeleted() {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$gifAddedOrDeleted(this);
        }

        public boolean needOpen() {
            return false;
        }

        public void openSet(TLRPC$InputStickerSet tLRPC$InputStickerSet, boolean z) {
        }

        public /* synthetic */ void sendGif(Object obj, boolean z, int i) {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$sendGif(this, obj, z, i);
        }

        public void sendSticker(TLRPC$Document tLRPC$Document, Object obj, boolean z, int i) {
            if (StickersAlert.this.delegate != null) {
                StickersAlert.this.delegate.onStickerSelected(tLRPC$Document, obj, StickersAlert.this.clearsInputField, z, i);
                StickersAlert.this.dismiss();
            }
        }

        public boolean canSchedule() {
            return StickersAlert.this.delegate != null && StickersAlert.this.delegate.canSchedule();
        }

        public boolean isInScheduleMode() {
            return StickersAlert.this.delegate != null && StickersAlert.this.delegate.isInScheduleMode();
        }

        public boolean needSend() {
            return StickersAlert.this.previewSendButton.getVisibility() == 0;
        }

        public long getDialogId() {
            if (StickersAlert.this.parentFragment instanceof ChatActivity) {
                return ((ChatActivity) StickersAlert.this.parentFragment).getDialogId();
            }
            return 0;
        }
    };
    /* access modifiers changed from: private */
    public TextView previewSendButton;
    private View previewSendButtonShadow;
    private int reqId;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    private TLRPC$Document selectedSticker;
    /* access modifiers changed from: private */
    public View[] shadow = new View[2];
    /* access modifiers changed from: private */
    public AnimatorSet[] shadowAnimation = new AnimatorSet[2];
    /* access modifiers changed from: private */
    public boolean showEmoji;
    private boolean showTooltipWhenToggle = true;
    private TextView stickerEmojiTextView;
    /* access modifiers changed from: private */
    public BackupImageView stickerImageView;
    /* access modifiers changed from: private */
    public FrameLayout stickerPreviewLayout;
    /* access modifiers changed from: private */
    public TLRPC$TL_messages_stickerSet stickerSet;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$StickerSetCovered> stickerSetCovereds;
    private RecyclerListView.OnItemClickListener stickersOnItemClickListener;
    private TextView titleTextView;
    private Pattern urlPattern;

    public interface StickersAlertDelegate {
        boolean canSchedule();

        boolean isInScheduleMode();

        void onStickerSelected(TLRPC$Document tLRPC$Document, Object obj, boolean z, boolean z2, int i);
    }

    public interface StickersAlertInstallDelegate {
        void onStickerSetInstalled();

        void onStickerSetUninstalled();
    }

    static /* synthetic */ boolean lambda$init$7(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            try {
                boolean onTouchEvent = super.onTouchEvent(textView, spannable, motionEvent);
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    Selection.removeSelection(spannable);
                }
                return onTouchEvent;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return false;
            }
        }
    }

    public StickersAlert(Context context, Object obj, TLRPC$Photo tLRPC$Photo) {
        super(context, false);
        this.parentActivity = (Activity) context;
        TLRPC$TL_messages_getAttachedStickers tLRPC$TL_messages_getAttachedStickers = new TLRPC$TL_messages_getAttachedStickers();
        TLRPC$TL_inputStickeredMediaPhoto tLRPC$TL_inputStickeredMediaPhoto = new TLRPC$TL_inputStickeredMediaPhoto();
        TLRPC$TL_inputPhoto tLRPC$TL_inputPhoto = new TLRPC$TL_inputPhoto();
        tLRPC$TL_inputStickeredMediaPhoto.id = tLRPC$TL_inputPhoto;
        tLRPC$TL_inputPhoto.id = tLRPC$Photo.id;
        tLRPC$TL_inputPhoto.access_hash = tLRPC$Photo.access_hash;
        byte[] bArr = tLRPC$Photo.file_reference;
        tLRPC$TL_inputPhoto.file_reference = bArr;
        if (bArr == null) {
            tLRPC$TL_inputPhoto.file_reference = new byte[0];
        }
        tLRPC$TL_messages_getAttachedStickers.media = tLRPC$TL_inputStickeredMediaPhoto;
        this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getAttachedStickers, new RequestDelegate(obj, tLRPC$TL_messages_getAttachedStickers, new RequestDelegate(tLRPC$TL_messages_getAttachedStickers) {
            private final /* synthetic */ TLRPC$TL_messages_getAttachedStickers f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                StickersAlert.this.lambda$new$1$StickersAlert(this.f$1, tLObject, tLRPC$TL_error);
            }
        }) {
            private final /* synthetic */ Object f$1;
            private final /* synthetic */ TLRPC$TL_messages_getAttachedStickers f$2;
            private final /* synthetic */ RequestDelegate f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                StickersAlert.this.lambda$new$2$StickersAlert(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
            }
        });
        init(context);
    }

    public /* synthetic */ void lambda$new$1$StickersAlert(TLRPC$TL_messages_getAttachedStickers tLRPC$TL_messages_getAttachedStickers, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, tLRPC$TL_messages_getAttachedStickers) {
            private final /* synthetic */ TLRPC$TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ TLRPC$TL_messages_getAttachedStickers f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                StickersAlert.this.lambda$null$0$StickersAlert(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$StickersAlert(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_messages_getAttachedStickers tLRPC$TL_messages_getAttachedStickers) {
        this.reqId = 0;
        if (tLRPC$TL_error == null) {
            TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
            if (tLRPC$Vector.objects.isEmpty()) {
                dismiss();
            } else if (tLRPC$Vector.objects.size() == 1) {
                TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
                this.inputStickerSet = tLRPC$TL_inputStickerSetID;
                TLRPC$StickerSet tLRPC$StickerSet = ((TLRPC$StickerSetCovered) tLRPC$Vector.objects.get(0)).set;
                tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
                tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
                loadStickerSet();
            } else {
                this.stickerSetCovereds = new ArrayList<>();
                for (int i = 0; i < tLRPC$Vector.objects.size(); i++) {
                    this.stickerSetCovereds.add((TLRPC$StickerSetCovered) tLRPC$Vector.objects.get(i));
                }
                this.gridView.setLayoutParams(LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
                this.titleTextView.setVisibility(8);
                this.shadow[0].setVisibility(8);
                this.adapter.notifyDataSetChanged();
            }
        } else {
            AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, this.parentFragment, tLRPC$TL_messages_getAttachedStickers, new Object[0]);
            dismiss();
        }
    }

    public /* synthetic */ void lambda$new$2$StickersAlert(Object obj, TLRPC$TL_messages_getAttachedStickers tLRPC$TL_messages_getAttachedStickers, RequestDelegate requestDelegate, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null || !FileRefController.isFileRefError(tLRPC$TL_error.text) || obj == null) {
            requestDelegate.run(tLObject, tLRPC$TL_error);
            return;
        }
        FileRefController.getInstance(this.currentAccount).requestReference(obj, tLRPC$TL_messages_getAttachedStickers, requestDelegate);
    }

    public StickersAlert(Context context, BaseFragment baseFragment, TLRPC$InputStickerSet tLRPC$InputStickerSet, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, StickersAlertDelegate stickersAlertDelegate) {
        super(context, false);
        this.delegate = stickersAlertDelegate;
        this.inputStickerSet = tLRPC$InputStickerSet;
        this.stickerSet = tLRPC$TL_messages_stickerSet;
        this.parentFragment = baseFragment;
        loadStickerSet();
        init(context);
    }

    public void setClearsInputField(boolean z) {
        this.clearsInputField = z;
    }

    private void loadStickerSet() {
        String str;
        if (this.inputStickerSet != null) {
            MediaDataController instance = MediaDataController.getInstance(this.currentAccount);
            if (this.stickerSet == null && (str = this.inputStickerSet.short_name) != null) {
                this.stickerSet = instance.getStickerSetByName(str);
            }
            if (this.stickerSet == null) {
                this.stickerSet = instance.getStickerSetById(this.inputStickerSet.id);
            }
            if (this.stickerSet == null) {
                TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet = new TLRPC$TL_messages_getStickerSet();
                tLRPC$TL_messages_getStickerSet.stickerset = this.inputStickerSet;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getStickerSet, new RequestDelegate(instance) {
                    private final /* synthetic */ MediaDataController f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        StickersAlert.this.lambda$loadStickerSet$4$StickersAlert(this.f$1, tLObject, tLRPC$TL_error);
                    }
                });
            } else {
                if (this.adapter != null) {
                    updateSendButton();
                    updateFields();
                    this.adapter.notifyDataSetChanged();
                }
                instance.preloadStickerSetThumb(this.stickerSet);
            }
        }
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.stickerSet;
        if (tLRPC$TL_messages_stickerSet != null) {
            this.showEmoji = !tLRPC$TL_messages_stickerSet.set.masks;
        }
    }

    public /* synthetic */ void lambda$loadStickerSet$4$StickersAlert(MediaDataController mediaDataController, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, mediaDataController) {
            private final /* synthetic */ TLRPC$TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ MediaDataController f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                StickersAlert.this.lambda$null$3$StickersAlert(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$3$StickersAlert(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, MediaDataController mediaDataController) {
        this.reqId = 0;
        if (tLRPC$TL_error == null) {
            this.optionsButton.setVisibility(0);
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) tLObject;
            this.stickerSet = tLRPC$TL_messages_stickerSet;
            this.showEmoji = !tLRPC$TL_messages_stickerSet.set.masks;
            mediaDataController.preloadStickerSetThumb(tLRPC$TL_messages_stickerSet);
            updateSendButton();
            updateFields();
            this.adapter.notifyDataSetChanged();
            return;
        }
        Toast.makeText(getContext(), LocaleController.getString("AddStickersNotFound", NUM), 0).show();
        dismiss();
    }

    private void init(Context context) {
        Context context2 = context;
        AnonymousClass2 r2 = new FrameLayout(context2) {
            private boolean fullHeight;
            private int lastNotifyWidth;
            private RectF rect = new RectF();

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || StickersAlert.this.scrollOffsetY == 0 || motionEvent.getY() >= ((float) StickersAlert.this.scrollOffsetY)) {
                    return super.onInterceptTouchEvent(motionEvent);
                }
                StickersAlert.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                return !StickersAlert.this.isDismissed() && super.onTouchEvent(motionEvent);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int dp;
                int i3;
                int size = View.MeasureSpec.getSize(i2);
                boolean z = true;
                if (Build.VERSION.SDK_INT >= 21) {
                    boolean unused = StickersAlert.this.ignoreLayout = true;
                    setPadding(StickersAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, StickersAlert.this.backgroundPaddingLeft, 0);
                    boolean unused2 = StickersAlert.this.ignoreLayout = false;
                }
                int unused3 = StickersAlert.this.itemSize = (View.MeasureSpec.getSize(i) - AndroidUtilities.dp(36.0f)) / 5;
                if (StickersAlert.this.stickerSetCovereds != null) {
                    dp = AndroidUtilities.dp(56.0f) + (AndroidUtilities.dp(60.0f) * StickersAlert.this.stickerSetCovereds.size()) + (StickersAlert.this.adapter.stickersRowCount * AndroidUtilities.dp(82.0f)) + StickersAlert.this.backgroundPaddingTop;
                    i3 = AndroidUtilities.dp(24.0f);
                } else {
                    dp = AndroidUtilities.dp(96.0f) + (Math.max(3, StickersAlert.this.stickerSet != null ? (int) Math.ceil((double) (((float) StickersAlert.this.stickerSet.documents.size()) / 5.0f)) : 0) * AndroidUtilities.dp(82.0f)) + StickersAlert.this.backgroundPaddingTop;
                    i3 = AndroidUtilities.statusBarHeight;
                }
                int i4 = dp + i3;
                int i5 = size / 5;
                double d = (double) i5;
                Double.isNaN(d);
                int i6 = ((double) i4) < d * 3.2d ? 0 : i5 * 2;
                if (i6 != 0 && i4 < size) {
                    i6 -= size - i4;
                }
                if (i6 == 0) {
                    i6 = StickersAlert.this.backgroundPaddingTop;
                }
                if (StickersAlert.this.stickerSetCovereds != null) {
                    i6 += AndroidUtilities.dp(8.0f);
                }
                if (StickersAlert.this.gridView.getPaddingTop() != i6) {
                    boolean unused4 = StickersAlert.this.ignoreLayout = true;
                    StickersAlert.this.gridView.setPadding(AndroidUtilities.dp(10.0f), i6, AndroidUtilities.dp(10.0f), 0);
                    StickersAlert.this.emptyView.setPadding(0, i6, 0, 0);
                    boolean unused5 = StickersAlert.this.ignoreLayout = false;
                }
                if (i4 < size) {
                    z = false;
                }
                this.fullHeight = z;
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(Math.min(i4, size), NUM));
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                int i5 = i3 - i;
                if (this.lastNotifyWidth != i5) {
                    this.lastNotifyWidth = i5;
                    if (!(StickersAlert.this.adapter == null || StickersAlert.this.stickerSetCovereds == null)) {
                        StickersAlert.this.adapter.notifyDataSetChanged();
                    }
                }
                super.onLayout(z, i, i2, i3, i4);
                StickersAlert.this.updateLayout();
            }

            public void requestLayout() {
                if (!StickersAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            /* access modifiers changed from: protected */
            /* JADX WARNING: Removed duplicated region for block: B:15:0x00b1  */
            /* JADX WARNING: Removed duplicated region for block: B:18:0x0144  */
            /* JADX WARNING: Removed duplicated region for block: B:20:? A[RETURN, SYNTHETIC] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onDraw(android.graphics.Canvas r14) {
                /*
                    r13 = this;
                    org.telegram.ui.Components.StickersAlert r0 = org.telegram.ui.Components.StickersAlert.this
                    int r0 = r0.scrollOffsetY
                    org.telegram.ui.Components.StickersAlert r1 = org.telegram.ui.Components.StickersAlert.this
                    int r1 = r1.backgroundPaddingTop
                    int r0 = r0 - r1
                    r1 = 1086324736(0x40CLASSNAME, float:6.0)
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    int r0 = r0 + r1
                    org.telegram.ui.Components.StickersAlert r1 = org.telegram.ui.Components.StickersAlert.this
                    int r1 = r1.scrollOffsetY
                    org.telegram.ui.Components.StickersAlert r2 = org.telegram.ui.Components.StickersAlert.this
                    int r2 = r2.backgroundPaddingTop
                    int r1 = r1 - r2
                    r2 = 1095761920(0x41500000, float:13.0)
                    int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                    int r1 = r1 - r2
                    int r2 = r13.getMeasuredHeight()
                    r3 = 1097859072(0x41700000, float:15.0)
                    int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                    int r2 = r2 + r3
                    org.telegram.ui.Components.StickersAlert r3 = org.telegram.ui.Components.StickersAlert.this
                    int r3 = r3.backgroundPaddingTop
                    int r2 = r2 + r3
                    int r3 = android.os.Build.VERSION.SDK_INT
                    r4 = 0
                    r5 = 1065353216(0x3var_, float:1.0)
                    r6 = 21
                    if (r3 < r6) goto L_0x0092
                    int r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    int r1 = r1 + r3
                    int r0 = r0 + r3
                    int r2 = r2 - r3
                    boolean r3 = r13.fullHeight
                    if (r3 == 0) goto L_0x0092
                    org.telegram.ui.Components.StickersAlert r3 = org.telegram.ui.Components.StickersAlert.this
                    int r3 = r3.backgroundPaddingTop
                    int r3 = r3 + r1
                    int r6 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    int r7 = r6 * 2
                    if (r3 >= r7) goto L_0x0077
                    int r3 = r6 * 2
                    int r3 = r3 - r1
                    org.telegram.ui.Components.StickersAlert r7 = org.telegram.ui.Components.StickersAlert.this
                    int r7 = r7.backgroundPaddingTop
                    int r3 = r3 - r7
                    int r3 = java.lang.Math.min(r6, r3)
                    int r1 = r1 - r3
                    int r2 = r2 + r3
                    int r3 = r3 * 2
                    float r3 = (float) r3
                    int r6 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    float r6 = (float) r6
                    float r3 = r3 / r6
                    float r3 = java.lang.Math.min(r5, r3)
                    float r3 = r5 - r3
                    goto L_0x0079
                L_0x0077:
                    r3 = 1065353216(0x3var_, float:1.0)
                L_0x0079:
                    org.telegram.ui.Components.StickersAlert r6 = org.telegram.ui.Components.StickersAlert.this
                    int r6 = r6.backgroundPaddingTop
                    int r6 = r6 + r1
                    int r7 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    if (r6 >= r7) goto L_0x0094
                    int r6 = r7 - r1
                    org.telegram.ui.Components.StickersAlert r8 = org.telegram.ui.Components.StickersAlert.this
                    int r8 = r8.backgroundPaddingTop
                    int r6 = r6 - r8
                    int r6 = java.lang.Math.min(r7, r6)
                    goto L_0x0095
                L_0x0092:
                    r3 = 1065353216(0x3var_, float:1.0)
                L_0x0094:
                    r6 = 0
                L_0x0095:
                    org.telegram.ui.Components.StickersAlert r7 = org.telegram.ui.Components.StickersAlert.this
                    android.graphics.drawable.Drawable r7 = r7.shadowDrawable
                    int r8 = r13.getMeasuredWidth()
                    r7.setBounds(r4, r1, r8, r2)
                    org.telegram.ui.Components.StickersAlert r2 = org.telegram.ui.Components.StickersAlert.this
                    android.graphics.drawable.Drawable r2 = r2.shadowDrawable
                    r2.draw(r14)
                    java.lang.String r2 = "dialogBackground"
                    int r4 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                    if (r4 == 0) goto L_0x0100
                    android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    int r5 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                    r4.setColor(r5)
                    android.graphics.RectF r4 = r13.rect
                    org.telegram.ui.Components.StickersAlert r5 = org.telegram.ui.Components.StickersAlert.this
                    int r5 = r5.backgroundPaddingLeft
                    float r5 = (float) r5
                    org.telegram.ui.Components.StickersAlert r7 = org.telegram.ui.Components.StickersAlert.this
                    int r7 = r7.backgroundPaddingTop
                    int r7 = r7 + r1
                    float r7 = (float) r7
                    int r8 = r13.getMeasuredWidth()
                    org.telegram.ui.Components.StickersAlert r9 = org.telegram.ui.Components.StickersAlert.this
                    int r9 = r9.backgroundPaddingLeft
                    int r8 = r8 - r9
                    float r8 = (float) r8
                    org.telegram.ui.Components.StickersAlert r9 = org.telegram.ui.Components.StickersAlert.this
                    int r9 = r9.backgroundPaddingTop
                    int r9 = r9 + r1
                    r1 = 1103101952(0x41CLASSNAME, float:24.0)
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    int r9 = r9 + r1
                    float r1 = (float) r9
                    r4.set(r5, r7, r8, r1)
                    android.graphics.RectF r1 = r13.rect
                    r4 = 1094713344(0x41400000, float:12.0)
                    int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
                    float r5 = (float) r5
                    float r5 = r5 * r3
                    int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                    float r4 = (float) r4
                    float r4 = r4 * r3
                    android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    r14.drawRoundRect(r1, r5, r4, r3)
                L_0x0100:
                    r1 = 1108344832(0x42100000, float:36.0)
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    android.graphics.RectF r3 = r13.rect
                    int r4 = r13.getMeasuredWidth()
                    int r4 = r4 - r1
                    int r4 = r4 / 2
                    float r4 = (float) r4
                    float r5 = (float) r0
                    int r7 = r13.getMeasuredWidth()
                    int r7 = r7 + r1
                    int r7 = r7 / 2
                    float r1 = (float) r7
                    r7 = 1082130432(0x40800000, float:4.0)
                    int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                    int r0 = r0 + r7
                    float r0 = (float) r0
                    r3.set(r4, r5, r1, r0)
                    android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    java.lang.String r1 = "key_sheet_scrollUp"
                    int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                    r0.setColor(r1)
                    android.graphics.RectF r0 = r13.rect
                    r1 = 1073741824(0x40000000, float:2.0)
                    int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    float r3 = (float) r3
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    float r1 = (float) r1
                    android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    r14.drawRoundRect(r0, r3, r1, r4)
                    if (r6 <= 0) goto L_0x018e
                    int r0 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                    r1 = 255(0xff, float:3.57E-43)
                    int r2 = android.graphics.Color.red(r0)
                    float r2 = (float) r2
                    r3 = 1061997773(0x3f4ccccd, float:0.8)
                    float r2 = r2 * r3
                    int r2 = (int) r2
                    int r4 = android.graphics.Color.green(r0)
                    float r4 = (float) r4
                    float r4 = r4 * r3
                    int r4 = (int) r4
                    int r0 = android.graphics.Color.blue(r0)
                    float r0 = (float) r0
                    float r0 = r0 * r3
                    int r0 = (int) r0
                    int r0 = android.graphics.Color.argb(r1, r2, r4, r0)
                    android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    r1.setColor(r0)
                    org.telegram.ui.Components.StickersAlert r0 = org.telegram.ui.Components.StickersAlert.this
                    int r0 = r0.backgroundPaddingLeft
                    float r8 = (float) r0
                    int r0 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    int r0 = r0 - r6
                    float r9 = (float) r0
                    int r0 = r13.getMeasuredWidth()
                    org.telegram.ui.Components.StickersAlert r1 = org.telegram.ui.Components.StickersAlert.this
                    int r1 = r1.backgroundPaddingLeft
                    int r0 = r0 - r1
                    float r10 = (float) r0
                    int r0 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    float r11 = (float) r0
                    android.graphics.Paint r12 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    r7 = r14
                    r7.drawRect(r8, r9, r10, r11, r12)
                L_0x018e:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickersAlert.AnonymousClass2.onDraw(android.graphics.Canvas):void");
            }
        };
        this.containerView = r2;
        r2.setWillNotDraw(false);
        ViewGroup viewGroup = this.containerView;
        int i = this.backgroundPaddingLeft;
        viewGroup.setPadding(i, 0, i, 0);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        layoutParams.topMargin = AndroidUtilities.dp(48.0f);
        this.shadow[0] = new View(context2);
        this.shadow[0].setBackgroundColor(Theme.getColor("dialogShadowLine"));
        this.shadow[0].setAlpha(0.0f);
        this.shadow[0].setVisibility(4);
        this.shadow[0].setTag(1);
        this.containerView.addView(this.shadow[0], layoutParams);
        AnonymousClass3 r22 = new RecyclerListView(context2) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                boolean onInterceptTouchEvent = ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, StickersAlert.this.gridView, 0, StickersAlert.this.previewDelegate);
                if (super.onInterceptTouchEvent(motionEvent) || onInterceptTouchEvent) {
                    return true;
                }
                return false;
            }

            public void requestLayout() {
                if (!StickersAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.gridView = r22;
        r22.setTag(14);
        RecyclerListView recyclerListView = this.gridView;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 5);
        this.layoutManager = gridLayoutManager;
        recyclerListView.setLayoutManager(gridLayoutManager);
        this.layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int i) {
                if ((StickersAlert.this.stickerSetCovereds == null || !(StickersAlert.this.adapter.cache.get(i) instanceof Integer)) && i != StickersAlert.this.adapter.totalItems) {
                    return 1;
                }
                return StickersAlert.this.adapter.stickersPerRow;
            }
        });
        RecyclerListView recyclerListView2 = this.gridView;
        GridAdapter gridAdapter = new GridAdapter(context2);
        this.adapter = gridAdapter;
        recyclerListView2.setAdapter(gridAdapter);
        this.gridView.setVerticalScrollBarEnabled(false);
        this.gridView.addItemDecoration(new RecyclerView.ItemDecoration(this) {
            public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                rect.left = 0;
                rect.right = 0;
                rect.bottom = 0;
                rect.top = 0;
            }
        });
        this.gridView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        this.gridView.setClipToPadding(false);
        this.gridView.setEnabled(true);
        this.gridView.setGlowColor(Theme.getColor("dialogScrollGlow"));
        this.gridView.setOnTouchListener(new View.OnTouchListener() {
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return StickersAlert.this.lambda$init$5$StickersAlert(view, motionEvent);
            }
        });
        this.gridView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                StickersAlert.this.updateLayout();
            }
        });
        $$Lambda$StickersAlert$pqY8OpOZq71Xrxq3rh8h2TYv4Gk r23 = new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                StickersAlert.this.lambda$init$6$StickersAlert(view, i);
            }
        };
        this.stickersOnItemClickListener = r23;
        this.gridView.setOnItemClickListener((RecyclerListView.OnItemClickListener) r23);
        this.containerView.addView(this.gridView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 48.0f, 0.0f, 48.0f));
        AnonymousClass7 r24 = new FrameLayout(context2) {
            public void requestLayout() {
                if (!StickersAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.emptyView = r24;
        this.containerView.addView(r24, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.gridView.setEmptyView(this.emptyView);
        this.emptyView.setOnTouchListener($$Lambda$StickersAlert$x4wnZ8iSeVTFndyEhtM4m12jW8.INSTANCE);
        TextView textView = new TextView(context2);
        this.titleTextView = textView;
        textView.setLines(1);
        this.titleTextView.setSingleLine(true);
        this.titleTextView.setTextColor(Theme.getColor("dialogTextBlack"));
        this.titleTextView.setTextSize(1, 20.0f);
        this.titleTextView.setLinkTextColor(Theme.getColor("dialogTextLink"));
        this.titleTextView.setHighlightColor(Theme.getColor("dialogLinkSelection"));
        this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.titleTextView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.titleTextView.setGravity(16);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.containerView.addView(this.titleTextView, LayoutHelper.createFrame(-1, 50.0f, 51, 0.0f, 0.0f, 40.0f, 0.0f));
        ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context2, (ActionBarMenu) null, 0, Theme.getColor("key_sheet_other"));
        this.optionsButton = actionBarMenuItem;
        actionBarMenuItem.setLongClickEnabled(false);
        this.optionsButton.setSubMenuOpenSide(2);
        this.optionsButton.setIcon(NUM);
        this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("player_actionBarSelector"), 1));
        this.containerView.addView(this.optionsButton, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, 5.0f, 5.0f, 0.0f));
        this.optionsButton.addSubItem(1, NUM, LocaleController.getString("StickersShare", NUM));
        this.optionsButton.addSubItem(2, NUM, LocaleController.getString("CopyLink", NUM));
        this.optionsButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                StickersAlert.this.lambda$init$8$StickersAlert(view);
            }
        });
        this.optionsButton.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() {
            public final void onItemClick(int i) {
                StickersAlert.this.onSubItemClick(i);
            }
        });
        this.optionsButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        this.optionsButton.setVisibility(this.inputStickerSet != null ? 0 : 8);
        this.emptyView.addView(new RadialProgressView(context2), LayoutHelper.createFrame(-2, -2, 17));
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83);
        layoutParams2.bottomMargin = AndroidUtilities.dp(48.0f);
        this.shadow[1] = new View(context2);
        this.shadow[1].setBackgroundColor(Theme.getColor("dialogShadowLine"));
        this.containerView.addView(this.shadow[1], layoutParams2);
        TextView textView2 = new TextView(context2);
        this.pickerBottomLayout = textView2;
        textView2.setBackgroundDrawable(Theme.createSelectorWithBackgroundDrawable(Theme.getColor("dialogBackground"), Theme.getColor("listSelectorSDK21")));
        this.pickerBottomLayout.setTextColor(Theme.getColor("dialogTextBlue2"));
        this.pickerBottomLayout.setTextSize(1, 14.0f);
        this.pickerBottomLayout.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.pickerBottomLayout.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.pickerBottomLayout.setGravity(17);
        this.containerView.addView(this.pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 83));
        FrameLayout frameLayout = new FrameLayout(context2);
        this.stickerPreviewLayout = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor("dialogBackground") & -NUM);
        this.stickerPreviewLayout.setVisibility(8);
        this.stickerPreviewLayout.setSoundEffectsEnabled(false);
        this.containerView.addView(this.stickerPreviewLayout, LayoutHelper.createFrame(-1, -1.0f));
        this.stickerPreviewLayout.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                StickersAlert.this.lambda$init$9$StickersAlert(view);
            }
        });
        BackupImageView backupImageView = new BackupImageView(context2);
        this.stickerImageView = backupImageView;
        backupImageView.setAspectFit(true);
        this.stickerImageView.setLayerNum(5);
        this.stickerPreviewLayout.addView(this.stickerImageView);
        TextView textView3 = new TextView(context2);
        this.stickerEmojiTextView = textView3;
        textView3.setTextSize(1, 30.0f);
        this.stickerEmojiTextView.setGravity(85);
        this.stickerPreviewLayout.addView(this.stickerEmojiTextView);
        TextView textView4 = new TextView(context2);
        this.previewSendButton = textView4;
        textView4.setTextSize(1, 14.0f);
        this.previewSendButton.setTextColor(Theme.getColor("dialogTextBlue2"));
        this.previewSendButton.setGravity(17);
        this.previewSendButton.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.previewSendButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
        this.previewSendButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.stickerPreviewLayout.addView(this.previewSendButton, LayoutHelper.createFrame(-1, 48, 83));
        this.previewSendButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                StickersAlert.this.lambda$init$10$StickersAlert(view);
            }
        });
        FrameLayout.LayoutParams layoutParams3 = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83);
        layoutParams3.bottomMargin = AndroidUtilities.dp(48.0f);
        View view = new View(context2);
        this.previewSendButtonShadow = view;
        view.setBackgroundColor(Theme.getColor("dialogShadowLine"));
        this.stickerPreviewLayout.addView(this.previewSendButtonShadow, layoutParams3);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        updateFields();
        updateSendButton();
        this.adapter.notifyDataSetChanged();
    }

    public /* synthetic */ boolean lambda$init$5$StickersAlert(View view, MotionEvent motionEvent) {
        return ContentPreviewViewer.getInstance().onTouch(motionEvent, this.gridView, 0, this.stickersOnItemClickListener, this.previewDelegate);
    }

    public /* synthetic */ void lambda$init$6$StickersAlert(View view, int i) {
        boolean z;
        if (this.stickerSetCovereds != null) {
            TLRPC$StickerSetCovered tLRPC$StickerSetCovered = (TLRPC$StickerSetCovered) this.adapter.positionsToSets.get(i);
            if (tLRPC$StickerSetCovered != null) {
                dismiss();
                TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
                TLRPC$StickerSet tLRPC$StickerSet = tLRPC$StickerSetCovered.set;
                tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
                tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
                new StickersAlert(this.parentActivity, this.parentFragment, tLRPC$TL_inputStickerSetID, (TLRPC$TL_messages_stickerSet) null, (StickersAlertDelegate) null).show();
                return;
            }
            return;
        }
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.stickerSet;
        if (tLRPC$TL_messages_stickerSet != null && i >= 0 && i < tLRPC$TL_messages_stickerSet.documents.size()) {
            this.selectedSticker = this.stickerSet.documents.get(i);
            int i2 = 0;
            while (true) {
                if (i2 >= this.selectedSticker.attributes.size()) {
                    break;
                }
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = this.selectedSticker.attributes.get(i2);
                if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) {
                    String str = tLRPC$DocumentAttribute.alt;
                    if (str != null && str.length() > 0) {
                        TextView textView = this.stickerEmojiTextView;
                        textView.setText(Emoji.replaceEmoji(tLRPC$DocumentAttribute.alt, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(30.0f), false));
                        z = true;
                    }
                } else {
                    i2++;
                }
            }
            z = false;
            if (!z) {
                this.stickerEmojiTextView.setText(Emoji.replaceEmoji(MediaDataController.getInstance(this.currentAccount).getEmojiForSticker(this.selectedSticker.id), this.stickerEmojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(30.0f), false));
            }
            this.stickerImageView.getImageReceiver().setImage(ImageLocation.getForDocument(this.selectedSticker), (String) null, ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(this.selectedSticker.thumbs, 90), this.selectedSticker), (String) null, "webp", (Object) this.stickerSet, 1);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.stickerPreviewLayout.getLayoutParams();
            layoutParams.topMargin = this.scrollOffsetY;
            this.stickerPreviewLayout.setLayoutParams(layoutParams);
            this.stickerPreviewLayout.setVisibility(0);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.stickerPreviewLayout, View.ALPHA, new float[]{0.0f, 1.0f})});
            animatorSet.setDuration(200);
            animatorSet.start();
        }
    }

    public /* synthetic */ void lambda$init$8$StickersAlert(View view) {
        this.optionsButton.toggleSubMenu();
    }

    public /* synthetic */ void lambda$init$9$StickersAlert(View view) {
        hidePreview();
    }

    public /* synthetic */ void lambda$init$10$StickersAlert(View view) {
        this.delegate.onStickerSelected(this.selectedSticker, this.stickerSet, this.clearsInputField, true, 0);
        dismiss();
    }

    private void updateSendButton() {
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet;
        Point point = AndroidUtilities.displaySize;
        int min = (int) (((float) (Math.min(point.x, point.y) / 2)) / AndroidUtilities.density);
        if (this.delegate == null || ((tLRPC$TL_messages_stickerSet = this.stickerSet) != null && tLRPC$TL_messages_stickerSet.set.masks)) {
            this.previewSendButton.setText(LocaleController.getString("Close", NUM).toUpperCase());
            this.stickerImageView.setLayoutParams(LayoutHelper.createFrame(min, min, 17));
            this.stickerEmojiTextView.setLayoutParams(LayoutHelper.createFrame(min, min, 17));
            this.previewSendButton.setVisibility(8);
            this.previewSendButtonShadow.setVisibility(8);
            return;
        }
        this.previewSendButton.setText(LocaleController.getString("SendSticker", NUM).toUpperCase());
        float f = (float) min;
        this.stickerImageView.setLayoutParams(LayoutHelper.createFrame(min, f, 17, 0.0f, 0.0f, 0.0f, 30.0f));
        this.stickerEmojiTextView.setLayoutParams(LayoutHelper.createFrame(min, f, 17, 0.0f, 0.0f, 0.0f, 30.0f));
        this.previewSendButton.setVisibility(0);
        this.previewSendButtonShadow.setVisibility(0);
    }

    public void setInstallDelegate(StickersAlertInstallDelegate stickersAlertInstallDelegate) {
        this.installDelegate = stickersAlertInstallDelegate;
    }

    /* access modifiers changed from: private */
    public void onSubItemClick(int i) {
        if (this.stickerSet != null) {
            String str = "https://" + MessagesController.getInstance(this.currentAccount).linkPrefix + "/addstickers/" + this.stickerSet.set.short_name;
            if (i == 1) {
                ShareAlert shareAlert = new ShareAlert(getContext(), (ArrayList<MessageObject>) null, str, false, str, false);
                BaseFragment baseFragment = this.parentFragment;
                if (baseFragment != null) {
                    baseFragment.showDialog(shareAlert);
                } else {
                    shareAlert.show();
                }
            } else if (i == 2) {
                try {
                    AndroidUtilities.addToClipboard(str);
                    Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("LinkCopied", NUM), 0).show();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0081, code lost:
        r4 = r4;
     */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0086  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00ad  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x010b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateFields() {
        /*
            r10 = this;
            android.widget.TextView r0 = r10.titleTextView
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r0 = r10.stickerSet
            java.lang.String r1 = "dialogTextBlue2"
            if (r0 == 0) goto L_0x0157
            r0 = 0
            r2 = 0
            java.util.regex.Pattern r3 = r10.urlPattern     // Catch:{ Exception -> 0x007c }
            if (r3 != 0) goto L_0x0019
            java.lang.String r3 = "@[a-zA-Z\\d_]{1,32}"
            java.util.regex.Pattern r3 = java.util.regex.Pattern.compile(r3)     // Catch:{ Exception -> 0x007c }
            r10.urlPattern = r3     // Catch:{ Exception -> 0x007c }
        L_0x0019:
            java.util.regex.Pattern r3 = r10.urlPattern     // Catch:{ Exception -> 0x007c }
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r4 = r10.stickerSet     // Catch:{ Exception -> 0x007c }
            org.telegram.tgnet.TLRPC$StickerSet r4 = r4.set     // Catch:{ Exception -> 0x007c }
            java.lang.String r4 = r4.title     // Catch:{ Exception -> 0x007c }
            java.util.regex.Matcher r3 = r3.matcher(r4)     // Catch:{ Exception -> 0x007c }
            r4 = r0
        L_0x0026:
            boolean r5 = r3.find()     // Catch:{ Exception -> 0x0079 }
            if (r5 == 0) goto L_0x0081
            if (r4 != 0) goto L_0x0048
            android.text.SpannableStringBuilder r5 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x0079 }
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r6 = r10.stickerSet     // Catch:{ Exception -> 0x0079 }
            org.telegram.tgnet.TLRPC$StickerSet r6 = r6.set     // Catch:{ Exception -> 0x0079 }
            java.lang.String r6 = r6.title     // Catch:{ Exception -> 0x0079 }
            r5.<init>(r6)     // Catch:{ Exception -> 0x0079 }
            android.widget.TextView r4 = r10.titleTextView     // Catch:{ Exception -> 0x0045 }
            org.telegram.ui.Components.StickersAlert$LinkMovementMethodMy r6 = new org.telegram.ui.Components.StickersAlert$LinkMovementMethodMy     // Catch:{ Exception -> 0x0045 }
            r6.<init>()     // Catch:{ Exception -> 0x0045 }
            r4.setMovementMethod(r6)     // Catch:{ Exception -> 0x0045 }
            r4 = r5
            goto L_0x0048
        L_0x0045:
            r3 = move-exception
            r0 = r5
            goto L_0x007d
        L_0x0048:
            int r5 = r3.start()     // Catch:{ Exception -> 0x0079 }
            int r6 = r3.end()     // Catch:{ Exception -> 0x0079 }
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r7 = r10.stickerSet     // Catch:{ Exception -> 0x0079 }
            org.telegram.tgnet.TLRPC$StickerSet r7 = r7.set     // Catch:{ Exception -> 0x0079 }
            java.lang.String r7 = r7.title     // Catch:{ Exception -> 0x0079 }
            char r7 = r7.charAt(r5)     // Catch:{ Exception -> 0x0079 }
            r8 = 64
            if (r7 == r8) goto L_0x0060
            int r5 = r5 + 1
        L_0x0060:
            org.telegram.ui.Components.StickersAlert$8 r7 = new org.telegram.ui.Components.StickersAlert$8     // Catch:{ Exception -> 0x0079 }
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r8 = r10.stickerSet     // Catch:{ Exception -> 0x0079 }
            org.telegram.tgnet.TLRPC$StickerSet r8 = r8.set     // Catch:{ Exception -> 0x0079 }
            java.lang.String r8 = r8.title     // Catch:{ Exception -> 0x0079 }
            int r9 = r5 + 1
            java.lang.CharSequence r8 = r8.subSequence(r9, r6)     // Catch:{ Exception -> 0x0079 }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x0079 }
            r7.<init>(r8)     // Catch:{ Exception -> 0x0079 }
            r4.setSpan(r7, r5, r6, r2)     // Catch:{ Exception -> 0x0079 }
            goto L_0x0026
        L_0x0079:
            r3 = move-exception
            r0 = r4
            goto L_0x007d
        L_0x007c:
            r3 = move-exception
        L_0x007d:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            r4 = r0
        L_0x0081:
            android.widget.TextView r0 = r10.titleTextView
            if (r4 == 0) goto L_0x0086
            goto L_0x008c
        L_0x0086:
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r3 = r10.stickerSet
            org.telegram.tgnet.TLRPC$StickerSet r3 = r3.set
            java.lang.String r4 = r3.title
        L_0x008c:
            r0.setText(r4)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r0 = r10.stickerSet
            org.telegram.tgnet.TLRPC$StickerSet r0 = r0.set
            java.lang.String r3 = "MasksCount"
            java.lang.String r4 = "Stickers"
            r5 = 1
            if (r0 == 0) goto L_0x010b
            int r0 = r10.currentAccount
            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r6 = r10.stickerSet
            org.telegram.tgnet.TLRPC$StickerSet r6 = r6.set
            long r6 = r6.id
            boolean r0 = r0.isStickerPackInstalled((long) r6)
            if (r0 != 0) goto L_0x00ad
            goto L_0x010b
        L_0x00ad:
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r0 = r10.stickerSet
            org.telegram.tgnet.TLRPC$StickerSet r1 = r0.set
            boolean r1 = r1.masks
            r6 = 2131626516(0x7f0e0a14, float:1.888027E38)
            java.lang.String r7 = "RemoveStickersCount"
            if (r1 == 0) goto L_0x00d1
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r0 = r0.documents
            int r0 = r0.size()
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r3, r0)
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r6, r1)
            java.lang.String r0 = r0.toUpperCase()
            goto L_0x00e7
        L_0x00d1:
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r0 = r0.documents
            int r0 = r0.size()
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r4, r0)
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r6, r1)
            java.lang.String r0 = r0.toUpperCase()
        L_0x00e7:
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r1 = r10.stickerSet
            org.telegram.tgnet.TLRPC$StickerSet r1 = r1.set
            boolean r1 = r1.official
            java.lang.String r2 = "dialogTextRed"
            if (r1 == 0) goto L_0x00fe
            org.telegram.ui.Components.-$$Lambda$StickersAlert$DGFO8T79ri5mrUocraZaq3nn2Bk r1 = new org.telegram.ui.Components.-$$Lambda$StickersAlert$DGFO8T79ri5mrUocraZaq3nn2Bk
            r1.<init>()
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r10.setButton(r1, r0, r2)
            goto L_0x0151
        L_0x00fe:
            org.telegram.ui.Components.-$$Lambda$StickersAlert$2Q0RkK5JO2x7zurWeDAdNpHEWcg r1 = new org.telegram.ui.Components.-$$Lambda$StickersAlert$2Q0RkK5JO2x7zurWeDAdNpHEWcg
            r1.<init>()
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r10.setButton(r1, r0, r2)
            goto L_0x0151
        L_0x010b:
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r0 = r10.stickerSet
            org.telegram.tgnet.TLRPC$StickerSet r6 = r0.set
            boolean r6 = r6.masks
            r7 = 2131624134(0x7f0e00c6, float:1.887544E38)
            java.lang.String r8 = "AddStickersCount"
            if (r6 == 0) goto L_0x012f
            java.lang.Object[] r4 = new java.lang.Object[r5]
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r0 = r0.documents
            int r0 = r0.size()
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r3, r0)
            r4[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r7, r4)
            java.lang.String r0 = r0.toUpperCase()
            goto L_0x0145
        L_0x012f:
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r0 = r0.documents
            int r0 = r0.size()
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r4, r0)
            r3[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r7, r3)
            java.lang.String r0 = r0.toUpperCase()
        L_0x0145:
            org.telegram.ui.Components.-$$Lambda$StickersAlert$TDKayuR9rZfu7Rq3IHs-62RtgkQ r2 = new org.telegram.ui.Components.-$$Lambda$StickersAlert$TDKayuR9rZfu7Rq3IHs-62RtgkQ
            r2.<init>()
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r10.setButton(r2, r0, r1)
        L_0x0151:
            org.telegram.ui.Components.StickersAlert$GridAdapter r0 = r10.adapter
            r0.notifyDataSetChanged()
            goto L_0x0170
        L_0x0157:
            r0 = 2131624737(0x7f0e0321, float:1.8876662E38)
            java.lang.String r2 = "Close"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            java.lang.String r0 = r0.toUpperCase()
            org.telegram.ui.Components.-$$Lambda$StickersAlert$v5kDf2Uds3Mnng7wUMVzZRK7fiE r2 = new org.telegram.ui.Components.-$$Lambda$StickersAlert$v5kDf2Uds3Mnng7wUMVzZRK7fiE
            r2.<init>()
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r10.setButton(r2, r0, r1)
        L_0x0170:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickersAlert.updateFields():void");
    }

    public /* synthetic */ void lambda$updateFields$13$StickersAlert(View view) {
        dismiss();
        StickersAlertInstallDelegate stickersAlertInstallDelegate = this.installDelegate;
        if (stickersAlertInstallDelegate != null) {
            stickersAlertInstallDelegate.onStickerSetInstalled();
        }
        if (!MediaDataController.getInstance(this.currentAccount).cancelRemovingStickerSet(this.inputStickerSet.id)) {
            TLRPC$TL_messages_installStickerSet tLRPC$TL_messages_installStickerSet = new TLRPC$TL_messages_installStickerSet();
            tLRPC$TL_messages_installStickerSet.stickerset = this.inputStickerSet;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_installStickerSet, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    StickersAlert.this.lambda$null$12$StickersAlert(tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$12$StickersAlert(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            private final /* synthetic */ TLRPC$TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                StickersAlert.this.lambda$null$11$StickersAlert(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$11$StickersAlert(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        boolean z = this.stickerSet.set.masks;
        if (tLRPC$TL_error == null) {
            try {
                if (this.showTooltipWhenToggle) {
                    Bulletin.make(this.parentFragment, (Bulletin.Layout) new StickerSetBulletinLayout(this.pickerBottomLayout.getContext(), this.stickerSet, 2), 1500).show();
                }
                if (tLObject instanceof TLRPC$TL_messages_stickerSetInstallResultArchive) {
                    MediaDataController.getInstance(this.currentAccount).processStickerSetInstallResultArchive(this.parentFragment, true, z, (TLRPC$TL_messages_stickerSetInstallResultArchive) tLObject);
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            Toast.makeText(getContext(), LocaleController.getString("ErrorOccurred", NUM), 0).show();
        }
        MediaDataController.getInstance(this.currentAccount).loadStickers(z ? 1 : 0, false, true);
    }

    public /* synthetic */ void lambda$updateFields$14$StickersAlert(View view) {
        StickersAlertInstallDelegate stickersAlertInstallDelegate = this.installDelegate;
        if (stickersAlertInstallDelegate != null) {
            stickersAlertInstallDelegate.onStickerSetUninstalled();
        }
        dismiss();
        MediaDataController.getInstance(this.currentAccount).toggleStickerSet(getContext(), this.stickerSet, 1, this.parentFragment, true, this.showTooltipWhenToggle);
    }

    public /* synthetic */ void lambda$updateFields$15$StickersAlert(View view) {
        StickersAlertInstallDelegate stickersAlertInstallDelegate = this.installDelegate;
        if (stickersAlertInstallDelegate != null) {
            stickersAlertInstallDelegate.onStickerSetUninstalled();
        }
        dismiss();
        MediaDataController.getInstance(this.currentAccount).toggleStickerSet(getContext(), this.stickerSet, 0, this.parentFragment, true, this.showTooltipWhenToggle);
    }

    public /* synthetic */ void lambda$updateFields$16$StickersAlert(View view) {
        dismiss();
    }

    /* access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    public void updateLayout() {
        if (this.gridView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.gridView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            if (this.stickerSetCovereds == null) {
                this.titleTextView.setTranslationY((float) this.scrollOffsetY);
                this.optionsButton.setTranslationY((float) this.scrollOffsetY);
                this.shadow[0].setTranslationY((float) this.scrollOffsetY);
            }
            this.containerView.invalidate();
            return;
        }
        View childAt = this.gridView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(childAt);
        int top = childAt.getTop();
        if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
            runShadowAnimation(0, true);
            top = 0;
        } else {
            runShadowAnimation(0, false);
        }
        if (this.scrollOffsetY != top) {
            RecyclerListView recyclerListView2 = this.gridView;
            this.scrollOffsetY = top;
            recyclerListView2.setTopGlowOffset(top);
            if (this.stickerSetCovereds == null) {
                this.titleTextView.setTranslationY((float) this.scrollOffsetY);
                this.optionsButton.setTranslationY((float) this.scrollOffsetY);
                this.shadow[0].setTranslationY((float) this.scrollOffsetY);
            }
            this.containerView.invalidate();
        }
    }

    private void hidePreview() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.stickerPreviewLayout, View.ALPHA, new float[]{0.0f})});
        animatorSet.setDuration(200);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                StickersAlert.this.stickerPreviewLayout.setVisibility(8);
                StickersAlert.this.stickerImageView.setImageDrawable((Drawable) null);
            }
        });
        animatorSet.start();
    }

    private void runShadowAnimation(final int i, final boolean z) {
        if (this.stickerSetCovereds == null) {
            if ((z && this.shadow[i].getTag() != null) || (!z && this.shadow[i].getTag() == null)) {
                this.shadow[i].setTag(z ? null : 1);
                if (z) {
                    this.shadow[i].setVisibility(0);
                }
                AnimatorSet[] animatorSetArr = this.shadowAnimation;
                if (animatorSetArr[i] != null) {
                    animatorSetArr[i].cancel();
                }
                this.shadowAnimation[i] = new AnimatorSet();
                AnimatorSet animatorSet = this.shadowAnimation[i];
                Animator[] animatorArr = new Animator[1];
                View view = this.shadow[i];
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = z ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
                animatorSet.playTogether(animatorArr);
                this.shadowAnimation[i].setDuration(150);
                this.shadowAnimation[i].addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (StickersAlert.this.shadowAnimation[i] != null && StickersAlert.this.shadowAnimation[i].equals(animator)) {
                            if (!z) {
                                StickersAlert.this.shadow[i].setVisibility(4);
                            }
                            StickersAlert.this.shadowAnimation[i] = null;
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (StickersAlert.this.shadowAnimation[i] != null && StickersAlert.this.shadowAnimation[i].equals(animator)) {
                            StickersAlert.this.shadowAnimation[i] = null;
                        }
                    }
                });
                this.shadowAnimation[i].start();
            }
        }
    }

    public void show() {
        super.show();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 4);
    }

    public void dismiss() {
        super.dismiss();
        if (this.reqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 4);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.emojiDidLoad) {
            RecyclerListView recyclerListView = this.gridView;
            if (recyclerListView != null) {
                int childCount = recyclerListView.getChildCount();
                for (int i3 = 0; i3 < childCount; i3++) {
                    this.gridView.getChildAt(i3).invalidate();
                }
            }
            if (ContentPreviewViewer.getInstance().isVisible()) {
                ContentPreviewViewer.getInstance().close();
            }
            ContentPreviewViewer.getInstance().reset();
        }
    }

    private void setButton(View.OnClickListener onClickListener, String str, int i) {
        this.pickerBottomLayout.setTextColor(i);
        this.pickerBottomLayout.setText(str.toUpperCase());
        this.pickerBottomLayout.setOnClickListener(onClickListener);
    }

    public void setShowTooltipWhenToggle(boolean z) {
        this.showTooltipWhenToggle = z;
    }

    private class GridAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public SparseArray<Object> cache = new SparseArray<>();
        private Context context;
        /* access modifiers changed from: private */
        public SparseArray<TLRPC$StickerSetCovered> positionsToSets = new SparseArray<>();
        /* access modifiers changed from: private */
        public int stickersPerRow;
        /* access modifiers changed from: private */
        public int stickersRowCount;
        /* access modifiers changed from: private */
        public int totalItems;

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        public GridAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            return this.totalItems;
        }

        public int getItemViewType(int i) {
            if (StickersAlert.this.stickerSetCovereds == null) {
                return 0;
            }
            Object obj = this.cache.get(i);
            if (obj == null) {
                return 1;
            }
            if (obj instanceof TLRPC$Document) {
                return 0;
            }
            return 2;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            FeaturedStickerSetInfoCell featuredStickerSetInfoCell;
            if (i == 0) {
                AnonymousClass1 r2 = new StickerEmojiCell(this.context) {
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(View.MeasureSpec.makeMeasureSpec(StickersAlert.this.itemSize, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                    }
                };
                r2.getImageView().setLayerNum(5);
                featuredStickerSetInfoCell = r2;
            } else if (i != 1) {
                featuredStickerSetInfoCell = i != 2 ? null : new FeaturedStickerSetInfoCell(this.context, 8);
            } else {
                featuredStickerSetInfoCell = new EmptyCell(this.context);
            }
            return new RecyclerListView.Holder(featuredStickerSetInfoCell);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (StickersAlert.this.stickerSetCovereds != null) {
                int itemViewType = viewHolder.getItemViewType();
                if (itemViewType == 0) {
                    ((StickerEmojiCell) viewHolder.itemView).setSticker((TLRPC$Document) this.cache.get(i), this.positionsToSets.get(i), false);
                } else if (itemViewType == 1) {
                    ((EmptyCell) viewHolder.itemView).setHeight(AndroidUtilities.dp(82.0f));
                } else if (itemViewType == 2) {
                    ((FeaturedStickerSetInfoCell) viewHolder.itemView).setStickerSet((TLRPC$StickerSetCovered) StickersAlert.this.stickerSetCovereds.get(((Integer) this.cache.get(i)).intValue()), false);
                }
            } else {
                ((StickerEmojiCell) viewHolder.itemView).setSticker(StickersAlert.this.stickerSet.documents.get(i), StickersAlert.this.stickerSet, StickersAlert.this.showEmoji);
            }
        }

        public void notifyDataSetChanged() {
            int i;
            int i2;
            int i3 = 0;
            if (StickersAlert.this.stickerSetCovereds != null) {
                int measuredWidth = StickersAlert.this.gridView.getMeasuredWidth();
                if (measuredWidth == 0) {
                    measuredWidth = AndroidUtilities.displaySize.x;
                }
                this.stickersPerRow = measuredWidth / AndroidUtilities.dp(72.0f);
                StickersAlert.this.layoutManager.setSpanCount(this.stickersPerRow);
                this.cache.clear();
                this.positionsToSets.clear();
                this.totalItems = 0;
                this.stickersRowCount = 0;
                for (int i4 = 0; i4 < StickersAlert.this.stickerSetCovereds.size(); i4++) {
                    TLRPC$StickerSetCovered tLRPC$StickerSetCovered = (TLRPC$StickerSetCovered) StickersAlert.this.stickerSetCovereds.get(i4);
                    if (!tLRPC$StickerSetCovered.covers.isEmpty() || tLRPC$StickerSetCovered.cover != null) {
                        double d = (double) this.stickersRowCount;
                        double ceil = Math.ceil((double) (((float) StickersAlert.this.stickerSetCovereds.size()) / ((float) this.stickersPerRow)));
                        Double.isNaN(d);
                        this.stickersRowCount = (int) (d + ceil);
                        this.positionsToSets.put(this.totalItems, tLRPC$StickerSetCovered);
                        SparseArray<Object> sparseArray = this.cache;
                        int i5 = this.totalItems;
                        this.totalItems = i5 + 1;
                        sparseArray.put(i5, Integer.valueOf(i4));
                        int i6 = this.totalItems / this.stickersPerRow;
                        if (!tLRPC$StickerSetCovered.covers.isEmpty()) {
                            i = (int) Math.ceil((double) (((float) tLRPC$StickerSetCovered.covers.size()) / ((float) this.stickersPerRow)));
                            for (int i7 = 0; i7 < tLRPC$StickerSetCovered.covers.size(); i7++) {
                                this.cache.put(this.totalItems + i7, tLRPC$StickerSetCovered.covers.get(i7));
                            }
                        } else {
                            this.cache.put(this.totalItems, tLRPC$StickerSetCovered.cover);
                            i = 1;
                        }
                        int i8 = 0;
                        while (true) {
                            i2 = this.stickersPerRow;
                            if (i8 >= i * i2) {
                                break;
                            }
                            this.positionsToSets.put(this.totalItems + i8, tLRPC$StickerSetCovered);
                            i8++;
                        }
                        this.totalItems += i * i2;
                    }
                }
            } else {
                if (StickersAlert.this.stickerSet != null) {
                    i3 = StickersAlert.this.stickerSet.documents.size();
                }
                this.totalItems = i3;
            }
            super.notifyDataSetChanged();
        }
    }
}
