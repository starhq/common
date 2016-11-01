package com.star.net.httpcomponent;

import com.star.collection.CollectionUtil;
import com.star.exception.pojo.ToolException;
import com.star.io.CharsetUtil;
import com.star.net.URLUtil;
import com.star.net.http.HttpMethod;
import com.star.string.StringUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 使用httpcomponent来做网络请求
 * <p>
 * Created by starhq on 2016/10/31.
 */
public final class HttpUtil {

    /**
     * 池的大小
     */
    private static final int SIZE = 100;
    /**
     * 超时时间
     */
    private static final int MAX_TIMEOUT = 7000;

    private HttpUtil() {
    }

    /**
     * 初始化httpclient
     *
     * @param isSSL 是否ssl加密
     * @return httpclient
     */
    private static CloseableHttpClient getHttpClient(final boolean isSSL) {
        final RequestConfig config = RequestConfig.custom().setConnectTimeout(MAX_TIMEOUT).setSocketTimeout
                (MAX_TIMEOUT).setConnectionRequestTimeout(MAX_TIMEOUT).build();

        CloseableHttpClient client;

        PoolingHttpClientConnectionManager connMgr;
        if (isSSL) {
            final RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder
                    .create();
            final ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();
            registryBuilder.register("http", plainSF);
            try {
                final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                final TrustStrategy anyTrustStrategy = (x509Certificates, str) -> true;
                final SSLContext sslContext = SSLContexts.custom().useProtocol("tls").loadTrustMaterial(trustStore,
                        anyTrustStrategy).build();
                final LayeredConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(sslContext,
                        (str, sslSession) -> true);
                registryBuilder.register("https", sslSF);
            } catch (KeyStoreException | KeyManagementException | NoSuchAlgorithmException e) {
                throw new ToolException(StringUtil.format("初始化https client失败: {}", e.getMessage()), e);
            }
            final Registry<ConnectionSocketFactory> registry = registryBuilder.build();
            connMgr = new PoolingHttpClientConnectionManager(registry);
            connMgr.setMaxTotal(SIZE);
            connMgr.setDefaultMaxPerRoute(SIZE);
            connMgr.setValidateAfterInactivity(1000);
            client = HttpClients.custom().setConnectionManager(connMgr).setDefaultRequestConfig(config).build();

        } else {
            connMgr = new PoolingHttpClientConnectionManager();
            connMgr.setMaxTotal(SIZE);
            connMgr.setDefaultMaxPerRoute(SIZE);
            connMgr.setValidateAfterInactivity(1000);
            client = HttpClients.custom().setConnectionManager(connMgr).setDefaultRequestConfig(config).build();
        }

        return client;
    }

    /**
     * 调用get方法
     *
     * @param apiUrl 接口地址
     * @param params kv参数对
     * @return 返回值
     */
    public static String callGetMethod(final String apiUrl, final Map<String, Object> params) {
        final HttpRequestBase httpGet = getHttpGetOrDelete(apiUrl, HttpMethod.GET, params);
        return doHttpMethod(httpGet, URLUtil.isHttps(apiUrl));
    }

    /**
     * 调用delete方法
     *
     * @param apiUrl 接口地址
     * @param params kv参数对
     * @return 返回值
     */
    public static String callDeleteMethod(final String apiUrl, final Map<String, Object> params) {
        final HttpRequestBase httpDelete = getHttpGetOrDelete(apiUrl, HttpMethod.DELETE, params);
        return doHttpMethod(httpDelete, URLUtil.isHttps(apiUrl));
    }

    /**
     * 调用post方法
     *
     * @param apiUrl 接口地址
     * @param params kv参数对
     * @return 返回值
     */
    public static String callPostMethod(final String apiUrl, final Object params) {
        final HttpRequestBase httpDelete = getHttpPostOrPut(apiUrl, HttpMethod.POST, params);
        return doHttpMethod(httpDelete, URLUtil.isHttps(apiUrl));
    }

    /**
     * 调用post方法
     *
     * @param apiUrl 接口地址
     * @param params kv参数对
     * @return 返回值
     */
    public static String callPutMethod(final String apiUrl, final Object params) {
        final HttpRequestBase httpDelete = getHttpPostOrPut(apiUrl, HttpMethod.PUT, params);
        return doHttpMethod(httpDelete, URLUtil.isHttps(apiUrl));
    }


