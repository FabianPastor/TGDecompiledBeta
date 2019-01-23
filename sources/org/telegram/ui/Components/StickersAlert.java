package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils.TruncateAt;
import android.text.method.LinkMovementMethod;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FileRefController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.GridLayoutManager;
import org.telegram.messenger.support.widget.GridLayoutManager.SpanSizeLookup;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.Photo;
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
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.FeaturedStickerSetInfoCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.StickerPreviewViewer;
import org.telegram.ui.StickerPreviewViewer.StickerPreviewViewerDelegate;

public class StickersAlert extends BottomSheet implements NotificationCenterDelegate {
    private GridAdapter adapter;
    private StickersAlertDelegate delegate;
    private FrameLayout emptyView;
    private RecyclerListView gridView;
    private boolean ignoreLayout;
    private InputStickerSet inputStickerSet;
    private StickersAlertInstallDelegate installDelegate;
    private int itemSize;
    private GridLayoutManager layoutManager;
    private Activity parentActivity;
    private BaseFragment parentFragment;
    private PickerBottomLayout pickerBottomLayout;
    private StickerPreviewViewerDelegate previewDelegate;
    private ImageView previewFavButton;
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

    public interface StickersAlertInstallDelegate {
        void onStickerSetInstalled();

        void onStickerSetUninstalled();
    }

    public interface StickersAlertDelegate {
        void onStickerSelected(Document document, Object obj);
    }

    private class GridAdapter extends SelectionAdapter {
        private SparseArray<Object> cache = new SparseArray();
        private Context context;
        private SparseArray<StickerSetCovered> positionsToSets = new SparseArray();
        private int stickersPerRow;
        private int stickersRowCount;
        private int totalItems;

        public GridAdapter(Context context) {
            this.context = context;
        }

        public int getItemCount() {
            return this.totalItems;
        }

        public int getItemViewType(int position) {
            if (StickersAlert.this.stickerSetCovereds == null) {
                return 0;
            }
            Object object = this.cache.get(position);
            if (object == null) {
                return 1;
            }
            if (object instanceof Document) {
                return 0;
            }
            return 2;
        }

        public boolean isEnabled(ViewHolder holder) {
            return false;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new StickerEmojiCell(this.context) {
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(MeasureSpec.makeMeasureSpec(StickersAlert.this.itemSize, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                        }
                    };
                    break;
                case 1:
                    view = new EmptyCell(this.context);
                    break;
                case 2:
                    view = new FeaturedStickerSetInfoCell(this.context, 8);
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            if (StickersAlert.this.stickerSetCovereds != null) {
                switch (holder.getItemViewType()) {
                    case 0:
                        ((StickerEmojiCell) holder.itemView).setSticker((Document) this.cache.get(position), this.positionsToSets.get(position), false);
                        return;
                    case 1:
                        ((EmptyCell) holder.itemView).setHeight(AndroidUtilities.dp(82.0f));
                        return;
                    case 2:
                        holder.itemView.setStickerSet((StickerSetCovered) StickersAlert.this.stickerSetCovereds.get(((Integer) this.cache.get(position)).intValue()), false);
                        return;
                    default:
                        return;
                }
            }
            ((StickerEmojiCell) holder.itemView).setSticker((Document) StickersAlert.this.stickerSet.documents.get(position), StickersAlert.this.stickerSet, StickersAlert.this.showEmoji);
        }

        public void notifyDataSetChanged() {
            int i = 0;
            if (StickersAlert.this.stickerSetCovereds != null) {
                int width = StickersAlert.this.gridView.getMeasuredWidth();
                if (width == 0) {
                    width = AndroidUtilities.displaySize.x;
                }
                this.stickersPerRow = width / AndroidUtilities.dp(72.0f);
                StickersAlert.this.layoutManager.setSpanCount(this.stickersPerRow);
                this.cache.clear();
                this.positionsToSets.clear();
                this.totalItems = 0;
                this.stickersRowCount = 0;
                for (int a = 0; a < StickersAlert.this.stickerSetCovereds.size(); a++) {
                    StickerSetCovered pack = (StickerSetCovered) StickersAlert.this.stickerSetCovereds.get(a);
                    if (!pack.covers.isEmpty() || pack.cover != null) {
                        int count;
                        int b;
                        this.stickersRowCount = (int) (((double) this.stickersRowCount) + Math.ceil((double) (((float) StickersAlert.this.stickerSetCovereds.size()) / ((float) this.stickersPerRow))));
                        this.positionsToSets.put(this.totalItems, pack);
                        SparseArray sparseArray = this.cache;
                        int i2 = this.totalItems;
                        this.totalItems = i2 + 1;
                        sparseArray.put(i2, Integer.valueOf(a));
                        int startRow = this.totalItems / this.stickersPerRow;
                        if (pack.covers.isEmpty()) {
                            count = 1;
                            this.cache.put(this.totalItems, pack.cover);
                        } else {
                            count = (int) Math.ceil((double) (((float) pack.covers.size()) / ((float) this.stickersPerRow)));
                            for (b = 0; b < pack.covers.size(); b++) {
                                this.cache.put(this.totalItems + b, pack.covers.get(b));
                            }
                        }
                        for (b = 0; b < this.stickersPerRow * count; b++) {
                            this.positionsToSets.put(this.totalItems + b, pack);
                        }
                        this.totalItems += this.stickersPerRow * count;
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

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        /* synthetic */ LinkMovementMethodMy(AnonymousClass1 x0) {
            this();
        }

        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                boolean result = super.onTouchEvent(widget, buffer, event);
                if (event.getAction() != 1 && event.getAction() != 3) {
                    return result;
                }
                Selection.removeSelection(buffer);
                return result;
            } catch (Throwable e) {
                FileLog.e(e);
                return false;
            }
        }
    }

    public StickersAlert(Context context, Object parentObject, Photo photo) {
        super(context, false);
        this.shadowAnimation = new AnimatorSet[2];
        this.shadow = new View[2];
        this.previewDelegate = new StickerPreviewViewerDelegate() {
            public void sendSticker(Document sticker, Object parent) {
                StickersAlert.this.delegate.onStickerSelected(sticker, parent);
                StickersAlert.this.lambda$init$8$StickersAlert();
            }

            public void openSet(InputStickerSet set) {
            }

            public boolean needSend() {
                return StickersAlert.this.previewSendButton.getVisibility() == 0;
            }

            public boolean needOpen() {
                return false;
            }
        };
        this.parentActivity = (Activity) context;
        TL_messages_getAttachedStickers req = new TL_messages_getAttachedStickers();
        TL_inputStickeredMediaPhoto inputStickeredMediaPhoto = new TL_inputStickeredMediaPhoto();
        inputStickeredMediaPhoto.id = new TL_inputPhoto();
        inputStickeredMediaPhoto.id.id = photo.id;
        inputStickeredMediaPhoto.id.access_hash = photo.access_hash;
        inputStickeredMediaPhoto.id.file_reference = photo.file_reference;
        if (inputStickeredMediaPhoto.id.file_reference == null) {
            inputStickeredMediaPhoto.id.file_reference = new byte[0];
        }
        req.media = inputStickeredMediaPhoto;
        this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new StickersAlert$$Lambda$1(this, parentObject, req, new StickersAlert$$Lambda$0(this, req)));
        init(context);
    }

    final /* synthetic */ void lambda$new$1$StickersAlert(TL_messages_getAttachedStickers req, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new StickersAlert$$Lambda$17(this, error, response, req));
    }

