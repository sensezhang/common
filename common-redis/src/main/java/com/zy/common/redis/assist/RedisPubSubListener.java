/**===================================================================
 * 北京深思数盾科技有限公司
 * 日期：2015年10月15日 上午10:08:15
 * 作者：jiangtao
 * 版本：1.0.0
 * 版权：All rights reserved.
 *===================================================================
 * 修订日期           修订人               描述
 * 2015年10月15日     jiangtao 创建
 */

package com.zy.common.redis.assist;

import redis.clients.jedis.JedisPubSub;

import java.util.Map;


public class RedisPubSubListener<K, V> extends JedisPubSub {
  private Map<K, V> map;
  
  public RedisPubSubListener(Map<K, V> map) {
    
    this.map = map;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see redis.clients.jedis.JedisPubSub#onMessage(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void onMessage(String channel, String message) {
    map.remove(message);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see redis.clients.jedis.JedisPubSub#onPMessage(java.lang.String,
   * java.lang.String, java.lang.String)
   */
  @Override
  public void onPMessage(String arg0, String arg1, String arg2) {
    
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see redis.clients.jedis.JedisPubSub#onPSubscribe(java.lang.String, int)
   */
  @Override
  public void onPSubscribe(String arg0, int arg1) {
    
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see redis.clients.jedis.JedisPubSub#onPUnsubscribe(java.lang.String, int)
   */
  @Override
  public void onPUnsubscribe(String arg0, int arg1) {
    
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see redis.clients.jedis.JedisPubSub#onSubscribe(java.lang.String, int)
   */
  @Override
  public void onSubscribe(String arg0, int arg1) {
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see redis.clients.jedis.JedisPubSub#onUnsubscribe(java.lang.String, int)
   */
  @Override
  public void onUnsubscribe(String arg0, int arg1) {
    
  }
  
}
