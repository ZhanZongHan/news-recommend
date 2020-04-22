package com.zzh.tools;

import org.elasticsearch.common.unit.TimeValue;

import java.util.concurrent.TimeUnit;

public class EsProperties {
    public static final String HOSTNAME = "47.112.103.152";
    public static final Integer PORT = 9200;
    public static final String SCHEMA = "http";
    public static final String INDEX = "news_info";

    public static final Integer FROM = 0;
    public static final Integer SIZE = 15;
    public static final Integer HIGHLIGHT_NUMBER_OF_FRAGMENTS = 2;
    public static final String HIGHLIGHT_PRE_TAGS = "<span style='color:red'>";
    public static final String HIGHLIGHT_POST_TAGS = "</span>";

    public static final TimeValue TERN_TIMEOUT = new TimeValue(10, TimeUnit.SECONDS);
    public static final TimeValue BULK_TIMEOUT = new TimeValue(60, TimeUnit.SECONDS);
}
