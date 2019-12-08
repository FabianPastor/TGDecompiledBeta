package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils.TruncateAt;
import android.text.method.LinkMovementMethod;
import android.util.Property;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.State;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FileRefController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.InputPhoto;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPhoto;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC.TL_inputStickeredMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messages_getAttachedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getStickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_installStickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSetInstallResultArchive;
import org.telegram.tgnet.TLRPC.Vector;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.FeaturedStickerSetInfoCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.ContentPreviewViewer;
import org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate;
import org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate.-CC;

public class StickersAlert extends BottomSheet implements NotificationCenterDelegate {
    private GridAdapter adapter;
    private boolean clearsInputField;
    private StickersAlertDelegate delegate;
    private FrameLayout emptyView;
    private RecyclerListView gridView;
    private boolean ignoreLayout;
    private InputStickerSet inputStickerSet;
    private StickersAlertInstallDelegate installDelegate;
    private int itemSize;
    private GridLayoutManager layoutManager;
    private ActionBarMenuItem optionsButton;
    private Activity parentActivity;
    private BaseFragment parentFragment;
    private TextView pickerBottomLayout;
    private ContentPreviewViewerDelegate previewDelegate;
    private TextView previewSendButton;
    private View previewSendButtonShadow;
    private int reqId;
    private int scrollOffsetY;
    private Document selectedSticker;
    private View[] shadow;
    private AnimatorSet[] shadowAnimation;
    private Drawable shadowDrawable;
    private boolean showEmoji;
    private TextView stickerEmojiTextView;
    private BackupImageView stickerImageView;
    private FrameLayout stickerPreviewLayout;
    private TL_messages_stickerSet stickerSet;
    private ArrayList<StickerSetCovered> stickerSetCovereds;
    private OnItemClickListener stickersOnItemClickListener;
    private TextView titleTextView;
    private Pattern urlPattern;

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        /* synthetic */ LinkMovementMethodMy(AnonymousClass1 anonymousClass1) {
            this();
        }

        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            try {
                boolean onTouchEvent = super.onTouchEvent(textView, spannable, motionEvent);
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    Selection.removeSelection(spannable);
                }
                return onTouchEvent;
            } catch (Exception e) {
                FileLog.e(e);
                return false;
            }
        }
    }

    public interface StickersAlertDelegate {
        void onStickerSelected(Document document, Object obj, boolean z);
    }

    public interface StickersAlertInstallDelegate {
        void onStickerSetInstalled();

        void onStickerSetUninstalled();
    }

    private class GridAdapter extends SelectionAdapter {
        private SparseArray<Object> cache = new SparseArray();
        private Context context;
        private SparseArray<StickerSetCovered> positionsToSets = new SparseArray();
        private int stickersPerRow;
        private int stickersRowCount;
        private int totalItems;

        public boolean isEnabled(ViewHolder viewHolder) {
            return false;
        }

        public GridAdapter(Context context) {
            this.context = context;
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
            if (obj instanceof Document) {
                return 0;
            }
            return 2;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View featuredStickerSetInfoCell;
            if (i != 0) {
                featuredStickerSetInfoCell = i != 1 ? i != 2 ? null : new FeaturedStickerSetInfoCell(this.context, 8) : new EmptyCell(this.context);
            } else {
                featuredStickerSetInfoCell = new StickerEmojiCell(this.context) {
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(MeasureSpec.makeMeasureSpec(StickersAlert.this.itemSize, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                    }
                };
                featuredStickerSetInfoCell.getImageView().setLayerNum(3);
            }
            return new Holder(featuredStickerSetInfoCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (StickersAlert.this.stickerSetCovereds != null) {
                int itemViewType = viewHolder.getItemViewType();
                if (itemViewType == 0) {
                    ((StickerEmojiCell) viewHolder.itemView).setSticker((Document) this.cache.get(i), this.positionsToSets.get(i), false);
                    return;
                } else if (itemViewType == 1) {
                    ((EmptyCell) viewHolder.itemView).setHeight(AndroidUtilities.dp(82.0f));
                    return;
                } else if (itemViewType == 2) {
                    ((FeaturedStickerSetInfoCell) viewHolder.itemView).setStickerSet((StickerSetCovered) StickersAlert.this.stickerSetCovereds.get(((Integer) this.cache.get(i)).intValue()), false);
                    return;
                } else {
                    return;
                }
            }
            ((StickerEmojiCell) viewHolder.itemView).setSticker((Document) StickersAlert.this.stickerSet.documents.get(i), StickersAlert.this.stickerSet, StickersAlert.this.showEmoji);
        }

        public void notifyDataSetChanged() {
            int i = 0;
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
                for (measuredWidth = 0; measuredWidth < StickersAlert.this.stickerSetCovereds.size(); measuredWidth++) {
                    StickerSetCovered stickerSetCovered = (StickerSetCovered) StickersAlert.this.stickerSetCovereds.get(measuredWidth);
                    if (!stickerSetCovered.covers.isEmpty() || stickerSetCovered.cover != null) {
                        int i2;
                        double d = (double) this.stickersRowCount;
                        double ceil = Math.ceil((double) (((float) StickersAlert.this.stickerSetCovereds.size()) / ((float) this.stickersPerRow)));
                        Double.isNaN(d);
                        this.stickersRowCount = (int) (d + ceil);
                        this.positionsToSets.put(this.totalItems, stickerSetCovered);
                        SparseArray sparseArray = this.cache;
                        int i3 = this.totalItems;
                        this.totalItems = i3 + 1;
                        sparseArray.put(i3, Integer.valueOf(measuredWidth));
                        int i4 = this.totalItems / this.stickersPerRow;
                        if (stickerSetCovered.covers.isEmpty()) {
                            this.cache.put(this.totalItems, stickerSetCovered.cover);
                            i4 = 1;
                        } else {
                            i4 = (int) Math.ceil((double) (((float) stickerSetCovered.covers.size()) / ((float) this.stickersPerRow)));
                            for (i3 = 0; i3 < stickerSetCovered.covers.size(); i3++) {
                                this.cache.put(this.totalItems + i3, stickerSetCovered.covers.get(i3));
                            }
                        }
                        i3 = 0;
                        while (true) {
                            i2 = this.stickersPerRow;
                            if (i3 >= i4 * i2) {
                                break;
                            }
                            this.positionsToSets.put(this.totalItems + i3, stickerSetCovered);
                            i3++;
                        }
                        this.totalItems += i4 * i2;
                    }
                }
            } else {
                if (StickersAlert.this.stickerSet != null) {
                    i = StickersAlert.this.stickerSet.documents.size();
                }
                this.totalItems = i;
            }
            super.notifyDataSetChanged();
        }
    }

    /* Access modifiers changed, original: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public StickersAlert(Context context, Object obj, Photo photo) {
        super(context, false, 1);
        this.shadowAnimation = new AnimatorSet[2];
        this.shadow = new View[2];
        this.previewDelegate = new ContentPreviewViewerDelegate() {
            public /* synthetic */ void gifAddedOrDeleted() {
                -CC.$default$gifAddedOrDeleted(this);
            }

            public boolean needOpen() {
                return false;
            }

            public void openSet(InputStickerSet inputStickerSet, boolean z) {
            }

            public /* synthetic */ void sendGif(Object obj) {
                -CC.$default$sendGif(this, obj);
            }

            public void sendSticker(Document document, Object obj) {
                StickersAlert.this.delegate.onStickerSelected(document, obj, StickersAlert.this.clearsInputField);
                StickersAlert.this.dismiss();
            }

            public boolean needSend() {
                return StickersAlert.this.previewSendButton.getVisibility() == 0;
            }
        };
        this.parentActivity = (Activity) context;
        TL_messages_getAttachedStickers tL_messages_getAttachedStickers = new TL_messages_getAttachedStickers();
        TL_inputStickeredMediaPhoto tL_inputStickeredMediaPhoto = new TL_inputStickeredMediaPhoto();
        tL_inputStickeredMediaPhoto.id = new TL_inputPhoto();
        InputPhoto inputPhoto = tL_inputStickeredMediaPhoto.id;
        inputPhoto.id = photo.id;
        inputPhoto.access_hash = photo.access_hash;
        inputPhoto.file_reference = photo.file_reference;
        if (inputPhoto.file_reference == null) {
            inputPhoto.file_reference = new byte[0];
        }
        tL_messages_getAttachedStickers.media = tL_inputStickeredMediaPhoto;
        this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getAttachedStickers, new -$$Lambda$StickersAlert$70AzTfODxLbeSRDlB8gJ7WKPlnE(this, obj, tL_messages_getAttachedStickers, new -$$Lambda$StickersAlert$R4T4Ne-ypM57cFmDbPuLpbHC0Ro(this, tL_messages_getAttachedStickers)));
        init(context);
    }

    public /* synthetic */ void lambda$new$1$StickersAlert(TL_messages_getAttachedStickers tL_messages_getAttachedStickers, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$StickersAlert$NU6Lj7s7oms1kuoHZ-7sXpiftZ4(this, tL_error, tLObject, tL_messages_getAttachedStickers));
    }

    public /* synthetic */ void lambda$null$0$StickersAlert(TL_error tL_error, TLObject tLObject, TL_messages_getAttachedStickers tL_messages_getAttachedStickers) {
        this.reqId = 0;
        if (tL_error == null) {
            Vector vector = (Vector) tLObject;
            if (vector.objects.isEmpty()) {
                dismiss();
                return;
            } else if (vector.objects.size() == 1) {
                StickerSetCovered stickerSetCovered = (StickerSetCovered) vector.objects.get(0);
                this.inputStickerSet = new TL_inputStickerSetID();
                InputStickerSet inputStickerSet = this.inputStickerSet;
                StickerSet stickerSet = stickerSetCovered.set;
                inputStickerSet.id = stickerSet.id;
                inputStickerSet.access_hash = stickerSet.access_hash;
                loadStickerSet();
                return;
            } else {
                this.stickerSetCovereds = new ArrayList();
                for (int i = 0; i < vector.objects.size(); i++) {
                    this.stickerSetCovereds.add((StickerSetCovered) vector.objects.get(i));
                }
                this.gridView.setLayoutParams(LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
                this.titleTextView.setVisibility(8);
                this.shadow[0].setVisibility(8);
                this.adapter.notifyDataSetChanged();
                return;
            }
        }
        AlertsCreator.processError(this.currentAccount, tL_error, this.parentFragment, tL_messages_getAttachedStickers, new Object[0]);
        dismiss();
    }

    public /* synthetic */ void lambda$new$2$StickersAlert(Object obj, TL_messages_getAttachedStickers tL_messages_getAttachedStickers, RequestDelegate requestDelegate, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null || !FileRefController.isFileRefError(tL_error.text) || obj == null) {
            requestDelegate.run(tLObject, tL_error);
            return;
        }
        FileRefController.getInstance(this.currentAccount).requestReference(obj, tL_messages_getAttachedStickers, requestDelegate);
    }

    public StickersAlert(Context context, BaseFragment baseFragment, InputStickerSet inputStickerSet, TL_messages_stickerSet tL_messages_stickerSet, StickersAlertDelegate stickersAlertDelegate) {
        super(context, false, 1);
        this.shadowAnimation = new AnimatorSet[2];
        this.shadow = new View[2];
        this.previewDelegate = /* anonymous class already generated */;
        this.delegate = stickersAlertDelegate;
        this.inputStickerSet = inputStickerSet;
        this.stickerSet = tL_messages_stickerSet;
        this.parentFragment = baseFragment;
        loadStickerSet();
        init(context);
    }

    public void show() {
        super.show();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, Integer.valueOf(2));
    }

    public void setClearsInputField(boolean z) {
        this.clearsInputField = z;
    }

    public boolean isClearsInputField() {
        return this.clearsInputField;
    }

    private void loadStickerSet() {
        InputStickerSet inputStickerSet = this.inputStickerSet;
        if (inputStickerSet != null) {
            if (this.stickerSet == null && inputStickerSet.short_name != null) {
                this.stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSetByName(this.inputStickerSet.short_name);
            }
            if (this.stickerSet == null) {
                this.stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSetById(this.inputStickerSet.id);
            }
            if (this.stickerSet == null) {
                TL_messages_getStickerSet tL_messages_getStickerSet = new TL_messages_getStickerSet();
                tL_messages_getStickerSet.stickerset = this.inputStickerSet;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getStickerSet, new -$$Lambda$StickersAlert$dpylIMqKi3ssdFN3dGRn2NGUFco(this));
            } else if (this.adapter != null) {
                updateSendButton();
                updateFields();
                this.adapter.notifyDataSetChanged();
            }
        }
        TL_messages_stickerSet tL_messages_stickerSet = this.stickerSet;
        if (tL_messages_stickerSet != null) {
            this.showEmoji = tL_messages_stickerSet.set.masks ^ 1;
        }
    }

    public /* synthetic */ void lambda$loadStickerSet$4$StickersAlert(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$StickersAlert$6dF2CqzwWOiq-mtWdyHxV9DIihc(this, tL_error, tLObject));
    }

    public /* synthetic */ void lambda$null$3$StickersAlert(TL_error tL_error, TLObject tLObject) {
        this.reqId = 0;
        if (tL_error == null) {
            this.optionsButton.setVisibility(0);
            this.stickerSet = (TL_messages_stickerSet) tLObject;
            this.showEmoji = this.stickerSet.set.masks ^ 1;
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
        this.shadowDrawable = context.getResources().getDrawable(NUM).mutate();
        String str = "dialogBackground";
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
        this.containerView = new FrameLayout(context2) {
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

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                int dp;
                int dp2;
                i2 = MeasureSpec.getSize(i2);
                boolean z = true;
                if (VERSION.SDK_INT >= 21) {
                    StickersAlert.this.ignoreLayout = true;
                    setPadding(StickersAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, StickersAlert.this.backgroundPaddingLeft, 0);
                    StickersAlert.this.ignoreLayout = false;
                }
                getMeasuredWidth();
                StickersAlert.this.itemSize = (MeasureSpec.getSize(i) - AndroidUtilities.dp(36.0f)) / 5;
                if (StickersAlert.this.stickerSetCovereds != null) {
                    dp = ((AndroidUtilities.dp(56.0f) + (AndroidUtilities.dp(60.0f) * StickersAlert.this.stickerSetCovereds.size())) + (StickersAlert.this.adapter.stickersRowCount * AndroidUtilities.dp(82.0f))) + StickersAlert.this.backgroundPaddingTop;
                    dp2 = AndroidUtilities.dp(24.0f);
                } else {
                    dp = (AndroidUtilities.dp(96.0f) + (Math.max(3, StickersAlert.this.stickerSet != null ? (int) Math.ceil((double) (((float) StickersAlert.this.stickerSet.documents.size()) / 5.0f)) : 0) * AndroidUtilities.dp(82.0f))) + StickersAlert.this.backgroundPaddingTop;
                    dp2 = AndroidUtilities.statusBarHeight;
                }
                dp += dp2;
                double d = (double) dp;
                int i3 = i2 / 5;
                double d2 = (double) i3;
                Double.isNaN(d2);
                dp2 = d < d2 * 3.2d ? 0 : i3 * 2;
                if (dp2 != 0 && dp < i2) {
                    dp2 -= i2 - dp;
                }
                if (dp2 == 0) {
                    dp2 = StickersAlert.this.backgroundPaddingTop;
                }
                if (StickersAlert.this.stickerSetCovereds != null) {
                    dp2 += AndroidUtilities.dp(8.0f);
                }
                if (StickersAlert.this.gridView.getPaddingTop() != dp2) {
                    StickersAlert.this.ignoreLayout = true;
                    StickersAlert.this.gridView.setPadding(AndroidUtilities.dp(10.0f), dp2, AndroidUtilities.dp(10.0f), 0);
                    StickersAlert.this.emptyView.setPadding(0, dp2, 0, 0);
                    StickersAlert.this.ignoreLayout = false;
                }
                if (dp < i2) {
                    z = false;
                }
                this.fullHeight = z;
                super.onMeasure(i, MeasureSpec.makeMeasureSpec(Math.min(dp, i2), NUM));
            }

            /* Access modifiers changed, original: protected */
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

            /* Access modifiers changed, original: protected */
            /* JADX WARNING: Removed duplicated region for block: B:15:0x00b1  */
            /* JADX WARNING: Removed duplicated region for block: B:20:? A:{SYNTHETIC, RETURN} */
            /* JADX WARNING: Removed duplicated region for block: B:18:0x0144  */
            /* JADX WARNING: Removed duplicated region for block: B:15:0x00b1  */
            /* JADX WARNING: Removed duplicated region for block: B:18:0x0144  */
            /* JADX WARNING: Removed duplicated region for block: B:20:? A:{SYNTHETIC, RETURN} */
            public void onDraw(android.graphics.Canvas r14) {
                /*
                r13 = this;
                r0 = org.telegram.ui.Components.StickersAlert.this;
                r0 = r0.scrollOffsetY;
                r1 = org.telegram.ui.Components.StickersAlert.this;
                r1 = r1.backgroundPaddingTop;
                r0 = r0 - r1;
                r1 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
                r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
                r0 = r0 + r1;
                r1 = org.telegram.ui.Components.StickersAlert.this;
                r1 = r1.scrollOffsetY;
                r2 = org.telegram.ui.Components.StickersAlert.this;
                r2 = r2.backgroundPaddingTop;
                r1 = r1 - r2;
                r2 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
                r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
                r1 = r1 - r2;
                r2 = r13.getMeasuredHeight();
                r3 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
                r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                r2 = r2 + r3;
                r3 = org.telegram.ui.Components.StickersAlert.this;
                r3 = r3.backgroundPaddingTop;
                r2 = r2 + r3;
                r3 = android.os.Build.VERSION.SDK_INT;
                r4 = 0;
                r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
                r6 = 21;
                if (r3 < r6) goto L_0x0092;
            L_0x0043:
                r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
                r1 = r1 + r3;
                r0 = r0 + r3;
                r2 = r2 - r3;
                r3 = r13.fullHeight;
                if (r3 == 0) goto L_0x0092;
            L_0x004c:
                r3 = org.telegram.ui.Components.StickersAlert.this;
                r3 = r3.backgroundPaddingTop;
                r3 = r3 + r1;
                r6 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
                r7 = r6 * 2;
                if (r3 >= r7) goto L_0x0077;
            L_0x0059:
                r3 = r6 * 2;
                r3 = r3 - r1;
                r7 = org.telegram.ui.Components.StickersAlert.this;
                r7 = r7.backgroundPaddingTop;
                r3 = r3 - r7;
                r3 = java.lang.Math.min(r6, r3);
                r1 = r1 - r3;
                r2 = r2 + r3;
                r3 = r3 * 2;
                r3 = (float) r3;
                r6 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
                r6 = (float) r6;
                r3 = r3 / r6;
                r3 = java.lang.Math.min(r5, r3);
                r3 = r5 - r3;
                goto L_0x0079;
            L_0x0077:
                r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            L_0x0079:
                r6 = org.telegram.ui.Components.StickersAlert.this;
                r6 = r6.backgroundPaddingTop;
                r6 = r6 + r1;
                r7 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
                if (r6 >= r7) goto L_0x0094;
            L_0x0084:
                r6 = r7 - r1;
                r8 = org.telegram.ui.Components.StickersAlert.this;
                r8 = r8.backgroundPaddingTop;
                r6 = r6 - r8;
                r6 = java.lang.Math.min(r7, r6);
                goto L_0x0095;
            L_0x0092:
                r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            L_0x0094:
                r6 = 0;
            L_0x0095:
                r7 = org.telegram.ui.Components.StickersAlert.this;
                r7 = r7.shadowDrawable;
                r8 = r13.getMeasuredWidth();
                r7.setBounds(r4, r1, r8, r2);
                r2 = org.telegram.ui.Components.StickersAlert.this;
                r2 = r2.shadowDrawable;
                r2.draw(r14);
                r2 = "dialogBackground";
                r4 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
                if (r4 == 0) goto L_0x0100;
            L_0x00b1:
                r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
                r5 = org.telegram.ui.ActionBar.Theme.getColor(r2);
                r4.setColor(r5);
                r4 = r13.rect;
                r5 = org.telegram.ui.Components.StickersAlert.this;
                r5 = r5.backgroundPaddingLeft;
                r5 = (float) r5;
                r7 = org.telegram.ui.Components.StickersAlert.this;
                r7 = r7.backgroundPaddingTop;
                r7 = r7 + r1;
                r7 = (float) r7;
                r8 = r13.getMeasuredWidth();
                r9 = org.telegram.ui.Components.StickersAlert.this;
                r9 = r9.backgroundPaddingLeft;
                r8 = r8 - r9;
                r8 = (float) r8;
                r9 = org.telegram.ui.Components.StickersAlert.this;
                r9 = r9.backgroundPaddingTop;
                r9 = r9 + r1;
                r1 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
                r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
                r9 = r9 + r1;
                r1 = (float) r9;
                r4.set(r5, r7, r8, r1);
                r1 = r13.rect;
                r4 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
                r5 = org.telegram.messenger.AndroidUtilities.dp(r4);
                r5 = (float) r5;
                r5 = r5 * r3;
                r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                r4 = (float) r4;
                r4 = r4 * r3;
                r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
                r14.drawRoundRect(r1, r5, r4, r3);
            L_0x0100:
                r1 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
                r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
                r3 = r13.rect;
                r4 = r13.getMeasuredWidth();
                r4 = r4 - r1;
                r4 = r4 / 2;
                r4 = (float) r4;
                r5 = (float) r0;
                r7 = r13.getMeasuredWidth();
                r7 = r7 + r1;
                r7 = r7 / 2;
                r1 = (float) r7;
                r7 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
                r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
                r0 = r0 + r7;
                r0 = (float) r0;
                r3.set(r4, r5, r1, r0);
                r0 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
                r1 = "key_sheet_scrollUp";
                r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
                r0.setColor(r1);
                r0 = r13.rect;
                r1 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
                r3 = org.telegram.messenger.AndroidUtilities.dp(r1);
                r3 = (float) r3;
                r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
                r1 = (float) r1;
                r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
                r14.drawRoundRect(r0, r3, r1, r4);
                if (r6 <= 0) goto L_0x018e;
            L_0x0144:
                r0 = org.telegram.ui.ActionBar.Theme.getColor(r2);
                r1 = 255; // 0xff float:3.57E-43 double:1.26E-321;
                r2 = android.graphics.Color.red(r0);
                r2 = (float) r2;
                r3 = NUM; // 0x3f4ccccd float:0.8 double:5.246966156E-315;
                r2 = r2 * r3;
                r2 = (int) r2;
                r4 = android.graphics.Color.green(r0);
                r4 = (float) r4;
                r4 = r4 * r3;
                r4 = (int) r4;
                r0 = android.graphics.Color.blue(r0);
                r0 = (float) r0;
                r0 = r0 * r3;
                r0 = (int) r0;
                r0 = android.graphics.Color.argb(r1, r2, r4, r0);
                r1 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
                r1.setColor(r0);
                r0 = org.telegram.ui.Components.StickersAlert.this;
                r0 = r0.backgroundPaddingLeft;
                r8 = (float) r0;
                r0 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
                r0 = r0 - r6;
                r9 = (float) r0;
                r0 = r13.getMeasuredWidth();
                r1 = org.telegram.ui.Components.StickersAlert.this;
                r1 = r1.backgroundPaddingLeft;
                r0 = r0 - r1;
                r10 = (float) r0;
                r0 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
                r11 = (float) r0;
                r12 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
                r7 = r14;
                r7.drawRect(r8, r9, r10, r11, r12);
            L_0x018e:
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickersAlert$AnonymousClass2.onDraw(android.graphics.Canvas):void");
            }
        };
        this.containerView.setWillNotDraw(false);
        ViewGroup viewGroup = this.containerView;
        int i = this.backgroundPaddingLeft;
        viewGroup.setPadding(i, 0, i, 0);
        LayoutParams layoutParams = new LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        layoutParams.topMargin = AndroidUtilities.dp(48.0f);
        this.shadow[0] = new View(context2);
        String str2 = "dialogShadowLine";
        this.shadow[0].setBackgroundColor(Theme.getColor(str2));
        this.shadow[0].setAlpha(0.0f);
        this.shadow[0].setVisibility(4);
        this.shadow[0].setTag(Integer.valueOf(1));
        this.containerView.addView(this.shadow[0], layoutParams);
        this.gridView = new RecyclerListView(context2) {
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
        this.gridView.setTag(Integer.valueOf(14));
        RecyclerListView recyclerListView = this.gridView;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 5);
        this.layoutManager = gridLayoutManager;
        recyclerListView.setLayoutManager(gridLayoutManager);
        this.layoutManager.setSpanSizeLookup(new SpanSizeLookup() {
            public int getSpanSize(int i) {
                return ((StickersAlert.this.stickerSetCovereds == null || !(StickersAlert.this.adapter.cache.get(i) instanceof Integer)) && i != StickersAlert.this.adapter.totalItems) ? 1 : StickersAlert.this.adapter.stickersPerRow;
            }
        });
        recyclerListView = this.gridView;
        GridAdapter gridAdapter = new GridAdapter(context2);
        this.adapter = gridAdapter;
        recyclerListView.setAdapter(gridAdapter);
        this.gridView.setVerticalScrollBarEnabled(false);
        this.gridView.addItemDecoration(new ItemDecoration() {
            public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
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
        this.gridView.setOnTouchListener(new -$$Lambda$StickersAlert$_nwA8t-8QJfVh6HJUgTR9--hiGc(this));
        this.gridView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                StickersAlert.this.updateLayout();
            }
        });
        this.stickersOnItemClickListener = new -$$Lambda$StickersAlert$pqY8OpOZq71Xrxq3rh8h2TYv4Gk(this);
        this.gridView.setOnItemClickListener(this.stickersOnItemClickListener);
        this.containerView.addView(this.gridView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 48.0f, 0.0f, 48.0f));
        this.emptyView = new FrameLayout(context2) {
            public void requestLayout() {
                if (!StickersAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.containerView.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.gridView.setEmptyView(this.emptyView);
        this.emptyView.setOnTouchListener(-$$Lambda$StickersAlert$x4wnZ8iSeVTFndyEhtM4m12j-W8.INSTANCE);
        this.titleTextView = new TextView(context2);
        this.titleTextView.setLines(1);
        this.titleTextView.setSingleLine(true);
        this.titleTextView.setTextColor(Theme.getColor("dialogTextBlack"));
        this.titleTextView.setTextSize(1, 20.0f);
        this.titleTextView.setLinkTextColor(Theme.getColor("dialogTextLink"));
        this.titleTextView.setHighlightColor(Theme.getColor("dialogLinkSelection"));
        this.titleTextView.setEllipsize(TruncateAt.END);
        this.titleTextView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.titleTextView.setGravity(16);
        String str3 = "fonts/rmedium.ttf";
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface(str3));
        this.containerView.addView(this.titleTextView, LayoutHelper.createFrame(-1, 50.0f, 51, 0.0f, 0.0f, 40.0f, 0.0f));
        this.optionsButton = new ActionBarMenuItem(context2, null, 0, Theme.getColor("key_sheet_other"));
        this.optionsButton.setLongClickEnabled(false);
        this.optionsButton.setSubMenuOpenSide(2);
        this.optionsButton.setIcon(NUM);
        this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("player_actionBarSelector"), 1));
        this.containerView.addView(this.optionsButton, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, 5.0f, 5.0f, 0.0f));
        this.optionsButton.addSubItem(1, NUM, LocaleController.getString("StickersShare", NUM));
        this.optionsButton.addSubItem(2, NUM, LocaleController.getString("CopyLink", NUM));
        this.optionsButton.setOnClickListener(new -$$Lambda$StickersAlert$7978RXQKfGxYvNzfCRh5mokiXDU(this));
        this.optionsButton.setDelegate(new -$$Lambda$StickersAlert$9rn6i7qVo7Tr6wYcNUHGgSRyKIM(this));
        this.optionsButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        this.optionsButton.setVisibility(this.inputStickerSet != null ? 0 : 8);
        this.emptyView.addView(new RadialProgressView(context2), LayoutHelper.createFrame(-2, -2, 17));
        layoutParams = new LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83);
        layoutParams.bottomMargin = AndroidUtilities.dp(48.0f);
        this.shadow[1] = new View(context2);
        this.shadow[1].setBackgroundColor(Theme.getColor(str2));
        this.containerView.addView(this.shadow[1], layoutParams);
        this.pickerBottomLayout = new TextView(context2);
        this.pickerBottomLayout.setBackgroundDrawable(Theme.createSelectorWithBackgroundDrawable(Theme.getColor(str), Theme.getColor("listSelectorSDK21")));
        String str4 = "dialogTextBlue2";
        this.pickerBottomLayout.setTextColor(Theme.getColor(str4));
        this.pickerBottomLayout.setTextSize(1, 14.0f);
        this.pickerBottomLayout.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.pickerBottomLayout.setTypeface(AndroidUtilities.getTypeface(str3));
        this.pickerBottomLayout.setGravity(17);
        this.containerView.addView(this.pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 83));
        this.stickerPreviewLayout = new FrameLayout(context2);
        this.stickerPreviewLayout.setBackgroundColor(Theme.getColor(str) & -NUM);
        this.stickerPreviewLayout.setVisibility(8);
        this.stickerPreviewLayout.setSoundEffectsEnabled(false);
        this.containerView.addView(this.stickerPreviewLayout, LayoutHelper.createFrame(-1, -1.0f));
        this.stickerPreviewLayout.setOnClickListener(new -$$Lambda$StickersAlert$nsigcALZ-A8eXyilWi1cmTZCn2o(this));
        this.stickerImageView = new BackupImageView(context2);
        this.stickerImageView.setAspectFit(true);
        this.stickerImageView.setLayerNum(3);
        this.stickerPreviewLayout.addView(this.stickerImageView);
        this.stickerEmojiTextView = new TextView(context2);
        this.stickerEmojiTextView.setTextSize(1, 30.0f);
        this.stickerEmojiTextView.setGravity(85);
        this.stickerPreviewLayout.addView(this.stickerEmojiTextView);
        this.previewSendButton = new TextView(context2);
        this.previewSendButton.setTextSize(1, 14.0f);
        this.previewSendButton.setTextColor(Theme.getColor(str4));
        this.previewSendButton.setGravity(17);
        this.previewSendButton.setBackgroundColor(Theme.getColor(str));
        this.previewSendButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
        this.previewSendButton.setTypeface(AndroidUtilities.getTypeface(str3));
        this.stickerPreviewLayout.addView(this.previewSendButton, LayoutHelper.createFrame(-1, 48, 83));
        this.previewSendButton.setOnClickListener(new -$$Lambda$StickersAlert$BjajSs_-vXEFUr5UZfKatrtPlkU(this));
        layoutParams = new LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83);
        layoutParams.bottomMargin = AndroidUtilities.dp(48.0f);
        this.previewSendButtonShadow = new View(context2);
        this.previewSendButtonShadow.setBackgroundColor(Theme.getColor(str2));
        this.stickerPreviewLayout.addView(this.previewSendButtonShadow, layoutParams);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        updateFields();
        updateSendButton();
        this.adapter.notifyDataSetChanged();
    }

    public /* synthetic */ boolean lambda$init$5$StickersAlert(View view, MotionEvent motionEvent) {
        return ContentPreviewViewer.getInstance().onTouch(motionEvent, this.gridView, 0, this.stickersOnItemClickListener, this.previewDelegate);
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0097  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0097  */
    public /* synthetic */ void lambda$init$6$StickersAlert(android.view.View r12, int r13) {
        /*
        r11 = this;
        r12 = r11.stickerSetCovereds;
        if (r12 == 0) goto L_0x0035;
    L_0x0004:
        r12 = r11.adapter;
        r12 = r12.positionsToSets;
        r12 = r12.get(r13);
        r12 = (org.telegram.tgnet.TLRPC.StickerSetCovered) r12;
        if (r12 == 0) goto L_0x011a;
    L_0x0012:
        r11.dismiss();
        r3 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
        r3.<init>();
        r12 = r12.set;
        r0 = r12.access_hash;
        r3.access_hash = r0;
        r12 = r12.id;
        r3.id = r12;
        r12 = new org.telegram.ui.Components.StickersAlert;
        r1 = r11.parentActivity;
        r2 = r11.parentFragment;
        r4 = 0;
        r5 = 0;
        r0 = r12;
        r0.<init>(r1, r2, r3, r4, r5);
        r12.show();
        goto L_0x011a;
    L_0x0035:
        r12 = r11.stickerSet;
        if (r12 == 0) goto L_0x011a;
    L_0x0039:
        if (r13 < 0) goto L_0x011a;
    L_0x003b:
        r12 = r12.documents;
        r12 = r12.size();
        if (r13 < r12) goto L_0x0045;
    L_0x0043:
        goto L_0x011a;
    L_0x0045:
        r12 = r11.stickerSet;
        r12 = r12.documents;
        r12 = r12.get(r13);
        r12 = (org.telegram.tgnet.TLRPC.Document) r12;
        r11.selectedSticker = r12;
        r12 = 0;
        r13 = 0;
    L_0x0053:
        r0 = r11.selectedSticker;
        r0 = r0.attributes;
        r0 = r0.size();
        r1 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
        r2 = 1;
        if (r13 >= r0) goto L_0x0094;
    L_0x0060:
        r0 = r11.selectedSticker;
        r0 = r0.attributes;
        r0 = r0.get(r13);
        r0 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r0;
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
        if (r3 == 0) goto L_0x0091;
    L_0x006e:
        r13 = r0.alt;
        if (r13 == 0) goto L_0x0094;
    L_0x0072:
        r13 = r13.length();
        if (r13 <= 0) goto L_0x0094;
    L_0x0078:
        r13 = r11.stickerEmojiTextView;
        r0 = r0.alt;
        r3 = r13.getPaint();
        r3 = r3.getFontMetricsInt();
        r4 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r0 = org.telegram.messenger.Emoji.replaceEmoji(r0, r3, r4, r12);
        r13.setText(r0);
        r13 = 1;
        goto L_0x0095;
    L_0x0091:
        r13 = r13 + 1;
        goto L_0x0053;
    L_0x0094:
        r13 = 0;
    L_0x0095:
        if (r13 != 0) goto L_0x00bc;
    L_0x0097:
        r13 = r11.stickerEmojiTextView;
        r0 = r11.currentAccount;
        r0 = org.telegram.messenger.MediaDataController.getInstance(r0);
        r3 = r11.selectedSticker;
        r3 = r3.id;
        r0 = r0.getEmojiForSticker(r3);
        r3 = r11.stickerEmojiTextView;
        r3 = r3.getPaint();
        r3 = r3.getFontMetricsInt();
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r0 = org.telegram.messenger.Emoji.replaceEmoji(r0, r3, r1, r12);
        r13.setText(r0);
    L_0x00bc:
        r13 = r11.selectedSticker;
        r13 = r13.thumbs;
        r0 = 90;
        r13 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r13, r0);
        r0 = r11.stickerImageView;
        r3 = r0.getImageReceiver();
        r0 = r11.selectedSticker;
        r4 = org.telegram.messenger.ImageLocation.getForDocument(r0);
        r5 = 0;
        r0 = r11.selectedSticker;
        r6 = org.telegram.messenger.ImageLocation.getForDocument(r13, r0);
        r7 = 0;
        r9 = r11.stickerSet;
        r10 = 1;
        r8 = "webp";
        r3.setImage(r4, r5, r6, r7, r8, r9, r10);
        r13 = r11.stickerPreviewLayout;
        r13 = r13.getLayoutParams();
        r13 = (android.widget.FrameLayout.LayoutParams) r13;
        r0 = r11.scrollOffsetY;
        r13.topMargin = r0;
        r0 = r11.stickerPreviewLayout;
        r0.setLayoutParams(r13);
        r13 = r11.stickerPreviewLayout;
        r13.setVisibility(r12);
        r13 = new android.animation.AnimatorSet;
        r13.<init>();
        r0 = new android.animation.Animator[r2];
        r1 = r11.stickerPreviewLayout;
        r2 = android.view.View.ALPHA;
        r3 = 2;
        r3 = new float[r3];
        r3 = {0, NUM};
        r1 = android.animation.ObjectAnimator.ofFloat(r1, r2, r3);
        r0[r12] = r1;
        r13.playTogether(r0);
        r0 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r13.setDuration(r0);
        r13.start();
    L_0x011a:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickersAlert.lambda$init$6$StickersAlert(android.view.View, int):void");
    }

    public /* synthetic */ void lambda$init$8$StickersAlert(View view) {
        this.optionsButton.toggleSubMenu();
    }

    public /* synthetic */ void lambda$init$9$StickersAlert(View view) {
        hidePreview();
    }

    public /* synthetic */ void lambda$init$10$StickersAlert(View view) {
        this.delegate.onStickerSelected(this.selectedSticker, this.stickerSet, this.clearsInputField);
        dismiss();
    }

    private void updateSendButton() {
        Point point = AndroidUtilities.displaySize;
        int min = (int) (((float) (Math.min(point.x, point.y) / 2)) / AndroidUtilities.density);
        if (this.delegate != null) {
            TL_messages_stickerSet tL_messages_stickerSet = this.stickerSet;
            if (tL_messages_stickerSet == null || !tL_messages_stickerSet.set.masks) {
                this.previewSendButton.setText(LocaleController.getString("SendSticker", NUM).toUpperCase());
                float f = (float) min;
                this.stickerImageView.setLayoutParams(LayoutHelper.createFrame(min, f, 17, 0.0f, 0.0f, 0.0f, 30.0f));
                this.stickerEmojiTextView.setLayoutParams(LayoutHelper.createFrame(min, f, 17, 0.0f, 0.0f, 0.0f, 30.0f));
                this.previewSendButton.setVisibility(0);
                this.previewSendButtonShadow.setVisibility(0);
                return;
            }
        }
        this.previewSendButton.setText(LocaleController.getString("Close", NUM).toUpperCase());
        this.stickerImageView.setLayoutParams(LayoutHelper.createFrame(min, min, 17));
        this.stickerEmojiTextView.setLayoutParams(LayoutHelper.createFrame(min, min, 17));
        this.previewSendButton.setVisibility(8);
        this.previewSendButtonShadow.setVisibility(8);
    }

    public void setInstallDelegate(StickersAlertInstallDelegate stickersAlertInstallDelegate) {
        this.installDelegate = stickersAlertInstallDelegate;
    }

    private void onSubItemClick(int i) {
        if (this.stickerSet != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("https://");
            stringBuilder.append(MessagesController.getInstance(this.currentAccount).linkPrefix);
            stringBuilder.append("/addstickers/");
            stringBuilder.append(this.stickerSet.set.short_name);
            String stringBuilder2 = stringBuilder.toString();
            if (i == 1) {
                ShareAlert shareAlert = new ShareAlert(getContext(), null, stringBuilder2, false, stringBuilder2, false);
                BaseFragment baseFragment = this.parentFragment;
                if (baseFragment != null) {
                    baseFragment.showDialog(shareAlert);
                } else {
                    shareAlert.show();
                }
            } else if (i == 2) {
                try {
                    AndroidUtilities.addToClipboard(stringBuilder2);
                    Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("LinkCopied", NUM), 0).show();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:34:0x0085  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x012e  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0085  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0099  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x012e  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0085  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0099  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x012e  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0117  */
    private void updateFields() {
        /*
        r10 = this;
        r0 = r10.titleTextView;
        if (r0 != 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r0 = r10.stickerSet;
        r1 = "dialogTextBlue2";
        if (r0 == 0) goto L_0x0156;
    L_0x000b:
        r0 = 0;
        r2 = 0;
        r3 = r10.urlPattern;	 Catch:{ Exception -> 0x007b }
        if (r3 != 0) goto L_0x0019;
    L_0x0011:
        r3 = "@[a-zA-Z\\d_]{1,32}";
        r3 = java.util.regex.Pattern.compile(r3);	 Catch:{ Exception -> 0x007b }
        r10.urlPattern = r3;	 Catch:{ Exception -> 0x007b }
    L_0x0019:
        r3 = r10.urlPattern;	 Catch:{ Exception -> 0x007b }
        r4 = r10.stickerSet;	 Catch:{ Exception -> 0x007b }
        r4 = r4.set;	 Catch:{ Exception -> 0x007b }
        r4 = r4.title;	 Catch:{ Exception -> 0x007b }
        r3 = r3.matcher(r4);	 Catch:{ Exception -> 0x007b }
        r4 = r0;
    L_0x0026:
        r5 = r3.find();	 Catch:{ Exception -> 0x0079 }
        if (r5 == 0) goto L_0x0080;
    L_0x002c:
        if (r4 != 0) goto L_0x0048;
    L_0x002e:
        r5 = new android.text.SpannableStringBuilder;	 Catch:{ Exception -> 0x0079 }
        r6 = r10.stickerSet;	 Catch:{ Exception -> 0x0079 }
        r6 = r6.set;	 Catch:{ Exception -> 0x0079 }
        r6 = r6.title;	 Catch:{ Exception -> 0x0079 }
        r5.<init>(r6);	 Catch:{ Exception -> 0x0079 }
        r4 = r10.titleTextView;	 Catch:{ Exception -> 0x0045 }
        r6 = new org.telegram.ui.Components.StickersAlert$LinkMovementMethodMy;	 Catch:{ Exception -> 0x0045 }
        r6.<init>(r0);	 Catch:{ Exception -> 0x0045 }
        r4.setMovementMethod(r6);	 Catch:{ Exception -> 0x0045 }
        r4 = r5;
        goto L_0x0048;
    L_0x0045:
        r3 = move-exception;
        r4 = r5;
        goto L_0x007d;
    L_0x0048:
        r5 = r3.start();	 Catch:{ Exception -> 0x0079 }
        r6 = r3.end();	 Catch:{ Exception -> 0x0079 }
        r7 = r10.stickerSet;	 Catch:{ Exception -> 0x0079 }
        r7 = r7.set;	 Catch:{ Exception -> 0x0079 }
        r7 = r7.title;	 Catch:{ Exception -> 0x0079 }
        r7 = r7.charAt(r5);	 Catch:{ Exception -> 0x0079 }
        r8 = 64;
        if (r7 == r8) goto L_0x0060;
    L_0x005e:
        r5 = r5 + 1;
    L_0x0060:
        r7 = new org.telegram.ui.Components.StickersAlert$8;	 Catch:{ Exception -> 0x0079 }
        r8 = r10.stickerSet;	 Catch:{ Exception -> 0x0079 }
        r8 = r8.set;	 Catch:{ Exception -> 0x0079 }
        r8 = r8.title;	 Catch:{ Exception -> 0x0079 }
        r9 = r5 + 1;
        r8 = r8.subSequence(r9, r6);	 Catch:{ Exception -> 0x0079 }
        r8 = r8.toString();	 Catch:{ Exception -> 0x0079 }
        r7.<init>(r8);	 Catch:{ Exception -> 0x0079 }
        r4.setSpan(r7, r5, r6, r2);	 Catch:{ Exception -> 0x0079 }
        goto L_0x0026;
    L_0x0079:
        r3 = move-exception;
        goto L_0x007d;
    L_0x007b:
        r3 = move-exception;
        r4 = r0;
    L_0x007d:
        org.telegram.messenger.FileLog.e(r3);
    L_0x0080:
        r0 = r10.titleTextView;
        if (r4 == 0) goto L_0x0085;
    L_0x0084:
        goto L_0x008b;
    L_0x0085:
        r3 = r10.stickerSet;
        r3 = r3.set;
        r4 = r3.title;
    L_0x008b:
        r0.setText(r4);
        r0 = r10.stickerSet;
        r0 = r0.set;
        r3 = "MasksCount";
        r4 = "Stickers";
        r5 = 1;
        if (r0 == 0) goto L_0x010a;
    L_0x0099:
        r0 = r10.currentAccount;
        r0 = org.telegram.messenger.MediaDataController.getInstance(r0);
        r6 = r10.stickerSet;
        r6 = r6.set;
        r6 = r6.id;
        r0 = r0.isStickerPackInstalled(r6);
        if (r0 != 0) goto L_0x00ac;
    L_0x00ab:
        goto L_0x010a;
    L_0x00ac:
        r0 = r10.stickerSet;
        r1 = r0.set;
        r1 = r1.masks;
        r6 = NUM; // 0x7f0d08af float:1.8746624E38 double:1.053130876E-314;
        r7 = "RemoveStickersCount";
        if (r1 == 0) goto L_0x00d0;
    L_0x00b9:
        r1 = new java.lang.Object[r5];
        r0 = r0.documents;
        r0 = r0.size();
        r0 = org.telegram.messenger.LocaleController.formatPluralString(r3, r0);
        r1[r2] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r7, r6, r1);
        r0 = r0.toUpperCase();
        goto L_0x00e6;
    L_0x00d0:
        r1 = new java.lang.Object[r5];
        r0 = r0.documents;
        r0 = r0.size();
        r0 = org.telegram.messenger.LocaleController.formatPluralString(r4, r0);
        r1[r2] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r7, r6, r1);
        r0 = r0.toUpperCase();
    L_0x00e6:
        r1 = r10.stickerSet;
        r1 = r1.set;
        r1 = r1.official;
        r2 = "dialogTextRed";
        if (r1 == 0) goto L_0x00fd;
    L_0x00f0:
        r1 = new org.telegram.ui.Components.-$$Lambda$StickersAlert$DGFO8T79ri5mrUocraZaq3nn2Bk;
        r1.<init>(r10);
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r10.setButton(r1, r0, r2);
        goto L_0x0150;
    L_0x00fd:
        r1 = new org.telegram.ui.Components.-$$Lambda$StickersAlert$2Q0RkK5JO2x7zurWeDAdNpHEWcg;
        r1.<init>(r10);
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r10.setButton(r1, r0, r2);
        goto L_0x0150;
    L_0x010a:
        r0 = r10.stickerSet;
        r6 = r0.set;
        r6 = r6.masks;
        r7 = NUM; // 0x7f0d00bc float:1.8742496E38 double:1.0531298704E-314;
        r8 = "AddStickersCount";
        if (r6 == 0) goto L_0x012e;
    L_0x0117:
        r4 = new java.lang.Object[r5];
        r0 = r0.documents;
        r0 = r0.size();
        r0 = org.telegram.messenger.LocaleController.formatPluralString(r3, r0);
        r4[r2] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r8, r7, r4);
        r0 = r0.toUpperCase();
        goto L_0x0144;
    L_0x012e:
        r3 = new java.lang.Object[r5];
        r0 = r0.documents;
        r0 = r0.size();
        r0 = org.telegram.messenger.LocaleController.formatPluralString(r4, r0);
        r3[r2] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r8, r7, r3);
        r0 = r0.toUpperCase();
    L_0x0144:
        r2 = new org.telegram.ui.Components.-$$Lambda$StickersAlert$TDKayuR9rZfu7Rq3IHs-62RtgkQ;
        r2.<init>(r10);
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r10.setButton(r2, r0, r1);
    L_0x0150:
        r0 = r10.adapter;
        r0.notifyDataSetChanged();
        goto L_0x016f;
    L_0x0156:
        r0 = NUM; // 0x7f0d02df float:1.8743605E38 double:1.0531301407E-314;
        r2 = "Close";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        r0 = r0.toUpperCase();
        r2 = new org.telegram.ui.Components.-$$Lambda$StickersAlert$v5kDf2Uds3Mnng7wUMVzZRK7fiE;
        r2.<init>(r10);
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r10.setButton(r2, r0, r1);
    L_0x016f:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickersAlert.updateFields():void");
    }

    public /* synthetic */ void lambda$updateFields$13$StickersAlert(View view) {
        dismiss();
        StickersAlertInstallDelegate stickersAlertInstallDelegate = this.installDelegate;
        if (stickersAlertInstallDelegate != null) {
            stickersAlertInstallDelegate.onStickerSetInstalled();
        }
        TL_messages_installStickerSet tL_messages_installStickerSet = new TL_messages_installStickerSet();
        tL_messages_installStickerSet.stickerset = this.inputStickerSet;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_installStickerSet, new -$$Lambda$StickersAlert$93kNqcLfjawvVuCFuwipI3J4db8(this));
    }

    public /* synthetic */ void lambda$null$12$StickersAlert(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$StickersAlert$u0aZ0wOaqO_c5AszLHG59q2qf_I(this, tL_error, tLObject));
    }

    public /* synthetic */ void lambda$null$11$StickersAlert(TL_error tL_error, TLObject tLObject) {
        if (tL_error == null) {
            try {
                if (this.stickerSet.set.masks) {
                    Toast.makeText(getContext(), LocaleController.getString("AddMasksInstalled", NUM), 0).show();
                } else {
                    Toast.makeText(getContext(), LocaleController.getString("AddStickersInstalled", NUM), 0).show();
                }
                if (tLObject instanceof TL_messages_stickerSetInstallResultArchive) {
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needReloadArchivedStickers, new Object[0]);
                    if (!(this.parentFragment == null || this.parentFragment.getParentActivity() == null)) {
                        this.parentFragment.showDialog(new StickersArchiveAlert(this.parentFragment.getParentActivity(), this.parentFragment, ((TL_messages_stickerSetInstallResultArchive) tLObject).sets).create());
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else {
            Toast.makeText(getContext(), LocaleController.getString("ErrorOccurred", NUM), 0).show();
        }
        MediaDataController.getInstance(this.currentAccount).loadStickers(this.stickerSet.set.masks, false, true);
    }

    public /* synthetic */ void lambda$updateFields$14$StickersAlert(View view) {
        StickersAlertInstallDelegate stickersAlertInstallDelegate = this.installDelegate;
        if (stickersAlertInstallDelegate != null) {
            stickersAlertInstallDelegate.onStickerSetUninstalled();
        }
        dismiss();
        MediaDataController.getInstance(this.currentAccount).removeStickersSet(getContext(), this.stickerSet.set, 1, this.parentFragment, true);
    }

    public /* synthetic */ void lambda$updateFields$15$StickersAlert(View view) {
        StickersAlertInstallDelegate stickersAlertInstallDelegate = this.installDelegate;
        if (stickersAlertInstallDelegate != null) {
            stickersAlertInstallDelegate.onStickerSetUninstalled();
        }
        dismiss();
        MediaDataController.getInstance(this.currentAccount).removeStickersSet(getContext(), this.stickerSet.set, 0, this.parentFragment, true);
    }

    public /* synthetic */ void lambda$updateFields$16$StickersAlert(View view) {
        dismiss();
    }

    @SuppressLint({"NewApi"})
    private void updateLayout() {
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
        Holder holder = (Holder) this.gridView.findContainingViewHolder(childAt);
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
        Animator[] animatorArr = new Animator[1];
        animatorArr[0] = ObjectAnimator.ofFloat(this.stickerPreviewLayout, View.ALPHA, new float[]{0.0f});
        animatorSet.playTogether(animatorArr);
        animatorSet.setDuration(200);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                StickersAlert.this.stickerPreviewLayout.setVisibility(8);
            }
        });
        animatorSet.start();
    }

    private void runShadowAnimation(final int i, final boolean z) {
        if (this.stickerSetCovereds == null) {
            if ((z && this.shadow[i].getTag() != null) || (!z && this.shadow[i].getTag() == null)) {
                this.shadow[i].setTag(z ? null : Integer.valueOf(1));
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
                Object obj = this.shadow[i];
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = z ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(obj, property, fArr);
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

    public void dismiss() {
        super.dismiss();
        if (this.reqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, Integer.valueOf(2));
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.emojiDidLoad) {
            RecyclerListView recyclerListView = this.gridView;
            if (recyclerListView != null) {
                i = recyclerListView.getChildCount();
                for (i2 = 0; i2 < i; i2++) {
                    this.gridView.getChildAt(i2).invalidate();
                }
            }
            if (ContentPreviewViewer.getInstance().isVisible()) {
                ContentPreviewViewer.getInstance().close();
            }
            ContentPreviewViewer.getInstance().reset();
        }
    }

    private void setButton(OnClickListener onClickListener, String str, int i) {
        this.pickerBottomLayout.setTextColor(i);
        this.pickerBottomLayout.setText(str.toUpperCase());
        this.pickerBottomLayout.setOnClickListener(onClickListener);
    }
}
