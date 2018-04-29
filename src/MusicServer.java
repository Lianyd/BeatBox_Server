import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class MusicServer {
    ArrayList<ObjectOutputStream> clientOutputStreams;

    public static void main (String[] args){
        new MusicServer().go();
    }

    public class ClientHandler implements Runnable{

        ObjectInputStream in;
        Socket clientSocket;

        public ClientHandler(Socket socket){   // 重写构造函数
            try{
                clientSocket = socket;
                in = new ObjectInputStream(clientSocket.getInputStream());
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        public void run(){
            Object o2 = null;
            Object o1 = null;
            try {
                while ((o1 = in.readObject()) != null){

                    o2 = in.readObject();

                    System.out.println("read two objects");
                    tellEveryone(o1,o2);
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public void go(){
        clientOutputStreams = new ArrayList<ObjectOutputStream>();
        try {
            ServerSocket serverSock = new ServerSocket(4242); // 设置本地服务器端口号
            while (true){
                Socket clientSocket = serverSock.accept();   // 识别来自客户端的端口号
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());    // 获取要发送给客户端的信息流
                clientOutputStreams.add(out);

                Thread t = new Thread(new ClientHandler(clientSocket)); // 将读取客户端信息作为新线程,在新线程内也实现发送
                t.start();

                System.out.println("got a connection");
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void tellEveryone(Object one, Object two){
        Iterator it = clientOutputStreams.iterator();
        while (it.hasNext()){
            try {
                ObjectOutputStream out = (ObjectOutputStream)it.next();
                out.writeObject(one);
                out.writeObject(two);
        }catch (Exception ex){
            ex.printStackTrace();}
        }
    }
}
