package com.abdallaadelessa.core.view.custom;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


public class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    private static final int INDEX_FIRST_FETCH = 1;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private int previousTotal; // The total number of items in the dataset after the last load
    private boolean loading; // True if we are still waiting for the last set of data to load.
    private int currentPage;
    public int pageSize = 30;
    private LinearLayoutManager mLinearLayoutManager;
    private IEndlessRecyclerOnScrollListener iEndlessRecyclerOnScrollListener;

    public EndlessRecyclerOnScrollListener() {
        this(30);
    }

    public EndlessRecyclerOnScrollListener(int pageSize) {
        this.pageSize = pageSize;
        initValues();
    }

    public void initComponent(LinearLayoutManager linearLayoutManager, IEndlessRecyclerOnScrollListener iEndlessRecyclerOnScrollListener) {
        this.mLinearLayoutManager = linearLayoutManager;
        this.iEndlessRecyclerOnScrollListener = iEndlessRecyclerOnScrollListener;
    }

    private void initValues() {
        loading = true;
        firstVisibleItem = 0;
        visibleItemCount = 0;
        totalItemCount = 0;
        previousTotal = 0;
        currentPage = INDEX_FIRST_FETCH;
    }

    private boolean isFirstFetch() {
        return currentPage == EndlessRecyclerOnScrollListener.INDEX_FIRST_FETCH;
    }

    public void reset() {
        initValues();
        if(iEndlessRecyclerOnScrollListener != null) {
            iEndlessRecyclerOnScrollListener.onLoadMore(isFirstFetch(), currentPage);
        }
    }

    // ------------------>

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        if(loading) {
            if(totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if(!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + pageSize)) {
            // End has been reached
            currentPage++;
            if(iEndlessRecyclerOnScrollListener != null) {
                iEndlessRecyclerOnScrollListener.onLoadMore(isFirstFetch(), currentPage);
            }
            loading = true;
        }
    }

    // ------------------>

    public interface IEndlessRecyclerOnScrollListener {
        void onLoadMore(boolean firstFetch, int currentPage);
    }
}