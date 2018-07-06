package com.georgemcarlson.jbasen;

import java.util.ArrayList;
import java.util.List;

public class BaseN {
    private final byte[] encodingTable;
    private int bytesNeeded=-1;
//    Note: A packed bit length of 21 takes way longer to calculate than 18
    private static final int PACKED_BIT_LENGTH = 21;
//    18 is optimized for base64    
//    private static final int PACKED_BIT_LENGTH = 18;
    
    private BaseN(byte[] encodingTable){
        this.encodingTable = encodingTable;
    }
    
    /**
     * Supply an encoding table to use. 
     * 
     * Note: encoding table length must be 
     * greater than two.
     * 
     * @param encodingTable
     * @return
     * @throws Exception 
     */
    public static BaseN getInstance(byte[] encodingTable) throws Exception{
        if(encodingTable.length<3){
            throw new Exception("Encoding table length must be greater than two");
        }
        return new BaseN(encodingTable);
    }
    
    public byte[] encode(byte[] data){
        List<Boolean> bits = new ArrayList();
        bits.addAll(getMetaData(data));
        bits.addAll(getBitsFromData(data));

        List<Byte> byteList = new ArrayList();
        for(int i=0; i<bits.size(); i+=PACKED_BIT_LENGTH){
            StringBuilder aByte = new StringBuilder();
            for(int j=i; j<bits.size()&j-i<PACKED_BIT_LENGTH; j++){
                if(bits.get(j)){
                    aByte.append("1");
                } else{
                    aByte.append("0");
                }
            }
            long l = Long.parseLong(aByte.toString(),2);
            byteList.addAll(getEncodedLong(l));
        }
        return getByteArray(byteList);
    }
    
    public byte[] decode(byte[] data){
        List<Boolean> decodedBits = new ArrayList();
        for(Long decodedLong : getDecodedLongs(data)){
            String bits = Long.toBinaryString(decodedLong);
            while(bits.length()<PACKED_BIT_LENGTH){
                bits="0"+bits;
            }
            for(int i=0; i<bits.length(); i++){
                if(bits.charAt(i)=='1'){
                    decodedBits.add(true);
                } else{
                    decodedBits.add(false);
                }
            }
        }
        
        int bitsPerByte = isData7Bits(decodedBits)?7:8;
        int paddingLength = getPaddingLength(decodedBits);
        
        List<Boolean> decodedBitsWithoutPadding=new ArrayList();
        decodedBitsWithoutPadding.addAll(decodedBits.subList(getMetaDataLength(),decodedBits.size()-PACKED_BIT_LENGTH));
        decodedBitsWithoutPadding.addAll(decodedBits.subList(decodedBits.size()-paddingLength,decodedBits.size()));
        List<Byte> potentialDecodedBytes = new ArrayList();
        for(int i=0;i<decodedBitsWithoutPadding.size();i+=bitsPerByte){
            StringBuilder aByte = new StringBuilder();
            for(int j=i;j<decodedBitsWithoutPadding.size()&&aByte.length()<bitsPerByte;j++){
                if(decodedBitsWithoutPadding.get(j)){
                    aByte.append("1");
                } else{
                    aByte.append("0");
                }
            }
            potentialDecodedBytes.add((byte)Long.parseLong(aByte.toString(),2));
        }
        
        byte[] decodedBytes = new byte[potentialDecodedBytes.size()];
        for(int i=0;i<potentialDecodedBytes.size();i++){
            decodedBytes[i]=potentialDecodedBytes.get(i);
        }
        return decodedBytes;
    }
    
    private List<Boolean> getMetaData(byte[] data){
        List<Boolean> bitStream = new ArrayList();
        byte metaData = 0;
        if(isData7Bits(data)){
            metaData=(byte)((data.length*7+getMetaDataLength())%PACKED_BIT_LENGTH);
        } else{
            metaData=(byte)(PACKED_BIT_LENGTH+(data.length*8+getMetaDataLength())%PACKED_BIT_LENGTH);
        }
        String bits = Long.toBinaryString(metaData);
        while(bits.length()<getMetaDataLength()){
            bits = "0"+bits;
        }
        for(int i=0; i<bits.length(); i++){
            if(bits.charAt(i)=='1'){
                bitStream.add(true);
            } else{
                bitStream.add(false);
            }
        }
        return bitStream;
    }
    
