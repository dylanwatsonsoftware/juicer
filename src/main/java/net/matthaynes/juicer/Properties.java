package net.matthaynes.juicer;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;

public class Properties {

	@Nonnull
	public static final String TEMP_DIRECTORY = getProperty("temp.dir", "/tmp/goose");

	@Nonnull
	public static final String IMAGE_MAGICK_CONVERT_HOME = getProperty("imagemagick.convert.home",
			"C:\\Program Files\\ImageMagick-6.9.1-Q16\\convert.exe");

	@Nonnull
	public static final String IMAGE_MAGICK_IDENTIFY_HOME = getProperty("imagemagick.identify.home",
			"C:\\Program Files\\ImageMagick-6.9.1-Q16\\identify.exe");

	@CheckForNull
	public static final String PROXY_HOST = getProperty("http.proxyHost");

	@Nonnull
	public static final int PROXY_PORT = getProperty("http.proxyPort", 8080);

	@CheckForNull
	private static String getProperty(String key) {
		String value = System.getProperty(key);
		printProperties(key, value);
		return value;
	}

	@Nonnull
	private static String getProperty(String key, String defaultValue) {
		String value = System.getProperty(key, defaultValue);
		printProperties(key, value);
		return value;
	}

	@Nonnull
	private static int getProperty(String key, int defaultValue) {
		int value = Integer.getInteger(key, defaultValue);
		printProperties(key, value);
		return value;
	}

	private static void printProperties(@Nonnull String key, @Nonnull Object value) {
		System.out.println(StringUtils.leftPad(key, 25) + " : " + value);
	}
}
