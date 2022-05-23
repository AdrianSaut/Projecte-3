package application;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class ObtenirXML {
	public static final Date date = new Date();
	public static final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
	public static final String timeStamp = formatter.format(date);
	public static Scanner sc = new Scanner(System.in);
	public static Boolean decriptat = false;
	public static void main(String[] args) {

		System.out.println("De quin usuari vols obtenir el xml? (DNI)");
		String id = sc.nextLine();
		System.out.println("I a quina ruta vols guardar l'arxiu?");
		String arxiu = sc.nextLine();
		
		
		SecretKey key;
		key = keygenKeyGeneration(256);
		String clau = "\\\\192.168.1.218\\ParaulesDitesUsuaris\\"+id+"\\Encriptacio";
		File ruta = new File("\\\\192.168.1.218\\ParaulesDitesUsuaris\\" + id);
		File rutaArxiu = new File(ruta+"\\"+id+".xml");
		File rutaArxiuEnc = new File(ruta+"\\E_"+id+".xml");
		File rutaArxiuDec = new File(arxiu+"\\"+id+".xml");

		encriptacioIdecriptacio(Cipher.ENCRYPT_MODE, key, rutaArxiu, rutaArxiuEnc);
		encriptacioIdecriptacio(Cipher.DECRYPT_MODE, key, rutaArxiuEnc, rutaArxiuDec);
		
		if(decriptat == true) {
			System.out.println("L'arxiu decriptat es troba a la ruta: " + rutaArxiuDec);
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
			decriptat = true;
			
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
}
