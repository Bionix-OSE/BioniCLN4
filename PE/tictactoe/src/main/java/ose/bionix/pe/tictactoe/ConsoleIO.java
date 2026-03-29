package ose.bionix.pe.tictactoe;

public class ConsoleIO implements IO {
    private final java.util.Scanner scanner;

    public ConsoleIO() {
        scanner = new java.util.Scanner(System.in);
    }

    @Override
    public void print(String s) { System.out.print(s); }

    @Override
    public void println(String s) { System.out.println(s); }

    @Override
    public void println() { System.out.println(); }

    @Override
    public int promptCell() {
        if (!scanner.hasNextInt()) {
            scanner.nextLine();
            return -1;
        }
        int pick = scanner.nextInt();
        return pick - 1;
    }

    @Override
    public void close() {
        try { scanner.close(); } catch (Exception e) { }
    }
}
