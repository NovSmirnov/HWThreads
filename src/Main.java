import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

public class Main {
    public static void main(String[] args) {
        try {
            MonthReport[] year = new MonthReport[12];
            OutcomeData[] threadArray = new OutcomeData[12];
            // Создаём 12 потоков для считывания файлов из архива с данными
            for (int i = 0; i < 12; i++) {
                OutcomeData monthOutcome = new OutcomeData(i + 1, 2014);
                monthOutcome.start();
                threadArray[i] = monthOutcome;
            }
            for (OutcomeData thread : threadArray) {
                thread.join();
            }
            // Заполняем массив с данными по каждому месяцу
            for (int i = 0; i < 12; i++) {
                year[i] = threadArray[i].getMonthReport();
            }
            yearReportToFile("src/yearReport.csv",year);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создаёт 2 массива с подкатегориями и соответсвующими расходами по каждой категории и выводит в виде отчёта в файл
     * @param filePath Путь к файлу
     * @param year Массив из 12 объектов класса MonthReport, каждый из которых содержит месяные данные
     */
    private static void yearReportToFile(String filePath, MonthReport[] year) {
        // Создаём множество подкатегорий из всех месячных файлов с расходами, а затем массив из этого множества
        SortedSet<String> subCategories = new TreeSet<>();
        for (int i = 0; i < 12; i++) {
            subCategories.addAll(Arrays.asList(year[i].getSubCategory()));
        }
        String[] subCatArray = subCategories.toArray(new String[0]);
        double[] sumArray = new double[subCatArray.length];

        // Создаём массив содержащий суммы расходов по подкатегориям расходов за весь год
        for (int i = 0; i < subCatArray.length; i++) {
            for (int j = 0; j < 12; j++) {
                for (int k = 0; k < year[j].getSubCategory().length; k++) {
                    if (subCatArray[i].equals(year[j].getSubCategory()[k])) {
                        sumArray[i] += year[j].getOutcome()[k];
                    }
                }
            }
        }
        writeToFile(filePath, subCatArray, sumArray);
    }

    /**
     * Вспомогательный метод для вывода в файл
     * @param filePath Путь к файлу
     * @param subCatArray Массив с названиями подкатегрий расходов
     * @param sumArray Массив с суммами расходов за год. Индекс суммы расходов соответсвует индексу в предыдущем массиве
     */
    private static void writeToFile(String filePath, String[] subCatArray, double[] sumArray) {
        StringBuilder finalInfo;
        String oneString;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        DecimalFormat df = new DecimalFormat("########.##", symbols);
        try (FileWriter writer = new FileWriter(filePath, Charset.forName("cp1251"))) {
            finalInfo = new StringBuilder("Subcategory;Outcome" + "\n");
            for (int i = 0; i < subCatArray.length; i++) {
                oneString = subCatArray[i] + ";" + df.format(sumArray[i]) + "\n";
                finalInfo.append(oneString);
            }
            writer.write(finalInfo.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
