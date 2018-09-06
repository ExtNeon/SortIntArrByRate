import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Программа читает отформатированный целочисленный массив из текстового файла "input.txt" и сортирует его по частоте появления
 * определённого числа, а затем записывет в файл "output.txt" с тем же форматированием.
 * Пример: 4 2 69 2 5 3 2 4
 * Выход: 2 2 2 4 4 69 5 3
 * Автор: Малякин Кирилл
 */
class Main {

    private static final String REGEX = "(\\d+)(?: )*";
    private static final String OUTPUT_FILENAME = "output.txt";
    private static final String INPUT_FILENAME = "input.txt";

    public static void main(String[] args) {
        try {
            ArrayList<Integer> readedArray = readIntegerArrayFromFile(INPUT_FILENAME);
            System.out.println("Исходный массив: " + readedArray);
            sortNumbersByRate(readedArray);
            System.out.println("Отсортированный массив: " + readedArray);
            writeStrToFile(OUTPUT_FILENAME, formatIntArrayAsAStringWithSpacesBetween(readedArray));
        } catch (FileNotFoundException e) {
            System.err.println("Файл \"" + INPUT_FILENAME + "\" не найден");
        } catch (IOException e) {
            System.err.println("Ошибка ввода - вывода во время чтения файла " + INPUT_FILENAME);
        }

    }

    /**
     * Считавает содержимое файла filename, парсит в нём числа с пробелами в качестве разделителей и возвращает их в виде массива
     *
     * @param filename Путь до файла, который будет прочитан
     * @return ArrayList of Integer с числами, извлечёнными из файла
     * @throws IOException В случае ошибок ввода - вывода
     */
    private static ArrayList<Integer> readIntegerArrayFromFile(String filename) throws IOException {
        Matcher matcher = Pattern.compile(REGEX).matcher(readContentOfTextFile(filename));
        ArrayList<Integer> result = new ArrayList<>();
        while (matcher.find()) {
            result.add(Integer.valueOf(matcher.group(1)));
        }
        return result;
    }

    /**
     * Открывает файл filename и возвращает его содержимое в виде строки
     * @param filename Путь до файла, который необходимо прочитать
     * @return Содержимое файла в виде строки
     * @throws IOException в случае ошибок ввода - вывода
     */
    private static String readContentOfTextFile(String filename) throws IOException {
        if (!new File(filename).exists()) {
            throw new FileNotFoundException(filename);
        }
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            int readedChar;
            while ((readedChar = reader.read()) != -1) {
                result.append((char) readedChar);
            }
        }
        return result.toString();
    }

    /**
     * Форматирует ArrayList of Integer таким образом, чтобы значения элементов разделялись пробелом.
     * @param list ArrayList of Integer, который будет отформатирован
     * @return Строка с элементами массива, отделёнными друг от друга символом пробела
     */
    private static String formatIntArrayAsAStringWithSpacesBetween(ArrayList<Integer> list) {
        StringBuilder result = new StringBuilder();
        for (Integer currentInt : list) {
            result.append(currentInt);
            result.append(' ');
        }
        return result.toString();
    }

    /**
     * Записывает в файл filename строку. В случае, если такой файл существует - перезаписывает его
     * @param filename Имя файла, в который будет записана строка
     * @param content Строка, которую необходимо записать в файл
     * @throws IOException В случае ошибок ввода-вывода
     */
    private static void writeStrToFile(String filename, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(content);
            writer.flush();
        }
    }

    /**
     * Сортирует ArrayList of Integer по частоте появления в нём чисел в порядке убывания.
     * @param array ArrayList of Integer, который необходимо отсортировать
     */
    private static void sortNumbersByRate(ArrayList<Integer> array) {
        ArrayList<IntRateContainer> numbersContainer = new ArrayList<>();
        for (int num : array) {
            boolean foundedInEntropyArr = false;
            for (IntRateContainer currentNumber : numbersContainer) {
                if (num == currentNumber.value) {
                    foundedInEntropyArr = true;
                    currentNumber.rate++;
                    break;
                }
            }
            if (!foundedInEntropyArr) {
                numbersContainer.add(new IntRateContainer(num));
            }
        }
        sortIntRateContainerListByRate(numbersContainer);
        array.clear();
        for (IntRateContainer currentNum : numbersContainer) {
            while (currentNum.rate-- > 0) {
                array.add(currentNum.value);
            }
        }
    }

    /**
     * Сортирует ArrayList с объектами IntRateContainer по полю rate в порядке убывания.
     * @param list ArrayList с объектами IntRateContainer
     */
    private static void sortIntRateContainerListByRate(ArrayList<IntRateContainer> list) {
        IntRateContainer buff;
        int left = 0;
        int right = list.size() - 1;
        do {
            for (int i = left; i < right; i++) {
                if (list.get(i).rate < list.get(i + 1).rate) {
                    buff = list.get(i);
                    list.set(i, list.get(i + 1));
                    list.set(i + 1, buff);
                }
            }
            right--;
            for (int i = right; i > left; i--) {
                if (list.get(i).rate > list.get(i - 1).rate) {
                    buff = list.get(i);
                    list.set(i, list.get(i - 1));
                    list.set(i - 1, buff);
                }
            }
            left++;
        } while (left < right);
    }

    /**
     * Примитивный класс - контейнер, содержащий два целочисленных поля: value, в котором сохраняется само число
     * и rate, в котором хранится частота появления числа value в массиве. Содержит конструктор, позволяющий задать
     * поле value сразу при создании. Поле rate инициализируется единицей.
     */
    private static class IntRateContainer {
        final int value;
        int rate;

        IntRateContainer(int value) {
            this.rate = 1;
            this.value = value;
        }
    }
}
