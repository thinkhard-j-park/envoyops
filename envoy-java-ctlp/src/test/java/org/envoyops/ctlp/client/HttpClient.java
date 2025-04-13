package org.envoyops.ctlp.client;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

public final class HttpClient {

	private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

	private HttpClient() {
	}

	private static HttpComponentsClientHttpRequestFactory requestFactory(String clientName) {
		RequestConfig config = RequestConfig.custom()
			.setConnectionRequestTimeout(Timeout.ofSeconds(5))
			.setResponseTimeout(Timeout.ofSeconds(5))
			.build();

		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(200);
		cm.setDefaultMaxPerRoute(200);
		cm.setDefaultSocketConfig(SocketConfig.DEFAULT);

		CloseableHttpClient client = HttpClientBuilder.create()
			.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
			.setRetryStrategy(new DefaultHttpRequestRetryStrategy(1, TimeValue.ofMilliseconds(1000)))
			.addRequestInterceptorFirst((httpRequest, entityDetails, httpContext) -> {
				httpContext.setAttribute("ts", System.currentTimeMillis());
				logger.info("[{}] Request -> {}", clientName, httpRequest);
			})
			.addResponseInterceptorFirst((httpResponse, entityDetails, httpContext) -> {
				long reqTs = (long) httpContext.getAttribute("ts");
				httpContext.removeAttribute("ts");
				logger.info("[{}] Response <- {}, {} ms", clientName, httpResponse, System.currentTimeMillis() - reqTs);
			})
			.setDefaultRequestConfig(config)
			.setConnectionManager(cm)
			.evictExpiredConnections()
			.evictIdleConnections(TimeValue.ofSeconds(30))
			.build();

		return new HttpComponentsClientHttpRequestFactory(client);
	}

	private static String getUserAgent(String clientName) {
		String osName = System.getProperty("os.name");
		String osVersion = System.getProperty("os.version");
		String javaVersion = System.getProperty("java.version");
		return String.format("%s/%s (%s; %s) Java/%s", clientName, "envoy-java-control-plane-test", osName, osVersion,
				javaVersion);
	}

	public static RestClient restClient(String clientName, String baseUrl) {
		return RestClient.builder()
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.defaultHeader(HttpHeaders.USER_AGENT, getUserAgent(clientName))
			.requestFactory(requestFactory(clientName))
			.baseUrl(baseUrl)
			.build();

	}

}
