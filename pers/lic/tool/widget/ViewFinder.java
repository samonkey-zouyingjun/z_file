package pers.lic.tool.widget;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.TextView;

public class ViewFinder {
    protected Finder finder;
    private View v;

    private interface Finder {
        View findViewById(int i);
    }

    private final class LayoutFinder implements Finder {
        View layout;

        LayoutFinder(View layout) {
            this.layout = layout;
        }

        public View findViewById(int id) {
            return this.layout.findViewById(id);
        }
    }

    public static class TextFinder extends ViewFinder {
        TextView v;

        public TextFinder(Finder finder) {
            super(finder);
        }

        protected void setView(View v) {
            this.v = (TextView) v;
            super.setView(v);
        }

        public TextFinder findText(int id) {
            this.v = (TextView) findView(id);
            super.setView(this.v);
            return this;
        }

        public TextFinder setText(CharSequence text) {
            this.v.setText(text);
            return this;
        }

        public TextFinder setText(int resid) {
            this.v.setText(resid);
            return this;
        }
    }

    private final class WindowFinder implements Finder {
        Window window;

        WindowFinder(Window window) {
            this.window = window;
        }

        public View findViewById(int id) {
            return this.window.findViewById(id);
        }
    }

    public ViewFinder(Window window) {
        this.finder = new WindowFinder(window);
    }

    public ViewFinder(View v) {
        this.finder = new LayoutFinder(v);
    }

    ViewFinder(Finder finder) {
        this.finder = finder;
    }

    public ViewFinder find(int id) {
        this.v = this.finder.findViewById(id);
        return this;
    }

    public <F extends ViewFinder> F find(Class<F> cls, int id) {
        try {
            ViewFinder f = (ViewFinder) cls.cast(cls.getConstructor(new Class[]{Finder.class}).newInstance(new Object[]{this.finder}));
            f.setView(this.finder.findViewById(id));
            return f;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public TextFinder findText(int id) {
        find(id);
        TextFinder textFinder = new TextFinder(this.finder);
        textFinder.setView(this.v);
        return textFinder;
    }

    public <T extends View> T findView(int id) {
        return this.finder.findViewById(id);
    }

    protected void setView(View v) {
        this.v = v;
    }

    public ViewFinder setOnClickListener(OnClickListener listener) {
        this.v.setOnClickListener(listener);
        return this;
    }

    public ViewFinder setOnKeyListener(OnKeyListener listener) {
        this.v.setOnKeyListener(listener);
        return this;
    }

    public ViewFinder setOnFocusChangeListener(OnFocusChangeListener listener) {
        this.v.setOnFocusChangeListener(listener);
        return this;
    }

    public ViewFinder setSelected(boolean selected) {
        this.v.setSelected(selected);
        return this;
    }

    public ViewFinder requestFocus() {
        this.v.requestFocus();
        return this;
    }

    public ViewFinder setVisibility(int visibility) {
        this.v.setVisibility(visibility);
        return this;
    }
}
