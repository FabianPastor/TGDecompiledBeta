package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.math.MathUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC$TL_messages_installStickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSetInstallResultArchive;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.Premium.PremiumButtonView;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PremiumPreviewFragment;

public class EmojiPacksAlert extends BottomSheet {
    /* access modifiers changed from: private */
    public LongSparseArray<AnimatedEmojiDrawable> animatedEmojiDrawables;
    private TextView buttonView;
    /* access modifiers changed from: private */
    public EmojiPacksLoader customEmojiPacks;
    private BaseFragment fragment;
    /* access modifiers changed from: private */
    public GridLayoutManager gridLayoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public View paddingView;
    private PremiumButtonView premiumButtonView;
    /* access modifiers changed from: private */
    public View shadowView;

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public EmojiPacksAlert(BaseFragment baseFragment, Context context, Theme.ResourcesProvider resourcesProvider, ArrayList<TLRPC$InputStickerSet> arrayList) {
        super(context, false, resourcesProvider);
        boolean z = arrayList.size() <= 1;
        this.fragment = baseFragment;
        fixNavigationBar();
        this.customEmojiPacks = new EmojiPacksLoader(this.currentAccount, arrayList) {
            /* access modifiers changed from: protected */
            public void onUpdate() {
                if (!(EmojiPacksAlert.this.listView == null || EmojiPacksAlert.this.listView.getAdapter() == null)) {
                    EmojiPacksAlert.this.listView.getAdapter().notifyDataSetChanged();
                }
                EmojiPacksAlert.this.updateButton();
            }
        };
        this.containerView = new FrameLayout(context) {
            boolean attached;
            private Boolean lastOpen = null;
            ArrayList<DrawingInBackgroundLine> lineDrawables = new ArrayList<>();
            ArrayList<DrawingInBackgroundLine> lineDrawablesTmp = new ArrayList<>();
            private Paint paint = new Paint();
            private Path path = new Path();
            ArrayList<ArrayList<EmojiImageView>> unusedArrays = new ArrayList<>();
            ArrayList<DrawingInBackgroundLine> unusedLineDrawables = new ArrayList<>();
            SparseArray<ArrayList<EmojiImageView>> viewsGroupedByLines = new SparseArray<>();

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                DrawingInBackgroundLine drawingInBackgroundLine;
                Canvas canvas2 = canvas;
                if (this.attached) {
                    this.paint.setColor(EmojiPacksAlert.this.getThemedColor("dialogBackground"));
                    this.paint.setShadowLayer((float) AndroidUtilities.dp(2.0f), 0.0f, (float) AndroidUtilities.dp(-0.66f), NUM);
                    this.path.reset();
                    float access$300 = (float) EmojiPacksAlert.this.getTop();
                    float clamp = 1.0f - MathUtils.clamp((access$300 - ((float) EmojiPacksAlert.this.containerView.getPaddingTop())) / ((float) AndroidUtilities.dp(32.0f)), 0.0f, 1.0f);
                    float paddingTop = access$300 - (((float) EmojiPacksAlert.this.containerView.getPaddingTop()) * clamp);
                    float dp = (float) AndroidUtilities.dp((1.0f - clamp) * 14.0f);
                    RectF rectF = AndroidUtilities.rectTmp;
                    rectF.set((float) getPaddingLeft(), paddingTop, (float) (getWidth() - getPaddingRight()), ((float) getBottom()) + dp);
                    this.path.addRoundRect(rectF, dp, dp, Path.Direction.CW);
                    canvas2.drawPath(this.path, this.paint);
                    boolean z = clamp > 0.75f;
                    Boolean bool = this.lastOpen;
                    if (bool == null || z != bool.booleanValue()) {
                        EmojiPacksAlert emojiPacksAlert = EmojiPacksAlert.this;
                        Boolean valueOf = Boolean.valueOf(z);
                        this.lastOpen = valueOf;
                        emojiPacksAlert.updateLightStatusBar(valueOf.booleanValue());
                    }
                    Theme.dialogs_onlineCirclePaint.setColor(EmojiPacksAlert.this.getThemedColor("key_sheet_scrollUp"));
                    Theme.dialogs_onlineCirclePaint.setAlpha((int) (MathUtils.clamp(paddingTop / ((float) AndroidUtilities.dp(20.0f)), 0.0f, 1.0f) * ((float) Theme.dialogs_onlineCirclePaint.getAlpha())));
                    int dp2 = AndroidUtilities.dp(36.0f);
                    float dp3 = paddingTop + ((float) AndroidUtilities.dp(10.0f));
                    rectF.set((float) ((getMeasuredWidth() - dp2) / 2), dp3, (float) ((getMeasuredWidth() + dp2) / 2), ((float) AndroidUtilities.dp(4.0f)) + dp3);
                    canvas2.drawRoundRect(rectF, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                    EmojiPacksAlert.this.shadowView.setVisibility(EmojiPacksAlert.this.listView.canScrollVertically(1) ? 0 : 4);
                    if (EmojiPacksAlert.this.listView != null) {
                        canvas.save();
                        canvas2.translate((float) EmojiPacksAlert.this.listView.getLeft(), (float) EmojiPacksAlert.this.listView.getTop());
                        canvas2.clipRect(0, 0, EmojiPacksAlert.this.listView.getWidth(), EmojiPacksAlert.this.listView.getHeight());
                        for (int i = 0; i < this.viewsGroupedByLines.size(); i++) {
                            ArrayList valueAt = this.viewsGroupedByLines.valueAt(i);
                            valueAt.clear();
                            this.unusedArrays.add(valueAt);
                        }
                        this.viewsGroupedByLines.clear();
                        if (EmojiPacksAlert.this.listView != null) {
                            for (int i2 = 0; i2 < EmojiPacksAlert.this.listView.getChildCount(); i2++) {
                                View childAt = EmojiPacksAlert.this.listView.getChildAt(i2);
                                if (childAt instanceof EmojiImageView) {
                                    if (EmojiPacksAlert.this.animatedEmojiDrawables == null) {
                                        LongSparseArray unused = EmojiPacksAlert.this.animatedEmojiDrawables = new LongSparseArray();
                                    }
                                    AnimatedEmojiSpan animatedEmojiSpan = ((EmojiImageView) childAt).span;
                                    if (animatedEmojiSpan != null) {
                                        long documentId = animatedEmojiSpan.getDocumentId();
                                        AnimatedEmojiDrawable animatedEmojiDrawable = (AnimatedEmojiDrawable) EmojiPacksAlert.this.animatedEmojiDrawables.get(documentId);
                                        if (animatedEmojiDrawable == null) {
                                            LongSparseArray access$900 = EmojiPacksAlert.this.animatedEmojiDrawables;
                                            AnimatedEmojiDrawable make = AnimatedEmojiDrawable.make(2, documentId);
                                            access$900.put(documentId, make);
                                            animatedEmojiDrawable = make;
                                        }
                                        animatedEmojiDrawable.addView((View) this);
                                        ArrayList arrayList = this.viewsGroupedByLines.get(childAt.getTop());
                                        if (arrayList == null) {
                                            if (!this.unusedArrays.isEmpty()) {
                                                ArrayList<ArrayList<EmojiImageView>> arrayList2 = this.unusedArrays;
                                                arrayList = arrayList2.remove(arrayList2.size() - 1);
                                            } else {
                                                arrayList = new ArrayList();
                                            }
                                            this.viewsGroupedByLines.put(childAt.getTop(), arrayList);
                                        }
                                        arrayList.add((EmojiImageView) childAt);
                                    }
                                } else {
                                    canvas.save();
                                    canvas2.translate((float) childAt.getLeft(), (float) childAt.getTop());
                                    childAt.draw(canvas2);
                                    canvas.restore();
                                }
                            }
                        }
                        this.lineDrawablesTmp.clear();
                        this.lineDrawablesTmp.addAll(this.lineDrawables);
                        this.lineDrawables.clear();
                        long currentTimeMillis = System.currentTimeMillis();
                        int i3 = 0;
                        while (true) {
                            DrawingInBackgroundLine drawingInBackgroundLine2 = null;
                            if (i3 >= this.viewsGroupedByLines.size()) {
                                break;
                            }
                            ArrayList<EmojiImageView> valueAt2 = this.viewsGroupedByLines.valueAt(i3);
                            View view = valueAt2.get(0);
                            int childAdapterPosition = EmojiPacksAlert.this.listView.getChildAdapterPosition(view);
                            int i4 = 0;
                            while (true) {
                                if (i4 >= this.lineDrawablesTmp.size()) {
                                    break;
                                } else if (this.lineDrawablesTmp.get(i4).position == childAdapterPosition) {
                                    drawingInBackgroundLine2 = this.lineDrawablesTmp.get(i4);
                                    this.lineDrawablesTmp.remove(i4);
                                    break;
                                } else {
                                    i4++;
                                }
                            }
                            if (drawingInBackgroundLine == null) {
                                if (!this.unusedLineDrawables.isEmpty()) {
                                    ArrayList<DrawingInBackgroundLine> arrayList3 = this.unusedLineDrawables;
                                    drawingInBackgroundLine = arrayList3.remove(arrayList3.size() - 1);
                                } else {
                                    drawingInBackgroundLine = new DrawingInBackgroundLine();
                                }
                                drawingInBackgroundLine.position = childAdapterPosition;
                                drawingInBackgroundLine.onAttachToWindow();
                            }
                            this.lineDrawables.add(drawingInBackgroundLine);
                            drawingInBackgroundLine.imageViewEmojis = valueAt2;
                            canvas.save();
                            canvas2.translate(0.0f, view.getY() + ((float) view.getPaddingTop()));
                            drawingInBackgroundLine.draw(canvas, currentTimeMillis, getMeasuredWidth(), view.getMeasuredHeight() - view.getPaddingBottom(), 1.0f);
                            canvas.restore();
                            i3++;
                        }
                        for (int i5 = 0; i5 < this.lineDrawablesTmp.size(); i5++) {
                            if (this.unusedLineDrawables.size() < 3) {
                                this.unusedLineDrawables.add(this.lineDrawablesTmp.get(i5));
                                this.lineDrawablesTmp.get(i5).imageViewEmojis = null;
                                this.lineDrawablesTmp.get(i5).reset();
                            } else {
                                this.lineDrawablesTmp.get(i5).onDetachFromWindow();
                            }
                        }
                        this.lineDrawablesTmp.clear();
                        canvas.restore();
                    }
                    super.dispatchDraw(canvas);
                }
            }

            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0 && motionEvent.getY() < ((float) (EmojiPacksAlert.this.getTop() - AndroidUtilities.dp(6.0f)))) {
                    EmojiPacksAlert.this.dismiss();
                }
                return super.dispatchTouchEvent(motionEvent);
            }

