package com.lbh.openapi.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.slf4j.LoggerFactory;

import com.lbh.openapi.domain.TraceData;

import ch.qos.logback.classic.Logger;

public class TraceUtil {
	protected static final Logger log = (Logger)LoggerFactory.getLogger(TraceUtil.class);
	public final static String TRACE_HEADER="X-LBH-TRACE-ID";
	public final static String TRACE_FIELD_SEPERATOR="\u250f\u2510";
	//\u250F\u2510
	private final static SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
	private final static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
	private final static Map<String, TraceData> mapTrace = new HashMap<String, TraceData>();
	
	public static String getTraceId() {
		String uuid = getUUID();
		String[] tmp = uuid.split("-");
		return tmp[tmp.length-2]+tmp[tmp.length-1];
	}
	
	public static String getSpanId() {
		String uuid = getUUID();
		String[] tmp = uuid.split("-");
		return tmp[tmp.length-2]+tmp[tmp.length-1];
	}
	
	public static String getUUID() {
		return UUID.randomUUID().toString();
	}
	
	public static String getTimestamp() {
		return getTimestamp(System.currentTimeMillis());
	}
	
	public static String getTimestamp(long stmp) {
		Date d = Date.from(Instant.ofEpochMilli(stmp));
		dayFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dayFormat.format(d)+"T"+timeFormat.format(d)+"Z";
	}
	public static String getIso8610Timestamp() {
		return getIso8610Timestamp(System.currentTimeMillis());
	}

	
	public static String getIso8610Timestamp(long stmp) {
		return DateTimeFormatter
				.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
				.format(
						ZonedDateTime.ofInstant(
								Instant.ofEpochMilli(stmp), ZoneId.of("Z")
								)
						);
	}

	public static String addSpanId(String values, String spanId) {
		String[] tmp = values.split(",");
		StringBuilder sb = new StringBuilder();
		sb.append(tmp[0]).append(",").append(spanId).append(",");
		for (int i=1; i < tmp.length; i++) {
			sb.append(tmp[i]).append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	public synchronized static TraceData getTraceData(String traceType, String spanId) {
		String key = traceType+":"+spanId;
		TraceData ret = null;
		if (mapTrace.containsKey(key)) {
			ret = mapTrace.get(key);
		} else {
			ret = new TraceData()
					.setTraceType(traceType)
					.setTraceId(getTraceId())
					.setSpanId(spanId);
			mapTrace.put(key, ret);
		}
		
		return ret;
	}
	
	public synchronized static TraceData getTraceData(String traceType, String traceId, String spanId) {
		String key = traceType+":"+spanId;
		TraceData ret = null;
		if (mapTrace.containsKey(key)) {
			ret = mapTrace.get(key);
		} else {
			ret = new TraceData()
					.setTraceType(traceType)
					.setTraceId(traceId)
					.setSpanId(spanId);
			mapTrace.put(key, ret);
		}
		return ret;
	}
	
	public synchronized static TraceData remove(String traceType, String spanId) {
		String key = traceType+":"+spanId;
		TraceData ret = null;
		if (mapTrace.containsKey(key)) {
			ret = mapTrace.remove(key);
		} else {
			log.info("traceType:"+traceType+",spanId:"+spanId+", not found");
		}
		return ret;
	}
}
