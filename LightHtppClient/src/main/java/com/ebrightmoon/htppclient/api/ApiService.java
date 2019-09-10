package com.ebrightmoon.htppclient.api;


import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Time: 2019-09-09
 * Author:wyy
 * Description:
 */
public interface ApiService {
    /**
     * 通用get请求
     *
     * @param url
     * @param maps
     * @return
     */
    @GET()
    Observable<ResponseBody> get(@Url String url, @HeaderMap Map<String, String> header, @QueryMap Map<String, String> maps);


    /**
     *
     * @param url
     * @param maps
     * @return
     */

    @FormUrlEncoded
    @POST()
    Observable<ResponseBody> post(@Url() String url, @HeaderMap Map<String, String> header, @FieldMap Map<String, Object> maps);

    /**
     * json
     * @param url
     * @param requestBody
     * @return
     */
    @POST()
    Observable<ResponseBody> postBody(@Url() String url, @HeaderMap Map<String, String> header, @Body RequestBody requestBody);


    /**
     * 文件下载
     *
     * @param url
     * @param maps
     * @return
     */
    @Streaming
    @GET()
    Observable<ResponseBody> downFile(@Url() String url, @HeaderMap Map<String, String> header, @QueryMap Map<String, String> maps);

    /**
     * @param url
     * @param responseBody
     * @return
     */
    @Streaming
    @POST()
    Observable<ResponseBody> downFile(@Url() String url, @HeaderMap Map<String, String> header, @Body RequestBody responseBody);


    /**
     * 多个文件上传
     *
     * @param url
     * @param params
     * @return
     */
    @Multipart
    @POST()
    Observable<ResponseBody> uploadFiles(@Url() String url, @HeaderMap Map<String, String> header, @PartMap() Map<String, RequestBody> params);

    /**
     * 多个文件上传
     *
     * @param url
     * @param params
     * @return
     */
    @Multipart
    @POST()
    Observable<ResponseBody> uploadFiles(@Url() String url, @HeaderMap Map<String, String> header,
                                         @Part() List<MultipartBody.Part> params);


}
