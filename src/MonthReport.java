import java.util.ArrayList;
import java.util.Arrays;

/**
 * ����� ��������������� ������ � �������� �������� �� ������ ������������, ������� � ����� ������ ����� ���������� ������
 * � ������ �� ������� ������ ������� ����������� ������ �� ������� ������� �������
 */
public class MonthReport {

    private String[] subCategory;
    private double[] outcome;
    private int monthNumber;

    public MonthReport(String[] subCategory, double[] outcome, int monthNumber) {
        this.subCategory = subCategory;
        this.outcome = outcome;
        this.monthNumber = monthNumber;
    }
    public String[] getSubCategory() {
        return subCategory;
    }

    public double[] getOutcome() {
        return outcome;
    }
}