    final /* synthetic */ void lambda$null$0$StickersAlert(TL_error error, TLObject response, TL_messages_getAttachedStickers req) {
        this.reqId = 0;
        if (error == null) {
            Vector vector = (Vector) response;
            if (vector.objects.isEmpty()) {
                lambda$init$8$StickersAlert();
                return;
            } else if (vector.objects.size() == 1) {
                StickerSetCovered set = (StickerSetCovered) vector.objects.get(0);
                this.inputStickerSet = new TL_inputStickerSetID();
                this.inputStickerSet.id = set.set.id;
                this.inputStickerSet.access_hash = set.set.access_hash;
                loadStickerSet();
                return;
            } else {
                this.stickerSetCovereds = new ArrayList();
                for (int a = 0; a < vector.objects.size(); a++) {
                    this.stickerSetCovereds.add((StickerSetCovered) vector.objects.get(a));
                }
                this.gridView.setLayoutParams(LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
                this.titleTextView.setVisibility(8);
                this.shadow[0].setVisibility(8);
                this.adapter.notifyDataSetChanged();
                return;
            }
        }
        AlertsCreator.processError(this.currentAccount, error, this.parentFragment, req, new Object[0]);
        lambda$init$8$StickersAlert();
    }

    final /* synthetic */ void lambda$new$2$StickersAlert(Object parentObject, TL_messages_getAttachedStickers req, RequestDelegate requestDelegate, TLObject response, TL_error error) {
        if (error == null || !FileRefController.isFileRefError(error.text) || parentObject == null) {
            requestDelegate.run(response, error);
            return;
        }
        FileRefController.getInstance(this.currentAccount).requestReference(parentObject, req, requestDelegate);
    }

    public StickersAlert(Context context, BaseFragment baseFragment, InputStickerSet set, TL_messages_stickerSet loadedSet, StickersAlertDelegate stickersAlertDelegate) {
        super(context, false);
        this.shadowAnimation = new AnimatorSet[2];
        this.shadow = new View[2];
        this.previewDelegate = /* anonymous class already generated */;
        this.delegate = stickersAlertDelegate;
        this.inputStickerSet = set;
        this.stickerSet = loadedSet;
        this.parentFragment = baseFragment;
        loadStickerSet();
        init(context);
    }

    private void loadStickerSet() {
        if (this.inputStickerSet != null) {
            if (this.stickerSet == null && this.inputStickerSet.short_name != null) {
                this.stickerSet = DataQuery.getInstance(this.currentAccount).getStickerSetByName(this.inputStickerSet.short_name);
            }
            if (this.stickerSet == null) {
                this.stickerSet = DataQuery.getInstance(this.currentAccount).getStickerSetById(this.inputStickerSet.id);
            }
            if (this.stickerSet == null) {
                TL_messages_getStickerSet req = new TL_messages_getStickerSet();
                req.stickerset = this.inputStickerSet;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new StickersAlert$$Lambda$2(this));
            } else if (this.adapter != null) {
                updateSendButton();
                updateFields();
                this.adapter.notifyDataSetChanged();
            }
        }
        if (this.stickerSet != null) {
            this.showEmoji = !this.stickerSet.set.masks;
        }
    }

    final /* synthetic */ void lambda$loadStickerSet$4$StickersAlert(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new StickersAlert$$Lambda$16(this, error, response));
    }

    final /* synthetic */ void lambda$null$3$StickersAlert(TL_error error, TLObject response) {
        boolean z = false;
        this.reqId = 0;
        if (error == null) {
            this.stickerSet = (TL_messages_stickerSet) response;
            if (!this.stickerSet.set.masks) {
                z = true;
            }
            this.showEmoji = z;
            updateSendButton();
            updateFields();
            this.adapter.notifyDataSetChanged();
            return;
        }
        Toast.makeText(getContext(), LocaleController.getString("AddStickersNotFound", R.string.AddStickersNotFound), 0).show();
        lambda$init$8$StickersAlert();
    }

