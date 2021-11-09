package muonssh.app.updater;

import java.net.ProxySelector;
import java.net.URL;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.markusbernhardt.proxy.ProxySearch;

import muonssh.app.App;

import static util.Constants.API_UPDATE_URL;

public class UpdateChecker {

	static {
		CertificateValidator.registerCertificateHook();
	}

	public static boolean isNewUpdateAvailable() {
		try {
			ProxySearch proxySearch = ProxySearch.getDefaultProxySearch();
			ProxySelector myProxySelector = proxySearch.getProxySelector();

			ProxySelector.setDefault(myProxySelector);

			System.out.println("Checking for url");
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			VersionEntry latestRelease = objectMapper.readValue(new URL(API_UPDATE_URL).openStream(),
					new TypeReference<VersionEntry>() {
					});
			System.out.println("Latest release: " + latestRelease);
			return latestRelease.compareTo(App.VERSION) > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
