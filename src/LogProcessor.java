import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;

public class LogProcessor {
    private static int infoCount = 0;
    private static int warnCount = 0;
    private static int errorCount = 0;
    private static int memoryWarnCount = 0;
    private static ArrayDeque<LogEntry> recentErrors = new ArrayDeque<>(100); // Circular buffer for last 100 errors

    //read the log file and enqueue all entries
    public static void readLogFileAndEnqueue(LogQueue logQueue, String filePath) {
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                // Split the log entry using regex to match the format: [timestamp] LEVEL message
                String regex = "\\[(.*?)\\] (\\w+) (.*)";
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
                java.util.regex.Matcher matcher = pattern.matcher(line);

                if (matcher.matches()) {
                    String timestamp = matcher.group(1);  // First group is the timestamp
                    String logLevel = matcher.group(2);  // Second group is the log level (INFO, WARN, ERROR)
                    String message = matcher.group(3);   // Third group is the message

                    LogEntry logEntry = new LogEntry(timestamp, logLevel, message);
                    logQueue.enqueue(logEntry);
                } else {
                    System.out.println("Malformed log entry: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the log file: " + e.getMessage());
        }
    }

    //dequeue all entries, count log levels, track errors, and check for memory warnings
    public static void processLogs(LogQueue logQueue, ErrorLogStack errorStack) {
        while (!logQueue.isEmpty()) {
            LogEntry logEntry = logQueue.dequeue();
            String logLevel = logEntry.getLogLevel();
            String message = logEntry.getMessage();

            // Count log levels
            switch (logLevel) {
                case "INFO":
                    infoCount++;
                    break;
                case "WARN":
                    warnCount++;
                    if (message.contains("Memory")) {
                        memoryWarnCount++;
                    }
                    break;
                case "ERROR":
                    errorCount++;
                    // Track the last 100 errors using a circular buffer
                    if (recentErrors.size() == 100) {
                        recentErrors.poll(); // Remove oldest error
                    }
                    recentErrors.offer(logEntry); // Add new error

                    // Push error log onto the stack
                    errorStack.push(logEntry);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        LogQueue logQueue = new LogQueue();
        ErrorLogStack errorStack = new ErrorLogStack();

        // Path to the log file

        // Step 1: Read the log file and enqueue all log entries into the queue
        readLogFileAndEnqueue(logQueue, logFilePath);

        // Step 2: Process log entries, count log levels, track errors, and check for memory warnings
        processLogs(logQueue, errorStack);

        // Output the results
        System.out.println("Log Level Counts:");
        System.out.println("INFO: " + infoCount);
        System.out.println("WARN: " + warnCount);
        System.out.println("ERROR: " + errorCount);

        System.out.println("\nMemory Warnings (containing 'Memory'): " + memoryWarnCount);

        System.out.println("\nRecent 100 Errors:");
        for (LogEntry errorLog : recentErrors) {
            System.out.println(errorLog);
        }

        // Print all errors in LIFO order from the stack
        System.out.println("\nError Logs in LIFO order (from stack):");
        while (!errorStack.isEmpty()) {
            System.out.println(errorStack.pop());
        }
    }
}
