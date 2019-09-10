package com.ebrightmoon.http;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.ebrightmoon.htppclient.callback.ACallback;
import com.ebrightmoon.htppclient.core.AppHttpClient;
import com.ebrightmoon.htppclient.response.ResponseResult;

import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Map<String, Object> forms;
    private Map<String, String> params;
    private Button btn_post_json;
    private Button btn_post_form;
    private Button btn_get;
    private Button btn_upload;
    private Button btn_download;
    private Button btn_post_cache;
    private Button btn_get_cache;
    private Button btn_post_offcache;
    private Button btn_post_retrofit;
    private Button btn_post_baseurl;
    private String json;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        forms = new LinkedHashMap<>();
        params = new LinkedHashMap<>();
        params.put("member_id", "1502");
        params.put("loginAccount", "hetong001");
        params.put("account_type", "10");
        params.put("device_id", "b25e8eb903401c72e0175589");
        params.put("cartId", "16126");
        params.put("os_version", "8.0.0");
        params.put("version_code", "17");
        params.put("channel", "anzhi");
        params.put("productCount", "1");
        params.put("token", "1f41c45931205bb9d8b65f945ba0d811");
        params.put("network", "wifi");
        params.put("device_brand", "Xiaomi");
        params.put("device_platform", "android");
        params.put("timestamp", System.currentTimeMillis() + "");
        forms.putAll(params);
        json = "{\"catSort\":0,\"cityId\":1,\"currentPage\":1,\"customerId\":\"\",\"memberId\":\"1149\",\"timeSort\":1,\"warehouseId\":1,\"platformType\":2,\"token\":\"63e000663ba54343ee374811ac6d50bc\",\"accountType\":\"20\",\"loginAccount\":\"\",\"device_platform\":\"mobile\",\"sign\":\"20748eb8f360ac9c08a2e1e242d00393\"}";

        initView();
    }

    private void initView() {

        btn_post_json = findViewById(R.id.btn_post_json);
        btn_post_json.setOnClickListener(this);
        btn_post_form = findViewById(R.id.btn_post_form);
        btn_post_form.setOnClickListener(this);
        btn_post_cache = findViewById(R.id.btn_post_cache);
        btn_post_cache.setOnClickListener(this);
        btn_get_cache = findViewById(R.id.btn_get_cache);
        btn_get_cache.setOnClickListener(this);
        btn_get = findViewById(R.id.btn_get);
        btn_get.setOnClickListener(this);
        btn_upload = findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(this);
        btn_post_offcache = findViewById(R.id.btn_post_offcache);
        btn_post_offcache.setOnClickListener(this);
        btn_post_baseurl = findViewById(R.id.btn_post_baseurl);
        btn_post_baseurl.setOnClickListener(this);
        btn_post_retrofit = findViewById(R.id.btn_post_retrofit);
        btn_post_retrofit.setOnClickListener(this);
        findViewById(R.id.btn_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                AppHttpClient.getInstance().get(MainActivity.this, "api/mobile/cart/updateCartCount", params, new ACallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        Toast.makeText(MainActivity.this, data.toString(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        Toast.makeText(MainActivity.this, errCode+errMsg, Toast.LENGTH_LONG).show();

                    }
                });
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_post_json:
                AppHttpClient.getInstance().postJson(MainActivity.this, "api/mobile/cart/getShoppingCartList", json, new ACallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        Toast.makeText(MainActivity.this, data, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        Toast.makeText(MainActivity.this, errCode+errMsg, Toast.LENGTH_LONG).show();

                    }
                });
                break;
            case R.id.btn_post_form:
                AppHttpClient.getInstance().post(MainActivity.this, "api/mobile/cart/updateCartCount", forms, new ACallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        Toast.makeText(MainActivity.this, data, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        Toast.makeText(MainActivity.this, errCode+errMsg, Toast.LENGTH_LONG).show();

                    }
                });
                break;
            case R.id.btn_post_cache:
                break;
            case R.id.btn_post_offcache:
                break;
            case R.id.btn_post_retrofit:
                break;
            case R.id.btn_post_baseurl:
                break;
            case R.id.btn_get_cache:
                break;
            case R.id.btn_get:
                break;
            case R.id.btn_upload:
                break;

        }
    }
}
