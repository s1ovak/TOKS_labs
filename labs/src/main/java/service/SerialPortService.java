package service;

import jssc.SerialPort;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialPortService {
    private SerialPort port;
    private boolean opened;

    public SerialPortService(String name) {
        this.port = new SerialPort(name);
        opened = false;
    }

    public boolean write(byte symbol) {
        try {
            return port.writeByte(symbol);
        } catch (SerialPortException e) {
            e.printStackTrace();
            return false;
        }
    }

    public byte[] read() {
        try {
            return port.readBytes(1);
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
        }
        catch (SerialPortException ex) {
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
        return opened;
    }

    public SerialPort getPort() {
        return port;
    }
}
