package battleship;

import java.util.*;

public class Main {
    static final int N = 10;
    static final char WATER = '~', SHIP = 'O';

    public static void main(String[] args) {
        char[][] field = new char[N][N];
        for (char[] row : field) Arrays.fill(row, WATER);

        printField(field);

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the coordinates of the ship:");
        String a = sc.next(), b = sc.next();
        int[] p1 = parse(a), p2 = parse(b);

        if (p1 == null || p2 == null) {
            System.out.println("Error!");
            return;
        }
        int r1 = p1[0], c1 = p1[1], r2 = p2[0], c2 = p2[1];

        // same row or same column only
        if (r1 != r2 && c1 != c2) {
            System.out.println("Error");
            return;
        }

        // normalize order (min..max)
        int rMin = Math.min(r1, r2), rMax = Math.max(r1, r2);
        int cMin = Math.min(c1, c2), cMax = Math.max(c1, c2);

        // place ship
        if (r1 == r2) {
            for (int c = cMin; c <= cMax; c++) field[r1][c] = SHIP;
        } else {
            for (int r = rMin; r <= rMax; r++) field[r][c1] = SHIP;
        }

        // print updated field
        printField(field);

        // report length and parts
        List<String> parts = new ArrayList<>();
        if (r1 == r2) {
            for (int c = cMin; c <= cMax; c++) parts.add(fmt(r1, c));
        } else {
            for (int r = rMin; r <= rMax; r++) parts.add(fmt(r, c1));
        }
        System.out.println("Length: " + parts.size() + "  Parts: " + String.join(" ", parts));
    }

    // Parse like "A1", "J10" → [row, col] (0-based). Returns null if invalid.
    static int[] parse(String s) {
        s = s.trim().toUpperCase();
        if (s.length() < 2) return null;
        char rowCh = s.charAt(0);
        if (rowCh < 'A' || rowCh > 'J') return null;
        String num = s.substring(1);
        if (!num.matches("\\d{1,2}")) return null;
        int col = Integer.parseInt(num);
        if (col < 1 || col > 10) return null;
        return new int[]{rowCh - 'A', col - 1};
    }

    static String fmt(int r, int c) { return "" + (char)('A' + r) + (c + 1); }

    static void printField(char[][] f) {
        System.out.print("  ");
        for (int c = 1; c <= N; c++) System.out.print(c + (c == 10 ? "" : " "));
        System.out.println();
        for (int r = 0; r < N; r++) {
            System.out.print((char) ('A' + r) + " ");
            for (int c = 0; c < N; c++) {
                System.out.print(f[r][c]);
                if (c < N - 1) System.out.print(" ");
            }
            System.out.println();
        }
    }
}
