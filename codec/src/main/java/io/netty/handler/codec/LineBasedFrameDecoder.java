/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ByteProcessor;

import java.util.List;

/**
 * A decoder that splits the received {@link ByteBuf}s on line endings.
 * <p>
 * Both {@code "\n"} and {@code "\r\n"} are handled.
 * <p>
 * The byte stream is expected to be in UTF-8 character encoding or ASCII. The current implementation
 * uses direct {@code byte} to {@code char} cast and then compares that {@code char} to a few low range
 * ASCII characters like {@code '\n'} or {@code '\r'}. UTF-8 is not using low range [0..0x7F]
 * byte values for multibyte codepoint representations therefore fully supported by this implementation.
 * <p>
 * For a more general delimiter-based decoder, see {@link DelimiterBasedFrameDecoder}.
 */
public class LineBasedFrameDecoder extends ByteToMessageDecoder {

    /** 解码的最大长度.  */
    private final int maxLength;
    /** 是否在超过maxLength时立即抛出异常。 */
    private final boolean failFast;
    /**是否解析换行符(\n,\r\n)*/
    private final boolean stripDelimiter;

    /** 如果因为已经超过maxLength的长度，而丢弃数据，则为True。  */
    private boolean discarding;
    /**已经丢弃多少字节*/
    private int discardedBytes;

    /**最后一次扫描的位置 */
    private int offset;

    /**
     * Creates a new decoder.
     * @param maxLength  the maximum length of the decoded frame.
     *                   A {@link TooLongFrameException} is thrown if
     *                   the length of the frame exceeds this value.
     */
    public LineBasedFrameDecoder(final int maxLength) {
        this(maxLength, true, false);
    }

    /**
     * Creates a new decoder.
     * @param maxLength  the maximum length of the decoded frame.
     *                   A {@link TooLongFrameException} is thrown if
     *                   the length of the frame exceeds this value.
     * @param stripDelimiter  whether the decoded frame should strip out the
     *                        delimiter or not
     * @param failFast  If <tt>true</tt>, a {@link TooLongFrameException} is
     *                  thrown as soon as the decoder notices the length of the
     *                  frame will exceed <tt>maxFrameLength</tt> regardless of
     *                  whether the entire frame has been read.
     *                  If <tt>false</tt>, a {@link TooLongFrameException} is
     *                  thrown after the entire frame that exceeds
     *                  <tt>maxFrameLength</tt> has been read.
     */
    public LineBasedFrameDecoder(final int maxLength, final boolean stripDelimiter, final boolean failFast) {
        this.maxLength = maxLength;
        this.failFast = failFast;
        this.stripDelimiter = stripDelimiter;
    }

    @Override
    protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Object decoded = decode(ctx, in);
        if (decoded != null) {
            out.add(decoded);
        }
    }

    /**
     * Create a frame out of the {@link ByteBuf} and return it.
     *
     * @param   ctx             the {@link ChannelHandlerContext} which this {@link ByteToMessageDecoder} belongs to
     * @param   buffer          the {@link ByteBuf} from which to read data
     * @return  frame           the {@link ByteBuf} which represent the frame or {@code null} if no frame could
     *                          be created.
     */
    protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        //返回\r\n的下标位置
        final int eol = findEndOfLine(buffer);
        if (!discarding) {
            if (eol >= 0) {
                final ByteBuf frame;
                //算出本次要截取数据的长度
                final int length = eol - buffer.readerIndex();
                //判断\n前面是否是\r  是返回2  不是返回1
                final int delimLength = buffer.getByte(eol) == '\r'? 2 : 1;

                //如果读取的长度>指定的最大长度
                if (length > maxLength) {
                    //设置此缓冲区的readerIndex。  跳过这段数据
                    buffer.readerIndex(eol + delimLength);
                    fail(ctx, length);
                    return null;
                }

                //判断解析的数据是否要带\r\n
                if (stripDelimiter) {
                    //返回从当前readerIndex开始的缓冲区子区域的一个新保留的片
                    //返回到\r\n的有效数据 不包括\r\n
                    frame = buffer.readRetainedSlice(length);
                    //跳过\r\n
                    buffer.skipBytes(delimLength);
                } else {
                    //截取到\r\n的有效数据 包括\r\n
                    frame = buffer.readRetainedSlice(length + delimLength);
                }
                return frame;
            } else {
                //如果没有找到\r\n
                final int length = buffer.readableBytes();
                //如果本次数据的可读长度》最大可读长度
                if (length > maxLength) {
                    //设置丢弃的长度为本次buffer的可读取长度
                    discardedBytes = length;
                    //跳过本次数据
                    buffer.readerIndex(buffer.writerIndex());
                    //设置为丢弃模式
                    discarding = true;
                    offset = 0;
                    if (failFast) {
                        fail(ctx, "over " + discardedBytes);
                    }
                }
                return null;
            }
        } else {
            //找到了\r\n
            if (eol >= 0) {
                //以前丢弃的数据长度+本次可读的数据长度
                final int length = discardedBytes + eol - buffer.readerIndex();
                //拿到分隔符的长度
                final int delimLength = buffer.getByte(eol) == '\r' ? 2 : 1;
                //跳过（丢弃）本次数据
                buffer.readerIndex(eol + delimLength);
                //设置丢弃数据长度为0
                discardedBytes = 0;
                //设置非丢弃模式
                discarding = false;
                if (!failFast) {
                    fail(ctx, length);
                }
            } else {
                //没找到\r\n
                //丢弃的数据长度+本次可读数据的长度
                discardedBytes += buffer.readableBytes();
                //跳过本次可读取的数据
                buffer.readerIndex(buffer.writerIndex());
                // 我们跳过缓冲区中的所有内容，需要再次将偏移量设置为0。
                offset = 0;
            }
            return null;
        }
    }

    private void fail(final ChannelHandlerContext ctx, int length) {
        fail(ctx, String.valueOf(length));
    }

    private void fail(final ChannelHandlerContext ctx, String length) {
        ctx.fireExceptionCaught(
                new TooLongFrameException(
                        "frame length (" + length + ") exceeds the allowed maximum (" + maxLength + ')'));
    }

    /**
     * 返回找到的行末尾的缓冲区中的索引。
     * 如果缓冲区中没有找到行尾，则返回-1。
     */
    private int findEndOfLine(final ByteBuf buffer) {
        int totalLength = buffer.readableBytes();
        int i = buffer.forEachByte(buffer.readerIndex() + offset, totalLength - offset, ByteProcessor.FIND_LF);
        if (i >= 0) {
            offset = 0;
            if (i > 0 && buffer.getByte(i - 1) == '\r') {
                i--;
            }
        } else {
            offset = totalLength;
        }
        return i;
    }
}
