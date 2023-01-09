package com.gm.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAUtils {
    private static final Logger logger = LoggerFactory.getLogger(RSAUtils.class);

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 256;

    /**
     * 秘钥大小
     */
    private static final int KEY_SIZE = 2048;
    /**
     * 加密算法RSA
     */
    private static final String KEY_ALGORITHM = "RSA";
    /**
     * 签名算法
     */
    private static final String SIGNATURE_ALGORITHM_SHA256 = "SHA256WithRSA";

    private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    /**
     * 生成RSA秘钥对
     */
    public static RSAKey genRSAKey() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(KEY_SIZE);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        String publicKeyStr = getPublicKeyStr(publicKey);
        String privateKeyStr = getPrivateKeyStr(privateKey);

        RSAKey rsaKey = new RSAKey();
        rsaKey.setPrivateKey(privateKeyStr);
        rsaKey.setPublicKey(publicKeyStr);

        return rsaKey;
    }

    /**
     * 私钥签名
     *
     * @param data       待签名数据
     * @param privateKey 私钥
     * @return String 签名结果
     */
    public static String RSASign(String data, String privateKey) throws Exception {
        PrivateKey priKey = getPK8PrivateKey(Base64.getDecoder().decode(privateKey), KEY_ALGORITHM);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM_SHA256);
        signature.initSign(priKey);
        signature.update(data.getBytes());
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    /**
     * 公钥验签
     *
     * @param data      待验签数据
     * @param publicKey 商户公钥
     * @param sign      sign
     * @return boolean
     */
    public static boolean RSAVerify(String data, String publicKey, String sign) throws Exception {
        PublicKey pubKey = getX509PublicKey(Base64.getDecoder().decode(publicKey), KEY_ALGORITHM);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM_SHA256);
        signature.initVerify(pubKey);
        signature.update(data.getBytes());
        return signature.verify(Base64.getDecoder().decode(sign));
    }

    /**
     * 公钥加密
     *
     * @param data      待加密数据
     * @param publicKey 公钥
     * @return String   加密数据
     */
    public static String encryptByPublicKey(String data, String publicKey) throws Exception {
        Key key = getX509PublicKey(Base64.getDecoder().decode(publicKey), KEY_ALGORITHM);
        byte[] encrypt = encrypt(TRANSFORMATION, key, data.getBytes(), MAX_ENCRYPT_BLOCK);
        return Base64.getEncoder().encodeToString(encrypt);
    }

    /**
     * 私钥解密
     *
     * @param data       待解密数据
     * @param privateKey 私钥
     * @return String    解密数据
     */
    public static String decryptByPrivateKey(String data, String privateKey) throws Exception {
        Key key = getPK8PrivateKey(Base64.getDecoder().decode(privateKey), KEY_ALGORITHM);
        byte[] decrypt = decrypt(TRANSFORMATION, key, Base64.getDecoder().decode(data), MAX_DECRYPT_BLOCK);
        return new String(decrypt);
    }

    private static String getPrivateKeyStr(PrivateKey privateKey) throws Exception {
        return new String(Base64.getEncoder().encode(privateKey.getEncoded()));
    }

    private static String getPublicKeyStr(PublicKey publicKey) throws Exception {
        return new String(Base64.getEncoder().encode(publicKey.getEncoded()));
    }

    private static PrivateKey getPK8PrivateKey(byte pem[], String algorithm) throws Exception {
        PKCS8EncodedKeySpec pKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(pem);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePrivate(pKCS8EncodedKeySpec);
    }

    private static PublicKey getX509PublicKey(byte pem[], String algorithm) throws Exception {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(pem);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePublic(x509EncodedKeySpec);
    }

    private static byte[] encrypt(String transformation, Key key, byte data[], int blockSize) throws Exception {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(1, key);
        return finalCrypt(cipher, data, blockSize);
    }

    private static byte[] decrypt(String transformation, Key key, byte data[], int blockSize) throws Exception {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(2, key);
        return finalCrypt(cipher, data, blockSize);
    }

    private static byte[] finalCrypt(Cipher cipher, byte data[], int blockSize) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte bytes[];
        int dataLen = data.length;
        int blockNum = dataLen / blockSize;
        int lastSize = dataLen % blockSize;
        int off = 0;
        for (int i = 0; i < blockNum; i++) {
            baos.write(cipher.doFinal(data, off, blockSize));
            off += blockSize;
        }
        if (lastSize > 0) {
            baos.write(cipher.doFinal(data, off, lastSize));
        }
        bytes = baos.toByteArray();
        baos.close();
        return bytes;
    }

    public static void main(String[] args) throws Exception {
//        String data = "123456一二三！@#￥!@#$";
        RSAKey rsaKey = genRSAKey();
        System.out.println(rsaKey.getPublicKey());
        System.out.println(rsaKey.getPrivateKey());
//        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAomWoKeU+7jOW7A8H8Zr6FcX+TAVvYkwDvATK+hBQOjSlugpsyUhXDXhc0N9fpLzPAD9BkEAB5yAr5i2xqyVSFhXFE0oRhgkTtPKX9+t0y8ydWawqXDCMGeqb5XIbPJSNF2folVgYXV372ZulQEwKvEYp7SsgBklAR7uuEI+Zcd2P0yUyCmjB3mwM8pL+e/5GnJqcf24glJdV6+sp38bOax9NBgSx1y7qEKfX1Cibf7Y1e8S/q/qjqhY4cJkEkCssQefN0UB8qWWoOVHXI7CbWFqIPEewpg02qJt6+ORQemR9+lm+486xW38Vws/BaF1R/Xd6EQaYg6m1HMCcO+oMjQIDAQAB";
//        String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCwyD0m9xY3GrX1OymSI36JwHr67Xj12+2mG3D2KQyqVTV5s19ig8jlf2sKDX520yqFtMLmGAYr6btwXLhFw8KiAAo74Fjr6TtSce62ClVqqEAiHI0NGeSSc8vTIW+EbJRuOwyeI8O3mdtEJu1dMQABGbdV1DCAoj+366FjWeAeNlx0MialbRH+++MdLAhaT88wiyqcTlzVIXzmhCeYkF2i8QjceudJChzmnE5zIGk6vp0LjGvC/Zbz5am3Zh4M6vQD8tPCHISHAu38iQMws86ATaorRWGc7rzE+oFPY+z4SPcz17Q4dp8sMFbQSOLyYHKDD+oN35oP9Jy2McGGzpaNAgMBAAECggEACxr5JHp/LcUCyz4FNYX/S/4Ie7zxTVicjEPLC9u2TPj/W/7VriWjOC+R9Na2LBbF5BJf7/5wXM/ZE4zvkopirrSb7/j+/QrwzBQWL99kUEfzh3ENt1X6Eu0tT70Y+VOs22PgPgr3H5CNwrFDVghe/LK13rFuvpXytMYbpFcgCiW7QQzwxeMiBSTTS+Sh9TNLmF0n063eAnRVIC9tbboCvfVWhUvhixdaLa1Rq0uTl85C/Iz6ogJVfpAcu1muquTcjFpfbFHTFvX0YNyPjb67+mYfv17Oho4UTamDeijm5xwQF4phGsrFSTQWWCT6Sk5X6257MI7VeUuff5VKbc44rQKBgQDovADQhGsstiBmMECPKOfVrCfCA48svNQ5Dmh5bafWIVwTFXqfalfJPfNkPHISoyxkNlZ/kOLOnPFdhSKKpj3/jPiT0ZsxAXHniG8WNjGspJobmaBbM/wpD1KwNQRR32Sl9b5gueKiO59kvHm5JE0k3J+Ke+euyRVYJLtA1dOWWwKBgQDCdFdHJ2r81YVW80RHBez+M7FPTJgIdytHK3Vui/IjZXiTXgOmA5OXrujA4xgdjFNOu3hCMWw1aXJSEyXKNYUB0gQGsJFICBUDyolz9HV26e8946CN8UX4hXn7CJDpYfT4KV0AWPdZR8iAZU8LAfURuLH/f2/dVxhs2UuYz8grNwKBgQCzsoXGepft++nkPRl78A+hUb2Y9fqUhnAuZcuRBnPyx+s56Pm3IPsLLURCftFtRoQ+FB6RfKNC+Lb8iJF1EIsKSCSerdbv7bevGA79zXZpYOaErQe9o9PtOCGWYDknph8mbSpDsQMmVHbr5wLtSvQtpBOjb6whrq1GU4Ypt3AZxQKBgCQeUfvQcYnm6y58XwlGK4AH//43vuGLqQk+JEDVU7OJbH0K8GiMr4R0I514UQlkADFWlv7MiUnefTKLhfBqQntVFBReBY44mharLx7PoGXnC94WAsN6tkF0IXUkYS5IWKUjobenzsHRmIdoyebEhU22XbGUbSXSwKGvNuB6b7a/AoGAV4kmzfwXEw9nQXX3qLdjueWmhLGM9G9SqtmUbvsKqIwiQ52ovtAL821EhX2CMuD9OpDdgvUHR1j2GYozyEpf32c+9OkEYKrka/hEKiF1Vm0b8W1jwOeDg0y4XXWQkFQkXcWyOcgPmlT1ujB89ZEp6GHeamstFuIxVgr2ggn4KXE=";
//
//        String rsaSign = RSASign(data, privateKey);
//        System.out.println(rsaSign);
//        boolean b = RSAVerify(data, publicKey, rsaSign);
//        System.out.println(b);
//
        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwWKm489De/qdC8tGGKYqEoN+VGI4gZDJMgXHDF75Zb5ofKAPyrz/cVbd/P6RBDr1KEL1X+UmRSmRM+JmZx0A/Em8KuGdqe9areyfDEHDLOkJ+HfrtyoNC7WOPP4zj6COFAcjV8dFBYVTtpiBvBoyLQcHF32w0i22TswHOJEwpe1ae8ty+6UlSnUUZVmr0bEO09tmEV1mU5UuhdS2+i6HK7mfma5cBQVinvYSOcdKXCzOX6OkwKuim8T3aUNdn1taiybdXLLa5fdSiXd7/enk3C1RttPgQOlUH4eo4PsLnzjrcycFrYlIuEoMNUF6nnLsUAaiPt2AgkTzsUxru22f7wIDAQAB";
        String encryptByPublicKey = encryptByPublicKey("f94a3b8f8b5b7d6ed5a8a77a60252de6adc867629bfe2316b495c05e15017e54", publicKey);
        System.out.println(encryptByPublicKey);
//        String decryptByPrivateKey = decryptByPrivateKey("ZjsJvB8Z3S4Bw5971Zq7JdaUvFpbMbHqsLfo8KmbtWl4lKaE1mKBBEBgWcWqDLzOUJ4gkwJoFRCpPBomtsJNbX+pNN6trcG8mGXVdjxrtWUsi9d60Vibl0O9Ds43CEwRCQCjQW7vWa3h76kJuQRdt4HeHwzc/ZrPBEsSrk7oz4xRCQBnI55GRLZsvlQZLfQwWfO8QiHOWUFVj9vrg2aVHiflY6QX5LddgE1F+9uUqzUnKnDKHLdY50bcc9AmB1GDi8NwjZolDUaCQ9kf3bTCy+rJOuw/v5RDB8ETGUi7VPM9V4teFAtrRRIawSMQ+Wu0NwecxIVl+3QT7gP6uzNaUg==", privateKey);
//        System.out.println(decryptByPrivateKey);

    }
}
//        MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmCZxAlR89Gqtfwj8ilR1eEepljc7zGHXYtdffNfYhgQ9EbbjNRc23fWibfES/wsV8TQ/tyj7HT9upNJeQo+i0pjPlm6zAZ+YP9nX8JDVmcvytdp9AZVRHeADEE5hA8L2MhDNs/XEezib/FJAZGz5rIFl56DgYwMWmruwgmCj2362oe9ezw7zu87J44nhRy5/mdI8b98LxzWFzcufd+wvccM8mxOIiQ/FwxLUZP8/a4IS1r7Egk1IvVl+RALvtEShTxhbkOJinApmJI+0cIJoToh68Jvo0zG+IExMsenrU1GrQqg9mkUBzr9Xya1PCJmDhe2nIEXaVCq25Awnv+k3qwIDAQAB
//        MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCYJnECVHz0aq1/CPyKVHV4R6mWNzvMYddi119819iGBD0RtuM1Fzbd9aJt8RL/CxXxND+3KPsdP26k0l5Cj6LSmM+WbrMBn5g/2dfwkNWZy/K12n0BlVEd4AMQTmEDwvYyEM2z9cR7OJv8UkBkbPmsgWXnoOBjAxaau7CCYKPbfrah717PDvO7zsnjieFHLn+Z0jxv3wvHNYXNy5937C9xwzybE4iJD8XDEtRk/z9rghLWvsSCTUi9WX5EAu+0RKFPGFuQ4mKcCmYkj7RwgmhOiHrwm+jTMb4gTEyx6etTUatCqD2aRQHOv1fJrU8ImYOF7acgRdpUKrbkDCe/6TerAgMBAAECggEBAJAVv2l0d0zDhX/LE9Uv/HqmarwdnFyFv4IT8ZJ5mcv4UZjn+Cy7yjLSPjsdVF8AsYvVbg1XGs9s1avyqF8iRRBotBWCybRc3cKB2xQvJIjb8gIzYbRRIhtHm78iao80Xw+0iAjHUoVaa6W8gsxXwu2RMN70o+o/73UjDxm4yRO/awSg2BW6bBhklc0x63GNB6DnUXrGPMzVWMsa/vwz75wZreolsfzWoqP3eKuPRPhy4i23tZWAHlkCI8/3SAgj2qranIjy3yPZAo12KTSPgldOr2tgh5k4u5qIi8oK6ZZLIa592IyrtaZlL95U1LZOYjPgEDv25auYbR6t64IRtIkCgYEAx3uE2ZbzJUS1EZuQTONw371LNcDssYRy9ZLBa/vdETPKYNBENG2uQ/QTlHidK6eJ5KXtIxRJbVwyiIZ5uypQ+g4A+GdPjD5tKJZ2ezFPHCaQxqU8Y5jg07y24pTxIjN4fDKt5bXkpUPBByi8slhnfQD1ncrEMUUfk2+zqu7PFCUCgYEAw0HnlBhZcMuEQ4DxujmO4pj2eC+uFYc8Y1NjwEO9IjGK8q+RISebxLo+mbqyDkGBZiAMN/TqQ1AGGih9EvcN5Xhu5fvJQpk5zi2k+X+k2cM+NQnocdJKVDThr4Pru2eCqUmBQA1xN+jokVU5vb6CGmAVXiUOOV5QdjTjkXQT648CgYBzGtr1kxyKDLNOja0eML8Y22ligwgRBpQvD5d8b8CIBJJqesJgXkmH/Hav3UkTv4DZ5sSG0VXtPJ4MGUx0q14+a4Yzz6kfC4aBPRDw1OsJG1e+x+morrZMd0AwTjE1wu1q/PNK8UIV+pqPPvpjDo/omX0gp7e9ll05/eJ2Z4Yl4QKBgDvihqgBH81cXeBmN70p0gzlZRJPbVDk/TxpYvqgy+0kzLzJ4/ULNNcNWA3GGL4OkSVFRvoSf5bNgFGdgV7DrttmKH0Y0A1A+V2m2iY7Y8/19apGs6fVGz7gSSePikkb4hMRnDaM6OCA6nNIdEEhoL1+Io+RRd+KcZsPdYwy0bk5AoGBAIObBaXKLaUVCLo7tvTpUlWgAxxtZAgGP2ZRK0AX6A0SaIli6QRp7Z0mu6iqoxK6DghH7tf2qnI/VW4uvMoNIHN/wgFK6FTZgRHonG4MCTPnJ4qkXWiPL/Jr209jk32Tc98Y7A+l3byxFaIXVvbAPVRh1c2Ztiz+C66DalpWaGvw


