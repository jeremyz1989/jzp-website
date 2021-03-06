package com.jzp.utils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author Hongyi Zheng
 * @date 2019/4/19
 */
public class HttpUtils {

    private static final Charset DEFAULT_ENC = StandardCharsets.UTF_8;
    private static final String CHARSET = "UTF-8";
    private static final String DEFAULT_CONTENT_TYPE = "application/json; charset=utf-8";
    private static final int STAT_SUC = 200;
    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * 发送POST请求
     * 默认contentType为application/json
     *
     * @param uri         请求地址
     * @param requestBody 请求体
     * @return 请求失败返回null
     */
    public static String doPost(String uri, String requestBody) {
        PostMethod postMethod = new PostMethod(uri);
        byte[] b = requestBody.getBytes(DEFAULT_ENC);
        RequestEntity entity = new InputStreamRequestEntity(new ByteArrayInputStream(b, 0, b.length), b.length, DEFAULT_CONTENT_TYPE);
        postMethod.setRequestEntity(entity);
        HttpClient httpClient = new HttpClient();
        try {
            int statusCode = httpClient.executeMethod(postMethod);
            if (statusCode == STAT_SUC) {
                return postMethod.getResponseBodyAsString();
            }
        } catch (IOException e) {
            logger.error("httpPost请求异常:{}", ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    public static String doGet(String url) {
        return doGet(url, null);
    }

    /**
     * 发送GET请求
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return 返回响应结果
     */
    public static String doGet(String url, Map<String, String> params) {

        // 创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String respString = "";
        CloseableHttpResponse response = null;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (params != null) {
                for (String key : params.keySet()) {
                    builder.addParameter(key, params.get(key));
                }
            }
            URI uri = builder.build();
            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);

            // 执行请求
            response = httpClient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == STAT_SUC) {
                respString = EntityUtils.toString(response.getEntity(), CHARSET);
            }
        } catch (Exception e) {
            logger.error("httpGet请求异常：{}", ExceptionUtils.getStackTrace(e));
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return respString;
    }

}
