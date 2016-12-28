package com.abdallaadelessa.core.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.abdallaadelessa.core.app.BaseCoreApp;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abdullah on 12/10/16.
 */

public abstract class BaseCoreRecyclerAdapter<VH extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<VH> {
    protected static final int INVALID_SIZE = -1;
    private List<T> data;
    private int rowWidth;
    private int rowHeight;

    public BaseCoreRecyclerAdapter(int rowWidth, int rowHeight) {
        this.rowWidth = rowWidth;
        this.rowHeight = rowHeight;
        reset();
    }

    public BaseCoreRecyclerAdapter() {
        this(INVALID_SIZE, INVALID_SIZE);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = onCreateView(parent, viewType);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        boolean paramsChanged = false;
        if(rowWidth != INVALID_SIZE && rowWidth != params.width) {
            params.width = rowWidth;
            paramsChanged = true;
        }
        if(rowHeight != INVALID_SIZE && rowHeight != params.height) {
            params.height = rowHeight;
            paramsChanged = true;
        }
        if(paramsChanged) view.setLayoutParams(params);
        return createNewHolderInstance(view);
    }

    private VH createNewHolderInstance(View view) {
        try {
            Class<VH> holderClass = (Class<VH>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            return holderClass.getDeclaredConstructor(View.class).newInstance(view);
        }
        catch(Exception e) {
            BaseCoreApp.getInstance().getLoggerComponent().getLogger().logError(e);
            throw new RuntimeException(e);
        }
    }

    protected abstract View onCreateView(ViewGroup parent, int viewType);

    // ----------------------------->

    public void reset() {
        data = new ArrayList<>();
    }

    public void resetWithNotify() {
        reset();
        notifyDataSetChanged();
    }

    public void addAll(boolean reset, List<T> data) {
        if(data == null) return;
        if(reset) reset();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void removeAndAdd(T t) {
        if(t == null) return;
        this.data.remove(t);
        this.data.add(t);
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    // ----------------------------->

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public List<T> getData() {
        return data;
    }

    public T getDataItem(int position) {
        return getData().get(position);
    }
}