    /**
     * 生成get或者delete方法
     *
     * @param apiUrl 接口地址
     * @param method http方法
     * @param params 参数，get和delete只支持kv形式
     * @return http方法
     */
    private static HttpRequestBase getHttpGetOrDelete(final String apiUrl, final HttpMethod method, final Map<String,
            Object>
            params) {
        final StringBuilder builder = new StringBuilder(apiUrl);
        if (!CollectionUtil.isEmpty(params)) {
            final List<NameValuePair> pairs = getNameValuePairs(params);
            try {
                builder.append('?').append(EntityUtils.toString(new UrlEncodedFormEntity(pairs, CharsetUtil
                        .CHARSET_UTF_8)));
            } catch (IOException e) {
                throw new ToolException(StringUtil.format("set params on get or delete method failure: {}", e
                        .getMessage()), e);
            }
        }

        HttpRequestBase httpMethod = null;

        switch (method) {
            case GET:
                httpMethod = new HttpGet(builder.toString());
                break;
            case DELETE:
                httpMethod = new HttpDelete(builder.toString());
                break;
            default:
                break;
        }


        return httpMethod;
    }

    /**
     * 生成post或者put方法
     *
     * @param apiUrl 接口地址
     * @param method http方法
     * @param params 参数，get和delete只支持kv形式
     * @return http方法
     */
    private static HttpRequestBase getHttpPostOrPut(final String apiUrl, final HttpMethod method, final Object
            params) {
        StringEntity entity = null;
        if (!Objects.isNull(params)) {
            if (params instanceof Map) {
                final List<NameValuePair> pairs = getNameValuePairs((Map<String, Object>) params);
                entity = new UrlEncodedFormEntity(pairs, CharsetUtil.CHARSET_UTF_8);
            } else {
                entity = new StringEntity(params.toString(), CharsetUtil.CHARSET_UTF_8);
                entity.setContentType("application/json");
            }
        }

        HttpEntityEnclosingRequestBase httpMethod = null;

        switch (method) {
            case POST:
                httpMethod = new HttpPost(apiUrl);
                break;
            case PUT:
                httpMethod = new HttpPut(apiUrl);
                break;
            default:
                break;
        }

        if (!Objects.isNull(entity)) {
            httpMethod.setEntity(entity);
        }


        return httpMethod;
    }

    /**
     * 调用弄http方法
     *
     * @param httpRequestBase http方法
     * @param isSSL           是否加密
     * @return 返回值
     */
    private static String doHttpMethod(final HttpRequestBase httpRequestBase, final boolean isSSL) {
        CloseableHttpResponse response = null;
        try {
            final CloseableHttpClient httpClient = getHttpClient(isSSL);
            response = httpClient.execute(httpRequestBase);
            return getResult(httpRequestBase, response);
        } catch (IOException e) {
            throw new ToolException(StringUtil.format("调用http接口失败: {}", e.getMessage()), e);
        } finally {
            close(response);
        }
    }

    /**
     * 关闭 response的输入流
     *
     * @param response 响应
     */
    private static void close(final CloseableHttpResponse response) {
        if (response != null) {
            try {
                EntityUtils.consume(response.getEntity());
            } catch (IOException e) {
                throw new ToolException(StringUtil.format("关闭response输入流失败: {}: {}", e.getMessage()), e);
            }
        }
    }

    /**
     * 获得返回值
     *
     * @param method   http方法
     * @param response 响应
     * @return 响应中的实体
     */
    private static String getResult(final HttpRequestBase method, final HttpResponse response) {
        final int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode != HttpStatus.SC_OK) {
            method.abort();
            throw new ToolException(StringUtil.format("调用接口返回状态码: {}", statusCode));
        }
        final HttpEntity entity = response.getEntity();
        try {
            return Objects.isNull(entity) ? "" : EntityUtils.toString(entity, CharsetUtil.CHARSET_UTF_8);
        } catch (IOException e) {
            throw new ToolException(StringUtil.format("handle http resoponse failure: {}", e.getMessage()), e);
        }
    }

    /**
     * 键值对封装成namevaluepair集合
     *
     * @param params 键值对
     * @return http参数
     */
    private static List<NameValuePair> getNameValuePairs(final Map<String, Object> params) {
        List<NameValuePair> pairs;
        if (CollectionUtil.isEmpty(params)) {
            pairs = Collections.emptyList();
        } else {
            pairs = new ArrayList<>(params.size());
            pairs.addAll(params.entrySet().stream().map(entry -> new BasicNameValuePair(entry.getKey(), entry
                    .getValue().toString())).collect(Collectors.toList()));
        }
        return pairs;

    }

}