            /* renamed from: org.telegram.ui.Components.EmojiPacksAlert$2$DrawingInBackgroundLine */
            class DrawingInBackgroundLine extends DrawingInBackgroundThreadDrawable {
                ArrayList<EmojiImageView> drawInBackgroundViews = new ArrayList<>();
                ArrayList<EmojiImageView> imageViewEmojis;
                public int position;

                DrawingInBackgroundLine() {
                }

                public void prepareDraw(long j) {
                    AnimatedEmojiDrawable animatedEmojiDrawable;
                    this.drawInBackgroundViews.clear();
                    for (int i = 0; i < this.imageViewEmojis.size(); i++) {
                        EmojiImageView emojiImageView = this.imageViewEmojis.get(i);
                        if (!(emojiImageView.span == null || (animatedEmojiDrawable = (AnimatedEmojiDrawable) EmojiPacksAlert.this.animatedEmojiDrawables.get(emojiImageView.span.getDocumentId())) == null || animatedEmojiDrawable.getImageReceiver() == null)) {
                            animatedEmojiDrawable.update(j);
                            ImageReceiver.BackgroundThreadDrawHolder drawInBackgroundThread = animatedEmojiDrawable.getImageReceiver().setDrawInBackgroundThread(emojiImageView.backgroundThreadDrawHolder);
                            emojiImageView.backgroundThreadDrawHolder = drawInBackgroundThread;
                            drawInBackgroundThread.time = j;
                            animatedEmojiDrawable.setAlpha(255);
                            Rect rect = AndroidUtilities.rectTmp2;
                            rect.set(emojiImageView.getLeft() + emojiImageView.getPaddingLeft(), emojiImageView.getPaddingTop(), emojiImageView.getRight() - emojiImageView.getPaddingRight(), emojiImageView.getMeasuredHeight() - emojiImageView.getPaddingBottom());
                            emojiImageView.backgroundThreadDrawHolder.setBounds(rect);
                            emojiImageView.imageReceiver = animatedEmojiDrawable.getImageReceiver();
                            this.drawInBackgroundViews.add(emojiImageView);
                        }
                    }
                }

                public void drawInBackground(Canvas canvas) {
                    for (int i = 0; i < this.drawInBackgroundViews.size(); i++) {
                        EmojiImageView emojiImageView = this.drawInBackgroundViews.get(i);
                        emojiImageView.imageReceiver.draw(canvas, emojiImageView.backgroundThreadDrawHolder);
                    }
                }

