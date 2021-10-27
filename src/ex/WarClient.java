package ex;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class WarClient extends JFrame {
    private BufferedReader in;
    private PrintWriter out;

    public static void main(String[] args) {
        WarClient wc = new WarClient();
    }

    WarClient() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("CardWar");
        setSize(850, 900);

        JPanel basePane = new JPanel();
        basePane.setLayout(null);
        basePane = (JPanel) getContentPane();
        setVisible(true);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu menu = new JMenu("ルール");
        JMenuItem menuItem = new JMenuItem(new MenuAction());
        menu.add(menuItem);
        menuBar.add(menu);

        Socket socket = null;

        try {
            socket = new Socket("localhost", 8888);//サーバーに接続
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            in = new BufferedReader(isr);
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<Integer, ImageIcon> iconMap = new HashMap<>();
        iconMap.put(0, new ImageIcon("ex/ura.png"));//画像の取り込み
        iconMap.put(1, new ImageIcon("ex/b1.png"));
        iconMap.put(2, new ImageIcon("ex/b2.png"));
        iconMap.put(3, new ImageIcon("ex/b3.png"));
        iconMap.put(4, new ImageIcon("ex/b4.png"));
        iconMap.put(5, new ImageIcon("ex/b5.png"));
        iconMap.put(6, new ImageIcon("ex/b6.png"));
        iconMap.put(7, new ImageIcon("ex/b7.png"));
        iconMap.put(8, new ImageIcon("ex/b8.png"));
        iconMap.put(9, new ImageIcon("ex/b9.png"));
        iconMap.put(10, new ImageIcon("ex/b10.png"));
        iconMap.put(11, new ImageIcon("ex/b11.png"));
        iconMap.put(12, new ImageIcon("ex/b12.png"));
        iconMap.put(13, new ImageIcon("ex/b13.png"));

        JButton[] buttons = new JButton[9];//カードの描画と選択をするためのボタン
        ButtonAction[] buttonActions=new ButtonAction[3];
        LineBorder border = new LineBorder(Color.BLACK, 1, true);
        for (int i = 0; i < buttons.length-1; i++) {
            if(i<buttonActions.length) {
                buttonActions[i] = new ButtonAction(i, out, buttons, iconMap);
                buttons[i] = new JButton(buttonActions[i]);
                buttons[i].setBounds(150 +200 * i, 588, 150, 212);
                buttons[i].setBorder(border);
            }else{
                buttons[i] = new JButton();
                if(i==3){
                    buttons[i].setBounds(225, 312, 150, 212);
                }else if(i==4){
                    buttons[i].setBounds(475, 312, 150, 212);
                }else
                    buttons[i].setBounds(150 +200*(i-5), 50, 150, 212);
            }
            buttons[i].setBorder(border);
            buttons[i].setIcon(iconMap.get(0));
            basePane.add(buttons[i]);
        }

        JTextArea[] textArea = new JTextArea[2];//山札の数や得点を表示
        TitledBorder tb0=new TitledBorder("自分");
        TitledBorder tb1=new TitledBorder("相手");
        textArea[0]=new JTextArea("");
        textArea[0].setEditable(false);
        textArea[0].setBorder(tb0);
        textArea[0].setBounds(30,500,100,100);
        basePane.add(textArea[0]);
        textArea[1]=new JTextArea("");
        textArea[1].setEditable(false);
        textArea[1].setBorder(tb1);
        textArea[1].setBounds(730,250,100,100);
        basePane.add(textArea[1]);

        buttons[8]=new JButton();//背景や勝敗、保留された得点を表示
        buttons[8].setIcon(new ImageIcon("ex/background.png"));
        buttons[8].setHorizontalTextPosition(JButton.CENTER);
        buttons[8].setFont(new Font("メイリオ", Font.PLAIN, 28));
        basePane.add(buttons[8]);

        WarClientThread wct = new WarClientThread(socket,in,out, buttons,buttonActions, iconMap, textArea);
        wct.start();
    }

    static class ButtonAction extends AbstractAction {
        private final int ID;
        private final PrintWriter out;
        private int hand;
        private final JButton[] buttons;
        private final Map<Integer, ImageIcon> iconMap;
        private boolean stopButtonAction;

        ButtonAction(int id, PrintWriter o,JButton[] b,Map<Integer, ImageIcon> i) {
            ID = id;
            out = o;
            hand=0;
            buttons=b;
            iconMap=i;
            stopButtonAction=false;
        }

        public void setHand(int h) {
            hand = h;
        }

        public int getHand(){ return hand; }

        public void setStopButtonAction(boolean b){stopButtonAction=b;}

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!stopButtonAction&&hand!=0) {
                out.println(ID + "," + hand);
                buttons[3].setIcon(iconMap.get(hand));
                hand=0;
                buttons[ID].setIcon(iconMap.get(hand));
            }
        }
    }

    static class MenuAction extends AbstractAction{

        MenuAction(){
            putValue(Action.NAME,"ルール");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showConfirmDialog( null,
                    "このゲームでは「A」が一番弱くて「K」が一番強い。ただし、「A」は「k」に対してだけ強い。\n"
                            +"K<A<2<3 … J<Q<K<A\n"
                            +"引き分けになると得点がストックされ、次に勝ったプレイヤーの総取りとなる。"

                    ,"ルール", JOptionPane.DEFAULT_OPTION );
        }
    }
}
