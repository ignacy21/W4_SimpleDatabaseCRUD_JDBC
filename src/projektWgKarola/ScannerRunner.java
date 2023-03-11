package projektWgKarola;

import java.util.Scanner;

public class ScannerRunner {

    public static void main(String[] args) {
        System.out.println("Write command");

        CommandBuilder commandBuilder = new CommandBuilder();
        DatabaseRunner databaseRunner = new DatabaseRunner();
        Scanner scanner = new Scanner(System.in);

        while(scanner.hasNext()) {
            String stringCommand = scanner.nextLine();

            commandBuilder.buildCommand(stringCommand)
                    .ifPresent(databaseRunner::run);
        }
    }
}
