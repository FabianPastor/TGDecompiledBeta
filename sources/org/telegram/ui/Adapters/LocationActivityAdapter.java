package org.telegram.ui.Adapters;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$TL_channelLocation;
import org.telegram.tgnet.TLRPC$TL_geoPoint;
import org.telegram.tgnet.TLRPC$TL_messageMediaVenue;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.LocationActivityAdapter;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LocationCell;
import org.telegram.ui.Cells.LocationDirectionCell;
import org.telegram.ui.Cells.LocationLoadingCell;
import org.telegram.ui.Cells.LocationPoweredCell;
import org.telegram.ui.Cells.SendLocationCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.SharingLiveLocationCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.LocationActivity;

public class LocationActivityAdapter extends BaseLocationAdapter implements LocationController.LocationFetchCallback {
    private String addressName;
    private TLRPC$TL_channelLocation chatLocation;
    private int currentAccount = UserConfig.selectedAccount;
    private ArrayList<LocationActivity.LiveLocation> currentLiveLocations = new ArrayList<>();
    private MessageObject currentMessageObject;
    private Location customLocation;
    private long dialogId;
    private boolean fetchingLocation;
    private Location gpsLocation;
    private int locationType;
    private Context mContext;
    private boolean needEmptyView;
    private int overScrollHeight;
    private Location previousFetchedLocation;
    private SendLocationCell sendLocationCell;
    private int shareLiveLocationPotistion = -1;
    /* access modifiers changed from: private */
    public Runnable updateRunnable;

    /* access modifiers changed from: protected */
    public void onDirectionClick() {
    }

    public LocationActivityAdapter(Context context, int i, long j, boolean z) {
        this.mContext = context;
        this.locationType = i;
        this.dialogId = j;
        this.needEmptyView = z;
    }

    public void setOverScrollHeight(int i) {
        this.overScrollHeight = i;
    }

    public void setUpdateRunnable(Runnable runnable) {
        this.updateRunnable = runnable;
    }

    public void setGpsLocation(Location location) {
        int i;
        boolean z = this.gpsLocation == null;
        this.gpsLocation = location;
        if (this.customLocation == null) {
            fetchLocationAddress();
        }
        if (z && (i = this.shareLiveLocationPotistion) > 0) {
            notifyItemChanged(i);
        }
        if (this.currentMessageObject != null) {
            notifyItemChanged(1, new Object());
            updateLiveLocations();
        } else if (this.locationType != 2) {
            updateCell();
        } else {
            updateLiveLocations();
        }
    }

    public void updateLiveLocationCell() {
        int i = this.shareLiveLocationPotistion;
        if (i > 0) {
            notifyItemChanged(i);
        }
    }

    public void updateLiveLocations() {
        if (!this.currentLiveLocations.isEmpty()) {
            notifyItemRangeChanged(2, this.currentLiveLocations.size(), new Object());
        }
    }

    public void setCustomLocation(Location location) {
        this.customLocation = location;
        fetchLocationAddress();
        updateCell();
    }

    public void setLiveLocations(ArrayList<LocationActivity.LiveLocation> arrayList) {
        this.currentLiveLocations = new ArrayList<>(arrayList);
        int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        int i = 0;
        while (true) {
            if (i >= this.currentLiveLocations.size()) {
                break;
            } else if (this.currentLiveLocations.get(i).id == clientUserId) {
                this.currentLiveLocations.remove(i);
                break;
            } else {
                i++;
            }
        }
        notifyDataSetChanged();
    }

    public void setMessageObject(MessageObject messageObject) {
        this.currentMessageObject = messageObject;
        notifyDataSetChanged();
    }

    public void setChatLocation(TLRPC$TL_channelLocation tLRPC$TL_channelLocation) {
        this.chatLocation = tLRPC$TL_channelLocation;
    }

