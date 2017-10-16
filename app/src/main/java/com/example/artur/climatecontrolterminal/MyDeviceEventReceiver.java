package com.example.artur.climatecontrolterminal;

public interface MyDeviceEventReceiver {
    void SetConnectionStatus( ConnectionStatus status );

    void SetConnectionStatus(ConnectionStatus status, int tryRemains);

    void CommandComplete(DeviceCommand command );
}

