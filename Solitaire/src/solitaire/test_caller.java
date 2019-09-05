package solitaire;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Created by sid on 2/17/17.
 */
public class test_caller {
    public static void main(String[] args){
        Solitaire ss = new Solitaire();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter deck file name => ");

        Scanner sc = null;
        try {
            sc = new Scanner(new File(br.readLine()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ss.makeDeck(sc);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ss.jokerB();
        CardNode x = ss.deckRear;

        while(x.next.cardValue != ss.deckRear.cardValue){
            x = x.next;
            System.out.print(x.cardValue + " ");
        }

        System.out.println(ss.deckRear.cardValue);

    }
}
