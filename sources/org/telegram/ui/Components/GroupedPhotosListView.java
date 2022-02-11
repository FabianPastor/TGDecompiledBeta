package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$PageBlock;
import org.telegram.tgnet.TLRPC$PhotoSize;

public class GroupedPhotosListView extends View implements GestureDetector.OnGestureListener {
    private boolean animateAllLine;
    private boolean animateBackground = true;
    private int animateToDX;
    private int animateToDXStart;
    private int animateToItem = -1;
    private boolean animateToItemFast;
    private boolean animationsEnabled = true;
    private Paint backgroundPaint = new Paint();
    private long currentGroupId;
    private int currentImage;
    private float currentItemProgress = 1.0f;
    private ArrayList<Object> currentObjects = new ArrayList<>();
    public ArrayList<ImageLocation> currentPhotos = new ArrayList<>();
    /* access modifiers changed from: private */
    public GroupedPhotosListViewDelegate delegate;
    private float drawAlpha;
    private int drawDx;
    private GestureDetector gestureDetector;
    private boolean hasPhotos;
    /* access modifiers changed from: private */
    public ValueAnimator hideAnimator;
    private boolean ignoreChanges;
    private ArrayList<ImageReceiver> imagesToDraw = new ArrayList<>();
    private int itemHeight;
    private int itemSpacing;
    private int itemWidth;
    private int itemY;
    private long lastUpdateTime;
    private float moveLineProgress;
    private boolean moving;
    private int nextImage;
    private float nextItemProgress = 0.0f;
    private int nextPhotoScrolling = -1;
    private Scroller scroll;
    private boolean scrolling;
    /* access modifiers changed from: private */
    public ValueAnimator showAnimator;
    private boolean stopedScrolling;
    private ArrayList<ImageReceiver> unusedReceivers = new ArrayList<>();

    public interface GroupedPhotosListViewDelegate {
        long getAvatarsDialogId();

        int getCurrentAccount();

        int getCurrentIndex();

        ArrayList<MessageObject> getImagesArr();

        ArrayList<ImageLocation> getImagesArrLocations();

        List<TLRPC$PageBlock> getPageBlockArr();

        Object getParentObject();

        int getSlideshowMessageId();

        void onShowAnimationStart();

        void onStopScrolling();

        void setCurrentIndex(int i);
    }

    public void onLongPress(MotionEvent motionEvent) {
    }

    public void onShowPress(MotionEvent motionEvent) {
    }

    public GroupedPhotosListView(Context context, int i) {
        super(context);
        this.gestureDetector = new GestureDetector(context, this);
        this.scroll = new Scroller(context);
        this.itemWidth = AndroidUtilities.dp(42.0f);
        this.itemHeight = AndroidUtilities.dp(56.0f);
        this.itemSpacing = AndroidUtilities.dp(1.0f);
        this.itemY = i;
        this.backgroundPaint.setColor(NUM);
    }

