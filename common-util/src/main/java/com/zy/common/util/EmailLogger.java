package com.zy.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EmailLogger {
    private static final Logger logger = LoggerFactory.getLogger(EmailLogger.class);
    private static final Map<String, Long> uniqueKeyMap = new HashMap<>();
    private static final Map<String, MailCounter> repeatCountMap = new HashMap<>();
    /** 12小时 **/
    public static final int H12 = 43200;
    /** 24小时 **/
    public static final int DAY = 86400;
    /** 1小时 **/
    public static final int HOUR = 3600;
    /** 半小时 **/
    public static final int HALF_HOUR = 1800;
    /** 一周 **/
    public static final int WEEK = 604800;

    public static void trace(String arg0) {
        logger.trace(arg0);
    }

    public static void trace(String arg0, Object arg1) {
        logger.trace(arg0, arg1);
    }

    public static void trace(String arg0, Object arg1, Object arg2) {
        logger.trace(arg0, arg1, arg2);
    }

    public static void trace(String arg0, Object... arg1) {
        logger.trace(arg0, arg1);
    }

    public static void trace(String arg0, Throwable arg1) {
        logger.trace(arg0, arg1);
    }

    public static void trace(Marker arg0, String arg1) {
        logger.trace(arg0, arg1);
    }

    public static void trace(Marker arg0, String arg1, Object arg2) {
        logger.trace(arg0, arg1, arg2);
    }

    public static void trace(Marker arg0, String arg1, Object arg2, Object arg3) {
        logger.trace(arg0, arg1, arg2, arg3);
    }

    public static void trace(Marker arg0, String arg1, Object... arg2) {
        logger.trace(arg0, arg1, arg2);
    }

    public static void trace(Marker arg0, String arg1, Throwable arg2) {
        logger.trace(arg0, arg1, arg2);
    }

    public static void debug(String arg0) {
        logger.debug(arg0);
    }

    public static void debug(String arg0, Object arg1) {
        logger.debug(arg0, arg1);
    }

    public static void debug(String arg0, Object arg1, Object arg2) {
        logger.debug(arg0, arg1, arg2);
    }

    public static void debug(String arg0, Object... arg1) {
        logger.debug(arg0, arg1);
    }

    public static void debug(String arg0, Throwable arg1) {
        logger.debug(arg0, arg1);
    }

    public static void debug(Marker arg0, String arg1) {
        logger.debug(arg0, arg1);
    }

    public static void debug(Marker arg0, String arg1, Object arg2) {
        logger.debug(arg0, arg1, arg2);
    }

    public static void debug(Marker arg0, String arg1, Object arg2, Object arg3) {
        logger.debug(arg0, arg1, arg2, arg3);
    }

    public static void debug(Marker arg0, String arg1, Object... arg2) {
        logger.debug(arg0, arg1, arg2);
    }

    public static void debug(Marker arg0, String arg1, Throwable arg2) {
        logger.debug(arg0, arg1, arg2);
    }

    public static void info(String arg0) {
        logger.info(arg0);
    }

    public static void info(String arg0, Object arg1) {
        logger.info(arg0, arg1);
    }

    public static void info(String arg0, Object arg1, Object arg2) {
        logger.info(arg0, arg1, arg2);
    }

    public static void info(String arg0, Object... arg1) {
        logger.info(arg0, arg1);
    }

    public static void info(String arg0, Throwable arg1) {
        logger.info(arg0, arg1);
    }

    public static void info(Marker arg0, String arg1) {
        logger.info(arg0, arg1);
    }

    public static void info(Marker arg0, String arg1, Object arg2) {
        logger.info(arg0, arg1, arg2);
    }

    public static void info(Marker arg0, String arg1, Object arg2, Object arg3) {
        logger.info(arg0, arg1, arg2, arg3);
    }

    public static void info(Marker arg0, String arg1, Object... arg2) {
        logger.info(arg0, arg1, arg2);
    }

    public static void info(Marker arg0, String arg1, Throwable arg2) {
        logger.info(arg0, arg1, arg2);
    }

    public static void warn(String arg0) {
        logger.warn(arg0);
    }

    public static void warn(String arg0, Object arg1) {
        logger.warn(arg0, arg1);
    }

    public static void warn(String arg0, Object... arg1) {
        logger.warn(arg0, arg1);
    }

    public static void warn(String arg0, Object arg1, Object arg2) {
        logger.warn(arg0, arg1, arg2);
    }

    public static void warn(String arg0, Throwable arg1) {
        logger.warn(arg0, arg1);
    }

    public static void warn(Marker arg0, String arg1) {
        logger.warn(arg0, arg1);
    }

    public static void warn(Marker arg0, String arg1, Object arg2) {
        logger.warn(arg0, arg1, arg2);
    }

    public static void warn(Marker arg0, String arg1, Object arg2, Object arg3) {
        logger.warn(arg0, arg1, arg2, arg3);
    }

    public static void warn(Marker arg0, String arg1, Object... arg2) {
        logger.warn(arg0, arg1, arg2);
    }

    public static void warn(Marker arg0, String arg1, Throwable arg2) {
        logger.warn(arg0, arg1, arg2);
    }

    public static void error(String msg) {
        logger.error(msg);
    }

    public static void error(String format, Object arg) {
        logger.error(format, arg);
    }

    public static void error(String format, Object arg1, Object arg2) {
        logger.error(format, arg1, arg2);
    }

    public static void error(String format, Object... arguments) {
        logger.error(format, arguments);
    }

    public static void error(String msg, Throwable t) {
        logger.error(msg, t);
    }

    public static void error(Marker marker, String msg) {
        logger.error(marker, msg);
    }

    public static void error(Marker marker, String format, Object arg) {
        logger.error(marker, format, arg);
    }

    public static void error(Marker marker, String format, Object arg1, Object arg2) {
        logger.error(marker, format, arg1, arg2);
    }

    public static void error(Marker marker, String format, Object... arguments) {
        logger.error(marker, format, arguments);
    }

    public static void error(Marker marker, String msg, Throwable t) {
        logger.error(marker, msg, t);
    }

    /**
     * 发送报错邮件，如果在限定时间段内已经发过邮件了，则不发邮件。如果报错邮件的重复次数达到上限，则发送次数邮件
     *
     * @param seconds  限定的时间（秒）
     * @param maxTimes 报错的次数上限
     */
    public static void distinctError(long seconds, int maxTimes, String msg, Throwable t) {
        String uniqueKey = getUniqueKey();
        //如果在一定的秒数内没有发过邮件，才会发送
        if (isNotRepeated(uniqueKey, seconds)) {
            logger.error(msg, t);
        }
        //返回累积的报错邮件达到了多少次,如果达到了上限，则发送次数邮件并清空次数
        MailCounter counter = getRepeatMailCounter(uniqueKey, maxTimes);
        if (counter.getTimes() >= maxTimes) {
            String content = "\t 重复次数:" + counter.getTimes()
                    + "\n \t 代码位置:" + uniqueKey
                    + "\n \t 开始时间:" + new Date(counter.getFirstTime()) + "\n \t ";
            logger.error(content + msg, t);
        }
    }

    /**
     * 发送报错邮件，如果在限定时间段内已经发过邮件了，则不发邮件。如果报错邮件的重复次数达到上限，则发送次数邮件
     *
     * @param seconds  限定的时间（秒）
     * @param maxTimes 报错的次数上限
     */
    public static void distinctError(long seconds, int maxTimes, String msg) {
        String uniqueKey = getUniqueKey();
        //如果在一定的秒数内没有发过邮件，才会发送
        if (isNotRepeated(uniqueKey, seconds)) {
            logger.error(msg);
        }
        //返回累积的报错邮件达到了多少次,如果达到了上限，则发送次数邮件并清空次数
        MailCounter counter = getRepeatMailCounter(uniqueKey, maxTimes);
        if (counter.getTimes() >= maxTimes) {
            String content = "\t 重复次数:" + counter.getTimes()
                    + "\n \t 代码位置:" + uniqueKey
                    + "\n \t 开始时间:" + new Date(counter.getFirstTime()) + "\n \t ";
            logger.error(content + msg);
        }
    }

    /**
     * 发送报错邮件，如果在限定时间段内已经发过邮件了，则不发邮件。如果报错邮件的重复次数达到上限，则发送次数邮件
     *
     * @param seconds  限定的时间（秒）
     * @param maxTimes 报错的次数上限
     */
    public static void distinctError(long seconds, int maxTimes, String msg, Object t) {
        String uniqueKey = getUniqueKey();
        //如果在一定的秒数内没有发过邮件，才会发送
        if (isNotRepeated(uniqueKey, seconds)) {
            logger.error(msg, t);
        }
        //返回累积的报错邮件达到了多少次,如果达到了上限，则发送次数邮件并清空次数
        MailCounter counter = getRepeatMailCounter(uniqueKey, maxTimes);
        if (counter.getTimes() >= maxTimes) {
            String content = "\t 重复次数:" + counter.getTimes()
                    + "\n \t 代码位置:" + uniqueKey
                    + "\n \t 开始时间:" + new Date(counter.getFirstTime()) + "\n \t ";
            logger.error(content + msg, t);
        }
    }

    /**
     * 发送报错邮件，如果在限定时间段内已经发过邮件了，则不发邮件。如果报错邮件的重复次数达到上限，则发送次数邮件
     *
     * @param seconds  限定的时间（秒）
     * @param maxTimes 报错的次数上限
     */
    public static void distinctError(long seconds, int maxTimes, String msg, Object... obj) {
        String uniqueKey = getUniqueKey();
        //如果在一定的秒数内没有发过邮件，才会发送
        if (isNotRepeated(uniqueKey, seconds)) {
            logger.error(msg, obj);
        }
        //返回累积的报错邮件达到了多少次,如果达到了上限，则发送次数邮件并清空次数
        MailCounter counter = getRepeatMailCounter(uniqueKey, maxTimes);
        if (counter.getTimes() >= maxTimes) {
            String content = "\t 重复次数:" + counter.getTimes()
                    + "\n \t 代码位置:" + uniqueKey
                    + "\n \t 开始时间:" + new Date(counter.getFirstTime()) + "\n \t ";
            logger.error(content + msg, obj);
        }
    }

    /**
     * 发送报错邮件，如果在限定时间段内已经发过邮件了，则不发邮件。如果报错邮件的重复次数达到上限，则发送次数邮件
     *
     * @param seconds  限定的时间（秒）
     * @param maxTimes 报错的次数上限
     */
    public static void distinctError(long seconds, int maxTimes, Marker marker, String msg, Throwable t) {
        String uniqueKey = getUniqueKey();
        //如果在一定的秒数内没有发过邮件，才会发送
        if (isNotRepeated(uniqueKey, seconds)) {
            logger.error(marker, msg, t);
        }
        //返回累积的报错邮件达到了多少次,如果达到了上限，则发送次数邮件并清空次数
        MailCounter counter = getRepeatMailCounter(uniqueKey, maxTimes);
        if (counter.getTimes() >= maxTimes) {
            String content = "\n \t 重复次数:" + counter.getTimes()
                    + "\n \t 代码位置:" + uniqueKey
                    + "\n \t 开始时间:" + new Date(counter.getFirstTime()) + "\n \t ";
            logger.error(marker, content + msg, t);
        }
    }

    /**
     * 发送报错邮件，如果在限定时间段内已经发过邮件了，则不发邮件。如果报错邮件的重复次数达到上限，则发送次数邮件
     *
     * @param seconds  限定的时间（秒）
     * @param maxTimes 报错的次数上限
     */
    public static void distinctError(long seconds, int maxTimes, Marker marker, String msg, Object... objects) {
        String uniqueKey = getUniqueKey();
        //如果在一定的秒数内没有发过邮件，才会发送
        if (isNotRepeated(uniqueKey, seconds)) {
            logger.error(marker, msg, objects);
        }
        //返回累积的报错邮件达到了多少次,如果达到了上限，则发送次数邮件并清空次数
        MailCounter counter = getRepeatMailCounter(uniqueKey, maxTimes);
        if (counter.getTimes() >= maxTimes) {
            String content = "\n \t 重复次数:" + counter.getTimes()
                    + "\n \t 代码位置:" + uniqueKey
                    + "\n \t 开始时间:" + new Date(counter.getFirstTime()) + "\n \t ";
            logger.error(content + msg, objects);
        }
    }

    /**
     * 发送报错邮件，如果在限定时间段内已经发过邮件了，则不发邮件。如果报错邮件的重复次数达到上限，则发送次数邮件
     *
     * @param seconds  限定的时间（秒）
     * @param maxTimes 报错的次数上限
     */
    public static void distinctError(long seconds, int maxTimes, Marker marker, String msg) {
        String uniqueKey = getUniqueKey();
        //如果在一定的秒数内没有发过邮件，才会发送
        if (isNotRepeated(uniqueKey, seconds)) {
            logger.error(marker, msg);
        }
        //返回累积的报错邮件达到了多少次,如果达到了上限，则发送次数邮件并清空次数
        MailCounter counter = getRepeatMailCounter(uniqueKey, maxTimes);
        if (counter.getTimes() >= maxTimes) {
            String content = "\t 重复次数:" + counter.getTimes()
                    + "\n \t 代码位置:" + uniqueKey
                    + "\n \t 开始时间:" + new Date(counter.getFirstTime()) + "\n \t ";
            logger.error(content + msg);
        }
    }

    /**
     * 返回同样的错误邮件已经累积的次数，如果次数已经达到上限，则清除次数
     *
     * @param uniqueKey
     * @param maxTimes
     * @return
     */
    private static MailCounter getRepeatMailCounter(String uniqueKey, int maxTimes) {
        if (maxTimes <= 1) {
            maxTimes = 2;
        }
        MailCounter counter = repeatCountMap.get(uniqueKey);
        if (counter == null) {
            counter = new MailCounter();
            repeatCountMap.put(uniqueKey, counter);
        } else {
            counter.setTimes(counter.getTimes() + 1);
            if (maxTimes <= counter.getTimes()) {
                repeatCountMap.remove(uniqueKey);
            }
        }
        return counter;
    }

    /**
     * 判断在限定的秒数内是否有过重复的操作
     *
     * @param uniqueKey
     * @param seconds
     * @return
     */
    private static boolean isNotRepeated(String uniqueKey, long seconds) {
        Long value = uniqueKeyMap.get(uniqueKey);
        if (value == null) {
            uniqueKeyMap.put(uniqueKey, System.currentTimeMillis());
            return true;
        } else {
            if ((System.currentTimeMillis() - value) / 1000 >= seconds) {
                uniqueKeyMap.put(uniqueKey, System.currentTimeMillis());
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 获取外层调用工具类的方法的堆栈信息
     *
     * @return
     */
    private static String getUniqueKey() {
        return Thread.currentThread().getStackTrace()[3].toString();
    }

    private static class MailCounter {
        private long firstTime;
        private int times;

        public MailCounter() {
            this.firstTime = System.currentTimeMillis();
            this.times = 1;
        }

        public long getFirstTime() {
            return firstTime;
        }

        public void setFirstTime(long firstTime) {
            this.firstTime = firstTime;
        }

        public int getTimes() {
            return times;
        }

        public void setTimes(int times) {
            this.times = times;
        }
    }
}
