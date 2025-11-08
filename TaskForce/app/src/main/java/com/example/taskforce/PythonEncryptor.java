//package com.example.taskforce;
//
//import android.content.Context;
//import org.json.JSONObject;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//
//public class PythonEncryptor {
//    private Context context;
//    private boolean initialized = false;
//    private final String CACHE_FILE_PATH = "/assets/cache.txt"; // Fixed path
//
//    public PythonEncryptor(Context context) {
//        this.context = context;
//        initPython();
//    }
//
//    private void initPython() {
//        if (!initialized) {
//            if (!Python.isStarted()) {
//                Python.start(new AndroidPlatform(context));
//            }
//            initialized = true;
//        }
//    }
//
//    /**
//     * Encrypt: Write plain text to cache file, encrypt it, return encrypted content
//     */
//    public String encrypt(String userId, String plainText) {
//        try {
//            // Write plain text to cache file
//            writeToCacheFile(plainText);
//
//            // Call Python encryption
//            Python py = Python.getInstance();
//            String resultJson = py.getModule("encrypt")
//                    .callAttr("main_wrapper", "encrypt", userId, CACHE_FILE_PATH)
//                    .toString();
//
//            JSONObject jsonResult = new JSONObject(resultJson);
//            boolean success = "success".equals(jsonResult.getString("status"));
//
//            if (success) {
//                // Return the encrypted content
//                return readCacheFile();
//            } else {
//                return "Error: " + jsonResult.getString("message");
//            }
//
//        } catch (Exception e) {
//            return "Error: " + e.getMessage();
//        }
//    }
//
//    /**
//     * Decrypt: Decrypt cache file, return decrypted content, then clear cache
//     */
//    public String decrypt(String userId) {
//        try {
//            // Call Python decryption
//            Python py = Python.getInstance();
//            String resultJson = py.getModule("encrypt")
//                    .callAttr("main_wrapper", "decrypt", userId, CACHE_FILE_PATH)
//                    .toString();
//
//            JSONObject jsonResult = new JSONObject(resultJson);
//            boolean success = "success".equals(jsonResult.getString("status"));
//
//            if (success) {
//                // Read decrypted content
//                String decryptedContent = readCacheFile();
//
//                // Clear cache file after reading
//                clearCacheFile();
//
//                return decryptedContent;
//            } else {
//                return "Error: " + jsonResult.getString("message");
//            }
//
//        } catch (Exception e) {
//            return "Error: " + e.getMessage();
//        }
//    }
//
//    /**
//     * Write content to cache file
//     */
//    private void writeToCacheFile(String content) throws IOException {
//        File file = new File(CACHE_FILE_PATH);
//        try (FileOutputStream fos = new FileOutputStream(file)) {
//            fos.write(content.getBytes(StandardCharsets.UTF_8));
//        }
//    }
//
//    /**
//     * Read content from cache file
//     */
//    private String readCacheFile() throws IOException {
//        File file = new File(CACHE_FILE_PATH);
//        if (!file.exists()) {
//            throw new IOException("Cache file does not exist: " + CACHE_FILE_PATH);
//        }
//
//        try (FileInputStream fis = new FileInputStream(file)) {
//            byte[] contentBytes = new byte[(int) file.length()];
//            fis.read(contentBytes);
//            return new String(contentBytes, StandardCharsets.UTF_8);
//        }
//    }
//
//    /**
//     * Clear/empty the cache file
//     */
//    private void clearCacheFile() throws IOException {
//        File file = new File(CACHE_FILE_PATH);
//        try (FileOutputStream fos = new FileOutputStream(file)) {
//            fos.write("".getBytes(StandardCharsets.UTF_8));
//        }
//    }
//}