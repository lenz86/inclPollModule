package classes;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.util.ArrayList;

public class PackageHandler {
    //извлекает из пакета набор байт с версией протокола датчика и конвертирует в строку
    public static String pullVersion(String data) {
        String version = "";
        if (data.startsWith("7E") && data.endsWith("7E")) {
            String[] bytes = data.split(" ");
            StringBuilder tmp = new StringBuilder("");
            for (int i = 4; i < bytes.length - 2; i++) {
                tmp.append((char) fromHex(bytes[i]));        //конвертирует 1 байт hex-формата String типа в ASCII-символ
            }
            version = tmp.toString();
        }
        return version;
    }

    //извлекает из пакета набор байт с заводским номером датчика и конвертирует в строку
    public static String pullFactoryId(String data) {
        String factoryId = "";
        if (data.startsWith("7E") && data.endsWith("7E")) {
            String[] bytes = data.split(" ");
            StringBuilder tmp = new StringBuilder("");
            StringBuilder tmp2 = new StringBuilder("");
            StringBuilder tmp3 = new StringBuilder("");
            for (int i = bytes.length - 3; i > 3; i--) {
                //конвертируем 1 байт пакета в бинарное значение
                byte t = fromHex(bytes[i]);
                tmp2.append(Integer.toBinaryString(Byte.toUnsignedInt(t)));
                for (int k = tmp2.length(); k < 8; k++) {
                    tmp3.append("0");
                }
                tmp3.append(tmp2);
                tmp.append(tmp3);
                tmp2.setLength(0);
                tmp3.setLength(0);
            }
            int tmp4 = Integer.parseInt(tmp.toString(), 2);
            factoryId = Integer.toString(tmp4);
        }
        return factoryId;
    }

    public static String[] pullValues(String data) {
        String[] values = new String[2];
        if (data.startsWith("7E") && data.endsWith("7E")) {
            String[] bytes = data.split(" ");
            StringBuilder tmp = new StringBuilder("");
            StringBuilder axisX = new StringBuilder("");
            StringBuilder axisY = new StringBuilder("");
            ArrayList<String> binaryBytes = new ArrayList<>();
            for (int i = bytes.length - 3; i > 3; i--) {
                //конвертируем 1 байт пакета в бинарное значение
                byte t = fromHex(bytes[i]);
                String convertByte = Integer.toBinaryString(Byte.toUnsignedInt(t));
                if (convertByte.length() < 8) {
                    for (int j = 0; j < 8 - convertByte.length(); j++) {
                        tmp.append(0);
                    }
                    tmp.append(convertByte);
                    binaryBytes.add(tmp.toString());
                } else {
                    binaryBytes.add(convertByte);
                }
                tmp.setLength(0);
            }
            if (binaryBytes.get(0).substring(0, 1).equals("1")) {
                axisY.append("-");
            }
            if (binaryBytes.get(3).substring(0, 1).equals("1")) {
                axisX.append("-");
            }
            tmp.append(binaryBytes.get(0).substring(2, 8));
            tmp.append(binaryBytes.get(1));
            axisX.append(Integer.parseInt(tmp.toString(), 2));
            tmp.setLength(0);
            tmp.append(binaryBytes.get(3).substring(2, 8));
            tmp.append(binaryBytes.get(4));
            axisY.append(Integer.parseInt(tmp.toString(), 2));
            values[0] = axisX.toString();
            values[1] = axisY.toString();
        }
        return values;
    }

    //конвертирует 1 байт hex-формата String типа в byte
    private static byte fromHex(String s) {
        byte hexByte = 0;
        try {
            byte[] h1 = Hex.decodeHex(s);
            hexByte = h1[0];
        } catch (DecoderException e) {
            e.printStackTrace();
        }
        return hexByte;
    }


    //преобразуем каждый байт из бинарной строки в int-значение
    public static Integer binaryToInt(String data) {
        Integer t = Integer.parseInt(data, 2);
        return t;
    }
}
