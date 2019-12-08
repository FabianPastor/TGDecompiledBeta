package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.GeoPoint;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.TL_channelLocation;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_geoPoint;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_messages_getRecentLocations;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.LocationActivityAdapter;
import org.telegram.ui.Adapters.LocationActivitySearchAdapter;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LocationCell;
import org.telegram.ui.Cells.LocationLoadingCell;
import org.telegram.ui.Cells.LocationPoweredCell;
import org.telegram.ui.Cells.SendLocationCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MapPlaceholderDrawable;
import org.telegram.ui.Components.RecyclerListView;

public class LocationActivity extends BaseFragment implements NotificationCenterDelegate {
    public static final int LOCATION_TYPE_GROUP = 4;
    public static final int LOCATION_TYPE_GROUP_VIEW = 5;
    public static final int LOCATION_TYPE_SEND = 0;
    private static final int map_list_menu_hybrid = 4;
    private static final int map_list_menu_map = 2;
    private static final int map_list_menu_satellite = 3;
    private static final int share = 1;
    private LocationActivityAdapter adapter;
    private AnimatorSet animatorSet;
    private AvatarDrawable avatarDrawable;
    private TL_channelLocation chatLocation;
    private boolean checkGpsEnabled = true;
    private boolean checkPermission = true;
    private CircleOptions circleOptions;
    private LocationActivityDelegate delegate;
    private long dialogId;
    private EmptyTextProgressView emptyView;
    private boolean firstFocus = true;
    private boolean firstWas;
    private GoogleMap googleMap;
    private TL_channelLocation initialLocation;
    private boolean isFirstLocation = true;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private ImageView locationButton;
    private int locationType;
    private MapView mapView;
    private FrameLayout mapViewClip;
    private boolean mapsInitialized;
    private View markerImageView;
    private int markerTop;
    private ArrayList<LiveLocation> markers = new ArrayList();
    private SparseArray<LiveLocation> markersMap = new SparseArray();
    private MessageObject messageObject;
    private Location myLocation;
    private boolean onResumeCalled;
    private ActionBarMenuItem otherItem;
    private int overScrollHeight = ((AndroidUtilities.displaySize.x - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(66.0f));
    private ChatActivity parentFragment;
    private ImageView routeButton;
    private LocationActivitySearchAdapter searchAdapter;
    private RecyclerListView searchListView;
    private boolean searchWas;
    private boolean searching;
    private Runnable updateRunnable;
    private Location userLocation;
    private boolean userLocationMoved;
    private boolean wasResults;

    public class LiveLocation {
        public Chat chat;
        public int id;
        public Marker marker;
        public Message object;
        public User user;
    }

    public interface LocationActivityDelegate {
        void didSelectLocation(MessageMedia messageMedia, int i, boolean z, int i2);
    }

    static /* synthetic */ void lambda$getThemeDescriptions$21() {
    }

    public LocationActivity(int i) {
        this.locationType = i;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.swipeBackEnabled = false;
        getNotificationCenter().addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.locationPermissionGranted);
        MessageObject messageObject = this.messageObject;
        if (messageObject != null && messageObject.isLiveLocation()) {
            getNotificationCenter().addObserver(this, NotificationCenter.didReceiveNewMessages);
            getNotificationCenter().addObserver(this, NotificationCenter.replaceMessagesObjects);
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.locationPermissionGranted);
        getNotificationCenter().removeObserver(this, NotificationCenter.closeChats);
        getNotificationCenter().removeObserver(this, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().removeObserver(this, NotificationCenter.replaceMessagesObjects);
        try {
            if (this.googleMap != null) {
                this.googleMap.setMyLocationEnabled(false);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            if (this.mapView != null) {
                this.mapView.onDestroy();
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        LocationActivityAdapter locationActivityAdapter = this.adapter;
        if (locationActivityAdapter != null) {
            locationActivityAdapter.destroy();
        }
        LocationActivitySearchAdapter locationActivitySearchAdapter = this.searchAdapter;
        if (locationActivitySearchAdapter != null) {
            locationActivitySearchAdapter.destroy();
        }
        Runnable runnable = this.updateRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.updateRunnable = null;
        }
    }

    public View createView(Context context) {
        MessageObject messageObject;
        MessageObject messageObject2;
        Context context2 = context;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAddToContainer(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                String str = ",";
                if (i == -1) {
                    LocationActivity.this.finishFragment();
                } else if (i == 2) {
                    if (LocationActivity.this.googleMap != null) {
                        LocationActivity.this.googleMap.setMapType(1);
                    }
                } else if (i == 3) {
                    if (LocationActivity.this.googleMap != null) {
                        LocationActivity.this.googleMap.setMapType(2);
                    }
                } else if (i == 4) {
                    if (LocationActivity.this.googleMap != null) {
                        LocationActivity.this.googleMap.setMapType(4);
                    }
                } else if (i == 1) {
                    try {
                        double d = LocationActivity.this.messageObject.messageOwner.media.geo.lat;
                        double d2 = LocationActivity.this.messageObject.messageOwner.media.geo._long;
                        Activity parentActivity = LocationActivity.this.getParentActivity();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("geo:");
                        stringBuilder.append(d);
                        stringBuilder.append(str);
                        stringBuilder.append(d2);
                        stringBuilder.append("?q=");
                        stringBuilder.append(d);
                        stringBuilder.append(str);
                        stringBuilder.append(d2);
                        parentActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(stringBuilder.toString())));
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            }
        });
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (this.chatLocation != null) {
            this.actionBar.setTitle(LocaleController.getString("ChatLocation", NUM));
        } else {
            messageObject = this.messageObject;
            if (messageObject == null) {
                this.actionBar.setTitle(LocaleController.getString("ShareLocation", NUM));
                if (this.locationType != 4) {
                    createMenu.addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
                        public void onSearchExpand() {
                            LocationActivity.this.searching = true;
                            LocationActivity.this.otherItem.setVisibility(8);
                            LocationActivity.this.listView.setVisibility(8);
                            LocationActivity.this.mapViewClip.setVisibility(8);
                            LocationActivity.this.searchListView.setVisibility(0);
                            LocationActivity.this.searchListView.setEmptyView(LocationActivity.this.emptyView);
                            LocationActivity.this.emptyView.showTextView();
                        }

                        public void onSearchCollapse() {
                            LocationActivity.this.searching = false;
                            LocationActivity.this.searchWas = false;
                            LocationActivity.this.otherItem.setVisibility(0);
                            LocationActivity.this.searchListView.setEmptyView(null);
                            LocationActivity.this.listView.setVisibility(0);
                            LocationActivity.this.mapViewClip.setVisibility(0);
                            LocationActivity.this.searchListView.setVisibility(8);
                            LocationActivity.this.emptyView.setVisibility(8);
                            LocationActivity.this.searchAdapter.searchDelayed(null, null);
                        }

                        public void onTextChanged(EditText editText) {
                            if (LocationActivity.this.searchAdapter != null) {
                                String obj = editText.getText().toString();
                                if (obj.length() != 0) {
                                    LocationActivity.this.searchWas = true;
                                }
                                LocationActivity.this.emptyView.showProgress();
                                LocationActivity.this.searchAdapter.searchDelayed(obj, LocationActivity.this.userLocation);
                            }
                        }
                    }).setSearchFieldHint(LocaleController.getString("Search", NUM));
                }
            } else if (messageObject.isLiveLocation()) {
                this.actionBar.setTitle(LocaleController.getString("AttachLiveLocation", NUM));
            } else {
                String str = this.messageObject.messageOwner.media.title;
                if (str == null || str.length() <= 0) {
                    this.actionBar.setTitle(LocaleController.getString("ChatLocation", NUM));
                } else {
                    this.actionBar.setTitle(LocaleController.getString("SharedPlace", NUM));
                }
                createMenu.addItem(1, NUM).setContentDescription(LocaleController.getString("ShareFile", NUM));
            }
        }
        this.otherItem = createMenu.addItem(0, NUM);
        this.otherItem.addSubItem(2, NUM, LocaleController.getString("Map", NUM));
        this.otherItem.addSubItem(3, NUM, LocaleController.getString("Satellite", NUM));
        this.otherItem.addSubItem(4, NUM, LocaleController.getString("Hybrid", NUM));
        this.otherItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        this.fragmentView = new FrameLayout(context2) {
            private boolean first = true;

            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                if (z) {
                    LocationActivity.this.fixLayoutInternal(this.first);
                    this.first = false;
                }
            }
        };
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.locationButton = new ImageView(context2);
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("profile_actionBackground"), Theme.getColor("profile_actionPressedBackground"));
        if (VERSION.SDK_INT < 21) {
            Drawable mutate = context.getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, Mode.MULTIPLY));
            Drawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        this.locationButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
        this.locationButton.setImageResource(NUM);
        this.locationButton.setScaleType(ScaleType.CENTER);
        this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("profile_actionIcon"), Mode.MULTIPLY));
        this.locationButton.setContentDescription(LocaleController.getString("AccDescrMyLocation", NUM));
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.locationButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.locationButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.locationButton.setStateListAnimator(stateListAnimator);
            this.locationButton.setOutlineProvider(new ViewOutlineProvider() {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        if (this.chatLocation != null) {
            this.userLocation = new Location("network");
            this.userLocation.setLatitude(this.chatLocation.geo_point.lat);
            this.userLocation.setLongitude(this.chatLocation.geo_point._long);
        } else if (this.messageObject != null) {
            this.userLocation = new Location("network");
            this.userLocation.setLatitude(this.messageObject.messageOwner.media.geo.lat);
            this.userLocation.setLongitude(this.messageObject.messageOwner.media.geo._long);
        }
        this.searchWas = false;
        this.searching = false;
        this.mapViewClip = new FrameLayout(context2);
        this.mapViewClip.setBackgroundDrawable(new MapPlaceholderDrawable());
        LocationActivityAdapter locationActivityAdapter = this.adapter;
        if (locationActivityAdapter != null) {
            locationActivityAdapter.destroy();
        }
        LocationActivitySearchAdapter locationActivitySearchAdapter = this.searchAdapter;
        if (locationActivitySearchAdapter != null) {
            locationActivitySearchAdapter.destroy();
        }
        this.listView = new RecyclerListView(context2);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        RecyclerListView recyclerListView = this.listView;
        LocationActivityAdapter locationActivityAdapter2 = new LocationActivityAdapter(context2, this.locationType, this.dialogId);
        this.adapter = locationActivityAdapter2;
        recyclerListView.setAdapter(locationActivityAdapter2);
        this.listView.setVerticalScrollBarEnabled(false);
        recyclerListView = this.listView;
        AnonymousClass5 anonymousClass5 = new LinearLayoutManager(context2, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = anonymousClass5;
        recyclerListView.setLayoutManager(anonymousClass5);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                if (LocationActivity.this.adapter.getItemCount() != 0) {
                    int findFirstVisibleItemPosition = LocationActivity.this.layoutManager.findFirstVisibleItemPosition();
                    if (findFirstVisibleItemPosition != -1) {
                        LocationActivity.this.updateClipView(findFirstVisibleItemPosition);
                        if (!(LocationActivity.this.locationType == 4 || i2 <= 0 || LocationActivity.this.adapter.isPulledUp())) {
                            LocationActivity.this.adapter.setPulledUp();
                            if (LocationActivity.this.myLocation != null) {
                                AndroidUtilities.runOnUIThread(new -$$Lambda$LocationActivity$6$e4diUUxQq_JKRLDvY3ypH_z7db4(this));
                            }
                        }
                    }
                }
            }

            public /* synthetic */ void lambda$onScrolled$0$LocationActivity$6() {
                LocationActivity.this.adapter.searchPlacesWithQuery(null, LocationActivity.this.myLocation, true);
            }
        });
        this.listView.setOnItemClickListener(new -$$Lambda$LocationActivity$3ZuEtEMOSdh5Ci0BsPodKsI3Wyo(this));
        this.adapter.setDelegate(this.dialogId, new -$$Lambda$LocationActivity$NtWcIBHqOy4w4UYgRV-QpiBtBE8(this));
        this.adapter.setOverScrollHeight(this.overScrollHeight);
        frameLayout.addView(this.mapViewClip, LayoutHelper.createFrame(-1, -1, 51));
        this.mapView = new MapView(context2) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (LocationActivity.this.messageObject == null && LocationActivity.this.chatLocation == null) {
                    AnimatorSet access$1800;
                    Animator[] animatorArr;
                    if (motionEvent.getAction() == 0) {
                        if (LocationActivity.this.animatorSet != null) {
                            LocationActivity.this.animatorSet.cancel();
                        }
                        LocationActivity.this.animatorSet = new AnimatorSet();
                        LocationActivity.this.animatorSet.setDuration(200);
                        access$1800 = LocationActivity.this.animatorSet;
                        animatorArr = new Animator[1];
                        animatorArr[0] = ObjectAnimator.ofFloat(LocationActivity.this.markerImageView, View.TRANSLATION_Y, new float[]{(float) (LocationActivity.this.markerTop - AndroidUtilities.dp(10.0f))});
                        access$1800.playTogether(animatorArr);
                        LocationActivity.this.animatorSet.start();
                    } else if (motionEvent.getAction() == 1) {
                        if (LocationActivity.this.animatorSet != null) {
                            LocationActivity.this.animatorSet.cancel();
                        }
                        LocationActivity.this.animatorSet = new AnimatorSet();
                        LocationActivity.this.animatorSet.setDuration(200);
                        access$1800 = LocationActivity.this.animatorSet;
                        animatorArr = new Animator[1];
                        animatorArr[0] = ObjectAnimator.ofFloat(LocationActivity.this.markerImageView, View.TRANSLATION_Y, new float[]{(float) LocationActivity.this.markerTop});
                        access$1800.playTogether(animatorArr);
                        LocationActivity.this.animatorSet.start();
                        LocationActivity.this.adapter.fetchLocationAddress();
                    }
                    if (motionEvent.getAction() == 2) {
                        if (!LocationActivity.this.userLocationMoved) {
                            access$1800 = new AnimatorSet();
                            access$1800.setDuration(200);
                            access$1800.play(ObjectAnimator.ofFloat(LocationActivity.this.locationButton, View.ALPHA, new float[]{1.0f}));
                            access$1800.start();
                            LocationActivity.this.userLocationMoved = true;
                        }
                        if (!(LocationActivity.this.googleMap == null || LocationActivity.this.userLocation == null)) {
                            LocationActivity.this.userLocation.setLatitude(LocationActivity.this.googleMap.getCameraPosition().target.latitude);
                            LocationActivity.this.userLocation.setLongitude(LocationActivity.this.googleMap.getCameraPosition().target.longitude);
                        }
                        LocationActivity.this.adapter.setCustomLocation(LocationActivity.this.userLocation);
                    }
                }
                return super.onInterceptTouchEvent(motionEvent);
            }
        };
        new Thread(new -$$Lambda$LocationActivity$kRAJeBbg2Ym7K8em4l4Pd9kMTUc(this, this.mapView)).start();
        View view = new View(context2);
        view.setBackgroundResource(NUM);
        this.mapViewClip.addView(view, LayoutHelper.createFrame(-1, 3, 83));
        if (this.messageObject == null && this.chatLocation == null) {
            if (this.locationType == 4 && this.dialogId != 0) {
                Object chat = getMessagesController().getChat(Integer.valueOf(-((int) this.dialogId)));
                if (chat != null) {
                    FrameLayout frameLayout2 = new FrameLayout(context2);
                    frameLayout2.setBackgroundResource(NUM);
                    this.mapViewClip.addView(frameLayout2, LayoutHelper.createFrame(62, 76, 49));
                    BackupImageView backupImageView = new BackupImageView(context2);
                    backupImageView.setRoundRadius(AndroidUtilities.dp(26.0f));
                    backupImageView.setImage(ImageLocation.getForChat(chat, false), "50_50", new AvatarDrawable((Chat) chat), chat);
                    frameLayout2.addView(backupImageView, LayoutHelper.createFrame(52, 52.0f, 51, 5.0f, 5.0f, 0.0f, 0.0f));
                    this.markerImageView = frameLayout2;
                    this.markerImageView.setTag(Integer.valueOf(1));
                }
            }
            if (this.markerImageView == null) {
                ImageView imageView = new ImageView(context2);
                imageView.setImageResource(NUM);
                this.mapViewClip.addView(imageView, LayoutHelper.createFrame(28, 48, 49));
                this.markerImageView = imageView;
            }
            this.emptyView = new EmptyTextProgressView(context2);
            this.emptyView.setText(LocaleController.getString("NoResult", NUM));
            this.emptyView.setShowAtCenter(true);
            this.emptyView.setVisibility(8);
            frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
            this.searchListView = new RecyclerListView(context2);
            this.searchListView.setVisibility(8);
            this.searchListView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
            RecyclerListView recyclerListView2 = this.searchListView;
            locationActivitySearchAdapter = new LocationActivitySearchAdapter(context2);
            this.searchAdapter = locationActivitySearchAdapter;
            recyclerListView2.setAdapter(locationActivitySearchAdapter);
            frameLayout.addView(this.searchListView, LayoutHelper.createFrame(-1, -1, 51));
            this.searchListView.setOnScrollListener(new OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                    if (i == 1 && LocationActivity.this.searching && LocationActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(LocationActivity.this.getParentActivity().getCurrentFocus());
                    }
                }
            });
            this.searchListView.setOnItemClickListener(new -$$Lambda$LocationActivity$3aEWKRVmFFczwCGULRKYkpzZ6Ac(this));
        } else {
            messageObject = this.messageObject;
            if (!((messageObject == null || messageObject.isLiveLocation()) && this.chatLocation == null)) {
                this.routeButton = new ImageView(context2);
                Drawable createSimpleSelectorCircleDrawable2 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
                if (VERSION.SDK_INT < 21) {
                    Drawable mutate2 = context.getResources().getDrawable(NUM).mutate();
                    mutate2.setColorFilter(new PorterDuffColorFilter(-16777216, Mode.MULTIPLY));
                    Drawable combinedDrawable2 = new CombinedDrawable(mutate2, createSimpleSelectorCircleDrawable2, 0, 0);
                    combinedDrawable2.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    createSimpleSelectorCircleDrawable2 = combinedDrawable2;
                }
                this.routeButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable2);
                this.routeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), Mode.MULTIPLY));
                this.routeButton.setImageResource(NUM);
                this.routeButton.setScaleType(ScaleType.CENTER);
                if (VERSION.SDK_INT >= 21) {
                    StateListAnimator stateListAnimator2 = new StateListAnimator();
                    stateListAnimator2.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.routeButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
                    stateListAnimator2.addState(new int[0], ObjectAnimator.ofFloat(this.routeButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
                    this.routeButton.setStateListAnimator(stateListAnimator2);
                    this.routeButton.setOutlineProvider(new ViewOutlineProvider() {
                        @SuppressLint({"NewApi"})
                        public void getOutline(View view, Outline outline) {
                            outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                        }
                    });
                }
                frameLayout.addView(this.routeButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 37.0f));
                this.routeButton.setOnClickListener(new -$$Lambda$LocationActivity$0vuMAC0UTT1fhCHoXsMPQshzxPI(this));
                TL_channelLocation tL_channelLocation = this.chatLocation;
                if (tL_channelLocation != null) {
                    this.adapter.setChatLocation(tL_channelLocation);
                } else {
                    messageObject2 = this.messageObject;
                    if (messageObject2 != null) {
                        this.adapter.setMessageObject(messageObject2);
                    }
                }
            }
        }
        messageObject2 = this.messageObject;
        if ((messageObject2 == null || messageObject2.isLiveLocation()) && this.chatLocation == null) {
            this.mapViewClip.addView(this.locationButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 14.0f));
        } else {
            this.mapViewClip.addView(this.locationButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 43.0f));
        }
        this.locationButton.setOnClickListener(new -$$Lambda$LocationActivity$skB9LwCh2ItrV0m_dOMOlJlUXlA(this));
        if (this.messageObject == null && this.chatLocation == null) {
            if (this.initialLocation == null) {
                this.locationButton.setAlpha(0.0f);
            } else {
                this.userLocationMoved = true;
            }
        }
        frameLayout.addView(this.actionBar);
        return this.fragmentView;
    }

    /* JADX WARNING: Missing block: B:59:0x0199, code skipped:
            return;
     */
    public /* synthetic */ void lambda$createView$6$LocationActivity(android.view.View r9, int r10) {
        /*
        r8 = this;
        r9 = r8.locationType;
        r0 = 4;
        r1 = 3;
        r2 = 0;
        r3 = 1;
        if (r9 != r0) goto L_0x007c;
    L_0x0008:
        if (r10 != r3) goto L_0x01ee;
    L_0x000a:
        r9 = r8.adapter;
        r9 = r9.getItem(r10);
        r9 = (org.telegram.tgnet.TLRPC.TL_messageMediaVenue) r9;
        if (r9 != 0) goto L_0x0015;
    L_0x0014:
        return;
    L_0x0015:
        r4 = r8.dialogId;
        r6 = 0;
        r10 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r10 != 0) goto L_0x0027;
    L_0x001d:
        r10 = r8.delegate;
        r10.didSelectLocation(r9, r0, r3, r2);
        r8.finishFragment();
        goto L_0x01ee;
    L_0x0027:
        r10 = new org.telegram.ui.ActionBar.AlertDialog[r3];
        r0 = new org.telegram.ui.ActionBar.AlertDialog;
        r3 = r8.getParentActivity();
        r0.<init>(r3, r1);
        r10[r2] = r0;
        r0 = new org.telegram.tgnet.TLRPC$TL_channels_editLocation;
        r0.<init>();
        r1 = r9.address;
        r0.address = r1;
        r1 = r8.getMessagesController();
        r3 = r8.dialogId;
        r4 = (int) r3;
        r3 = -r4;
        r1 = r1.getInputChannel(r3);
        r0.channel = r1;
        r1 = new org.telegram.tgnet.TLRPC$TL_inputGeoPoint;
        r1.<init>();
        r0.geo_point = r1;
        r1 = r0.geo_point;
        r3 = r9.geo;
        r4 = r3.lat;
        r1.lat = r4;
        r3 = r3._long;
        r1._long = r3;
        r1 = r8.getConnectionsManager();
        r3 = new org.telegram.ui.-$$Lambda$LocationActivity$eTbcUM4Sn7VU43gkS_W-mbUaQEY;
        r3.<init>(r8, r10, r9);
        r9 = r1.sendRequest(r0, r3);
        r0 = r10[r2];
        r1 = new org.telegram.ui.-$$Lambda$LocationActivity$LnFEOAIDJMLhZmlmCTS4P1Oq0bU;
        r1.<init>(r8, r9);
        r0.setOnCancelListener(r1);
        r9 = r10[r2];
        r8.showDialog(r9);
        goto L_0x01ee;
    L_0x007c:
        r0 = 5;
        r4 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        if (r9 != r0) goto L_0x00a2;
    L_0x0081:
        r9 = r8.googleMap;
        if (r9 == 0) goto L_0x01ee;
    L_0x0085:
        r10 = new com.google.android.gms.maps.model.LatLng;
        r0 = r8.chatLocation;
        r0 = r0.geo_point;
        r1 = r0.lat;
        r5 = r0._long;
        r10.<init>(r1, r5);
        r0 = r8.googleMap;
        r0 = r0.getMaxZoomLevel();
        r0 = r0 - r4;
        r10 = com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(r10, r0);
        r9.animateCamera(r10);
        goto L_0x01ee;
    L_0x00a2:
        if (r10 != r3) goto L_0x00d3;
    L_0x00a4:
        r9 = r8.messageObject;
        if (r9 == 0) goto L_0x00d3;
    L_0x00a8:
        r9 = r9.isLiveLocation();
        if (r9 != 0) goto L_0x00d3;
    L_0x00ae:
        r9 = r8.googleMap;
        if (r9 == 0) goto L_0x01ee;
    L_0x00b2:
        r10 = new com.google.android.gms.maps.model.LatLng;
        r0 = r8.messageObject;
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = r0.geo;
        r1 = r0.lat;
        r5 = r0._long;
        r10.<init>(r1, r5);
        r0 = r8.googleMap;
        r0 = r0.getMaxZoomLevel();
        r0 = r0 - r4;
        r10 = com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(r10, r0);
        r9.animateCamera(r10);
        goto L_0x01ee;
    L_0x00d3:
        r9 = 2;
        if (r10 != r3) goto L_0x0138;
    L_0x00d6:
        r0 = r8.locationType;
        if (r0 == r9) goto L_0x0138;
    L_0x00da:
        r9 = r8.delegate;
        if (r9 == 0) goto L_0x01ee;
    L_0x00de:
        r9 = r8.userLocation;
        if (r9 == 0) goto L_0x01ee;
    L_0x00e2:
        r9 = new org.telegram.tgnet.TLRPC$TL_messageMediaGeo;
        r9.<init>();
        r10 = new org.telegram.tgnet.TLRPC$TL_geoPoint;
        r10.<init>();
        r9.geo = r10;
        r10 = r9.geo;
        r0 = r8.userLocation;
        r0 = r0.getLatitude();
        r0 = org.telegram.messenger.AndroidUtilities.fixLocationCoord(r0);
        r10.lat = r0;
        r10 = r9.geo;
        r0 = r8.userLocation;
        r0 = r0.getLongitude();
        r0 = org.telegram.messenger.AndroidUtilities.fixLocationCoord(r0);
        r10._long = r0;
        r10 = r8.parentFragment;
        if (r10 == 0) goto L_0x012c;
    L_0x010e:
        r10 = r10.isInScheduleMode();
        if (r10 == 0) goto L_0x012c;
    L_0x0114:
        r10 = r8.getParentActivity();
        r0 = r8.parentFragment;
        r0 = r0.getCurrentUser();
        r0 = org.telegram.messenger.UserObject.isUserSelf(r0);
        r1 = new org.telegram.ui.-$$Lambda$LocationActivity$S_gW1aUqJZbo1RA9hlk6cnI2yls;
        r1.<init>(r8, r9);
        org.telegram.ui.Components.AlertsCreator.createScheduleDatePickerDialog(r10, r0, r1);
        goto L_0x01ee;
    L_0x012c:
        r10 = r8.delegate;
        r0 = r8.locationType;
        r10.didSelectLocation(r9, r0, r3, r2);
        r8.finishFragment();
        goto L_0x01ee;
    L_0x0138:
        if (r10 != r9) goto L_0x013e;
    L_0x013a:
        r0 = r8.locationType;
        if (r0 == r3) goto L_0x014a;
    L_0x013e:
        if (r10 != r3) goto L_0x0144;
    L_0x0140:
        r0 = r8.locationType;
        if (r0 == r9) goto L_0x014a;
    L_0x0144:
        if (r10 != r1) goto L_0x019a;
    L_0x0146:
        r9 = r8.locationType;
        if (r9 != r1) goto L_0x019a;
    L_0x014a:
        r9 = r8.getLocationController();
        r0 = r8.dialogId;
        r9 = r9.isSharingLocation(r0);
        if (r9 == 0) goto L_0x0164;
    L_0x0156:
        r9 = r8.getLocationController();
        r0 = r8.dialogId;
        r9.removeSharingLocation(r0);
        r8.finishFragment();
        goto L_0x01ee;
    L_0x0164:
        r9 = r8.delegate;
        if (r9 == 0) goto L_0x0199;
    L_0x0168:
        r9 = r8.getParentActivity();
        if (r9 != 0) goto L_0x016f;
    L_0x016e:
        goto L_0x0199;
    L_0x016f:
        r9 = r8.myLocation;
        if (r9 == 0) goto L_0x01ee;
    L_0x0173:
        r9 = 0;
        r0 = r8.dialogId;
        r10 = (int) r0;
        if (r10 <= 0) goto L_0x0188;
    L_0x0179:
        r9 = r8.getMessagesController();
        r0 = r8.dialogId;
        r10 = (int) r0;
        r10 = java.lang.Integer.valueOf(r10);
        r9 = r9.getUser(r10);
    L_0x0188:
        r10 = r8.getParentActivity();
        r0 = new org.telegram.ui.-$$Lambda$LocationActivity$y-H_YdHAcjNzU40g5n2ffIy3l44;
        r0.<init>(r8);
        r9 = org.telegram.ui.Components.AlertsCreator.createLocationUpdateDialog(r10, r9, r0);
        r8.showDialog(r9);
        goto L_0x01ee;
    L_0x0199:
        return;
    L_0x019a:
        r9 = r8.adapter;
        r9 = r9.getItem(r10);
        r10 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r10 == 0) goto L_0x01d2;
    L_0x01a4:
        r10 = r8.parentFragment;
        if (r10 == 0) goto L_0x01c5;
    L_0x01a8:
        r10 = r10.isInScheduleMode();
        if (r10 == 0) goto L_0x01c5;
    L_0x01ae:
        r10 = r8.getParentActivity();
        r0 = r8.parentFragment;
        r0 = r0.getCurrentUser();
        r0 = org.telegram.messenger.UserObject.isUserSelf(r0);
        r1 = new org.telegram.ui.-$$Lambda$LocationActivity$-xlefUAlTwXwh9jaF5AoFfwPyZg;
        r1.<init>(r8, r9);
        org.telegram.ui.Components.AlertsCreator.createScheduleDatePickerDialog(r10, r0, r1);
        goto L_0x01ee;
    L_0x01c5:
        r10 = r8.delegate;
        r9 = (org.telegram.tgnet.TLRPC.TL_messageMediaVenue) r9;
        r0 = r8.locationType;
        r10.didSelectLocation(r9, r0, r3, r2);
        r8.finishFragment();
        goto L_0x01ee;
    L_0x01d2:
        r10 = r9 instanceof org.telegram.ui.LocationActivity.LiveLocation;
        if (r10 == 0) goto L_0x01ee;
    L_0x01d6:
        r9 = (org.telegram.ui.LocationActivity.LiveLocation) r9;
        r10 = r8.googleMap;
        r9 = r9.marker;
        r9 = r9.getPosition();
        r0 = r8.googleMap;
        r0 = r0.getMaxZoomLevel();
        r0 = r0 - r4;
        r9 = com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(r9, r0);
        r10.animateCamera(r9);
    L_0x01ee:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LocationActivity.lambda$createView$6$LocationActivity(android.view.View, int):void");
    }

    public /* synthetic */ void lambda$null$1$LocationActivity(AlertDialog[] alertDialogArr, TL_messageMediaVenue tL_messageMediaVenue, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$LocationActivity$JGVJcTpeqmcZpqQe4AImaLh0UqE(this, alertDialogArr, tL_messageMediaVenue));
    }

    public /* synthetic */ void lambda$null$0$LocationActivity(AlertDialog[] alertDialogArr, TL_messageMediaVenue tL_messageMediaVenue) {
        try {
            alertDialogArr[0].dismiss();
        } catch (Throwable unused) {
        }
        alertDialogArr[0] = null;
        this.delegate.didSelectLocation(tL_messageMediaVenue, 4, true, 0);
        finishFragment();
    }

    public /* synthetic */ void lambda$null$2$LocationActivity(int i, DialogInterface dialogInterface) {
        getConnectionsManager().cancelRequest(i, true);
    }

    public /* synthetic */ void lambda$null$3$LocationActivity(TL_messageMediaGeo tL_messageMediaGeo, boolean z, int i) {
        this.delegate.didSelectLocation(tL_messageMediaGeo, this.locationType, z, i);
        finishFragment();
    }

    public /* synthetic */ void lambda$null$4$LocationActivity(int i) {
        TL_messageMediaGeoLive tL_messageMediaGeoLive = new TL_messageMediaGeoLive();
        tL_messageMediaGeoLive.geo = new TL_geoPoint();
        tL_messageMediaGeoLive.geo.lat = AndroidUtilities.fixLocationCoord(this.myLocation.getLatitude());
        tL_messageMediaGeoLive.geo._long = AndroidUtilities.fixLocationCoord(this.myLocation.getLongitude());
        tL_messageMediaGeoLive.period = i;
        this.delegate.didSelectLocation(tL_messageMediaGeoLive, this.locationType, true, 0);
        finishFragment();
    }

    public /* synthetic */ void lambda$null$5$LocationActivity(Object obj, boolean z, int i) {
        this.delegate.didSelectLocation((TL_messageMediaVenue) obj, this.locationType, z, i);
        finishFragment();
    }

    public /* synthetic */ void lambda$createView$7$LocationActivity(ArrayList arrayList) {
        if (!(this.wasResults || arrayList.isEmpty())) {
            this.wasResults = true;
        }
        this.emptyView.showTextView();
    }

    public /* synthetic */ void lambda$createView$10$LocationActivity(MapView mapView) {
        try {
            mapView.onCreate(null);
        } catch (Exception unused) {
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$LocationActivity$d2e6Q_JsxG2BaEHYA1j6od8jEKw(this, mapView));
    }

    public /* synthetic */ void lambda$null$9$LocationActivity(MapView mapView) {
        if (this.mapView != null && getParentActivity() != null) {
            try {
                mapView.onCreate(null);
                MapsInitializer.initialize(ApplicationLoader.applicationContext);
                this.mapView.getMapAsync(new -$$Lambda$LocationActivity$nYJv5cWPYZ4MasIiIaWmUb5tLGQ(this));
                this.mapsInitialized = true;
                if (this.onResumeCalled) {
                    this.mapView.onResume();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public /* synthetic */ void lambda$null$8$LocationActivity(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
        onMapInit();
    }

    public /* synthetic */ void lambda$createView$12$LocationActivity(View view, int i) {
        TL_messageMediaVenue item = this.searchAdapter.getItem(i);
        if (item != null && this.delegate != null) {
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity == null || !chatActivity.isInScheduleMode()) {
                this.delegate.didSelectLocation(item, this.locationType, true, 0);
                finishFragment();
                return;
            }
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), UserObject.isUserSelf(this.parentFragment.getCurrentUser()), new -$$Lambda$LocationActivity$0Eo_xWGFn0CLASSNAMEMjMoPV41J-G1B4(this, item));
        }
    }

    public /* synthetic */ void lambda$null$11$LocationActivity(TL_messageMediaVenue tL_messageMediaVenue, boolean z, int i) {
        this.delegate.didSelectLocation(tL_messageMediaVenue, this.locationType, z, i);
        finishFragment();
    }

    public /* synthetic */ void lambda$createView$13$LocationActivity(View view) {
        if (VERSION.SDK_INT >= 23) {
            Activity parentActivity = getParentActivity();
            if (!(parentActivity == null || parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0)) {
                showPermissionAlert(true);
                return;
            }
        }
        if (this.myLocation != null) {
            try {
                Intent intent;
                String str = "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f";
                String str2 = "android.intent.action.VIEW";
                if (this.messageObject != null) {
                    intent = new Intent(str2, Uri.parse(String.format(Locale.US, str, new Object[]{Double.valueOf(r12.getLatitude()), Double.valueOf(this.myLocation.getLongitude()), Double.valueOf(this.messageObject.messageOwner.media.geo.lat), Double.valueOf(this.messageObject.messageOwner.media.geo._long)})));
                } else {
                    intent = new Intent(str2, Uri.parse(String.format(Locale.US, str, new Object[]{Double.valueOf(r12.getLatitude()), Double.valueOf(this.myLocation.getLongitude()), Double.valueOf(this.chatLocation.geo_point.lat), Double.valueOf(this.chatLocation.geo_point._long)})));
                }
                getParentActivity().startActivity(intent);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public /* synthetic */ void lambda$createView$14$LocationActivity(View view) {
        if (VERSION.SDK_INT >= 23) {
            Activity parentActivity = getParentActivity();
            if (!(parentActivity == null || parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0)) {
                showPermissionAlert(false);
                return;
            }
        }
        if (this.messageObject != null || this.chatLocation != null) {
            Location location = this.myLocation;
            if (location != null) {
                GoogleMap googleMap = this.googleMap;
                if (googleMap != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), this.myLocation.getLongitude()), this.googleMap.getMaxZoomLevel() - 4.0f));
                }
            }
        } else if (!(this.myLocation == null || this.googleMap == null)) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(200);
            animatorSet.play(ObjectAnimator.ofFloat(this.locationButton, View.ALPHA, new float[]{0.0f}));
            animatorSet.start();
            this.adapter.setCustomLocation(null);
            this.userLocationMoved = false;
            this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude())));
        }
    }

    private Bitmap createUserBitmap(LiveLocation liveLocation) {
        Bitmap createBitmap;
        Throwable th;
        try {
            TLObject tLObject = (liveLocation.user == null || liveLocation.user.photo == null) ? (liveLocation.chat == null || liveLocation.chat.photo == null) ? null : liveLocation.chat.photo.photo_small : liveLocation.user.photo.photo_small;
            createBitmap = Bitmap.createBitmap(AndroidUtilities.dp(62.0f), AndroidUtilities.dp(76.0f), Config.ARGB_8888);
            try {
                createBitmap.eraseColor(0);
                Canvas canvas = new Canvas(createBitmap);
                Drawable drawable = ApplicationLoader.applicationContext.getResources().getDrawable(NUM);
                drawable.setBounds(0, 0, AndroidUtilities.dp(62.0f), AndroidUtilities.dp(76.0f));
                drawable.draw(canvas);
                Paint paint = new Paint(1);
                RectF rectF = new RectF();
                canvas.save();
                if (tLObject != null) {
                    Bitmap decodeFile = BitmapFactory.decodeFile(FileLoader.getPathToAttach(tLObject, true).toString());
                    if (decodeFile != null) {
                        BitmapShader bitmapShader = new BitmapShader(decodeFile, TileMode.CLAMP, TileMode.CLAMP);
                        Matrix matrix = new Matrix();
                        float dp = ((float) AndroidUtilities.dp(52.0f)) / ((float) decodeFile.getWidth());
                        matrix.postTranslate((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f));
                        matrix.postScale(dp, dp);
                        paint.setShader(bitmapShader);
                        bitmapShader.setLocalMatrix(matrix);
                        rectF.set((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(57.0f), (float) AndroidUtilities.dp(57.0f));
                        canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(26.0f), (float) AndroidUtilities.dp(26.0f), paint);
                    }
                } else {
                    AvatarDrawable avatarDrawable = new AvatarDrawable();
                    if (liveLocation.user != null) {
                        avatarDrawable.setInfo(liveLocation.user);
                    } else if (liveLocation.chat != null) {
                        avatarDrawable.setInfo(liveLocation.chat);
                    }
                    canvas.translate((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f));
                    avatarDrawable.setBounds(0, 0, AndroidUtilities.dp(52.2f), AndroidUtilities.dp(52.2f));
                    avatarDrawable.draw(canvas);
                }
                canvas.restore();
                try {
                    canvas.setBitmap(null);
                } catch (Exception unused) {
                }
            } catch (Throwable th2) {
                th = th2;
                FileLog.e(th);
                return createBitmap;
            }
        } catch (Throwable th3) {
            th = th3;
            createBitmap = null;
            FileLog.e(th);
            return createBitmap;
        }
        return createBitmap;
    }

    private int getMessageId(Message message) {
        int i = message.from_id;
        if (i != 0) {
            return i;
        }
        return (int) MessageObject.getDialogId(message);
    }

    private LiveLocation addUserMarker(Message message) {
        GeoPoint geoPoint = message.media.geo;
        LatLng latLng = new LatLng(geoPoint.lat, geoPoint._long);
        LiveLocation liveLocation = (LiveLocation) this.markersMap.get(message.from_id);
        if (liveLocation == null) {
            liveLocation = new LiveLocation();
            liveLocation.object = message;
            if (liveLocation.object.from_id != 0) {
                liveLocation.user = getMessagesController().getUser(Integer.valueOf(liveLocation.object.from_id));
                liveLocation.id = liveLocation.object.from_id;
            } else {
                int dialogId = (int) MessageObject.getDialogId(message);
                if (dialogId > 0) {
                    liveLocation.user = getMessagesController().getUser(Integer.valueOf(dialogId));
                    liveLocation.id = dialogId;
                } else {
                    liveLocation.chat = getMessagesController().getChat(Integer.valueOf(-dialogId));
                    liveLocation.id = dialogId;
                }
            }
            try {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                Bitmap createUserBitmap = createUserBitmap(liveLocation);
                if (createUserBitmap != null) {
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createUserBitmap));
                    markerOptions.anchor(0.5f, 0.907f);
                    liveLocation.marker = this.googleMap.addMarker(markerOptions);
                    this.markers.add(liveLocation);
                    this.markersMap.put(liveLocation.id, liveLocation);
                    SharingLocationInfo sharingLocationInfo = getLocationController().getSharingLocationInfo(this.dialogId);
                    if (liveLocation.id == getUserConfig().getClientUserId() && sharingLocationInfo != null && liveLocation.object.id == sharingLocationInfo.mid && this.myLocation != null) {
                        liveLocation.marker.setPosition(new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude()));
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else {
            liveLocation.object = message;
            liveLocation.marker.setPosition(latLng);
        }
        return liveLocation;
    }

    private LiveLocation addUserMarker(TL_channelLocation tL_channelLocation) {
        GeoPoint geoPoint = tL_channelLocation.geo_point;
        LatLng latLng = new LatLng(geoPoint.lat, geoPoint._long);
        LiveLocation liveLocation = new LiveLocation();
        int i = (int) this.dialogId;
        if (i > 0) {
            liveLocation.user = getMessagesController().getUser(Integer.valueOf(i));
            liveLocation.id = i;
        } else {
            liveLocation.chat = getMessagesController().getChat(Integer.valueOf(-i));
            liveLocation.id = i;
        }
        try {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            Bitmap createUserBitmap = createUserBitmap(liveLocation);
            if (createUserBitmap != null) {
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createUserBitmap));
                markerOptions.anchor(0.5f, 0.907f);
                liveLocation.marker = this.googleMap.addMarker(markerOptions);
                this.markers.add(liveLocation);
                this.markersMap.put(liveLocation.id, liveLocation);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return liveLocation;
    }

    private void onMapInit() {
        if (this.googleMap != null) {
            TL_channelLocation tL_channelLocation = this.chatLocation;
            if (tL_channelLocation != null) {
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(addUserMarker(tL_channelLocation).marker.getPosition(), this.googleMap.getMaxZoomLevel() - 4.0f));
            } else {
                MessageObject messageObject = this.messageObject;
                if (messageObject == null) {
                    this.userLocation = new Location("network");
                    tL_channelLocation = this.initialLocation;
                    if (tL_channelLocation != null) {
                        GeoPoint geoPoint = tL_channelLocation.geo_point;
                        LatLng latLng = new LatLng(geoPoint.lat, geoPoint._long);
                        GoogleMap googleMap = this.googleMap;
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, googleMap.getMaxZoomLevel() - 4.0f));
                        this.userLocation.setLatitude(this.initialLocation.geo_point.lat);
                        this.userLocation.setLongitude(this.initialLocation.geo_point._long);
                        this.adapter.setCustomLocation(this.userLocation);
                    } else {
                        this.userLocation.setLatitude(20.659322d);
                        this.userLocation.setLongitude(-11.40625d);
                    }
                } else if (messageObject.isLiveLocation()) {
                    LiveLocation addUserMarker = addUserMarker(this.messageObject.messageOwner);
                    if (!getRecentLocations()) {
                        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(addUserMarker.marker.getPosition(), this.googleMap.getMaxZoomLevel() - 4.0f));
                    }
                } else {
                    LatLng latLng2 = new LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude());
                    try {
                        GoogleMap googleMap2 = this.googleMap;
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng2);
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(NUM));
                        googleMap2.addMarker(markerOptions);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng2, this.googleMap.getMaxZoomLevel() - 4.0f));
                    this.firstFocus = false;
                    getRecentLocations();
                }
            }
            try {
                this.googleMap.setMyLocationEnabled(true);
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            this.googleMap.getUiSettings().setZoomControlsEnabled(false);
            this.googleMap.getUiSettings().setCompassEnabled(false);
            this.googleMap.setOnMyLocationChangeListener(new -$$Lambda$LocationActivity$n5ruFpUEwgnDDJzhX4rkCKjDMXw(this));
            Location lastLocation = getLastLocation();
            this.myLocation = lastLocation;
            positionMarker(lastLocation);
            if (this.checkGpsEnabled && getParentActivity() != null) {
                this.checkGpsEnabled = false;
                if (getParentActivity().getPackageManager().hasSystemFeature("android.hardware.location.gps")) {
                    try {
                        if (!((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isProviderEnabled("gps")) {
                            Builder builder = new Builder(getParentActivity());
                            builder.setTitle(LocaleController.getString("GpsDisabledAlertTitle", NUM));
                            builder.setMessage(LocaleController.getString("GpsDisabledAlertText", NUM));
                            builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", NUM), new -$$Lambda$LocationActivity$THwaiRnXnH4pqYDHVBK3BEyn4Yw(this));
                            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                            showDialog(builder.create());
                        }
                    } catch (Exception e22) {
                        FileLog.e(e22);
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$onMapInit$15$LocationActivity(Location location) {
        positionMarker(location);
        getLocationController().setGoogleMapLocation(location, this.isFirstLocation);
        this.isFirstLocation = false;
    }

    public /* synthetic */ void lambda$onMapInit$16$LocationActivity(DialogInterface dialogInterface, int i) {
        if (getParentActivity() != null) {
            try {
                getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            } catch (Exception unused) {
            }
        }
    }

    private void showPermissionAlert(boolean z) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            if (z) {
                builder.setMessage(LocaleController.getString("PermissionNoLocationPosition", NUM));
            } else {
                builder.setMessage(LocaleController.getString("PermissionNoLocation", NUM));
            }
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new -$$Lambda$LocationActivity$__Y9YObwAbB4mFrG8nu118LJw0g(this));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$showPermissionAlert$17$LocationActivity(DialogInterface dialogInterface, int i) {
        if (getParentActivity() != null) {
            try {
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("package:");
                stringBuilder.append(ApplicationLoader.applicationContext.getPackageName());
                intent.setData(Uri.parse(stringBuilder.toString()));
                getParentActivity().startActivity(intent);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            try {
                if (this.mapView.getParent() instanceof ViewGroup) {
                    ((ViewGroup) this.mapView.getParent()).removeView(this.mapView);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            FrameLayout frameLayout = this.mapViewClip;
            if (frameLayout != null) {
                frameLayout.addView(this.mapView, 0, LayoutHelper.createFrame(-1, this.overScrollHeight + AndroidUtilities.dp(10.0f), 51));
                updateClipView(this.layoutManager.findFirstVisibleItemPosition());
                return;
            }
            View view = this.fragmentView;
            if (view != null) {
                ((FrameLayout) view).addView(this.mapView, 0, LayoutHelper.createFrame(-1, -1, 51));
            }
        }
    }

    private void updateClipView(int i) {
        if (i != -1) {
            View childAt = this.listView.getChildAt(0);
            if (childAt != null) {
                int i2;
                if (i == 0) {
                    i = childAt.getTop();
                    i2 = this.overScrollHeight + (i < 0 ? i : 0);
                } else {
                    i = 0;
                    i2 = 0;
                }
                if (((LayoutParams) this.mapViewClip.getLayoutParams()) != null) {
                    if (i2 <= 0) {
                        if (this.mapView.getVisibility() == 0) {
                            this.mapView.setVisibility(4);
                            this.mapViewClip.setVisibility(4);
                        }
                    } else if (this.mapView.getVisibility() == 4) {
                        this.mapView.setVisibility(0);
                        this.mapViewClip.setVisibility(0);
                    }
                    this.mapViewClip.setTranslationY((float) Math.min(0, i));
                    int i3 = -i;
                    this.mapView.setTranslationY((float) Math.max(0, i3 / 2));
                    View view = this.markerImageView;
                    if (view != null) {
                        i3 = (i3 - AndroidUtilities.dp(view.getTag() == null ? 48.0f : 69.0f)) + (i2 / 2);
                        this.markerTop = i3;
                        view.setTranslationY((float) i3);
                    }
                    ImageView imageView = this.routeButton;
                    if (imageView != null) {
                        imageView.setTranslationY((float) i);
                    }
                    LayoutParams layoutParams = (LayoutParams) this.mapView.getLayoutParams();
                    if (!(layoutParams == null || layoutParams.height == this.overScrollHeight + AndroidUtilities.dp(10.0f))) {
                        layoutParams.height = this.overScrollHeight + AndroidUtilities.dp(10.0f);
                        GoogleMap googleMap = this.googleMap;
                        if (googleMap != null) {
                            googleMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
                        }
                        this.mapView.setLayoutParams(layoutParams);
                    }
                }
            }
        }
    }

    private void fixLayoutInternal(boolean z) {
        if (this.listView != null) {
            int currentActionBarHeight = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
            int measuredHeight = this.fragmentView.getMeasuredHeight();
            if (measuredHeight != 0) {
                this.overScrollHeight = (measuredHeight - AndroidUtilities.dp(66.0f)) - currentActionBarHeight;
                LayoutParams layoutParams = (LayoutParams) this.listView.getLayoutParams();
                layoutParams.topMargin = currentActionBarHeight;
                this.listView.setLayoutParams(layoutParams);
                layoutParams = (LayoutParams) this.mapViewClip.getLayoutParams();
                layoutParams.topMargin = currentActionBarHeight;
                layoutParams.height = this.overScrollHeight;
                this.mapViewClip.setLayoutParams(layoutParams);
                RecyclerListView recyclerListView = this.searchListView;
                if (recyclerListView != null) {
                    layoutParams = (LayoutParams) recyclerListView.getLayoutParams();
                    layoutParams.topMargin = currentActionBarHeight;
                    this.searchListView.setLayoutParams(layoutParams);
                }
                this.adapter.setOverScrollHeight(this.overScrollHeight);
                LayoutParams layoutParams2 = (LayoutParams) this.mapView.getLayoutParams();
                if (layoutParams2 != null) {
                    layoutParams2.height = this.overScrollHeight + AndroidUtilities.dp(10.0f);
                    GoogleMap googleMap = this.googleMap;
                    if (googleMap != null) {
                        googleMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
                    }
                    this.mapView.setLayoutParams(layoutParams2);
                }
                this.adapter.notifyDataSetChanged();
                if (z) {
                    LinearLayoutManager linearLayoutManager = this.layoutManager;
                    measuredHeight = this.locationType;
                    measuredHeight = (measuredHeight == 1 || measuredHeight == 2) ? 66 : 0;
                    linearLayoutManager.scrollToPositionWithOffset(0, -AndroidUtilities.dp((float) (32 + measuredHeight)));
                    updateClipView(this.layoutManager.findFirstVisibleItemPosition());
                    this.listView.post(new -$$Lambda$LocationActivity$jAl7_qjp7LboFShPHexNbuYz8OQ(this));
                } else {
                    updateClipView(this.layoutManager.findFirstVisibleItemPosition());
                }
            }
        }
    }

    public /* synthetic */ void lambda$fixLayoutInternal$18$LocationActivity() {
        LinearLayoutManager linearLayoutManager = this.layoutManager;
        int i = this.locationType;
        i = (i == 1 || i == 2) ? 66 : 0;
        linearLayoutManager.scrollToPositionWithOffset(0, -AndroidUtilities.dp((float) (32 + i)));
        updateClipView(this.layoutManager.findFirstVisibleItemPosition());
    }

    private Location getLastLocation() {
        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        List providers = locationManager.getProviders(true);
        Location location = null;
        for (int size = providers.size() - 1; size >= 0; size--) {
            location = locationManager.getLastKnownLocation((String) providers.get(size));
            if (location != null) {
                break;
            }
        }
        return location;
    }

    private void positionMarker(Location location) {
        if (location != null) {
            this.myLocation = new Location(location);
            LiveLocation liveLocation = (LiveLocation) this.markersMap.get(getUserConfig().getClientUserId());
            SharingLocationInfo sharingLocationInfo = getLocationController().getSharingLocationInfo(this.dialogId);
            if (!(liveLocation == null || sharingLocationInfo == null || liveLocation.object.id != sharingLocationInfo.mid)) {
                liveLocation.marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            }
            if (this.messageObject == null && this.chatLocation == null && this.googleMap != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                LocationActivityAdapter locationActivityAdapter = this.adapter;
                if (locationActivityAdapter != null) {
                    if (locationActivityAdapter.isPulledUp()) {
                        this.adapter.searchPlacesWithQuery(null, this.myLocation, true);
                    }
                    this.adapter.setGpsLocation(this.myLocation);
                }
                if (!this.userLocationMoved) {
                    this.userLocation = new Location(location);
                    if (this.firstWas) {
                        this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    } else {
                        this.firstWas = true;
                        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, this.googleMap.getMaxZoomLevel() - 4.0f));
                    }
                }
            } else {
                this.adapter.setGpsLocation(this.myLocation);
            }
        }
    }

    public void setMessageObject(MessageObject messageObject) {
        this.messageObject = messageObject;
        this.dialogId = this.messageObject.getDialogId();
    }

    public void setChatLocation(int i, TL_channelLocation tL_channelLocation) {
        this.dialogId = (long) (-i);
        this.chatLocation = tL_channelLocation;
    }

    public void setDialogId(long j) {
        this.dialogId = j;
    }

    public void setInitialLocation(TL_channelLocation tL_channelLocation) {
        this.initialLocation = tL_channelLocation;
    }

    private void fetchRecentLocations(ArrayList<Message> arrayList) {
        LatLngBounds.Builder builder = this.firstFocus ? new LatLngBounds.Builder() : null;
        int currentTime = getConnectionsManager().getCurrentTime();
        for (int i = 0; i < arrayList.size(); i++) {
            Message message = (Message) arrayList.get(i);
            int i2 = message.date;
            MessageMedia messageMedia = message.media;
            if (i2 + messageMedia.period > currentTime) {
                if (builder != null) {
                    GeoPoint geoPoint = messageMedia.geo;
                    builder.include(new LatLng(geoPoint.lat, geoPoint._long));
                }
                addUserMarker(message);
            }
        }
        if (builder != null) {
            this.firstFocus = false;
            this.adapter.setLiveLocations(this.markers);
            if (this.messageObject.isLiveLocation()) {
                try {
                    LatLngBounds build = builder.build();
                    if (arrayList.size() > 1) {
                        try {
                            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(build, AndroidUtilities.dp(60.0f)));
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                } catch (Exception unused) {
                }
            }
        }
    }

    private boolean getRecentLocations() {
        ArrayList arrayList = (ArrayList) getLocationController().locationsCache.get(this.messageObject.getDialogId());
        if (arrayList == null || !arrayList.isEmpty()) {
            arrayList = null;
        } else {
            fetchRecentLocations(arrayList);
        }
        int i = (int) this.dialogId;
        boolean z = false;
        if (i < 0) {
            Chat chat = getMessagesController().getChat(Integer.valueOf(-i));
            if (ChatObject.isChannel(chat) && !chat.megagroup) {
                return false;
            }
        }
        TL_messages_getRecentLocations tL_messages_getRecentLocations = new TL_messages_getRecentLocations();
        long dialogId = this.messageObject.getDialogId();
        tL_messages_getRecentLocations.peer = getMessagesController().getInputPeer((int) dialogId);
        tL_messages_getRecentLocations.limit = 100;
        getConnectionsManager().sendRequest(tL_messages_getRecentLocations, new -$$Lambda$LocationActivity$10_gJYtZI1WBT9zW2NISAwbjMSQ(this, dialogId));
        if (arrayList != null) {
            z = true;
        }
        return z;
    }

    public /* synthetic */ void lambda$getRecentLocations$20$LocationActivity(long j, TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LocationActivity$fv_kzLlSvHXBWIPxMmguf3j5Xvg(this, tLObject, j));
        }
    }

    public /* synthetic */ void lambda$null$19$LocationActivity(TLObject tLObject, long j) {
        if (this.googleMap != null) {
            messages_Messages messages_messages = (messages_Messages) tLObject;
            int i = 0;
            while (i < messages_messages.messages.size()) {
                if (!(((Message) messages_messages.messages.get(i)).media instanceof TL_messageMediaGeoLive)) {
                    messages_messages.messages.remove(i);
                    i--;
                }
                i++;
            }
            getMessagesStorage().putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
            getMessagesController().putUsers(messages_messages.users, false);
            getMessagesController().putChats(messages_messages.chats, false);
            getLocationController().locationsCache.put(j, messages_messages.messages);
            getNotificationCenter().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(j));
            fetchRecentLocations(messages_messages.messages);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (i == NotificationCenter.locationPermissionGranted) {
            GoogleMap googleMap = this.googleMap;
            if (googleMap != null) {
                try {
                    googleMap.setMyLocationEnabled(true);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        } else {
            int i3 = 0;
            LocationActivityAdapter locationActivityAdapter;
            if (i == NotificationCenter.didReceiveNewMessages) {
                if (!((Boolean) objArr[2]).booleanValue() && ((Long) objArr[0]).longValue() == this.dialogId && this.messageObject != null) {
                    ArrayList arrayList = (ArrayList) objArr[1];
                    Object obj = null;
                    while (i3 < arrayList.size()) {
                        MessageObject messageObject = (MessageObject) arrayList.get(i3);
                        if (messageObject.isLiveLocation()) {
                            addUserMarker(messageObject.messageOwner);
                            obj = 1;
                        }
                        i3++;
                    }
                    if (obj != null) {
                        locationActivityAdapter = this.adapter;
                        if (locationActivityAdapter != null) {
                            locationActivityAdapter.setLiveLocations(this.markers);
                        }
                    }
                }
            } else if (i == NotificationCenter.replaceMessagesObjects) {
                long longValue = ((Long) objArr[0]).longValue();
                if (longValue == this.dialogId && this.messageObject != null) {
                    ArrayList arrayList2 = (ArrayList) objArr[1];
                    Object obj2 = null;
                    while (i3 < arrayList2.size()) {
                        MessageObject messageObject2 = (MessageObject) arrayList2.get(i3);
                        if (messageObject2.isLiveLocation()) {
                            LiveLocation liveLocation = (LiveLocation) this.markersMap.get(getMessageId(messageObject2.messageOwner));
                            if (liveLocation != null) {
                                SharingLocationInfo sharingLocationInfo = getLocationController().getSharingLocationInfo(longValue);
                                if (sharingLocationInfo == null || sharingLocationInfo.mid != messageObject2.getId()) {
                                    Marker marker = liveLocation.marker;
                                    GeoPoint geoPoint = messageObject2.messageOwner.media.geo;
                                    marker.setPosition(new LatLng(geoPoint.lat, geoPoint._long));
                                }
                                obj2 = 1;
                            }
                        }
                        i3++;
                    }
                    if (obj2 != null) {
                        locationActivityAdapter = this.adapter;
                        if (locationActivityAdapter != null) {
                            locationActivityAdapter.updateLiveLocations();
                        }
                    }
                }
            }
        }
    }

    public void onPause() {
        super.onPause();
        MapView mapView = this.mapView;
        if (mapView != null && this.mapsInitialized) {
            try {
                mapView.onPause();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        this.onResumeCalled = false;
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        MapView mapView = this.mapView;
        if (mapView != null && this.mapsInitialized) {
            try {
                mapView.onResume();
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
        this.onResumeCalled = true;
        GoogleMap googleMap = this.googleMap;
        if (googleMap != null) {
            try {
                googleMap.setMyLocationEnabled(true);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        fixLayoutInternal(true);
        if (this.checkPermission && VERSION.SDK_INT >= 23) {
            Activity parentActivity = getParentActivity();
            if (parentActivity != null) {
                this.checkPermission = false;
                if (parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
                    parentActivity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
                }
            }
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        MapView mapView = this.mapView;
        if (mapView != null && this.mapsInitialized) {
            mapView.onLowMemory();
        }
    }

    public void setDelegate(LocationActivityDelegate locationActivityDelegate) {
        this.delegate = locationActivityDelegate;
    }

    public void setChatActivity(ChatActivity chatActivity) {
        this.parentFragment = chatActivity;
    }

    private void updateSearchInterface() {
        LocationActivityAdapter locationActivityAdapter = this.adapter;
        if (locationActivityAdapter != null) {
            locationActivityAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        -$$Lambda$LocationActivity$WwhCA7YBxi7IkAuDR0iGxPPbxvQ -__lambda_locationactivity_wwhca7ybxi7ikaudr0igxppbxvq = -$$Lambda$LocationActivity$WwhCA7YBxi7IkAuDR0iGxPPbxvQ.INSTANCE;
        r10 = new ThemeDescription[52];
        r10[12] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        r10[13] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
        r10[14] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle");
        r10[15] = new ThemeDescription(this.locationButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "profile_actionIcon");
        r10[16] = new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "profile_actionBackground");
        r10[17] = new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "profile_actionPressedBackground");
        r10[18] = new ThemeDescription(this.routeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chats_actionIcon");
        r10[19] = new ThemeDescription(this.routeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "chats_actionBackground");
        r10[20] = new ThemeDescription(this.routeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "chats_actionPressedBackground");
        View view = this.listView;
        Class[] clsArr = new Class[]{GraySectionCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        r10[21] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "key_graySectionText");
        r10[22] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, "graySection");
        -$$Lambda$LocationActivity$WwhCA7YBxi7IkAuDR0iGxPPbxvQ -__lambda_locationactivity_wwhca7ybxi7ikaudr0igxppbxvq2 = -__lambda_locationactivity_wwhca7ybxi7ikaudr0igxppbxvq;
        r10[23] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.avatar_savedDrawable}, -__lambda_locationactivity_wwhca7ybxi7ikaudr0igxppbxvq2, "avatar_text");
        r10[24] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_wwhca7ybxi7ikaudr0igxppbxvq2, "avatar_backgroundRed");
        r10[25] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_wwhca7ybxi7ikaudr0igxppbxvq2, "avatar_backgroundOrange");
        r10[26] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_wwhca7ybxi7ikaudr0igxppbxvq2, "avatar_backgroundViolet");
        r10[27] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_wwhca7ybxi7ikaudr0igxppbxvq2, "avatar_backgroundGreen");
        r10[28] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_wwhca7ybxi7ikaudr0igxppbxvq2, "avatar_backgroundCyan");
        r10[29] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_wwhca7ybxi7ikaudr0igxppbxvq2, "avatar_backgroundBlue");
        r10[30] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_wwhca7ybxi7ikaudr0igxppbxvq2, "avatar_backgroundPink");
        r10[31] = new ThemeDescription(null, 0, null, null, null, null, "location_liveLocationProgress");
        r10[32] = new ThemeDescription(null, 0, null, null, null, null, "location_placeLocationBackground");
        r10[33] = new ThemeDescription(null, 0, null, null, null, null, "dialog_liveLocationProgress");
        view = this.listView;
        int i = ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG;
        clsArr = new Class[]{SendLocationCell.class};
        strArr = new String[1];
        strArr[0] = "imageView";
        r10[34] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "location_sendLocationIcon");
        r10[35] = new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, null, null, null, "location_sendLiveLocationIcon");
        r10[36] = new ThemeDescription(this.listView, (ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE) | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, null, null, null, "location_sendLocationBackground");
        r10[37] = new ThemeDescription(this.listView, (ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE) | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, null, null, null, "location_sendLiveLocationBackground");
        r10[38] = new ThemeDescription(this.listView, 0, new Class[]{SendLocationCell.class}, new String[]{"accurateTextView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        view = this.listView;
        i = ThemeDescription.FLAG_CHECKTAG;
        clsArr = new Class[]{SendLocationCell.class};
        strArr = new String[1];
        strArr[0] = "titleTextView";
        r10[39] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteRedText2");
        r10[40] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"titleTextView"}, null, null, null, "windowBackgroundWhiteBlueText7");
        r10[41] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LocationCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        view = this.listView;
        clsArr = new Class[]{LocationCell.class};
        strArr = new String[1];
        strArr[0] = "nameTextView";
        r10[42] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        clsArr = new Class[]{LocationCell.class};
        strArr = new String[1];
        strArr[0] = "addressTextView";
        r10[43] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayText3");
        r10[44] = new ThemeDescription(this.searchListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LocationCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        r10[45] = new ThemeDescription(this.searchListView, 0, new Class[]{LocationCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r10[46] = new ThemeDescription(this.searchListView, 0, new Class[]{LocationCell.class}, new String[]{"addressTextView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        r10[47] = new ThemeDescription(this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"progressBar"}, null, null, null, "progressCircle");
        r10[48] = new ThemeDescription(this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        r10[49] = new ThemeDescription(this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        r10[50] = new ThemeDescription(this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        r10[51] = new ThemeDescription(this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView2"}, null, null, null, "windowBackgroundWhiteGrayText3");
        return r10;
    }
}
