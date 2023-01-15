package cn.watchdog.accountprotection.utils;

import org.apache.commons.codec.binary.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;

public class GoogleAuthenticator {
    // 生成的key长度( Generate secret key length)
    public static final int SECRET_SIZE = 10;

    public static final String SEED = "g8GjEvTbW5oVSV7avL47357438reyhreyuryetredLDVKs2m0QN7vxRs2im5MDaNCWGmcD2rvcZx";
    // Java实现随机数算法
    public static final String RANDOM_NUMBER_ALGORITHM = "SHA1PRNG";
    // 最多可偏移的时间
    int window_size = 3; // default 3 - max 17

    /**
     * 生成随机密钥。这必须由服务器保存并与用户帐户相关联，以验证Google身份验证器显示的代码。用户必须在其设备上注册此机密。
     *
     * @return 密钥
     */
    public static String generateSecretKey() {
        SecureRandom sr;
        try {
            sr = SecureRandom.getInstance(RANDOM_NUMBER_ALGORITHM);
            sr.setSeed(Base64.decodeBase64(SEED));
            byte[] buffer = sr.generateSeed(SECRET_SIZE);
            Base32 codec = new Base32();
            byte[] bEncodedKey = codec.encode(buffer);
            String encodedKey = new String(bEncodedKey);
            return encodedKey;
        } catch (NoSuchAlgorithmException e) {
            // should never occur... configuration error
        }
        return null;
    }

    /**
     * 返回生成并显示 QR 条形码的 URL。
     * 用户使用智能手机上的Google身份验证器应用程序扫描此条形码以注册身份验证代码。如果需要，他们还可以手动输入密钥
     *
     * @param user   用户 ID（例如弗林斯通）
     * @param host   代码所针对的主机或系统（例如 myapp.com）
     * @param secret 之前为此用户生成的密钥
     * @return 要扫描的二维码的网址
     */
    public static String getQRBarcodeURL(String user, String host, String secret) {
        String format = "http://www.google.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=otpauth://totp/%s@%s?secret=%s";
        return String.format(format, user, host, secret);
    }

    /**
     * 生成一个google身份验证器，识别的字符串，只需要把该方法返回值生成二维码扫描就可以了。
     *
     * @param user   账号
     * @param secret 密钥
     * @return
     */
    public static String getQRBarcode(String user, String secret) {
        String format = "otpauth://totp/%s?secret=%s";
        return String.format(format, user, secret);
    }

    private static int verify_code(byte[] key, long t) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = new byte[8];
        long value = t;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }
        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);
        int offset = hash[20 - 1] & 0xF;
        // 我们之所以使用long，是因为Java还没有无符号的int。
        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            // 我们正在处理有符号字节：
            // 我们只保留第一个字节。
            truncatedHash |= (hash[offset + i] & 0xFF);
        }
        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;
        return (int) truncatedHash;
    }

    /**
     * 设置窗口大小。这是一个整数值，表示我们允许的 30 秒窗口数 窗口越大，我们对时钟偏差的容忍度就越高。
     *
     * @param s 窗口大小 - 必须为 >=1 和 <=17。其他值将被忽略
     */
    public void setWindowSize(int s) {
        if (s >= 1 && s <= 17) window_size = s;
    }

    /**
     * 检查用户输入的代码是否有效
     *
     * @param secret   用户密钥。
     * @param code     用户设备上显示的代码
     * @param timeMsec 以毫秒为单位的时间（例如System.currentTimeMillis（））
     * @return
     */
    public boolean check_code(String secret, long code, long timeMsec) {
        Base32 codec = new Base32();
        byte[] decodedKey = codec.decode(secret);
        // 将 Unix 毫秒时间转换为 30 秒的“窗口”，这是根据 TOTP 规范（有关详细信息，请参阅 RFC）
        long t = (timeMsec / 1000L) / 30L;
        // 窗口用于检查不久前生成的代码。
        // 您可以使用此值来调整您愿意走多远。
        for (int i = -window_size; i <= window_size; ++i) {
            long hash;
            try {
                hash = verify_code(decodedKey, t + i);
            } catch (Exception e) {
                // Yes, this is bad form - but
                // the exceptions thrown would be rare and a static
                // configuration problem
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
                // return false;
            }
            if (hash == code) {
                return true;
            }
        }
        // The validation code is invalid.
        return false;
    }
}
