import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Кирилл on 06.09.2018.
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

    private static ArrayList<Integer> readIntegerArrayFromFile(String filename) throws IOException {
        Matcher matcher = Pattern.compile(REGEX).matcher(readContentOfTextFile(filename));
        ArrayList<Integer> result = new ArrayList<>();
        while (matcher.find()) {
            result.add(Integer.valueOf(matcher.group(1)));
        }
        return result;
    }

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

    private static String formatIntArrayAsAStringWithSpacesBetween(ArrayList<Integer> list) {
        StringBuilder result = new StringBuilder();
        for (Integer currentInt : list) {
            result.append(currentInt);
            result.append(' ');
        }
        return result.toString();
    }

    private static void writeStrToFile(String filename, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(content);
            writer.flush();
        }
    }

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

    private static class IntRateContainer {
        final int value;
        int rate;

        IntRateContainer(int value) {
            this.rate = 1;
            this.value = value;
        }

        @Override
        public String toString() {
            return "" + value;
        }
    }
}
