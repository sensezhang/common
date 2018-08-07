package com.zy.common.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpStreamConnection {
  private String url;
  private byte[] data;
  
  public HttpStreamConnection(String url, byte[] data) {
    this.url = url;
    this.data = data;
  }
  
  public byte[] sendRequest() {
    HttpURLConnection connection = null;
    try {
      URL url = new URL(this.url);
      
      connection = (HttpURLConnection) url.openConnection();
      connection.setDoOutput(true);
      connection.setRequestMethod("POST");
      connection.getOutputStream().write(data);
      connection.getOutputStream().flush();
      connection.getOutputStream().close();
      int code = connection.getResponseCode();
      if (code != 200) {
        return null;
      }
      InputStream in = connection.getInputStream();
      byte[] result = new byte[in.available()];
      in.read(result);
      return result;
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    } catch (ProtocolException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }
  
}
