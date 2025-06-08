package my.project;

import java.util.*;

public class LiquidSortConsole {

    static int N, V;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Пример корректного ввода:\n" +
                "Введите количество пробирок (N): 5\n" +
                "Введите объем одной пробирки (V): 3\n" +
                "Введите содержимое пробирок по строкам (снизу вверх). Используйте '-' для пустых ячеек.\n" +
                "Пробирка 0: A B A\n" +
                "Пробирка 1: B A B\n" +
                "Пробирка 2: - - -\n" +
                "Пробирка 3: - - -\n" +
                "Пробирка 4: - - -\n ");
        System.out.print("Введите количество пробирок (N): ");
        N = scanner.nextInt();
        System.out.print("Введите объем одной пробирки (V): ");
        V = scanner.nextInt();
        scanner.nextLine(); // переход на новую строку

        String[][] input = new String[N][V];
        System.out.println("Введите содержимое пробирок по строкам (снизу вверх). Используйте '-' для пустых ячеек.");

        for (int i = 0; i < N; i++) {
            System.out.print("Пробирка " + i + ": ");
            String[] line = scanner.nextLine().trim().split("\\s+");
            for (int j = 0; j < V; j++) {
                input[i][j] = line[j].equals("-") ? null : line[j];
            }
        }

        Map<String, Integer> colorCounts = new HashMap<>();
        int totalNonEmptyTubes = 0;
        for (String[] row : input) {
            boolean isEmpty = true;
            for (String s : row) {
                if (s != null) {
                    colorCounts.put(s, colorCounts.getOrDefault(s, 0) + 1);
                    isEmpty = false;
                }
            }
            if (!isEmpty) totalNonEmptyTubes++;
        }

        boolean valid = true;
        for (Map.Entry<String, Integer> entry : colorCounts.entrySet()) {
            String color = entry.getKey();
            int count = entry.getValue();
            if (count % V != 0) {
                System.out.println("Ошибка: Цвет '" + color + "' встречается " + count + " раз, что не кратно объему пробирки " + V + ".");
                valid = false;
            }
        }

        int neededTubes = colorCounts.size(); // по одному на каждый цвет
        int recommendedTubes = neededTubes + 2; // минимум для свободы действий

        if (N < recommendedTubes) {
            System.out.println("️Внимание: всего " + N + " пробирок, а цветов " + neededTubes +
                    ". Рекомендуется минимум " + recommendedTubes + " пробирок (цветов + 2).");
            if (N <= neededTubes) {
                System.out.println("Ошибка: Недостаточно пробирок для сортировки жидкостей.");
                valid = false;
            }
        }

        if (!valid) {
            System.out.println("\n Исправьте входные данные и попробуйте снова.");
            return;
        }

        // Преобразование во внутреннее представление
        List<Stack<String>> tubes = new ArrayList<>();
        for (String[] row : input) {
            Stack<String> tube = new Stack<>();
            for (int i = 0; i < V; i++) {
                if (row[i] != null) {
                    tube.push(row[i]);
                }
            }
            tubes.add(tube);
        }

        // Запуск решения
        List<String> moves = new ArrayList<>();
        if (!solve(tubes, moves, new HashSet<>())) {
            System.out.println("Решение не найдено.");
        }
    }

    private static boolean solve(List<Stack<String>> state, List<String> moves, Set<String> visited) {
        if (isSolved(state)) {
            System.out.println("\nРешение найдено за " + moves.size() + " ходов:");
            moves.forEach(System.out::println);
            return true;
        }

        String hash = hashState(state);
        if (visited.contains(hash)) return false;
        visited.add(hash);

        int n = state.size();
        for (int from = 0; from < n; from++) {
            if (state.get(from).isEmpty()) continue;
            String color = state.get(from).peek();
            int count = countTopColor(state.get(from), color);

            for (int to = 0; to < n; to++) {
                if (from == to) continue;
                if (state.get(to).size() == V) continue;
                if (!state.get(to).isEmpty() && !state.get(to).peek().equals(color)) continue;

                int space = V - state.get(to).size();
                int moveCount = Math.min(count, space);

                for (int i = 0; i < moveCount; i++) {
                    state.get(to).push(state.get(from).pop());
                }

                moves.add("(" + from + ", " + to + ")");
                if (solve(state, moves, visited)) return true;

                for (int i = 0; i < moveCount; i++) {
                    state.get(from).push(state.get(to).pop());
                }
                moves.removeLast();
            }
        }
        return false;
    }

    private static int countTopColor(Stack<String> tube, String color) {
        int count = 0;
        for (int i = tube.size() - 1; i >= 0; i--) {
            if (tube.get(i).equals(color)) {
                count++;
            } else break;
        }
        return count;
    }

    private static boolean isSolved(List<Stack<String>> state) {
        for (Stack<String> tube : state) {
            if (tube.isEmpty()) continue;
            if (tube.size() != V) return false;
            String color = tube.peek();
            for (String liquid : tube) {
                if (!liquid.equals(color)) return false;
            }
        }
        return true;
    }

    private static String hashState(List<Stack<String>> state) {
        StringBuilder sb = new StringBuilder();
        for (Stack<String> tube : state) {
            sb.append("[");
            for (String liquid : tube) sb.append(liquid == null ? "-" : liquid);
            sb.append("]");
        }
        return sb.toString();
    }
}
