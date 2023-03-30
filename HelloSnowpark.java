import com.snowflake.snowpark_java.*;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;

import java.io.FileReader;
import java.io.StringWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.PrivateKey;
import javax.crypto.SecretKey;
import java.security.Security;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.Base64;

public class HelloSnowpark {

    // Path to the private key file that you generated earlier.
    private static final String PRIVATE_KEY_FILE = "/Users/dong/keys/rsa_key.p8";

    public static class PrivateKeyReader
    {

        // If you generated an encrypted private key, implement this method to return
        // the passphrase for decrypting your private key.
        private static String getPrivateKeyPassphrase() {
            return "snowflake";
        }

        public static PrivateKey get(String filename)
                throws Exception
        {
            PrivateKeyInfo privateKeyInfo = null;
            Security.addProvider(new BouncyCastleProvider());
            // Read an object from the private key file.
            PEMParser pemParser = new PEMParser(new FileReader(Paths.get(filename).toFile()));
            Object pemObject = pemParser.readObject();
            if (pemObject instanceof PKCS8EncryptedPrivateKeyInfo) {
                // Handle the case where the private key is encrypted.
                PKCS8EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = (PKCS8EncryptedPrivateKeyInfo) pemObject;
                String passphrase = getPrivateKeyPassphrase();
                InputDecryptorProvider pkcs8Prov = new JceOpenSSLPKCS8DecryptorProviderBuilder().build(passphrase.toCharArray());
                privateKeyInfo = encryptedPrivateKeyInfo.decryptPrivateKeyInfo(pkcs8Prov);
            } else if (pemObject instanceof PrivateKeyInfo) {
                // Handle the case where the private key is unencrypted.
                privateKeyInfo = (PrivateKeyInfo) pemObject;
            }
            pemParser.close();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME);
            return converter.getPrivateKey(privateKeyInfo);
        }
    }

    public static String convertKeyToString(PrivateKey privateKey) throws Exception {
        StringWriter sw = new StringWriter();
        JcaPEMWriter writer = new JcaPEMWriter(sw);
        writer.writeObject(privateKey);
        writer.close();
        return sw.getBuffer().toString().replace("-----BEGIN RSA PRIVATE KEY-----","").replace("-----END RSA PRIVATE KEY-----","");
    }

    public static void main(String[] args) throws Exception{

        // Replace the <placeholders> below.
        Map<String, String> properties = new HashMap<>();
        //String url = "jdbc:snowflake://ws90724.ap-southeast-1.snowflakecomputing.com";
        //Properties properties = new Properties();
        properties.put("URL", "https://ws90724.ap-southeast-1.snowflakecomputing.com:443");
        properties.put("USER", "admin");
        properties.put("privateKey", convertKeyToString(PrivateKeyReader.get(PRIVATE_KEY_FILE)));
        properties.put("ROLE", "SYSADMIN");
        properties.put("WAREHOUSE", "COMPUTE_WH");
        properties.put("DB", "de_demo");
        properties.put("SCHEMA", "curated");
        Session session = Session.builder().configs(properties).create();
        //Connection conn = DriverManager.getConnection(url, properties);
        session.sql("show tables").show();

    }
}