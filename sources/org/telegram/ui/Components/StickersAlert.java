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
import android.text.SpannableStringBuilder;
import android.text.TextUtils.TruncateAt;
import android.text.method.LinkMovementMethod;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
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
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.InputStickeredMedia;
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

    /* renamed from: org.telegram.ui.Components.StickersAlert$7 */
    class C13157 implements OnTouchListener {
        C13157() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return StickerPreviewViewer.getInstance().onTouch(motionEvent, StickersAlert.this.gridView, 0, StickersAlert.this.stickersOnItemClickListener, null);
        }
    }

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            try {
                textView = super.onTouchEvent(textView, spannable, motionEvent);
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    Selection.removeSelection(spannable);
                }
                return textView;
            } catch (Throwable e) {
                FileLog.m3e(e);
                return null;
            }
        }
    }

    public interface StickersAlertDelegate {
        void onStickerSelected(Document document);
    }

    public interface StickersAlertInstallDelegate {
        void onStickerSetInstalled();

        void onStickerSetUninstalled();
    }

    /* renamed from: org.telegram.ui.Components.StickersAlert$2 */
    class C20932 implements RequestDelegate {
        C20932() {
        }

        public void run(final TLObject tLObject, final TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    StickersAlert.this.reqId = 0;
                    if (tL_error == null) {
                        StickersAlert.this.stickerSet = (TL_messages_stickerSet) tLObject;
                        StickersAlert.this.showEmoji = StickersAlert.this.stickerSet.set.masks ^ 1;
                        StickersAlert.this.updateSendButton();
                        StickersAlert.this.updateFields();
                        StickersAlert.this.adapter.notifyDataSetChanged();
                        return;
                    }
                    Toast.makeText(StickersAlert.this.getContext(), LocaleController.getString("AddStickersNotFound", C0446R.string.AddStickersNotFound), 0).show();
                    StickersAlert.this.dismiss();
                }
            });
        }
    }

    /* renamed from: org.telegram.ui.Components.StickersAlert$5 */
    class C20945 extends SpanSizeLookup {
        C20945() {
        }

        public int getSpanSize(int i) {
            return ((StickersAlert.this.stickerSetCovereds == null || !(StickersAlert.this.adapter.cache.get(i) instanceof Integer)) && i != StickersAlert.this.adapter.totalItems) ? 1 : StickersAlert.this.adapter.stickersPerRow;
        }
    }

    /* renamed from: org.telegram.ui.Components.StickersAlert$6 */
    class C20956 extends ItemDecoration {
        C20956() {
        }

        public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
            rect.left = 0;
            rect.right = 0;
            rect.bottom = 0;
            rect.top = 0;
        }
    }

    /* renamed from: org.telegram.ui.Components.StickersAlert$8 */
    class C20968 extends OnScrollListener {
        C20968() {
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            StickersAlert.this.updateLayout();
        }
    }

    /* renamed from: org.telegram.ui.Components.StickersAlert$9 */
    class C20979 implements OnItemClickListener {
        C20979() {
        }

        public void onItemClick(View view, int i) {
            if (StickersAlert.this.stickerSetCovereds != null) {
                StickerSetCovered stickerSetCovered = (StickerSetCovered) StickersAlert.this.adapter.positionsToSets.get(i);
                if (stickerSetCovered != null) {
                    StickersAlert.this.dismiss();
                    InputStickerSet tL_inputStickerSetID = new TL_inputStickerSetID();
                    tL_inputStickerSetID.access_hash = stickerSetCovered.set.access_hash;
                    tL_inputStickerSetID.id = stickerSetCovered.set.id;
                    new StickersAlert(StickersAlert.this.parentActivity, StickersAlert.this.parentFragment, tL_inputStickerSetID, null, null).show();
                }
            } else {
                if (StickersAlert.this.stickerSet != null && i >= 0) {
                    if (i < StickersAlert.this.stickerSet.documents.size()) {
                        FileLocation fileLocation;
                        ImageView access$3400;
                        ImageReceiver imageReceiver;
                        TLObject access$3000;
                        LayoutParams layoutParams;
                        StickersAlert.this.selectedSticker = (Document) StickersAlert.this.stickerSet.documents.get(i);
                        i = 0;
                        while (i < StickersAlert.this.selectedSticker.attributes.size()) {
                            DocumentAttribute documentAttribute = (DocumentAttribute) StickersAlert.this.selectedSticker.attributes.get(i);
                            if (documentAttribute instanceof TL_documentAttributeSticker) {
                                if (documentAttribute.alt != 0 && documentAttribute.alt.length() > 0) {
                                    StickersAlert.this.stickerEmojiTextView.setText(Emoji.replaceEmoji(documentAttribute.alt, StickersAlert.this.stickerEmojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(30.0f), false));
                                    i = 1;
                                    if (i == 0) {
                                        StickersAlert.this.stickerEmojiTextView.setText(Emoji.replaceEmoji(DataQuery.getInstance(StickersAlert.this.currentAccount).getEmojiForSticker(StickersAlert.this.selectedSticker.id), StickersAlert.this.stickerEmojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(30.0f), false));
                                    }
                                    i = DataQuery.getInstance(StickersAlert.this.currentAccount).isStickerInFavorites(StickersAlert.this.selectedSticker);
                                    StickersAlert.this.previewFavButton.setImageResource(i != 0 ? C0446R.drawable.stickers_unfavorite : C0446R.drawable.stickers_favorite);
                                    fileLocation = null;
                                    StickersAlert.this.previewFavButton.setTag(i != 0 ? Integer.valueOf(1) : null);
                                    if (StickersAlert.this.previewFavButton.getVisibility() != 8) {
                                        access$3400 = StickersAlert.this.previewFavButton;
                                        if (i == 0) {
                                            if (DataQuery.getInstance(StickersAlert.this.currentAccount).canAddStickerToFavorites() != 0) {
                                                i = 4;
                                                access$3400.setVisibility(i);
                                            }
                                        }
                                        i = 0;
                                        access$3400.setVisibility(i);
                                    }
                                    imageReceiver = StickersAlert.this.stickerImageView.getImageReceiver();
                                    access$3000 = StickersAlert.this.selectedSticker;
                                    if (StickersAlert.this.selectedSticker.thumb != 0) {
                                        fileLocation = StickersAlert.this.selectedSticker.thumb.location;
                                    }
                                    imageReceiver.setImage(access$3000, null, fileLocation, null, "webp", 1);
                                    layoutParams = (LayoutParams) StickersAlert.this.stickerPreviewLayout.getLayoutParams();
                                    layoutParams.topMargin = StickersAlert.this.scrollOffsetY;
                                    StickersAlert.this.stickerPreviewLayout.setLayoutParams(layoutParams);
                                    StickersAlert.this.stickerPreviewLayout.setVisibility(0);
                                    i = new AnimatorSet();
                                    i.playTogether(new Animator[]{ObjectAnimator.ofFloat(StickersAlert.this.stickerPreviewLayout, "alpha", new float[]{0.0f, 1.0f})});
                                    i.setDuration(200);
                                    i.start();
                                }
                                i = 0;
                                if (i == 0) {
                                    StickersAlert.this.stickerEmojiTextView.setText(Emoji.replaceEmoji(DataQuery.getInstance(StickersAlert.this.currentAccount).getEmojiForSticker(StickersAlert.this.selectedSticker.id), StickersAlert.this.stickerEmojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(30.0f), false));
                                }
                                i = DataQuery.getInstance(StickersAlert.this.currentAccount).isStickerInFavorites(StickersAlert.this.selectedSticker);
                                if (i != 0) {
                                }
                                StickersAlert.this.previewFavButton.setImageResource(i != 0 ? C0446R.drawable.stickers_unfavorite : C0446R.drawable.stickers_favorite);
                                fileLocation = null;
                                if (i != 0) {
                                }
                                StickersAlert.this.previewFavButton.setTag(i != 0 ? Integer.valueOf(1) : null);
                                if (StickersAlert.this.previewFavButton.getVisibility() != 8) {
                                    access$3400 = StickersAlert.this.previewFavButton;
                                    if (i == 0) {
                                        if (DataQuery.getInstance(StickersAlert.this.currentAccount).canAddStickerToFavorites() != 0) {
                                            i = 4;
                                            access$3400.setVisibility(i);
                                        }
                                    }
                                    i = 0;
                                    access$3400.setVisibility(i);
                                }
                                imageReceiver = StickersAlert.this.stickerImageView.getImageReceiver();
                                access$3000 = StickersAlert.this.selectedSticker;
                                if (StickersAlert.this.selectedSticker.thumb != 0) {
                                    fileLocation = StickersAlert.this.selectedSticker.thumb.location;
                                }
                                imageReceiver.setImage(access$3000, null, fileLocation, null, "webp", 1);
                                layoutParams = (LayoutParams) StickersAlert.this.stickerPreviewLayout.getLayoutParams();
                                layoutParams.topMargin = StickersAlert.this.scrollOffsetY;
                                StickersAlert.this.stickerPreviewLayout.setLayoutParams(layoutParams);
                                StickersAlert.this.stickerPreviewLayout.setVisibility(0);
                                i = new AnimatorSet();
                                i.playTogether(new Animator[]{ObjectAnimator.ofFloat(StickersAlert.this.stickerPreviewLayout, "alpha", new float[]{0.0f, 1.0f})});
                                i.setDuration(200);
                                i.start();
                            } else {
                                i++;
                            }
                        }
                        i = 0;
                        if (i == 0) {
                            StickersAlert.this.stickerEmojiTextView.setText(Emoji.replaceEmoji(DataQuery.getInstance(StickersAlert.this.currentAccount).getEmojiForSticker(StickersAlert.this.selectedSticker.id), StickersAlert.this.stickerEmojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(30.0f), false));
                        }
                        i = DataQuery.getInstance(StickersAlert.this.currentAccount).isStickerInFavorites(StickersAlert.this.selectedSticker);
                        if (i != 0) {
                        }
                        StickersAlert.this.previewFavButton.setImageResource(i != 0 ? C0446R.drawable.stickers_unfavorite : C0446R.drawable.stickers_favorite);
                        fileLocation = null;
                        if (i != 0) {
                        }
                        StickersAlert.this.previewFavButton.setTag(i != 0 ? Integer.valueOf(1) : null);
                        if (StickersAlert.this.previewFavButton.getVisibility() != 8) {
                            access$3400 = StickersAlert.this.previewFavButton;
                            if (i == 0) {
                                if (DataQuery.getInstance(StickersAlert.this.currentAccount).canAddStickerToFavorites() != 0) {
                                    i = 4;
                                    access$3400.setVisibility(i);
                                }
                            }
                            i = 0;
                            access$3400.setVisibility(i);
                        }
                        imageReceiver = StickersAlert.this.stickerImageView.getImageReceiver();
                        access$3000 = StickersAlert.this.selectedSticker;
                        if (StickersAlert.this.selectedSticker.thumb != 0) {
                            fileLocation = StickersAlert.this.selectedSticker.thumb.location;
                        }
                        imageReceiver.setImage(access$3000, null, fileLocation, null, "webp", 1);
                        layoutParams = (LayoutParams) StickersAlert.this.stickerPreviewLayout.getLayoutParams();
                        layoutParams.topMargin = StickersAlert.this.scrollOffsetY;
                        StickersAlert.this.stickerPreviewLayout.setLayoutParams(layoutParams);
                        StickersAlert.this.stickerPreviewLayout.setVisibility(0);
                        i = new AnimatorSet();
                        i.playTogether(new Animator[]{ObjectAnimator.ofFloat(StickersAlert.this.stickerPreviewLayout, "alpha", new float[]{0.0f, 1.0f})});
                        i.setDuration(200);
                        i.start();
                    }
                }
            }
        }
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
            i = this.cache.get(i);
            if (i == 0) {
                return 1;
            }
            if ((i instanceof Document) != 0) {
                return 0;
            }
            return 2;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    viewGroup = new StickerEmojiCell(this.context) {
                        public void onMeasure(int i, int i2) {
                            super.onMeasure(MeasureSpec.makeMeasureSpec(StickersAlert.this.itemSize, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                        }
                    };
                    break;
                case 1:
                    viewGroup = new EmptyCell(this.context);
                    break;
                case 2:
                    viewGroup = new FeaturedStickerSetInfoCell(this.context, 8);
                    break;
                default:
                    viewGroup = null;
                    break;
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (StickersAlert.this.stickerSetCovereds != null) {
                switch (viewHolder.getItemViewType()) {
                    case 0:
                        ((StickerEmojiCell) viewHolder.itemView).setSticker((Document) this.cache.get(i), false);
                        return;
                    case 1:
                        ((EmptyCell) viewHolder.itemView).setHeight(AndroidUtilities.dp(NUM));
                        return;
                    case 2:
                        ((FeaturedStickerSetInfoCell) viewHolder.itemView).setStickerSet((StickerSetCovered) StickersAlert.this.stickerSetCovereds.get(((Integer) this.cache.get(i)).intValue()), false);
                        return;
                    default:
                        return;
                }
            }
            ((StickerEmojiCell) viewHolder.itemView).setSticker((Document) StickersAlert.this.stickerSet.documents.get(i), StickersAlert.this.showEmoji);
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
                        this.stickersRowCount = (int) (((double) this.stickersRowCount) + Math.ceil((double) (((float) StickersAlert.this.stickerSetCovereds.size()) / ((float) this.stickersPerRow))));
                        this.positionsToSets.put(this.totalItems, stickerSetCovered);
                        SparseArray sparseArray = this.cache;
                        int i2 = this.totalItems;
                        this.totalItems = i2 + 1;
                        sparseArray.put(i2, Integer.valueOf(measuredWidth));
                        int i3 = this.totalItems / this.stickersPerRow;
                        if (stickerSetCovered.covers.isEmpty()) {
                            this.cache.put(this.totalItems, stickerSetCovered.cover);
                            i3 = 1;
                        } else {
                            i3 = (int) Math.ceil((double) (((float) stickerSetCovered.covers.size()) / ((float) this.stickersPerRow)));
                            for (i2 = 0; i2 < stickerSetCovered.covers.size(); i2++) {
                                this.cache.put(this.totalItems + i2, stickerSetCovered.covers.get(i2));
                            }
                        }
                        for (i2 = 0; i2 < this.stickersPerRow * i3; i2++) {
                            this.positionsToSets.put(this.totalItems + i2, stickerSetCovered);
                        }
                        this.totalItems += i3 * this.stickersPerRow;
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

    protected boolean canDismissWithSwipe() {
        return false;
    }

    public StickersAlert(Context context, Photo photo) {
        super(context, false);
        this.shadowAnimation = new AnimatorSet[2];
        this.shadow = new View[2];
        this.parentActivity = (Activity) context;
        final TLObject tL_messages_getAttachedStickers = new TL_messages_getAttachedStickers();
        InputStickeredMedia tL_inputStickeredMediaPhoto = new TL_inputStickeredMediaPhoto();
        tL_inputStickeredMediaPhoto.id = new TL_inputPhoto();
        tL_inputStickeredMediaPhoto.id.id = photo.id;
        tL_inputStickeredMediaPhoto.id.access_hash = photo.access_hash;
        tL_messages_getAttachedStickers.media = tL_inputStickeredMediaPhoto;
        this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getAttachedStickers, new RequestDelegate() {
            public void run(final TLObject tLObject, final TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        StickersAlert.this.reqId = 0;
                        if (tL_error == null) {
                            Vector vector = (Vector) tLObject;
                            if (vector.objects.isEmpty()) {
                                StickersAlert.this.dismiss();
                                return;
                            } else if (vector.objects.size() == 1) {
                                StickerSetCovered stickerSetCovered = (StickerSetCovered) vector.objects.get(0);
                                StickersAlert.this.inputStickerSet = new TL_inputStickerSetID();
                                StickersAlert.this.inputStickerSet.id = stickerSetCovered.set.id;
                                StickersAlert.this.inputStickerSet.access_hash = stickerSetCovered.set.access_hash;
                                StickersAlert.this.loadStickerSet();
                                return;
                            } else {
                                StickersAlert.this.stickerSetCovereds = new ArrayList();
                                for (int i = 0; i < vector.objects.size(); i++) {
                                    StickersAlert.this.stickerSetCovereds.add((StickerSetCovered) vector.objects.get(i));
                                }
                                StickersAlert.this.gridView.setLayoutParams(LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
                                StickersAlert.this.titleTextView.setVisibility(8);
                                StickersAlert.this.shadow[0].setVisibility(8);
                                StickersAlert.this.adapter.notifyDataSetChanged();
                                return;
                            }
                        }
                        AlertsCreator.processError(StickersAlert.this.currentAccount, tL_error, StickersAlert.this.parentFragment, tL_messages_getAttachedStickers, new Object[0]);
                        StickersAlert.this.dismiss();
                    }
                });
            }
        });
        init(context);
    }

    public StickersAlert(Context context, BaseFragment baseFragment, InputStickerSet inputStickerSet, TL_messages_stickerSet tL_messages_stickerSet, StickersAlertDelegate stickersAlertDelegate) {
        super(context, false);
        this.shadowAnimation = new AnimatorSet[2];
        this.shadow = new View[2];
        this.delegate = stickersAlertDelegate;
        this.inputStickerSet = inputStickerSet;
        this.stickerSet = tL_messages_stickerSet;
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
                TLObject tL_messages_getStickerSet = new TL_messages_getStickerSet();
                tL_messages_getStickerSet.stickerset = this.inputStickerSet;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getStickerSet, new C20932());
            } else if (this.adapter != null) {
                updateSendButton();
                updateFields();
                this.adapter.notifyDataSetChanged();
            }
        }
        if (this.stickerSet != null) {
            this.showEmoji = this.stickerSet.set.masks ^ 1;
        }
    }

    private void init(Context context) {
        Context context2 = context;
        this.shadowDrawable = context.getResources().getDrawable(C0446R.drawable.sheet_shadow).mutate();
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), Mode.MULTIPLY));
        this.containerView = new FrameLayout(context2) {
            private int lastNotifyWidth;

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || StickersAlert.this.scrollOffsetY == 0 || motionEvent.getY() >= ((float) StickersAlert.this.scrollOffsetY)) {
                    return super.onInterceptTouchEvent(motionEvent);
                }
                StickersAlert.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                return (StickersAlert.this.isDismissed() || super.onTouchEvent(motionEvent) == null) ? null : true;
            }

            protected void onMeasure(int i, int i2) {
                int dp;
                i2 = MeasureSpec.getSize(i2);
                if (VERSION.SDK_INT >= 21) {
                    i2 -= AndroidUtilities.statusBarHeight;
                }
                getMeasuredWidth();
                StickersAlert.this.itemSize = (MeasureSpec.getSize(i) - AndroidUtilities.dp(36.0f)) / 5;
                if (StickersAlert.this.stickerSetCovereds != null) {
                    dp = (AndroidUtilities.dp(56.0f) + (AndroidUtilities.dp(60.0f) * StickersAlert.this.stickerSetCovereds.size())) + (StickersAlert.this.adapter.stickersRowCount * AndroidUtilities.dp(82.0f));
                } else {
                    dp = (AndroidUtilities.dp(96.0f) + (Math.max(3, StickersAlert.this.stickerSet != null ? (int) Math.ceil((double) (((float) StickersAlert.this.stickerSet.documents.size()) / 5.0f)) : 0) * AndroidUtilities.dp(82.0f))) + StickersAlert.backgroundPaddingTop;
                }
                int i3 = i2 / 5;
                i3 = ((double) dp) < ((double) i3) * 3.2d ? 0 : i3 * 2;
                if (i3 != 0 && dp < i2) {
                    i3 -= i2 - dp;
                }
                if (i3 == 0) {
                    i3 = StickersAlert.backgroundPaddingTop;
                }
                if (StickersAlert.this.stickerSetCovereds != null) {
                    i3 += AndroidUtilities.dp(8.0f);
                }
                if (StickersAlert.this.gridView.getPaddingTop() != i3) {
                    StickersAlert.this.ignoreLayout = true;
                    StickersAlert.this.gridView.setPadding(AndroidUtilities.dp(10.0f), i3, AndroidUtilities.dp(10.0f), 0);
                    StickersAlert.this.emptyView.setPadding(0, i3, 0, 0);
                    StickersAlert.this.ignoreLayout = false;
                }
                super.onMeasure(i, MeasureSpec.makeMeasureSpec(Math.min(dp, i2), NUM));
            }

            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
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

            protected void onDraw(Canvas canvas) {
                StickersAlert.this.shadowDrawable.setBounds(0, StickersAlert.this.scrollOffsetY - StickersAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
                StickersAlert.this.shadowDrawable.draw(canvas);
            }
        };
        this.containerView.setWillNotDraw(false);
        this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
        this.shadow[0] = new View(context2);
        this.shadow[0].setBackgroundResource(C0446R.drawable.header_shadow);
        this.shadow[0].setAlpha(0.0f);
        this.shadow[0].setVisibility(4);
        this.shadow[0].setTag(Integer.valueOf(1));
        this.containerView.addView(this.shadow[0], LayoutHelper.createFrame(-1, 3.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        this.gridView = new RecyclerListView(context2) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                boolean onInterceptTouchEvent = StickerPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, StickersAlert.this.gridView, 0, null);
                if (super.onInterceptTouchEvent(motionEvent) != null || onInterceptTouchEvent) {
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
        this.layoutManager.setSpanSizeLookup(new C20945());
        recyclerListView = this.gridView;
        Adapter gridAdapter = new GridAdapter(context2);
        this.adapter = gridAdapter;
        recyclerListView.setAdapter(gridAdapter);
        this.gridView.setVerticalScrollBarEnabled(false);
        this.gridView.addItemDecoration(new C20956());
        this.gridView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        this.gridView.setClipToPadding(false);
        this.gridView.setEnabled(true);
        this.gridView.setGlowColor(Theme.getColor(Theme.key_dialogScrollGlow));
        this.gridView.setOnTouchListener(new C13157());
        this.gridView.setOnScrollListener(new C20968());
        this.stickersOnItemClickListener = new C20979();
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
        this.emptyView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        this.titleTextView = new TextView(context2);
        this.titleTextView.setLines(1);
        this.titleTextView.setSingleLine(true);
        this.titleTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.titleTextView.setTextSize(1, 20.0f);
        this.titleTextView.setLinkTextColor(Theme.getColor(Theme.key_dialogTextLink));
        this.titleTextView.setHighlightColor(Theme.getColor(Theme.key_dialogLinkSelection));
        this.titleTextView.setEllipsize(TruncateAt.MIDDLE);
        this.titleTextView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.titleTextView.setGravity(16);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextView.setMovementMethod(new LinkMovementMethodMy());
        this.containerView.addView(this.titleTextView, LayoutHelper.createLinear(-1, 48));
        this.emptyView.addView(new RadialProgressView(context2), LayoutHelper.createFrame(-2, -2, 17));
        this.shadow[1] = new View(context2);
        this.shadow[1].setBackgroundResource(C0446R.drawable.header_shadow_reverse);
        this.containerView.addView(this.shadow[1], LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        this.pickerBottomLayout = new PickerBottomLayout(context2, false);
        this.pickerBottomLayout.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        this.containerView.addView(this.pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 83));
        this.pickerBottomLayout.cancelButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.pickerBottomLayout.cancelButton.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        this.pickerBottomLayout.cancelButton.setText(LocaleController.getString("Close", C0446R.string.Close).toUpperCase());
        this.pickerBottomLayout.cancelButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                StickersAlert.this.dismiss();
            }
        });
        this.pickerBottomLayout.doneButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.pickerBottomLayout.doneButtonBadgeTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(12.5f), Theme.getColor(Theme.key_dialogBadgeBackground)));
        this.stickerPreviewLayout = new FrameLayout(context2);
        this.stickerPreviewLayout.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground) & -536870913);
        this.stickerPreviewLayout.setVisibility(8);
        this.stickerPreviewLayout.setSoundEffectsEnabled(false);
        this.containerView.addView(this.stickerPreviewLayout, LayoutHelper.createFrame(-1, -1.0f));
        this.stickerPreviewLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                StickersAlert.this.hidePreview();
            }
        });
        View imageView = new ImageView(context2);
        imageView.setImageResource(C0446R.drawable.msg_panel_clear);
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogTextGray3), Mode.MULTIPLY));
        imageView.setScaleType(ScaleType.CENTER);
        this.stickerPreviewLayout.addView(imageView, LayoutHelper.createFrame(48, 48, 53));
        imageView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                StickersAlert.this.hidePreview();
            }
        });
        this.stickerImageView = new BackupImageView(context2);
        this.stickerImageView.setAspectFit(true);
        this.stickerPreviewLayout.addView(this.stickerImageView);
        this.stickerEmojiTextView = new TextView(context2);
        this.stickerEmojiTextView.setTextSize(1, 30.0f);
        this.stickerEmojiTextView.setGravity(85);
        this.stickerPreviewLayout.addView(this.stickerEmojiTextView);
        this.previewSendButton = new TextView(context2);
        this.previewSendButton.setTextSize(1, 14.0f);
        this.previewSendButton.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        this.previewSendButton.setGravity(17);
        this.previewSendButton.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        this.previewSendButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
        this.previewSendButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.stickerPreviewLayout.addView(this.previewSendButton, LayoutHelper.createFrame(-1, 48, 83));
        this.previewSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                StickersAlert.this.delegate.onStickerSelected(StickersAlert.this.selectedSticker);
                StickersAlert.this.dismiss();
            }
        });
        this.previewFavButton = new ImageView(context2);
        this.previewFavButton.setScaleType(ScaleType.CENTER);
        this.stickerPreviewLayout.addView(this.previewFavButton, LayoutHelper.createFrame(48, 48.0f, 85, 0.0f, 0.0f, 4.0f, 0.0f));
        this.previewFavButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogIcon), Mode.MULTIPLY));
        this.previewFavButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                DataQuery.getInstance(StickersAlert.this.currentAccount).addRecentSticker(2, StickersAlert.this.selectedSticker, (int) (System.currentTimeMillis() / 1000), StickersAlert.this.previewFavButton.getTag() != null);
                if (StickersAlert.this.previewFavButton.getTag() == null) {
                    StickersAlert.this.previewFavButton.setTag(Integer.valueOf(1));
                    StickersAlert.this.previewFavButton.setImageResource(C0446R.drawable.stickers_unfavorite);
                    return;
                }
                StickersAlert.this.previewFavButton.setTag(null);
                StickersAlert.this.previewFavButton.setImageResource(C0446R.drawable.stickers_favorite);
            }
        });
        this.previewSendButtonShadow = new View(context2);
        this.previewSendButtonShadow.setBackgroundResource(C0446R.drawable.header_shadow_reverse);
        this.stickerPreviewLayout.addView(this.previewSendButtonShadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
        updateFields();
        updateSendButton();
        this.adapter.notifyDataSetChanged();
    }

    private void updateSendButton() {
        int min = (int) (((float) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / 2)) / AndroidUtilities.density);
        if (this.delegate == null || (this.stickerSet != null && this.stickerSet.set.masks)) {
            this.previewSendButton.setText(LocaleController.getString("Close", C0446R.string.Close).toUpperCase());
            this.stickerImageView.setLayoutParams(LayoutHelper.createFrame(min, min, 17));
            this.stickerEmojiTextView.setLayoutParams(LayoutHelper.createFrame(min, min, 17));
            this.previewSendButton.setVisibility(8);
            this.previewFavButton.setVisibility(8);
            this.previewSendButtonShadow.setVisibility(8);
            return;
        }
        this.previewSendButton.setText(LocaleController.getString("SendSticker", C0446R.string.SendSticker).toUpperCase());
        float f = (float) min;
        this.stickerImageView.setLayoutParams(LayoutHelper.createFrame(min, f, 17, 0.0f, 0.0f, 0.0f, 30.0f));
        this.stickerEmojiTextView.setLayoutParams(LayoutHelper.createFrame(min, f, 17, 0.0f, 0.0f, 0.0f, 30.0f));
        this.previewSendButton.setVisibility(0);
        this.previewFavButton.setVisibility(0);
        this.previewSendButtonShadow.setVisibility(0);
    }

    public void setInstallDelegate(StickersAlertInstallDelegate stickersAlertInstallDelegate) {
        this.installDelegate = stickersAlertInstallDelegate;
    }

    private void updateFields() {
        if (this.titleTextView != null) {
            CharSequence charSequence = null;
            if (this.stickerSet != null) {
                String str;
                int i;
                try {
                    if (this.urlPattern == null) {
                        this.urlPattern = Pattern.compile("@[a-zA-Z\\d_]{1,32}");
                    }
                    Matcher matcher = this.urlPattern.matcher(this.stickerSet.set.title);
                    while (matcher.find()) {
                        if (charSequence == null) {
                            charSequence = new SpannableStringBuilder(this.stickerSet.set.title);
                        }
                        int start = matcher.start();
                        int end = matcher.end();
                        if (this.stickerSet.set.title.charAt(start) != '@') {
                            start++;
                        }
                        AnonymousClass17 anonymousClass17 = new URLSpanNoUnderline(this.stickerSet.set.title.subSequence(start + 1, end).toString()) {
                            public void onClick(View view) {
                                MessagesController.getInstance(StickersAlert.this.currentAccount).openByUserName(getURL(), StickersAlert.this.parentFragment, 1);
                                StickersAlert.this.dismiss();
                            }
                        };
                        if (anonymousClass17 != null) {
                            charSequence.setSpan(anonymousClass17, start, end, 0);
                        }
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                TextView textView = this.titleTextView;
                if (charSequence == null) {
                    charSequence = this.stickerSet.set.title;
                }
                textView.setText(charSequence);
                if (this.stickerSet.set != null) {
                    if (DataQuery.getInstance(this.currentAccount).isStickerPackInstalled(this.stickerSet.set.id)) {
                        if (this.stickerSet.set.official) {
                            setRightButton(new OnClickListener() {
                                public void onClick(View view) {
                                    if (StickersAlert.this.installDelegate != null) {
                                        StickersAlert.this.installDelegate.onStickerSetUninstalled();
                                    }
                                    StickersAlert.this.dismiss();
                                    DataQuery.getInstance(StickersAlert.this.currentAccount).removeStickersSet(StickersAlert.this.getContext(), StickersAlert.this.stickerSet.set, 1, StickersAlert.this.parentFragment, true);
                                }
                            }, LocaleController.getString("StickersRemove", C0446R.string.StickersHide), Theme.getColor(Theme.key_dialogTextRed), false);
                        } else {
                            setRightButton(new OnClickListener() {
                                public void onClick(View view) {
                                    if (StickersAlert.this.installDelegate != null) {
                                        StickersAlert.this.installDelegate.onStickerSetUninstalled();
                                    }
                                    StickersAlert.this.dismiss();
                                    DataQuery.getInstance(StickersAlert.this.currentAccount).removeStickersSet(StickersAlert.this.getContext(), StickersAlert.this.stickerSet.set, 0, StickersAlert.this.parentFragment, true);
                                }
                            }, LocaleController.getString("StickersRemove", C0446R.string.StickersRemove), Theme.getColor(Theme.key_dialogTextRed), false);
                        }
                        this.adapter.notifyDataSetChanged();
                    }
                }
                OnClickListener anonymousClass18 = new OnClickListener() {

                    /* renamed from: org.telegram.ui.Components.StickersAlert$18$1 */
                    class C20911 implements RequestDelegate {
                        C20911() {
                        }

                        public void run(final TLObject tLObject, final TL_error tL_error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    try {
                                        if (tL_error == null) {
                                            if (StickersAlert.this.stickerSet.set.masks) {
                                                Toast.makeText(StickersAlert.this.getContext(), LocaleController.getString("AddMasksInstalled", C0446R.string.AddMasksInstalled), 0).show();
                                            } else {
                                                Toast.makeText(StickersAlert.this.getContext(), LocaleController.getString("AddStickersInstalled", C0446R.string.AddStickersInstalled), 0).show();
                                            }
                                            if (tLObject instanceof TL_messages_stickerSetInstallResultArchive) {
                                                NotificationCenter.getInstance(StickersAlert.this.currentAccount).postNotificationName(NotificationCenter.needReloadArchivedStickers, new Object[0]);
                                                if (!(StickersAlert.this.parentFragment == null || StickersAlert.this.parentFragment.getParentActivity() == null)) {
                                                    StickersAlert.this.parentFragment.showDialog(new StickersArchiveAlert(StickersAlert.this.parentFragment.getParentActivity(), StickersAlert.this.parentFragment, ((TL_messages_stickerSetInstallResultArchive) tLObject).sets).create());
                                                }
                                            }
                                        } else {
                                            Toast.makeText(StickersAlert.this.getContext(), LocaleController.getString("ErrorOccurred", C0446R.string.ErrorOccurred), 0).show();
                                        }
                                    } catch (Throwable e) {
                                        FileLog.m3e(e);
                                    }
                                    DataQuery.getInstance(StickersAlert.this.currentAccount).loadStickers(StickersAlert.this.stickerSet.set.masks, false, true);
                                }
                            });
                        }
                    }

                    public void onClick(View view) {
                        StickersAlert.this.dismiss();
                        if (StickersAlert.this.installDelegate != null) {
                            StickersAlert.this.installDelegate.onStickerSetInstalled();
                        }
                        view = new TL_messages_installStickerSet();
                        view.stickerset = StickersAlert.this.inputStickerSet;
                        ConnectionsManager.getInstance(StickersAlert.this.currentAccount).sendRequest(view, new C20911());
                    }
                };
                if (this.stickerSet == null || !this.stickerSet.set.masks) {
                    str = "AddStickers";
                    i = C0446R.string.AddStickers;
                } else {
                    str = "AddMasks";
                    i = C0446R.string.AddMasks;
                }
                setRightButton(anonymousClass18, LocaleController.getString(str, i), Theme.getColor(Theme.key_dialogTextBlue2), true);
                this.adapter.notifyDataSetChanged();
            } else {
                setRightButton(null, null, Theme.getColor(Theme.key_dialogTextRed), false);
            }
        }
    }

    @SuppressLint({"NewApi"})
    private void updateLayout() {
        if (this.gridView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.gridView;
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
                if (this.shadowAnimation[i] != null) {
                    this.shadowAnimation[i].cancel();
                }
                this.shadowAnimation[i] = new AnimatorSet();
                AnimatorSet animatorSet = this.shadowAnimation[i];
                Animator[] animatorArr = new Animator[1];
                Object obj = this.shadow[i];
                String str = "alpha";
                float[] fArr = new float[1];
                fArr[0] = z ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(obj, str, fArr);
                animatorSet.playTogether(animatorArr);
                this.shadowAnimation[i].setDuration(150);
                this.shadowAnimation[i].addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (StickersAlert.this.shadowAnimation[i] != null && StickersAlert.this.shadowAnimation[i].equals(animator) != null) {
                            if (z == null) {
                                StickersAlert.this.shadow[i].setVisibility(4);
                            }
                            StickersAlert.this.shadowAnimation[i] = null;
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (StickersAlert.this.shadowAnimation[i] != null && StickersAlert.this.shadowAnimation[i].equals(animator) != null) {
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
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.emojiDidLoaded) {
            if (this.gridView != 0) {
                i = this.gridView.getChildCount();
                for (i2 = 0; i2 < i; i2++) {
                    this.gridView.getChildAt(i2).invalidate();
                }
            }
            if (StickerPreviewViewer.getInstance().isVisible() != 0) {
                StickerPreviewViewer.getInstance().close();
            }
            StickerPreviewViewer.getInstance().reset();
        }
    }

    private void setRightButton(OnClickListener onClickListener, String str, int i, boolean z) {
        if (str == null) {
            this.pickerBottomLayout.doneButton.setVisibility(8);
            return;
        }
        this.pickerBottomLayout.doneButton.setVisibility(0);
        if (z) {
            this.pickerBottomLayout.doneButtonBadgeTextView.setVisibility(0);
            this.pickerBottomLayout.doneButtonBadgeTextView.setText(String.format("%d", new Object[]{Integer.valueOf(this.stickerSet.documents.size())}));
        } else {
            this.pickerBottomLayout.doneButtonBadgeTextView.setVisibility(8);
        }
        this.pickerBottomLayout.doneButtonTextView.setTextColor(i);
        this.pickerBottomLayout.doneButtonTextView.setText(str.toUpperCase());
        this.pickerBottomLayout.doneButton.setOnClickListener(onClickListener);
    }
}
