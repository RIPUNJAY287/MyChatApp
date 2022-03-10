package com.example.chatapp;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
    private SecretKey key;
    private int KEY_SIZE = 256;
    private int T_SIZE = 128;
    private byte[] IV;

    public void init() throws Exception{
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(KEY_SIZE);
        key = generator.generateKey();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String encrypted(String message){
       try {
           byte[] msgByte = message.getBytes();
           Cipher encipher = Cipher.getInstance("AES/GCM/NoPadding");
           encipher.init(Cipher.ENCRYPT_MODE, key);
           IV = encipher.getIV();

           byte[] encryptedByte = encipher.doFinal(msgByte);
           System.out.println(message);
           //return encode(encryptedByte);
          return Base64.getEncoder().encodeToString(encryptedByte);
       }
       catch(Exception e){
           e.printStackTrace();
           return null;
       }
    }
@RequiresApi(api = Build.VERSION_CODES.O)
public String[] encrypt(String message,SecretKey keey){
    try {
        byte[] msgByte = message.getBytes();
        Cipher encipher = Cipher.getInstance("AES/GCM/NoPadding");
        encipher.init(Cipher.ENCRYPT_MODE, keey);
        IV = encipher.getIV();

        byte[] encryptedByte = encipher.doFinal(msgByte);
        System.out.println(message);
        //return encode(encryptedByte);
        String[] str = new String[2];
        str[0] = Base64.getEncoder().encodeToString(encryptedByte);
        str[1] = Base64.getEncoder().encodeToString(IV);
        return str;
    }
    catch(Exception e){
        e.printStackTrace();
        return null;
    }
}
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String decrypt(String encryptedMsg,SecretKey keey, byte[] IVV) {
           try {
               //byte[] msgByte = decode(encryptedMsg);
               byte[] msgByte = Base64.getDecoder().decode(encryptedMsg);
               Cipher decipher = Cipher.getInstance("AES/GCM/NoPadding");
               GCMParameterSpec spec = new GCMParameterSpec(T_SIZE, IVV);
               decipher.init(Cipher.DECRYPT_MODE, keey, spec);
               byte[] decryptedByte = decipher.doFinal(msgByte);

               return new String(decryptedByte);
           }
           catch(Exception e){
               e.printStackTrace();
               return null;
           }
    }
@RequiresApi(api = Build.VERSION_CODES.O)
public String decrypted(String encryptedMsg) {
    try {
        //byte[] msgByte = decode(encryptedMsg);
        byte[] msgByte = Base64.getDecoder().decode(encryptedMsg);
        Cipher decipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(T_SIZE, IV);
        decipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] decryptedByte = decipher.doFinal(msgByte);

        return new String(decryptedByte);
    }
    catch(Exception e){
        e.printStackTrace();
        return null;
    }
}

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void main(String[] args){
//        try {
//            AES aes = new AES();
//            aes.init();
//            String msg = "RIPUNJAY KUMAR";
//            byte[] decodedKey = Base64.getDecoder().decode("+/Nfriy7HGcNm7m7kOi+ELKxQ9UJm7Ruv3ErZWuVWNU=");
//            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
//
//            String encryptt =  aes.encrypt(msg,originalKey);
//            String encrypt = aes.encrypted(msg);
//            byte[] keydata = aes.key.getEncoded();
//            final byte[] originalIV = Base64.getDecoder().decode("YMA2utEB+G1onnQz");
//
//
//            String Skey = new String(Base64.getEncoder().encodeToString(keydata));
//
//            String IVkey = new String(Base64.getEncoder().encodeToString(aes.IV));
//            System.out.println("skey : " + Skey);
//            System.out.println("skeyIV  : " + IVkey);
//
//            System.out.println("enctyptedkey : " );
//            System.out.println("enctypted IV : " + aes.IV);
//
//            System.out.println("encrypted message  : "+encrypt);
//            System.out.println("decrypted message  : " + aes.decrypted(encrypt));
//
//             System.out.println("decrypted message  : " + aes.decrypt("EM0TnaQ++J2EkmlsnlxvYTiNcUJrNlRco2/ETlAw",originalKey,originalIV));
//           System.out.println("working fine");
//
//        }
//        catch(Exception ignored){
//               System.out.println(ignored);
//        }
    }


}
