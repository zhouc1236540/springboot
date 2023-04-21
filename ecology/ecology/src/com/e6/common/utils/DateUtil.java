package com.e6.common.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weaver.common.StringUtil;

/**
 * 时间工具类
 * 
 * @author HM
 *
 */
public class DateUtil {

	private static Logger logger = LoggerFactory.getLogger(DateUtil.class);

	/**
	 * 毫秒
	 */
	public static final long MS = 1;
	/**
	 * 每秒钟的毫秒数
	 */
	public static final long SECOND_MS = MS * 1000;
	/**
	 * 每分钟的毫秒数
	 */
	public static final long MINUTE_MS = SECOND_MS * 60;
	/**
	 * 每小时的毫秒数
	 */
	public static final long HOUR_MS = MINUTE_MS * 60;
	/**
	 * 每天的毫秒数
	 */
	public static final long DAY_MS = HOUR_MS * 24;
	/**
	 * 每月的毫秒数
	 */
	public static final long MONTH_MS = DAY_MS * 30;
	/**
	 * 每年的毫秒数
	 */
	public static final long YEAR_MS = MONTH_MS * 12;

	/**
	 * 标准日期（不含时间）格式化器
	 */
	public static final String NORM_DATE_FORMAT = new String("yyyy-MM-dd");
	/**
	 * 标准日期时间格式化器
	 */
	public static final String NORM_DATETIME_FORMAT = new String("yyyy-MM-dd HH:mm:ss");
	
	public static String converDate(String date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		 Date dateforMate;
		try {
			dateforMate = formatter.parse(date);
			 SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	         return sdf.format(dateforMate);
		} catch (ParseException e) {
			logger.error("OA请假历史报表时间转换异常"+e);
			e.printStackTrace();
		}
         return "";
	}
	
	
	public static Date getDateFromString(String dateStr) {
		if(StringUtil.isNull(dateStr)) {
			return null;
		}
	    DateFormat formatter = new SimpleDateFormat(NORM_DATE_FORMAT);
	    Date date = null;
	    try {
	        date = (Date) formatter.parse(dateStr);
	    } catch (ParseException e) {
	        e.printStackTrace();
	    }

	    return date;
	}

	/**
	 * 获取两个日期之间的日期
	 * 
	 * @param start 开始日期
	 * @param end   结束日期
	 * @return 日期集合
	 * @throws ParseException
	 */
	public static List<String> getBetweenDates(String s1, String s2) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<String> result = new ArrayList<String>();
		Date start = sdf.parse(s1);
		Date end = sdf.parse(s2);

		Calendar tempStart = Calendar.getInstance();
		tempStart.setTime(start);
		tempStart.add(Calendar.DAY_OF_YEAR, 1);

		Calendar tempEnd = Calendar.getInstance();
		tempEnd.setTime(end);

