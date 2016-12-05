package com.udacity.mlndcapstone.numtrans;

import android.util.Log;

import java.util.HashMap;

/**
 * Created by girishpai on 11/4/16.
 */
public class NumberToWordConv {
    HashMap<Integer, String> map;
    public NumberToWordConv () {
       map = new HashMap<Integer, String>();
        fillMap();
    }
    public String numberToWords(int num) {
        StringBuilder sb = new StringBuilder();

        if(num==0){
            return map.get(0);
        }

        if(num >= 1000000000){
            int extra = num/1000000000;
            sb.append(convert(extra) + " Billion");
            num = num%1000000000;
        }

        if(num >= 1000000){
            int extra = num/1000000;
            sb.append(convert(extra) + " Million");
            num = num%1000000;
        }

        if(num >= 1000){
            int extra = num/1000;
            sb.append(convert(extra) + " Thousand");
            num = num%1000;
        }

        if(num > 0){
            sb.append(convert(num));
        }

        return sb.toString().trim();
    }

    public String convert(int num){

        StringBuilder sb = new StringBuilder();

        if(num>=100){
            int numHundred = num/100;
            sb.append(" " +map.get(numHundred)+ " Hundred");
            num=num%100;
        }

        if(num > 0){
            if(num>0 && num<=20){
                sb.append(" "+map.get(num));
            }else{
                int numTen = num/10;
                sb.append(" "+map.get(numTen*10));

                int numOne=num%10;
                if(numOne>0){
                    sb.append(" " + map.get(numOne));
                }
            }
        }

        return sb.toString();
    }

    public void fillMap(){
        map.put(0, "Zero");
        map.put(1, "One");
        map.put(2, "Two");
        map.put(3, "Three");
        map.put(4, "Four");
        map.put(5, "Five");
        map.put(6, "Six");
        map.put(7, "Seven");
        map.put(8, "Eight");
        map.put(9, "Nine");
        map.put(10, "Ten");
        map.put(11, "Eleven");
        map.put(12, "Twelve");
        map.put(13, "Thirteen");
        map.put(14, "Fourteen");
        map.put(15, "Fifteen");
        map.put(16, "Sixteen");
        map.put(17, "Seventeen");
        map.put(18, "Eighteen");
        map.put(19, "Nineteen");
        map.put(20, "Twenty");
        map.put(30, "Thirty");
        map.put(40, "Forty");
        map.put(50, "Fifty");
        map.put(60, "Sixty");
        map.put(70, "Seventy");
        map.put(80, "Eighty");
        map.put(90, "Ninety");
    }
}