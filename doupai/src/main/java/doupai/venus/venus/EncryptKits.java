package doupai.venus.venus;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author lmx
 * Created by lmx on 2018/11/6.
 */
public class EncryptKits
{


    public static synchronized boolean cryptFile(@NonNull char[] chars, @NonNull java.io.File file, boolean r20)
    {
        try
        {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rws");
            randomAccessFile.seek(0);
            byte[] bytes = new byte[100];
            int read_index = randomAccessFile.read(bytes);

            int char_length = chars.length;
            int char_length_1 = char_length + 1;
            int char_length_2 = char_length_1 + 2;
            int char_length_3 = char_length_1 + 3;
            if (char_length <= 5)
            {

            }

            byte[] decrypt_bytes;
            String decrypt_key;
            // decrypt_bytes = Arrays.copyOfRange();

            // byte[] bytes1 = decryptAES(decrypt_bytes, decrypt_key);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return true;
    }

    public static byte[] decryptAES(byte[] bArr, String str)
    {
        try
        {
            byte[] bytes = MD5(str, false).substring(0, 16).getBytes();
            Key secretKeySpec = new SecretKeySpec(bytes, "AES");
            Cipher instance = Cipher.getInstance("AES/CBC/PKCS5Padding");
            instance.init(2, secretKeySpec, new IvParameterSpec(bytes));
            return instance.doFinal(bArr);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static String MD5(String str, Boolean bool)
    {
        return MD5(str.getBytes(), bool);
    }

    public static String MD5(byte[] bArr, Boolean bool)
    {
        String str = "";
        try
        {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(bArr);
            return parseByte2HexStr(instance.digest(), bool);
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            return str;
        }
    }

    public static String parseByte2HexStr(byte[] bArr, boolean bool)
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bArr)
        {
            String toHexString = Integer.toHexString(b & 255);
            if (toHexString.length() == 1)
            {
                toHexString = "0" + toHexString;
            }
            if (bool)
            {
                toHexString = toHexString.toUpperCase();
            }
            stringBuilder.append(toHexString);
        }
        return stringBuilder.toString();
    }

    public static byte[] parseHexStr2Byte(String str)
    {
        byte[] bArr = new byte[(str.length() / 2)];
        if (str.length() < 1)
        {
            return null;
        }
        for (int i = 0; i < str.length() / 2; i++)
        {
            int i2 = i * 2;
            int i3 = i2 + 1;
            bArr[i] = (byte) (Integer.parseInt(str.substring(i3, i2 + 2), 16) + (Integer.parseInt(str.substring(i2, i3), 16) * 16));
        }
        return bArr;
    }

    public static String encrypt(String seed, String cleartext) throws Exception
    {
        byte[] rawKey = deriveKeyInsecurely(seed, 32).getEncoded();
        byte[] result = encrypt(rawKey, cleartext.getBytes());
        return toHex(result);
    }

    public static String decrypt(String seed, String encrypted) throws Exception
    {
        byte[] rawKey = deriveKeyInsecurely(seed, 32).getEncoded();
        byte[] enc = toByte(encrypted);
        byte[] result = decrypt(rawKey, enc);
        return new String(result);
    }

    private static SecretKey deriveKeyInsecurely(String password, int keySizeInBytes)
    {
        byte[] passwordBytes = password.getBytes(StandardCharsets.US_ASCII);
        return new SecretKeySpec(InsecureSHA1PRNGKeyDerivator.deriveInsecureKey(passwordBytes, keySizeInBytes), "AES");
    }

    public static byte[] encrypt(byte[] raw, byte[] clear) throws Exception
    {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    public static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception
    {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    private static String toHex(String txt)
    {
        return toHex(txt.getBytes());
    }

    private static String fromHex(String hex)
    {
        return new String(toByte(hex));
    }

    private static byte[] toByte(String hexString)
    {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        return result;
    }

    private static String toHex(byte[] buf)
    {
        if (buf == null) return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++)
        {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private final static String HEX = "0123456789ABCDEF";

    private static void appendHex(StringBuffer sb, byte b)
    {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

    /**
     * 亦或加解密，适合对整个文件的部分加密，比如文件头部，和尾部
     * 对file文件头部和尾部加密，适合zip压缩包加密
     *
     * @param source 需要加密的文件
     * @param det    加密后保存文件名
     * @param key    加密key
     */
    public static void encryptionFile(File source, File det, int key)
    {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try
        {
            fis = new FileInputStream(source);
            fos = new FileOutputStream(det);
            int size = 2048;
            byte buff[] = new byte[size];
            int count = fis.read(buff);
            /**zip包头部加密*/
            for (int i = 0; i < count; i++)
            {
                fos.write(buff[i] ^ key);
            }
            while (true)
            {
                count = fis.read(buff);
                /**zip包结尾加密*/
                if (count < size)
                {
                    for (int j = 0; j < count; j++)
                    {
                        fos.write(buff[j] ^ key);
                    }
                    break;
                }
                fos.write(buff, 0, count);
            }
            fos.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (fis != null)
                {
                    fis.close();
                }
                if (fos != null)
                {
                    fos.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
    }

    /**
     * 亦或加解密，适合对整个文件加密
     *
     * @param source 需要加密文件的路径
     * @param det    加密后保存文件的路径
     * @param key    加密秘钥key
     */
    private static void encryptionFile(String source, String det, int key)
    {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try
        {
            fis = new FileInputStream(source);
            fos = new FileOutputStream(det);
            int read;
            while ((read = fis.read()) != -1)
            {
                fos.write(read ^ key);
            }
            fos.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (fis != null)
                {
                    fis.close();
                }
                if (fos != null)
                {
                    fos.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
    }
}
