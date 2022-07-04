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
import org.telegram.tgnet.TLRPC;

public class GroupedPhotosListView extends View implements GestureDetector.OnGestureListener {
    private boolean animateAllLine;
    private boolean animateBackground;
    private int animateToDX;
    private int animateToDXStart;
    private int animateToItem;
    private boolean animateToItemFast;
    private boolean animationsEnabled;
    private Paint backgroundPaint;
    private long currentGroupId;
    private int currentImage;
    private float currentItemProgress;
    private ArrayList<Object> currentObjects;
    public ArrayList<ImageLocation> currentPhotos;
    /* access modifiers changed from: private */
    public GroupedPhotosListViewDelegate delegate;
    private float drawAlpha;
    private int drawDx;
    private GestureDetector gestureDetector;
    private boolean hasPhotos;
    /* access modifiers changed from: private */
    public ValueAnimator hideAnimator;
    private boolean ignoreChanges;
    private ArrayList<ImageReceiver> imagesToDraw;
    private int itemHeight;
    private int itemSpacing;
    private int itemWidth;
    private int itemY;
    private long lastUpdateTime;
    private float moveLineProgress;
    private boolean moving;
    private int nextImage;
    private float nextItemProgress;
    private int nextPhotoScrolling;
    private Scroller scroll;
    private boolean scrolling;
    /* access modifiers changed from: private */
    public ValueAnimator showAnimator;
    private boolean stopedScrolling;
    private ArrayList<ImageReceiver> unusedReceivers;

    public interface GroupedPhotosListViewDelegate {
        long getAvatarsDialogId();

        int getCurrentAccount();

        int getCurrentIndex();

        ArrayList<MessageObject> getImagesArr();

        ArrayList<ImageLocation> getImagesArrLocations();

        List<TLRPC.PageBlock> getPageBlockArr();

        Object getParentObject();

        int getSlideshowMessageId();

        void onShowAnimationStart();

        void onStopScrolling();

        void setCurrentIndex(int i);

        boolean validGroupId(long j);
    }

    public GroupedPhotosListView(Context context) {
        this(context, AndroidUtilities.dp(3.0f));
    }

    public GroupedPhotosListView(Context context, int paddingTop) {
        super(context);
        this.backgroundPaint = new Paint();
        this.unusedReceivers = new ArrayList<>();
        this.imagesToDraw = new ArrayList<>();
        this.currentPhotos = new ArrayList<>();
        this.currentObjects = new ArrayList<>();
        this.currentItemProgress = 1.0f;
        this.nextItemProgress = 0.0f;
        this.animateToItem = -1;
        this.animationsEnabled = true;
        this.nextPhotoScrolling = -1;
        this.animateBackground = true;
        this.gestureDetector = new GestureDetector(context, this);
        this.scroll = new Scroller(context);
        this.itemWidth = AndroidUtilities.dp(42.0f);
        this.itemHeight = AndroidUtilities.dp(56.0f);
        this.itemSpacing = AndroidUtilities.dp(1.0f);
        this.itemY = paddingTop;
        this.backgroundPaint.setColor(NUM);
    }

