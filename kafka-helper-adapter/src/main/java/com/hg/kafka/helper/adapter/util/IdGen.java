package com.hg.kafka.helper.adapter.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import java.util.UUID;

public final class IdGen {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdGen.class);

    private static final short LONG_BYTE_LENTH = 8;

    public static String nextUuid() {
        UUID uuid = UUID.randomUUID();
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        try {
            return StringUtils.newStringUsAscii(Base64
                    .encodeBase64(mergeBytes(longToByteBigEndian(msb), longToByteBigEndian(lsb)), false, true));
        } catch (Exception e) {
            LOGGER.warn("uuid create error", e);
            return null;
        }
    }

    public static byte[] longToByteBigEndian(long l) {
        byte[] bytes = new byte[LONG_BYTE_LENTH];
        for (int i = LONG_BYTE_LENTH - 1; i >= 0; i --) {
            bytes[i] = (byte) l;
            l >>>= Byte.SIZE;
        }
        return bytes;

    }

    public static byte[] mergeBytes(byte[] b1, byte[] b2) {
        byte[] b = new byte[b1.length + b2.length];
        System.arraycopy(b1, 0, b, 0, b1.length);
        System.arraycopy(b2, 0, b, b1.length, b2.length);
        return b;
    }
}
