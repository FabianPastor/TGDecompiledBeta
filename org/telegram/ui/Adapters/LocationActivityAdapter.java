package org.telegram.ui.Adapters;

import android.content.Context;
import android.location.Location;
import android.view.View;
import android.view.ViewGroup;
import java.util.Locale;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.GreySectionCell;
import org.telegram.ui.Cells.LocationCell;
import org.telegram.ui.Cells.LocationLoadingCell;
import org.telegram.ui.Cells.LocationPoweredCell;
import org.telegram.ui.Cells.SendLocationCell;

public class LocationActivityAdapter extends BaseLocationAdapter {
    private Location customLocation;
    private Location gpsLocation;
    private Context mContext;
    private int overScrollHeight;
    private SendLocationCell sendLocationCell;

    public LocationActivityAdapter(Context context) {
        this.mContext = context;
    }

    public void setOverScrollHeight(int value) {
        this.overScrollHeight = value;
    }

    public void setGpsLocation(Location location) {
        this.gpsLocation = location;
        updateCell();
    }

    public void setCustomLocation(Location location) {
        this.customLocation = location;
        updateCell();
    }

    private void updateCell() {
        if (this.sendLocationCell == null) {
            return;
        }
        if (this.customLocation != null) {
            this.sendLocationCell.setText(LocaleController.getString("SendSelectedLocation", R.string.SendSelectedLocation), String.format(Locale.US, "(%f,%f)", new Object[]{Double.valueOf(this.customLocation.getLatitude()), Double.valueOf(this.customLocation.getLongitude())}));
        } else if (this.gpsLocation != null) {
            this.sendLocationCell.setText(LocaleController.getString("SendLocation", R.string.SendLocation), LocaleController.formatString("AccurateTo", R.string.AccurateTo, LocaleController.formatPluralString("Meters", (int) this.gpsLocation.getAccuracy())));
        } else {
            this.sendLocationCell.setText(LocaleController.getString("SendLocation", R.string.SendLocation), LocaleController.getString("Loading", R.string.Loading));
        }
    }

    public int getCount() {
        if (this.searching || (!this.searching && this.places.isEmpty())) {
            return 4;
        }
        return (this.places.isEmpty() ? 0 : 1) + (this.places.size() + 3);
    }

    public boolean isEmpty() {
        return false;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (i == 0) {
            if (view == null) {
                view = new EmptyCell(this.mContext);
            }
            ((EmptyCell) view).setHeight(this.overScrollHeight);
        } else if (i == 1) {
            if (view == null) {
                view = new SendLocationCell(this.mContext);
            }
            this.sendLocationCell = (SendLocationCell) view;
            updateCell();
            return view;
        } else if (i == 2) {
            if (view == null) {
                view = new GreySectionCell(this.mContext);
            }
            ((GreySectionCell) view).setText(LocaleController.getString("NearbyPlaces", R.string.NearbyPlaces));
        } else if (this.searching || (!this.searching && this.places.isEmpty())) {
            if (view == null) {
                view = new LocationLoadingCell(this.mContext);
            }
            ((LocationLoadingCell) view).setLoading(this.searching);
        } else if (i != this.places.size() + 3) {
            if (view == null) {
                view = new LocationCell(this.mContext);
            }
            ((LocationCell) view).setLocation((TL_messageMediaVenue) this.places.get(i - 3), (String) this.iconUrls.get(i - 3), true);
        } else if (view == null) {
            view = new LocationPoweredCell(this.mContext);
        }
        return view;
    }

    public TL_messageMediaVenue getItem(int i) {
        if (i <= 2 || i >= this.places.size() + 3) {
            return null;
        }
        return (TL_messageMediaVenue) this.places.get(i - 3);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }
        if (position == 1) {
            return 1;
        }
        if (position == 2) {
            return 2;
        }
        if (this.searching || (!this.searching && this.places.isEmpty())) {
            return 4;
        }
        if (position == this.places.size() + 3) {
            return 5;
        }
        return 3;
    }

    public int getViewTypeCount() {
        return 6;
    }

    public boolean areAllItemsEnabled() {
        return false;
    }

    public boolean isEnabled(int position) {
        return (position == 2 || position == 0 || ((position == 3 && (this.searching || (!this.searching && this.places.isEmpty()))) || position == this.places.size() + 3)) ? false : true;
    }

    public boolean hasStableIds() {
        return true;
    }
}
