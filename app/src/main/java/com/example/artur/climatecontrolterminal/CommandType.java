package com.example.artur.climatecontrolterminal;

public enum CommandType {
    PushButton,
    StartReadLCD,
    SetPWM,
    PulsePWM,
    SetReadLCDInterval;

    public static byte GetByte(CommandType ct) {
        switch (ct) {
            case PushButton:
                return 1;
            case StartReadLCD:
                return 2;
            case SetPWM:
                return 4;
            case PulsePWM:
                return 5;
            case SetReadLCDInterval:
                return 6;
        }
        return -1;
    }
}
