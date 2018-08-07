package com.zy.common.datasource;

import com.alibaba.fastjson.JSON;
import com.google.common.base.CaseFormat;
import com.zy.common.datasource.anno.Condition;
import com.zy.common.datasource.anno.InsertIgnore;
import com.zy.common.datasource.anno.Value;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by limeng on 2018/2/2.
 */
public class Dao<T> {

  private static Logger logger = LoggerFactory.getLogger(Dao.class);

  private static final Map<String, String> saveSqlMap = new ConcurrentHashMap<String, String>();

  private static final Map<String, String> isExistsByOriginSqlMap = new ConcurrentHashMap<String, String>();

  private static final Map<String, String> updateOriginDataSqlMap = new ConcurrentHashMap<String, String>();

  private static final Map<String, String> queryForObjSqlMap = new ConcurrentHashMap<>();

  private static final Map<String, String> updateSqlMap = new ConcurrentHashMap<>();

  private static final Map<String, String> updateKeyValuesSqlMap = new ConcurrentHashMap<>();

  private NamedParameterJdbcTemplate jdbcTemplate = null;

  public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public int save(Object entity) throws Exception {
    try {
      int res = jdbcTemplate.update(getSaveSql(entity), new BeanPropertySqlParameterSource(entity));
      logger.debug("sql execute result:{}", res);
      return res;
    } catch (Exception e) {
      logger.error("save entity error", e);
      logger.error("params:{}", JSON.toJSONString(entity));
      logger.error("sql:{}", getSaveSql(entity));
      throw e;
    }
  }

  public int update(Object entity, String valueColumn, String whereCondition) throws Exception {
    try {
      String sql = getUpdateSql(entity, valueColumn, whereCondition);
      int res = jdbcTemplate.update(sql, new BeanPropertySqlParameterSource(entity));
      logger.debug("sql:{}", sql);
      logger.debug("params:{}", JSON.toJSONString(entity));
      logger.debug("sql execute result:{}", res);
      return res;
    } catch (Exception e) {
      logger.error("update entity error", e);
      errLog(entity, getUpdateSql(entity, valueColumn, whereCondition));
      throw e;
    }
  }

  public <T> T queryForObjForUpdate(T t, String... whereColumns) throws Exception {
    StringBuffer sqlMapKey = new StringBuffer(t.getClass().getName());
    try {
      if (whereColumns != null) {
        Arrays.stream(whereColumns).forEach(o -> sqlMapKey.append("_" + o));
      }
      if (logger.isDebugEnabled()) {
        logger.debug("sql:{}", getQueryForObjSql(t, sqlMapKey.toString(), whereColumns));
        logger.debug("params:{}", JSON.toJSONString(t));
        logger.debug("sql execute result:{}", JSON.toJSONString(jdbcTemplate.queryForObject(getQueryForObjSql(t, sqlMapKey.toString(), whereColumns), new BeanPropertySqlParameterSource(t), BeanPropertyRowMapper.newInstance(t.getClass()))));
      }
      return jdbcTemplate.queryForObject(getQueryForObjSql(t, sqlMapKey.toString(), whereColumns)+" for update "
              , new BeanPropertySqlParameterSource(t)
              , BeanPropertyRowMapper.newInstance((Class<T>) t.getClass()));
    } catch (EmptyResultDataAccessException e0) {
      logger.debug("sql execute result:null");
      return null;
    } catch (Exception e) {
      logger.error("queryForObj err:", e);
      errLog(t, getQueryForObjSql(t, sqlMapKey.toString(), whereColumns));
      throw e;
    }
  }

  /**
   * 查询对象，t如果是字符串，则直接认为是表名
   *
   * @param t
   * @param whereColumns 列名
   * @param <T>
   * @return
   * @throws Exception
   */
  public <T> T queryForObj(T t, String... whereColumns) throws Exception {
    StringBuffer sqlMapKey = new StringBuffer(t.getClass().getName());
    try {
      if (whereColumns != null) {
        Arrays.stream(whereColumns).forEach(o -> sqlMapKey.append("_" + o));
      }
      if (logger.isDebugEnabled()) {
        logger.debug("sql:{}", getQueryForObjSql(t, sqlMapKey.toString(), whereColumns));
        logger.debug("params:{}", JSON.toJSONString(t));
        logger.debug("sql execute result:{}", JSON.toJSONString(jdbcTemplate.queryForObject(getQueryForObjSql(t, sqlMapKey.toString(), whereColumns), new BeanPropertySqlParameterSource(t), BeanPropertyRowMapper.newInstance(t.getClass()))));
      }
      return jdbcTemplate.queryForObject(getQueryForObjSql(t, sqlMapKey.toString(), whereColumns)
              , new BeanPropertySqlParameterSource(t)
              , BeanPropertyRowMapper.newInstance((Class<T>) t.getClass()));
    } catch (EmptyResultDataAccessException e0) {
      logger.debug("sql execute result:null");
      return null;
    } catch (Exception e) {
      logger.error("queryForObj err:", e);
      errLog(t, getQueryForObjSql(t, sqlMapKey.toString(), whereColumns));
      throw e;
    }
  }

