package net.matthaynes.juicer.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import javax.annotation.CheckForNull;

import org.apache.commons.io.IOUtils;

import net.matthaynes.juicer.Properties;

public class NetworkHelper {

	/**
	 * @param address
	 * @return the html for the given address
	 */
	@CheckForNull
	public String getHtml(String address) {
		try {
			URL url = new URL(address);

			final URLConnection urlConnection;
			if (Properties.PROXY_HOST != null) {
				Proxy proxy = new Proxy(Proxy.Type.HTTP,
						new InetSocketAddress(Properties.PROXY_HOST, Properties.PROXY_PORT));
				urlConnection = url.openConnection(proxy);
			} else {
				urlConnection = url.openConnection();
			}

			return IOUtils.toString(urlConnection.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
