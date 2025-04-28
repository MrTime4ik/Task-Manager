public enum TaskStatus {
    DONE("done"),
    IN_PROGRESS("in-progress"),
    TODO("todo");

    private final String status;

    public String getStatus() {
        return this.status;
    }

    public static TaskStatus getStatusObj(String strStatus) {
        return switch (strStatus) {
            case "in-progress" -> TaskStatus.IN_PROGRESS;
            case "done" -> TaskStatus.DONE;
            default -> TaskStatus.TODO;
        };
    }

    TaskStatus(String status) {
        this.status = status;
    }
}