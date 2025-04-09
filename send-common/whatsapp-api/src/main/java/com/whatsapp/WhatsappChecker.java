package com.whatsapp;

import com.google.gson.*;
import okhttp3.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.whispersystems.curve25519.Curve25519;
import org.whispersystems.curve25519.Curve25519KeyPair;
import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;
import org.whispersystems.libsignal.util.KeyHelper;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.security.Security;
import java.util.*;

public class WhatsappChecker {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private final static byte[] ENCRYPT_KEY = new byte[]{
            (byte) 142, (byte) 140, (byte) 15, (byte) 116, (byte) 195, (byte) 235, (byte) 197, (byte) 215,
            (byte) 166, (byte) 134, (byte) 92, (byte) 108, (byte) 60, (byte) 132, (byte) 56, (byte) 86,
            (byte) 176, (byte) 97, (byte) 33, (byte) 204, (byte) 232, (byte) 234, (byte) 119, (byte) 77,
            (byte) 34, (byte) 251, (byte) 111, (byte) 18, (byte) 37, (byte) 18, (byte) 48, (byte) 45,
    };

    public static WhatsappAccountStatus checkBlock(String phone, String appVersion, Proxy proxy) {
        if (phone == null || phone.isEmpty()) {
            return WhatsappAccountStatus.ERROR.setReason("phone is empty");
        }

        PhoneNumberParse.PhoneInfo parse = PhoneNumberParse.parse(phone);
        if (parse == null) {
            return WhatsappAccountStatus.ERROR.setReason("Invalid phone number");
        }

        String cc = parse.getCc();
        String in = parse.getIn();

        try {
            String response = reqLoginExit(cc, in, appVersion, proxy);
            if (isValidStrictly(response)) {
                JsonObject responseJson = JsonParser.parseString(response).getAsJsonObject();
                String reason = responseJson.get("reason").getAsString();
                if (reason.equals("incorrect")){
                    return WhatsappAccountStatus.SUCCESS;
                }else if (reason.equals("blocked")){
                    return WhatsappAccountStatus.BLOCK;
                }else {
                    return WhatsappAccountStatus.ERROR.setReason("unknown reason: " + reason);
                }
            } else {
                return WhatsappAccountStatus.ERROR.setReason("response is error: " + response);
            }
        } catch (Exception e) {
            return WhatsappAccountStatus.ERROR.setReason("check error: " + e.getMessage());
        }
    }

    public static String reqLoginExit(String cc, String phone, String appVersion, Proxy proxy) throws InvalidKeyException {
        boolean isBusiness = false;

        byte[] idBytes = AESUtils.createNewSharedKey128().getEncoded();
        String id = lkUrlEncodeIos(idBytes);

        Curve25519KeyPair v3 = Curve25519.getInstance(Curve25519.BEST).generateKeyPair();
        byte[] publicKey = v3.getPublicKey();
        String strauthkey = getBase64(publicKey);
        String stre_regid = getGUID() + "==";

        IdentityKeyPair v7 = KeyHelper.generateIdentityKeyPair();
        byte[] v8 = v7.getPublicKey().serialize();
        String v8_1 = getBase64(Arrays.copyOfRange(v8, 1, v8.length));
        SignedPreKeyRecord v1 = KeyHelper.generateSignedPreKey(v7, 0);
        String stre_skey_sig = getBase64(v1.getSignature());

        String strexpid = compDeviceIdentifierIos(UUID.randomUUID().toString());


        byte[] e_skey_id_bytes = new byte[3];
        new SecureRandom().nextBytes(e_skey_id_bytes);
        String e_skey_id = getBase64(e_skey_id_bytes);
        byte[] v7_1 = v1.getKeyPair().getPublicKey().serialize();
        String stre_skey_val = getBase64(Arrays.copyOfRange(v7_1, 1, v7_1.length));


        String fdid = UUID.randomUUID().toString().toUpperCase();

        String token = TokenHandle.iosToken(phone, appVersion, isBusiness);
        String t = String.valueOf(System.currentTimeMillis() / 1000);
        String reqSmsVal =
                "cc=" + cc +
                        "&in=" + phone +
                        "&rc=0" +
                        "&lg=" + "en" +
                        "&lc=" + "US" +
                        "&authkey=" + urlEncode3(strauthkey) +
                        "&e_regid=" + urlEncode3(stre_regid) +
                        "&e_keytype=" + urlEncode3("BQ==") +
                        "&e_ident=" + urlEncode3(v8_1) +
                        "&e_skey_id=" + urlEncode3(e_skey_id) +
                        "&e_skey_val=" + urlEncode3(stre_skey_val) +
                        "&e_skey_sig=" + urlEncode3(stre_skey_sig) +
                        "&fdid=" + fdid +
                        "&expid=" + urlEncode3(strexpid).replaceAll("=", "") +
                        "&offline_ab=%7B%22%65%78%70%6F%73%75%72%65%22%3A%5B%5D%2C%22%6D%65%74%72%69%63%73%22%3A%7B%22%65%78%70%69%64%5F%63%22%3A%74%72%75%65%2C%22%66%64%69%64%5F%63%22%3A%74%72%75%65%2C%22%72%63%5F%63%22%3A%74%72%75%65%2C%22%65%78%70%69%64%5F%6D%64%22%3A%31%37%34%32%33%37%33%37%39%39%2C%22%65%78%70%69%64%5F%63%64%22%3A%31%37%34%32%33%37%33%37%39%39%7D%7D" +
                        "&recovery_token_error=-25300" +
                        "&token=" + token +
                        "&id=" + id +
                        "&t=" + t;

        String urlEncode = (encryptParamsIOS(reqSmsVal));
        String sb2 = "https://v.whatsapp.net/v2/exist";
        return SendBaseRequestIOS(sb2, urlEncode, appVersion, proxy);
    }

