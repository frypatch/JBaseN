package com.georgemcarlson.jbasen;

import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

public class ArbitraryDataEncodingTest {

  @Test
  public void moderate7BitDataTest() throws Exception {
    String message = "hello world";
    byte[] potentialDictionary = new byte[101];
    for (int i = 0; i < 101; i++) {
      potentialDictionary[i] = (byte) (i - 20);
    }

    for (int i = 45; i < potentialDictionary.length; i++) {
      byte[] dictionary = Arrays.copyOfRange(potentialDictionary, 0, i);
      byte[] encodedData = BaseN.getInstance(dictionary).encode(message.getBytes());
      byte[] decodedData = BaseN.getInstance(dictionary).decode(encodedData);
      Assert.assertEquals(message, new String(decodedData));
    }
  }

  @Test
  public void moderate8BitDataTest() throws Exception {
    byte[] message = new byte[] {100, 23, 45, -2, 13, -65, 45, 9, 87, 66, 123, -115, 34, 56, 77};
    byte[] potentialDictionary = new byte[101];
    for (int i = 0; i < 101; i++) {
      potentialDictionary[i] = (byte) (i - 20);
    }

    for (int i = 3; i < potentialDictionary.length; i++) {
      byte[] dictionary = Arrays.copyOfRange(potentialDictionary, 0, i);
      byte[] encodedData = BaseN.getInstance(dictionary).encode(message);
      byte[] decodedData = BaseN.getInstance(dictionary).decode(encodedData);
      Assert.assertArrayEquals(message, decodedData);
    }
  }

  @Test
  public void small7BitDataTest() throws Exception {
    String message = "a";
    byte[] potentialDictionary = new byte[101];
    for (int i = 0; i < 101; i++) {
      potentialDictionary[i] = (byte) (i - 20);
    }

    for (int i = 33; i < 34; i++) {
      byte[] dictionary = Arrays.copyOfRange(potentialDictionary, 0, i);
      byte[] encodedData = BaseN.getInstance(dictionary).encode(message.getBytes());
      byte[] decodedData = BaseN.getInstance(dictionary).decode(encodedData);
      if (!message.equals(new String(decodedData))) {
        System.out.println("Fails: " + dictionary.length);
      }
    }
  }

  @Test
  public void small8BitDataTest() throws Exception {
    byte[] message = new byte[] {-5};
    byte[] potentialDictionary = new byte[101];
    for (int i = 0; i < 101; i++) {
      potentialDictionary[i] = (byte) (i - 20);
    }

    for (int i = 3; i < potentialDictionary.length; i++) {
      byte[] dictionary = Arrays.copyOfRange(potentialDictionary, 0, i);
      byte[] encodedData = BaseN.getInstance(dictionary).encode(message);
      byte[] decodedData = BaseN.getInstance(dictionary).decode(encodedData);
      Assert.assertArrayEquals(message, decodedData);
    }
  }

}