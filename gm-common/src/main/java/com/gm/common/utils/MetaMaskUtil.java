/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.gm.common.utils;

import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.security.SignatureException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * 以太坊签名消息校验工具
 *
 * @author Mark sunlightcs@gmail.com
 */
public class MetaMaskUtil {
    /**
     * 以太坊自定义的签名消息都以以下字符开头
     * 参考 eth_sign in https://github.com/ethereum/wiki/wiki/JSON-RPC
     */
    public static void main(String[] args) throws SignatureException, ParseException {
//        //签名后的数据
//        String signature="0xf845c2f9b4bcad63f841d02c7b42bf11c843b95df190cdf743dc8000eff8ef2b3c9cfe3f108b452fd9fda7759e802aecefb64f1c920f246517253209108f4b421c";
//        //签名原文
        String message="Please sign to let us verify that you are the owner of this address:\n0x89394Dd3903aE07723012292Ddb1f5CA1B6bCe45\n\n[2022/3/7 18:14:10]";
//        //签名的钱包地址
//        String address="0x89394Dd3903aE07723012292Ddb1f5CA1B6bCe45";
//        Boolean result = validate(signature,message,address);
//        System.out.println(result);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String idInfo = message.substring(message.indexOf("[") + 1,message.indexOf("]"));
        System.out.println(format.parse(idInfo));
        long ss = 1646730443000l;
        System.out.println(ss - (1000 * 12 * 60 * 60));
//        String pubKey = "0x89394Dd3903aE07723012292Ddb1f5CA1B6bCe45";  //ReservedKey, mykey返回，或者从ETH链上查询
//        String unsignedData = "Please sign to let us verify that you are the owner of this address:\n0x89394Dd3903aE07723012292Ddb1f5CA1B6bCe45\n\n[2022/3/7 18:14:10]";
//        byte[] signature2 = Numeric.hexStringToByteArray("0xf845c2f9b4bcad63f841d02c7b42bf11c843b95df190cdf743dc8000eff8ef2b3c9cfe3f108b452fd9fda7759e802aecefb64f1c920f246517253209108f4b421c");
//        boolean isTrue = verifyPrefixedMessage(Hash.sha3(unsignedData.getBytes()), signature2, Numeric.cleanHexPrefix(pubKey).toLowerCase());
//        System.out.println("verify sig " + isTrue);
    }
    /**
     * 对签名消息，原始消息，账号地址三项信息进行认证，判断签名是否有效
     * @param signature
     * @param message
     * @param address
     * @return
     */
    public static boolean validate(String signature, String message, String address) {
        final String personalMessagePrefix = "\u0019Ethereum Signed Message:\n";
        boolean match = false;
        final String prefix = personalMessagePrefix + message.length();
        final byte[] msgHash = Hash.sha3((prefix + message).getBytes());
        final byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
        byte v = signatureBytes[64];
        if (v < 27) {
            v += 27;
        }

        final Sign.SignatureData sd = new Sign.SignatureData(v,
                Arrays.copyOfRange(signatureBytes, 0, 32),
                Arrays.copyOfRange(signatureBytes, 32, 64));

        String addressRecovered = null;

        // Iterate for each possible key to recover
        for (int i = 0; i < 4; i++) {
            final BigInteger publicKey = Sign.recoverFromSignature((byte) i, new ECDSASignature(
                    new BigInteger(1, sd.getR()),
                    new BigInteger(1, sd.getS())), msgHash);

            if (publicKey != null) {
                addressRecovered = "0x" + Keys.getAddress(publicKey);
                if (addressRecovered.equals(address.toLowerCase())) {
                    match = true;
                    break;
                }
            }
        }

        return match;
    }





    public static boolean verifyPrefixedMessage(byte[] data, byte[] sig, String pubKeyAddress) throws SignatureException {
        byte[] r = Arrays.copyOfRange(sig, 0, 32);
        byte[] s = Arrays.copyOfRange(sig, 32, 64);
        byte[] v = Arrays.copyOfRange(sig, 64, sig.length);
        Sign.SignatureData signatureData = new Sign.SignatureData(v, r, s);
        BigInteger recoveredPubKey = Sign.signedPrefixedMessageToKey(data, signatureData);
        System.out.println(Keys.getAddress(recoveredPubKey));
        System.out.println(pubKeyAddress);
        return pubKeyAddress.equals(Keys.getAddress(recoveredPubKey));
    }

}