    private void updateCell() {
        String str;
        SendLocationCell sendLocationCell2 = this.sendLocationCell;
        if (sendLocationCell2 == null) {
            return;
        }
        if (this.locationType == 4 || this.customLocation != null) {
            if (!TextUtils.isEmpty(this.addressName)) {
                str = this.addressName;
            } else {
                Location location = this.customLocation;
                if ((location == null && this.gpsLocation == null) || this.fetchingLocation) {
                    str = LocaleController.getString("Loading", NUM);
                } else if (location != null) {
                    str = String.format(Locale.US, "(%f,%f)", new Object[]{Double.valueOf(location.getLatitude()), Double.valueOf(this.customLocation.getLongitude())});
                } else {
                    Location location2 = this.gpsLocation;
                    if (location2 != null) {
                        str = String.format(Locale.US, "(%f,%f)", new Object[]{Double.valueOf(location2.getLatitude()), Double.valueOf(this.gpsLocation.getLongitude())});
                    } else {
                        str = LocaleController.getString("Loading", NUM);
                    }
                }
            }
            if (this.locationType == 4) {
                this.sendLocationCell.setText(LocaleController.getString("ChatSetThisLocation", NUM), str);
            } else {
                this.sendLocationCell.setText(LocaleController.getString("SendSelectedLocation", NUM), str);
            }
        } else if (this.gpsLocation != null) {
            sendLocationCell2.setText(LocaleController.getString("SendLocation", NUM), LocaleController.formatString("AccurateTo", NUM, LocaleController.formatPluralString("Meters", (int) this.gpsLocation.getAccuracy())));
        } else {
            sendLocationCell2.setText(LocaleController.getString("SendLocation", NUM), LocaleController.getString("Loading", NUM));
        }
    }

    public void onLocationAddressAvailable(String str, String str2, Location location) {
        this.fetchingLocation = false;
        this.previousFetchedLocation = location;
        this.addressName = str;
        updateCell();
    }

    public void fetchLocationAddress() {
        if (this.locationType == 4) {
            Location location = this.customLocation;
            if (location != null || (location = this.gpsLocation) != null) {
                Location location2 = this.previousFetchedLocation;
                if (location2 == null || location2.distanceTo(location) > 100.0f) {
                    this.addressName = null;
                }
                this.fetchingLocation = true;
                updateCell();
                LocationController.fetchLocationAddress(location, this);
                return;
            }
            return;
        }
        Location location3 = this.customLocation;
        if (location3 != null) {
            Location location4 = this.previousFetchedLocation;
            if (location4 == null || location4.distanceTo(location3) > 20.0f) {
                this.addressName = null;
            }
            this.fetchingLocation = true;
            updateCell();
            LocationController.fetchLocationAddress(location3, this);
        }
    }

