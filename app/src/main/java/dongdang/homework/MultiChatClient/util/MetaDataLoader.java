package dongdang.homework.MultiChatClient.util;

import android.content.res.AssetManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class MetaDataLoader {
    private static Properties metaDataProfile = new Properties();

    public static void loading(AssetManager am) {
        try {
            metaDataProfile.load(am.open("meta_data.properties"));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            System.err.println("메타 데이터 파일이 존재하지 않습니다.");
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String getServerAdr() {
        return metaDataProfile.getProperty("serverAdr");
    }

    public static int getPort() {
        return Integer.parseInt(metaDataProfile.getProperty("port"));
    }
    public static int getFilePort() {
        return Integer.parseInt(metaDataProfile.getProperty("filePort"));
    }
}
