package ex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class WarServer{
    private static final int port = 8888;
    private static final int maxConnection = 2;
    private static final int maxRoom = 10;

    public static void main(String[] args) {
        Socket[][] socket = new Socket[maxRoom][maxConnection];
        InputStreamReader[][] isr = new InputStreamReader[maxRoom][maxConnection];
        BufferedReader[][] in = new BufferedReader[maxRoom][maxConnection];
        PrintWriter[][] out = new PrintWriter[maxRoom][maxConnection];
        WarServerThread[][] warServerThread = new WarServerThread[maxRoom][maxConnection];
        WarProcess[] warProcess = new WarProcess[maxRoom];
        int playerCount = 0;
        int roomCount = 0;
        try {
            System.out.println("サーバー起動");
            ServerSocket server = new ServerSocket(port);
            System.out.println("Ready");
            while(true) {
                    System.out.println("接続待ち");
                if(roomCount <maxRoom) {
                    socket[roomCount][playerCount] = server.accept();
                    System.out.println("roomNo."+ roomCount +"playerNo."+ playerCount +"connected");
                    isr[roomCount][playerCount] = new InputStreamReader(socket[roomCount][playerCount].getInputStream());
                    in[roomCount][playerCount] = new BufferedReader(isr[roomCount][playerCount]);
                    out[roomCount][playerCount] = new PrintWriter(socket[roomCount][playerCount].getOutputStream(), true);
                    if(playerCount ==0) {
                        warProcess[roomCount] = new WarProcess();
                    }
                    warProcess[roomCount].setPlayerID(playerCount);
                    warServerThread[roomCount][playerCount] = new WarServerThread(socket[roomCount][playerCount], playerCount, roomCount, in[roomCount][playerCount], out[roomCount][playerCount], warProcess[roomCount]);
                    warServerThread[roomCount][playerCount].start();
                    if(playerCount <maxConnection-1){
                        playerCount++;
                    }else {
                        playerCount =0;
                        roomCount++;
                    }

                }
            }

        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
