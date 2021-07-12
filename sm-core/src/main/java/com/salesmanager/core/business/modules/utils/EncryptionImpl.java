package com.salesmanager.core.business.modules.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;

import com.salesmanager.core.modules.utils.Encryption;

public final class EncryptionImpl implements Encryption {
	
	private final static String IV_P = "fedcba9876543210";
	private final static String KEY_SPEC = "AES";
	private final static String CYPHER_SPEC = "AES/CBC/PKCS5Padding";
	


    private String  secretKey;



	@Override
	public String encrypt(String value) throws Exception {

		
		// value = StringUtils.rightPad(value, 16,"*");
		// Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
		// NEED TO UNDERSTAND WHY PKCS5Padding DOES NOT WORK
		Cipher cipher = Cipher.getInstance(CYPHER_SPEC);
		SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), KEY_SPEC);
		IvParameterSpec ivSpec = new IvParameterSpec(IV_P
				.getBytes());
		System.out.println("$#1471#"); cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
		byte[] inpbytes = value.getBytes();
		byte[] encrypted = cipher.doFinal(inpbytes);
		System.out.println("$#1472#"); return new String(bytesToHex(encrypted));
		
		
	}

	@Override
	public String decrypt(String value) throws Exception {

		
		System.out.println("$#1473#"); if (StringUtils.isBlank(value))
			throw new Exception("Nothing to encrypt");

		// NEED TO UNDERSTAND WHY PKCS5Padding DOES NOT WORK
		// Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
		Cipher cipher = Cipher.getInstance(CYPHER_SPEC);
		SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), KEY_SPEC);
		IvParameterSpec ivSpec = new IvParameterSpec(IV_P
				.getBytes());
		System.out.println("$#1474#"); cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
		byte[] outText;
		outText = cipher.doFinal(hexToBytes(value));
		System.out.println("$#1475#"); return new String(outText);
		
		
	}
	
	
	private String bytesToHex(byte[] data) {
		System.out.println("$#1476#"); if (data == null) {
			System.out.println("$#1477#"); return null;
		} else {
			int len = data.length;
			String str = "";
			System.out.println("$#1480#"); System.out.println("$#1479#"); System.out.println("$#1478#"); for (int i = 0; i < len; i++) {
				System.out.println("$#1483#"); System.out.println("$#1482#"); System.out.println("$#1481#"); if ((data[i] & 0xFF) < 16) {
					str = str + "0"
							+ java.lang.Integer.toHexString(data[i] & 0xFF);
				} else {
					System.out.println("$#1485#"); str = str + java.lang.Integer.toHexString(data[i] & 0xFF);
				}

			}
			System.out.println("$#1486#"); return str;
		}
	}

	private static byte[] hexToBytes(String str) {
		System.out.println("$#1487#"); if (str == null) {
			return null;
		} else if (str.length() < 2) { System.out.println("$#1488#"); System.out.println("$#1489#");
			return null;
		} else {
			System.out.println("$#1488#"); System.out.println("$#1489#"); // manual correction for else-if mutation coverage
			System.out.println("$#1490#"); int len = str.length() / 2;
			byte[] buffer = new byte[len];
			System.out.println("$#1493#"); System.out.println("$#1492#"); System.out.println("$#1491#"); for (int i = 0; i < len; i++) {
				System.out.println("$#1495#"); buffer[i] = (byte) Integer.parseInt(str.substring(i * 2,
						i * 2 + 2), 16);
			}
			System.out.println("$#1497#"); return buffer;
		}
	}
	
	public String getSecretKey() {
		System.out.println("$#1498#"); return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

}