  /**
   * 查询list，t，则直接认为是表名
   *
   * @param t
   * @param whereColumns 列名
   * @return
   * @throws Exception
   */
  public <T> List<T> queryForList(T t, String... whereColumns) throws Exception {
    StringBuffer sqlMapKey = new StringBuffer(t.getClass().getName());
    try {
      if (whereColumns != null) {
        Arrays.stream(whereColumns).forEach(o -> sqlMapKey.append("_" + o));
      }
      if (logger.isDebugEnabled()) {
        logger.debug("sql:{}", getQueryForObjSql(t, sqlMapKey.toString(), whereColumns));
        logger.debug("params:{}", JSON.toJSONString(t));
        logger.debug("sql execute result:{}", JSON.toJSONString(jdbcTemplate.query(getQueryForObjSql(t, sqlMapKey.toString(), whereColumns), new BeanPropertySqlParameterSource(t), BeanPropertyRowMapper.newInstance(t.getClass()))));
      }
      return (List<T>) jdbcTemplate.query(getQueryForObjSql(t, sqlMapKey.toString(), whereColumns)
              , new BeanPropertySqlParameterSource(t), BeanPropertyRowMapper.newInstance(t.getClass()));
    } catch (EmptyResultDataAccessException e0) {
      logger.debug("sql execute result:null");
      return new ArrayList<>();
    } catch (Exception e) {
      logger.error("queryForObj err:", e);
      errLog(t, getQueryForObjSql(t, sqlMapKey.toString(), whereColumns));
      throw e;
    }
  }


  private String getQueryForObjSql(Object entity, String sqlMapKey, String... whereColumns) throws Exception {
    if (queryForObjSqlMap.get(sqlMapKey.toString()) == null) {
      StringBuffer sqlBuffer = new StringBuffer("SELECT * FROM " + getTableName(entity));
      if (whereColumns != null) {
        StringBuffer whereStatement = new StringBuffer();
        for (String column : whereColumns) {
          if (whereStatement.length() == 0) {
            whereStatement.append(" where ");
          } else {
            whereStatement.append(" and ");
          }
          whereStatement.append(turnHump2underLine(column) + "=:" + column);
        }
        sqlBuffer.append(whereStatement);
      }
      queryForObjSqlMap.put(sqlMapKey.toString(), sqlBuffer.toString());
    }
    debugLog(entity, queryForObjSqlMap.get(sqlMapKey));
    return queryForObjSqlMap.get(sqlMapKey);
  }

  public String getUpdateSql(Object entity, String valueColumn, String whereCondition) throws Exception {
    String mapKey = entity.getClass().getName() + "_" + valueColumn + "_" + whereCondition;
    if (updateSqlMap.get(mapKey) == null) {
      Field[] fields = entity.getClass().getDeclaredFields();
      StringBuffer whereConditions = new StringBuffer();
      StringBuffer setValues = new StringBuffer();
      for (Field field : fields) {
        appendWhereConditions(field, whereConditions, whereCondition);
        appendSetValues(field, setValues, valueColumn);
      }
      StringBuffer sqlBuffer = new StringBuffer("update " + getTableName(entity) + " set ");
      updateSqlMap.put(mapKey, sqlBuffer.append(setValues).append(whereConditions).toString());
    }
    debugLog(entity, updateSqlMap.get(mapKey));
    return updateSqlMap.get(mapKey);
  }

  /**
   * 根据 origin(作为where) 条件进行update.
   *
   * @param entity
   * @return
   * @throws Exception
   */
  public int updateOriginData(Object entity) throws Exception {
    try {
      int res = jdbcTemplate.update(getUpdateOriginDataSql(entity), new BeanPropertySqlParameterSource(entity));
      logger.debug("sql execute result:{}", res);
      return res;
    } catch (Exception e) {
      logger.error("save entity error", e);
      errLog(entity, getUpdateOriginDataSql(entity));
      throw e;
    }
  }

