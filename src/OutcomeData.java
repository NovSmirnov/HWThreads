import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Объект класса в отдельном потоке считывает файл из архива с данными о затратах за конкретный месяц,
 * и создаёт и хранит эти данные в объекте класса MonthReport
 */
public class OutcomeData extends Thread {

    private String fileName; // Имя считываемого файла
    private int monthNumber; // Порядковый номер месяца
    private MonthReport monthReport; // Данные за 1 месяц
    private String[] subCategory; // Подкатегория покупки
    private double[] outcome; // Сумма покупки


    public OutcomeData(int monthNumber, int year) {
        this.fileName = monthNumber + "_" + year + ".txt";
        this.monthNumber = monthNumber;
    }

    public MonthReport getMonthReport() {
        return monthReport;
    }

    @Override
    public void run() {
        setZipData();
        subCategoryData();
    }

    private void setZipData() {
        try (ZipInputStream zipReader = new ZipInputStream (new FileInputStream("src/yearsOutcome.zip"))) {
            ZipEntry entry;
            while ((entry = zipReader.getNextEntry()) != null) {
                if (entry.getName().equals(fileName)) {
                    ZipFile zipFile = new ZipFile("src/yearsOutcome.zip");
                    InputStream is = zipFile.getInputStream(entry);
                    BufferedReader reader = new BufferedReader( new InputStreamReader(is, StandardCharsets.UTF_8));
                    readData(reader);
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readData(BufferedReader reader) {
        try(reader) {
            Scanner scanner;
            String lineData;
            List<String> data = new ArrayList<>();
            while ((lineData = reader.readLine()) != null) {
                data.add(lineData);
            }
            int dataLen = data.size();
            reader.close();
            subCategory = new String[dataLen - 1];
            outcome = new double[dataLen - 1];

            int index = 0;
            for (int i = 1; i < dataLen; i++) {
                scanner = new Scanner(data.get(i));
                scanner.useDelimiter(";");
                while (scanner.hasNext()) {
                    String cell = scanner.next();
                    if (index == 3)
                        subCategory[i - 1] = cell;
                    else if (index == 4) {
                        String[] temp;// = new String[2];
                        temp = cell.split(" ");
                        outcome[i - 1] = Double.parseDouble(temp[0].replace(",", ".").replace("\u00A0", "")); //  
                    }
                    index++;
                }
                index = 0;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void subCategoryData(){
        SortedSet<String> subCategoryList = new TreeSet<String>(Arrays.asList(subCategory));
        String[] subCategoryArray = subCategoryList.toArray(new String[0]);
        double[] sumList = new double[subCategoryArray.length];
        for (int i = 0; i < subCategoryArray.length; i++) {
            double sum = 0;
            for (int j = 0; j < subCategory.length; j++) {
                if (subCategoryArray[i].equals(subCategory[j])) {
                    sum += outcome[j];
                }
            }
            sumList[i] = sum;
        }
        this.monthReport = new MonthReport(subCategoryArray, sumList, this.monthNumber);
    }
}
