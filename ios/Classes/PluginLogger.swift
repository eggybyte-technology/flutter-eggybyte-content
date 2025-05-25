import Foundation
import os.log

/**
 * Unified logging utility for the eggybyte_content plugin.
 *
 * This class provides a centralized logging mechanism with different log levels,
 * consistent formatting, and the ability to filter logs based on build configuration.
 * All logging throughout the plugin should use this class to ensure consistency
 * and easier debugging.
 */
public class PluginLogger {
    
    /// The subsystem identifier for the plugin logs
    private static let subsystem = "com.eggybyte.content"
    
    /// Logger instances for different categories
    private static let generalLogger = OSLog(subsystem: subsystem, category: "General")
    private static let sdkLogger = OSLog(subsystem: subsystem, category: "SDK")
    private static let platformViewLogger = OSLog(subsystem: subsystem, category: "PlatformView")
    private static let communicationLogger = OSLog(subsystem: subsystem, category: "Communication")
    
    /// Plugin tag for consistent log formatting
    private static let pluginTag = "[EggybyteContent]"
    
    // MARK: - General Logging Methods
    
    /**
     * Logs a debug message.
     *
     * Debug messages are only visible in debug builds and provide detailed
     * information for development and troubleshooting.
     *
     * @param message The message to log.
     * @param category The log category (default: general).
     * @param functionName The calling function name (automatically captured).
     * @param fileName The calling file name (automatically captured).
     * @param lineNumber The calling line number (automatically captured).
     */
    public static func debug(
        _ message: String,
        category: LogCategory = .general,
        functionName: String = #function,
        fileName: String = #file,
        lineNumber: Int = #line
    ) {
        #if DEBUG
        let logger = getLogger(for: category)
        let formattedMessage = formatMessage(message, level: "DEBUG", functionName: functionName, fileName: fileName, lineNumber: lineNumber)
        
        // Log to console
        printToConsole(formattedMessage, level: "DEBUG")
        
        // Log to system logger
        os_log("%{public}@", log: logger, type: .debug, formattedMessage)
        #endif
    }
    
    /**
     * Logs an informational message.
     *
     * Info messages provide general information about plugin operations
     * and are visible in both debug and release builds.
     *
     * @param message The message to log.
     * @param category The log category (default: general).
     * @param functionName The calling function name (automatically captured).
     * @param fileName The calling file name (automatically captured).
     * @param lineNumber The calling line number (automatically captured).
     */
    public static func info(
        _ message: String,
        category: LogCategory = .general,
        functionName: String = #function,
        fileName: String = #file,
        lineNumber: Int = #line
    ) {
        let logger = getLogger(for: category)
        let formattedMessage = formatMessage(message, level: "INFO", functionName: functionName, fileName: fileName, lineNumber: lineNumber)
        
        // Log to console
        printToConsole(formattedMessage, level: "INFO")
        
        // Log to system logger
        os_log("%{public}@", log: logger, type: .info, formattedMessage)
    }
    
    /**
     * Logs a warning message.
     *
     * Warning messages indicate potential issues that don't prevent
     * operation but should be addressed.
     *
     * @param message The message to log.
     * @param category The log category (default: general).
     * @param functionName The calling function name (automatically captured).
     * @param fileName The calling file name (automatically captured).
     * @param lineNumber The calling line number (automatically captured).
     */
    public static func warning(
        _ message: String,
        category: LogCategory = .general,
        functionName: String = #function,
        fileName: String = #file,
        lineNumber: Int = #line
    ) {
        let logger = getLogger(for: category)
        let formattedMessage = formatMessage(message, level: "WARNING", functionName: functionName, fileName: fileName, lineNumber: lineNumber)
        
        // Log to console
        printToConsole(formattedMessage, level: "WARNING")
        
        // Log to system logger
        os_log("%{public}@", log: logger, type: .default, formattedMessage)
    }
    
