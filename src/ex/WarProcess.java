package ex;

import java.util.*;

public class WarProcess {//ゲームの処理
    private final boolean[] waitResponse;
    private final int[] hand;
    private final int[] playerID;
    private final int[] score;
    private int scoreStack;
    private final Stack<String>[] card;
    private int count;


    WarProcess(){
        waitResponse=new boolean[]{false,false};
        hand=new int[]{-1,-1};
        playerID=new int[]{-1,-1};
        score=new int[]{0,0};
        card=new Stack[2];
        count=0;
        scoreStack=0;
    }

    public synchronized void setPlayerID(int p){
        if(playerID[0]==-1){
            playerID[0]=p;
            card[playerID[0]]= new Stack<>();
            List<String> a=new ArrayList<>(Arrays.asList("1","2","3","4","5","6","7","8","9","10","11","12","13"));
            Collections.shuffle(a);
            for(String s:a){
                card[playerID[0]].push(s);
            }
        }else{
            playerID[1]=p;
            card[playerID[1]]= new Stack<>();
            List<String> a=new ArrayList<>(Arrays.asList("1","2","3","4","5","6","7","8","9","10","11","12","13"));
            Collections.shuffle(a);
            for(String s:a){
                card[playerID[1]].push(s);
            }
        }
    }

    public synchronized String getCard(int pID){
        if(card[pID].size()!=0) {
            return card[pID].pop();
        }
        return "0";
    }

    public synchronized String getMyCardSize(int pID){
        return String.valueOf(card[pID].size());
    }

    public synchronized String getAnotherCardSize(int pID){
        if(playerID[0]==pID){
            return String.valueOf(card[1].size());
        }
        return String.valueOf(card[pID].size());
    }

    public synchronized void setScore(int pID){
        if(playerID[0]==pID){
            score[0]+=1+scoreStack;
            scoreStack=0;
        }else if(playerID[1]==pID){
            score[1]+=1+scoreStack;
            scoreStack=0;
        }else {
            scoreStack++;
        }
    }

    public synchronized int getMyScore(int pID){
        return score[pID];
    }

    public synchronized int getAnotherScore(int pID){
        if(playerID[0]==pID){
            return score[1];
        }
        return score[0];
    }

    public synchronized void setHand(int h,int pID){
        if(playerID[0]==pID){
            hand[0]=h;
            waitResponse[playerID[0]]=true;
        }else{
            hand[1]=h;
            waitResponse[playerID[1]]=true;
        }
    }

    public synchronized int getAnotherHand (int pID){
        if(playerID[0]==pID){
            return hand[1];
        }
        return hand[0];

    }

    private void count(){
        if(count==0) {
            count++;
        }else{
            count=0;
            waitResponse[0]=false;
            waitResponse[1]=false;
        }
    }

    public synchronized String winnerJudge(int pID){
        if(waitResponse[0]&&waitResponse[1]){//両プレイヤーが手札を選択したか
            if(hand[0]==hand[1]){//引き分けの時の処理
                if(count<1) {
                    setScore(-1);
                }
                count();
                return "draw";
            }else if(hand[0]>hand[1]){//プレイヤー１がプレイヤー２より数が大きい時

                if(playerID[0]==pID){
                    if(hand[0]==13&&hand[1]==1){//プレイヤー１がキング、プレイヤー2がエースのとき
                        count();
                        return "lose";
                    }
                    setScore(pID);
                    count();
                    return "win";
                }

                if(hand[0]==13&&hand[1]==1){//プレイヤー１がキング、プレイヤー2がエースのとき
                    setScore(pID);
                    count();
                    return "win";
                }
                count();
                return "lose";

            }else {

                if(playerID[0]==pID){
                    if(hand[0]==1&&hand[1]==13){//プレイヤー１がエース、プレイヤー2がキングのとき
                        setScore(pID);
                        count();
                        return "win";
                    }
                    count();
                    return "lose";
                }

                if(hand[0]==1&&hand[1]==13){//プレイヤー１がエース、プレイヤー2がキングのとき
                    count();
                    return "lose";
                }
                setScore(pID);
                count();
                return "win";
            }
        }
        return "wait";
    }
}