    public int getItemCount() {
        int i = this.locationType;
        int i2 = 6;
        if (i == 6 || i == 5 || i == 4) {
            return 2;
        }
        int i3 = 1;
        if (this.currentMessageObject != null) {
            if (!this.currentLiveLocations.isEmpty()) {
                i3 = this.currentLiveLocations.size() + 3;
            }
            return i3 + 2;
        } else if (i == 2) {
            return this.currentLiveLocations.size() + 2;
        } else {
            if (this.searching || this.places.isEmpty()) {
                if (this.locationType == 0) {
                    i2 = 5;
                }
                return i2 + (this.needEmptyView ? 1 : 0);
            } else if (this.locationType == 1) {
                return this.places.size() + 6 + (this.needEmptyView ? 1 : 0);
            } else {
                return this.places.size() + 5 + (this.needEmptyView ? 1 : 0);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateViewHolder$0 */
    public /* synthetic */ void lambda$onCreateViewHolder$0$LocationActivityAdapter(View view) {
        onDirectionClick();
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        ShadowSectionCell shadowSectionCell;
        View view;
        switch (i) {
            case 0:
                shadowSectionCell = new EmptyCell(this.mContext) {
                    public ViewPropertyAnimator animate() {
                        ViewPropertyAnimator animate = super.animate();
                        if (Build.VERSION.SDK_INT >= 19) {
                            animate.setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    LocationActivityAdapter.AnonymousClass1.this.lambda$animate$0$LocationActivityAdapter$1(valueAnimator);
                                }
                            });
                        }
                        return animate;
                    }

                    /* access modifiers changed from: private */
                    /* renamed from: lambda$animate$0 */
                    public /* synthetic */ void lambda$animate$0$LocationActivityAdapter$1(ValueAnimator valueAnimator) {
                        if (LocationActivityAdapter.this.updateRunnable != null) {
                            LocationActivityAdapter.this.updateRunnable.run();
                        }
                    }
                };
                break;
            case 1:
                view = new SendLocationCell(this.mContext, false);
                break;
            case 2:
                shadowSectionCell = new HeaderCell(this.mContext);
                break;
            case 3:
                view = new LocationCell(this.mContext, false);
                break;
            case 4:
                shadowSectionCell = new LocationLoadingCell(this.mContext);
                break;
            case 5:
                shadowSectionCell = new LocationPoweredCell(this.mContext);
                break;
            case 6:
                SendLocationCell sendLocationCell2 = new SendLocationCell(this.mContext, true);
                sendLocationCell2.setDialogId(this.dialogId);
                shadowSectionCell = sendLocationCell2;
                break;
            case 7:
                Context context = this.mContext;
                int i2 = this.locationType;
                shadowSectionCell = new SharingLiveLocationCell(context, true, (i2 == 4 || i2 == 5) ? 16 : 54);
                break;
            case 8:
                LocationDirectionCell locationDirectionCell = new LocationDirectionCell(this.mContext);
                locationDirectionCell.setOnButtonClick(new View.OnClickListener() {
                    public final void onClick(View view) {
                        LocationActivityAdapter.this.lambda$onCreateViewHolder$0$LocationActivityAdapter(view);
                    }
                });
                shadowSectionCell = locationDirectionCell;
                break;
            case 9:
                ShadowSectionCell shadowSectionCell2 = new ShadowSectionCell(this.mContext);
                CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                combinedDrawable.setFullsize(true);
                shadowSectionCell2.setBackgroundDrawable(combinedDrawable);
                shadowSectionCell = shadowSectionCell2;
                break;
            default:
                shadowSectionCell = new View(this.mContext);
                break;
        }
        shadowSectionCell = view;
        return new RecyclerListView.Holder(shadowSectionCell);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        int itemViewType = viewHolder.getItemViewType();
        if (itemViewType != 0) {
            boolean z = true;
            if (itemViewType != 1) {
                int i2 = 2;
                if (itemViewType == 2) {
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (this.currentMessageObject != null) {
                        headerCell.setText(LocaleController.getString("LiveLocations", NUM));
                    } else {
                        headerCell.setText(LocaleController.getString("NearbyVenue", NUM));
                    }
                } else if (itemViewType == 3) {
                    LocationCell locationCell = (LocationCell) viewHolder.itemView;
                    int i3 = this.locationType == 0 ? i - 4 : i - 5;
                    locationCell.setLocation(this.places.get(i3), this.iconUrls.get(i3), i3, true);
                } else if (itemViewType == 4) {
                    ((LocationLoadingCell) viewHolder.itemView).setLoading(this.searching);
                } else if (itemViewType == 6) {
                    SendLocationCell sendLocationCell2 = (SendLocationCell) viewHolder.itemView;
                    if (this.gpsLocation == null) {
                        z = false;
                    }
                    sendLocationCell2.setHasLocation(z);
                } else if (itemViewType == 7) {
                    SharingLiveLocationCell sharingLiveLocationCell = (SharingLiveLocationCell) viewHolder.itemView;
                    if (this.locationType == 6) {
                        sharingLiveLocationCell.setDialog(this.currentMessageObject, this.gpsLocation);
                        return;
                    }
                    TLRPC$TL_channelLocation tLRPC$TL_channelLocation = this.chatLocation;
                    if (tLRPC$TL_channelLocation != null) {
                        sharingLiveLocationCell.setDialog(this.dialogId, tLRPC$TL_channelLocation);
                        return;
                    }
                    MessageObject messageObject = this.currentMessageObject;
                    if (messageObject == null || i != 1) {
                        ArrayList<LocationActivity.LiveLocation> arrayList = this.currentLiveLocations;
                        if (messageObject != null) {
                            i2 = 5;
                        }
                        sharingLiveLocationCell.setDialog(arrayList.get(i - i2), this.gpsLocation);
                        return;
                    }
                    sharingLiveLocationCell.setDialog(messageObject, this.gpsLocation);
                }
            } else {
                this.sendLocationCell = (SendLocationCell) viewHolder.itemView;
                updateCell();
            }
        } else {
            ((EmptyCell) viewHolder.itemView).setHeight(this.overScrollHeight);
        }
    }