		result.add(s1);
		while (tempStart.before(tempEnd)) {
			result.add(sdf.format(tempStart.getTime()));
			tempStart.add(Calendar.DAY_OF_YEAR, 1);
		}
		result.add(s2);
		return result;
	}

	/**
	 * 判断当前日期是星期几
	 * 
	 * @param pTime 先要判断的时间
	 * @return dayForWeek 判断结果，1表示星期日，2~7分别表示星期一到星期六
	 * @Exception 发生异常
	 */
	public static int dayForWeek(String pTime) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(format.parse(pTime));
		int dayForWeek = c.get(Calendar.DAY_OF_WEEK);
		return dayForWeek;
	}

	/**
	 * 将字符串转换成日期。
	 * 
	 * @param date
	 * @param dateFormat
	 * @return
	 */
	public static Date toDate(String dateStr, String dateFormat) {
		try {
			Date date = new SimpleDateFormat(dateFormat).parse(dateStr);
			return date;
		} catch (Exception e) {
			logger.error("toDate error", e);
		}
		return null;
	}

	/**
	 * 计算指定日期属于星期几。
	 * 
	 * @param date 1表示星期日，2-7分别表示星期一到星期六
	 * @return
	 */
	public static int toDay(String dateStr) {
		Date date = toDate(dateStr, "yyy-MM-dd");
		return toDay(date);
	}

	/**
	 * 计算指定日期属于星期几。
	 * 
	 * @param date 1表示星期日，2-7分别表示星期一到星期六
	 * @return
	 */
	public static int toDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	public static long diffTime(String startTime1, String endTime1, String startTime2, String endTime2) {
		Date startTime = toDate(startTime1.compareTo(startTime2) > 0 ? startTime1 : startTime2, "HH:mm");
		Date endTime = toDate(endTime1.compareTo(endTime2) < 0 ? endTime1 : endTime2, "HH:mm");
		long time = endTime.getTime() - startTime.getTime();
		return time > 0 ? time : 0;
	}

	/**
	 * 计算两个时间相关的天数，相关天数以绝对值返回。
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int diffDate(String startDate, String endDate) {
		long time = toDate(endDate, "yyyy-MM-dd").getTime() - toDate(startDate, "yyyy-MM-dd").getTime();
		return (int) (Math.abs(time) / 3600 / 24 / 1000);
	}

	public static long diffTime(String startTime, String endTime) {
		long time = toDate(endTime, "HH:mm").getTime() - toDate(startTime, "HH:mm").getTime();
		return time > 0 ? time : 0;
	}

	public static int fullYear() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.YEAR);
	}

	/**
	 * Excel导入时间字段处理 String转换成Timestamp
	 *
	 * @param date 字符串时间
	 * @throws ParseException
	 */
	public static Timestamp getStrTimeStampDate(String date) throws ParseException {
		date = date.replaceAll("/", "-");
		Long timestamp = null;
		timestamp = new SimpleDateFormat("yyyy-MM-dd").parse(date).getTime();
		return new Timestamp(timestamp);
	}

	/**
	 * 获取当i前月份，自动补零
	 * <p>
	 * 
	 * @Title:getDateMonth
	 *                     </p>
	 *                     <p>
	 * @Description:
	 *               </p>
	 * 
	 * @return
	 */
	public static String getDateMonth() {
		LocalDateTime localDateTime = LocalDateTime.now();
		Month month = localDateTime.getMonth();
		int value = month.getValue();
		return String.valueOf(value);
	}

	/**
	 * 获取指定格式的字符串当i前日期
	 * <p>
	 * 
	 * @Title:dateFormat
	 *                   </p>
	 *                   <p>
	 * @Description:
	 *               </p>
	 * 
	 * @param format
	 * @return
	 */
	public static String dateFormat(String format) {
		return dateFormat(new Date(), format);
	}

	/**
	 * 获取指定格式的时间字符串日期
	 * <p>
	 * 
	 * @Title:dateFormat
	 *                   </p>
	 *                   <p>
	 * @Description:
	 *               </p>
	 * 
	 * @param format
	 * @return
	 */
	public static String dateFormat(Date date, String format) {
		if (null == date) {
			return "";
		}
		return new SimpleDateFormat(format).format(date);
	}

	/**
	 * 获取时间差的小时数
	 * <p>
	 * 
	 * @Title:dateFormat
	 *                   </p>
	 *                   <p>
	 * @Description:
	 *               </p>
	 * 
	 * @param format
	 * @return
	 */
	public static String getTimeDifference(String d1, String d2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date1 = df.parse(d1);
			Date date2 = df.parse(d2);
			long diff = date2.getTime() - date1.getTime();// 这样得到的差值是微秒级别
			long hours = diff / 1000 / 60 / 60 / 24 * 8;
			return hours + "";
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 将Date转换成String
	 * 
	 * @param date
	 * @return
	 */
	public static String date2String(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = sdf.format(date);
		return dateStr;
	}

	/**
	 * 将Long转换成String
	 * 
	 * @param date
	 * @return
	 */
	public static String date2Long(Long longVal) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(longVal);
		String dateStr = sdf.format(date);
		return dateStr;
	}

	/**
	 * 获取系统的年月日
	 * 
	 * @param date
	 * @return
	 */
	public static String gsinSysYmd() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());
		return dateStr;
	}

	/**
	 * 将Timestamp转换成String 用于数据库中字段类型为datetime
	 * 
	 * @param timestamp
	 * @return
	 */
	public static String time2String(Timestamp timestamp) {
		Date date = new Date(timestamp.getTime());
		String dateStr = date2String(date);
		return dateStr;
	}

	/**
	 * 时间戳转换成日期
	 *
	 * @param updateTime 时间戳类型
	 * @return String格式日志
	 */
	public static String getTimeStampDate(Timestamp updateTime) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(updateTime);
	}

	/**
	 * 获取当前系统时间【sql】缺失时分秒
	 *
	 * @return
	 */
	public static Date getDateSql() {
		return new java.sql.Date(System.currentTimeMillis());
	}

	/**
	 * 返回当前系统时间戳
	 *
	 * @return
	 */
	public static Timestamp getSysDate() {
		long currentTimeMillis = System.currentTimeMillis();
		return new Timestamp(currentTimeMillis);
	}

	/**
	 * 获取当前系统时间(Date)
	 *
	 * @return
	 */
	public static Date getDate() {
		return new Date(System.currentTimeMillis());
	}

	/**
	 * @param year  +-年份
	 * @param month +-月份
	 * @param day   +- 天数
	 * @return
	 */
	public static String getDateDay(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(getDate());
		calendar.add(Calendar.YEAR, year);// 减去年数
		calendar.add(Calendar.MONTH, month);// 减去月数
		calendar.add(Calendar.DATE, day);// 减去天数
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
	}

	/**
	 * 获取当期季度的天数
	 *
	 * @param cntDateBeg 开始时间
	 * @param cntDateEnd 结束时间
	 * @return
	 */
	public static List<String> addDates(String cntDateBeg, String cntDateEnd) {
		List<String> list = new ArrayList<>();
		String[] dateBegs = cntDateBeg.split("-");
		String[] dateEnds = cntDateEnd.split("-");
		Calendar start = Calendar.getInstance();
		start.set(Integer.valueOf(dateBegs[0]), Integer.valueOf(dateBegs[1]) - 1, Integer.valueOf(dateBegs[2]));
		Long startTIme = start.getTimeInMillis();
		Calendar end = Calendar.getInstance();
		end.set(Integer.valueOf(dateEnds[0]), Integer.valueOf(dateEnds[1]) - 1, Integer.valueOf(dateEnds[2]));
		Long endTime = end.getTimeInMillis();
		Long oneDay = 1000 * 60 * 60 * 24L;
		Long time = startTIme;
		while (time <= endTime) {
			Date d = new Date(time);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			time += oneDay;
			list.add(df.format(d));
		}
		return list;
	}

	/**
	 * 获取当前季度
	 *
	 * @param date 时间
	 * @return
	 */
	public static String getQuarterByDate(String date) throws ParseException {
		if (date == "" || "".equals(date)) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date datePar = sdf.parse(date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(datePar);

		String year = String.valueOf(calendar.get(Calendar.YEAR));
		@SuppressWarnings("deprecation")
		int mouth = datePar.getMonth() + 1;
		if (mouth >= 1 && mouth <= 3) {
			return year + "年第一季度";
		} else if (mouth >= 4 && mouth <= 6) {
			return year + "年第二季度";
		} else if (mouth >= 7 && mouth <= 9) {
			return year + "年第三季度";
		} else if (mouth >= 10 && mouth <= 12) {
			return year + "年第四季度";
		} else {
			return "";
		}
	}

	/**
	 * 获取当前日期所在季度的开始日期和结束日期 季度一年四季， 第一季度：1月-3月， 第二季度：4月-6月， 第三季度：7月-9月， 第四季度：10月-12月
	 *
	 * @param flag 1表示查询本季度开始日期 2表示查询本季度结束日期 3:本季度开始结束时间
	 * @return
	 */
	public static String getStartOrEndDayOfQuarter(Object flag) {
		LocalDate today = LocalDate.now();
		LocalDate resDate = LocalDate.now();
		String resDateStr = "";
		if (today == null) {
			today = resDate;
		}
		Month month = today.getMonth();
		Month firstMonthOfQuarter = month.firstMonthOfQuarter();
		Month endMonthOfQuarter = Month.of(firstMonthOfQuarter.getValue() + 2);
		if (Integer.parseInt(flag.toString()) == 1) {
			resDateStr = LocalDate.of(today.getYear(), firstMonthOfQuarter, 1).toString();
		}
		if (Integer.parseInt(flag.toString()) == 2) {
			resDateStr = LocalDate.of(today.getYear(), endMonthOfQuarter, endMonthOfQuarter.length(today.isLeapYear()))
					.toString();
		}
		if (Integer.parseInt(flag.toString()) == 3) {
			resDateStr = LocalDate.of(today.getYear(), firstMonthOfQuarter, 1) + " 00:00:00" + ","
					+ LocalDate.of(today.getYear(), endMonthOfQuarter, endMonthOfQuarter.length(today.isLeapYear()))
					+ " 23:59:59";
		}
		return resDateStr;
	}

	/**
	 * 当前时间，格式 yyyy-MM-dd HH:mm:ss
	 *
	 * @return 当前时间的标准形式字符串
	 */
	public static String now() {
		return formatDateTime(new Date());
	}

	/**
	 * 格式 yyyy-MM-dd HH:mm:ss
	 *
	 * @param date 被格式化的日期
	 * @return 格式化后的日期
	 */
	public static String formatDateTime(Date date) {
		return new SimpleDateFormat(NORM_DATETIME_FORMAT).format(date);
	}

	/**
	 * 当前日期，格式 yyyy-MM-dd
	 *
	 * @return 当前日期的标准形式字符串
	 */
	public static String today() {
		return formatDate(new Date());
	}

	/**
	 * 根据特定格式格式化日期
	 *
	 * @param date   被格式化的日期
	 * @param format 格式
	 * @return 格式化后的字符串
	 */
	public static String format(Date date, String format) {
		return new SimpleDateFormat(format).format(date);
	}

	/**
	 * 格式 yyyy-MM-dd
	 *
	 * @param date 被格式化的日期
	 * @return 格式化后的字符串
	 */
	public static String formatDate(Date date) {
		return new SimpleDateFormat(NORM_DATE_FORMAT).format(date);
	}

	/**
	 * 将特定格式的日期转换为Date对象
	 *
	 * @param dateString 特定格式的日期
	 * @param format     格式，例如yyyy-MM-dd
	 * @return 日期对象
	 */
	public static Date parse(String dateString, String format) {
		try {
			return (new SimpleDateFormat(format)).parse(dateString);
		} catch (ParseException e) {
			logger.error("Parse" + dateString + " with format " + format + " error!", e);
		}
		return null;
	}

	/**
	 * 格式yyyy-MM-dd HH:mm:ss
	 *
	 * @param dateString 标准形式的时间字符串
	 * @return 日期对象
	 */
	public static Date parseDateTime(String dateString) {
		try {
			return new SimpleDateFormat(NORM_DATETIME_FORMAT).parse(dateString);
		} catch (ParseException e) {
			logger.error("Parse " + dateString + " with format "
					+ new SimpleDateFormat(NORM_DATETIME_FORMAT).toPattern() + " error!", e);
		}
		return null;
	}

	/**
	 * 格式yyyy-MM-dd
	 *
	 * @param dateString 标准形式的日期字符串
	 * @return 标准形式的日期字符串
	 */
	public static Date parseDate(String dateString) {
		try {
			return new SimpleDateFormat(NORM_DATE_FORMAT).parse(dateString);
		} catch (ParseException e) {
			logger.error("Parse " + dateString + " with format " + new SimpleDateFormat(NORM_DATE_FORMAT).toPattern()
					+ " error!", e);
		}
		return null;
	}

	/**
	 * 获取指定日期偏移指定时间后的时间
	 *
	 * @param date          基准日期
	 * @param calendarField 偏移的粒度大小（小时、天、月等）使用Calendar中的常数
	 * @param offsite       偏移量，正数为向后偏移，负数为向前偏移
	 * @return 偏移后的日期
	 */
	public static Date getOffsiteDate(Date date, int calendarField, int offsite) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(calendarField, offsite);
		return cal.getTime();
	}

	/**
	 * 判断两个日期相差的时长<br/>
	 * (列：1年前7月25日) 返回 minuend - subtrahend 的差
	 *
	 * @param subtrahend 减数日期
	 * @param minuend    被减数日期
	 * @return 日期差
	 */
	public static String dateDiff(Date subtrahend, Date minuend) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(subtrahend);
		long diff = minuend.getTime() - subtrahend.getTime();
		if (diff <= HOUR_MS) {
			return diff / MINUTE_MS + "分钟前";
		} else if (diff <= DAY_MS) {
			return diff / HOUR_MS + "小时" + (diff % HOUR_MS / MINUTE_MS) + "分钟前";
		} else if (diff <= DAY_MS * 2) {
			return "昨天" + calendar.get(Calendar.HOUR_OF_DAY) + "点" + calendar.get(Calendar.MINUTE) + "分";
		} else if (diff <= DAY_MS * 3) {
			return "前天" + calendar.get(Calendar.HOUR_OF_DAY) + "点" + calendar.get(Calendar.MINUTE) + "分";
		} else if (diff <= MONTH_MS) {
			return diff / DAY_MS + "天前" + calendar.get(Calendar.HOUR_OF_DAY) + "点" + calendar.get(Calendar.MINUTE)
					+ "分";
		} else if (diff <= YEAR_MS) {
			return diff / MONTH_MS + "个月" + (diff % MONTH_MS) / DAY_MS + "天前" + calendar.get(Calendar.HOUR_OF_DAY) + "点"
					+ calendar.get(Calendar.MINUTE) + "分";
		} else {
			return diff / YEAR_MS + "年前" + (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DATE) + "日";
		}

	}

	/**
	 * 距离截止日期还有多长时间
	 * 
	 * @param date
	 * @return
	 */
	public static String fromDeadline(Date date) {
		long deadline = date.getTime();
		long now = new Timestamp(System.currentTimeMillis()).getTime();
		long remain = deadline - now;
		if (remain <= HOUR_MS) {
			return "只剩下" + remain / MINUTE_MS + "分钟";
		} else if (remain <= DAY_MS) {
			return "只剩下" + remain / HOUR_MS + "小时" + (remain % HOUR_MS / MINUTE_MS) + "分钟";
		} else {
			long day = remain / DAY_MS;
			long hour = remain % DAY_MS / HOUR_MS;
			long minute = remain % DAY_MS % HOUR_MS / MINUTE_MS;
			return "只剩下" + day + "天" + hour + "小时" + minute + "分钟";
		}
	}
	
	/**
	 * 	补零操作
	 * @param date
	 * @return
	 */
	public static String formatToDate(String date) {
		return  date+" 00:00:00";
	}

}
