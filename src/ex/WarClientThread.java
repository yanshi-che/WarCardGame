package ex;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class WarClientThread extends Thread{
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final JButton[] buttons;
    private final WarClient.ButtonAction[] buttonActions;
    private final Map<Integer, ImageIcon> iconMap;
    private final JTextArea[] textArea;
    private int scoreStack;
    WarClientThread(Socket s,BufferedReader i, PrintWriter o, JButton[] b, WarClient.ButtonAction[] ba, Map<Integer, ImageIcon> m, JTextArea[] ta){
        in=i;
        out=o;
        buttons = b;
        buttonActions=ba;
        iconMap=m;
        textArea=ta;
        socket=s;
        scoreStack=0;
    }

    public void run(){
        try {
            String response;//サーバーからの受信
            String[] split;//受信したデータの分割
            response=in.readLine();//最初の一回だけ実行、サーバーから山札の一人手札のカードを受け取る
            split=response.split(",",-1);
            for(int i=0;i<split.length-1;i++){
                buttonActions[i].setHand(Integer.parseInt(split[i]));
                buttons[i].setIcon(iconMap.get(Integer.parseInt(split[i])));
            }

            textArea[0].append("山札: "+split[split.length-1]+"\n"+"得点: 0");
            textArea[1].append("山札: "+split[split.length-1]+"\n"+"得点: 0");

            while(true){
                response=in.readLine();//一度選択したら他のボタンを押せなくする

                for (WarClient.ButtonAction buttonAction : buttonActions) {
                    buttonAction.setStopButtonAction(true);
                }

                response=in.readLine();//勝敗結果を受け取る

                split=response.split(",",-1);

                buttons[8].setText("");
                buttons[8].setText(split[1]);//勝敗の表示
                buttons[4].setIcon(iconMap.get(Integer.parseInt(split[7])));//相手の選んだカードの描画

                Thread.sleep(5000);
                textArea[0].setText("");
                textArea[1].setText("");
                textArea[0].append("山札: "+split[3]+"\n"+"得点: "+split[4]);//山札と得点を再表示
                textArea[1].append("山札: "+split[5]+"\n"+"得点: "+split[6]);
                buttonActions[Integer.parseInt(split[0])].setHand(Integer.parseInt(split[2]));//選択したボタンに新たにカードを設置
                buttons[Integer.parseInt(split[0])].setIcon(iconMap.get(Integer.parseInt(split[2])));//カードの描画
                buttons[3].setIcon(iconMap.get(0));//自分が選んだカードの描画を初期化
                buttons[4].setIcon(iconMap.get(0));//相手が選んだカードの描画を初期化
                buttons[8].setText("");
                if(split[1].equals("draw")){//引き分けだった時の保留点数の表示
                    scoreStack++;
                    buttons[8].setText("保留:"+ scoreStack);
                }else{
                    scoreStack=0;
                }

                if(buttonActions[0].getHand()==0&&buttonActions[1].getHand()==0&&buttonActions[2].getHand()==0){//ゲームが終了した時の処理
                    if(Integer.parseInt(split[4])>Integer.parseInt(split[6])) {
                        JOptionPane.showConfirmDialog( null, "You win", "結果",
                                JOptionPane.DEFAULT_OPTION );
                    }else if(Integer.parseInt(split[6])>Integer.parseInt(split[4])){
                        JOptionPane.showConfirmDialog( null, "You lose", "結果",
                                JOptionPane.DEFAULT_OPTION );
                    }else{
                        JOptionPane.showConfirmDialog( null, "Draw", "結果",
                                JOptionPane.DEFAULT_OPTION );
                    }
                    break;
                }

                for (WarClient.ButtonAction buttonAction : buttonActions) {//再びボタンを押せるように
                    buttonAction.setStopButtonAction(false);
                }

            }
        } catch (IOException | InterruptedException ignored) {
        }finally {
            try {
                socket.close();
                in.close();
                out.close();
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
