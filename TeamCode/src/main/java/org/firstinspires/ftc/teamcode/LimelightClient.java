package org.firstinspires.ftc.teamcode;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Minimal Limelight JSON reader for FTC Android (no NetworkTables).
 * It tries common endpoints and pulls tx/ty/botpose if present.
 *
 * Known endpoints seen in the wild (pick what your LL firmware serves):
 *   - http://<host>:5807/limelight.json
 *   - http://<host>:5807/limelight-json
 *   - http://<host>:5807/json
 *
 * If your camera exposes a different path/key, change ENDPOINTS or JSON keys below.
 */
public class LimelightClient {
    private static final String TAG = "LLClient";
    private final String base;
    private static final String[] ENDPOINTS = new String[]{
            "/limelight.json", "/limelight-json", "/json"
    };

    private boolean hasTarget = false;
    private double tx = 0.0;
    private double ty = 0.0;
    private double[] botpose = null;

    public LimelightClient(String baseUrl) {
        // Example baseUrl: "http://limelight.local:5807" or "http://10.0.0.11:5807"
        this.base = baseUrl.replaceAll("/+$", "");
    }

    public void update() {
        JSONObject root = fetchFirstAvailable();
        if (root == null) {
            hasTarget = false;
            return;
        }

        // Common fields (adjust if your JSON differs)
        // Prefer direct fields if present:
        if (root.has("tx")) tx = root.optDouble("tx", 0.0);
        if (root.has("ty")) ty = root.optDouble("ty", 0.0);
        if (root.has("botpose")) botpose = jsonArrayToDoubleArray(root.optJSONArray("botpose"));

        // Many Limelight builds wrap data under "Results" or "targets"
        JSONObject results = root.optJSONObject("Results");
        if (results != null) {
            // tx/ty might be arrays per target; grab the first if available
            if (results.has("tx")) tx = results.optDouble("tx", tx);
            if (results.has("ty")) ty = results.optDouble("ty", ty);

            // If they’re arrays:
            JSONArray txArr = results.optJSONArray("tx");
            if (txArr != null && txArr.length() > 0) tx = txArr.optDouble(0, tx);

            JSONArray tyArr = results.optJSONArray("ty");
            if (tyArr != null && tyArr.length() > 0) ty = tyArr.optDouble(0, ty);

            // botpose fields commonly named "botpose", "botpose_wpiblue", etc.
            if (results.has("botpose")) botpose = jsonArrayToDoubleArray(results.optJSONArray("botpose"));
            if (botpose == null) botpose = jsonArrayToDoubleArray(results.optJSONArray("botpose_wpiblue"));
            if (botpose == null) botpose = jsonArrayToDoubleArray(results.optJSONArray("botpose_wpired"));
        }

        // Heuristic: assume we “have target” if tx/ty are finite and results exists or tx != 0 recently.
        hasTarget = !(Double.isNaN(tx) || Double.isInfinite(tx));
    }

    public boolean hasTarget() { return hasTarget; }
    public double getTx() { return tx; }
    public double getTy() { return ty; }
    public double[] getBotpose() { return botpose; }

    // ---- internals ----

    private JSONObject fetchFirstAvailable() {
        for (String path : ENDPOINTS) {
            try {
                String s = httpGet(base + path, 80 /*ms*/);
                if (s == null || s.isEmpty()) continue;
                return new JSONObject(s);
            } catch (Exception e) {
                // try next
            }
        }
        Log.w(TAG, "No Limelight JSON endpoint responded.");
        return null;
    }

    private static String httpGet(String urlStr, int timeoutMs) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(timeoutMs);
            conn.setReadTimeout(timeoutMs);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            int code = conn.getResponseCode();
            if (code != 200) return null;

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder(512);
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            br.close();
            return sb.toString();
        } catch (Exception e) {
            return null;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private static double[] jsonArrayToDoubleArray(JSONArray arr) {
        if (arr == null) return null;
        double[] out = new double[arr.length()];
        for (int i = 0; i < arr.length(); i++) out[i] = arr.optDouble(i, 0.0);
        return out;
    }
}
