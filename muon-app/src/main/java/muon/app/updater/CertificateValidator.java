package muon.app.updater;

import java.net.Socket;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.swing.JOptionPane;

public class CertificateValidator {
	public static synchronized final void registerCertificateHook() {
		SSLContext sslContext = null;
		try {
			try {
				sslContext = SSLContext.getInstance("TLS");
			} catch (Exception e) {
				e.printStackTrace();
				sslContext = SSLContext.getInstance("SSL");
			}

			TrustManager[] trustAllCerts = new TrustManager[] { new X509ExtendedTrustManager() {

				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					// TODO Auto-generated method stub

				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					try {
						for (X509Certificate cert : chain) {
							//System.out.println("checking certificate: " + cert);
							cert.checkValidity();
						}
					} catch (CertificateException e) {
						e.printStackTrace();
						if (!confirmCert()) {
							throw e;
						}
					}
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket)
						throws CertificateException {
					// TODO Auto-generated method stub

				}

				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
						throws CertificateException {
					// TODO Auto-generated method stub

				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket)
						throws CertificateException {
					try {
						for (X509Certificate cert : chain) {
							//System.out.println("checking certificate:- " + cert);
							cert.checkValidity();
						}
					} catch (CertificateException e) {
						e.printStackTrace();
						if (!confirmCert()) {
							throw e;
						}
					}
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
						throws CertificateException {
					try {
						for (X509Certificate cert : chain) {
							//System.out.println("checking certificate::- " + cert);
							cert.checkValidity();
						}
					} catch (CertificateException e) {
						e.printStackTrace();
						if (!confirmCert()) {
							throw e;
						}
					}
				}
			} };
			sslContext.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean confirmCert() {
		return JOptionPane.showConfirmDialog(null, "Update-check\nTrust server certificate?") == JOptionPane.YES_OPTION;
	}
}
