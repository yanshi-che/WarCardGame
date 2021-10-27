package ex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class WarServerThread extends Thread{
    private String playerName;
    private final Socket socket;
    private final int playerID;
    private final int roomID;
    private final BufferedReader in;
    private final PrintWriter out;
    private final WarProcess warProcess;

    public WarServerThread(Socket s,int pid,int rid,BufferedReader i, PrintWriter o,WarProcess w){
        super();
        socket=s;
        playerID=pid;
        roomID=rid;
        in=i;
        out=o;
        warProcess=w;
    }

    public void run(){
        try {
            out.println("roomNo."+roomID+"playerNo." + playerID + "接続");

            String firstSend=""; //ボタン１,ボタン２,ボタン３,山札の枚数

            firstSend+=warProcess.getCard(playerID)+","+warProcess.getCard(playerID)+","+warProcess.getCard(playerID)
                    +","+warProcess.getMyCardSize(playerID);

            System.out.println(firstSend+"送信"+roomID+":"+playerID);

            out.println(firstSend);

            while (true) {
                System.out.println("送信待ち"+roomID+":"+playerID);
                String reply = in.readLine();
                String[] replySplit;
                if(reply!=null) {
                    replySplit = reply.split(",");
                    out.println("");
                    System.out.println("データ受信"+replySplit[0]+":"+replySplit[1]+ ":" + roomID + ":" + playerID);
                    String buttonID=replySplit[0];
                    warProcess.setHand(Integer.parseInt(replySplit[1]),playerID);
                    String judge=warProcess.winnerJudge(playerID);
                    while(judge.equals("wait")){
                        Thread.sleep(3000);
                        judge=warProcess.winnerJudge(playerID);
                    }
                    out.println(buttonID+","+judge+","+warProcess.getCard(playerID)+","+warProcess.getMyCardSize(playerID)
                    +","+warProcess.getMyScore(playerID)+","+warProcess.getAnotherCardSize(playerID)
                    +","+warProcess.getAnotherScore(playerID)+","+warProcess.getAnotherHand(playerID));
                    //選んだボタン、判定、次のカード、自分の山札、自分のスコア、相手の山札、相手のスコア、相手の選んだ手札、引き分けの時のスコアのスタック
                }else{
                    break;
                }
            }
        } catch (Exception ignored) {
        }finally {
            try {
                socket.close();
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}