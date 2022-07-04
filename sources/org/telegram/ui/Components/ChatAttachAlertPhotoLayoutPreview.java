package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.PhotoViewer;

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
    private final long durationMultiplier = 1;
    /* access modifiers changed from: private */
    public PreviewGroupsView groupsView;
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

    static /* synthetic */ float access$1416(ChatAttachAlertPhotoLayoutPreview x0, float x1) {
        float f = x0.draggingCellTouchY + x1;
        x0.draggingCellTouchY = f;
        return f;
    }

    public float getPreviewScale() {
        return AndroidUtilities.displaySize.y > AndroidUtilities.displaySize.x ? 0.8f : 0.45f;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ChatAttachAlertPhotoLayoutPreview(ChatAttachAlert alert, Context context, ChatActivity.ThemeDelegate themeDelegate2) {
        super(alert, context, themeDelegate2);
        Context context2 = context;
        this.isPortrait = AndroidUtilities.displaySize.y > AndroidUtilities.displaySize.x;
        this.themeDelegate = themeDelegate2;
        setWillNotDraw(false);
        ActionBarMenu menu = this.parentAlert.actionBar.createMenu();
        this.header = new TextView(context2);
        ActionBarMenuItem dropDownContainer = new ActionBarMenuItem(context, menu, 0, 0, this.resourcesProvider) {
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(info);
                info.setText(ChatAttachAlertPhotoLayoutPreview.this.header.getText());
            }
        };
        this.parentAlert.actionBar.addView(dropDownContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, AndroidUtilities.isTablet() ? 64.0f : 56.0f, 0.0f, 40.0f, 0.0f));
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
        dropDownContainer.addView(this.header, LayoutHelper.createFrame(-2, -2.0f, 16, 16.0f, 0.0f, 0.0f, 0.0f));
        AnonymousClass2 r1 = new RecyclerListView(context2, this.resourcesProvider) {
            public void onScrolled(int dx, int dy) {
                ChatAttachAlertPhotoLayoutPreview.this.invalidate();
                ChatAttachAlertPhotoLayoutPreview.this.parentAlert.updateLayout(ChatAttachAlertPhotoLayoutPreview.this, true, dy);
                ChatAttachAlertPhotoLayoutPreview.this.groupsView.onScroll();
                super.onScrolled(dx, dy);
            }

            public boolean onTouchEvent(MotionEvent ev) {
                if (ChatAttachAlertPhotoLayoutPreview.this.draggingCell != null) {
                    return false;
                }
                return super.onTouchEvent(ev);
            }

            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (ChatAttachAlertPhotoLayoutPreview.this.draggingCell != null) {
                    return false;
                }
                return super.onInterceptTouchEvent(ev);
            }
        };
        this.listView = r1;
        r1.setAdapter(new RecyclerView.Adapter() {
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RecyclerListView.Holder(ChatAttachAlertPhotoLayoutPreview.this.groupsView);
            }

            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            }

            public int getItemCount() {
                return 1;
            }
        });
        RecyclerListView recyclerListView = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.listView.setClipChildren(false);
        this.listView.setClipToPadding(false);
        this.listView.setOverScrollMode(2);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(46.0f));
        PreviewGroupsView previewGroupsView = new PreviewGroupsView(context2);
        this.groupsView = previewGroupsView;
        previewGroupsView.setClipToPadding(true);
        this.groupsView.setClipChildren(true);
        addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.photoLayout = this.parentAlert.getPhotoLayout();
        this.groupsView.deletedPhotos.clear();
        this.groupsView.fromPhotoLayout(this.photoLayout);
        UndoView undoView2 = new UndoView(context2, (BaseFragment) null, false, this.parentAlert.parentThemeDelegate);
        this.undoView = undoView2;
        undoView2.setEnterOffsetMargin(AndroidUtilities.dp(32.0f));
        addView(this.undoView, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 52.0f));
        this.videoPlayImage = context.getResources().getDrawable(NUM);
    }

    /* access modifiers changed from: package-private */
    public void onShow(ChatAttachAlert.AttachAlertLayout previousLayout) {
        this.shown = true;
        if (previousLayout instanceof ChatAttachAlertPhotoLayout) {
            this.photoLayout = (ChatAttachAlertPhotoLayout) previousLayout;
            this.groupsView.deletedPhotos.clear();
            this.groupsView.fromPhotoLayout(this.photoLayout);
            this.groupsView.requestLayout();
            this.layoutManager.scrollToPositionWithOffset(0, 0);
            this.listView.post(new ChatAttachAlertPhotoLayoutPreview$$ExternalSyntheticLambda1(this, previousLayout));
            postDelayed(new ChatAttachAlertPhotoLayoutPreview$$ExternalSyntheticLambda0(this), 250);
            this.groupsView.toPhotoLayout(this.photoLayout, false);
        } else {
            scrollToTop();
        }
        ViewPropertyAnimator viewPropertyAnimator = this.headerAnimator;
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.cancel();
        }
        ViewPropertyAnimator interpolator = this.header.animate().alpha(1.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.headerAnimator = interpolator;
        interpolator.start();
    }

    /* renamed from: lambda$onShow$0$org-telegram-ui-Components-ChatAttachAlertPhotoLayoutPreview  reason: not valid java name */
    public /* synthetic */ void m848x7a1626c9(ChatAttachAlert.AttachAlertLayout previousLayout) {
        int currentItemTop = previousLayout.getCurrentItemTop();
        int paddingTop2 = previousLayout.getListTopPadding();
        this.listView.scrollBy(0, currentItemTop > AndroidUtilities.dp(7.0f) ? paddingTop2 - currentItemTop : paddingTop2);
    }

    /* renamed from: lambda$onShow$1$org-telegram-ui-Components-ChatAttachAlertPhotoLayoutPreview  reason: not valid java name */
    public /* synthetic */ void m849xe549668() {
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
    public boolean shouldHideBottomButtons() {
        return true;
    }

    /* access modifiers changed from: package-private */
    public void applyCaption(CharSequence text) {
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = this.photoLayout;
        if (chatAttachAlertPhotoLayout != null) {
            chatAttachAlertPhotoLayout.applyCaption(text);
        }
    }

    private class GroupCalculator {
        float height;
        private final int maxSizeWidth = 1000;
        int maxX;
        int maxY;
        ArrayList<MediaController.PhotoEntry> photos;
        public ArrayList<MessageObject.GroupedMessagePosition> posArray = new ArrayList<>();
        public HashMap<MediaController.PhotoEntry, MessageObject.GroupedMessagePosition> positions = new HashMap<>();
        int width;

        private class MessageGroupedLayoutAttempt {
            public float[] heights;
            public int[] lineCounts;

            public MessageGroupedLayoutAttempt(int i1, int i2, float f1, float f2) {
                this.lineCounts = new int[]{i1, i2};
                this.heights = new float[]{f1, f2};
            }

            public MessageGroupedLayoutAttempt(int i1, int i2, int i3, float f1, float f2, float f3) {
                this.lineCounts = new int[]{i1, i2, i3};
                this.heights = new float[]{f1, f2, f3};
            }

            public MessageGroupedLayoutAttempt(int i1, int i2, int i3, int i4, float f1, float f2, float f3, float f4) {
                this.lineCounts = new int[]{i1, i2, i3, i4};
                this.heights = new float[]{f1, f2, f3, f4};
            }
        }

        private float multiHeight(float[] array, int start, int end) {
            float sum = 0.0f;
            for (int a = start; a < end; a++) {
                sum += array[a];
            }
            return 1000.0f / sum;
        }

        public GroupCalculator(ArrayList<MediaController.PhotoEntry> photos2) {
            this.photos = photos2;
            calculate();
        }

        public void calculate(ArrayList<MediaController.PhotoEntry> photos2) {
            this.photos = photos2;
            calculate();
        }

        /* JADX WARNING: Code restructure failed: missing block: B:186:0x0855, code lost:
            if (r5.lineCounts[2] > r5.lineCounts[3]) goto L_0x0859;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void calculate() {
            /*
                r43 = this;
                r11 = r43
                r12 = 200(0xc8, float:2.8E-43)
                java.util.ArrayList<org.telegram.messenger.MediaController$PhotoEntry> r0 = r11.photos
                int r13 = r0.size()
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r0 = r11.posArray
                r0.clear()
                java.util.HashMap<org.telegram.messenger.MediaController$PhotoEntry, org.telegram.messenger.MessageObject$GroupedMessagePosition> r0 = r11.positions
                r0.clear()
                r14 = 0
                if (r13 != 0) goto L_0x0021
                r11.width = r14
                r0 = 0
                r11.height = r0
                r11.maxX = r14
                r11.maxY = r14
                return
            L_0x0021:
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r0 = r11.posArray
                r0.ensureCapacity(r13)
                r15 = 1145798656(0x444b8000, float:814.0)
                char[] r10 = new char[r13]
                r0 = 1065353216(0x3var_, float:1.0)
                r1 = 0
                r2 = 0
                r16 = r1
                r1 = r0
            L_0x0032:
                r17 = 1067030938(0x3var_a, float:1.2)
                r0 = 1
                if (r2 >= r13) goto L_0x010f
                java.util.ArrayList<org.telegram.messenger.MediaController$PhotoEntry> r6 = r11.photos
                java.lang.Object r6 = r6.get(r2)
                org.telegram.messenger.MediaController$PhotoEntry r6 = (org.telegram.messenger.MediaController.PhotoEntry) r6
                org.telegram.messenger.MessageObject$GroupedMessagePosition r7 = new org.telegram.messenger.MessageObject$GroupedMessagePosition
                r7.<init>()
                int r8 = r13 + -1
                if (r2 != r8) goto L_0x004b
                r8 = 1
                goto L_0x004c
            L_0x004b:
                r8 = 0
            L_0x004c:
                r7.last = r8
                org.telegram.messenger.MediaController$CropState r8 = r6.cropState
                if (r8 == 0) goto L_0x0057
                org.telegram.messenger.MediaController$CropState r8 = r6.cropState
                int r8 = r8.width
                goto L_0x0059
            L_0x0057:
                int r8 = r6.width
            L_0x0059:
                org.telegram.messenger.MediaController$CropState r9 = r6.cropState
                if (r9 == 0) goto L_0x0062
                org.telegram.messenger.MediaController$CropState r9 = r6.cropState
                int r9 = r9.height
                goto L_0x0064
            L_0x0062:
                int r9 = r6.height
            L_0x0064:
                java.util.HashMap r4 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.photoRotate
                boolean r4 = r4.containsKey(r6)
                if (r4 == 0) goto L_0x007d
                java.util.HashMap r0 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.photoRotate
                java.lang.Object r0 = r0.get(r6)
                java.lang.Boolean r0 = (java.lang.Boolean) r0
                boolean r0 = r0.booleanValue()
                goto L_0x00ce
            L_0x007d:
                r4 = 0
                boolean r3 = r6.isVideo     // Catch:{ Exception -> 0x00c1 }
                if (r3 == 0) goto L_0x00ae
                int r3 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x00c1 }
                r14 = 17
                if (r3 < r14) goto L_0x00c2
                android.media.MediaMetadataRetriever r3 = new android.media.MediaMetadataRetriever     // Catch:{ Exception -> 0x00c1 }
                r3.<init>()     // Catch:{ Exception -> 0x00c1 }
                java.lang.String r14 = r6.path     // Catch:{ Exception -> 0x00c1 }
                r3.setDataSource(r14)     // Catch:{ Exception -> 0x00c1 }
                r14 = 24
                java.lang.String r14 = r3.extractMetadata(r14)     // Catch:{ Exception -> 0x00c1 }
                if (r14 == 0) goto L_0x00ab
                java.lang.String r5 = "90"
                boolean r5 = r14.equals(r5)     // Catch:{ Exception -> 0x00c1 }
                if (r5 != 0) goto L_0x00aa
                java.lang.String r5 = "270"
                boolean r5 = r14.equals(r5)     // Catch:{ Exception -> 0x00c1 }
                if (r5 == 0) goto L_0x00ab
            L_0x00aa:
                goto L_0x00ac
            L_0x00ab:
                r0 = 0
            L_0x00ac:
                r4 = r0
                goto L_0x00c2
            L_0x00ae:
                androidx.exifinterface.media.ExifInterface r3 = new androidx.exifinterface.media.ExifInterface     // Catch:{ Exception -> 0x00c1 }
                java.lang.String r5 = r6.path     // Catch:{ Exception -> 0x00c1 }
                r3.<init>((java.lang.String) r5)     // Catch:{ Exception -> 0x00c1 }
                java.lang.String r5 = "Orientation"
                int r0 = r3.getAttributeInt(r5, r0)     // Catch:{ Exception -> 0x00c1 }
                switch(r0) {
                    case 6: goto L_0x00bf;
                    case 7: goto L_0x00be;
                    case 8: goto L_0x00bf;
                    default: goto L_0x00be;
                }
            L_0x00be:
                goto L_0x00c2
            L_0x00bf:
                r4 = 1
                goto L_0x00c2
            L_0x00c1:
                r0 = move-exception
            L_0x00c2:
                r0 = r4
                java.util.HashMap r3 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.photoRotate
                java.lang.Boolean r4 = java.lang.Boolean.valueOf(r0)
                r3.put(r6, r4)
            L_0x00ce:
                if (r0 == 0) goto L_0x00d3
                r3 = r8
                r8 = r9
                r9 = r3
            L_0x00d3:
                float r3 = (float) r8
                float r4 = (float) r9
                float r3 = r3 / r4
                r7.aspectRatio = r3
                float r3 = r7.aspectRatio
                int r3 = (r3 > r17 ? 1 : (r3 == r17 ? 0 : -1))
                if (r3 <= 0) goto L_0x00e2
                r3 = 119(0x77, float:1.67E-43)
                goto L_0x00f0
            L_0x00e2:
                float r3 = r7.aspectRatio
                r4 = 1061997773(0x3f4ccccd, float:0.8)
                int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
                if (r3 >= 0) goto L_0x00ee
                r3 = 110(0x6e, float:1.54E-43)
                goto L_0x00f0
            L_0x00ee:
                r3 = 113(0x71, float:1.58E-43)
            L_0x00f0:
                r10[r2] = r3
                float r3 = r7.aspectRatio
                float r1 = r1 + r3
                float r3 = r7.aspectRatio
                r4 = 1073741824(0x40000000, float:2.0)
                int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
                if (r3 <= 0) goto L_0x0100
                r3 = 1
                r16 = r3
            L_0x0100:
                java.util.HashMap<org.telegram.messenger.MediaController$PhotoEntry, org.telegram.messenger.MessageObject$GroupedMessagePosition> r3 = r11.positions
                r3.put(r6, r7)
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r3 = r11.posArray
                r3.add(r7)
                int r2 = r2 + 1
                r14 = 0
                goto L_0x0032
            L_0x010f:
                java.lang.String r2 = new java.lang.String
                r2.<init>(r10)
                r14 = r2
                r2 = 1123024896(0x42var_, float:120.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r2 = (float) r2
                android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.displaySize
                int r3 = r3.x
                android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
                int r4 = r4.y
                int r3 = java.lang.Math.min(r3, r4)
                float r3 = (float) r3
                r4 = 1148846080(0x447a0000, float:1000.0)
                float r3 = r3 / r4
                float r2 = r2 / r3
                int r8 = (int) r2
                r2 = 1109393408(0x42200000, float:40.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r2 = (float) r2
                android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.displaySize
                int r3 = r3.x
                android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
                int r5 = r5.y
                int r3 = java.lang.Math.min(r3, r5)
                float r3 = (float) r3
                float r3 = r3 / r4
                float r2 = r2 / r3
                int r7 = (int) r2
                r6 = 1067270023(0x3f9d3var_, float:1.2285012)
                float r2 = (float) r13
                float r5 = r1 / r2
                r1 = 1120403456(0x42CLASSNAME, float:100.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                float r1 = (float) r1
                r3 = 1145798656(0x444b8000, float:814.0)
                float r2 = r1 / r3
                r1 = 2
                if (r13 != r0) goto L_0x01bd
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r3 = r11.posArray
                r4 = 0
                java.lang.Object r3 = r3.get(r4)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r3 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r3
                android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
                int r4 = r4.x
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r0 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                org.telegram.ui.Components.ChatAttachAlert r0 = r0.parentAlert
                int r0 = r0.getBackgroundPaddingLeft()
                int r0 = r0 * 2
                int r4 = r4 - r0
                android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
                int r0 = r0.x
                android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
                int r1 = r1.y
                int r0 = java.lang.Math.max(r0, r1)
                float r0 = (float) r0
                r1 = 1056964608(0x3var_, float:0.5)
                float r0 = r0 * r1
                r23 = 0
                r24 = 0
                r25 = 0
                r26 = 0
                r27 = 800(0x320, float:1.121E-42)
                float r1 = (float) r4
                r17 = 1061997773(0x3f4ccccd, float:0.8)
                float r1 = r1 * r17
                r17 = r4
                float r4 = r3.aspectRatio
                float r1 = r1 / r4
                float r28 = r1 / r0
                r29 = 15
                r22 = r3
                r22.set(r23, r24, r25, r26, r27, r28, r29)
                r1 = r2
                r27 = r5
                r21 = r6
                r22 = r7
                r19 = r8
                r32 = r9
                r25 = r10
                r2 = r11
                r29 = r12
                r26 = r13
                r18 = r14
                r28 = r15
                goto L_0x0919
            L_0x01bd:
                r0 = 4
                r1 = 3
                if (r16 != 0) goto L_0x0625
                r3 = 2
                if (r13 == r3) goto L_0x01dc
                if (r13 == r1) goto L_0x01dc
                if (r13 != r0) goto L_0x01c9
                goto L_0x01dc
            L_0x01c9:
                r27 = r5
                r24 = r6
                r25 = r10
                r29 = r12
                r18 = r14
                r28 = r15
                r6 = 1145798656(0x444b8000, float:814.0)
                r19 = 2
                goto L_0x0636
            L_0x01dc:
                r1 = 2
                if (r13 != r1) goto L_0x031d
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r1 = r11.posArray
                r3 = 0
                java.lang.Object r1 = r1.get(r3)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r1 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r1
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r3 = r11.posArray
                r0 = 1
                java.lang.Object r3 = r3.get(r0)
                r0 = r3
                org.telegram.messenger.MessageObject$GroupedMessagePosition r0 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r0
                java.lang.String r3 = "ww"
                boolean r18 = r14.equals(r3)
                if (r18 == 0) goto L_0x025c
                r26 = r9
                r25 = r10
                double r9 = (double) r5
                r18 = 4608983858650965606(0x3ffNUM, double:1.4)
                r27 = r5
                double r4 = (double) r6
                java.lang.Double.isNaN(r4)
                double r4 = r4 * r18
                int r18 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
                if (r18 <= 0) goto L_0x0262
                float r4 = r1.aspectRatio
                float r5 = r0.aspectRatio
                float r4 = r4 - r5
                double r4 = (double) r4
                r9 = 4596373779694328218(0x3fCLASSNAMEa, double:0.2)
                int r18 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
                if (r18 >= 0) goto L_0x0262
                float r3 = r1.aspectRatio
                r4 = 1148846080(0x447a0000, float:1000.0)
                float r3 = r4 / r3
                float r5 = r0.aspectRatio
                float r4 = r4 / r5
                r5 = 1137410048(0x43cb8000, float:407.0)
                float r4 = java.lang.Math.min(r4, r5)
                float r3 = java.lang.Math.min(r3, r4)
                int r3 = java.lang.Math.round(r3)
                float r3 = (float) r3
                r4 = 1145798656(0x444b8000, float:814.0)
                float r3 = r3 / r4
                r33 = 0
                r34 = 0
                r35 = 0
                r36 = 0
                r37 = 1000(0x3e8, float:1.401E-42)
                r39 = 7
                r32 = r1
                r38 = r3
                r32.set(r33, r34, r35, r36, r37, r38, r39)
                r35 = 1
                r36 = 1
                r39 = 11
                r32 = r0
                r32.set(r33, r34, r35, r36, r37, r38, r39)
                goto L_0x0309
            L_0x025c:
                r27 = r5
                r26 = r9
                r25 = r10
            L_0x0262:
                boolean r3 = r14.equals(r3)
                if (r3 != 0) goto L_0x02cf
                java.lang.String r3 = "qq"
                boolean r3 = r14.equals(r3)
                if (r3 == 0) goto L_0x0271
                goto L_0x02cf
            L_0x0271:
                float r3 = r1.aspectRatio
                r4 = 1148846080(0x447a0000, float:1000.0)
                float r4 = r4 / r3
                float r3 = r1.aspectRatio
                r5 = 1065353216(0x3var_, float:1.0)
                float r3 = r5 / r3
                float r9 = r0.aspectRatio
                float r5 = r5 / r9
                float r3 = r3 + r5
                float r4 = r4 / r3
                int r3 = java.lang.Math.round(r4)
                float r3 = (float) r3
                r4 = 1137180672(0x43CLASSNAME, float:400.0)
                float r3 = java.lang.Math.max(r4, r3)
                int r3 = (int) r3
                int r4 = 1000 - r3
                if (r4 >= r8) goto L_0x0295
                int r5 = r8 - r4
                r4 = r8
                int r3 = r3 - r5
            L_0x0295:
                float r5 = (float) r4
                float r9 = r1.aspectRatio
                float r5 = r5 / r9
                float r9 = (float) r3
                float r10 = r0.aspectRatio
                float r9 = r9 / r10
                float r5 = java.lang.Math.min(r5, r9)
                int r5 = java.lang.Math.round(r5)
                float r5 = (float) r5
                r9 = 1145798656(0x444b8000, float:814.0)
                float r5 = java.lang.Math.min(r9, r5)
                float r5 = r5 / r9
                r33 = 0
                r34 = 0
                r35 = 0
                r36 = 0
                r39 = 13
                r32 = r1
                r37 = r4
                r38 = r5
                r32.set(r33, r34, r35, r36, r37, r38, r39)
                r33 = 1
                r34 = 1
                r39 = 14
                r32 = r0
                r37 = r3
                r32.set(r33, r34, r35, r36, r37, r38, r39)
                goto L_0x0309
            L_0x02cf:
                r3 = 500(0x1f4, float:7.0E-43)
                float r4 = (float) r3
                float r5 = r1.aspectRatio
                float r4 = r4 / r5
                float r5 = (float) r3
                float r9 = r0.aspectRatio
                float r5 = r5 / r9
                r9 = 1145798656(0x444b8000, float:814.0)
                float r5 = java.lang.Math.min(r5, r9)
                float r4 = java.lang.Math.min(r4, r5)
                int r4 = java.lang.Math.round(r4)
                float r4 = (float) r4
                float r4 = r4 / r9
                r33 = 0
                r34 = 0
                r35 = 0
                r36 = 0
                r39 = 13
                r32 = r1
                r37 = r3
                r38 = r4
                r32.set(r33, r34, r35, r36, r37, r38, r39)
                r33 = 1
                r34 = 1
                r39 = 14
                r32 = r0
                r32.set(r33, r34, r35, r36, r37, r38, r39)
            L_0x0309:
                r1 = r2
                r21 = r6
                r22 = r7
                r19 = r8
                r2 = r11
                r29 = r12
                r18 = r14
                r28 = r15
                r32 = r26
                r26 = r13
                goto L_0x0919
            L_0x031d:
                r27 = r5
                r26 = r9
                r25 = r10
                r0 = 1141264221(0x44064f5d, float:537.24005)
                r3 = 3
                if (r13 != r3) goto L_0x0472
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r3 = r11.posArray
                r4 = 0
                java.lang.Object r3 = r3.get(r4)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r3 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r3
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r5 = r11.posArray
                r9 = 1
                java.lang.Object r5 = r5.get(r9)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r5 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r5
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r9 = r11.posArray
                r10 = 2
                java.lang.Object r9 = r9.get(r10)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r9 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r9
                char r10 = r14.charAt(r4)
                r4 = 110(0x6e, float:1.54E-43)
                if (r10 != r4) goto L_0x03f2
                float r0 = r5.aspectRatio
                r4 = 1148846080(0x447a0000, float:1000.0)
                float r0 = r0 * r4
                float r4 = r9.aspectRatio
                float r10 = r5.aspectRatio
                float r4 = r4 + r10
                float r0 = r0 / r4
                int r0 = java.lang.Math.round(r0)
                float r0 = (float) r0
                r4 = 1137410048(0x43cb8000, float:407.0)
                float r0 = java.lang.Math.min(r4, r0)
                r4 = 1145798656(0x444b8000, float:814.0)
                float r10 = r4 - r0
                float r4 = (float) r8
                float r1 = r9.aspectRatio
                float r1 = r1 * r0
                r24 = r6
                float r6 = r5.aspectRatio
                float r6 = r6 * r10
                float r1 = java.lang.Math.min(r1, r6)
                int r1 = java.lang.Math.round(r1)
                float r1 = (float) r1
                r6 = 1140457472(0x43fa0000, float:500.0)
                float r1 = java.lang.Math.min(r6, r1)
                float r1 = java.lang.Math.max(r4, r1)
                int r1 = (int) r1
                float r4 = r3.aspectRatio
                r6 = 1145798656(0x444b8000, float:814.0)
                float r4 = r4 * r6
                float r6 = (float) r7
                float r4 = r4 + r6
                int r6 = 1000 - r1
                float r6 = (float) r6
                float r4 = java.lang.Math.min(r4, r6)
                int r4 = java.lang.Math.round(r4)
                r33 = 0
                r34 = 0
                r35 = 0
                r36 = 1
                r38 = 1065353216(0x3var_, float:1.0)
                r39 = 13
                r32 = r3
                r37 = r4
                r32.set(r33, r34, r35, r36, r37, r38, r39)
                r33 = 1
                r34 = 1
                r36 = 0
                r6 = 1145798656(0x444b8000, float:814.0)
                float r38 = r10 / r6
                r39 = 6
                r32 = r5
                r37 = r1
                r32.set(r33, r34, r35, r36, r37, r38, r39)
                r35 = 1
                r36 = 1
                r6 = 1145798656(0x444b8000, float:814.0)
                float r38 = r0 / r6
                r39 = 10
                r32 = r9
                r32.set(r33, r34, r35, r36, r37, r38, r39)
                r6 = 1000(0x3e8, float:1.401E-42)
                r9.spanSize = r6
                r17 = r4
                r6 = 2
                float[] r4 = new float[r6]
                r6 = 1145798656(0x444b8000, float:814.0)
                float r18 = r0 / r6
                r19 = 0
                r4[r19] = r18
                float r6 = r10 / r6
                r18 = 1
                r4[r18] = r6
                r3.siblingHeights = r4
                int r4 = 1000 - r1
                r3.spanSize = r4
                goto L_0x045e
            L_0x03f2:
                r24 = r6
                float r1 = r3.aspectRatio
                r4 = 1148846080(0x447a0000, float:1000.0)
                float r4 = r4 / r1
                float r0 = java.lang.Math.min(r4, r0)
                int r0 = java.lang.Math.round(r0)
                float r0 = (float) r0
                r1 = 1145798656(0x444b8000, float:814.0)
                float r0 = r0 / r1
                r33 = 0
                r34 = 1
                r35 = 0
                r36 = 0
                r37 = 1000(0x3e8, float:1.401E-42)
                r39 = 7
                r32 = r3
                r38 = r0
                r32.set(r33, r34, r35, r36, r37, r38, r39)
                r1 = 500(0x1f4, float:7.0E-43)
                r4 = 1145798656(0x444b8000, float:814.0)
                float r6 = r4 - r0
                float r10 = (float) r1
                float r4 = r5.aspectRatio
                float r10 = r10 / r4
                float r4 = (float) r1
                r17 = r0
                float r0 = r9.aspectRatio
                float r4 = r4 / r0
                float r0 = java.lang.Math.min(r10, r4)
                int r0 = java.lang.Math.round(r0)
                float r0 = (float) r0
                float r0 = java.lang.Math.min(r6, r0)
                r4 = 1145798656(0x444b8000, float:814.0)
                float r0 = r0 / r4
                int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r4 >= 0) goto L_0x0440
                r0 = r2
            L_0x0440:
                r33 = 0
                r34 = 0
                r35 = 1
                r36 = 1
                r39 = 9
                r32 = r5
                r37 = r1
                r38 = r0
                r32.set(r33, r34, r35, r36, r37, r38, r39)
                r33 = 1
                r34 = 1
                r39 = 10
                r32 = r9
                r32.set(r33, r34, r35, r36, r37, r38, r39)
            L_0x045e:
                r1 = r2
                r22 = r7
                r19 = r8
                r2 = r11
                r29 = r12
                r18 = r14
                r28 = r15
                r21 = r24
                r32 = r26
                r26 = r13
                goto L_0x0919
            L_0x0472:
                r24 = r6
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r1 = r11.posArray
                r3 = 0
                java.lang.Object r1 = r1.get(r3)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r1 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r1
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r3 = r11.posArray
                r4 = 1
                java.lang.Object r3 = r3.get(r4)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r3 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r3
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r4 = r11.posArray
                r5 = 2
                java.lang.Object r4 = r4.get(r5)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r4 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r4
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r5 = r11.posArray
                r6 = 3
                java.lang.Object r5 = r5.get(r6)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r5 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r5
                r6 = 0
                char r9 = r14.charAt(r6)
                r6 = 119(0x77, float:1.67E-43)
                if (r9 != r6) goto L_0x055e
                float r6 = r1.aspectRatio
                r9 = 1148846080(0x447a0000, float:1000.0)
                float r6 = r9 / r6
                float r0 = java.lang.Math.min(r6, r0)
                int r0 = java.lang.Math.round(r0)
                float r0 = (float) r0
                r6 = 1145798656(0x444b8000, float:814.0)
                float r0 = r0 / r6
                r33 = 0
                r34 = 2
                r35 = 0
                r36 = 0
                r37 = 1000(0x3e8, float:1.401E-42)
                r39 = 7
                r32 = r1
                r38 = r0
                r32.set(r33, r34, r35, r36, r37, r38, r39)
                float r6 = r3.aspectRatio
                float r9 = r4.aspectRatio
                float r6 = r6 + r9
                float r9 = r5.aspectRatio
                float r6 = r6 + r9
                r9 = 1148846080(0x447a0000, float:1000.0)
                float r6 = r9 / r6
                int r6 = java.lang.Math.round(r6)
                float r6 = (float) r6
                float r9 = (float) r8
                float r10 = r3.aspectRatio
                float r10 = r10 * r6
                r18 = r14
                r14 = 1137180672(0x43CLASSNAME, float:400.0)
                float r10 = java.lang.Math.min(r14, r10)
                float r9 = java.lang.Math.max(r9, r10)
                int r9 = (int) r9
                float r10 = (float) r8
                r14 = 1134886912(0x43a50000, float:330.0)
                float r10 = java.lang.Math.max(r10, r14)
                float r14 = r5.aspectRatio
                float r14 = r14 * r6
                float r10 = java.lang.Math.max(r10, r14)
                int r10 = (int) r10
                int r14 = 1000 - r9
                int r14 = r14 - r10
                r17 = 1114112000(0x42680000, float:58.0)
                r28 = r15
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r17)
                if (r14 >= r15) goto L_0x051a
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r17)
                int r15 = r15 - r14
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r17)
                int r17 = r15 / 2
                int r9 = r9 - r17
                int r17 = r15 / 2
                int r17 = r15 - r17
                int r10 = r10 - r17
            L_0x051a:
                r29 = r12
                r15 = 1145798656(0x444b8000, float:814.0)
                float r12 = r15 - r0
                float r6 = java.lang.Math.min(r12, r6)
                float r6 = r6 / r15
                int r12 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
                if (r12 >= 0) goto L_0x052b
                r6 = r2
            L_0x052b:
                r33 = 0
                r34 = 0
                r35 = 1
                r36 = 1
                r39 = 9
                r32 = r3
                r37 = r9
                r38 = r6
                r32.set(r33, r34, r35, r36, r37, r38, r39)
                r33 = 1
                r34 = 1
                r39 = 8
                r32 = r4
                r37 = r14
                r32.set(r33, r34, r35, r36, r37, r38, r39)
                r33 = 2
                r34 = 2
                r39 = 10
                r32 = r5
                r37 = r10
                r32.set(r33, r34, r35, r36, r37, r38, r39)
                r9 = r26
                r19 = 2
                goto L_0x0617
            L_0x055e:
                r29 = r12
                r18 = r14
                r28 = r15
                float r0 = r3.aspectRatio
                r6 = 1065353216(0x3var_, float:1.0)
                float r0 = r6 / r0
                float r9 = r4.aspectRatio
                float r9 = r6 / r9
                float r0 = r0 + r9
                float r9 = r5.aspectRatio
                float r9 = r6 / r9
                float r0 = r0 + r9
                r6 = 1145798656(0x444b8000, float:814.0)
                float r0 = r6 / r0
                int r0 = java.lang.Math.round(r0)
                int r0 = java.lang.Math.max(r8, r0)
                r9 = r26
                float r10 = (float) r9
                float r12 = (float) r0
                float r14 = r3.aspectRatio
                float r12 = r12 / r14
                float r10 = java.lang.Math.max(r10, r12)
                float r10 = r10 / r6
                r12 = 1051260355(0x3ea8f5c3, float:0.33)
                float r10 = java.lang.Math.min(r12, r10)
                float r14 = (float) r9
                float r15 = (float) r0
                float r12 = r4.aspectRatio
                float r15 = r15 / r12
                float r12 = java.lang.Math.max(r14, r15)
                float r12 = r12 / r6
                r14 = 1051260355(0x3ea8f5c3, float:0.33)
                float r12 = java.lang.Math.min(r14, r12)
                r14 = 1065353216(0x3var_, float:1.0)
                float r14 = r14 - r10
                float r14 = r14 - r12
                float r15 = r1.aspectRatio
                float r15 = r15 * r6
                float r6 = (float) r7
                float r15 = r15 + r6
                int r6 = 1000 - r0
                float r6 = (float) r6
                float r6 = java.lang.Math.min(r15, r6)
                int r6 = java.lang.Math.round(r6)
                r33 = 0
                r34 = 0
                r35 = 0
                r36 = 2
                float r15 = r10 + r12
                float r38 = r15 + r14
                r39 = 13
                r32 = r1
                r37 = r6
                r32.set(r33, r34, r35, r36, r37, r38, r39)
                r33 = 1
                r34 = 1
                r36 = 0
                r39 = 6
                r32 = r3
                r37 = r0
                r38 = r10
                r32.set(r33, r34, r35, r36, r37, r38, r39)
                r35 = 1
                r36 = 1
                r39 = 2
                r32 = r4
                r38 = r12
                r32.set(r33, r34, r35, r36, r37, r38, r39)
                r15 = 1000(0x3e8, float:1.401E-42)
                r4.spanSize = r15
                r35 = 2
                r36 = 2
                r39 = 10
                r32 = r5
                r38 = r14
                r32.set(r33, r34, r35, r36, r37, r38, r39)
                r15 = 1000(0x3e8, float:1.401E-42)
                r5.spanSize = r15
                int r15 = 1000 - r0
                r1.spanSize = r15
                r15 = 3
                float[] r15 = new float[r15]
                r17 = 0
                r15[r17] = r10
                r17 = 1
                r15[r17] = r12
                r19 = 2
                r15[r19] = r14
                r1.siblingHeights = r15
            L_0x0617:
                r1 = r2
                r22 = r7
                r19 = r8
                r32 = r9
                r2 = r11
                r26 = r13
                r21 = r24
                goto L_0x0919
            L_0x0625:
                r27 = r5
                r24 = r6
                r25 = r10
                r29 = r12
                r18 = r14
                r28 = r15
                r6 = 1145798656(0x444b8000, float:814.0)
                r19 = 2
            L_0x0636:
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r1 = r11.posArray
                int r1 = r1.size()
                float[] r12 = new float[r1]
                r1 = 0
            L_0x063f:
                if (r1 >= r13) goto L_0x0682
                r3 = 1066192077(0x3f8ccccd, float:1.1)
                int r3 = (r27 > r3 ? 1 : (r27 == r3 ? 0 : -1))
                if (r3 <= 0) goto L_0x065b
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r3 = r11.posArray
                java.lang.Object r3 = r3.get(r1)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r3 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r3
                float r3 = r3.aspectRatio
                r4 = 1065353216(0x3var_, float:1.0)
                float r3 = java.lang.Math.max(r4, r3)
                r12[r1] = r3
                goto L_0x066d
            L_0x065b:
                r4 = 1065353216(0x3var_, float:1.0)
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r3 = r11.posArray
                java.lang.Object r3 = r3.get(r1)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r3 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r3
                float r3 = r3.aspectRatio
                float r3 = java.lang.Math.min(r4, r3)
                r12[r1] = r3
            L_0x066d:
                r3 = 1059760867(0x3f2aaae3, float:0.66667)
                r5 = 1071225242(0x3fd9999a, float:1.7)
                r10 = r12[r1]
                float r5 = java.lang.Math.min(r5, r10)
                float r3 = java.lang.Math.max(r3, r5)
                r12[r1] = r3
                int r1 = r1 + 1
                goto L_0x063f
            L_0x0682:
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
                r14 = r1
                r1 = 1
                r10 = r1
            L_0x068a:
                int r1 = r12.length
                if (r10 >= r1) goto L_0x06e6
                int r1 = r12.length
                int r15 = r1 - r10
                r1 = 3
                if (r10 > r1) goto L_0x06cd
                if (r15 <= r1) goto L_0x06a1
                r0 = r2
                r26 = r13
                r21 = r24
                r23 = 1145798656(0x444b8000, float:814.0)
                r24 = r15
                r15 = 2
                goto L_0x06d8
            L_0x06a1:
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator$MessageGroupedLayoutAttempt r5 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator$MessageGroupedLayoutAttempt
                r3 = 0
                float r21 = r11.multiHeight(r12, r3, r10)
                int r3 = r12.length
                float r22 = r11.multiHeight(r12, r10, r3)
                r3 = 2
                r4 = 3
                r1 = r5
                r0 = r2
                r2 = r43
                r6 = 2
                r23 = 1145798656(0x444b8000, float:814.0)
                r3 = r10
                r26 = r13
                r13 = 3
                r4 = r15
                r13 = r5
                r5 = r21
                r21 = r24
                r24 = r15
                r15 = 2
                r6 = r22
                r1.<init>(r3, r4, r5, r6)
                r14.add(r13)
                goto L_0x06d8
            L_0x06cd:
                r0 = r2
                r26 = r13
                r21 = r24
                r23 = 1145798656(0x444b8000, float:814.0)
                r24 = r15
                r15 = 2
            L_0x06d8:
                int r10 = r10 + 1
                r2 = r0
                r24 = r21
                r13 = r26
                r0 = 4
                r6 = 1145798656(0x444b8000, float:814.0)
                r19 = 2
                goto L_0x068a
            L_0x06e6:
                r0 = r2
                r26 = r13
                r21 = r24
                r15 = 2
                r23 = 1145798656(0x444b8000, float:814.0)
                r1 = 1
                r10 = r1
            L_0x06f1:
                int r1 = r12.length
                r2 = 1
                int r1 = r1 - r2
                if (r10 >= r1) goto L_0x0764
                r1 = 1
                r13 = r1
            L_0x06f8:
                int r1 = r12.length
                int r1 = r1 - r10
                if (r13 >= r1) goto L_0x0759
                int r1 = r12.length
                int r1 = r1 - r10
                int r6 = r1 - r13
                r1 = 3
                if (r10 > r1) goto L_0x0749
                r1 = 1062836634(0x3var_a, float:0.85)
                int r1 = (r27 > r1 ? 1 : (r27 == r1 ? 0 : -1))
                if (r1 >= 0) goto L_0x070c
                r1 = 4
                goto L_0x070d
            L_0x070c:
                r1 = 3
            L_0x070d:
                if (r13 > r1) goto L_0x0749
                r1 = 3
                if (r6 <= r1) goto L_0x071a
                r24 = r0
                r34 = r6
                r22 = r7
                r0 = r8
                goto L_0x0750
            L_0x071a:
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator$MessageGroupedLayoutAttempt r5 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator$MessageGroupedLayoutAttempt
                r1 = 0
                float r22 = r11.multiHeight(r12, r1, r10)
                int r1 = r10 + r13
                float r24 = r11.multiHeight(r12, r10, r1)
                int r1 = r10 + r13
                int r2 = r12.length
                float r32 = r11.multiHeight(r12, r1, r2)
                r1 = r5
                r2 = r43
                r3 = r10
                r4 = r13
                r15 = r5
                r5 = r6
                r34 = r6
                r6 = r22
                r22 = r7
                r7 = r24
                r24 = r0
                r0 = r8
                r8 = r32
                r1.<init>(r3, r4, r5, r6, r7, r8)
                r14.add(r15)
                goto L_0x0750
            L_0x0749:
                r24 = r0
                r34 = r6
                r22 = r7
                r0 = r8
            L_0x0750:
                int r13 = r13 + 1
                r8 = r0
                r7 = r22
                r0 = r24
                r15 = 2
                goto L_0x06f8
            L_0x0759:
                r24 = r0
                r22 = r7
                r0 = r8
                int r10 = r10 + 1
                r0 = r24
                r15 = 2
                goto L_0x06f1
            L_0x0764:
                r24 = r0
                r22 = r7
                r0 = r8
                r1 = 1
                r13 = r1
            L_0x076b:
                int r1 = r12.length
                r2 = 2
                int r1 = r1 - r2
                if (r13 >= r1) goto L_0x07ea
                r1 = 1
                r15 = r1
            L_0x0772:
                int r1 = r12.length
                int r1 = r1 - r13
                if (r15 >= r1) goto L_0x07e3
                r1 = 1
                r10 = r1
            L_0x0778:
                int r1 = r12.length
                int r1 = r1 - r13
                int r1 = r1 - r15
                if (r10 >= r1) goto L_0x07da
                int r1 = r12.length
                int r1 = r1 - r13
                int r1 = r1 - r15
                int r8 = r1 - r10
                r1 = 3
                if (r13 > r1) goto L_0x07cd
                if (r15 > r1) goto L_0x07cd
                if (r10 > r1) goto L_0x07cd
                if (r8 <= r1) goto L_0x0792
                r19 = r8
                r32 = r9
                r34 = r10
                goto L_0x07d3
            L_0x0792:
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator$MessageGroupedLayoutAttempt r7 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator$MessageGroupedLayoutAttempt
                r1 = 0
                float r19 = r11.multiHeight(r12, r1, r13)
                int r1 = r13 + r15
                float r32 = r11.multiHeight(r12, r13, r1)
                int r1 = r13 + r15
                int r2 = r13 + r15
                int r2 = r2 + r10
                float r34 = r11.multiHeight(r12, r1, r2)
                int r1 = r13 + r15
                int r1 = r1 + r10
                int r2 = r12.length
                float r35 = r11.multiHeight(r12, r1, r2)
                r1 = r7
                r2 = r43
                r3 = r13
                r4 = r15
                r5 = r10
                r6 = r8
                r11 = r7
                r7 = r19
                r19 = r8
                r8 = r32
                r32 = r9
                r9 = r34
                r34 = r10
                r10 = r35
                r1.<init>(r3, r4, r5, r6, r7, r8, r9, r10)
                r14.add(r11)
                goto L_0x07d3
            L_0x07cd:
                r19 = r8
                r32 = r9
                r34 = r10
            L_0x07d3:
                int r10 = r34 + 1
                r11 = r43
                r9 = r32
                goto L_0x0778
            L_0x07da:
                r32 = r9
                r34 = r10
                int r15 = r15 + 1
                r11 = r43
                goto L_0x0772
            L_0x07e3:
                r32 = r9
                int r13 = r13 + 1
                r11 = r43
                goto L_0x076b
            L_0x07ea:
                r32 = r9
                r1 = 0
                r2 = 0
                r3 = 1151762432(0x44a68000, float:1332.0)
                r4 = 0
            L_0x07f2:
                int r5 = r14.size()
                if (r4 >= r5) goto L_0x0872
                java.lang.Object r5 = r14.get(r4)
                org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$GroupCalculator$MessageGroupedLayoutAttempt r5 = (org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.GroupCalculator.MessageGroupedLayoutAttempt) r5
                r6 = 0
                r7 = 2139095039(0x7f7fffff, float:3.4028235E38)
                r8 = 0
            L_0x0803:
                float[] r9 = r5.heights
                int r9 = r9.length
                if (r8 >= r9) goto L_0x081c
                float[] r9 = r5.heights
                r9 = r9[r8]
                float r6 = r6 + r9
                float[] r9 = r5.heights
                r9 = r9[r8]
                int r9 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
                if (r9 >= 0) goto L_0x0819
                float[] r9 = r5.heights
                r7 = r9[r8]
            L_0x0819:
                int r8 = r8 + 1
                goto L_0x0803
            L_0x081c:
                float r8 = r6 - r3
                float r8 = java.lang.Math.abs(r8)
                int[] r9 = r5.lineCounts
                int r9 = r9.length
                r10 = 1
                if (r9 <= r10) goto L_0x085c
                int[] r9 = r5.lineCounts
                r11 = 0
                r9 = r9[r11]
                int[] r15 = r5.lineCounts
                r15 = r15[r10]
                if (r9 > r15) goto L_0x0858
                int[] r9 = r5.lineCounts
                int r9 = r9.length
                r15 = 2
                if (r9 <= r15) goto L_0x0846
                int[] r9 = r5.lineCounts
                r9 = r9[r10]
                int[] r10 = r5.lineCounts
                r10 = r10[r15]
                if (r9 > r10) goto L_0x0844
                goto L_0x0846
            L_0x0844:
                r10 = 3
                goto L_0x0859
            L_0x0846:
                int[] r9 = r5.lineCounts
                int r9 = r9.length
                r10 = 3
                if (r9 <= r10) goto L_0x085e
                int[] r9 = r5.lineCounts
                r15 = 2
                r9 = r9[r15]
                int[] r15 = r5.lineCounts
                r15 = r15[r10]
                if (r9 <= r15) goto L_0x085e
                goto L_0x0859
            L_0x0858:
                r10 = 3
            L_0x0859:
                float r8 = r8 * r17
                goto L_0x085e
            L_0x085c:
                r10 = 3
                r11 = 0
            L_0x085e:
                float r9 = (float) r0
                int r9 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
                if (r9 >= 0) goto L_0x0867
                r9 = 1069547520(0x3fCLASSNAME, float:1.5)
                float r8 = r8 * r9
            L_0x0867:
                if (r1 == 0) goto L_0x086d
                int r9 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1))
                if (r9 >= 0) goto L_0x086f
            L_0x086d:
                r1 = r5
                r2 = r8
            L_0x086f:
                int r4 = r4 + 1
                goto L_0x07f2
            L_0x0872:
                if (r1 != 0) goto L_0x0875
                return
            L_0x0875:
                r4 = 0
                r5 = 0
            L_0x0877:
                int[] r6 = r1.lineCounts
                int r6 = r6.length
                if (r5 >= r6) goto L_0x090d
                int[] r6 = r1.lineCounts
                r6 = r6[r5]
                float[] r7 = r1.heights
                r7 = r7[r5]
                r8 = 1000(0x3e8, float:1.401E-42)
                r9 = 0
                r10 = 0
            L_0x0888:
                if (r10 >= r6) goto L_0x08e7
                r11 = r12[r4]
                float r15 = r11 * r7
                int r15 = (int) r15
                int r8 = r8 - r15
                r19 = r0
                r17 = r2
                r2 = r43
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r0 = r2.posArray
                java.lang.Object r0 = r0.get(r4)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r0 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r0
                r20 = 0
                if (r5 != 0) goto L_0x08a4
                r20 = r20 | 4
            L_0x08a4:
                r31 = r3
                int[] r3 = r1.lineCounts
                int r3 = r3.length
                r30 = 1
                int r3 = r3 + -1
                if (r5 != r3) goto L_0x08b1
                r20 = r20 | 8
            L_0x08b1:
                if (r10 != 0) goto L_0x08b6
                r20 = r20 | 1
                r9 = r0
            L_0x08b6:
                int r3 = r6 + -1
                if (r10 != r3) goto L_0x08be
                r20 = r20 | 2
                r3 = r0
                r9 = r3
            L_0x08be:
                float r3 = r7 / r23
                r42 = r1
                r1 = r24
                float r40 = java.lang.Math.max(r1, r3)
                r34 = r0
                r35 = r10
                r36 = r10
                r37 = r5
                r38 = r5
                r39 = r15
                r41 = r20
                r34.set(r35, r36, r37, r38, r39, r40, r41)
                int r4 = r4 + 1
                int r10 = r10 + 1
                r2 = r17
                r0 = r19
                r3 = r31
                r1 = r42
                goto L_0x0888
            L_0x08e7:
                r19 = r0
                r42 = r1
                r17 = r2
                r31 = r3
                r1 = r24
                r2 = r43
                if (r9 == 0) goto L_0x08ff
                int r0 = r9.pw
                int r0 = r0 + r8
                r9.pw = r0
                int r0 = r9.spanSize
                int r0 = r0 + r8
                r9.spanSize = r0
            L_0x08ff:
                int r5 = r5 + 1
                r24 = r1
                r2 = r17
                r0 = r19
                r3 = r31
                r1 = r42
                goto L_0x0877
            L_0x090d:
                r19 = r0
                r42 = r1
                r17 = r2
                r31 = r3
                r1 = r24
                r2 = r43
            L_0x0919:
                r0 = 0
            L_0x091a:
                r3 = r26
                if (r0 >= r3) goto L_0x0960
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r4 = r2.posArray
                java.lang.Object r4 = r4.get(r0)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r4 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r4
                byte r5 = r4.minX
                if (r5 != 0) goto L_0x0930
                int r5 = r4.spanSize
                int r5 = r5 + r29
                r4.spanSize = r5
            L_0x0930:
                int r5 = r4.flags
                r6 = 2
                r5 = r5 & r6
                if (r5 == 0) goto L_0x093a
                r5 = 1
                r4.edge = r5
                goto L_0x093b
            L_0x093a:
                r5 = 1
            L_0x093b:
                int r7 = r2.maxX
                byte r8 = r4.maxX
                int r7 = java.lang.Math.max(r7, r8)
                r2.maxX = r7
                int r7 = r2.maxY
                byte r8 = r4.maxY
                int r7 = java.lang.Math.max(r7, r8)
                r2.maxY = r7
                byte r7 = r4.minY
                byte r8 = r4.maxY
                byte r9 = r4.minX
                float r7 = r2.getLeft(r4, r7, r8, r9)
                r4.left = r7
                int r0 = r0 + 1
                r26 = r3
                goto L_0x091a
            L_0x0960:
                r0 = 0
            L_0x0961:
                if (r0 >= r3) goto L_0x0976
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r4 = r2.posArray
                java.lang.Object r4 = r4.get(r0)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r4 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r4
                byte r5 = r4.minY
                float r5 = r2.getTop(r4, r5)
                r4.top = r5
                int r0 = r0 + 1
                goto L_0x0961
            L_0x0976:
                int r0 = r43.getWidth()
                r2.width = r0
                float r0 = r43.getHeight()
                r2.height = r0
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.GroupCalculator.calculate():void");
        }

        public int getWidth() {
            int[] lineWidths = new int[10];
            Arrays.fill(lineWidths, 0);
            int count = this.posArray.size();
            for (int i = 0; i < count; i++) {
                MessageObject.GroupedMessagePosition pos = this.posArray.get(i);
                int width2 = pos.pw;
                for (int y = pos.minY; y <= pos.maxY; y++) {
                    lineWidths[y] = lineWidths[y] + width2;
                }
            }
            int width3 = lineWidths[0];
            for (int y2 = 1; y2 < lineWidths.length; y2++) {
                if (width3 < lineWidths[y2]) {
                    width3 = lineWidths[y2];
                }
            }
            return width3;
        }

        public float getHeight() {
            float[] lineHeights = new float[10];
            Arrays.fill(lineHeights, 0.0f);
            int count = this.posArray.size();
            for (int i = 0; i < count; i++) {
                MessageObject.GroupedMessagePosition pos = this.posArray.get(i);
                float height2 = pos.ph;
                for (int x = pos.minX; x <= pos.maxX; x++) {
                    lineHeights[x] = lineHeights[x] + height2;
                }
            }
            float height3 = lineHeights[0];
            for (int y = 1; y < lineHeights.length; y++) {
                if (height3 < lineHeights[y]) {
                    height3 = lineHeights[y];
                }
            }
            return height3;
        }

        private float getLeft(MessageObject.GroupedMessagePosition except, int minY, int maxY2, int minX) {
            float[] sums = new float[((maxY2 - minY) + 1)];
            Arrays.fill(sums, 0.0f);
            int count = this.posArray.size();
            for (int i = 0; i < count; i++) {
                MessageObject.GroupedMessagePosition pos = this.posArray.get(i);
                if (pos != except && pos.maxX < minX) {
                    int end = Math.min(pos.maxY, maxY2) - minY;
                    for (int y = Math.max(pos.minY - minY, 0); y <= end; y++) {
                        sums[y] = sums[y] + ((float) pos.pw);
                    }
                }
            }
            float max = 0.0f;
            for (int i2 = 0; i2 < sums.length; i2++) {
                if (max < sums[i2]) {
                    max = sums[i2];
                }
            }
            return max;
        }

        private float getTop(MessageObject.GroupedMessagePosition except, int minY) {
            float[] sums = new float[(this.maxX + 1)];
            Arrays.fill(sums, 0.0f);
            int count = this.posArray.size();
            for (int i = 0; i < count; i++) {
                MessageObject.GroupedMessagePosition pos = this.posArray.get(i);
                if (pos != except && pos.maxY < minY) {
                    for (int x = pos.minX; x <= pos.maxX; x++) {
                        sums[x] = sums[x] + pos.ph;
                    }
                }
            }
            float max = 0.0f;
            for (int i2 = 0; i2 < sums.length; i2++) {
                if (max < sums[i2]) {
                    max = sums[i2];
                }
            }
            return max;
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
        View child = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        int top = child.getTop();
        int newOffset = AndroidUtilities.dp(8.0f);
        if (top >= AndroidUtilities.dp(8.0f) && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
        }
        this.listView.setTopGlowOffset(newOffset);
        return newOffset;
    }

    public void onPreMeasure(int availableWidth, int availableHeight) {
        this.ignoreLayout = true;
        ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = ActionBar.getCurrentActionBarHeight();
        if (AndroidUtilities.isTablet() || AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y) {
            this.paddingTop = (availableHeight / 5) * 2;
        } else {
            this.paddingTop = (int) (((float) availableHeight) / 3.5f);
        }
        int dp = this.paddingTop - AndroidUtilities.dp(52.0f);
        this.paddingTop = dp;
        if (dp < 0) {
            this.paddingTop = 0;
        }
        if (this.listView.getPaddingTop() != this.paddingTop) {
            RecyclerListView recyclerListView = this.listView;
            recyclerListView.setPadding(recyclerListView.getPaddingLeft(), this.paddingTop, this.listView.getPaddingRight(), this.listView.getPaddingBottom());
            invalidate();
        }
        this.header.setTextSize((AndroidUtilities.isTablet() || AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y) ? 20.0f : 18.0f);
        this.ignoreLayout = false;
    }

    /* access modifiers changed from: package-private */
    public void scrollToTop() {
        this.listView.smoothScrollToPosition(0);
    }

    /* access modifiers changed from: package-private */
    public int needsActionBar() {
        return 1;
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
    public void onMenuItemClick(int id) {
        try {
            this.parentAlert.getPhotoLayout().onMenuItemClick(id);
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        Drawable chatBackgroundDrawable;
        int finalMove;
        boolean restore = false;
        if (!(this.parentAlert.parentThemeDelegate == null || (chatBackgroundDrawable = this.parentAlert.parentThemeDelegate.getWallpaperDrawable()) == null)) {
            int paddingTop2 = getCurrentItemTop();
            if (AndroidUtilities.isTablet()) {
                finalMove = 16;
            } else if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                finalMove = 6;
            } else {
                finalMove = 12;
            }
            if (paddingTop2 < ActionBar.getCurrentActionBarHeight()) {
                paddingTop2 -= AndroidUtilities.dp((1.0f - (((float) paddingTop2) / ((float) ActionBar.getCurrentActionBarHeight()))) * ((float) finalMove));
            }
            int paddingTop3 = Math.max(0, paddingTop2);
            canvas.save();
            canvas.clipRect(0, paddingTop3, getWidth(), getHeight());
            chatBackgroundDrawable.setBounds(0, paddingTop3, getWidth(), AndroidUtilities.displaySize.y + paddingTop3);
            chatBackgroundDrawable.draw(canvas);
            restore = true;
        }
        super.dispatchDraw(canvas);
        if (restore) {
            canvas.restore();
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        boolean isPortrait2 = AndroidUtilities.displaySize.y > AndroidUtilities.displaySize.x;
        if (this.isPortrait != isPortrait2) {
            this.isPortrait = isPortrait2;
            int groupCellsCount = this.groupsView.groupCells.size();
            for (int i = 0; i < groupCellsCount; i++) {
                PreviewGroupsView.PreviewGroupCell groupCell = (PreviewGroupsView.PreviewGroupCell) this.groupsView.groupCells.get(i);
                if (groupCell.group.photos.size() == 1) {
                    groupCell.setGroup(groupCell.group, true);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onSelectedItemsCountChanged(int count) {
        if (count > 1) {
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
        private HashMap<MediaController.PhotoEntry, ImageReceiver> images = new HashMap<>();
        boolean[] lastGroupSeen = null;
        private int lastMeasuredHeight = 0;
        /* access modifiers changed from: private */
        public int paddingBottom = AndroidUtilities.dp(64.0f);
        /* access modifiers changed from: private */
        public int paddingTop = AndroidUtilities.dp(16.0f);
        GroupingPhotoViewerProvider photoViewerProvider = new GroupingPhotoViewerProvider();
        HashMap<Object, Object> photosMap;
        List<Map.Entry<Object, Object>> photosMapKeys;
        ArrayList<Object> photosOrder;
        private float savedDragFromX;
        private float savedDragFromY;
        private float savedDraggingT;
        private final Runnable scroller = new Runnable() {
            public void run() {
                if (ChatAttachAlertPhotoLayoutPreview.this.draggingCell != null && !ChatAttachAlertPhotoLayoutPreview.this.draggingCellHiding) {
                    int scrollY = ChatAttachAlertPhotoLayoutPreview.this.listView.computeVerticalScrollOffset();
                    boolean atBottom = ChatAttachAlertPhotoLayoutPreview.this.listView.computeVerticalScrollExtent() + scrollY >= (PreviewGroupsView.this.measurePureHeight() - PreviewGroupsView.this.paddingBottom) + PreviewGroupsView.this.paddingTop;
                    float top = Math.max(0.0f, (ChatAttachAlertPhotoLayoutPreview.this.draggingCellTouchY - ((float) Math.max(0, scrollY - ChatAttachAlertPhotoLayoutPreview.this.getListTopPadding()))) - ((float) AndroidUtilities.dp(52.0f)));
                    float bottom = Math.max(0.0f, ((((float) ChatAttachAlertPhotoLayoutPreview.this.listView.getMeasuredHeight()) - (ChatAttachAlertPhotoLayoutPreview.this.draggingCellTouchY - ((float) scrollY))) - ((float) ChatAttachAlertPhotoLayoutPreview.this.getListTopPadding())) - ((float) AndroidUtilities.dp(84.0f)));
                    float r = (float) AndroidUtilities.dp(32.0f);
                    float dy = 0.0f;
                    if (top < r && scrollY > ChatAttachAlertPhotoLayoutPreview.this.getListTopPadding()) {
                        dy = (-(1.0f - (top / r))) * ((float) AndroidUtilities.dp(6.0f));
                    } else if (bottom < r) {
                        dy = (1.0f - (bottom / r)) * ((float) AndroidUtilities.dp(6.0f));
                    }
                    if (Math.abs((int) dy) > 0 && ChatAttachAlertPhotoLayoutPreview.this.listView.canScrollVertically((int) dy) && (dy <= 0.0f || !atBottom)) {
                        ChatAttachAlertPhotoLayoutPreview.access$1416(ChatAttachAlertPhotoLayoutPreview.this, dy);
                        ChatAttachAlertPhotoLayoutPreview.this.listView.scrollBy(0, (int) dy);
                        PreviewGroupsView.this.invalidate();
                    }
                    boolean unused = PreviewGroupsView.this.scrollerStarted = true;
                    PreviewGroupsView.this.postDelayed(this, 15);
                }
            }
        };
        /* access modifiers changed from: private */
        public boolean scrollerStarted = false;
        HashMap<Object, Object> selectedPhotos;
        PreviewGroupCell tapGroupCell = null;
        PreviewGroupCell.MediaCell tapMediaCell = null;
        long tapTime = 0;
        private final Point tmpPoint = new Point();
        private int undoViewId = 0;
        float viewBottom;
        float viewTop;

        public PreviewGroupsView(Context context) {
            super(context);
            setWillNotDraw(false);
            ChatActionCell chatActionCell = new ChatActionCell(context, true, ChatAttachAlertPhotoLayoutPreview.this.themeDelegate);
            this.hintView = chatActionCell;
            chatActionCell.setCustomText(LocaleController.getString("AttachMediaDragHint", NUM));
            addView(this.hintView);
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean b, int i, int i1, int i2, int i3) {
            ChatActionCell chatActionCell = this.hintView;
            chatActionCell.layout(0, 0, chatActionCell.getMeasuredWidth(), this.hintView.getMeasuredHeight());
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View child, long drawingTime) {
            return false;
        }

        public void saveDeletedImageId(MediaController.PhotoEntry photo) {
            if (ChatAttachAlertPhotoLayoutPreview.this.photoLayout != null) {
                List<Map.Entry<Object, Object>> entries = new ArrayList<>(ChatAttachAlertPhotoLayoutPreview.this.photoLayout.getSelectedPhotos().entrySet());
                int entriesCount = entries.size();
                for (int i = 0; i < entriesCount; i++) {
                    if (entries.get(i).getValue() == photo) {
                        this.deletedPhotos.put(photo, entries.get(i).getKey());
                        return;
                    }
                }
            }
        }

        public void fromPhotoLayout(ChatAttachAlertPhotoLayout photoLayout) {
            this.photosOrder = photoLayout.getSelectedPhotosOrder();
            this.photosMap = photoLayout.getSelectedPhotos();
            fromPhotoArrays();
        }

        public void fromPhotoArrays() {
            this.groupCells.clear();
            ArrayList<MediaController.PhotoEntry> photos = new ArrayList<>();
            int photosOrderSize = this.photosOrder.size();
            int photosOrderLast = photosOrderSize - 1;
            for (int i = 0; i < photosOrderSize; i++) {
                photos.add((MediaController.PhotoEntry) this.photosMap.get(Integer.valueOf(((Integer) this.photosOrder.get(i)).intValue())));
                if (i % 10 == 9 || i == photosOrderLast) {
                    PreviewGroupCell groupCell = new PreviewGroupCell();
                    groupCell.setGroup(new GroupCalculator(photos), false);
                    this.groupCells.add(groupCell);
                    photos = new ArrayList<>();
                }
            }
        }

        public void calcPhotoArrays() {
            this.photosMap = ChatAttachAlertPhotoLayoutPreview.this.photoLayout.getSelectedPhotos();
            this.photosMapKeys = new ArrayList(this.photosMap.entrySet());
            this.selectedPhotos = new HashMap<>();
            this.photosOrder = new ArrayList<>();
            int groupCellsCount = this.groupCells.size();
            for (int i = 0; i < groupCellsCount; i++) {
                GroupCalculator group = this.groupCells.get(i).group;
                if (group.photos.size() != 0) {
                    int photosCount = group.photos.size();
                    for (int j = 0; j < photosCount; j++) {
                        MediaController.PhotoEntry photoEntry = group.photos.get(j);
                        if (this.deletedPhotos.containsKey(photoEntry)) {
                            Object imageId = this.deletedPhotos.get(photoEntry);
                            this.selectedPhotos.put(imageId, photoEntry);
                            this.photosOrder.add(imageId);
                        } else {
                            boolean found = false;
                            int k = 0;
                            while (true) {
                                if (k >= this.photosMapKeys.size()) {
                                    break;
                                }
                                Map.Entry<Object, Object> entry = this.photosMapKeys.get(k);
                                Object value = entry.getValue();
                                if (value == photoEntry) {
                                    Object key = entry.getKey();
                                    this.selectedPhotos.put(key, value);
                                    this.photosOrder.add(key);
                                    found = true;
                                    break;
                                }
                                k++;
                            }
                            if (!found) {
                                int k2 = 0;
                                while (true) {
                                    if (k2 >= this.photosMapKeys.size()) {
                                        break;
                                    }
                                    Map.Entry<Object, Object> entry2 = this.photosMapKeys.get(k2);
                                    Object value2 = entry2.getValue();
                                    if ((value2 instanceof MediaController.PhotoEntry) && ((MediaController.PhotoEntry) value2).path != null && photoEntry != null && ((MediaController.PhotoEntry) value2).path.equals(photoEntry.path)) {
                                        Object key2 = entry2.getKey();
                                        this.selectedPhotos.put(key2, value2);
                                        this.photosOrder.add(key2);
                                        break;
                                    }
                                    k2++;
                                }
                            }
                        }
                    }
                }
            }
        }

        public void toPhotoLayout(ChatAttachAlertPhotoLayout photoLayout, boolean updateLayout) {
            int previousCount = photoLayout.getSelectedPhotosOrder().size();
            calcPhotoArrays();
            photoLayout.updateSelected(this.selectedPhotos, this.photosOrder, updateLayout);
            if (previousCount != this.photosOrder.size()) {
                ChatAttachAlertPhotoLayoutPreview.this.parentAlert.updateCountButton(1);
            }
        }

        public int getPhotosCount() {
            int count = 0;
            int groupCellsCount = this.groupCells.size();
            for (int i = 0; i < groupCellsCount; i++) {
                PreviewGroupCell groupCell = this.groupCells.get(i);
                if (!(groupCell == null || groupCell.group == null || groupCell.group.photos == null)) {
                    count += groupCell.group.photos.size();
                }
            }
            return count;
        }

        public ArrayList<MediaController.PhotoEntry> getPhotos() {
            ArrayList<MediaController.PhotoEntry> photos = new ArrayList<>();
            int groupCellsCount = this.groupCells.size();
            for (int i = 0; i < groupCellsCount; i++) {
                PreviewGroupCell groupCell = this.groupCells.get(i);
                if (!(groupCell == null || groupCell.group == null || groupCell.group.photos == null)) {
                    photos.addAll(groupCell.group.photos);
                }
            }
            return photos;
        }

        /* access modifiers changed from: private */
        public int measurePureHeight() {
            int height = this.paddingTop + this.paddingBottom;
            int groupCellsCount = this.groupCells.size();
            for (int i = 0; i < groupCellsCount; i++) {
                height = (int) (((float) height) + this.groupCells.get(i).measure());
            }
            if (this.hintView.getMeasuredHeight() <= 0) {
                this.hintView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, NUM), View.MeasureSpec.makeMeasureSpec(9999, Integer.MIN_VALUE));
            }
            return height + this.hintView.getMeasuredHeight();
        }

        private int measureHeight() {
            return Math.max(measurePureHeight(), (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(45.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            this.hintView.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(9999, Integer.MIN_VALUE));
            if (this.lastMeasuredHeight <= 0) {
                this.lastMeasuredHeight = measureHeight();
            }
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(Math.max(View.MeasureSpec.getSize(heightMeasureSpec), this.lastMeasuredHeight), NUM));
        }

        public void invalidate() {
            int measuredHeight = measureHeight();
            if (this.lastMeasuredHeight != measuredHeight) {
                this.lastMeasuredHeight = measuredHeight;
                requestLayout();
            }
            super.invalidate();
        }

        private boolean[] groupSeen() {
            boolean[] seen = new boolean[this.groupCells.size()];
            float y = (float) this.paddingTop;
            int scrollY = ChatAttachAlertPhotoLayoutPreview.this.listView.computeVerticalScrollOffset();
            this.viewTop = (float) Math.max(0, scrollY - ChatAttachAlertPhotoLayoutPreview.this.getListTopPadding());
            this.viewBottom = (float) ((ChatAttachAlertPhotoLayoutPreview.this.listView.getMeasuredHeight() - ChatAttachAlertPhotoLayoutPreview.this.getListTopPadding()) + scrollY);
            int groupCellsSize = this.groupCells.size();
            for (int i = 0; i < groupCellsSize; i++) {
                float height = this.groupCells.get(i).measure();
                seen[i] = isSeen(y, y + height);
                y += height;
            }
            return seen;
        }

        public boolean isSeen(float fromY, float toY) {
            float f = this.viewTop;
            return (fromY >= f && fromY <= this.viewBottom) || (toY >= f && toY <= this.viewBottom) || (fromY <= f && toY >= this.viewBottom);
        }

        public void onScroll() {
            boolean newGroupSeen = this.lastGroupSeen == null;
            if (!newGroupSeen) {
                boolean[] seen = groupSeen();
                if (seen.length != this.lastGroupSeen.length) {
                    newGroupSeen = true;
                } else {
                    int i = 0;
                    while (true) {
                        if (i >= seen.length) {
                            break;
                        } else if (seen[i] != this.lastGroupSeen[i]) {
                            newGroupSeen = true;
                            break;
                        } else {
                            i++;
                        }
                    }
                }
            } else {
                this.lastGroupSeen = groupSeen();
            }
            if (newGroupSeen) {
                invalidate();
            }
        }

        public void remeasure() {
            float y = (float) this.paddingTop;
            int i = 0;
            int groupCellsCount = this.groupCells.size();
            for (int j = 0; j < groupCellsCount; j++) {
                PreviewGroupCell groupCell = this.groupCells.get(j);
                float height = groupCell.measure();
                groupCell.y = y;
                groupCell.indexStart = i;
                y += height;
                i += groupCell.group.photos.size();
            }
        }

        public void onDraw(Canvas canvas) {
            float y = (float) this.paddingTop;
            int i = 0;
            int scrollY = ChatAttachAlertPhotoLayoutPreview.this.listView.computeVerticalScrollOffset();
            this.viewTop = (float) Math.max(0, scrollY - ChatAttachAlertPhotoLayoutPreview.this.getListTopPadding());
            this.viewBottom = (float) ((ChatAttachAlertPhotoLayoutPreview.this.listView.getMeasuredHeight() - ChatAttachAlertPhotoLayoutPreview.this.getListTopPadding()) + scrollY);
            canvas.save();
            canvas.translate(0.0f, (float) this.paddingTop);
            int groupCellsCount = this.groupCells.size();
            int j = 0;
            while (true) {
                boolean groupIsSeen = true;
                if (j >= groupCellsCount) {
                    break;
                }
                PreviewGroupCell groupCell = this.groupCells.get(j);
                float height = groupCell.measure();
                groupCell.y = y;
                groupCell.indexStart = i;
                float f = this.viewTop;
                if ((y < f || y > this.viewBottom) && ((y + height < f || y + height > this.viewBottom) && (y > f || y + height < this.viewBottom))) {
                    groupIsSeen = false;
                }
                if (groupIsSeen && groupCell.draw(canvas)) {
                    invalidate();
                }
                canvas.translate(0.0f, height);
                y += height;
                i += groupCell.group.photos.size();
                j++;
            }
            ChatActionCell chatActionCell = this.hintView;
            chatActionCell.setVisiblePart(y, chatActionCell.getMeasuredHeight());
            if (this.hintView.hasGradientService()) {
                this.hintView.drawBackground(canvas, true);
            }
            this.hintView.draw(canvas);
            canvas.restore();
            if (ChatAttachAlertPhotoLayoutPreview.this.draggingCell != null) {
                canvas.save();
                Point point = dragTranslate();
                canvas.translate(point.x, point.y);
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
                this.tmpPoint.x = 0.0f;
                this.tmpPoint.y = 0.0f;
                return this.tmpPoint;
            }
            if (!ChatAttachAlertPhotoLayoutPreview.this.draggingCellHiding) {
                RectF drawingRect = ChatAttachAlertPhotoLayoutPreview.this.draggingCell.rect();
                RectF finalDrawingRect = ChatAttachAlertPhotoLayoutPreview.this.draggingCell.rect(1.0f);
                this.tmpPoint.x = AndroidUtilities.lerp(finalDrawingRect.left + (drawingRect.width() / 2.0f), ChatAttachAlertPhotoLayoutPreview.this.draggingCellTouchX - ((ChatAttachAlertPhotoLayoutPreview.this.draggingCellLeft - 0.5f) * ChatAttachAlertPhotoLayoutPreview.this.draggingCellFromWidth), this.draggingT);
                this.tmpPoint.y = AndroidUtilities.lerp(ChatAttachAlertPhotoLayoutPreview.this.draggingCell.groupCell.y + finalDrawingRect.top + (drawingRect.height() / 2.0f), (ChatAttachAlertPhotoLayoutPreview.this.draggingCellTouchY - ((ChatAttachAlertPhotoLayoutPreview.this.draggingCellTop - 0.5f) * ChatAttachAlertPhotoLayoutPreview.this.draggingCellFromHeight)) + ChatAttachAlertPhotoLayoutPreview.this.draggingCellGroupY, this.draggingT);
            } else {
                RectF drawingRect2 = ChatAttachAlertPhotoLayoutPreview.this.draggingCell.rect();
                RectF finalDrawingRect2 = ChatAttachAlertPhotoLayoutPreview.this.draggingCell.rect(1.0f);
                this.tmpPoint.x = AndroidUtilities.lerp(finalDrawingRect2.left + (drawingRect2.width() / 2.0f), this.savedDragFromX, this.draggingT / this.savedDraggingT);
                this.tmpPoint.y = AndroidUtilities.lerp(ChatAttachAlertPhotoLayoutPreview.this.draggingCell.groupCell.y + finalDrawingRect2.top + (drawingRect2.height() / 2.0f), this.savedDragFromY, this.draggingT / this.savedDraggingT);
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
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.addUpdateListener(new ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda1(this));
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    PreviewGroupCell.MediaCell unused = ChatAttachAlertPhotoLayoutPreview.this.draggingCell = null;
                    boolean unused2 = ChatAttachAlertPhotoLayoutPreview.this.draggingCellHiding = false;
                    PreviewGroupsView.this.invalidate();
                }
            });
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.setDuration(200);
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.start();
            invalidate();
        }

        /* renamed from: lambda$stopDragging$0$org-telegram-ui-Components-ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView  reason: not valid java name */
        public /* synthetic */ void m854x76aa0fb1(ValueAnimator a) {
            this.draggingT = ((Float) a.getAnimatedValue()).floatValue();
            invalidate();
        }

        /* access modifiers changed from: package-private */
        public void startDragging(PreviewGroupCell.MediaCell cell) {
            PreviewGroupCell.MediaCell unused = ChatAttachAlertPhotoLayoutPreview.this.draggingCell = cell;
            ChatAttachAlertPhotoLayoutPreview chatAttachAlertPhotoLayoutPreview = ChatAttachAlertPhotoLayoutPreview.this;
            float unused2 = chatAttachAlertPhotoLayoutPreview.draggingCellGroupY = chatAttachAlertPhotoLayoutPreview.draggingCell.groupCell.y;
            boolean unused3 = ChatAttachAlertPhotoLayoutPreview.this.draggingCellHiding = false;
            this.draggingT = 0.0f;
            invalidate();
            if (ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator != null) {
                ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.cancel();
            }
            ValueAnimator unused4 = ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.addUpdateListener(new ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda0(this));
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.setDuration(200);
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.start();
        }

        /* renamed from: lambda$startDragging$1$org-telegram-ui-Components-ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView  reason: not valid java name */
        public /* synthetic */ void m853x4294d1ca(ValueAnimator a) {
            this.draggingT = ((Float) a.getAnimatedValue()).floatValue();
            invalidate();
        }

        class GroupingPhotoViewerProvider extends PhotoViewer.EmptyPhotoViewerProvider {
            private ArrayList<MediaController.PhotoEntry> photos = new ArrayList<>();

            GroupingPhotoViewerProvider() {
            }

            public void init(ArrayList<MediaController.PhotoEntry> photos2) {
                this.photos = photos2;
            }

            public void onClose() {
                PreviewGroupsView.this.fromPhotoArrays();
                PreviewGroupsView previewGroupsView = PreviewGroupsView.this;
                previewGroupsView.toPhotoLayout(ChatAttachAlertPhotoLayoutPreview.this.photoLayout, false);
            }

            public boolean isPhotoChecked(int index) {
                if (index < 0 || index >= this.photos.size()) {
                    return false;
                }
                return PreviewGroupsView.this.photosOrder.contains(Integer.valueOf(this.photos.get(index).imageId));
            }

            public int setPhotoChecked(int index, VideoEditedInfo videoEditedInfo) {
                if (index < 0 || index >= this.photos.size()) {
                    return -1;
                }
                Integer valueOf = Integer.valueOf(this.photos.get(index).imageId);
                int orderIndex = PreviewGroupsView.this.photosOrder.indexOf(valueOf);
                if (orderIndex < 0) {
                    PreviewGroupsView.this.photosOrder.add(valueOf);
                    PreviewGroupsView.this.fromPhotoArrays();
                    return PreviewGroupsView.this.photosOrder.size() - 1;
                } else if (PreviewGroupsView.this.photosOrder.size() <= 1) {
                    return -1;
                } else {
                    PreviewGroupsView.this.photosOrder.remove(orderIndex);
                    PreviewGroupsView.this.fromPhotoArrays();
                    return orderIndex;
                }
            }

            public int setPhotoUnchecked(Object entry) {
                int index;
                Integer valueOf = Integer.valueOf(((MediaController.PhotoEntry) entry).imageId);
                if (PreviewGroupsView.this.photosOrder.size() <= 1 || (index = PreviewGroupsView.this.photosOrder.indexOf(valueOf)) < 0) {
                    return -1;
                }
                PreviewGroupsView.this.photosOrder.remove(index);
                PreviewGroupsView.this.fromPhotoArrays();
                return index;
            }

            public int getSelectedCount() {
                return PreviewGroupsView.this.photosOrder.size();
            }

            public ArrayList<Object> getSelectedPhotosOrder() {
                return PreviewGroupsView.this.photosOrder;
            }

            public HashMap<Object, Object> getSelectedPhotos() {
                return PreviewGroupsView.this.photosMap;
            }

            public int getPhotoIndex(int index) {
                MediaController.PhotoEntry photoEntry;
                if (index < 0 || index >= this.photos.size() || (photoEntry = this.photos.get(index)) == null) {
                    return -1;
                }
                return PreviewGroupsView.this.photosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
            }

            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v3, resolved type: java.lang.Object} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v3, resolved type: org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell} */
            /* JADX WARNING: Multi-variable type inference failed */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public org.telegram.ui.PhotoViewer.PlaceProviderObject getPlaceForPhoto(org.telegram.messenger.MessageObject r17, org.telegram.tgnet.TLRPC.FileLocation r18, int r19, boolean r20) {
                /*
                    r16 = this;
                    r0 = r16
                    r1 = r19
                    r2 = 0
                    if (r1 < 0) goto L_0x011c
                    java.util.ArrayList<org.telegram.messenger.MediaController$PhotoEntry> r3 = r0.photos
                    int r3 = r3.size()
                    if (r1 >= r3) goto L_0x011c
                    boolean r3 = r0.isPhotoChecked(r1)
                    if (r3 != 0) goto L_0x0017
                    goto L_0x011c
                L_0x0017:
                    java.util.ArrayList<org.telegram.messenger.MediaController$PhotoEntry> r3 = r0.photos
                    java.lang.Object r3 = r3.get(r1)
                    org.telegram.messenger.MediaController$PhotoEntry r3 = (org.telegram.messenger.MediaController.PhotoEntry) r3
                    if (r3 == 0) goto L_0x011b
                    r4 = 0
                    r5 = 0
                    org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView r6 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.this
                    java.util.ArrayList r6 = r6.groupCells
                    int r6 = r6.size()
                    r7 = 0
                L_0x002e:
                    if (r7 >= r6) goto L_0x0076
                    org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView r8 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.this
                    java.util.ArrayList r8 = r8.groupCells
                    java.lang.Object r8 = r8.get(r7)
                    r4 = r8
                    org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell r4 = (org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.PreviewGroupCell) r4
                    if (r4 == 0) goto L_0x0073
                    java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell> r8 = r4.media
                    if (r8 == 0) goto L_0x0073
                    java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell> r8 = r4.media
                    int r8 = r8.size()
                    r9 = 0
                L_0x004a:
                    if (r9 >= r8) goto L_0x0070
                    java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell> r10 = r4.media
                    java.lang.Object r10 = r10.get(r9)
                    org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r10 = (org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.PreviewGroupCell.MediaCell) r10
                    if (r10 == 0) goto L_0x006d
                    org.telegram.messenger.MediaController$PhotoEntry r11 = r10.photoEntry
                    if (r11 != r3) goto L_0x006d
                    float r11 = r10.scale
                    double r11 = (double) r11
                    r13 = 4602678819172646912(0x3feNUM, double:0.5)
                    int r15 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
                    if (r15 <= 0) goto L_0x006d
                    java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell> r11 = r4.media
                    java.lang.Object r11 = r11.get(r9)
                    r5 = r11
                    org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$PreviewGroupCell$MediaCell r5 = (org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.PreviewGroupCell.MediaCell) r5
                    goto L_0x0070
                L_0x006d:
                    int r9 = r9 + 1
                    goto L_0x004a
                L_0x0070:
                    if (r5 == 0) goto L_0x0073
                    goto L_0x0076
                L_0x0073:
                    int r7 = r7 + 1
                    goto L_0x002e
                L_0x0076:
                    if (r4 == 0) goto L_0x011b
                    if (r5 == 0) goto L_0x011b
                    org.telegram.ui.PhotoViewer$PlaceProviderObject r2 = new org.telegram.ui.PhotoViewer$PlaceProviderObject
                    r2.<init>()
                    r7 = 2
                    int[] r8 = new int[r7]
                    org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView r9 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.this
                    r9.getLocationInWindow(r8)
                    int r9 = android.os.Build.VERSION.SDK_INT
                    r10 = 26
                    r11 = 0
                    if (r9 >= r10) goto L_0x009d
                    r9 = r8[r11]
                    org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView r10 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.this
                    org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r10 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                    org.telegram.ui.Components.ChatAttachAlert r10 = r10.parentAlert
                    int r10 = r10.getLeftInset()
                    int r9 = r9 - r10
                    r8[r11] = r9
                L_0x009d:
                    r9 = r8[r11]
                    r2.viewX = r9
                    r9 = 1
                    r10 = r8[r9]
                    float r12 = r4.y
                    int r12 = (int) r12
                    int r10 = r10 + r12
                    r2.viewY = r10
                    r10 = 1065353216(0x3var_, float:1.0)
                    r2.scale = r10
                    org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView r10 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.this
                    r2.parentView = r10
                    org.telegram.messenger.ImageReceiver r10 = r5.image
                    r2.imageReceiver = r10
                    org.telegram.messenger.ImageReceiver r10 = r2.imageReceiver
                    org.telegram.messenger.ImageReceiver$BitmapHolder r10 = r10.getBitmapSafe()
                    r2.thumb = r10
                    r10 = 4
                    int[] r10 = new int[r10]
                    r2.radius = r10
                    int[] r10 = r2.radius
                    android.graphics.RectF r12 = r5.roundRadiuses
                    float r12 = r12.left
                    int r12 = (int) r12
                    r10[r11] = r12
                    int[] r10 = r2.radius
                    android.graphics.RectF r11 = r5.roundRadiuses
                    float r11 = r11.top
                    int r11 = (int) r11
                    r10[r9] = r11
                    int[] r9 = r2.radius
                    android.graphics.RectF r10 = r5.roundRadiuses
                    float r10 = r10.right
                    int r10 = (int) r10
                    r9[r7] = r10
                    int[] r7 = r2.radius
                    r9 = 3
                    android.graphics.RectF r10 = r5.roundRadiuses
                    float r10 = r10.bottom
                    int r10 = (int) r10
                    r7[r9] = r10
                    org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView r7 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.this
                    float r7 = r7.getY()
                    float r7 = -r7
                    int r7 = (int) r7
                    r2.clipTopAddition = r7
                    org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView r7 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.this
                    int r7 = r7.getHeight()
                    org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView r9 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.this
                    float r9 = r9.getY()
                    float r9 = -r9
                    org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView r10 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.this
                    org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r10 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                    org.telegram.ui.Components.RecyclerListView r10 = r10.listView
                    int r10 = r10.getHeight()
                    float r10 = (float) r10
                    float r9 = r9 + r10
                    org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView r10 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.this
                    org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r10 = org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.this
                    org.telegram.ui.Components.ChatAttachAlert r10 = r10.parentAlert
                    float r10 = r10.getClipLayoutBottom()
                    float r9 = r9 - r10
                    int r9 = (int) r9
                    int r7 = r7 - r9
                    r2.clipBottomAddition = r7
                    return r2
                L_0x011b:
                    return r2
                L_0x011c:
                    return r2
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.GroupingPhotoViewerProvider.getPlaceForPhoto(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$FileLocation, int, boolean):org.telegram.ui.PhotoViewer$PlaceProviderObject");
            }

            public boolean cancelButtonPressed() {
                return false;
            }

            public void updatePhotoAtIndex(int index) {
                MediaController.PhotoEntry photoEntry;
                if (index >= 0 && index < this.photos.size() && (photoEntry = this.photos.get(index)) != null) {
                    int imageId = photoEntry.imageId;
                    PreviewGroupsView.this.invalidate();
                    for (int i = 0; i < PreviewGroupsView.this.groupCells.size(); i++) {
                        PreviewGroupCell groupCell = (PreviewGroupCell) PreviewGroupsView.this.groupCells.get(i);
                        if (!(groupCell == null || groupCell.media == null)) {
                            for (int j = 0; j < groupCell.media.size(); j++) {
                                PreviewGroupCell.MediaCell mediaCell = groupCell.media.get(j);
                                if (mediaCell != null && mediaCell.photoEntry.imageId == imageId) {
                                    mediaCell.setImage(photoEntry);
                                }
                            }
                            boolean hadUpdates = false;
                            if (!(groupCell.group == null || groupCell.group.photos == null)) {
                                for (int j2 = 0; j2 < groupCell.group.photos.size(); j2++) {
                                    if (groupCell.group.photos.get(j2).imageId == imageId) {
                                        groupCell.group.photos.set(j2, photoEntry);
                                        hadUpdates = true;
                                    }
                                }
                            }
                            if (hadUpdates) {
                                groupCell.setGroup(groupCell.group, true);
                            }
                        }
                    }
                    PreviewGroupsView.this.remeasure();
                    PreviewGroupsView.this.invalidate();
                }
            }
        }

        public boolean onTouchEvent(MotionEvent event) {
            PreviewGroupCell touchGroupCell;
            boolean result;
            PreviewGroupCell draggingOverGroupCell;
            boolean result2;
            PreviewGroupCell.MediaCell mediaCell;
            int type;
            ChatActivity chatActivity;
            PreviewGroupCell.MediaCell replaceMediaCell;
            PreviewGroupCell replaceGroupCell;
            int mediaCount;
            PreviewGroupCell draggingOverGroupCell2;
            float maxLength;
            float f;
            RectF drawingRect;
            float touchX = event.getX();
            float touchY = event.getY();
            PreviewGroupCell.MediaCell touchMediaCell = null;
            float groupY = 0.0f;
            int groupCellsCount = this.groupCells.size();
            int j = 0;
            while (true) {
                if (j >= groupCellsCount) {
                    touchGroupCell = null;
                    break;
                }
                PreviewGroupCell groupCell = this.groupCells.get(j);
                float height = groupCell.measure();
                if (touchY >= groupY && touchY <= groupY + height) {
                    touchGroupCell = groupCell;
                    break;
                }
                groupY += height;
                j++;
            }
            if (touchGroupCell != null) {
                int mediaCount2 = touchGroupCell.media.size();
                int i = 0;
                while (true) {
                    if (i < mediaCount2) {
                        PreviewGroupCell.MediaCell mediaCell2 = touchGroupCell.media.get(i);
                        if (mediaCell2 != null && mediaCell2.drawingRect().contains(touchX, touchY - groupY)) {
                            touchMediaCell = mediaCell2;
                            break;
                        }
                        i++;
                    } else {
                        break;
                    }
                }
            }
            PreviewGroupCell.MediaCell draggingOverMediaCell = null;
            if (ChatAttachAlertPhotoLayoutPreview.this.draggingCell != null) {
                RectF drawingRect2 = ChatAttachAlertPhotoLayoutPreview.this.draggingCell.rect();
                Point dragPoint = dragTranslate();
                RectF draggingCellXY = new RectF();
                float cx = dragPoint.x;
                float cy = dragPoint.y;
                result = false;
                float groupY2 = 0.0f;
                draggingCellXY.set(cx - (drawingRect2.width() / 2.0f), cy - (drawingRect2.height() / 2.0f), cx + (drawingRect2.width() / 2.0f), cy + (drawingRect2.height() / 2.0f));
                int j2 = 0;
                float maxLength2 = 0.0f;
                PreviewGroupCell draggingOverGroupCell3 = null;
                while (j2 < groupCellsCount) {
                    PreviewGroupCell groupCell2 = this.groupCells.get(j2);
                    float height2 = groupCell2.measure();
                    float top = groupY2;
                    int groupCellsCount2 = groupCellsCount;
                    float bottom = groupY2 + height2;
                    PreviewGroupCell.MediaCell draggingOverMediaCell2 = draggingOverMediaCell;
                    if (bottom >= draggingCellXY.top) {
                        drawingRect = drawingRect2;
                        float top2 = top;
                        if (draggingCellXY.bottom >= top2) {
                            float f2 = bottom;
                            float length = Math.min(bottom, draggingCellXY.bottom) - Math.max(top2, draggingCellXY.top);
                            if (length > maxLength2) {
                                draggingOverGroupCell3 = groupCell2;
                                maxLength2 = length;
                            }
                        }
                    } else {
                        drawingRect = drawingRect2;
                        float f3 = top;
                        float top3 = bottom;
                    }
                    groupY2 += height2;
                    j2++;
                    groupCellsCount = groupCellsCount2;
                    draggingOverMediaCell = draggingOverMediaCell2;
                    drawingRect2 = drawingRect;
                }
                PreviewGroupCell.MediaCell draggingOverMediaCell3 = draggingOverMediaCell;
                RectF rectF = drawingRect2;
                if (draggingOverGroupCell3 != null) {
                    float maxArea = 0.0f;
                    int mediaCount3 = draggingOverGroupCell3.media.size();
                    int i2 = 0;
                    while (i2 < mediaCount3) {
                        PreviewGroupCell.MediaCell mediaCell3 = draggingOverGroupCell3.media.get(i2);
                        if (mediaCell3 == null || mediaCell3 == ChatAttachAlertPhotoLayoutPreview.this.draggingCell) {
                            draggingOverGroupCell2 = draggingOverGroupCell3;
                            maxLength = maxLength2;
                            mediaCount = mediaCount3;
                        } else {
                            maxLength = maxLength2;
                            if (draggingOverGroupCell3.group.photos.contains(mediaCell3.photoEntry)) {
                                RectF mediaCellRect = mediaCell3.drawingRect();
                                if ((mediaCell3.positionFlags & 4) > 0) {
                                    f = 0.0f;
                                    mediaCellRect.top = 0.0f;
                                } else {
                                    f = 0.0f;
                                }
                                if ((mediaCell3.positionFlags & 1) > 0) {
                                    mediaCellRect.left = f;
                                }
                                if ((mediaCell3.positionFlags & 2) > 0) {
                                    mediaCellRect.right = (float) getWidth();
                                }
                                if ((mediaCell3.positionFlags & 8) > 0) {
                                    mediaCellRect.bottom = draggingOverGroupCell3.height;
                                }
                                if (RectF.intersects(draggingCellXY, mediaCellRect)) {
                                    draggingOverGroupCell2 = draggingOverGroupCell3;
                                    mediaCount = mediaCount3;
                                    RectF rectF2 = mediaCellRect;
                                    float area = ((Math.min(mediaCellRect.right, draggingCellXY.right) - Math.max(mediaCellRect.left, draggingCellXY.left)) * (Math.min(mediaCellRect.bottom, draggingCellXY.bottom) - Math.max(mediaCellRect.top, draggingCellXY.top))) / (draggingCellXY.width() * draggingCellXY.height());
                                    if (area > 0.15f && area > maxArea) {
                                        maxArea = area;
                                        draggingOverMediaCell3 = mediaCell3;
                                    }
                                } else {
                                    draggingOverGroupCell2 = draggingOverGroupCell3;
                                    RectF rectF3 = mediaCellRect;
                                    mediaCount = mediaCount3;
                                }
                            } else {
                                draggingOverGroupCell2 = draggingOverGroupCell3;
                                mediaCount = mediaCount3;
                            }
                        }
                        i2++;
                        maxLength2 = maxLength;
                        draggingOverGroupCell3 = draggingOverGroupCell2;
                        mediaCount3 = mediaCount;
                    }
                    draggingOverGroupCell = draggingOverGroupCell3;
                    float f4 = maxLength2;
                    int i3 = mediaCount3;
                    float maxArea2 = groupY2;
                    draggingOverMediaCell = draggingOverMediaCell3;
                } else {
                    draggingOverGroupCell = draggingOverGroupCell3;
                    float f5 = maxLength2;
                    float f6 = groupY2;
                    draggingOverMediaCell = draggingOverMediaCell3;
                }
            } else {
                draggingOverGroupCell = null;
                result = false;
                int i4 = groupCellsCount;
            }
            int action = event.getAction();
            if (action == 0 && ChatAttachAlertPhotoLayoutPreview.this.draggingCell == null && !ChatAttachAlertPhotoLayoutPreview.this.listView.scrollingByUser && ((ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator == null || !ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.isRunning()) && touchGroupCell != null && touchMediaCell != null && touchGroupCell.group != null && touchGroupCell.group.photos.contains(touchMediaCell.photoEntry))) {
                this.tapGroupCell = touchGroupCell;
                this.tapMediaCell = touchMediaCell;
                float unused = ChatAttachAlertPhotoLayoutPreview.this.draggingCellTouchX = touchX;
                float unused2 = ChatAttachAlertPhotoLayoutPreview.this.draggingCellTouchY = touchY;
                PreviewGroupCell.MediaCell unused3 = ChatAttachAlertPhotoLayoutPreview.this.draggingCell = null;
                long wasTapTime = SystemClock.elapsedRealtime();
                this.tapTime = wasTapTime;
                AndroidUtilities.runOnUIThread(new ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda3(this, wasTapTime, this.tapMediaCell), (long) ViewConfiguration.getLongPressTimeout());
                invalidate();
                result2 = true;
                float f7 = touchX;
                float f8 = touchY;
                PreviewGroupCell.MediaCell mediaCell4 = touchMediaCell;
            } else if (action == 2 && ChatAttachAlertPhotoLayoutPreview.this.draggingCell != null && !ChatAttachAlertPhotoLayoutPreview.this.draggingCellHiding) {
                float unused4 = ChatAttachAlertPhotoLayoutPreview.this.draggingCellTouchX = touchX;
                float unused5 = ChatAttachAlertPhotoLayoutPreview.this.draggingCellTouchY = touchY;
                if (!this.scrollerStarted) {
                    this.scrollerStarted = true;
                    postDelayed(this.scroller, 16);
                }
                invalidate();
                result2 = true;
                float f9 = touchX;
                float var_ = touchY;
                PreviewGroupCell.MediaCell mediaCell5 = touchMediaCell;
            } else if (action != 1 || ChatAttachAlertPhotoLayoutPreview.this.draggingCell == null) {
                if (action != 1 || ChatAttachAlertPhotoLayoutPreview.this.draggingCell != null || (mediaCell = this.tapMediaCell) == null || this.tapGroupCell == null) {
                    float var_ = touchY;
                    result2 = result;
                } else {
                    RectF cellRect = mediaCell.drawingRect();
                    AndroidUtilities.rectTmp.set(cellRect.right - ((float) AndroidUtilities.dp(36.4f)), this.tapGroupCell.top + cellRect.top, cellRect.right, this.tapGroupCell.top + cellRect.top + ((float) AndroidUtilities.dp(36.4f)));
                    if (!AndroidUtilities.rectTmp.contains(touchX, touchY - this.tapMediaCell.groupCell.y)) {
                        float var_ = touchY;
                        calcPhotoArrays();
                        ArrayList<MediaController.PhotoEntry> arrayList = getPhotos();
                        int position = arrayList.indexOf(this.tapMediaCell.photoEntry);
                        if (ChatAttachAlertPhotoLayoutPreview.this.parentAlert.avatarPicker != 0) {
                            chatActivity = null;
                            type = 1;
                        } else if (ChatAttachAlertPhotoLayoutPreview.this.parentAlert.baseFragment instanceof ChatActivity) {
                            chatActivity = (ChatActivity) ChatAttachAlertPhotoLayoutPreview.this.parentAlert.baseFragment;
                            type = 0;
                        } else {
                            chatActivity = null;
                            type = 4;
                        }
                        if (!ChatAttachAlertPhotoLayoutPreview.this.parentAlert.delegate.needEnterComment()) {
                            AndroidUtilities.hideKeyboard(ChatAttachAlertPhotoLayoutPreview.this.parentAlert.baseFragment.getFragmentView().findFocus());
                            AndroidUtilities.hideKeyboard(ChatAttachAlertPhotoLayoutPreview.this.parentAlert.getContainer().findFocus());
                        }
                        PhotoViewer.getInstance().setParentActivity(ChatAttachAlertPhotoLayoutPreview.this.parentAlert.baseFragment.getParentActivity(), ChatAttachAlertPhotoLayoutPreview.this.resourcesProvider);
                        PhotoViewer.getInstance().setParentAlert(ChatAttachAlertPhotoLayoutPreview.this.parentAlert);
                        PhotoViewer.getInstance().setMaxSelectedPhotos(ChatAttachAlertPhotoLayoutPreview.this.parentAlert.maxSelectedPhotos, ChatAttachAlertPhotoLayoutPreview.this.parentAlert.allowOrder);
                        this.photoViewerProvider.init(arrayList);
                        PhotoViewer.getInstance().openPhotoForSelect(new ArrayList(arrayList), position, type, false, this.photoViewerProvider, chatActivity);
                        if (ChatAttachAlertPhotoLayoutPreview.this.photoLayout.captionForAllMedia()) {
                            PhotoViewer.getInstance().setCaption(ChatAttachAlertPhotoLayoutPreview.this.parentAlert.getCommentTextView().getText());
                        }
                    } else if (ChatAttachAlertPhotoLayoutPreview.this.getSelectedItemsCount() > 1) {
                        MediaController.PhotoEntry photo = this.tapMediaCell.photoEntry;
                        int index = this.tapGroupCell.group.photos.indexOf(photo);
                        if (index >= 0) {
                            saveDeletedImageId(photo);
                            PreviewGroupCell groupCell3 = this.tapGroupCell;
                            groupCell3.group.photos.remove(index);
                            groupCell3.setGroup(groupCell3.group, true);
                            updateGroups();
                            toPhotoLayout(ChatAttachAlertPhotoLayoutPreview.this.photoLayout, false);
                            int currentUndoViewId = this.undoViewId + 1;
                            this.undoViewId = currentUndoViewId;
                            ChatAttachAlertPhotoLayoutPreview.this.undoView.showWithAction(0, 82, photo, (Runnable) null, new ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda4(this, groupCell3, photo, index));
                            float var_ = touchX;
                            float var_ = touchY;
                            postDelayed(new ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda2(this, currentUndoViewId), 4000);
                        } else {
                            float var_ = touchY;
                        }
                        if (ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator != null) {
                            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.cancel();
                        }
                    } else {
                        float var_ = touchY;
                    }
                    this.tapMediaCell = null;
                    this.tapTime = 0;
                    PreviewGroupCell.MediaCell unused6 = ChatAttachAlertPhotoLayoutPreview.this.draggingCell = null;
                    this.draggingT = 0.0f;
                    result2 = true;
                }
            } else {
                if (touchGroupCell != null && touchMediaCell != null && touchMediaCell != ChatAttachAlertPhotoLayoutPreview.this.draggingCell) {
                    replaceMediaCell = touchMediaCell;
                    replaceGroupCell = touchGroupCell;
                } else if (draggingOverGroupCell == null || draggingOverMediaCell == null || draggingOverMediaCell == ChatAttachAlertPhotoLayoutPreview.this.draggingCell || draggingOverMediaCell.photoEntry == ChatAttachAlertPhotoLayoutPreview.this.draggingCell.photoEntry) {
                    replaceMediaCell = null;
                    replaceGroupCell = null;
                } else {
                    replaceMediaCell = draggingOverMediaCell;
                    replaceGroupCell = draggingOverGroupCell;
                }
                if (replaceGroupCell == null || replaceMediaCell == null || replaceMediaCell == ChatAttachAlertPhotoLayoutPreview.this.draggingCell) {
                } else {
                    int draggingIndex = ChatAttachAlertPhotoLayoutPreview.this.draggingCell.groupCell.group.photos.indexOf(ChatAttachAlertPhotoLayoutPreview.this.draggingCell.photoEntry);
                    int tapIndex = replaceGroupCell.group.photos.indexOf(replaceMediaCell.photoEntry);
                    if (draggingIndex >= 0) {
                        ChatAttachAlertPhotoLayoutPreview.this.draggingCell.groupCell.group.photos.remove(draggingIndex);
                        ChatAttachAlertPhotoLayoutPreview.this.draggingCell.groupCell.setGroup(ChatAttachAlertPhotoLayoutPreview.this.draggingCell.groupCell.group, true);
                    }
                    if (tapIndex >= 0) {
                        if (this.groupCells.indexOf(replaceGroupCell) > this.groupCells.indexOf(ChatAttachAlertPhotoLayoutPreview.this.draggingCell.groupCell)) {
                            tapIndex++;
                        }
                        pushToGroup(replaceGroupCell, ChatAttachAlertPhotoLayoutPreview.this.draggingCell.photoEntry, tapIndex);
                        if (ChatAttachAlertPhotoLayoutPreview.this.draggingCell.groupCell != replaceGroupCell) {
                            PreviewGroupCell.MediaCell newDraggingCell = null;
                            int mediaCount4 = replaceGroupCell.media.size();
                            int i5 = 0;
                            while (true) {
                                if (i5 >= mediaCount4) {
                                    PreviewGroupCell.MediaCell mediaCell6 = touchMediaCell;
                                    break;
                                }
                                PreviewGroupCell.MediaCell mediaCell7 = replaceGroupCell.media.get(i5);
                                int tapIndex2 = tapIndex;
                                PreviewGroupCell.MediaCell touchMediaCell2 = touchMediaCell;
                                if (mediaCell7.photoEntry == ChatAttachAlertPhotoLayoutPreview.this.draggingCell.photoEntry) {
                                    newDraggingCell = mediaCell7;
                                    break;
                                }
                                i5++;
                                tapIndex = tapIndex2;
                                touchMediaCell = touchMediaCell2;
                            }
                            if (newDraggingCell != null) {
                                remeasure();
                                newDraggingCell.layoutFrom(ChatAttachAlertPhotoLayoutPreview.this.draggingCell);
                                PreviewGroupCell.MediaCell unused7 = ChatAttachAlertPhotoLayoutPreview.this.draggingCell = newDraggingCell;
                                newDraggingCell.groupCell = replaceGroupCell;
                                ChatAttachAlertPhotoLayoutPreview.this.draggingCell.fromScale = 1.0f;
                                newDraggingCell.scale = 1.0f;
                                remeasure();
                            }
                        } else {
                            PreviewGroupCell.MediaCell mediaCell8 = touchMediaCell;
                        }
                    } else {
                        int i6 = tapIndex;
                    }
                    try {
                        ChatAttachAlertPhotoLayoutPreview.this.performHapticFeedback(7, 2);
                    } catch (Exception e) {
                    }
                    updateGroups();
                    toPhotoLayout(ChatAttachAlertPhotoLayoutPreview.this.photoLayout, false);
                }
                stopDragging();
                result2 = true;
                float var_ = touchX;
                float var_ = touchY;
            }
            if (action != 1 && action != 3) {
                return result2;
            }
            this.tapTime = 0;
            removeCallbacks(this.scroller);
            this.scrollerStarted = false;
            if (result2) {
                return result2;
            }
            stopDragging();
            return true;
        }

        /* renamed from: lambda$onTouchEvent$2$org-telegram-ui-Components-ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView  reason: not valid java name */
        public /* synthetic */ void m850x25db1826(long wasTapTime, PreviewGroupCell.MediaCell wasTapMediaCell) {
            PreviewGroupCell.MediaCell mediaCell;
            if (!ChatAttachAlertPhotoLayoutPreview.this.listView.scrollingByUser && this.tapTime == wasTapTime && (mediaCell = this.tapMediaCell) == wasTapMediaCell) {
                startDragging(mediaCell);
                RectF draggingCellRect = ChatAttachAlertPhotoLayoutPreview.this.draggingCell.rect();
                RectF draggingCellDrawingRect = ChatAttachAlertPhotoLayoutPreview.this.draggingCell.drawingRect();
                ChatAttachAlertPhotoLayoutPreview chatAttachAlertPhotoLayoutPreview = ChatAttachAlertPhotoLayoutPreview.this;
                float unused = chatAttachAlertPhotoLayoutPreview.draggingCellLeft = (((chatAttachAlertPhotoLayoutPreview.draggingCellTouchX - draggingCellRect.left) / draggingCellRect.width()) + 0.5f) / 2.0f;
                ChatAttachAlertPhotoLayoutPreview chatAttachAlertPhotoLayoutPreview2 = ChatAttachAlertPhotoLayoutPreview.this;
                float unused2 = chatAttachAlertPhotoLayoutPreview2.draggingCellTop = (chatAttachAlertPhotoLayoutPreview2.draggingCellTouchY - draggingCellRect.top) / draggingCellRect.height();
                float unused3 = ChatAttachAlertPhotoLayoutPreview.this.draggingCellFromWidth = draggingCellDrawingRect.width();
                float unused4 = ChatAttachAlertPhotoLayoutPreview.this.draggingCellFromHeight = draggingCellDrawingRect.height();
                try {
                    ChatAttachAlertPhotoLayoutPreview.this.performHapticFeedback(0, 2);
                } catch (Exception e) {
                }
            }
        }

        /* renamed from: lambda$onTouchEvent$3$org-telegram-ui-Components-ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView  reason: not valid java name */
        public /* synthetic */ void m851x3ce7e05(PreviewGroupCell groupCell, MediaController.PhotoEntry photo, int index) {
            if (ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator != null) {
                ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.cancel();
            }
            PreviewGroupCell.MediaCell unused = ChatAttachAlertPhotoLayoutPreview.this.draggingCell = null;
            this.draggingT = 0.0f;
            pushToGroup(groupCell, photo, index);
            updateGroups();
            toPhotoLayout(ChatAttachAlertPhotoLayoutPreview.this.photoLayout, false);
        }

        /* renamed from: lambda$onTouchEvent$4$org-telegram-ui-Components-ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView  reason: not valid java name */
        public /* synthetic */ void m852xe1c1e3e4(int currentUndoViewId) {
            if (currentUndoViewId == this.undoViewId && ChatAttachAlertPhotoLayoutPreview.this.undoView.isShown()) {
                ChatAttachAlertPhotoLayoutPreview.this.undoView.hide(true, 1);
            }
        }

        private void pushToGroup(PreviewGroupCell groupCell, MediaController.PhotoEntry photoEntry, int index) {
            groupCell.group.photos.add(Math.min(groupCell.group.photos.size(), index), photoEntry);
            if (groupCell.group.photos.size() == 11) {
                MediaController.PhotoEntry jumpPhoto = groupCell.group.photos.get(10);
                groupCell.group.photos.remove(10);
                int groupIndex = this.groupCells.indexOf(groupCell);
                if (groupIndex >= 0) {
                    PreviewGroupCell nextGroupCell = groupIndex + 1 == this.groupCells.size() ? null : this.groupCells.get(groupIndex + 1);
                    if (nextGroupCell == null) {
                        PreviewGroupCell nextGroupCell2 = new PreviewGroupCell();
                        ArrayList<MediaController.PhotoEntry> newPhotos = new ArrayList<>();
                        newPhotos.add(jumpPhoto);
                        nextGroupCell2.setGroup(new GroupCalculator(newPhotos), true);
                        invalidate();
                    } else {
                        pushToGroup(nextGroupCell, jumpPhoto, 0);
                    }
                }
            }
            groupCell.setGroup(groupCell.group, true);
        }

        private void updateGroups() {
            int groupCellsCount = this.groupCells.size();
            for (int i = 0; i < groupCellsCount; i++) {
                PreviewGroupCell groupCell = this.groupCells.get(i);
                if (groupCell.group.photos.size() < 10 && i < this.groupCells.size() - 1) {
                    int photosToTake = 10 - groupCell.group.photos.size();
                    PreviewGroupCell nextGroup = this.groupCells.get(i + 1);
                    ArrayList<MediaController.PhotoEntry> takenPhotos = new ArrayList<>();
                    int photosToTake2 = Math.min(photosToTake, nextGroup.group.photos.size());
                    for (int j = 0; j < photosToTake2; j++) {
                        takenPhotos.add(nextGroup.group.photos.remove(0));
                    }
                    groupCell.group.photos.addAll(takenPhotos);
                    groupCell.setGroup(groupCell.group, true);
                    nextGroup.setGroup(nextGroup.group, true);
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
            public boolean needToUpdate;
            final int padding;
            private float previousGroupHeight;
            private float previousGroupWidth;
            private float right;
            /* access modifiers changed from: private */
            public float top;
            private final long updateDuration;
            /* access modifiers changed from: private */
            public float width;
            public float y;

            private PreviewGroupCell() {
                this.y = 0.0f;
                this.indexStart = 0;
                this.updateDuration = 200;
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
                this.needToUpdate = false;
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
                private long lastVisibleTUpdate;
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
                private final long updateDuration;
                private Bitmap videoDurationBitmap;
                private String videoDurationBitmapText;
                private String videoDurationText;
                private TextPaint videoDurationTextPaint;
                private float visibleT;

                private MediaCell() {
                    this.groupCell = PreviewGroupCell.this;
                    this.fromRect = null;
                    this.rect = new RectF();
                    this.lastUpdate = 0;
                    this.updateDuration = 200;
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
                    this.visibleT = 1.0f;
                    this.lastVisibleTUpdate = 0;
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
                    if (this.image == null) {
                        this.image = new ImageReceiver(PreviewGroupsView.this);
                    }
                    if (photoEntry3 == null) {
                        return;
                    }
                    if (photoEntry3.thumbPath != null) {
                        this.image.setImage(ImageLocation.getForPath(photoEntry3.thumbPath), (String) null, (ImageLocation) null, (String) null, Theme.chat_attachEmptyDrawable, 0, (String) null, (Object) null, 0);
                    } else if (photoEntry3.path == null) {
                        this.image.setImageBitmap(Theme.chat_attachEmptyDrawable);
                    } else if (photoEntry3.isVideo) {
                        ImageReceiver imageReceiver = this.image;
                        imageReceiver.setImage(ImageLocation.getForPath("vthumb://" + photoEntry3.imageId + ":" + photoEntry3.path), (String) null, (ImageLocation) null, (String) null, Theme.chat_attachEmptyDrawable, 0, (String) null, (Object) null, 0);
                        this.image.setAllowStartAnimation(true);
                    } else {
                        this.image.setOrientation(photoEntry3.orientation, true);
                        ImageReceiver imageReceiver2 = this.image;
                        imageReceiver2.setImage(ImageLocation.getForPath("thumb://" + photoEntry3.imageId + ":" + photoEntry3.path), (String) null, (ImageLocation) null, (String) null, Theme.chat_attachEmptyDrawable, 0, (String) null, (Object) null, 0);
                    }
                }

                /* access modifiers changed from: private */
                public void layoutFrom(MediaCell fromCell) {
                    this.fromScale = AndroidUtilities.lerp(fromCell.fromScale, fromCell.scale, fromCell.getT());
                    if (this.fromRect == null) {
                        this.fromRect = new RectF();
                    }
                    RectF myRect = new RectF();
                    RectF rectF = this.fromRect;
                    if (rectF == null) {
                        myRect.set(this.rect);
                    } else {
                        AndroidUtilities.lerp(rectF, this.rect, getT(), myRect);
                    }
                    RectF rectF2 = fromCell.fromRect;
                    if (rectF2 != null) {
                        AndroidUtilities.lerp(rectF2, fromCell.rect, fromCell.getT(), this.fromRect);
                        this.fromRect.set(myRect.centerX() - (((this.fromRect.width() / 2.0f) * fromCell.groupCell.width) / PreviewGroupCell.this.width), myRect.centerY() - (((this.fromRect.height() / 2.0f) * fromCell.groupCell.height) / PreviewGroupCell.this.height), myRect.centerX() + (((this.fromRect.width() / 2.0f) * fromCell.groupCell.width) / PreviewGroupCell.this.width), myRect.centerY() + (((this.fromRect.height() / 2.0f) * fromCell.groupCell.height) / PreviewGroupCell.this.height));
                    } else {
                        this.fromRect.set(myRect.centerX() - (((fromCell.rect.width() / 2.0f) * fromCell.groupCell.width) / PreviewGroupCell.this.width), myRect.centerY() - (((fromCell.rect.height() / 2.0f) * fromCell.groupCell.height) / PreviewGroupCell.this.height), myRect.centerX() + (((fromCell.rect.width() / 2.0f) * fromCell.groupCell.width) / PreviewGroupCell.this.width), myRect.centerY() + (((fromCell.rect.height() / 2.0f) * fromCell.groupCell.height) / PreviewGroupCell.this.height));
                    }
                    this.fromScale = AndroidUtilities.lerp(this.fromScale, this.scale, getT());
                    this.lastUpdate = SystemClock.elapsedRealtime();
                }

                /* access modifiers changed from: private */
                public void layout(GroupCalculator group, MessageObject.GroupedMessagePosition pos, boolean animated) {
                    if (group != null && pos != null) {
                        this.positionFlags = pos.flags;
                        if (animated) {
                            float t = getT();
                            RectF rectF = this.fromRect;
                            if (rectF != null) {
                                AndroidUtilities.lerp(rectF, this.rect, t, rectF);
                            }
                            RectF rectF2 = this.fromRoundRadiuses;
                            if (rectF2 != null) {
                                AndroidUtilities.lerp(rectF2, this.roundRadiuses, t, rectF2);
                            }
                            this.fromScale = AndroidUtilities.lerp(this.fromScale, this.scale, t);
                            this.lastUpdate = SystemClock.elapsedRealtime();
                        }
                        float x = pos.left / ((float) group.width);
                        float y = pos.top / group.height;
                        float w = ((float) pos.pw) / ((float) group.width);
                        float h = pos.ph / group.height;
                        this.scale = 1.0f;
                        this.rect.set(x, y, x + w, y + h);
                        float r = (float) AndroidUtilities.dp(2.0f);
                        float R = (float) AndroidUtilities.dp((float) (SharedConfig.bubbleRadius - 1));
                        RectF rectF3 = this.roundRadiuses;
                        int i = this.positionFlags;
                        rectF3.set((i & 5) == 5 ? R : r, (i & 6) == 6 ? R : r, (i & 10) == 10 ? R : r, (i & 9) == 9 ? R : r);
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
                    } else if (animated) {
                        long now = SystemClock.elapsedRealtime();
                        this.fromScale = AndroidUtilities.lerp(this.fromScale, this.scale, getT());
                        RectF rectF6 = this.fromRect;
                        if (rectF6 != null) {
                            AndroidUtilities.lerp(rectF6, this.rect, getT(), this.fromRect);
                        }
                        this.scale = 0.0f;
                        this.lastUpdate = now;
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
                    MediaCell newMediaCell = new MediaCell();
                    newMediaCell.rect.set(this.rect);
                    newMediaCell.image = this.image;
                    newMediaCell.photoEntry = this.photoEntry;
                    return newMediaCell;
                }

                public RectF rect() {
                    return rect(getT());
                }

                public RectF rect(float t) {
                    if (this.rect == null || this.image == null) {
                        this.tempRect.set(0.0f, 0.0f, 0.0f, 0.0f);
                        return this.tempRect;
                    }
                    float x = PreviewGroupCell.this.left + (this.rect.left * PreviewGroupCell.this.width);
                    float y = PreviewGroupCell.this.top + (this.rect.top * PreviewGroupCell.this.height);
                    float w = this.rect.width() * PreviewGroupCell.this.width;
                    float h = this.rect.height() * PreviewGroupCell.this.height;
                    if (t < 1.0f && this.fromRect != null) {
                        x = AndroidUtilities.lerp(PreviewGroupCell.this.left + (this.fromRect.left * PreviewGroupCell.this.width), x, t);
                        y = AndroidUtilities.lerp(PreviewGroupCell.this.top + (this.fromRect.top * PreviewGroupCell.this.height), y, t);
                        w = AndroidUtilities.lerp(this.fromRect.width() * PreviewGroupCell.this.width, w, t);
                        h = AndroidUtilities.lerp(this.fromRect.height() * PreviewGroupCell.this.height, h, t);
                    }
                    if ((this.positionFlags & 4) == 0) {
                        y += (float) PreviewGroupCell.this.halfGap;
                        h -= (float) PreviewGroupCell.this.halfGap;
                    }
                    if ((this.positionFlags & 8) == 0) {
                        h -= (float) PreviewGroupCell.this.halfGap;
                    }
                    if ((this.positionFlags & 1) == 0) {
                        x += (float) PreviewGroupCell.this.halfGap;
                        w -= (float) PreviewGroupCell.this.halfGap;
                    }
                    if ((this.positionFlags & 2) == 0) {
                        w -= (float) PreviewGroupCell.this.halfGap;
                    }
                    this.tempRect.set(x, y, x + w, y + h);
                    return this.tempRect;
                }

                public RectF drawingRect() {
                    float dragging = 0.0f;
                    if (this.rect == null || this.image == null) {
                        this.tempRect.set(0.0f, 0.0f, 0.0f, 0.0f);
                        return this.tempRect;
                    }
                    if (ChatAttachAlertPhotoLayoutPreview.this.draggingCell != null && ChatAttachAlertPhotoLayoutPreview.this.draggingCell.photoEntry == this.photoEntry) {
                        dragging = PreviewGroupsView.this.draggingT;
                    }
                    float scale2 = AndroidUtilities.lerp(this.fromScale, this.scale, getT()) * (((1.0f - dragging) * 0.2f) + 0.8f);
                    RectF myRect = rect();
                    myRect.set(myRect.left + ((myRect.width() * (1.0f - scale2)) / 2.0f), myRect.top + ((myRect.height() * (1.0f - scale2)) / 2.0f), myRect.left + ((myRect.width() * (scale2 + 1.0f)) / 2.0f), myRect.top + ((myRect.height() * (1.0f + scale2)) / 2.0f));
                    return myRect;
                }

                private void drawPhotoIndex(Canvas canvas, float top, float right, String indexText, float scale2, float alpha) {
                    float textSize;
                    String str;
                    String str2 = indexText;
                    int radius = AndroidUtilities.dp(12.0f);
                    int strokeWidth = AndroidUtilities.dp(1.2f);
                    int sz = (radius + strokeWidth) * 2;
                    int pad = strokeWidth * 4;
                    if (str2 != null && (this.indexBitmap == null || (str = this.indexBitmapText) == null || !str.equals(str2))) {
                        if (this.indexBitmap == null) {
                            this.indexBitmap = Bitmap.createBitmap(sz, sz, Bitmap.Config.ARGB_8888);
                        }
                        Canvas bitmapCanvas = new Canvas(this.indexBitmap);
                        bitmapCanvas.drawColor(0);
                        if (this.textPaint == null) {
                            TextPaint textPaint2 = new TextPaint(1);
                            this.textPaint = textPaint2;
                            textPaint2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        }
                        this.textPaint.setColor(ChatAttachAlertPhotoLayoutPreview.this.getThemedColor("chat_attachCheckBoxCheck"));
                        switch (indexText.length()) {
                            case 0:
                            case 1:
                            case 2:
                                textSize = 14.0f;
                                break;
                            case 3:
                                textSize = 10.0f;
                                break;
                            default:
                                textSize = 8.0f;
                                break;
                        }
                        this.textPaint.setTextSize((float) AndroidUtilities.dp(textSize));
                        float cx = ((float) sz) / 2.0f;
                        float cy = ((float) sz) / 2.0f;
                        this.paint.setColor(ChatAttachAlertPhotoLayoutPreview.this.getThemedColor("chat_attachCheckBoxBackground"));
                        bitmapCanvas.drawCircle((float) ((int) cx), (float) ((int) cy), (float) radius, this.paint);
                        this.strokePaint.setColor(AndroidUtilities.getOffsetColor(-1, ChatAttachAlertPhotoLayoutPreview.this.getThemedColor("chat_attachCheckBoxCheck"), 1.0f, 1.0f));
                        this.strokePaint.setStyle(Paint.Style.STROKE);
                        this.strokePaint.setStrokeWidth((float) strokeWidth);
                        bitmapCanvas.drawCircle((float) ((int) cx), (float) ((int) cy), (float) radius, this.strokePaint);
                        bitmapCanvas.drawText(str2, cx - (this.textPaint.measureText(str2) / 2.0f), ((float) AndroidUtilities.dp(1.0f)) + cy + ((float) AndroidUtilities.dp(textSize / 4.0f)), this.textPaint);
                        this.indexIn.set(0, 0, sz, sz);
                        this.indexBitmapText = str2;
                    }
                    if (this.indexBitmap != null) {
                        this.indexOut.set((int) ((right - (((float) sz) * scale2)) + ((float) pad)), (int) (top - ((float) pad)), (int) (right + ((float) pad)), (int) ((top - ((float) pad)) + (((float) sz) * scale2)));
                        this.bitmapPaint.setAlpha((int) (255.0f * alpha));
                        canvas.drawBitmap(this.indexBitmap, this.indexIn, this.indexOut, this.bitmapPaint);
                        return;
                    }
                    Canvas canvas2 = canvas;
                }

                private void drawDuration(Canvas canvas, float left, float bottom, String durationText, float scale2, float alpha) {
                    String str;
                    float f = left;
                    float f2 = bottom;
                    String str2 = durationText;
                    if (str2 != null) {
                        if (this.videoDurationBitmap == null || (str = this.videoDurationBitmapText) == null || !str.equals(str2)) {
                            if (this.videoDurationTextPaint == null) {
                                TextPaint textPaint2 = new TextPaint(1);
                                this.videoDurationTextPaint = textPaint2;
                                textPaint2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                                this.videoDurationTextPaint.setColor(-1);
                            }
                            float textSize = (float) AndroidUtilities.dp(12.0f);
                            this.videoDurationTextPaint.setTextSize(textSize);
                            float width = ((float) ChatAttachAlertPhotoLayoutPreview.this.videoPlayImage.getIntrinsicWidth()) + this.videoDurationTextPaint.measureText(str2) + ((float) AndroidUtilities.dp(15.0f));
                            float height = Math.max(textSize, (float) (ChatAttachAlertPhotoLayoutPreview.this.videoPlayImage.getIntrinsicHeight() + AndroidUtilities.dp(4.0f)));
                            int w = (int) Math.ceil((double) width);
                            int h = (int) Math.ceil((double) height);
                            Bitmap bitmap = this.videoDurationBitmap;
                            if (!(bitmap != null && bitmap.getWidth() == w && this.videoDurationBitmap.getHeight() == h)) {
                                Bitmap bitmap2 = this.videoDurationBitmap;
                                if (bitmap2 != null) {
                                    bitmap2.recycle();
                                }
                                this.videoDurationBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                            }
                            Canvas bitmapCanvas = new Canvas(this.videoDurationBitmap);
                            AndroidUtilities.rectTmp.set(0.0f, 0.0f, width, height);
                            bitmapCanvas.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_timeBackgroundPaint);
                            int imageLeft = AndroidUtilities.dp(5.0f);
                            int imageTop = (int) ((height - ((float) ChatAttachAlertPhotoLayoutPreview.this.videoPlayImage.getIntrinsicHeight())) / 2.0f);
                            ChatAttachAlertPhotoLayoutPreview.this.videoPlayImage.setBounds(imageLeft, imageTop, ChatAttachAlertPhotoLayoutPreview.this.videoPlayImage.getIntrinsicWidth() + imageLeft, ChatAttachAlertPhotoLayoutPreview.this.videoPlayImage.getIntrinsicHeight() + imageTop);
                            ChatAttachAlertPhotoLayoutPreview.this.videoPlayImage.draw(bitmapCanvas);
                            bitmapCanvas.drawText(str2, (float) AndroidUtilities.dp(18.0f), ((float) AndroidUtilities.dp(-0.7f)) + textSize, this.videoDurationTextPaint);
                            this.durationIn.set(0, 0, w, h);
                            this.videoDurationBitmapText = str2;
                        }
                        int w2 = this.videoDurationBitmap.getWidth();
                        this.durationOut.set((int) f, (int) (f2 - (((float) this.videoDurationBitmap.getHeight()) * scale2)), (int) ((((float) w2) * scale2) + f), (int) f2);
                        this.bitmapPaint.setAlpha((int) (255.0f * alpha));
                        canvas.drawBitmap(this.videoDurationBitmap, this.durationIn, this.durationOut, this.bitmapPaint);
                        return;
                    }
                    Canvas canvas2 = canvas;
                }

                public boolean draw(Canvas canvas) {
                    return draw(canvas, false);
                }

                public boolean draw(Canvas canvas, boolean ignoreBounds) {
                    return draw(canvas, getT(), ignoreBounds);
                }

                public boolean draw(Canvas canvas, float t, boolean ignoreBounds) {
                    String str;
                    float bl;
                    float br;
                    RectF rectF;
                    Canvas canvas2 = canvas;
                    float f = t;
                    if (this.rect == null || this.image == null) {
                        return false;
                    }
                    float dragging = ChatAttachAlertPhotoLayoutPreview.this.draggingCell == this ? PreviewGroupsView.this.draggingT : 0.0f;
                    float scale2 = AndroidUtilities.lerp(this.fromScale, this.scale, f);
                    if (scale2 <= 0.0f) {
                        return false;
                    }
                    RectF drawingRect = drawingRect();
                    float R = (float) AndroidUtilities.dp((float) (SharedConfig.bubbleRadius - 1));
                    float tl = this.roundRadiuses.left;
                    float tr = this.roundRadiuses.top;
                    float br2 = this.roundRadiuses.right;
                    float bl2 = this.roundRadiuses.bottom;
                    if (f < 1.0f && (rectF = this.fromRoundRadiuses) != null) {
                        tl = AndroidUtilities.lerp(rectF.left, tl, f);
                        tr = AndroidUtilities.lerp(this.fromRoundRadiuses.top, tr, f);
                        br2 = AndroidUtilities.lerp(this.fromRoundRadiuses.right, br2, f);
                        bl2 = AndroidUtilities.lerp(this.fromRoundRadiuses.bottom, bl2, f);
                    }
                    float tl2 = AndroidUtilities.lerp(tl, R, dragging);
                    float tr2 = AndroidUtilities.lerp(tr, R, dragging);
                    float br3 = AndroidUtilities.lerp(br2, R, dragging);
                    float bl3 = AndroidUtilities.lerp(bl2, R, dragging);
                    if (ignoreBounds) {
                        canvas.save();
                        canvas2.translate(-drawingRect.centerX(), -drawingRect.centerY());
                    }
                    this.image.setRoundRadius((int) tl2, (int) tr2, (int) br3, (int) bl3);
                    this.image.setImageCoords(drawingRect.left, drawingRect.top, drawingRect.width(), drawingRect.height());
                    this.image.setAlpha(scale2);
                    this.image.draw(canvas2);
                    int index = PreviewGroupCell.this.indexStart + PreviewGroupCell.this.group.photos.indexOf(this.photoEntry);
                    if (index >= 0) {
                        str = (index + 1) + "";
                    } else {
                        str = null;
                    }
                    String indexText = str;
                    float shouldVisibleT = this.image.getVisible() ? 1.0f : 0.0f;
                    boolean z = Math.abs(this.visibleT - shouldVisibleT) > 0.01f;
                    boolean needVisibleTUpdate = z;
                    if (z) {
                        bl = bl3;
                        br = br3;
                        long tx = Math.min(17, SystemClock.elapsedRealtime() - this.lastVisibleTUpdate);
                        this.lastVisibleTUpdate = SystemClock.elapsedRealtime();
                        float upd = ((float) tx) / 100.0f;
                        float f2 = this.visibleT;
                        if (shouldVisibleT < f2) {
                            long j = tx;
                            this.visibleT = Math.max(0.0f, f2 - upd);
                        } else {
                            this.visibleT = Math.min(1.0f, f2 + upd);
                        }
                    } else {
                        bl = bl3;
                        br = br3;
                    }
                    float f3 = bl;
                    float f4 = br;
                    float f5 = tr2;
                    float f6 = tl2;
                    drawPhotoIndex(canvas, ((float) AndroidUtilities.dp(10.0f)) + drawingRect.top, drawingRect.right - ((float) AndroidUtilities.dp(10.0f)), indexText, scale2, scale2 * this.visibleT);
                    float f7 = R;
                    RectF rectF2 = drawingRect;
                    float f8 = scale2;
                    float f9 = dragging;
                    drawDuration(canvas, drawingRect.left + ((float) AndroidUtilities.dp(4.0f)), drawingRect.bottom - ((float) AndroidUtilities.dp(4.0f)), this.videoDurationText, scale2, this.visibleT * scale2);
                    if (ignoreBounds) {
                        canvas.restore();
                    }
                    if (f < 1.0f || needVisibleTUpdate) {
                        return true;
                    }
                    return false;
                }
            }

            /* access modifiers changed from: private */
            public void setGroup(GroupCalculator group2, boolean animated) {
                GroupCalculator groupCalculator = group2;
                boolean z = animated;
                this.group = groupCalculator;
                if (groupCalculator != null) {
                    group2.calculate();
                    long now = SystemClock.elapsedRealtime();
                    long j = this.lastMediaUpdate;
                    if (now - j < 200) {
                        float t = ((float) (now - j)) / 200.0f;
                        this.previousGroupHeight = AndroidUtilities.lerp(this.previousGroupHeight, this.groupHeight, t);
                        this.previousGroupWidth = AndroidUtilities.lerp(this.previousGroupWidth, this.groupWidth, t);
                    } else {
                        this.previousGroupHeight = this.groupHeight;
                        this.previousGroupWidth = this.groupWidth;
                    }
                    this.groupWidth = ((float) groupCalculator.width) / 1000.0f;
                    this.groupHeight = groupCalculator.height;
                    this.lastMediaUpdate = z ? now : 0;
                    List<MediaController.PhotoEntry> photoEntries = new ArrayList<>(groupCalculator.positions.keySet());
                    int photoEntriesCount = photoEntries.size();
                    for (int j2 = 0; j2 < photoEntriesCount; j2++) {
                        MediaController.PhotoEntry photoEntry = photoEntries.get(j2);
                        MessageObject.GroupedMessagePosition pos = groupCalculator.positions.get(photoEntry);
                        MediaCell properCell = null;
                        int mediaCount = this.media.size();
                        int i = 0;
                        while (true) {
                            if (i >= mediaCount) {
                                break;
                            }
                            MediaCell cell = this.media.get(i);
                            if (cell.photoEntry == photoEntry) {
                                properCell = cell;
                                break;
                            }
                            i++;
                        }
                        if (properCell == null) {
                            MediaCell properCell2 = new MediaCell();
                            properCell2.setImage(photoEntry);
                            properCell2.layout(groupCalculator, pos, z);
                            this.media.add(properCell2);
                        } else {
                            properCell.layout(groupCalculator, pos, z);
                        }
                    }
                    int mediaCount2 = this.media.size();
                    int i2 = 0;
                    while (i2 < mediaCount2) {
                        MediaCell cell2 = this.media.get(i2);
                        if (!groupCalculator.positions.containsKey(cell2.photoEntry)) {
                            if (cell2.scale <= 0.0f) {
                                if (cell2.lastUpdate + 200 <= now) {
                                    this.media.remove(i2);
                                    i2--;
                                    mediaCount2--;
                                }
                            }
                            cell2.layout((GroupCalculator) null, (MessageObject.GroupedMessagePosition) null, z);
                        }
                        i2++;
                    }
                    PreviewGroupsView.this.invalidate();
                }
            }

            public float getT() {
                return this.interpolator.getInterpolation(Math.min(1.0f, ((float) (SystemClock.elapsedRealtime() - this.lastMediaUpdate)) / 200.0f));
            }

            public float measure() {
                return AndroidUtilities.lerp(this.previousGroupHeight, this.groupHeight, getT()) * ((float) Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.5f * ChatAttachAlertPhotoLayoutPreview.this.getPreviewScale();
            }

            public float maxHeight() {
                return getT() >= 0.95f ? this.groupHeight * ((float) Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.5f * ChatAttachAlertPhotoLayoutPreview.this.getPreviewScale() : measure();
            }

            public void invalidate() {
                this.needToUpdate = true;
            }

            public boolean draw(Canvas canvas) {
                Canvas canvas2 = canvas;
                boolean update = false;
                float t = this.interpolator.getInterpolation(Math.min(1.0f, ((float) (SystemClock.elapsedRealtime() - this.lastMediaUpdate)) / 200.0f));
                if (t < 1.0f) {
                    update = true;
                }
                float groupWidth2 = AndroidUtilities.lerp(this.previousGroupWidth, this.groupWidth, t) * ((float) PreviewGroupsView.this.getWidth()) * ChatAttachAlertPhotoLayoutPreview.this.getPreviewScale();
                float groupHeight2 = AndroidUtilities.lerp(this.previousGroupHeight, this.groupHeight, t) * ((float) Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.5f * ChatAttachAlertPhotoLayoutPreview.this.getPreviewScale();
                if (this.messageBackground != null) {
                    this.top = 0.0f;
                    this.left = (((float) PreviewGroupsView.this.getWidth()) - Math.max((float) this.padding, groupWidth2)) / 2.0f;
                    this.right = (((float) PreviewGroupsView.this.getWidth()) + Math.max((float) this.padding, groupWidth2)) / 2.0f;
                    this.bottom = Math.max((float) (this.padding * 2), groupHeight2);
                    this.messageBackground.setTop(0, (int) groupWidth2, (int) groupHeight2, 0, 0, 0, false, false);
                    this.messageBackground.setBounds((int) this.left, (int) this.top, (int) this.right, (int) this.bottom);
                    float alpha = 1.0f;
                    if (this.groupWidth <= 0.0f) {
                        alpha = 1.0f - t;
                    } else if (this.previousGroupWidth <= 0.0f) {
                        alpha = t;
                    }
                    this.messageBackground.setAlpha((int) (255.0f * alpha));
                    this.messageBackground.drawCached(canvas2, this.backgroundCacheParams);
                    float f = this.top;
                    int i = this.padding;
                    this.top = f + ((float) i);
                    this.left += (float) i;
                    this.bottom -= (float) i;
                    this.right -= (float) i;
                }
                this.width = this.right - this.left;
                this.height = this.bottom - this.top;
                int count = this.media.size();
                for (int i2 = 0; i2 < count; i2++) {
                    MediaCell cell = this.media.get(i2);
                    if (cell != null && ((ChatAttachAlertPhotoLayoutPreview.this.draggingCell == null || ChatAttachAlertPhotoLayoutPreview.this.draggingCell.photoEntry != cell.photoEntry) && cell.draw(canvas2))) {
                        update = true;
                    }
                }
                return update;
            }
        }
    }

    public Drawable getThemedDrawable(String drawableKey) {
        Drawable drawable = this.themeDelegate.getDrawable(drawableKey);
        return drawable != null ? drawable : Theme.getThemeDrawable(drawableKey);
    }
}
