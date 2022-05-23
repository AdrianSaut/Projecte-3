package application;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class ServidorSSL {
	public static void startSSLServer() throws IOException {
		final int port = 16666;
		String keyFile = "G:\\DAM2\\Projecte-3\\Chat\\ChatDeliverass\\serverkey.jks";
		String keyFilePass = "claussl";
		String keyPass = "claussl";
		SSLServerSocket sslsocket = null;
		KeyStore ks;
		KeyManagerFactory kmf;
		SSLContext sslc = null;
		Connection conn = null;
		ArrayList<String> paraules = new ArrayList<String>();
		ArrayList<String> paraulesClient = new ArrayList<String>();
		try {
			ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream(keyFile), keyFilePass.toCharArray());
			kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, keyPass.toCharArray());
			sslc = SSLContext.getInstance("SSLv3");
			sslc.init(kmf.getKeyManagers(), null, null);
		} catch (KeyManagementException ex) {

		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException e) {
			e.printStackTrace();
		}
		
		SSLServerSocketFactory sslssf = sslc.getServerSocketFactory();
		sslsocket = (SSLServerSocket) sslssf.createServerSocket();
		SocketAddress sa = new InetSocketAddress("localhost", port);
		sslsocket.bind(sa);
		System.out.println("Escoltant...");
		SSLSocket ssocket = (SSLSocket) sslsocket.accept();
		System.out.println("Connexió Server OK~");
		System.out.println("========================");
		System.out.println("");
		BufferedReader socketIn = new BufferedReader(new InputStreamReader(ssocket.getInputStream()));
		BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
		PrintStream socketOut = new PrintStream(ssocket.getOutputStream());
		String s;
		while (true) {

			System.out.println("Esperar missatge del client...");
			System.out.println("");
			s = socketIn.readLine().trim();
			if (s != null && !s.equals("")) {
				System.out.println("Client Missatge: " + s);
				if (s.trim().equals("adeu")) {
					break;
				}
				System.out.print("Server Missatge: ");

			}
			s = userIn.readLine().trim();
			if (s != null && !s.equals("")) {
				socketOut.println(s);
				if (s.trim().equalsIgnoreCase("adeu")) {
					break;
				}
			}
		}
		socketIn.close();
		socketOut.close();
		userIn.close();
		sslsocket.close();
	}

	public static void main(String[] args) {
		try {
			startSSLServer();
		} catch (Exception e) {
			System.out.println("Error: " + e);
		}
	}
}