    private List<Byte> getEncodedLong(long number){
        List<Integer> count = new ArrayList();
        while(count.size()<getBytesNeeded()+1){
            count.add(0);
        }
        for(int i=0; i<number; i++){
            int j=0;
            while(true){
                if(count.get(j)+1<getBase()){
                    count.set(j,count.get(j)+1);
                    break;
                } else{
                    count.set(j, 0);
                }
                j++;
            }
        }
        List<Byte> byteList = new ArrayList();
        for(int i=0; i<getBytesNeeded(); i++){
            byteList.add(getEncodingTable()[count.get(i)]);
        }
        if(count.get(count.size()-1)>0){
            byteList.add(getOverflowCharacterByte());
        }
        return byteList;
    }
    
    public List<Long> getDecodedLongs(byte[] data){
        List<Long> decodedLongs = new ArrayList();
        
        int i = 0;
        List<Byte> potentialData = new ArrayList();
        while(i<data.length){
            potentialData.add(data[i]);
            if(potentialData.size()==getBytesNeeded()){
                if(i+1<data.length&&data[i+1]==getOverflowCharacterByte()){
                    potentialData.add(getOverflowCharacterByte());
                    i++;
                }
                decodedLongs.add(getDecodedLong(potentialData));
                potentialData = new ArrayList();
            }
            i++;
        }
        return decodedLongs;
    }
    
    private long getDecodedLong(List<Byte> data){
        List<Integer> count = new ArrayList();
        while(count.size()<getBytesNeeded()+1){
            count.add(0);
        }
        for(int i=0; i<data.size(); i++){
            byte aByte = data.get(i);
            if(aByte==getOverflowCharacterByte()){
                count.set(i, 1);
            } else{
                count.set(i, getEncodingTableIndexOf(aByte));
            }
        }
        long decodedLong=0;
        for(int i=0; i<count.size(); i++){
            decodedLong+=(long)Math.pow(getBase(),i)*count.get(i);
        }
        return decodedLong;
    }
    
    private byte[] getEncodingTable(){
        return this.encodingTable;
    }
    
    private int getEncodingTableIndexOf(byte aByte){
        for(int i=0; i<getEncodingTable().length; i++){
            if(getEncodingTable()[i]==aByte){
                return i;
            }
        }
        return -1;
    }
    
    private byte getOverflowCharacterByte(){
        return getEncodingTable()[getEncodingTable().length-1];
    }
    
    private boolean isData7Bits(List<Boolean> decodedBits){
        StringBuilder metaDataBits = new StringBuilder();
        for(int i=0; i<getMetaDataLength(); i++){
            if(decodedBits.get(i)){
                metaDataBits.append("1");
        
            } else{
                metaDataBits.append("0");
            }
        }
        return Long.parseLong(metaDataBits.toString(),2)<PACKED_BIT_LENGTH;
    }
    
    private boolean isData7Bits(byte[] data){
        for(int i=0; i<data.length; i++){
            byte aByte = data[i];
            if((int)aByte<0){
                return false;
            }
        }
        return true;
    }
    
    private int getPaddingLength(List<Boolean> decodedBits){
        StringBuilder metaDataBits = new StringBuilder();
        for(int i=0; i<getMetaDataLength(); i++){
            if(decodedBits.get(i)){
                metaDataBits.append("1");
            } else{
                metaDataBits.append("0");
            }
        }
        int i = Integer.parseInt(metaDataBits.toString(),2);
        if(i<PACKED_BIT_LENGTH+1){
            return i;
        } else{
            return i-PACKED_BIT_LENGTH;
        }
    }
    
    private List<Boolean> getBitsFromData(byte[] data){
        List<Boolean> bitStream = new ArrayList();
        int bitLength;
        if(isData7Bits(data)){
            bitLength=7;
        } else{
            bitLength=8;
        }
        for(byte aByte : data){
            String bits = Integer.toBinaryString(aByte & 0xFF);
            while(bits.length()<bitLength){
                bits="0"+bits;
            }
            for(int i=0; i<bits.length(); i++){
                if(bits.charAt(i)=='1'){
                    bitStream.add(true);
                } else{
                    bitStream.add(false);
                }
            }
        }
        return bitStream;
    }
    
    private int getMetaDataLength(){
        return Integer.toBinaryString(PACKED_BIT_LENGTH*2).length();
    }
    
    private int getBase(){
        return getEncodingTable().length-1;
    }
    
    private Integer getBytesNeeded(){
        if(bytesNeeded==-1){
            double viability = Math.pow(2, PACKED_BIT_LENGTH)-1;
            int count = 1;
            while((Math.pow(getBase(), count)-1)*2<viability){
                count++;
            }
            bytesNeeded = count;
        }
        return bytesNeeded;
    }
    
    private byte[] getByteArray(List<Byte> byteList){
        byte[] bytes = new byte[byteList.size()];
        for(int i=0; i<byteList.size(); i++){
            bytes[i]=byteList.get(i);
        }
        return bytes;
    }
}