package Entitys;

import java.util.List;

/**
 * Created by g.shestakov on 24.06.2015.
 */
public class DebtData {

    public String partnerId;

    public String customerId;

    public String transactionNumber;
    public String transactionId;

    public String transactionDate;

    public double transactionSum;

    public String paymentDate;

    public double debt;

    public double overdueDebt;

    public int overdueDays;

    public double claimedSum;

    public static double getDebtSum(List<DebtData> debts)
    {
        double result = 0;
        for (DebtData d : debts)
        {
            result += d.debt;
        }
        return result;
    }

    public static double getOverdueDebtSum(List<DebtData> debts)
    {
        double result = 0;
        for (DebtData d : debts)
        {
            result += d.overdueDebt;
        }
        return result;
    }

    public static double getClaimedSum(List<DebtData> debts)
    {
        double result = 0;
        for (DebtData d : debts)
        {
            result += d.claimedSum;
        }
        return result;
    }

    public static double getOverdueAndClaimedSum(List<DebtData> debts)
    {
        double result = 0;
        for (DebtData d : debts)
        {
           if (d.overdueDebt > 0)
                result += d.overdueDebt - d.claimedSum;
        }
        return result;
    }

    public String CustomerName;

}