    /**
     * Logs an error message.
     *
     * Error messages indicate serious issues that affect plugin functionality.
     *
     * @param message The message to log.
     * @param error Optional error object for additional context.
     * @param category The log category (default: general).
     * @param functionName The calling function name (automatically captured).
     * @param fileName The calling file name (automatically captured).
     * @param lineNumber The calling line number (automatically captured).
     */
    public static func error(
        _ message: String,
        error: Error? = nil,
        category: LogCategory = .general,
        functionName: String = #function,
        fileName: String = #file,
        lineNumber: Int = #line
    ) {
        let logger = getLogger(for: category)
        var fullMessage = message
        if let error = error {
            fullMessage += " | Error: \(error.localizedDescription)"
        }
        let formattedMessage = formatMessage(fullMessage, level: "ERROR", functionName: functionName, fileName: fileName, lineNumber: lineNumber)
        
        // Log to console
        printToConsole(formattedMessage, level: "ERROR")
        
        // Log to system logger
        os_log("%{public}@", log: logger, type: .error, formattedMessage)
    }
    
    // MARK: - Category-Specific Logging Methods
    
    /**
     * Logs SDK-related messages.
     */
    public static func sdk(_ message: String, level: LogLevel = .info) {
        logWithLevel(message, level: level, category: .sdk)
    }
    
    /**
     * Logs platform view related messages.
     */
    public static func platformView(_ message: String, level: LogLevel = .info) {
        logWithLevel(message, level: level, category: .platformView)
    }
    
    /**
     * Logs communication-related messages.
     */
    public static func communication(_ message: String, level: LogLevel = .info) {
        logWithLevel(message, level: level, category: .communication)
    }
    
    // MARK: - Private Helper Methods
    
    /**
     * Prints a message to the console with timestamp.
     */
    private static func printToConsole(_ message: String, level: String) {
        let timestamp = getCurrentTimestamp()
        let consoleMessage = "\(timestamp) [\(level)] \(message)"
        print(consoleMessage)
        
        // Force flush the console output to ensure immediate display
        fflush(stdout)
    }
    
    /**
     * Gets the current timestamp in HH:mm:ss.SSS format.
     */
    private static func getCurrentTimestamp() -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm:ss.SSS"
        return formatter.string(from: Date())
    }
    
    /**
     * Formats a log message with consistent structure.
     */
    private static func formatMessage(
        _ message: String,
        level: String,
        functionName: String,
        fileName: String,
        lineNumber: Int
    ) -> String {
        let shortFileName = (fileName as NSString).lastPathComponent
        return "\(pluginTag) \(shortFileName):\(lineNumber) \(functionName) - \(message)"
    }
    
    /**
     * Gets the appropriate logger for a category.
     */
    private static func getLogger(for category: LogCategory) -> OSLog {
        switch category {
        case .general:
            return generalLogger
        case .sdk:
            return sdkLogger
        case .platformView:
            return platformViewLogger
        case .communication:
            return communicationLogger
        }
    }
    
    /**
     * Logs a message with specified level and category.
     */
    private static func logWithLevel(
        _ message: String,
        level: LogLevel,
        category: LogCategory,
        functionName: String = #function,
        fileName: String = #file,
        lineNumber: Int = #line
    ) {
        switch level {
        case .debug:
            debug(message, category: category, functionName: functionName, fileName: fileName, lineNumber: lineNumber)
        case .info:
            info(message, category: category, functionName: functionName, fileName: fileName, lineNumber: lineNumber)
        case .warning:
            warning(message, category: category, functionName: functionName, fileName: fileName, lineNumber: lineNumber)
        case .error:
            error(message, category: category, functionName: functionName, fileName: fileName, lineNumber: lineNumber)
        }
    }
}

// MARK: - Supporting Enums

/**
 * Log categories for organizing different types of log messages.
 */
public enum LogCategory {
    case general
    case sdk
    case platformView
    case communication
}

/**
 * Log levels for controlling message importance and visibility.
 */
public enum LogLevel {
    case debug
    case info
    case warning
    case error
} 