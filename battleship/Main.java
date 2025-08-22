package battleship;

import java.util.*;

public class Main {
    static final int N = 10;
    static final char WATER = '~', SHIP = 'O', HIT = 'X', MISS = 'M';

    static final String[] SHIP_NAMES = {
            "Aircraft Carrier", "Battleship", "Submarine", "Cruiser", "Destroyer"
    };
    static final int[] SHIP_SIZES = {5, 4, 3, 3, 2};

    static class Ship {
        final String name;
        final List<int[]> cells = new ArrayList<>();
        Ship(String name) { this.name = name; }
        boolean contains(int r, int c) {
            for (int[] p : cells) if (p[0] == r && p[1] == c) return true;
            return false;
        }
        boolean isSunk(char[][] field) {
            for (int[] p : cells) if (field[p[0]][p[1]] == SHIP) return false;
            return true;
        }
    }

    static class Player {
        char[][] field = new char[N][N];
        char[][] fog = new char[N][N];
        List<Ship> fleet = new ArrayList<>();
        String name;
        Player(String name) {
            this.name = name;
            for (char[] row : field) Arrays.fill(row, WATER);
            for (char[] row : fog) Arrays.fill(row, WATER);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");

        // Ship placement
        placeShips(sc, player1);
        promptEnterKey();
        placeShips(sc, player2);
        promptEnterKey();

        Player current = player1;
        Player opponent = player2;

        // Shooting loop
        while (true) {
            printBoards(current, opponent);
            System.out.printf("%s, it's your turn:%n", current.name);
            int[] shot;
            while (true) {
                String s = sc.next();
                shot = parse(s);
                if (shot == null) {
                    System.out.println("Error! You entered wrong coordinates! Try again:");
                    continue;
                }
                break;
            }
            int r = shot[0], c = shot[1];

            if (opponent.field[r][c] == SHIP) {
                opponent.field[r][c] = HIT;
                opponent.fog[r][c] = HIT;
                Ship hitShip = null;
                for (Ship sh : opponent.fleet) if (sh.contains(r, c)) { hitShip = sh; break; }
                boolean sunkNow = hitShip != null && hitShip.isSunk(opponent.field);
                boolean allSunkAfter = sunkNow && allSunk(opponent.fleet, opponent.field);

                printBoards(current, opponent);

                if (allSunkAfter) {
                    System.out.println("You sank the last ship. You won. Congratulations!");
                    break;
                } else if (sunkNow) {
                    System.out.println("You sank a ship!");
                } else {
                    System.out.println("You hit a ship!");
                }
            } else if (opponent.field[r][c] == WATER) {
                opponent.field[r][c] = MISS;
                opponent.fog[r][c] = MISS;
                printBoards(current, opponent);
                System.out.println("You missed!");
            } else { // Already hit/miss
                printBoards(current, opponent);
                System.out.println("You already shot here!");
            }

            promptEnterKey();
            // swap players
            Player temp = current;
            current = opponent;
            opponent = temp;
        }

        sc.close();
    }

    static void placeShips(Scanner sc, Player player) {
        System.out.printf("%s, place your ships on the game field%n", player.name);
        printField(player.field);
        for (int i = 0; i < SHIP_NAMES.length; i++) {
            final String shipName = SHIP_NAMES[i];
            final int shipSize = SHIP_SIZES[i];

            while (true) {
                System.out.printf("Enter the coordinates of the %s (%d cells):%n", shipName, shipSize);
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
                if (length != shipSize) {
                    System.out.printf("Error! Wrong length of the %s! Try again:%n", shipName);
                    continue;
                }
                if (!canPlace(player.field, rMin, rMax, cMin, cMax)) {
                    System.out.println("Error! You placed it too close to another one. Try again:");
                    continue;
                }

                Ship ship = new Ship(shipName);
                if (r1 == r2) {
                    for (int c = cMin; c <= cMax; c++) {
                        player.field[r1][c] = SHIP;
                        ship.cells.add(new int[]{r1, c});
                    }
                } else {
                    for (int r = rMin; r <= rMax; r++) {
                        player.field[r][c1] = SHIP;
                        ship.cells.add(new int[]{r, c1});
                    }
                }
                player.fleet.add(ship);
                printField(player.field);
                break;
            }
        }
    }

    static void printBoards(Player current, Player opponent) {
        // opponent's fog at top, current's field at bottom
        printField(opponent.fog);
        System.out.println("---------------------");
        printField(current.field);
    }

    static void promptEnterKey() {
        System.out.println("Press Enter and pass the move to another player");
        try { System.in.read(); } catch (Exception e) {}
        System.out.print("\033[H\033[2J"); // Clear screen
        System.out.flush();
    }

    static boolean canPlace(char[][] f, int rMin, int rMax, int cMin, int cMax) {
        for (int r = rMin; r <= rMax; r++) {
            for (int c = cMin; c <= cMax; c++) {
                for (int dr = -1; dr <= 1; dr++)
                    for (int dc = -1; dc <= 1; dc++) {
                        int nr = r + dr, nc = c + dc;
                        if (nr >= 0 && nr < N && nc >= 0 && nc < N && f[nr][nc] == SHIP) return false;
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
        int col;
        try { col = Integer.parseInt(num); } catch (Exception e) { return null; }
        if (col < 1 || col > 10) return null;
        return new int[]{rowCh - 'A', col - 1};
    }

    static boolean allSunk(List<Ship> fleet, char[][] field) {
        for (Ship s : fleet) if (!s.isSunk(field)) return false;
        return true;
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
