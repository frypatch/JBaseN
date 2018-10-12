# JBaseN
A Java labrary to encode arbitrary data into a set of 3 to 128 bytes of arbitrary data

## Importing Dependency
You can import JBaseN into any gradle project by adding `maven { url 'https://jitpack.io' }` to your build.gradle repositories and `implementation 'com.github.geo-gs:JBaseN:0.0.1'` to your build.gradle dependencies.

Example:
```
repositories {
   jcenter()
   maven { url "https://jitpack.io" }
}

dependencies {
    implementation 'com.github.geo-gs:sawwit-integration:0.1.4'
}
```

## Implementation
Define an encoding table byte[] to use. Then create an instance of BaseN with that table. Then use the endcode or decode functions to encode or decode arbitrary data.

Base56 Example:
```
public class Base56 {
    private static final String ENCODING_TABLE= "abcdefghijkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final String UTF8 = "UTF-8";

    public static String encode(String data) throws Exception {
        byte[] rawData = data.getBytes(UTF8);
        byte[] encodedData = BaseN.getInstance(ENCODING_TABLE.getBytes(UTF8)).encode(rawData);
        return new String(encodedData, UTF8);
    }

    public static String decode(String data) throws Exception {
        byte[] rawData = data.getBytes(UTF8);
        byte[] decodedData = BaseN.getInstance(ENCODING_TABLE.getBytes(UTF8)).decode(rawData);
        return new String(decodedData, UTF8);
    }
}
```
