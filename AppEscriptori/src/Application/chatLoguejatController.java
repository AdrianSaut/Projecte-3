package application;

import javafx.fxml.FXML;

import javafx.scene.control.Button;

import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Semaphore;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import Chat.MissatgeUsuari;
import Chat.RegisteParaula;
import Chat.UsuariParaula;
import javafx.event.ActionEvent;

import javafx.scene.control.TextArea;

public class chatLoguejatController {

	// static int port = 28080;
	// public static Scanner sc = new Scanner(System.in);
	public static ArrayList<String> paraulesProhibides = new ArrayList<String>();
	public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static Date date = new Date(System.currentTimeMillis());
	public static String[] paraules = null;
	public static ArrayList<String> paraulesString = new ArrayList<String>();
	public static BufferedReader socketIn = null;
	public static PrintStream socketOut = null;
	static Semaphore inici = new Semaphore(0);

	@FXML
	private Button logOut;
	@FXML
	private TextArea missatges;
	@FXML
	public TextField input;
	@FXML
	private Button enviar;

	public static String idUser = "";
	public static String nomUser = "";

	public void obtencioDades(String id, String usuariConnectat) {

		idUser = id;
		nomUser = usuariConnectat;

	}

	public void arrencarClient() {
		try {
			Thread paraulesBD = new paraulesBD();
			Thread sslClient = new startSSLClient();

			paraulesBD.start();
			sslClient.start();

		} catch (Exception e) {
			System.out.println("Error: " + e);
		}
	}

	public static class startSSLClient extends Thread {
		public startSSLClient() {
			super();
		}
		public void run() {

			try {
				inici.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			int port = 16666;
			String serverAdd = "localhost";
			try {
				System.setProperty("javax.net.ssl.trustStore",
						"G:\\DAM2\\Projecte-3\\Chat\\ChatDeliverass\\serverkey.jks");
				System.setProperty("javax.net.ssl.trustStorePassword", "claussl");
				SSLSocketFactory sslsf = (SSLSocketFactory) SSLSocketFactory.getDefault();

				Socket csocket = sslsf.createSocket(serverAdd, port);

				socketIn = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
				socketOut = new PrintStream(csocket.getOutputStream());
				BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));

				
				String s = socketIn.readLine().trim();
				if (s != null && !s.equals("")) {
					System.out.println("Server: " + s);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			inici.release();
		}
	}

	public static String enviarMissatge(String m) throws IOException {
		boolean enviat = true;
		String paraulesFinal = null;
		while (enviat == true) {
			if (m != "") {
				String texte = m;

				missatgesUsuaris(texte);

				paraules = texte.split(" ");

				for (String paraula : paraules) {
					paraulesString.add(paraula);
				}

				for (int i = 0; i < paraulesString.size(); i++) {
					if (paraulesProhibides.contains(paraulesString.get(i)) == true) {
						paraulaProhibidaDita(nomUser, idUser, paraulesString.get(i));
						paraulaXml(paraulesString.get(i));
						String censurat = "*";
						for (int j = 1; j < paraulesString.get(i).length() - 1; j++) {
							censurat += "*";
						}
						int repetir = paraulesString.get(i).length();
						paraulesString.set(i, paraulesString.get(i).substring(0, 1) + censurat);
					}
				}
				paraulesFinal = String.join(" ", paraulesString);

				/*
				 * String s = socketIn.readLine().trim(); if (s != null && !s.equals("")) {
				 * System.out.println("Server: " + s); if (s.trim().equals("adeu")) { break; }
				 * 
				 * }
				 */
				if (paraulesFinal != null && !paraulesFinal.equals("")) {
					socketOut.println(paraulesFinal);
					if (paraulesFinal.trim().equalsIgnoreCase("adeu")) {
						break;
					} else {
						paraules = null;
						paraulesString.clear();
						enviat = false;
					}
				}

			}
			// return paraulesFinal;
		}

		return paraulesFinal;
	}

	public static class paraulesBD extends Thread {

		public paraulesBD() {
			super();
		}

