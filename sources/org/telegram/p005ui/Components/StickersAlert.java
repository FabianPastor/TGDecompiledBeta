package org.telegram.p005ui.Components;

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
import android.text.SpannableStringBuilder;
import android.text.TextUtils.TruncateAt;
import android.text.method.LinkMovementMethod;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FileRefController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
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
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.BottomSheet;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Cells.EmptyCell;
import org.telegram.p005ui.Cells.FeaturedStickerSetInfoCell;
import org.telegram.p005ui.Cells.StickerEmojiCell;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.p005ui.StickerPreviewViewer;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
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

/* renamed from: org.telegram.ui.Components.StickersAlert */
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

    /* renamed from: org.telegram.ui.Components.StickersAlert$StickersAlertInstallDelegate */
    public interface StickersAlertInstallDelegate {
        void onStickerSetInstalled();

        void onStickerSetUninstalled();
    }

    /* renamed from: org.telegram.ui.Components.StickersAlert$StickersAlertDelegate */
    public interface StickersAlertDelegate {
        void onStickerSelected(Document document, Object obj);
    }

    /* renamed from: org.telegram.ui.Components.StickersAlert$3 */
    class CLASSNAME extends SpanSizeLookup {
        CLASSNAME() {
        }

        public int getSpanSize(int position) {
            if ((StickersAlert.this.stickerSetCovereds == null || !(StickersAlert.this.adapter.cache.get(position) instanceof Integer)) && position != StickersAlert.this.adapter.totalItems) {
                return 1;
            }
            return StickersAlert.this.adapter.stickersPerRow;
        }
    }

    /* renamed from: org.telegram.ui.Components.StickersAlert$4 */
    class CLASSNAME extends ItemDecoration {
        CLASSNAME() {
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            outRect.left = 0;
            outRect.right = 0;
            outRect.bottom = 0;
            outRect.top = 0;
        }
    }

    /* renamed from: org.telegram.ui.Components.StickersAlert$5 */
    class CLASSNAME extends OnScrollListener {
        CLASSNAME() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            StickersAlert.this.updateLayout();
        }
    }

    /* renamed from: org.telegram.ui.Components.StickersAlert$8 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            StickersAlert.this.stickerPreviewLayout.setVisibility(8);
        }
    }

    /* renamed from: org.telegram.ui.Components.StickersAlert$GridAdapter */
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
                            super.onMeasure(MeasureSpec.makeMeasureSpec(StickersAlert.this.itemSize, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(82.0f), NUM));
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
                        ((EmptyCell) holder.itemView).setHeight(AndroidUtilities.m9dp(82.0f));
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
                this.stickersPerRow = width / AndroidUtilities.m9dp(72.0f);
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

    /* renamed from: org.telegram.ui.Components.StickersAlert$LinkMovementMethodMy */
    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        /* synthetic */ LinkMovementMethodMy(CLASSNAME x0) {
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
                FileLog.m13e(e);
                return false;
            }
        }
    }

    public StickersAlert(Context context, Object parentObject, Photo photo) {
        super(context, false);
        this.shadowAnimation = new AnimatorSet[2];
        this.shadow = new View[2];
        this.parentActivity = (Activity) context;
        TL_messages_getAttachedStickers req = new TL_messages_getAttachedStickers();
        TL_inputStickeredMediaPhoto inputStickeredMediaPhoto = new TL_inputStickeredMediaPhoto();
        inputStickeredMediaPhoto.var_id = new TL_inputPhoto();
        inputStickeredMediaPhoto.var_id.var_id = photo.var_id;
        inputStickeredMediaPhoto.var_id.access_hash = photo.access_hash;
        inputStickeredMediaPhoto.var_id.file_reference = photo.file_reference;
        if (inputStickeredMediaPhoto.var_id.file_reference == null) {
            inputStickeredMediaPhoto.var_id.file_reference = new byte[0];
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
                this.inputStickerSet.var_id = set.set.var_id;
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
                this.stickerSet = DataQuery.getInstance(this.currentAccount).getStickerSetById(this.inputStickerSet.var_id);
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
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), Mode.MULTIPLY));
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
                StickersAlert.this.itemSize = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.m9dp(36.0f)) / 5;
                if (StickersAlert.this.stickerSetCovereds != null) {
                    contentSize = (AndroidUtilities.m9dp(56.0f) + (AndroidUtilities.m9dp(60.0f) * StickersAlert.this.stickerSetCovereds.size())) + (StickersAlert.this.adapter.stickersRowCount * AndroidUtilities.m9dp(82.0f));
                } else {
                    contentSize = ((Math.max(3, StickersAlert.this.stickerSet != null ? (int) Math.ceil((double) (((float) StickersAlert.this.stickerSet.documents.size()) / 5.0f)) : 0) * AndroidUtilities.m9dp(82.0f)) + AndroidUtilities.m9dp(96.0f)) + StickersAlert.backgroundPaddingTop;
                }
                int padding = ((double) contentSize) < ((double) (height / 5)) * 3.2d ? 0 : (height / 5) * 2;
                if (padding != 0 && contentSize < height) {
                    padding -= height - contentSize;
                }
                if (padding == 0) {
                    padding = StickersAlert.backgroundPaddingTop;
                }
                if (StickersAlert.this.stickerSetCovereds != null) {
                    padding += AndroidUtilities.m9dp(8.0f);
                }
                if (StickersAlert.this.gridView.getPaddingTop() != padding) {
                    StickersAlert.this.ignoreLayout = true;
                    StickersAlert.this.gridView.setPadding(AndroidUtilities.m9dp(10.0f), padding, AndroidUtilities.m9dp(10.0f), 0);
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
                boolean result = StickerPreviewViewer.getInstance().onInterceptTouchEvent(event, StickersAlert.this.gridView, 0, null);
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
        this.layoutManager.setSpanSizeLookup(new CLASSNAME());
        recyclerListView = this.gridView;
        Adapter gridAdapter = new GridAdapter(context);
        this.adapter = gridAdapter;
        recyclerListView.setAdapter(gridAdapter);
        this.gridView.setVerticalScrollBarEnabled(false);
        this.gridView.addItemDecoration(new CLASSNAME());
        this.gridView.setPadding(AndroidUtilities.m9dp(10.0f), 0, AndroidUtilities.m9dp(10.0f), 0);
        this.gridView.setClipToPadding(false);
        this.gridView.setEnabled(true);
        this.gridView.setGlowColor(Theme.getColor(Theme.key_dialogScrollGlow));
        this.gridView.setOnTouchListener(new StickersAlert$$Lambda$3(this));
        this.gridView.setOnScrollListener(new CLASSNAME());
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
        this.titleTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.titleTextView.setTextSize(1, 20.0f);
        this.titleTextView.setLinkTextColor(Theme.getColor(Theme.key_dialogTextLink));
        this.titleTextView.setHighlightColor(Theme.getColor(Theme.key_dialogLinkSelection));
        this.titleTextView.setEllipsize(TruncateAt.MIDDLE);
        this.titleTextView.setPadding(AndroidUtilities.m9dp(18.0f), 0, AndroidUtilities.m9dp(18.0f), 0);
        this.titleTextView.setGravity(16);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.containerView.addView(this.titleTextView, LayoutHelper.createLinear(-1, 50));
        this.emptyView.addView(new RadialProgressView(context), LayoutHelper.createFrame(-2, -2, 17));
        this.shadow[1] = new View(context);
        this.shadow[1].setBackgroundResource(R.drawable.header_shadow_reverse);
        this.containerView.addView(this.shadow[1], LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        this.pickerBottomLayout = new PickerBottomLayout(context, false);
        this.pickerBottomLayout.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        this.containerView.addView(this.pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 83));
        this.pickerBottomLayout.cancelButton.setPadding(AndroidUtilities.m9dp(18.0f), 0, AndroidUtilities.m9dp(18.0f), 0);
        this.pickerBottomLayout.cancelButton.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        this.pickerBottomLayout.cancelButton.setText(LocaleController.getString("Close", R.string.Close).toUpperCase());
        this.pickerBottomLayout.cancelButton.setOnClickListener(new StickersAlert$$Lambda$6(this));
        this.pickerBottomLayout.doneButton.setPadding(AndroidUtilities.m9dp(18.0f), 0, AndroidUtilities.m9dp(18.0f), 0);
        this.pickerBottomLayout.doneButtonBadgeTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.m9dp(12.5f), Theme.getColor(Theme.key_dialogBadgeBackground)));
        this.stickerPreviewLayout = new FrameLayout(context);
        this.stickerPreviewLayout.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground) & -NUM);
        this.stickerPreviewLayout.setVisibility(8);
        this.stickerPreviewLayout.setSoundEffectsEnabled(false);
        this.containerView.addView(this.stickerPreviewLayout, LayoutHelper.createFrame(-1, -1.0f));
        this.stickerPreviewLayout.setOnClickListener(new StickersAlert$$Lambda$7(this));
        ImageView closeButton = new ImageView(context);
        closeButton.setImageResource(R.drawable.msg_panel_clear);
        closeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogTextGray3), Mode.MULTIPLY));
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
        this.previewSendButton.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        this.previewSendButton.setGravity(17);
        this.previewSendButton.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        this.previewSendButton.setPadding(AndroidUtilities.m9dp(29.0f), 0, AndroidUtilities.m9dp(29.0f), 0);
        this.previewSendButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.stickerPreviewLayout.addView(this.previewSendButton, LayoutHelper.createFrame(-1, 48, 83));
        this.previewSendButton.setOnClickListener(new StickersAlert$$Lambda$9(this));
        this.previewFavButton = new ImageView(context);
        this.previewFavButton.setScaleType(ScaleType.CENTER);
        this.stickerPreviewLayout.addView(this.previewFavButton, LayoutHelper.createFrame(48, 48.0f, 85, 0.0f, 0.0f, 4.0f, 0.0f));
        this.previewFavButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogIcon), Mode.MULTIPLY));
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
        return StickerPreviewViewer.getInstance().onTouch(event, this.gridView, 0, this.stickersOnItemClickListener, null);
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x00c2  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x01b9  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0108  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x01be  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0114  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0128  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x01c4  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0155  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    final /* synthetic */ void lambda$init$6$StickersAlert(View view, int position) {
        if (this.stickerSetCovereds != null) {
            StickerSetCovered pack = (StickerSetCovered) this.adapter.positionsToSets.get(position);
            if (pack != null) {
                lambda$init$8$StickersAlert();
                TL_inputStickerSetID inputStickerSetID = new TL_inputStickerSetID();
                inputStickerSetID.access_hash = pack.set.access_hash;
                inputStickerSetID.var_id = pack.set.var_id;
                new StickersAlert(this.parentActivity, this.parentFragment, inputStickerSetID, null, null).show();
            }
        } else if (this.stickerSet != null && position >= 0 && position < this.stickerSet.documents.size()) {
            boolean fav;
            LayoutParams layoutParams;
            AnimatorSet animatorSet;
            this.selectedSticker = (Document) this.stickerSet.documents.get(position);
            boolean set = false;
            for (int a = 0; a < this.selectedSticker.attributes.size(); a++) {
                DocumentAttribute attribute = (DocumentAttribute) this.selectedSticker.attributes.get(a);
                if (attribute instanceof TL_documentAttributeSticker) {
                    if (attribute.alt != null && attribute.alt.length() > 0) {
                        this.stickerEmojiTextView.setText(Emoji.replaceEmoji(attribute.alt, this.stickerEmojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.m9dp(30.0f), false));
                        set = true;
                    }
                    if (!set) {
                        this.stickerEmojiTextView.setText(Emoji.replaceEmoji(DataQuery.getInstance(this.currentAccount).getEmojiForSticker(this.selectedSticker.var_id), this.stickerEmojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.m9dp(30.0f), false));
                    }
                    fav = DataQuery.getInstance(this.currentAccount).isStickerInFavorites(this.selectedSticker);
                    this.previewFavButton.setImageResource(fav ? R.drawable.stickers_unfavorite : R.drawable.stickers_favorite);
                    this.previewFavButton.setTag(fav ? Integer.valueOf(1) : null);
                    if (this.previewFavButton.getVisibility() != 8) {
                        ImageView imageView = this.previewFavButton;
                        int i = (fav || DataQuery.getInstance(this.currentAccount).canAddStickerToFavorites()) ? 0 : 4;
                        imageView.setVisibility(i);
                    }
                    this.stickerImageView.getImageReceiver().setImage(this.selectedSticker, null, this.selectedSticker.thumb == null ? this.selectedSticker.thumb.location : null, null, "webp", this.stickerSet, 1);
                    layoutParams = (FrameLayout.LayoutParams) this.stickerPreviewLayout.getLayoutParams();
                    layoutParams.topMargin = this.scrollOffsetY;
                    this.stickerPreviewLayout.setLayoutParams(layoutParams);
                    this.stickerPreviewLayout.setVisibility(0);
                    animatorSet = new AnimatorSet();
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.stickerPreviewLayout, "alpha", new float[]{0.0f, 1.0f})});
                    animatorSet.setDuration(200);
                    animatorSet.start();
                }
            }
            if (set) {
            }
            fav = DataQuery.getInstance(this.currentAccount).isStickerInFavorites(this.selectedSticker);
            if (fav) {
            }
            this.previewFavButton.setImageResource(fav ? R.drawable.stickers_unfavorite : R.drawable.stickers_favorite);
            if (fav) {
            }
            this.previewFavButton.setTag(fav ? Integer.valueOf(1) : null);
            if (this.previewFavButton.getVisibility() != 8) {
            }
            if (this.selectedSticker.thumb == null) {
            }
            this.stickerImageView.getImageReceiver().setImage(this.selectedSticker, null, this.selectedSticker.thumb == null ? this.selectedSticker.thumb.location : null, null, "webp", this.stickerSet, 1);
            layoutParams = (FrameLayout.LayoutParams) this.stickerPreviewLayout.getLayoutParams();
            layoutParams.topMargin = this.scrollOffsetY;
            this.stickerPreviewLayout.setLayoutParams(layoutParams);
            this.stickerPreviewLayout.setVisibility(0);
            animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.stickerPreviewLayout, "alpha", new float[]{0.0f, 1.0f})});
            animatorSet.setDuration(200);
            animatorSet.start();
        }
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
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateFields() {
        Throwable e;
        TextView textView;
        OnClickListener stickersAlert$$Lambda$11;
        String string;
        if (this.titleTextView != null) {
            if (this.stickerSet != null) {
                SpannableStringBuilder stringBuilder = null;
                try {
                    SpannableStringBuilder stringBuilder2;
                    if (this.urlPattern == null) {
                        this.urlPattern = Pattern.compile("@[a-zA-Z\\d_]{1,32}");
                    }
                    Matcher matcher = this.urlPattern.matcher(this.stickerSet.set.title);
                    while (true) {
                        try {
                            stringBuilder2 = stringBuilder;
                            if (!matcher.find()) {
                                break;
                            }
                            if (stringBuilder2 == null) {
                                stringBuilder = new SpannableStringBuilder(this.stickerSet.set.title);
                                this.titleTextView.setMovementMethod(new LinkMovementMethodMy());
                            } else {
                                stringBuilder = stringBuilder2;
                            }
                            int start = matcher.start();
                            int end = matcher.end();
                            if (this.stickerSet.set.title.charAt(start) != '@') {
                                start++;
                            }
                            URLSpanNoUnderline url = new URLSpanNoUnderline(this.stickerSet.set.title.subSequence(start + 1, end).toString()) {
                                public void onClick(View widget) {
                                    MessagesController.getInstance(StickersAlert.this.currentAccount).openByUserName(getURL(), StickersAlert.this.parentFragment, 1);
                                    StickersAlert.this.lambda$init$8$StickersAlert();
                                }
                            };
                            if (url != null) {
                                stringBuilder.setSpan(url, start, end, 0);
                            }
                        } catch (Exception e2) {
                            e = e2;
                            stringBuilder = stringBuilder2;
                            FileLog.m13e(e);
                            textView = this.titleTextView;
                            if (stringBuilder == null) {
                            }
                            textView.setText(stringBuilder);
                            if (this.stickerSet.set != null) {
                            }
                            stickersAlert$$Lambda$11 = new StickersAlert$$Lambda$11(this);
                            if (this.stickerSet == null) {
                            }
                            setRightButton(stickersAlert$$Lambda$11, string, Theme.getColor(Theme.key_dialogTextBlue2), true);
                            this.adapter.notifyDataSetChanged();
                            return;
                        }
                    }
                    stringBuilder = stringBuilder2;
                } catch (Exception e3) {
                    e = e3;
                    FileLog.m13e(e);
                    textView = this.titleTextView;
                    if (stringBuilder == null) {
                    }
                    textView.setText(stringBuilder);
                    if (this.stickerSet.set != null) {
                    }
                    stickersAlert$$Lambda$11 = new StickersAlert$$Lambda$11(this);
                    if (this.stickerSet == null) {
                    }
                    setRightButton(stickersAlert$$Lambda$11, string, Theme.getColor(Theme.key_dialogTextBlue2), true);
                    this.adapter.notifyDataSetChanged();
                    return;
                }
                textView = this.titleTextView;
                if (stringBuilder == null) {
                    stringBuilder = this.stickerSet.set.title;
                }
                textView.setText(stringBuilder);
                if (this.stickerSet.set != null || !DataQuery.getInstance(this.currentAccount).isStickerPackInstalled(this.stickerSet.set.var_id)) {
                    stickersAlert$$Lambda$11 = new StickersAlert$$Lambda$11(this);
                    string = (this.stickerSet == null && this.stickerSet.set.masks) ? LocaleController.getString("AddMasks", R.string.AddMasks) : LocaleController.getString("AddStickers", R.string.AddStickers);
                    setRightButton(stickersAlert$$Lambda$11, string, Theme.getColor(Theme.key_dialogTextBlue2), true);
                } else if (this.stickerSet.set.official) {
                    setRightButton(new StickersAlert$$Lambda$12(this), LocaleController.getString("StickersRemove", R.string.StickersHide), Theme.getColor(Theme.key_dialogTextRed), false);
                } else {
                    setRightButton(new StickersAlert$$Lambda$13(this), LocaleController.getString("StickersRemove", R.string.StickersRemove), Theme.getColor(Theme.key_dialogTextRed), false);
                }
                this.adapter.notifyDataSetChanged();
                return;
            }
            setRightButton(null, null, Theme.getColor(Theme.key_dialogTextRed), false);
        }
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
                FileLog.m13e(e);
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
        animatorSet.addListener(new CLASSNAME());
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
