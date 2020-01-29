package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.GestureDetector;
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
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.CircularViewPager;
import org.telegram.ui.ProfileActivity;

public class ProfileGalleryView extends CircularViewPager implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public final ViewPagerAdapter adapter;
    private int currentAccount = UserConfig.selectedAccount;
    private final long dialogId;
    private final PointF downPoint = new PointF();
    private final GestureDetector gestureDetector;
    /* access modifiers changed from: private */
    public ArrayList<ImageLocation> imagesLocations = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<Integer> imagesLocationsSizes = new ArrayList<>();
    private boolean isScrollingListView = true;
    private boolean isSwipingViewPager = true;
    private final int parentClassGuid;
    private final RecyclerListView parentListView;
    private ImageLocation prevImageLocation;
    /* access modifiers changed from: private */
    public final SparseArray<RadialProgress2> radialProgresses = new SparseArray<>();
    private ArrayList<String> thumbsFileNames = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<ImageLocation> thumbsLocations = new ArrayList<>();
    private final int touchSlop;

    private class Item {
        /* access modifiers changed from: private */
        public BackupImageView imageView;

        private Item() {
        }
    }

    public ProfileGalleryView(Context context, long j, ActionBar actionBar, RecyclerListView recyclerListView, ProfileActivity.AvatarImageView avatarImageView, int i) {
        super(context);
        setVisibility(8);
        setOverScrollMode(2);
        setOffscreenPageLimit(2);
        this.dialogId = j;
        this.parentListView = recyclerListView;
        this.parentClassGuid = i;
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(context, avatarImageView, actionBar);
        this.adapter = viewPagerAdapter;
        setAdapter((CircularViewPager.Adapter) viewPagerAdapter);
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.gestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener() {
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

            /* JADX WARNING: Code restructure failed: missing block: B:5:0x0031, code lost:
                if (r1 >= (r0 - r6)) goto L_0x0047;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public boolean onSingleTapUp(android.view.MotionEvent r6) {
                /*
                    r5 = this;
                    org.telegram.ui.Components.ProfileGalleryView r0 = org.telegram.ui.Components.ProfileGalleryView.this
                    org.telegram.ui.Components.ProfileGalleryView$ViewPagerAdapter r0 = r0.adapter
                    int r0 = r0.getCount()
                    org.telegram.ui.Components.ProfileGalleryView r1 = org.telegram.ui.Components.ProfileGalleryView.this
                    int r1 = r1.getCurrentItem()
                    r2 = 0
                    r3 = 1
                    if (r0 <= r3) goto L_0x004d
                    float r6 = r6.getX()
                    org.telegram.ui.Components.ProfileGalleryView r4 = org.telegram.ui.Components.ProfileGalleryView.this
                    int r4 = r4.getWidth()
                    int r4 = r4 / 3
                    float r4 = (float) r4
                    int r6 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
                    if (r6 <= 0) goto L_0x0034
                    org.telegram.ui.Components.ProfileGalleryView r6 = org.telegram.ui.Components.ProfileGalleryView.this
                    org.telegram.ui.Components.ProfileGalleryView$ViewPagerAdapter r6 = r6.adapter
                    int r6 = r6.getExtraCount()
                    int r1 = r1 + r3
                    int r0 = r0 - r6
                    if (r1 < r0) goto L_0x0046
                    goto L_0x0047
                L_0x0034:
                    org.telegram.ui.Components.ProfileGalleryView r6 = org.telegram.ui.Components.ProfileGalleryView.this
                    org.telegram.ui.Components.ProfileGalleryView$ViewPagerAdapter r6 = r6.adapter
                    int r6 = r6.getExtraCount()
                    int r1 = r1 + -1
                    if (r1 >= r6) goto L_0x0046
                    int r0 = r0 - r6
                    int r6 = r0 + -1
                    goto L_0x0047
                L_0x0046:
                    r6 = r1
                L_0x0047:
                    org.telegram.ui.Components.ProfileGalleryView r0 = org.telegram.ui.Components.ProfileGalleryView.this
                    r0.setCurrentItem(r6, r2)
                    return r3
                L_0x004d:
                    return r2
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ProfileGalleryView.AnonymousClass1.onSingleTapUp(android.view.MotionEvent):boolean");
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
                if (!this.isSwipingViewPager || !this.isScrollingListView) {
                    if (this.isSwipingViewPager && !canScrollHorizontally(-1) && x > ((float) this.touchSlop)) {
                        return false;
                    }
                } else if (Math.abs(y) >= ((float) this.touchSlop) || Math.abs(x) >= ((float) this.touchSlop)) {
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
            boolean onTouchEvent = this.isScrollingListView ? this.parentListView.onTouchEvent(motionEvent) | false : false;
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

    public void initIfEmpty(ImageLocation imageLocation, ImageLocation imageLocation2) {
        if (imageLocation != null && imageLocation2 != null) {
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
                this.imagesLocationsSizes.add(-1);
                getAdapter().notifyDataSetChanged();
            }
        }
    }

    public ImageLocation getImageLocation(int i) {
        if (i < 0 || i >= this.imagesLocations.size()) {
            return null;
        }
        return this.imagesLocations.get(i);
    }

    public ImageLocation getThumbLocation(int i) {
        if (i < 0 || i >= this.thumbsLocations.size()) {
            return null;
        }
        return this.thumbsLocations.get(i);
    }

    public boolean hasImages() {
        return !this.imagesLocations.isEmpty();
    }

    public BackupImageView getCurrentItemView() {
        ViewPagerAdapter viewPagerAdapter = this.adapter;
        if (viewPagerAdapter != null) {
            return ((Item) viewPagerAdapter.objects.get(getCurrentItem())).imageView;
        }
        return null;
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
                    FileLoader.getInstance(this.currentAccount).loadFile(this.thumbsLocations.get(i == 0 ? 1 : size - 1), (Object) null, (String) null, 0, 1);
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        RadialProgress2 radialProgress2;
        RadialProgress2 radialProgress22;
        ArrayList<TLRPC.PhotoSize> arrayList;
        boolean z;
        int i3 = i;
        int i4 = 0;
        if (i3 == NotificationCenter.dialogPhotosLoaded) {
            int intValue = objArr[3].intValue();
            int intValue2 = objArr[0].intValue();
            if (((long) intValue2) == this.dialogId && this.parentClassGuid == intValue) {
                boolean booleanValue = objArr[2].booleanValue();
                ArrayList arrayList2 = objArr[4];
                this.thumbsFileNames.clear();
                this.imagesLocations.clear();
                this.thumbsLocations.clear();
                this.imagesLocationsSizes.clear();
                ImageLocation imageLocation = null;
                if (intValue2 < 0) {
                    TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-intValue2));
                    ImageLocation forChat = ImageLocation.getForChat(chat, true);
                    if (forChat != null) {
                        this.thumbsFileNames.add("");
                        this.imagesLocations.add(forChat);
                        this.thumbsLocations.add(ImageLocation.getForChat(chat, false));
                        this.imagesLocationsSizes.add(-1);
                    }
                    imageLocation = forChat;
                }
                for (int i5 = 0; i5 < arrayList2.size(); i5++) {
                    TLRPC.Photo photo = (TLRPC.Photo) arrayList2.get(i5);
                    if (!(photo == null || (photo instanceof TLRPC.TL_photoEmpty) || (arrayList = photo.sizes) == null)) {
                        TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, 640);
                        TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 50);
                        if (imageLocation != null) {
                            int i6 = 0;
                            while (true) {
                                if (i6 >= photo.sizes.size()) {
                                    z = false;
                                    break;
                                }
                                TLRPC.FileLocation fileLocation = photo.sizes.get(i6).location;
                                int i7 = fileLocation.local_id;
                                TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated = imageLocation.location;
                                if (i7 == tL_fileLocationToBeDeprecated.local_id && fileLocation.volume_id == tL_fileLocationToBeDeprecated.volume_id) {
                                    z = true;
                                    break;
                                }
                                i6++;
                            }
                            if (z) {
                            }
                        }
                        if (closestPhotoSizeWithSize != null) {
                            int i8 = photo.dc_id;
                            if (i8 != 0) {
                                TLRPC.FileLocation fileLocation2 = closestPhotoSizeWithSize.location;
                                fileLocation2.dc_id = i8;
                                fileLocation2.file_reference = photo.file_reference;
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
                loadNeighboringThumbs();
                getAdapter().notifyDataSetChanged();
                if (booleanValue) {
                    MessagesController.getInstance(this.currentAccount).loadDialogPhotos(intValue2, 80, 0, false, this.parentClassGuid);
                }
            }
        } else if (i3 == NotificationCenter.fileDidLoad) {
            String str = objArr[0];
            while (i4 < this.thumbsFileNames.size()) {
                if (this.thumbsFileNames.get(i4).equals(str) && (radialProgress22 = this.radialProgresses.get(i4)) != null) {
                    radialProgress22.setProgress(1.0f, true);
                }
                i4++;
            }
        } else if (i3 == NotificationCenter.FileLoadProgressChanged) {
            String str2 = objArr[0];
            while (i4 < this.thumbsFileNames.size()) {
                if (this.thumbsFileNames.get(i4).equals(str2) && (radialProgress2 = this.radialProgresses.get(i4)) != null) {
                    radialProgress2.setProgress(Math.min(1.0f, ((float) objArr[1].longValue()) / ((float) objArr[2].longValue())), true);
                }
                i4++;
            }
        }
    }

    public class ViewPagerAdapter extends CircularViewPager.Adapter {
        private final Context context;
        /* access modifiers changed from: private */
        public final ArrayList<Item> objects = new ArrayList<>();
        /* access modifiers changed from: private */
        public final ActionBar parentActionBar;
        /* access modifiers changed from: private */
        public final ProfileActivity.AvatarImageView parentAvatarImageView;
        /* access modifiers changed from: private */
        public final Paint placeholderPaint;

        public ViewPagerAdapter(Context context2, ProfileActivity.AvatarImageView avatarImageView, ActionBar actionBar) {
            this.context = context2;
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
            if (indexOf == -1) {
                return -2;
            }
            return indexOf;
        }

        public Item instantiateItem(ViewGroup viewGroup, final int i) {
            Item item = this.objects.get(i);
            if (item.imageView == null) {
                BackupImageView unused = item.imageView = new BackupImageView(this.context) {
                    private long firstDrawTime = -1;
                    /* access modifiers changed from: private */
                    public RadialProgress2 radialProgress;
                    private ValueAnimator radialProgressHideAnimator;
                    private float radialProgressHideAnimatorStartValue;
                    private final int radialProgressSize = AndroidUtilities.dp(64.0f);

                    {
                        int realPosition = ViewPagerAdapter.this.getRealPosition(i);
                        if (realPosition == 0) {
                            setImage((ImageLocation) ProfileGalleryView.this.imagesLocations.get(realPosition), (String) null, ViewPagerAdapter.this.parentAvatarImageView.getImageReceiver().getBitmap(), ((Integer) ProfileGalleryView.this.imagesLocationsSizes.get(realPosition)).intValue(), (Object) null);
                        } else {
                            setImage((ImageLocation) ProfileGalleryView.this.imagesLocations.get(realPosition), (String) null, (ImageLocation) ProfileGalleryView.this.thumbsLocations.get(realPosition), (String) null, (Drawable) null, (Bitmap) null, (String) null, ((Integer) ProfileGalleryView.this.imagesLocationsSizes.get(realPosition)).intValue(), (Object) null);
                            this.radialProgress = new RadialProgress2(this);
                            this.radialProgress.setOverrideAlpha(0.0f);
                            this.radialProgress.setIcon(10, false, false);
                            this.radialProgress.setColors(NUM, NUM, -1, -1);
                            ProfileGalleryView.this.radialProgresses.append(i, this.radialProgress);
                        }
                        getImageReceiver().setCrossfadeAlpha((byte) 2);
                    }

                    /* access modifiers changed from: protected */
                    public void onSizeChanged(int i, int i2, int i3, int i4) {
                        super.onSizeChanged(i, i2, i3, i4);
                        if (this.radialProgress != null) {
                            int currentActionBarHeight = (ViewPagerAdapter.this.parentActionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
                            int dp2 = AndroidUtilities.dp2(80.0f);
                            RadialProgress2 radialProgress2 = this.radialProgress;
                            int i5 = this.radialProgressSize;
                            int i6 = (i2 - currentActionBarHeight) - dp2;
                            radialProgress2.setProgressRect((i - i5) / 2, ((i6 - i5) / 2) + currentActionBarHeight, (i + i5) / 2, currentActionBarHeight + ((i6 + i5) / 2));
                        }
                    }

                    /* access modifiers changed from: protected */
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
                                this.radialProgressHideAnimator.addUpdateListener(
                                /*  JADX ERROR: Method code generation error
                                    jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0040: INVOKE  
                                      (wrap: android.animation.ValueAnimator : 0x0039: IGET  (r0v26 android.animation.ValueAnimator) = 
                                      (r11v0 'this' org.telegram.ui.Components.ProfileGalleryView$ViewPagerAdapter$1 A[THIS])
                                     org.telegram.ui.Components.ProfileGalleryView.ViewPagerAdapter.1.radialProgressHideAnimator android.animation.ValueAnimator)
                                      (wrap: org.telegram.ui.Components.-$$Lambda$ProfileGalleryView$ViewPagerAdapter$1$wvZBxr78pLdwArB7T9Z7FL-LYVM : 0x003d: CONSTRUCTOR  (r1v6 org.telegram.ui.Components.-$$Lambda$ProfileGalleryView$ViewPagerAdapter$1$wvZBxr78pLdwArB7T9Z7FL-LYVM) = 
                                      (r11v0 'this' org.telegram.ui.Components.ProfileGalleryView$ViewPagerAdapter$1 A[THIS])
                                     call: org.telegram.ui.Components.-$$Lambda$ProfileGalleryView$ViewPagerAdapter$1$wvZBxr78pLdwArB7T9Z7FL-LYVM.<init>(org.telegram.ui.Components.ProfileGalleryView$ViewPagerAdapter$1):void type: CONSTRUCTOR)
                                     android.animation.ValueAnimator.addUpdateListener(android.animation.ValueAnimator$AnimatorUpdateListener):void type: VIRTUAL in method: org.telegram.ui.Components.ProfileGalleryView.ViewPagerAdapter.1.onDraw(android.graphics.Canvas):void, dex: classes.dex
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                    	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                    	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                    	at jadx.core.codegen.RegionGen.connectElseIf(RegionGen.java:175)
                                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:152)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                    	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                    	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                    	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                    	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                    	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                    	at java.util.ArrayList.forEach(ArrayList.java:1257)
                                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                    	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:429)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                    	at jadx.core.codegen.InsnGen.inlineMethod(InsnGen.java:924)
                                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:684)
                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                    	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                    	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                    	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                    	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                    	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                    	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                    	at java.util.ArrayList.forEach(ArrayList.java:1257)
                                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                    	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                    	at java.util.ArrayList.forEach(ArrayList.java:1257)
                                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                    	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                                    	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                                    	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                                    	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                                    	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                                    	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                                    Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x003d: CONSTRUCTOR  (r1v6 org.telegram.ui.Components.-$$Lambda$ProfileGalleryView$ViewPagerAdapter$1$wvZBxr78pLdwArB7T9Z7FL-LYVM) = 
                                      (r11v0 'this' org.telegram.ui.Components.ProfileGalleryView$ViewPagerAdapter$1 A[THIS])
                                     call: org.telegram.ui.Components.-$$Lambda$ProfileGalleryView$ViewPagerAdapter$1$wvZBxr78pLdwArB7T9Z7FL-LYVM.<init>(org.telegram.ui.Components.ProfileGalleryView$ViewPagerAdapter$1):void type: CONSTRUCTOR in method: org.telegram.ui.Components.ProfileGalleryView.ViewPagerAdapter.1.onDraw(android.graphics.Canvas):void, dex: classes.dex
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                    	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                    	... 95 more
                                    Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.Components.-$$Lambda$ProfileGalleryView$ViewPagerAdapter$1$wvZBxr78pLdwArB7T9Z7FL-LYVM, state: NOT_LOADED
                                    	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                    	... 101 more
                                    */
                                /*
                                    this = this;
                                    org.telegram.ui.Components.RadialProgress2 r0 = r11.radialProgress
                                    if (r0 == 0) goto L_0x009e
                                    org.telegram.messenger.ImageReceiver r0 = r11.getImageReceiver()
                                    android.graphics.drawable.Drawable r0 = r0.getDrawable()
                                    if (r0 == 0) goto L_0x0053
                                    android.animation.ValueAnimator r0 = r11.radialProgressHideAnimator
                                    if (r0 != 0) goto L_0x0088
                                    org.telegram.ui.Components.RadialProgress2 r0 = r11.radialProgress
                                    float r0 = r0.getOverrideAlpha()
                                    r11.radialProgressHideAnimatorStartValue = r0
                                    r0 = 2
                                    float[] r0 = new float[r0]
                                    r0 = {0, NUM} // fill-array
                                    android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
                                    r11.radialProgressHideAnimator = r0
                                    android.animation.ValueAnimator r0 = r11.radialProgressHideAnimator
                                    float r1 = r11.radialProgressHideAnimatorStartValue
                                    r2 = 1127153664(0x432var_, float:175.0)
                                    float r1 = r1 * r2
                                    long r1 = (long) r1
                                    r0.setDuration(r1)
                                    android.animation.ValueAnimator r0 = r11.radialProgressHideAnimator
                                    org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
                                    r0.setInterpolator(r1)
                                    android.animation.ValueAnimator r0 = r11.radialProgressHideAnimator
                                    org.telegram.ui.Components.-$$Lambda$ProfileGalleryView$ViewPagerAdapter$1$wvZBxr78pLdwArB7T9Z7FL-LYVM r1 = new org.telegram.ui.Components.-$$Lambda$ProfileGalleryView$ViewPagerAdapter$1$wvZBxr78pLdwArB7T9Z7FL-LYVM
                                    r1.<init>(r11)
                                    r0.addUpdateListener(r1)
                                    android.animation.ValueAnimator r0 = r11.radialProgressHideAnimator
                                    org.telegram.ui.Components.ProfileGalleryView$ViewPagerAdapter$1$1 r1 = new org.telegram.ui.Components.ProfileGalleryView$ViewPagerAdapter$1$1
                                    r1.<init>()
                                    r0.addListener(r1)
                                    android.animation.ValueAnimator r0 = r11.radialProgressHideAnimator
                                    r0.start()
                                    goto L_0x0088
                                L_0x0053:
                                    long r0 = r11.firstDrawTime
                                    r2 = 0
                                    int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                                    if (r4 >= 0) goto L_0x0062
                                    long r0 = java.lang.System.currentTimeMillis()
                                    r11.firstDrawTime = r0
                                    goto L_0x0085
                                L_0x0062:
                                    long r0 = java.lang.System.currentTimeMillis()
                                    long r2 = r11.firstDrawTime
                                    long r0 = r0 - r2
                                    r2 = 1000(0x3e8, double:4.94E-321)
                                    int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                                    if (r4 > 0) goto L_0x0085
                                    r2 = 750(0x2ee, double:3.705E-321)
                                    int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                                    if (r4 <= 0) goto L_0x0085
                                    org.telegram.ui.Components.RadialProgress2 r4 = r11.radialProgress
                                    org.telegram.ui.Components.CubicBezierInterpolator r5 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
                                    long r0 = r0 - r2
                                    float r0 = (float) r0
                                    r1 = 1132068864(0x437a0000, float:250.0)
                                    float r0 = r0 / r1
                                    float r0 = r5.getInterpolation(r0)
                                    r4.setOverrideAlpha(r0)
                                L_0x0085:
                                    r11.postInvalidateOnAnimation()
                                L_0x0088:
                                    r6 = 0
                                    r7 = 0
                                    int r0 = r11.getWidth()
                                    float r8 = (float) r0
                                    int r0 = r11.getHeight()
                                    float r9 = (float) r0
                                    org.telegram.ui.Components.ProfileGalleryView$ViewPagerAdapter r0 = org.telegram.ui.Components.ProfileGalleryView.ViewPagerAdapter.this
                                    android.graphics.Paint r10 = r0.placeholderPaint
                                    r5 = r12
                                    r5.drawRect(r6, r7, r8, r9, r10)
                                L_0x009e:
                                    super.onDraw(r12)
                                    org.telegram.ui.Components.RadialProgress2 r0 = r11.radialProgress
                                    if (r0 == 0) goto L_0x00b3
                                    float r0 = r0.getOverrideAlpha()
                                    r1 = 0
                                    int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                                    if (r0 <= 0) goto L_0x00b3
                                    org.telegram.ui.Components.RadialProgress2 r0 = r11.radialProgress
                                    r0.draw(r12)
                                L_0x00b3:
                                    return
                                */
                                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ProfileGalleryView.ViewPagerAdapter.AnonymousClass1.onDraw(android.graphics.Canvas):void");
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
                    return (getRealPosition(i) + 1) + "/" + (getCount() - (getExtraCount() * 2));
                }

                public void notifyDataSetChanged() {
                    this.objects.clear();
                    int size = ProfileGalleryView.this.imagesLocations.size() + (getExtraCount() * 2);
                    for (int i = 0; i < size; i++) {
                        this.objects.add(new Item());
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
        }
