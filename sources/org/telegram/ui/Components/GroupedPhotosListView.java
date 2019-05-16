package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC.PageBlock;
import org.telegram.tgnet.TLRPC.PhotoSize;

public class GroupedPhotosListView extends View implements OnGestureListener {
    private boolean animateAllLine;
    private int animateToDX;
    private int animateToDXStart;
    private int animateToItem = -1;
    private Paint backgroundPaint = new Paint();
    private long currentGroupId;
    private int currentImage;
    private float currentItemProgress = 1.0f;
    private ArrayList<Object> currentObjects = new ArrayList();
    public ArrayList<ImageLocation> currentPhotos = new ArrayList();
    private GroupedPhotosListViewDelegate delegate;
    private int drawDx;
    private GestureDetector gestureDetector;
    private boolean ignoreChanges;
    private ArrayList<ImageReceiver> imagesToDraw = new ArrayList();
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
    private boolean stopedScrolling;
    private ArrayList<ImageReceiver> unusedReceivers = new ArrayList();

    public interface GroupedPhotosListViewDelegate {
        int getAvatarsDialogId();

        int getCurrentAccount();

        int getCurrentIndex();

        ArrayList<MessageObject> getImagesArr();

        ArrayList<ImageLocation> getImagesArrLocations();

        ArrayList<PageBlock> getPageBlockArr();

        Object getParentObject();

        int getSlideshowMessageId();

        void setCurrentIndex(int i);
    }

    public void onLongPress(MotionEvent motionEvent) {
    }

    public void onShowPress(MotionEvent motionEvent) {
    }

    public GroupedPhotosListView(Context context) {
        super(context);
        this.gestureDetector = new GestureDetector(context, this);
        this.scroll = new Scroller(context);
        this.itemWidth = AndroidUtilities.dp(42.0f);
        this.itemHeight = AndroidUtilities.dp(56.0f);
        this.itemSpacing = AndroidUtilities.dp(1.0f);
        this.itemY = AndroidUtilities.dp(3.0f);
        this.backgroundPaint.setColor(NUM);
    }

    public void clear() {
        this.currentPhotos.clear();
        this.currentObjects.clear();
        this.imagesToDraw.clear();
    }

