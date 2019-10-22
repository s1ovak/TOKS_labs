package service;

import jssc.SerialPort;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SerialPortService {
    private SerialPort port;
    private boolean opened;

    public SerialPortService(String name) {
        this.port = new SerialPort(name);
        opened = false;
    }

    public boolean write(byte[] packet) {
        try {
            return port.writeBytes(packet);
        } catch (SerialPortException e) {
            e.printStackTrace();
            return false;
        }
    }

    public byte[] read(int count) {
        try {
            return port.readBytes(count);
        } catch (SerialPortException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean open() {
        try {
            opened = port.openPort();
            return opened;
        } catch (SerialPortException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean close() {
        try {
            if (opened) {
                opened = false;
                return port.closePort();
            }
            return false;
        } catch (SerialPortException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setParams(int dataBits, int speed, int parity, int stopBits) {
        try {
            port.setParams(speed, dataBits, stopBits, parity);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public void addListener(SerialPortEventListener listener) {
        try {
            port.setEventsMask(SerialPort.MASK_RXCHAR);
            port.addEventListener(listener);
        } catch (SerialPortException ex) {
            ex.printStackTrace();
        }
    }

    public void setFlowControl(int mask) {
        try {
            port.setFlowControlMode(mask);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public boolean isOpen() {
        if (port == null) {
            return false;
        }
        return opened;
    }

    public SerialPort getPort() {
        return port;
    }

    public byte[] createPacket(int source, int destination, List<Character> dataBytes, boolean error) {
        String binaryPacket = "00001110";
        binaryPacket = binaryPacket + TranslationNumberUtil.intToBinary(source)
                + TranslationNumberUtil.intToBinary(destination);
        char[] charData = ArrayUtils.toPrimitive(dataBytes.toArray(new Character[dataBytes.size()]));
        binaryPacket = binaryPacket + TranslationNumberUtil.
                bytesToBinary(new String(charData).getBytes());
        String CRC = CRCUtil.divideCRC(binaryPacket + "0000000");
        if (error) {
            Random random = new Random();
            int index = 24 + random.nextInt(54);
            System.out.println(index);
            binaryPacket = makeError(binaryPacket, index);
        }
        binaryPacket = binaryPacket + CRC + "0";
        return TranslationNumberUtil.binaryToBytes(stuff(binaryPacket));
    }

    public byte[] getDataFromPacket(byte[] packet) {
        byte[] result = new byte[7];
        for (int i = 3; i < 10; i++) {
            result[i - 3] = packet[i];
        }
        return result;
    }

    public byte getSourceFromPacket(byte[] packet) {
        return packet[2];
    }

    public byte getDestinationFromPacket(byte[] packet) {
        return packet[1];
    }

    public byte getCRCFromPacket(byte[] packet) {
        return packet[10];
    }

    public String stuff(String packet) {
        String result = packet.substring(8);
        result = result.replaceAll("0000111", "00001111");
        return "00001110" + result;
    }

    public byte[] unstuff(byte[] packet) {
        String binaryPacket = TranslationNumberUtil.bytesToBinary(packet);
        binaryPacket = binaryPacket.substring(8);
        binaryPacket = binaryPacket.replaceAll("00001111", "0000111");
        return TranslationNumberUtil.binaryToBytes("00001110" + binaryPacket);
    }

    private String makeError(String binary, int index) {
        String buf = binary.substring(0, index);
        char a = binary.charAt(index);
        binary = binary.substring(index + 1);
        return a == '0' ? (buf + "1" + binary) : (buf + "0" + binary);
    }
}
