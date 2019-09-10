package com.ebrightmoon.htppclient.subscriber;






import com.ebrightmoon.htppclient.util.ApiCode;
import com.ebrightmoon.htppclient.util.ApiException;

import io.reactivex.observers.DisposableObserver;


abstract class ApiSubscriber<T> extends DisposableObserver<T> {

    ApiSubscriber() {

    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof ApiException) {
            onError((ApiException) e);
        } else {
            onError(new ApiException(e, ApiCode.Request.UNKNOWN));
        }

    }

    protected abstract void onError(ApiException e);
}
