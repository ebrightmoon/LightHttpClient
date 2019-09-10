package com.ebrightmoon.htppclient.core;

import android.content.Context;


import com.ebrightmoon.htppclient.api.ApiService;
import com.ebrightmoon.htppclient.api.AppConfig;
import com.ebrightmoon.htppclient.callback.ACallback;
import com.ebrightmoon.htppclient.callback.UCallback;
import com.ebrightmoon.htppclient.convert.GsonConverterFactory;
import com.ebrightmoon.htppclient.interceptor.LoggingInterceptor;
import com.ebrightmoon.htppclient.subscriber.ApiCallbackSubscriber;
import com.ebrightmoon.htppclient.util.MediaTypes;
import com.ebrightmoon.htppclient.util.SSLUtils;
import com.ebrightmoon.htppclient.util.UploadProgressRequestBody;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import static com.ebrightmoon.htppclient.util.MediaTypes.APPLICATION_JSON_TYPE;


/**
 * Time: 2019-09-09
 * Author:wyy
 * Description:
 */
public class AppHttpClient {


    private static int DEFAULT_TIMEOUT = 120;
    private Retrofit retrofit;
    private ApiService apiService;
    public static String baseUrl = AppConfig.BASE_URL;
    // ================    请求类      ==============
    private Context mContext;
    private OkHttpClient.Builder okHttpBuilder;
    private Retrofit.Builder retrofitBuilder;
    private List<MultipartBody.Part> multipartBodyParts;
    private Map<String, RequestBody> params = new HashMap<>();
    private Map<String, String> header = new HashMap<>();

    private static AppHttpClient instance;

    public static AppHttpClient getInstance() {
        if (instance == null) {
            synchronized (AppHttpClient.class) {
                if (instance == null) {
                    instance = new AppHttpClient();
                }
            }
        }
        return instance;
    }

    private AppHttpClient() {
        okHttpBuilder = new OkHttpClient.Builder();
        retrofitBuilder = new Retrofit.Builder();
    }

    /**
     * 初始化参数
     */
    private void initParams() {
        okHttpBuilder.addInterceptor(new LoggingInterceptor())
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        okHttpBuilder.hostnameVerifier(new SSLUtils.UnSafeHostnameVerifier(baseUrl));
        retrofitBuilder = new Retrofit.Builder();
        retrofit = retrofitBuilder
                .client(okHttpBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();

    }


    private  <T> T create(final Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        return retrofit.create(service);
    }


    public <T> void get(Context mContext, String suffixUrl, Map<String, String> params, ACallback<T> callback) {
        initParams();
        DisposableObserver disposableObserver = new ApiCallbackSubscriber<T>(callback);
        create(ApiService.class)
                .get(suffixUrl,header,params)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new ApiFunc<T>(getType(callback)))
                .retryWhen(new ApiRetryFunc(AppConfig.DEFAULT_RETRY_COUNT,
                        AppConfig.DEFAULT_RETRY_DELAY_MILLIS))
                .subscribe(disposableObserver);


    }