  /**
   * 根据 origin(作为where) 条件进行update.如果没有,则insert
   *
   * @param entity
   * @return
   * @throws Exception
   */
  @Transactional
  public int saveOrUpdateOriginData(Object entity) throws Exception {
    int res = 0;
    try {
      BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(entity);
      if (jdbcTemplate.queryForObject(getIsExistsSql(entity, "origin"), parameterSource, Integer.TYPE) > 0) {
        res = 1;
        res = jdbcTemplate.update(getUpdateOriginDataSql(entity), parameterSource);
        logger.debug("sql execute result:{}", res);
        return res;
      } else {
        res = 2;
        res = jdbcTemplate.update(getSaveSql(entity), parameterSource);
        logger.debug("sql execute result:{}", res);
        return res;
      }
    } catch (Exception e) {
      logger.error("saveOrUpdateOriginData error", e);
      logger.error("params:{}", JSON.toJSONString(entity));
      logger.error("res:{},sql:{}", res, res == 1 ? getUpdateOriginDataSql(entity) : getSaveSql(entity));
      throw e;
    }
  }

  public boolean isExistsByOrigin(Object entity) throws Exception {
    return jdbcTemplate.queryForObject(getIsExistsSql(entity, "origin"), new BeanPropertySqlParameterSource(entity), Integer.TYPE) > 0;

  }

  public boolean isExistsByCondition(Object entity, String condition) throws Exception {
    return jdbcTemplate.queryForObject(getIsExistsSql(entity, condition), new BeanPropertySqlParameterSource(entity), Integer.TYPE) > 0;
  }

  /**
   * 返回根据'origin'条件判断的是否存在
   *
   * @param entity
   * @return
   * @throws Exception
   */
  private static String getIsExistsSql(Object entity, String condition) throws Exception {
    String className = entity.getClass().getName();
    if (isExistsByOriginSqlMap.get(className + "_" + condition) == null) {
      Field[] fields = entity.getClass().getDeclaredFields();
      StringBuffer whereConditions = new StringBuffer();

      for (Field field : fields) {
        appendWhereConditions(field, whereConditions, condition);
      }
      String sql = "select count(1) from " + getTableName(entity) + whereConditions.toString();
      isExistsByOriginSqlMap.put(className, sql);
    }
    debugLog(entity, isExistsByOriginSqlMap.get(className));
    return isExistsByOriginSqlMap.get(className);
  }

  private static String getTableName(Object entity) throws Exception {
    Table tableAnnotation = entity.getClass().getAnnotation(Table.class);
    if (tableAnnotation != null && !StringUtils.isEmpty(tableAnnotation.name())) {
      return tableAnnotation.name();
    } else {
      return turnHump2underLine(entity.getClass().getName().substring(
              entity.getClass().getName().lastIndexOf(".") + 1));
    }
  }

  private static String getColumnName(Field field) throws Exception {
    Column column = field.getAnnotation(Column.class);
    if (column != null && !StringUtils.isEmpty(column.name())) {
      return column.name();
    } else {
      return turnHump2underLine(field.getName());
    }
  }

  /**
   * 根据conditionName判断是否追加该字段为where条件
   *
   * @param field
   * @param whereConditions
   * @param conditionName
   * @throws Exception
   */
  private static void appendWhereConditions(Field field, StringBuffer whereConditions, String conditionName) throws Exception {
    String columnName = getColumnName(field);
    Condition condition = field.getAnnotation(Condition.class);
    if (condition != null && condition.conditions().contains(conditionName)) {
      if (whereConditions.length() == 0) {
        whereConditions.append(" where ");
      } else {
        whereConditions.append(" and ");
      }
      whereConditions.append(columnName + "=:" + field.getName());
    }
  }

  /**
   * 根据conditionName判断是否追加该字段为where条件
   *
   * @param field
   * @param setValues
   * @param valueField
   * @throws Exception
   */
  private static void appendSetValues(Field field, StringBuffer setValues, String valueField) throws Exception {
    String columnName = getColumnName(field);
    Value value = field.getAnnotation(Value.class);
    if (value != null && value.name().equals(valueField)) {
      if (setValues.length() != 0) {
        setValues.append(" , ");
      }
      setValues.append(columnName + "=:" + field.getName());
    }
  }

