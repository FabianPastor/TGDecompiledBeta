package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
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
    public static HashMap<MediaController.PhotoEntry, Boolean> photoRotate = new HashMap<>();
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
        UndoView undoView2 = this.undoView;
        if (undoView2 != null) {
            undoView2.hide(false, 0);
        }
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

        /* JADX WARNING: Code restructure failed: missing block: B:30:0x00a5, code lost:
            if (r1 != 8) goto L_0x00a7;
         */
        /* JADX WARNING: Removed duplicated region for block: B:212:0x078e  */
        /* JADX WARNING: Removed duplicated region for block: B:222:0x07d1 A[LOOP:13: B:221:0x07cf->B:222:0x07d1, LOOP_END] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void calculate() {
            /*
                r38 = this;
                r10 = r38
                java.util.ArrayList<org.telegram.messenger.MediaController$PhotoEntry> r0 = r10.photos
                int r11 = r0.size()
                r12 = 0
                r13 = 0
                if (r11 != 0) goto L_0x0015
                r10.width = r13
                r10.height = r12
                r10.maxX = r13
                r10.maxY = r13
                return
            L_0x0015:
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r0 = r10.posArray
                r0.clear()
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r0 = r10.posArray
                r0.ensureCapacity(r11)
                java.util.HashMap<org.telegram.messenger.MediaController$PhotoEntry, org.telegram.messenger.MessageObject$GroupedMessagePosition> r0 = r10.positions
                r0.clear()
                char[] r0 = new char[r11]
                r2 = 0
                r3 = 1065353216(0x3var_, float:1.0)
                r4 = 0
            L_0x002a:
                r14 = 1067030938(0x3var_a, float:1.2)
                r7 = 1061997773(0x3f4ccccd, float:0.8)
                r15 = 1
                if (r2 >= r11) goto L_0x00ee
                java.util.ArrayList<org.telegram.messenger.MediaController$PhotoEntry> r8 = r10.photos
                java.lang.Object r8 = r8.get(r2)
                org.telegram.messenger.MediaController$PhotoEntry r8 = (org.telegram.messenger.MediaController.PhotoEntry) r8
                org.telegram.messenger.MessageObject$GroupedMessagePosition r9 = new org.telegram.messenger.MessageObject$GroupedMessagePosition
                r9.<init>()
                int r12 = r11 + -1
                if (r2 != r12) goto L_0x0046
                r12 = 1
                goto L_0x0047
            L_0x0046:
                r12 = 0
            L_0x0047:
                r9.last = r12
                int r12 = r8.width
                int r6 = r8.height
                java.util.HashMap r5 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.photoRotate
                boolean r5 = r5.containsKey(r8)
                if (r5 == 0) goto L_0x0066
                java.util.HashMap r5 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.photoRotate
                java.lang.Object r5 = r5.get(r8)
                java.lang.Boolean r5 = (java.lang.Boolean) r5
                boolean r5 = r5.booleanValue()
                goto L_0x00b8
            L_0x0066:
                boolean r5 = r8.isVideo     // Catch:{ Exception -> 0x00ac }
                if (r5 == 0) goto L_0x0093
                int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x00ac }
                r1 = 17
                if (r5 < r1) goto L_0x00a7
                android.media.MediaMetadataRetriever r1 = new android.media.MediaMetadataRetriever     // Catch:{ Exception -> 0x00ac }
                r1.<init>()     // Catch:{ Exception -> 0x00ac }
                java.lang.String r5 = r8.path     // Catch:{ Exception -> 0x00ac }
                r1.setDataSource(r5)     // Catch:{ Exception -> 0x00ac }
                r5 = 24
                java.lang.String r1 = r1.extractMetadata(r5)     // Catch:{ Exception -> 0x00ac }
                if (r1 == 0) goto L_0x00a7
                java.lang.String r5 = "90"
                boolean r5 = r1.equals(r5)     // Catch:{ Exception -> 0x00ac }
                if (r5 != 0) goto L_0x00a9
                java.lang.String r5 = "270"
                boolean r1 = r1.equals(r5)     // Catch:{ Exception -> 0x00ac }
                if (r1 == 0) goto L_0x00a7
                goto L_0x00a9
            L_0x0093:
                androidx.exifinterface.media.ExifInterface r1 = new androidx.exifinterface.media.ExifInterface     // Catch:{ Exception -> 0x00ac }
                java.lang.String r5 = r8.path     // Catch:{ Exception -> 0x00ac }
                r1.<init>((java.lang.String) r5)     // Catch:{ Exception -> 0x00ac }
                java.lang.String r5 = "Orientation"
                int r1 = r1.getAttributeInt(r5, r15)     // Catch:{ Exception -> 0x00ac }
                r5 = 6
                if (r1 == r5) goto L_0x00a9
                r5 = 8
                if (r1 == r5) goto L_0x00a9
            L_0x00a7:
                r1 = 0
                goto L_0x00aa
            L_0x00a9:
                r1 = 1
            L_0x00aa:
                r5 = r1
                goto L_0x00ad
            L_0x00ac:
                r5 = 0
            L_0x00ad:
                java.util.HashMap r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.photoRotate
                java.lang.Boolean r13 = java.lang.Boolean.valueOf(r5)
                r1.put(r8, r13)
            L_0x00b8:
                if (r5 == 0) goto L_0x00bf
                r37 = r12
                r12 = r6
                r6 = r37
            L_0x00bf:
                float r1 = (float) r12
                float r5 = (float) r6
                float r1 = r1 / r5
                r9.aspectRatio = r1
                int r5 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
                if (r5 <= 0) goto L_0x00cb
                r5 = 119(0x77, float:1.67E-43)
                goto L_0x00d4
            L_0x00cb:
                int r5 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1))
                if (r5 >= 0) goto L_0x00d2
                r5 = 110(0x6e, float:1.54E-43)
                goto L_0x00d4
            L_0x00d2:
                r5 = 113(0x71, float:1.58E-43)
            L_0x00d4:
                r0[r2] = r5
                float r3 = r3 + r1
                r5 = 1073741824(0x40000000, float:2.0)
                int r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
                if (r1 <= 0) goto L_0x00de
                r4 = 1
            L_0x00de:
                java.util.HashMap<org.telegram.messenger.MediaController$PhotoEntry, org.telegram.messenger.MessageObject$GroupedMessagePosition> r1 = r10.positions
                r1.put(r8, r9)
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r1 = r10.posArray
                r1.add(r9)
                int r2 = r2 + 1
                r12 = 0
                r13 = 0
                goto L_0x002a
            L_0x00ee:
                java.lang.String r1 = new java.lang.String
                r1.<init>(r0)
                r0 = 1123024896(0x42var_, float:120.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                float r0 = (float) r0
                android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
                int r6 = r5.x
                int r5 = r5.y
                int r5 = java.lang.Math.min(r6, r5)
                float r5 = (float) r5
                r6 = 1148846080(0x447a0000, float:1000.0)
                float r5 = r5 / r6
                float r0 = r0 / r5
                int r12 = (int) r0
                r0 = 1109393408(0x42200000, float:40.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                float r0 = (float) r0
                android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
                int r8 = r5.x
                int r5 = r5.y
                int r5 = java.lang.Math.min(r8, r5)
                float r5 = (float) r5
                float r5 = r5 / r6
                float r0 = r0 / r5
                int r0 = (int) r0
                r5 = 1067270023(0x3f9d3var_, float:1.2285012)
                float r8 = (float) r11
                float r8 = r3 / r8
                r3 = 1120403456(0x42CLASSNAME, float:100.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                r13 = 1145798656(0x444b8000, float:814.0)
                float r9 = r3 / r13
                r3 = 2
                if (r11 != r15) goto L_0x017d
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r0 = r10.posArray
                r1 = 0
                java.lang.Object r0 = r0.get(r1)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r0 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r0
                android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
                int r1 = r1.x
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r2 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlert r2 = r2.parentAlert
                int r2 = r2.getBackgroundPaddingLeft()
                int r2 = r2 * 2
                int r1 = r1 - r2
                android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
                int r4 = r2.x
                int r2 = r2.y
                int r2 = java.lang.Math.max(r4, r2)
                float r2 = (float) r2
                r4 = 1056964608(0x3var_, float:0.5)
                float r2 = r2 * r4
                r21 = 0
                r22 = 0
                r23 = 0
                r24 = 0
                r25 = 800(0x320, float:1.121E-42)
                float r1 = (float) r1
                float r1 = r1 * r7
                float r4 = r0.aspectRatio
                float r1 = r1 / r4
                float r26 = r1 / r2
                r27 = 15
                r20 = r0
                r20.set(r21, r22, r23, r24, r25, r26, r27)
            L_0x0178:
                r18 = r11
                r8 = 0
                goto L_0x0789
            L_0x017d:
                r7 = 4
                r14 = 3
                if (r4 != 0) goto L_0x0513
                if (r11 == r3) goto L_0x0187
                if (r11 == r14) goto L_0x0187
                if (r11 != r7) goto L_0x0513
            L_0x0187:
                r4 = 1137410048(0x43cb8000, float:407.0)
                if (r11 != r3) goto L_0x0294
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r0 = r10.posArray
                r2 = 0
                java.lang.Object r0 = r0.get(r2)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r0 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r0
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r2 = r10.posArray
                java.lang.Object r2 = r2.get(r15)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r2 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r2
                java.lang.String r9 = "ww"
                boolean r16 = r1.equals(r9)
                if (r16 == 0) goto L_0x01f5
                double r7 = (double) r8
                r17 = 4608983858650965606(0x3ffNUM, double:1.4)
                double r14 = (double) r5
                java.lang.Double.isNaN(r14)
                double r14 = r14 * r17
                int r5 = (r7 > r14 ? 1 : (r7 == r14 ? 0 : -1))
                if (r5 <= 0) goto L_0x01f5
                float r5 = r0.aspectRatio
                float r7 = r2.aspectRatio
                float r8 = r5 - r7
                double r14 = (double) r8
                r17 = 4596373779694328218(0x3fCLASSNAMEa, double:0.2)
                int r8 = (r14 > r17 ? 1 : (r14 == r17 ? 0 : -1))
                if (r8 >= 0) goto L_0x01f5
                float r1 = r6 / r5
                float r6 = r6 / r7
                float r4 = java.lang.Math.min(r6, r4)
                float r1 = java.lang.Math.min(r1, r4)
                int r1 = java.lang.Math.round(r1)
                float r1 = (float) r1
                float r1 = r1 / r13
                r21 = 0
                r22 = 0
                r23 = 0
                r24 = 0
                r25 = 1000(0x3e8, float:1.401E-42)
                r27 = 7
                r20 = r0
                r26 = r1
                r20.set(r21, r22, r23, r24, r25, r26, r27)
                r23 = 1
                r24 = 1
                r27 = 11
                r20 = r2
                r20.set(r21, r22, r23, r24, r25, r26, r27)
                goto L_0x0178
            L_0x01f5:
                boolean r4 = r1.equals(r9)
                if (r4 != 0) goto L_0x025c
                java.lang.String r4 = "qq"
                boolean r1 = r1.equals(r4)
                if (r1 == 0) goto L_0x0204
                goto L_0x025c
            L_0x0204:
                float r1 = r0.aspectRatio
                float r6 = r6 / r1
                r4 = 1065353216(0x3var_, float:1.0)
                float r1 = r4 / r1
                float r5 = r2.aspectRatio
                float r4 = r4 / r5
                float r1 = r1 + r4
                float r6 = r6 / r1
                int r1 = java.lang.Math.round(r6)
                float r1 = (float) r1
                r4 = 1137180672(0x43CLASSNAME, float:400.0)
                float r1 = java.lang.Math.max(r4, r1)
                int r1 = (int) r1
                int r4 = 1000 - r1
                if (r4 >= r12) goto L_0x0224
                int r4 = r12 - r4
                int r1 = r1 - r4
                r4 = r12
            L_0x0224:
                float r5 = (float) r4
                float r6 = r0.aspectRatio
                float r5 = r5 / r6
                float r6 = (float) r1
                float r7 = r2.aspectRatio
                float r6 = r6 / r7
                float r5 = java.lang.Math.min(r5, r6)
                int r5 = java.lang.Math.round(r5)
                float r5 = (float) r5
                float r5 = java.lang.Math.min(r13, r5)
                float r5 = r5 / r13
                r21 = 0
                r22 = 0
                r23 = 0
                r24 = 0
                r27 = 13
                r20 = r0
                r25 = r4
                r26 = r5
                r20.set(r21, r22, r23, r24, r25, r26, r27)
                r21 = 1
                r22 = 1
                r27 = 14
                r20 = r2
                r25 = r1
                r20.set(r21, r22, r23, r24, r25, r26, r27)
                goto L_0x0178
            L_0x025c:
                r1 = 500(0x1f4, float:7.0E-43)
                float r1 = (float) r1
                float r4 = r0.aspectRatio
                float r4 = r1 / r4
                float r5 = r2.aspectRatio
                float r1 = r1 / r5
                float r1 = java.lang.Math.min(r1, r13)
                float r1 = java.lang.Math.min(r4, r1)
                int r1 = java.lang.Math.round(r1)
                float r1 = (float) r1
                float r1 = r1 / r13
                r21 = 0
                r22 = 0
                r23 = 0
                r24 = 0
                r27 = 13
                r25 = 500(0x1f4, float:7.0E-43)
                r20 = r0
                r26 = r1
                r20.set(r21, r22, r23, r24, r25, r26, r27)
                r21 = 1
                r22 = 1
                r27 = 14
                r20 = r2
                r20.set(r21, r22, r23, r24, r25, r26, r27)
                goto L_0x0178
            L_0x0294:
                r5 = 1141264221(0x44064f5d, float:537.24005)
                r7 = 3
                if (r11 != r7) goto L_0x039f
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r2 = r10.posArray
                r7 = 0
                java.lang.Object r2 = r2.get(r7)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r2 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r2
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r8 = r10.posArray
                r14 = 1
                java.lang.Object r8 = r8.get(r14)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r8 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r8
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r14 = r10.posArray
                java.lang.Object r14 = r14.get(r3)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r14 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r14
                char r1 = r1.charAt(r7)
                r7 = 110(0x6e, float:1.54E-43)
                if (r1 != r7) goto L_0x0340
                float r1 = r8.aspectRatio
                float r6 = r6 * r1
                float r5 = r14.aspectRatio
                float r5 = r5 + r1
                float r6 = r6 / r5
                int r1 = java.lang.Math.round(r6)
                float r1 = (float) r1
                float r1 = java.lang.Math.min(r4, r1)
                float r4 = r13 - r1
                float r5 = (float) r12
                r6 = 1140457472(0x43fa0000, float:500.0)
                float r7 = r14.aspectRatio
                float r7 = r7 * r1
                float r9 = r8.aspectRatio
                float r9 = r9 * r4
                float r7 = java.lang.Math.min(r7, r9)
                int r7 = java.lang.Math.round(r7)
                float r7 = (float) r7
                float r6 = java.lang.Math.min(r6, r7)
                float r5 = java.lang.Math.max(r5, r6)
                int r5 = (int) r5
                float r6 = r2.aspectRatio
                float r6 = r6 * r13
                float r0 = (float) r0
                float r6 = r6 + r0
                int r0 = 1000 - r5
                float r7 = (float) r0
                float r6 = java.lang.Math.min(r6, r7)
                int r33 = java.lang.Math.round(r6)
                r29 = 0
                r30 = 0
                r31 = 0
                r32 = 1
                r34 = 1065353216(0x3var_, float:1.0)
                r35 = 13
                r28 = r2
                r28.set(r29, r30, r31, r32, r33, r34, r35)
                r29 = 1
                r30 = 1
                r32 = 0
                float r4 = r4 / r13
                r35 = 6
                r28 = r8
                r33 = r5
                r34 = r4
                r28.set(r29, r30, r31, r32, r33, r34, r35)
                r31 = 1
                r32 = 1
                float r1 = r1 / r13
                r35 = 10
                r28 = r14
                r34 = r1
                r28.set(r29, r30, r31, r32, r33, r34, r35)
                r5 = 1000(0x3e8, float:1.401E-42)
                r14.spanSize = r5
                float[] r5 = new float[r3]
                r6 = 0
                r5[r6] = r1
                r1 = 1
                r5[r1] = r4
                r2.siblingHeights = r5
                r2.spanSize = r0
                goto L_0x0178
            L_0x0340:
                float r0 = r2.aspectRatio
                float r6 = r6 / r0
                float r0 = java.lang.Math.min(r6, r5)
                int r0 = java.lang.Math.round(r0)
                float r0 = (float) r0
                float r0 = r0 / r13
                r29 = 0
                r30 = 1
                r31 = 0
                r32 = 0
                r33 = 1000(0x3e8, float:1.401E-42)
                r35 = 7
                r28 = r2
                r34 = r0
                r28.set(r29, r30, r31, r32, r33, r34, r35)
                float r0 = r13 - r0
                r1 = 500(0x1f4, float:7.0E-43)
                float r1 = (float) r1
                float r2 = r8.aspectRatio
                float r2 = r1 / r2
                float r4 = r14.aspectRatio
                float r1 = r1 / r4
                float r1 = java.lang.Math.min(r2, r1)
                int r1 = java.lang.Math.round(r1)
                float r1 = (float) r1
                float r0 = java.lang.Math.min(r0, r1)
                float r0 = r0 / r13
                int r1 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
                if (r1 >= 0) goto L_0x037f
                r0 = r9
            L_0x037f:
                r29 = 0
                r30 = 0
                r31 = 1
                r32 = 1
                r35 = 9
                r33 = 500(0x1f4, float:7.0E-43)
                r28 = r8
                r34 = r0
                r28.set(r29, r30, r31, r32, r33, r34, r35)
                r29 = 1
                r30 = 1
                r35 = 10
                r28 = r14
                r28.set(r29, r30, r31, r32, r33, r34, r35)
                goto L_0x0178
            L_0x039f:
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r4 = r10.posArray
                r7 = 0
                java.lang.Object r4 = r4.get(r7)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r4 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r4
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r8 = r10.posArray
                r14 = 1
                java.lang.Object r8 = r8.get(r14)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r8 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r8
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r14 = r10.posArray
                java.lang.Object r14 = r14.get(r3)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r14 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r14
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r15 = r10.posArray
                r3 = 3
                java.lang.Object r15 = r15.get(r3)
                r3 = r15
                org.telegram.messenger.MessageObject$GroupedMessagePosition r3 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r3
                char r1 = r1.charAt(r7)
                r7 = 119(0x77, float:1.67E-43)
                if (r1 != r7) goto L_0x046e
                float r0 = r4.aspectRatio
                float r0 = r6 / r0
                float r0 = java.lang.Math.min(r0, r5)
                int r0 = java.lang.Math.round(r0)
                float r0 = (float) r0
                float r0 = r0 / r13
                r29 = 0
                r30 = 2
                r31 = 0
                r32 = 0
                r33 = 1000(0x3e8, float:1.401E-42)
                r35 = 7
                r28 = r4
                r34 = r0
                r28.set(r29, r30, r31, r32, r33, r34, r35)
                float r1 = r8.aspectRatio
                float r2 = r14.aspectRatio
                float r1 = r1 + r2
                float r2 = r3.aspectRatio
                float r1 = r1 + r2
                float r6 = r6 / r1
                int r1 = java.lang.Math.round(r6)
                float r1 = (float) r1
                float r2 = (float) r12
                float r4 = r8.aspectRatio
                float r4 = r4 * r1
                r5 = 1137180672(0x43CLASSNAME, float:400.0)
                float r4 = java.lang.Math.min(r5, r4)
                float r4 = java.lang.Math.max(r2, r4)
                int r4 = (int) r4
                r5 = 1134886912(0x43a50000, float:330.0)
                float r2 = java.lang.Math.max(r2, r5)
                float r5 = r3.aspectRatio
                float r5 = r5 * r1
                float r2 = java.lang.Math.max(r2, r5)
                int r2 = (int) r2
                int r5 = 1000 - r4
                int r5 = r5 - r2
                r6 = 1114112000(0x42680000, float:58.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
                if (r5 >= r7) goto L_0x0432
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
                int r7 = r7 - r5
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r6)
                int r6 = r7 / 2
                int r4 = r4 - r6
                int r7 = r7 - r6
                int r2 = r2 - r7
            L_0x0432:
                r33 = r4
                float r0 = r13 - r0
                float r0 = java.lang.Math.min(r0, r1)
                float r0 = r0 / r13
                int r1 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
                if (r1 >= 0) goto L_0x0440
                r0 = r9
            L_0x0440:
                r29 = 0
                r30 = 0
                r31 = 1
                r32 = 1
                r35 = 9
                r28 = r8
                r34 = r0
                r28.set(r29, r30, r31, r32, r33, r34, r35)
                r29 = 1
                r30 = 1
                r35 = 8
                r28 = r14
                r33 = r5
                r28.set(r29, r30, r31, r32, r33, r34, r35)
                r29 = 2
                r30 = 2
                r35 = 10
                r28 = r3
                r33 = r2
                r28.set(r29, r30, r31, r32, r33, r34, r35)
                r3 = 2
                goto L_0x0178
            L_0x046e:
                float r1 = r8.aspectRatio
                r5 = 1065353216(0x3var_, float:1.0)
                float r1 = r5 / r1
                float r6 = r14.aspectRatio
                float r6 = r5 / r6
                float r1 = r1 + r6
                float r6 = r3.aspectRatio
                float r6 = r5 / r6
                float r1 = r1 + r6
                float r1 = r13 / r1
                int r1 = java.lang.Math.round(r1)
                int r1 = java.lang.Math.max(r12, r1)
                float r2 = (float) r2
                float r5 = (float) r1
                float r6 = r8.aspectRatio
                float r6 = r5 / r6
                float r6 = java.lang.Math.max(r2, r6)
                float r6 = r6 / r13
                r7 = 1051260355(0x3ea8f5c3, float:0.33)
                float r6 = java.lang.Math.min(r7, r6)
                float r9 = r14.aspectRatio
                float r5 = r5 / r9
                float r2 = java.lang.Math.max(r2, r5)
                float r2 = r2 / r13
                float r2 = java.lang.Math.min(r7, r2)
                r5 = 1065353216(0x3var_, float:1.0)
                float r5 = r5 - r6
                float r5 = r5 - r2
                float r7 = r4.aspectRatio
                float r7 = r7 * r13
                float r0 = (float) r0
                float r7 = r7 + r0
                int r0 = 1000 - r1
                float r9 = (float) r0
                float r7 = java.lang.Math.min(r7, r9)
                int r33 = java.lang.Math.round(r7)
                r29 = 0
                r30 = 0
                r31 = 0
                r32 = 2
                float r7 = r6 + r2
                float r34 = r7 + r5
                r35 = 13
                r28 = r4
                r28.set(r29, r30, r31, r32, r33, r34, r35)
                r29 = 1
                r30 = 1
                r32 = 0
                r35 = 6
                r28 = r8
                r33 = r1
                r34 = r6
                r28.set(r29, r30, r31, r32, r33, r34, r35)
                r31 = 1
                r32 = 1
                r35 = 2
                r28 = r14
                r34 = r2
                r28.set(r29, r30, r31, r32, r33, r34, r35)
                r7 = 1000(0x3e8, float:1.401E-42)
                r14.spanSize = r7
                r31 = 2
                r32 = 2
                r35 = 10
                r28 = r3
                r34 = r5
                r28.set(r29, r30, r31, r32, r33, r34, r35)
                r14 = 1000(0x3e8, float:1.401E-42)
                r3.spanSize = r14
                r4.spanSize = r0
                r0 = 3
                float[] r0 = new float[r0]
                r1 = 0
                r0[r1] = r6
                r1 = 1
                r0[r1] = r2
                r3 = 2
                r0[r3] = r5
                r4.siblingHeights = r0
                goto L_0x0178
            L_0x0513:
                r14 = 1000(0x3e8, float:1.401E-42)
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r0 = r10.posArray
                int r15 = r0.size()
                float[] r6 = new float[r15]
                r0 = 0
            L_0x051e:
                if (r0 >= r11) goto L_0x0561
                r1 = 1066192077(0x3f8ccccd, float:1.1)
                int r1 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1))
                if (r1 <= 0) goto L_0x053a
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r1 = r10.posArray
                java.lang.Object r1 = r1.get(r0)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r1 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r1
                float r1 = r1.aspectRatio
                r2 = 1065353216(0x3var_, float:1.0)
                float r1 = java.lang.Math.max(r2, r1)
                r6[r0] = r1
                goto L_0x054c
            L_0x053a:
                r2 = 1065353216(0x3var_, float:1.0)
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r1 = r10.posArray
                java.lang.Object r1 = r1.get(r0)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r1 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r1
                float r1 = r1.aspectRatio
                float r1 = java.lang.Math.min(r2, r1)
                r6[r0] = r1
            L_0x054c:
                r1 = 1059760867(0x3f2aaae3, float:0.66667)
                r4 = 1071225242(0x3fd9999a, float:1.7)
                r5 = r6[r0]
                float r4 = java.lang.Math.min(r4, r5)
                float r1 = java.lang.Math.max(r1, r4)
                r6[r0] = r1
                int r0 = r0 + 1
                goto L_0x051e
            L_0x0561:
                java.util.ArrayList r5 = new java.util.ArrayList
                r5.<init>()
                r4 = 1
            L_0x0567:
                if (r4 >= r15) goto L_0x05a3
                int r2 = r15 - r4
                r0 = 3
                if (r4 > r0) goto L_0x0594
                if (r2 <= r0) goto L_0x0571
                goto L_0x0594
            L_0x0571:
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator$MessageGroupedLayoutAttempt r1 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator$MessageGroupedLayoutAttempt
                r0 = 0
                float r17 = r10.multiHeight(r6, r0, r4)
                float r18 = r10.multiHeight(r6, r4, r15)
                r0 = r1
                r7 = r1
                r1 = r38
                r21 = r2
                r2 = r4
                r14 = 2
                r3 = r21
                r21 = r4
                r4 = r17
                r13 = r5
                r5 = r18
                r0.<init>(r1, r2, r3, r4, r5)
                r13.add(r7)
                goto L_0x0598
            L_0x0594:
                r21 = r4
                r13 = r5
                r14 = 2
            L_0x0598:
                int r4 = r21 + 1
                r5 = r13
                r3 = 2
                r7 = 4
                r13 = 1145798656(0x444b8000, float:814.0)
                r14 = 1000(0x3e8, float:1.401E-42)
                goto L_0x0567
            L_0x05a3:
                r13 = r5
                r14 = 2
                r7 = 1
            L_0x05a6:
                int r0 = r15 + -1
                if (r7 >= r0) goto L_0x060f
                r5 = 1
            L_0x05ab:
                int r0 = r15 - r7
                if (r5 >= r0) goto L_0x0602
                int r4 = r0 - r5
                r0 = 3
                if (r7 > r0) goto L_0x05f0
                r1 = 1062836634(0x3var_a, float:0.85)
                int r1 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1))
                if (r1 >= 0) goto L_0x05bd
                r1 = 4
                goto L_0x05be
            L_0x05bd:
                r1 = 3
            L_0x05be:
                if (r5 > r1) goto L_0x05f0
                if (r4 <= r0) goto L_0x05c3
                goto L_0x05f0
            L_0x05c3:
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator$MessageGroupedLayoutAttempt r3 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator$MessageGroupedLayoutAttempt
                r0 = 0
                float r18 = r10.multiHeight(r6, r0, r7)
                int r0 = r7 + r5
                float r21 = r10.multiHeight(r6, r7, r0)
                float r24 = r10.multiHeight(r6, r0, r15)
                r0 = r3
                r1 = r38
                r2 = r7
                r14 = r3
                r3 = r5
                r26 = r5
                r5 = r18
                r18 = r11
                r11 = r6
                r6 = r21
                r19 = r7
                r21 = 4
                r7 = r24
                r0.<init>(r1, r2, r3, r4, r5, r6, r7)
                r13.add(r14)
                goto L_0x05f9
            L_0x05f0:
                r26 = r5
                r19 = r7
                r18 = r11
                r21 = 4
                r11 = r6
            L_0x05f9:
                int r5 = r26 + 1
                r6 = r11
                r11 = r18
                r7 = r19
                r14 = 2
                goto L_0x05ab
            L_0x0602:
                r19 = r7
                r18 = r11
                r21 = 4
                r11 = r6
                int r7 = r19 + 1
                r11 = r18
                r14 = 2
                goto L_0x05a6
            L_0x060f:
                r18 = r11
                r21 = 4
                r11 = r6
                r14 = 1
            L_0x0615:
                int r0 = r15 + -2
                if (r14 >= r0) goto L_0x068b
                r8 = 1
            L_0x061a:
                int r7 = r15 - r14
                if (r8 >= r7) goto L_0x0684
                r6 = 1
            L_0x061f:
                int r0 = r7 - r8
                if (r6 >= r0) goto L_0x067b
                int r5 = r0 - r6
                r0 = 3
                if (r14 > r0) goto L_0x0666
                if (r8 > r0) goto L_0x0666
                if (r6 > r0) goto L_0x0666
                if (r5 <= r0) goto L_0x062f
                goto L_0x0666
            L_0x062f:
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator$MessageGroupedLayoutAttempt r4 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator$MessageGroupedLayoutAttempt
                r0 = 0
                float r19 = r10.multiHeight(r11, r0, r14)
                int r0 = r14 + r8
                float r24 = r10.multiHeight(r11, r14, r0)
                int r1 = r0 + r6
                float r26 = r10.multiHeight(r11, r0, r1)
                float r27 = r10.multiHeight(r11, r1, r15)
                r0 = r4
                r1 = r38
                r2 = r14
                r3 = r8
                r28 = r15
                r15 = r4
                r4 = r6
                r29 = r6
                r6 = r19
                r19 = r7
                r7 = r24
                r24 = r8
                r8 = r26
                r36 = r9
                r9 = r27
                r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9)
                r13.add(r15)
                goto L_0x0670
            L_0x0666:
                r29 = r6
                r19 = r7
                r24 = r8
                r36 = r9
                r28 = r15
            L_0x0670:
                int r6 = r29 + 1
                r7 = r19
                r8 = r24
                r15 = r28
                r9 = r36
                goto L_0x061f
            L_0x067b:
                r24 = r8
                r36 = r9
                r28 = r15
                int r8 = r24 + 1
                goto L_0x061a
            L_0x0684:
                r36 = r9
                r28 = r15
                int r14 = r14 + 1
                goto L_0x0615
            L_0x068b:
                r36 = r9
                r0 = 0
                r1 = 1151762432(0x44a68000, float:1332.0)
                r2 = 0
                r3 = 0
            L_0x0693:
                int r4 = r13.size()
                if (r2 >= r4) goto L_0x070b
                java.lang.Object r4 = r13.get(r2)
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator$MessageGroupedLayoutAttempt r4 = (org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.GroupCalculator.MessageGroupedLayoutAttempt) r4
                r5 = 2139095039(0x7f7fffff, float:3.4028235E38)
                r5 = 0
                r6 = 2139095039(0x7f7fffff, float:3.4028235E38)
                r7 = 0
            L_0x06a7:
                float[] r8 = r4.heights
                int r9 = r8.length
                if (r5 >= r9) goto L_0x06ba
                r9 = r8[r5]
                float r7 = r7 + r9
                r9 = r8[r5]
                int r9 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1))
                if (r9 >= 0) goto L_0x06b7
                r6 = r8[r5]
            L_0x06b7:
                int r5 = r5 + 1
                goto L_0x06a7
            L_0x06ba:
                float r7 = r7 - r1
                float r5 = java.lang.Math.abs(r7)
                int[] r7 = r4.lineCounts
                int r8 = r7.length
                r9 = 1
                if (r8 <= r9) goto L_0x06f2
                r8 = 0
                r14 = r7[r8]
                r15 = r7[r9]
                if (r14 > r15) goto L_0x06eb
                int r14 = r7.length
                r15 = 2
                if (r14 <= r15) goto L_0x06dc
                r14 = r7[r9]
                r9 = r7[r15]
                if (r14 > r9) goto L_0x06d7
                goto L_0x06dc
            L_0x06d7:
                r7 = 1067030938(0x3var_a, float:1.2)
                r14 = 3
                goto L_0x06ef
            L_0x06dc:
                int r9 = r7.length
                r14 = 3
                if (r9 <= r14) goto L_0x06e7
                r9 = r7[r15]
                r7 = r7[r14]
                if (r9 <= r7) goto L_0x06e7
                goto L_0x06ec
            L_0x06e7:
                r7 = 1067030938(0x3var_a, float:1.2)
                goto L_0x06f7
            L_0x06eb:
                r14 = 3
            L_0x06ec:
                r7 = 1067030938(0x3var_a, float:1.2)
            L_0x06ef:
                float r5 = r5 * r7
                goto L_0x06f7
            L_0x06f2:
                r7 = 1067030938(0x3var_a, float:1.2)
                r8 = 0
                r14 = 3
            L_0x06f7:
                float r9 = (float) r12
                int r6 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
                if (r6 >= 0) goto L_0x0700
                r6 = 1069547520(0x3fCLASSNAME, float:1.5)
                float r5 = r5 * r6
            L_0x0700:
                if (r0 == 0) goto L_0x0706
                int r6 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
                if (r6 >= 0) goto L_0x0708
            L_0x0706:
                r0 = r4
                r3 = r5
            L_0x0708:
                int r2 = r2 + 1
                goto L_0x0693
            L_0x070b:
                r8 = 0
                if (r0 != 0) goto L_0x070f
                return
            L_0x070f:
                r1 = 0
                r2 = 0
            L_0x0711:
                int[] r3 = r0.lineCounts
                int r4 = r3.length
                if (r1 >= r4) goto L_0x0789
                r3 = r3[r1]
                float[] r4 = r0.heights
                r4 = r4[r1]
                r5 = 0
                r6 = r5
                r7 = 1000(0x3e8, float:1.401E-42)
                r5 = r2
                r2 = 0
            L_0x0722:
                if (r2 >= r3) goto L_0x0772
                r9 = r11[r5]
                float r9 = r9 * r4
                int r9 = (int) r9
                int r7 = r7 - r9
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r12 = r10.posArray
                java.lang.Object r12 = r12.get(r5)
                r28 = r12
                org.telegram.messenger.MessageObject$GroupedMessagePosition r28 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r28
                if (r1 != 0) goto L_0x0738
                r12 = 4
                goto L_0x0739
            L_0x0738:
                r12 = 0
            L_0x0739:
                int[] r13 = r0.lineCounts
                int r13 = r13.length
                r14 = 1
                int r13 = r13 - r14
                if (r1 != r13) goto L_0x0742
                r12 = r12 | 8
            L_0x0742:
                if (r2 != 0) goto L_0x0748
                r12 = r12 | 1
                r6 = r28
            L_0x0748:
                int r13 = r3 + -1
                if (r2 != r13) goto L_0x0753
                r6 = r12 | 2
                r35 = r6
                r6 = r28
                goto L_0x0755
            L_0x0753:
                r35 = r12
            L_0x0755:
                r12 = 1145798656(0x444b8000, float:814.0)
                float r13 = r4 / r12
                r14 = r36
                float r34 = java.lang.Math.max(r14, r13)
                r29 = r2
                r30 = r2
                r31 = r1
                r32 = r1
                r33 = r9
                r28.set(r29, r30, r31, r32, r33, r34, r35)
                int r5 = r5 + 1
                int r2 = r2 + 1
                goto L_0x0722
            L_0x0772:
                r14 = r36
                r12 = 1145798656(0x444b8000, float:814.0)
                if (r6 == 0) goto L_0x0783
                int r2 = r6.pw
                int r2 = r2 + r7
                r6.pw = r2
                int r2 = r6.spanSize
                int r2 = r2 + r7
                r6.spanSize = r2
            L_0x0783:
                int r1 = r1 + 1
                r2 = r5
                r36 = r14
                goto L_0x0711
            L_0x0789:
                r0 = r18
                r1 = 0
            L_0x078c:
                if (r1 >= r0) goto L_0x07ce
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r2 = r10.posArray
                java.lang.Object r2 = r2.get(r1)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r2 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r2
                byte r3 = r2.minX
                if (r3 != 0) goto L_0x07a0
                int r3 = r2.spanSize
                int r3 = r3 + 200
                r2.spanSize = r3
            L_0x07a0:
                int r3 = r2.flags
                r4 = 2
                r3 = r3 & r4
                if (r3 == 0) goto L_0x07aa
                r3 = 1
                r2.edge = r3
                goto L_0x07ab
            L_0x07aa:
                r3 = 1
            L_0x07ab:
                int r5 = r10.maxX
                byte r6 = r2.maxX
                int r5 = java.lang.Math.max(r5, r6)
                r10.maxX = r5
                int r5 = r10.maxY
                byte r6 = r2.maxY
                int r5 = java.lang.Math.max(r5, r6)
                r10.maxY = r5
                byte r5 = r2.minY
                byte r6 = r2.maxY
                byte r7 = r2.minX
                float r5 = r10.getLeft(r2, r5, r6, r7)
                r2.left = r5
                int r1 = r1 + 1
                goto L_0x078c
            L_0x07ce:
                r13 = 0
            L_0x07cf:
                if (r13 >= r0) goto L_0x07e4
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r1 = r10.posArray
                java.lang.Object r1 = r1.get(r13)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r1 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r1
                byte r2 = r1.minY
                float r2 = r10.getTop(r1, r2)
                r1.top = r2
                int r13 = r13 + 1
                goto L_0x07cf
            L_0x07e4:
                int r0 = r38.getWidth()
                r10.width = r0
                float r0 = r38.getHeight()
                r10.height = r0
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.GroupCalculator.calculate():void");
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
            wallpaperDrawable.setBounds(0, max, getWidth(), AndroidUtilities.displaySize.y);
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

    /* access modifiers changed from: package-private */
    public void onSelectedItemsCountChanged(int i) {
        if (i > 1) {
            this.parentAlert.selectedMenuItem.showSubItem(0);
        } else {
            this.parentAlert.selectedMenuItem.hideSubItem(0);
        }
    }

    private class PreviewGroupsView extends ViewGroup {
        /* access modifiers changed from: private */
        public HashMap<Object, Object> deletedPhotos = new HashMap<>();
        /* access modifiers changed from: private */
        public float draggingT = 0.0f;
        /* access modifiers changed from: private */
        public ArrayList<PreviewGroupCell> groupCells = new ArrayList<>();
        private ChatActionCell hintView;
        boolean[] lastGroupSeen = null;
        private int lastMeasuredHeight = 0;
        /* access modifiers changed from: private */
        public int paddingBottom = AndroidUtilities.dp(64.0f);
        /* access modifiers changed from: private */
        public int paddingTop = AndroidUtilities.dp(16.0f);
        private float savedDragFromX;
        private float savedDragFromY;
        private float savedDraggingT;
        private final Runnable scroller = new Runnable() {
            public void run() {
                float f;
                if (ChatAttachAlertPhotoLayoutPreview.this.draggingCell != null && !ChatAttachAlertPhotoLayoutPreview.this.draggingCellHiding) {
                    int computeVerticalScrollOffset = ChatAttachAlertPhotoLayoutPreview.this.listView.computeVerticalScrollOffset();
                    boolean z = ChatAttachAlertPhotoLayoutPreview.this.listView.computeVerticalScrollExtent() + computeVerticalScrollOffset >= (PreviewGroupsView.this.measurePureHeight() - PreviewGroupsView.this.paddingBottom) + PreviewGroupsView.this.paddingTop;
                    float max = Math.max(0.0f, (ChatAttachAlertPhotoLayoutPreview.this.draggingCellTouchY - ((float) Math.max(0, computeVerticalScrollOffset - ChatAttachAlertPhotoLayoutPreview.this.getListTopPadding()))) - ((float) AndroidUtilities.dp(52.0f)));
                    float max2 = Math.max(0.0f, ((((float) ChatAttachAlertPhotoLayoutPreview.this.listView.getMeasuredHeight()) - (ChatAttachAlertPhotoLayoutPreview.this.draggingCellTouchY - ((float) computeVerticalScrollOffset))) - ((float) ChatAttachAlertPhotoLayoutPreview.this.getListTopPadding())) - ((float) AndroidUtilities.dp(84.0f)));
                    float dp = (float) AndroidUtilities.dp(32.0f);
                    if (max >= dp || computeVerticalScrollOffset <= ChatAttachAlertPhotoLayoutPreview.this.getListTopPadding()) {
                        f = max2 < dp ? ((float) AndroidUtilities.dp(6.0f)) * (1.0f - (max2 / dp)) : 0.0f;
                    } else {
                        f = (-(1.0f - (max / dp))) * ((float) AndroidUtilities.dp(6.0f));
                    }
                    int i = (int) f;
                    if (Math.abs(i) > 0 && ChatAttachAlertPhotoLayoutPreview.this.listView.canScrollVertically(i) && (f <= 0.0f || !z)) {
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
        private final Point tmpPoint = new Point();
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
            ChatActionCell chatActionCell = new ChatActionCell(context, true, ChatAttachAlertPhotoLayoutPreview.this.themeDelegate);
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
                GroupCalculator access$600 = this.groupCells.get(i).group;
                if (access$600.photos.size() != 0) {
                    int size3 = access$600.photos.size();
                    for (int i2 = 0; i2 < size3; i2++) {
                        MediaController.PhotoEntry photoEntry = access$600.photos.get(i2);
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

        /* access modifiers changed from: private */
        public int measurePureHeight() {
            int i = this.paddingTop + this.paddingBottom;
            int size = this.groupCells.size();
            for (int i2 = 0; i2 < size; i2++) {
                i = (int) (((float) i) + this.groupCells.get(i2).measure());
            }
            if (this.hintView.getMeasuredHeight() <= 0) {
                this.hintView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, NUM), View.MeasureSpec.makeMeasureSpec(9999, Integer.MIN_VALUE));
            }
            return i + this.hintView.getMeasuredHeight();
        }

        private int measureHeight() {
            return Math.max(measurePureHeight(), (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(45.0f));
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
            ChatActionCell chatActionCell = this.hintView;
            chatActionCell.setVisiblePart(f, chatActionCell.getMeasuredHeight());
            if (this.hintView.hasGradientService()) {
                this.hintView.drawBackground(canvas, true);
            }
            this.hintView.draw(canvas);
            canvas.restore();
            if (ChatAttachAlertPhotoLayoutPreview.this.draggingCell != null) {
                canvas.save();
                Point dragTranslate = dragTranslate();
                canvas.translate(dragTranslate.x, dragTranslate.y);
                if (ChatAttachAlertPhotoLayoutPreview.this.draggingCell.draw(canvas, true)) {
                    invalidate();
                }
                canvas.restore();
            }
            super.onDraw(canvas);
        }

        /* access modifiers changed from: package-private */
        public Point dragTranslate() {
            if (ChatAttachAlertPhotoLayoutPreview.this.draggingCell == null) {
                Point point = this.tmpPoint;
                point.x = 0.0f;
                point.y = 0.0f;
                return point;
            }
            if (!ChatAttachAlertPhotoLayoutPreview.this.draggingCellHiding) {
                RectF rect = ChatAttachAlertPhotoLayoutPreview.this.draggingCell.rect();
                RectF rect2 = ChatAttachAlertPhotoLayoutPreview.this.draggingCell.rect(1.0f);
                this.tmpPoint.x = AndroidUtilities.lerp(rect2.left + (rect.width() / 2.0f), ChatAttachAlertPhotoLayoutPreview.this.draggingCellTouchX - ((ChatAttachAlertPhotoLayoutPreview.this.draggingCellLeft - 0.5f) * ChatAttachAlertPhotoLayoutPreview.this.draggingCellFromWidth), this.draggingT);
                this.tmpPoint.y = AndroidUtilities.lerp(ChatAttachAlertPhotoLayoutPreview.this.draggingCell.groupCell.y + rect2.top + (rect.height() / 2.0f), (ChatAttachAlertPhotoLayoutPreview.this.draggingCellTouchY - ((ChatAttachAlertPhotoLayoutPreview.this.draggingCellTop - 0.5f) * ChatAttachAlertPhotoLayoutPreview.this.draggingCellFromHeight)) + ChatAttachAlertPhotoLayoutPreview.this.draggingCellGroupY, this.draggingT);
            } else {
                RectF rect3 = ChatAttachAlertPhotoLayoutPreview.this.draggingCell.rect();
                RectF rect4 = ChatAttachAlertPhotoLayoutPreview.this.draggingCell.rect(1.0f);
                this.tmpPoint.x = AndroidUtilities.lerp(rect4.left + (rect3.width() / 2.0f), this.savedDragFromX, this.draggingT / this.savedDraggingT);
                this.tmpPoint.y = AndroidUtilities.lerp(ChatAttachAlertPhotoLayoutPreview.this.draggingCell.groupCell.y + rect4.top + (rect3.height() / 2.0f), this.savedDragFromY, this.draggingT / this.savedDraggingT);
            }
            return this.tmpPoint;
        }

        /* access modifiers changed from: package-private */
        public void stopDragging() {
            if (ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator != null) {
                ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.cancel();
            }
            Point dragTranslate = dragTranslate();
            this.savedDraggingT = this.draggingT;
            this.savedDragFromX = dragTranslate.x;
            this.savedDragFromY = dragTranslate.y;
            boolean unused = ChatAttachAlertPhotoLayoutPreview.this.draggingCellHiding = true;
            ValueAnimator unused2 = ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator = ValueAnimator.ofFloat(new float[]{this.savedDraggingT, 0.0f});
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.addUpdateListener(new ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda0(this));
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    PreviewGroupCell.MediaCell unused = ChatAttachAlertPhotoLayoutPreview.this.draggingCell = null;
                    boolean unused2 = ChatAttachAlertPhotoLayoutPreview.this.draggingCellHiding = false;
                    PreviewGroupsView.this.invalidate();
                }
            });
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.setDuration(200);
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.start();
            invalidate();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$stopDragging$0(ValueAnimator valueAnimator) {
            this.draggingT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
        }

        /* access modifiers changed from: package-private */
        public void startDragging(PreviewGroupCell.MediaCell mediaCell) {
            PreviewGroupCell.MediaCell unused = ChatAttachAlertPhotoLayoutPreview.this.draggingCell = mediaCell;
            ChatAttachAlertPhotoLayoutPreview chatAttachAlertPhotoLayoutPreview = ChatAttachAlertPhotoLayoutPreview.this;
            float unused2 = chatAttachAlertPhotoLayoutPreview.draggingCellGroupY = chatAttachAlertPhotoLayoutPreview.draggingCell.groupCell.y;
            boolean unused3 = ChatAttachAlertPhotoLayoutPreview.this.draggingCellHiding = false;
            this.draggingT = 0.0f;
            invalidate();
            if (ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator != null) {
                ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.cancel();
            }
            ValueAnimator unused4 = ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.addUpdateListener(new ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda1(this));
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.setDuration(200);
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.start();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$startDragging$1(ValueAnimator valueAnimator) {
            this.draggingT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
        }

        /* JADX WARNING: Removed duplicated region for block: B:155:0x03e5  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouchEvent(android.view.MotionEvent r20) {
            /*
                r19 = this;
                r0 = r19
                float r1 = r20.getX()
                float r2 = r20.getY()
                java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell> r3 = r0.groupCells
                int r3 = r3.size()
                r4 = 0
                r6 = 0
                r7 = 0
            L_0x0013:
                if (r6 >= r3) goto L_0x0030
                java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell> r9 = r0.groupCells
                java.lang.Object r9 = r9.get(r6)
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell r9 = (org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.PreviewGroupCell) r9
                float r10 = r9.measure()
                int r11 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
                if (r11 < 0) goto L_0x002c
                float r11 = r7 + r10
                int r11 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1))
                if (r11 > 0) goto L_0x002c
                goto L_0x0031
            L_0x002c:
                float r7 = r7 + r10
                int r6 = r6 + 1
                goto L_0x0013
            L_0x0030:
                r9 = 0
            L_0x0031:
                if (r9 == 0) goto L_0x0056
                java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell> r6 = r9.media
                int r6 = r6.size()
                r10 = 0
            L_0x003a:
                if (r10 >= r6) goto L_0x0056
                java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell> r11 = r9.media
                java.lang.Object r11 = r11.get(r10)
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r11 = (org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.PreviewGroupCell.MediaCell) r11
                if (r11 == 0) goto L_0x0053
                android.graphics.RectF r12 = r11.drawingRect()
                float r13 = r2 - r7
                boolean r12 = r12.contains(r1, r13)
                if (r12 == 0) goto L_0x0053
                goto L_0x0057
            L_0x0053:
                int r10 = r10 + 1
                goto L_0x003a
            L_0x0056:
                r11 = 0
            L_0x0057:
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r6 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r6 = r6.draggingCell
                if (r6 == 0) goto L_0x017a
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r6 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r6 = r6.draggingCell
                android.graphics.RectF r6 = r6.rect()
                org.telegram.ui.Components.Point r12 = r19.dragTranslate()
                android.graphics.RectF r13 = new android.graphics.RectF
                r13.<init>()
                float r14 = r12.x
                float r12 = r12.y
                float r15 = r6.width()
                r16 = 1073741824(0x40000000, float:2.0)
                float r15 = r15 / r16
                float r15 = r14 - r15
                float r17 = r6.height()
                float r17 = r17 / r16
                float r5 = r12 - r17
                float r17 = r6.width()
                float r17 = r17 / r16
                float r14 = r14 + r17
                float r6 = r6.height()
                float r6 = r6 / r16
                float r12 = r12 + r6
                r13.set(r15, r5, r14, r12)
                r5 = 0
                r6 = 0
                r12 = 0
                r14 = 0
            L_0x009e:
                if (r5 >= r3) goto L_0x00cf
                java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell> r15 = r0.groupCells
                java.lang.Object r15 = r15.get(r5)
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell r15 = (org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.PreviewGroupCell) r15
                float r16 = r15.measure()
                float r8 = r12 + r16
                float r7 = r13.top
                int r7 = (r8 > r7 ? 1 : (r8 == r7 ? 0 : -1))
                if (r7 < 0) goto L_0x00cb
                float r7 = r13.bottom
                int r18 = (r7 > r12 ? 1 : (r7 == r12 ? 0 : -1))
                if (r18 < 0) goto L_0x00cb
                float r7 = java.lang.Math.min(r8, r7)
                float r10 = r13.top
                float r10 = java.lang.Math.max(r12, r10)
                float r7 = r7 - r10
                int r10 = (r7 > r14 ? 1 : (r7 == r14 ? 0 : -1))
                if (r10 <= 0) goto L_0x00cb
                r14 = r7
                r6 = r15
            L_0x00cb:
                int r5 = r5 + 1
                r12 = r8
                goto L_0x009e
            L_0x00cf:
                if (r6 == 0) goto L_0x0178
                java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell> r3 = r6.media
                int r3 = r3.size()
                r5 = 0
                r7 = 0
                r8 = 0
            L_0x00da:
                if (r7 >= r3) goto L_0x017c
                java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell> r10 = r6.media
                java.lang.Object r10 = r10.get(r7)
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r10 = (org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.PreviewGroupCell.MediaCell) r10
                if (r10 == 0) goto L_0x0173
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r12 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r12 = r12.draggingCell
                if (r10 == r12) goto L_0x0173
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator r12 = r6.group
                java.util.ArrayList<org.telegram.messenger.MediaController$PhotoEntry> r12 = r12.photos
                org.telegram.messenger.MediaController$PhotoEntry r14 = r10.photoEntry
                boolean r12 = r12.contains(r14)
                if (r12 == 0) goto L_0x0173
                android.graphics.RectF r12 = r10.drawingRect()
                int r14 = r10.positionFlags
                r14 = r14 & 4
                if (r14 <= 0) goto L_0x010a
                r12.top = r4
            L_0x010a:
                int r14 = r10.positionFlags
                r15 = 1
                r14 = r14 & r15
                if (r14 <= 0) goto L_0x0114
                r12.left = r4
            L_0x0114:
                int r14 = r10.positionFlags
                r15 = 2
                r14 = r14 & r15
                if (r14 <= 0) goto L_0x0123
                int r14 = r19.getWidth()
                float r14 = (float) r14
                r12.right = r14
            L_0x0123:
                int r14 = r10.positionFlags
                r14 = r14 & 8
                if (r14 <= 0) goto L_0x0131
                float r14 = r6.height
                r12.bottom = r14
            L_0x0131:
                boolean r14 = android.graphics.RectF.intersects(r13, r12)
                if (r14 == 0) goto L_0x0173
                float r14 = r12.right
                float r15 = r13.right
                float r14 = java.lang.Math.min(r14, r15)
                float r15 = r12.left
                float r4 = r13.left
                float r4 = java.lang.Math.max(r15, r4)
                float r14 = r14 - r4
                float r4 = r12.bottom
                float r15 = r13.bottom
                float r4 = java.lang.Math.min(r4, r15)
                float r12 = r12.top
                float r15 = r13.top
                float r12 = java.lang.Math.max(r12, r15)
                float r4 = r4 - r12
                float r14 = r14 * r4
                float r4 = r13.width()
                float r12 = r13.height()
                float r4 = r4 * r12
                float r14 = r14 / r4
                r4 = 1041865114(0x3e19999a, float:0.15)
                int r4 = (r14 > r4 ? 1 : (r14 == r4 ? 0 : -1))
                if (r4 <= 0) goto L_0x0173
                int r4 = (r14 > r8 ? 1 : (r14 == r8 ? 0 : -1))
                if (r4 <= 0) goto L_0x0173
                r5 = r10
                r8 = r14
            L_0x0173:
                int r7 = r7 + 1
                r4 = 0
                goto L_0x00da
            L_0x0178:
                r5 = 0
                goto L_0x017c
            L_0x017a:
                r5 = 0
                r6 = 0
            L_0x017c:
                int r3 = r20.getAction()
                r7 = 0
                if (r3 != 0) goto L_0x01ec
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r4 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r4 = r4.draggingCell
                if (r4 != 0) goto L_0x01ec
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r4 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.RecyclerListView r10 = r4.listView
                boolean r10 = r10.scrollingByUser
                if (r10 != 0) goto L_0x01ec
                android.animation.ValueAnimator r4 = r4.draggingAnimator
                if (r4 == 0) goto L_0x01a6
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r4 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                android.animation.ValueAnimator r4 = r4.draggingAnimator
                boolean r4 = r4.isRunning()
                if (r4 != 0) goto L_0x01ec
            L_0x01a6:
                if (r9 == 0) goto L_0x01ec
                if (r11 == 0) goto L_0x01ec
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator r4 = r9.group
                if (r4 == 0) goto L_0x01ec
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator r4 = r9.group
                java.util.ArrayList<org.telegram.messenger.MediaController$PhotoEntry> r4 = r4.photos
                org.telegram.messenger.MediaController$PhotoEntry r10 = r11.photoEntry
                boolean r4 = r4.contains(r10)
                if (r4 == 0) goto L_0x01ec
                r0.tapGroupCell = r9
                r0.tapMediaCell = r11
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r4 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                float unused = r4.draggingCellTouchX = r1
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                float unused = r1.draggingCellTouchY = r2
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                r2 = 0
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.PreviewGroupCell.MediaCell unused = r1.draggingCell = r2
                long r1 = android.os.SystemClock.elapsedRealtime()
                r0.tapTime = r1
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r4 = r0.tapMediaCell
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda3 r5 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda3
                r5.<init>(r0, r1, r4)
                int r1 = android.view.ViewConfiguration.getLongPressTimeout()
                long r1 = (long) r1
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r5, r1)
                r19.invalidate()
                goto L_0x033d
            L_0x01ec:
                r4 = 2
                if (r3 != r4) goto L_0x021f
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r4 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r4 = r4.draggingCell
                if (r4 == 0) goto L_0x021f
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r4 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                boolean r4 = r4.draggingCellHiding
                if (r4 != 0) goto L_0x021f
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r4 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                float unused = r4.draggingCellTouchX = r1
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                float unused = r1.draggingCellTouchY = r2
                boolean r1 = r0.scrollerStarted
                if (r1 != 0) goto L_0x0218
                r1 = 1
                r0.scrollerStarted = r1
                java.lang.Runnable r2 = r0.scroller
                r4 = 16
                r0.postDelayed(r2, r4)
                goto L_0x0219
            L_0x0218:
                r1 = 1
            L_0x0219:
                r19.invalidate()
            L_0x021c:
                r15 = 1
                goto L_0x03d4
            L_0x021f:
                r1 = 1
                if (r3 != r1) goto L_0x0341
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r1 = r1.draggingCell
                if (r1 == 0) goto L_0x0340
                if (r9 == 0) goto L_0x0237
                if (r11 == 0) goto L_0x0237
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r1 = r1.draggingCell
                if (r11 == r1) goto L_0x0237
                goto L_0x0254
            L_0x0237:
                if (r6 == 0) goto L_0x0252
                if (r5 == 0) goto L_0x0252
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r1 = r1.draggingCell
                if (r5 == r1) goto L_0x0252
                org.telegram.messenger.MediaController$PhotoEntry r1 = r5.photoEntry
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r2 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r2 = r2.draggingCell
                org.telegram.messenger.MediaController$PhotoEntry r2 = r2.photoEntry
                if (r1 == r2) goto L_0x0252
                r11 = r5
                r9 = r6
                goto L_0x0254
            L_0x0252:
                r9 = 0
                r11 = 0
            L_0x0254:
                if (r9 == 0) goto L_0x033a
                if (r11 == 0) goto L_0x033a
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r1 = r1.draggingCell
                if (r11 == r1) goto L_0x033a
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r1 = r1.draggingCell
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell r1 = r1.groupCell
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator r1 = r1.group
                java.util.ArrayList<org.telegram.messenger.MediaController$PhotoEntry> r1 = r1.photos
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r2 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r2 = r2.draggingCell
                org.telegram.messenger.MediaController$PhotoEntry r2 = r2.photoEntry
                int r1 = r1.indexOf(r2)
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator r2 = r9.group
                java.util.ArrayList<org.telegram.messenger.MediaController$PhotoEntry> r2 = r2.photos
                org.telegram.messenger.MediaController$PhotoEntry r4 = r11.photoEntry
                int r2 = r2.indexOf(r4)
                if (r1 < 0) goto L_0x02b1
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r4 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r4 = r4.draggingCell
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell r4 = r4.groupCell
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator r4 = r4.group
                java.util.ArrayList<org.telegram.messenger.MediaController$PhotoEntry> r4 = r4.photos
                r4.remove(r1)
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r1 = r1.draggingCell
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell r1 = r1.groupCell
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r4 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r4 = r4.draggingCell
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell r4 = r4.groupCell
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator r4 = r4.group
                r5 = 1
                r1.setGroup(r4, r5)
            L_0x02b1:
                if (r2 < 0) goto L_0x0326
                java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell> r1 = r0.groupCells
                int r1 = r1.indexOf(r9)
                java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell> r4 = r0.groupCells
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r5 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r5 = r5.draggingCell
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell r5 = r5.groupCell
                int r4 = r4.indexOf(r5)
                if (r1 <= r4) goto L_0x02cb
                int r2 = r2 + 1
            L_0x02cb:
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r1 = r1.draggingCell
                org.telegram.messenger.MediaController$PhotoEntry r1 = r1.photoEntry
                r0.pushToGroup(r9, r1, r2)
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r1 = r1.draggingCell
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell r1 = r1.groupCell
                if (r1 == r9) goto L_0x0326
                java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell> r1 = r9.media
                int r1 = r1.size()
                r2 = 0
            L_0x02e7:
                if (r2 >= r1) goto L_0x0301
                java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell> r4 = r9.media
                java.lang.Object r4 = r4.get(r2)
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r4 = (org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.PreviewGroupCell.MediaCell) r4
                org.telegram.messenger.MediaController$PhotoEntry r5 = r4.photoEntry
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r6 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r6 = r6.draggingCell
                org.telegram.messenger.MediaController$PhotoEntry r6 = r6.photoEntry
                if (r5 != r6) goto L_0x02fe
                goto L_0x0302
            L_0x02fe:
                int r2 = r2 + 1
                goto L_0x02e7
            L_0x0301:
                r4 = 0
            L_0x0302:
                if (r4 == 0) goto L_0x0326
                r19.remeasure()
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r1 = r1.draggingCell
                r4.layoutFrom(r1)
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.PreviewGroupCell.MediaCell unused = r1.draggingCell = r4
                r4.groupCell = r9
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r1 = r1.draggingCell
                r2 = 1065353216(0x3var_, float:1.0)
                r1.fromScale = r2
                r4.scale = r2
                r19.remeasure()
            L_0x0326:
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this     // Catch:{ Exception -> 0x032d }
                r2 = 7
                r4 = 2
                r1.performHapticFeedback(r2, r4)     // Catch:{ Exception -> 0x032d }
            L_0x032d:
                r19.updateGroups()
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayout r1 = r1.photoLayout
                r2 = 0
                r0.toPhotoLayout(r1, r2)
            L_0x033a:
                r19.stopDragging()
            L_0x033d:
                r1 = 1
                goto L_0x021c
            L_0x0340:
                r1 = 1
            L_0x0341:
                if (r3 != r1) goto L_0x03d2
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r2 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r2 = r2.draggingCell
                if (r2 != 0) goto L_0x03d2
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r2 = r0.tapMediaCell
                if (r2 == 0) goto L_0x03d2
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell r2 = r0.tapGroupCell
                if (r2 == 0) goto L_0x03d2
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r2 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                int r2 = r2.getSelectedItemsCount()
                if (r2 <= r1) goto L_0x03c3
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r1 = r0.tapMediaCell
                org.telegram.messenger.MediaController$PhotoEntry r13 = r1.photoEntry
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell r1 = r0.tapGroupCell
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator r1 = r1.group
                java.util.ArrayList<org.telegram.messenger.MediaController$PhotoEntry> r1 = r1.photos
                int r1 = r1.indexOf(r13)
                if (r1 < 0) goto L_0x03b2
                r0.saveDeletedImageId(r13)
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell r2 = r0.tapGroupCell
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator r4 = r2.group
                java.util.ArrayList<org.telegram.messenger.MediaController$PhotoEntry> r4 = r4.photos
                r4.remove(r1)
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator r4 = r2.group
                r5 = 1
                r2.setGroup(r4, r5)
                r19.updateGroups()
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r4 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayout r4 = r4.photoLayout
                r6 = 0
                r0.toPhotoLayout(r4, r6)
                int r4 = r0.undoViewId
                int r4 = r4 + r5
                r0.undoViewId = r4
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r5 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.UndoView r9 = r5.undoView
                r10 = 0
                r12 = 82
                r14 = 0
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda4 r15 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda4
                r15.<init>(r0, r2, r13, r1)
                r9.showWithAction(r10, r12, r13, r14, r15)
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda2 r1 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda2
                r1.<init>(r0, r4)
                r4 = 4000(0xfa0, double:1.9763E-320)
                r0.postDelayed(r1, r4)
            L_0x03b2:
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                android.animation.ValueAnimator r1 = r1.draggingAnimator
                if (r1 == 0) goto L_0x03c3
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r1 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                android.animation.ValueAnimator r1 = r1.draggingAnimator
                r1.cancel()
            L_0x03c3:
                r1 = 0
                r0.tapMediaCell = r1
                r0.tapTime = r7
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r2 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.PreviewGroupCell.MediaCell unused = r2.draggingCell = r1
                r1 = 0
                r0.draggingT = r1
                goto L_0x033d
            L_0x03d2:
                r1 = 1
                r15 = 0
            L_0x03d4:
                if (r3 == r1) goto L_0x03d9
                r2 = 3
                if (r3 != r2) goto L_0x03ea
            L_0x03d9:
                r0.tapTime = r7
                java.lang.Runnable r2 = r0.scroller
                r0.removeCallbacks(r2)
                r2 = 0
                r0.scrollerStarted = r2
                if (r15 != 0) goto L_0x03ea
                r19.stopDragging()
                r10 = 1
                goto L_0x03eb
            L_0x03ea:
                r10 = r15
            L_0x03eb:
                return r10
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
                float unused = chatAttachAlertPhotoLayoutPreview.draggingCellLeft = (((chatAttachAlertPhotoLayoutPreview.draggingCellTouchX - rect.left) / rect.width()) + 0.5f) / 2.0f;
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
            this.draggingT = 0.0f;
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
            previewGroupCell.group.photos.add(Math.min(previewGroupCell.group.photos.size(), i), photoEntry);
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
                /* access modifiers changed from: private */
                public int positionFlags;
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
                    float access$3000 = PreviewGroupCell.this.left + (this.rect.left * PreviewGroupCell.this.width);
                    float access$3100 = PreviewGroupCell.this.top + (this.rect.top * PreviewGroupCell.this.height);
                    float width = this.rect.width() * PreviewGroupCell.this.width;
                    float height = this.rect.height() * PreviewGroupCell.this.height;
                    if (f < 1.0f && this.fromRect != null) {
                        access$3000 = AndroidUtilities.lerp(PreviewGroupCell.this.left + (this.fromRect.left * PreviewGroupCell.this.width), access$3000, f);
                        access$3100 = AndroidUtilities.lerp(PreviewGroupCell.this.top + (this.fromRect.top * PreviewGroupCell.this.height), access$3100, f);
                        width = AndroidUtilities.lerp(this.fromRect.width() * PreviewGroupCell.this.width, width, f);
                        height = AndroidUtilities.lerp(this.fromRect.height() * PreviewGroupCell.this.height, height, f);
                    }
                    int i = this.positionFlags;
                    if ((i & 4) == 0) {
                        int i2 = PreviewGroupCell.this.halfGap;
                        access$3100 += (float) i2;
                        height -= (float) i2;
                    }
                    if ((i & 8) == 0) {
                        height -= (float) PreviewGroupCell.this.halfGap;
                    }
                    if ((i & 1) == 0) {
                        int i3 = PreviewGroupCell.this.halfGap;
                        access$3000 += (float) i3;
                        width -= (float) i3;
                    }
                    if ((i & 2) == 0) {
                        width -= (float) PreviewGroupCell.this.halfGap;
                    }
                    this.tempRect.set(access$3000, access$3100, width + access$3000, height + access$3100);
                    return this.tempRect;
                }

                public RectF drawingRect() {
                    float f = 0.0f;
                    if (this.rect == null || this.image == null) {
                        this.tempRect.set(0.0f, 0.0f, 0.0f, 0.0f);
                        return this.tempRect;
                    }
                    if (ChatAttachAlertPhotoLayoutPreview.this.draggingCell != null && ChatAttachAlertPhotoLayoutPreview.this.draggingCell.photoEntry == this.photoEntry) {
                        f = PreviewGroupsView.this.draggingT;
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
                    float access$3200 = ChatAttachAlertPhotoLayoutPreview.this.draggingCell == this ? PreviewGroupsView.this.draggingT : 0.0f;
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
                    float lerp2 = AndroidUtilities.lerp(f3, dp, access$3200);
                    float lerp3 = AndroidUtilities.lerp(f4, dp, access$3200);
                    float lerp4 = AndroidUtilities.lerp(f5, dp, access$3200);
                    float lerp5 = AndroidUtilities.lerp(f6, dp, access$3200);
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
                this.group = groupCalculator2;
                if (groupCalculator2 != null) {
                    groupCalculator.calculate();
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
