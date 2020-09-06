package com.util.date;

import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

import static java.time.DayOfWeek.SUNDAY;

/**
 * @Version 1.0
 * @ClassName DateUtil
 * @Author jiachenXu
 * @Date 2020/8/9
 * @Description 时间工具类
 */
@Component
public class DateUtil {

    private volatile static LocalDate localDate;

    private volatile static LocalDateTime localDateTime;

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    static {
        localDate = LocalDate.now( );
        localDateTime = LocalDateTime.now( );
    }

    public static LocalDate getLocalDate() {
        return localDate;
    }

    public static LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public LocalDate modifyDate(int year, Month month, int dayOfMonth) {
        localDate = LocalDate.of(year, month, dayOfMonth);
        return localDate;
    }

    /**
     * 设置当前时间
     *
     * @param year
     * @param month
     * @param dayOfMonth
     * @param hour
     * @param minute
     * @param second
     * @param nanoOfSecond
     */
    public static LocalDateTime modifyDateTime(int year, Month month, int dayOfMonth, int hour, int minute,
                                               int second, int nanoOfSecond) {
        localDateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond);
        return localDateTime;
    }

    /**
     * 时间字符串转换Date
     *
     * @param date
     * @return
     */
    public static LocalDate parseOfLocalDate(String date) {
        return LocalDate.parse(date);
    }

    /**
     * 时间字符串转换Date
     *
     * @param date
     * @param dateTimeFormatter
     * @return
     */
    public static LocalDate parseOfLocalDate(String date, DateTimeFormatter dateTimeFormatter) {
        return LocalDate.parse(date, dateTimeFormatter);
    }

    /**
     * 当前时间转换成时间字符串
     *
     * @param pattern
     * @return
     */
    public static String formatOfLocalDate(String pattern) {
        if (localDate != null) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
            return localDate.format(dateTimeFormatter);
        }
        return null;
    }

    /**
     * data转换时间字符串
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String formatOfLocalDate(LocalDate date, String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 获取本地时区
     *
     * @return
     */
    public static ZoneId getLocalZoneId() {
        return ZoneId.systemDefault( );
    }

    /**
     * 获取当前周
     *
     * @return
     */
    public static String getWeek() {
        DayOfWeek dayOfWeek = localDate.getDayOfWeek( );
        return dayOfWeek.toString( );
    }

    /**
     * 获取当前月
     *
     * @return
     */
    public static String getMonth() {
        Month month = localDate.getMonth( );
        return month.toString( );
    }

    /**
     * 当前月有多少天
     *
     * @return
     */
    public static int lengthOfMonth() {
        return localDate.lengthOfMonth( );
    }

    /**
     * 获取指定月份的第一天
     *
     * @param month 当前月
     * @return
     */
    public static String getFirstDayOfMonth(int month) {
        LocalDate with = LocalDate.now( );
        with = with.withMonth(month);
        with = with.with(TemporalAdjusters.firstDayOfMonth( ));
        return formatOfLocalDate(with, YYYY_MM_DD);
    }

    /**
     * 获取指定月份的最后一天
     *
     * @param month
     * @return
     */
    public static String getLastDayOfMonth(int month) {
        LocalDate with = LocalDate.now( );
        with = with.withMonth(month);
        with = with.with(TemporalAdjusters.lastDayOfMonth( ));
        return formatOfLocalDate(with, YYYY_MM_DD);
    }

    /**
     * 是否为闰年
     *
     * @return
     */
    public static boolean isLeapYear() {
        return localDate.isLeapYear( );
    }

    /**
     * 是否为周末
     *
     * @return
     */
    public static boolean isSunday() {
        return (SUNDAY.toString( )).equals(getWeek( ));
    }

    /**
     * 当前时间是否大于参数date
     *
     * @param date
     * @return
     */
    public static boolean isAfter(String date) {
        return localDate.isAfter(parseOfLocalDate(date));
    }

    /**
     * 当前时间是否小于参数date
     *
     * @param date
     * @return
     */
    public static boolean isBefore(String date) {
        return localDate.isBefore(parseOfLocalDate(date));
    }


//    final DateTime DISTRIBUTION_TIME_SPLIT_TIME = new DateTime().withTime(15,0,0,0);
//
//    private Date calculateDistributionTimeByOrderCreateTime(Date orderCreateTime){
//        DateTime orderCreateDateTime = new DateTime(orderCreateTime);
//        Date tomorrow = orderCreateDateTime.plusDays(1).toDate();
//        Date theDayAfterTomorrow = orderCreateDateTime.plusDays(2).toDate();
//        return orderCreateDateTime.isAfter(DISTRIBUTION_TIME_SPLIT_TIME) ? wrapDistributionTime(theDayAfterTomorrow) : wrapDistributionTime(tomorrow);
//    }
//
//    private Date wrapDistributionTime(Date distributionTime){
//        DateTime currentDistributionDateTime = new DateTime(distributionTime);
//        DateTime plusOneDay = currentDistributionDateTime.plusDays(1);
//        boolean isSunday = (DateTimeConstants.SUNDAY == currentDistributionDateTime.getDayOfWeek());
//        return isSunday ? plusOneDay.toDate() : currentDistributionDateTime.toDate() ;
//    }


}