    public void clear() {
        this.currentPhotos.clear();
        this.currentObjects.clear();
        this.imagesToDraw.clear();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v0, resolved type: org.telegram.messenger.ImageLocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v4, resolved type: org.telegram.messenger.ImageLocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v5, resolved type: org.telegram.messenger.ImageLocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v11, resolved type: org.telegram.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v0, resolved type: org.telegram.messenger.ImageLocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v12, resolved type: org.telegram.messenger.ImageLocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v1, resolved type: org.telegram.messenger.ImageLocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v2, resolved type: org.telegram.messenger.ImageLocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v3, resolved type: org.telegram.messenger.ImageLocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v4, resolved type: org.telegram.messenger.ImageLocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v17, resolved type: org.telegram.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v5, resolved type: org.telegram.messenger.ImageLocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v6, resolved type: org.telegram.messenger.ImageLocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v22, resolved type: org.telegram.messenger.ImageLocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v23, resolved type: org.telegram.tgnet.TLRPC$PageBlock} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v24, resolved type: org.telegram.tgnet.TLRPC$PageBlock} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v25, resolved type: org.telegram.messenger.ImageLocation} */
    /* JADX WARNING: type inference failed for: r11v13 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x025a  */
    /* JADX WARNING: Removed duplicated region for block: B:191:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void fillList() {
        /*
            r20 = this;
            r0 = r20
            boolean r1 = r0.ignoreChanges
            r2 = 0
            if (r1 == 0) goto L_0x000a
            r0.ignoreChanges = r2
            return
        L_0x000a:
            org.telegram.ui.Components.GroupedPhotosListView$GroupedPhotosListViewDelegate r1 = r0.delegate
            int r1 = r1.getCurrentIndex()
            org.telegram.ui.Components.GroupedPhotosListView$GroupedPhotosListViewDelegate r3 = r0.delegate
            java.util.ArrayList r3 = r3.getImagesArrLocations()
            org.telegram.ui.Components.GroupedPhotosListView$GroupedPhotosListViewDelegate r4 = r0.delegate
            java.util.ArrayList r4 = r4.getImagesArr()
            org.telegram.ui.Components.GroupedPhotosListView$GroupedPhotosListViewDelegate r5 = r0.delegate
            java.util.List r5 = r5.getPageBlockArr()
            org.telegram.ui.Components.GroupedPhotosListView$GroupedPhotosListViewDelegate r6 = r0.delegate
            int r6 = r6.getSlideshowMessageId()
            org.telegram.ui.Components.GroupedPhotosListView$GroupedPhotosListViewDelegate r7 = r0.delegate
            r7.getCurrentAccount()
            r0.hasPhotos = r2
            r7 = 0
            r8 = 0
            r10 = 1
            if (r3 == 0) goto L_0x0058
            boolean r11 = r3.isEmpty()
            if (r11 != 0) goto L_0x0058
            int r11 = r3.size()
            if (r1 < r11) goto L_0x0046
            int r1 = r3.size()
            int r1 = r1 - r10
        L_0x0046:
            java.lang.Object r11 = r3.get(r1)
            org.telegram.messenger.ImageLocation r11 = (org.telegram.messenger.ImageLocation) r11
            int r12 = r3.size()
            r0.hasPhotos = r10
            r17 = r3
            r15 = r12
            r12 = 0
            goto L_0x014f
        L_0x0058:
            if (r4 == 0) goto L_0x00eb
            boolean r11 = r4.isEmpty()
            if (r11 != 0) goto L_0x00eb
            int r11 = r4.size()
            if (r1 < r11) goto L_0x006b
            int r1 = r4.size()
            int r1 = r1 - r10
        L_0x006b:
            java.lang.Object r11 = r4.get(r1)
            org.telegram.messenger.MessageObject r11 = (org.telegram.messenger.MessageObject) r11
            long r12 = r11.getGroupIdForUse()
            long r14 = r0.currentGroupId
            int r16 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r16 == 0) goto L_0x007f
            r0.currentGroupId = r12
            r12 = 1
            goto L_0x0080
        L_0x007f:
            r12 = 0
        L_0x0080:
            long r13 = r0.currentGroupId
            int r15 = (r13 > r8 ? 1 : (r13 == r8 ? 0 : -1))
            if (r15 == 0) goto L_0x00e3
            r0.hasPhotos = r10
            int r13 = r1 + 10
            int r14 = r4.size()
            int r13 = java.lang.Math.min(r13, r14)
            r14 = r1
            r15 = 0
        L_0x0094:
            if (r14 >= r13) goto L_0x00b5
            java.lang.Object r16 = r4.get(r14)
            org.telegram.messenger.MessageObject r16 = (org.telegram.messenger.MessageObject) r16
            if (r6 != 0) goto L_0x00ab
            long r16 = r16.getGroupIdForUse()
            r18 = r11
            long r10 = r0.currentGroupId
            int r19 = (r16 > r10 ? 1 : (r16 == r10 ? 0 : -1))
            if (r19 != 0) goto L_0x00b7
            goto L_0x00ad
        L_0x00ab:
            r18 = r11
        L_0x00ad:
            int r15 = r15 + 1
            int r14 = r14 + 1
            r11 = r18
            r10 = 1
            goto L_0x0094
        L_0x00b5:
            r18 = r11
        L_0x00b7:
            int r10 = r1 + -10
            int r10 = java.lang.Math.max(r10, r2)
            int r11 = r1 + -1
        L_0x00bf:
            if (r11 < r10) goto L_0x00e0
            java.lang.Object r13 = r4.get(r11)
            org.telegram.messenger.MessageObject r13 = (org.telegram.messenger.MessageObject) r13
            if (r6 != 0) goto L_0x00d6
            long r13 = r13.getGroupIdForUse()
            r17 = r3
            long r2 = r0.currentGroupId
            int r19 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1))
            if (r19 != 0) goto L_0x00e8
            goto L_0x00d8
        L_0x00d6:
            r17 = r3
        L_0x00d8:
            int r15 = r15 + 1
            int r11 = r11 + -1
            r3 = r17
            r2 = 0
            goto L_0x00bf
        L_0x00e0:
            r17 = r3
            goto L_0x00e8
        L_0x00e3:
            r17 = r3
            r18 = r11
            r15 = 0
        L_0x00e8:
            r11 = r18
            goto L_0x014f
        L_0x00eb:
            r17 = r3
            if (r5 == 0) goto L_0x014c
            boolean r2 = r5.isEmpty()
            if (r2 != 0) goto L_0x014c
            java.lang.Object r2 = r5.get(r1)
            r11 = r2
            org.telegram.tgnet.TLRPC$PageBlock r11 = (org.telegram.tgnet.TLRPC$PageBlock) r11
            int r2 = r11.groupId
            long r12 = (long) r2
            long r14 = r0.currentGroupId
            int r3 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r3 == 0) goto L_0x010a
            long r2 = (long) r2
            r0.currentGroupId = r2
            r12 = 1
            goto L_0x010b
        L_0x010a:
            r12 = 0
        L_0x010b:
            long r2 = r0.currentGroupId
            int r10 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r10 == 0) goto L_0x014e
            r2 = 1
            r0.hasPhotos = r2
            int r2 = r5.size()
            r10 = r1
            r3 = 0
        L_0x011a:
            if (r10 >= r2) goto L_0x0132
            java.lang.Object r13 = r5.get(r10)
            org.telegram.tgnet.TLRPC$PageBlock r13 = (org.telegram.tgnet.TLRPC$PageBlock) r13
            int r13 = r13.groupId
            long r13 = (long) r13
            long r8 = r0.currentGroupId
            int r15 = (r13 > r8 ? 1 : (r13 == r8 ? 0 : -1))
            if (r15 != 0) goto L_0x0132
            int r3 = r3 + 1
            int r10 = r10 + 1
            r8 = 0
            goto L_0x011a
        L_0x0132:
            int r2 = r1 + -1
        L_0x0134:
            if (r2 < 0) goto L_0x014a
            java.lang.Object r8 = r5.get(r2)
            org.telegram.tgnet.TLRPC$PageBlock r8 = (org.telegram.tgnet.TLRPC$PageBlock) r8
            int r8 = r8.groupId
            long r8 = (long) r8
            long r13 = r0.currentGroupId
            int r10 = (r8 > r13 ? 1 : (r8 == r13 ? 0 : -1))
            if (r10 != 0) goto L_0x014a
            int r3 = r3 + 1
            int r2 = r2 + -1
            goto L_0x0134
        L_0x014a:
            r15 = r3
            goto L_0x014f
        L_0x014c:
            r11 = r7
            r12 = 0
        L_0x014e:
            r15 = 0
        L_0x014f:
            if (r11 != 0) goto L_0x0152
            return
        L_0x0152:
            boolean r2 = r0.animationsEnabled
            if (r2 == 0) goto L_0x01eb
            boolean r2 = r0.hasPhotos
            r3 = 1128792064(0x43480000, float:200.0)
            r8 = 2
            if (r2 != 0) goto L_0x01ab
            android.animation.ValueAnimator r2 = r0.showAnimator
            if (r2 == 0) goto L_0x0166
            r2.cancel()
            r0.showAnimator = r7
        L_0x0166:
            float r2 = r0.drawAlpha
            r7 = 0
            int r2 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r2 <= 0) goto L_0x01eb
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r2 = r0.currentPhotos
            int r2 = r2.size()
            r9 = 1
            if (r2 <= r9) goto L_0x01eb
            android.animation.ValueAnimator r1 = r0.hideAnimator
            if (r1 != 0) goto L_0x01aa
            float[] r1 = new float[r8]
            float r2 = r0.drawAlpha
            r4 = 0
            r1[r4] = r2
            r1[r9] = r7
            android.animation.ValueAnimator r1 = android.animation.ValueAnimator.ofFloat(r1)
            r0.hideAnimator = r1
            float r2 = r0.drawAlpha
            float r2 = r2 * r3
            long r2 = (long) r2
            r1.setDuration(r2)
            android.animation.ValueAnimator r1 = r0.hideAnimator
            org.telegram.ui.Components.GroupedPhotosListView$1 r2 = new org.telegram.ui.Components.GroupedPhotosListView$1
            r2.<init>()
            r1.addListener(r2)
            android.animation.ValueAnimator r1 = r0.hideAnimator
            org.telegram.ui.Components.GroupedPhotosListView$$ExternalSyntheticLambda0 r2 = new org.telegram.ui.Components.GroupedPhotosListView$$ExternalSyntheticLambda0
            r2.<init>(r0)
            r1.addUpdateListener(r2)
            android.animation.ValueAnimator r1 = r0.hideAnimator
            r1.start()
        L_0x01aa:
            return
        L_0x01ab:
            android.animation.ValueAnimator r2 = r0.hideAnimator
            if (r2 == 0) goto L_0x01b4
            r0.hideAnimator = r7
            r2.cancel()
        L_0x01b4:
            float r2 = r0.drawAlpha
            r7 = 1065353216(0x3var_, float:1.0)
            int r9 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r9 >= 0) goto L_0x01eb
            android.animation.ValueAnimator r9 = r0.showAnimator
            if (r9 != 0) goto L_0x01eb
            float[] r8 = new float[r8]
            r9 = 0
            r8[r9] = r2
            r2 = 1
            r8[r2] = r7
            android.animation.ValueAnimator r2 = android.animation.ValueAnimator.ofFloat(r8)
            r0.showAnimator = r2
            float r8 = r0.drawAlpha
            float r7 = r7 - r8
            float r7 = r7 * r3
            long r7 = (long) r7
            r2.setDuration(r7)
            android.animation.ValueAnimator r2 = r0.showAnimator
            org.telegram.ui.Components.GroupedPhotosListView$2 r3 = new org.telegram.ui.Components.GroupedPhotosListView$2
            r3.<init>()
            r2.addListener(r3)
            android.animation.ValueAnimator r2 = r0.showAnimator
            org.telegram.ui.Components.GroupedPhotosListView$$ExternalSyntheticLambda1 r3 = new org.telegram.ui.Components.GroupedPhotosListView$$ExternalSyntheticLambda1
            r3.<init>(r0)
            r2.addUpdateListener(r3)
        L_0x01eb:
            r2 = -1
            if (r12 != 0) goto L_0x0257
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r3 = r0.currentPhotos
            int r3 = r3.size()
            if (r15 != r3) goto L_0x0254
            java.util.ArrayList<java.lang.Object> r3 = r0.currentObjects
            boolean r3 = r3.contains(r11)
            if (r3 != 0) goto L_0x01ff
            goto L_0x0254
        L_0x01ff:
            java.util.ArrayList<java.lang.Object> r3 = r0.currentObjects
            int r3 = r3.indexOf(r11)
            int r7 = r0.currentImage
            if (r7 == r3) goto L_0x0257
            if (r3 == r2) goto L_0x0257
            boolean r8 = r0.animateAllLine
            if (r8 != 0) goto L_0x021f
            boolean r9 = r0.moving
            if (r9 != 0) goto L_0x021f
            int r9 = r7 + -1
            if (r3 == r9) goto L_0x021b
            int r9 = r7 + 1
            if (r3 != r9) goto L_0x021f
        L_0x021b:
            r8 = 1
            r0.animateToItemFast = r8
            r8 = 1
        L_0x021f:
            if (r8 == 0) goto L_0x0240
            r0.animateToItem = r3
            r0.nextImage = r3
            int r7 = r7 - r3
            int r3 = r0.itemWidth
            int r8 = r0.itemSpacing
            int r3 = r3 + r8
            int r7 = r7 * r3
            r0.animateToDX = r7
            r8 = 1
            r0.moving = r8
            r3 = 0
            r0.animateAllLine = r3
            long r9 = java.lang.System.currentTimeMillis()
            r0.lastUpdateTime = r9
            r20.invalidate()
            r3 = 0
            goto L_0x0251
        L_0x0240:
            r8 = 1
            int r7 = r7 - r3
            int r9 = r0.itemWidth
            int r10 = r0.itemSpacing
            int r9 = r9 + r10
            int r7 = r7 * r9
            r0.fillImages(r8, r7)
            r0.currentImage = r3
            r3 = 0
            r0.moving = r3
        L_0x0251:
            r0.drawDx = r3
            goto L_0x0258
        L_0x0254:
            r3 = 0
            r12 = 1
            goto L_0x0258
        L_0x0257:
            r3 = 0
        L_0x0258:
            if (r12 == 0) goto L_0x03ac
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r7 = r0.currentPhotos
            int r7 = r7.size()
            r0.animateAllLine = r3
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r3 = r0.currentPhotos
            r3.clear()
            java.util.ArrayList<java.lang.Object> r3 = r0.currentObjects
            r3.clear()
            if (r17 == 0) goto L_0x0289
            boolean r3 = r17.isEmpty()
            if (r3 != 0) goto L_0x0289
            java.util.ArrayList<java.lang.Object> r3 = r0.currentObjects
            r4 = r17
            r3.addAll(r4)
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r3 = r0.currentPhotos
            r3.addAll(r4)
            r0.currentImage = r1
            r0.animateToItem = r2
            r1 = 0
            r0.animateToItemFast = r1
            goto L_0x038a
        L_0x0289:
            if (r4 == 0) goto L_0x0319
            boolean r3 = r4.isEmpty()
            if (r3 != 0) goto L_0x0319
            long r8 = r0.currentGroupId
            r10 = 0
            int r3 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r3 != 0) goto L_0x029b
            if (r6 == 0) goto L_0x038a
        L_0x029b:
            int r3 = r1 + 10
            int r5 = r4.size()
            int r3 = java.lang.Math.min(r3, r5)
            r5 = r1
        L_0x02a6:
            r8 = 56
            if (r5 >= r3) goto L_0x02d6
            java.lang.Object r9 = r4.get(r5)
            org.telegram.messenger.MessageObject r9 = (org.telegram.messenger.MessageObject) r9
            if (r6 != 0) goto L_0x02bc
            long r10 = r9.getGroupIdForUse()
            long r12 = r0.currentGroupId
            int r14 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r14 != 0) goto L_0x02d6
        L_0x02bc:
            java.util.ArrayList<java.lang.Object> r10 = r0.currentObjects
            r10.add(r9)
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r10 = r0.currentPhotos
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r11 = r9.photoThumbs
            r12 = 1
            org.telegram.tgnet.TLRPC$PhotoSize r8 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r11, r8, r12)
            org.telegram.tgnet.TLObject r9 = r9.photoThumbsObject
            org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForObject(r8, r9)
            r10.add(r8)
            int r5 = r5 + 1
            goto L_0x02a6
        L_0x02d6:
            r3 = 0
            r0.currentImage = r3
            r0.animateToItem = r2
            r0.animateToItemFast = r3
            int r2 = r1 + -10
            int r2 = java.lang.Math.max(r2, r3)
            r3 = 1
            int r1 = r1 - r3
        L_0x02e5:
            if (r1 < r2) goto L_0x038a
            java.lang.Object r3 = r4.get(r1)
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            if (r6 != 0) goto L_0x02f9
            long r9 = r3.getGroupIdForUse()
            long r11 = r0.currentGroupId
            int r5 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r5 != 0) goto L_0x038a
        L_0x02f9:
            java.util.ArrayList<java.lang.Object> r5 = r0.currentObjects
            r9 = 0
            r5.add(r9, r3)
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r5 = r0.currentPhotos
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r10 = r3.photoThumbs
            r11 = 1
            org.telegram.tgnet.TLRPC$PhotoSize r10 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r10, r8, r11)
            org.telegram.tgnet.TLObject r3 = r3.photoThumbsObject
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForObject(r10, r3)
            r5.add(r9, r3)
            int r3 = r0.currentImage
            int r3 = r3 + r11
            r0.currentImage = r3
            int r1 = r1 + -1
            goto L_0x02e5
        L_0x0319:
            if (r5 == 0) goto L_0x038a
            boolean r3 = r5.isEmpty()
            if (r3 != 0) goto L_0x038a
            long r3 = r0.currentGroupId
            r8 = 0
            int r6 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r6 == 0) goto L_0x038a
            int r3 = r5.size()
            r4 = r1
        L_0x032e:
            if (r4 >= r3) goto L_0x0354
            java.lang.Object r6 = r5.get(r4)
            org.telegram.tgnet.TLRPC$PageBlock r6 = (org.telegram.tgnet.TLRPC$PageBlock) r6
            int r8 = r6.groupId
            long r8 = (long) r8
            long r10 = r0.currentGroupId
            int r12 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r12 != 0) goto L_0x0354
            java.util.ArrayList<java.lang.Object> r8 = r0.currentObjects
            r8.add(r6)
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r8 = r0.currentPhotos
            org.telegram.tgnet.TLRPC$PhotoSize r9 = r6.thumb
            org.telegram.tgnet.TLObject r6 = r6.thumbObject
            org.telegram.messenger.ImageLocation r6 = org.telegram.messenger.ImageLocation.getForObject(r9, r6)
            r8.add(r6)
            int r4 = r4 + 1
            goto L_0x032e
        L_0x0354:
            r3 = 0
            r0.currentImage = r3
            r0.animateToItem = r2
            r0.animateToItemFast = r3
            r2 = 1
            int r1 = r1 - r2
        L_0x035d:
            if (r1 < 0) goto L_0x038a
            java.lang.Object r2 = r5.get(r1)
            org.telegram.tgnet.TLRPC$PageBlock r2 = (org.telegram.tgnet.TLRPC$PageBlock) r2
            int r3 = r2.groupId
            long r3 = (long) r3
            long r8 = r0.currentGroupId
            int r6 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r6 != 0) goto L_0x038a
            java.util.ArrayList<java.lang.Object> r3 = r0.currentObjects
            r4 = 0
            r3.add(r4, r2)
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r3 = r0.currentPhotos
            org.telegram.tgnet.TLRPC$PhotoSize r6 = r2.thumb
            org.telegram.tgnet.TLObject r2 = r2.thumbObject
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForObject(r6, r2)
            r3.add(r4, r2)
            int r2 = r0.currentImage
            r3 = 1
            int r2 = r2 + r3
            r0.currentImage = r2
            int r1 = r1 + -1
            goto L_0x035d
        L_0x038a:
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r1 = r0.currentPhotos
            int r1 = r1.size()
            r2 = 1
            if (r1 != r2) goto L_0x039d
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r1 = r0.currentPhotos
            r1.clear()
            java.util.ArrayList<java.lang.Object> r1 = r0.currentObjects
            r1.clear()
        L_0x039d:
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r1 = r0.currentPhotos
            int r1 = r1.size()
            if (r1 == r7) goto L_0x03a8
            r20.requestLayout()
        L_0x03a8:
            r1 = 0
            r0.fillImages(r1, r1)
        L_0x03ac:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupedPhotosListView.fillList():void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fillList$0(ValueAnimator valueAnimator) {
        this.drawAlpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fillList$1(ValueAnimator valueAnimator) {
        this.drawAlpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void setMoveProgress(float f) {
        if (!this.scrolling && this.animateToItem < 0) {
            if (f > 0.0f) {
                this.nextImage = this.currentImage - 1;
            } else {
                this.nextImage = this.currentImage + 1;
            }
            int i = this.nextImage;
            if (i < 0 || i >= this.currentPhotos.size()) {
                this.currentItemProgress = 1.0f;
            } else {
                this.currentItemProgress = 1.0f - Math.abs(f);
            }
            this.nextItemProgress = 1.0f - this.currentItemProgress;
            this.moving = f != 0.0f;
            invalidate();
            if (this.currentPhotos.isEmpty()) {
                return;
            }
            if (f < 0.0f && this.currentImage == this.currentPhotos.size() - 1) {
                return;
            }
            if (f <= 0.0f || this.currentImage != 0) {
                int i2 = (int) (f * ((float) (this.itemWidth + this.itemSpacing)));
                this.drawDx = i2;
                fillImages(true, i2);
            }
        }
    }

    private ImageReceiver getFreeReceiver() {
        ImageReceiver imageReceiver;
        if (this.unusedReceivers.isEmpty()) {
            imageReceiver = new ImageReceiver(this);
        } else {
            imageReceiver = this.unusedReceivers.get(0);
            this.unusedReceivers.remove(0);
        }
        this.imagesToDraw.add(imageReceiver);
        imageReceiver.setCurrentAccount(this.delegate.getCurrentAccount());
        return imageReceiver;
    }

    private void fillImages(boolean z, int i) {
        int i2;
        int i3;
        Object obj;
        Object obj2;
        if (!z && !this.imagesToDraw.isEmpty()) {
            this.unusedReceivers.addAll(this.imagesToDraw);
            this.imagesToDraw.clear();
            this.moving = false;
            this.moveLineProgress = 1.0f;
            this.currentItemProgress = 1.0f;
            this.nextItemProgress = 0.0f;
        }
        invalidate();
        if (getMeasuredWidth() != 0 && !this.currentPhotos.isEmpty()) {
            int measuredWidth = getMeasuredWidth();
            int measuredWidth2 = (getMeasuredWidth() / 2) - (this.itemWidth / 2);
            if (z) {
                int size = this.imagesToDraw.size();
                int i4 = 0;
                i3 = Integer.MIN_VALUE;
                i2 = Integer.MAX_VALUE;
                while (i4 < size) {
                    ImageReceiver imageReceiver = this.imagesToDraw.get(i4);
                    int param = imageReceiver.getParam();
                    int i5 = this.itemWidth;
                    int i6 = ((param - this.currentImage) * (this.itemSpacing + i5)) + measuredWidth2 + i;
                    if (i6 > measuredWidth || i6 + i5 < 0) {
                        this.unusedReceivers.add(imageReceiver);
                        this.imagesToDraw.remove(i4);
                        size--;
                        i4--;
                    }
                    i2 = Math.min(i2, param - 1);
                    i3 = Math.max(i3, param + 1);
                    i4++;
                }
            } else {
                i3 = this.currentImage;
                i2 = i3 - 1;
            }
            if (i3 != Integer.MIN_VALUE) {
                int size2 = this.currentPhotos.size();
                while (i3 < size2) {
                    int i7 = ((i3 - this.currentImage) * (this.itemWidth + this.itemSpacing)) + measuredWidth2 + i;
                    if (i7 >= measuredWidth) {
                        break;
                    }
                    ImageLocation imageLocation = this.currentPhotos.get(i3);
                    ImageReceiver freeReceiver = getFreeReceiver();
                    freeReceiver.setImageCoords((float) i7, (float) this.itemY, (float) this.itemWidth, (float) this.itemHeight);
                    if (this.currentObjects.get(0) instanceof MessageObject) {
                        obj2 = this.currentObjects.get(i3);
                    } else if (this.currentObjects.get(0) instanceof TLRPC$PageBlock) {
                        obj2 = this.delegate.getParentObject();
                    } else {
                        obj2 = "avatar_" + this.delegate.getAvatarsDialogId();
                    }
                    freeReceiver.setImage((ImageLocation) null, (String) null, imageLocation, "80_80", 0, (String) null, obj2, 1);
                    freeReceiver.setParam(i3);
                    i3++;
                }
            }
            if (i2 != Integer.MAX_VALUE) {
                while (i2 >= 0) {
                    int i8 = this.itemWidth;
                    int i9 = ((i2 - this.currentImage) * (this.itemSpacing + i8)) + measuredWidth2 + i + i8;
                    if (i9 <= 0) {
                        break;
                    }
                    ImageLocation imageLocation2 = this.currentPhotos.get(i2);
                    ImageReceiver freeReceiver2 = getFreeReceiver();
                    freeReceiver2.setImageCoords((float) i9, (float) this.itemY, (float) this.itemWidth, (float) this.itemHeight);
                    if (this.currentObjects.get(0) instanceof MessageObject) {
                        obj = this.currentObjects.get(i2);
                    } else if (this.currentObjects.get(0) instanceof TLRPC$PageBlock) {
                        obj = this.delegate.getParentObject();
                    } else {
                        obj = "avatar_" + this.delegate.getAvatarsDialogId();
                    }
                    freeReceiver2.setImage((ImageLocation) null, (String) null, imageLocation2, "80_80", 0, (String) null, obj, 1);
                    freeReceiver2.setParam(i2);
                    i2--;
                }
            }
            ValueAnimator valueAnimator = this.showAnimator;
            if (valueAnimator != null && !valueAnimator.isStarted()) {
                this.showAnimator.start();
            }
        }
    }

    public boolean onDown(MotionEvent motionEvent) {
        if (!this.scroll.isFinished()) {
            this.scroll.abortAnimation();
        }
        this.animateToItem = -1;
        this.animateToItemFast = false;
        return true;
    }

    public boolean onSingleTapUp(MotionEvent motionEvent) {
        int currentIndex = this.delegate.getCurrentIndex();
        ArrayList<ImageLocation> imagesArrLocations = this.delegate.getImagesArrLocations();
        ArrayList<MessageObject> imagesArr = this.delegate.getImagesArr();
        List<TLRPC$PageBlock> pageBlockArr = this.delegate.getPageBlockArr();
        stopScrolling();
        int size = this.imagesToDraw.size();
        int i = 0;
        while (true) {
            if (i >= size) {
                break;
            }
            ImageReceiver imageReceiver = this.imagesToDraw.get(i);
            if (imageReceiver.isInsideImage(motionEvent.getX(), motionEvent.getY())) {
                int param = imageReceiver.getParam();
                if (param < 0 || param >= this.currentObjects.size()) {
                    return true;
                }
                if (imagesArr != null && !imagesArr.isEmpty()) {
                    int indexOf = imagesArr.indexOf((MessageObject) this.currentObjects.get(param));
                    if (currentIndex == indexOf) {
                        return true;
                    }
                    this.moveLineProgress = 1.0f;
                    this.animateAllLine = true;
                    this.delegate.setCurrentIndex(indexOf);
                } else if (pageBlockArr != null && !pageBlockArr.isEmpty()) {
                    int indexOf2 = pageBlockArr.indexOf((TLRPC$PageBlock) this.currentObjects.get(param));
                    if (currentIndex == indexOf2) {
                        return true;
                    }
                    this.moveLineProgress = 1.0f;
                    this.animateAllLine = true;
                    this.delegate.setCurrentIndex(indexOf2);
                } else if (imagesArrLocations != null && !imagesArrLocations.isEmpty()) {
                    int indexOf3 = imagesArrLocations.indexOf((ImageLocation) this.currentObjects.get(param));
                    if (currentIndex == indexOf3) {
                        return true;
                    }
                    this.moveLineProgress = 1.0f;
                    this.animateAllLine = true;
                    this.delegate.setCurrentIndex(indexOf3);
                }
            } else {
                i++;
            }
        }
        return false;
    }

    private void updateAfterScroll() {
        int i;
        int i2;
        int i3;
        int i4 = this.drawDx;
        int abs = Math.abs(i4);
        int i5 = this.itemWidth;
        int i6 = this.itemSpacing;
        int i7 = -1;
        if (abs > (i5 / 2) + i6) {
            if (i4 > 0) {
                i3 = i4 - ((i5 / 2) + i6);
                i2 = 1;
            } else {
                i3 = i4 + (i5 / 2) + i6;
                i2 = -1;
            }
            i = i2 + (i3 / (i5 + (i6 * 2)));
        } else {
            i = 0;
        }
        this.nextPhotoScrolling = this.currentImage - i;
        int currentIndex = this.delegate.getCurrentIndex();
        ArrayList<ImageLocation> imagesArrLocations = this.delegate.getImagesArrLocations();
        ArrayList<MessageObject> imagesArr = this.delegate.getImagesArr();
        List<TLRPC$PageBlock> pageBlockArr = this.delegate.getPageBlockArr();
        int i8 = this.nextPhotoScrolling;
        if (currentIndex != i8 && i8 >= 0 && i8 < this.currentPhotos.size()) {
            Object obj = this.currentObjects.get(this.nextPhotoScrolling);
            if (imagesArr != null && !imagesArr.isEmpty()) {
                i7 = imagesArr.indexOf((MessageObject) obj);
            } else if (pageBlockArr != null && !pageBlockArr.isEmpty()) {
                i7 = pageBlockArr.indexOf((TLRPC$PageBlock) obj);
            } else if (imagesArrLocations != null && !imagesArrLocations.isEmpty()) {
                i7 = imagesArrLocations.indexOf((ImageLocation) obj);
            }
            if (i7 >= 0) {
                this.ignoreChanges = true;
                this.delegate.setCurrentIndex(i7);
            }
        }
        if (!this.scrolling) {
            this.scrolling = true;
            this.stopedScrolling = false;
        }
        fillImages(true, this.drawDx);
    }

    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        this.drawDx = (int) (((float) this.drawDx) - f);
        int minScrollX = getMinScrollX();
        int maxScrollX = getMaxScrollX();
        int i = this.drawDx;
        if (i < minScrollX) {
            this.drawDx = minScrollX;
        } else if (i > maxScrollX) {
            this.drawDx = maxScrollX;
        }
        updateAfterScroll();
        return false;
    }

    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        this.scroll.abortAnimation();
        if (this.currentPhotos.size() < 10) {
            return false;
        }
        this.scroll.fling(this.drawDx, 0, Math.round(f), 0, getMinScrollX(), getMaxScrollX(), 0, 0);
        return false;
    }

    private void stopScrolling() {
        this.scrolling = false;
        if (!this.scroll.isFinished()) {
            this.scroll.abortAnimation();
        }
        int i = this.nextPhotoScrolling;
        if (i >= 0 && i < this.currentObjects.size()) {
            this.stopedScrolling = true;
            this.animateToItemFast = false;
            int i2 = this.nextPhotoScrolling;
            this.animateToItem = i2;
            this.nextImage = i2;
            this.animateToDX = (this.currentImage - i2) * (this.itemWidth + this.itemSpacing);
            this.animateToDXStart = this.drawDx;
            this.moveLineProgress = 1.0f;
            this.nextPhotoScrolling = -1;
            GroupedPhotosListViewDelegate groupedPhotosListViewDelegate = this.delegate;
            if (groupedPhotosListViewDelegate != null) {
                groupedPhotosListViewDelegate.onStopScrolling();
            }
        }
        invalidate();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z = false;
        if (!this.currentPhotos.isEmpty() && getAlpha() == 1.0f) {
            if (this.gestureDetector.onTouchEvent(motionEvent) || super.onTouchEvent(motionEvent)) {
                z = true;
            }
            if (this.scrolling && motionEvent.getAction() == 1 && this.scroll.isFinished()) {
                stopScrolling();
            }
        }
        return z;
    }

    private int getMinScrollX() {
        return (-((this.currentPhotos.size() - this.currentImage) - 1)) * (this.itemWidth + (this.itemSpacing * 2));
    }

    private int getMaxScrollX() {
        return this.currentImage * (this.itemWidth + (this.itemSpacing * 2));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        fillImages(false, 0);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i;
        int i2;
        TLRPC$PhotoSize tLRPC$PhotoSize;
        TLRPC$PhotoSize tLRPC$PhotoSize2;
        if (this.hasPhotos || !this.imagesToDraw.isEmpty()) {
            float f = this.drawAlpha;
            if (!this.animateBackground) {
                f = this.hasPhotos ? 1.0f : 0.0f;
            }
            this.backgroundPaint.setAlpha((int) (f * 127.0f));
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.backgroundPaint);
            if (!this.imagesToDraw.isEmpty()) {
                int size = this.imagesToDraw.size();
                int i3 = this.drawDx;
                int i4 = (int) (((float) this.itemWidth) * 2.0f);
                int dp = AndroidUtilities.dp(8.0f);
                ImageLocation imageLocation = this.currentPhotos.get(this.currentImage);
                if (imageLocation == null || (tLRPC$PhotoSize2 = imageLocation.photoSize) == null) {
                    i = this.itemHeight;
                } else {
                    i = Math.max(this.itemWidth, (int) (((float) tLRPC$PhotoSize2.w) * (((float) this.itemHeight) / ((float) tLRPC$PhotoSize2.h))));
                }
                int min = Math.min(i4, i);
                float f2 = (float) (dp * 2);
                float f3 = this.currentItemProgress;
                int i5 = (int) (f2 * f3);
                int i6 = this.itemWidth;
                int i7 = i6 + ((int) (((float) (min - i6)) * f3)) + i5;
                int i8 = this.nextImage;
                if (i8 < 0 || i8 >= this.currentPhotos.size()) {
                    i2 = this.itemWidth;
                } else {
                    ImageLocation imageLocation2 = this.currentPhotos.get(this.nextImage);
                    if (imageLocation2 == null || (tLRPC$PhotoSize = imageLocation2.photoSize) == null) {
                        i2 = this.itemHeight;
                    } else {
                        i2 = Math.max(this.itemWidth, (int) (((float) tLRPC$PhotoSize.w) * (((float) this.itemHeight) / ((float) tLRPC$PhotoSize.h))));
                    }
                }
                int min2 = Math.min(i4, i2);
                float f4 = this.nextItemProgress;
                int i9 = (int) (f2 * f4);
                float f5 = (float) i3;
                int i10 = this.itemWidth;
                int i11 = (int) (f5 + (((float) (((min2 + i9) - i10) / 2)) * f4 * ((float) (this.nextImage > this.currentImage ? -1 : 1))));
                int i12 = i10 + ((int) (((float) (min2 - i10)) * f4)) + i9;
                int measuredWidth = (getMeasuredWidth() - i7) / 2;
                for (int i13 = 0; i13 < size; i13++) {
                    ImageReceiver imageReceiver = this.imagesToDraw.get(i13);
                    int param = imageReceiver.getParam();
                    int i14 = this.currentImage;
                    if (param == i14) {
                        imageReceiver.setImageX(measuredWidth + i11 + (i5 / 2));
                        imageReceiver.setImageWidth(i7 - i5);
                    } else {
                        int i15 = this.nextImage;
                        if (i15 < i14) {
                            if (param >= i14) {
                                imageReceiver.setImageX(measuredWidth + i7 + this.itemSpacing + (((imageReceiver.getParam() - this.currentImage) - 1) * (this.itemWidth + this.itemSpacing)) + i11);
                            } else if (param <= i15) {
                                int i16 = this.itemWidth;
                                int i17 = this.itemSpacing;
                                imageReceiver.setImageX((((((imageReceiver.getParam() - this.currentImage) + 1) * (i16 + i17)) + measuredWidth) - (i17 + i12)) + i11);
                            } else {
                                imageReceiver.setImageX(((imageReceiver.getParam() - this.currentImage) * (this.itemWidth + this.itemSpacing)) + measuredWidth + i11);
                            }
                        } else if (param < i14) {
                            imageReceiver.setImageX(((imageReceiver.getParam() - this.currentImage) * (this.itemWidth + this.itemSpacing)) + measuredWidth + i11);
                        } else if (param <= i15) {
                            imageReceiver.setImageX(measuredWidth + i7 + this.itemSpacing + (((imageReceiver.getParam() - this.currentImage) - 1) * (this.itemWidth + this.itemSpacing)) + i11);
                        } else {
                            int i18 = this.itemWidth;
                            int i19 = this.itemSpacing;
                            imageReceiver.setImageX(measuredWidth + i7 + this.itemSpacing + (((imageReceiver.getParam() - this.currentImage) - 2) * (i18 + i19)) + i19 + i12 + i11);
                        }
                        if (param == this.nextImage) {
                            imageReceiver.setImageWidth(i12 - i9);
                            imageReceiver.setImageX((int) (imageReceiver.getImageX() + ((float) (i9 / 2))));
                        } else {
                            imageReceiver.setImageWidth(this.itemWidth);
                        }
                    }
                    imageReceiver.setAlpha(this.drawAlpha);
                    imageReceiver.draw(canvas);
                }
                long currentTimeMillis = System.currentTimeMillis();
                long j = currentTimeMillis - this.lastUpdateTime;
                if (j > 17) {
                    j = 17;
                }
                this.lastUpdateTime = currentTimeMillis;
                int i20 = this.animateToItem;
                if (i20 >= 0) {
                    float f6 = this.moveLineProgress;
                    if (f6 > 0.0f) {
                        float f7 = (float) j;
                        float f8 = f6 - (f7 / (this.animateToItemFast ? 100.0f : 200.0f));
                        this.moveLineProgress = f8;
                        if (i20 == this.currentImage) {
                            float f9 = this.currentItemProgress;
                            if (f9 < 1.0f) {
                                float var_ = f9 + (f7 / 200.0f);
                                this.currentItemProgress = var_;
                                if (var_ > 1.0f) {
                                    this.currentItemProgress = 1.0f;
                                }
                            }
                            int i21 = this.animateToDXStart;
                            this.drawDx = i21 + ((int) Math.ceil((double) (this.currentItemProgress * ((float) (this.animateToDX - i21)))));
                        } else {
                            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT;
                            float interpolation = cubicBezierInterpolator.getInterpolation(1.0f - f8);
                            this.nextItemProgress = interpolation;
                            if (this.stopedScrolling) {
                                float var_ = this.currentItemProgress;
                                if (var_ > 0.0f) {
                                    float var_ = var_ - (f7 / 200.0f);
                                    this.currentItemProgress = var_;
                                    if (var_ < 0.0f) {
                                        this.currentItemProgress = 0.0f;
                                    }
                                }
                                int i22 = this.animateToDXStart;
                                this.drawDx = i22 + ((int) Math.ceil((double) (interpolation * ((float) (this.animateToDX - i22)))));
                            } else {
                                this.currentItemProgress = cubicBezierInterpolator.getInterpolation(this.moveLineProgress);
                                this.drawDx = (int) Math.ceil((double) (this.nextItemProgress * ((float) this.animateToDX)));
                            }
                        }
                        if (this.moveLineProgress <= 0.0f) {
                            this.currentImage = this.animateToItem;
                            this.moveLineProgress = 1.0f;
                            this.currentItemProgress = 1.0f;
                            this.nextItemProgress = 0.0f;
                            this.moving = false;
                            this.stopedScrolling = false;
                            this.drawDx = 0;
                            this.animateToItem = -1;
                            this.animateToItemFast = false;
                        }
                    }
                    fillImages(true, this.drawDx);
                    invalidate();
                }
                if (this.scrolling) {
                    float var_ = this.currentItemProgress;
                    if (var_ > 0.0f) {
                        float var_ = var_ - (((float) j) / 200.0f);
                        this.currentItemProgress = var_;
                        if (var_ < 0.0f) {
                            this.currentItemProgress = 0.0f;
                        }
                        invalidate();
                    }
                }
                if (!this.scroll.isFinished()) {
                    if (this.scroll.computeScrollOffset()) {
                        this.drawDx = this.scroll.getCurrX();
                        updateAfterScroll();
                        invalidate();
                    }
                    if (this.scroll.isFinished()) {
                        stopScrolling();
                    }
                }
            }
        }
    }

    public void setDelegate(GroupedPhotosListViewDelegate groupedPhotosListViewDelegate) {
        this.delegate = groupedPhotosListViewDelegate;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0013, code lost:
        r0 = r2.showAnimator;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean hasPhotos() {
        /*
            r2 = this;
            boolean r0 = r2.hasPhotos
            if (r0 == 0) goto L_0x001f
            android.animation.ValueAnimator r0 = r2.hideAnimator
            if (r0 != 0) goto L_0x001f
            float r0 = r2.drawAlpha
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 > 0) goto L_0x001d
            boolean r0 = r2.animateBackground
            if (r0 == 0) goto L_0x001d
            android.animation.ValueAnimator r0 = r2.showAnimator
            if (r0 == 0) goto L_0x001f
            boolean r0 = r0.isStarted()
            if (r0 == 0) goto L_0x001f
        L_0x001d:
            r0 = 1
            goto L_0x0020
        L_0x001f:
            r0 = 0
        L_0x0020:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupedPhotosListView.hasPhotos():boolean");
    }

    public void setAnimationsEnabled(boolean z) {
        if (this.animationsEnabled != z) {
            this.animationsEnabled = z;
            if (!z) {
                ValueAnimator valueAnimator = this.showAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    this.showAnimator = null;
                }
                ValueAnimator valueAnimator2 = this.hideAnimator;
                if (valueAnimator2 != null) {
                    valueAnimator2.cancel();
                    this.hideAnimator = null;
                }
                this.drawAlpha = 0.0f;
                invalidate();
            }
        }
    }

    public void setAnimateBackground(boolean z) {
        this.animateBackground = z;
    }

    public void reset() {
        this.hasPhotos = false;
        if (this.animationsEnabled) {
            this.drawAlpha = 0.0f;
        }
    }
}
