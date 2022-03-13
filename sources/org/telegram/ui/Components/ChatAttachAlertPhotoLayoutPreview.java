package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.RecyclerListView;

public class ChatAttachAlertPhotoLayoutPreview extends ChatAttachAlert.AttachAlertLayout {
    /* access modifiers changed from: private */
    public ValueAnimator draggingAnimator;
    /* access modifiers changed from: private */
    public PreviewGroupsView.PreviewGroupCell.MediaCell draggingCell = null;
    /* access modifiers changed from: private */
    public float draggingCellFromHeight = 0.0f;
    /* access modifiers changed from: private */
    public float draggingCellFromWidth = 0.0f;
    /* access modifiers changed from: private */
    public float draggingCellGroupY = 0.0f;
    /* access modifiers changed from: private */
    public boolean draggingCellHiding = false;
    /* access modifiers changed from: private */
    public float draggingCellLeft = 0.0f;
    /* access modifiers changed from: private */
    public float draggingCellTop = 0.0f;
    /* access modifiers changed from: private */
    public float draggingCellTouchX = 0.0f;
    /* access modifiers changed from: private */
    public float draggingCellTouchY = 0.0f;
    /* access modifiers changed from: private */
    public PreviewGroupsView.PreviewGroupCell draggingFromGroupCell;
    /* access modifiers changed from: private */
    public float draggingT = 0.0f;
    /* access modifiers changed from: private */
    public PreviewGroupsView groupsView;
    /* access modifiers changed from: private */
    public TextView header;
    private ViewPropertyAnimator headerAnimator;
    private boolean ignoreLayout = false;
    private boolean isPortrait;
    private LinearLayoutManager layoutManager;
    public RecyclerListView listView;
    private int paddingTop;
    /* access modifiers changed from: private */
    public ChatAttachAlertPhotoLayout photoLayout;
    private boolean shown = false;
    /* access modifiers changed from: private */
    public ChatActivity.ThemeDelegate themeDelegate;
    /* access modifiers changed from: private */
    public UndoView undoView;
    /* access modifiers changed from: private */
    public Drawable videoPlayImage;

    /* access modifiers changed from: package-private */
    public int needsActionBar() {
        return 1;
    }

    /* access modifiers changed from: package-private */
    public boolean shouldHideBottomButtons() {
        return true;
    }

    static /* synthetic */ float access$1516(ChatAttachAlertPhotoLayoutPreview chatAttachAlertPhotoLayoutPreview, float f) {
        float f2 = chatAttachAlertPhotoLayoutPreview.draggingCellTouchY + f;
        chatAttachAlertPhotoLayoutPreview.draggingCellTouchY = f2;
        return f2;
    }

    public float getPreviewScale() {
        Point point = AndroidUtilities.displaySize;
        return point.y > point.x ? 0.8f : 0.45f;
    }

