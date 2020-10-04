package com.hg.kafka.helper.adapter.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoUtils {
    private static final KryoFactory FACTORY = () -> new Kryo();

    private static final KryoPool POOL = new KryoPool.Builder(FACTORY).softReferences().build();

    public static byte[] serialize(Object data) {
        Kryo kryo = null;
        try (Output output = new Output(new ByteArrayOutputStream())) {
            kryo = POOL.borrow();
            kryo.writeClassAndObject(output, data);
            return output.toBytes();
        } finally {
            if (kryo != null) {
                POOL.release(kryo);
            }
        }
    }

    public static Object deserialize(byte[] data) {
        Kryo kryo = null;
        try (Input input = new Input(new ByteArrayInputStream(data))) {
            kryo = POOL.borrow();
            return kryo.readClassAndObject(input);
        } finally {
            if (kryo != null) {
                POOL.release(kryo);
            }
        }
    }
}