    private static String urlEncode3(String str) {
        try {
            if (str.contains("%")) {
                return str;
            }
            byte[] decode = Base64.getDecoder().decode(str);
            return Base64.getUrlEncoder().encodeToString(decode);
        } catch (Exception e) {
            try {
                byte[] decode = Base64.getUrlDecoder().decode(str);
                return Base64.getUrlEncoder().encodeToString(decode);
            } catch (Exception e2) {
                return str;
            }

        }
    }

    private static String lkUrlEncodeIos(byte[] bytes) {
        try {
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                short b1 = (short) (b & 0xFF);
                result.append("%");
                if (b1 < 16) {
                    result.append("0");
                }
                result.append(Integer.toHexString(b1));
            }
            return result.toString().toUpperCase();
        } catch (Exception e) {
            return null;
        }
    }

    private static String getBase64(byte[] bArr) {
        return Base64.getEncoder().encodeToString(bArr);
    }

    private static String compDeviceIdentifierIos(String perf_device_id) {
        byte[] bArr;
        try {
            UUID fromString = UUID.fromString(perf_device_id);
            ByteBuffer allocate = ByteBuffer.allocate(16);
            allocate.putLong(fromString.getMostSignificantBits());
            allocate.putLong(fromString.getLeastSignificantBits());
            bArr = allocate.array();
        } catch (IllegalArgumentException unused) {
            System.err.println("RegistrationUtils/getBytesFromUUIDString/invalid-input ");
            bArr = new byte[0];
        }
        return Base64.getUrlEncoder().encodeToString(bArr);
    }

    private static String getGUID() {
        StringBuilder uid = new StringBuilder();
        //产生22位的强随机数
        Random rd = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            //产生0-2的3位随机数
            int type = rd.nextInt(3);
            switch (type) {
                case 0:
                case 2:
                    //ASCII在97-122之间为小写，获取小写随机
                    uid.append((char) (rd.nextInt(25) + 97));
                    break;
                case 1:
                    //ASCII在65-90之间为大写,获取大写随机
                    uid.append((char) (rd.nextInt(25) + 65));
                    break;
                default:
                    break;
            }
        }
        return uid.toString();
    }

    private static String encryptParamsIOS(String str) {
        try {
            return encryptParametersIOS(str);
        } catch (Exception exception) {
            return "1";
        }
    }

    private static String encryptParametersIOS(String parameters) {
        try {
            Curve25519KeyPair keyPair = Curve25519.getInstance(Curve25519.BEST).generateKeyPair();

            byte[] publicKey = keyPair.getPublicKey();
            byte[] sharedSecret = Curve25519.getInstance(Curve25519.BEST).calculateAgreement(ENCRYPT_KEY, keyPair.getPrivateKey());

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(sharedSecret, "AES/GCM/NoPadding"), new IvParameterSpec(new byte[12]));
            byte[] ciphertext = cipher.doFinal(parameters.getBytes());


            byte[] encryptedParameters = new byte[publicKey.length + ciphertext.length];
            System.arraycopy(publicKey, 0, encryptedParameters, 0, publicKey.length);
            System.arraycopy(ciphertext, 0, encryptedParameters, publicKey.length, ciphertext.length);

            return Base64.getUrlEncoder().encodeToString(encryptedParameters)/*.replace("=", "")*/;
        } catch (Exception ignore) {
        }
        return parameters;
    }

    private static boolean isValidStrictly(String json) {
        TypeAdapter<JsonElement> strictAdapter = new Gson().getAdapter(JsonElement.class);
        try {
            strictAdapter.fromJson(json);
        } catch (JsonSyntaxException | IOException e) {
            return false;
        }
        return true;
    }

    private static final OkHttpClient client;

    static {
        client = Objects.requireNonNull(OKHttpClientBuilder.buildOKHttpClient()).build();
    }

    public static String SendBaseRequestIOS(String url,String Enc,String appVersion, Proxy proxy) {
        String strResult;

        OkHttpClient m_client;
        if (proxy != null) {
            m_client = client.newBuilder()
                    .proxy(proxy)
                    .build();
        } else {
            m_client = client;
        }

        Response response = null;
        try {
            String ua;
            {
                String format;
                format = "WhatsApp/%s iOS/%s Device/%s";
                ua = String.format(format, appVersion, "16.5.1", "iPhone_15_Pro_Max");
            }

            FormBody.Builder builder = new FormBody.Builder();
            builder.add("ENC", Enc);
            FormBody formBody = builder.build();

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("user-agent", ua)
                    .addHeader("accept", "*/*")
                    .addHeader("accept-language", "en-US")
                    .addHeader("accept-encoding", "gzip, deflate, br")
                    .post(formBody)
                    .build();

            response = m_client.newCall(request).execute();
            if (response.isSuccessful()) {
                assert response.body() != null;
                strResult = response.body().string();
            }else {
                strResult = "Http Send Failed: " + response.message();
            }
        } catch (Exception e) {
            strResult = "HTTP Send Failed=" + e.getMessage();
        }finally {
            if (response != null) {
                response.close();
            }
        }
        return strResult;
    }
}
