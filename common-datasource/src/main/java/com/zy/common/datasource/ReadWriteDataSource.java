package com.zy.common.datasource;

import com.zy.common.datasource.constants.Constants;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.util.CollectionUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;


public class ReadWriteDataSource extends AbstractDataSource implements InitializingBean {
  // private static final Logger log =
  // Logger.getLogger(ReadWriteDataSource.class);
  
  private DataSource writeDataSource;
  private Map<String, DataSource> readDataSourceMap;
  
  private String[] readDataSourceNames;
  private List<DataSource> readDataSources;
  private int readDataSourceCount;
  
  private AtomicInteger counter = new AtomicInteger(1);
  
  public void setReadDataSourceMap(Map<String, DataSource> readDataSourceMap) {
    this.readDataSourceMap = readDataSourceMap;
  }
  
  public void setWriteDataSource(DataSource writeDataSource) {
    this.writeDataSource = writeDataSource;
  }
  
  @Override
  public void afterPropertiesSet() throws Exception {
    if (writeDataSource == null) {
      throw new IllegalArgumentException("property 'writeDataSource' is required");
    }
    if (CollectionUtils.isEmpty(readDataSourceMap)) {
      throw new IllegalArgumentException("property 'readDataSourceMap' is required");
    }
    readDataSourceCount = readDataSourceMap.size();
    
    readDataSources = new CopyOnWriteArrayList<DataSource>();
    readDataSourceNames = new String[readDataSourceCount];
    
    int index = 0;
    for (Entry<String, DataSource> e : readDataSourceMap.entrySet()) {
      readDataSources.add(e.getValue());
      readDataSourceNames[index] = e.getKey();
      index++;
    }
    new Thread(new Runnable() {
      final List<DataSource> lost = new ArrayList<DataSource>();
      
      @Override
      public void run() {
        try {
          while (true) {
            for (DataSource datasource : readDataSources) {
              if (checkDataSource(datasource)) {
                continue;
              } else {
                logger.error("数据源:" + datasource + " 连接失败");
                readDataSources.remove(datasource);
                readDataSourceCount--;
                reCon(datasource);
              }
            }
            for (DataSource datasource : lost) {
              if (checkDataSource(datasource)) {
                logger.info("数据源:" + datasource + " 重连成功");
                readDataSources.add(datasource);
                readDataSourceCount++;
                lost.remove(datasource);
                break;
              } else {
                logger.info("数据源:" + datasource + " 重连失败..retry...");
              }
            }
            TimeUnit.MINUTES.sleep(Constants.Minutes.ONE);
          }
        } catch (InterruptedException ex) {
          logger.error("check datasource thread interrupted");
        }
      }
      
      private void reCon(DataSource datasource) {
        lost.add(datasource);
      }
    }).start();
  }
  
  private DataSource determineDataSource() {
    if (ReadWriteDataSourceDecision.isChoiceWrite()) {
      if (logger.isDebugEnabled()) {
        logger.debug("current determine write datasource");
      }
      return writeDataSource;
    }
    
    if (ReadWriteDataSourceDecision.isChoiceNone()) {
      if (logger.isDebugEnabled()) {
        logger.debug("no choice read/write, default determine write datasource");
      }
      return writeDataSource;
    }
    return determineReadDataSource();
  }
  
  private DataSource determineReadDataSource() {
    int index = counter.incrementAndGet() % readDataSourceCount;
    if (index < 0) {
      index = -index;
    }
    
    String dataSourceName = readDataSourceNames[index];
    if (logger.isDebugEnabled()) {
      logger.debug("current determine read datasource : {" + dataSourceName + "}");
    }
    
    return readDataSources.get(index);
  }
  
  private boolean checkDataSource(DataSource dataSource) {
    Statement stm = null;
    Connection con = null;
    ResultSet rs = null;
    try {
      con = dataSource.getConnection();
      stm = con.createStatement();
      rs = stm.executeQuery("SELECT VERSION()");
    } catch (SQLException ex) {
      return false;
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (stm != null) {
          stm.close();
        }
        if (con != null) {
          con.close();
        }
      } catch (SQLException ex) {
        logger.error("close datasource exception");
      }
    }
    return true;
  }
  
  @Override
  public Connection getConnection() throws SQLException {
    return determineDataSource().getConnection();
  }
  
  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    return determineDataSource().getConnection(username, password);
  }
  
  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    // TODO Auto-generated method stub
    return false;
  }
  
  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    // TODO Auto-generated method stub
    return null;
  }
  
}
