package com.georgemcarlson.jbasen;

import java.util.Arrays;
import static org.junit.Assert.*;
import org.junit.Test;

public class ArbitraryDataEncodignTest {
    @Test
    public void runQuery() throws Exception {
        byte[] message = new byte[]{100,23,45,-2,13,-65,45,9,87,66,123,-115,34,56,77};
        byte[] potentialDictionary = new byte[101];
        for(int i=0; i<101; i++){
            potentialDictionary[i]=(byte)(i-20);
        }
        
        for(int i=3; i<potentialDictionary.length; i++){
            byte[] dictionary = Arrays.copyOfRange(potentialDictionary, 0, i);
            byte[] encodedData = BaseN.getInstance(dictionary).encode(message);
            byte[] decodedData = BaseN.getInstance(dictionary).decode(encodedData);
            if(!Arrays.equals(message,decodedData)){
                fail();
            }

            System.out.println("dictionary: "+StreamReader.read(dictionary));
            System.out.println("encodedData: "+StreamReader.read(encodedData));
            System.out.println("decodedData: "+StreamReader.read(decodedData));
            System.out.println("-----------------------------------------");
        }
    }
}