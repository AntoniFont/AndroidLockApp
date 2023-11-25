package com.example.applock;
/*
* CHAT GPT IS JUST TOO OP
*
* */
import android.os.Handler;
import android.os.Looper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PhpRequestHandler {

    public interface OnResultListener {
        void onResult(boolean isYes);
    }

    public static class PhpRequestTask implements Runnable {

        private final String url;
        private final OnResultListener listener;

        public PhpRequestTask(String url, OnResultListener listener) {
            this.url = url;
            this.listener = listener;
        }

        @Override
        public void run() {
            try {
                String result = makeRequest(url);
                boolean isYes = result != null && result.trim().equalsIgnoreCase("yes");

                // Ensure the listener callback is executed on the main thread
                new Handler(Looper.getMainLooper()).post(() -> listener.onResult(isYes));
            } catch (IOException e) {
                e.printStackTrace();
                // Handle error, for example, by calling listener.onResult(false);
            }
        }

        private String makeRequest(String urlStr) throws IOException {
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                return stringBuilder.toString();
            } finally {
                urlConnection.disconnect();
            }
        }
    }

    public static void getResponse(String phpUrl, OnResultListener listener) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new PhpRequestTask(phpUrl, listener));
    }
}

