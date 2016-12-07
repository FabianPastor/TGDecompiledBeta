package org.telegram.ui.Adapters;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseSectionsAdapter extends BaseFragmentAdapter {
    private int count;
    private SparseArray<Integer> sectionCache;
    private int sectionCount;
    private SparseArray<Integer> sectionCountCache;
    private SparseArray<Integer> sectionPositionCache;

    public abstract int getCountForSection(int i);

    public abstract Object getItem(int i, int i2);

    public abstract View getItemView(int i, int i2, View view, ViewGroup viewGroup);

    public abstract int getItemViewType(int i, int i2);

    public abstract int getSectionCount();

    public abstract View getSectionHeaderView(int i, View view, ViewGroup viewGroup);

    public abstract boolean isRowEnabled(int i, int i2);

    private void cleanupCache() {
        this.sectionCache = new SparseArray();
        this.sectionPositionCache = new SparseArray();
        this.sectionCountCache = new SparseArray();
        this.count = -1;
        this.sectionCount = -1;
    }

    public BaseSectionsAdapter() {
        cleanupCache();
    }

    public void notifyDataSetChanged() {
        cleanupCache();
        super.notifyDataSetChanged();
    }

    public void notifyDataSetInvalidated() {
        cleanupCache();
        super.notifyDataSetInvalidated();
    }

    public boolean areAllItemsEnabled() {
        return false;
    }

    public boolean isEnabled(int position) {
        return isRowEnabled(getSectionForPosition(position), getPositionInSectionForPosition(position));
    }

    public final long getItemId(int position) {
        return (long) position;
    }

    public final int getCount() {
        if (this.count >= 0) {
            return this.count;
        }
        this.count = 0;
        for (int i = 0; i < internalGetSectionCount(); i++) {
            this.count += internalGetCountForSection(i);
        }
        return this.count;
    }

    public final Object getItem(int position) {
        return getItem(getSectionForPosition(position), getPositionInSectionForPosition(position));
    }

    public final int getItemViewType(int position) {
        return getItemViewType(getSectionForPosition(position), getPositionInSectionForPosition(position));
    }

    public final View getView(int position, View convertView, ViewGroup parent) {
        return getItemView(getSectionForPosition(position), getPositionInSectionForPosition(position), convertView, parent);
    }

    private int internalGetCountForSection(int section) {
        Integer cachedSectionCount = (Integer) this.sectionCountCache.get(section);
        if (cachedSectionCount != null) {
            return cachedSectionCount.intValue();
        }
        int sectionCount = getCountForSection(section);
        this.sectionCountCache.put(section, Integer.valueOf(sectionCount));
        return sectionCount;
    }

    private int internalGetSectionCount() {
        if (this.sectionCount >= 0) {
            return this.sectionCount;
        }
        this.sectionCount = getSectionCount();
        return this.sectionCount;
    }

    public final int getSectionForPosition(int position) {
        Integer cachedSection = (Integer) this.sectionCache.get(position);
        if (cachedSection != null) {
            return cachedSection.intValue();
        }
        int sectionStart = 0;
        int i = 0;
        while (i < internalGetSectionCount()) {
            int sectionEnd = sectionStart + internalGetCountForSection(i);
            if (position < sectionStart || position >= sectionEnd) {
                sectionStart = sectionEnd;
                i++;
            } else {
                this.sectionCache.put(position, Integer.valueOf(i));
                return i;
            }
        }
        return -1;
    }

    public int getPositionInSectionForPosition(int position) {
        Integer cachedPosition = (Integer) this.sectionPositionCache.get(position);
        if (cachedPosition != null) {
            return cachedPosition.intValue();
        }
        int sectionStart = 0;
        int i = 0;
        while (i < internalGetSectionCount()) {
            int sectionEnd = sectionStart + internalGetCountForSection(i);
            if (position < sectionStart || position >= sectionEnd) {
                sectionStart = sectionEnd;
                i++;
            } else {
                int positionInSection = position - sectionStart;
                this.sectionPositionCache.put(position, Integer.valueOf(positionInSection));
                return positionInSection;
            }
        }
        return -1;
    }
}
