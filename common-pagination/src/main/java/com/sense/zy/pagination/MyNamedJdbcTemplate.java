package com.zy.zy.pagination;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

import javax.sql.DataSource;

/**
 * Created by Administrator on 2016/8/29.
 */
public class MyNamedJdbcTemplate extends NamedParameterJdbcTemplate {
  
  public MyNamedJdbcTemplate(DataSource dataSource) {
    super(dataSource);
  }
  
  public <T> PaginationResult<T> queryForPage(String sql, Pagination pagination,
      MapSqlParameterSource var2, RowMapper<T> var3) throws DataAccessException {
    
    PaginationResult<T> result = new PaginationResult<T>();
    
    // 获取记录条数
    String countWhereSql = sql.substring(sql.toLowerCase().indexOf("from"));
    String countSql = "select count(1) as count " + countWhereSql;
    Integer countList = super.queryForObject(countSql, var2, Integer.class);
    
    result.setTotalCount(countList);
    result.setCurrentPage(pagination.getCurrentPage());
    result.setPageSize(pagination.getPageSize());
    
    int pageCount = result.getTotalCount() % result.getPageSize();
    result.setTotalPage(pageCount == 0 ? (result.getTotalCount() / result.getPageSize())
        : (result.getTotalCount() / result.getPageSize() + 1));
    
    String sortSql = parseSort(pagination);
    if (sortSql != null) {
      sql += sortSql;
    }
    sql += parseLimit(pagination);
    
    List<T> data = super.query(sql, var2, var3);
    result.setData(data);
    
    return result;
  }
  
  private String parseLimit(Pagination pagination) {
    
    StringBuilder stringBuffer = new StringBuilder();
    stringBuffer.append(" ");
    stringBuffer.append("limit");
    stringBuffer.append(" ");
    stringBuffer.append((pagination.getCurrentPage() - 1) * pagination.getPageSize());
    stringBuffer.append(",");
    stringBuffer.append(pagination.getPageSize());
    
    return stringBuffer.toString();
  }
  
  private String parseSort(Pagination pagination) {
    
    List<SortBy> list = pagination.getSorts();
    if (list.size() == 0) {
      return null;
    }
    
    StringBuilder stringBuffer = new StringBuilder();
    stringBuffer.append(" ");
    stringBuffer.append("order by ");
    for (SortBy sortBy : list) {
      stringBuffer.append(sortBy.getColName());
      stringBuffer.append(" ");
      stringBuffer.append(sortBy.getSortType());
      stringBuffer.append(",");
    }
    
    return stringBuffer.toString().substring(0, stringBuffer.length() - 1);
    
  }
  
}