		public void run() {
			Connection conn = null;
			try {
				conn = DriverManager.getConnection("jdbc:mysql://192.168.1.218:3306/chat", "admin", "Fat/3232");
				Statement st = conn.createStatement();
				String sql = "SELECT * FROM DEL_paraules_prohibides;";
				ResultSet rs = st.executeQuery(sql);
				while (rs.next()) {
					String paraula = rs.getString("paraula");
					paraulesProhibides.add(paraula);
					// System.out.println(paraula);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (conn != null)
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
			inici.release();
		}
	}

	public static void missatgesUsuaris(String missatge) {
		MongoClient mongoClient = new MongoClient("192.168.1.218", 27017);
		DB db = mongoClient.getDB("chatEmpresa");
		DBCollection coll = db.getCollection("missatgesUsuaris");

		MissatgeUsuari missUsr = new MissatgeUsuari();
		missUsr.setDni(idUser);
		missUsr.setMissatge(missatge);
		missUsr.setDatahora(formatter.format(date));

		String json = "{'id_emisor' : '" + missUsr.getDni() + "', 'missatge' : '" + missUsr.getMissatge()
				+ "','dataHora' : '" + missUsr.getDatahora() + "'}";
		DBObject dbObject = (DBObject) JSON.parse(json);

		coll.insert(dbObject);
	}

	public static void paraulaProhibidaDita(String usuariConnectat, String id, String paraula) {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://192.168.1.218:3306/chat", "admin", "Fat/3232");
			Statement st = conn.createStatement();
			String paraulaSql = "SELECT id FROM DEL_paraules_prohibides WHERE paraula = '" + paraula + "';";
			ResultSet rs = st.executeQuery(paraulaSql);
			int id_paraula = 0;
			while (rs.next()) {
				id_paraula = rs.getInt("id");
			}
			st.close();

			Statement stIdUsr = conn.createStatement();
			String idUsrSql = "SELECT id FROM DEL_usuaris WHERE dni ='" + id + "';";
			rs = stIdUsr.executeQuery(idUsrSql);
			int id_emisor = 0;
			while (rs.next()) {
				id_emisor = rs.getInt("id");
			}
			stIdUsr.close();
			rs.close();

			String sql = "INSERT INTO DEL_paraula_usuari (id_usuari_emiso, id_paraula, paraula, data_hora)  VALUES ("
					+ id_emisor + ", " + id_paraula + ", '" + paraula + "', NOW());";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.execute();

			ps.close();
			MongoClient mongoClient = new MongoClient("192.168.1.218", 27017);
			DB db = mongoClient.getDB("chatEmpresa");
			DBCollection coll = db.getCollection("paraulesDites");

			RegisteParaula rp = new RegisteParaula();
			rp.setId_paraula(id_paraula);
			rp.setId_emisor(id_emisor);
			rp.setNomEmisor(usuariConnectat);
			rp.setParaula(paraula);
			rp.setData_hora(formatter.format(date));

			String json = "{'id_paraula' : " + rp.getId_paraula() + "," + "'emisor' : {'id_emisor' : "
					+ rp.getId_emisor() + ", 'nomEmisor' : '" + rp.getNomEmisor() + "'},"
					+ "'paraulaProhibida' : { 'id_paraula' : " + rp.getId_paraula() + " , " + "'paraula' : '"
					+ rp.getParaula() + "'}," + "'dataHora' : '" + rp.getData_hora() + "'}";
			DBObject dbObject = (DBObject) JSON.parse(json);

			coll.insert(dbObject);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}

	public static void paraulaXml(String paraula) {
		SecretKey key;
		key = keygenKeyGeneration(256);

		File ruta = new File("\\\\192.168.1.218\\ParaulesDitesUsuaris\\" + idUser);
		File rutaArxiu = new File(ruta + "\\" + idUser + ".xml");
		File rutaArxiuEnc = new File(ruta + "\\Encriptacio\\E_" + idUser + ".txt");

		if (!ruta.exists()) {
			ruta.mkdirs();
		}
		if (!rutaArxiuEnc.exists()) {
			rutaArxiuEnc.mkdirs();
		}

		/*
		 * System.out.println(ruta); System.out.println(rutaArxiu);
		 * System.out.println(rutaArxiuEnc);
		 */

		UsuariParaula usuariParaula = new UsuariParaula();
		usuariParaula.setIdEmisor(idUser);
		usuariParaula.setNomEmisor(nomUser);
		usuariParaula.setParaula(paraula);
		usuariParaula.setDatahora(formatter.format(date));

		try {
			DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = df.newDocumentBuilder();
			if (!rutaArxiu.exists()) {
				Document document = db.newDocument();

				Element arrel = document.createElement("deliverass");
				document.appendChild(arrel);

				Element pare = document.createElement("paraulesProhibides");
				arrel.appendChild(pare);

				Attr id = document.createAttribute("id");
				id.setValue("'" + usuariParaula.getIdEmisor() + "'");
				pare.setAttributeNode(id);

				Element nomEmisor = document.createElement("nomEmisor");
				nomEmisor.appendChild(document.createTextNode(usuariParaula.getNomEmisor()));
				pare.appendChild(nomEmisor);

				Element paraulaDita = document.createElement("paraula");
				paraulaDita.appendChild(document.createTextNode(usuariParaula.getParaula()));
				pare.appendChild(paraulaDita);

				Element dataHora = document.createElement("datahora");
				dataHora.appendChild(document.createTextNode(usuariParaula.getDatahora()));
				pare.appendChild(dataHora);

				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer t = tf.newTransformer();
				DOMSource ds = new DOMSource(document);
				StreamResult sr = new StreamResult(rutaArxiu);

				t.transform(ds, sr);
			} else {
				Document document = db.parse(rutaArxiu);

				Element documentElement = document.getDocumentElement();

				Element nomEmisor = document.createElement("nomEmisor");
				nomEmisor.setTextContent(usuariParaula.getIdEmisor());

				Element paraulaDita = document.createElement("paraula");
				paraulaDita.setTextContent(usuariParaula.getParaula());

				Element datahora = document.createElement("datahora");
				datahora.setTextContent(usuariParaula.getDatahora());

				Element nodeElement = document.createElement("paraulesProhibides");
				nodeElement.appendChild(nomEmisor);
				nodeElement.appendChild(paraulaDita);
				nodeElement.appendChild(datahora);

				documentElement.appendChild(nodeElement);
				document.replaceChild(documentElement, documentElement);

				Transformer tFormer = TransformerFactory.newInstance().newTransformer();
				tFormer.setOutputProperty(OutputKeys.METHOD, "xml");
				Source source = new DOMSource(document);
				Result result = new StreamResult(rutaArxiu);
				tFormer.transform(source, result);
			}

			encriptacioIdecriptacio(Cipher.ENCRYPT_MODE, key, rutaArxiu, rutaArxiuEnc);

		} catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
			e.printStackTrace();
		}

	}

