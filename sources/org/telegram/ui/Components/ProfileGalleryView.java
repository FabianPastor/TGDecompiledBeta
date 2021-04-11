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
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated;
import org.telegram.tgnet.TLRPC$VideoSize;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.Components.CircularViewPager;
import org.telegram.ui.Components.ProfileGalleryView;
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
    private long dialogId;
    private final PointF downPoint = new PointF();
    private boolean forceResetPosition;
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

    private static class Item {
        /* access modifiers changed from: private */
        public AvatarImageView imageView;

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
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getContext(), (ProfileActivity.AvatarImageView) null, actionBar);
        this.adapter = viewPagerAdapter;
        setAdapter((CircularViewPager.Adapter) viewPagerAdapter);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.dialogPhotosLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileLoadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.reloadDialogPhotos);
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
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileLoadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.reloadDialogPhotos);
        MessagesController.getInstance(this.currentAccount).loadDialogPhotos((int) j, 80, 0, true, i);
    }

    public void onDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogPhotosLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileLoadProgressChanged);
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
            PinchToZoomHelper pinchToZoomHelper2 = this.pinchToZoomHelper;
            if (pinchToZoomHelper2 != null) {
                if (action != 0 && this.isDownReleased && !pinchToZoomHelper2.isInOverlayMode()) {
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
        if (!this.photos.isEmpty() && this.photos.get(0) == null && this.chatInfo != null && FileLoader.isSamePhoto(this.imagesLocations.get(0).location, this.chatInfo.chat_photo)) {
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
                MessagesController.getInstance(this.currentAccount).loadDialogPhotos((int) this.dialogId, 80, 0, true, this.parentClassGuid);
                return true;
            }
            MessagesController.getInstance(this.currentAccount).loadDialogPhotos((int) this.dialogId, 80, 0, true, this.parentClassGuid);
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
        int i = 0;
        while (true) {
            if (i >= this.imagesLocations.size()) {
                break;
            } else if (this.imagesLocations.get(i) == imageLocation) {
                this.thumbsFileNames.remove(i);
                this.videoFileNames.remove(i);
                this.imagesLocations.remove(i);
                this.thumbsLocations.remove(i);
                this.videoLocations.remove(i);
                this.photos.remove(i);
                this.imagesLocationsSizes.remove(i);
                this.imagesUploadProgress.remove(i);
                this.adapter.notifyDataSetChanged();
                break;
            } else {
                i++;
            }
        }
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
        if (this.videoLocations.get(getRealPosition()) == null || (currentItemView = getCurrentItemView()) == null) {
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
        return this.videoLocations.get(getRealPosition()) != null;
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
        return this.photos.size();
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

    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0182, code lost:
        if (r3 != false) goto L_0x0205;
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
            if (r1 != r2) goto L_0x0260
            r1 = 3
            r1 = r19[r1]
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            r2 = r19[r5]
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r7 = r2.intValue()
            long r8 = (long) r7
            long r10 = r0.dialogId
            int r2 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r2 != 0) goto L_0x030d
            int r2 = r0.parentClassGuid
            if (r2 != r1) goto L_0x030d
            org.telegram.ui.Components.ProfileGalleryView$ViewPagerAdapter r1 = r0.adapter
            if (r1 == 0) goto L_0x030d
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
            r3 = 0
            if (r7 >= 0) goto L_0x00fb
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            int r8 = -r7
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r8)
            org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForUserOrChat(r6, r5)
            if (r8 == 0) goto L_0x00fc
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r9 = r0.imagesLocations
            r9.add(r8)
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r9 = r0.thumbsLocations
            org.telegram.messenger.ImageLocation r6 = org.telegram.messenger.ImageLocation.getForUserOrChat(r6, r4)
            r9.add(r6)
            java.util.ArrayList<java.lang.String> r6 = r0.thumbsFileNames
            r6.add(r3)
            org.telegram.tgnet.TLRPC$ChatFull r6 = r0.chatInfo
            if (r6 == 0) goto L_0x00dc
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r9 = r8.location
            org.telegram.tgnet.TLRPC$Photo r6 = r6.chat_photo
            boolean r6 = org.telegram.messenger.FileLoader.isSamePhoto(r9, r6)
            if (r6 == 0) goto L_0x00dc
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r6 = r0.photos
            org.telegram.tgnet.TLRPC$ChatFull r9 = r0.chatInfo
            org.telegram.tgnet.TLRPC$Photo r9 = r9.chat_photo
            r6.add(r9)
            org.telegram.tgnet.TLRPC$ChatFull r6 = r0.chatInfo
            org.telegram.tgnet.TLRPC$Photo r6 = r6.chat_photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r6 = r6.video_sizes
            boolean r6 = r6.isEmpty()
            if (r6 != 0) goto L_0x00d1
            org.telegram.tgnet.TLRPC$ChatFull r6 = r0.chatInfo
            org.telegram.tgnet.TLRPC$Photo r6 = r6.chat_photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r6 = r6.video_sizes
            java.lang.Object r6 = r6.get(r5)
            org.telegram.tgnet.TLRPC$VideoSize r6 = (org.telegram.tgnet.TLRPC$VideoSize) r6
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r9 = r0.videoLocations
            org.telegram.tgnet.TLRPC$ChatFull r10 = r0.chatInfo
            org.telegram.tgnet.TLRPC$Photo r10 = r10.chat_photo
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$VideoSize) r6, (org.telegram.tgnet.TLRPC$Photo) r10)
            r9.add(r10)
            java.util.ArrayList<java.lang.String> r9 = r0.videoFileNames
            java.lang.String r6 = org.telegram.messenger.FileLoader.getAttachFileName(r6)
            r9.add(r6)
            goto L_0x00eb
        L_0x00d1:
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r6 = r0.videoLocations
            r6.add(r3)
            java.util.ArrayList<java.lang.String> r6 = r0.videoFileNames
            r6.add(r3)
            goto L_0x00eb
        L_0x00dc:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r6 = r0.photos
            r6.add(r3)
            java.util.ArrayList<java.lang.String> r6 = r0.videoFileNames
            r6.add(r3)
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r6 = r0.videoLocations
            r6.add(r3)
        L_0x00eb:
            java.util.ArrayList<java.lang.Integer> r6 = r0.imagesLocationsSizes
            r9 = -1
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r6.add(r9)
            java.util.ArrayList<java.lang.Float> r6 = r0.imagesUploadProgress
            r6.add(r3)
            goto L_0x00fc
        L_0x00fb:
            r8 = r3
        L_0x00fc:
            r6 = 0
        L_0x00fd:
            int r9 = r2.size()
            if (r6 >= r9) goto L_0x020e
            java.lang.Object r9 = r2.get(r6)
            org.telegram.tgnet.TLRPC$Photo r9 = (org.telegram.tgnet.TLRPC$Photo) r9
            if (r9 == 0) goto L_0x0207
            boolean r10 = r9 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r10 != 0) goto L_0x0207
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r10 = r9.sizes
            if (r10 != 0) goto L_0x0115
            goto L_0x0207
        L_0x0115:
            r11 = 50
            org.telegram.tgnet.TLRPC$PhotoSize r10 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r10, r11)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r11 = r9.sizes
            int r11 = r11.size()
            r12 = 0
        L_0x0122:
            if (r12 >= r11) goto L_0x0135
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r13 = r9.sizes
            java.lang.Object r13 = r13.get(r12)
            org.telegram.tgnet.TLRPC$PhotoSize r13 = (org.telegram.tgnet.TLRPC$PhotoSize) r13
            boolean r14 = r13 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
            if (r14 == 0) goto L_0x0132
            r10 = r13
            goto L_0x0135
        L_0x0132:
            int r12 = r12 + 1
            goto L_0x0122
        L_0x0135:
            if (r8 == 0) goto L_0x0186
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r11 = r9.sizes
            int r11 = r11.size()
            r12 = 0
        L_0x013e:
            if (r12 >= r11) goto L_0x0181
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r13 = r9.sizes
            java.lang.Object r13 = r13.get(r12)
            org.telegram.tgnet.TLRPC$PhotoSize r13 = (org.telegram.tgnet.TLRPC$PhotoSize) r13
            org.telegram.tgnet.TLRPC$FileLocation r13 = r13.location
            if (r13 == 0) goto L_0x017c
            int r14 = r13.local_id
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r15 = r8.location
            int r4 = r15.local_id
            if (r14 != r4) goto L_0x017c
            long r13 = r13.volume_id
            long r3 = r15.volume_id
            int r15 = (r13 > r3 ? 1 : (r13 == r3 ? 0 : -1))
            if (r15 != 0) goto L_0x017c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r3 = r0.photos
            r3.set(r5, r9)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r3 = r9.video_sizes
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x017a
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r3 = r0.videoLocations
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r4 = r9.video_sizes
            java.lang.Object r4 = r4.get(r5)
            org.telegram.tgnet.TLRPC$VideoSize r4 = (org.telegram.tgnet.TLRPC$VideoSize) r4
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$VideoSize) r4, (org.telegram.tgnet.TLRPC$Photo) r9)
            r3.set(r5, r4)
        L_0x017a:
            r3 = 1
            goto L_0x0182
        L_0x017c:
            int r12 = r12 + 1
            r3 = 0
            r4 = 1
            goto L_0x013e
        L_0x0181:
            r3 = 0
        L_0x0182:
            if (r3 == 0) goto L_0x0186
            goto L_0x0205
        L_0x0186:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r9.sizes
            r4 = 640(0x280, float:8.97E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r4)
            if (r3 == 0) goto L_0x0205
            int r4 = r9.dc_id
            if (r4 == 0) goto L_0x019c
            org.telegram.tgnet.TLRPC$FileLocation r11 = r3.location
            r11.dc_id = r4
            byte[] r4 = r9.file_reference
            r11.file_reference = r4
        L_0x019c:
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r3, (org.telegram.tgnet.TLRPC$Photo) r9)
            if (r4 == 0) goto L_0x0205
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r11 = r0.imagesLocations
            r11.add(r4)
            java.util.ArrayList<java.lang.String> r4 = r0.thumbsFileNames
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
            if (r11 == 0) goto L_0x01af
            r11 = r3
            goto L_0x01b0
        L_0x01af:
            r11 = r10
        L_0x01b0:
            java.lang.String r11 = org.telegram.messenger.FileLoader.getAttachFileName(r11)
            r4.add(r11)
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r4 = r0.thumbsLocations
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r10, (org.telegram.tgnet.TLRPC$Photo) r9)
            r4.add(r10)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r4 = r9.video_sizes
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x01e4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r4 = r9.video_sizes
            java.lang.Object r4 = r4.get(r5)
            org.telegram.tgnet.TLRPC$VideoSize r4 = (org.telegram.tgnet.TLRPC$VideoSize) r4
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r10 = r0.videoLocations
            org.telegram.messenger.ImageLocation r11 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$VideoSize) r4, (org.telegram.tgnet.TLRPC$Photo) r9)
            r10.add(r11)
            java.util.ArrayList<java.lang.String> r10 = r0.videoFileNames
            java.lang.String r4 = org.telegram.messenger.FileLoader.getAttachFileName(r4)
            r10.add(r4)
            r10 = 0
            goto L_0x01ef
        L_0x01e4:
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r4 = r0.videoLocations
            r10 = 0
            r4.add(r10)
            java.util.ArrayList<java.lang.String> r4 = r0.videoFileNames
            r4.add(r10)
        L_0x01ef:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r4 = r0.photos
            r4.add(r9)
            java.util.ArrayList<java.lang.Integer> r4 = r0.imagesLocationsSizes
            int r3 = r3.size
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r4.add(r3)
            java.util.ArrayList<java.lang.Float> r3 = r0.imagesUploadProgress
            r3.add(r10)
            goto L_0x0208
        L_0x0205:
            r10 = 0
            goto L_0x0208
        L_0x0207:
            r10 = r3
        L_0x0208:
            int r6 = r6 + 1
            r3 = r10
            r4 = 1
            goto L_0x00fd
        L_0x020e:
            r16.loadNeighboringThumbs()
            androidx.viewpager.widget.PagerAdapter r2 = r16.getAdapter()
            r2.notifyDataSetChanged()
            boolean r2 = r0.isProfileFragment
            if (r2 == 0) goto L_0x0228
            boolean r2 = r0.scrolledByUser
            if (r2 == 0) goto L_0x0224
            boolean r2 = r0.forceResetPosition
            if (r2 == 0) goto L_0x023a
        L_0x0224:
            r16.resetCurrentItem()
            goto L_0x023a
        L_0x0228:
            boolean r2 = r0.scrolledByUser
            if (r2 == 0) goto L_0x0230
            boolean r2 = r0.forceResetPosition
            if (r2 == 0) goto L_0x023a
        L_0x0230:
            r16.resetCurrentItem()
            androidx.viewpager.widget.PagerAdapter r2 = r16.getAdapter()
            r2.notifyDataSetChanged()
        L_0x023a:
            r0.forceResetPosition = r5
            if (r1 == 0) goto L_0x024e
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r1)
            r8 = 80
            r9 = 0
            r11 = 0
            int r12 = r0.parentClassGuid
            r6.loadDialogPhotos(r7, r8, r9, r11, r12)
        L_0x024e:
            org.telegram.ui.Components.ProfileGalleryView$Callback r1 = r0.callback
            if (r1 == 0) goto L_0x0255
            r1.onPhotosLoaded()
        L_0x0255:
            org.telegram.messenger.ImageLocation r1 = r0.currentUploadingImageLocation
            if (r1 == 0) goto L_0x030d
            org.telegram.messenger.ImageLocation r2 = r0.curreantUploadingThumbLocation
            r0.addUploadingImage(r1, r2)
            goto L_0x030d
        L_0x0260:
            int r2 = org.telegram.messenger.NotificationCenter.fileDidLoad
            r4 = 1065353216(0x3var_, float:1.0)
            if (r1 != r2) goto L_0x029d
            r1 = r19[r5]
            java.lang.String r1 = (java.lang.String) r1
        L_0x026a:
            java.util.ArrayList<java.lang.String> r2 = r0.thumbsFileNames
            int r2 = r2.size()
            if (r5 >= r2) goto L_0x030d
            java.util.ArrayList<java.lang.String> r2 = r0.videoFileNames
            java.lang.Object r2 = r2.get(r5)
            java.lang.String r2 = (java.lang.String) r2
            if (r2 != 0) goto L_0x0284
            java.util.ArrayList<java.lang.String> r2 = r0.thumbsFileNames
            java.lang.Object r2 = r2.get(r5)
            java.lang.String r2 = (java.lang.String) r2
        L_0x0284:
            if (r2 == 0) goto L_0x029a
            boolean r2 = android.text.TextUtils.equals(r1, r2)
            if (r2 == 0) goto L_0x029a
            android.util.SparseArray<org.telegram.ui.Components.RadialProgress2> r2 = r0.radialProgresses
            java.lang.Object r2 = r2.get(r5)
            org.telegram.ui.Components.RadialProgress2 r2 = (org.telegram.ui.Components.RadialProgress2) r2
            if (r2 == 0) goto L_0x029a
            r3 = 1
            r2.setProgress(r4, r3)
        L_0x029a:
            int r5 = r5 + 1
            goto L_0x026a
        L_0x029d:
            int r2 = org.telegram.messenger.NotificationCenter.FileLoadProgressChanged
            if (r1 != r2) goto L_0x02f1
            r1 = r19[r5]
            java.lang.String r1 = (java.lang.String) r1
        L_0x02a5:
            java.util.ArrayList<java.lang.String> r2 = r0.thumbsFileNames
            int r2 = r2.size()
            if (r5 >= r2) goto L_0x030d
            java.util.ArrayList<java.lang.String> r2 = r0.videoFileNames
            java.lang.Object r2 = r2.get(r5)
            java.lang.String r2 = (java.lang.String) r2
            if (r2 != 0) goto L_0x02bf
            java.util.ArrayList<java.lang.String> r2 = r0.thumbsFileNames
            java.lang.Object r2 = r2.get(r5)
            java.lang.String r2 = (java.lang.String) r2
        L_0x02bf:
            if (r2 == 0) goto L_0x02ed
            boolean r2 = android.text.TextUtils.equals(r1, r2)
            if (r2 == 0) goto L_0x02ed
            android.util.SparseArray<org.telegram.ui.Components.RadialProgress2> r2 = r0.radialProgresses
            java.lang.Object r2 = r2.get(r5)
            org.telegram.ui.Components.RadialProgress2 r2 = (org.telegram.ui.Components.RadialProgress2) r2
            if (r2 == 0) goto L_0x02ed
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
            goto L_0x02ee
        L_0x02ed:
            r6 = 1
        L_0x02ee:
            int r5 = r5 + 1
            goto L_0x02a5
        L_0x02f1:
            int r2 = org.telegram.messenger.NotificationCenter.reloadDialogPhotos
            if (r1 != r2) goto L_0x030d
            int r1 = r0.settingMainPhoto
            if (r1 == 0) goto L_0x02fa
            return
        L_0x02fa:
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r1)
            long r3 = r0.dialogId
            int r3 = (int) r3
            r4 = 80
            r5 = 0
            r7 = 1
            int r8 = r0.parentClassGuid
            r2.loadDialogPhotos(r3, r4, r5, r7, r8)
        L_0x030d:
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
            return view == ((Item) obj).imageView;
        }

        public int getItemPosition(Object obj) {
            int indexOf = this.objects.indexOf((Item) obj);
            if (indexOf == -1) {
                return -2;
            }
            return indexOf;
        }

        /* JADX WARNING: Removed duplicated region for block: B:56:0x0210  */
        /* JADX WARNING: Removed duplicated region for block: B:58:0x0213  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public org.telegram.ui.Components.ProfileGalleryView.Item instantiateItem(android.view.ViewGroup r23, int r24) {
            /*
                r22 = this;
                r0 = r22
                r1 = r24
                java.util.ArrayList<org.telegram.ui.Components.ProfileGalleryView$Item> r2 = r0.objects
                java.lang.Object r2 = r2.get(r1)
                org.telegram.ui.Components.ProfileGalleryView$Item r2 = (org.telegram.ui.Components.ProfileGalleryView.Item) r2
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r3 = r2.imageView
                if (r3 != 0) goto L_0x0029
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r3 = new org.telegram.ui.Components.ProfileGalleryView$AvatarImageView
                org.telegram.ui.Components.ProfileGalleryView r4 = org.telegram.ui.Components.ProfileGalleryView.this
                android.content.Context r5 = r0.context
                android.graphics.Paint r6 = r0.placeholderPaint
                r3.<init>(r5, r1, r6)
                org.telegram.ui.Components.ProfileGalleryView.AvatarImageView unused = r2.imageView = r3
                java.util.ArrayList<org.telegram.ui.Components.BackupImageView> r3 = r0.imageViews
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r4 = r2.imageView
                r3.set(r1, r4)
            L_0x0029:
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r3 = r2.imageView
                android.view.ViewParent r3 = r3.getParent()
                if (r3 != 0) goto L_0x003c
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r3 = r2.imageView
                r4 = r23
                r4.addView(r3)
            L_0x003c:
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r3 = r2.imageView
                org.telegram.messenger.ImageReceiver r3 = r3.getImageReceiver()
                r4 = 1
                r3.setAllowDecodeSingleFrame(r4)
                int r1 = r0.getRealPosition(r1)
                r3 = 2
                java.lang.String r5 = "b"
                r6 = 0
                r7 = 0
                if (r1 != 0) goto L_0x019c
                org.telegram.ui.Components.BackupImageView r8 = r0.parentAvatarImageView
                if (r8 != 0) goto L_0x0059
                r8 = r6
                goto L_0x0061
            L_0x0059:
                org.telegram.messenger.ImageReceiver r8 = r8.getImageReceiver()
                android.graphics.drawable.Drawable r8 = r8.getDrawable()
            L_0x0061:
                boolean r9 = r8 instanceof org.telegram.ui.Components.AnimatedFileDrawable
                if (r9 == 0) goto L_0x0082
                r9 = r8
                org.telegram.ui.Components.AnimatedFileDrawable r9 = (org.telegram.ui.Components.AnimatedFileDrawable) r9
                boolean r10 = r9.hasBitmap()
                if (r10 == 0) goto L_0x0082
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r5 = r2.imageView
                r5.setImageDrawable(r8)
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r5 = r2.imageView
                r9.addSecondParentView(r5)
                r9.setInvalidateParentViewWithSecond(r4)
                r5 = 0
                goto L_0x0203
            L_0x0082:
                org.telegram.ui.Components.ProfileGalleryView r8 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r8 = r8.videoLocations
                java.lang.Object r8 = r8.get(r1)
                r10 = r8
                org.telegram.messenger.ImageLocation r10 = (org.telegram.messenger.ImageLocation) r10
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r8 = r2.imageView
                if (r10 == 0) goto L_0x0097
                r9 = 1
                goto L_0x0098
            L_0x0097:
                r9 = 0
            L_0x0098:
                r8.isVideo = r9
                org.telegram.ui.Components.ProfileGalleryView r8 = org.telegram.ui.Components.ProfileGalleryView.this
                boolean r8 = r8.isProfileFragment
                if (r8 == 0) goto L_0x00ac
                if (r10 == 0) goto L_0x00ac
                int r8 = r10.imageType
                if (r8 != r3) goto L_0x00ac
                java.lang.String r8 = "g"
                r13 = r8
                goto L_0x00ad
            L_0x00ac:
                r13 = r6
            L_0x00ad:
                org.telegram.ui.Components.ProfileGalleryView r8 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r8 = r8.thumbsLocations
                java.lang.Object r8 = r8.get(r1)
                org.telegram.messenger.ImageLocation r8 = (org.telegram.messenger.ImageLocation) r8
                org.telegram.ui.Components.BackupImageView r9 = r0.parentAvatarImageView
                if (r9 == 0) goto L_0x00d3
                org.telegram.ui.Components.ProfileGalleryView r9 = org.telegram.ui.Components.ProfileGalleryView.this
                boolean r9 = r9.createThumbFromParent
                if (r9 != 0) goto L_0x00c6
                goto L_0x00d3
            L_0x00c6:
                org.telegram.ui.Components.BackupImageView r9 = r0.parentAvatarImageView
                org.telegram.messenger.ImageReceiver r9 = r9.getImageReceiver()
                android.graphics.Bitmap r9 = r9.getBitmap()
                r16 = r9
                goto L_0x00d5
            L_0x00d3:
                r16 = r6
            L_0x00d5:
                if (r16 == 0) goto L_0x010f
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r11 = r2.imageView
                org.telegram.ui.Components.ProfileGalleryView r5 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r5 = r5.videoLocations
                java.lang.Object r5 = r5.get(r1)
                r12 = r5
                org.telegram.messenger.ImageLocation r12 = (org.telegram.messenger.ImageLocation) r12
                org.telegram.ui.Components.ProfileGalleryView r5 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r5 = r5.imagesLocations
                java.lang.Object r5 = r5.get(r1)
                r14 = r5
                org.telegram.messenger.ImageLocation r14 = (org.telegram.messenger.ImageLocation) r14
                r15 = 0
                org.telegram.ui.Components.ProfileGalleryView r5 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r5 = r5.imagesLocationsSizes
                java.lang.Object r5 = r5.get(r1)
                java.lang.Integer r5 = (java.lang.Integer) r5
                int r17 = r5.intValue()
                r18 = 1
                r19 = r8
                r11.setImageMedia(r12, r13, r14, r15, r16, r17, r18, r19)
                goto L_0x0202
            L_0x010f:
                org.telegram.ui.Components.ProfileGalleryView r9 = org.telegram.ui.Components.ProfileGalleryView.this
                org.telegram.messenger.ImageLocation r9 = r9.uploadingImageLocation
                if (r9 == 0) goto L_0x0159
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r11 = r2.imageView
                org.telegram.ui.Components.ProfileGalleryView r5 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r5 = r5.videoLocations
                java.lang.Object r5 = r5.get(r1)
                r12 = r5
                org.telegram.messenger.ImageLocation r12 = (org.telegram.messenger.ImageLocation) r12
                org.telegram.ui.Components.ProfileGalleryView r5 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r5 = r5.imagesLocations
                java.lang.Object r5 = r5.get(r1)
                r14 = r5
                org.telegram.messenger.ImageLocation r14 = (org.telegram.messenger.ImageLocation) r14
                r15 = 0
                org.telegram.ui.Components.ProfileGalleryView r5 = org.telegram.ui.Components.ProfileGalleryView.this
                org.telegram.messenger.ImageLocation r16 = r5.uploadingImageLocation
                r17 = 0
                r18 = 0
                org.telegram.ui.Components.ProfileGalleryView r5 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r5 = r5.imagesLocationsSizes
                java.lang.Object r5 = r5.get(r1)
                java.lang.Integer r5 = (java.lang.Integer) r5
                int r19 = r5.intValue()
                r20 = 1
                r21 = r8
                r11.setImageMedia(r12, r13, r14, r15, r16, r17, r18, r19, r20, r21)
                goto L_0x0202
            L_0x0159:
                org.telegram.tgnet.TLRPC$PhotoSize r9 = r8.photoSize
                boolean r9 = r9 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
                if (r9 == 0) goto L_0x0161
                r15 = r5
                goto L_0x0162
            L_0x0161:
                r15 = r6
            L_0x0162:
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r9 = r2.imageView
                r11 = 0
                org.telegram.ui.Components.ProfileGalleryView r5 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r5 = r5.imagesLocations
                java.lang.Object r5 = r5.get(r1)
                r12 = r5
                org.telegram.messenger.ImageLocation r12 = (org.telegram.messenger.ImageLocation) r12
                r13 = 0
                org.telegram.ui.Components.ProfileGalleryView r5 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r5 = r5.thumbsLocations
                java.lang.Object r5 = r5.get(r1)
                r14 = r5
                org.telegram.messenger.ImageLocation r14 = (org.telegram.messenger.ImageLocation) r14
                r16 = 0
                org.telegram.ui.Components.ProfileGalleryView r5 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r5 = r5.imagesLocationsSizes
                java.lang.Object r5 = r5.get(r1)
                java.lang.Integer r5 = (java.lang.Integer) r5
                int r17 = r5.intValue()
                r18 = 1
                r19 = r8
                r9.setImageMedia(r10, r11, r12, r13, r14, r15, r16, r17, r18, r19)
                goto L_0x0202
            L_0x019c:
                org.telegram.ui.Components.ProfileGalleryView r8 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r8 = r8.videoLocations
                java.lang.Object r8 = r8.get(r1)
                r10 = r8
                org.telegram.messenger.ImageLocation r10 = (org.telegram.messenger.ImageLocation) r10
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r8 = r2.imageView
                if (r10 == 0) goto L_0x01b1
                r9 = 1
                goto L_0x01b2
            L_0x01b1:
                r9 = 0
            L_0x01b2:
                r8.isVideo = r9
                org.telegram.ui.Components.ProfileGalleryView r8 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r8 = r8.thumbsLocations
                java.lang.Object r8 = r8.get(r1)
                org.telegram.messenger.ImageLocation r8 = (org.telegram.messenger.ImageLocation) r8
                org.telegram.tgnet.TLRPC$PhotoSize r9 = r8.photoSize
                boolean r9 = r9 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
                if (r9 == 0) goto L_0x01c8
                r15 = r5
                goto L_0x01c9
            L_0x01c8:
                r15 = r6
            L_0x01c9:
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r9 = r2.imageView
                r11 = 0
                org.telegram.ui.Components.ProfileGalleryView r5 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r5 = r5.imagesLocations
                java.lang.Object r5 = r5.get(r1)
                r12 = r5
                org.telegram.messenger.ImageLocation r12 = (org.telegram.messenger.ImageLocation) r12
                r13 = 0
                org.telegram.ui.Components.ProfileGalleryView r5 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r5 = r5.thumbsLocations
                java.lang.Object r5 = r5.get(r1)
                r14 = r5
                org.telegram.messenger.ImageLocation r14 = (org.telegram.messenger.ImageLocation) r14
                r16 = 0
                org.telegram.ui.Components.ProfileGalleryView r5 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r5 = r5.imagesLocationsSizes
                java.lang.Object r5 = r5.get(r1)
                java.lang.Integer r5 = (java.lang.Integer) r5
                int r17 = r5.intValue()
                r18 = 1
                r19 = r8
                r9.setImageMedia(r10, r11, r12, r13, r14, r15, r16, r17, r18, r19)
            L_0x0202:
                r5 = 1
            L_0x0203:
                org.telegram.ui.Components.ProfileGalleryView r6 = org.telegram.ui.Components.ProfileGalleryView.this
                java.util.ArrayList r6 = r6.imagesUploadProgress
                java.lang.Object r6 = r6.get(r1)
                if (r6 == 0) goto L_0x0210
                goto L_0x0211
            L_0x0210:
                r4 = r5
            L_0x0211:
                if (r4 == 0) goto L_0x028b
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r4 = r2.imageView
                org.telegram.ui.Components.ProfileGalleryView r5 = org.telegram.ui.Components.ProfileGalleryView.this
                android.util.SparseArray r5 = r5.radialProgresses
                java.lang.Object r5 = r5.get(r1)
                org.telegram.ui.Components.RadialProgress2 r5 = (org.telegram.ui.Components.RadialProgress2) r5
                org.telegram.ui.Components.RadialProgress2 unused = r4.radialProgress = r5
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r4 = r2.imageView
                org.telegram.ui.Components.RadialProgress2 r4 = r4.radialProgress
                if (r4 != 0) goto L_0x0278
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r4 = r2.imageView
                org.telegram.ui.Components.RadialProgress2 r5 = new org.telegram.ui.Components.RadialProgress2
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r6 = r2.imageView
                r5.<init>(r6)
                org.telegram.ui.Components.RadialProgress2 unused = r4.radialProgress = r5
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r4 = r2.imageView
                org.telegram.ui.Components.RadialProgress2 r4 = r4.radialProgress
                r5 = 0
                r4.setOverrideAlpha(r5)
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r4 = r2.imageView
                org.telegram.ui.Components.RadialProgress2 r4 = r4.radialProgress
                r5 = 10
                r4.setIcon(r5, r7, r7)
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r4 = r2.imageView
                org.telegram.ui.Components.RadialProgress2 r4 = r4.radialProgress
                r5 = 1107296256(0x42000000, float:32.0)
                r6 = -1
                r4.setColors((int) r5, (int) r5, (int) r6, (int) r6)
                org.telegram.ui.Components.ProfileGalleryView r4 = org.telegram.ui.Components.ProfileGalleryView.this
                android.util.SparseArray r4 = r4.radialProgresses
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r5 = r2.imageView
                org.telegram.ui.Components.RadialProgress2 r5 = r5.radialProgress
                r4.append(r1, r5)
            L_0x0278:
                org.telegram.ui.Components.ProfileGalleryView r1 = org.telegram.ui.Components.ProfileGalleryView.this
                boolean r1 = r1.invalidateWithParent
                if (r1 == 0) goto L_0x0286
                org.telegram.ui.Components.ProfileGalleryView r1 = org.telegram.ui.Components.ProfileGalleryView.this
                r1.invalidate()
                goto L_0x028b
            L_0x0286:
                org.telegram.ui.Components.ProfileGalleryView r1 = org.telegram.ui.Components.ProfileGalleryView.this
                r1.postInvalidateOnAnimation()
            L_0x028b:
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r1 = r2.imageView
                org.telegram.messenger.ImageReceiver r1 = r1.getImageReceiver()
                org.telegram.ui.Components.ProfileGalleryView$ViewPagerAdapter$1 r4 = new org.telegram.ui.Components.ProfileGalleryView$ViewPagerAdapter$1
                r4.<init>()
                r1.setDelegate(r4)
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r1 = r2.imageView
                org.telegram.messenger.ImageReceiver r1 = r1.getImageReceiver()
                r1.setCrossfadeAlpha(r3)
                org.telegram.ui.Components.ProfileGalleryView$AvatarImageView r1 = r2.imageView
                org.telegram.ui.Components.ProfileGalleryView r3 = org.telegram.ui.Components.ProfileGalleryView.this
                int r3 = r3.roundTopRadius
                org.telegram.ui.Components.ProfileGalleryView r4 = org.telegram.ui.Components.ProfileGalleryView.this
                int r4 = r4.roundTopRadius
                org.telegram.ui.Components.ProfileGalleryView r5 = org.telegram.ui.Components.ProfileGalleryView.this
                int r5 = r5.roundBottomRadius
                org.telegram.ui.Components.ProfileGalleryView r6 = org.telegram.ui.Components.ProfileGalleryView.this
                int r6 = r6.roundBottomRadius
                r1.setRoundRadius(r3, r4, r5, r6)
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ProfileGalleryView.ViewPagerAdapter.instantiateItem(android.view.ViewGroup, int):org.telegram.ui.Components.ProfileGalleryView$Item");
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            AvatarImageView access$500 = ((Item) obj).imageView;
            if (access$500.getImageReceiver().hasStaticThumb()) {
                Drawable drawable = access$500.getImageReceiver().getDrawable();
                if (drawable instanceof AnimatedFileDrawable) {
                    ((AnimatedFileDrawable) drawable).removeSecondParentView(access$500);
                }
            }
            access$500.setRoundRadius(0);
            viewGroup.removeView(access$500);
            access$500.getImageReceiver().cancelLoadImage();
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
            int size = ProfileGalleryView.this.imagesLocations.size() + (getExtraCount() * 2);
            for (int i2 = 0; i2 < size; i2++) {
                this.objects.add(new Item());
                this.imageViews.add((Object) null);
            }
            super.notifyDataSetChanged();
        }

        public int getExtraCount() {
            if (ProfileGalleryView.this.imagesLocations.size() >= 2) {
                return ProfileGalleryView.this.getOffscreenPageLimit();
            }
            return 0;
        }
    }

    public void setData(long j) {
        if (this.dialogId == j) {
            resetCurrentItem();
            return;
        }
        this.forceResetPosition = true;
        this.adapter.notifyDataSetChanged();
        reset();
        this.dialogId = j;
        MessagesController.getInstance(this.currentAccount).loadDialogPhotos((int) j, 80, 0, true, this.parentClassGuid);
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
                    AvatarImageView access$500 = ((Item) this.adapter.objects.get(i3)).imageView;
                    int i4 = this.roundTopRadius;
                    int i5 = this.roundBottomRadius;
                    access$500.setRoundRadius(i4, i4, i5, i5);
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
        /* access modifiers changed from: private */
        public final int position;
        /* access modifiers changed from: private */
        public RadialProgress2 radialProgress;
        private ValueAnimator radialProgressHideAnimator;
        private float radialProgressHideAnimatorStartValue;
        private final int radialProgressSize = AndroidUtilities.dp(64.0f);

        public AvatarImageView(Context context, int i, Paint paint) {
            super(context);
            this.position = i;
            this.placeholderPaint = paint;
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
                    int realPosition = ProfileGalleryView.this.getRealPosition(this.position);
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
                        this.radialProgressHideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                ProfileGalleryView.AvatarImageView.this.lambda$onDraw$0$ProfileGalleryView$AvatarImageView(valueAnimator);
                            }
                        });
                        this.radialProgressHideAnimator.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                RadialProgress2 unused = AvatarImageView.this.radialProgress = null;
                                SparseArray access$1400 = ProfileGalleryView.this.radialProgresses;
                                AvatarImageView avatarImageView = AvatarImageView.this;
                                access$1400.delete(ProfileGalleryView.this.getRealPosition(avatarImageView.position));
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
        /* renamed from: lambda$onDraw$0 */
        public /* synthetic */ void lambda$onDraw$0$ProfileGalleryView$AvatarImageView(ValueAnimator valueAnimator) {
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
}
