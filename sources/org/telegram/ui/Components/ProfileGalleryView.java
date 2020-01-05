package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_fileLocationToBeDeprecated;
import org.telegram.tgnet.TLRPC.TL_photoEmpty;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.CircularViewPager;
import org.telegram.ui.CircularViewPager.Adapter;
import org.telegram.ui.ProfileActivity.AvatarImageView;

public class ProfileGalleryView extends CircularViewPager implements NotificationCenterDelegate {
    private final ViewPagerAdapter adapter;
    private int currentAccount = UserConfig.selectedAccount;
    private final long dialogId;
    private final PointF downPoint = new PointF();
    private final GestureDetector gestureDetector;
    private ArrayList<ImageLocation> imagesLocations = new ArrayList();
    private ArrayList<Integer> imagesLocationsSizes = new ArrayList();
    private boolean isScrollingListView = true;
    private boolean isSwipingViewPager = true;
    private final int parentClassGuid;
    private final RecyclerListView parentListView;
    private ImageLocation prevImageLocation;
    private final SparseArray<RadialProgress2> radialProgresses = new SparseArray();
    private ArrayList<String> thumbsFileNames = new ArrayList();
    private ArrayList<ImageLocation> thumbsLocations = new ArrayList();
    private final int touchSlop;

    private class Item {
        private BackupImageView imageView;

        private Item() {
        }

        /* synthetic */ Item(ProfileGalleryView profileGalleryView, AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    public class ViewPagerAdapter extends Adapter {
        private final Context context;
        private final ArrayList<Item> objects = new ArrayList();
        private final ActionBar parentActionBar;
        private final AvatarImageView parentAvatarImageView;
        private final Paint placeholderPaint;

        public ViewPagerAdapter(Context context, AvatarImageView avatarImageView, ActionBar actionBar) {
            this.context = context;
            this.parentAvatarImageView = avatarImageView;
            this.parentActionBar = actionBar;
            this.placeholderPaint = new Paint(1);
            this.placeholderPaint.setColor(-16777216);
        }

        public int getCount() {
            return this.objects.size();
        }

        public boolean isViewFromObject(View view, Object obj) {
            return view == ((Item) obj).imageView;
        }

        public int getItemPosition(Object obj) {
            int indexOf = this.objects.indexOf(obj);
            return indexOf == -1 ? -2 : indexOf;
        }

        public Item instantiateItem(ViewGroup viewGroup, final int i) {
            Item item = (Item) this.objects.get(i);
            if (item.imageView == null) {
                item.imageView = new BackupImageView(this.context) {
                    private long firstDrawTime = -1;
                    private RadialProgress2 radialProgress;
                    private ValueAnimator radialProgressHideAnimator;
                    private float radialProgressHideAnimatorStartValue;
                    private final int radialProgressSize = AndroidUtilities.dp(64.0f);

                    /* Access modifiers changed, original: protected */
                    public void onSizeChanged(int i, int i2, int i3, int i4) {
                        super.onSizeChanged(i, i2, i3, i4);
                        if (this.radialProgress != null) {
                            i3 = (ViewPagerAdapter.this.parentActionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
                            i4 = AndroidUtilities.dp2(80.0f);
                            RadialProgress2 radialProgress2 = this.radialProgress;
                            int i5 = this.radialProgressSize;
                            i2 = (i2 - i3) - i4;
                            radialProgress2.setProgressRect((i - i5) / 2, ((i2 - i5) / 2) + i3, (i + i5) / 2, i3 + ((i2 + i5) / 2));
                        }
                    }

                    /* Access modifiers changed, original: protected */
                    public void onDraw(Canvas canvas) {
                        if (this.radialProgress != null) {
                            if (getImageReceiver().getDrawable() == null) {
                                if (this.firstDrawTime < 0) {
                                    this.firstDrawTime = System.currentTimeMillis();
                                } else {
                                    long currentTimeMillis = System.currentTimeMillis() - this.firstDrawTime;
                                    if (currentTimeMillis <= 1000 && currentTimeMillis > 750) {
                                        this.radialProgress.setOverrideAlpha(CubicBezierInterpolator.DEFAULT.getInterpolation(((float) (currentTimeMillis - 750)) / 250.0f));
                                    }
                                }
                                postInvalidateOnAnimation();
                            } else if (this.radialProgressHideAnimator == null) {
                                this.radialProgressHideAnimatorStartValue = this.radialProgress.getOverrideAlpha();
                                this.radialProgressHideAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                                this.radialProgressHideAnimator.setDuration((long) (this.radialProgressHideAnimatorStartValue * 175.0f));
                                this.radialProgressHideAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                                this.radialProgressHideAnimator.addUpdateListener(new -$$Lambda$ProfileGalleryView$ViewPagerAdapter$1$wvZBxr78pLdwArB7T9Z7FL-LYVM(this));
                                this.radialProgressHideAnimator.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animator) {
                                        AnonymousClass1.this.radialProgress = null;
                                        ProfileGalleryView.this.radialProgresses.delete(i);
                                    }
                                });
                                this.radialProgressHideAnimator.start();
                            }
                            canvas.drawRect(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), ViewPagerAdapter.this.placeholderPaint);
                        }
                        super.onDraw(canvas);
                        RadialProgress2 radialProgress2 = this.radialProgress;
                        if (radialProgress2 != null && radialProgress2.getOverrideAlpha() > 0.0f) {
                            this.radialProgress.draw(canvas);
                        }
                    }

                    public /* synthetic */ void lambda$onDraw$0$ProfileGalleryView$ViewPagerAdapter$1(ValueAnimator valueAnimator) {
                        this.radialProgress.setOverrideAlpha(AndroidUtilities.lerp(this.radialProgressHideAnimatorStartValue, 0.0f, valueAnimator.getAnimatedFraction()));
                    }
                };
            }
            if (item.imageView.getParent() == null) {
                viewGroup.addView(item.imageView);
            }
            return item;
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView(((Item) obj).imageView);
            ProfileGalleryView.this.radialProgresses.delete(getRealPosition(i));
        }