    public void clear() {
        this.currentPhotos.clear();
        this.currentObjects.clear();
        this.imagesToDraw.clear();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v0, resolved type: org.telegram.messenger.ImageLocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v1, resolved type: org.telegram.messenger.ImageLocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v2, resolved type: org.telegram.tgnet.TLRPC$PageBlock} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v17, resolved type: org.telegram.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v3, resolved type: org.telegram.tgnet.TLRPC$PageBlock} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v4, resolved type: org.telegram.messenger.ImageLocation} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void fillList() {
        /*
            r24 = this;
            r0 = r24
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
            int r7 = r7.getCurrentAccount()
            r0.hasPhotos = r2
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r13 = 1
            if (r3 == 0) goto L_0x005d
            boolean r14 = r3.isEmpty()
            if (r14 != 0) goto L_0x005d
            int r14 = r3.size()
            if (r1 < r14) goto L_0x004a
            int r14 = r3.size()
            int r1 = r14 + -1
        L_0x004a:
            java.lang.Object r14 = r3.get(r1)
            org.telegram.messenger.ImageLocation r14 = (org.telegram.messenger.ImageLocation) r14
            int r9 = r3.size()
            r10 = r14
            r0.hasPhotos = r13
            r16 = r3
            r17 = r7
            goto L_0x016e
        L_0x005d:
            if (r4 == 0) goto L_0x00f8
            boolean r14 = r4.isEmpty()
            if (r14 != 0) goto L_0x00f8
            int r14 = r4.size()
            if (r1 < r14) goto L_0x0071
            int r14 = r4.size()
            int r1 = r14 + -1
        L_0x0071:
            java.lang.Object r14 = r4.get(r1)
            org.telegram.messenger.MessageObject r14 = (org.telegram.messenger.MessageObject) r14
            r10 = r14
            r16 = r3
            long r2 = r14.getGroupIdForUse()
            r17 = r14
            long r13 = r0.currentGroupId
            int r18 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r18 == 0) goto L_0x0089
            r8 = 1
            r0.currentGroupId = r2
        L_0x0089:
            long r13 = r0.currentGroupId
            int r18 = (r13 > r11 ? 1 : (r13 == r11 ? 0 : -1))
            if (r18 == 0) goto L_0x00ee
            r13 = 1
            r0.hasPhotos = r13
            int r13 = r1 + 10
            int r14 = r4.size()
            int r13 = java.lang.Math.min(r13, r14)
            r14 = r1
        L_0x009d:
            if (r14 >= r13) goto L_0x00b9
            java.lang.Object r18 = r4.get(r14)
            org.telegram.messenger.MessageObject r18 = (org.telegram.messenger.MessageObject) r18
            if (r6 != 0) goto L_0x00b1
            long r19 = r18.getGroupIdForUse()
            long r11 = r0.currentGroupId
            int r22 = (r19 > r11 ? 1 : (r19 == r11 ? 0 : -1))
            if (r22 != 0) goto L_0x00b9
        L_0x00b1:
            int r9 = r9 + 1
            int r14 = r14 + 1
            r11 = 0
            goto L_0x009d
        L_0x00b9:
            int r11 = r1 + -10
            r12 = 0
            int r11 = java.lang.Math.max(r11, r12)
            int r12 = r1 + -1
        L_0x00c2:
            if (r12 < r11) goto L_0x00e9
            java.lang.Object r14 = r4.get(r12)
            org.telegram.messenger.MessageObject r14 = (org.telegram.messenger.MessageObject) r14
            if (r6 != 0) goto L_0x00db
            long r18 = r14.getGroupIdForUse()
            r20 = r1
            r22 = r2
            long r1 = r0.currentGroupId
            int r3 = (r18 > r1 ? 1 : (r18 == r1 ? 0 : -1))
            if (r3 != 0) goto L_0x00f2
            goto L_0x00df
        L_0x00db:
            r20 = r1
            r22 = r2
        L_0x00df:
            int r9 = r9 + 1
            int r12 = r12 + -1
            r1 = r20
            r2 = r22
            goto L_0x00c2
        L_0x00e9:
            r20 = r1
            r22 = r2
            goto L_0x00f2
        L_0x00ee:
            r20 = r1
            r22 = r2
        L_0x00f2:
            r17 = r7
            r1 = r20
            goto L_0x016e
        L_0x00f8:
            r16 = r3
            if (r5 == 0) goto L_0x016c
            boolean r2 = r5.isEmpty()
            if (r2 != 0) goto L_0x016c
            java.lang.Object r2 = r5.get(r1)
            org.telegram.tgnet.TLRPC$PageBlock r2 = (org.telegram.tgnet.TLRPC.PageBlock) r2
            r10 = r2
            int r3 = r2.groupId
            long r11 = (long) r3
            long r13 = r0.currentGroupId
            int r3 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r3 == 0) goto L_0x0118
            r8 = 1
            int r3 = r2.groupId
            long r11 = (long) r3
            r0.currentGroupId = r11
        L_0x0118:
            long r11 = r0.currentGroupId
            r13 = 0
            int r3 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r3 == 0) goto L_0x0167
            r3 = 1
            r0.hasPhotos = r3
            r3 = r1
            int r11 = r5.size()
        L_0x0128:
            if (r3 >= r11) goto L_0x0147
            java.lang.Object r12 = r5.get(r3)
            org.telegram.tgnet.TLRPC$PageBlock r12 = (org.telegram.tgnet.TLRPC.PageBlock) r12
            int r13 = r12.groupId
            long r13 = (long) r13
            r17 = r7
            r18 = r8
            long r7 = r0.currentGroupId
            int r19 = (r13 > r7 ? 1 : (r13 == r7 ? 0 : -1))
            if (r19 != 0) goto L_0x014b
            int r9 = r9 + 1
            int r3 = r3 + 1
            r7 = r17
            r8 = r18
            goto L_0x0128
        L_0x0147:
            r17 = r7
            r18 = r8
        L_0x014b:
            int r3 = r1 + -1
        L_0x014d:
            if (r3 < 0) goto L_0x0164
            java.lang.Object r7 = r5.get(r3)
            org.telegram.tgnet.TLRPC$PageBlock r7 = (org.telegram.tgnet.TLRPC.PageBlock) r7
            int r8 = r7.groupId
            long r11 = (long) r8
            long r13 = r0.currentGroupId
            int r8 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r8 != 0) goto L_0x0164
            int r9 = r9 + 1
            int r3 = r3 + -1
            goto L_0x014d
        L_0x0164:
            r8 = r18
            goto L_0x016e
        L_0x0167:
            r17 = r7
            r18 = r8
            goto L_0x016e
        L_0x016c:
            r17 = r7
        L_0x016e:
            if (r10 != 0) goto L_0x0171
            return
        L_0x0171:
            boolean r2 = r0.animationsEnabled
            if (r2 == 0) goto L_0x020d
            boolean r2 = r0.hasPhotos
            r3 = 1128792064(0x43480000, float:200.0)
            r7 = 2
            r11 = 0
            if (r2 != 0) goto L_0x01cb
            android.animation.ValueAnimator r2 = r0.showAnimator
            if (r2 == 0) goto L_0x0186
            r2.cancel()
            r0.showAnimator = r11
        L_0x0186:
            float r2 = r0.drawAlpha
            r11 = 0
            int r2 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1))
            if (r2 <= 0) goto L_0x020d
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r2 = r0.currentPhotos
            int r2 = r2.size()
            r12 = 1
            if (r2 <= r12) goto L_0x020d
            android.animation.ValueAnimator r2 = r0.hideAnimator
            if (r2 != 0) goto L_0x01ca
            float[] r2 = new float[r7]
            float r7 = r0.drawAlpha
            r13 = 0
            r2[r13] = r7
            r2[r12] = r11
            android.animation.ValueAnimator r2 = android.animation.ValueAnimator.ofFloat(r2)
            r0.hideAnimator = r2
            float r7 = r0.drawAlpha
            float r7 = r7 * r3
            long r11 = (long) r7
            r2.setDuration(r11)
            android.animation.ValueAnimator r2 = r0.hideAnimator
            org.telegram.ui.Components.GroupedPhotosListView$1 r3 = new org.telegram.ui.Components.GroupedPhotosListView$1
            r3.<init>()
            r2.addListener(r3)
            android.animation.ValueAnimator r2 = r0.hideAnimator
            org.telegram.ui.Components.GroupedPhotosListView$$ExternalSyntheticLambda0 r3 = new org.telegram.ui.Components.GroupedPhotosListView$$ExternalSyntheticLambda0
            r3.<init>(r0)
            r2.addUpdateListener(r3)
            android.animation.ValueAnimator r2 = r0.hideAnimator
            r2.start()
        L_0x01ca:
            return
        L_0x01cb:
            android.animation.ValueAnimator r2 = r0.hideAnimator
            if (r2 == 0) goto L_0x01d6
            android.animation.ValueAnimator r2 = r0.hideAnimator
            r0.hideAnimator = r11
            r2.cancel()
        L_0x01d6:
            float r2 = r0.drawAlpha
            r11 = 1065353216(0x3var_, float:1.0)
            int r12 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1))
            if (r12 >= 0) goto L_0x020d
            android.animation.ValueAnimator r12 = r0.showAnimator
            if (r12 != 0) goto L_0x020d
            float[] r7 = new float[r7]
            r12 = 0
            r7[r12] = r2
            r2 = 1
            r7[r2] = r11
            android.animation.ValueAnimator r2 = android.animation.ValueAnimator.ofFloat(r7)
            r0.showAnimator = r2
            float r7 = r0.drawAlpha
            float r11 = r11 - r7
            float r11 = r11 * r3
            long r11 = (long) r11
            r2.setDuration(r11)
            android.animation.ValueAnimator r2 = r0.showAnimator
            org.telegram.ui.Components.GroupedPhotosListView$2 r3 = new org.telegram.ui.Components.GroupedPhotosListView$2
            r3.<init>()
            r2.addListener(r3)
            android.animation.ValueAnimator r2 = r0.showAnimator
            org.telegram.ui.Components.GroupedPhotosListView$$ExternalSyntheticLambda1 r3 = new org.telegram.ui.Components.GroupedPhotosListView$$ExternalSyntheticLambda1
            r3.<init>(r0)
            r2.addUpdateListener(r3)
        L_0x020d:
            r2 = -1
            if (r8 != 0) goto L_0x0277
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r3 = r0.currentPhotos
            int r3 = r3.size()
            if (r9 != r3) goto L_0x0276
            java.util.ArrayList<java.lang.Object> r3 = r0.currentObjects
            boolean r3 = r3.contains(r10)
            if (r3 != 0) goto L_0x0221
            goto L_0x0276
        L_0x0221:
            java.util.ArrayList<java.lang.Object> r3 = r0.currentObjects
            int r3 = r3.indexOf(r10)
            int r7 = r0.currentImage
            if (r7 == r3) goto L_0x0277
            if (r3 == r2) goto L_0x0277
            boolean r11 = r0.animateAllLine
            if (r11 != 0) goto L_0x0241
            boolean r12 = r0.moving
            if (r12 != 0) goto L_0x0241
            int r12 = r7 + -1
            if (r3 == r12) goto L_0x023d
            int r12 = r7 + 1
            if (r3 != r12) goto L_0x0241
        L_0x023d:
            r11 = 1
            r12 = 1
            r0.animateToItemFast = r12
        L_0x0241:
            if (r11 == 0) goto L_0x0262
            r0.animateToItem = r3
            r0.nextImage = r3
            int r7 = r7 - r3
            int r12 = r0.itemWidth
            int r13 = r0.itemSpacing
            int r12 = r12 + r13
            int r7 = r7 * r12
            r0.animateToDX = r7
            r7 = 1
            r0.moving = r7
            r7 = 0
            r0.animateAllLine = r7
            long r12 = java.lang.System.currentTimeMillis()
            r0.lastUpdateTime = r12
            r24.invalidate()
            r7 = 0
            goto L_0x0273
        L_0x0262:
            int r7 = r7 - r3
            int r12 = r0.itemWidth
            int r13 = r0.itemSpacing
            int r12 = r12 + r13
            int r7 = r7 * r12
            r12 = 1
            r0.fillImages(r12, r7)
            r0.currentImage = r3
            r7 = 0
            r0.moving = r7
        L_0x0273:
            r0.drawDx = r7
            goto L_0x0277
        L_0x0276:
            r8 = 1
        L_0x0277:
            if (r8 == 0) goto L_0x040b
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r3 = r0.currentPhotos
            int r3 = r3.size()
            r7 = 0
            r0.animateAllLine = r7
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r7 = r0.currentPhotos
            r7.clear()
            java.util.ArrayList<java.lang.Object> r7 = r0.currentObjects
            r7.clear()
            if (r16 == 0) goto L_0x02ad
            boolean r7 = r16.isEmpty()
            if (r7 != 0) goto L_0x02ad
            java.util.ArrayList<java.lang.Object> r7 = r0.currentObjects
            r11 = r16
            r7.addAll(r11)
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r7 = r0.currentPhotos
            r7.addAll(r11)
            r0.currentImage = r1
            r0.animateToItem = r2
            r2 = 0
            r0.animateToItemFast = r2
            r16 = r3
            r14 = r4
            r7 = r5
            goto L_0x03e6
        L_0x02ad:
            r11 = r16
            if (r4 == 0) goto L_0x0360
            boolean r7 = r4.isEmpty()
            if (r7 != 0) goto L_0x0360
            long r12 = r0.currentGroupId
            r18 = 0
            int r7 = (r12 > r18 ? 1 : (r12 == r18 ? 0 : -1))
            if (r7 != 0) goto L_0x02c8
            if (r6 == 0) goto L_0x02c2
            goto L_0x02c8
        L_0x02c2:
            r16 = r3
            r14 = r4
            r7 = r5
            goto L_0x03e6
        L_0x02c8:
            int r7 = r1 + 10
            int r12 = r4.size()
            int r7 = java.lang.Math.min(r7, r12)
            r12 = r1
        L_0x02d3:
            r13 = 56
            if (r12 >= r7) goto L_0x030b
            java.lang.Object r14 = r4.get(r12)
            org.telegram.messenger.MessageObject r14 = (org.telegram.messenger.MessageObject) r14
            if (r6 != 0) goto L_0x02ec
            long r18 = r14.getGroupIdForUse()
            r16 = r3
            long r2 = r0.currentGroupId
            int r21 = (r18 > r2 ? 1 : (r18 == r2 ? 0 : -1))
            if (r21 != 0) goto L_0x030d
            goto L_0x02ee
        L_0x02ec:
            r16 = r3
        L_0x02ee:
            java.util.ArrayList<java.lang.Object> r2 = r0.currentObjects
            r2.add(r14)
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r2 = r0.currentPhotos
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r14.photoThumbs
            r15 = 1
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r13, r15)
            org.telegram.tgnet.TLObject r13 = r14.photoThumbsObject
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForObject(r3, r13)
            r2.add(r3)
            int r12 = r12 + 1
            r3 = r16
            r2 = -1
            goto L_0x02d3
        L_0x030b:
            r16 = r3
        L_0x030d:
            r2 = 0
            r0.currentImage = r2
            r3 = -1
            r0.animateToItem = r3
            r0.animateToItemFast = r2
            int r3 = r1 + -10
            int r3 = java.lang.Math.max(r3, r2)
            int r2 = r1 + -1
        L_0x031d:
            if (r2 < r3) goto L_0x035a
            java.lang.Object r12 = r4.get(r2)
            org.telegram.messenger.MessageObject r12 = (org.telegram.messenger.MessageObject) r12
            if (r6 != 0) goto L_0x0331
            long r18 = r12.getGroupIdForUse()
            long r13 = r0.currentGroupId
            int r21 = (r18 > r13 ? 1 : (r18 == r13 ? 0 : -1))
            if (r21 != 0) goto L_0x035c
        L_0x0331:
            java.util.ArrayList<java.lang.Object> r13 = r0.currentObjects
            r14 = 0
            r13.add(r14, r12)
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r13 = r0.currentPhotos
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r15 = r12.photoThumbs
            r19 = r3
            r3 = 1
            r14 = 56
            org.telegram.tgnet.TLRPC$PhotoSize r15 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r15, r14, r3)
            org.telegram.tgnet.TLObject r14 = r12.photoThumbsObject
            org.telegram.messenger.ImageLocation r14 = org.telegram.messenger.ImageLocation.getForObject(r15, r14)
            r15 = 0
            r13.add(r15, r14)
            int r13 = r0.currentImage
            int r13 = r13 + r3
            r0.currentImage = r13
            int r2 = r2 + -1
            r3 = r19
            r13 = 56
            goto L_0x031d
        L_0x035a:
            r19 = r3
        L_0x035c:
            r14 = r4
            r7 = r5
            goto L_0x03e6
        L_0x0360:
            r16 = r3
            if (r5 == 0) goto L_0x03e4
            boolean r2 = r5.isEmpty()
            if (r2 != 0) goto L_0x03e4
            long r2 = r0.currentGroupId
            r12 = 0
            int r7 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
            if (r7 == 0) goto L_0x03e1
            r2 = r1
            int r3 = r5.size()
        L_0x0377:
            if (r2 >= r3) goto L_0x03a3
            java.lang.Object r7 = r5.get(r2)
            org.telegram.tgnet.TLRPC$PageBlock r7 = (org.telegram.tgnet.TLRPC.PageBlock) r7
            int r12 = r7.groupId
            long r12 = (long) r12
            r18 = r3
            r14 = r4
            long r3 = r0.currentGroupId
            int r19 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
            if (r19 != 0) goto L_0x03a6
            java.util.ArrayList<java.lang.Object> r3 = r0.currentObjects
            r3.add(r7)
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r3 = r0.currentPhotos
            org.telegram.tgnet.TLRPC$PhotoSize r4 = r7.thumb
            org.telegram.tgnet.TLObject r12 = r7.thumbObject
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForObject(r4, r12)
            r3.add(r4)
            int r2 = r2 + 1
            r4 = r14
            r3 = r18
            goto L_0x0377
        L_0x03a3:
            r18 = r3
            r14 = r4
        L_0x03a6:
            r2 = 0
            r0.currentImage = r2
            r3 = -1
            r0.animateToItem = r3
            r0.animateToItemFast = r2
            int r2 = r1 + -1
        L_0x03b0:
            if (r2 < 0) goto L_0x03df
            java.lang.Object r3 = r5.get(r2)
            org.telegram.tgnet.TLRPC$PageBlock r3 = (org.telegram.tgnet.TLRPC.PageBlock) r3
            int r4 = r3.groupId
            long r12 = (long) r4
            r7 = r5
            long r4 = r0.currentGroupId
            int r18 = (r12 > r4 ? 1 : (r12 == r4 ? 0 : -1))
            if (r18 != 0) goto L_0x03e6
            java.util.ArrayList<java.lang.Object> r4 = r0.currentObjects
            r5 = 0
            r4.add(r5, r3)
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r4 = r0.currentPhotos
            org.telegram.tgnet.TLRPC$PhotoSize r12 = r3.thumb
            org.telegram.tgnet.TLObject r13 = r3.thumbObject
            org.telegram.messenger.ImageLocation r12 = org.telegram.messenger.ImageLocation.getForObject(r12, r13)
            r4.add(r5, r12)
            int r4 = r0.currentImage
            r5 = 1
            int r4 = r4 + r5
            r0.currentImage = r4
            int r2 = r2 + -1
            r5 = r7
            goto L_0x03b0
        L_0x03df:
            r7 = r5
            goto L_0x03e6
        L_0x03e1:
            r14 = r4
            r7 = r5
            goto L_0x03e6
        L_0x03e4:
            r14 = r4
            r7 = r5
        L_0x03e6:
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r2 = r0.currentPhotos
            int r2 = r2.size()
            r3 = 1
            if (r2 != r3) goto L_0x03f9
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r2 = r0.currentPhotos
            r2.clear()
            java.util.ArrayList<java.lang.Object> r2 = r0.currentObjects
            r2.clear()
        L_0x03f9:
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r2 = r0.currentPhotos
            int r2 = r2.size()
            r3 = r16
            if (r2 == r3) goto L_0x0406
            r24.requestLayout()
        L_0x0406:
            r2 = 0
            r0.fillImages(r2, r2)
            goto L_0x040f
        L_0x040b:
            r14 = r4
            r7 = r5
            r11 = r16
        L_0x040f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupedPhotosListView.fillList():void");
    }

    /* renamed from: lambda$fillList$0$org-telegram-ui-Components-GroupedPhotosListView  reason: not valid java name */
    public /* synthetic */ void m1023x71b0d47b(ValueAnimator a) {
        this.drawAlpha = ((Float) a.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* renamed from: lambda$fillList$1$org-telegram-ui-Components-GroupedPhotosListView  reason: not valid java name */
    public /* synthetic */ void m1024x635a7a9a(ValueAnimator a) {
        this.drawAlpha = ((Float) a.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void setMoveProgress(float progress) {
        if (!this.scrolling && this.animateToItem < 0) {
            if (progress > 0.0f) {
                this.nextImage = this.currentImage - 1;
            } else {
                this.nextImage = this.currentImage + 1;
            }
            int i = this.nextImage;
            if (i < 0 || i >= this.currentPhotos.size()) {
                this.currentItemProgress = 1.0f;
            } else {
                this.currentItemProgress = 1.0f - Math.abs(progress);
            }
            this.nextItemProgress = 1.0f - this.currentItemProgress;
            this.moving = progress != 0.0f;
            invalidate();
            if (this.currentPhotos.isEmpty()) {
                return;
            }
            if (progress < 0.0f && this.currentImage == this.currentPhotos.size() - 1) {
                return;
            }
            if (progress <= 0.0f || this.currentImage != 0) {
                int i2 = (int) (((float) (this.itemWidth + this.itemSpacing)) * progress);
                this.drawDx = i2;
                fillImages(true, i2);
            }
        }
    }

    private ImageReceiver getFreeReceiver() {
        ImageReceiver receiver;
        if (this.unusedReceivers.isEmpty()) {
            receiver = new ImageReceiver(this);
        } else {
            receiver = this.unusedReceivers.get(0);
            this.unusedReceivers.remove(0);
        }
        this.imagesToDraw.add(receiver);
        receiver.setCurrentAccount(this.delegate.getCurrentAccount());
        return receiver;
    }

    private void fillImages(boolean move, int dx) {
        int addLeftIndex;
        int addRightIndex;
        Object parent;
        Object parent2;
        int i = 0;
        if (!move && !this.imagesToDraw.isEmpty()) {
            this.unusedReceivers.addAll(this.imagesToDraw);
            this.imagesToDraw.clear();
            this.moving = false;
            this.moveLineProgress = 1.0f;
            this.currentItemProgress = 1.0f;
            this.nextItemProgress = 0.0f;
        }
        invalidate();
        if (getMeasuredWidth() != 0 && !this.currentPhotos.isEmpty()) {
            int width = getMeasuredWidth();
            int startX = (getMeasuredWidth() / 2) - (this.itemWidth / 2);
            if (move) {
                addRightIndex = Integer.MIN_VALUE;
                addLeftIndex = Integer.MAX_VALUE;
                int count = this.imagesToDraw.size();
                int a = 0;
                while (a < count) {
                    ImageReceiver receiver = this.imagesToDraw.get(a);
                    int num = receiver.getParam();
                    int i2 = this.itemWidth;
                    int x = ((num - this.currentImage) * (this.itemSpacing + i2)) + startX + dx;
                    if (x > width || i2 + x < 0) {
                        this.unusedReceivers.add(receiver);
                        this.imagesToDraw.remove(a);
                        count--;
                        a--;
                    }
                    addLeftIndex = Math.min(addLeftIndex, num - 1);
                    addRightIndex = Math.max(addRightIndex, num + 1);
                    a++;
                }
            } else {
                addRightIndex = this.currentImage;
                addLeftIndex = this.currentImage - 1;
            }
            if (addRightIndex != Integer.MIN_VALUE) {
                int count2 = this.currentPhotos.size();
                int a2 = addRightIndex;
                while (a2 < count2) {
                    int x2 = ((a2 - this.currentImage) * (this.itemWidth + this.itemSpacing)) + startX + dx;
                    if (x2 >= width) {
                        break;
                    }
                    ImageLocation location = this.currentPhotos.get(a2);
                    ImageReceiver receiver2 = getFreeReceiver();
                    receiver2.setImageCoords((float) x2, (float) this.itemY, (float) this.itemWidth, (float) this.itemHeight);
                    if (this.currentObjects.get(i) instanceof MessageObject) {
                        parent2 = this.currentObjects.get(a2);
                    } else if (this.currentObjects.get(i) instanceof TLRPC.PageBlock) {
                        parent2 = this.delegate.getParentObject();
                    } else {
                        parent2 = "avatar_" + this.delegate.getAvatarsDialogId();
                    }
                    receiver2.setImage((ImageLocation) null, (String) null, location, "80_80", 0, (String) null, parent2, 1);
                    receiver2.setParam(a2);
                    a2++;
                    i = 0;
                }
            }
            if (addLeftIndex != Integer.MAX_VALUE) {
                for (int a3 = addLeftIndex; a3 >= 0; a3--) {
                    int i3 = this.itemWidth;
                    int x3 = ((a3 - this.currentImage) * (this.itemSpacing + i3)) + startX + dx + i3;
                    if (x3 <= 0) {
                        break;
                    }
                    ImageLocation location2 = this.currentPhotos.get(a3);
                    ImageReceiver receiver3 = getFreeReceiver();
                    receiver3.setImageCoords((float) x3, (float) this.itemY, (float) this.itemWidth, (float) this.itemHeight);
                    if (this.currentObjects.get(0) instanceof MessageObject) {
                        parent = this.currentObjects.get(a3);
                    } else if (this.currentObjects.get(0) instanceof TLRPC.PageBlock) {
                        parent = this.delegate.getParentObject();
                    } else {
                        parent = "avatar_" + this.delegate.getAvatarsDialogId();
                    }
                    receiver3.setImage((ImageLocation) null, (String) null, location2, "80_80", 0, (String) null, parent, 1);
                    receiver3.setParam(a3);
                }
            }
            ValueAnimator valueAnimator = this.showAnimator;
            if (valueAnimator != null && !valueAnimator.isStarted()) {
                this.showAnimator.start();
            }
        }
    }

    public boolean onDown(MotionEvent e) {
        if (!this.scroll.isFinished()) {
            this.scroll.abortAnimation();
        }
        this.animateToItem = -1;
        this.animateToItemFast = false;
        return true;
    }

    public void onShowPress(MotionEvent e) {
    }

    public boolean onSingleTapUp(MotionEvent e) {
        int currentIndex = this.delegate.getCurrentIndex();
        ArrayList<ImageLocation> imagesArrLocations = this.delegate.getImagesArrLocations();
        ArrayList<MessageObject> imagesArr = this.delegate.getImagesArr();
        List<TLRPC.PageBlock> pageBlockArr = this.delegate.getPageBlockArr();
        stopScrolling();
        int count = this.imagesToDraw.size();
        int a = 0;
        while (a < count) {
            ImageReceiver receiver = this.imagesToDraw.get(a);
            if (receiver.isInsideImage(e.getX(), e.getY())) {
                int num = receiver.getParam();
                if (num < 0 || num >= this.currentObjects.size()) {
                    return true;
                }
                if (imagesArr != null && !imagesArr.isEmpty()) {
                    int idx = imagesArr.indexOf((MessageObject) this.currentObjects.get(num));
                    if (currentIndex == idx) {
                        return true;
                    }
                    this.moveLineProgress = 1.0f;
                    this.animateAllLine = true;
                    this.delegate.setCurrentIndex(idx);
                    return false;
                } else if (pageBlockArr != null && !pageBlockArr.isEmpty()) {
                    int idx2 = pageBlockArr.indexOf((TLRPC.PageBlock) this.currentObjects.get(num));
                    if (currentIndex == idx2) {
                        return true;
                    }
                    this.moveLineProgress = 1.0f;
                    this.animateAllLine = true;
                    this.delegate.setCurrentIndex(idx2);
                    return false;
                } else if (imagesArrLocations == null || imagesArrLocations.isEmpty()) {
                    return false;
                } else {
                    int idx3 = imagesArrLocations.indexOf((ImageLocation) this.currentObjects.get(num));
                    if (currentIndex == idx3) {
                        return true;
                    }
                    this.moveLineProgress = 1.0f;
                    this.animateAllLine = true;
                    this.delegate.setCurrentIndex(idx3);
                    return false;
                }
            } else {
                a++;
            }
        }
        return false;
    }

    private void updateAfterScroll() {
        int dx;
        int indexChange;
        int indexChange2 = 0;
        int dx2 = this.drawDx;
        int abs = Math.abs(dx2);
        int i = this.itemWidth;
        int i2 = this.itemSpacing;
        if (abs > (i / 2) + i2) {
            if (dx2 > 0) {
                dx = dx2 - ((i / 2) + i2);
                indexChange = 0 + 1;
            } else {
                dx = dx2 + (i / 2) + i2;
                indexChange = 0 - 1;
            }
            indexChange2 = indexChange + (dx / (i + (i2 * 2)));
        }
        this.nextPhotoScrolling = this.currentImage - indexChange2;
        int currentIndex = this.delegate.getCurrentIndex();
        ArrayList<ImageLocation> imagesArrLocations = this.delegate.getImagesArrLocations();
        ArrayList<MessageObject> imagesArr = this.delegate.getImagesArr();
        List<TLRPC.PageBlock> pageBlockArr = this.delegate.getPageBlockArr();
        int i3 = this.nextPhotoScrolling;
        if (currentIndex != i3 && i3 >= 0 && i3 < this.currentPhotos.size()) {
            Object photo = this.currentObjects.get(this.nextPhotoScrolling);
            int nextPhoto = -1;
            if (imagesArr != null && !imagesArr.isEmpty()) {
                nextPhoto = imagesArr.indexOf((MessageObject) photo);
            } else if (pageBlockArr != null && !pageBlockArr.isEmpty()) {
                nextPhoto = pageBlockArr.indexOf((TLRPC.PageBlock) photo);
            } else if (imagesArrLocations != null && !imagesArrLocations.isEmpty()) {
                nextPhoto = imagesArrLocations.indexOf((ImageLocation) photo);
            }
            if (nextPhoto >= 0) {
                this.ignoreChanges = true;
                this.delegate.setCurrentIndex(nextPhoto);
            }
        }
        if (!this.scrolling) {
            this.scrolling = true;
            this.stopedScrolling = false;
        }
        fillImages(true, this.drawDx);
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        this.drawDx = (int) (((float) this.drawDx) - distanceX);
        int min = getMinScrollX();
        int max = getMaxScrollX();
        int i = this.drawDx;
        if (i < min) {
            this.drawDx = min;
        } else if (i > max) {
            this.drawDx = max;
        }
        updateAfterScroll();
        return false;
    }

    public void onLongPress(MotionEvent e) {
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        this.scroll.abortAnimation();
        if (this.currentPhotos.size() < 10) {
            return false;
        }
        this.scroll.fling(this.drawDx, 0, Math.round(velocityX), 0, getMinScrollX(), getMaxScrollX(), 0, 0);
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

    public boolean onTouchEvent(MotionEvent event) {
        boolean z = false;
        if (this.currentPhotos.isEmpty() || getAlpha() != 1.0f) {
            return false;
        }
        if (this.gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)) {
            z = true;
        }
        boolean result = z;
        if (this.scrolling && event.getAction() == 1 && this.scroll.isFinished()) {
            stopScrolling();
        }
        return result;
    }

    private int getMinScrollX() {
        return (-((this.currentPhotos.size() - this.currentImage) - 1)) * (this.itemWidth + (this.itemSpacing * 2));
    }

    private int getMaxScrollX() {
        return this.currentImage * (this.itemWidth + (this.itemSpacing * 2));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        fillImages(false, 0);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int trueWidth;
        int nextTrueWidth;
        int maxItemWidth;
        int count;
        if (this.hasPhotos || !this.imagesToDraw.isEmpty()) {
            float bgAlpha = this.drawAlpha;
            if (!this.animateBackground) {
                bgAlpha = this.hasPhotos ? 1.0f : 0.0f;
            }
            this.backgroundPaint.setAlpha((int) (127.0f * bgAlpha));
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.backgroundPaint);
            if (!this.imagesToDraw.isEmpty()) {
                int count2 = this.imagesToDraw.size();
                int moveX = this.drawDx;
                int maxItemWidth2 = (int) (((float) this.itemWidth) * 2.0f);
                int padding = AndroidUtilities.dp(8.0f);
                ImageLocation object = this.currentPhotos.get(this.currentImage);
                if (object == null || object.photoSize == null) {
                    trueWidth = this.itemHeight;
                } else {
                    trueWidth = Math.max(this.itemWidth, (int) (((float) object.photoSize.w) * (((float) this.itemHeight) / ((float) object.photoSize.h))));
                }
                int trueWidth2 = Math.min(maxItemWidth2, trueWidth);
                float f = this.currentItemProgress;
                int currentPaddings = (int) (((float) (padding * 2)) * f);
                int i = this.itemWidth;
                int trueWidth3 = i + ((int) (((float) (trueWidth2 - i)) * f)) + currentPaddings;
                int trueWidth4 = this.nextImage;
                if (trueWidth4 < 0 || trueWidth4 >= this.currentPhotos.size()) {
                    nextTrueWidth = this.itemWidth;
                } else {
                    ImageLocation object2 = this.currentPhotos.get(this.nextImage);
                    if (object2 == null || object2.photoSize == null) {
                        nextTrueWidth = this.itemHeight;
                    } else {
                        nextTrueWidth = Math.max(this.itemWidth, (int) (((float) object2.photoSize.w) * (((float) this.itemHeight) / ((float) object2.photoSize.h))));
                    }
                }
                int nextTrueWidth2 = Math.min(maxItemWidth2, nextTrueWidth);
                float f2 = this.nextItemProgress;
                int nextPaddings = (int) (((float) (padding * 2)) * f2);
                float f3 = (float) moveX;
                int i2 = this.itemWidth;
                float f4 = bgAlpha;
                int i3 = moveX;
                int moveX2 = (int) (f3 + (((float) (((nextTrueWidth2 + nextPaddings) - i2) / 2)) * f2 * ((float) (this.nextImage > this.currentImage ? -1 : 1))));
                int nextTrueWidth3 = i2 + ((int) (((float) (nextTrueWidth2 - i2)) * f2)) + nextPaddings;
                int startX = (getMeasuredWidth() - trueWidth3) / 2;
                int a = 0;
                while (a < count2) {
                    ImageReceiver receiver = this.imagesToDraw.get(a);
                    int num = receiver.getParam();
                    int i4 = this.currentImage;
                    if (num == i4) {
                        receiver.setImageX(startX + moveX2 + (currentPaddings / 2));
                        receiver.setImageWidth(trueWidth3 - currentPaddings);
                        count = count2;
                        maxItemWidth = maxItemWidth2;
                    } else {
                        int i5 = this.nextImage;
                        if (i5 >= i4) {
                            count = count2;
                            maxItemWidth = maxItemWidth2;
                            if (num < i4) {
                                receiver.setImageX(((receiver.getParam() - this.currentImage) * (this.itemWidth + this.itemSpacing)) + startX + moveX2);
                            } else if (num <= i5) {
                                receiver.setImageX(startX + trueWidth3 + this.itemSpacing + (((receiver.getParam() - this.currentImage) - 1) * (this.itemWidth + this.itemSpacing)) + moveX2);
                            } else {
                                int i6 = this.itemWidth;
                                int i7 = this.itemSpacing;
                                receiver.setImageX(startX + trueWidth3 + this.itemSpacing + (((receiver.getParam() - this.currentImage) - 2) * (i6 + i7)) + i7 + nextTrueWidth3 + moveX2);
                            }
                        } else if (num >= i4) {
                            count = count2;
                            maxItemWidth = maxItemWidth2;
                            receiver.setImageX(startX + trueWidth3 + this.itemSpacing + (((receiver.getParam() - this.currentImage) - 1) * (this.itemWidth + this.itemSpacing)) + moveX2);
                        } else if (num <= i5) {
                            int i8 = this.itemWidth;
                            count = count2;
                            int count3 = this.itemSpacing;
                            receiver.setImageX((((((receiver.getParam() - this.currentImage) + 1) * (i8 + count3)) + startX) - (count3 + nextTrueWidth3)) + moveX2);
                            maxItemWidth = maxItemWidth2;
                        } else {
                            count = count2;
                            receiver.setImageX(((receiver.getParam() - this.currentImage) * (this.itemWidth + this.itemSpacing)) + startX + moveX2);
                            maxItemWidth = maxItemWidth2;
                        }
                        if (num == this.nextImage) {
                            receiver.setImageWidth(nextTrueWidth3 - nextPaddings);
                            receiver.setImageX((int) (receiver.getImageX() + ((float) (nextPaddings / 2))));
                        } else {
                            receiver.setImageWidth(this.itemWidth);
                        }
                    }
                    receiver.setAlpha(this.drawAlpha);
                    receiver.setRoundRadius(AndroidUtilities.dp(2.0f));
                    receiver.draw(canvas);
                    a++;
                    count2 = count;
                    maxItemWidth2 = maxItemWidth;
                }
                Canvas canvas2 = canvas;
                int i9 = count2;
                int i10 = maxItemWidth2;
                long newTime = System.currentTimeMillis();
                long dt = newTime - this.lastUpdateTime;
                if (dt > 17) {
                    dt = 17;
                }
                this.lastUpdateTime = newTime;
                int i11 = this.animateToItem;
                if (i11 >= 0) {
                    float f5 = this.moveLineProgress;
                    if (f5 > 0.0f) {
                        int i12 = moveX2;
                        int i13 = nextTrueWidth3;
                        this.moveLineProgress = f5 - (((float) dt) / (this.animateToItemFast != 0 ? 100.0f : 200.0f));
                        if (i11 == this.currentImage) {
                            float f6 = this.currentItemProgress;
                            if (f6 < 1.0f) {
                                float f7 = f6 + (((float) dt) / 200.0f);
                                this.currentItemProgress = f7;
                                if (f7 > 1.0f) {
                                    this.currentItemProgress = 1.0f;
                                }
                            }
                            int i14 = this.animateToDXStart;
                            this.drawDx = i14 + ((int) Math.ceil((double) (this.currentItemProgress * ((float) (this.animateToDX - i14)))));
                            int i15 = startX;
                        } else {
                            float interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(1.0f - this.moveLineProgress);
                            this.nextItemProgress = interpolation;
                            if (this.stopedScrolling) {
                                float f8 = this.currentItemProgress;
                                if (f8 > 0.0f) {
                                    float f9 = f8 - (((float) dt) / 200.0f);
                                    this.currentItemProgress = f9;
                                    if (f9 < 0.0f) {
                                        this.currentItemProgress = 0.0f;
                                    }
                                }
                                int i16 = this.animateToDXStart;
                                int i17 = startX;
                                this.drawDx = i16 + ((int) Math.ceil((double) (interpolation * ((float) (this.animateToDX - i16)))));
                            } else {
                                this.currentItemProgress = CubicBezierInterpolator.EASE_OUT.getInterpolation(this.moveLineProgress);
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
                    } else {
                        int i18 = nextTrueWidth3;
                        int i19 = startX;
                    }
                    fillImages(true, this.drawDx);
                    invalidate();
                } else {
                    int i20 = nextTrueWidth3;
                    int i21 = startX;
                }
                if (this.scrolling != 0) {
                    float var_ = this.currentItemProgress;
                    if (var_ > 0.0f) {
                        float var_ = var_ - (((float) dt) / 200.0f);
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

    public boolean isAnimationsEnabled() {
        return this.animationsEnabled;
    }

    public void setAnimationsEnabled(boolean animationsEnabled2) {
        if (this.animationsEnabled != animationsEnabled2) {
            this.animationsEnabled = animationsEnabled2;
            if (!animationsEnabled2) {
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

    public void setAnimateBackground(boolean animateBackground2) {
        this.animateBackground = animateBackground2;
    }

    public void reset() {
        this.hasPhotos = false;
        if (this.animationsEnabled) {
            this.drawAlpha = 0.0f;
        }
    }
}
