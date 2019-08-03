package im.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class ByteBufTest {
    public static void main(String[] args) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(9, 100);

        print("allocate ByteBuf(9,100)", buffer);

        buffer.writeBytes(new byte[]{1, 2, 3, 4});
        print("writeBytes(1,2,3,4)", buffer);

        buffer.writeInt(12);
        print("writeInt(12)", buffer);

        buffer.writeBytes(new byte[]{5});
        print("writeBytes(5)", buffer);

        buffer.writeBytes(new byte[]{6});
        print("writeBytes(6)", buffer);

        // get 方法不改变读写指针
        System.out.println("getByte(3) return: " + buffer.getByte(3));
        System.out.println("getShort(3) return: " + buffer.getShort(3));
        System.out.println("getInt(3) return: " + buffer.getInt(3));
        print("getByte()", buffer);


        // set 方法不改变读写指针
        buffer.setByte(buffer.readableBytes() + 1, 0);
        print("setByte()", buffer);

        // read 方法改变读指针
        byte[] dst = new byte[buffer.readableBytes()];
        buffer.readBytes(dst);
        print("readBytes(" + dst.length + ")", buffer);

    }

    private static void print(String action, ByteBuf buffer) {
        System.out.format("after ======= %s =======%n", action);
        System.out.format("capacity(): %s%n", buffer.capacity());
        System.out.format("maxCapacity(): %s%n", buffer.maxCapacity());
        System.out.format("readerIndex(): %s%n", buffer.readerIndex());
        System.out.format("readableBytes(): %s%n", buffer.readableBytes());
        System.out.format("isReadable(): %s%n", buffer.isReadable());
        System.out.format("writerIndex(): %s%n", buffer.writerIndex());
        System.out.format("writableBytes(): %s%n", buffer.writableBytes());
        System.out.format("isWritable(): %s%n", buffer.isWritable());
        System.out.format("maxWritableBytes(): %s%n", buffer.maxWritableBytes());
        System.out.println();
    }
}
