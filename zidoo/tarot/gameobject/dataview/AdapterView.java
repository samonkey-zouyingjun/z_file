package zidoo.tarot.gameobject.dataview;

import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.widget.Layout;

public abstract class AdapterView<T extends Adapter> extends Layout {
    public OnItemClickListener mOnItemClickListener = null;
    public OnItemLongClickListener mOnItemLongClickListener = null;
    public OnItemSelectedListener mOnItemSelectedListener = null;

    public interface OnItemClickListener {
        void onItemClick(AdapterView<?> adapterView, GameObject gameObject, int i, long j);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(AdapterView<?> adapterView, GameObject gameObject, int i, long j);
    }

    public interface OnItemSelectedListener {
        void onItemSelected(AdapterView<?> adapterView, GameObject gameObject, int i, long j);

        void onNothingSelected(AdapterView<?> adapterView);
    }

    public abstract T getAdapter();

    public abstract GameObject getSelectedView();

    public abstract void setAdapter(T t);

    public abstract void setSelection(int i);

    public AdapterView(GLContext glContext) {
        super(glContext);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.mOnItemSelectedListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    public boolean performItemClick(GameObject view, int position, long id) {
        if (this.mOnItemClickListener == null) {
            return false;
        }
        if (view != null) {
            view.sendAccessibilityEvent(1);
        }
        this.mOnItemClickListener.onItemClick(this, view, position, id);
        return true;
    }

    public Object getItemAtPosition(int position) {
        T adapter = getAdapter();
        return (adapter == null || position < 0) ? null : adapter.getItem(position);
    }

    public long getItemIdAtPosition(int position) {
        T adapter = getAdapter();
        return (adapter == null || position < 0) ? -1 : adapter.getItemId(position);
    }

    public boolean addGameObject(GameObject child) {
        throw new UnsupportedOperationException("addGameObject is not supported in AdapterView, subclass does");
    }

    public boolean removeGameObject(GameObject child) {
        throw new UnsupportedOperationException("removeGameObject is not supported in AdapterView, subclass does");
    }

    public GameObject removeGameObject(int index) {
        throw new UnsupportedOperationException("removeGameObject is not supported in AdapterView, subclass does");
    }

    public void removeAll() {
        throw new UnsupportedOperationException("removeAll is not supported in AdapterView, subclass does");
    }
}
