package com.example.artur.climatecontrolterminal;

public class DeviceCommand{
    public int TryCountRemains;

    public DeviceCommand(CommandType command) {
        Init();
        Bytes[ 0 ] = CommandType.GetByte(command);
    }

    public DeviceCommand(CommandType command, byte param) {
        Init();
        Bytes[ 0 ] = CommandType.GetByte(command);
        Bytes[ 1 ] = param;
    }

    public DeviceCommand(CommandType command, short param) {
        Init();
        Bytes[0] = CommandType.GetByte(command);
        Bytes[1] = (byte) (param & 0xff);
        Bytes[2] = (byte) ((param >> 8) & 0xff);
    }

    public byte[] GetBytes() {
        return Bytes;
    }

    public void ResetTryCount()
    {
        TryCountRemains = 5;
    }

    protected byte[] Bytes;

    protected DeviceCommand(){
        Init();
    }

    private void Init(){
        ResetTryCount();
        Bytes = new byte[MyDevice.MESSAGE_SIZE];
    }
}