                /* access modifiers changed from: protected */
                public void drawInUiThread(Canvas canvas) {
                    AnimatedEmojiDrawable animatedEmojiDrawable;
                    if (this.imageViewEmojis != null) {
                        for (int i = 0; i < this.imageViewEmojis.size(); i++) {
                            EmojiImageView emojiImageView = this.imageViewEmojis.get(i);
                            if (!(emojiImageView.span == null || (animatedEmojiDrawable = (AnimatedEmojiDrawable) EmojiPacksAlert.this.animatedEmojiDrawables.get(emojiImageView.span.getDocumentId())) == null || animatedEmojiDrawable.getImageReceiver() == null || emojiImageView.imageReceiver == null)) {
                                animatedEmojiDrawable.setAlpha(255);
                                Rect rect = AndroidUtilities.rectTmp2;
                                rect.set(emojiImageView.getLeft() + emojiImageView.getPaddingLeft(), emojiImageView.getPaddingTop(), emojiImageView.getRight() - emojiImageView.getPaddingRight(), emojiImageView.getMeasuredHeight() - emojiImageView.getPaddingBottom());
                                animatedEmojiDrawable.getImageReceiver().setImageCoords(rect);
                                animatedEmojiDrawable.getImageReceiver().draw(canvas);
                            }
                        }
                    }
                }

                public void onFrameReady() {
                    super.onFrameReady();
                    for (int i = 0; i < this.drawInBackgroundViews.size(); i++) {
                        this.drawInBackgroundViews.get(i).backgroundThreadDrawHolder.release();
                    }
                    EmojiPacksAlert.this.containerView.invalidate();
                }
            }