    public <T> void post(Context mContext, String suffixUrl, Map<String, Object> params, ACallback<T> callback) {
        initParams();
        DisposableObserver disposableObserver = new ApiCallbackSubscriber<T>(callback);
        create(ApiService.class)
                .post(suffixUrl,header,params)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new ApiFunc<T>(getType(callback)))
                .retryWhen(new ApiRetryFunc(AppConfig.DEFAULT_RETRY_COUNT,
                        AppConfig.DEFAULT_RETRY_DELAY_MILLIS))
                .subscribe(disposableObserver);


    }


    public <T> void postJson(Context mContext, String suffixUrl, String json, ACallback<T> callback) {
        initParams();
        DisposableObserver disposableObserver = new ApiCallbackSubscriber<T>(callback);
        RequestBody body = RequestBody.create(APPLICATION_JSON_TYPE, json);
        create(ApiService.class)
                .postBody(suffixUrl,header ,body)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new ApiFunc<T>(getType(callback)))
                .retryWhen(new ApiRetryFunc(AppConfig.DEFAULT_RETRY_COUNT,
                        AppConfig.DEFAULT_RETRY_DELAY_MILLIS))
                .subscribe(disposableObserver);


    }

    /**
     * @param url                下载地址
     * @param saveFile           保存文件
     * @param disposableObserver 控制是否取消网络
     * @return
     */
    public DisposableObserver downLoad(String url,Map<String, String> params, String saveFile, DisposableObserver disposableObserver) {
        create(ApiService.class).downFile(url,header,params)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(ResponseBody::byteStream)
                .observeOn(Schedulers.computation())// 用于计算任务
                .map(inputStream -> writeFile(inputStream, saveFile))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposableObserver);
        return disposableObserver;
    }

    /**
     * 将输入流写入文件
     *
     * @param inputString
     * @param filePath
     */
    private String writeFile(InputStream inputString, String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);

            byte[] b = new byte[1024];

            int len;
            while ((len = inputString.read(b)) != -1) {
                fos.write(b, 0, len);
            }
            inputString.close();
            fos.close();
            return "下载成功";
        } catch (FileNotFoundException e) {
            return e.toString();
        } catch (IOException e) {
            return e.toString();
        }
    }


    /**
     * 上传文件 无头文件 返回 返回ResponseResult  数据 不能监控文件上传进度
     *
     * @param url
     * @param <T>
     */
    public <T> void uploadFiles(String url, Map<String, File> files, ACallback<T> callback) {
        params = new HashMap<>();
        for (Map.Entry<String, File> entry : files.entrySet()) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), entry.getValue());
            params.put("file\"; filename=\"" + entry.getValue() + "", requestBody);
        }
        DisposableObserver disposableObserver = new ApiCallbackSubscriber<T>(callback);
        create(ApiService.class).uploadFiles(url,header,params)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new ApiRetryFunc(AppConfig.DEFAULT_RETRY_COUNT,
                        AppConfig.DEFAULT_RETRY_DELAY_MILLIS))
                .subscribe(disposableObserver);


    }
    /**
     * 上传文件带参数校验
     *
     * @param url
     * @param <T>
     */
    public <T> void uploadFiles(String url, ACallback<T> callback) {
        if (multipartBodyParts == null) {
            multipartBodyParts = new ArrayList<>();
        }
        DisposableObserver disposableObserver = new ApiCallbackSubscriber<T>(callback);
        create(ApiService.class).uploadFiles(url, header, multipartBodyParts)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new ApiRetryFunc(AppConfig.DEFAULT_RETRY_COUNT,
                        AppConfig.DEFAULT_RETRY_DELAY_MILLIS))
                .subscribe(disposableObserver);

    }

    /**
     * @param fileMap
     * @return
     */
    public AppHttpClient addFiles(Map<String, File> fileMap) {
        if (fileMap == null) {
            return this;
        }
        for (Map.Entry<String, File> entry : fileMap.entrySet()) {
            addFile(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * @param key
     * @param file
     * @return
     */
    public AppHttpClient addFile(String key, File file) {
        return addFile(key, file, null);
    }

    /**
     * 返回ResponseResult  数据
     *
     * @param key
     * @param file
     * @param callback
     * @return
     */
    public AppHttpClient addFile(String key, File file, UCallback callback) {
        if (key == null || file == null) {
            return this;
        }
        if (multipartBodyParts == null) {
            multipartBodyParts = new ArrayList<>();
        }
        RequestBody requestBody = RequestBody.create(MediaTypes.APPLICATION_OCTET_STREAM_TYPE, file);
        if (callback != null) {
            UploadProgressRequestBody uploadProgressRequestBody = new UploadProgressRequestBody(requestBody, callback);
            MultipartBody.Part part = MultipartBody.Part.createFormData(key, file.getName(), uploadProgressRequestBody);
            multipartBodyParts.add(part);
        } else {
            MultipartBody.Part part = MultipartBody.Part.createFormData(key, file.getName(), requestBody);
            multipartBodyParts.add(part);
        }
        return this;
    }

    /**
     * 获取第一级type
     *
     * @param t
     * @param <T>
     * @return
     */
    protected <T> Type getType(T t) {
        Type genType = t.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];
        Type finalNeedType;
        if (params.length > 1) {
            if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");
            finalNeedType = ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            finalNeedType = type;
        }
        return finalNeedType;
    }

    /**
     * 获取次一级type(如果有)
     *
     * @param t
     * @param <T>
     * @return
     */
    protected <T> Type getSubType(T t) {
        Type genType = t.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];
        Type finalNeedType;
        if (params.length > 1) {
            if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");
            finalNeedType = ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            if (type instanceof ParameterizedType) {
                finalNeedType = ((ParameterizedType) type).getActualTypeArguments()[0];
            } else {
                finalNeedType = type;
            }
        }
        return finalNeedType;
    }


}
