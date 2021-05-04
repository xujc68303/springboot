package com.xjc.redis;

import java.util.Arrays;

/**
 * @Author jiachenxu
 * @Date 2021/5/2
 * @Descripetion
 */
public class test {
    public static void main(String[] args) {
        int[] array = new int[]{49, 38, 65, 97, 76, 13, 27, 50};
        sort(array, 0, array.length - 1);
        System.out.println(Arrays.toString(array));
    }

    public static void sort(int[] array, int min, int max) {
        int i, j, index;
        if (min > max) {
            return;
        }
        i = min;
        j = max;
        index = array[i];
        while (i < j) {
            while (index <= array[j] && i<j){
                j--;
            }
            if(i<j){
                array[i++] = array[j];
            }
            while (index >= array[i] && i<j){
                i++;
            }
            if(i<j){
                array[j--] = array[i];
                array[i] = index;
            }
            array[i] = index;
        }
        sort(array, min, j-1);
        sort(array, i+1, max);

    }

//    public static void sort(int[] array, int min, int max) {
//        int i, j, index;
//        if(min>max){
//            return;
//        }
//        i = min;
//        j = max;
//        index = array[i];
//        while (i < j) {
//            while (index <= array[j] && i < j) {
//                j--;
//            }
//            if (i < j) {
//                array[i++] = array[j];
//            }
//            while (index >= array[i] && i < j) {
//                i++;
//            }
//            if (i < j) {
//                array[j--] = array[i];
//                array[i] = index;
//            }
//            array[i] = index;
//        }
//        sort(array, min, j - 1);
//        sort(array, i + 1, max);
//
//    }


//    public static void sort(int[] array, int min, int max) {
//        int i,j,index;
//        if(min>max){
//            return;
//        }
//        i = min;
//        j = max;
//        index = array[i];
//        while (i<j){
//            while (index<=array[j] && i<j){
//                j--;
//            }
//            if(i<j){
//                array[i++]=array[j];
//            }
//            while (index>=array[i] && i<j){
//                i++;
//            }
//            if(i<j){
//                array[j--] = array[i];
//                array[i] = index;
//            }
//            array[i]=index;
//        }
//        sort(array, min, j-1);
//        sort(array, i+1, max);
//    }

    public static void sort(int[] array) {
        int temp;//定义一个临时变量
        for (int i = 0; i < array.length - 1; i++) {//冒泡趟数，n-1趟
            for (int j = 0; j < array.length - 1 - i; j++) {
                if (array[j] > array[j + 1]) {
                    temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;

                }
            }
        }
    }

    public static void soryByDesc(int[] array) {
        int t;
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - i - 1; j++) {
                if (array[j] < array[j + 1]) {
                    t = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = t;
                }
            }
        }
    }


}
