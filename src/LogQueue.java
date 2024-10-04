public class LogQueue {
    private Node front, rear;

    public LogQueue() {
        this.front = this.rear = null;
    }

    // Enqueue a log entry into the queue
    public void enqueue(LogEntry logEntry) {
        Node newNode = new Node(logEntry);
        if (this.rear == null) {
            this.front = this.rear = newNode;
        } else {
            this.rear.next = newNode;
            this.rear = newNode;
        }
    }

    // Dequeue an entry from the queue
    public LogEntry dequeue() {
        if (this.front == null) {
            return null; // Queue is empty
        }

        LogEntry logEntry = this.front.data;
        this.front = this.front.next;

        if (this.front == null) {
            this.rear = null;
        }

        return logEntry;
    }

    // Check if queue is empty
    public boolean isEmpty() {
        return this.front == null;
    }
}