    /* JADX WARNING: Removed duplicated region for block: B:51:0x00ff  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00fe A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00fe A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00ff  */
    public void fillList() {
        /*
        r17 = this;
        r0 = r17;
        r1 = r0.ignoreChanges;
        r2 = 0;
        if (r1 == 0) goto L_0x000a;
    L_0x0007:
        r0.ignoreChanges = r2;
        return;
    L_0x000a:
        r1 = r0.delegate;
        r1 = r1.getCurrentIndex();
        r3 = r0.delegate;
        r3 = r3.getImagesArrLocations();
        r4 = r0.delegate;
        r4 = r4.getImagesArr();
        r5 = r0.delegate;
        r5 = r5.getPageBlockArr();
        r6 = r0.delegate;
        r6 = r6.getSlideshowMessageId();
        r7 = r0.delegate;
        r7.getCurrentAccount();
        r7 = 0;
        r8 = 1;
        if (r3 == 0) goto L_0x0045;
    L_0x0031:
        r9 = r3.isEmpty();
        if (r9 != 0) goto L_0x0045;
    L_0x0037:
        r7 = r3.get(r1);
        r7 = (org.telegram.messenger.ImageLocation) r7;
        r9 = r3.size();
        r11 = r9;
    L_0x0042:
        r9 = 0;
        goto L_0x00fc;
    L_0x0045:
        if (r4 == 0) goto L_0x00aa;
    L_0x0047:
        r9 = r4.isEmpty();
        if (r9 != 0) goto L_0x00aa;
    L_0x004d:
        r7 = r4.get(r1);
        r7 = (org.telegram.messenger.MessageObject) r7;
        r9 = r7.getGroupIdForUse();
        r11 = r0.currentGroupId;
        r13 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1));
        if (r13 == 0) goto L_0x0064;
    L_0x005d:
        r9 = r7.getGroupIdForUse();
        r0.currentGroupId = r9;
        goto L_0x00c4;
    L_0x0064:
        r9 = r1 + 10;
        r10 = r4.size();
        r9 = java.lang.Math.min(r9, r10);
        r10 = r1;
        r11 = 0;
    L_0x0070:
        if (r10 >= r9) goto L_0x0089;
    L_0x0072:
        r12 = r4.get(r10);
        r12 = (org.telegram.messenger.MessageObject) r12;
        if (r6 != 0) goto L_0x0084;
    L_0x007a:
        r12 = r12.getGroupIdForUse();
        r14 = r0.currentGroupId;
        r16 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1));
        if (r16 != 0) goto L_0x0089;
    L_0x0084:
        r11 = r11 + 1;
        r10 = r10 + 1;
        goto L_0x0070;
    L_0x0089:
        r9 = r1 + -10;
        r9 = java.lang.Math.max(r9, r2);
        r10 = r1 + -1;
    L_0x0091:
        if (r10 < r9) goto L_0x0042;
    L_0x0093:
        r12 = r4.get(r10);
        r12 = (org.telegram.messenger.MessageObject) r12;
        if (r6 != 0) goto L_0x00a5;
    L_0x009b:
        r12 = r12.getGroupIdForUse();
        r14 = r0.currentGroupId;
        r16 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1));
        if (r16 != 0) goto L_0x0042;
    L_0x00a5:
        r11 = r11 + 1;
        r10 = r10 + -1;
        goto L_0x0091;
    L_0x00aa:
        if (r5 == 0) goto L_0x00fa;
    L_0x00ac:
        r9 = r5.isEmpty();
        if (r9 != 0) goto L_0x00fa;
    L_0x00b2:
        r7 = r5.get(r1);
        r7 = (org.telegram.tgnet.TLRPC.PageBlock) r7;
        r9 = r7.groupId;
        r10 = (long) r9;
        r12 = r0.currentGroupId;
        r14 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1));
        if (r14 == 0) goto L_0x00c6;
    L_0x00c1:
        r9 = (long) r9;
        r0.currentGroupId = r9;
    L_0x00c4:
        r9 = 1;
        goto L_0x00fb;
    L_0x00c6:
        r9 = r5.size();
        r10 = r1;
        r11 = 0;
    L_0x00cc:
        if (r10 >= r9) goto L_0x00e2;
    L_0x00ce:
        r12 = r5.get(r10);
        r12 = (org.telegram.tgnet.TLRPC.PageBlock) r12;
        r12 = r12.groupId;
        r12 = (long) r12;
        r14 = r0.currentGroupId;
        r16 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1));
        if (r16 != 0) goto L_0x00e2;
    L_0x00dd:
        r11 = r11 + 1;
        r10 = r10 + 1;
        goto L_0x00cc;
    L_0x00e2:
        r9 = r1 + -1;
    L_0x00e4:
        if (r9 < 0) goto L_0x0042;
    L_0x00e6:
        r10 = r5.get(r9);
        r10 = (org.telegram.tgnet.TLRPC.PageBlock) r10;
        r10 = r10.groupId;
        r12 = (long) r10;
        r14 = r0.currentGroupId;
        r10 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1));
        if (r10 != 0) goto L_0x0042;
    L_0x00f5:
        r11 = r11 + 1;
        r9 = r9 + -1;
        goto L_0x00e4;
    L_0x00fa:
        r9 = 0;
    L_0x00fb:
        r11 = 0;
    L_0x00fc:
        if (r7 != 0) goto L_0x00ff;
    L_0x00fe:
        return;
    L_0x00ff:
        r10 = -1;
        if (r9 != 0) goto L_0x0152;
    L_0x0102:
        r12 = r0.currentPhotos;
        r12 = r12.size();
        if (r11 != r12) goto L_0x0151;
    L_0x010a:
        r11 = r0.currentObjects;
        r11 = r11.indexOf(r7);
        if (r11 != r10) goto L_0x0113;
    L_0x0112:
        goto L_0x0151;
    L_0x0113:
        r11 = r0.currentObjects;
        r7 = r11.indexOf(r7);
        r11 = r0.currentImage;
        if (r11 == r7) goto L_0x0152;
    L_0x011d:
        if (r7 == r10) goto L_0x0152;
    L_0x011f:
        r12 = r0.animateAllLine;
        if (r12 == 0) goto L_0x013f;
    L_0x0123:
        r0.animateToItem = r7;
        r0.nextImage = r7;
        r11 = r11 - r7;
        r7 = r0.itemWidth;
        r12 = r0.itemSpacing;
        r7 = r7 + r12;
        r11 = r11 * r7;
        r0.animateToDX = r11;
        r0.moving = r8;
        r0.animateAllLine = r2;
        r11 = java.lang.System.currentTimeMillis();
        r0.lastUpdateTime = r11;
        r17.invalidate();
        goto L_0x014e;
    L_0x013f:
        r11 = r11 - r7;
        r12 = r0.itemWidth;
        r13 = r0.itemSpacing;
        r12 = r12 + r13;
        r11 = r11 * r12;
        r0.fillImages(r8, r11);
        r0.currentImage = r7;
        r0.moving = r2;
    L_0x014e:
        r0.drawDx = r2;
        goto L_0x0152;
    L_0x0151:
        r9 = 1;
    L_0x0152:
        if (r9 == 0) goto L_0x027f;
    L_0x0154:
        r0.animateAllLine = r2;
        r7 = r0.currentPhotos;
        r7.clear();
        r7 = r0.currentObjects;
        r7.clear();
        if (r3 == 0) goto L_0x0178;
    L_0x0162:
        r7 = r3.isEmpty();
        if (r7 != 0) goto L_0x0178;
    L_0x0168:
        r4 = r0.currentObjects;
        r4.addAll(r3);
        r4 = r0.currentPhotos;
        r4.addAll(r3);
        r0.currentImage = r1;
        r0.animateToItem = r10;
        goto L_0x026a;
    L_0x0178:
        r11 = 0;
        if (r4 == 0) goto L_0x0201;
    L_0x017c:
        r3 = r4.isEmpty();
        if (r3 != 0) goto L_0x0201;
    L_0x0182:
        r13 = r0.currentGroupId;
        r3 = (r13 > r11 ? 1 : (r13 == r11 ? 0 : -1));
        if (r3 != 0) goto L_0x018a;
    L_0x0188:
        if (r6 == 0) goto L_0x026a;
    L_0x018a:
        r3 = r1 + 10;
        r5 = r4.size();
        r3 = java.lang.Math.min(r3, r5);
        r5 = r1;
    L_0x0195:
        r7 = 56;
        if (r5 >= r3) goto L_0x01c4;
    L_0x0199:
        r9 = r4.get(r5);
        r9 = (org.telegram.messenger.MessageObject) r9;
        if (r6 != 0) goto L_0x01ab;
    L_0x01a1:
        r11 = r9.getGroupIdForUse();
        r13 = r0.currentGroupId;
        r15 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1));
        if (r15 != 0) goto L_0x01c4;
    L_0x01ab:
        r11 = r0.currentObjects;
        r11.add(r9);
        r11 = r0.currentPhotos;
        r12 = r9.photoThumbs;
        r7 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r12, r7, r8);
        r9 = r9.photoThumbsObject;
        r7 = org.telegram.messenger.ImageLocation.getForObject(r7, r9);
        r11.add(r7);
        r5 = r5 + 1;
        goto L_0x0195;
    L_0x01c4:
        r0.currentImage = r2;
        r0.animateToItem = r10;
        r3 = r1 + -10;
        r3 = java.lang.Math.max(r3, r2);
        r1 = r1 - r8;
    L_0x01cf:
        if (r1 < r3) goto L_0x026a;
    L_0x01d1:
        r5 = r4.get(r1);
        r5 = (org.telegram.messenger.MessageObject) r5;
        if (r6 != 0) goto L_0x01e3;
    L_0x01d9:
        r9 = r5.getGroupIdForUse();
        r11 = r0.currentGroupId;
        r13 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1));
        if (r13 != 0) goto L_0x026a;
    L_0x01e3:
        r9 = r0.currentObjects;
        r9.add(r2, r5);
        r9 = r0.currentPhotos;
        r10 = r5.photoThumbs;
        r10 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r10, r7, r8);
        r5 = r5.photoThumbsObject;
        r5 = org.telegram.messenger.ImageLocation.getForObject(r10, r5);
        r9.add(r2, r5);
        r5 = r0.currentImage;
        r5 = r5 + r8;
        r0.currentImage = r5;
        r1 = r1 + -1;
        goto L_0x01cf;
    L_0x0201:
        if (r5 == 0) goto L_0x026a;
    L_0x0203:
        r3 = r5.isEmpty();
        if (r3 != 0) goto L_0x026a;
    L_0x0209:
        r3 = r0.currentGroupId;
        r6 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1));
        if (r6 == 0) goto L_0x026a;
    L_0x020f:
        r3 = r5.size();
        r4 = r1;
    L_0x0214:
        if (r4 >= r3) goto L_0x023a;
    L_0x0216:
        r6 = r5.get(r4);
        r6 = (org.telegram.tgnet.TLRPC.PageBlock) r6;
        r7 = r6.groupId;
        r11 = (long) r7;
        r13 = r0.currentGroupId;
        r7 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1));
        if (r7 != 0) goto L_0x023a;
    L_0x0225:
        r7 = r0.currentObjects;
        r7.add(r6);
        r7 = r0.currentPhotos;
        r9 = r6.thumb;
        r6 = r6.thumbObject;
        r6 = org.telegram.messenger.ImageLocation.getForObject(r9, r6);
        r7.add(r6);
        r4 = r4 + 1;
        goto L_0x0214;
    L_0x023a:
        r0.currentImage = r2;
        r0.animateToItem = r10;
        r1 = r1 - r8;
    L_0x023f:
        if (r1 < 0) goto L_0x026a;
    L_0x0241:
        r3 = r5.get(r1);
        r3 = (org.telegram.tgnet.TLRPC.PageBlock) r3;
        r4 = r3.groupId;
        r6 = (long) r4;
        r9 = r0.currentGroupId;
        r4 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1));
        if (r4 != 0) goto L_0x026a;
    L_0x0250:
        r4 = r0.currentObjects;
        r4.add(r2, r3);
        r4 = r0.currentPhotos;
        r6 = r3.thumb;
        r3 = r3.thumbObject;
        r3 = org.telegram.messenger.ImageLocation.getForObject(r6, r3);
        r4.add(r2, r3);
        r3 = r0.currentImage;
        r3 = r3 + r8;
        r0.currentImage = r3;
        r1 = r1 + -1;
        goto L_0x023f;
    L_0x026a:
        r1 = r0.currentPhotos;
        r1 = r1.size();
        if (r1 != r8) goto L_0x027c;
    L_0x0272:
        r1 = r0.currentPhotos;
        r1.clear();
        r1 = r0.currentObjects;
        r1.clear();
    L_0x027c:
        r0.fillImages(r2, r2);
    L_0x027f:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupedPhotosListView.fillList():void");
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
            if (!this.currentPhotos.isEmpty()) {
                if (f < 0.0f && this.currentImage == this.currentPhotos.size() - 1) {
                    return;
                }
                if (f <= 0.0f || this.currentImage != 0) {
                    this.drawDx = (int) (f * ((float) (this.itemWidth + this.itemSpacing)));
                    fillImages(true, this.drawDx);
                }
            }
        }
    }

    private ImageReceiver getFreeReceiver() {
        Object imageReceiver;
        if (this.unusedReceivers.isEmpty()) {
            imageReceiver = new ImageReceiver(this);
        } else {
            imageReceiver = (ImageReceiver) this.unusedReceivers.get(0);
            this.unusedReceivers.remove(0);
        }
        this.imagesToDraw.add(imageReceiver);
        imageReceiver.setCurrentAccount(this.delegate.getCurrentAccount());
        return imageReceiver;
    }

    private void fillImages(boolean z, int i) {
        if (!(z || this.imagesToDraw.isEmpty())) {
            this.unusedReceivers.addAll(this.imagesToDraw);
            this.imagesToDraw.clear();
            this.moving = false;
            this.moveLineProgress = 1.0f;
            this.currentItemProgress = 1.0f;
            this.nextItemProgress = 0.0f;
        }
        invalidate();
        if (getMeasuredWidth() != 0 && !this.currentPhotos.isEmpty()) {
            int size;
            int i2;
            int i3;
            ImageReceiver imageReceiver;
            int measuredWidth = getMeasuredWidth();
            int measuredWidth2 = (getMeasuredWidth() / 2) - (this.itemWidth / 2);
            if (z) {
                size = this.imagesToDraw.size();
                int i4 = 0;
                i2 = Integer.MIN_VALUE;
                i3 = Integer.MAX_VALUE;
                while (i4 < size) {
                    imageReceiver = (ImageReceiver) this.imagesToDraw.get(i4);
                    int param = imageReceiver.getParam();
                    int i5 = param - this.currentImage;
                    int i6 = this.itemWidth;
                    i5 = ((i5 * (this.itemSpacing + i6)) + measuredWidth2) + i;
                    if (i5 > measuredWidth || i5 + i6 < 0) {
                        this.unusedReceivers.add(imageReceiver);
                        this.imagesToDraw.remove(i4);
                        size--;
                        i4--;
                    }
                    i3 = Math.min(i3, param - 1);
                    i2 = Math.max(i2, param + 1);
                    i4++;
                }
            } else {
                i2 = this.currentImage;
                i3 = i2 - 1;
            }
            String str = "avatar_";
            if (i2 != Integer.MIN_VALUE) {
                int size2 = this.currentPhotos.size();
                while (i2 < size2) {
                    size = (((i2 - this.currentImage) * (this.itemWidth + this.itemSpacing)) + measuredWidth2) + i;
                    if (size >= measuredWidth) {
                        break;
                    }
                    Object obj;
                    ImageLocation imageLocation = (ImageLocation) this.currentPhotos.get(i2);
                    imageReceiver = getFreeReceiver();
                    imageReceiver.setImageCoords(size, this.itemY, this.itemWidth, this.itemHeight);
                    if (this.currentObjects.get(0) instanceof MessageObject) {
                        obj = this.currentObjects.get(i2);
                    } else if (this.currentObjects.get(0) instanceof PageBlock) {
                        obj = this.delegate.getParentObject();
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(str);
                        stringBuilder.append(this.delegate.getAvatarsDialogId());
                        obj = stringBuilder.toString();
                    }
                    imageReceiver.setImage(null, null, imageLocation, "80_80", 0, null, obj, 1);
                    imageReceiver.setParam(i2);
                    i2++;
                }
            }
            if (i3 != Integer.MAX_VALUE) {
                while (i3 >= 0) {
                    measuredWidth = i3 - this.currentImage;
                    int i7 = this.itemWidth;
                    measuredWidth = (((measuredWidth * (this.itemSpacing + i7)) + measuredWidth2) + i) + i7;
                    if (measuredWidth > 0) {
                        Object obj2;
                        ImageLocation imageLocation2 = (ImageLocation) this.currentPhotos.get(i3);
                        ImageReceiver freeReceiver = getFreeReceiver();
                        freeReceiver.setImageCoords(measuredWidth, this.itemY, this.itemWidth, this.itemHeight);
                        if (this.currentObjects.get(0) instanceof MessageObject) {
                            obj2 = this.currentObjects.get(i3);
                        } else if (this.currentObjects.get(0) instanceof PageBlock) {
                            obj2 = this.delegate.getParentObject();
                        } else {
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(str);
                            stringBuilder2.append(this.delegate.getAvatarsDialogId());
                            obj2 = stringBuilder2.toString();
                        }
                        freeReceiver.setImage(null, null, imageLocation2, "80_80", 0, null, obj2, 1);
                        freeReceiver.setParam(i3);
                        i3--;
                    } else {
                        return;
                    }
                }
            }
        }
    }

    public boolean onDown(MotionEvent motionEvent) {
        if (!this.scroll.isFinished()) {
            this.scroll.abortAnimation();
        }
        this.animateToItem = -1;
        return true;
    }

    public boolean onSingleTapUp(MotionEvent motionEvent) {
        int currentIndex = this.delegate.getCurrentIndex();
        ArrayList imagesArrLocations = this.delegate.getImagesArrLocations();
        ArrayList imagesArr = this.delegate.getImagesArr();
        ArrayList pageBlockArr = this.delegate.getPageBlockArr();
        stopScrolling();
        int size = this.imagesToDraw.size();
        for (int i = 0; i < size; i++) {
            ImageReceiver imageReceiver = (ImageReceiver) this.imagesToDraw.get(i);
            if (imageReceiver.isInsideImage(motionEvent.getX(), motionEvent.getY())) {
                int param = imageReceiver.getParam();
                if (param < 0 || param >= this.currentObjects.size()) {
                    return true;
                }
                if (imagesArr != null && !imagesArr.isEmpty()) {
                    param = imagesArr.indexOf((MessageObject) this.currentObjects.get(param));
                    if (currentIndex == param) {
                        return true;
                    }
                    this.moveLineProgress = 1.0f;
                    this.animateAllLine = true;
                    this.delegate.setCurrentIndex(param);
                    return false;
                } else if (pageBlockArr == null || pageBlockArr.isEmpty()) {
                    if (!(imagesArrLocations == null || imagesArrLocations.isEmpty())) {
                        param = imagesArrLocations.indexOf((ImageLocation) this.currentObjects.get(param));
                        if (currentIndex == param) {
                            return true;
                        }
                        this.moveLineProgress = 1.0f;
                        this.animateAllLine = true;
                        this.delegate.setCurrentIndex(param);
                    }
                    return false;
                } else {
                    param = pageBlockArr.indexOf((PageBlock) this.currentObjects.get(param));
                    if (currentIndex == param) {
                        return true;
                    }
                    this.moveLineProgress = 1.0f;
                    this.animateAllLine = true;
                    this.delegate.setCurrentIndex(param);
                    return false;
                }
            }
        }
        return false;
    }

    private void updateAfterScroll() {
        int i = this.drawDx;
        int abs = Math.abs(i);
        int i2 = this.itemWidth;
        int i3 = i2 / 2;
        int i4 = this.itemSpacing;
        int i5 = -1;
        if (abs > i3 + i4) {
            if (i > 0) {
                abs = i - ((i2 / 2) + i4);
                i = 1;
            } else {
                abs = i + ((i2 / 2) + i4);
                i = -1;
            }
            i += abs / (this.itemWidth + (this.itemSpacing * 2));
        } else {
            i = 0;
        }
        this.nextPhotoScrolling = this.currentImage - i;
        i = this.delegate.getCurrentIndex();
        ArrayList imagesArrLocations = this.delegate.getImagesArrLocations();
        ArrayList imagesArr = this.delegate.getImagesArr();
        ArrayList pageBlockArr = this.delegate.getPageBlockArr();
        i4 = this.nextPhotoScrolling;
        if (i != i4 && i4 >= 0 && i4 < this.currentPhotos.size()) {
            Object obj = this.currentObjects.get(this.nextPhotoScrolling);
            if (imagesArr != null && !imagesArr.isEmpty()) {
                i5 = imagesArr.indexOf((MessageObject) obj);
            } else if (pageBlockArr != null && !pageBlockArr.isEmpty()) {
                i5 = pageBlockArr.indexOf((PageBlock) obj);
            } else if (!(imagesArrLocations == null || imagesArrLocations.isEmpty())) {
                i5 = imagesArrLocations.indexOf((ImageLocation) obj);
            }
            if (i5 >= 0) {
                this.ignoreChanges = true;
                this.delegate.setCurrentIndex(i5);
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
        if (this.currentPhotos.size() >= 10) {
            this.scroll.fling(this.drawDx, 0, Math.round(f), 0, getMinScrollX(), getMaxScrollX(), 0, 0);
        }
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
            i = this.nextPhotoScrolling;
            this.animateToItem = i;
            this.nextImage = i;
            this.animateToDX = (this.currentImage - i) * (this.itemWidth + this.itemSpacing);
            this.animateToDXStart = this.drawDx;
            this.moveLineProgress = 1.0f;
            this.nextPhotoScrolling = -1;
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

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        fillImages(false, 0);
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (!this.imagesToDraw.isEmpty()) {
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.backgroundPaint);
            int size = this.imagesToDraw.size();
            int i = this.drawDx;
            int i2 = (int) (((float) this.itemWidth) * 2.0f);
            int dp = AndroidUtilities.dp(8.0f);
            ImageLocation imageLocation = (ImageLocation) this.currentPhotos.get(this.currentImage);
            if (imageLocation != null) {
                int max;
                float f;
                PhotoSize photoSize = imageLocation.photoSize;
                if (photoSize != null) {
                    max = Math.max(this.itemWidth, (int) (((float) photoSize.w) * (((float) this.itemHeight) / ((float) photoSize.h))));
                } else {
                    max = this.itemHeight;
                }
                max = Math.min(i2, max);
                float f2 = (float) (dp * 2);
                float f3 = this.currentItemProgress;
                int i3 = (int) (f2 * f3);
                int i4 = this.itemWidth;
                i4 = (i4 + ((int) (((float) (max - i4)) * f3))) + i3;
                max = this.nextImage;
                if (max < 0 || max >= this.currentPhotos.size()) {
                    max = this.itemWidth;
                } else {
                    photoSize = ((ImageLocation) this.currentPhotos.get(this.nextImage)).photoSize;
                    if (photoSize != null) {
                        max = Math.max(this.itemWidth, (int) (((float) photoSize.w) * (((float) this.itemHeight) / ((float) photoSize.h))));
                    } else {
                        max = this.itemHeight;
                    }
                }
                i2 = Math.min(i2, max);
                float f4 = this.nextItemProgress;
                dp = (int) (f2 * f4);
                i = (int) (((float) i) + ((((float) (((i2 + dp) - this.itemWidth) / 2)) * f4) * ((float) (this.nextImage > this.currentImage ? -1 : 1))));
                max = this.itemWidth;
                max = (max + ((int) (((float) (i2 - max)) * this.nextItemProgress))) + dp;
                i2 = (getMeasuredWidth() - i4) / 2;
                for (int i5 = 0; i5 < size; i5++) {
                    ImageReceiver imageReceiver = (ImageReceiver) this.imagesToDraw.get(i5);
                    int param = imageReceiver.getParam();
                    int i6 = this.currentImage;
                    if (param == i6) {
                        imageReceiver.setImageX((i2 + i) + (i3 / 2));
                        imageReceiver.setImageWidth(i4 - i3);
                    } else {
                        int i7 = this.nextImage;
                        int i8;
                        if (i7 < i6) {
                            if (param >= i6) {
                                imageReceiver.setImageX((((i2 + i4) + this.itemSpacing) + (((imageReceiver.getParam() - this.currentImage) - 1) * (this.itemWidth + this.itemSpacing))) + i);
                            } else if (param <= i7) {
                                i6 = (imageReceiver.getParam() - this.currentImage) + 1;
                                i7 = this.itemWidth;
                                i8 = this.itemSpacing;
                                imageReceiver.setImageX((((i6 * (i7 + i8)) + i2) - (i8 + max)) + i);
                            } else {
                                imageReceiver.setImageX((((imageReceiver.getParam() - this.currentImage) * (this.itemWidth + this.itemSpacing)) + i2) + i);
                            }
                        } else if (param < i6) {
                            imageReceiver.setImageX((((imageReceiver.getParam() - this.currentImage) * (this.itemWidth + this.itemSpacing)) + i2) + i);
                        } else if (param <= i7) {
                            imageReceiver.setImageX((((i2 + i4) + this.itemSpacing) + (((imageReceiver.getParam() - this.currentImage) - 1) * (this.itemWidth + this.itemSpacing))) + i);
                        } else {
                            int i9 = (i2 + i4) + this.itemSpacing;
                            i8 = (imageReceiver.getParam() - this.currentImage) - 2;
                            i6 = this.itemWidth;
                            i7 = this.itemSpacing;
                            imageReceiver.setImageX(((i9 + (i8 * (i6 + i7))) + (i7 + max)) + i);
                        }
                        if (param == this.nextImage) {
                            imageReceiver.setImageWidth(max - dp);
                            imageReceiver.setImageX(imageReceiver.getImageX() + (dp / 2));
                        } else {
                            imageReceiver.setImageWidth(this.itemWidth);
                        }
                    }
                    imageReceiver.draw(canvas);
                }
                long currentTimeMillis = System.currentTimeMillis();
                long j = currentTimeMillis - this.lastUpdateTime;
                if (j > 17) {
                    j = 17;
                }
                this.lastUpdateTime = currentTimeMillis;
                size = this.animateToItem;
                if (size >= 0) {
                    f3 = this.moveLineProgress;
                    if (f3 > 0.0f) {
                        float f5 = ((float) j) / 200.0f;
                        this.moveLineProgress = f3 - f5;
                        if (size == this.currentImage) {
                            f = this.currentItemProgress;
                            if (f < 1.0f) {
                                this.currentItemProgress = f + f5;
                                if (this.currentItemProgress > 1.0f) {
                                    this.currentItemProgress = 1.0f;
                                }
                            }
                            size = this.animateToDXStart;
                            this.drawDx = size + ((int) Math.ceil((double) (this.currentItemProgress * ((float) (this.animateToDX - size)))));
                        } else {
                            this.nextItemProgress = CubicBezierInterpolator.EASE_OUT.getInterpolation(1.0f - this.moveLineProgress);
                            if (this.stopedScrolling) {
                                f = this.currentItemProgress;
                                if (f > 0.0f) {
                                    this.currentItemProgress = f - f5;
                                    if (this.currentItemProgress < 0.0f) {
                                        this.currentItemProgress = 0.0f;
                                    }
                                }
                                size = this.animateToDXStart;
                                this.drawDx = size + ((int) Math.ceil((double) (this.nextItemProgress * ((float) (this.animateToDX - size)))));
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
                        }
                    }
                    fillImages(true, this.drawDx);
                    invalidate();
                }
                if (this.scrolling) {
                    f = this.currentItemProgress;
                    if (f > 0.0f) {
                        this.currentItemProgress = f - (((float) j) / 200.0f);
                        if (this.currentItemProgress < 0.0f) {
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
}
