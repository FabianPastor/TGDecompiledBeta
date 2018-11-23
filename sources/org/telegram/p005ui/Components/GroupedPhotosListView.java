package org.telegram.p005ui.Components;

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
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessageObject;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.PageBlock;
import org.telegram.tgnet.TLRPC.PhotoSize;

/* renamed from: org.telegram.ui.Components.GroupedPhotosListView */
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
    public ArrayList<TLObject> currentPhotos = new ArrayList();
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

    /* renamed from: org.telegram.ui.Components.GroupedPhotosListView$GroupedPhotosListViewDelegate */
    public interface GroupedPhotosListViewDelegate {
        int getAvatarsDialogId();

        int getCurrentAccount();

        int getCurrentIndex();

        ArrayList<MessageObject> getImagesArr();

        ArrayList<FileLocation> getImagesArrLocations();

        ArrayList<PageBlock> getPageBlockArr();

        Object getParentObject();

        int getSlideshowMessageId();

        void setCurrentIndex(int i);
    }

    public GroupedPhotosListView(Context context) {
        super(context);
        this.gestureDetector = new GestureDetector(context, this);
        this.scroll = new Scroller(context);
        this.itemWidth = AndroidUtilities.m9dp(42.0f);
        this.itemHeight = AndroidUtilities.m9dp(56.0f);
        this.itemSpacing = AndroidUtilities.m9dp(1.0f);
        this.itemY = AndroidUtilities.m9dp(3.0f);
        this.backgroundPaint.setColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
    }

    public void clear() {
        this.currentPhotos.clear();
        this.currentObjects.clear();
        this.imagesToDraw.clear();
    }

    public void fillList() {
        if (this.ignoreChanges) {
            this.ignoreChanges = false;
            return;
        }
        int max;
        int a;
        MessageObject object;
        int min;
        int size;
        int currentIndex = this.delegate.getCurrentIndex();
        ArrayList<FileLocation> imagesArrLocations = this.delegate.getImagesArrLocations();
        ArrayList<MessageObject> imagesArr = this.delegate.getImagesArr();
        ArrayList<PageBlock> pageBlockArr = this.delegate.getPageBlockArr();
        int slideshowMessageId = this.delegate.getSlideshowMessageId();
        boolean changed = false;
        int newCount = 0;
        Object currentObject = null;
        if (imagesArrLocations == null || imagesArrLocations.isEmpty()) {
            if (imagesArr != null && !imagesArr.isEmpty()) {
                MessageObject messageObject = (MessageObject) imagesArr.get(currentIndex);
                MessageObject currentObject2 = messageObject;
                if (messageObject.getGroupIdForUse() == this.currentGroupId) {
                    max = Math.min(currentIndex + 10, imagesArr.size());
                    for (a = currentIndex; a < max; a++) {
                        object = (MessageObject) imagesArr.get(a);
                        if (slideshowMessageId == 0 && object.getGroupIdForUse() != this.currentGroupId) {
                            break;
                        }
                        newCount++;
                    }
                    min = Math.max(currentIndex - 10, 0);
                    for (a = currentIndex - 1; a >= min; a--) {
                        object = (MessageObject) imagesArr.get(a);
                        if (slideshowMessageId == 0 && object.getGroupIdForUse() != this.currentGroupId) {
                            break;
                        }
                        newCount++;
                    }
                } else {
                    changed = true;
                    this.currentGroupId = messageObject.getGroupIdForUse();
                }
            } else if (!(pageBlockArr == null || pageBlockArr.isEmpty())) {
                PageBlock pageBlock = (PageBlock) pageBlockArr.get(currentIndex);
                PageBlock currentObject22 = pageBlock;
                if (((long) pageBlock.groupId) != this.currentGroupId) {
                    changed = true;
                    this.currentGroupId = (long) pageBlock.groupId;
                } else {
                    a = currentIndex;
                    size = pageBlockArr.size();
                    while (a < size && ((long) ((PageBlock) pageBlockArr.get(a)).groupId) == this.currentGroupId) {
                        newCount++;
                        a++;
                    }
                    a = currentIndex - 1;
                    while (a >= 0 && ((long) ((PageBlock) pageBlockArr.get(a)).groupId) == this.currentGroupId) {
                        newCount++;
                        a--;
                    }
                }
            }
        } else {
            FileLocation location = (FileLocation) imagesArrLocations.get(currentIndex);
            newCount = imagesArrLocations.size();
            currentObject22 = location;
        }
        if (currentObject22 != null) {
            if (!changed) {
                if (newCount != this.currentPhotos.size() || this.currentObjects.indexOf(currentObject22) == -1) {
                    changed = true;
                } else {
                    int newImageIndex = this.currentObjects.indexOf(currentObject22);
                    if (!(this.currentImage == newImageIndex || newImageIndex == -1)) {
                        if (this.animateAllLine) {
                            this.animateToItem = newImageIndex;
                            this.nextImage = newImageIndex;
                            this.animateToDX = (this.currentImage - newImageIndex) * (this.itemWidth + this.itemSpacing);
                            this.moving = true;
                            this.animateAllLine = false;
                            this.lastUpdateTime = System.currentTimeMillis();
                            invalidate();
                        } else {
                            fillImages(true, (this.currentImage - newImageIndex) * (this.itemWidth + this.itemSpacing));
                            this.currentImage = newImageIndex;
                            this.moving = false;
                        }
                        this.drawDx = 0;
                    }
                }
            }
            if (changed) {
                this.animateAllLine = false;
                this.currentPhotos.clear();
                this.currentObjects.clear();
                if (imagesArrLocations != null && !imagesArrLocations.isEmpty()) {
                    this.currentObjects.addAll(imagesArrLocations);
                    this.currentPhotos.addAll(imagesArrLocations);
                    this.currentImage = currentIndex;
                    this.animateToItem = -1;
                } else if (imagesArr == null || imagesArr.isEmpty()) {
                    if (pageBlockArr != null && !pageBlockArr.isEmpty() && this.currentGroupId != 0) {
                        PageBlock object2;
                        size = pageBlockArr.size();
                        for (a = currentIndex; a < size; a++) {
                            object2 = (PageBlock) pageBlockArr.get(a);
                            if (((long) object2.groupId) != this.currentGroupId) {
                                break;
                            }
                            this.currentObjects.add(object2);
                            this.currentPhotos.add(object2.thumb);
                        }
                        this.currentImage = 0;
                        this.animateToItem = -1;
                        for (a = currentIndex - 1; a >= 0; a--) {
                            object2 = (PageBlock) pageBlockArr.get(a);
                            if (((long) object2.groupId) != this.currentGroupId) {
                                break;
                            }
                            this.currentObjects.add(0, object2);
                            this.currentPhotos.add(0, object2.thumb);
                            this.currentImage++;
                        }
                    }
                } else if (this.currentGroupId != 0 || slideshowMessageId != 0) {
                    max = Math.min(currentIndex + 10, imagesArr.size());
                    for (a = currentIndex; a < max; a++) {
                        object = (MessageObject) imagesArr.get(a);
                        if (slideshowMessageId == 0 && object.getGroupIdForUse() != this.currentGroupId) {
                            break;
                        }
                        this.currentObjects.add(object);
                        this.currentPhotos.add(FileLoader.getClosestPhotoSizeWithSize(object.photoThumbs, 56, true));
                    }
                    this.currentImage = 0;
                    this.animateToItem = -1;
                    min = Math.max(currentIndex - 10, 0);
                    for (a = currentIndex - 1; a >= min; a--) {
                        object = (MessageObject) imagesArr.get(a);
                        if (slideshowMessageId == 0 && object.getGroupIdForUse() != this.currentGroupId) {
                            break;
                        }
                        this.currentObjects.add(0, object);
                        this.currentPhotos.add(0, FileLoader.getClosestPhotoSizeWithSize(object.photoThumbs, 56, true));
                        this.currentImage++;
                    }
                }
                if (this.currentPhotos.size() == 1) {
                    this.currentPhotos.clear();
                    this.currentObjects.clear();
                }
                fillImages(false, 0);
            }
        }
    }

    public void setMoveProgress(float progress) {
        if (!this.scrolling && this.animateToItem < 0) {
            if (progress > 0.0f) {
                this.nextImage = this.currentImage - 1;
            } else {
                this.nextImage = this.currentImage + 1;
            }
            if (this.nextImage < 0 || this.nextImage >= this.currentPhotos.size()) {
                this.currentItemProgress = 1.0f;
            } else {
                this.currentItemProgress = 1.0f - Math.abs(progress);
            }
            this.nextItemProgress = 1.0f - this.currentItemProgress;
            this.moving = progress != 0.0f;
            invalidate();
            if (!this.currentPhotos.isEmpty()) {
                if (progress < 0.0f && this.currentImage == this.currentPhotos.size() - 1) {
                    return;
                }
                if (progress <= 0.0f || this.currentImage != 0) {
                    this.drawDx = (int) (((float) (this.itemWidth + this.itemSpacing)) * progress);
                    fillImages(true, this.drawDx);
                }
            }
        }
    }

    private ImageReceiver getFreeReceiver() {
        ImageReceiver receiver;
        if (this.unusedReceivers.isEmpty()) {
            receiver = new ImageReceiver(this);
        } else {
            receiver = (ImageReceiver) this.unusedReceivers.get(0);
            this.unusedReceivers.remove(0);
        }
        this.imagesToDraw.add(receiver);
        receiver.setCurrentAccount(this.delegate.getCurrentAccount());
        return receiver;
    }

    private void fillImages(boolean move, int dx) {
        if (!(move || this.imagesToDraw.isEmpty())) {
            this.unusedReceivers.addAll(this.imagesToDraw);
            this.imagesToDraw.clear();
            this.moving = false;
            this.moveLineProgress = 1.0f;
            this.currentItemProgress = 1.0f;
            this.nextItemProgress = 0.0f;
        }
        invalidate();
        if (getMeasuredWidth() != 0 && !this.currentPhotos.isEmpty()) {
            int addRightIndex;
            int addLeftIndex;
            int count;
            int a;
            ImageReceiver receiver;
            int x;
            TLObject location;
            Object parent;
            int width = getMeasuredWidth();
            int startX = (getMeasuredWidth() / 2) - (this.itemWidth / 2);
            if (move) {
                addRightIndex = Integer.MIN_VALUE;
                addLeftIndex = ConnectionsManager.DEFAULT_DATACENTER_ID;
                count = this.imagesToDraw.size();
                a = 0;
                while (a < count) {
                    receiver = (ImageReceiver) this.imagesToDraw.get(a);
                    int num = receiver.getParam();
                    x = (((num - this.currentImage) * (this.itemWidth + this.itemSpacing)) + startX) + dx;
                    if (x > width || this.itemWidth + x < 0) {
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
                count = this.currentPhotos.size();
                for (a = addRightIndex; a < count; a++) {
                    x = (((a - this.currentImage) * (this.itemWidth + this.itemSpacing)) + startX) + dx;
                    if (x >= width) {
                        break;
                    }
                    location = (TLObject) this.currentPhotos.get(a);
                    if (location instanceof PhotoSize) {
                        location = ((PhotoSize) location).location;
                    }
                    receiver = getFreeReceiver();
                    receiver.setImageCoords(x, this.itemY, this.itemWidth, this.itemHeight);
                    if (this.currentObjects.get(0) instanceof MessageObject) {
                        parent = this.currentObjects.get(a);
                    } else if (this.currentObjects.get(0) instanceof PageBlock) {
                        parent = this.delegate.getParentObject();
                    } else {
                        parent = "avatar_" + this.delegate.getAvatarsDialogId();
                    }
                    receiver.setImage(null, null, null, null, (FileLocation) location, "80_80", 0, null, parent, 1);
                    receiver.setParam(a);
                }
            }
            if (addLeftIndex != ConnectionsManager.DEFAULT_DATACENTER_ID) {
                a = addLeftIndex;
                while (a >= 0) {
                    x = ((((a - this.currentImage) * (this.itemWidth + this.itemSpacing)) + startX) + dx) + this.itemWidth;
                    if (x > 0) {
                        location = (TLObject) this.currentPhotos.get(a);
                        if (location instanceof PhotoSize) {
                            location = ((PhotoSize) location).location;
                        }
                        receiver = getFreeReceiver();
                        receiver.setImageCoords(x, this.itemY, this.itemWidth, this.itemHeight);
                        if (this.currentObjects.get(0) instanceof MessageObject) {
                            parent = this.currentObjects.get(a);
                        } else if (this.currentObjects.get(0) instanceof PageBlock) {
                            parent = this.delegate.getParentObject();
                        } else {
                            parent = "avatar_" + this.delegate.getAvatarsDialogId();
                        }
                        receiver.setImage(null, null, null, null, (FileLocation) location, "80_80", 0, null, parent, 1);
                        receiver.setParam(a);
                        a--;
                    } else {
                        return;
                    }
                }
            }
        }
    }

    public boolean onDown(MotionEvent e) {
        if (!this.scroll.isFinished()) {
            this.scroll.abortAnimation();
        }
        this.animateToItem = -1;
        return true;
    }

    public void onShowPress(MotionEvent e) {
    }

    public boolean onSingleTapUp(MotionEvent e) {
        int currentIndex = this.delegate.getCurrentIndex();
        ArrayList<FileLocation> imagesArrLocations = this.delegate.getImagesArrLocations();
        ArrayList<MessageObject> imagesArr = this.delegate.getImagesArr();
        ArrayList<PageBlock> pageBlockArr = this.delegate.getPageBlockArr();
        stopScrolling();
        int count = this.imagesToDraw.size();
        for (int a = 0; a < count; a++) {
            ImageReceiver receiver = (ImageReceiver) this.imagesToDraw.get(a);
            if (receiver.isInsideImage(e.getX(), e.getY())) {
                int num = receiver.getParam();
                if (num < 0 || num >= this.currentObjects.size()) {
                    return true;
                }
                int idx;
                if (imagesArr != null && !imagesArr.isEmpty()) {
                    idx = imagesArr.indexOf((MessageObject) this.currentObjects.get(num));
                    if (currentIndex == idx) {
                        return true;
                    }
                    this.moveLineProgress = 1.0f;
                    this.animateAllLine = true;
                    this.delegate.setCurrentIndex(idx);
                    return false;
                } else if (pageBlockArr == null || pageBlockArr.isEmpty()) {
                    if (!(imagesArrLocations == null || imagesArrLocations.isEmpty())) {
                        idx = imagesArrLocations.indexOf((FileLocation) this.currentObjects.get(num));
                        if (currentIndex == idx) {
                            return true;
                        }
                        this.moveLineProgress = 1.0f;
                        this.animateAllLine = true;
                        this.delegate.setCurrentIndex(idx);
                    }
                    return false;
                } else {
                    idx = pageBlockArr.indexOf((PageBlock) this.currentObjects.get(num));
                    if (currentIndex == idx) {
                        return true;
                    }
                    this.moveLineProgress = 1.0f;
                    this.animateAllLine = true;
                    this.delegate.setCurrentIndex(idx);
                    return false;
                }
            }
        }
        return false;
    }

    private void updateAfterScroll() {
        int indexChange = 0;
        int dx = this.drawDx;
        if (Math.abs(dx) > (this.itemWidth / 2) + this.itemSpacing) {
            if (dx > 0) {
                dx -= (this.itemWidth / 2) + this.itemSpacing;
                indexChange = 0 + 1;
            } else {
                dx += (this.itemWidth / 2) + this.itemSpacing;
                indexChange = 0 - 1;
            }
            indexChange += dx / (this.itemWidth + (this.itemSpacing * 2));
        }
        this.nextPhotoScrolling = this.currentImage - indexChange;
        int currentIndex = this.delegate.getCurrentIndex();
        ArrayList<FileLocation> imagesArrLocations = this.delegate.getImagesArrLocations();
        ArrayList<MessageObject> imagesArr = this.delegate.getImagesArr();
        ArrayList<PageBlock> pageBlockArr = this.delegate.getPageBlockArr();
        if (currentIndex != this.nextPhotoScrolling && this.nextPhotoScrolling >= 0 && this.nextPhotoScrolling < this.currentPhotos.size()) {
            MessageObject photo = this.currentObjects.get(this.nextPhotoScrolling);
            int nextPhoto = -1;
            if (imagesArr != null && !imagesArr.isEmpty()) {
                nextPhoto = imagesArr.indexOf(photo);
            } else if (pageBlockArr != null && !pageBlockArr.isEmpty()) {
                nextPhoto = pageBlockArr.indexOf((PageBlock) photo);
            } else if (!(imagesArrLocations == null || imagesArrLocations.isEmpty())) {
                nextPhoto = imagesArrLocations.indexOf((FileLocation) photo);
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
        if (this.drawDx < min) {
            this.drawDx = min;
        } else if (this.drawDx > max) {
            this.drawDx = max;
        }
        updateAfterScroll();
        return false;
    }

    public void onLongPress(MotionEvent e) {
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        this.scroll.abortAnimation();
        if (this.currentPhotos.size() >= 10) {
            this.scroll.fling(this.drawDx, 0, Math.round(velocityX), 0, getMinScrollX(), getMaxScrollX(), 0, 0);
        }
        return false;
    }

    private void stopScrolling() {
        this.scrolling = false;
        if (!this.scroll.isFinished()) {
            this.scroll.abortAnimation();
        }
        if (this.nextPhotoScrolling >= 0 && this.nextPhotoScrolling < this.currentObjects.size()) {
            this.stopedScrolling = true;
            int i = this.nextPhotoScrolling;
            this.animateToItem = i;
            this.nextImage = i;
            this.animateToDX = (this.currentImage - this.nextPhotoScrolling) * (this.itemWidth + this.itemSpacing);
            this.animateToDXStart = this.drawDx;
            this.moveLineProgress = 1.0f;
            this.nextPhotoScrolling = -1;
        }
        invalidate();
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;
        if (!this.currentPhotos.isEmpty() && getAlpha() == 1.0f) {
            if (this.gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)) {
                result = true;
            }
            if (this.scrolling && event.getAction() == 1 && this.scroll.isFinished()) {
                stopScrolling();
            }
        }
        return result;
    }

    private int getMinScrollX() {
        return (-((this.currentPhotos.size() - this.currentImage) - 1)) * (this.itemWidth + (this.itemSpacing * 2));
    }

    private int getMaxScrollX() {
        return this.currentImage * (this.itemWidth + (this.itemSpacing * 2));
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        fillImages(false, 0);
    }

    protected void onDraw(Canvas canvas) {
        if (!this.imagesToDraw.isEmpty()) {
            PhotoSize photoSize;
            int trueWidth;
            int nextTrueWidth;
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.backgroundPaint);
            int count = this.imagesToDraw.size();
            int moveX = this.drawDx;
            int maxItemWidth = (int) (((float) this.itemWidth) * 2.0f);
            int padding = AndroidUtilities.m9dp(8.0f);
            TLObject object = (TLObject) this.currentPhotos.get(this.currentImage);
            if (object instanceof PhotoSize) {
                photoSize = (PhotoSize) object;
                trueWidth = Math.max(this.itemWidth, (int) (((float) photoSize.f108w) * (((float) this.itemHeight) / ((float) photoSize.f107h))));
            } else {
                trueWidth = this.itemHeight;
            }
            int currentPaddings = (int) (((float) (padding * 2)) * this.currentItemProgress);
            trueWidth = (this.itemWidth + ((int) (((float) (Math.min(maxItemWidth, trueWidth) - this.itemWidth)) * this.currentItemProgress))) + currentPaddings;
            if (this.nextImage < 0 || this.nextImage >= this.currentPhotos.size()) {
                nextTrueWidth = this.itemWidth;
            } else {
                object = (TLObject) this.currentPhotos.get(this.nextImage);
                if (object instanceof PhotoSize) {
                    photoSize = (PhotoSize) object;
                    nextTrueWidth = Math.max(this.itemWidth, (int) (((float) photoSize.f108w) * (((float) this.itemHeight) / ((float) photoSize.f107h))));
                } else {
                    nextTrueWidth = this.itemHeight;
                }
            }
            nextTrueWidth = Math.min(maxItemWidth, nextTrueWidth);
            int nextPaddings = (int) (((float) (padding * 2)) * this.nextItemProgress);
            moveX = (int) ((((float) (this.nextImage > this.currentImage ? -1 : 1)) * (this.nextItemProgress * ((float) (((nextTrueWidth + nextPaddings) - this.itemWidth) / 2)))) + ((float) moveX));
            nextTrueWidth = (this.itemWidth + ((int) (((float) (nextTrueWidth - this.itemWidth)) * this.nextItemProgress))) + nextPaddings;
            int startX = (getMeasuredWidth() - trueWidth) / 2;
            for (int a = 0; a < count; a++) {
                ImageReceiver receiver = (ImageReceiver) this.imagesToDraw.get(a);
                int num = receiver.getParam();
                if (num == this.currentImage) {
                    receiver.setImageX((startX + moveX) + (currentPaddings / 2));
                    receiver.setImageWidth(trueWidth - currentPaddings);
                } else {
                    if (this.nextImage < this.currentImage) {
                        if (num >= this.currentImage) {
                            receiver.setImageX((((startX + trueWidth) + this.itemSpacing) + (((receiver.getParam() - this.currentImage) - 1) * (this.itemWidth + this.itemSpacing))) + moveX);
                        } else if (num <= this.nextImage) {
                            receiver.setImageX((((((receiver.getParam() - this.currentImage) + 1) * (this.itemWidth + this.itemSpacing)) + startX) - (this.itemSpacing + nextTrueWidth)) + moveX);
                        } else {
                            receiver.setImageX((((receiver.getParam() - this.currentImage) * (this.itemWidth + this.itemSpacing)) + startX) + moveX);
                        }
                    } else if (num < this.currentImage) {
                        receiver.setImageX((((receiver.getParam() - this.currentImage) * (this.itemWidth + this.itemSpacing)) + startX) + moveX);
                    } else if (num <= this.nextImage) {
                        receiver.setImageX((((startX + trueWidth) + this.itemSpacing) + (((receiver.getParam() - this.currentImage) - 1) * (this.itemWidth + this.itemSpacing))) + moveX);
                    } else {
                        receiver.setImageX(((((startX + trueWidth) + this.itemSpacing) + (((receiver.getParam() - this.currentImage) - 2) * (this.itemWidth + this.itemSpacing))) + (this.itemSpacing + nextTrueWidth)) + moveX);
                    }
                    if (num == this.nextImage) {
                        receiver.setImageWidth(nextTrueWidth - nextPaddings);
                        receiver.setImageX(receiver.getImageX() + (nextPaddings / 2));
                    } else {
                        receiver.setImageWidth(this.itemWidth);
                    }
                }
                receiver.draw(canvas);
            }
            long newTime = System.currentTimeMillis();
            long dt = newTime - this.lastUpdateTime;
            if (dt > 17) {
                dt = 17;
            }
            this.lastUpdateTime = newTime;
            if (this.animateToItem >= 0) {
                if (this.moveLineProgress > 0.0f) {
                    this.moveLineProgress -= ((float) dt) / 200.0f;
                    if (this.animateToItem == this.currentImage) {
                        if (this.currentItemProgress < 1.0f) {
                            this.currentItemProgress += ((float) dt) / 200.0f;
                            if (this.currentItemProgress > 1.0f) {
                                this.currentItemProgress = 1.0f;
                            }
                        }
                        this.drawDx = this.animateToDXStart + ((int) Math.ceil((double) (this.currentItemProgress * ((float) (this.animateToDX - this.animateToDXStart)))));
                    } else {
                        this.nextItemProgress = CubicBezierInterpolator.EASE_OUT.getInterpolation(1.0f - this.moveLineProgress);
                        if (this.stopedScrolling) {
                            if (this.currentItemProgress > 0.0f) {
                                this.currentItemProgress -= ((float) dt) / 200.0f;
                                if (this.currentItemProgress < 0.0f) {
                                    this.currentItemProgress = 0.0f;
                                }
                            }
                            this.drawDx = this.animateToDXStart + ((int) Math.ceil((double) (this.nextItemProgress * ((float) (this.animateToDX - this.animateToDXStart)))));
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
            if (this.scrolling && this.currentItemProgress > 0.0f) {
                this.currentItemProgress -= ((float) dt) / 200.0f;
                if (this.currentItemProgress < 0.0f) {
                    this.currentItemProgress = 0.0f;
                }
                invalidate();
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

    public void setDelegate(GroupedPhotosListViewDelegate groupedPhotosListViewDelegate) {
        this.delegate = groupedPhotosListViewDelegate;
    }
}
