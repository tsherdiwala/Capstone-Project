package com.knoxpo.stackyandroid.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by khushboo on 23/1/17.
 */

public class CircleNetworkImageView extends CircleImageView {

    private String mUrl;
    private int mDefaultImageId;
    private int mErrorImageId;
    private ImageLoader mImageLoader;
    private ImageLoader.ImageContainer mImageContainer;

    public CircleNetworkImageView(Context context) {
        super(context);
    }

    public CircleNetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public void setImageUrl(String url, ImageLoader imageLoader) {
        this.mUrl = url;
        this.mImageLoader = imageLoader;
        this.loadImageIfNecessary(false);
    }

    public void setDefaultImageResId(int defaultImage) {
        this.mDefaultImageId = defaultImage;
    }

    public void setErrorImageResId(int errorImage) {
        this.mErrorImageId = errorImage;
    }

    void loadImageIfNecessary(final boolean isInLayoutPass) {
        int width = this.getWidth();
        int height = this.getHeight();
        ScaleType scaleType = this.getScaleType();
        boolean wrapWidth = false;
        boolean wrapHeight = false;
        if(this.getLayoutParams() != null) {
            wrapWidth = this.getLayoutParams().width == -2;
            wrapHeight = this.getLayoutParams().height == -2;
        }

        boolean isFullyWrapContent = wrapWidth && wrapHeight;
        if(width != 0 || height != 0 || isFullyWrapContent) {
            if(TextUtils.isEmpty(this.mUrl)) {
                if(this.mImageContainer != null) {
                    this.mImageContainer.cancelRequest();
                    this.mImageContainer = null;
                }

                this.setDefaultImageOrNull();
            } else {
                if(this.mImageContainer != null && this.mImageContainer.getRequestUrl() != null) {
                    if(this.mImageContainer.getRequestUrl().equals(this.mUrl)) {
                        return;
                    }

                    this.mImageContainer.cancelRequest();
                    this.setDefaultImageOrNull();
                }

                int maxWidth = wrapWidth?0:width;
                int maxHeight = wrapHeight?0:height;
                ImageLoader.ImageContainer newContainer = this.mImageLoader.get(this.mUrl, new ImageLoader.ImageListener() {
                    public void onErrorResponse(VolleyError error) {
                        if(CircleNetworkImageView.this.mErrorImageId != 0) {
                            CircleNetworkImageView.this.setImageResource(CircleNetworkImageView.this.mErrorImageId);
                        }

                    }

                    public void onResponse(final ImageLoader.ImageContainer response, boolean isImmediate) {
                        if(isImmediate && isInLayoutPass) {
                            CircleNetworkImageView.this.post(new Runnable() {
                                public void run() {
                                    onResponse(response, false);
                                }
                            });
                        } else {
                            if(response.getBitmap() != null) {
                                CircleNetworkImageView.this.setImageBitmap(response.getBitmap());
                            } else if(CircleNetworkImageView.this.mDefaultImageId != 0) {
                                CircleNetworkImageView.this.setImageResource(CircleNetworkImageView.this.mDefaultImageId);
                            }

                        }
                    }
                }, maxWidth, maxHeight, scaleType);
                this.mImageContainer = newContainer;
            }
        }
    }

    private void setDefaultImageOrNull() {
        if(this.mDefaultImageId != 0) {
            this.setImageResource(this.mDefaultImageId);
        } else {
            this.setImageBitmap((Bitmap)null);
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.loadImageIfNecessary(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        if(this.mImageContainer != null) {
            this.mImageContainer.cancelRequest();
            this.setImageBitmap((Bitmap)null);
            this.mImageContainer = null;
        }

        super.onDetachedFromWindow();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        this.invalidate();
    }
}