        public CharSequence getPageTitle(int i) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getRealPosition(i) + 1);
            stringBuilder.append("/");
            stringBuilder.append(getCount() - (getExtraCount() * 2));
            return stringBuilder.toString();
        }

        public void notifyDataSetChanged() {
            this.objects.clear();
            int size = ProfileGalleryView.this.imagesLocations.size() + (getExtraCount() * 2);
            for (int i = 0; i < size; i++) {
                this.objects.add(new Item(ProfileGalleryView.this, null));
            }
            super.notifyDataSetChanged();
        }

        public int getExtraCount() {
            return ProfileGalleryView.this.imagesLocations.size() >= 2 ? ProfileGalleryView.this.getOffscreenPageLimit() : 0;
        }
    }

    public ProfileGalleryView(Context context, long j, ActionBar actionBar, RecyclerListView recyclerListView, AvatarImageView avatarImageView, int i) {
        super(context);
        setVisibility(8);
        setOverScrollMode(2);
        setOffscreenPageLimit(2);
        this.dialogId = j;
        this.parentListView = recyclerListView;
        this.parentClassGuid = i;
        Adapter viewPagerAdapter = new ViewPagerAdapter(context, avatarImageView, actionBar);
        this.adapter = viewPagerAdapter;
        setAdapter(viewPagerAdapter);
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.gestureDetector = new GestureDetector(context, new OnGestureListener() {
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                return false;
            }

            public void onLongPress(MotionEvent motionEvent) {
            }

            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                return false;
            }

            public void onShowPress(MotionEvent motionEvent) {
            }

            /* JADX WARNING: Missing block: B:5:0x0031, code skipped:
            if (r1 >= (r0 - r6)) goto L_0x0047;
     */
            public boolean onSingleTapUp(android.view.MotionEvent r6) {
                /*
                r5 = this;
                r0 = org.telegram.ui.Components.ProfileGalleryView.this;
                r0 = r0.adapter;
                r0 = r0.getCount();
                r1 = org.telegram.ui.Components.ProfileGalleryView.this;
                r1 = r1.getCurrentItem();
                r2 = 0;
                r3 = 1;
                if (r0 <= r3) goto L_0x004d;
            L_0x0014:
                r6 = r6.getX();
                r4 = org.telegram.ui.Components.ProfileGalleryView.this;
                r4 = r4.getWidth();
                r4 = r4 / 3;
                r4 = (float) r4;
                r6 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1));
                if (r6 <= 0) goto L_0x0034;
            L_0x0025:
                r6 = org.telegram.ui.Components.ProfileGalleryView.this;
                r6 = r6.adapter;
                r6 = r6.getExtraCount();
                r1 = r1 + r3;
                r0 = r0 - r6;
                if (r1 < r0) goto L_0x0046;
            L_0x0033:
                goto L_0x0047;
            L_0x0034:
                r6 = org.telegram.ui.Components.ProfileGalleryView.this;
                r6 = r6.adapter;
                r6 = r6.getExtraCount();
                r1 = r1 + -1;
                if (r1 >= r6) goto L_0x0046;
            L_0x0042:
                r0 = r0 - r6;
                r6 = r0 + -1;
                goto L_0x0047;
            L_0x0046:
                r6 = r1;
            L_0x0047:
                r0 = org.telegram.ui.Components.ProfileGalleryView.this;
                r0.setCurrentItem(r6, r2);
                return r3;
            L_0x004d:
                return r2;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ProfileGalleryView$AnonymousClass1.onSingleTapUp(android.view.MotionEvent):boolean");
            }
        });
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.dialogPhotosLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileLoadProgressChanged);
        MessagesController.getInstance(this.currentAccount).loadDialogPhotos((int) j, 80, 0, true, i);
    }

    public void onDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogPhotosLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileLoadProgressChanged);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        this.gestureDetector.onTouchEvent(motionEvent);
        if (this.parentListView.getScrollState() == 0 || this.isScrollingListView || !this.isSwipingViewPager) {
            int action = motionEvent.getAction();
            if (action == 0) {
                this.isScrollingListView = true;
                this.isSwipingViewPager = true;
                this.downPoint.set(motionEvent.getX(), motionEvent.getY());
            } else if (action == 2) {
                float x = motionEvent.getX() - this.downPoint.x;
                float y = motionEvent.getY() - this.downPoint.y;
                if (this.isSwipingViewPager && this.isScrollingListView) {
                    if (Math.abs(y) >= ((float) this.touchSlop) || Math.abs(x) >= ((float) this.touchSlop)) {
                        MotionEvent obtain;
                        if (Math.abs(y) > Math.abs(x)) {
                            this.isSwipingViewPager = false;
                            obtain = MotionEvent.obtain(motionEvent);
                            obtain.setAction(3);
                            super.onTouchEvent(obtain);
                            obtain.recycle();
                        } else {
                            this.isScrollingListView = false;
                            obtain = MotionEvent.obtain(motionEvent);
                            obtain.setAction(3);
                            this.parentListView.onTouchEvent(obtain);
                            obtain.recycle();
                        }
                    }
                } else if (this.isSwipingViewPager && !canScrollHorizontally(-1) && x > ((float) this.touchSlop)) {
                    return false;
                }
            }
            boolean onTouchEvent = this.isScrollingListView ? this.parentListView.onTouchEvent(motionEvent) | 0 : false;
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
        motionEvent = MotionEvent.obtain(motionEvent);
        motionEvent.setAction(3);
        super.onTouchEvent(motionEvent);
        motionEvent.recycle();
        return false;
    }

    public void initIfEmpty(ImageLocation imageLocation, ImageLocation imageLocation2) {
        if (!(imageLocation == null || imageLocation2 == null)) {
            ImageLocation imageLocation3 = this.prevImageLocation;
            if (!(imageLocation3 == null || imageLocation3.location.local_id == imageLocation.location.local_id)) {
                this.imagesLocations.clear();
                MessagesController.getInstance(this.currentAccount).loadDialogPhotos((int) this.dialogId, 80, 0, true, this.parentClassGuid);
            }
            if (this.imagesLocations.isEmpty()) {
                this.prevImageLocation = imageLocation;
                this.thumbsFileNames.add("");
                this.imagesLocations.add(imageLocation);
                this.thumbsLocations.add(imageLocation2);
                this.imagesLocationsSizes.add(Integer.valueOf(-1));
                getAdapter().notifyDataSetChanged();
            }
        }
    }

    public ImageLocation getImageLocation(int i) {
        return (i < 0 || i >= this.imagesLocations.size()) ? null : (ImageLocation) this.imagesLocations.get(i);
    }

    public ImageLocation getThumbLocation(int i) {
        return (i < 0 || i >= this.thumbsLocations.size()) ? null : (ImageLocation) this.thumbsLocations.get(i);
    }

    public boolean hasImages() {
        return this.imagesLocations.isEmpty() ^ 1;
    }

    public BackupImageView getCurrentItemView() {
        ViewPagerAdapter viewPagerAdapter = this.adapter;
        return viewPagerAdapter != null ? ((Item) viewPagerAdapter.objects.get(getCurrentItem())).imageView : null;
    }

    public void resetCurrentItem() {
        setCurrentItem(this.adapter.getExtraCount(), false);
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
                    FileLoader.getInstance(this.currentAccount).loadFile((ImageLocation) this.thumbsLocations.get(i == 0 ? 1 : size - 1), null, null, 0, 1);
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3 = i;
        int i4 = 0;
        String str;
        RadialProgress2 radialProgress2;
        if (i3 == NotificationCenter.dialogPhotosLoaded) {
            i3 = ((Integer) objArr[3]).intValue();
            int intValue = ((Integer) objArr[0]).intValue();
            if (((long) intValue) == this.dialogId && this.parentClassGuid == i3) {
                boolean booleanValue = ((Boolean) objArr[2]).booleanValue();
                ArrayList arrayList = (ArrayList) objArr[4];
                this.thumbsFileNames.clear();
                this.imagesLocations.clear();
                this.thumbsLocations.clear();
                this.imagesLocationsSizes.clear();
                ImageLocation imageLocation = null;
                if (intValue < 0) {
                    Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-intValue));
                    ImageLocation forChat = ImageLocation.getForChat(chat, true);
                    if (forChat != null) {
                        this.thumbsFileNames.add("");
                        this.imagesLocations.add(forChat);
                        this.thumbsLocations.add(ImageLocation.getForChat(chat, false));
                        this.imagesLocationsSizes.add(Integer.valueOf(-1));
                    }
                    imageLocation = forChat;
                }
                for (int i5 = 0; i5 < arrayList.size(); i5++) {
                    Photo photo = (Photo) arrayList.get(i5);
                    if (!(photo == null || (photo instanceof TL_photoEmpty))) {
                        ArrayList arrayList2 = photo.sizes;
                        if (arrayList2 != null) {
                            int i6;
                            FileLocation fileLocation;
                            PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList2, 640);
                            PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 50);
                            if (imageLocation != null) {
                                Object obj;
                                for (i6 = 0; i6 < photo.sizes.size(); i6++) {
                                    fileLocation = ((PhotoSize) photo.sizes.get(i6)).location;
                                    int i7 = fileLocation.local_id;
                                    TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated = imageLocation.location;
                                    if (i7 == tL_fileLocationToBeDeprecated.local_id && fileLocation.volume_id == tL_fileLocationToBeDeprecated.volume_id) {
                                        obj = 1;
                                        break;
                                    }
                                }
                                obj = null;
                                if (obj != null) {
                                }
                            }
                            if (closestPhotoSizeWithSize != null) {
                                i6 = photo.dc_id;
                                if (i6 != 0) {
                                    fileLocation = closestPhotoSizeWithSize.location;
                                    fileLocation.dc_id = i6;
                                    fileLocation.file_reference = photo.file_reference;
                                }
                                ImageLocation forPhoto = ImageLocation.getForPhoto(closestPhotoSizeWithSize, photo);
                                if (forPhoto != null) {
                                    this.imagesLocations.add(forPhoto);
                                    this.thumbsFileNames.add(FileLoader.getAttachFileName(closestPhotoSizeWithSize2));
                                    this.thumbsLocations.add(ImageLocation.getForPhoto(closestPhotoSizeWithSize2, photo));
                                    this.imagesLocationsSizes.add(Integer.valueOf(closestPhotoSizeWithSize.size));
                                }
                            }
                        }
                    }
                }
                loadNeighboringThumbs();
                getAdapter().notifyDataSetChanged();
                if (booleanValue) {
                    MessagesController.getInstance(this.currentAccount).loadDialogPhotos(intValue, 80, 0, false, this.parentClassGuid);
                }
            }
        } else if (i3 == NotificationCenter.fileDidLoad) {
            str = (String) objArr[0];
            while (i4 < this.thumbsFileNames.size()) {
                if (((String) this.thumbsFileNames.get(i4)).equals(str)) {
                    radialProgress2 = (RadialProgress2) this.radialProgresses.get(i4);
                    if (radialProgress2 != null) {
                        radialProgress2.setProgress(1.0f, true);
                    }
                }
                i4++;
            }
        } else if (i3 == NotificationCenter.FileLoadProgressChanged) {
            str = (String) objArr[0];
            while (i4 < this.thumbsFileNames.size()) {
                if (((String) this.thumbsFileNames.get(i4)).equals(str)) {
                    radialProgress2 = (RadialProgress2) this.radialProgresses.get(i4);
                    if (radialProgress2 != null) {
                        radialProgress2.setProgress(((Float) objArr[1]).floatValue(), true);
                    }
                }
                i4++;
            }
        }
    }
}
