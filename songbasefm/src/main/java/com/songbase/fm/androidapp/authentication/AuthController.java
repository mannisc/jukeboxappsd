package com.songbase.fm.androidapp.authentication;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.SharedPreferences;

import com.songbase.fm.androidapp.settings.Settings;

public class AuthController {

	public static SharedPreferences preferences;

	public static String ip_token = "iamadmin";
	public static BigInteger publicKeyModulus = new BigInteger(
			"a0bb4bfeb95482f621562fa9f946528febc4a23f4aabbc029b4459ca68972ec2ca9e1341ab3282fc7bacabfc0fc48aeb18fe5c964563fdd0116afdd6cb24255158fbf48b2447864303cc18ee0a65b0ee6e660d8ad021d010bb27bccdb19140ee80d0b2a3883d62ca2943a64a02665a1c23e5c786081f6fdfe01b43aee80d917d",
			16);
	public static BigInteger publicKeyExponent = BigInteger.valueOf(3);

	public static String loginToken = "";

	public static void init(Context con) {
		// Restore preferences
		preferences = con.getSharedPreferences(Settings.SETTINGS_NAME, 0);
		AuthController.loginToken = preferences.getString("loginToken", "");

	}

	public static void setLoginToken(String loginToken) {

		AuthController.loginToken = loginToken;

		SharedPreferences.Editor editor = AuthController.preferences.edit();
		editor.putString("loginToken", AuthController.loginToken);
		editor.commit();

	}

	public static final String md5(final String s) {
		final String MD5 = "MD5";
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuilder hexString = new StringBuilder();
			for (byte aMessageDigest : messageDigest) {
				String h = Integer.toHexString(0xFF & aMessageDigest);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

}
