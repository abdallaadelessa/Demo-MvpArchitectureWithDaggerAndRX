package com.abdallaadelessa.core.utils;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.abdallaadelessa.core.R;
import com.abdallaadelessa.core.app.BaseCoreApp;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.ViewTarget;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ImageLoaderManager {
    public static final int MAX_HEIGHT = UIUtils.dpToPx(544);//816.0f
    public static final int MAX_WIDTH = UIUtils.dpToPx(408);//612.0f;
    public static final String ANDROID_RESOURCE = "android.resource://";
    public static final String FORWARD_SLASH = "/";
    //---> PlaceHolders
    private static final int DEFAULT_PLACE_HOLDER_ID = BaseCoreApp.getDefaultPlaceholder();
    private static final int DEFAULT_IMAGE_SIZE = 0;
    public static final int DEFAULT_PLACE_HOLDER = -1;
    public static final int NO_PLACE_HOLDER = -2;

    public static void init(Context context) {
        ViewTarget.setTagId(R.id.glide_tag);
    }

    public static Builder getDefaultBuilder() {
        return new Builder();
    }

    public static String getDrawableUrl(int resId) {
        return Uri.parse(ANDROID_RESOURCE + BaseCoreApp.getInstance().getPackageName() + FORWARD_SLASH + resId).toString();
    }

    public static void loadVideoThumbnail(ImageView imageView, final String videoFilePath, int placeHolder, int width, int height) {
        Glide.with(imageView.getContext()).load(videoFilePath).placeholder(placeHolder).override(width, height).into(imageView);
    }

    // --------------------->

    public static class Builder {
        private Context context;
        private String path;
        private int width;
        private int height;
        private boolean animate;
        private boolean offlineOnly;
        private int placeHolderDrawableId;
        private ScaleType scaleType;
        private Callback callback;
        //---> Holders
        private Target target;
        private ImageView imageView;
        private NotificationTargetModel notificationTargetModel;

        private Builder() {
            this(DEFAULT_IMAGE_SIZE, DEFAULT_IMAGE_SIZE, DEFAULT_PLACE_HOLDER, ScaleType.CENTER_INSIDE, false, true);
        }

        private Builder(int width, int height, int placeHolderDrawableId, ScaleType scaleType, boolean offlineOnly, boolean animate) {
            this.width = width;
            this.height = height;
            this.placeHolderDrawableId = placeHolderDrawableId;
            this.scaleType = scaleType;
            this.offlineOnly = offlineOnly;
            this.animate = animate;
        }

        // --------------------->

        public Builder path(Context context, String path) {
            this.context = context;
            this.path = path;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder widthAndHeight(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder widthAndHeight(int size) {
            this.width = size;
            this.height = size;
            return this;
        }

        public Builder placeHolder(int placeHolderDrawableId) {
            this.placeHolderDrawableId = placeHolderDrawableId;
            return this;
        }

        public Builder noPlaceHolder() {
            this.placeHolderDrawableId = NO_PLACE_HOLDER;
            return this;
        }

        public Builder scaleType(ScaleType scaleType) {
            this.scaleType = scaleType;
            return this;
        }

        public Builder animate(boolean animate) {
            this.animate = animate;
            return this;
        }

        public Builder offlineOnly(boolean offlineOnly) {
            this.offlineOnly = offlineOnly;
            return this;
        }

        public Builder callback(Callback callback) {
            this.callback = callback;
            return this;
        }

        public Builder target(Target target) {
            this.target = target;
            return this;
        }

        public Builder imageView(ImageView imageView) {
            this.imageView = imageView;
            return this;
        }

        public Builder notificationTarget(int notificationId, Notification notification, int remoteViewImageViewId, RemoteViews notificationView) {
            this.notificationTargetModel = new NotificationTargetModel(notificationId, notification, remoteViewImageViewId, notificationView);
            return this;
        }

        public void load() {
            ImageLoaderManager.load(this);
        }

        public void oneTimeDownload() {
            ImageLoaderManager.oneTimeDownload(this);
        }

        public Observable<File> getFileObservable() {
            return Observable.create(new Observable.OnSubscribe<File>() {
                @Override
                public void call(Subscriber<? super File> subscriber) {
                    subscriber.onNext(ImageLoaderManager.getFile(Builder.this));
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
        }

        public File getFile() {
            return ImageLoaderManager.getFile(Builder.this);
        }
    }

    // --------------------->

    private static void load(Builder builder) {
        if (!validate(builder)) return;
        Context context = builder.context;
        String path = builder.path;
        prepareBuilderAttrs(builder);
        //-------------------> Create
        DrawableTypeRequest<String> request;
        if (!ValidationUtils.isStringEmpty(path)) {
            request = Glide.with(context).load(path);
        } else {
            request = Glide.with(context).load(getDrawableUrl(builder.placeHolderDrawableId));
        }
        addRequestAttrs(request, builder);
        addTargetObject(request, builder);
    }

    private static void oneTimeDownload(final Builder builder) {
        Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                try {
                    File file = Glide.with(builder.context).using(new NetworkDisablingLoader()).load(builder.path).downloadOnly(builder.width, builder.height).get();
                    subscriber.onNext(file);
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    BaseCoreApp.getAppComponent().getLogger().logError(e);
                    subscriber.onError(e);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread()).subscribe(new Action1<File>() {
            @Override
            public void call(File file) {
                if (file != null) {
                    //From Cache
                    load(builder.path(builder.context, file.getPath()));
                } else {
                    // From Network
                    if (builder.target != null) {
                        builder.target.onImageReady(BaseCoreApp.getDefaultPlaceholderBitmap(builder.context));
                    }
                    load(builder);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                // From Network
                if (builder.target != null) {
                    builder.target.onImageReady(BaseCoreApp.getDefaultPlaceholderBitmap(builder.context));
                }
                load(builder);
            }
        });
    }

    private static File getFile(Builder builder) {
        File file = null;
        if (!validate(builder)) return file;
        Context context = builder.context;
        String path = builder.path;
        prepareBuilderAttrs(builder);
        //-------------------> Create
        try {
            RequestManager requestManager = Glide.with(context);
            if (builder.offlineOnly) {
                file = requestManager.using(new NetworkDisablingLoader()).load(path).downloadOnly(builder.width, builder.height).get();
            } else {
                file = requestManager.load(path).downloadOnly(builder.width, builder.height).get();
            }
        } catch (Throwable e) {
            BaseCoreApp.getAppComponent().getLogger().logError(e);
        }
        return file;
    }

    // -------------------------> Builder Helpers

    private static boolean validate(Builder builder) {
        return builder != null;
    }

    private static void prepareBuilderAttrs(Builder builder) {
        ImageView imageView = builder.imageView;
        final Target target = builder.target;
        NotificationTargetModel notificationTargetModel = builder.notificationTargetModel;
        if (imageView != null) {
            if (builder.width == DEFAULT_IMAGE_SIZE) {
                builder.width(imageView.getWidth() > 0 ? imageView.getWidth() : DEFAULT_IMAGE_SIZE);
            }
            if (builder.height == DEFAULT_IMAGE_SIZE) {
                builder.height(imageView.getHeight() > 0 ? imageView.getHeight() : DEFAULT_IMAGE_SIZE);
            }
        } else if (notificationTargetModel != null) {
            builder.noPlaceHolder();
        }
        // Resize
        if (builder.width == DEFAULT_IMAGE_SIZE) {
            builder.width = MAX_WIDTH;
        }
        if (builder.height == DEFAULT_IMAGE_SIZE) {
            builder.height = MAX_HEIGHT;
        }
        // PlaceHolder
        if (builder.placeHolderDrawableId == DEFAULT_PLACE_HOLDER) {
            builder.placeHolderDrawableId = DEFAULT_PLACE_HOLDER_ID;
        }
    }

    private static void addRequestAttrs(DrawableTypeRequest<String> request, Builder builder) {
        final Callback callback = builder.callback;
        //Resize
        request.override(builder.width, builder.height);
        // Scale Type
        if (builder.scaleType != null) {
            switch (builder.scaleType) {
                case CENTER_INSIDE:
                    request.fitCenter();
                    break;
                case CENTER_CROP:
                    request.centerCrop();
                    break;
            }
        }
        // PlaceHolder
        if (builder.placeHolderDrawableId != NO_PLACE_HOLDER) {
            request.placeholder(builder.placeHolderDrawableId).error(builder.placeHolderDrawableId);
        }
        // Default Animation Enabled
        if (!builder.animate) {
            request.dontAnimate().dontTransform();
        }
        //Listener
        RequestListener<String, GlideDrawable> requestListener = new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFirstResource) {
                try {
                    BaseCoreApp.getAppComponent().getLogger().logError(e);
                    if (callback != null) {
                        callback.onError();
                    }
                } catch (Throwable ee) {
                    BaseCoreApp.getAppComponent().getLogger().logError(ee);
                }
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                try {
                    if (callback != null) {
                        callback.onSuccess();
                    }
                } catch (Throwable ee) {
                    BaseCoreApp.getAppComponent().getLogger().logError(ee);
                }
                return false;
            }
        };
        request.listener(requestListener).diskCacheStrategy(DiskCacheStrategy.SOURCE);
    }

    private static void addTargetObject(DrawableTypeRequest<String> request, final Builder builder) {
        ImageView imageView = builder.imageView;
        final Target target = builder.target;
        NotificationTargetModel notificationTargetModel = builder.notificationTargetModel;
        if (imageView != null) {
            request.dontAnimate().dontTransform().into(imageView);
        } else if (target != null) {
            // Load Target PlaceHolder
            SimpleTarget simpleTarget = new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                    try {
                        if (target != null) {
                            if (bitmap != null) target.onImageReady(bitmap);
                        }
                    } catch (Throwable e) {
                        BaseCoreApp.getAppComponent().getLogger().logError(e);
                    }
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    try {
                        if (target != null) {
                            target.onError(e);
                        }
                    } catch (Throwable ee) {
                        BaseCoreApp.getAppComponent().getLogger().logError(ee);
                    }
                }
            };
            request.asBitmap().dontAnimate().dontTransform().into(simpleTarget);
        } else if (notificationTargetModel != null) {
            NotificationTarget notificationTarget = new NotificationTarget(builder.context, notificationTargetModel.notificationRemoteView, notificationTargetModel.remoteViewImageViewId, builder.width, builder.height, notificationTargetModel.notification, notificationTargetModel.notificationId);
            request.asBitmap().dontAnimate().dontTransform().into(notificationTarget);
        }
    }

    // --------------------->

    public enum ScaleType {
        CENTER_INSIDE, CENTER_CROP;
    }

    public static abstract class Target {
        public void onImageReady(Bitmap bitmap) {
        }

        public void onError(Exception e) {
        }
    }

    public static abstract class Callback {
        public void onSuccess() {
        }

        public void onError() {
        }
    }

    private static class NotificationTargetModel {
        private int notificationId;
        private Notification notification;
        private int remoteViewImageViewId;
        private RemoteViews notificationRemoteView;

        public NotificationTargetModel(int notificationId, Notification notification, int remoteViewImageViewId, RemoteViews notificationRemoteView) {
            this.notificationId = notificationId;
            this.notification = notification;
            this.remoteViewImageViewId = remoteViewImageViewId;
            this.notificationRemoteView = notificationRemoteView;
        }

        public int getNotificationId() {
            return notificationId;
        }

        public Notification getNotification() {
            return notification;
        }

        public int getRemoteViewImageViewId() {
            return remoteViewImageViewId;
        }

        public RemoteViews getNotificationRemoteView() {
            return notificationRemoteView;
        }
    }

    static class NetworkDisablingLoader implements StreamModelLoader<String> {
        @Override
        public DataFetcher<InputStream> getResourceFetcher(final String model, int width, int height) {
            return new DataFetcher<InputStream>() {
                @Override
                public InputStream loadData(Priority priority) throws Exception {
                    throw new IOException("Forced Glide network failure");
                }

                @Override
                public void cleanup() {
                }

                @Override
                public String getId() {
                    return model;
                }

                @Override
                public void cancel() {
                }
            };
        }
    }
}