package com.zy.zy.pagination;

/**
 * Created by Administrator on 2016/8/31.
 */
public class IllegalArgumentException extends Exception {
  
  /**
  * 
  */
  private static final long serialVersionUID = 5553780544211529488L;
  
  public IllegalArgumentException() {
  }
  
  public IllegalArgumentException(String message) {
    super(message);
  }
  
  public IllegalArgumentException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public IllegalArgumentException(Throwable cause) {
    super(cause);
  }
  
}
