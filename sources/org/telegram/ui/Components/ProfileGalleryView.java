package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated;
import org.telegram.tgnet.TLRPC$VideoSize;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.Components.CircularViewPager;
import org.telegram.ui.PinchToZoomHelper;
import org.telegram.ui.ProfileActivity;

public class ProfileGalleryView extends CircularViewPager implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public ViewPagerAdapter adapter;
    /* access modifiers changed from: private */
    public final Callback callback;
    private TLRPC$ChatFull chatInfo;
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
    Path path = new Path();
    private ArrayList<TLRPC$Photo> photos = new ArrayList<>();
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

    public void setHasActiveVideo(boolean z) {
        this.hasActiveVideo = z;
    }

    public View findVideoActiveView() {
        if (!this.hasActiveVideo) {
            return null;
        }
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof TextureStubView) {
                return childAt;
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

    public ProfileGalleryView(Context context, ActionBar actionBar, RecyclerListView recyclerListView, Callback callback2) {
        super(context);
        setOffscreenPageLimit(2);
        this.isProfileFragment = false;
        this.parentListView = recyclerListView;
        this.parentClassGuid = ConnectionsManager.generateClassGuid();
        this.parentActionBar = actionBar;
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.callback = callback2;
        addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int i) {
            }

            public void onPageSelected(int i) {
            }

            public void onPageScrolled(int i, float f, int i2) {
                ImageLocation imageLocation;
                if (i2 == 0) {
                    int realPosition = ProfileGalleryView.this.adapter.getRealPosition(i);
                    if (ProfileGalleryView.this.hasActiveVideo) {
                        realPosition--;
                    }
                    ProfileGalleryView.this.getCurrentItemView();
                    int childCount = ProfileGalleryView.this.getChildCount();
                    for (int i3 = 0; i3 < childCount; i3++) {
                        View childAt = ProfileGalleryView.this.getChildAt(i3);
                        if (childAt instanceof BackupImageView) {
                            int realPosition2 = ProfileGalleryView.this.adapter.getRealPosition(ProfileGalleryView.this.adapter.imageViews.indexOf(childAt));
                            if (ProfileGalleryView.this.hasActiveVideo) {
                                realPosition2--;
                            }
                            ImageReceiver imageReceiver = ((BackupImageView) childAt).getImageReceiver();
                            boolean allowStartAnimation = imageReceiver.getAllowStartAnimation();
                            if (realPosition2 == realPosition) {
                                if (!allowStartAnimation) {
                                    imageReceiver.setAllowStartAnimation(true);
                                    imageReceiver.startAnimation();
                                }
                                ImageLocation imageLocation2 = (ImageLocation) ProfileGalleryView.this.videoLocations.get(realPosition2);
                                if (imageLocation2 != null) {
                                    FileLoader.getInstance(ProfileGalleryView.this.currentAccount).setForceStreamLoadingFile(imageLocation2.location, "mp4");
                                }
                            } else if (allowStartAnimation) {
                                AnimatedFileDrawable animation = imageReceiver.getAnimation();
                                if (!(animation == null || (imageLocation = (ImageLocation) ProfileGalleryView.this.videoLocations.get(realPosition2)) == null)) {
                                    animation.seekTo(imageLocation.videoSeekTo, false, true);
                                }
                                imageReceiver.setAllowStartAnimation(false);
                                imageReceiver.stopAnimation();
                            }
                        }
                    }
                }
            }
        });
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getContext(), (ProfileActivity.AvatarImageView) null, actionBar);
        this.adapter = viewPagerAdapter;
        setAdapter((CircularViewPager.Adapter) viewPagerAdapter);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.dialogPhotosLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.reloadDialogPhotos);
    }

    public void setImagesLayerNum(int i) {
        this.imagesLayerNum = i;
    }

    public ProfileGalleryView(Context context, long j, ActionBar actionBar, RecyclerListView recyclerListView, ProfileActivity.AvatarImageView avatarImageView, int i, Callback callback2) {
        super(context);
        setVisibility(8);
        setOverScrollMode(2);
        setOffscreenPageLimit(2);
        this.isProfileFragment = true;
        this.dialogId = j;
        this.parentListView = recyclerListView;
        this.parentClassGuid = i;
        this.parentActionBar = actionBar;
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getContext(), avatarImageView, actionBar);
        this.adapter = viewPagerAdapter;
        setAdapter((CircularViewPager.Adapter) viewPagerAdapter);
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.callback = callback2;
        addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int i) {
            }

            public void onPageSelected(int i) {
            }

            public void onPageScrolled(int i, float f, int i2) {
                ImageLocation imageLocation;
                if (i2 == 0) {
                    int realPosition = ProfileGalleryView.this.adapter.getRealPosition(i);
                    ProfileGalleryView.this.getCurrentItemView();
                    int childCount = ProfileGalleryView.this.getChildCount();
                    for (int i3 = 0; i3 < childCount; i3++) {
                        View childAt = ProfileGalleryView.this.getChildAt(i3);
                        if (childAt instanceof BackupImageView) {
                            int realPosition2 = ProfileGalleryView.this.adapter.getRealPosition(ProfileGalleryView.this.adapter.imageViews.indexOf(childAt));
                            ImageReceiver imageReceiver = ((BackupImageView) childAt).getImageReceiver();
                            boolean allowStartAnimation = imageReceiver.getAllowStartAnimation();
                            if (realPosition2 == realPosition) {
                                if (!allowStartAnimation) {
                                    imageReceiver.setAllowStartAnimation(true);
                                    imageReceiver.startAnimation();
                                }
                                ImageLocation imageLocation2 = (ImageLocation) ProfileGalleryView.this.videoLocations.get(realPosition2);
                                if (imageLocation2 != null) {
                                    FileLoader.getInstance(ProfileGalleryView.this.currentAccount).setForceStreamLoadingFile(imageLocation2.location, "mp4");
                                }
                            } else if (allowStartAnimation) {
                                AnimatedFileDrawable animation = imageReceiver.getAnimation();
                                if (!(animation == null || (imageLocation = (ImageLocation) ProfileGalleryView.this.videoLocations.get(realPosition2)) == null)) {
                                    animation.seekTo(imageLocation.videoSeekTo, false, true);
                                }
                                imageReceiver.setAllowStartAnimation(false);
                                imageReceiver.stopAnimation();
                            }
                        }
                    }
                }
            }
        });
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.dialogPhotosLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.reloadDialogPhotos);
        MessagesController.getInstance(this.currentAccount).loadDialogPhotos(j, 80, 0, true, i);
    }

    public void onDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogPhotosLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.reloadDialogPhotos);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof BackupImageView) {
                BackupImageView backupImageView = (BackupImageView) childAt;
                if (backupImageView.getImageReceiver().hasStaticThumb()) {
                    Drawable drawable = backupImageView.getImageReceiver().getDrawable();
                    if (drawable instanceof AnimatedFileDrawable) {
                        ((AnimatedFileDrawable) drawable).removeSecondParentView(backupImageView);
                    }
                }
            }
        }
    }

    public void setAnimatedFileMaybe(AnimatedFileDrawable animatedFileDrawable) {
        if (animatedFileDrawable != null && this.adapter != null) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                if (childAt instanceof BackupImageView) {
                    ViewPagerAdapter viewPagerAdapter = this.adapter;
                    if (viewPagerAdapter.getRealPosition(viewPagerAdapter.imageViews.indexOf(childAt)) == 0) {
                        BackupImageView backupImageView = (BackupImageView) childAt;
                        AnimatedFileDrawable animation = backupImageView.getImageReceiver().getAnimation();
                        if (animation != animatedFileDrawable) {
                            if (animation != null) {
                                animation.removeSecondParentView(backupImageView);
                            }
                            backupImageView.setImageDrawable(animatedFileDrawable);
                            animatedFileDrawable.addSecondParentView(this);
                            animatedFileDrawable.setInvalidateParentViewWithSecond(true);
                        }
                    }
                }
            }
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int i;
        if (this.adapter == null) {
            return false;
        }
        if (this.parentListView.getScrollState() == 0 || this.isScrollingListView || !this.isSwipingViewPager) {
            int action = motionEvent.getAction();
            if (!(this.pinchToZoomHelper == null || getCurrentItemView() == null)) {
                if (action != 0 && this.isDownReleased && !this.pinchToZoomHelper.isInOverlayMode()) {
                    this.pinchToZoomHelper.checkPinchToZoom(MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0), this, getCurrentItemView().getImageReceiver(), (MessageObject) null);
                } else if (this.pinchToZoomHelper.checkPinchToZoom(motionEvent, this, getCurrentItemView().getImageReceiver(), (MessageObject) null)) {
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
                this.downPoint.set(motionEvent.getX(), motionEvent.getY());
                if (this.adapter.getCount() > 1) {
                    this.callback.onDown(motionEvent.getX() < ((float) getWidth()) / 3.0f);
                }
                this.isDownReleased = false;
            } else if (action == 1) {
                if (!this.isDownReleased) {
                    int count = this.adapter.getCount();
                    int currentItem = getCurrentItem();
                    if (count > 1) {
                        if (motionEvent.getX() > ((float) getWidth()) / 3.0f) {
                            i = this.adapter.getExtraCount();
                            int i2 = currentItem + 1;
                            if (i2 < count - i) {
                                i = i2;
                            }
                        } else {
                            int extraCount = this.adapter.getExtraCount();
                            int i3 = -1 + currentItem;
                            i = i3 < extraCount ? (count - extraCount) - 1 : i3;
                        }
                        this.callback.onRelease();
                        setCurrentItem(i, false);
                    }
                }
            } else if (action == 2) {
                float x = motionEvent.getX() - this.downPoint.x;
                float y = motionEvent.getY() - this.downPoint.y;
                boolean z = Math.abs(y) >= ((float) this.touchSlop) || Math.abs(x) >= ((float) this.touchSlop);
                if (z) {
                    this.isDownReleased = true;
                    this.callback.onRelease();
                }
                boolean z2 = this.isSwipingViewPager;
                if (!z2 || !this.isScrollingListView) {
                    if (z2 && !canScrollHorizontally(-1) && x > ((float) this.touchSlop)) {
                        return false;
                    }
                } else if (z) {
                    if (Math.abs(y) > Math.abs(x)) {
                        this.isSwipingViewPager = false;
                        MotionEvent obtain = MotionEvent.obtain(motionEvent);
                        obtain.setAction(3);
                        super.onTouchEvent(obtain);
                        obtain.recycle();
                    } else {
                        this.isScrollingListView = false;
                        MotionEvent obtain2 = MotionEvent.obtain(motionEvent);
                        obtain2.setAction(3);
                        this.parentListView.onTouchEvent(obtain2);
                        obtain2.recycle();
                    }
                }
            }
            boolean onTouchEvent = this.isScrollingListView ? this.parentListView.onTouchEvent(motionEvent) : false;
            if (this.isSwipingViewPager) {
                onTouchEvent |= super.onTouchEvent(motionEvent);
            }
            if (action == 1 || action == 3) {
                this.isScrollingListView = false;
                this.isSwipingViewPager = false;
            }
            return onTouchEvent;
        }
        this.isSwipingViewPager = false;
        MotionEvent obtain3 = MotionEvent.obtain(motionEvent);
        obtain3.setAction(3);
        super.onTouchEvent(obtain3);
        obtain3.recycle();
        return false;
    }

    public void setChatInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        this.chatInfo = tLRPC$ChatFull;
        if (!this.photos.isEmpty() && this.photos.get(0) == null && this.chatInfo != null && FileLoader.isSamePhoto((TLRPC$FileLocation) this.imagesLocations.get(0).location, this.chatInfo.chat_photo)) {
            this.photos.set(0, this.chatInfo.chat_photo);
            if (!this.chatInfo.chat_photo.video_sizes.isEmpty()) {
                TLRPC$VideoSize tLRPC$VideoSize = this.chatInfo.chat_photo.video_sizes.get(0);
                this.videoLocations.set(0, ImageLocation.getForPhoto(tLRPC$VideoSize, this.chatInfo.chat_photo));
                this.videoFileNames.set(0, FileLoader.getAttachFileName(tLRPC$VideoSize));
                this.callback.onPhotosLoaded();
            } else {
                this.videoLocations.set(0, (Object) null);
                this.videoFileNames.add(0, (Object) null);
            }
            this.imagesUploadProgress.set(0, (Object) null);
            this.adapter.notifyDataSetChanged();
        }
    }

    public boolean initIfEmpty(ImageLocation imageLocation, ImageLocation imageLocation2) {
        if (imageLocation == null || imageLocation2 == null || this.settingMainPhoto != 0) {
            return false;
        }
        ImageLocation imageLocation3 = this.prevImageLocation;
        if (imageLocation3 == null || imageLocation3.location.local_id != imageLocation.location.local_id) {
            if (!this.imagesLocations.isEmpty()) {
                this.prevImageLocation = imageLocation;
                MessagesController.getInstance(this.currentAccount).loadDialogPhotos(this.dialogId, 80, 0, true, this.parentClassGuid);
                return true;
            }
            MessagesController.getInstance(this.currentAccount).loadDialogPhotos(this.dialogId, 80, 0, true, this.parentClassGuid);
        }
        if (!this.imagesLocations.isEmpty()) {
            return false;
        }
        this.prevImageLocation = imageLocation;
        this.thumbsFileNames.add((Object) null);
        this.videoFileNames.add((Object) null);
        this.imagesLocations.add(imageLocation);
        this.thumbsLocations.add(imageLocation2);
        this.videoLocations.add((Object) null);
        this.photos.add((Object) null);
        this.imagesLocationsSizes.add(-1);
        this.imagesUploadProgress.add((Object) null);
        getAdapter().notifyDataSetChanged();
        return true;
    }

    public void addUploadingImage(ImageLocation imageLocation, ImageLocation imageLocation2) {
        this.prevImageLocation = imageLocation;
        this.thumbsFileNames.add(0, (Object) null);
        this.videoFileNames.add(0, (Object) null);
        this.imagesLocations.add(0, imageLocation);
        this.thumbsLocations.add(0, imageLocation2);
        this.videoLocations.add(0, (Object) null);
        this.photos.add(0, (Object) null);
        this.imagesLocationsSizes.add(0, -1);
        this.imagesUploadProgress.add(0, Float.valueOf(0.0f));
        this.adapter.notifyDataSetChanged();
        resetCurrentItem();
        this.currentUploadingImageLocation = imageLocation;
        this.curreantUploadingThumbLocation = imageLocation2;
    }

    public void removeUploadingImage(ImageLocation imageLocation) {
        this.uploadingImageLocation = imageLocation;
        this.currentUploadingImageLocation = null;
        this.curreantUploadingThumbLocation = null;
    }

    public ImageLocation getImageLocation(int i) {
        if (i < 0 || i >= this.imagesLocations.size()) {
            return null;
        }
        ImageLocation imageLocation = this.videoLocations.get(i);
        if (imageLocation != null) {
            return imageLocation;
        }
        return this.imagesLocations.get(i);
    }

    public ImageLocation getRealImageLocation(int i) {
        if (i < 0 || i >= this.imagesLocations.size()) {
            return null;
        }
        return this.imagesLocations.get(i);
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
        BackupImageView currentItemView;
        if (this.videoLocations.get(this.hasActiveVideo ? getRealPosition() - 1 : getRealPosition()) == null || (currentItemView = getCurrentItemView()) == null) {
            return false;
        }
        AnimatedFileDrawable animation = currentItemView.getImageReceiver().getAnimation();
        if (animation == null || !animation.hasBitmap()) {
            return true;
        }
        return false;
    }

    public float getCurrentItemProgress() {
        AnimatedFileDrawable animation;
        BackupImageView currentItemView = getCurrentItemView();
        if (currentItemView == null || (animation = currentItemView.getImageReceiver().getAnimation()) == null) {
            return 0.0f;
        }
        return animation.getCurrentProgress();
    }

    public boolean isCurrentItemVideo() {
        int realPosition = getRealPosition();
        if (this.hasActiveVideo) {
            if (realPosition == 0) {
                return false;
            }
            realPosition--;
        }
        if (this.videoLocations.get(realPosition) != null) {
            return true;
        }
        return false;
    }

    public ImageLocation getCurrentVideoLocation(ImageLocation imageLocation, ImageLocation imageLocation2) {
        TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated;
        if (imageLocation == null) {
            return null;
        }
        int i = 0;
        while (i < 2) {
            ArrayList<ImageLocation> arrayList = i == 0 ? this.thumbsLocations : this.imagesLocations;
            int size = arrayList.size();
            for (int i2 = 0; i2 < size; i2++) {
                ImageLocation imageLocation3 = arrayList.get(i2);
                if (!(imageLocation3 == null || (tLRPC$TL_fileLocationToBeDeprecated = imageLocation3.location) == null)) {
                    int i3 = imageLocation3.dc_id;
                    if (i3 == imageLocation.dc_id) {
                        int i4 = tLRPC$TL_fileLocationToBeDeprecated.local_id;
                        TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated2 = imageLocation.location;
                        if (i4 == tLRPC$TL_fileLocationToBeDeprecated2.local_id && tLRPC$TL_fileLocationToBeDeprecated.volume_id == tLRPC$TL_fileLocationToBeDeprecated2.volume_id) {
                            return this.videoLocations.get(i2);
                        }
                    }
                    if (i3 == imageLocation2.dc_id) {
                        int i5 = tLRPC$TL_fileLocationToBeDeprecated.local_id;
                        TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated3 = imageLocation2.location;
                        if (i5 == tLRPC$TL_fileLocationToBeDeprecated3.local_id && tLRPC$TL_fileLocationToBeDeprecated.volume_id == tLRPC$TL_fileLocationToBeDeprecated3.volume_id) {
                            return this.videoLocations.get(i2);
                        }
                    } else {
                        continue;
                    }
                }
            }
            i++;
        }
        return null;
    }

    public void resetCurrentItem() {
        setCurrentItem(this.adapter.getExtraCount(), false);
    }

    public int getRealCount() {
        int size = this.photos.size();
        return this.hasActiveVideo ? size + 1 : size;
    }

    public int getRealPosition(int i) {
        return this.adapter.getRealPosition(i);
    }

    public int getRealPosition() {
        return this.adapter.getRealPosition(getCurrentItem());
    }

    public TLRPC$Photo getPhoto(int i) {
        if (i < 0 || i >= this.photos.size()) {
            return null;
        }
        return this.photos.get(i);
    }

    public void replaceFirstPhoto(TLRPC$Photo tLRPC$Photo, TLRPC$Photo tLRPC$Photo2) {
        int indexOf;
        if (!this.photos.isEmpty() && (indexOf = this.photos.indexOf(tLRPC$Photo)) >= 0) {
            this.photos.set(indexOf, tLRPC$Photo2);
        }
    }

    public void finishSettingMainPhoto() {
        this.settingMainPhoto--;
    }

    public void startMovePhotoToBegin(int i) {
        if (i > 0 && i < this.photos.size()) {
            this.settingMainPhoto++;
            this.photos.remove(i);
            this.photos.add(0, this.photos.get(i));
            this.thumbsFileNames.remove(i);
            this.thumbsFileNames.add(0, this.thumbsFileNames.get(i));
            ArrayList<String> arrayList = this.videoFileNames;
            arrayList.add(0, arrayList.remove(i));
            this.videoLocations.remove(i);
            this.videoLocations.add(0, this.videoLocations.get(i));
            this.imagesLocations.remove(i);
            this.imagesLocations.add(0, this.imagesLocations.get(i));
            this.thumbsLocations.remove(i);
            this.thumbsLocations.add(0, this.thumbsLocations.get(i));
            this.imagesLocationsSizes.remove(i);
            this.imagesLocationsSizes.add(0, this.imagesLocationsSizes.get(i));
            this.imagesUploadProgress.remove(i);
            this.imagesUploadProgress.add(0, this.imagesUploadProgress.get(i));
            this.prevImageLocation = this.imagesLocations.get(0);
        }
    }

    public void commitMoveToBegin() {
        this.adapter.notifyDataSetChanged();
        resetCurrentItem();
    }

    public boolean removePhotoAtIndex(int i) {
        if (i < 0 || i >= this.photos.size()) {
            return false;
        }
        this.photos.remove(i);
        this.thumbsFileNames.remove(i);
        this.videoFileNames.remove(i);
        this.videoLocations.remove(i);
        this.imagesLocations.remove(i);
        this.thumbsLocations.remove(i);
        this.imagesLocationsSizes.remove(i);
        this.radialProgresses.delete(i);
        this.imagesUploadProgress.remove(i);
        if (i == 0 && !this.imagesLocations.isEmpty()) {
            this.prevImageLocation = this.imagesLocations.get(0);
        }
        this.adapter.notifyDataSetChanged();
        return this.photos.isEmpty();
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.parentListView.getScrollState() != 0) {
            return false;
        }
        if (!(getParent() == null || getParent().getParent() == null)) {
            getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    private void loadNeighboringThumbs() {
        int size = this.thumbsLocations.size();
        if (size > 1) {
            int i = 0;
            while (true) {
                int i2 = 2;
                if (size <= 2) {
                    i2 = 1;
                }
                if (i < i2) {
                    FileLoader.getInstance(this.currentAccount).loadFile(this.thumbsLocations.get(i == 0 ? 1 : size - 1), (Object) null, (String) null, 0, 1);
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0187, code lost:
        if (r4 != false) goto L_0x020b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r17, int r18, java.lang.Object... r19) {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
            int r2 = org.telegram.messenger.NotificationCenter.dialogPhotosLoaded
            r3 = 2
            r4 = 1
            r5 = 0
            if (r1 != r2) goto L_0x0264
            r1 = 3
            r1 = r19[r1]
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            r2 = r19[r5]
            java.lang.Long r2 = (java.lang.Long) r2
            long r7 = r2.longValue()
            long r9 = r0.dialogId
            int r2 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r2 != 0) goto L_0x0313
            int r2 = r0.parentClassGuid
            if (r2 != r1) goto L_0x0313
            org.telegram.ui.Components.ProfileGalleryView$ViewPagerAdapter r1 = r0.adapter
            if (r1 == 0) goto L_0x0313
            r1 = r19[r3]
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            r2 = 4
            r2 = r19[r2]
            java.util.ArrayList r2 = (java.util.ArrayList) r2
            java.util.ArrayList<java.lang.String> r3 = r0.thumbsFileNames
            r3.clear()
            java.util.ArrayList<java.lang.String> r3 = r0.videoFileNames
            r3.clear()
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r3 = r0.imagesLocations
            r3.clear()
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r3 = r0.videoLocations
            r3.clear()
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r3 = r0.thumbsLocations
            r3.clear()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r3 = r0.photos
            r3.clear()
            java.util.ArrayList<java.lang.Integer> r3 = r0.imagesLocationsSizes
            r3.clear()
            java.util.ArrayList<java.lang.Float> r3 = r0.imagesUploadProgress
            r3.clear()
            boolean r3 = org.telegram.messenger.DialogObject.isChatDialog(r7)
            r6 = 0
            if (r3 == 0) goto L_0x00fe
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r9 = -r7
            java.lang.Long r9 = java.lang.Long.valueOf(r9)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r9)
            org.telegram.messenger.ImageLocation r9 = org.telegram.messenger.ImageLocation.getForUserOrChat(r3, r5)
            if (r9 == 0) goto L_0x00ff
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r10 = r0.imagesLocations
            r10.add(r9)
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r10 = r0.thumbsLocations
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForUserOrChat(r3, r4)
            r10.add(r3)
            java.util.ArrayList<java.lang.String> r3 = r0.thumbsFileNames
            r3.add(r6)
            org.telegram.tgnet.TLRPC$ChatFull r3 = r0.chatInfo
            if (r3 == 0) goto L_0x00df
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r10 = r9.location
            org.telegram.tgnet.TLRPC$Photo r3 = r3.chat_photo
            boolean r3 = org.telegram.messenger.FileLoader.isSamePhoto((org.telegram.tgnet.TLRPC$FileLocation) r10, (org.telegram.tgnet.TLRPC$Photo) r3)
            if (r3 == 0) goto L_0x00df
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r3 = r0.photos
            org.telegram.tgnet.TLRPC$ChatFull r10 = r0.chatInfo
            org.telegram.tgnet.TLRPC$Photo r10 = r10.chat_photo
            r3.add(r10)
            org.telegram.tgnet.TLRPC$ChatFull r3 = r0.chatInfo
            org.telegram.tgnet.TLRPC$Photo r3 = r3.chat_photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r3 = r3.video_sizes
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x00d4
            org.telegram.tgnet.TLRPC$ChatFull r3 = r0.chatInfo
            org.telegram.tgnet.TLRPC$Photo r3 = r3.chat_photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r3 = r3.video_sizes
            java.lang.Object r3 = r3.get(r5)
            org.telegram.tgnet.TLRPC$VideoSize r3 = (org.telegram.tgnet.TLRPC$VideoSize) r3
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r10 = r0.videoLocations
            org.telegram.tgnet.TLRPC$ChatFull r11 = r0.chatInfo
            org.telegram.tgnet.TLRPC$Photo r11 = r11.chat_photo
            org.telegram.messenger.ImageLocation r11 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$VideoSize) r3, (org.telegram.tgnet.TLRPC$Photo) r11)
            r10.add(r11)
            java.util.ArrayList<java.lang.String> r10 = r0.videoFileNames
            java.lang.String r3 = org.telegram.messenger.FileLoader.getAttachFileName(r3)
            r10.add(r3)
            goto L_0x00ee
        L_0x00d4:
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r3 = r0.videoLocations
            r3.add(r6)
            java.util.ArrayList<java.lang.String> r3 = r0.videoFileNames
            r3.add(r6)
            goto L_0x00ee
        L_0x00df:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r3 = r0.photos
            r3.add(r6)
            java.util.ArrayList<java.lang.String> r3 = r0.videoFileNames
            r3.add(r6)
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r3 = r0.videoLocations
            r3.add(r6)
        L_0x00ee:
            java.util.ArrayList<java.lang.Integer> r3 = r0.imagesLocationsSizes
            r10 = -1
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r3.add(r10)
            java.util.ArrayList<java.lang.Float> r3 = r0.imagesUploadProgress
            r3.add(r6)
            goto L_0x00ff
        L_0x00fe:
            r9 = r6
        L_0x00ff:
            r3 = 0
        L_0x0100:
            int r10 = r2.size()
            if (r3 >= r10) goto L_0x0212
            java.lang.Object r10 = r2.get(r3)
            org.telegram.tgnet.TLRPC$Photo r10 = (org.telegram.tgnet.TLRPC$Photo) r10
            if (r10 == 0) goto L_0x020c
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r11 != 0) goto L_0x020c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r11 = r10.sizes
            if (r11 != 0) goto L_0x0118
            goto L_0x020c
        L_0x0118:
            r12 = 50
            org.telegram.tgnet.TLRPC$PhotoSize r11 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r11, r12)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r12 = r10.sizes
            int r12 = r12.size()
            r13 = 0
        L_0x0125:
            if (r13 >= r12) goto L_0x0138
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r14 = r10.sizes
            java.lang.Object r14 = r14.get(r13)
            org.telegram.tgnet.TLRPC$PhotoSize r14 = (org.telegram.tgnet.TLRPC$PhotoSize) r14
            boolean r15 = r14 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
            if (r15 == 0) goto L_0x0135
            r11 = r14
            goto L_0x0138
        L_0x0135:
            int r13 = r13 + 1
            goto L_0x0125
        L_0x0138:
            if (r9 == 0) goto L_0x018b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r12 = r10.sizes
            int r12 = r12.size()
            r13 = 0
        L_0x0141:
            if (r13 >= r12) goto L_0x0186
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r14 = r10.sizes
            java.lang.Object r14 = r14.get(r13)
            org.telegram.tgnet.TLRPC$PhotoSize r14 = (org.telegram.tgnet.TLRPC$PhotoSize) r14
            org.telegram.tgnet.TLRPC$FileLocation r14 = r14.location
            if (r14 == 0) goto L_0x0180
            int r15 = r14.local_id
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r4 = r9.location
            int r6 = r4.local_id
            if (r15 != r6) goto L_0x0180
            long r14 = r14.volume_id
            long r5 = r4.volume_id
            int r4 = (r14 > r5 ? 1 : (r14 == r5 ? 0 : -1))
            if (r4 != 0) goto L_0x0180
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r4 = r0.photos
            r5 = 0
            r4.set(r5, r10)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r4 = r10.video_sizes
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x017e
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r4 = r0.videoLocations
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r6 = r10.video_sizes
            java.lang.Object r6 = r6.get(r5)
            org.telegram.tgnet.TLRPC$VideoSize r6 = (org.telegram.tgnet.TLRPC$VideoSize) r6
            org.telegram.messenger.ImageLocation r6 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$VideoSize) r6, (org.telegram.tgnet.TLRPC$Photo) r10)
            r4.set(r5, r6)
        L_0x017e:
            r4 = 1
            goto L_0x0187
        L_0x0180:
            int r13 = r13 + 1
            r4 = 1
            r5 = 0
            r6 = 0
            goto L_0x0141
        L_0x0186:
            r4 = 0
        L_0x0187:
            if (r4 == 0) goto L_0x018b
            goto L_0x020b
        L_0x018b:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r10.sizes
            r5 = 640(0x280, float:8.97E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r5)
            if (r4 == 0) goto L_0x020b
            int r5 = r10.dc_id
            if (r5 == 0) goto L_0x01a1
            org.telegram.tgnet.TLRPC$FileLocation r6 = r4.location
            r6.dc_id = r5
            byte[] r5 = r10.file_reference
            r6.file_reference = r5
        L_0x01a1:
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r4, (org.telegram.tgnet.TLRPC$Photo) r10)
            if (r5 == 0) goto L_0x020b
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r6 = r0.imagesLocations
            r6.add(r5)
            java.util.ArrayList<java.lang.String> r5 = r0.thumbsFileNames
            boolean r6 = r11 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
            if (r6 == 0) goto L_0x01b4
            r6 = r4
            goto L_0x01b5
        L_0x01b4:
            r6 = r11
        L_0x01b5:
            java.lang.String r6 = org.telegram.messenger.FileLoader.getAttachFileName(r6)
            r5.add(r6)
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r5 = r0.thumbsLocations
            org.telegram.messenger.ImageLocation r6 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r11, (org.telegram.tgnet.TLRPC$Photo) r10)
            r5.add(r6)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r5 = r10.video_sizes
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x01ea
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r5 = r10.video_sizes
            r6 = 0
            java.lang.Object r5 = r5.get(r6)
            org.telegram.tgnet.TLRPC$VideoSize r5 = (org.telegram.tgnet.TLRPC$VideoSize) r5
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r6 = r0.videoLocations
            org.telegram.messenger.ImageLocation r11 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$VideoSize) r5, (org.telegram.tgnet.TLRPC$Photo) r10)
            r6.add(r11)
            java.util.ArrayList<java.lang.String> r6 = r0.videoFileNames
            java.lang.String r5 = org.telegram.messenger.FileLoader.getAttachFileName(r5)
            r6.add(r5)
            r6 = 0
            goto L_0x01f5
        L_0x01ea:
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r5 = r0.videoLocations
            r6 = 0
            r5.add(r6)
            java.util.ArrayList<java.lang.String> r5 = r0.videoFileNames
            r5.add(r6)
        L_0x01f5:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r5 = r0.photos
            r5.add(r10)
            java.util.ArrayList<java.lang.Integer> r5 = r0.imagesLocationsSizes
            int r4 = r4.size
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r5.add(r4)
            java.util.ArrayList<java.lang.Float> r4 = r0.imagesUploadProgress
            r4.add(r6)
            goto L_0x020c
        L_0x020b:
            r6 = 0
        L_0x020c:
            int r3 = r3 + 1
            r4 = 1
            r5 = 0
            goto L_0x0100
        L_0x0212:
            r16.loadNeighboringThumbs()
            androidx.viewpager.widget.PagerAdapter r2 = r16.getAdapter()
            r2.notifyDataSetChanged()
            boolean r2 = r0.isProfileFragment
            if (r2 == 0) goto L_0x022c
            boolean r2 = r0.scrolledByUser
            if (r2 == 0) goto L_0x0228
            boolean r2 = r0.forceResetPosition
            if (r2 == 0) goto L_0x023e
        L_0x0228:
            r16.resetCurrentItem()
            goto L_0x023e
        L_0x022c:
            boolean r2 = r0.scrolledByUser
            if (r2 == 0) goto L_0x0234
            boolean r2 = r0.forceResetPosition
            if (r2 == 0) goto L_0x023e
        L_0x0234:
            r16.resetCurrentItem()
            androidx.viewpager.widget.PagerAdapter r2 = r16.getAdapter()
            r2.notifyDataSetChanged()
        L_0x023e:
            r2 = 0
            r0.forceResetPosition = r2
            if (r1 == 0) goto L_0x0252
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r1)
            r9 = 80
            r10 = 0
            r11 = 0
            int r12 = r0.parentClassGuid
            r6.loadDialogPhotos(r7, r9, r10, r11, r12)
        L_0x0252:
            org.telegram.ui.Components.ProfileGalleryView$Callback r1 = r0.callback
            if (r1 == 0) goto L_0x0259
            r1.onPhotosLoaded()
        L_0x0259:
            org.telegram.messenger.ImageLocation r1 = r0.currentUploadingImageLocation
            if (r1 == 0) goto L_0x0313
            org.telegram.messenger.ImageLocation r2 = r0.curreantUploadingThumbLocation
            r0.addUploadingImage(r1, r2)
            goto L_0x0313
        L_0x0264:
            int r2 = org.telegram.messenger.NotificationCenter.fileLoaded
            r4 = 1065353216(0x3var_, float:1.0)
            if (r1 != r2) goto L_0x02a3
            r2 = 0
            r1 = r19[r2]
            java.lang.String r1 = (java.lang.String) r1
            r5 = 0
        L_0x0270:
            java.util.ArrayList<java.lang.String> r2 = r0.thumbsFileNames
            int r2 = r2.size()
            if (r5 >= r2) goto L_0x0313
            java.util.ArrayList<java.lang.String> r2 = r0.videoFileNames
            java.lang.Object r2 = r2.get(r5)
            java.lang.String r2 = (java.lang.String) r2
            if (r2 != 0) goto L_0x028a
            java.util.ArrayList<java.lang.String> r2 = r0.thumbsFileNames
            java.lang.Object r2 = r2.get(r5)
            java.lang.String r2 = (java.lang.String) r2
        L_0x028a:
            if (r2 == 0) goto L_0x02a0
            boolean r2 = android.text.TextUtils.equals(r1, r2)
            if (r2 == 0) goto L_0x02a0
            android.util.SparseArray<org.telegram.ui.Components.RadialProgress2> r2 = r0.radialProgresses
            java.lang.Object r2 = r2.get(r5)
            org.telegram.ui.Components.RadialProgress2 r2 = (org.telegram.ui.Components.RadialProgress2) r2
            if (r2 == 0) goto L_0x02a0
            r3 = 1
            r2.setProgress(r4, r3)
        L_0x02a0:
            int r5 = r5 + 1
            goto L_0x0270
        L_0x02a3:
            int r2 = org.telegram.messenger.NotificationCenter.fileLoadProgressChanged
            if (r1 != r2) goto L_0x02f9
            r2 = 0
            r1 = r19[r2]
            java.lang.String r1 = (java.lang.String) r1
            r5 = 0
        L_0x02ad:
            java.util.ArrayList<java.lang.String> r2 = r0.thumbsFileNames
            int r2 = r2.size()
            if (r5 >= r2) goto L_0x0313
            java.util.ArrayList<java.lang.String> r2 = r0.videoFileNames
            java.lang.Object r2 = r2.get(r5)
            java.lang.String r2 = (java.lang.String) r2
            if (r2 != 0) goto L_0x02c7
            java.util.ArrayList<java.lang.String> r2 = r0.thumbsFileNames
            java.lang.Object r2 = r2.get(r5)
            java.lang.String r2 = (java.lang.String) r2
        L_0x02c7:
            if (r2 == 0) goto L_0x02f5
            boolean r2 = android.text.TextUtils.equals(r1, r2)
            if (r2 == 0) goto L_0x02f5
            android.util.SparseArray<org.telegram.ui.Components.RadialProgress2> r2 = r0.radialProgresses
            java.lang.Object r2 = r2.get(r5)
            org.telegram.ui.Components.RadialProgress2 r2 = (org.telegram.ui.Components.RadialProgress2) r2
            if (r2 == 0) goto L_0x02f5
            r6 = 1
            r7 = r19[r6]
            java.lang.Long r7 = (java.lang.Long) r7
            r8 = r19[r3]
            java.lang.Long r8 = (java.lang.Long) r8
            long r9 = r7.longValue()
            float r7 = (float) r9
            long r8 = r8.longValue()
            float r8 = (float) r8
            float r7 = r7 / r8
            float r7 = java.lang.Math.min(r4, r7)
            r2.setProgress(r7, r6)
            goto L_0x02f6
        L_0x02f5:
            r6 = 1
        L_0x02f6:
            int r5 = r5 + 1
            goto L_0x02ad
        L_0x02f9:
            int r2 = org.telegram.messenger.NotificationCenter.reloadDialogPhotos
            if (r1 != r2) goto L_0x0313
            int r1 = r0.settingMainPhoto
            if (r1 == 0) goto L_0x0302
            return
        L_0x0302:
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r1)
            long r3 = r0.dialogId
            r5 = 80
            r6 = 0
            r7 = 1
            int r8 = r0.parentClassGuid
            r2.loadDialogPhotos(r3, r5, r6, r7, r8)
        L_0x0313:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ProfileGalleryView.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    public class ViewPagerAdapter extends CircularViewPager.Adapter {
        private final Context context;
        /* access modifiers changed from: private */
        public final ArrayList<BackupImageView> imageViews = new ArrayList<>();
        /* access modifiers changed from: private */
        public final ArrayList<Item> objects = new ArrayList<>();
        /* access modifiers changed from: private */
        public BackupImageView parentAvatarImageView;
        private final Paint placeholderPaint;

        public ViewPagerAdapter(Context context2, ProfileActivity.AvatarImageView avatarImageView, ActionBar actionBar) {
            this.context = context2;
            this.parentAvatarImageView = avatarImageView;
            Paint paint = new Paint(1);
            this.placeholderPaint = paint;
            paint.setColor(-16777216);
        }

        public int getCount() {
            return this.objects.size();
        }

        public boolean isViewFromObject(View view, Object obj) {
            Item item = (Item) obj;
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

        public int getItemPosition(Object obj) {
            int indexOf = this.objects.indexOf((Item) obj);
            if (indexOf == -1) {
                return -2;
            }
            return indexOf;
        }

        /* JADX WARNING: Removed duplicated region for block: B:75:0x0292  */
        /* JADX WARNING: Removed duplicated region for block: B:77:0x0295  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public org.telegram.ui.Components.ProfileGalleryView.Item instantiateItem(android.view.ViewGroup r24, int r25) {
            /*
                r23 = this;
                r0 = r23
                r1 = r24
                r2 = r25
                java.util.ArrayList<org.telegram.ui.Components.ProfileGalleryView$Item> r3 = r0.objects
                java.lang.Object r3 = r3.get(r2)
                org.telegram.ui.Components.ProfileGalleryView$Item r3 = (org.telegram.ui.Components.ProfileGalleryView.Item) r3
                int r4 = r0.getRealPosition(r2)
                org.telegram.ui.Components.ProfileGalleryView r5 = org.telegram.ui.Components.ProfileGalleryView.this
                boolean r5 = r5.hasActiveVideo
                r6 = 1
                if (r5 == 0) goto L_0x0043
                if (r4 != 0) goto L_0x0043
                r3.isActiveVideo = r6
                android.view.View r2 = r3.textureViewStubView
                if (r2 != 0) goto L_0x0031
                org.telegram.ui.Components.ProfileGalleryView$TextureStubView r2 = new org.telegram.ui.Components.ProfileGalleryView$TextureStubView
                org.telegram.ui.Components.ProfileGalleryView r4 = org.telegram.ui.Components.ProfileGalleryView.this
                android.content.Context r5 = r0.context
                r2.<init>(r4, r5)
                android.view.View unused = r3.textureViewStubView = r2
            L_0x0031:
                android.view.View r2 = r3.textureViewStubView
                android.view.ViewParent r2 = r2.getParent()
                if (r2 != 0) goto L_0x0042
                android.view.View r2 = r3.textureViewStubView
                r1.addView(r2)
            L_0x0042:
                return r3
            L_0x0043:
                r5 = 0
                r3.isActiveVideo = r5
                android.view.View r7 = r3.textureViewStubView
                if (r7 == 0) goto L_0x005d
                android.view.View r7 = r3.textureViewStubView
                android.view.ViewParent r7 = r7.getParent()
                if (r7 == 0) goto L_0x005d
                android.view.View r7 = r3.textureViewStubView
                r1.removeView(r7)
            L_0x005d:
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r7 = r3.imageView
                if (r7 != 0) goto L_0x007a
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r7 = new org.telegram.ui.Components.ProfileGalleryView$AvatarImageView
                org.telegram.ui.Components.ProfileGalleryView r8 = org.telegram.ui.Components.ProfileGalleryView.this
                android.content.Context r9 = r0.context
                android.graphics.Paint r10 = r0.placeholderPaint
                r7.<init>(r9, r2, r10)
                org.telegram.ui.Components.ProfileGalleryView.AvatarImageView unused = r3.imageView = r7
                java.util.ArrayList<org.telegram.ui.Components.BackupImageView> r7 = r0.imageViews
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r8 = r3.imageView
                r7.set(r2, r8)
            L_0x007a:
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r2 = r3.imageView
                android.view.ViewParent r2 = r2.getParent()
                if (r2 != 0) goto L_0x008b
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r2 = r3.imageView
                r1.addView(r2)
            L_0x008b:
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r1 = r3.imageView
                org.telegram.messenger.ImageReceiver r1 = r1.getImageReceiver()
                r1.setAllowDecodeSingleFrame(r6)
                org.telegram.ui.Components.ProfileGalleryView r1 = org.telegram.ui.Components.ProfileGalleryView.this
                boolean r1 = r1.hasActiveVideo
                if (r1 == 0) goto L_0x00a0
                int r4 = r4 + -1
            L_0x00a0:
                r1 = 2
                java.lang.String r2 = "avatar_"
                java.lang.String r7 = "b"
                r8 = 0
                if (r4 != 0) goto L_0x0209
                org.telegram.ui.Components.BackupImageView r9 = r0.parentAvatarImageView
                if (r9 != 0) goto L_0x00ae
                r9 = r8
                goto L_0x00b6
            L_0x00ae:
                org.telegram.messenger.ImageReceiver r9 = r9.getImageReceiver()
                android.graphics.drawable.Drawable r9 = r9.getDrawable()
            L_0x00b6:
                boolean r10 = r9 instanceof org.telegram.ui.Components.AnimatedFileDrawable
                if (r10 == 0) goto L_0x00d7
                r10 = r9
                org.telegram.ui.Components.AnimatedFileDrawable r10 = (org.telegram.ui.Components.AnimatedFileDrawable) r10
                boolean r11 = r10.hasBitmap()
                if (r11 == 0) goto L_0x00d7
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r2 = r3.imageView
                r2.setImageDrawable(r9)
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r2 = r3.imageView
                r10.addSecondParentView(r2)
                r10.setInvalidateParentViewWithSecond(r6)
                r2 = 0
                goto L_0x0285
            L_0x00d7:
                org.telegram.ui.Components.ProfileGalleryView r9 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r9 = r9.videoLocations
                java.lang.Object r9 = r9.get(r4)
                r11 = r9
                org.telegram.messenger.ImageLocation r11 = (org.telegram.messenger.ImageLocation) r11
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r9 = r3.imageView
                if (r11 == 0) goto L_0x00ec
                r10 = 1
                goto L_0x00ed
            L_0x00ec:
                r10 = 0
            L_0x00ed:
                r9.isVideo = r10
                org.telegram.ui.Components.ProfileGalleryView r9 = org.telegram.ui.Components.ProfileGalleryView.this
                boolean r9 = r9.isProfileFragment
                if (r9 == 0) goto L_0x0101
                if (r11 == 0) goto L_0x0101
                int r9 = r11.imageType
                if (r9 != r1) goto L_0x0101
                java.lang.String r9 = "avatar"
                r14 = r9
                goto L_0x0102
            L_0x0101:
                r14 = r8
            L_0x0102:
                org.telegram.ui.Components.ProfileGalleryView r9 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r9 = r9.thumbsLocations
                java.lang.Object r9 = r9.get(r4)
                org.telegram.messenger.ImageLocation r9 = (org.telegram.messenger.ImageLocation) r9
                org.telegram.ui.Components.BackupImageView r10 = r0.parentAvatarImageView
                if (r10 == 0) goto L_0x0128
                org.telegram.ui.Components.ProfileGalleryView r10 = org.telegram.ui.Components.ProfileGalleryView.this
                boolean r10 = r10.createThumbFromParent
                if (r10 != 0) goto L_0x011b
                goto L_0x0128
            L_0x011b:
                org.telegram.ui.Components.BackupImageView r10 = r0.parentAvatarImageView
                org.telegram.messenger.ImageReceiver r10 = r10.getImageReceiver()
                android.graphics.Bitmap r10 = r10.getBitmap()
                r17 = r10
                goto L_0x012a
            L_0x0128:
                r17 = r8
            L_0x012a:
                java.lang.StringBuilder r10 = new java.lang.StringBuilder
                r10.<init>()
                r10.append(r2)
                org.telegram.ui.Components.ProfileGalleryView r2 = org.telegram.ui.Components.ProfileGalleryView.this
                long r12 = r2.dialogId
                r10.append(r12)
                java.lang.String r22 = r10.toString()
                if (r17 == 0) goto L_0x017a
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r12 = r3.imageView
                org.telegram.ui.Components.ProfileGalleryView r2 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r2 = r2.videoLocations
                java.lang.Object r2 = r2.get(r4)
                r13 = r2
                org.telegram.messenger.ImageLocation r13 = (org.telegram.messenger.ImageLocation) r13
                org.telegram.ui.Components.ProfileGalleryView r2 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r2 = r2.imagesLocations
                java.lang.Object r2 = r2.get(r4)
                r15 = r2
                org.telegram.messenger.ImageLocation r15 = (org.telegram.messenger.ImageLocation) r15
                r16 = 0
                org.telegram.ui.Components.ProfileGalleryView r2 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r2 = r2.imagesLocationsSizes
                java.lang.Object r2 = r2.get(r4)
                java.lang.Integer r2 = (java.lang.Integer) r2
                int r18 = r2.intValue()
                r19 = 1
                r20 = r22
                r12.setImageMedia(r13, r14, r15, r16, r17, r18, r19, r20)
                goto L_0x0284
            L_0x017a:
                org.telegram.ui.Components.ProfileGalleryView r2 = org.telegram.ui.Components.ProfileGalleryView.this
                org.telegram.messenger.ImageLocation r2 = r2.uploadingImageLocation
                if (r2 == 0) goto L_0x01c3
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r12 = r3.imageView
                org.telegram.ui.Components.ProfileGalleryView r2 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r2 = r2.videoLocations
                java.lang.Object r2 = r2.get(r4)
                r13 = r2
                org.telegram.messenger.ImageLocation r13 = (org.telegram.messenger.ImageLocation) r13
                org.telegram.ui.Components.ProfileGalleryView r2 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r2 = r2.imagesLocations
                java.lang.Object r2 = r2.get(r4)
                r15 = r2
                org.telegram.messenger.ImageLocation r15 = (org.telegram.messenger.ImageLocation) r15
                r16 = 0
                org.telegram.ui.Components.ProfileGalleryView r2 = org.telegram.ui.Components.ProfileGalleryView.this
                org.telegram.messenger.ImageLocation r17 = r2.uploadingImageLocation
                r18 = 0
                r19 = 0
                org.telegram.ui.Components.ProfileGalleryView r2 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r2 = r2.imagesLocationsSizes
                java.lang.Object r2 = r2.get(r4)
                java.lang.Integer r2 = (java.lang.Integer) r2
                int r20 = r2.intValue()
                r21 = 1
                r12.setImageMedia(r13, r14, r15, r16, r17, r18, r19, r20, r21, r22)
                goto L_0x0284
            L_0x01c3:
                org.telegram.tgnet.TLRPC$PhotoSize r2 = r9.photoSize
                boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
                if (r2 == 0) goto L_0x01cc
                r16 = r7
                goto L_0x01ce
            L_0x01cc:
                r16 = r8
            L_0x01ce:
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r10 = r3.imageView
                r12 = 0
                org.telegram.ui.Components.ProfileGalleryView r2 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r2 = r2.imagesLocations
                java.lang.Object r2 = r2.get(r4)
                r13 = r2
                org.telegram.messenger.ImageLocation r13 = (org.telegram.messenger.ImageLocation) r13
                r14 = 0
                org.telegram.ui.Components.ProfileGalleryView r2 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r2 = r2.thumbsLocations
                java.lang.Object r2 = r2.get(r4)
                r15 = r2
                org.telegram.messenger.ImageLocation r15 = (org.telegram.messenger.ImageLocation) r15
                r17 = 0
                org.telegram.ui.Components.ProfileGalleryView r2 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r2 = r2.imagesLocationsSizes
                java.lang.Object r2 = r2.get(r4)
                java.lang.Integer r2 = (java.lang.Integer) r2
                int r18 = r2.intValue()
                r19 = 1
                r20 = r22
                r10.setImageMedia(r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)
                goto L_0x0284
            L_0x0209:
                org.telegram.ui.Components.ProfileGalleryView r9 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r9 = r9.videoLocations
                java.lang.Object r9 = r9.get(r4)
                r11 = r9
                org.telegram.messenger.ImageLocation r11 = (org.telegram.messenger.ImageLocation) r11
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r9 = r3.imageView
                if (r11 == 0) goto L_0x021e
                r10 = 1
                goto L_0x021f
            L_0x021e:
                r10 = 0
            L_0x021f:
                r9.isVideo = r10
                org.telegram.ui.Components.ProfileGalleryView r9 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r9 = r9.thumbsLocations
                java.lang.Object r9 = r9.get(r4)
                org.telegram.messenger.ImageLocation r9 = (org.telegram.messenger.ImageLocation) r9
                org.telegram.tgnet.TLRPC$PhotoSize r9 = r9.photoSize
                boolean r9 = r9 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
                if (r9 == 0) goto L_0x0236
                r16 = r7
                goto L_0x0238
            L_0x0236:
                r16 = r8
            L_0x0238:
                java.lang.StringBuilder r7 = new java.lang.StringBuilder
                r7.<init>()
                r7.append(r2)
                org.telegram.ui.Components.ProfileGalleryView r2 = org.telegram.ui.Components.ProfileGalleryView.this
                long r8 = r2.dialogId
                r7.append(r8)
                java.lang.String r20 = r7.toString()
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r10 = r3.imageView
                r12 = 0
                org.telegram.ui.Components.ProfileGalleryView r2 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r2 = r2.imagesLocations
                java.lang.Object r2 = r2.get(r4)
                r13 = r2
                org.telegram.messenger.ImageLocation r13 = (org.telegram.messenger.ImageLocation) r13
                r14 = 0
                org.telegram.ui.Components.ProfileGalleryView r2 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r2 = r2.thumbsLocations
                java.lang.Object r2 = r2.get(r4)
                r15 = r2
                org.telegram.messenger.ImageLocation r15 = (org.telegram.messenger.ImageLocation) r15
                r17 = 0
                org.telegram.ui.Components.ProfileGalleryView r2 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r2 = r2.imagesLocationsSizes
                java.lang.Object r2 = r2.get(r4)
                java.lang.Integer r2 = (java.lang.Integer) r2
                int r18 = r2.intValue()
                r19 = 1
                r10.setImageMedia(r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)
            L_0x0284:
                r2 = 1
            L_0x0285:
                org.telegram.ui.Components.ProfileGalleryView r7 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r7 = r7.imagesUploadProgress
                java.lang.Object r7 = r7.get(r4)
                if (r7 == 0) goto L_0x0292
                goto L_0x0293
            L_0x0292:
                r6 = r2
            L_0x0293:
                if (r6 == 0) goto L_0x030d
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r2 = r3.imageView
                org.telegram.ui.Components.ProfileGalleryView r6 = org.telegram.ui.Components.ProfileGalleryView.this
                android.util.SparseArray r6 = r6.radialProgresses
                java.lang.Object r6 = r6.get(r4)
                org.telegram.ui.Components.RadialProgress2 r6 = (org.telegram.ui.Components.RadialProgress2) r6
                org.telegram.ui.Components.RadialProgress2 unused = r2.radialProgress = r6
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r2 = r3.imageView
                org.telegram.ui.Components.RadialProgress2 r2 = r2.radialProgress
                if (r2 != 0) goto L_0x02fa
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r2 = r3.imageView
                org.telegram.ui.Components.RadialProgress2 r6 = new org.telegram.ui.Components.RadialProgress2
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r7 = r3.imageView
                r6.<init>(r7)
                org.telegram.ui.Components.RadialProgress2 unused = r2.radialProgress = r6
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r2 = r3.imageView
                org.telegram.ui.Components.RadialProgress2 r2 = r2.radialProgress
                r6 = 0
                r2.setOverrideAlpha(r6)
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r2 = r3.imageView
                org.telegram.ui.Components.RadialProgress2 r2 = r2.radialProgress
                r6 = 10
                r2.setIcon(r6, r5, r5)
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r2 = r3.imageView
                org.telegram.ui.Components.RadialProgress2 r2 = r2.radialProgress
                r5 = 1107296256(0x42000000, float:32.0)
                r6 = -1
                r2.setColors((int) r5, (int) r5, (int) r6, (int) r6)
                org.telegram.ui.Components.ProfileGalleryView r2 = org.telegram.ui.Components.ProfileGalleryView.this
                android.util.SparseArray r2 = r2.radialProgresses
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r5 = r3.imageView
                org.telegram.ui.Components.RadialProgress2 r5 = r5.radialProgress
                r2.append(r4, r5)
            L_0x02fa:
                org.telegram.ui.Components.ProfileGalleryView r2 = org.telegram.ui.Components.ProfileGalleryView.this
                boolean r2 = r2.invalidateWithParent
                if (r2 == 0) goto L_0x0308
                org.telegram.ui.Components.ProfileGalleryView r2 = org.telegram.ui.Components.ProfileGalleryView.this
                r2.invalidate()
                goto L_0x030d
            L_0x0308:
                org.telegram.ui.Components.ProfileGalleryView r2 = org.telegram.ui.Components.ProfileGalleryView.this
                r2.postInvalidateOnAnimation()
            L_0x030d:
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r2 = r3.imageView
                org.telegram.messenger.ImageReceiver r2 = r2.getImageReceiver()
                org.telegram.ui.Components.ProfileGalleryView$ViewPagerAdapter$1 r4 = new org.telegram.ui.Components.ProfileGalleryView$ViewPagerAdapter$1
                r4.<init>()
                r2.setDelegate(r4)
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r2 = r3.imageView
                org.telegram.messenger.ImageReceiver r2 = r2.getImageReceiver()
                r2.setCrossfadeAlpha(r1)
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r1 = r3.imageView
                org.telegram.ui.Components.ProfileGalleryView r2 = org.telegram.ui.Components.ProfileGalleryView.this
                int r2 = r2.roundTopRadius
                org.telegram.ui.Components.ProfileGalleryView r4 = org.telegram.ui.Components.ProfileGalleryView.this
                int r4 = r4.roundTopRadius
                org.telegram.ui.Components.ProfileGalleryView r5 = org.telegram.ui.Components.ProfileGalleryView.this
                int r5 = r5.roundBottomRadius
                org.telegram.ui.Components.ProfileGalleryView r6 = org.telegram.ui.Components.ProfileGalleryView.this
                int r6 = r6.roundBottomRadius
                r1.setRoundRadius(r2, r4, r5, r6)
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ProfileGalleryView.ViewPagerAdapter.instantiateItem(android.view.ViewGroup, int):org.telegram.ui.Components.ProfileGalleryView$Item");
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            Item item = (Item) obj;
            if (item.textureViewStubView != null) {
                viewGroup.removeView(item.textureViewStubView);
            }
            if (!item.isActiveVideo) {
                AvatarImageView access$600 = item.imageView;
                if (access$600.getImageReceiver().hasStaticThumb()) {
                    Drawable drawable = access$600.getImageReceiver().getDrawable();
                    if (drawable instanceof AnimatedFileDrawable) {
                        ((AnimatedFileDrawable) drawable).removeSecondParentView(access$600);
                    }
                }
                access$600.setRoundRadius(0);
                viewGroup.removeView(access$600);
                access$600.getImageReceiver().cancelLoadImage();
            }
        }

        public CharSequence getPageTitle(int i) {
            return (getRealPosition(i) + 1) + "/" + (getCount() - (getExtraCount() * 2));
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
            int extraCount = size + (getExtraCount() * 2);
            for (int i2 = 0; i2 < extraCount; i2++) {
                this.objects.add(new Item());
                this.imageViews.add((Object) null);
            }
            super.notifyDataSetChanged();
        }

        public int getExtraCount() {
            int size = ProfileGalleryView.this.imagesLocations.size();
            if (ProfileGalleryView.this.hasActiveVideo) {
                size++;
            }
            if (size >= 2) {
                return ProfileGalleryView.this.getOffscreenPageLimit();
            }
            return 0;
        }
    }

    public void setData(long j) {
        setData(j, false);
    }

    public void setData(long j, boolean z) {
        if (this.dialogId != j || z) {
            this.forceResetPosition = true;
            this.adapter.notifyDataSetChanged();
            reset();
            this.dialogId = j;
            if (j != 0) {
                MessagesController.getInstance(this.currentAccount).loadDialogPhotos(j, 80, 0, true, this.parentClassGuid);
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

    public void setRoundRadius(int i, int i2) {
        this.roundTopRadius = i;
        this.roundBottomRadius = i2;
        if (this.adapter != null) {
            for (int i3 = 0; i3 < this.adapter.objects.size(); i3++) {
                if (((Item) this.adapter.objects.get(i3)).imageView != null) {
                    AvatarImageView access$600 = ((Item) this.adapter.objects.get(i3)).imageView;
                    int i4 = this.roundTopRadius;
                    int i5 = this.roundBottomRadius;
                    access$600.setRoundRadius(i4, i4, i5, i5);
                }
            }
        }
    }

    public void setParentAvatarImage(BackupImageView backupImageView) {
        ViewPagerAdapter viewPagerAdapter = this.adapter;
        if (viewPagerAdapter != null) {
            BackupImageView unused = viewPagerAdapter.parentAvatarImageView = backupImageView;
        }
    }

    public void setUploadProgress(ImageLocation imageLocation, float f) {
        if (imageLocation != null) {
            int i = 0;
            while (true) {
                if (i >= this.imagesLocations.size()) {
                    break;
                } else if (this.imagesLocations.get(i) == imageLocation) {
                    this.imagesUploadProgress.set(i, Float.valueOf(f));
                    if (this.radialProgresses.get(i) != null) {
                        this.radialProgresses.get(i).setProgress(f, true);
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

    public void setCreateThumbFromParent(boolean z) {
        this.createThumbFromParent = z;
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

        public AvatarImageView(Context context, int i, Paint paint) {
            super(context);
            this.position = i;
            this.placeholderPaint = paint;
            setLayerNum(ProfileGalleryView.this.imagesLayerNum);
        }

        /* access modifiers changed from: protected */
        public void onSizeChanged(int i, int i2, int i3, int i4) {
            super.onSizeChanged(i, i2, i3, i4);
            if (this.radialProgress != null) {
                int currentActionBarHeight = (ProfileGalleryView.this.parentActionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
                int dp2 = AndroidUtilities.dp2(80.0f);
                RadialProgress2 radialProgress2 = this.radialProgress;
                int i5 = this.radialProgressSize;
                int i6 = (i2 - currentActionBarHeight) - dp2;
                radialProgress2.setProgressRect((i - i5) / 2, ((i6 - i5) / 2) + currentActionBarHeight, (i + i5) / 2, currentActionBarHeight + ((i6 + i5) / 2));
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            PinchToZoomHelper pinchToZoomHelper = ProfileGalleryView.this.pinchToZoomHelper;
            if (pinchToZoomHelper == null || !pinchToZoomHelper.isInOverlayMode()) {
                if (this.radialProgress != null) {
                    final int realPosition = ProfileGalleryView.this.getRealPosition(this.position);
                    if (ProfileGalleryView.this.hasActiveVideo) {
                        realPosition--;
                    }
                    Drawable drawable = getImageReceiver().getDrawable();
                    long j = 0;
                    if (!(realPosition >= ProfileGalleryView.this.imagesUploadProgress.size() || ProfileGalleryView.this.imagesUploadProgress.get(realPosition) == null ? !(drawable == null || (this.isVideo && (!(drawable instanceof AnimatedFileDrawable) || ((AnimatedFileDrawable) drawable).getDurationMs() <= 0))) : ((Float) ProfileGalleryView.this.imagesUploadProgress.get(realPosition)).floatValue() >= 1.0f)) {
                        if (this.firstDrawTime < 0) {
                            this.firstDrawTime = System.currentTimeMillis();
                        } else {
                            long currentTimeMillis = System.currentTimeMillis() - this.firstDrawTime;
                            long j2 = this.isVideo ? 250 : 750;
                            if (currentTimeMillis <= 250 + j2 && currentTimeMillis > j2) {
                                this.radialProgress.setOverrideAlpha(CubicBezierInterpolator.DEFAULT.getInterpolation(((float) (currentTimeMillis - j2)) / 250.0f));
                            }
                        }
                        if (ProfileGalleryView.this.invalidateWithParent) {
                            invalidate();
                        } else {
                            postInvalidateOnAnimation();
                        }
                        invalidate();
                    } else if (this.radialProgressHideAnimator == null) {
                        if (this.radialProgress.getProgress() < 1.0f) {
                            this.radialProgress.setProgress(1.0f, true);
                            j = 100;
                        }
                        this.radialProgressHideAnimatorStartValue = this.radialProgress.getOverrideAlpha();
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                        this.radialProgressHideAnimator = ofFloat;
                        ofFloat.setStartDelay(j);
                        this.radialProgressHideAnimator.setDuration((long) (this.radialProgressHideAnimatorStartValue * 250.0f));
                        this.radialProgressHideAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                        this.radialProgressHideAnimator.addUpdateListener(new ProfileGalleryView$AvatarImageView$$ExternalSyntheticLambda0(this));
                        this.radialProgressHideAnimator.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                RadialProgress2 unused = AvatarImageView.this.radialProgress = null;
                                ProfileGalleryView.this.radialProgresses.delete(realPosition);
                            }
                        });
                        this.radialProgressHideAnimator.start();
                    }
                    if (ProfileGalleryView.this.roundTopRadius == 0 && ProfileGalleryView.this.roundBottomRadius == 0) {
                        canvas.drawRect(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), this.placeholderPaint);
                    } else if (ProfileGalleryView.this.roundTopRadius == ProfileGalleryView.this.roundBottomRadius) {
                        ProfileGalleryView.this.rect.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
                        ProfileGalleryView profileGalleryView = ProfileGalleryView.this;
                        canvas.drawRoundRect(profileGalleryView.rect, (float) profileGalleryView.roundTopRadius, (float) ProfileGalleryView.this.roundTopRadius, this.placeholderPaint);
                    } else {
                        ProfileGalleryView.this.path.reset();
                        ProfileGalleryView.this.rect.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
                        for (int i = 0; i < 4; i++) {
                            ProfileGalleryView profileGalleryView2 = ProfileGalleryView.this;
                            profileGalleryView2.radii[i] = (float) profileGalleryView2.roundTopRadius;
                            ProfileGalleryView profileGalleryView3 = ProfileGalleryView.this;
                            profileGalleryView3.radii[i + 4] = (float) profileGalleryView3.roundBottomRadius;
                        }
                        ProfileGalleryView profileGalleryView4 = ProfileGalleryView.this;
                        profileGalleryView4.path.addRoundRect(profileGalleryView4.rect, profileGalleryView4.radii, Path.Direction.CW);
                        canvas.drawPath(ProfileGalleryView.this.path, this.placeholderPaint);
                    }
                }
                super.onDraw(canvas);
                RadialProgress2 radialProgress2 = this.radialProgress;
                if (radialProgress2 != null && radialProgress2.getOverrideAlpha() > 0.0f) {
                    this.radialProgress.draw(canvas);
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onDraw$0(ValueAnimator valueAnimator) {
            this.radialProgress.setOverrideAlpha(AndroidUtilities.lerp(this.radialProgressHideAnimatorStartValue, 0.0f, valueAnimator.getAnimatedFraction()));
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

    public void setInvalidateWithParent(boolean z) {
        this.invalidateWithParent = z;
    }

    private class TextureStubView extends View {
        public TextureStubView(ProfileGalleryView profileGalleryView, Context context) {
            super(context);
        }
    }
}
