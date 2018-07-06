package com.georgemcarlson.jbasen;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamReader {
    public static String read(byte[] bytes){
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        return read(stream);
    }
    
    public static String read(InputStream stream){
        StringBuilder value = new StringBuilder();
        InputStreamReader reader = null;
        BufferedReader buffer = null;
        int character;
        try{
            reader = new InputStreamReader(stream);
            buffer = new BufferedReader(reader);
            while ((character = buffer.read()) != -1) {
                value.append((char)character);
            }
        } catch(IOException e){
            //Could not determine hostname
            return null;
        } finally{
            if(buffer!=null){
                try{
                    buffer.close();
                } catch(IOException e){
                    //Could not close buffer
                    return null;
                }
            } else if(reader!=null){
                try{
                    reader.close();
                } catch(IOException e){
                    //Could not close reader
                    return null;
                }
            } else if(stream!=null){
                try{
                    stream.close();
                } catch(IOException e){
                    //Could not close stream
                    return null;
                }
            }
        }
        return value.toString();
    }
    
    public static void close(InputStream stream){
        if(stream!=null){
            try{
                stream.close();
            } catch(IOException e){
                //Could not close stream
            }
        }
    }
}