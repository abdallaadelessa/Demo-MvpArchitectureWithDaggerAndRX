package com.abdallaadelessa.core.dagger.networkModule.builders;

import android.support.annotation.Nullable;

import com.abdallaadelessa.core.dagger.networkModule.volley.VolleyRequestManager;
import com.abdallaadelessa.core.utils.ValidationUtils;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.google.auto.value.AutoValue;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import rx.Observable;

/**
 * Created by Abdalla on 13/10/2016.
 */
@AutoValue
public abstract class HttpRequest extends HttpRequestGetters {

    public static Builder builder(RequestQueue requestQueue, BaseResponseInterceptor responseInterceptor, ExecutorService executorService) {
        Builder builder = new AutoValue_HttpRequest.Builder();
        builder.requestQueue(requestQueue);
        builder.responseInterceptor(responseInterceptor);
        builder.executorService(executorService);
        // Default Values
        builder.GET();
        builder.type(String.class);
        builder.headers(new HashMap<String, String>());
        builder.params(new HashMap<String, String>());
        builder.retryPolicy(DEFAULT_RETRY_POLICY);
        builder.shouldCache(false);
        builder.cancelIfRunning(true);
        builder.cancelOnUnSubscribe(true);
        return builder;
    }

    @AutoValue.Builder
    public abstract static class Builder extends HttpRequestGetters {

        abstract HttpRequest.Builder requestQueue(RequestQueue requestQueue);

        public abstract HttpRequest.Builder responseInterceptor(BaseResponseInterceptor responseInterceptor);

        abstract HttpRequest.Builder executorService(ExecutorService executorService);

        public abstract HttpRequest.Builder tag(String tag);

        public abstract HttpRequest.Builder url(String url);

        public abstract HttpRequest.Builder method(int method);

        public abstract HttpRequest.Builder type(Type type);

        public abstract HttpRequest.Builder headers(Map<String, String> headers);

        public abstract HttpRequest.Builder params(Map<String, String> params);

        public abstract HttpRequest.Builder body(String body);

        public abstract HttpRequest.Builder retryPolicy(RetryPolicy retryPolicy);

        public abstract HttpRequest.Builder shouldCache(boolean shouldCache);

        public abstract HttpRequest.Builder cancelIfRunning(boolean cancelIfRunning);

        public abstract HttpRequest.Builder cancelOnUnSubscribe(boolean cancelOnUnSubscribe);

        //------------->

        public Builder GET() {
            return method(Request.Method.GET);
        }

        public Builder POST() {
            return method(Request.Method.POST);
        }

        public Builder contentType(String contentType) {
            return addHeader(HEADER_CONTENT_TYPE, contentType);
        }

        public Builder addHeader(String key, String value) {
            headers().put(key, value);
            return this;
        }

        public Builder addParam(String key, String value) {
            body(null);
            contentType(contentType());
            params().put(key, value);
            method(method());
            return this;
        }

        public Builder addBody(String body) {
            params(new HashMap<String, String>());
            contentType(CONTENT_TYPE_JSON);
            body(body);
            POST();
            return this;
        }

        abstract HttpRequest autoBuild();

        public <T> Observable<T> build() {
            return new VolleyRequestManager<T>().createObservableFrom(autoBuild());
        }
    }

}

abstract class HttpRequestGetters {
    public abstract RequestQueue requestQueue();

    public abstract BaseResponseInterceptor responseInterceptor();

    public abstract ExecutorService executorService();

    @Nullable
    public abstract String tag();

    public abstract String url();

    public abstract int method();

    public abstract Type type();

    public abstract Map<String, String> headers();

    public abstract Map<String, String> params();

    @Nullable
    public abstract String body();

    public abstract RetryPolicy retryPolicy();

    public abstract boolean shouldCache();

    public abstract boolean cancelIfRunning();

    public abstract boolean cancelOnUnSubscribe();

    public String contentType() {
        return headers().get(HEADER_CONTENT_TYPE);
    }

    public boolean hasBody() {
        return !ValidationUtils.isStringEmpty(body());
    }

    public byte[] bodyToBytes() {
        byte[] bodyBytes = null;
        try {
            if (!ValidationUtils.isStringEmpty(body())) {
                bodyBytes = body().getBytes(PROTOCOL_CHARSET);
            }
        } catch (Exception ee) {
            responseInterceptor().getLogger().logError(ee);
        }
        return bodyBytes;
    }

    // ----------> Constants
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String PROTOCOL_CHARSET = "utf-8";
    public static final String CONTENT_TYPE_JSON = String.format("application/json; charset=%s", PROTOCOL_CHARSET);
    public static final RetryPolicy FILE_UPLOAD_RETRY_POLICY = new DefaultRetryPolicy(40000, 0, 0);
    public static final DefaultRetryPolicy DEFAULT_RETRY_POLICY = new DefaultRetryPolicy(5000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
}