    private void init(Context context) {
        this.shadowDrawable = context.getResources().getDrawable(R.drawable.sheet_shadow).mutate();
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), Mode.MULTIPLY));
        this.containerView = new FrameLayout(context) {
            private int lastNotifyWidth;

            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (ev.getAction() != 0 || StickersAlert.this.scrollOffsetY == 0 || ev.getY() >= ((float) StickersAlert.this.scrollOffsetY)) {
                    return super.onInterceptTouchEvent(ev);
                }
                StickersAlert.this.lambda$init$8$StickersAlert();
                return true;
            }

            public boolean onTouchEvent(MotionEvent e) {
                return !StickersAlert.this.isDismissed() && super.onTouchEvent(e);
            }

            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int contentSize;
                int height = MeasureSpec.getSize(heightMeasureSpec);
                if (VERSION.SDK_INT >= 21) {
                    height -= AndroidUtilities.statusBarHeight;
                }
                int measuredWidth = getMeasuredWidth();
                StickersAlert.this.itemSize = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(36.0f)) / 5;
                if (StickersAlert.this.stickerSetCovereds != null) {
                    contentSize = (AndroidUtilities.dp(56.0f) + (AndroidUtilities.dp(60.0f) * StickersAlert.this.stickerSetCovereds.size())) + (StickersAlert.this.adapter.stickersRowCount * AndroidUtilities.dp(82.0f));
                } else {
                    contentSize = ((Math.max(3, StickersAlert.this.stickerSet != null ? (int) Math.ceil((double) (((float) StickersAlert.this.stickerSet.documents.size()) / 5.0f)) : 0) * AndroidUtilities.dp(82.0f)) + AndroidUtilities.dp(96.0f)) + StickersAlert.backgroundPaddingTop;
                }
                int padding = ((double) contentSize) < ((double) (height / 5)) * 3.2d ? 0 : (height / 5) * 2;
                if (padding != 0 && contentSize < height) {
                    padding -= height - contentSize;
                }
                if (padding == 0) {
                    padding = StickersAlert.backgroundPaddingTop;
                }
                if (StickersAlert.this.stickerSetCovereds != null) {
                    padding += AndroidUtilities.dp(8.0f);
                }
                if (StickersAlert.this.gridView.getPaddingTop() != padding) {
                    StickersAlert.this.ignoreLayout = true;
                    StickersAlert.this.gridView.setPadding(AndroidUtilities.dp(10.0f), padding, AndroidUtilities.dp(10.0f), 0);
                    StickersAlert.this.emptyView.setPadding(0, padding, 0, 0);
                    StickersAlert.this.ignoreLayout = false;
                }
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(Math.min(contentSize, height), NUM));
            }

            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                if (this.lastNotifyWidth != right - left) {
                    this.lastNotifyWidth = right - left;
                    if (!(StickersAlert.this.adapter == null || StickersAlert.this.stickerSetCovereds == null)) {
                        StickersAlert.this.adapter.notifyDataSetChanged();
                    }
                }
                super.onLayout(changed, left, top, right, bottom);
                StickersAlert.this.updateLayout();
            }

            public void requestLayout() {
                if (!StickersAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            protected void onDraw(Canvas canvas) {
                StickersAlert.this.shadowDrawable.setBounds(0, StickersAlert.this.scrollOffsetY - StickersAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
                StickersAlert.this.shadowDrawable.draw(canvas);
            }
        };
        this.containerView.setWillNotDraw(false);
        this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
        this.shadow[0] = new View(context);
        this.shadow[0].setBackgroundResource(R.drawable.header_shadow);
        this.shadow[0].setAlpha(0.0f);
        this.shadow[0].setVisibility(4);
        this.shadow[0].setTag(Integer.valueOf(1));
        this.containerView.addView(this.shadow[0], LayoutHelper.createFrame(-1, 3.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        this.gridView = new RecyclerListView(context) {
            public boolean onInterceptTouchEvent(MotionEvent event) {
                boolean result = StickerPreviewViewer.getInstance().onInterceptTouchEvent(event, StickersAlert.this.gridView, 0, StickersAlert.this.previewDelegate);
                if (super.onInterceptTouchEvent(event) || result) {
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
        LayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 5);
        this.layoutManager = gridLayoutManager;
        recyclerListView.setLayoutManager(gridLayoutManager);
        this.layoutManager.setSpanSizeLookup(new SpanSizeLookup() {
            public int getSpanSize(int position) {
                if ((StickersAlert.this.stickerSetCovereds == null || !(StickersAlert.this.adapter.cache.get(position) instanceof Integer)) && position != StickersAlert.this.adapter.totalItems) {
                    return 1;
                }
                return StickersAlert.this.adapter.stickersPerRow;
            }
        });
        recyclerListView = this.gridView;
        Adapter gridAdapter = new GridAdapter(context);
        this.adapter = gridAdapter;
        recyclerListView.setAdapter(gridAdapter);
        this.gridView.setVerticalScrollBarEnabled(false);
        this.gridView.addItemDecoration(new ItemDecoration() {
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
                outRect.left = 0;
                outRect.right = 0;
                outRect.bottom = 0;
                outRect.top = 0;
            }
        });
        this.gridView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        this.gridView.setClipToPadding(false);
        this.gridView.setEnabled(true);
        this.gridView.setGlowColor(Theme.getColor("dialogScrollGlow"));
        this.gridView.setOnTouchListener(new StickersAlert$$Lambda$3(this));
        this.gridView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                StickersAlert.this.updateLayout();
            }
        });
        this.stickersOnItemClickListener = new StickersAlert$$Lambda$4(this);
        this.gridView.setOnItemClickListener(this.stickersOnItemClickListener);
        this.containerView.addView(this.gridView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 48.0f, 0.0f, 48.0f));
        this.emptyView = new FrameLayout(context) {
            public void requestLayout() {
                if (!StickersAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.containerView.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.gridView.setEmptyView(this.emptyView);
        this.emptyView.setOnTouchListener(StickersAlert$$Lambda$5.$instance);
        this.titleTextView = new TextView(context);
        this.titleTextView.setLines(1);
        this.titleTextView.setSingleLine(true);
        this.titleTextView.setTextColor(Theme.getColor("dialogTextBlack"));
        this.titleTextView.setTextSize(1, 20.0f);
        this.titleTextView.setLinkTextColor(Theme.getColor("dialogTextLink"));
        this.titleTextView.setHighlightColor(Theme.getColor("dialogLinkSelection"));
        this.titleTextView.setEllipsize(TruncateAt.MIDDLE);
        this.titleTextView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.titleTextView.setGravity(16);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.containerView.addView(this.titleTextView, LayoutHelper.createLinear(-1, 50));
        this.emptyView.addView(new RadialProgressView(context), LayoutHelper.createFrame(-2, -2, 17));
        this.shadow[1] = new View(context);
        this.shadow[1].setBackgroundResource(R.drawable.header_shadow_reverse);
        this.containerView.addView(this.shadow[1], LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        this.pickerBottomLayout = new PickerBottomLayout(context, false);
        this.pickerBottomLayout.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.containerView.addView(this.pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 83));
        this.pickerBottomLayout.cancelButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.pickerBottomLayout.cancelButton.setTextColor(Theme.getColor("dialogTextBlue2"));
        this.pickerBottomLayout.cancelButton.setText(LocaleController.getString("Close", R.string.Close).toUpperCase());
        this.pickerBottomLayout.cancelButton.setOnClickListener(new StickersAlert$$Lambda$6(this));
        this.pickerBottomLayout.doneButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.pickerBottomLayout.doneButtonBadgeTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(12.5f), Theme.getColor("dialogBadgeBackground")));
        this.stickerPreviewLayout = new FrameLayout(context);
        this.stickerPreviewLayout.setBackgroundColor(Theme.getColor("dialogBackground") & -NUM);
        this.stickerPreviewLayout.setVisibility(8);
        this.stickerPreviewLayout.setSoundEffectsEnabled(false);
        this.containerView.addView(this.stickerPreviewLayout, LayoutHelper.createFrame(-1, -1.0f));
        this.stickerPreviewLayout.setOnClickListener(new StickersAlert$$Lambda$7(this));
        ImageView closeButton = new ImageView(context);
        closeButton.setImageResource(R.drawable.msg_panel_clear);
        closeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogTextGray3"), Mode.MULTIPLY));
        closeButton.setScaleType(ScaleType.CENTER);
        this.stickerPreviewLayout.addView(closeButton, LayoutHelper.createFrame(48, 48, 53));
        closeButton.setOnClickListener(new StickersAlert$$Lambda$8(this));
        this.stickerImageView = new BackupImageView(context);
        this.stickerImageView.setAspectFit(true);
        this.stickerPreviewLayout.addView(this.stickerImageView);
        this.stickerEmojiTextView = new TextView(context);
        this.stickerEmojiTextView.setTextSize(1, 30.0f);
        this.stickerEmojiTextView.setGravity(85);
        this.stickerPreviewLayout.addView(this.stickerEmojiTextView);
        this.previewSendButton = new TextView(context);
        this.previewSendButton.setTextSize(1, 14.0f);
        this.previewSendButton.setTextColor(Theme.getColor("dialogTextBlue2"));
        this.previewSendButton.setGravity(17);
        this.previewSendButton.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.previewSendButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
        this.previewSendButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.stickerPreviewLayout.addView(this.previewSendButton, LayoutHelper.createFrame(-1, 48, 83));
        this.previewSendButton.setOnClickListener(new StickersAlert$$Lambda$9(this));
        this.previewFavButton = new ImageView(context);
        this.previewFavButton.setScaleType(ScaleType.CENTER);
        this.stickerPreviewLayout.addView(this.previewFavButton, LayoutHelper.createFrame(48, 48.0f, 85, 0.0f, 0.0f, 4.0f, 0.0f));
        this.previewFavButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogIcon"), Mode.MULTIPLY));
        this.previewFavButton.setOnClickListener(new StickersAlert$$Lambda$10(this));
        this.previewSendButtonShadow = new View(context);
        this.previewSendButtonShadow.setBackgroundResource(R.drawable.header_shadow_reverse);
        this.stickerPreviewLayout.addView(this.previewSendButtonShadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        updateFields();
        updateSendButton();
        this.adapter.notifyDataSetChanged();
    }

    final /* synthetic */ boolean lambda$init$5$StickersAlert(View v, MotionEvent event) {
        return StickerPreviewViewer.getInstance().onTouch(event, this.gridView, 0, this.stickersOnItemClickListener, this.previewDelegate);
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x00c2  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0108  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x01ba  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0114  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0128  */
    final /* synthetic */ void lambda$init$6$StickersAlert(android.view.View r22, int r23) {
        /*
        r21 = this;
        r0 = r21;
        r3 = r0.stickerSetCovereds;
        if (r3 == 0) goto L_0x0043;
    L_0x0006:
        r0 = r21;
        r3 = r0.adapter;
        r3 = r3.positionsToSets;
        r0 = r23;
        r19 = r3.get(r0);
        r19 = (org.telegram.tgnet.TLRPC.StickerSetCovered) r19;
        if (r19 == 0) goto L_0x0042;
    L_0x0018:
        r21.lambda$init$8$StickersAlert();
        r5 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
        r5.<init>();
        r0 = r19;
        r3 = r0.set;
        r6 = r3.access_hash;
        r5.access_hash = r6;
        r0 = r19;
        r3 = r0.set;
        r6 = r3.id;
        r5.id = r6;
        r2 = new org.telegram.ui.Components.StickersAlert;
        r0 = r21;
        r3 = r0.parentActivity;
        r0 = r21;
        r4 = r0.parentFragment;
        r6 = 0;
        r7 = 0;
        r2.<init>(r3, r4, r5, r6, r7);
        r2.show();
    L_0x0042:
        return;
    L_0x0043:
        r0 = r21;
        r3 = r0.stickerSet;
        if (r3 == 0) goto L_0x0042;
    L_0x0049:
        if (r23 < 0) goto L_0x0042;
    L_0x004b:
        r0 = r21;
        r3 = r0.stickerSet;
        r3 = r3.documents;
        r3 = r3.size();
        r0 = r23;
        if (r0 >= r3) goto L_0x0042;
    L_0x0059:
        r0 = r21;
        r3 = r0.stickerSet;
        r3 = r3.documents;
        r0 = r23;
        r3 = r3.get(r0);
        r3 = (org.telegram.tgnet.TLRPC.Document) r3;
        r0 = r21;
        r0.selectedSticker = r3;
        r20 = 0;
        r14 = 0;
    L_0x006e:
        r0 = r21;
        r3 = r0.selectedSticker;
        r3 = r3.attributes;
        r3 = r3.size();
        if (r14 >= r3) goto L_0x00c0;
    L_0x007a:
        r0 = r21;
        r3 = r0.selectedSticker;
        r3 = r3.attributes;
        r16 = r3.get(r14);
        r16 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r16;
        r0 = r16;
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
        if (r3 == 0) goto L_0x01b1;
    L_0x008c:
        r0 = r16;
        r3 = r0.alt;
        if (r3 == 0) goto L_0x00c0;
    L_0x0092:
        r0 = r16;
        r3 = r0.alt;
        r3 = r3.length();
        if (r3 <= 0) goto L_0x00c0;
    L_0x009c:
        r0 = r21;
        r3 = r0.stickerEmojiTextView;
        r0 = r16;
        r4 = r0.alt;
        r0 = r21;
        r6 = r0.stickerEmojiTextView;
        r6 = r6.getPaint();
        r6 = r6.getFontMetricsInt();
        r7 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r8 = 0;
        r4 = org.telegram.messenger.Emoji.replaceEmoji(r4, r6, r7, r8);
        r3.setText(r4);
        r20 = 1;
    L_0x00c0:
        if (r20 != 0) goto L_0x00f2;
    L_0x00c2:
        r0 = r21;
        r3 = r0.stickerEmojiTextView;
        r0 = r21;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DataQuery.getInstance(r4);
        r0 = r21;
        r6 = r0.selectedSticker;
        r6 = r6.id;
        r4 = r4.getEmojiForSticker(r6);
        r0 = r21;
        r6 = r0.stickerEmojiTextView;
        r6 = r6.getPaint();
        r6 = r6.getFontMetricsInt();
        r7 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r8 = 0;
        r4 = org.telegram.messenger.Emoji.replaceEmoji(r4, r6, r7, r8);
        r3.setText(r4);
    L_0x00f2:
        r0 = r21;
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.DataQuery.getInstance(r3);
        r0 = r21;
        r4 = r0.selectedSticker;
        r17 = r3.isStickerInFavorites(r4);
        r0 = r21;
        r4 = r0.previewFavButton;
        if (r17 == 0) goto L_0x01b5;
    L_0x0108:
        r3 = NUM; // 0x7var_c float:1.794564E38 double:1.052935762E-314;
    L_0x010b:
        r4.setImageResource(r3);
        r0 = r21;
        r4 = r0.previewFavButton;
        if (r17 == 0) goto L_0x01ba;
    L_0x0114:
        r3 = 1;
        r3 = java.lang.Integer.valueOf(r3);
    L_0x0119:
        r4.setTag(r3);
        r0 = r21;
        r3 = r0.previewFavButton;
        r3 = r3.getVisibility();
        r4 = 8;
        if (r3 == r4) goto L_0x0140;
    L_0x0128:
        r0 = r21;
        r4 = r0.previewFavButton;
        if (r17 != 0) goto L_0x013c;
    L_0x012e:
        r0 = r21;
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.DataQuery.getInstance(r3);
        r3 = r3.canAddStickerToFavorites();
        if (r3 == 0) goto L_0x01bd;
    L_0x013c:
        r3 = 0;
    L_0x013d:
        r4.setVisibility(r3);
    L_0x0140:
        r0 = r21;
        r3 = r0.selectedSticker;
        r3 = r3.thumbs;
        r4 = 90;
        r9 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r4);
        r0 = r21;
        r3 = r0.stickerImageView;
        r6 = r3.getImageReceiver();
        r0 = r21;
        r7 = r0.selectedSticker;
        r8 = 0;
        r10 = 0;
        r11 = "webp";
        r0 = r21;
        r12 = r0.stickerSet;
        r13 = 1;
        r6.setImage(r7, r8, r9, r10, r11, r12, r13);
        r0 = r21;
        r3 = r0.stickerPreviewLayout;
        r18 = r3.getLayoutParams();
        r18 = (android.widget.FrameLayout.LayoutParams) r18;
        r0 = r21;
        r3 = r0.scrollOffsetY;
        r0 = r18;
        r0.topMargin = r3;
        r0 = r21;
        r3 = r0.stickerPreviewLayout;
        r0 = r18;
        r3.setLayoutParams(r0);
        r0 = r21;
        r3 = r0.stickerPreviewLayout;
        r4 = 0;
        r3.setVisibility(r4);
        r15 = new android.animation.AnimatorSet;
        r15.<init>();
        r3 = 1;
        r3 = new android.animation.Animator[r3];
        r4 = 0;
        r0 = r21;
        r6 = r0.stickerPreviewLayout;
        r7 = "alpha";
        r8 = 2;
        r8 = new float[r8];
        r8 = {0, NUM};
        r6 = android.animation.ObjectAnimator.ofFloat(r6, r7, r8);
        r3[r4] = r6;
        r15.playTogether(r3);
        r6 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r15.setDuration(r6);
        r15.start();
        goto L_0x0042;
    L_0x01b1:
        r14 = r14 + 1;
        goto L_0x006e;
    L_0x01b5:
        r3 = NUM; // 0x7var_ float:1.7945633E38 double:1.05293576E-314;
        goto L_0x010b;
    L_0x01ba:
        r3 = 0;
        goto L_0x0119;
    L_0x01bd:
        r3 = 4;
        goto L_0x013d;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickersAlert.lambda$init$6$StickersAlert(android.view.View, int):void");
    }

    final /* synthetic */ void lambda$init$11$StickersAlert(View v) {
        this.delegate.onStickerSelected(this.selectedSticker, this.stickerSet);
        lambda$init$8$StickersAlert();
    }

    final /* synthetic */ void lambda$init$12$StickersAlert(View v) {
        DataQuery.getInstance(this.currentAccount).addRecentSticker(2, this.stickerSet, this.selectedSticker, (int) (System.currentTimeMillis() / 1000), this.previewFavButton.getTag() != null);
        if (this.previewFavButton.getTag() == null) {
            this.previewFavButton.setTag(Integer.valueOf(1));
            this.previewFavButton.setImageResource(R.drawable.stickers_unfavorite);
            return;
        }
        this.previewFavButton.setTag(null);
        this.previewFavButton.setImageResource(R.drawable.stickers_favorite);
    }

    private void updateSendButton() {
        int size = (int) (((float) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / 2)) / AndroidUtilities.density);
        if (this.delegate == null || (this.stickerSet != null && this.stickerSet.set.masks)) {
            this.previewSendButton.setText(LocaleController.getString("Close", R.string.Close).toUpperCase());
            this.stickerImageView.setLayoutParams(LayoutHelper.createFrame(size, size, 17));
            this.stickerEmojiTextView.setLayoutParams(LayoutHelper.createFrame(size, size, 17));
            this.previewSendButton.setVisibility(8);
            this.previewFavButton.setVisibility(8);
            this.previewSendButtonShadow.setVisibility(8);
            return;
        }
        this.previewSendButton.setText(LocaleController.getString("SendSticker", R.string.SendSticker).toUpperCase());
        this.stickerImageView.setLayoutParams(LayoutHelper.createFrame(size, (float) size, 17, 0.0f, 0.0f, 0.0f, 30.0f));
        this.stickerEmojiTextView.setLayoutParams(LayoutHelper.createFrame(size, (float) size, 17, 0.0f, 0.0f, 0.0f, 30.0f));
        this.previewSendButton.setVisibility(0);
        this.previewFavButton.setVisibility(0);
        this.previewSendButtonShadow.setVisibility(0);
    }

    public void setInstallDelegate(StickersAlertInstallDelegate stickersAlertInstallDelegate) {
        this.installDelegate = stickersAlertInstallDelegate;
    }

    /* JADX WARNING: Removed duplicated region for block: B:40:0x00cb  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00cb  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0087  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00a2  */
    private void updateFields() {
        /*
        r11 = this;
        r8 = 0;
        r10 = 0;
        r7 = r11.titleTextView;
        if (r7 != 0) goto L_0x0007;
    L_0x0006:
        return;
    L_0x0007:
        r7 = r11.stickerSet;
        if (r7 == 0) goto L_0x0119;
    L_0x000b:
        r4 = 0;
        r7 = r11.urlPattern;	 Catch:{ Exception -> 0x00c6 }
        if (r7 != 0) goto L_0x0019;
    L_0x0010:
        r7 = "@[a-zA-Z\\d_]{1,32}";
        r7 = java.util.regex.Pattern.compile(r7);	 Catch:{ Exception -> 0x00c6 }
        r11.urlPattern = r7;	 Catch:{ Exception -> 0x00c6 }
    L_0x0019:
        r7 = r11.urlPattern;	 Catch:{ Exception -> 0x00c6 }
        r8 = r11.stickerSet;	 Catch:{ Exception -> 0x00c6 }
        r8 = r8.set;	 Catch:{ Exception -> 0x00c6 }
        r8 = r8.title;	 Catch:{ Exception -> 0x00c6 }
        r2 = r7.matcher(r8);	 Catch:{ Exception -> 0x00c6 }
        r5 = r4;
    L_0x0026:
        r7 = r2.find();	 Catch:{ Exception -> 0x0125 }
        if (r7 == 0) goto L_0x0079;
    L_0x002c:
        if (r5 != 0) goto L_0x0128;
    L_0x002e:
        r4 = new android.text.SpannableStringBuilder;	 Catch:{ Exception -> 0x0125 }
        r7 = r11.stickerSet;	 Catch:{ Exception -> 0x0125 }
        r7 = r7.set;	 Catch:{ Exception -> 0x0125 }
        r7 = r7.title;	 Catch:{ Exception -> 0x0125 }
        r4.<init>(r7);	 Catch:{ Exception -> 0x0125 }
        r7 = r11.titleTextView;	 Catch:{ Exception -> 0x00c6 }
        r8 = new org.telegram.ui.Components.StickersAlert$LinkMovementMethodMy;	 Catch:{ Exception -> 0x00c6 }
        r9 = 0;
        r8.<init>(r9);	 Catch:{ Exception -> 0x00c6 }
        r7.setMovementMethod(r8);	 Catch:{ Exception -> 0x00c6 }
    L_0x0044:
        r3 = r2.start();	 Catch:{ Exception -> 0x00c6 }
        r1 = r2.end();	 Catch:{ Exception -> 0x00c6 }
        r7 = r11.stickerSet;	 Catch:{ Exception -> 0x00c6 }
        r7 = r7.set;	 Catch:{ Exception -> 0x00c6 }
        r7 = r7.title;	 Catch:{ Exception -> 0x00c6 }
        r7 = r7.charAt(r3);	 Catch:{ Exception -> 0x00c6 }
        r8 = 64;
        if (r7 == r8) goto L_0x005c;
    L_0x005a:
        r3 = r3 + 1;
    L_0x005c:
        r6 = new org.telegram.ui.Components.StickersAlert$8;	 Catch:{ Exception -> 0x00c6 }
        r7 = r11.stickerSet;	 Catch:{ Exception -> 0x00c6 }
        r7 = r7.set;	 Catch:{ Exception -> 0x00c6 }
        r7 = r7.title;	 Catch:{ Exception -> 0x00c6 }
        r8 = r3 + 1;
        r7 = r7.subSequence(r8, r1);	 Catch:{ Exception -> 0x00c6 }
        r7 = r7.toString();	 Catch:{ Exception -> 0x00c6 }
        r6.<init>(r7);	 Catch:{ Exception -> 0x00c6 }
        if (r6 == 0) goto L_0x0077;
    L_0x0073:
        r7 = 0;
        r4.setSpan(r6, r3, r1, r7);	 Catch:{ Exception -> 0x00c6 }
    L_0x0077:
        r5 = r4;
        goto L_0x0026;
    L_0x0079:
        r4 = r5;
    L_0x007a:
        r7 = r11.titleTextView;
        if (r4 == 0) goto L_0x00cb;
    L_0x007e:
        r7.setText(r4);
        r7 = r11.stickerSet;
        r7 = r7.set;
        if (r7 == 0) goto L_0x0099;
    L_0x0087:
        r7 = r11.currentAccount;
        r7 = org.telegram.messenger.DataQuery.getInstance(r7);
        r8 = r11.stickerSet;
        r8 = r8.set;
        r8 = r8.id;
        r7 = r7.isStickerPackInstalled(r8);
        if (r7 != 0) goto L_0x00dd;
    L_0x0099:
        r8 = new org.telegram.ui.Components.StickersAlert$$Lambda$11;
        r8.<init>(r11);
        r7 = r11.stickerSet;
        if (r7 == 0) goto L_0x00d2;
    L_0x00a2:
        r7 = r11.stickerSet;
        r7 = r7.set;
        r7 = r7.masks;
        if (r7 == 0) goto L_0x00d2;
    L_0x00aa:
        r7 = "AddMasks";
        r9 = NUM; // 0x7f0CLASSNAME float:1.860937E38 double:1.053097442E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r9);
    L_0x00b4:
        r9 = "dialogTextBlue2";
        r9 = org.telegram.ui.ActionBar.Theme.getColor(r9);
        r10 = 1;
        r11.setRightButton(r8, r7, r9, r10);
    L_0x00bf:
        r7 = r11.adapter;
        r7.notifyDataSetChanged();
        goto L_0x0006;
    L_0x00c6:
        r0 = move-exception;
    L_0x00c7:
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x007a;
    L_0x00cb:
        r8 = r11.stickerSet;
        r8 = r8.set;
        r4 = r8.title;
        goto L_0x007e;
    L_0x00d2:
        r7 = "AddStickers";
        r9 = NUM; // 0x7f0CLASSNAMEf float:1.8609385E38 double:1.0530974454E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r9);
        goto L_0x00b4;
    L_0x00dd:
        r7 = r11.stickerSet;
        r7 = r7.set;
        r7 = r7.official;
        if (r7 == 0) goto L_0x00ff;
    L_0x00e5:
        r7 = new org.telegram.ui.Components.StickersAlert$$Lambda$12;
        r7.<init>(r11);
        r8 = "StickersRemove";
        r9 = NUM; // 0x7f0CLASSNAMEf5 float:1.8613323E38 double:1.053098405E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r9);
        r9 = "dialogTextRed";
        r9 = org.telegram.ui.ActionBar.Theme.getColor(r9);
        r11.setRightButton(r7, r8, r9, r10);
        goto L_0x00bf;
    L_0x00ff:
        r7 = new org.telegram.ui.Components.StickersAlert$$Lambda$13;
        r7.<init>(r11);
        r8 = "StickersRemove";
        r9 = NUM; // 0x7f0CLASSNAMEf7 float:1.8613328E38 double:1.053098406E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r9);
        r9 = "dialogTextRed";
        r9 = org.telegram.ui.ActionBar.Theme.getColor(r9);
        r11.setRightButton(r7, r8, r9, r10);
        goto L_0x00bf;
    L_0x0119:
        r7 = "dialogTextRed";
        r7 = org.telegram.ui.ActionBar.Theme.getColor(r7);
        r11.setRightButton(r8, r8, r7, r10);
        goto L_0x0006;
    L_0x0125:
        r0 = move-exception;
        r4 = r5;
        goto L_0x00c7;
    L_0x0128:
        r4 = r5;
        goto L_0x0044;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickersAlert.updateFields():void");
    }

    final /* synthetic */ void lambda$updateFields$15$StickersAlert(View v) {
        lambda$init$8$StickersAlert();
        if (this.installDelegate != null) {
            this.installDelegate.onStickerSetInstalled();
        }
        TL_messages_installStickerSet req = new TL_messages_installStickerSet();
        req.stickerset = this.inputStickerSet;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new StickersAlert$$Lambda$14(this));
    }

    final /* synthetic */ void lambda$null$14$StickersAlert(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new StickersAlert$$Lambda$15(this, error, response));
    }

    final /* synthetic */ void lambda$null$13$StickersAlert(TL_error error, TLObject response) {
        int i;
        if (error == null) {
            try {
                if (this.stickerSet.set.masks) {
                    Toast.makeText(getContext(), LocaleController.getString("AddMasksInstalled", R.string.AddMasksInstalled), 0).show();
                } else {
                    Toast.makeText(getContext(), LocaleController.getString("AddStickersInstalled", R.string.AddStickersInstalled), 0).show();
                }
                if (response instanceof TL_messages_stickerSetInstallResultArchive) {
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needReloadArchivedStickers, new Object[0]);
                    if (!(this.parentFragment == null || this.parentFragment.getParentActivity() == null)) {
                        this.parentFragment.showDialog(new StickersArchiveAlert(this.parentFragment.getParentActivity(), this.parentFragment, ((TL_messages_stickerSetInstallResultArchive) response).sets).create());
                    }
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        } else {
            Toast.makeText(getContext(), LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred), 0).show();
        }
        DataQuery instance = DataQuery.getInstance(this.currentAccount);
        if (this.stickerSet.set.masks) {
            i = 1;
        } else {
            i = 0;
        }
        instance.loadStickers(i, false, true);
    }

    final /* synthetic */ void lambda$updateFields$16$StickersAlert(View v) {
        if (this.installDelegate != null) {
            this.installDelegate.onStickerSetUninstalled();
        }
        lambda$init$8$StickersAlert();
        DataQuery.getInstance(this.currentAccount).removeStickersSet(getContext(), this.stickerSet.set, 1, this.parentFragment, true);
    }

    final /* synthetic */ void lambda$updateFields$17$StickersAlert(View v) {
        if (this.installDelegate != null) {
            this.installDelegate.onStickerSetUninstalled();
        }
        lambda$init$8$StickersAlert();
        DataQuery.getInstance(this.currentAccount).removeStickersSet(getContext(), this.stickerSet.set, 0, this.parentFragment, true);
    }

    protected boolean canDismissWithSwipe() {
        return false;
    }

    @SuppressLint({"NewApi"})
    private void updateLayout() {
        RecyclerListView recyclerListView;
        if (this.gridView.getChildCount() <= 0) {
            recyclerListView = this.gridView;
            int paddingTop = this.gridView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            if (this.stickerSetCovereds == null) {
                this.titleTextView.setTranslationY((float) this.scrollOffsetY);
                this.shadow[0].setTranslationY((float) this.scrollOffsetY);
            }
            this.containerView.invalidate();
            return;
        }
        View child = this.gridView.getChildAt(0);
        Holder holder = (Holder) this.gridView.findContainingViewHolder(child);
        int top = child.getTop();
        int newOffset = 0;
        if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
            runShadowAnimation(0, true);
        } else {
            newOffset = top;
            runShadowAnimation(0, false);
        }
        if (this.scrollOffsetY != newOffset) {
            recyclerListView = this.gridView;
            this.scrollOffsetY = newOffset;
            recyclerListView.setTopGlowOffset(newOffset);
            if (this.stickerSetCovereds == null) {
                this.titleTextView.setTranslationY((float) this.scrollOffsetY);
                this.shadow[0].setTranslationY((float) this.scrollOffsetY);
            }
            this.containerView.invalidate();
        }
    }

    private void hidePreview() {
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr = new Animator[1];
        animatorArr[0] = ObjectAnimator.ofFloat(this.stickerPreviewLayout, "alpha", new float[]{0.0f});
        animatorSet.playTogether(animatorArr);
        animatorSet.setDuration(200);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                StickersAlert.this.stickerPreviewLayout.setVisibility(8);
            }
        });
        animatorSet.start();
    }

    private void runShadowAnimation(final int num, final boolean show) {
        if (this.stickerSetCovereds == null) {
            if ((show && this.shadow[num].getTag() != null) || (!show && this.shadow[num].getTag() == null)) {
                this.shadow[num].setTag(show ? null : Integer.valueOf(1));
                if (show) {
                    this.shadow[num].setVisibility(0);
                }
                if (this.shadowAnimation[num] != null) {
                    this.shadowAnimation[num].cancel();
                }
                this.shadowAnimation[num] = new AnimatorSet();
                AnimatorSet animatorSet = this.shadowAnimation[num];
                Animator[] animatorArr = new Animator[1];
                Object obj = this.shadow[num];
                String str = "alpha";
                float[] fArr = new float[1];
                fArr[0] = show ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(obj, str, fArr);
                animatorSet.playTogether(animatorArr);
                this.shadowAnimation[num].setDuration(150);
                this.shadowAnimation[num].addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (StickersAlert.this.shadowAnimation[num] != null && StickersAlert.this.shadowAnimation[num].equals(animation)) {
                            if (!show) {
                                StickersAlert.this.shadow[num].setVisibility(4);
                            }
                            StickersAlert.this.shadowAnimation[num] = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (StickersAlert.this.shadowAnimation[num] != null && StickersAlert.this.shadowAnimation[num].equals(animation)) {
                            StickersAlert.this.shadowAnimation[num] = null;
                        }
                    }
                });
                this.shadowAnimation[num].start();
            }
        }
    }

    /* renamed from: dismiss */
    public void lambda$init$8$StickersAlert() {
        super.lambda$new$4$EmbedBottomSheet();
        if (this.reqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.emojiDidLoad) {
            if (this.gridView != null) {
                int count = this.gridView.getChildCount();
                for (int a = 0; a < count; a++) {
                    this.gridView.getChildAt(a).invalidate();
                }
            }
            if (StickerPreviewViewer.getInstance().isVisible()) {
                StickerPreviewViewer.getInstance().close();
            }
            StickerPreviewViewer.getInstance().reset();
        }
    }

    private void setRightButton(OnClickListener onClickListener, String title, int color, boolean showCircle) {
        if (title == null) {
            this.pickerBottomLayout.doneButton.setVisibility(8);
            return;
        }
        this.pickerBottomLayout.doneButton.setVisibility(0);
        if (showCircle) {
            this.pickerBottomLayout.doneButtonBadgeTextView.setVisibility(0);
            this.pickerBottomLayout.doneButtonBadgeTextView.setText(String.format("%d", new Object[]{Integer.valueOf(this.stickerSet.documents.size())}));
        } else {
            this.pickerBottomLayout.doneButtonBadgeTextView.setVisibility(8);
        }
        this.pickerBottomLayout.doneButtonTextView.setTextColor(color);
        this.pickerBottomLayout.doneButtonTextView.setText(title.toUpperCase());
        this.pickerBottomLayout.doneButton.setOnClickListener(onClickListener);
    }
}
