/**
 * Copyright (c) 2008-2012 Bird Dog Games, Inc..
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package com.ardor3d.scenegraph;

import java.io.IOException;
import java.nio.FloatBuffer;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector2;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.Vector4;
import com.ardor3d.util.export.InputCapsule;
import com.ardor3d.util.export.OutputCapsule;
import com.ardor3d.util.export.Savable;
import com.ardor3d.util.geom.BufferUtils;

/**
 * Simple data class storing a buffer of floats and a number that indicates how many floats to group together to make up
 * a "tuple"
 */
public class FloatBufferData extends AbstractBufferData<FloatBuffer> implements Savable {

    /**
     * Instantiates a new FloatBufferData.
     */
    public FloatBufferData() {}

    /**
     * Instantiates a new FloatBufferData with a buffer of the given size.
     */
    public FloatBufferData(final int size, final int valuesPerTuple) {
        this(BufferUtils.createFloatBuffer(size), valuesPerTuple);
    }

    /**
     * Creates a new FloatBufferData.
     *
     * @param buffer
     *            Buffer holding the data. Must not be null.
     * @param valuesPerTuple
     *            Specifies the number of values per tuple. Can not be < 1.
     */
    public FloatBufferData(final FloatBuffer buffer, final int valuesPerTuple) {
        if (buffer == null) {
            throw new IllegalArgumentException("Buffer can not be null!");
        }

        if (valuesPerTuple < 1) {
            throw new IllegalArgumentException("valuesPerTuple must be greater than 1.");
        }

        _buffer = buffer;
        _valuesPerTuple = valuesPerTuple;
    }

    @Override
    public int getByteCount() {
        return 4;
    }

    /**
     * Scale the data in this buffer by the given value(s)
     *
     * @param scales
     *            the scale values to use. The Nth buffer element is scaled by the (N % scales.length) scales element.
     */
    public void scaleData(final float... scales) {
        _buffer.rewind();
        for (int i = 0; i < _buffer.limit();) {
            _buffer.put(_buffer.get(i) * scales[i % scales.length]);
            i++;
        }
        _buffer.rewind();
    }

    /**
     * Translate the data in this buffer by the given value(s)
     *
     * @param translates
     *            the translation values to use. The Nth buffer element is translated by the (N % translates.length)
     *            translates element.
     */
    public void translateData(final float... translates) {
        _buffer.rewind();
        for (int i = 0; i < _buffer.limit();) {
            _buffer.put(_buffer.get(i) + translates[i % translates.length]);
            i++;
        }
        _buffer.rewind();
    }

    public void put(final Vector2 vector) {
        _buffer.put(vector.getXf());
        _buffer.put(vector.getYf());
    }

    public void put(final Vector3 vector) {
        _buffer.put(vector.getXf());
        _buffer.put(vector.getYf());
        _buffer.put(vector.getZf());
    }

    public void put(final Vector4 vector) {
        _buffer.put(vector.getXf());
        _buffer.put(vector.getYf());
        _buffer.put(vector.getZf());
        _buffer.put(vector.getWf());
    }

    public void put(final Quaternion quat) {
        _buffer.put(quat.getXf());
        _buffer.put(quat.getYf());
        _buffer.put(quat.getZf());
        _buffer.put(quat.getWf());
    }

    public void putRGBA(final ColorRGBA color) {
        _buffer.put(color.getRed());
        _buffer.put(color.getGreen());
        _buffer.put(color.getBlue());
        _buffer.put(color.getAlpha());
    }

    public void putRGB(final ColorRGBA color) {
        _buffer.put(color.getRed());
        _buffer.put(color.getGreen());
        _buffer.put(color.getBlue());
    }

    public void putBGRA(final ColorRGBA color) {
        _buffer.put(color.getBlue());
        _buffer.put(color.getGreen());
        _buffer.put(color.getRed());
        _buffer.put(color.getAlpha());
    }

    public void putABGR(final ColorRGBA color) {
        _buffer.put(color.getAlpha());
        _buffer.put(color.getBlue());
        _buffer.put(color.getGreen());
        _buffer.put(color.getRed());
    }

    public void putBGR(final ColorRGBA color) {
        _buffer.put(color.getBlue());
        _buffer.put(color.getGreen());
        _buffer.put(color.getRed());
    }

    @Override
    public FloatBufferData makeCopy() {
        final FloatBufferData copy = new FloatBufferData();
        copy._buffer = BufferUtils.clone(_buffer);
        copy._valuesPerTuple = _valuesPerTuple;
        copy._vboAccessMode = _vboAccessMode;
        return copy;
    }

    public Class<? extends FloatBufferData> getClassTag() {
        return getClass();
    }

    @Override
    public void read(final InputCapsule capsule) throws IOException {
        super.read(capsule);
        _buffer = capsule.readFloatBuffer("buffer", null);
        _valuesPerTuple = capsule.readInt("valuesPerTuple", 0);
    }

    @Override
    public void write(final OutputCapsule capsule) throws IOException {
        super.write(capsule);
        capsule.write(_buffer, "buffer", null);
        capsule.write(_valuesPerTuple, "valuesPerTuple", 0);
    }
}
