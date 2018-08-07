package com.zy.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

  private static String dateTimePattern = "yyyy-MM-dd HH:mm:ss";

  /**
   * 返回当天零点时间
   *
   * @return Date
   */
  public static Date getToday() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }

  /**
   * 返回昨天零点时间
   *
   * @return Date
   */
  public static Date getYesterday() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, -1);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }

  /**
   * 返回明天零点时间
   *
   * @return Date
   */
  public static Date getTomorrow() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, 1);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }

  /**
   * 返回指定日期的下一天时间
   *
   * @param date
   * @return Date
   */
  public static Date getNextDate(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DAY_OF_MONTH, 1);
    return calendar.getTime();
  }

  /**
   * 返回当前时间的下24小时的时间
   *
   * @return
   */
  public static Date getNextDate() {
    return getNextDate(new Date());
  }

  /**
   * 返回指定日期上一天时间
   *
   * @param date
   * @return Date
   */
  public static Date getPrevDay(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DAY_OF_MONTH, -1);
    return calendar.getTime();
  }

  /**
   * 返回指定分钟后的日期
   *
   * @param minutes
   * @return Date
   */
  public static Date getDateBySpecificMinute(int minutes) {
    return new Date(System.currentTimeMillis() + minutes * 60 * 1000);
  }

  /**
   * Format Date To String (yyyy-MM-dd HH:mm:ss)
   *
   * @param date
   * @return String
   */
  public static String getDateTime(Date date) {
    return new SimpleDateFormat(dateTimePattern).format(date);
  }

  /**
   * Format String To Date (yyyy-MM-dd HH:mm:ss)
   *
   * @param dateString
   * @return Date
   */
  public static Date getDateTime(String dateString) {
    try {
      return new SimpleDateFormat(dateTimePattern).parse(dateString);
    } catch (ParseException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }

  }

  public static String getCurrentDateTime() {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    return formatter.format(new Date());
  }
}