	public static void encriptacioIdecriptacio(int cipherMode, SecretKey key, File arxiuEntrada, File arxiuSortida) {
		try {
			// Xifrar en mode AES
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(cipherMode, key);

			// Obtencio de dades de l'arxiu
			FileInputStream is = new FileInputStream(arxiuEntrada);
			byte[] bytes = new byte[(int) arxiuEntrada.length()];
			is.read(bytes);

			byte[] os = cipher.doFinal(bytes);

			FileOutputStream outputStream = new FileOutputStream(arxiuSortida);
			outputStream.write(os);

			is.close();
			outputStream.close();

		} catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException
				| IllegalBlockSizeException | IOException e) {
			e.printStackTrace();
		}
	}

	public static SecretKey keygenKeyGeneration(int keySize) {
		SecretKey sKey = null;
		if ((keySize == 128) || (keySize == 192) || (keySize == 256)) {
			try {
				KeyGenerator kgen = KeyGenerator.getInstance("AES");
				kgen.init(keySize);
				sKey = kgen.generateKey();

			} catch (NoSuchAlgorithmException ex) {
				System.err.println("Generador no disponible.");
			}
		}
		return sKey;
	}

	@FXML
	public void usuariLogout(ActionEvent event) throws IOException {
		Main m = new Main();
		m.cambiarEscena("Sample.fxml");
	}

	public void enviarMissatge(ActionEvent event) throws IOException {

		String s = input.getText().toString();
		String paraulesFinals = enviarMissatge(s);
		missatges.appendText(nomUser + ": " + paraulesFinals + "\n");
		input.setText("");
	}
}