    public Object getItem(int i) {
        int i2 = this.locationType;
        if (i2 != 4) {
            MessageObject messageObject = this.currentMessageObject;
            if (messageObject != null) {
                if (i == 1) {
                    return messageObject;
                }
                if (i > 4 && i < this.places.size() + 4) {
                    return this.currentLiveLocations.get(i - 5);
                }
            } else if (i2 == 2) {
                if (i >= 2) {
                    return this.currentLiveLocations.get(i - 2);
                }
                return null;
            } else if (i2 == 1) {
                if (i > 4 && i < this.places.size() + 5) {
                    return this.places.get(i - 5);
                }
            } else if (i > 3 && i < this.places.size() + 4) {
                return this.places.get(i - 4);
            }
            return null;
        } else if (this.addressName == null) {
            return null;
        } else {
            TLRPC$TL_messageMediaVenue tLRPC$TL_messageMediaVenue = new TLRPC$TL_messageMediaVenue();
            tLRPC$TL_messageMediaVenue.address = this.addressName;
            TLRPC$TL_geoPoint tLRPC$TL_geoPoint = new TLRPC$TL_geoPoint();
            tLRPC$TL_messageMediaVenue.geo = tLRPC$TL_geoPoint;
            Location location = this.customLocation;
            if (location != null) {
                tLRPC$TL_geoPoint.lat = location.getLatitude();
                tLRPC$TL_messageMediaVenue.geo._long = this.customLocation.getLongitude();
            } else {
                Location location2 = this.gpsLocation;
                if (location2 != null) {
                    tLRPC$TL_geoPoint.lat = location2.getLatitude();
                    tLRPC$TL_messageMediaVenue.geo._long = this.gpsLocation.getLongitude();
                }
            }
            return tLRPC$TL_messageMediaVenue;
        }
    }

    public int getItemViewType(int i) {
        if (i == 0) {
            return 0;
        }
        if (this.locationType == 6) {
            return 7;
        }
        if (this.needEmptyView && i == getItemCount() - 1) {
            return 10;
        }
        int i2 = this.locationType;
        if (i2 == 5) {
            return 7;
        }
        if (i2 == 4) {
            return 1;
        }
        if (this.currentMessageObject != null) {
            if (this.currentLiveLocations.isEmpty()) {
                if (i == 2) {
                    return 8;
                }
            } else if (i == 2) {
                return 9;
            } else {
                if (i == 3) {
                    return 2;
                }
                if (i == 4) {
                    this.shareLiveLocationPotistion = i;
                    return 6;
                }
            }
            return 7;
        } else if (i2 != 2) {
            if (i2 == 1) {
                if (i == 1) {
                    return 1;
                }
                if (i == 2) {
                    this.shareLiveLocationPotistion = i;
                    return 6;
                } else if (i == 3) {
                    return 9;
                } else {
                    if (i == 4) {
                        return 2;
                    }
                    if (this.searching || this.places.isEmpty()) {
                        return 4;
                    }
                    if (i == this.places.size() + 5) {
                        return 5;
                    }
                }
            } else if (i == 1) {
                return 1;
            } else {
                if (i == 2) {
                    return 9;
                }
                if (i == 3) {
                    return 2;
                }
                if (this.searching || this.places.isEmpty()) {
                    return 4;
                }
                if (i == this.places.size() + 4) {
                    return 5;
                }
            }
            return 3;
        } else if (i != 1) {
            return 7;
        } else {
            this.shareLiveLocationPotistion = i;
            return 6;
        }
    }

    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        int itemViewType = viewHolder.getItemViewType();
        if (itemViewType == 6) {
            if (LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId) == null && this.gpsLocation == null) {
                return false;
            }
            return true;
        } else if (itemViewType == 1 || itemViewType == 3 || itemViewType == 7) {
            return true;
        } else {
            return false;
        }
    }
}
