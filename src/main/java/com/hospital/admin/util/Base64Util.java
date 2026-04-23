package com.hospital.admin.util;

import java.util.Base64;

public class Base64Util {

	public static byte[] decode(String base64) {
	    if (base64 == null) return null;

	    try {
	        if (base64.contains(",")) {
	            base64 = base64.split(",")[1];
	        }

	        byte[] data = Base64.getDecoder().decode(base64);

	        // limit ~2MB
	        if (data.length > 2_000_000) {
	            throw new RuntimeException("Image too large");
	        }

	        return data;
	    } catch (IllegalArgumentException e) {
	        throw new RuntimeException("Invalid Base64 input");
	    }
	}

    public static String encode(byte[] bytes) {
        if (bytes == null) return null;

        return Base64.getEncoder().encodeToString(bytes);
    }
}
