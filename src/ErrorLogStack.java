public class ErrorLogStack {
    private Node top;

    public ErrorLogStack() {
        this.top = null;
    }

    // Push an error log entry onto stack
    public void push(LogEntry logEntry) {
        Node newNode = new Node(logEntry);
        newNode.next = top;
        top = newNode;
    }

    // Pop top error log entry from stack
    public LogEntry pop() {
        if (top == null) {
            return null; // Stack empty
        }

        LogEntry logEntry = top.data;
        top = top.next;
        return logEntry;
    }

    // Check if the stack is empty
    public boolean isEmpty() {
        return top == null;
    }
}
