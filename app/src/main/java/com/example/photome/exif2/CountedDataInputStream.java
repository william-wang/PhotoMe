/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.photome.exif2;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

class CountedDataInputStream extends FilterInputStream {

    // allocate a byte buffer for a long value;
    private final byte mByteArray[] = new byte[8];
    private final ByteBuffer mByteBuffer = ByteBuffer.wrap(mByteArray);
    private int mCount = 0;
    private int mEnd = 0;

    protected CountedDataInputStream(InputStream in) {
        super(in);
    }

    public void setEnd(int end) {
        mEnd = end;
    }

    public int getEnd() {
        return mEnd;
    }

    public int getReadByteCount() {
        return mCount;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int r = in.read(b);
        mCount += (r >= 0) ? r : 0;
        return r;
    }

    @Override
    public int read() throws IOException {
        int r = in.read();
        mCount += (r >= 0) ? 1 : 0;
        return r;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int r = in.read(b, off, len);
        mCount += (r >= 0) ? r : 0;
        return r;
    }

    @Override
    public long skip(long length) throws IOException {
        long skip = in.skip(length);
        mCount += skip;
        return skip;
    }

    public void skipTo(long target) throws IOException {
        long cur = mCount;
        long diff = target - cur;
        assert (diff >= 0);
        skipOrThrow(diff);
    }

    public void skipOrThrow(long length) throws IOException {
        if (skip(length) != length) throw new EOFException();
    }

    public ByteOrder getByteOrder() {
        return mByteBuffer.order();
    }

    public void setByteOrder(ByteOrder order) {
        mByteBuffer.order(order);
    }

    public int readUnsignedShort() throws IOException {
        return readShort() & 0xffff;
    }

    public short readShort() throws IOException {
        readOrThrow(mByteArray, 0, 2);
        mByteBuffer.rewind();
        return mByteBuffer.getShort();
    }

    public byte readByte() throws IOException {
        readOrThrow(mByteArray, 0, 1);
        mByteBuffer.rewind();
        return mByteBuffer.get();
    }

    public int readUnsignedByte() throws IOException {
        readOrThrow(mByteArray, 0, 1);
        mByteBuffer.rewind();
        return (mByteBuffer.get() & 0xff);
    }

    public void readOrThrow(byte[] b, int off, int len) throws IOException {
        int r = read(b, off, len);
        if (r != len) throw new EOFException();
    }

    public long readUnsignedInt() throws IOException {
        return readInt() & 0xffffffffL;
    }

    public int readInt() throws IOException {
        readOrThrow(mByteArray, 0, 4);
        mByteBuffer.rewind();
        return mByteBuffer.getInt();
    }

    public long readLong() throws IOException {
        readOrThrow(mByteArray, 0, 8);
        mByteBuffer.rewind();
        return mByteBuffer.getLong();
    }

    public String readString(int n) throws IOException {
        byte buf[] = new byte[n];
        readOrThrow(buf);
        return new String(buf, "UTF8");
    }

    public void readOrThrow(byte[] b) throws IOException {
        readOrThrow(b, 0, b.length);
    }

    public String readString(int n, Charset charset) throws IOException {
        byte buf[] = new byte[n];
        readOrThrow(buf);
        return new String(buf, charset);
    }
}