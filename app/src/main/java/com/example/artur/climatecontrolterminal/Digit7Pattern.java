package com.example.artur.climatecontrolterminal;

/*
* Нумерация бит в 7-сегментном цифро-месте:
*
*  *4*
*  7 5
*  *6*
*  3 1
*  *2*
*/
public class Digit7Pattern{
    @SuppressWarnings("PointlessBitwiseExpression")
    private static final int[] _pattern = {
            ( 1 << 1 ) | ( 1 << 2 ) | ( 1 << 3 ) | ( 1 << 4 ) | ( 1 << 5 ) | ( 0 << 6 ) | ( 1 << 7 ), // 0
            ( 1 << 1 ) | ( 0 << 2 ) | ( 0 << 3 ) | ( 0 << 4 ) | ( 1 << 5 ) | ( 0 << 6 ) | ( 0 << 7 ), // 1
            ( 0 << 1 ) | ( 1 << 2 ) | ( 1 << 3 ) | ( 1 << 4 ) | ( 1 << 5 ) | ( 1 << 6 ) | ( 0 << 7 ), // 2
            ( 1 << 1 ) | ( 1 << 2 ) | ( 0 << 3 ) | ( 1 << 4 ) | ( 1 << 5 ) | ( 1 << 6 ) | ( 0 << 7 ), // 3
            ( 1 << 1 ) | ( 0 << 2 ) | ( 0 << 3 ) | ( 0 << 4 ) | ( 1 << 5 ) | ( 1 << 6 ) | ( 1 << 7 ), // 4
            ( 1 << 1 ) | ( 1 << 2 ) | ( 0 << 3 ) | ( 1 << 4 ) | ( 0 << 5 ) | ( 1 << 6 ) | ( 1 << 7 ), // 5
            ( 1 << 1 ) | ( 1 << 2 ) | ( 1 << 3 ) | ( 1 << 4 ) | ( 0 << 5 ) | ( 1 << 6 ) | ( 1 << 7 ), // 6
            ( 1 << 1 ) | ( 0 << 2 ) | ( 0 << 3 ) | ( 1 << 4 ) | ( 1 << 5 ) | ( 0 << 6 ) | ( 0 << 7 ), // 7
            ( 1 << 1 ) | ( 1 << 2 ) | ( 1 << 3 ) | ( 1 << 4 ) | ( 1 << 5 ) | ( 1 << 6 ) | ( 1 << 7 ), // 8
            ( 1 << 1 ) | ( 1 << 2 ) | ( 0 << 3 ) | ( 1 << 4 ) | ( 1 << 5 ) | ( 1 << 6 ) | ( 1 << 7 ), // 9
    };

    static int DecodeDigit(byte codedValue)
    {
        for (int i = 0; i < _pattern.length; i++) {
            if ( _pattern[ i ] == codedValue ) return i;
        }
        return -1;
    }

    static byte BitTransposition(byte[] source, int[] order) {
        byte result = 0;
        for (int i:order) {
            result = (byte) (result >> 1);
            int nByte = i / 8;
            int nBit = i % 8;
            result |= ( source[ nByte ] << ( 7 - nBit ) ) & 0x80;
        }
        return result;
    }
}
