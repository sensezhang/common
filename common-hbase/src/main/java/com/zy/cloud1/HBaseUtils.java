package com.zy.cloud1;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/30.
 */
public class HBaseUtils {

  private static Configuration conf;
  private static Connection con;

  private static final String ZKUrl = "hbase.zookeeper.quorum";

  private static Logger logger = LoggerFactory.getLogger(HBaseUtils.class);

  //初始化链接
  static {
    conf = HBaseConfiguration.create(); //获取配置文件对象
    conf.set("hbase.zookeeper.quorum",HBasePropertiesUtil.getStr(ZKUrl, "127.0.0.1:2181"));
    try {
      con = ConnectionFactory.createConnection(conf);
    } catch (IOException e) {
      e.printStackTrace();
      logger.error("get hbase clien connection error! please check properties", e);
    }
  }

  // 获得链接
  public static Connection getCon() {
    if (con == null || con.isClosed()) {
      try {
        con = ConnectionFactory.createConnection(conf);
      } catch (IOException e) {
        e.printStackTrace();
        logger.error("get hbase clien connection error! please check properties", e);
      }
    }
    return con;
  }

  // 关闭链接
  public static void close() {
    if (con != null) {
      try {
        con.close();
      } catch (IOException e) {
        logger.error("close hbase clien connection error!", e);
      }
    }
  }

  // 创建表
  public static void createTable(String tableName, String... familyColumn) {
    TableName tn = TableName.valueOf(tableName);
    try {
      Admin admin = getCon().getAdmin();
      HTableDescriptor htd = new HTableDescriptor(tn);
      for (String fc : familyColumn) {
        HColumnDescriptor hcd = new HColumnDescriptor(fc);
        htd.addFamily(hcd);
      }
      admin.createTable(htd);
      admin.close();
    } catch (IOException e) {
      e.printStackTrace();
      logger.error("create hbase table error", e);
    }
  }

  // 删除表
  public static void dropTable(String tableName) {
    TableName tn = TableName.valueOf(tableName);
    try {
      Admin admin = getCon().getAdmin();
      admin.disableTable(tn);
      admin.deleteTable(tn);
      admin.close();
    } catch (IOException e) {
      e.printStackTrace();
      logger.error("drop table error", e);
    }
  }

  //插入或更新数据
  public static boolean save(String tableName, String rowkey, String family, String qualifier,
          String value) {
    try {
      Table t = getCon().getTable(TableName.valueOf(tableName));
      Put put = new Put(Bytes.toBytes(rowkey));
      put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
      t.put(put);
      return true;
    } catch (IOException e) {
      logger.error("save table error!", e);
    } finally {
      HBaseUtils.close();
    }
    return false;
  }

  // 删除记录
  public static boolean del(String tableName, String rowkey, String family, String qualifier) {
    try {
      Table t = getCon().getTable(TableName.valueOf(tableName));
      Delete del = new Delete(Bytes.toBytes(rowkey));
      if (qualifier != null) {
        del.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
      } else if (family != null) {
        del.addFamily(Bytes.toBytes(family));
      }
      t.delete(del);
      return true;
    } catch (IOException e) {
      logger.error("delete table value error!", e);
    } finally {
      HBaseUtils.close();
    }
    return false;
  }

  // 删除一行记录
  public static boolean del(String tableName, String rowkey) {
    return del(tableName, rowkey, null, null);
  }

  // 删除一行下的一个列族
  public static boolean del(String tableName, String rowkey, String family) {
    return del(tableName, rowkey, family, null);
  }

  // 读取数据
  public static String byGet(String tableName, String rowkey, String family, String qualifier) {
    try {
      Table t = getCon().getTable(TableName.valueOf(tableName));
      Get get = new Get(Bytes.toBytes(rowkey));
      get.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
      Result result = t.get(get);
      return Bytes.toString(CellUtil.cloneValue(result.listCells().get(0)));
    } catch (IOException e) {
      logger.error("get value from hbase error", e);
    }
    return null;
  }

  // 取到一个族列的值
  public static Map<String, String> byGet(String tableName, String rowkey, String family) {
    final Map<String, String> result = new HashMap<>();
    try {
      Table t = getCon().getTable(TableName.valueOf(tableName));
      Get get = new Get(Bytes.toBytes(rowkey));
      get.addFamily(Bytes.toBytes(family));
      Result rs = t.get(get);
      List<Cell> cs = rs.listCells();
      if (cs != null) {
        cs.forEach((cell) -> result.put(Bytes.toString(CellUtil.cloneQualifier(cell)),
                Bytes.toString(CellUtil.cloneValue(cell))));
      }
    } catch (IOException e) {
      logger.error("get map from hbase error", e);
    }
    return result.size() == 0 ? null : result;
  }

  public static Map<String, Map<String, String>> byGet(String tableName, String rowkey) {
    final Map<String, Map<String, String>> results = new HashMap<>();

    try {
      Table t = getCon().getTable(TableName.valueOf(tableName));
      Get get = new Get(Bytes.toBytes(rowkey));
      Result rs = t.get(get);
      List<Cell> cs = rs.listCells();
      if (cs != null) {
        for (Cell cell : cs) {
          String familyName = Bytes.toString(CellUtil.cloneFamily(cell));
          if (!results.containsKey(familyName)) {
            results.put(familyName, new HashMap<>());
          }
          results.get(familyName).put(Bytes.toString(CellUtil.cloneQualifier(cell)),
                  Bytes.toString(CellUtil.cloneValue(cell)));
        }
      }
    } catch (IOException e) {
      logger.error("get map from hbase error", e);
    }
    return results.size() == 0 ? null : results;
  }
}
