package battleship;

import java.util.*;

public class Main {
    static final int N = 10;
    static final char WATER = '~', SHIP = 'O';

    static String[] shipNames = {
            "Aircraft Carrier", "Battleship", "Submarine", "Cruiser", "Destroyer"
    };
    static int[] shipSizes = {5, 4, 3, 3, 2};

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        char[][] field = new char[N][N];
        char[][] fog = new char[N][N];
        for (char[] row : field) Arrays.fill(row, WATER);
        for (char[] row : fog) Arrays.fill(row, WATER);

        printField(field);

        // Place all ships
        for (int i = 0; i < shipNames.length; i++) {
            while (true) {
                System.out.printf("Enter the coordinates of the %s (%d cells):%n",
                        shipNames[i], shipSizes[i]);
                String a = sc.next(), b = sc.next();
                int[] p1 = parse(a), p2 = parse(b);
                if (p1 == null || p2 == null) {
                    System.out.println("Error! Wrong coordinates! Try again:");
                    continue;
                }

                int r1 = p1[0], c1 = p1[1], r2 = p2[0], c2 = p2[1];
                if (r1 != r2 && c1 != c2) {
                    System.out.println("Error! Wrong ship location! Try again:");
                    continue;
                }

                int rMin = Math.min(r1, r2), rMax = Math.max(r1, r2);
                int cMin = Math.min(c1, c2), cMax = Math.max(c1, c2);
                int length = (r1 == r2) ? (cMax - cMin + 1) : (rMax - rMin + 1);

                if (length != shipSizes[i]) {
                    System.out.printf("Error! Wrong length of the %s! Try again:%n", shipNames[i]);
                    continue;
                }

                if (!canPlace(field, rMin, rMax, cMin, cMax)) {
                    System.out.println("Error! You placed it too close to another one. Try again:");
                    continue;
                }

                if (r1 == r2) {
                    for (int c = cMin; c <= cMax; c++) field[r1][c] = SHIP;
                } else {
                    for (int r = rMin; r <= rMax; r++) field[r][c1] = SHIP;
                }

                printField(field);
                break;
            }
        }

        System.out.println("The game starts!");
        printField(fog); // show fog at start

        // Shooting stage
        while (true) {
            System.out.println("Take a shot!");
            String shot = sc.next();
            int[] p = parse(shot);

            if (p == null) {
                System.out.println("Error! You entered wrong coordinates! Try again:");
                continue;
            }

            int r = p[0], c = p[1];
            if (field[r][c] == SHIP) {
                field[r][c] = 'X';
                fog[r][c] = 'X';
                printField(fog);
                System.out.println("You hit a ship!");
                printField(field);
            } else if (field[r][c] == WATER) {
                field[r][c] = 'M';
                fog[r][c] = 'M';
                printField(fog);
                System.out.println("You missed!");
                printField(field);
            } else {
                printField(fog);
                System.out.println("You missed!");
                printField(field);
            }
            break; // stop after one shot
        }

        sc.close();
    }

    static boolean canPlace(char[][] f, int rMin, int rMax, int cMin, int cMax) {
        for (int r = rMin; r <= rMax; r++) {
            for (int c = cMin; c <= cMax; c++) {
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int nr = r + dr, nc = c + dc;
                        if (nr >= 0 && nr < N && nc >= 0 && nc < N) {
                            if (f[nr][nc] == SHIP) return false;
                        }
                    }
                }
            }
        }
        return true;
    }

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
