import java.io.*;
import java.text.ParseException;
import java.util.*;

public class TaskJsonHandler implements ITaskHandler {

    private static final File dir = new File("D:\\tm_data");
    private static final File file = new File(dir, "task.json");

    @Override
    public boolean save(Task task) {
        String fields = task.toString();
        ArrayList<String> fileLines = fileToLines().orElse(new ArrayList<>());
        fileLines.add(fields);
        return linesToFile(fileLines);
    }

    @Override
    public Task load(int id) {

       ArrayList<String> lines = fileToLines().orElse(new ArrayList<>());
       if (lines.isEmpty()) {
           return new Task("null");
       }

        for (String line : lines) {

           ArrayList<String> currentTaskData = new ArrayList<>(List.of(line.split("; ")));
           int taskID = Integer.parseInt(currentTaskData.get(0).split("=")[1]);
           if (taskID == id) {

               String description = currentTaskData.get(1).split("=")[1];
               TaskStatus status = TaskStatus.getStatusObj(currentTaskData.get(2).split("=")[1]);
               Date createdAt;
               Date updatedAt;

               try {
                   createdAt = Task.formatter.parse(currentTaskData.get(3).split("=")[1]);
                   updatedAt = Task.formatter.parse(currentTaskData.get(4).split("=")[1]);
               } catch (ParseException e) {
                   throw new RuntimeException(e);
               }
               return new Task(id, description, status, createdAt, updatedAt);
           }
       }
        return new Task("null");
    }

    @Override
    public boolean update(int id, String description) {

        ArrayList<String> lines = fileToLines().orElse(new ArrayList<>());
        if (lines.isEmpty()) {
            return false;
        }

        for (int i = 0; i < lines.size(); i++) {
            ArrayList<String> lineData = new ArrayList<>(List.of(lines.get(i).split("; ")));
            int taskID = Integer.parseInt(lineData.getFirst().split("=")[1]);
            if (taskID == id) {

                lineData.set(1, String.format("description=%s", description));
                lineData.set(4, String.format("updatedAt=%s", Task.formatter.format(new Date())));
                lines.set(i, String.join("; ", lineData));


                linesToFile(lines);
                return true;
            }

        }
        return false;
    }

    @Override
    public boolean delete(int id) {

        ArrayList<String> lines = fileToLines().orElse(new ArrayList<>());
        if (lines.isEmpty()) {
            return false;
        }
        for (int i = 0; i < lines.size(); i++) {
            if (Integer.parseInt(lines.get(i).split("; ")[0].split("=")[1]) == id && lines.get(i).split("; ")[0].split("=")[0].equals("id")) {
                lines.remove(i);
                if (linesToFile(lines)) {
                    return true;
                }
                break;
            }
        }
        return false;
    }

    @Override
    public boolean mark(int id, TaskStatus status) {

        ArrayList<String> lines = fileToLines().orElse(new ArrayList<>());
        if (lines.isEmpty()) {
            return false;
        }

        for (int i = 0; i < lines.size(); i++) {
            ArrayList<String> lineData = new ArrayList<>(List.of(lines.get(i).split("; ")));
            int taskID = Integer.parseInt(lineData.getFirst().split("=")[1]);
            if (taskID == id) {
                lineData.set(2, String.format("status=%s", status.getStatus()));
                lines.set(i, String.join("; ", lineData));

                linesToFile(lines);
                return true;
            }
        }
        return false;
    }

    @Override
    public ArrayList<Task> loadAllTasks() {

        ArrayList<Task> allTasks = new ArrayList<>();
        ArrayList<Integer> idList = getAllIDs();
        if (idList.isEmpty()) {
            return new ArrayList<>();
        }
        for (int id : idList) {
            Task task = load(id);
            if (task.getDescription().equals("null")) {
                continue;
            }
            allTasks.add(task);
        }
        return allTasks;
    }

    @Override
    public ArrayList<Task> loadTasksByStatus(TaskStatus status) {

        ArrayList<Task> allTasks = new ArrayList<>();
        ArrayList<Integer> idList = getIDByStatus(status);

        for (int id : idList) {
            Task task = load(id);
            if (task.getDescription().equals("null")) {
                continue;
            }
            allTasks.add(task);
        }
        return allTasks;
    }

    @Override
    public int getNextID() {
        ArrayList<Integer> idList = getAllIDs();
        if (idList.isEmpty()) {
            return 1;
        }
        return Collections.max(idList) + 1;
    }

    private ArrayList<Integer> getAllIDs() {
        ArrayList<String> fileLines = fileToLines().orElse(new ArrayList<>());

        if (fileLines.isEmpty()) {
            return new ArrayList<>();
        }

        ArrayList<Integer> idList = new ArrayList<>(fileLines.size());

        for (String line : fileLines) {
            idList.add(Integer.parseInt(line.split("; ")[0].split("=")[1]));
        }
        return idList;
    }

    private ArrayList<Integer> getIDByStatus(TaskStatus status) {
        ArrayList<String> fileLines = fileToLines().orElse(new ArrayList<>());

        ArrayList<Integer> idList = new ArrayList<>(fileLines.size());

        for (String line : fileLines) {
            if (line.split("; ")[2].split("=")[1].equals(status.getStatus())) {
                idList.add(Integer.parseInt(line.split("; ")[0].split("=")[1]));
            }
        }
        return idList;
    }

    private static Optional<ArrayList<String>> fileToLines() {
        try (FileReader reader = new FileReader(file)) {

            List<Character> charList = new ArrayList<>();
            int fileChar;

            while ((fileChar=reader.read())!=-1) {
                charList.add((char) fileChar);
            }

            StringBuilder fileDataBuilder = new StringBuilder();
            for (char c : charList) {
                fileDataBuilder.append(c);
            }

            String fileData = fileDataBuilder.toString();

            return Optional.of(new ArrayList<>(List.of(fileData.split("\r\n"))));

        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private static boolean linesToFile(ArrayList<String> fileLines) {
        try (FileWriter writer = new FileWriter(file)) {

            StringBuilder dataBuilder = new StringBuilder();
            for (String s : fileLines) {
                dataBuilder.append(s);
                dataBuilder.append("\r\n");
            }

            String data = dataBuilder.toString();

            writer.write(data);

            return true;

        } catch (IOException e) {
            return false;
        }
    }
}