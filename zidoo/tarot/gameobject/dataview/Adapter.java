package zidoo.tarot.gameobject.dataview;

public abstract class Adapter implements AdapterInterface {
    private boolean isLoading = false;
    private DataSetObservable mDataSetObservable = new DataSetObservable();

    public void registerDataSetObserver(DataSetObserver observer) {
        this.mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        this.mDataSetObservable.unregisterObserver(observer);
    }

    public void notifyDataSetChanged() {
        this.isLoading = false;
        this.mDataSetObservable.notifyChanged();
    }

    public void notifyDataSetInvalidated() {
        this.mDataSetObservable.notifyInvalidated();
    }

    public boolean isEmpty() {
        return getCount() == 0;
    }

    public boolean isLoading() {
        return this.isLoading;
    }

    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }
}