    public ChatAttachAlertPhotoLayoutPreview(ChatAttachAlert chatAttachAlert, Context context, ChatActivity.ThemeDelegate themeDelegate2) {
        super(chatAttachAlert, context, themeDelegate2);
        Point point = AndroidUtilities.displaySize;
        this.isPortrait = point.y > point.x;
        this.themeDelegate = themeDelegate2;
        setWillNotDraw(false);
        ActionBarMenu createMenu = this.parentAlert.actionBar.createMenu();
        this.header = new TextView(context);
        AnonymousClass1 r4 = new ActionBarMenuItem(context, createMenu, 0, 0, this.resourcesProvider) {
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                accessibilityNodeInfo.setText(ChatAttachAlertPhotoLayoutPreview.this.header.getText());
            }
        };
        this.parentAlert.actionBar.addView(r4, 0, LayoutHelper.createFrame(-2, -1.0f, 51, AndroidUtilities.isTablet() ? 64.0f : 56.0f, 0.0f, 40.0f, 0.0f));
        this.header.setImportantForAccessibility(2);
        this.header.setGravity(3);
        this.header.setSingleLine(true);
        this.header.setLines(1);
        this.header.setMaxLines(1);
        this.header.setEllipsize(TextUtils.TruncateAt.END);
        this.header.setTextColor(getThemedColor("dialogTextBlack"));
        this.header.setText(LocaleController.getString("AttachMediaPreview", NUM));
        this.header.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.header.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
        this.header.setPadding(0, 0, AndroidUtilities.dp(10.0f), 0);
        this.header.setAlpha(0.0f);
        r4.addView(this.header, LayoutHelper.createFrame(-2, -2.0f, 16, 16.0f, 0.0f, 0.0f, 0.0f));
        AnonymousClass2 r12 = new RecyclerListView(context, this.resourcesProvider) {
            public void onScrolled(int i, int i2) {
                ChatAttachAlertPhotoLayoutPreview.this.invalidate();
                ChatAttachAlertPhotoLayoutPreview chatAttachAlertPhotoLayoutPreview = ChatAttachAlertPhotoLayoutPreview.this;
                chatAttachAlertPhotoLayoutPreview.parentAlert.updateLayout(chatAttachAlertPhotoLayoutPreview, true, i2);
                ChatAttachAlertPhotoLayoutPreview.this.groupsView.onScroll();
                super.onScrolled(i, i2);
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (ChatAttachAlertPhotoLayoutPreview.this.draggingCell != null) {
                    return false;
                }
                return super.onTouchEvent(motionEvent);
            }

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (ChatAttachAlertPhotoLayoutPreview.this.draggingCell != null) {
                    return false;
                }
                return super.onInterceptTouchEvent(motionEvent);
            }
        };
        this.listView = r12;
        r12.setAdapter(new RecyclerView.Adapter() {
            public int getItemCount() {
                return 1;
            }

            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            }

            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                return new RecyclerListView.Holder(ChatAttachAlertPhotoLayoutPreview.this.groupsView);
            }
        });
        RecyclerListView recyclerListView = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.listView.setClipChildren(false);
        this.listView.setClipToPadding(false);
        this.listView.setOverScrollMode(2);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(46.0f));
        PreviewGroupsView previewGroupsView = new PreviewGroupsView(context);
        this.groupsView = previewGroupsView;
        previewGroupsView.setClipToPadding(true);
        this.groupsView.setClipChildren(true);
        addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.photoLayout = this.parentAlert.getPhotoLayout();
        this.groupsView.deletedPhotos.clear();
        this.groupsView.fromPhotoLayout(this.photoLayout);
        UndoView undoView2 = new UndoView(context);
        this.undoView = undoView2;
        undoView2.setEnterOffsetMargin(AndroidUtilities.dp(32.0f));
        addView(this.undoView, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 52.0f));
        this.videoPlayImage = context.getResources().getDrawable(NUM);
    }

    /* access modifiers changed from: package-private */
    public void onShow(ChatAttachAlert.AttachAlertLayout attachAlertLayout) {
        this.shown = true;
        if (attachAlertLayout instanceof ChatAttachAlertPhotoLayout) {
            this.photoLayout = (ChatAttachAlertPhotoLayout) attachAlertLayout;
            this.groupsView.deletedPhotos.clear();
            this.groupsView.fromPhotoLayout(this.photoLayout);
            this.groupsView.requestLayout();
            this.layoutManager.scrollToPositionWithOffset(0, 0);
            this.listView.post(new ChatAttachAlertPhotoLayoutPreview$$ExternalSyntheticLambda1(this, attachAlertLayout));
            postDelayed(new ChatAttachAlertPhotoLayoutPreview$$ExternalSyntheticLambda0(this), 250);
            this.groupsView.toPhotoLayout(this.photoLayout, false);
        } else {
            scrollToTop();
        }
        ViewPropertyAnimator viewPropertyAnimator = this.headerAnimator;
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.cancel();
        }
        ViewPropertyAnimator interpolator = this.header.animate().alpha(1.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.EASE_BOTH);
        this.headerAnimator = interpolator;
        interpolator.start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onShow$0(ChatAttachAlert.AttachAlertLayout attachAlertLayout) {
        int currentItemTop = attachAlertLayout.getCurrentItemTop();
        int listTopPadding = attachAlertLayout.getListTopPadding();
        RecyclerListView recyclerListView = this.listView;
        if (currentItemTop > AndroidUtilities.dp(7.0f)) {
            listTopPadding -= currentItemTop;
        }
        recyclerListView.scrollBy(0, listTopPadding);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onShow$1() {
        if (this.shown) {
            this.parentAlert.selectedMenuItem.hideSubItem(3);
        }
    }

    /* access modifiers changed from: package-private */
    public void onHide() {
        this.shown = false;
        ViewPropertyAnimator viewPropertyAnimator = this.headerAnimator;
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.cancel();
        }
        ViewPropertyAnimator interpolator = this.header.animate().alpha(0.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.EASE_BOTH);
        this.headerAnimator = interpolator;
        interpolator.start();
        if (getSelectedItemsCount() > 1) {
            this.parentAlert.selectedMenuItem.showSubItem(3);
        }
        this.groupsView.toPhotoLayout(this.photoLayout, true);
    }

    /* access modifiers changed from: package-private */
    public int getSelectedItemsCount() {
        return this.groupsView.getPhotosCount();
    }

    /* access modifiers changed from: package-private */
    public void onHidden() {
        this.draggingCell = null;
    }

    /* access modifiers changed from: package-private */
    public int getFirstOffset() {
        return getListTopPadding() + AndroidUtilities.dp(56.0f);
    }

    /* access modifiers changed from: package-private */
    public void applyCaption(CharSequence charSequence) {
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = this.photoLayout;
        if (chatAttachAlertPhotoLayout != null) {
            chatAttachAlertPhotoLayout.applyCaption(charSequence);
        }
    }

    private class GroupCalculator {
        float height;
        int maxX;
        int maxY;
        ArrayList<MediaController.PhotoEntry> photos;
        public ArrayList<MessageObject.GroupedMessagePosition> posArray = new ArrayList<>();
        public HashMap<MediaController.PhotoEntry, MessageObject.GroupedMessagePosition> positions = new HashMap<>();
        int width;

        private class MessageGroupedLayoutAttempt {
            public float[] heights;
            public int[] lineCounts;

            public MessageGroupedLayoutAttempt(GroupCalculator groupCalculator, int i, int i2, float f, float f2) {
                this.lineCounts = new int[]{i, i2};
                this.heights = new float[]{f, f2};
            }

            public MessageGroupedLayoutAttempt(GroupCalculator groupCalculator, int i, int i2, int i3, float f, float f2, float f3) {
                this.lineCounts = new int[]{i, i2, i3};
                this.heights = new float[]{f, f2, f3};
            }

            public MessageGroupedLayoutAttempt(GroupCalculator groupCalculator, int i, int i2, int i3, int i4, float f, float f2, float f3, float f4) {
                this.lineCounts = new int[]{i, i2, i3, i4};
                this.heights = new float[]{f, f2, f3, f4};
            }
        }

        private float multiHeight(float[] fArr, int i, int i2) {
            float f = 0.0f;
            while (i < i2) {
                f += fArr[i];
                i++;
            }
            return 1000.0f / f;
        }

        public GroupCalculator(ArrayList<MediaController.PhotoEntry> arrayList) {
            this.photos = arrayList;
            calculate();
        }

        public void calculate() {
            int i;
            int i2;
            int i3;
            float f;
            int i4;
            int i5;
            float[] fArr;
            int i6;
            int i7;
            int i8;
            float[] fArr2;
            int i9;
            int i10;
            float f2;
            int i11;
            ArrayList arrayList;
            int size = this.photos.size();
            if (size == 0) {
                this.width = 0;
                this.height = 0.0f;
                this.maxX = 0;
                this.maxY = 0;
                return;
            }
            this.posArray.clear();
            this.posArray.ensureCapacity(size);
            this.positions.clear();
            char[] cArr = new char[size];
            int i12 = 0;
            float f3 = 1.0f;
            boolean z = false;
            while (i12 < size) {
                MediaController.PhotoEntry photoEntry = this.photos.get(i12);
                MessageObject.GroupedMessagePosition groupedMessagePosition = new MessageObject.GroupedMessagePosition();
                groupedMessagePosition.last = i12 == size + -1;
                float f4 = ((float) photoEntry.width) / ((float) photoEntry.height);
                groupedMessagePosition.aspectRatio = f4;
                cArr[i12] = f4 > 1.2f ? 'w' : f4 < 0.8f ? 'n' : 'q';
                f3 += f4;
                if (f4 > 2.0f) {
                    z = true;
                }
                this.positions.put(photoEntry, groupedMessagePosition);
                this.posArray.add(groupedMessagePosition);
                i12++;
            }
            String str = new String(cArr);
            int dp = AndroidUtilities.dp(120.0f);
            Point point = AndroidUtilities.displaySize;
            int dp2 = (int) (((float) AndroidUtilities.dp(120.0f)) / (((float) Math.min(point.x, point.y)) / 1000.0f));
            Point point2 = AndroidUtilities.displaySize;
            int dp3 = (int) (((float) AndroidUtilities.dp(40.0f)) / (((float) Math.min(point2.x, point2.y)) / 1000.0f));
            float f5 = f3 / ((float) size);
            float dp4 = ((float) AndroidUtilities.dp(100.0f)) / 814.0f;
            int i13 = 3;
            if ((z || !(size == 2 || size == 3 || size == 4)) && size != 1) {
                int size2 = this.posArray.size();
                float[] fArr3 = new float[size2];
                for (int i14 = 0; i14 < size; i14++) {
                    if (f5 > 1.1f) {
                        fArr3[i14] = Math.max(1.0f, this.posArray.get(i14).aspectRatio);
                    } else {
                        fArr3[i14] = Math.min(1.0f, this.posArray.get(i14).aspectRatio);
                    }
                    fArr3[i14] = Math.max(0.66667f, Math.min(1.7f, fArr3[i14]));
                }
                ArrayList arrayList2 = new ArrayList();
                int i15 = 1;
                while (i15 < size2) {
                    int i16 = size2 - i15;
                    if (i15 > 3 || i16 > 3) {
                        i11 = i15;
                        f2 = dp4;
                        arrayList = arrayList2;
                    } else {
                        MessageGroupedLayoutAttempt messageGroupedLayoutAttempt = r0;
                        i11 = i15;
                        f2 = dp4;
                        arrayList = arrayList2;
                        MessageGroupedLayoutAttempt messageGroupedLayoutAttempt2 = new MessageGroupedLayoutAttempt(this, i15, i16, multiHeight(fArr3, 0, i15), multiHeight(fArr3, i15, size2));
                        arrayList.add(messageGroupedLayoutAttempt2);
                    }
                    i15 = i11 + 1;
                    arrayList2 = arrayList;
                    dp4 = f2;
                }
                float f6 = dp4;
                ArrayList arrayList3 = arrayList2;
                int i17 = 1;
                while (i17 < size2 - 1) {
                    int i18 = 1;
                    while (true) {
                        int i19 = size2 - i17;
                        if (i18 >= i19) {
                            break;
                        }
                        int i20 = i19 - i18;
                        if (i17 <= 3) {
                            if (i18 <= (f5 < 0.85f ? 4 : 3) && i20 <= 3) {
                                int i21 = i17 + i18;
                                MessageGroupedLayoutAttempt messageGroupedLayoutAttempt3 = r0;
                                i9 = i18;
                                fArr2 = fArr3;
                                i10 = i17;
                                MessageGroupedLayoutAttempt messageGroupedLayoutAttempt4 = new MessageGroupedLayoutAttempt(this, i17, i18, i20, multiHeight(fArr3, 0, i17), multiHeight(fArr3, i17, i21), multiHeight(fArr3, i21, size2));
                                arrayList3.add(messageGroupedLayoutAttempt3);
                                i18 = i9 + 1;
                                i17 = i10;
                                fArr3 = fArr2;
                            }
                        }
                        i9 = i18;
                        fArr2 = fArr3;
                        i10 = i17;
                        i18 = i9 + 1;
                        i17 = i10;
                        fArr3 = fArr2;
                    }
                    float[] fArr4 = fArr3;
                    i17++;
                }
                float[] fArr5 = fArr3;
                int i22 = 1;
                while (i22 < size2 - 2) {
                    int i23 = 1;
                    while (true) {
                        int i24 = size2 - i22;
                        if (i23 >= i24) {
                            break;
                        }
                        int i25 = 1;
                        while (true) {
                            int i26 = i24 - i23;
                            if (i25 >= i26) {
                                break;
                            }
                            int i27 = i26 - i25;
                            if (i22 > i13 || i23 > i13 || i25 > i13 || i27 > i13) {
                                i4 = i25;
                                i8 = i24;
                                i7 = i23;
                                i6 = size;
                                i5 = size2;
                                fArr = fArr5;
                            } else {
                                float[] fArr6 = fArr5;
                                float multiHeight = multiHeight(fArr6, 0, i22);
                                int i28 = i22 + i23;
                                float multiHeight2 = multiHeight(fArr6, i22, i28);
                                int i29 = i28 + i25;
                                float multiHeight3 = multiHeight(fArr6, i28, i29);
                                float multiHeight4 = multiHeight(fArr6, i29, size2);
                                fArr = fArr6;
                                i5 = size2;
                                MessageGroupedLayoutAttempt messageGroupedLayoutAttempt5 = r0;
                                int i30 = i25;
                                i4 = i25;
                                float f7 = multiHeight;
                                i8 = i24;
                                float f8 = multiHeight2;
                                i7 = i23;
                                i6 = size;
                                MessageGroupedLayoutAttempt messageGroupedLayoutAttempt6 = new MessageGroupedLayoutAttempt(this, i22, i23, i30, i27, f7, f8, multiHeight3, multiHeight4);
                                arrayList3.add(messageGroupedLayoutAttempt5);
                            }
                            i25 = i4 + 1;
                            i24 = i8;
                            i23 = i7;
                            size = i6;
                            fArr5 = fArr;
                            size2 = i5;
                            i13 = 3;
                        }
                        int i31 = size2;
                        float[] fArr7 = fArr5;
                        i23++;
                        size = size;
                        i13 = 3;
                    }
                    int i32 = size2;
                    float[] fArr8 = fArr5;
                    i22++;
                    size = size;
                    i13 = 3;
                }
                i = size;
                float[] fArr9 = fArr5;
                i2 = 2;
                MessageGroupedLayoutAttempt messageGroupedLayoutAttempt7 = null;
                float f9 = 0.0f;
                for (int i33 = 0; i33 < arrayList3.size(); i33++) {
                    MessageGroupedLayoutAttempt messageGroupedLayoutAttempt8 = (MessageGroupedLayoutAttempt) arrayList3.get(i33);
                    float var_ = Float.MAX_VALUE;
                    int i34 = 0;
                    float var_ = 0.0f;
                    while (true) {
                        float[] fArr10 = messageGroupedLayoutAttempt8.heights;
                        if (i34 >= fArr10.length) {
                            break;
                        }
                        var_ += fArr10[i34];
                        if (fArr10[i34] < var_) {
                            var_ = fArr10[i34];
                        }
                        i34++;
                    }
                    float abs = Math.abs(var_ - 1332.0f);
                    int[] iArr = messageGroupedLayoutAttempt8.lineCounts;
                    if (iArr.length > 1) {
                        if (iArr[0] <= iArr[1]) {
                            if (iArr.length > 2 && iArr[1] > iArr[2]) {
                                f = 1.2f;
                                abs *= f;
                            } else if (iArr.length <= 3 || iArr[2] <= iArr[3]) {
                            }
                        }
                        f = 1.2f;
                        abs *= f;
                    }
                    if (var_ < ((float) dp2)) {
                        abs *= 1.5f;
                    }
                    if (messageGroupedLayoutAttempt7 == null || abs < f9) {
                        messageGroupedLayoutAttempt7 = messageGroupedLayoutAttempt8;
                        f9 = abs;
                    }
                }
                if (messageGroupedLayoutAttempt7 != null) {
                    int i35 = 0;
                    int i36 = 0;
                    while (true) {
                        int[] iArr2 = messageGroupedLayoutAttempt7.lineCounts;
                        if (i36 >= iArr2.length) {
                            break;
                        }
                        int i37 = iArr2[i36];
                        float var_ = messageGroupedLayoutAttempt7.heights[i36];
                        MessageObject.GroupedMessagePosition groupedMessagePosition2 = null;
                        int i38 = 1000;
                        for (int i39 = 0; i39 < i37; i39++) {
                            int i40 = (int) (fArr9[i35] * var_);
                            i38 -= i40;
                            MessageObject.GroupedMessagePosition groupedMessagePosition3 = this.posArray.get(i35);
                            int i41 = i36 == 0 ? 4 : 0;
                            if (i36 == messageGroupedLayoutAttempt7.lineCounts.length - 1) {
                                i41 |= 8;
                            }
                            if (i39 == 0) {
                                i41 |= 1;
                                groupedMessagePosition2 = groupedMessagePosition3;
                            }
                            if (i39 == i37 - 1) {
                                i3 = i41 | 2;
                                groupedMessagePosition2 = groupedMessagePosition3;
                            } else {
                                i3 = i41;
                            }
                            groupedMessagePosition3.set(i39, i39, i36, i36, i40, Math.max(f6, var_ / 814.0f), i3);
                            i35++;
                        }
                        float var_ = f6;
                        if (groupedMessagePosition2 != null) {
                            groupedMessagePosition2.pw += i38;
                            groupedMessagePosition2.spanSize += i38;
                        }
                        i36++;
                        f6 = var_;
                    }
                } else {
                    return;
                }
            } else {
                if (size == 1) {
                    MessageObject.GroupedMessagePosition groupedMessagePosition4 = this.posArray.get(0);
                    int backgroundPaddingLeft = AndroidUtilities.displaySize.x - (ChatAttachAlertPhotoLayoutPreview.this.parentAlert.getBackgroundPaddingLeft() * 2);
                    Point point3 = AndroidUtilities.displaySize;
                    groupedMessagePosition4.set(0, 0, 0, 0, 800, ((((float) backgroundPaddingLeft) * 0.8f) / groupedMessagePosition4.aspectRatio) / (((float) Math.max(point3.x, point3.y)) * 0.5f), 15);
                } else if (size == 2) {
                    MessageObject.GroupedMessagePosition groupedMessagePosition5 = this.posArray.get(0);
                    MessageObject.GroupedMessagePosition groupedMessagePosition6 = this.posArray.get(1);
                    if (str.equals("ww")) {
                        double d = (double) 1.2285012f;
                        Double.isNaN(d);
                        if (((double) f5) > d * 1.4d) {
                            float var_ = groupedMessagePosition5.aspectRatio;
                            float var_ = groupedMessagePosition6.aspectRatio;
                            if (((double) (var_ - var_)) < 0.2d) {
                                float round = ((float) Math.round(Math.min(1000.0f / var_, Math.min(1000.0f / var_, 407.0f)))) / 814.0f;
                                groupedMessagePosition5.set(0, 0, 0, 0, 1000, round, 7);
                                groupedMessagePosition6.set(0, 0, 1, 1, 1000, round, 11);
                            }
                        }
                    }
                    if (str.equals("ww") || str.equals("qq")) {
                        float var_ = (float) 500;
                        float round2 = ((float) Math.round(Math.min(var_ / groupedMessagePosition5.aspectRatio, Math.min(var_ / groupedMessagePosition6.aspectRatio, 814.0f)))) / 814.0f;
                        groupedMessagePosition5.set(0, 0, 0, 0, 500, round2, 13);
                        groupedMessagePosition6.set(1, 1, 0, 0, 500, round2, 14);
                    } else {
                        float var_ = groupedMessagePosition5.aspectRatio;
                        int max = (int) Math.max(400.0f, (float) Math.round((1000.0f / var_) / ((1.0f / var_) + (1.0f / groupedMessagePosition6.aspectRatio))));
                        int i42 = 1000 - max;
                        if (i42 < dp2) {
                            max -= dp2 - i42;
                            i42 = dp2;
                        }
                        float min = Math.min(814.0f, (float) Math.round(Math.min(((float) i42) / groupedMessagePosition5.aspectRatio, ((float) max) / groupedMessagePosition6.aspectRatio))) / 814.0f;
                        groupedMessagePosition5.set(0, 0, 0, 0, i42, min, 13);
                        groupedMessagePosition6.set(1, 1, 0, 0, max, min, 14);
                    }
                } else if (size == 3) {
                    MessageObject.GroupedMessagePosition groupedMessagePosition7 = this.posArray.get(0);
                    MessageObject.GroupedMessagePosition groupedMessagePosition8 = this.posArray.get(1);
                    MessageObject.GroupedMessagePosition groupedMessagePosition9 = this.posArray.get(2);
                    if (str.charAt(0) == 'n') {
                        float var_ = groupedMessagePosition8.aspectRatio;
                        float min2 = Math.min(407.0f, (float) Math.round((1000.0f * var_) / (groupedMessagePosition9.aspectRatio + var_)));
                        float var_ = 814.0f - min2;
                        int max2 = (int) Math.max((float) dp2, Math.min(500.0f, (float) Math.round(Math.min(groupedMessagePosition9.aspectRatio * min2, groupedMessagePosition8.aspectRatio * var_))));
                        float var_ = (groupedMessagePosition7.aspectRatio * 814.0f) + ((float) dp3);
                        int i43 = 1000 - max2;
                        groupedMessagePosition7.set(0, 0, 0, 1, Math.round(Math.min(var_, (float) i43)), 1.0f, 13);
                        float var_ = var_ / 814.0f;
                        int i44 = max2;
                        groupedMessagePosition8.set(1, 1, 0, 0, i44, var_, 6);
                        float var_ = min2 / 814.0f;
                        groupedMessagePosition9.set(1, 1, 1, 1, i44, var_, 10);
                        groupedMessagePosition9.spanSize = 1000;
                        groupedMessagePosition7.siblingHeights = new float[]{var_, var_};
                        groupedMessagePosition7.spanSize = i43;
                    } else {
                        float round3 = ((float) Math.round(Math.min(1000.0f / groupedMessagePosition7.aspectRatio, 537.24005f))) / 814.0f;
                        groupedMessagePosition7.set(0, 1, 0, 0, 1000, round3, 7);
                        float var_ = (float) 500;
                        float min3 = Math.min(814.0f - round3, (float) Math.round(Math.min(var_ / groupedMessagePosition8.aspectRatio, var_ / groupedMessagePosition9.aspectRatio))) / 814.0f;
                        if (min3 < dp4) {
                            min3 = dp4;
                        }
                        float var_ = min3;
                        groupedMessagePosition8.set(0, 0, 1, 1, 500, var_, 9);
                        groupedMessagePosition9.set(1, 1, 1, 1, 500, var_, 10);
                    }
                } else {
                    MessageObject.GroupedMessagePosition groupedMessagePosition10 = this.posArray.get(0);
                    MessageObject.GroupedMessagePosition groupedMessagePosition11 = this.posArray.get(1);
                    MessageObject.GroupedMessagePosition groupedMessagePosition12 = this.posArray.get(2);
                    MessageObject.GroupedMessagePosition groupedMessagePosition13 = this.posArray.get(3);
                    if (str.charAt(0) == 'w') {
                        float round4 = ((float) Math.round(Math.min(1000.0f / groupedMessagePosition10.aspectRatio, 537.24005f))) / 814.0f;
                        groupedMessagePosition10.set(0, 2, 0, 0, 1000, round4, 7);
                        float round5 = (float) Math.round(1000.0f / ((groupedMessagePosition11.aspectRatio + groupedMessagePosition12.aspectRatio) + groupedMessagePosition13.aspectRatio));
                        float var_ = (float) dp2;
                        int max3 = (int) Math.max(var_, Math.min(400.0f, groupedMessagePosition11.aspectRatio * round5));
                        int max4 = (int) Math.max(Math.max(var_, 330.0f), groupedMessagePosition13.aspectRatio * round5);
                        int i45 = (1000 - max3) - max4;
                        if (i45 < AndroidUtilities.dp(58.0f)) {
                            int dp5 = AndroidUtilities.dp(58.0f) - i45;
                            i45 = AndroidUtilities.dp(58.0f);
                            int i46 = dp5 / 2;
                            max3 -= i46;
                            max4 -= dp5 - i46;
                        }
                        int i47 = max3;
                        float min4 = Math.min(814.0f - round4, round5) / 814.0f;
                        if (min4 < dp4) {
                            min4 = dp4;
                        }
                        float var_ = min4;
                        groupedMessagePosition11.set(0, 0, 1, 1, i47, var_, 9);
                        groupedMessagePosition12.set(1, 1, 1, 1, i45, var_, 8);
                        groupedMessagePosition13.set(2, 2, 1, 1, max4, var_, 10);
                    } else {
                        int max5 = Math.max(dp2, Math.round(814.0f / (((1.0f / groupedMessagePosition11.aspectRatio) + (1.0f / groupedMessagePosition12.aspectRatio)) + (1.0f / groupedMessagePosition13.aspectRatio))));
                        float var_ = (float) dp;
                        float var_ = (float) max5;
                        float min5 = Math.min(0.33f, Math.max(var_, var_ / groupedMessagePosition11.aspectRatio) / 814.0f);
                        float min6 = Math.min(0.33f, Math.max(var_, var_ / groupedMessagePosition12.aspectRatio) / 814.0f);
                        float var_ = (1.0f - min5) - min6;
                        float var_ = (groupedMessagePosition10.aspectRatio * 814.0f) + ((float) dp3);
                        int i48 = 1000 - max5;
                        groupedMessagePosition10.set(0, 0, 0, 2, Math.round(Math.min(var_, (float) i48)), min5 + min6 + var_, 13);
                        int i49 = max5;
                        groupedMessagePosition11.set(1, 1, 0, 0, i49, min5, 6);
                        groupedMessagePosition12.set(1, 1, 1, 1, i49, min6, 2);
                        groupedMessagePosition12.spanSize = 1000;
                        groupedMessagePosition13.set(1, 1, 2, 2, i49, var_, 10);
                        groupedMessagePosition13.spanSize = 1000;
                        groupedMessagePosition10.spanSize = i48;
                        groupedMessagePosition10.siblingHeights = new float[]{min5, min6, var_};
                    }
                }
                i = size;
                i2 = 2;
            }
            int i50 = i;
            for (int i51 = 0; i51 < i50; i51++) {
                MessageObject.GroupedMessagePosition groupedMessagePosition14 = this.posArray.get(i51);
                if (groupedMessagePosition14.minX == 0) {
                    groupedMessagePosition14.spanSize += 200;
                }
                if ((groupedMessagePosition14.flags & i2) != 0) {
                    groupedMessagePosition14.edge = true;
                }
                this.maxX = Math.max(this.maxX, groupedMessagePosition14.maxX);
                this.maxY = Math.max(this.maxY, groupedMessagePosition14.maxY);
                groupedMessagePosition14.left = getLeft(groupedMessagePosition14, groupedMessagePosition14.minY, groupedMessagePosition14.maxY, groupedMessagePosition14.minX);
            }
            for (int i52 = 0; i52 < i50; i52++) {
                MessageObject.GroupedMessagePosition groupedMessagePosition15 = this.posArray.get(i52);
                groupedMessagePosition15.top = getTop(groupedMessagePosition15, groupedMessagePosition15.minY);
            }
            this.width = getWidth();
            this.height = getHeight();
        }

        public int getWidth() {
            int[] iArr = new int[10];
            Arrays.fill(iArr, 0);
            int size = this.posArray.size();
            for (int i = 0; i < size; i++) {
                MessageObject.GroupedMessagePosition groupedMessagePosition = this.posArray.get(i);
                int i2 = groupedMessagePosition.pw;
                for (int i3 = groupedMessagePosition.minY; i3 <= groupedMessagePosition.maxY; i3++) {
                    iArr[i3] = iArr[i3] + i2;
                }
            }
            int i4 = iArr[0];
            for (int i5 = 1; i5 < 10; i5++) {
                if (i4 < iArr[i5]) {
                    i4 = iArr[i5];
                }
            }
            return i4;
        }

        public float getHeight() {
            float[] fArr = new float[10];
            Arrays.fill(fArr, 0.0f);
            int size = this.posArray.size();
            for (int i = 0; i < size; i++) {
                MessageObject.GroupedMessagePosition groupedMessagePosition = this.posArray.get(i);
                float f = groupedMessagePosition.ph;
                for (int i2 = groupedMessagePosition.minX; i2 <= groupedMessagePosition.maxX; i2++) {
                    fArr[i2] = fArr[i2] + f;
                }
            }
            float f2 = fArr[0];
            for (int i3 = 1; i3 < 10; i3++) {
                if (f2 < fArr[i3]) {
                    f2 = fArr[i3];
                }
            }
            return f2;
        }

        private float getLeft(MessageObject.GroupedMessagePosition groupedMessagePosition, int i, int i2, int i3) {
            int i4 = (i2 - i) + 1;
            float[] fArr = new float[i4];
            float f = 0.0f;
            Arrays.fill(fArr, 0.0f);
            int size = this.posArray.size();
            for (int i5 = 0; i5 < size; i5++) {
                MessageObject.GroupedMessagePosition groupedMessagePosition2 = this.posArray.get(i5);
                if (groupedMessagePosition2 != groupedMessagePosition && groupedMessagePosition2.maxX < i3) {
                    int min = Math.min(groupedMessagePosition2.maxY, i2) - i;
                    for (int max = Math.max(groupedMessagePosition2.minY - i, 0); max <= min; max++) {
                        fArr[max] = fArr[max] + ((float) groupedMessagePosition2.pw);
                    }
                }
            }
            for (int i6 = 0; i6 < i4; i6++) {
                if (f < fArr[i6]) {
                    f = fArr[i6];
                }
            }
            return f;
        }

        private float getTop(MessageObject.GroupedMessagePosition groupedMessagePosition, int i) {
            int i2 = this.maxX + 1;
            float[] fArr = new float[i2];
            float f = 0.0f;
            Arrays.fill(fArr, 0.0f);
            int size = this.posArray.size();
            for (int i3 = 0; i3 < size; i3++) {
                MessageObject.GroupedMessagePosition groupedMessagePosition2 = this.posArray.get(i3);
                if (groupedMessagePosition2 != groupedMessagePosition && groupedMessagePosition2.maxY < i) {
                    for (int i4 = groupedMessagePosition2.minX; i4 <= groupedMessagePosition2.maxX; i4++) {
                        fArr[i4] = fArr[i4] + groupedMessagePosition2.ph;
                    }
                }
            }
            for (int i5 = 0; i5 < i2; i5++) {
                if (f < fArr[i5]) {
                    f = fArr[i5];
                }
            }
            return f;
        }
    }

    /* access modifiers changed from: package-private */
    public int getListTopPadding() {
        return this.listView.getPaddingTop();
    }

    /* access modifiers changed from: package-private */
    public int getCurrentItemTop() {
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            recyclerListView.setTopGlowOffset(recyclerListView.getPaddingTop());
            return Integer.MAX_VALUE;
        }
        View childAt = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
        int top = childAt.getTop();
        int dp = AndroidUtilities.dp(8.0f);
        if (top < AndroidUtilities.dp(8.0f) || holder == null || holder.getAdapterPosition() != 0) {
            top = dp;
        }
        this.listView.setTopGlowOffset(top);
        return top;
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0045  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0067  */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0039  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onPreMeasure(int r5, int r6) {
        /*
            r4 = this;
            r5 = 1
            r4.ignoreLayout = r5
            android.view.ViewGroup$LayoutParams r5 = r4.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r5 = (android.widget.FrameLayout.LayoutParams) r5
            int r0 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            r5.topMargin = r0
            boolean r5 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r5 != 0) goto L_0x0025
            android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r5.x
            int r5 = r5.y
            if (r0 <= r5) goto L_0x0025
            float r5 = (float) r6
            r6 = 1080033280(0x40600000, float:3.5)
            float r5 = r5 / r6
            int r5 = (int) r5
            r4.paddingTop = r5
            goto L_0x002b
        L_0x0025:
            int r6 = r6 / 5
            int r6 = r6 * 2
            r4.paddingTop = r6
        L_0x002b:
            int r5 = r4.paddingTop
            r6 = 1112539136(0x42500000, float:52.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            r4.paddingTop = r5
            r6 = 0
            if (r5 >= 0) goto L_0x003b
            r4.paddingTop = r6
        L_0x003b:
            org.telegram.ui.Components.RecyclerListView r5 = r4.listView
            int r5 = r5.getPaddingTop()
            int r0 = r4.paddingTop
            if (r5 == r0) goto L_0x005f
            org.telegram.ui.Components.RecyclerListView r5 = r4.listView
            int r0 = r5.getPaddingLeft()
            int r1 = r4.paddingTop
            org.telegram.ui.Components.RecyclerListView r2 = r4.listView
            int r2 = r2.getPaddingRight()
            org.telegram.ui.Components.RecyclerListView r3 = r4.listView
            int r3 = r3.getPaddingBottom()
            r5.setPadding(r0, r1, r2, r3)
            r4.invalidate()
        L_0x005f:
            android.widget.TextView r5 = r4.header
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 != 0) goto L_0x0072
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r0.x
            int r0 = r0.y
            if (r1 <= r0) goto L_0x0072
            r0 = 1099956224(0x41900000, float:18.0)
            goto L_0x0074
        L_0x0072:
            r0 = 1101004800(0x41a00000, float:20.0)
        L_0x0074:
            r5.setTextSize(r0)
            r4.ignoreLayout = r6
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.onPreMeasure(int, int):void");
    }

    /* access modifiers changed from: package-private */
    public void scrollToTop() {
        this.listView.smoothScrollToPosition(0);
    }

    /* access modifiers changed from: package-private */
    public boolean onBackPressed() {
        this.parentAlert.updatePhotoPreview(false);
        return true;
    }

    public void requestLayout() {
        if (!this.ignoreLayout) {
            super.requestLayout();
        }
    }

    /* access modifiers changed from: package-private */
    public void onMenuItemClick(int i) {
        try {
            this.parentAlert.getPhotoLayout().onMenuItemClick(i);
        } catch (Exception unused) {
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        Drawable wallpaperDrawable;
        int i;
        ChatActivity.ThemeDelegate themeDelegate2 = this.parentAlert.parentThemeDelegate;
        boolean z = false;
        if (!(themeDelegate2 == null || (wallpaperDrawable = themeDelegate2.getWallpaperDrawable()) == null)) {
            int currentItemTop = getCurrentItemTop();
            if (AndroidUtilities.isTablet()) {
                i = 16;
            } else {
                Point point = AndroidUtilities.displaySize;
                i = point.x > point.y ? 6 : 12;
            }
            if (currentItemTop < ActionBar.getCurrentActionBarHeight()) {
                currentItemTop -= AndroidUtilities.dp((1.0f - (((float) currentItemTop) / ((float) ActionBar.getCurrentActionBarHeight()))) * ((float) i));
            }
            int max = Math.max(0, currentItemTop);
            canvas.save();
            canvas.clipRect(0, max, getWidth(), getHeight());
            wallpaperDrawable.setBounds(0, 0, getWidth(), AndroidUtilities.displaySize.y);
            wallpaperDrawable.draw(canvas);
            z = true;
        }
        super.dispatchDraw(canvas);
        if (z) {
            canvas.restore();
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        Point point = AndroidUtilities.displaySize;
        boolean z2 = point.y > point.x;
        if (this.isPortrait != z2) {
            this.isPortrait = z2;
            int size = this.groupsView.groupCells.size();
            for (int i5 = 0; i5 < size; i5++) {
                PreviewGroupsView.PreviewGroupCell previewGroupCell = (PreviewGroupsView.PreviewGroupCell) this.groupsView.groupCells.get(i5);
                if (previewGroupCell.group.photos.size() == 1) {
                    previewGroupCell.setGroup(previewGroupCell.group, true);
                }
            }
        }
    }

    private class PreviewGroupsView extends ViewGroup {
        /* access modifiers changed from: private */
        public HashMap<Object, Object> deletedPhotos = new HashMap<>();
        /* access modifiers changed from: private */
        public ArrayList<PreviewGroupCell> groupCells = new ArrayList<>();
        private ChatActionCell hintView;
        boolean[] lastGroupSeen = null;
        private int lastMeasuredHeight = 0;
        private int paddingBottom = AndroidUtilities.dp(64.0f);
        private int paddingTop = AndroidUtilities.dp(16.0f);
        private final Runnable scroller = new Runnable() {
            public void run() {
                if (ChatAttachAlertPhotoLayoutPreview.this.draggingCell != null && !ChatAttachAlertPhotoLayoutPreview.this.draggingCellHiding) {
                    int computeVerticalScrollOffset = ChatAttachAlertPhotoLayoutPreview.this.listView.computeVerticalScrollOffset();
                    float f = 0.0f;
                    float max = Math.max(0.0f, (ChatAttachAlertPhotoLayoutPreview.this.draggingCellTouchY - ((float) Math.max(0, computeVerticalScrollOffset - ChatAttachAlertPhotoLayoutPreview.this.getListTopPadding()))) - ((float) AndroidUtilities.dp(52.0f)));
                    float max2 = Math.max(0.0f, ((((float) ChatAttachAlertPhotoLayoutPreview.this.listView.getMeasuredHeight()) - (ChatAttachAlertPhotoLayoutPreview.this.draggingCellTouchY - ((float) computeVerticalScrollOffset))) - ((float) ChatAttachAlertPhotoLayoutPreview.this.getListTopPadding())) - ((float) AndroidUtilities.dp(84.0f)));
                    float dp = (float) AndroidUtilities.dp(32.0f);
                    if (max < dp && computeVerticalScrollOffset > ChatAttachAlertPhotoLayoutPreview.this.getListTopPadding()) {
                        f = (-(1.0f - (max / dp))) * ((float) AndroidUtilities.dp(6.0f));
                    } else if (max2 < dp) {
                        f = (1.0f - (max2 / dp)) * ((float) AndroidUtilities.dp(6.0f));
                    }
                    int i = (int) f;
                    if (Math.abs(i) > 0 && ChatAttachAlertPhotoLayoutPreview.this.listView.canScrollVertically(i)) {
                        ChatAttachAlertPhotoLayoutPreview.access$1516(ChatAttachAlertPhotoLayoutPreview.this, f);
                        ChatAttachAlertPhotoLayoutPreview.this.listView.scrollBy(0, i);
                        PreviewGroupsView.this.invalidate();
                    }
                    boolean unused = PreviewGroupsView.this.scrollerStarted = true;
                    PreviewGroupsView.this.postDelayed(this, 15);
                }
            }
        };
        /* access modifiers changed from: private */
        public boolean scrollerStarted = false;
        PreviewGroupCell tapGroupCell = null;
        PreviewGroupCell.MediaCell tapMediaCell = null;
        long tapTime = 0;
        private int undoViewId = 0;
        float viewBottom;
        float viewTop;

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View view, long j) {
            return false;
        }

        public PreviewGroupsView(Context context) {
            super(context);
            new HashMap();
            setWillNotDraw(false);
            ChatActionCell chatActionCell = new ChatActionCell(context, false, ChatAttachAlertPhotoLayoutPreview.this.themeDelegate);
            this.hintView = chatActionCell;
            chatActionCell.setCustomText(LocaleController.getString("AttachMediaDragHint", NUM));
            addView(this.hintView);
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            ChatActionCell chatActionCell = this.hintView;
            chatActionCell.layout(0, 0, chatActionCell.getMeasuredWidth(), this.hintView.getMeasuredHeight());
        }

        public void saveDeletedImageId(MediaController.PhotoEntry photoEntry) {
            if (ChatAttachAlertPhotoLayoutPreview.this.photoLayout != null) {
                ArrayList arrayList = new ArrayList(ChatAttachAlertPhotoLayoutPreview.this.photoLayout.getSelectedPhotos().entrySet());
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    if (((Map.Entry) arrayList.get(i)).getValue() == photoEntry) {
                        this.deletedPhotos.put(photoEntry, ((Map.Entry) arrayList.get(i)).getKey());
                        return;
                    }
                }
            }
        }

        public void fromPhotoLayout(ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout) {
            ArrayList<Object> selectedPhotosOrder = chatAttachAlertPhotoLayout.getSelectedPhotosOrder();
            HashMap<Object, Object> selectedPhotos = chatAttachAlertPhotoLayout.getSelectedPhotos();
            this.groupCells.clear();
            ArrayList arrayList = new ArrayList();
            int size = selectedPhotosOrder.size();
            int i = size - 1;
            for (int i2 = 0; i2 < size; i2++) {
                arrayList.add((MediaController.PhotoEntry) selectedPhotos.get(Integer.valueOf(((Integer) selectedPhotosOrder.get(i2)).intValue())));
                if (i2 % 10 == 9 || i2 == i) {
                    PreviewGroupCell previewGroupCell = new PreviewGroupCell();
                    previewGroupCell.setGroup(new GroupCalculator(arrayList), false);
                    this.groupCells.add(previewGroupCell);
                    arrayList = new ArrayList();
                }
            }
        }

        public void toPhotoLayout(ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout, boolean z) {
            boolean z2;
            String str;
            int size = chatAttachAlertPhotoLayout.getSelectedPhotosOrder().size();
            ArrayList arrayList = new ArrayList(chatAttachAlertPhotoLayout.getSelectedPhotos().entrySet());
            HashMap hashMap = new HashMap();
            ArrayList arrayList2 = new ArrayList();
            int size2 = this.groupCells.size();
            for (int i = 0; i < size2; i++) {
                GroupCalculator access$500 = this.groupCells.get(i).group;
                if (access$500.photos.size() != 0) {
                    int size3 = access$500.photos.size();
                    for (int i2 = 0; i2 < size3; i2++) {
                        MediaController.PhotoEntry photoEntry = access$500.photos.get(i2);
                        if (this.deletedPhotos.containsKey(photoEntry)) {
                            Object obj = this.deletedPhotos.get(photoEntry);
                            hashMap.put(obj, photoEntry);
                            arrayList2.add(obj);
                        } else {
                            int i3 = 0;
                            while (true) {
                                if (i3 >= arrayList.size()) {
                                    z2 = false;
                                    break;
                                }
                                Map.Entry entry = (Map.Entry) arrayList.get(i3);
                                Object value = entry.getValue();
                                if (value == photoEntry) {
                                    Object key = entry.getKey();
                                    hashMap.put(key, value);
                                    arrayList2.add(key);
                                    z2 = true;
                                    break;
                                }
                                i3++;
                            }
                            if (!z2) {
                                int i4 = 0;
                                while (true) {
                                    if (i4 >= arrayList.size()) {
                                        break;
                                    }
                                    Map.Entry entry2 = (Map.Entry) arrayList.get(i4);
                                    Object value2 = entry2.getValue();
                                    if ((value2 instanceof MediaController.PhotoEntry) && (str = ((MediaController.PhotoEntry) value2).path) != null && photoEntry != null && str.equals(photoEntry.path)) {
                                        Object key2 = entry2.getKey();
                                        hashMap.put(key2, value2);
                                        arrayList2.add(key2);
                                        break;
                                    }
                                    i4++;
                                }
                            }
                        }
                    }
                }
            }
            chatAttachAlertPhotoLayout.updateSelected(hashMap, arrayList2, z);
            if (size != arrayList2.size()) {
                ChatAttachAlertPhotoLayoutPreview.this.parentAlert.updateCountButton(1);
            }
        }

        public int getPhotosCount() {
            int size = this.groupCells.size();
            int i = 0;
            for (int i2 = 0; i2 < size; i2++) {
                PreviewGroupCell previewGroupCell = this.groupCells.get(i2);
                if (!(previewGroupCell == null || previewGroupCell.group == null || previewGroupCell.group.photos == null)) {
                    i += previewGroupCell.group.photos.size();
                }
            }
            return i;
        }

        private int measureHeight() {
            int i = this.paddingTop + this.paddingBottom;
            int size = this.groupCells.size();
            for (int i2 = 0; i2 < size; i2++) {
                i = (int) (((float) i) + this.groupCells.get(i2).measure());
            }
            if (this.hintView.getMeasuredHeight() <= 0) {
                this.hintView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, NUM), View.MeasureSpec.makeMeasureSpec(9999, Integer.MIN_VALUE));
            }
            return Math.max((AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(45.0f), i + this.hintView.getMeasuredHeight());
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            this.hintView.measure(i, View.MeasureSpec.makeMeasureSpec(9999, Integer.MIN_VALUE));
            if (this.lastMeasuredHeight <= 0) {
                this.lastMeasuredHeight = measureHeight();
            }
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(Math.max(View.MeasureSpec.getSize(i2), this.lastMeasuredHeight), NUM));
        }

        public void invalidate() {
            int measureHeight = measureHeight();
            if (this.lastMeasuredHeight != measureHeight) {
                this.lastMeasuredHeight = measureHeight;
                requestLayout();
            }
            super.invalidate();
        }

        private boolean[] groupSeen() {
            boolean[] zArr = new boolean[this.groupCells.size()];
            float f = (float) this.paddingTop;
            int computeVerticalScrollOffset = ChatAttachAlertPhotoLayoutPreview.this.listView.computeVerticalScrollOffset();
            int i = 0;
            this.viewTop = (float) Math.max(0, computeVerticalScrollOffset - ChatAttachAlertPhotoLayoutPreview.this.getListTopPadding());
            this.viewBottom = (float) ((ChatAttachAlertPhotoLayoutPreview.this.listView.getMeasuredHeight() - ChatAttachAlertPhotoLayoutPreview.this.getListTopPadding()) + computeVerticalScrollOffset);
            int size = this.groupCells.size();
            while (i < size) {
                float measure = this.groupCells.get(i).measure() + f;
                zArr[i] = isSeen(f, measure);
                i++;
                f = measure;
            }
            return zArr;
        }

        public boolean isSeen(float f, float f2) {
            float f3 = this.viewTop;
            return (f >= f3 && f <= this.viewBottom) || (f2 >= f3 && f2 <= this.viewBottom) || (f <= f3 && f2 >= this.viewBottom);
        }

        public void onScroll() {
            int i = 0;
            boolean z = true;
            boolean z2 = this.lastGroupSeen == null;
            if (!z2) {
                boolean[] groupSeen = groupSeen();
                if (groupSeen.length == this.lastGroupSeen.length) {
                    while (true) {
                        if (i >= groupSeen.length) {
                            z = z2;
                            break;
                        } else if (groupSeen[i] != this.lastGroupSeen[i]) {
                            break;
                        } else {
                            i++;
                        }
                    }
                }
                z2 = z;
            } else {
                this.lastGroupSeen = groupSeen();
            }
            if (z2) {
                invalidate();
            }
        }

        public void remeasure() {
            float f = (float) this.paddingTop;
            int size = this.groupCells.size();
            int i = 0;
            for (int i2 = 0; i2 < size; i2++) {
                PreviewGroupCell previewGroupCell = this.groupCells.get(i2);
                float measure = previewGroupCell.measure();
                previewGroupCell.y = f;
                previewGroupCell.indexStart = i;
                f += measure;
                i += previewGroupCell.group.photos.size();
            }
        }

        public void onDraw(Canvas canvas) {
            float f = (float) this.paddingTop;
            int computeVerticalScrollOffset = ChatAttachAlertPhotoLayoutPreview.this.listView.computeVerticalScrollOffset();
            this.viewTop = (float) Math.max(0, computeVerticalScrollOffset - ChatAttachAlertPhotoLayoutPreview.this.getListTopPadding());
            this.viewBottom = (float) ((ChatAttachAlertPhotoLayoutPreview.this.listView.getMeasuredHeight() - ChatAttachAlertPhotoLayoutPreview.this.getListTopPadding()) + computeVerticalScrollOffset);
            canvas.save();
            canvas.translate(0.0f, (float) this.paddingTop);
            int size = this.groupCells.size();
            int i = 0;
            int i2 = 0;
            while (true) {
                boolean z = true;
                if (i >= size) {
                    break;
                }
                PreviewGroupCell previewGroupCell = this.groupCells.get(i);
                float measure = previewGroupCell.measure();
                previewGroupCell.y = f;
                previewGroupCell.indexStart = i2;
                float f2 = this.viewTop;
                if (f < f2 || f > this.viewBottom) {
                    float f3 = f + measure;
                    if ((f3 < f2 || f3 > this.viewBottom) && (f > f2 || f3 < this.viewBottom)) {
                        z = false;
                    }
                }
                if (z && previewGroupCell.draw(canvas)) {
                    invalidate();
                }
                canvas.translate(0.0f, measure);
                f += measure;
                i2 += previewGroupCell.group.photos.size();
                i++;
            }
            this.hintView.draw(canvas);
            canvas.restore();
            if (ChatAttachAlertPhotoLayoutPreview.this.draggingCell != null) {
                canvas.save();
                RectF rect = ChatAttachAlertPhotoLayoutPreview.this.draggingCell.rect();
                RectF rect2 = ChatAttachAlertPhotoLayoutPreview.this.draggingCell.rect(1.0f);
                canvas.translate(AndroidUtilities.lerp(rect2.left + (rect.width() / 2.0f), ChatAttachAlertPhotoLayoutPreview.this.draggingCellTouchX - ((ChatAttachAlertPhotoLayoutPreview.this.draggingCellLeft - 0.5f) * ChatAttachAlertPhotoLayoutPreview.this.draggingCellFromWidth), ChatAttachAlertPhotoLayoutPreview.this.draggingT), AndroidUtilities.lerp(ChatAttachAlertPhotoLayoutPreview.this.draggingCell.groupCell.y + rect2.top + (rect.height() / 2.0f), (ChatAttachAlertPhotoLayoutPreview.this.draggingCellGroupY + ChatAttachAlertPhotoLayoutPreview.this.draggingCellTouchY) - ((ChatAttachAlertPhotoLayoutPreview.this.draggingCellTop - 0.5f) * ChatAttachAlertPhotoLayoutPreview.this.draggingCellFromHeight), ChatAttachAlertPhotoLayoutPreview.this.draggingT));
                if (ChatAttachAlertPhotoLayoutPreview.this.draggingCell.draw(canvas, true)) {
                    invalidate();
                }
                canvas.restore();
            }
            super.onDraw(canvas);
        }

        /* access modifiers changed from: package-private */
        public void stopDragging() {
            if (ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator != null) {
                ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.cancel();
            }
            boolean unused = ChatAttachAlertPhotoLayoutPreview.this.draggingCellHiding = true;
            ChatAttachAlertPhotoLayoutPreview chatAttachAlertPhotoLayoutPreview = ChatAttachAlertPhotoLayoutPreview.this;
            ValueAnimator unused2 = chatAttachAlertPhotoLayoutPreview.draggingAnimator = ValueAnimator.ofFloat(new float[]{chatAttachAlertPhotoLayoutPreview.draggingT, 0.0f});
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.addUpdateListener(new ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda0(this));
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    PreviewGroupCell.MediaCell unused = ChatAttachAlertPhotoLayoutPreview.this.draggingCell = null;
                    PreviewGroupCell unused2 = ChatAttachAlertPhotoLayoutPreview.this.draggingFromGroupCell = null;
                    boolean unused3 = ChatAttachAlertPhotoLayoutPreview.this.draggingCellHiding = false;
                    PreviewGroupsView.this.invalidate();
                }
            });
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.setDuration((long) (Math.abs(ChatAttachAlertPhotoLayoutPreview.this.draggingT) * 180.0f));
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.start();
            invalidate();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$stopDragging$0(ValueAnimator valueAnimator) {
            float unused = ChatAttachAlertPhotoLayoutPreview.this.draggingT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
        }

        /* access modifiers changed from: package-private */
        public void startDragging(PreviewGroupCell.MediaCell mediaCell) {
            PreviewGroupCell.MediaCell unused = ChatAttachAlertPhotoLayoutPreview.this.draggingCell = mediaCell;
            ChatAttachAlertPhotoLayoutPreview chatAttachAlertPhotoLayoutPreview = ChatAttachAlertPhotoLayoutPreview.this;
            PreviewGroupCell unused2 = chatAttachAlertPhotoLayoutPreview.draggingFromGroupCell = chatAttachAlertPhotoLayoutPreview.draggingCell.groupCell;
            ChatAttachAlertPhotoLayoutPreview chatAttachAlertPhotoLayoutPreview2 = ChatAttachAlertPhotoLayoutPreview.this;
            float unused3 = chatAttachAlertPhotoLayoutPreview2.draggingCellGroupY = chatAttachAlertPhotoLayoutPreview2.draggingCell.groupCell.y;
            boolean unused4 = ChatAttachAlertPhotoLayoutPreview.this.draggingCellHiding = false;
            float unused5 = ChatAttachAlertPhotoLayoutPreview.this.draggingT = 0.0f;
            invalidate();
            if (ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator != null) {
                ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.cancel();
            }
            ValueAnimator unused6 = ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.addUpdateListener(new ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda1(this));
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.setDuration((long) (Math.abs(1.0f - ChatAttachAlertPhotoLayoutPreview.this.draggingT) * 180.0f));
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.start();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$startDragging$1(ValueAnimator valueAnimator) {
            float unused = ChatAttachAlertPhotoLayoutPreview.this.draggingT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
        }

        /* JADX WARNING: Removed duplicated region for block: B:123:0x03d4  */
        /* JADX WARNING: Removed duplicated region for block: B:19:0x0060  */
        /* JADX WARNING: Removed duplicated region for block: B:43:0x019c  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouchEvent(android.view.MotionEvent r19) {
            /*
                r18 = this;
                r0 = r18
                float r1 = r19.getX()
                float r2 = r19.getY()
                java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell> r3 = r0.groupCells
                int r3 = r3.size()
                r6 = 0
                r7 = 0
            L_0x0012:
                if (r6 >= r3) goto L_0x002f
                java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell> r9 = r0.groupCells
                java.lang.Object r9 = r9.get(r6)
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell r9 = (org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.PreviewGroupCell) r9
                float r10 = r9.measure()
                int r11 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
                if (r11 < 0) goto L_0x002b
                float r11 = r7 + r10
                int r11 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1))
                if (r11 > 0) goto L_0x002b
                goto L_0x0030
            L_0x002b:
                float r7 = r7 + r10
                int r6 = r6 + 1
                goto L_0x0012
            L_0x002f:
                r9 = 0
            L_0x0030:
                if (r9 == 0) goto L_0x0055
                java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell> r6 = r9.media
                int r6 = r6.size()
                r10 = 0
            L_0x0039:
                if (r10 >= r6) goto L_0x0055
                java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell> r11 = r9.media
                java.lang.Object r11 = r11.get(r10)
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r11 = (org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.PreviewGroupCell.MediaCell) r11
                if (r11 == 0) goto L_0x0052
                android.graphics.RectF r12 = r11.drawingRect()
                float r13 = r2 - r7
                boolean r12 = r12.contains(r1, r13)
                if (r12 == 0) goto L_0x0052
                goto L_0x0056
            L_0x0052:
                int r10 = r10 + 1
                goto L_0x0039
            L_0x0055:
                r11 = 0
            L_0x0056:
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r6 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r6 = r6.draggingCell
                r7 = 1065353216(0x3var_, float:1.0)
                if (r6 == 0) goto L_0x019c
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r6 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r6 = r6.draggingCell
                android.graphics.RectF r6 = r6.rect()
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r10 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r10 = r10.draggingCell
                android.graphics.RectF r10 = r10.rect(r7)
                android.graphics.RectF r12 = new android.graphics.RectF
                r12.<init>()
                float r13 = r10.left
                float r14 = r6.width()
                r15 = 1073741824(0x40000000, float:2.0)
                float r14 = r14 / r15
                float r13 = r13 + r14
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r14 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                float r14 = r14.draggingCellTouchX
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r4 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                float r4 = r4.draggingCellLeft
                r16 = 1056964608(0x3var_, float:0.5)
                float r4 = r4 - r16
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r5 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                float r5 = r5.draggingCellFromWidth
                float r4 = r4 * r5
                float r14 = r14 - r4
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r4 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                float r4 = r4.draggingT
                float r4 = org.telegram.messenger.AndroidUtilities.lerp((float) r13, (float) r14, (float) r4)
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r5 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r5 = r5.draggingCell
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell r5 = r5.groupCell
                float r5 = r5.y
                float r10 = r10.top
                float r5 = r5 + r10
                float r10 = r6.height()
                float r10 = r10 / r15
                float r5 = r5 + r10
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r10 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                float r10 = r10.draggingCellGroupY
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r13 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                float r13 = r13.draggingCellTouchY
                float r10 = r10 + r13
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r13 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                float r13 = r13.draggingCellTop
                float r13 = r13 - r16
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r14 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                float r14 = r14.draggingCellFromHeight
                float r13 = r13 * r14
                float r10 = r10 - r13
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r13 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                float r13 = r13.draggingT
                float r5 = org.telegram.messenger.AndroidUtilities.lerp((float) r5, (float) r10, (float) r13)
                float r10 = r6.width()
                float r10 = r10 / r15
                float r10 = r4 - r10
                float r13 = r6.height()
                float r13 = r13 / r15
                float r13 = r5 - r13
                float r14 = r6.width()
                float r14 = r14 / r15
                float r4 = r4 + r14
                float r6 = r6.height()
                float r6 = r6 / r15
                float r5 = r5 + r6
                r12.set(r10, r13, r4, r5)
                r4 = 0
                r5 = 0
                r6 = 0
                r10 = 0
            L_0x0102:
                if (r4 >= r3) goto L_0x0134
                java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell> r13 = r0.groupCells
                java.lang.Object r13 = r13.get(r4)
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell r13 = (org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.PreviewGroupCell) r13
                float r14 = r13.measure()
                float r14 = r14 + r6
                float r15 = r12.top
                int r15 = (r14 > r15 ? 1 : (r14 == r15 ? 0 : -1))
                if (r15 < 0) goto L_0x012e
                float r15 = r12.bottom
                int r16 = (r15 > r6 ? 1 : (r15 == r6 ? 0 : -1))
                if (r16 < 0) goto L_0x012e
                float r15 = java.lang.Math.min(r14, r15)
                float r7 = r12.top
                float r6 = java.lang.Math.max(r6, r7)
                float r15 = r15 - r6
                int r6 = (r15 > r10 ? 1 : (r15 == r10 ? 0 : -1))
                if (r6 <= 0) goto L_0x012e
                r5 = r13
                r10 = r15
            L_0x012e:
                int r4 = r4 + 1
                r6 = r14
                r7 = 1065353216(0x3var_, float:1.0)
                goto L_0x0102
            L_0x0134:
                if (r5 == 0) goto L_0x019d
                java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell> r3 = r5.media
                int r3 = r3.size()
                r4 = 0
                r6 = 0
                r7 = 0
            L_0x013f:
                if (r4 >= r3) goto L_0x019e
                java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell> r10 = r5.media
                java.lang.Object r10 = r10.get(r4)
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r10 = (org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.PreviewGroupCell.MediaCell) r10
                if (r10 == 0) goto L_0x0195
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r13 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r13 = r13.draggingCell
                if (r10 == r13) goto L_0x0195
                android.graphics.RectF r13 = r10.drawingRect()
                boolean r14 = android.graphics.RectF.intersects(r12, r13)
                if (r14 == 0) goto L_0x0195
                float r14 = r13.right
                float r15 = r12.right
                float r14 = java.lang.Math.min(r14, r15)
                float r15 = r13.left
                float r8 = r12.left
                float r8 = java.lang.Math.max(r15, r8)
                float r14 = r14 - r8
                float r8 = r13.bottom
                float r15 = r12.bottom
                float r8 = java.lang.Math.min(r8, r15)
                float r15 = r13.top
                r17 = r3
                float r3 = r12.top
                float r3 = java.lang.Math.max(r15, r3)
                float r8 = r8 - r3
                float r14 = r14 * r8
                float r3 = r13.width()
                float r8 = r13.height()
                float r3 = r3 * r8
                float r14 = r14 / r3
                int r3 = (r14 > r7 ? 1 : (r14 == r7 ? 0 : -1))
                if (r3 <= 0) goto L_0x0197
                r6 = r10
                r7 = r14
                goto L_0x0197
            L_0x0195:
                r17 = r3
            L_0x0197:
                int r4 = r4 + 1
                r3 = r17
                goto L_0x013f
            L_0x019c:
                r5 = 0
            L_0x019d:
                r6 = 0
            L_0x019e:
                int r3 = r19.getAction()
                r7 = 0
                r4 = 1
                if (r3 != 0) goto L_0x01ff
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r10 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r10 = r10.draggingCell
                if (r10 != 0) goto L_0x01ff
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r10 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.RecyclerListView r12 = r10.listView
                boolean r12 = r12.scrollingByUser
                if (r12 != 0) goto L_0x01ff
                android.animation.ValueAnimator r10 = r10.draggingAnimator
                if (r10 == 0) goto L_0x01c9
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r10 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                android.animation.ValueAnimator r10 = r10.draggingAnimator
                boolean r10 = r10.isRunning()
                if (r10 != 0) goto L_0x01ff
            L_0x01c9:
                if (r9 == 0) goto L_0x01ff
                if (r11 == 0) goto L_0x01ff
                r0.tapGroupCell = r9
                r0.tapMediaCell = r11
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r5 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                float unused = r5.draggingCellTouchX = r1
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                float unused = r1.draggingCellTouchY = r2
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                r2 = 0
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.PreviewGroupCell.MediaCell unused = r1.draggingCell = r2
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.PreviewGroupCell unused = r1.draggingFromGroupCell = r2
                long r1 = android.os.SystemClock.elapsedRealtime()
                r0.tapTime = r1
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r5 = r0.tapMediaCell
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda3 r6 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda3
                r6.<init>(r0, r1, r5)
                int r1 = android.view.ViewConfiguration.getLongPressTimeout()
                long r1 = (long) r1
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r6, r1)
                r18.invalidate()
                goto L_0x022c
            L_0x01ff:
                r9 = 2
                if (r3 != r9) goto L_0x022f
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r10 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r10 = r10.draggingCell
                if (r10 == 0) goto L_0x022f
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r10 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                boolean r10 = r10.draggingCellHiding
                if (r10 != 0) goto L_0x022f
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r5 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                float unused = r5.draggingCellTouchX = r1
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                float unused = r1.draggingCellTouchY = r2
                boolean r1 = r0.scrollerStarted
                if (r1 != 0) goto L_0x0229
                r0.scrollerStarted = r4
                java.lang.Runnable r1 = r0.scroller
                r5 = 16
                r0.postDelayed(r1, r5)
            L_0x0229:
                r18.invalidate()
            L_0x022c:
                r2 = 1
                goto L_0x03c3
            L_0x022f:
                if (r3 != r4) goto L_0x032f
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r1 = r1.draggingCell
                if (r1 == 0) goto L_0x032f
                if (r5 == 0) goto L_0x0246
                if (r6 == 0) goto L_0x0246
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r1 = r1.draggingCell
                if (r6 == r1) goto L_0x0246
                goto L_0x0248
            L_0x0246:
                r5 = 0
                r6 = 0
            L_0x0248:
                if (r5 == 0) goto L_0x032a
                if (r6 == 0) goto L_0x032a
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r1 = r1.draggingCell
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell r1 = r1.groupCell
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator r1 = r1.group
                java.util.ArrayList<org.telegram.messenger.MediaController$PhotoEntry> r1 = r1.photos
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r2 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r2 = r2.draggingCell
                org.telegram.messenger.MediaController$PhotoEntry r2 = r2.photoEntry
                int r1 = r1.indexOf(r2)
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator r2 = r5.group
                java.util.ArrayList<org.telegram.messenger.MediaController$PhotoEntry> r2 = r2.photos
                org.telegram.messenger.MediaController$PhotoEntry r6 = r6.photoEntry
                int r2 = r2.indexOf(r6)
                if (r1 < 0) goto L_0x029c
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r6 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r6 = r6.draggingCell
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell r6 = r6.groupCell
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator r6 = r6.group
                java.util.ArrayList<org.telegram.messenger.MediaController$PhotoEntry> r6 = r6.photos
                r6.remove(r1)
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r1 = r1.draggingCell
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell r1 = r1.groupCell
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r6 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r6 = r6.draggingCell
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell r6 = r6.groupCell
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator r6 = r6.group
                r1.setGroup(r6, r4)
            L_0x029c:
                if (r2 < 0) goto L_0x0317
                java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell> r1 = r0.groupCells
                int r1 = r1.indexOf(r5)
                java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell> r6 = r0.groupCells
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r10 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r10 = r10.draggingCell
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell r10 = r10.groupCell
                int r6 = r6.indexOf(r10)
                if (r1 <= r6) goto L_0x02b6
                int r2 = r2 + 1
            L_0x02b6:
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r1 = r1.draggingCell
                org.telegram.messenger.MediaController$PhotoEntry r1 = r1.photoEntry
                r0.pushToGroup(r5, r1, r2)
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r1 = r1.draggingCell
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell r1 = r1.groupCell
                if (r1 == r5) goto L_0x0317
                java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell> r1 = r5.media
                int r1 = r1.size()
                r2 = 0
            L_0x02d2:
                if (r2 >= r1) goto L_0x02ec
                java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell> r6 = r5.media
                java.lang.Object r6 = r6.get(r2)
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r6 = (org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.PreviewGroupCell.MediaCell) r6
                org.telegram.messenger.MediaController$PhotoEntry r10 = r6.photoEntry
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r11 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r11 = r11.draggingCell
                org.telegram.messenger.MediaController$PhotoEntry r11 = r11.photoEntry
                if (r10 != r11) goto L_0x02e9
                goto L_0x02ed
            L_0x02e9:
                int r2 = r2 + 1
                goto L_0x02d2
            L_0x02ec:
                r6 = 0
            L_0x02ed:
                if (r6 == 0) goto L_0x0317
                r18.remeasure()
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r1 = r1.draggingCell
                r6.layoutFrom(r1)
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.PreviewGroupCell.MediaCell unused = r1.draggingCell = r6
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell r1 = r1.draggingFromGroupCell = r5
                r6.groupCell = r1
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r1 = r1.draggingCell
                r2 = 1065353216(0x3var_, float:1.0)
                r1.fromScale = r2
                r6.scale = r2
                r18.remeasure()
            L_0x0317:
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this     // Catch:{ Exception -> 0x031d }
                r2 = 7
                r1.performHapticFeedback(r2, r9)     // Catch:{ Exception -> 0x031d }
            L_0x031d:
                r18.updateGroups()
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayout r1 = r1.photoLayout
                r2 = 0
                r0.toPhotoLayout(r1, r2)
            L_0x032a:
                r18.stopDragging()
                goto L_0x022c
            L_0x032f:
                if (r3 != r4) goto L_0x03c2
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r1 = r1.draggingCell
                if (r1 != 0) goto L_0x03c2
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r1 = r0.tapMediaCell
                if (r1 == 0) goto L_0x03c2
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell r1 = r0.tapGroupCell
                if (r1 == 0) goto L_0x03c2
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                int r1 = r1.getSelectedItemsCount()
                if (r1 <= r4) goto L_0x03b0
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r1 = r0.tapMediaCell
                org.telegram.messenger.MediaController$PhotoEntry r13 = r1.photoEntry
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell r1 = r0.tapGroupCell
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator r1 = r1.group
                java.util.ArrayList<org.telegram.messenger.MediaController$PhotoEntry> r1 = r1.photos
                int r1 = r1.indexOf(r13)
                if (r1 < 0) goto L_0x039f
                r0.saveDeletedImageId(r13)
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell r2 = r0.tapGroupCell
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator r5 = r2.group
                java.util.ArrayList<org.telegram.messenger.MediaController$PhotoEntry> r5 = r5.photos
                r5.remove(r1)
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator r5 = r2.group
                r2.setGroup(r5, r4)
                r18.updateGroups()
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r5 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayout r5 = r5.photoLayout
                r6 = 0
                r0.toPhotoLayout(r5, r6)
                int r5 = r0.undoViewId
                int r5 = r5 + r4
                r0.undoViewId = r5
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r6 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.UndoView r9 = r6.undoView
                r10 = 0
                r12 = 82
                r14 = 0
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda4 r15 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda4
                r15.<init>(r0, r2, r13, r1)
                r9.showWithAction(r10, r12, r13, r14, r15)
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda2 r1 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda2
                r1.<init>(r0, r5)
                r5 = 4000(0xfa0, double:1.9763E-320)
                r0.postDelayed(r1, r5)
            L_0x039f:
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                android.animation.ValueAnimator r1 = r1.draggingAnimator
                if (r1 == 0) goto L_0x03b0
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                android.animation.ValueAnimator r1 = r1.draggingAnimator
                r1.cancel()
            L_0x03b0:
                r1 = 0
                r0.tapMediaCell = r1
                r0.tapTime = r7
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r2 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.PreviewGroupCell.MediaCell unused = r2.draggingCell = r1
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                r2 = 0
                float unused = r1.draggingT = r2
                goto L_0x022c
            L_0x03c2:
                r2 = 0
            L_0x03c3:
                if (r3 == r4) goto L_0x03c8
                r1 = 3
                if (r3 != r1) goto L_0x03d8
            L_0x03c8:
                r0.tapTime = r7
                java.lang.Runnable r1 = r0.scroller
                r0.removeCallbacks(r1)
                r1 = 0
                r0.scrollerStarted = r1
                if (r2 != 0) goto L_0x03d8
                r18.stopDragging()
                goto L_0x03d9
            L_0x03d8:
                r4 = r2
            L_0x03d9:
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.onTouchEvent(android.view.MotionEvent):boolean");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onTouchEvent$2(long j, PreviewGroupCell.MediaCell mediaCell) {
            PreviewGroupCell.MediaCell mediaCell2;
            if (!ChatAttachAlertPhotoLayoutPreview.this.listView.scrollingByUser && this.tapTime == j && (mediaCell2 = this.tapMediaCell) == mediaCell) {
                startDragging(mediaCell2);
                RectF rect = ChatAttachAlertPhotoLayoutPreview.this.draggingCell.rect();
                RectF drawingRect = ChatAttachAlertPhotoLayoutPreview.this.draggingCell.drawingRect();
                ChatAttachAlertPhotoLayoutPreview chatAttachAlertPhotoLayoutPreview = ChatAttachAlertPhotoLayoutPreview.this;
                float unused = chatAttachAlertPhotoLayoutPreview.draggingCellLeft = (chatAttachAlertPhotoLayoutPreview.draggingCellTouchX - rect.left) / rect.width();
                ChatAttachAlertPhotoLayoutPreview chatAttachAlertPhotoLayoutPreview2 = ChatAttachAlertPhotoLayoutPreview.this;
                float unused2 = chatAttachAlertPhotoLayoutPreview2.draggingCellTop = (chatAttachAlertPhotoLayoutPreview2.draggingCellTouchY - rect.top) / rect.height();
                float unused3 = ChatAttachAlertPhotoLayoutPreview.this.draggingCellFromWidth = drawingRect.width();
                float unused4 = ChatAttachAlertPhotoLayoutPreview.this.draggingCellFromHeight = drawingRect.height();
                try {
                    ChatAttachAlertPhotoLayoutPreview.this.performHapticFeedback(0, 2);
                } catch (Exception unused5) {
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onTouchEvent$3(PreviewGroupCell previewGroupCell, MediaController.PhotoEntry photoEntry, int i) {
            if (ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator != null) {
                ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.cancel();
            }
            PreviewGroupCell.MediaCell unused = ChatAttachAlertPhotoLayoutPreview.this.draggingCell = null;
            float unused2 = ChatAttachAlertPhotoLayoutPreview.this.draggingT = 0.0f;
            pushToGroup(previewGroupCell, photoEntry, i);
            updateGroups();
            toPhotoLayout(ChatAttachAlertPhotoLayoutPreview.this.photoLayout, false);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onTouchEvent$4(int i) {
            if (i == this.undoViewId && ChatAttachAlertPhotoLayoutPreview.this.undoView.isShown()) {
                ChatAttachAlertPhotoLayoutPreview.this.undoView.hide(true, 1);
            }
        }

        private void pushToGroup(PreviewGroupCell previewGroupCell, MediaController.PhotoEntry photoEntry, int i) {
            previewGroupCell.group.photos.add(i, photoEntry);
            if (previewGroupCell.group.photos.size() == 11) {
                MediaController.PhotoEntry photoEntry2 = previewGroupCell.group.photos.get(10);
                previewGroupCell.group.photos.remove(10);
                int indexOf = this.groupCells.indexOf(previewGroupCell);
                if (indexOf >= 0) {
                    int i2 = indexOf + 1;
                    PreviewGroupCell previewGroupCell2 = i2 == this.groupCells.size() ? null : this.groupCells.get(i2);
                    if (previewGroupCell2 == null) {
                        PreviewGroupCell previewGroupCell3 = new PreviewGroupCell();
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(photoEntry2);
                        previewGroupCell3.setGroup(new GroupCalculator(arrayList), true);
                        invalidate();
                    } else {
                        pushToGroup(previewGroupCell2, photoEntry2, 0);
                    }
                }
            }
            previewGroupCell.setGroup(previewGroupCell.group, true);
        }

        private void updateGroups() {
            int size = this.groupCells.size();
            for (int i = 0; i < size; i++) {
                PreviewGroupCell previewGroupCell = this.groupCells.get(i);
                if (previewGroupCell.group.photos.size() < 10 && i < this.groupCells.size() - 1) {
                    int size2 = 10 - previewGroupCell.group.photos.size();
                    PreviewGroupCell previewGroupCell2 = this.groupCells.get(i + 1);
                    ArrayList arrayList = new ArrayList();
                    int min = Math.min(size2, previewGroupCell2.group.photos.size());
                    for (int i2 = 0; i2 < min; i2++) {
                        arrayList.add(previewGroupCell2.group.photos.remove(0));
                    }
                    previewGroupCell.group.photos.addAll(arrayList);
                    previewGroupCell.setGroup(previewGroupCell.group, true);
                    previewGroupCell2.setGroup(previewGroupCell2.group, true);
                }
            }
        }

        private class PreviewGroupCell {
            private Theme.MessageDrawable.PathDrawParams backgroundCacheParams;
            private float bottom;
            final int gap;
            /* access modifiers changed from: private */
            public GroupCalculator group;
            private float groupHeight;
            private float groupWidth;
            final int halfGap;
            /* access modifiers changed from: private */
            public float height;
            public int indexStart;
            /* access modifiers changed from: private */
            public Interpolator interpolator;
            private long lastMediaUpdate;
            /* access modifiers changed from: private */
            public float left;
            public ArrayList<MediaCell> media;
            private Theme.MessageDrawable messageBackground;
            final int padding;
            private float previousGroupHeight;
            private float previousGroupWidth;
            private float right;
            /* access modifiers changed from: private */
            public float top;
            /* access modifiers changed from: private */
            public float width;
            public float y;

            private PreviewGroupCell() {
                this.y = 0.0f;
                this.indexStart = 0;
                this.lastMediaUpdate = 0;
                this.groupWidth = 0.0f;
                this.groupHeight = 0.0f;
                this.previousGroupWidth = 0.0f;
                this.previousGroupHeight = 0.0f;
                this.media = new ArrayList<>();
                this.interpolator = CubicBezierInterpolator.EASE_BOTH;
                this.padding = AndroidUtilities.dp(4.0f);
                int dp = AndroidUtilities.dp(2.0f);
                this.gap = dp;
                this.halfGap = dp / 2;
                this.messageBackground = (Theme.MessageDrawable) ChatAttachAlertPhotoLayoutPreview.this.getThemedDrawable("drawableMsgOutMedia");
                this.backgroundCacheParams = new Theme.MessageDrawable.PathDrawParams();
                new Path();
            }

            private class MediaCell {
                private Paint bitmapPaint;
                private Rect durationIn;
                private Rect durationOut;
                private RectF fromRect;
                public RectF fromRoundRadiuses;
                public float fromScale;
                public PreviewGroupCell groupCell;
                public ImageReceiver image;
                private Bitmap indexBitmap;
                private String indexBitmapText;
                private Rect indexIn;
                private Rect indexOut;
                /* access modifiers changed from: private */
                public long lastUpdate;
                private Paint paint;
                public MediaController.PhotoEntry photoEntry;
                private int positionFlags;
                public RectF rect;
                public RectF roundRadiuses;
                public float scale;
                private Paint strokePaint;
                private RectF tempRect;
                private TextPaint textPaint;
                private Bitmap videoDurationBitmap;
                private String videoDurationBitmapText;
                private String videoDurationText;
                private TextPaint videoDurationTextPaint;

                private MediaCell() {
                    this.groupCell = PreviewGroupCell.this;
                    this.fromRect = null;
                    this.rect = new RectF();
                    this.lastUpdate = 0;
                    this.positionFlags = 0;
                    this.fromScale = 1.0f;
                    this.scale = 0.0f;
                    this.fromRoundRadiuses = null;
                    this.roundRadiuses = new RectF();
                    this.videoDurationText = null;
                    this.tempRect = new RectF();
                    this.paint = new Paint(1);
                    this.strokePaint = new Paint(1);
                    this.bitmapPaint = new Paint(1);
                    this.indexBitmap = null;
                    this.indexBitmapText = null;
                    this.videoDurationBitmap = null;
                    this.videoDurationBitmapText = null;
                    this.indexIn = new Rect();
                    this.indexOut = new Rect();
                    this.durationIn = new Rect();
                    this.durationOut = new Rect();
                }

                /* access modifiers changed from: private */
                public void setImage(MediaController.PhotoEntry photoEntry2) {
                    MediaController.PhotoEntry photoEntry3 = photoEntry2;
                    this.photoEntry = photoEntry3;
                    if (photoEntry3 == null || !photoEntry3.isVideo) {
                        this.videoDurationText = null;
                    } else {
                        this.videoDurationText = AndroidUtilities.formatShortDuration(photoEntry3.duration);
                    }
                    ImageReceiver imageReceiver = new ImageReceiver(PreviewGroupsView.this);
                    this.image = imageReceiver;
                    if (photoEntry3 != null) {
                        String str = photoEntry3.thumbPath;
                        if (str != null) {
                            imageReceiver.setImage(ImageLocation.getForPath(str), (String) null, (ImageLocation) null, (String) null, Theme.chat_attachEmptyDrawable, 0, (String) null, (Object) null, 0);
                        } else if (photoEntry3.path == null) {
                            imageReceiver.setImageBitmap(Theme.chat_attachEmptyDrawable);
                        } else if (photoEntry3.isVideo) {
                            imageReceiver.setImage(ImageLocation.getForPath("vthumb://" + photoEntry3.imageId + ":" + photoEntry3.path), (String) null, (ImageLocation) null, (String) null, Theme.chat_attachEmptyDrawable, 0, (String) null, (Object) null, 0);
                            this.image.setAllowStartAnimation(true);
                        } else {
                            imageReceiver.setOrientation(photoEntry3.orientation, true);
                            ImageReceiver imageReceiver2 = this.image;
                            imageReceiver2.setImage(ImageLocation.getForPath("thumb://" + photoEntry3.imageId + ":" + photoEntry3.path), (String) null, (ImageLocation) null, (String) null, Theme.chat_attachEmptyDrawable, 0, (String) null, (Object) null, 0);
                        }
                    }
                }

                /* access modifiers changed from: private */
                public void layoutFrom(MediaCell mediaCell) {
                    this.fromScale = AndroidUtilities.lerp(mediaCell.fromScale, mediaCell.scale, mediaCell.getT());
                    if (this.fromRect == null) {
                        this.fromRect = new RectF();
                    }
                    RectF rectF = new RectF();
                    RectF rectF2 = this.fromRect;
                    if (rectF2 == null) {
                        rectF.set(this.rect);
                    } else {
                        AndroidUtilities.lerp(rectF2, this.rect, getT(), rectF);
                    }
                    RectF rectF3 = mediaCell.fromRect;
                    if (rectF3 != null) {
                        AndroidUtilities.lerp(rectF3, mediaCell.rect, mediaCell.getT(), this.fromRect);
                        this.fromRect.set(rectF.centerX() - (((this.fromRect.width() / 2.0f) * mediaCell.groupCell.width) / PreviewGroupCell.this.width), rectF.centerY() - (((this.fromRect.height() / 2.0f) * mediaCell.groupCell.height) / PreviewGroupCell.this.height), rectF.centerX() + (((this.fromRect.width() / 2.0f) * mediaCell.groupCell.width) / PreviewGroupCell.this.width), rectF.centerY() + (((this.fromRect.height() / 2.0f) * mediaCell.groupCell.height) / PreviewGroupCell.this.height));
                    } else {
                        this.fromRect.set(rectF.centerX() - (((mediaCell.rect.width() / 2.0f) * mediaCell.groupCell.width) / PreviewGroupCell.this.width), rectF.centerY() - (((mediaCell.rect.height() / 2.0f) * mediaCell.groupCell.height) / PreviewGroupCell.this.height), rectF.centerX() + (((mediaCell.rect.width() / 2.0f) * mediaCell.groupCell.width) / PreviewGroupCell.this.width), rectF.centerY() + (((mediaCell.rect.height() / 2.0f) * mediaCell.groupCell.height) / PreviewGroupCell.this.height));
                    }
                    this.fromScale = AndroidUtilities.lerp(this.fromScale, this.scale, getT());
                    this.lastUpdate = SystemClock.elapsedRealtime();
                }

                /* access modifiers changed from: private */
                public void layout(GroupCalculator groupCalculator, MessageObject.GroupedMessagePosition groupedMessagePosition, boolean z) {
                    if (groupCalculator != null && groupedMessagePosition != null) {
                        this.positionFlags = groupedMessagePosition.flags;
                        if (z) {
                            long elapsedRealtime = SystemClock.elapsedRealtime();
                            float interpolation = PreviewGroupCell.this.interpolator.getInterpolation(Math.min(1.0f, ((float) (elapsedRealtime - this.lastUpdate)) / 200.0f));
                            RectF rectF = this.fromRect;
                            if (rectF != null) {
                                AndroidUtilities.lerp(rectF, this.rect, interpolation, rectF);
                            }
                            RectF rectF2 = this.fromRoundRadiuses;
                            if (rectF2 != null) {
                                AndroidUtilities.lerp(rectF2, this.roundRadiuses, interpolation, rectF2);
                            }
                            this.fromScale = AndroidUtilities.lerp(this.fromScale, this.scale, interpolation);
                            this.lastUpdate = elapsedRealtime;
                        }
                        float f = groupedMessagePosition.left;
                        int i = groupCalculator.width;
                        float f2 = f / ((float) i);
                        float f3 = groupedMessagePosition.top;
                        float f4 = groupCalculator.height;
                        float f5 = f3 / f4;
                        float f6 = groupedMessagePosition.ph / f4;
                        this.scale = 1.0f;
                        this.rect.set(f2, f5, (((float) groupedMessagePosition.pw) / ((float) i)) + f2, f6 + f5);
                        float dp = (float) AndroidUtilities.dp(2.0f);
                        float dp2 = (float) AndroidUtilities.dp((float) (SharedConfig.bubbleRadius - 1));
                        RectF rectF3 = this.roundRadiuses;
                        int i2 = this.positionFlags;
                        float f7 = (i2 & 5) == 5 ? dp2 : dp;
                        float f8 = (i2 & 6) == 6 ? dp2 : dp;
                        float f9 = (i2 & 10) == 10 ? dp2 : dp;
                        if ((i2 & 9) == 9) {
                            dp = dp2;
                        }
                        rectF3.set(f7, f8, f9, dp);
                        if (this.fromRect == null) {
                            RectF rectF4 = new RectF();
                            this.fromRect = rectF4;
                            rectF4.set(this.rect);
                        }
                        if (this.fromRoundRadiuses == null) {
                            RectF rectF5 = new RectF();
                            this.fromRoundRadiuses = rectF5;
                            rectF5.set(this.roundRadiuses);
                        }
                    } else if (z) {
                        long elapsedRealtime2 = SystemClock.elapsedRealtime();
                        float interpolation2 = PreviewGroupCell.this.interpolator.getInterpolation(Math.min(1.0f, ((float) (elapsedRealtime2 - this.lastUpdate)) / 200.0f));
                        this.fromScale = AndroidUtilities.lerp(this.fromScale, this.scale, interpolation2);
                        RectF rectF6 = this.fromRect;
                        if (rectF6 != null) {
                            AndroidUtilities.lerp(rectF6, this.rect, interpolation2, rectF6);
                        }
                        this.scale = 0.0f;
                        this.lastUpdate = elapsedRealtime2;
                    } else {
                        this.fromScale = 0.0f;
                        this.scale = 0.0f;
                    }
                }

                public float getT() {
                    return PreviewGroupCell.this.interpolator.getInterpolation(Math.min(1.0f, ((float) (SystemClock.elapsedRealtime() - this.lastUpdate)) / 200.0f));
                }

                /* access modifiers changed from: protected */
                public MediaCell clone() {
                    MediaCell mediaCell = new MediaCell();
                    mediaCell.rect.set(this.rect);
                    mediaCell.image = this.image;
                    mediaCell.photoEntry = this.photoEntry;
                    return mediaCell;
                }

                public RectF rect() {
                    return rect(getT());
                }

                public RectF rect(float f) {
                    if (this.rect == null || this.image == null) {
                        this.tempRect.set(0.0f, 0.0f, 0.0f, 0.0f);
                        return this.tempRect;
                    }
                    float access$2700 = PreviewGroupCell.this.left + (this.rect.left * PreviewGroupCell.this.width);
                    float access$2800 = PreviewGroupCell.this.top + (this.rect.top * PreviewGroupCell.this.height);
                    float width = this.rect.width() * PreviewGroupCell.this.width;
                    float height = this.rect.height() * PreviewGroupCell.this.height;
                    if (f < 1.0f && this.fromRect != null) {
                        access$2700 = AndroidUtilities.lerp(PreviewGroupCell.this.left + (this.fromRect.left * PreviewGroupCell.this.width), access$2700, f);
                        access$2800 = AndroidUtilities.lerp(PreviewGroupCell.this.top + (this.fromRect.top * PreviewGroupCell.this.height), access$2800, f);
                        width = AndroidUtilities.lerp(this.fromRect.width() * PreviewGroupCell.this.width, width, f);
                        height = AndroidUtilities.lerp(this.fromRect.height() * PreviewGroupCell.this.height, height, f);
                    }
                    int i = this.positionFlags;
                    if ((i & 4) == 0) {
                        int i2 = PreviewGroupCell.this.halfGap;
                        access$2800 += (float) i2;
                        height -= (float) i2;
                    }
                    if ((i & 8) == 0) {
                        height -= (float) PreviewGroupCell.this.halfGap;
                    }
                    if ((i & 1) == 0) {
                        int i3 = PreviewGroupCell.this.halfGap;
                        access$2700 += (float) i3;
                        width -= (float) i3;
                    }
                    if ((i & 2) == 0) {
                        width -= (float) PreviewGroupCell.this.halfGap;
                    }
                    this.tempRect.set(access$2700, access$2800, width + access$2700, height + access$2800);
                    return this.tempRect;
                }

                public RectF drawingRect() {
                    float f = 0.0f;
                    if (this.rect == null || this.image == null) {
                        this.tempRect.set(0.0f, 0.0f, 0.0f, 0.0f);
                        return this.tempRect;
                    }
                    if (ChatAttachAlertPhotoLayoutPreview.this.draggingCell != null && ChatAttachAlertPhotoLayoutPreview.this.draggingCell.photoEntry == this.photoEntry) {
                        f = ChatAttachAlertPhotoLayoutPreview.this.draggingT;
                    }
                    float lerp = AndroidUtilities.lerp(this.fromScale, this.scale, getT()) * (((1.0f - f) * 0.2f) + 0.8f);
                    RectF rect2 = rect();
                    float f2 = 1.0f - lerp;
                    float f3 = lerp + 1.0f;
                    rect2.set(rect2.left + ((rect2.width() * f2) / 2.0f), rect2.top + ((rect2.height() * f2) / 2.0f), rect2.left + ((rect2.width() * f3) / 2.0f), rect2.top + ((rect2.height() * f3) / 2.0f));
                    return rect2;
                }

                private void drawPhotoIndex(Canvas canvas, float f, float f2, String str, float f3) {
                    String str2;
                    String str3 = str;
                    int dp = AndroidUtilities.dp(12.0f);
                    int dp2 = AndroidUtilities.dp(1.2f);
                    int i = (dp + dp2) * 2;
                    int i2 = dp2 * 4;
                    if (str3 != null && (this.indexBitmap == null || (str2 = this.indexBitmapText) == null || !str2.equals(str3))) {
                        if (this.indexBitmap == null) {
                            this.indexBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
                        }
                        Canvas canvas2 = new Canvas(this.indexBitmap);
                        canvas2.drawColor(0);
                        if (this.textPaint == null) {
                            TextPaint textPaint2 = new TextPaint(1);
                            this.textPaint = textPaint2;
                            textPaint2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        }
                        this.textPaint.setColor(ChatAttachAlertPhotoLayoutPreview.this.getThemedColor("chat_attachCheckBoxCheck"));
                        int length = str.length();
                        float f4 = (length == 0 || length == 1 || length == 2) ? 14.0f : length != 3 ? 8.0f : 10.0f;
                        this.textPaint.setTextSize((float) AndroidUtilities.dp(f4));
                        float f5 = ((float) i) / 2.0f;
                        this.paint.setColor(ChatAttachAlertPhotoLayoutPreview.this.getThemedColor("chat_attachCheckBoxBackground"));
                        float f6 = (float) ((int) f5);
                        float f7 = (float) dp;
                        canvas2.drawCircle(f6, f6, f7, this.paint);
                        this.strokePaint.setColor(AndroidUtilities.getOffsetColor(-1, ChatAttachAlertPhotoLayoutPreview.this.getThemedColor("chat_attachCheckBoxCheck"), 1.0f, 1.0f));
                        this.strokePaint.setStyle(Paint.Style.STROKE);
                        this.strokePaint.setStrokeWidth((float) dp2);
                        canvas2.drawCircle(f6, f6, f7, this.strokePaint);
                        canvas2.drawText(str3, f5 - (this.textPaint.measureText(str3) / 2.0f), f5 + ((float) AndroidUtilities.dp(1.0f)) + ((float) AndroidUtilities.dp(f4 / 4.0f)), this.textPaint);
                        this.indexIn.set(0, 0, i, i);
                        this.indexBitmapText = str3;
                    }
                    if (this.indexBitmap != null) {
                        float f8 = ((float) i) * f3;
                        float f9 = (float) i2;
                        float var_ = f - f9;
                        this.indexOut.set((int) ((f2 - f8) + f9), (int) var_, (int) (f2 + f9), (int) (var_ + f8));
                        this.bitmapPaint.setAlpha((int) (255.0f * f3));
                        canvas.drawBitmap(this.indexBitmap, this.indexIn, this.indexOut, this.bitmapPaint);
                    }
                }

                private void drawDuration(Canvas canvas, float f, float f2, String str, float f3) {
                    String str2;
                    if (str != null) {
                        if (this.videoDurationBitmap == null || (str2 = this.videoDurationBitmapText) == null || !str2.equals(str)) {
                            if (this.videoDurationTextPaint == null) {
                                TextPaint textPaint2 = new TextPaint(1);
                                this.videoDurationTextPaint = textPaint2;
                                textPaint2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                                this.videoDurationTextPaint.setColor(-1);
                            }
                            float dp = (float) AndroidUtilities.dp(12.0f);
                            this.videoDurationTextPaint.setTextSize(dp);
                            float intrinsicWidth = ((float) ChatAttachAlertPhotoLayoutPreview.this.videoPlayImage.getIntrinsicWidth()) + this.videoDurationTextPaint.measureText(str) + ((float) AndroidUtilities.dp(15.0f));
                            float max = Math.max(dp, (float) (ChatAttachAlertPhotoLayoutPreview.this.videoPlayImage.getIntrinsicHeight() + AndroidUtilities.dp(4.0f)));
                            int ceil = (int) Math.ceil((double) intrinsicWidth);
                            int ceil2 = (int) Math.ceil((double) max);
                            Bitmap bitmap = this.videoDurationBitmap;
                            if (!(bitmap != null && bitmap.getWidth() == ceil && this.videoDurationBitmap.getHeight() == ceil2)) {
                                Bitmap bitmap2 = this.videoDurationBitmap;
                                if (bitmap2 != null) {
                                    bitmap2.recycle();
                                }
                                this.videoDurationBitmap = Bitmap.createBitmap(ceil, ceil2, Bitmap.Config.ARGB_8888);
                            }
                            Canvas canvas2 = new Canvas(this.videoDurationBitmap);
                            RectF rectF = AndroidUtilities.rectTmp;
                            rectF.set(0.0f, 0.0f, intrinsicWidth, max);
                            canvas2.drawRoundRect(rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_timeBackgroundPaint);
                            int dp2 = AndroidUtilities.dp(5.0f);
                            int intrinsicHeight = (int) ((max - ((float) ChatAttachAlertPhotoLayoutPreview.this.videoPlayImage.getIntrinsicHeight())) / 2.0f);
                            ChatAttachAlertPhotoLayoutPreview.this.videoPlayImage.setBounds(dp2, intrinsicHeight, ChatAttachAlertPhotoLayoutPreview.this.videoPlayImage.getIntrinsicWidth() + dp2, ChatAttachAlertPhotoLayoutPreview.this.videoPlayImage.getIntrinsicHeight() + intrinsicHeight);
                            ChatAttachAlertPhotoLayoutPreview.this.videoPlayImage.draw(canvas2);
                            canvas2.drawText(str, (float) AndroidUtilities.dp(18.0f), dp + ((float) AndroidUtilities.dp(-0.7f)), this.videoDurationTextPaint);
                            this.durationIn.set(0, 0, ceil, ceil2);
                            this.videoDurationBitmapText = str;
                        }
                        int width = this.videoDurationBitmap.getWidth();
                        this.durationOut.set((int) f, (int) (f2 - (((float) this.videoDurationBitmap.getHeight()) * f3)), (int) (f + (((float) width) * f3)), (int) f2);
                        this.bitmapPaint.setAlpha((int) (f3 * 255.0f));
                        canvas.drawBitmap(this.videoDurationBitmap, this.durationIn, this.durationOut, this.bitmapPaint);
                    }
                }

                public boolean draw(Canvas canvas) {
                    return draw(canvas, false);
                }

                public boolean draw(Canvas canvas, boolean z) {
                    return draw(canvas, getT(), z);
                }

                public boolean draw(Canvas canvas, float f, boolean z) {
                    String str;
                    RectF rectF;
                    Canvas canvas2 = canvas;
                    float f2 = f;
                    if (this.rect == null || this.image == null) {
                        return false;
                    }
                    float access$1300 = ChatAttachAlertPhotoLayoutPreview.this.draggingCell == this ? ChatAttachAlertPhotoLayoutPreview.this.draggingT : 0.0f;
                    float lerp = AndroidUtilities.lerp(this.fromScale, this.scale, f2);
                    if (lerp <= 0.0f) {
                        return false;
                    }
                    RectF drawingRect = drawingRect();
                    float dp = (float) AndroidUtilities.dp((float) (SharedConfig.bubbleRadius - 1));
                    RectF rectF2 = this.roundRadiuses;
                    float f3 = rectF2.left;
                    float f4 = rectF2.top;
                    float f5 = rectF2.right;
                    float f6 = rectF2.bottom;
                    if (f2 < 1.0f && (rectF = this.fromRoundRadiuses) != null) {
                        f3 = AndroidUtilities.lerp(rectF.left, f3, f2);
                        f4 = AndroidUtilities.lerp(this.fromRoundRadiuses.top, f4, f2);
                        f5 = AndroidUtilities.lerp(this.fromRoundRadiuses.right, f5, f2);
                        f6 = AndroidUtilities.lerp(this.fromRoundRadiuses.bottom, f6, f2);
                    }
                    float lerp2 = AndroidUtilities.lerp(f3, dp, access$1300);
                    float lerp3 = AndroidUtilities.lerp(f4, dp, access$1300);
                    float lerp4 = AndroidUtilities.lerp(f5, dp, access$1300);
                    float lerp5 = AndroidUtilities.lerp(f6, dp, access$1300);
                    if (z) {
                        canvas.save();
                        canvas2.translate(-drawingRect.centerX(), -drawingRect.centerY());
                    }
                    this.image.setRoundRadius((int) lerp2, (int) lerp3, (int) lerp4, (int) lerp5);
                    this.image.setImageCoords(drawingRect.left, drawingRect.top, drawingRect.width(), drawingRect.height());
                    this.image.setAlpha(lerp);
                    this.image.draw(canvas2);
                    PreviewGroupCell previewGroupCell = PreviewGroupCell.this;
                    int indexOf = previewGroupCell.indexStart + previewGroupCell.group.photos.indexOf(this.photoEntry);
                    if (indexOf >= 0) {
                        str = (indexOf + 1) + "";
                    } else {
                        str = null;
                    }
                    float f7 = lerp;
                    drawPhotoIndex(canvas, ((float) AndroidUtilities.dp(10.0f)) + drawingRect.top, drawingRect.right - ((float) AndroidUtilities.dp(10.0f)), str, f7);
                    drawDuration(canvas, ((float) AndroidUtilities.dp(4.0f)) + drawingRect.left, drawingRect.bottom - ((float) AndroidUtilities.dp(4.0f)), this.videoDurationText, f7);
                    if (z) {
                        canvas.restore();
                    }
                    if (f2 < 1.0f) {
                        return true;
                    }
                    return false;
                }
            }

            /* access modifiers changed from: private */
            public void setGroup(GroupCalculator groupCalculator, boolean z) {
                MediaCell mediaCell;
                GroupCalculator groupCalculator2 = groupCalculator;
                boolean z2 = z;
                groupCalculator.calculate();
                this.group = groupCalculator2;
                long elapsedRealtime = SystemClock.elapsedRealtime();
                long j = this.lastMediaUpdate;
                if (elapsedRealtime - j < 200) {
                    float f = ((float) (elapsedRealtime - j)) / 200.0f;
                    this.previousGroupHeight = AndroidUtilities.lerp(this.previousGroupHeight, this.groupHeight, f);
                    this.previousGroupWidth = AndroidUtilities.lerp(this.previousGroupWidth, this.groupWidth, f);
                } else {
                    this.previousGroupHeight = this.groupHeight;
                    this.previousGroupWidth = this.groupWidth;
                }
                this.groupWidth = ((float) groupCalculator2.width) / 1000.0f;
                this.groupHeight = groupCalculator2.height;
                this.lastMediaUpdate = z2 ? elapsedRealtime : 0;
                ArrayList arrayList = new ArrayList(groupCalculator2.positions.keySet());
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) arrayList.get(i);
                    MessageObject.GroupedMessagePosition groupedMessagePosition = groupCalculator2.positions.get(photoEntry);
                    int size2 = this.media.size();
                    int i2 = 0;
                    while (true) {
                        if (i2 >= size2) {
                            mediaCell = null;
                            break;
                        }
                        mediaCell = this.media.get(i2);
                        if (mediaCell.photoEntry == photoEntry) {
                            break;
                        }
                        i2++;
                    }
                    if (mediaCell == null) {
                        MediaCell mediaCell2 = new MediaCell();
                        mediaCell2.setImage(photoEntry);
                        mediaCell2.layout(groupCalculator2, groupedMessagePosition, z2);
                        this.media.add(mediaCell2);
                    } else {
                        mediaCell.layout(groupCalculator2, groupedMessagePosition, z2);
                    }
                }
                int size3 = this.media.size();
                int i3 = 0;
                while (i3 < size3) {
                    MediaCell mediaCell3 = this.media.get(i3);
                    if (!groupCalculator2.positions.containsKey(mediaCell3.photoEntry)) {
                        if (mediaCell3.scale <= 0.0f) {
                            if (mediaCell3.lastUpdate + 200 <= elapsedRealtime) {
                                this.media.remove(i3);
                                i3--;
                                size3--;
                            }
                        }
                        mediaCell3.layout((GroupCalculator) null, (MessageObject.GroupedMessagePosition) null, z2);
                    }
                    i3++;
                }
                PreviewGroupsView.this.invalidate();
            }

            public float getT() {
                return this.interpolator.getInterpolation(Math.min(1.0f, ((float) (SystemClock.elapsedRealtime() - this.lastMediaUpdate)) / 200.0f));
            }

            public float measure() {
                Point point = AndroidUtilities.displaySize;
                return AndroidUtilities.lerp(this.previousGroupHeight, this.groupHeight, getT()) * ((float) Math.max(point.x, point.y)) * 0.5f * ChatAttachAlertPhotoLayoutPreview.this.getPreviewScale();
            }

            public boolean draw(Canvas canvas) {
                Canvas canvas2 = canvas;
                float f = 1.0f;
                float interpolation = this.interpolator.getInterpolation(Math.min(1.0f, ((float) (SystemClock.elapsedRealtime() - this.lastMediaUpdate)) / 200.0f));
                boolean z = interpolation < 1.0f;
                Point point = AndroidUtilities.displaySize;
                float lerp = AndroidUtilities.lerp(this.previousGroupWidth, this.groupWidth, interpolation) * ((float) PreviewGroupsView.this.getWidth()) * ChatAttachAlertPhotoLayoutPreview.this.getPreviewScale();
                float lerp2 = AndroidUtilities.lerp(this.previousGroupHeight, this.groupHeight, interpolation) * ((float) Math.max(point.x, point.y)) * 0.5f * ChatAttachAlertPhotoLayoutPreview.this.getPreviewScale();
                if (this.messageBackground != null) {
                    this.top = 0.0f;
                    this.left = (((float) PreviewGroupsView.this.getWidth()) - Math.max((float) this.padding, lerp)) / 2.0f;
                    this.right = (((float) PreviewGroupsView.this.getWidth()) + Math.max((float) this.padding, lerp)) / 2.0f;
                    this.bottom = Math.max((float) (this.padding * 2), lerp2);
                    this.messageBackground.setTop(0, (int) lerp, (int) lerp2, 0, 0, 0, false, false);
                    this.messageBackground.setBounds((int) this.left, (int) this.top, (int) this.right, (int) this.bottom);
                    if (this.groupWidth <= 0.0f) {
                        f = 1.0f - interpolation;
                    } else if (this.previousGroupWidth <= 0.0f) {
                        f = interpolation;
                    }
                    this.messageBackground.setAlpha((int) (f * 255.0f));
                    this.messageBackground.drawCached(canvas2, this.backgroundCacheParams);
                    float f2 = this.top;
                    int i = this.padding;
                    this.top = f2 + ((float) i);
                    this.left += (float) i;
                    this.bottom -= (float) i;
                    this.right -= (float) i;
                }
                this.width = this.right - this.left;
                this.height = this.bottom - this.top;
                int size = this.media.size();
                for (int i2 = 0; i2 < size; i2++) {
                    MediaCell mediaCell = this.media.get(i2);
                    if (mediaCell != null && ((ChatAttachAlertPhotoLayoutPreview.this.draggingCell == null || ChatAttachAlertPhotoLayoutPreview.this.draggingCell.photoEntry != mediaCell.photoEntry) && mediaCell.draw(canvas2))) {
                        z = true;
                    }
                }
                return z;
            }
        }
    }

    public Drawable getThemedDrawable(String str) {
        Drawable drawable = this.themeDelegate.getDrawable(str);
        return drawable != null ? drawable : Theme.getThemeDrawable(str);
    }
}