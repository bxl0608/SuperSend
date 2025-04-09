package com.whatsapp;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

class AESUtils {

    public static SecretKey createNewSharedKey128(){
        CipherKeyGenerator var0 = new CipherKeyGenerator();
        var0.init(new KeyGenerationParameters(new SecureRandom(), 128));
        return new SecretKeySpec(var0.generateKey(), "AES");
    }

}