            /* access modifiers changed from: protected */
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                this.attached = true;
            }

            /* access modifiers changed from: protected */
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                this.attached = false;
                for (int i = 0; i < this.lineDrawables.size(); i++) {
                    this.lineDrawables.get(i).onDetachFromWindow();
                }
                for (int i2 = 0; i2 < this.unusedLineDrawables.size(); i2++) {
                    this.unusedLineDrawables.get(i2).onDetachFromWindow();
                }
                this.lineDrawables.clear();
            }
        };
        this.paddingView = new View(this, context) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec((int) (((float) AndroidUtilities.displaySize.y) * 0.4f), NUM));
            }
        };
        this.listView = new RecyclerListView(context) {
            public boolean drawChild(Canvas canvas, View view, long j) {
                return false;
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                EmojiPacksAlert.this.gridLayoutManager.setSpanCount(Math.max(1, View.MeasureSpec.getSize(i) / AndroidUtilities.dp(AndroidUtilities.isTablet() ? 60.0f : 45.0f)));
                super.onMeasure(i, i2);
            }

            public void onScrolled(int i, int i2) {
                super.onScrolled(i, i2);
                EmojiPacksAlert.this.containerView.invalidate();
            }

            /* access modifiers changed from: protected */
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                AnimatedEmojiSpan.release((View) EmojiPacksAlert.this.containerView, (LongSparseArray<AnimatedEmojiDrawable>) EmojiPacksAlert.this.animatedEmojiDrawables);
            }
        };
        ViewGroup viewGroup = this.containerView;
        int i = this.backgroundPaddingLeft;
        viewGroup.setPadding(i, AndroidUtilities.statusBarHeight, i, 0);
        this.containerView.setClipChildren(false);
        this.containerView.setClipToPadding(false);
        this.containerView.setWillNotDraw(false);
        this.listView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), z ? 0 : AndroidUtilities.dp(16.0f));
        this.listView.setAdapter(new Adapter());
        RecyclerListView recyclerListView = this.listView;
        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(context, 8);
        this.gridLayoutManager = gridLayoutManager2;
        recyclerListView.setLayoutManager(gridLayoutManager2);
        this.gridLayoutManager.setReverseLayout(false);
        this.gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int i) {
                if (EmojiPacksAlert.this.listView.getAdapter().getItemViewType(i) != 1) {
                    return EmojiPacksAlert.this.gridLayoutManager.getSpanCount();
                }
                return 1;
            }
        });
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, z ? 68.0f : 0.0f));
        View view = new View(context);
        this.shadowView = view;
        view.setBackgroundColor(Theme.getColor("dialogShadowLine"));
        this.containerView.addView(this.shadowView, LayoutHelper.createFrame(-1, 1.0f / AndroidUtilities.density, 80, 0.0f, 0.0f, 0.0f, z ? 68.0f : 0.0f));
        if (z) {
            TextView textView = new TextView(context);
            this.buttonView = textView;
            textView.setTextColor(getThemedColor("featuredStickers_buttonText"));
            this.buttonView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.buttonView.setGravity(17);
            this.buttonView.setClickable(true);
            this.containerView.addView(this.buttonView, LayoutHelper.createFrame(-1, 48.0f, 80, 12.0f, 10.0f, 12.0f, 10.0f));
            PremiumButtonView premiumButtonView2 = new PremiumButtonView(context, false);
            this.premiumButtonView = premiumButtonView2;
            premiumButtonView2.setIcon(NUM);
            this.containerView.addView(this.premiumButtonView, LayoutHelper.createFrame(-1, 48.0f, 80, 12.0f, 10.0f, 12.0f, 10.0f));
            updateButton();
        }
    }

    public void showPremiumAlert() {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment != null) {
            new PremiumFeatureBottomSheet(baseFragment, 11, false).show();
        } else if (getContext() instanceof LaunchActivity) {
            ((LaunchActivity) getContext()).lambda$runLinkRequest$59(new PremiumPreviewFragment((String) null));
        }
    }

    /* access modifiers changed from: private */
    public void updateLightStatusBar(boolean z) {
        boolean z2 = true;
        boolean z3 = AndroidUtilities.computePerceivedBrightness(getThemedColor("dialogBackground")) > 0.721f;
        if (AndroidUtilities.computePerceivedBrightness(Theme.blendOver(getThemedColor("actionBarDefault"), NUM)) <= 0.721f) {
            z2 = false;
        }
        if (!z) {
            z3 = z2;
        }
        AndroidUtilities.setLightStatusBar(getWindow(), z3);
    }

    /* access modifiers changed from: private */
    public boolean isInstalled(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        ArrayList<TLRPC$TL_messages_stickerSet> stickerSets;
        if (!(tLRPC$TL_messages_stickerSet == null || tLRPC$TL_messages_stickerSet.set == null || (stickerSets = MediaDataController.getInstance(this.currentAccount).getStickerSets(5)) == null)) {
            int i = 0;
            while (i < stickerSets.size()) {
                try {
                    if (stickerSets.get(i).set.id == tLRPC$TL_messages_stickerSet.set.id) {
                        return true;
                    }
                    i++;
                } catch (Exception unused) {
                }
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void installSet(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, BaseFragment baseFragment) {
        if (tLRPC$TL_messages_stickerSet != null && !MediaDataController.getInstance(this.currentAccount).cancelRemovingStickerSet(tLRPC$TL_messages_stickerSet.set.id)) {
            TLRPC$TL_messages_installStickerSet tLRPC$TL_messages_installStickerSet = new TLRPC$TL_messages_installStickerSet();
            TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
            tLRPC$TL_messages_installStickerSet.stickerset = tLRPC$TL_inputStickerSetID;
            TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set;
            tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
            tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_installStickerSet, new EmojiPacksAlert$$ExternalSyntheticLambda4(this, tLRPC$TL_messages_stickerSet, baseFragment));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$installSet$1(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, BaseFragment baseFragment, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new EmojiPacksAlert$$ExternalSyntheticLambda3(this, tLRPC$TL_messages_stickerSet, tLRPC$TL_error, baseFragment, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$installSet$0(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, TLRPC$TL_error tLRPC$TL_error, BaseFragment baseFragment, TLObject tLObject) {
        int i;
        TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set;
        if (tLRPC$StickerSet.masks) {
            i = 1;
        } else {
            i = tLRPC$StickerSet.emojis ? 5 : 0;
        }
        if (tLRPC$TL_error == null) {
            if (baseFragment != null) {
                try {
                    Bulletin.make(baseFragment, (Bulletin.Layout) new StickerSetBulletinLayout(getContext(), tLRPC$TL_messages_stickerSet, 2, (TLRPC$Document) null, this.resourcesProvider), 1500).show();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            if (tLObject instanceof TLRPC$TL_messages_stickerSetInstallResultArchive) {
                MediaDataController.getInstance(this.currentAccount).processStickerSetInstallResultArchive(this.fragment, true, i, (TLRPC$TL_messages_stickerSetInstallResultArchive) tLObject);
            }
        } else {
            Toast.makeText(getContext(), LocaleController.getString("ErrorOccurred", NUM), 0).show();
        }
        MediaDataController.getInstance(this.currentAccount).loadStickers(i, false, true);
    }

    /* access modifiers changed from: private */
    public void uninstallSet(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, BaseFragment baseFragment, Runnable runnable) {
        MediaDataController instance = MediaDataController.getInstance(this.currentAccount);
        Context context = getContext();
        boolean z = tLRPC$TL_messages_stickerSet.set.official;
        instance.toggleStickerSet(context, tLRPC$TL_messages_stickerSet, z ? 1 : 0, baseFragment, true, baseFragment != null, runnable);
    }

    /* access modifiers changed from: private */
    public void updateButton() {
        if (this.buttonView != null) {
            ArrayList<TLRPC$TL_messages_stickerSet> arrayList = this.customEmojiPacks.stickerSets;
            if (arrayList == null) {
                arrayList = new ArrayList<>();
            }
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = arrayList.get(i);
                if (!(tLRPC$TL_messages_stickerSet == null || tLRPC$TL_messages_stickerSet.set == null)) {
                    if (!isInstalled(tLRPC$TL_messages_stickerSet)) {
                        arrayList3.add(tLRPC$TL_messages_stickerSet);
                    } else {
                        arrayList2.add(tLRPC$TL_messages_stickerSet);
                    }
                }
            }
            boolean z = false;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet2 = arrayList.get(i2);
                if (!(tLRPC$TL_messages_stickerSet2 == null || tLRPC$TL_messages_stickerSet2.documents == null)) {
                    for (int i3 = 0; i3 < tLRPC$TL_messages_stickerSet2.documents.size(); i3++) {
                        if (!MessageObject.isFreeEmoji(tLRPC$TL_messages_stickerSet2.documents.get(i3))) {
                            z = true;
                        }
                    }
                }
            }
            if (!z || UserConfig.getInstance(this.currentAccount).isPremium()) {
                this.premiumButtonView.setVisibility(4);
                this.buttonView.setVisibility(0);
                if (arrayList3.size() > 0) {
                    this.buttonView.setBackground(Theme.AdaptiveRipple.filledRect("featuredStickers_addButton", 6.0f));
                    this.buttonView.setTextColor(getThemedColor("featuredStickers_buttonText"));
                    if (arrayList3.size() == 1) {
                        this.buttonView.setText(LocaleController.formatString("AddStickersCount", NUM, LocaleController.formatPluralString("EmojiCountButton", ((TLRPC$TL_messages_stickerSet) arrayList3.get(0)).documents.size(), new Object[0])));
                    } else {
                        this.buttonView.setText(LocaleController.formatString("AddStickersCount", NUM, LocaleController.formatPluralString("EmojiPackCount", arrayList3.size(), new Object[0])));
                    }
                    this.buttonView.setOnClickListener(new EmojiPacksAlert$$ExternalSyntheticLambda1(this, arrayList3));
                    return;
                }
                this.buttonView.setBackground(Theme.createRadSelectorDrawable(NUM & getThemedColor("dialogTextRed"), 6, 6));
                this.buttonView.setTextColor(getThemedColor("dialogTextRed"));
                if (arrayList2.size() == 1) {
                    this.buttonView.setText(LocaleController.formatString("RemoveStickersCount", NUM, LocaleController.formatPluralString("EmojiCountButton", ((TLRPC$TL_messages_stickerSet) arrayList2.get(0)).documents.size(), new Object[0])));
                } else {
                    this.buttonView.setText(LocaleController.formatString("RemoveStickersCount", NUM, LocaleController.formatPluralString("EmojiPackCount", arrayList2.size(), new Object[0])));
                }
                this.buttonView.setOnClickListener(new EmojiPacksAlert$$ExternalSyntheticLambda2(this, arrayList2));
                return;
            }
            this.premiumButtonView.setVisibility(0);
            this.buttonView.setVisibility(4);
            this.premiumButtonView.setButton(LocaleController.getString("UnlockPremiumEmoji", NUM), new EmojiPacksAlert$$ExternalSyntheticLambda0(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateButton$2(View view) {
        showPremiumAlert();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateButton$3(ArrayList arrayList, View view) {
        dismiss();
        int i = 0;
        while (i < arrayList.size()) {
            installSet((TLRPC$TL_messages_stickerSet) arrayList.get(i), i == 0 ? this.fragment : null);
            i++;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateButton$4(ArrayList arrayList, View view) {
        dismiss();
        int i = 0;
        while (i < arrayList.size()) {
            uninstallSet((TLRPC$TL_messages_stickerSet) arrayList.get(i), i == 0 ? this.fragment : null, (Runnable) null);
            i++;
        }
    }

    /* access modifiers changed from: private */
    public int getTop() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView == null || recyclerListView.getChildCount() < 1) {
            return this.containerView.getPaddingTop();
        }
        View childAt = this.listView.getChildAt(0);
        View view = this.paddingView;
        if (childAt != view) {
            return this.containerView.getPaddingTop();
        }
        return view.getBottom() + this.containerView.getPaddingTop();
    }

    public void show() {
        super.show();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 4);
    }

    public void dismiss() {
        super.dismiss();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 4);
    }

    public int getContainerViewHeight() {
        return (this.listView.getMeasuredHeight() - getTop()) + this.containerView.getPaddingTop() + AndroidUtilities.navigationBarHeight + AndroidUtilities.dp(8.0f);
    }

    private class Adapter extends RecyclerView.Adapter {
        private Adapter() {
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                view = EmojiPacksAlert.this.paddingView;
            } else {
                boolean z = true;
                if (i == 1) {
                    EmojiPacksAlert emojiPacksAlert = EmojiPacksAlert.this;
                    view = new EmojiImageView(emojiPacksAlert, emojiPacksAlert.getContext());
                } else if (i == 2) {
                    EmojiPacksAlert emojiPacksAlert2 = EmojiPacksAlert.this;
                    Context context = emojiPacksAlert2.getContext();
                    if (EmojiPacksAlert.this.customEmojiPacks.data.length > 1) {
                        z = false;
                    }
                    view = new EmojiPackHeader(emojiPacksAlert2, context, z);
                } else {
                    view = null;
                }
            }
            return new RecyclerListView.Holder(view);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v9, resolved type: boolean} */
        /* JADX WARNING: type inference failed for: r2v0 */
        /* JADX WARNING: type inference failed for: r2v1, types: [int] */
        /* JADX WARNING: type inference failed for: r2v10 */
        /* JADX WARNING: type inference failed for: r2v11 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r8, int r9) {
            /*
                r7 = this;
                int r9 = r9 + -1
                int r0 = r8.getItemViewType()
                r1 = 0
                r2 = 0
                r3 = 1
                if (r0 == r3) goto L_0x0092
                r4 = 2
                if (r0 == r4) goto L_0x0010
                goto L_0x0107
            L_0x0010:
                r0 = 0
                r4 = 0
                r5 = 0
            L_0x0013:
                org.telegram.ui.Components.EmojiPacksAlert r6 = org.telegram.ui.Components.EmojiPacksAlert.this
                org.telegram.ui.Components.EmojiPacksLoader r6 = r6.customEmojiPacks
                java.util.ArrayList<org.telegram.ui.Components.EmojiView$CustomEmoji>[] r6 = r6.data
                int r6 = r6.length
                if (r0 >= r6) goto L_0x0042
                org.telegram.ui.Components.EmojiPacksAlert r5 = org.telegram.ui.Components.EmojiPacksAlert.this
                org.telegram.ui.Components.EmojiPacksLoader r5 = r5.customEmojiPacks
                java.util.ArrayList<org.telegram.ui.Components.EmojiView$CustomEmoji>[] r5 = r5.data
                r5 = r5[r0]
                int r5 = r5.size()
                if (r9 != r4) goto L_0x002f
                goto L_0x0042
            L_0x002f:
                org.telegram.ui.Components.EmojiPacksAlert r6 = org.telegram.ui.Components.EmojiPacksAlert.this
                org.telegram.ui.Components.EmojiPacksLoader r6 = r6.customEmojiPacks
                java.util.ArrayList<org.telegram.ui.Components.EmojiView$CustomEmoji>[] r6 = r6.data
                r6 = r6[r0]
                int r6 = r6.size()
                int r6 = r6 + r3
                int r4 = r4 + r6
                int r0 = r0 + 1
                goto L_0x0013
            L_0x0042:
                org.telegram.ui.Components.EmojiPacksAlert r9 = org.telegram.ui.Components.EmojiPacksAlert.this
                org.telegram.ui.Components.EmojiPacksLoader r9 = r9.customEmojiPacks
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_messages_stickerSet> r9 = r9.stickerSets
                if (r9 != 0) goto L_0x004d
                goto L_0x005c
            L_0x004d:
                org.telegram.ui.Components.EmojiPacksAlert r9 = org.telegram.ui.Components.EmojiPacksAlert.this
                org.telegram.ui.Components.EmojiPacksLoader r9 = r9.customEmojiPacks
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_messages_stickerSet> r9 = r9.stickerSets
                java.lang.Object r9 = r9.get(r0)
                r1 = r9
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r1 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r1
            L_0x005c:
                if (r1 == 0) goto L_0x007e
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r9 = r1.documents
                if (r9 == 0) goto L_0x007e
                r9 = 0
            L_0x0063:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r1.documents
                int r4 = r4.size()
                if (r9 >= r4) goto L_0x007e
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r1.documents
                java.lang.Object r4 = r4.get(r9)
                org.telegram.tgnet.TLRPC$Document r4 = (org.telegram.tgnet.TLRPC$Document) r4
                boolean r4 = org.telegram.messenger.MessageObject.isFreeEmoji(r4)
                if (r4 != 0) goto L_0x007b
                r2 = 1
                goto L_0x007e
            L_0x007b:
                int r9 = r9 + 1
                goto L_0x0063
            L_0x007e:
                org.telegram.ui.Components.EmojiPacksAlert r9 = org.telegram.ui.Components.EmojiPacksAlert.this
                org.telegram.ui.Components.EmojiPacksLoader r9 = r9.customEmojiPacks
                java.util.ArrayList<org.telegram.ui.Components.EmojiView$CustomEmoji>[] r9 = r9.data
                int r9 = r9.length
                if (r0 >= r9) goto L_0x0107
                android.view.View r8 = r8.itemView
                org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader r8 = (org.telegram.ui.Components.EmojiPacksAlert.EmojiPackHeader) r8
                r8.set(r1, r5, r2)
                goto L_0x0107
            L_0x0092:
                android.view.View r8 = r8.itemView
                org.telegram.ui.Components.EmojiPacksAlert$EmojiImageView r8 = (org.telegram.ui.Components.EmojiPacksAlert.EmojiImageView) r8
                r0 = 0
            L_0x0097:
                org.telegram.ui.Components.EmojiPacksAlert r4 = org.telegram.ui.Components.EmojiPacksAlert.this
                org.telegram.ui.Components.EmojiPacksLoader r4 = r4.customEmojiPacks
                java.util.ArrayList<org.telegram.ui.Components.EmojiView$CustomEmoji>[] r4 = r4.data
                int r4 = r4.length
                if (r2 >= r4) goto L_0x00cf
                org.telegram.ui.Components.EmojiPacksAlert r4 = org.telegram.ui.Components.EmojiPacksAlert.this
                org.telegram.ui.Components.EmojiPacksLoader r4 = r4.customEmojiPacks
                java.util.ArrayList<org.telegram.ui.Components.EmojiView$CustomEmoji>[] r4 = r4.data
                r4 = r4[r2]
                int r4 = r4.size()
                if (r9 <= r0) goto L_0x00c9
                int r5 = r0 + r4
                if (r9 > r5) goto L_0x00c9
                org.telegram.ui.Components.EmojiPacksAlert r4 = org.telegram.ui.Components.EmojiPacksAlert.this
                org.telegram.ui.Components.EmojiPacksLoader r4 = r4.customEmojiPacks
                java.util.ArrayList<org.telegram.ui.Components.EmojiView$CustomEmoji>[] r4 = r4.data
                r2 = r4[r2]
                int r9 = r9 - r0
                int r9 = r9 - r3
                java.lang.Object r9 = r2.get(r9)
                org.telegram.ui.Components.EmojiView$CustomEmoji r9 = (org.telegram.ui.Components.EmojiView.CustomEmoji) r9
                goto L_0x00d0
            L_0x00c9:
                int r4 = r4 + 1
                int r0 = r0 + r4
                int r2 = r2 + 1
                goto L_0x0097
            L_0x00cf:
                r9 = r1
            L_0x00d0:
                org.telegram.ui.Components.AnimatedEmojiSpan r0 = r8.span
                if (r0 != 0) goto L_0x00d6
                if (r9 != 0) goto L_0x00e4
            L_0x00d6:
                if (r9 != 0) goto L_0x00da
                if (r0 != 0) goto L_0x00e4
            L_0x00da:
                if (r9 == 0) goto L_0x0107
                long r2 = r0.documentId
                long r4 = r9.documentId
                int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                if (r0 == 0) goto L_0x0107
            L_0x00e4:
                if (r9 != 0) goto L_0x00e9
                r8.span = r1
                goto L_0x0107
            L_0x00e9:
                org.telegram.tgnet.TLRPC$TL_inputStickerSetID r0 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetID
                r0.<init>()
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r2 = r9.stickerSet
                org.telegram.tgnet.TLRPC$StickerSet r2 = r2.set
                long r3 = r2.id
                r0.id = r3
                java.lang.String r3 = r2.short_name
                r0.short_name = r3
                long r2 = r2.access_hash
                r0.access_hash = r2
                org.telegram.ui.Components.AnimatedEmojiSpan r0 = new org.telegram.ui.Components.AnimatedEmojiSpan
                long r2 = r9.documentId
                r0.<init>((long) r2, (android.graphics.Paint.FontMetricsInt) r1)
                r8.span = r0
            L_0x0107:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiPacksAlert.Adapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemViewType(int i) {
            if (i == 0) {
                return 0;
            }
            int i2 = i - 1;
            int i3 = 0;
            for (ArrayList<EmojiView.CustomEmoji> size : EmojiPacksAlert.this.customEmojiPacks.data) {
                if (i2 == i3) {
                    return 2;
                }
                i3 += size.size() + 1;
            }
            return 1;
        }

        public int getItemCount() {
            return EmojiPacksAlert.this.customEmojiPacks.getItemsCount() + 1;
        }
    }

    private class EmojiImageView extends View {
        public ImageReceiver.BackgroundThreadDrawHolder backgroundThreadDrawHolder;
        public ImageReceiver imageReceiver;
        public AnimatedEmojiSpan span;

        public EmojiImageView(EmojiPacksAlert emojiPacksAlert, Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            setPadding(AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM));
        }
    }

    private class EmojiPackHeader extends FrameLayout {
        public TextView addButtonView;
        private ValueAnimator animator;
        public TextView removeButtonView;
        private TLRPC$TL_messages_stickerSet set;
        private boolean single;
        public TextView subtitleView;
        final /* synthetic */ EmojiPacksAlert this$0;
        public TextView titleView;
        private float toggleT = 0.0f;
        private boolean toggled = false;
        public PremiumButtonView unlockButtonView;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public EmojiPackHeader(org.telegram.ui.Components.EmojiPacksAlert r26, android.content.Context r27, boolean r28) {
            /*
                r25 = this;
                r0 = r25
                r1 = r26
                r2 = r27
                r3 = r28
                r0.this$0 = r1
                r0.<init>(r2)
                r4 = 0
                r0.toggled = r4
                r5 = 0
                r0.toggleT = r5
                r0.single = r3
                r6 = 1101004800(0x41a00000, float:20.0)
                java.lang.String r7 = "fonts/rmedium.ttf"
                r8 = 1090519040(0x41000000, float:8.0)
                if (r3 != 0) goto L_0x0213
                int r10 = r26.currentAccount
                org.telegram.messenger.UserConfig r10 = org.telegram.messenger.UserConfig.getInstance(r10)
                boolean r10 = r10.isPremium()
                r11 = 1082130432(0x40800000, float:4.0)
                r12 = 1098907648(0x41800000, float:16.0)
                r13 = 1073741824(0x40000000, float:2.0)
                r14 = 1105199104(0x41e00000, float:28.0)
                r15 = -2147483648(0xfffffffvar_, float:-0.0)
                r5 = 99999(0x1869f, float:1.40128E-40)
                if (r10 != 0) goto L_0x00dd
                org.telegram.ui.Components.Premium.PremiumButtonView r10 = new org.telegram.ui.Components.Premium.PremiumButtonView
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r11)
                r10.<init>(r2, r9, r4)
                r0.unlockButtonView = r10
                r9 = 2131628786(0x7f0e12f2, float:1.8884875E38)
                java.lang.String r11 = "Unlock"
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
                org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda2 r11 = new org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda2
                r11.<init>(r0)
                r10.setButton(r9, r11)
                org.telegram.ui.Components.Premium.PremiumButtonView r9 = r0.unlockButtonView
                r10 = 2131558576(0x7f0d00b0, float:1.8742472E38)
                r9.setIcon(r10)
                org.telegram.ui.Components.Premium.PremiumButtonView r9 = r0.unlockButtonView
                org.telegram.ui.Components.RLottieImageView r9 = r9.getIconView()
                android.view.ViewGroup$LayoutParams r9 = r9.getLayoutParams()
                android.view.ViewGroup$MarginLayoutParams r9 = (android.view.ViewGroup.MarginLayoutParams) r9
                r10 = 1065353216(0x3var_, float:1.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r9.leftMargin = r11
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r9.topMargin = r10
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r6)
                r9.height = r10
                r9.width = r10
                org.telegram.ui.Components.Premium.PremiumButtonView r9 = r0.unlockButtonView
                org.telegram.ui.Components.AnimatedTextView r9 = r9.getTextView()
                android.view.ViewGroup$LayoutParams r9 = r9.getLayoutParams()
                android.view.ViewGroup$MarginLayoutParams r9 = (android.view.ViewGroup.MarginLayoutParams) r9
                r10 = 1077936128(0x40400000, float:3.0)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r9.leftMargin = r10
                org.telegram.ui.Components.Premium.PremiumButtonView r9 = r0.unlockButtonView
                android.view.View r9 = r9.getChildAt(r4)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                r9.setPadding(r10, r4, r8, r4)
                org.telegram.ui.Components.Premium.PremiumButtonView r8 = r0.unlockButtonView
                r16 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r17 = 1105199104(0x41e00000, float:28.0)
                r18 = 8388661(0x800035, float:1.1755018E-38)
                r19 = 0
                r20 = 1106069422(0x41ed47ae, float:29.66)
                r21 = 1085611704(0x40b51eb8, float:5.66)
                r22 = 0
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrameRelatively(r16, r17, r18, r19, r20, r21, r22)
                r0.addView(r8, r9)
                org.telegram.ui.Components.Premium.PremiumButtonView r8 = r0.unlockButtonView
                int r9 = android.view.View.MeasureSpec.makeMeasureSpec(r5, r15)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r10 = android.view.View.MeasureSpec.makeMeasureSpec(r10, r13)
                r8.measure(r9, r10)
                org.telegram.ui.Components.Premium.PremiumButtonView r8 = r0.unlockButtonView
                int r8 = r8.getMeasuredWidth()
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r12)
                int r8 = r8 + r9
                float r8 = (float) r8
                float r9 = org.telegram.messenger.AndroidUtilities.density
                float r8 = r8 / r9
            L_0x00dd:
                android.widget.TextView r9 = new android.widget.TextView
                r9.<init>(r2)
                r0.addButtonView = r9
                android.graphics.Typeface r10 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
                r9.setTypeface(r10)
                android.widget.TextView r9 = r0.addButtonView
                java.lang.String r10 = "featuredStickers_buttonText"
                int r10 = r1.getThemedColor(r10)
                r9.setTextColor(r10)
                android.widget.TextView r9 = r0.addButtonView
                java.lang.String r10 = "featuredStickers_addButton"
                int r11 = r1.getThemedColor(r10)
                r6 = 1
                float[] r12 = new float[r6]
                r6 = 1082130432(0x40800000, float:4.0)
                r12[r4] = r6
                android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.AdaptiveRipple.filledRect((int) r11, (float[]) r12)
                r9.setBackground(r6)
                android.widget.TextView r6 = r0.addButtonView
                r9 = 2131624242(0x7f0e0132, float:1.8875658E38)
                java.lang.String r11 = "Add"
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
                r6.setText(r9)
                android.widget.TextView r6 = r0.addButtonView
                r9 = 1099956224(0x41900000, float:18.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r9)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
                r6.setPadding(r11, r4, r9, r4)
                android.widget.TextView r6 = r0.addButtonView
                r9 = 17
                r6.setGravity(r9)
                android.widget.TextView r6 = r0.addButtonView
                org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda3 r11 = new org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda3
                r11.<init>(r0)
                r6.setOnClickListener(r11)
                android.widget.TextView r6 = r0.addButtonView
                r18 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r19 = 1105199104(0x41e00000, float:28.0)
                r20 = 8388661(0x800035, float:1.1755018E-38)
                r21 = 0
                r22 = 1106069422(0x41ed47ae, float:29.66)
                r23 = 1085611704(0x40b51eb8, float:5.66)
                r24 = 0
                android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrameRelatively(r18, r19, r20, r21, r22, r23, r24)
                r0.addView(r6, r11)
                android.widget.TextView r6 = r0.addButtonView
                int r11 = android.view.View.MeasureSpec.makeMeasureSpec(r5, r15)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r12 = android.view.View.MeasureSpec.makeMeasureSpec(r12, r13)
                r6.measure(r11, r12)
                android.widget.TextView r6 = r0.addButtonView
                int r6 = r6.getMeasuredWidth()
                r11 = 1098907648(0x41800000, float:16.0)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r11)
                int r6 = r6 + r12
                float r6 = (float) r6
                float r11 = org.telegram.messenger.AndroidUtilities.density
                float r6 = r6 / r11
                float r6 = java.lang.Math.max(r8, r6)
                android.widget.TextView r8 = new android.widget.TextView
                r8.<init>(r2)
                r0.removeButtonView = r8
                android.graphics.Typeface r11 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
                r8.setTypeface(r11)
                android.widget.TextView r8 = r0.removeButtonView
                int r11 = r1.getThemedColor(r10)
                r8.setTextColor(r11)
                android.widget.TextView r8 = r0.removeButtonView
                r11 = 268435455(0xfffffff, float:2.5243547E-29)
                int r10 = r1.getThemedColor(r10)
                r10 = r10 & r11
                r11 = 4
                android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.createRadSelectorDrawable(r10, r11, r11)
                r8.setBackground(r10)
                android.widget.TextView r8 = r0.removeButtonView
                r10 = 2131628501(0x7f0e11d5, float:1.8884296E38)
                java.lang.String r11 = "StickersRemove"
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
                r8.setText(r10)
                android.widget.TextView r8 = r0.removeButtonView
                r10 = 1094713344(0x41400000, float:12.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r8.setPadding(r11, r4, r10, r4)
                android.widget.TextView r8 = r0.removeButtonView
                r8.setGravity(r9)
                android.widget.TextView r8 = r0.removeButtonView
                org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda1 r9 = new org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda1
                r9.<init>(r0)
                r8.setOnClickListener(r9)
                android.widget.TextView r8 = r0.removeButtonView
                r8.setClickable(r4)
                android.widget.TextView r4 = r0.removeButtonView
                android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrameRelatively(r18, r19, r20, r21, r22, r23, r24)
                r0.addView(r4, r8)
                android.widget.TextView r4 = r0.removeButtonView
                r8 = 0
                r4.setScaleX(r8)
                android.widget.TextView r4 = r0.removeButtonView
                r4.setScaleY(r8)
                android.widget.TextView r4 = r0.removeButtonView
                r4.setAlpha(r8)
                android.widget.TextView r4 = r0.removeButtonView
                int r5 = android.view.View.MeasureSpec.makeMeasureSpec(r5, r15)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r8 = android.view.View.MeasureSpec.makeMeasureSpec(r8, r13)
                r4.measure(r5, r8)
                android.widget.TextView r4 = r0.removeButtonView
                int r4 = r4.getMeasuredWidth()
                r5 = 1098907648(0x41800000, float:16.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r4 = r4 + r5
                float r4 = (float) r4
                float r5 = org.telegram.messenger.AndroidUtilities.density
                float r4 = r4 / r5
                float r8 = java.lang.Math.max(r6, r4)
            L_0x0213:
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r2)
                r0.titleView = r4
                android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
                r4.setTypeface(r5)
                android.widget.TextView r4 = r0.titleView
                android.text.TextUtils$TruncateAt r5 = android.text.TextUtils.TruncateAt.END
                r4.setEllipsize(r5)
                android.widget.TextView r4 = r0.titleView
                r5 = 1
                r4.setSingleLine(r5)
                android.widget.TextView r4 = r0.titleView
                r4.setLines(r5)
                android.widget.TextView r4 = r0.titleView
                java.lang.String r6 = "dialogTextBlack"
                int r6 = r1.getThemedColor(r6)
                r4.setTextColor(r6)
                if (r3 == 0) goto L_0x025f
                android.widget.TextView r4 = r0.titleView
                r6 = 1101004800(0x41a00000, float:20.0)
                r4.setTextSize(r5, r6)
                android.widget.TextView r4 = r0.titleView
                r9 = -1082130432(0xffffffffbvar_, float:-1.0)
                r10 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r11 = 8388659(0x800033, float:1.1755015E-38)
                r12 = 1097502556(0x416a8f5c, float:14.66)
                r13 = 1103626240(0x41CLASSNAME, float:25.0)
                r15 = 0
                r14 = r8
                android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrameRelatively(r9, r10, r11, r12, r13, r14, r15)
                r0.addView(r4, r5)
                goto L_0x027d
            L_0x025f:
                android.widget.TextView r4 = r0.titleView
                r5 = 1099431936(0x41880000, float:17.0)
                r6 = 1
                r4.setTextSize(r6, r5)
                android.widget.TextView r4 = r0.titleView
                r9 = -1082130432(0xffffffffbvar_, float:-1.0)
                r10 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r11 = 8388659(0x800033, float:1.1755015E-38)
                r12 = 1090519040(0x41000000, float:8.0)
                r13 = 1103101952(0x41CLASSNAME, float:24.0)
                r15 = 0
                r14 = r8
                android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrameRelatively(r9, r10, r11, r12, r13, r14, r15)
                r0.addView(r4, r5)
            L_0x027d:
                if (r3 != 0) goto L_0x02bf
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                r0.subtitleView = r3
                r2 = 1095761920(0x41500000, float:13.0)
                r4 = 1
                r3.setTextSize(r4, r2)
                android.widget.TextView r2 = r0.subtitleView
                java.lang.String r3 = "dialogTextGray2"
                int r1 = r1.getThemedColor(r3)
                r2.setTextColor(r1)
                android.widget.TextView r1 = r0.subtitleView
                android.text.TextUtils$TruncateAt r2 = android.text.TextUtils.TruncateAt.END
                r1.setEllipsize(r2)
                android.widget.TextView r1 = r0.subtitleView
                r1.setSingleLine(r4)
                android.widget.TextView r1 = r0.subtitleView
                r1.setLines(r4)
                android.widget.TextView r1 = r0.subtitleView
                r9 = -1082130432(0xffffffffbvar_, float:-1.0)
                r10 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r11 = 8388659(0x800033, float:1.1755015E-38)
                r12 = 1090519040(0x41000000, float:8.0)
                r13 = 1110877143(0x4236a3d7, float:45.66)
                r15 = 0
                r14 = r8
                android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrameRelatively(r9, r10, r11, r12, r13, r14, r15)
                r0.addView(r1, r2)
            L_0x02bf:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiPacksAlert.EmojiPackHeader.<init>(org.telegram.ui.Components.EmojiPacksAlert, android.content.Context, boolean):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            this.this$0.showPremiumAlert();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(View view) {
            this.this$0.installSet(this.set, new BaseFragment() {
                public FrameLayout getLayoutContainer() {
                    return (FrameLayout) EmojiPackHeader.this.this$0.containerView;
                }
            });
            toggle(true, true);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$3(View view) {
            this.this$0.uninstallSet(this.set, new BaseFragment() {
                public FrameLayout getLayoutContainer() {
                    return (FrameLayout) EmojiPackHeader.this.this$0.containerView;
                }
            }, new EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda4(this));
            toggle(false, true);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$2() {
            toggle(true, true);
        }

        private void toggle(boolean z, boolean z2) {
            if (this.toggled != z) {
                this.toggled = z;
                ValueAnimator valueAnimator = this.animator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    this.animator = null;
                }
                TextView textView = this.addButtonView;
                if (textView != null && this.removeButtonView != null) {
                    textView.setClickable(!z);
                    this.removeButtonView.setClickable(z);
                    float f = 1.0f;
                    if (z2) {
                        float[] fArr = new float[2];
                        fArr[0] = this.toggleT;
                        if (!z) {
                            f = 0.0f;
                        }
                        fArr[1] = f;
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                        this.animator = ofFloat;
                        ofFloat.addUpdateListener(new EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda0(this));
                        this.animator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                        this.animator.setDuration(250);
                        this.animator.start();
                        return;
                    }
                    this.toggleT = z ? 1.0f : 0.0f;
                    this.addButtonView.setScaleX(z ? 0.0f : 1.0f);
                    this.addButtonView.setScaleY(z ? 0.0f : 1.0f);
                    this.addButtonView.setAlpha(z ? 0.0f : 1.0f);
                    this.removeButtonView.setScaleX(z ? 1.0f : 0.0f);
                    this.removeButtonView.setScaleY(z ? 1.0f : 0.0f);
                    TextView textView2 = this.removeButtonView;
                    if (!z) {
                        f = 0.0f;
                    }
                    textView2.setAlpha(f);
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$toggle$4(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.toggleT = floatValue;
            this.addButtonView.setScaleX(1.0f - floatValue);
            this.addButtonView.setScaleY(1.0f - this.toggleT);
            this.addButtonView.setAlpha(1.0f - this.toggleT);
            this.removeButtonView.setScaleX(this.toggleT);
            this.removeButtonView.setScaleY(this.toggleT);
            this.removeButtonView.setAlpha(this.toggleT);
        }

        public void set(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, int i, boolean z) {
            TLRPC$StickerSet tLRPC$StickerSet;
            this.set = tLRPC$TL_messages_stickerSet;
            if (tLRPC$TL_messages_stickerSet == null || (tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set) == null) {
                this.titleView.setText((CharSequence) null);
            } else {
                this.titleView.setText(tLRPC$StickerSet.title);
            }
            TextView textView = this.subtitleView;
            if (textView != null) {
                textView.setText(LocaleController.formatPluralString("EmojiCount", i, new Object[0]));
            }
            if (!z || this.unlockButtonView == null || UserConfig.getInstance(this.this$0.currentAccount).isPremium()) {
                PremiumButtonView premiumButtonView = this.unlockButtonView;
                if (premiumButtonView != null) {
                    premiumButtonView.setVisibility(8);
                }
                TextView textView2 = this.addButtonView;
                if (textView2 != null) {
                    textView2.setVisibility(0);
                }
                TextView textView3 = this.removeButtonView;
                if (textView3 != null) {
                    textView3.setVisibility(0);
                }
                toggle(this.this$0.isInstalled(tLRPC$TL_messages_stickerSet), false);
                return;
            }
            this.unlockButtonView.setVisibility(0);
            TextView textView4 = this.addButtonView;
            if (textView4 != null) {
                textView4.setVisibility(8);
            }
            TextView textView5 = this.removeButtonView;
            if (textView5 != null) {
                textView5.setVisibility(8);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.single ? 65.0f : 70.0f), NUM));
        }
    }
}
