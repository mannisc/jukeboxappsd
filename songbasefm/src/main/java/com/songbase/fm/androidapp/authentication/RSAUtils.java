package com.songbase.fm.androidapp.authentication;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

import android.util.Base64;

public class RSAUtils {

	private static EasyCrypt easyCrypt;

	public static void init() {

		try {
			RSAPublicKeySpec spec = new RSAPublicKeySpec(
					AuthController.publicKeyModulus,
					AuthController.publicKeyExponent);
			KeyFactory factory;
			factory = KeyFactory.getInstance("RSA");
			PublicKey publicKey;
			publicKey = factory.generatePublic(spec);

			easyCrypt = new EasyCrypt(publicKey, "RSA/None/PKCS1Padding");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String base64StringtoHexString(String base64String) {

		StringBuilder sb = new StringBuilder();
		try {
			for (byte b : Base64.decode(base64String, Base64.DEFAULT)) {

				sb.append(String.format("%02X", b));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sb.toString().toLowerCase();

	}

	public static String encrypt(String text) {
		try {
			return RSAUtils.base64StringtoHexString(easyCrypt.encrypt(text));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";

		}
	}

}
