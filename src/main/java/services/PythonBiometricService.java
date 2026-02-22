package services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PythonBiometricService {

    public static class VerificationResult {
        private final boolean success;
        private final String message;
        private final int faceCount;
        private final String imagePath;
        private final String rawJson;

        public VerificationResult(boolean success, String message, int faceCount, String imagePath, String rawJson) {
            this.success = success;
            this.message = message;
            this.faceCount = faceCount;
            this.imagePath = imagePath;
            this.rawJson = rawJson;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public int getFaceCount() {
            return faceCount;
        }

        public String getImagePath() {
            return imagePath;
        }

        public String getRawJson() {
            return rawJson;
        }

        @Override
        public String toString() {
            return "VerificationResult{" +
                   "success=" + success +
                   ", message='" + message + '\'' +
                   ", faceCount=" + faceCount +
                   ", imagePath='" + imagePath + '\'' +
                   '}';
        }
    }

    private final String pythonPath;
    private final String scriptPath;
    private String lastError;

    public PythonBiometricService() {
        this(null, null);
    }

    public PythonBiometricService(String pythonPath, String scriptPath) {
        this.pythonPath = pythonPath != null && !pythonPath.isBlank() ? pythonPath : resolvePythonPath();
        this.scriptPath = scriptPath != null && !scriptPath.isBlank() ? scriptPath : resolveScriptPath();
    }

    public String getLastError() {
        return lastError;
    }

    public boolean isPythonAvailable() {
        if (pythonPath == null) {
            lastError = "Python introuvable (TAHWISSA_PYTHON ou venv_tahwissa)";
            return false;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder(pythonPath, "-c", "import cv2; print('OK')");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            String output = readAll(process);
            int exitCode = process.waitFor();

            if (exitCode != 0 || !output.contains("OK")) {
                lastError = output.isBlank() ? "OpenCV non disponible" : output.trim();
                return false;
            }

            return true;
        } catch (Exception e) {
            lastError = e.getMessage();
            return false;
        }
    }

    public VerificationResult verifyWithWebcam(int durationSeconds, String savePath) {
        if (pythonPath == null) {
            return new VerificationResult(false, "Python introuvable", 0, null, null);
        }
        if (scriptPath == null) {
            return new VerificationResult(false, "Script human_verification.py introuvable", 0, null, null);
        }

        List<String> command = new ArrayList<>();
        command.add(pythonPath);
        command.add(scriptPath);
        command.add("webcam");
        command.add(String.valueOf(durationSeconds));
        if (savePath != null && !savePath.isBlank()) {
            command.add(savePath);
        }

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            List<String> lines = readAllLines(process);
            int exitCode = process.waitFor();

            String jsonLine = findJsonLine(lines);
            if (jsonLine == null) {
                String raw = String.join("\n", lines);
                String msg = raw.isBlank() ? "Réponse vide du script" : "Réponse invalide du script";
                return new VerificationResult(false, msg, 0, null, null);
            }

            JsonObject json = JsonParser.parseString(jsonLine).getAsJsonObject();
            boolean success = json.has("success") && json.get("success").getAsBoolean();
            String message = json.has("message") ? json.get("message").getAsString() : "";
            int faceCount = json.has("face_count") ? json.get("face_count").getAsInt() : 0;
            String imagePath = json.has("image_path") && !json.get("image_path").isJsonNull()
                    ? json.get("image_path").getAsString()
                    : null;

            if (!success && exitCode == 0) {
                message = message.isBlank() ? "Vérification échouée" : message;
            }

            return new VerificationResult(success, message, faceCount, imagePath, jsonLine);
        } catch (Exception e) {
            return new VerificationResult(false, "Erreur: " + e.getMessage(), 0, null, null);
        }
    }

    private String resolvePythonPath() {
        String envPython = System.getenv("TAHWISSA_PYTHON");
        if (envPython != null && !envPython.isBlank() && Files.exists(Paths.get(envPython))) {
            return envPython;
        }

        String propPython = System.getProperty("tahwissa.python");
        if (propPython != null && !propPython.isBlank() && Files.exists(Paths.get(propPython))) {
            return propPython;
        }

        Path projectRoot = Paths.get(System.getProperty("user.dir"));
        Path venvPython = projectRoot.resolve("venv_tahwissa").resolve("Scripts").resolve("python.exe");
        if (Files.exists(venvPython)) {
            return venvPython.toString();
        }

        return "python";
    }

    private String resolveScriptPath() {
        Path projectRoot = Paths.get(System.getProperty("user.dir"));
        Path script = projectRoot.resolve("scripts").resolve("human_verification.py");
        if (Files.exists(script)) {
            return script.toString();
        }
        return null;
    }

    private String readAll(Process process) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    private List<String> readAllLines(Process process) throws Exception {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    private String findJsonLine(List<String> lines) {
        for (int i = lines.size() - 1; i >= 0; i--) {
            String line = lines.get(i).trim();
            if (line.startsWith("{") && line.contains("\"success\"")) {
                return line;
            }
        }
        return null;
    }
}

