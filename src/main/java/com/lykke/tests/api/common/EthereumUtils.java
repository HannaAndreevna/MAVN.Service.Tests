package com.lykke.tests.api.common;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

@UtilityClass
public class EthereumUtils {

    public static String signLinkingCode(String code, String privateKey) {
        val credentials = Credentials.create(privateKey);
        byte[] data = code.getBytes();
        Sign.SignatureData signature = Sign.signPrefixedMessage(data, credentials.getEcKeyPair());
        return Numeric.toHexString(signature.getR()) + Numeric.toHexString(signature.getS()).substring(2) + Numeric
                .toHexString(signature.getV()).substring(2);
    }
}
