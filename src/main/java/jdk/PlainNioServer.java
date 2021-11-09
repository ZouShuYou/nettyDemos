package jdk;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2020-12-09 15:27
 */
public class PlainNioServer {
    public void serve(int port) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        ServerSocket serverSocket = serverSocketChannel.socket();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
        serverSocket.bind(inetSocketAddress);
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        final ByteBuffer byteBuffer = ByteBuffer.wrap("HI \r\n".getBytes());
        while (true){
            try {
                selector.select();
            }catch (Exception e){
                e.printStackTrace();
                break;
            }
            Set<SelectionKey> readKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                try {
                    if (key.isAcceptable()){
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_WRITE |
                                SelectionKey.OP_READ, byteBuffer.duplicate());
                        System.out.println(
                                "Accepted connection from " + client);
                    }
                    if (key.isWritable()){
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        while (buffer.hasRemaining()){
                            if (client.write(buffer) == 0){
                                break;
                            }
                        }
                        client.close();
                    }
                }catch (IOException e){
                    key.cancel();
                    try {
                        key.channel().close();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        PlainNioServer plainNioServer =new PlainNioServer();
        plainNioServer.serve(9999);
    }
}
