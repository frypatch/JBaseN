package com.georgemcarlson.jbasen;

import java.util.Arrays;
import java.util.Base64;

public class Examples {

    public static void main(String[] args) throws Exception {
        boolean success=true;

        byte[] message;
        byte[] potentialEncodingTable;
        
        if(success){
            message = new byte[]{100,23,45,-2,13,-65,45,9,87,66,123,-115,34,56,77};
            potentialEncodingTable = new byte[101];
            for(int i=0; i<101; i++){
                potentialEncodingTable[i]=(byte)(i-128);
            }
            success = test(message,potentialEncodingTable);
        }
        
        if(success){
            message = new byte[]{100,23,45,-2,13,-65,45,9,87,66,123,-115,34,56,77};
            potentialEncodingTable = new byte[101];
            for(int i=0; i<101; i++){
                potentialEncodingTable[i]=(byte)(i);
            }
            success = test(message,potentialEncodingTable);
        }
        
        if(success){
            message = new byte[]{100,23,45,-2,13,-65,45,9,87,66,123,-115,34,56,77};
            potentialEncodingTable = new byte[101];
            for(int i=0; i<101; i++){
                potentialEncodingTable[i]=(byte)(i-20);
            }
            success = test(message,potentialEncodingTable);
        }
        
        if(success){
            message = "Hello World".getBytes();
            potentialEncodingTable = new byte[101];
            for(int i=0; i<101; i++){
                potentialEncodingTable[i]=(byte)(i-20);
            }
            success = test(message,potentialEncodingTable);
        }
        
        if(success){
            message = "Hello World".getBytes();
            potentialEncodingTable = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789,./".getBytes();
            success = test(message,potentialEncodingTable);
        }
        
        message = "Hello World".getBytes();
        byte[] encodedData = Base64.getEncoder().encode(message);
        byte[] decodedData = Base64.getDecoder().decode(encodedData);
        System.out.println("Base64");
        System.out.println(StreamReader.read(encodedData));
        System.out.println(StreamReader.read(decodedData));
        System.out.println(Arrays.equals(message,decodedData));
        System.out.println("-----------------------------------------");

        System.out.println("success: "+success);
    }
    
    private static boolean test(byte[] message, byte[] potentialEncodingTable) throws Exception{
        boolean success = true;
        for(int i=3; i<potentialEncodingTable.length; i++){
            byte[] encodingTable = new byte[i];
            for(int j=0;j<i;j++){
                encodingTable[j]=potentialEncodingTable[j];
            }
            byte[] encodedData = BaseN.getInstance(encodingTable).encode(message);
            byte[] decodedData = BaseN.getInstance(encodingTable).decode(encodedData);
            if(!Arrays.equals(message,decodedData)){
                success = false;
            }
            System.out.println(encodingTable.length);
            System.out.println(StreamReader.read(encodingTable));
            System.out.println(StreamReader.read(encodedData));
            System.out.println(StreamReader.read(decodedData));
            System.out.println(Arrays.equals(message,decodedData));
            System.out.println("-----------------------------------------");
        }
        return success;
    }
}