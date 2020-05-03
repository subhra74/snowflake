package muon.app.updater;

import java.net.URL;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import muon.app.App;

public class UpdateChecker {
	public static final String UPDATE_URL = "https://api.github.com/repos/subhra74/snowflake/releases/latest";

	public static boolean isNewUpdateAvailable() {
		try {
			System.out.println("Checking for url");
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			VersionEntry latestRelease = objectMapper.readValue(new URL(UPDATE_URL).openStream(),
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
