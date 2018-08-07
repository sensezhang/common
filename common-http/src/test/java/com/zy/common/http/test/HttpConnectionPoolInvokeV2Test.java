package com.zy.common.http.test;

import com.zy.common.http.HttpConnectPoolInvokeV2;

import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

public class HttpConnectionPoolInvokeV2Test {
  
  @Rule
  public MockServerRule mockServerRule = new MockServerRule(this, 5000);
  public MockServerClient mockServerClient = new MockServerClient("localhost", 5000);

  
  private static final String TEST_URI = "http://localhost:5000/http/json/post";
  private static final String JSON = "{\"userToken\":\"\"}";
  
  @Test
  public void testHttpPostJson() throws Exception {
    // mock http server
    mockServerClient
        .when(HttpRequest.request().withMethod("POST").withPath("/http/json/post"))
        .respond(HttpResponse.response().withStatusCode(200).withBody(
            "{\"access_token\": \"da13ddf9-c3c6-49b7-ae4d-7cb3594b9c9f\",\"token_type\": \"bearer\",\"refresh_token\": \"77af4b19-7bd3-4162-b00e-e76b4e5a6fda\",\"expires_in\": 1612855,\"scope\": \"read write\"}"));
    
    // 发送Post请求
    String res = HttpConnectPoolInvokeV2.sendHttpPostRequest(TEST_URI, JSON);
    System.out.println("===========================");
    System.out.println("Result:\n\t" + res);
    System.out.println("===========================");
  }
}
