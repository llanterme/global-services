package za.co.digitalcowboy.global.services.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.utils.URIBuilder;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import java.io.*;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Utils {


	public static int MAXIMUM_LOG_CHARACTERS = 9000;
	private static ObjectMapper objectMapper = new ObjectMapper();
	private static int BigDecimalComparerScale = 3;
	private static Mapper dozerMapper = new DozerBeanMapper();

	public static Mapper getDozerMapper() {
		return dozerMapper;
	}

	public static ObjectMapper getObjectMapper() {
		return objectMapper;
	}
	public static boolean tryParseInt(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static <T, U> int frequency(List<T> list, Function<T, U> mapper, U criteria) {
		Objects.requireNonNull(list);
		Objects.requireNonNull(mapper);
		Objects.requireNonNull(criteria);
		int frequency = 0;
		for(T t : list) {
			if(mapper.apply(t).equals(criteria)) {
				frequency++;
			}
		}
		return frequency;
	}

	private Utils() {
		super();
	}

	public static String toJsonString(String description, Object object) {
		try {
			return description + ": " + objectMapper.writeValueAsString(object);
		}catch(JsonProcessingException e) {
			return "not able to convert to JSON";
		}		
	}

	public static <T> T jsonToObject(String content, Class<T> classReference) {
		try {
			return objectMapper.readValue(content, classReference);
		}catch(IOException e) {
			log.error(String.format("Not able to convert to Object: %s ", content));
			return null;
		}		
	}

	public static int compare(BigDecimal first, BigDecimal second) {
		return first.setScale(BigDecimalComparerScale).compareTo(second.setScale(BigDecimalComparerScale));
	}

	public static boolean isBetween(BigDecimal value, BigDecimal start, BigDecimal end){
		return value.compareTo(start) > 0 && value.compareTo(end) <= 0;
	}

	public static List<String> splitValueIntoList(final String valueToBeSplit, final String delimiter){
		List<String> valueList = new ArrayList<>();

		if(!StringUtils.isEmpty(valueToBeSplit)){
			String[] fieldArr = valueToBeSplit.split(delimiter);

			if(fieldArr != null && fieldArr.length > 0){
				valueList = Arrays.asList(fieldArr);
			}
		}

		return valueList;
	}
	////////
	public static String toJson(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException var2) {
			return "not able to convert to JSON";
		}
	}

	public static String toJsonLog(String description, Object object) {
		try {
			String jsonString = description + ": " + objectMapper.writeValueAsString(object);
			return jsonString.substring(0, Math.min(MAXIMUM_LOG_CHARACTERS, jsonString.length()));
		} catch (JsonProcessingException var3) {
			return "not able to convert to JSON";
		}
	}

	public static String toLogString(String description, Object object) {
		try {
			String logString = description + ": " + object.toString();
			return logString.substring(0, Math.min(MAXIMUM_LOG_CHARACTERS, logString.length()));
		} catch (Exception var3) {
			return "not able to convert to JSON";
		}
	}

	public static <T> T fromJson(String jsonString, Class<T> valueType) throws IOException {
		return objectMapper.readValue(jsonString, valueType);
	}

	public static <T> T fromJson(String jsonString, TypeReference<T> typeReference) throws IOException {
		return objectMapper.readValue(jsonString, typeReference);
	}

	public static <T> List<T> listFromJson(String jsonString, Class<T> valueType) throws IOException {
		CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, valueType);
		List<T> jsonList = (List)objectMapper.readValue(jsonString, listType);
		return jsonList;
	}

	public static <T> T fromJSON(TypeReference<T> type, String jsonPacket) {
		Object data = null;

		try {
			data = (getObjectMapper()).readValue(jsonPacket, type);
		} catch (Exception var4) {
		}

		return (T) data;
	}

	public static String readFromInputStream(InputStream inputStream) throws IOException {
		StringBuilder resultStringBuilder = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		Throwable var3 = null;

		try {
			String line;
			try {
				while((line = br.readLine()) != null) {
					resultStringBuilder.append(line).append("\n");
				}
			} catch (Throwable var12) {
				var3 = var12;
				throw var12;
			}
		} finally {
			if (br != null) {
				if (var3 != null) {
					try {
						br.close();
					} catch (Throwable var11) {
						var3.addSuppressed(var11);
					}
				} else {
					br.close();
				}
			}

		}

		return resultStringBuilder.toString();
	}

	public static String buildUri(URIBuilder uriBuilder, String basePath, String path) throws UnsupportedEncodingException {
		StringBuilder stringBuilder = new StringBuilder(uriBuilder.toString());
		stringBuilder.append(basePath);
		stringBuilder.append(path);

		return URLDecoder.decode(stringBuilder.toString(), "UTF-8");
	}


	public static boolean isPatternMatch(String textToSearch, String regex){
		if(!StringUtils.isEmpty(textToSearch) && !StringUtils.isEmpty(regex)) {
			final Pattern pattern = Pattern.compile(regex);
			final Matcher matcher = pattern.matcher(textToSearch);
			return matcher.matches();
		} else {
			return false;
		}
	}


	public static String toJsonWithRuntimeEx(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T fromJsonWithRunTimeEx(String jsonString, Class<T> valueType) {
		try {
			return objectMapper.readValue(jsonString, valueType);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static HttpHeaders getHeaders() {

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);

		return requestHeaders;
	}
}
