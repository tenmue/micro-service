package com.tenmue.uaa.common;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public interface UaaConstants {
	
	String UNIQ_REQ_ID_KEY = "UNIREQ_ID";

	String ISO_DATE = "yyyy-MM-dd";
    String FULL_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    String NO_SECOND_DATE_TIME = "yyyy-MM-dd HH:mm";
    String CHINESE_DATE_TIME = "yyyy年MM月dd日 HH:mm:ss";
    
    String TOKEN_HEADER = "Authorization";
    String TOKEN_PREFIX = "Bearer ";

    enum DateTimeFormatterEnum {
        DATETIME(FULL_DATE_TIME),
        DATETIME_NO_SECOND(NO_SECOND_DATE_TIME),
        DATETIME_IN_CHINESE(CHINESE_DATE_TIME);
        private transient String pattern;

        DateTimeFormatterEnum(String pattern) {
            this.pattern = pattern;
        }

        public DateTimeFormatter formatter() {
            return DateTimeFormatter.ofPattern(pattern, Locale.getDefault());
        }
    }
    
    /** 默认分页数据大小 */
    int DEFAULT_PAGE_SIZE = 20;
    
    /** 默认起始行 */
    int DEFAULT_PAGE_OFFSET = 0;
}
