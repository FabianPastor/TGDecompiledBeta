package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.Components.CircularViewPager;
import org.telegram.ui.PinchToZoomHelper;
import org.telegram.ui.ProfileActivity;

public class ProfileGalleryView extends CircularViewPager implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public ViewPagerAdapter adapter;
    /* access modifiers changed from: private */
    public final Callback callback;
    private TLRPC.ChatFull chatInfo;
    /* access modifiers changed from: private */
    public boolean createThumbFromParent = true;
    ImageLocation curreantUploadingThumbLocation;
    /* access modifiers changed from: private */
    public int currentAccount = UserConfig.selectedAccount;
    ImageLocation currentUploadingImageLocation;
    /* access modifiers changed from: private */
    public long dialogId;
    private final PointF downPoint = new PointF();
    private boolean forceResetPosition;
    /* access modifiers changed from: private */
    public boolean hasActiveVideo;
    /* access modifiers changed from: private */
    public int imagesLayerNum;
    /* access modifiers changed from: private */
    public ArrayList<ImageLocation> imagesLocations = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<Integer> imagesLocationsSizes = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<Float> imagesUploadProgress = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean invalidateWithParent;
    private boolean isDownReleased;
    /* access modifiers changed from: private */
    public final boolean isProfileFragment;
    private boolean isScrollingListView = true;
    private boolean isSwipingViewPager = true;
    /* access modifiers changed from: private */
    public final ActionBar parentActionBar;
    private final int parentClassGuid;
    private final RecyclerListView parentListView;
    private TLRPC.TL_groupCallParticipant participant;
    Path path = new Path();
    private ArrayList<TLRPC.Photo> photos = new ArrayList<>();
    PinchToZoomHelper pinchToZoomHelper;
    private ImageLocation prevImageLocation;
    /* access modifiers changed from: private */
    public final SparseArray<RadialProgress2> radialProgresses = new SparseArray<>();
    float[] radii = new float[8];
    RectF rect = new RectF();
    /* access modifiers changed from: private */
    public int roundBottomRadius;
    /* access modifiers changed from: private */
    public int roundTopRadius;
    private boolean scrolledByUser;
    private int settingMainPhoto;
    private ArrayList<String> thumbsFileNames = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<ImageLocation> thumbsLocations = new ArrayList<>();
    private final int touchSlop;
    /* access modifiers changed from: private */
    public ImageLocation uploadingImageLocation;
    private ArrayList<String> videoFileNames = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<ImageLocation> videoLocations = new ArrayList<>();

    public interface Callback {
        void onDown(boolean z);

        void onPhotosLoaded();

        void onRelease();

        void onVideoSet();
    }

    public void setHasActiveVideo(boolean hasActiveVideo2) {
        this.hasActiveVideo = hasActiveVideo2;
    }

    public View findVideoActiveView() {
        if (!this.hasActiveVideo) {
            return null;
        }
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof TextureStubView) {
                return view;
            }
        }
        return null;
    }

    private static class Item {
        /* access modifiers changed from: private */
        public AvatarImageView imageView;
        boolean isActiveVideo;
        /* access modifiers changed from: private */
        public View textureViewStubView;

        private Item() {
        }
    }

    public ProfileGalleryView(Context context, ActionBar parentActionBar2, RecyclerListView parentListView2, Callback callback2) {
        super(context);
        setOffscreenPageLimit(2);
        this.isProfileFragment = false;
        this.parentListView = parentListView2;
        this.parentClassGuid = ConnectionsManager.generateClassGuid();
        this.parentActionBar = parentActionBar2;
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.callback = callback2;
        addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                ImageLocation location;
                if (positionOffsetPixels == 0) {
                    int position2 = ProfileGalleryView.this.adapter.getRealPosition(position);
                    if (ProfileGalleryView.this.hasActiveVideo) {
                        position2--;
                    }
                    BackupImageView currentItemView = ProfileGalleryView.this.getCurrentItemView();
                    int count = ProfileGalleryView.this.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = ProfileGalleryView.this.getChildAt(a);
                        if (child instanceof BackupImageView) {
                            int p = ProfileGalleryView.this.adapter.getRealPosition(ProfileGalleryView.this.adapter.imageViews.indexOf(child));
                            if (ProfileGalleryView.this.hasActiveVideo) {
                                p--;
                            }
                            ImageReceiver imageReceiver = ((BackupImageView) child).getImageReceiver();
                            boolean currentAllow = imageReceiver.getAllowStartAnimation();
                            if (p == position2) {
                                if (!currentAllow) {
                                    imageReceiver.setAllowStartAnimation(true);
                                    imageReceiver.startAnimation();
                                }
                                ImageLocation location2 = (ImageLocation) ProfileGalleryView.this.videoLocations.get(p);
                                if (location2 != null) {
                                    FileLoader.getInstance(ProfileGalleryView.this.currentAccount).setForceStreamLoadingFile(location2.location, "mp4");
                                }
                            } else if (currentAllow) {
                                AnimatedFileDrawable fileDrawable = imageReceiver.getAnimation();
                                if (!(fileDrawable == null || (location = (ImageLocation) ProfileGalleryView.this.videoLocations.get(p)) == null)) {
                                    fileDrawable.seekTo(location.videoSeekTo, false, true);
                                }
                                imageReceiver.setAllowStartAnimation(false);
                                imageReceiver.stopAnimation();
                            }
                        }
                    }
                    return;
                }
                int i = position;
            }

            public void onPageSelected(int position) {
            }

            public void onPageScrollStateChanged(int state) {
            }
        });
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getContext(), (ProfileActivity.AvatarImageView) null, parentActionBar2);
        this.adapter = viewPagerAdapter;
        setAdapter((CircularViewPager.Adapter) viewPagerAdapter);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.dialogPhotosLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.reloadDialogPhotos);
    }

    public void setImagesLayerNum(int value) {
        this.imagesLayerNum = value;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ProfileGalleryView(Context context, long dialogId2, ActionBar parentActionBar2, RecyclerListView parentListView2, ProfileActivity.AvatarImageView parentAvatarImageView, int parentClassGuid2, Callback callback2) {
        super(context);
        ActionBar actionBar = parentActionBar2;
        setVisibility(8);
        setOverScrollMode(2);
        setOffscreenPageLimit(2);
        this.isProfileFragment = true;
        this.dialogId = dialogId2;
        this.parentListView = parentListView2;
        this.parentClassGuid = parentClassGuid2;
        this.parentActionBar = actionBar;
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getContext(), parentAvatarImageView, actionBar);
        this.adapter = viewPagerAdapter;
        setAdapter((CircularViewPager.Adapter) viewPagerAdapter);
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.callback = callback2;
        addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                ImageLocation location;
                if (positionOffsetPixels == 0) {
                    int position2 = ProfileGalleryView.this.adapter.getRealPosition(position);
                    BackupImageView currentItemView = ProfileGalleryView.this.getCurrentItemView();
                    int count = ProfileGalleryView.this.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = ProfileGalleryView.this.getChildAt(a);
                        if (child instanceof BackupImageView) {
                            int p = ProfileGalleryView.this.adapter.getRealPosition(ProfileGalleryView.this.adapter.imageViews.indexOf(child));
                            ImageReceiver imageReceiver = ((BackupImageView) child).getImageReceiver();
                            boolean currentAllow = imageReceiver.getAllowStartAnimation();
                            if (p == position2) {
                                if (!currentAllow) {
                                    imageReceiver.setAllowStartAnimation(true);
                                    imageReceiver.startAnimation();
                                }
                                ImageLocation location2 = (ImageLocation) ProfileGalleryView.this.videoLocations.get(p);
                                if (location2 != null) {
                                    FileLoader.getInstance(ProfileGalleryView.this.currentAccount).setForceStreamLoadingFile(location2.location, "mp4");
                                }
                            } else if (currentAllow) {
                                AnimatedFileDrawable fileDrawable = imageReceiver.getAnimation();
                                if (!(fileDrawable == null || (location = (ImageLocation) ProfileGalleryView.this.videoLocations.get(p)) == null)) {
                                    fileDrawable.seekTo(location.videoSeekTo, false, true);
                                }
                                imageReceiver.setAllowStartAnimation(false);
                                imageReceiver.stopAnimation();
                            }
                        }
                    }
                    return;
                }
                int i = position;
            }

            public void onPageSelected(int position) {
            }

            public void onPageScrollStateChanged(int state) {
            }
        });
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.dialogPhotosLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.reloadDialogPhotos);
        MessagesController.getInstance(this.currentAccount).loadDialogPhotos(dialogId2, 80, 0, true, parentClassGuid2);
    }

    public void onDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogPhotosLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.reloadDialogPhotos);
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View child = getChildAt(a);
            if (child instanceof BackupImageView) {
                BackupImageView imageView = (BackupImageView) child;
                if (imageView.getImageReceiver().hasStaticThumb()) {
                    Drawable drawable = imageView.getImageReceiver().getDrawable();
                    if (drawable instanceof AnimatedFileDrawable) {
                        ((AnimatedFileDrawable) drawable).removeSecondParentView(imageView);
                    }
                }
            }
        }
    }

    public void setAnimatedFileMaybe(AnimatedFileDrawable drawable) {
        if (drawable != null && this.adapter != null) {
            int count = getChildCount();
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                if (child instanceof BackupImageView) {
                    ViewPagerAdapter viewPagerAdapter = this.adapter;
                    if (viewPagerAdapter.getRealPosition(viewPagerAdapter.imageViews.indexOf(child)) == 0) {
                        BackupImageView imageView = (BackupImageView) child;
                        AnimatedFileDrawable currentDrawable = imageView.getImageReceiver().getAnimation();
                        if (currentDrawable != drawable) {
                            if (currentDrawable != null) {
                                currentDrawable.removeSecondParentView(imageView);
                            }
                            imageView.setImageDrawable(drawable);
                            drawable.addSecondParentView(this);
                            drawable.setInvalidateParentViewWithSecond(true);
                        }
                    }
                }
            }
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        int currentItem;
        if (this.adapter == null) {
            return false;
        }
        if (this.parentListView.getScrollState() == 0 || this.isScrollingListView || !this.isSwipingViewPager) {
            int action = ev.getAction();
            if (!(this.pinchToZoomHelper == null || getCurrentItemView() == null)) {
                if (action != 0 && this.isDownReleased && !this.pinchToZoomHelper.isInOverlayMode()) {
                    this.pinchToZoomHelper.checkPinchToZoom(MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0), this, getCurrentItemView().getImageReceiver(), (MessageObject) null);
                } else if (this.pinchToZoomHelper.checkPinchToZoom(ev, this, getCurrentItemView().getImageReceiver(), (MessageObject) null)) {
                    if (!this.isDownReleased) {
                        this.isDownReleased = true;
                        this.callback.onRelease();
                    }
                    return true;
                }
            }
            if (action == 0) {
                this.isScrollingListView = true;
                this.isSwipingViewPager = true;
                this.scrolledByUser = true;
                this.downPoint.set(ev.getX(), ev.getY());
                if (this.adapter.getCount() > 1) {
                    this.callback.onDown(ev.getX() < ((float) getWidth()) / 3.0f);
                }
                this.isDownReleased = false;
            } else if (action == 1) {
                if (!this.isDownReleased) {
                    int itemsCount = this.adapter.getCount();
                    int currentItem2 = getCurrentItem();
                    if (itemsCount > 1) {
                        if (ev.getX() > ((float) getWidth()) / 3.0f) {
                            int extraCount = this.adapter.getExtraCount();
                            currentItem = currentItem2 + 1;
                            if (currentItem >= itemsCount - extraCount) {
                                currentItem = extraCount;
                            }
                        } else {
                            int extraCount2 = this.adapter.getExtraCount();
                            currentItem = currentItem2 - 1;
                            if (currentItem < extraCount2) {
                                currentItem = (itemsCount - extraCount2) - 1;
                            }
                        }
                        this.callback.onRelease();
                        setCurrentItem(currentItem, false);
                    }
                }
            } else if (action == 2) {
                float dx = ev.getX() - this.downPoint.x;
                float dy = ev.getY() - this.downPoint.y;
                boolean move = Math.abs(dy) >= ((float) this.touchSlop) || Math.abs(dx) >= ((float) this.touchSlop);
                if (move) {
                    this.isDownReleased = true;
                    this.callback.onRelease();
                }
                boolean z = this.isSwipingViewPager;
                if (!z || !this.isScrollingListView) {
                    if (z && !canScrollHorizontally(-1) && dx > ((float) this.touchSlop)) {
                        return false;
                    }
                } else if (move) {
                    if (Math.abs(dy) > Math.abs(dx)) {
                        this.isSwipingViewPager = false;
                        MotionEvent cancelEvent = MotionEvent.obtain(ev);
                        cancelEvent.setAction(3);
                        super.onTouchEvent(cancelEvent);
                        cancelEvent.recycle();
                    } else {
                        this.isScrollingListView = false;
                        MotionEvent cancelEvent2 = MotionEvent.obtain(ev);
                        cancelEvent2.setAction(3);
                        this.parentListView.onTouchEvent(cancelEvent2);
                        cancelEvent2.recycle();
                    }
                }
            }
            boolean result = false;
            if (this.isScrollingListView) {
                result = this.parentListView.onTouchEvent(ev);
            }
            if (this.isSwipingViewPager) {
                result |= super.onTouchEvent(ev);
            }
            if (action == 1 || action == 3) {
                this.isScrollingListView = false;
                this.isSwipingViewPager = false;
            }
            return result;
        }
        this.isSwipingViewPager = false;
        MotionEvent cancelEvent3 = MotionEvent.obtain(ev);
        cancelEvent3.setAction(3);
        super.onTouchEvent(cancelEvent3);
        cancelEvent3.recycle();
        return false;
    }

    public void setChatInfo(TLRPC.ChatFull chatFull) {
        this.chatInfo = chatFull;
        if (!this.photos.isEmpty() && this.photos.get(0) == null && this.chatInfo != null && FileLoader.isSamePhoto((TLRPC.FileLocation) this.imagesLocations.get(0).location, this.chatInfo.chat_photo)) {
            this.photos.set(0, this.chatInfo.chat_photo);
            if (!this.chatInfo.chat_photo.video_sizes.isEmpty()) {
                TLRPC.VideoSize videoSize = this.chatInfo.chat_photo.video_sizes.get(0);
                this.videoLocations.set(0, ImageLocation.getForPhoto(videoSize, this.chatInfo.chat_photo));
                this.videoFileNames.set(0, FileLoader.getAttachFileName(videoSize));
                this.callback.onPhotosLoaded();
            } else {
                this.videoLocations.set(0, (Object) null);
                this.videoFileNames.add(0, (Object) null);
            }
            this.imagesUploadProgress.set(0, (Object) null);
            this.adapter.notifyDataSetChanged();
        }
    }

    public boolean initIfEmpty(ImageLocation imageLocation, ImageLocation thumbLocation, boolean reload) {
        if (imageLocation == null || thumbLocation == null || this.settingMainPhoto != 0) {
            return false;
        }
        ImageLocation imageLocation2 = this.prevImageLocation;
        if (imageLocation2 == null || imageLocation2.location.local_id != imageLocation.location.local_id) {
            if (!this.imagesLocations.isEmpty()) {
                this.prevImageLocation = imageLocation;
                if (reload) {
                    MessagesController.getInstance(this.currentAccount).loadDialogPhotos(this.dialogId, 80, 0, true, this.parentClassGuid);
                }
                return true;
            } else if (reload) {
                MessagesController.getInstance(this.currentAccount).loadDialogPhotos(this.dialogId, 80, 0, true, this.parentClassGuid);
            }
        }
        if (!this.imagesLocations.isEmpty()) {
            return false;
        }
        this.prevImageLocation = imageLocation;
        this.thumbsFileNames.add((Object) null);
        this.videoFileNames.add((Object) null);
        this.imagesLocations.add(imageLocation);
        this.thumbsLocations.add(thumbLocation);
        this.videoLocations.add((Object) null);
        this.photos.add((Object) null);
        this.imagesLocationsSizes.add(-1);
        this.imagesUploadProgress.add((Object) null);
        getAdapter().notifyDataSetChanged();
        return true;
    }

    public void addUploadingImage(ImageLocation imageLocation, ImageLocation thumbLocation) {
        this.prevImageLocation = imageLocation;
        this.thumbsFileNames.add(0, (Object) null);
        this.videoFileNames.add(0, (Object) null);
        this.imagesLocations.add(0, imageLocation);
        this.thumbsLocations.add(0, thumbLocation);
        this.videoLocations.add(0, (Object) null);
        this.photos.add(0, (Object) null);
        this.imagesLocationsSizes.add(0, -1);
        this.imagesUploadProgress.add(0, Float.valueOf(0.0f));
        this.adapter.notifyDataSetChanged();
        resetCurrentItem();
        this.currentUploadingImageLocation = imageLocation;
        this.curreantUploadingThumbLocation = thumbLocation;
    }

    public void removeUploadingImage(ImageLocation imageLocation) {
        this.uploadingImageLocation = imageLocation;
        this.currentUploadingImageLocation = null;
        this.curreantUploadingThumbLocation = null;
    }

    public ImageLocation getImageLocation(int index) {
        if (index < 0 || index >= this.imagesLocations.size()) {
            return null;
        }
        ImageLocation location = this.videoLocations.get(index);
        if (location != null) {
            return location;
        }
        return this.imagesLocations.get(index);
    }

    public ImageLocation getRealImageLocation(int index) {
        if (index < 0 || index >= this.imagesLocations.size()) {
            return null;
        }
        return this.imagesLocations.get(index);
    }

    public ImageLocation getThumbLocation(int index) {
        if (index < 0 || index >= this.thumbsLocations.size()) {
            return null;
        }
        return this.thumbsLocations.get(index);
    }

    public boolean hasImages() {
        return !this.imagesLocations.isEmpty();
    }

    public BackupImageView getCurrentItemView() {
        ViewPagerAdapter viewPagerAdapter = this.adapter;
        if (viewPagerAdapter == null || viewPagerAdapter.objects.isEmpty()) {
            return null;
        }
        return ((Item) this.adapter.objects.get(getCurrentItem())).imageView;
    }

    public boolean isLoadingCurrentVideo() {
        BackupImageView imageView;
        if (this.videoLocations.get(this.hasActiveVideo ? getRealPosition() - 1 : getRealPosition()) == null || (imageView = getCurrentItemView()) == null) {
            return false;
        }
        AnimatedFileDrawable drawable = imageView.getImageReceiver().getAnimation();
        if (drawable == null || !drawable.hasBitmap()) {
            return true;
        }
        return false;
    }

    public float getCurrentItemProgress() {
        AnimatedFileDrawable drawable;
        BackupImageView imageView = getCurrentItemView();
        if (imageView == null || (drawable = imageView.getImageReceiver().getAnimation()) == null) {
            return 0.0f;
        }
        return drawable.getCurrentProgress();
    }

    public boolean isCurrentItemVideo() {
        int i = getRealPosition();
        if (this.hasActiveVideo) {
            if (i == 0) {
                return false;
            }
            i--;
        }
        if (this.videoLocations.get(i) != null) {
            return true;
        }
        return false;
    }

    public ImageLocation getCurrentVideoLocation(ImageLocation thumbLocation, ImageLocation imageLocation) {
        if (thumbLocation == null) {
            return null;
        }
        int b = 0;
        while (b < 2) {
            ArrayList<ImageLocation> arrayList = b == 0 ? this.thumbsLocations : this.imagesLocations;
            int N = arrayList.size();
            for (int a = 0; a < N; a++) {
                ImageLocation loc = arrayList.get(a);
                if (loc != null && loc.location != null && ((loc.dc_id == thumbLocation.dc_id && loc.location.local_id == thumbLocation.location.local_id && loc.location.volume_id == thumbLocation.location.volume_id) || (loc.dc_id == imageLocation.dc_id && loc.location.local_id == imageLocation.location.local_id && loc.location.volume_id == imageLocation.location.volume_id))) {
                    return this.videoLocations.get(a);
                }
            }
            b++;
        }
        return null;
    }

    public void resetCurrentItem() {
        setCurrentItem(this.adapter.getExtraCount(), false);
    }

    public int getRealCount() {
        int size = this.photos.size();
        if (this.hasActiveVideo) {
            return size + 1;
        }
        return size;
    }

    public int getRealPosition(int position) {
        return this.adapter.getRealPosition(position);
    }

    public int getRealPosition() {
        return this.adapter.getRealPosition(getCurrentItem());
    }

    public TLRPC.Photo getPhoto(int index) {
        if (index < 0 || index >= this.photos.size()) {
            return null;
        }
        return this.photos.get(index);
    }

    public void replaceFirstPhoto(TLRPC.Photo oldPhoto, TLRPC.Photo photo) {
        int idx;
        if (!this.photos.isEmpty() && (idx = this.photos.indexOf(oldPhoto)) >= 0) {
            this.photos.set(idx, photo);
        }
    }

    public void finishSettingMainPhoto() {
        this.settingMainPhoto--;
    }

    public void startMovePhotoToBegin(int index) {
        if (index > 0 && index < this.photos.size()) {
            this.settingMainPhoto++;
            this.photos.remove(index);
            this.photos.add(0, this.photos.get(index));
            this.thumbsFileNames.remove(index);
            this.thumbsFileNames.add(0, this.thumbsFileNames.get(index));
            ArrayList<String> arrayList = this.videoFileNames;
            arrayList.add(0, arrayList.remove(index));
            this.videoLocations.remove(index);
            this.videoLocations.add(0, this.videoLocations.get(index));
            this.imagesLocations.remove(index);
            this.imagesLocations.add(0, this.imagesLocations.get(index));
            this.thumbsLocations.remove(index);
            this.thumbsLocations.add(0, this.thumbsLocations.get(index));
            this.imagesLocationsSizes.remove(index);
            this.imagesLocationsSizes.add(0, this.imagesLocationsSizes.get(index));
            this.imagesUploadProgress.remove(index);
            this.imagesUploadProgress.add(0, this.imagesUploadProgress.get(index));
            this.prevImageLocation = this.imagesLocations.get(0);
        }
    }

    public void commitMoveToBegin() {
        this.adapter.notifyDataSetChanged();
        resetCurrentItem();
    }

    public boolean removePhotoAtIndex(int index) {
        if (index < 0 || index >= this.photos.size()) {
            return false;
        }
        this.photos.remove(index);
        this.thumbsFileNames.remove(index);
        this.videoFileNames.remove(index);
        this.videoLocations.remove(index);
        this.imagesLocations.remove(index);
        this.thumbsLocations.remove(index);
        this.imagesLocationsSizes.remove(index);
        this.radialProgresses.delete(index);
        this.imagesUploadProgress.remove(index);
        if (index == 0 && !this.imagesLocations.isEmpty()) {
            this.prevImageLocation = this.imagesLocations.get(0);
        }
        this.adapter.notifyDataSetChanged();
        return this.photos.isEmpty();
    }

    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (this.parentListView.getScrollState() != 0) {
            return false;
        }
        if (!(getParent() == null || getParent().getParent() == null)) {
            getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
        }
        return super.onInterceptTouchEvent(e);
    }

    private void loadNeighboringThumbs() {
        int locationsCount = this.thumbsLocations.size();
        if (locationsCount > 1) {
            int i = 0;
            while (true) {
                int i2 = 2;
                if (locationsCount <= 2) {
                    i2 = 1;
                }
                if (i < i2) {
                    FileLoader.getInstance(this.currentAccount).loadFile(this.thumbsLocations.get(i == 0 ? 1 : locationsCount - 1), (Object) null, (String) null, 0, 1);
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        RadialProgress2 radialProgress;
        RadialProgress2 radialProgress2;
        ImageLocation currentImageLocation;
        ArrayList<TLRPC.Photo> arrayList;
        int guid;
        int N;
        int i = id;
        if (i == NotificationCenter.dialogPhotosLoaded) {
            int guid2 = args[3].intValue();
            long did = args[0].longValue();
            if (did == this.dialogId && this.parentClassGuid == guid2 && this.adapter != null) {
                boolean fromCache = args[2].booleanValue();
                ArrayList<TLRPC.Photo> arrayList2 = args[4];
                this.thumbsFileNames.clear();
                this.videoFileNames.clear();
                this.imagesLocations.clear();
                this.videoLocations.clear();
                this.thumbsLocations.clear();
                this.photos.clear();
                this.imagesLocationsSizes.clear();
                this.imagesUploadProgress.clear();
                if (DialogObject.isChatDialog(did)) {
                    TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-did));
                    ImageLocation currentImageLocation2 = ImageLocation.getForUserOrChat(chat, 0);
                    if (currentImageLocation2 != null) {
                        this.imagesLocations.add(currentImageLocation2);
                        this.thumbsLocations.add(ImageLocation.getForUserOrChat(chat, 1));
                        this.thumbsFileNames.add((Object) null);
                        if (this.chatInfo == null || !FileLoader.isSamePhoto((TLRPC.FileLocation) currentImageLocation2.location, this.chatInfo.chat_photo)) {
                            this.photos.add((Object) null);
                            this.videoFileNames.add((Object) null);
                            this.videoLocations.add((Object) null);
                        } else {
                            this.photos.add(this.chatInfo.chat_photo);
                            if (!this.chatInfo.chat_photo.video_sizes.isEmpty()) {
                                TLRPC.VideoSize videoSize = this.chatInfo.chat_photo.video_sizes.get(0);
                                this.videoLocations.add(ImageLocation.getForPhoto(videoSize, this.chatInfo.chat_photo));
                                this.videoFileNames.add(FileLoader.getAttachFileName(videoSize));
                            } else {
                                this.videoLocations.add((Object) null);
                                this.videoFileNames.add((Object) null);
                            }
                        }
                        this.imagesLocationsSizes.add(-1);
                        this.imagesUploadProgress.add((Object) null);
                    }
                    currentImageLocation = currentImageLocation2;
                } else {
                    currentImageLocation = null;
                }
                int a = 0;
                while (a < arrayList2.size()) {
                    TLRPC.Photo photo = arrayList2.get(a);
                    if (photo == null || (photo instanceof TLRPC.TL_photoEmpty)) {
                        guid = guid2;
                        arrayList = arrayList2;
                    } else if (photo.sizes == null) {
                        guid = guid2;
                        arrayList = arrayList2;
                    } else {
                        TLRPC.PhotoSize sizeThumb = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 50);
                        int b = 0;
                        int N2 = photo.sizes.size();
                        while (true) {
                            if (b >= N2) {
                                break;
                            }
                            TLRPC.PhotoSize photoSize = photo.sizes.get(b);
                            if (photoSize instanceof TLRPC.TL_photoStrippedSize) {
                                sizeThumb = photoSize;
                                break;
                            }
                            b++;
                        }
                        if (currentImageLocation != null) {
                            boolean cont = false;
                            int b2 = 0;
                            int N3 = photo.sizes.size();
                            while (true) {
                                if (b2 >= N3) {
                                    guid = guid2;
                                    arrayList = arrayList2;
                                    int i2 = N3;
                                    break;
                                }
                                TLRPC.PhotoSize size = photo.sizes.get(b2);
                                if (size.location != null) {
                                    guid = guid2;
                                    if (size.location.local_id == currentImageLocation.location.local_id) {
                                        arrayList = arrayList2;
                                        N = N3;
                                        TLRPC.PhotoSize photoSize2 = size;
                                        if (size.location.volume_id == currentImageLocation.location.volume_id) {
                                            this.photos.set(0, photo);
                                            if (!photo.video_sizes.isEmpty()) {
                                                this.videoLocations.set(0, ImageLocation.getForPhoto(photo.video_sizes.get(0), photo));
                                            }
                                            cont = true;
                                        }
                                    } else {
                                        arrayList = arrayList2;
                                        N = N3;
                                        TLRPC.PhotoSize photoSize3 = size;
                                    }
                                } else {
                                    guid = guid2;
                                    arrayList = arrayList2;
                                    N = N3;
                                    TLRPC.PhotoSize photoSize4 = size;
                                }
                                b2++;
                                guid2 = guid;
                                arrayList2 = arrayList;
                                N3 = N;
                            }
                            if (cont) {
                            }
                        } else {
                            guid = guid2;
                            arrayList = arrayList2;
                        }
                        TLRPC.PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 640);
                        if (sizeFull != null) {
                            if (photo.dc_id != 0) {
                                sizeFull.location.dc_id = photo.dc_id;
                                sizeFull.location.file_reference = photo.file_reference;
                            }
                            ImageLocation location = ImageLocation.getForPhoto(sizeFull, photo);
                            if (location != null) {
                                this.imagesLocations.add(location);
                                this.thumbsFileNames.add(FileLoader.getAttachFileName(sizeThumb instanceof TLRPC.TL_photoStrippedSize ? sizeFull : sizeThumb));
                                this.thumbsLocations.add(ImageLocation.getForPhoto(sizeThumb, photo));
                                if (!photo.video_sizes.isEmpty()) {
                                    TLRPC.VideoSize videoSize2 = photo.video_sizes.get(0);
                                    this.videoLocations.add(ImageLocation.getForPhoto(videoSize2, photo));
                                    this.videoFileNames.add(FileLoader.getAttachFileName(videoSize2));
                                } else {
                                    this.videoLocations.add((Object) null);
                                    this.videoFileNames.add((Object) null);
                                }
                                this.photos.add(photo);
                                this.imagesLocationsSizes.add(Integer.valueOf(sizeFull.size));
                                this.imagesUploadProgress.add((Object) null);
                            }
                        }
                    }
                    a++;
                    guid2 = guid;
                    arrayList2 = arrayList;
                }
                ArrayList<TLRPC.Photo> arrayList3 = arrayList2;
                loadNeighboringThumbs();
                getAdapter().notifyDataSetChanged();
                if (this.isProfileFragment) {
                    if (!this.scrolledByUser || this.forceResetPosition) {
                        resetCurrentItem();
                    }
                } else if (!this.scrolledByUser || this.forceResetPosition) {
                    resetCurrentItem();
                    getAdapter().notifyDataSetChanged();
                }
                this.forceResetPosition = false;
                if (fromCache) {
                    MessagesController.getInstance(this.currentAccount).loadDialogPhotos(did, 80, 0, false, this.parentClassGuid);
                }
                Callback callback2 = this.callback;
                if (callback2 != null) {
                    callback2.onPhotosLoaded();
                }
                ImageLocation imageLocation = this.currentUploadingImageLocation;
                if (imageLocation != null) {
                    addUploadingImage(imageLocation, this.curreantUploadingThumbLocation);
                    return;
                }
                return;
            }
        } else if (i == NotificationCenter.fileLoaded) {
            String fileName = args[0];
            for (int i3 = 0; i3 < this.thumbsFileNames.size(); i3++) {
                String fileName2 = this.videoFileNames.get(i3);
                if (fileName2 == null) {
                    fileName2 = this.thumbsFileNames.get(i3);
                }
                if (!(fileName2 == null || !TextUtils.equals(fileName, fileName2) || (radialProgress2 = this.radialProgresses.get(i3)) == null)) {
                    radialProgress2.setProgress(1.0f, true);
                }
            }
        } else if (i == NotificationCenter.fileLoadProgressChanged) {
            String fileName3 = args[0];
            for (int i4 = 0; i4 < this.thumbsFileNames.size(); i4++) {
                String fileName22 = this.videoFileNames.get(i4);
                if (fileName22 == null) {
                    fileName22 = this.thumbsFileNames.get(i4);
                }
                if (!(fileName22 == null || !TextUtils.equals(fileName3, fileName22) || (radialProgress = this.radialProgresses.get(i4)) == null)) {
                    radialProgress.setProgress(Math.min(1.0f, ((float) args[1].longValue()) / ((float) args[2].longValue())), true);
                }
            }
        } else if (i == NotificationCenter.reloadDialogPhotos && this.settingMainPhoto == 0) {
            MessagesController.getInstance(this.currentAccount).loadDialogPhotos(this.dialogId, 80, 0, true, this.parentClassGuid);
        }
    }

    public class ViewPagerAdapter extends CircularViewPager.Adapter {
        private final Context context;
        /* access modifiers changed from: private */
        public final ArrayList<BackupImageView> imageViews = new ArrayList<>();
        /* access modifiers changed from: private */
        public final ArrayList<Item> objects = new ArrayList<>();
        private final ActionBar parentActionBar;
        /* access modifiers changed from: private */
        public BackupImageView parentAvatarImageView;
        private final Paint placeholderPaint;

        public ViewPagerAdapter(Context context2, ProfileActivity.AvatarImageView parentAvatarImageView2, ActionBar parentActionBar2) {
            this.context = context2;
            this.parentAvatarImageView = parentAvatarImageView2;
            this.parentActionBar = parentActionBar2;
            Paint paint = new Paint(1);
            this.placeholderPaint = paint;
            paint.setColor(-16777216);
        }

        public int getCount() {
            return this.objects.size();
        }

        public boolean isViewFromObject(View view, Object object) {
            Item item = (Item) object;
            if (item.isActiveVideo) {
                if (view == item.textureViewStubView) {
                    return true;
                }
                return false;
            } else if (view == item.imageView) {
                return true;
            } else {
                return false;
            }
        }

        public int getItemPosition(Object object) {
            int idx = this.objects.indexOf((Item) object);
            if (idx == -1) {
                return -2;
            }
            return idx;
        }

        public Item instantiateItem(ViewGroup container, int position) {
            String filter;
            String filter2;
            String thumbFilter;
            ViewGroup viewGroup = container;
            int i = position;
            Item item = this.objects.get(i);
            int realPosition = getRealPosition(i);
            boolean z = true;
            if (!ProfileGalleryView.this.hasActiveVideo || realPosition != 0) {
                item.isActiveVideo = false;
                if (!(item.textureViewStubView == null || item.textureViewStubView.getParent() == null)) {
                    viewGroup.removeView(item.textureViewStubView);
                }
                if (item.imageView == null) {
                    AvatarImageView unused = item.imageView = new AvatarImageView(this.context, i, this.placeholderPaint);
                    this.imageViews.set(i, item.imageView);
                }
                if (item.imageView.getParent() == null) {
                    viewGroup.addView(item.imageView);
                }
                item.imageView.getImageReceiver().setAllowDecodeSingleFrame(true);
                int imageLocationPosition = ProfileGalleryView.this.hasActiveVideo ? realPosition - 1 : realPosition;
                boolean needProgress = false;
                if (imageLocationPosition == 0) {
                    BackupImageView backupImageView = this.parentAvatarImageView;
                    Drawable drawable = backupImageView == null ? null : backupImageView.getImageReceiver().getDrawable();
                    if (!(drawable instanceof AnimatedFileDrawable) || !((AnimatedFileDrawable) drawable).hasBitmap()) {
                        ImageLocation videoLocation = (ImageLocation) ProfileGalleryView.this.videoLocations.get(imageLocationPosition);
                        AvatarImageView access$600 = item.imageView;
                        if (videoLocation == null) {
                            z = false;
                        }
                        access$600.isVideo = z;
                        needProgress = true;
                        if (!ProfileGalleryView.this.isProfileFragment || videoLocation == null || videoLocation.imageType != 2) {
                            filter2 = null;
                        } else {
                            filter2 = "avatar";
                        }
                        ImageLocation location = (ImageLocation) ProfileGalleryView.this.thumbsLocations.get(imageLocationPosition);
                        Bitmap thumb = (this.parentAvatarImageView == null || !ProfileGalleryView.this.createThumbFromParent) ? null : this.parentAvatarImageView.getImageReceiver().getBitmap();
                        StringBuilder sb = new StringBuilder();
                        sb.append("avatar_");
                        String filter3 = filter2;
                        sb.append(ProfileGalleryView.this.dialogId);
                        String parent = sb.toString();
                        if (thumb != null) {
                            ImageLocation imageLocation = location;
                            item.imageView.setImageMedia((ImageLocation) ProfileGalleryView.this.videoLocations.get(imageLocationPosition), filter3, (ImageLocation) ProfileGalleryView.this.imagesLocations.get(imageLocationPosition), (String) null, thumb, ((Integer) ProfileGalleryView.this.imagesLocationsSizes.get(imageLocationPosition)).intValue(), 1, parent);
                        } else {
                            ImageLocation location2 = location;
                            if (ProfileGalleryView.this.uploadingImageLocation != null) {
                                item.imageView.setImageMedia((ImageLocation) ProfileGalleryView.this.videoLocations.get(imageLocationPosition), filter3, (ImageLocation) ProfileGalleryView.this.imagesLocations.get(imageLocationPosition), (String) null, ProfileGalleryView.this.uploadingImageLocation, (String) null, (String) null, ((Integer) ProfileGalleryView.this.imagesLocationsSizes.get(imageLocationPosition)).intValue(), 1, parent);
                            } else {
                                if (location2.photoSize instanceof TLRPC.TL_photoStrippedSize) {
                                    thumbFilter = "b";
                                } else {
                                    thumbFilter = null;
                                }
                                item.imageView.setImageMedia(videoLocation, (String) null, (ImageLocation) ProfileGalleryView.this.imagesLocations.get(imageLocationPosition), (String) null, (ImageLocation) ProfileGalleryView.this.thumbsLocations.get(imageLocationPosition), thumbFilter, (String) null, ((Integer) ProfileGalleryView.this.imagesLocationsSizes.get(imageLocationPosition)).intValue(), 1, parent);
                            }
                        }
                    } else {
                        AnimatedFileDrawable animatedFileDrawable = (AnimatedFileDrawable) drawable;
                        item.imageView.setImageDrawable(drawable);
                        animatedFileDrawable.addSecondParentView(item.imageView);
                        animatedFileDrawable.setInvalidateParentViewWithSecond(true);
                    }
                } else {
                    ImageLocation videoLocation2 = (ImageLocation) ProfileGalleryView.this.videoLocations.get(imageLocationPosition);
                    AvatarImageView access$6002 = item.imageView;
                    if (videoLocation2 == null) {
                        z = false;
                    }
                    access$6002.isVideo = z;
                    needProgress = true;
                    if (((ImageLocation) ProfileGalleryView.this.thumbsLocations.get(imageLocationPosition)).photoSize instanceof TLRPC.TL_photoStrippedSize) {
                        filter = "b";
                    } else {
                        filter = null;
                    }
                    item.imageView.setImageMedia(videoLocation2, (String) null, (ImageLocation) ProfileGalleryView.this.imagesLocations.get(imageLocationPosition), (String) null, (ImageLocation) ProfileGalleryView.this.thumbsLocations.get(imageLocationPosition), filter, (String) null, ((Integer) ProfileGalleryView.this.imagesLocationsSizes.get(imageLocationPosition)).intValue(), 1, "avatar_" + ProfileGalleryView.this.dialogId);
                }
                if (ProfileGalleryView.this.imagesUploadProgress.get(imageLocationPosition) != null) {
                    needProgress = true;
                }
                if (needProgress) {
                    RadialProgress2 unused2 = item.imageView.radialProgress = (RadialProgress2) ProfileGalleryView.this.radialProgresses.get(imageLocationPosition);
                    if (item.imageView.radialProgress == null) {
                        RadialProgress2 unused3 = item.imageView.radialProgress = new RadialProgress2(item.imageView);
                        item.imageView.radialProgress.setOverrideAlpha(0.0f);
                        item.imageView.radialProgress.setIcon(10, false, false);
                        item.imageView.radialProgress.setColors(NUM, NUM, -1, -1);
                        ProfileGalleryView.this.radialProgresses.append(imageLocationPosition, item.imageView.radialProgress);
                    }
                    if (ProfileGalleryView.this.invalidateWithParent) {
                        ProfileGalleryView.this.invalidate();
                    } else {
                        ProfileGalleryView.this.postInvalidateOnAnimation();
                    }
                }
                item.imageView.getImageReceiver().setDelegate(new ImageReceiver.ImageReceiverDelegate() {
                    public void didSetImage(ImageReceiver imageReceiver, boolean set, boolean thumb, boolean memCache) {
                    }

                    public void onAnimationReady(ImageReceiver imageReceiver) {
                        ProfileGalleryView.this.callback.onVideoSet();
                    }
                });
                item.imageView.getImageReceiver().setCrossfadeAlpha((byte) 2);
                item.imageView.setRoundRadius(ProfileGalleryView.this.roundTopRadius, ProfileGalleryView.this.roundTopRadius, ProfileGalleryView.this.roundBottomRadius, ProfileGalleryView.this.roundBottomRadius);
                return item;
            }
            item.isActiveVideo = true;
            if (item.textureViewStubView == null) {
                View unused4 = item.textureViewStubView = new TextureStubView(this.context);
            }
            if (item.textureViewStubView.getParent() == null) {
                viewGroup.addView(item.textureViewStubView);
            }
            return item;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            Item item = (Item) object;
            if (item.textureViewStubView != null) {
                container.removeView(item.textureViewStubView);
            }
            if (!item.isActiveVideo) {
                BackupImageView imageView = item.imageView;
                if (imageView.getImageReceiver().hasStaticThumb()) {
                    Drawable drawable = imageView.getImageReceiver().getDrawable();
                    if (drawable instanceof AnimatedFileDrawable) {
                        ((AnimatedFileDrawable) drawable).removeSecondParentView(imageView);
                    }
                }
                imageView.setRoundRadius(0);
                container.removeView(imageView);
                imageView.getImageReceiver().cancelLoadImage();
            }
        }

        public CharSequence getPageTitle(int position) {
            return (getRealPosition(position) + 1) + "/" + (getCount() - (getExtraCount() * 2));
        }

        public void notifyDataSetChanged() {
            for (int i = 0; i < this.imageViews.size(); i++) {
                if (this.imageViews.get(i) != null) {
                    this.imageViews.get(i).getImageReceiver().cancelLoadImage();
                }
            }
            this.objects.clear();
            this.imageViews.clear();
            int size = ProfileGalleryView.this.imagesLocations.size();
            if (ProfileGalleryView.this.hasActiveVideo) {
                size++;
            }
            int N = (getExtraCount() * 2) + size;
            for (int a = 0; a < N; a++) {
                this.objects.add(new Item());
                this.imageViews.add((Object) null);
            }
            super.notifyDataSetChanged();
        }

        public int getExtraCount() {
            int count = ProfileGalleryView.this.imagesLocations.size();
            if (ProfileGalleryView.this.hasActiveVideo) {
                count++;
            }
            if (count >= 2) {
                return ProfileGalleryView.this.getOffscreenPageLimit();
            }
            return 0;
        }
    }

    public void setData(long dialogId2) {
        setData(dialogId2, false);
    }

    public void setData(long dialogId2, boolean forceReset) {
        if (this.dialogId != dialogId2 || forceReset) {
            this.forceResetPosition = true;
            this.adapter.notifyDataSetChanged();
            reset();
            this.dialogId = dialogId2;
            if (dialogId2 != 0) {
                MessagesController.getInstance(this.currentAccount).loadDialogPhotos(dialogId2, 80, 0, true, this.parentClassGuid);
                return;
            }
            return;
        }
        resetCurrentItem();
    }

    private void reset() {
        this.videoFileNames.clear();
        this.thumbsFileNames.clear();
        this.photos.clear();
        this.videoLocations.clear();
        this.imagesLocations.clear();
        this.thumbsLocations.clear();
        this.imagesLocationsSizes.clear();
        this.imagesUploadProgress.clear();
        this.adapter.notifyDataSetChanged();
        this.uploadingImageLocation = null;
    }

    public void setRoundRadius(int topRadius, int bottomRadius) {
        this.roundTopRadius = topRadius;
        this.roundBottomRadius = bottomRadius;
        if (this.adapter != null) {
            for (int i = 0; i < this.adapter.objects.size(); i++) {
                if (((Item) this.adapter.objects.get(i)).imageView != null) {
                    AvatarImageView access$600 = ((Item) this.adapter.objects.get(i)).imageView;
                    int i2 = this.roundTopRadius;
                    int i3 = this.roundBottomRadius;
                    access$600.setRoundRadius(i2, i2, i3, i3);
                }
            }
        }
    }

    public void setParentAvatarImage(BackupImageView parentImageView) {
        ViewPagerAdapter viewPagerAdapter = this.adapter;
        if (viewPagerAdapter != null) {
            BackupImageView unused = viewPagerAdapter.parentAvatarImageView = parentImageView;
        }
    }

    public void setUploadProgress(ImageLocation imageLocation, float p) {
        if (imageLocation != null) {
            int i = 0;
            while (true) {
                if (i >= this.imagesLocations.size()) {
                    break;
                } else if (this.imagesLocations.get(i) == imageLocation) {
                    this.imagesUploadProgress.set(i, Float.valueOf(p));
                    if (this.radialProgresses.get(i) != null) {
                        this.radialProgresses.get(i).setProgress(p, true);
                    }
                } else {
                    i++;
                }
            }
            for (int i2 = 0; i2 < getChildCount(); i2++) {
                getChildAt(i2).invalidate();
            }
        }
    }

    public void setCreateThumbFromParent(boolean createThumbFromParent2) {
        this.createThumbFromParent = createThumbFromParent2;
    }

    private class AvatarImageView extends BackupImageView {
        private long firstDrawTime = -1;
        public boolean isVideo;
        private final Paint placeholderPaint;
        private final int position;
        /* access modifiers changed from: private */
        public RadialProgress2 radialProgress;
        private ValueAnimator radialProgressHideAnimator;
        private float radialProgressHideAnimatorStartValue;
        private final int radialProgressSize = AndroidUtilities.dp(64.0f);

        public AvatarImageView(Context context, int position2, Paint placeholderPaint2) {
            super(context);
            this.position = position2;
            this.placeholderPaint = placeholderPaint2;
            setLayerNum(ProfileGalleryView.this.imagesLayerNum);
        }

        /* access modifiers changed from: protected */
        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            if (this.radialProgress != null) {
                int paddingTop = (ProfileGalleryView.this.parentActionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
                int paddingBottom = AndroidUtilities.dp2(80.0f);
                RadialProgress2 radialProgress2 = this.radialProgress;
                int i = this.radialProgressSize;
                radialProgress2.setProgressRect((w - i) / 2, ((((h - paddingTop) - paddingBottom) - i) / 2) + paddingTop, (w + i) / 2, ((((h - paddingTop) - paddingBottom) + i) / 2) + paddingTop);
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int realPosition;
            boolean hideProgress;
            Canvas canvas2 = canvas;
            if (ProfileGalleryView.this.pinchToZoomHelper == null || !ProfileGalleryView.this.pinchToZoomHelper.isInOverlayMode()) {
                if (this.radialProgress != null) {
                    int realPosition2 = ProfileGalleryView.this.getRealPosition(this.position);
                    if (ProfileGalleryView.this.hasActiveVideo) {
                        realPosition = realPosition2 - 1;
                    } else {
                        realPosition = realPosition2;
                    }
                    Drawable drawable = getImageReceiver().getDrawable();
                    boolean z = false;
                    if (realPosition >= ProfileGalleryView.this.imagesUploadProgress.size() || ProfileGalleryView.this.imagesUploadProgress.get(realPosition) == null) {
                        if (drawable != null && (!this.isVideo || ((drawable instanceof AnimatedFileDrawable) && ((AnimatedFileDrawable) drawable).getDurationMs() > 0))) {
                            z = true;
                        }
                        hideProgress = z;
                    } else {
                        if (((Float) ProfileGalleryView.this.imagesUploadProgress.get(realPosition)).floatValue() >= 1.0f) {
                            z = true;
                        }
                        hideProgress = z;
                    }
                    if (!hideProgress) {
                        if (this.firstDrawTime < 0) {
                            this.firstDrawTime = System.currentTimeMillis();
                            int i = realPosition;
                        } else {
                            long elapsedTime = System.currentTimeMillis() - this.firstDrawTime;
                            long startDelay = this.isVideo ? 250 : 750;
                            if (elapsedTime > 250 + startDelay) {
                            } else if (elapsedTime > startDelay) {
                                int i2 = realPosition;
                                this.radialProgress.setOverrideAlpha(CubicBezierInterpolator.DEFAULT.getInterpolation(((float) (elapsedTime - startDelay)) / 250.0f));
                            }
                        }
                        if (ProfileGalleryView.this.invalidateWithParent) {
                            invalidate();
                        } else {
                            postInvalidateOnAnimation();
                        }
                        invalidate();
                    } else if (this.radialProgressHideAnimator == null) {
                        long startDelay2 = 0;
                        if (this.radialProgress.getProgress() < 1.0f) {
                            this.radialProgress.setProgress(1.0f, true);
                            startDelay2 = 100;
                        }
                        this.radialProgressHideAnimatorStartValue = this.radialProgress.getOverrideAlpha();
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                        this.radialProgressHideAnimator = ofFloat;
                        ofFloat.setStartDelay(startDelay2);
                        this.radialProgressHideAnimator.setDuration((long) (this.radialProgressHideAnimatorStartValue * 250.0f));
                        this.radialProgressHideAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                        this.radialProgressHideAnimator.addUpdateListener(new ProfileGalleryView$AvatarImageView$$ExternalSyntheticLambda0(this));
                        final int finalRealPosition = realPosition;
                        this.radialProgressHideAnimator.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                RadialProgress2 unused = AvatarImageView.this.radialProgress = null;
                                ProfileGalleryView.this.radialProgresses.delete(finalRealPosition);
                            }
                        });
                        this.radialProgressHideAnimator.start();
                        int i3 = realPosition;
                    } else {
                        int i4 = realPosition;
                    }
                    if (ProfileGalleryView.this.roundTopRadius == 0 && ProfileGalleryView.this.roundBottomRadius == 0) {
                        canvas.drawRect(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), this.placeholderPaint);
                    } else if (ProfileGalleryView.this.roundTopRadius == ProfileGalleryView.this.roundBottomRadius) {
                        ProfileGalleryView.this.rect.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
                        canvas2.drawRoundRect(ProfileGalleryView.this.rect, (float) ProfileGalleryView.this.roundTopRadius, (float) ProfileGalleryView.this.roundTopRadius, this.placeholderPaint);
                    } else {
                        ProfileGalleryView.this.path.reset();
                        ProfileGalleryView.this.rect.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
                        for (int i5 = 0; i5 < 4; i5++) {
                            ProfileGalleryView.this.radii[i5] = (float) ProfileGalleryView.this.roundTopRadius;
                            ProfileGalleryView.this.radii[i5 + 4] = (float) ProfileGalleryView.this.roundBottomRadius;
                        }
                        ProfileGalleryView.this.path.addRoundRect(ProfileGalleryView.this.rect, ProfileGalleryView.this.radii, Path.Direction.CW);
                        canvas2.drawPath(ProfileGalleryView.this.path, this.placeholderPaint);
                    }
                }
                super.onDraw(canvas);
                RadialProgress2 radialProgress2 = this.radialProgress;
                if (radialProgress2 != null && radialProgress2.getOverrideAlpha() > 0.0f) {
                    this.radialProgress.draw(canvas2);
                }
            }
        }

        /* renamed from: lambda$onDraw$0$org-telegram-ui-Components-ProfileGalleryView$AvatarImageView  reason: not valid java name */
        public /* synthetic */ void m1260xe910519a(ValueAnimator anim) {
            this.radialProgress.setOverrideAlpha(AndroidUtilities.lerp(this.radialProgressHideAnimatorStartValue, 0.0f, anim.getAnimatedFraction()));
        }

        public void invalidate() {
            super.invalidate();
            if (ProfileGalleryView.this.invalidateWithParent) {
                ProfileGalleryView.this.invalidate();
            }
        }
    }

    public void setPinchToZoomHelper(PinchToZoomHelper pinchToZoomHelper2) {
        this.pinchToZoomHelper = pinchToZoomHelper2;
    }

    public void setInvalidateWithParent(boolean invalidateWithParent2) {
        this.invalidateWithParent = invalidateWithParent2;
    }

    private class TextureStubView extends View {
        public TextureStubView(Context context) {
            super(context);
        }
    }
}
