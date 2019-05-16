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
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.GeoPoint;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_geoPoint;
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
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MapPlaceholderDrawable;
import org.telegram.ui.Components.RecyclerListView;

public class LocationActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int map_list_menu_hybrid = 4;
    private static final int map_list_menu_map = 2;
    private static final int map_list_menu_satellite = 3;
    private static final int share = 1;
    private LocationActivityAdapter adapter;
    private AnimatorSet animatorSet;
    private AvatarDrawable avatarDrawable;
    private boolean checkGpsEnabled = true;
    private boolean checkPermission = true;
    private CircleOptions circleOptions;
    private LocationActivityDelegate delegate;
    private long dialogId;
    private EmptyTextProgressView emptyView;
    private boolean firstFocus = true;
    private boolean firstWas = false;
    private GoogleMap googleMap;
    private boolean isFirstLocation = true;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private int liveLocationType;
    private ImageView locationButton;
    private MapView mapView;
    private FrameLayout mapViewClip;
    private boolean mapsInitialized;
    private ImageView markerImageView;
    private int markerTop;
    private ImageView markerXImageView;
    private ArrayList<LiveLocation> markers = new ArrayList();
    private SparseArray<LiveLocation> markersMap = new SparseArray();
    private MessageObject messageObject;
    private Location myLocation;
    private boolean onResumeCalled;
    private ActionBarMenuItem otherItem;
    private int overScrollHeight = ((AndroidUtilities.displaySize.x - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(66.0f));
    private ImageView routeButton;
    private LocationActivitySearchAdapter searchAdapter;
    private RecyclerListView searchListView;
    private boolean searchWas;
    private boolean searching;
    private Runnable updateRunnable;
    private Location userLocation;
    private boolean userLocationMoved = false;
    private boolean wasResults;

    public class LiveLocation {
        public Chat chat;
        public int id;
        public Marker marker;
        public Message object;
        public User user;
    }

    public interface LocationActivityDelegate {
        void didSelectLocation(MessageMedia messageMedia, int i);
    }

    static /* synthetic */ void lambda$getThemeDescriptions$15() {
    }

    public LocationActivity(int i) {
        this.liveLocationType = i;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.swipeBackEnabled = false;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.locationPermissionGranted);
        MessageObject messageObject = this.messageObject;
        if (messageObject != null && messageObject.isLiveLocation()) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didReceiveNewMessages);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagesDeleted);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.replaceMessagesObjects);
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.locationPermissionGranted);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didReceiveNewMessages);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.replaceMessagesObjects);
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
        MessageObject messageObject = this.messageObject;
        if (messageObject == null) {
            this.actionBar.setTitle(LocaleController.getString("ShareLocation", NUM));
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
        String str2 = "translationZ";
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.locationButton, str2, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.locationButton, str2, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.locationButton.setStateListAnimator(stateListAnimator);
            this.locationButton.setOutlineProvider(new ViewOutlineProvider() {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        if (this.messageObject != null) {
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
        LocationActivityAdapter locationActivityAdapter2 = new LocationActivityAdapter(context2, this.liveLocationType, this.dialogId);
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
                        if (i2 > 0 && !LocationActivity.this.adapter.isPulledUp()) {
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
        this.listView.setOnItemClickListener(new -$$Lambda$LocationActivity$hR-6D0AqpgGzQVaKwSZDSV7l5U0(this));
        this.adapter.setDelegate(this.dialogId, new -$$Lambda$LocationActivity$1r8OhsfCqopqgQX3qFh-nzBXEt8(this));
        this.adapter.setOverScrollHeight(this.overScrollHeight);
        frameLayout.addView(this.mapViewClip, LayoutHelper.createFrame(-1, -1, 51));
        this.mapView = new MapView(context2) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (LocationActivity.this.messageObject == null) {
                    AnimatorSet access$1600;
                    String str = "translationY";
                    String str2 = "alpha";
                    Animator[] animatorArr;
                    if (motionEvent.getAction() == 0) {
                        if (LocationActivity.this.animatorSet != null) {
                            LocationActivity.this.animatorSet.cancel();
                        }
                        LocationActivity.this.animatorSet = new AnimatorSet();
                        LocationActivity.this.animatorSet.setDuration(200);
                        access$1600 = LocationActivity.this.animatorSet;
                        animatorArr = new Animator[2];
                        animatorArr[0] = ObjectAnimator.ofFloat(LocationActivity.this.markerImageView, str, new float[]{(float) (LocationActivity.this.markerTop + (-AndroidUtilities.dp(10.0f)))});
                        animatorArr[1] = ObjectAnimator.ofFloat(LocationActivity.this.markerXImageView, str2, new float[]{1.0f});
                        access$1600.playTogether(animatorArr);
                        LocationActivity.this.animatorSet.start();
                    } else if (motionEvent.getAction() == 1) {
                        if (LocationActivity.this.animatorSet != null) {
                            LocationActivity.this.animatorSet.cancel();
                        }
                        LocationActivity.this.animatorSet = new AnimatorSet();
                        LocationActivity.this.animatorSet.setDuration(200);
                        access$1600 = LocationActivity.this.animatorSet;
                        animatorArr = new Animator[2];
                        animatorArr[0] = ObjectAnimator.ofFloat(LocationActivity.this.markerImageView, str, new float[]{(float) LocationActivity.this.markerTop});
                        animatorArr[1] = ObjectAnimator.ofFloat(LocationActivity.this.markerXImageView, str2, new float[]{0.0f});
                        access$1600.playTogether(animatorArr);
                        LocationActivity.this.animatorSet.start();
                    }
                    if (motionEvent.getAction() == 2) {
                        if (!LocationActivity.this.userLocationMoved) {
                            access$1600 = new AnimatorSet();
                            access$1600.setDuration(200);
                            access$1600.play(ObjectAnimator.ofFloat(LocationActivity.this.locationButton, str2, new float[]{1.0f}));
                            access$1600.start();
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
        new Thread(new -$$Lambda$LocationActivity$z_aGLcBJZM2u7Mu8z1CZYS4CKts(this, this.mapView)).start();
        View view = new View(context2);
        view.setBackgroundResource(NUM);
        this.mapViewClip.addView(view, LayoutHelper.createFrame(-1, 3, 83));
        messageObject = this.messageObject;
        if (messageObject == null) {
            this.markerImageView = new ImageView(context2);
            this.markerImageView.setImageResource(NUM);
            this.mapViewClip.addView(this.markerImageView, LayoutHelper.createFrame(24, 42, 49));
            this.markerXImageView = new ImageView(context2);
            this.markerXImageView.setAlpha(0.0f);
            this.markerXImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("location_markerX"), Mode.MULTIPLY));
            this.markerXImageView.setImageResource(NUM);
            this.mapViewClip.addView(this.markerXImageView, LayoutHelper.createFrame(14, 14, 49));
            this.emptyView = new EmptyTextProgressView(context2);
            this.emptyView.setText(LocaleController.getString("NoResult", NUM));
            this.emptyView.setShowAtCenter(true);
            this.emptyView.setVisibility(8);
            frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
            this.searchListView = new RecyclerListView(context2);
            this.searchListView.setVisibility(8);
            this.searchListView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
            RecyclerListView recyclerListView2 = this.searchListView;
            LocationActivitySearchAdapter locationActivitySearchAdapter2 = new LocationActivitySearchAdapter(context2);
            this.searchAdapter = locationActivitySearchAdapter2;
            recyclerListView2.setAdapter(locationActivitySearchAdapter2);
            frameLayout.addView(this.searchListView, LayoutHelper.createFrame(-1, -1, 51));
            this.searchListView.setOnScrollListener(new OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                    if (i == 1 && LocationActivity.this.searching && LocationActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(LocationActivity.this.getParentActivity().getCurrentFocus());
                    }
                }
            });
            this.searchListView.setOnItemClickListener(new -$$Lambda$LocationActivity$3ZuEtEMOSdh5Ci0BsPodKsI3Wyo(this));
        } else if (!messageObject.isLiveLocation()) {
            this.routeButton = new ImageView(context2);
            Drawable createSimpleSelectorCircleDrawable2 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
            if (VERSION.SDK_INT < 21) {
                Drawable mutate2 = context.getResources().getDrawable(NUM).mutate();
                mutate2.setColorFilter(new PorterDuffColorFilter(-16777216, Mode.MULTIPLY));
                createSimpleSelectorCircleDrawable = new CombinedDrawable(mutate2, createSimpleSelectorCircleDrawable2, 0, 0);
                createSimpleSelectorCircleDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                createSimpleSelectorCircleDrawable2 = createSimpleSelectorCircleDrawable;
            }
            this.routeButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable2);
            this.routeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), Mode.MULTIPLY));
            this.routeButton.setImageResource(NUM);
            this.routeButton.setScaleType(ScaleType.CENTER);
            if (VERSION.SDK_INT >= 21) {
                StateListAnimator stateListAnimator2 = new StateListAnimator();
                stateListAnimator2.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.routeButton, str2, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
                stateListAnimator2.addState(new int[0], ObjectAnimator.ofFloat(this.routeButton, str2, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
                this.routeButton.setStateListAnimator(stateListAnimator2);
                this.routeButton.setOutlineProvider(new ViewOutlineProvider() {
                    @SuppressLint({"NewApi"})
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    }
                });
            }
            frameLayout.addView(this.routeButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 37.0f));
            this.routeButton.setOnClickListener(new -$$Lambda$LocationActivity$nAAcoj784-MCQC1UQOD3DiQbuVY(this));
            this.adapter.setMessageObject(this.messageObject);
        }
        MessageObject messageObject2 = this.messageObject;
        if (messageObject2 == null || messageObject2.isLiveLocation()) {
            this.mapViewClip.addView(this.locationButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 14.0f));
        } else {
            this.mapViewClip.addView(this.locationButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 43.0f));
        }
        this.locationButton.setOnClickListener(new -$$Lambda$LocationActivity$xEpamdnSvUSxGrybxDZDzuiRGhw(this));
        if (this.messageObject == null) {
            this.locationButton.setAlpha(0.0f);
        }
        frameLayout.addView(this.actionBar);
        return this.fragmentView;
    }

    /* JADX WARNING: Missing block: B:42:0x00df, code skipped:
            return;
     */
    public /* synthetic */ void lambda$createView$1$LocationActivity(android.view.View r7, int r8) {
        /*
        r6 = this;
        r7 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r0 = 1;
        if (r8 != r0) goto L_0x0034;
    L_0x0005:
        r1 = r6.messageObject;
        if (r1 == 0) goto L_0x0034;
    L_0x0009:
        r1 = r1.isLiveLocation();
        if (r1 != 0) goto L_0x0034;
    L_0x000f:
        r8 = r6.googleMap;
        if (r8 == 0) goto L_0x0117;
    L_0x0013:
        r0 = new com.google.android.gms.maps.model.LatLng;
        r1 = r6.messageObject;
        r1 = r1.messageOwner;
        r1 = r1.media;
        r1 = r1.geo;
        r2 = r1.lat;
        r4 = r1._long;
        r0.<init>(r2, r4);
        r1 = r6.googleMap;
        r1 = r1.getMaxZoomLevel();
        r1 = r1 - r7;
        r7 = com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(r0, r1);
        r8.animateCamera(r7);
        goto L_0x0117;
    L_0x0034:
        r1 = 2;
        if (r8 != r0) goto L_0x0077;
    L_0x0037:
        r2 = r6.liveLocationType;
        if (r2 == r1) goto L_0x0077;
    L_0x003b:
        r7 = r6.delegate;
        if (r7 == 0) goto L_0x0072;
    L_0x003f:
        r7 = r6.userLocation;
        if (r7 == 0) goto L_0x0072;
    L_0x0043:
        r7 = new org.telegram.tgnet.TLRPC$TL_messageMediaGeo;
        r7.<init>();
        r8 = new org.telegram.tgnet.TLRPC$TL_geoPoint;
        r8.<init>();
        r7.geo = r8;
        r8 = r7.geo;
        r0 = r6.userLocation;
        r0 = r0.getLatitude();
        r0 = org.telegram.messenger.AndroidUtilities.fixLocationCoord(r0);
        r8.lat = r0;
        r8 = r7.geo;
        r0 = r6.userLocation;
        r0 = r0.getLongitude();
        r0 = org.telegram.messenger.AndroidUtilities.fixLocationCoord(r0);
        r8._long = r0;
        r8 = r6.delegate;
        r0 = r6.liveLocationType;
        r8.didSelectLocation(r7, r0);
    L_0x0072:
        r6.finishFragment();
        goto L_0x0117;
    L_0x0077:
        if (r8 != r1) goto L_0x007d;
    L_0x0079:
        r2 = r6.liveLocationType;
        if (r2 == r0) goto L_0x008a;
    L_0x007d:
        if (r8 != r0) goto L_0x0083;
    L_0x007f:
        r0 = r6.liveLocationType;
        if (r0 == r1) goto L_0x008a;
    L_0x0083:
        r0 = 3;
        if (r8 != r0) goto L_0x00e0;
    L_0x0086:
        r1 = r6.liveLocationType;
        if (r1 != r0) goto L_0x00e0;
    L_0x008a:
        r7 = r6.currentAccount;
        r7 = org.telegram.messenger.LocationController.getInstance(r7);
        r0 = r6.dialogId;
        r7 = r7.isSharingLocation(r0);
        if (r7 == 0) goto L_0x00a8;
    L_0x0098:
        r7 = r6.currentAccount;
        r7 = org.telegram.messenger.LocationController.getInstance(r7);
        r0 = r6.dialogId;
        r7.removeSharingLocation(r0);
        r6.finishFragment();
        goto L_0x0117;
    L_0x00a8:
        r7 = r6.delegate;
        if (r7 == 0) goto L_0x00df;
    L_0x00ac:
        r7 = r6.getParentActivity();
        if (r7 != 0) goto L_0x00b3;
    L_0x00b2:
        goto L_0x00df;
    L_0x00b3:
        r7 = r6.myLocation;
        if (r7 == 0) goto L_0x0117;
    L_0x00b7:
        r7 = 0;
        r0 = r6.dialogId;
        r8 = (int) r0;
        if (r8 <= 0) goto L_0x00ce;
    L_0x00bd:
        r7 = r6.currentAccount;
        r7 = org.telegram.messenger.MessagesController.getInstance(r7);
        r0 = r6.dialogId;
        r8 = (int) r0;
        r8 = java.lang.Integer.valueOf(r8);
        r7 = r7.getUser(r8);
    L_0x00ce:
        r8 = r6.getParentActivity();
        r0 = new org.telegram.ui.-$$Lambda$LocationActivity$3XRrdwHi6MppmANpYRKFISBi3vg;
        r0.<init>(r6);
        r7 = org.telegram.ui.Components.AlertsCreator.createLocationUpdateDialog(r8, r7, r0);
        r6.showDialog(r7);
        goto L_0x0117;
    L_0x00df:
        return;
    L_0x00e0:
        r0 = r6.adapter;
        r8 = r0.getItem(r8);
        r0 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r0 == 0) goto L_0x00fb;
    L_0x00ea:
        if (r8 == 0) goto L_0x00f7;
    L_0x00ec:
        r7 = r6.delegate;
        if (r7 == 0) goto L_0x00f7;
    L_0x00f0:
        r8 = (org.telegram.tgnet.TLRPC.TL_messageMediaVenue) r8;
        r0 = r6.liveLocationType;
        r7.didSelectLocation(r8, r0);
    L_0x00f7:
        r6.finishFragment();
        goto L_0x0117;
    L_0x00fb:
        r0 = r8 instanceof org.telegram.ui.LocationActivity.LiveLocation;
        if (r0 == 0) goto L_0x0117;
    L_0x00ff:
        r0 = r6.googleMap;
        r8 = (org.telegram.ui.LocationActivity.LiveLocation) r8;
        r8 = r8.marker;
        r8 = r8.getPosition();
        r1 = r6.googleMap;
        r1 = r1.getMaxZoomLevel();
        r1 = r1 - r7;
        r7 = com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(r8, r1);
        r0.animateCamera(r7);
    L_0x0117:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LocationActivity.lambda$createView$1$LocationActivity(android.view.View, int):void");
    }

    public /* synthetic */ void lambda$null$0$LocationActivity(int i) {
        TL_messageMediaGeoLive tL_messageMediaGeoLive = new TL_messageMediaGeoLive();
        tL_messageMediaGeoLive.geo = new TL_geoPoint();
        tL_messageMediaGeoLive.geo.lat = AndroidUtilities.fixLocationCoord(this.myLocation.getLatitude());
        tL_messageMediaGeoLive.geo._long = AndroidUtilities.fixLocationCoord(this.myLocation.getLongitude());
        tL_messageMediaGeoLive.period = i;
        this.delegate.didSelectLocation(tL_messageMediaGeoLive, this.liveLocationType);
        finishFragment();
    }

    public /* synthetic */ void lambda$createView$2$LocationActivity(ArrayList arrayList) {
        if (!(this.wasResults || arrayList.isEmpty())) {
            this.wasResults = true;
        }
        this.emptyView.showTextView();
    }

    public /* synthetic */ void lambda$createView$5$LocationActivity(MapView mapView) {
        try {
            mapView.onCreate(null);
        } catch (Exception unused) {
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$LocationActivity$NiH59QMX89dkoPAXrkTuzCstw_w(this, mapView));
    }

    public /* synthetic */ void lambda$null$4$LocationActivity(MapView mapView) {
        if (this.mapView != null && getParentActivity() != null) {
            try {
                mapView.onCreate(null);
                MapsInitializer.initialize(ApplicationLoader.applicationContext);
                this.mapView.getMapAsync(new -$$Lambda$LocationActivity$OpI_QmWhZUOli9wduv3mB6KjOlA(this));
                this.mapsInitialized = true;
                if (this.onResumeCalled) {
                    this.mapView.onResume();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public /* synthetic */ void lambda$null$3$LocationActivity(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
        onMapInit();
    }

    public /* synthetic */ void lambda$createView$6$LocationActivity(View view, int i) {
        TL_messageMediaVenue item = this.searchAdapter.getItem(i);
        if (item != null) {
            LocationActivityDelegate locationActivityDelegate = this.delegate;
            if (locationActivityDelegate != null) {
                locationActivityDelegate.didSelectLocation(item, this.liveLocationType);
            }
        }
        finishFragment();
    }

    public /* synthetic */ void lambda$createView$7$LocationActivity(View view) {
        if (VERSION.SDK_INT >= 23) {
            Activity parentActivity = getParentActivity();
            if (!(parentActivity == null || parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0)) {
                showPermissionAlert(true);
                return;
            }
        }
        if (this.myLocation != null) {
            try {
                getParentActivity().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(String.format(Locale.US, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", new Object[]{Double.valueOf(this.myLocation.getLatitude()), Double.valueOf(this.myLocation.getLongitude()), Double.valueOf(this.messageObject.messageOwner.media.geo.lat), Double.valueOf(this.messageObject.messageOwner.media.geo._long)}))));
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public /* synthetic */ void lambda$createView$8$LocationActivity(View view) {
        if (VERSION.SDK_INT >= 23) {
            Activity parentActivity = getParentActivity();
            if (!(parentActivity == null || parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0)) {
                showPermissionAlert(false);
                return;
            }
        }
        if (this.messageObject != null) {
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
            animatorSet.play(ObjectAnimator.ofFloat(this.locationButton, "alpha", new float[]{0.0f}));
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
                liveLocation.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(liveLocation.object.from_id));
                liveLocation.id = liveLocation.object.from_id;
            } else {
                int dialogId = (int) MessageObject.getDialogId(message);
                if (dialogId > 0) {
                    liveLocation.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(dialogId));
                    liveLocation.id = dialogId;
                } else {
                    liveLocation.chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-dialogId));
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
                    SharingLocationInfo sharingLocationInfo = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
                    if (liveLocation.id == UserConfig.getInstance(this.currentAccount).getClientUserId() && sharingLocationInfo != null && liveLocation.object.id == sharingLocationInfo.mid && this.myLocation != null) {
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

    private void onMapInit() {
        if (this.googleMap != null) {
            MessageObject messageObject = this.messageObject;
            if (messageObject == null) {
                this.userLocation = new Location("network");
                this.userLocation.setLatitude(20.659322d);
                this.userLocation.setLongitude(-11.40625d);
            } else if (messageObject.isLiveLocation()) {
                LiveLocation addUserMarker = addUserMarker(this.messageObject.messageOwner);
                if (!getRecentLocations()) {
                    this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(addUserMarker.marker.getPosition(), this.googleMap.getMaxZoomLevel() - 4.0f));
                }
            } else {
                LatLng latLng = new LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude());
                try {
                    GoogleMap googleMap = this.googleMap;
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(NUM));
                    googleMap.addMarker(markerOptions);
                } catch (Exception e) {
                    FileLog.e(e);
                }
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, this.googleMap.getMaxZoomLevel() - 4.0f));
                this.firstFocus = false;
                getRecentLocations();
            }
            try {
                this.googleMap.setMyLocationEnabled(true);
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            this.googleMap.getUiSettings().setZoomControlsEnabled(false);
            this.googleMap.getUiSettings().setCompassEnabled(false);
            this.googleMap.setOnMyLocationChangeListener(new -$$Lambda$LocationActivity$2chWRVga2GjoWIu3BZj92CU0Ewk(this));
            Location lastLocation = getLastLocation();
            this.myLocation = lastLocation;
            positionMarker(lastLocation);
            if (this.checkGpsEnabled && getParentActivity() != null) {
                this.checkGpsEnabled = false;
                if (getParentActivity().getPackageManager().hasSystemFeature("android.hardware.location.gps")) {
                    try {
                        if (!((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isProviderEnabled("gps")) {
                            Builder builder = new Builder(getParentActivity());
                            builder.setTitle(LocaleController.getString("AppName", NUM));
                            builder.setMessage(LocaleController.getString("GpsDisabledAlert", NUM));
                            builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", NUM), new -$$Lambda$LocationActivity$ecBiG4gg1AoOIl5zSqpWvsUsFzI(this));
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

    public /* synthetic */ void lambda$onMapInit$9$LocationActivity(Location location) {
        positionMarker(location);
        LocationController.getInstance(this.currentAccount).setGoogleMapLocation(location, this.isFirstLocation);
        this.isFirstLocation = false;
    }

    public /* synthetic */ void lambda$onMapInit$10$LocationActivity(DialogInterface dialogInterface, int i) {
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
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new -$$Lambda$LocationActivity$t5na51IRh1RFl9y_cBlGsx9lXz0(this));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$showPermissionAlert$11$LocationActivity(DialogInterface dialogInterface, int i) {
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
                    ImageView imageView = this.markerImageView;
                    if (imageView != null) {
                        i2 /= 2;
                        int dp = (i3 - AndroidUtilities.dp(42.0f)) + i2;
                        this.markerTop = dp;
                        imageView.setTranslationY((float) dp);
                        this.markerXImageView.setTranslationY((float) ((i3 - AndroidUtilities.dp(7.0f)) + i2));
                    }
                    ImageView imageView2 = this.routeButton;
                    if (imageView2 != null) {
                        imageView2.setTranslationY((float) i);
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
                    measuredHeight = this.liveLocationType;
                    measuredHeight = (measuredHeight == 1 || measuredHeight == 2) ? 66 : 0;
                    linearLayoutManager.scrollToPositionWithOffset(0, -AndroidUtilities.dp((float) (32 + measuredHeight)));
                    updateClipView(this.layoutManager.findFirstVisibleItemPosition());
                    this.listView.post(new -$$Lambda$LocationActivity$qghsUwV5L9H6Gi2vIZQkTtoUMvo(this));
                } else {
                    updateClipView(this.layoutManager.findFirstVisibleItemPosition());
                }
            }
        }
    }

    public /* synthetic */ void lambda$fixLayoutInternal$12$LocationActivity() {
        LinearLayoutManager linearLayoutManager = this.layoutManager;
        int i = this.liveLocationType;
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
            LiveLocation liveLocation = (LiveLocation) this.markersMap.get(UserConfig.getInstance(this.currentAccount).getClientUserId());
            SharingLocationInfo sharingLocationInfo = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
            if (!(liveLocation == null || sharingLocationInfo == null || liveLocation.object.id != sharingLocationInfo.mid)) {
                liveLocation.marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            }
            if (this.messageObject != null || this.googleMap == null) {
                this.adapter.setGpsLocation(this.myLocation);
            } else {
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
            }
        }
    }

    public void setMessageObject(MessageObject messageObject) {
        this.messageObject = messageObject;
        this.dialogId = this.messageObject.getDialogId();
    }

    public void setDialogId(long j) {
        this.dialogId = j;
    }

    private void fetchRecentLocations(ArrayList<Message> arrayList) {
        LatLngBounds.Builder builder = this.firstFocus ? new LatLngBounds.Builder() : null;
        int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
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
        ArrayList arrayList = (ArrayList) LocationController.getInstance(this.currentAccount).locationsCache.get(this.messageObject.getDialogId());
        if (arrayList == null || !arrayList.isEmpty()) {
            arrayList = null;
        } else {
            fetchRecentLocations(arrayList);
        }
        int i = (int) this.dialogId;
        boolean z = false;
        if (i < 0) {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
            if (ChatObject.isChannel(chat) && !chat.megagroup) {
                return false;
            }
        }
        TL_messages_getRecentLocations tL_messages_getRecentLocations = new TL_messages_getRecentLocations();
        long dialogId = this.messageObject.getDialogId();
        tL_messages_getRecentLocations.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) dialogId);
        tL_messages_getRecentLocations.limit = 100;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getRecentLocations, new -$$Lambda$LocationActivity$uTsW3Ho8X6I1AXo-KR8AEhCdGdk(this, dialogId));
        if (arrayList != null) {
            z = true;
        }
        return z;
    }

    public /* synthetic */ void lambda$getRecentLocations$14$LocationActivity(long j, TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LocationActivity$3hZnXAXM1XfwZoeBcJ8IsfAmd5w(this, tLObject, j));
        }
    }

    public /* synthetic */ void lambda$null$13$LocationActivity(TLObject tLObject, long j) {
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
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
            MessagesController.getInstance(this.currentAccount).putUsers(messages_messages.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(messages_messages.chats, false);
            LocationController.getInstance(this.currentAccount).locationsCache.put(j, messages_messages.messages);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(j));
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
                if (((Long) objArr[0]).longValue() == this.dialogId && this.messageObject != null) {
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
            } else if (i != NotificationCenter.messagesDeleted && i == NotificationCenter.replaceMessagesObjects) {
                long longValue = ((Long) objArr[0]).longValue();
                if (longValue == this.dialogId && this.messageObject != null) {
                    ArrayList arrayList2 = (ArrayList) objArr[1];
                    Object obj2 = null;
                    while (i3 < arrayList2.size()) {
                        MessageObject messageObject2 = (MessageObject) arrayList2.get(i3);
                        if (messageObject2.isLiveLocation()) {
                            LiveLocation liveLocation = (LiveLocation) this.markersMap.get(getMessageId(messageObject2.messageOwner));
                            if (liveLocation != null) {
                                SharingLocationInfo sharingLocationInfo = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(longValue);
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

    private void updateSearchInterface() {
        LocationActivityAdapter locationActivityAdapter = this.adapter;
        if (locationActivityAdapter != null) {
            locationActivityAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        -$$Lambda$LocationActivity$Q2KQKSBNMWi143XRFdcugER6Ui4 -__lambda_locationactivity_q2kqksbnmwi143xrfdcuger6ui4 = -$$Lambda$LocationActivity$Q2KQKSBNMWi143XRFdcugER6Ui4.INSTANCE;
        r10 = new ThemeDescription[52];
        r10[11] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        r10[12] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
        r10[13] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle");
        r10[14] = new ThemeDescription(this.locationButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "profile_actionIcon");
        r10[15] = new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "profile_actionBackground");
        r10[16] = new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "profile_actionPressedBackground");
        r10[17] = new ThemeDescription(this.routeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chats_actionIcon");
        r10[18] = new ThemeDescription(this.routeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "chats_actionBackground");
        r10[19] = new ThemeDescription(this.routeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "chats_actionPressedBackground");
        r10[20] = new ThemeDescription(this.markerXImageView, 0, null, null, null, null, "location_markerX");
        View view = this.listView;
        Class[] clsArr = new Class[]{GraySectionCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        r10[21] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "key_graySectionText");
        r10[22] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, "graySection");
        -$$Lambda$LocationActivity$Q2KQKSBNMWi143XRFdcugER6Ui4 -__lambda_locationactivity_q2kqksbnmwi143xrfdcuger6ui42 = -__lambda_locationactivity_q2kqksbnmwi143xrfdcuger6ui4;
        r10[23] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, -__lambda_locationactivity_q2kqksbnmwi143xrfdcuger6ui42, "avatar_text");
        r10[24] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_q2kqksbnmwi143xrfdcuger6ui42, "avatar_backgroundRed");
        r10[25] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_q2kqksbnmwi143xrfdcuger6ui42, "avatar_backgroundOrange");
        r10[26] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_q2kqksbnmwi143xrfdcuger6ui42, "avatar_backgroundViolet");
        r10[27] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_q2kqksbnmwi143xrfdcuger6ui42, "avatar_backgroundGreen");
        r10[28] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_q2kqksbnmwi143xrfdcuger6ui42, "avatar_backgroundCyan");
        r10[29] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_q2kqksbnmwi143xrfdcuger6ui42, "avatar_backgroundBlue");
        r10[30] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_q2kqksbnmwi143xrfdcuger6ui42, "avatar_backgroundPink");
        r10[31] = new ThemeDescription(null, 0, null, null, null, null, "location_liveLocationProgress");
        r10[32] = new ThemeDescription(null, 0, null, null, null, null, "location_placeLocationBackground");
        r10[33] = new ThemeDescription(null, 0, null, null, null, null, "dialog_liveLocationProgress");
        view = this.listView;
        int i = ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG;
        Class[] clsArr2 = new Class[]{SendLocationCell.class};
        String[] strArr2 = new String[1];
        strArr2[0] = "imageView";
        r10[34] = new ThemeDescription(view, i, clsArr2, strArr2, null, null, null, "location_sendLocationIcon");
        r10[35] = new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, null, null, null, "location_sendLiveLocationIcon");
        r10[36] = new ThemeDescription(this.listView, (ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE) | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, null, null, null, "location_sendLocationBackground");
        r10[37] = new ThemeDescription(this.listView, (ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE) | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, null, null, null, "location_sendLiveLocationBackground");
        r10[38] = new ThemeDescription(this.listView, 0, new Class[]{SendLocationCell.class}, new String[]{"accurateTextView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        view = this.listView;
        i = ThemeDescription.FLAG_CHECKTAG;
        clsArr2 = new Class[]{SendLocationCell.class};
        strArr2 = new String[1];
        strArr2[0] = "titleTextView";
        r10[39] = new ThemeDescription(view, i, clsArr2, strArr2, null, null, null, "windowBackgroundWhiteRedText2");
        r10[40] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"titleTextView"}, null, null, null, "windowBackgroundWhiteBlueText7");
        r10[41] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LocationCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        view = this.listView;
        clsArr2 = new Class[]{LocationCell.class};
        strArr2 = new String[1];
        strArr2[0] = "nameTextView";
        r10[42] = new ThemeDescription(view, 0, clsArr2, strArr2, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        clsArr2 = new Class[]{LocationCell.class};
        strArr2 = new String[1];
        strArr2[0] = "addressTextView";
        r10[43] = new ThemeDescription(view, 0, clsArr2, strArr2, null, null, null, "windowBackgroundWhiteGrayText3");
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