  /**
   * 返回save的sql
   *
   * @param entity
   * @return
   * @throws Exception
   */
  private static String getSaveSql(Object entity) throws Exception {
    String className = entity.getClass().getName();
    if (saveSqlMap.get(className) == null) {
      Table tableAnnotation = entity.getClass().getAnnotation(Table.class);
      Field[] fields = entity.getClass().getDeclaredFields();
      StringBuffer columns = new StringBuffer();
      StringBuffer values = new StringBuffer();
      for (Field field : fields) {
        InsertIgnore insertIgnore = field.getAnnotation(InsertIgnore.class);
        if (insertIgnore != null) {
          continue;
        }
        Id id = field.getAnnotation(Id.class);
        if (id != null) {
          continue;
        }
        Column column = field.getAnnotation(Column.class);
        if (column != null && !StringUtils.isEmpty(column.name())) {
          columns.append(column.name() + ",");
        } else {
          columns.append(turnHump2underLine(field.getName()) + ",");
        }
        values.append(":" + field.getName() + ",");
      }
      columns.deleteCharAt(columns.lastIndexOf(","));
      values.deleteCharAt(values.lastIndexOf(","));
      String sql = "insert into " + getTableName(entity) + "(" + columns.toString() + ") values(" + values.toString() + ")";
      saveSqlMap.put(className, sql);
    }
    debugLog(entity, saveSqlMap.get(className));
    return saveSqlMap.get(className);
  }

  /**
   * 根据'origin'条件,返回update的sql
   *
   * @param entity
   * @return
   * @throws Exception
   */
  private static String getUpdateOriginDataSql(Object entity) throws Exception {
    String className = entity.getClass().getName();
    if (updateOriginDataSqlMap.get(className) == null) {
      String tableName = getTableName(entity);
      Field[] fields = entity.getClass().getDeclaredFields();
      StringBuffer updateSet = new StringBuffer();
      StringBuffer whereConditions = new StringBuffer();
      StringBuffer sqlBuffer = new StringBuffer("update " + tableName + " set ");
      for (Field field : fields) {
        String columnName;
        if (field.getName().equals("updateAt") || field.getName().equals("id")) {
          continue;
        }
        columnName = getColumnName(field);
        appendWhereConditions(field, whereConditions, "origin");
        if (updateSet.length() != 0) {
          updateSet.append(", ");
        }
        updateSet.append(columnName + "=:" + field.getName());
      }
      updateOriginDataSqlMap.put(className, sqlBuffer.append(updateSet).append(whereConditions).toString());
    }
    debugLog(entity, updateOriginDataSqlMap.get(className));
    return updateOriginDataSqlMap.get(className);
  }

  public NamedParameterJdbcTemplate getJdbcTemplate() {
    return jdbcTemplate;
  }

  /**
   * 将驼峰转换为下划线
   *
   * @param s
   * @return
   * @throws Exception
   */
  private static String turnHump2underLine(String s) throws Exception {
    return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, s);
  }

  public Dao() {
  }

  public Dao(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private static void debugLog(Object entity, String sql) {
    logger.debug("sql:{}", sql);
    logger.debug("param:" + JSON.toJSONString(entity));
  }

  private static void errLog(Object entity, String sql) {
    logger.error("sql:{}", sql);
    logger.error("param:" + JSON.toJSONString(entity));
  }

  private static <T> Class<T> getType(T t) {
    return (Class<T>) t.getClass().getGenericSuperclass();
  }

  private ResultSetExtractor getResultSetExtractorList(Object t) throws Exception {
    return new ResultSetExtractor() {
      @Override
      public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
        List list = new ArrayList();
        try {
          if (rs != null) {
            ResultSetMetaData rsmd = rs.getMetaData();
            Field[] fields = t.getClass().getDeclaredFields();
            while (rs.next()) {
              Object bean = t.getClass().newInstance();
              for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                String columnName = rsmd.getColumnName(i);
                Object columnValue = rs.getObject(i);
                for (Field field : fields) {
                  if (getColumnName(field).equalsIgnoreCase(columnName)) {
                    if (field.getType() == Date.class) {
                      if (columnValue.getClass() == Timestamp.class) {
                        BeanUtils.setProperty(bean, field.getName(), new Date(rs.getTimestamp(columnName).getTime()));
                      } else {
                        BeanUtils.setProperty(bean, field.getName(), new Date(Long.parseLong((String) columnValue)));
                      }
                    } else {
                      BeanUtils.setProperty(bean, field.getName(), columnValue);
                    }
                    break;
                  }
                }
              }
              list.add(bean);
            }
          }
        } catch (SQLException e0) {
          logger.error("getResultSetExtractorList error", e0);
          throw e0;
        } catch (DataAccessException e) {
          logger.error("getResultSetExtractorList error", e);
          throw e;
        } catch (Exception e1) {
          logger.error("getResultSetExtractorList error", e1);
        }
        return list;
      }
    };
  }
}
