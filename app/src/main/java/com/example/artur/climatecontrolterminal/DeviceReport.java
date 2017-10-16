package com.example.artur.climatecontrolterminal;

public class DeviceReport extends DeviceCommand {
    public DeviceReport() {
    }

    public double GetTemp() {
        return _temp;
    }

    public double GetPassengerTemp() {
        return _passengerTemp;
    }

    public double GetOutsideTemp() {
        return _outsideTemp;
    }

    public boolean GetBlowModeFace() {
        return _blowModeFace;
    }

    public boolean GetBlowModeBottom() {
        return _blowModeBottom;
    }

    public boolean GetBlowModeFront() {
        return _blowModeFront;
    }

    public int GetFanSpeed() {
        return _fanSpeed;
    }

    public boolean GetAC() {
        return _ac;
    }

    public boolean GetDual() {
        return _dual;
    }

    public boolean GetRear() {
        return _rear;
    }

    public boolean GetAuto() {
        return _auto;
    }

    public AirCirculation GetAirCirculation() {
        return _airCirculation;
    }

    public void Decode() {
        if ( _decoded ) return;
        byte ledByte = Bytes[0];

        int a, b, c;

        a = Digit7Pattern.DecodeDigit(Digit7Pattern.BitTransposition(Bytes, TempABitOrder));
        b = Digit7Pattern.DecodeDigit(Digit7Pattern.BitTransposition(Bytes, TempBBitOrder));
        c = Digit7Pattern.DecodeDigit(Digit7Pattern.BitTransposition(Bytes, TempCBitOrder));
        _temp = a == -1 || b == -1 || c == -1 ? Double.NaN : 10 * a + b + c * 0.1;

        a = Digit7Pattern.DecodeDigit(Digit7Pattern.BitTransposition(Bytes, PassengerTempABitOrder));
        b = Digit7Pattern.DecodeDigit(Digit7Pattern.BitTransposition(Bytes, PassengerTempBBitOrder));
        c = Digit7Pattern.DecodeDigit(Digit7Pattern.BitTransposition(Bytes, PassengerTempCBitOrder));
        _passengerTemp = a == -1 || b == -1 || c == -1 ? Double.NaN : 10 * a + b + c * 0.1;

        a = Digit7Pattern.DecodeDigit(Digit7Pattern.BitTransposition(Bytes, OutsideTempABitOrder));
        b = Digit7Pattern.DecodeDigit(Digit7Pattern.BitTransposition(Bytes, OutsideTempBBitOrder));
        c = LCDV(0, 17) ? 1 : -1;
        _outsideTemp = a == -1 || b == -1 ? Double.NaN : c * (10 * a + b);

        _blowModeFace = LCDV(1, 17);
        _blowModeBottom = LCDV(2, 18);
        _blowModeFront = LCDV(1, 18) || (ledByte & 0x01) == 0x01;

        if (((ledByte >> 4) & 0x01) == 0x01)
            _airCirculation = AirCirculation.Fresh;
        else
            _airCirculation = ((ledByte >> 1) & 0x01) == 0x01 ? AirCirculation.Recirculation : AirCirculation.None;
        _auto = ((ledByte >> 2) & 0x01) == 0x01;
        _ac = ((ledByte >> 3) & 0x01) == 0x01;
        _dual = ((ledByte >> 6) & 0x01) == 0x01;
        _rear = ((ledByte >> 7) & 0x01) == 0x01;

        _fanSpeed = 0;
        if (LCDV(0, 11)) _fanSpeed++;
        if (LCDV(1, 11)) _fanSpeed++;
        if (LCDV(1, 10)) _fanSpeed++;
        if (LCDV(2, 10)) _fanSpeed++;
        if (LCDV(0, 10)) _fanSpeed++;

        _decoded = true;
    }

    public boolean LCDV(int com, int line) {
        int bitIndex, byteIndex;
        if (line == 1) {
            byteIndex = 1;
            bitIndex = com;
        } else {
            byteIndex = 2 + com * 3 + (line - 2) / 8;
            bitIndex = 7 - (line - 2) % 8;
        }
        return ((Bytes[byteIndex] >> bitIndex) & 0x01) == 0x01;
    }

    private double _temp;
    private double _passengerTemp;
    private double _outsideTemp;
    private boolean _blowModeFace;
    private boolean _blowModeBottom;
    private boolean _blowModeFront;
    private int _fanSpeed;
    private boolean _ac;
    private boolean _dual;
    private boolean _rear;
    private boolean _auto;
    private AirCirculation _airCirculation;
    private boolean _decoded = false;

    private static final int[] TempCBitOrder = {
            LCDI(2, 1), LCDI(0, 2), LCDI(2, 3), LCDI(1, 2), LCDI(1, 1), LCDI(2, 2), LCDI(1, 3)};

    private static final int[] TempBBitOrder = {
            LCDI(2, 4), LCDI(0, 5), LCDI(2, 6), LCDI(1, 5), LCDI(1, 4), LCDI(2, 5), LCDI(1, 6)};

    private static final int[] TempABitOrder = {
            LCDI(2, 7), LCDI(0, 8), LCDI(2, 8), LCDI(1, 8), LCDI(1, 7), LCDI(2, 8), LCDI(1, 9)};

    private static final int[] PassengerTempCBitOrder = {
            LCDI(2, 19), LCDI(0, 19), LCDI(0, 20), LCDI(1, 20), LCDI(1, 19), LCDI(2, 20), LCDI(1, 21)};

    private static final int[] PassengerTempBBitOrder = {
            LCDI(0, 21), LCDI(0, 22), LCDI(0, 23), LCDI(1, 22), LCDI(2, 21), LCDI(2, 22), LCDI(2, 23)};

    private static final int[] PassengerTempABitOrder = {
            LCDI(0, 24), LCDI(0, 25), LCDI(2, 25), LCDI(1, 24), LCDI(1, 23), LCDI(2, 24), LCDI(1, 25)};

    private static final int[] OutsideTempBBitOrder = {
            LCDI(2, 12), LCDI(0, 13), LCDI(0, 14), LCDI(1, 13), LCDI(1, 12), LCDI(2, 13), LCDI(2, 14)};

    private static final int[] OutsideTempABitOrder = {
            LCDI(0, 15), LCDI(0, 16), LCDI(2, 16), LCDI(1, 15), LCDI(1, 14), LCDI(2, 15), LCDI(1, 16)};

    private static int LCDI(int com, int line) {
        if (line == 1)
            return 8 + com;
        return 8 * (2 + com * 3 + (line - 2) / 8) + (line - 2) % 8;
    }
}
