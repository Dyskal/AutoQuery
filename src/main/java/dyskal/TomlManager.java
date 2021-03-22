package dyskal;

import com.electronwill.nightconfig.core.file.FileConfig;
import net.harawata.appdirs.AppDirsFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TomlManager {
    private final File dir = new File(AppDirsFactory.getInstance().getUserConfigDir("AutoQuery", null, "Dyskal", true));
    private final File file = new File(dir + "/settings.toml");
    private final FileConfig config = FileConfig.of(file);
    private String dbname;
    private String path;
    private String username;
    private String password;
    private boolean recreate = false;

    public TomlManager() {
        try {
            makeFile();
        } catch (Exception e) {
            e.printStackTrace();
            recreate = true;
            try {
                makeFile();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void makeFile() throws IOException {
        if (dir.exists() && file.exists() && !recreate) {
            BufferedReader br = new BufferedReader(new FileReader(file));
            if (br.readLine() != null) {
                config.load();
                dbname = config.get("dbname");
                path = config.get("path");
                username = config.get("username");
                password = config.get("password");
                return;
            }
        }
        dir.mkdirs();
        file.createNewFile();
        config.set("dbname", "placeholder");
        config.set("path", "placeholder");
        config.set("username", "placeholder");
        config.set("password", "placeholder");
        config.save();

        config.load();
        dbname = config.get("dbname");
        path = config.get("path");
        username = config.get("username");
        password = config.get("password");
    }

    public void writer(String path, String var) {
        config.remove(path);
        config.set(path, var);
        config.save();
    }

    public String getDbname() {
        return this.dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
        writer("dbname", this.dbname);
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
        writer("path", this.path);
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
        writer("username", this.username);
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
        writer("password", this.password);
    }
}
