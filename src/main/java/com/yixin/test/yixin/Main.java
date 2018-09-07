package com.yixin.test.yixin;


import java.io.IOException;
import java.util.UUID;

import com.yixin.until.EncryptUtils;
import com.yixin.until.SSLClientUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 此例子实现发起呼叫请求
 * @author Administrator
 *
 */
public class Main {
	private static Logger logger = LoggerFactory.getLogger(Main.class);

	private static RequestConfig requestConfig = RequestConfig.custom()
			.setSocketTimeout(5000).setConnectTimeout(3000).build();

	public static void main(String[] args) throws ClientProtocolException,
			IOException {
		String accountSid = "35d5b7f7-0795-4d95-ac41-09bf34986480"; // 开发者账号 accountSid，需要替换为开发者真正的值
		String token = "13478732-a438-468e-86b0-cde97d436bc3"; // 开发者账号的token，需要替换为开发者真正的值
		//测试企业 0902302190003
		String from = "13515716840"; // 主叫号码
        from = "18925720847";
		String to = "17756602705"; // 被叫号码
		//测试 appid
		String appId = "e1ee6064-fe09-4035-a6ee-c952623ac219"; //应用appId，需要替换为开发者真正的值
		
		CloseableHttpClient client = SSLClientUtils.createSSLInsecureClient();
		
		String url = "https://naas.ecplive.cn/api/exec/Account/"+accountSid+"/Call";
		
		
		RequestBuilder requestBuilder = RequestBuilder.post().setUri(url);
		requestBuilder.setConfig(requestConfig);
		long ts = System.currentTimeMillis();
		requestBuilder.addHeader("ts", ts + "");
		requestBuilder.addHeader("apiId", UUID.randomUUID().toString());
		requestBuilder.addHeader("accountSid", accountSid);
		requestBuilder.addHeader("sign", EncryptUtils.md5(accountSid + token + ts));

		requestBuilder.addParameter("from", from);
		requestBuilder.addParameter("to", to);
		requestBuilder.addParameter("appId", appId);
		
		HttpUriRequest req = requestBuilder.build();

		CloseableHttpResponse resp = client.execute(req);
		int statusCode = resp.getStatusLine().getStatusCode();
		if (statusCode != 200) {
			throw new RuntimeException("HTTP响应错误，statusCode:" + statusCode);
		}
		HttpEntity entity = resp.getEntity();
		if (entity != null) {
			String result = EntityUtils.toString(entity, "utf-8");
			logger.info(result);
		}
		EntityUtils.consume(entity);
	}

}
