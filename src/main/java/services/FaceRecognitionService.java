package services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FaceRecognitionService {

    public static class FaceResult {
        private final boolean success;
        private final String message;
        private final double[] embedding;
        private final double distance;

        public FaceResult(boolean success, String message, double[] embedding, double distance) {
            this.success = success;
            this.message = message;
            this.embedding = embedding;
            this.distance = distance;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public double[] getEmbedding() {
            return embedding;
        }

        public double getDistance() {
            return distance;
        }

        @Override
        public String toString() {
            return "FaceResult{" +
                   "success=" + success +
                   ", message='" + message + '\'' +
                   ", distance=" + distance +
                   '}';
        }
    }

    private final String pythonPath;
    private final String scriptPath;
    private String lastError;

    public FaceRecognitionService() {
        this(null, null);
    }

    public FaceRecognitionService(String pythonPath, String scriptPath) {
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
            ProcessBuilder pb = new ProcessBuilder(pythonPath, "-c", "import face_recognition, cv2, numpy; print('OK')");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            String output = readAll(process);
            int exitCode = process.waitFor();

            if (exitCode != 0 || !output.contains("OK")) {
                lastError = output.isBlank() ? "face_recognition indisponible" : output.trim();
                return false;
            }
            return true;
        } catch (Exception e) {
            lastError = e.getMessage();
            return false;
        }
    }

    public FaceResult enrollWithWebcam(int durationSeconds) {
        if (pythonPath == null) {
            return new FaceResult(false, "Python introuvable", null, -1);
        }
        if (scriptPath == null) {
            return new FaceResult(false, "Script face_recognition_service.py introuvable", null, -1);
        }

        List<String> command = new ArrayList<>();
        command.add(pythonPath);
        command.add(scriptPath);
        command.add("enroll");
        command.add(String.valueOf(durationSeconds));

        return runCommand(command);
    }

    public FaceResult verifyWithWebcam(double[] storedEmbedding, int durationSeconds, double threshold) {
        if (pythonPath == null) {
            return new FaceResult(false, "Python introuvable", null, -1);
        }
        if (scriptPath == null) {
            return new FaceResult(false, "Script face_recognition_service.py introuvable", null, -1);
        }
        if (storedEmbedding == null || storedEmbedding.length == 0) {
            return new FaceResult(false, "Aucun embedding stocke", null, -1);
        }

        try {
            Path tempEmbedding = Files.createTempFile("tahwissa_face_", ".json");
            Files.writeString(tempEmbedding, toJsonArray(storedEmbedding), StandardCharsets.UTF_8);

            List<String> command = new ArrayList<>();
            command.add(pythonPath);
            command.add(scriptPath);
            command.add("verify");
            command.add(tempEmbedding.toString());
            command.add(String.valueOf(durationSeconds));
            command.add(String.valueOf(threshold));

            FaceResult result = runCommand(command);
            Files.deleteIfExists(tempEmbedding);
            return result;
        } catch (Exception e) {
            return new FaceResult(false, "Erreur: " + e.getMessage(), null, -1);
        }
    }

    private FaceResult runCommand(List<String> command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            List<String> lines = readAllLines(process);
            int exitCode = process.waitFor();

            String jsonLine = findJsonLine(lines);
            if (jsonLine == null) {
                String raw = String.join("\n", lines);
                String msg = raw.isBlank() ? "Reponse vide du script" : "Reponse invalide du script";
                return new FaceResult(false, msg, null, -1);
            }

            JsonObject json = JsonParser.parseString(jsonLine).getAsJsonObject();
            boolean success = json.has("success") && json.get("success").getAsBoolean();
            String message = json.has("message") ? json.get("message").getAsString() : "";
            double distance = json.has("distance") ? json.get("distance").getAsDouble() : -1;

            double[] embedding = null;
            if (json.has("embedding") && json.get("embedding").isJsonArray()) {
                JsonArray array = json.get("embedding").getAsJsonArray();
                embedding = new double[array.size()];
                for (int i = 0; i < array.size(); i++) {
                    embedding[i] = array.get(i).getAsDouble();
                }
            }

            if (!success && exitCode == 0 && message.isBlank()) {
                message = "Verification echouee";
            }

            return new FaceResult(success, message, embedding, distance);
        } catch (Exception e) {
            return new FaceResult(false, "Erreur: " + e.getMessage(), null, -1);
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
        Path script = projectRoot.resolve("scripts").resolve("face_recognition_service.py");
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

    private String toJsonArray(double[] values) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(values[i]);
        }
        sb.append(']');
        return sb.toString();
    }
}
