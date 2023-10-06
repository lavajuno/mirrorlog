package src.test;
import org.junit.jupiter.api.Test;
import org.lavajuno.mirrorlog.io.LogEntry;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestLogEntry {
    @Test
    public void testLog1() {
        System.out.println("Testing log entry:");
        System.out.println(new LogEntry("MirrorDispatch", 0, "Configuration update requested by user."));
        System.out.println(new LogEntry("MirrorDispatch", 0, "Asking MirrorAPI to update configuration..."));
        System.out.println(new LogEntry("MirrorAPI", 1, "Configuration has been updated, but there is a syntax error in mirrors.json. Keeping old configuration."));
        System.out.println(new LogEntry("MirrorDispatch", 2, "Failed to update MirrorAPI configuration."));
        System.out.println(new LogEntry("MirrorDispatch", 0, "Asking MirrorDiscord to report the error..."));
        System.out.println(new LogEntry("MirrorDiscord", 0, "Sending error notification..."));
        System.out.println(new LogEntry("MirrorDiscord", 0, "Sent error notification."));
    }


}
