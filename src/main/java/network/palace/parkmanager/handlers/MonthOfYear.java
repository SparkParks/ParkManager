package network.palace.parkmanager.handlers;

import lombok.Getter;

/**
 * Represents the months of the year as an enumeration, providing the number of days
 * in each month as well as a numerical representation.
 *
 * <p>This enum includes helper methods to retrieve a month based on its numerical value.
 * The months are zero-indexed, with {@code JANUARY} being assigned a value of 0, and
 * {@code DECEMBER} a value of 11.</p>
 *
 * <p>The number of days for February is set to 28 and does not account for leap years.</p>
 *
 * <p>Each enum constant has the following associated properties:
 * <ul>
 *     <li><strong>num</strong>: The zero-based numeric representation of the month.</li>
 *     <li><strong>days</strong>: The number of days in the month.</li>
 * </ul>
 * </p>
 */
public enum MonthOfYear {
    JANUARY(0, 31), FEBRUARY(1, 28), MARCH(2, 31), APRIL(3, 30), MAY(4, 31), JUNE(5, 30), JULY(6, 31), AUGUST(7, 31),
    SEPTEMBER(8, 30), OCTOBER(9, 31), NOVEMBER(11, 30), DECEMBER(12, 31);

    /**
     * Represents the zero-based numeric representation of a month.
     *
     * <p>This field is a property of each month in the {@code MonthOfYear} enumeration
     * and is used to distinguish months numerically, starting with {@code JANUARY} as 0
     * and ending with {@code DECEMBER} as 11.</p>
     *
     * <p>Key details:</p>
     * <ul>
     *     <li>Used for retrieving, identifying, or processing months based on their numeric values.</li>
     *     <li>Provides a consistent zero-based indexing scheme for the months of the year.</li>
     * </ul>
     */
    @Getter private int num;

    /**
     * Represents the number of days in a given context, typically for a month.
     *
     * <p>This property is used to specify the length of a time period in days.
     * For example, it may indicate the number of days in a specific month of the year
     * or the duration of a park or resort-related context.</p>
     *
     * <p>The value may vary depending on the associated entity, such as the number of days
     * in a particular month or cycle. Note that in some cases, leap year adjustments are
     * not accounted for (e.g., February is set to 28 days).</p>
     */
    @Getter private int days;

    /**
     * Constructs a new instance of {@code MonthOfYear} with the specified numerical value
     * and the number of days in the month.
     *
     * <p>This constructor is used to associate each month with its zero-indexed numerical
     * position and the count of days it has.</p>
     *
     * @param num  An integer representing the zero-based numeric value of the month. For example,
     *             0 for January, 11 for December.
     * @param days An integer representing the number of days in the month. For example, 31 for
     *             January, 28 for February (ignoring leap years).
     */
    MonthOfYear(int num, int days) {
        this.num = num;
        this.days = days;
    }

    /**
     * Retrieves the corresponding {@code MonthOfYear} for a given numerical value.
     * <p>
     * This method maps numbers to months in a zero-based index format:
     * <ul>
     *     <li>0 - {@code JANUARY}</li>
     *     <li>1 - {@code FEBRUARY}</li>
     *     <li>2 - {@code MARCH}</li>
     *     <li>... and so on up to 11 - {@code DECEMBER}</li>
     * </ul>
     * </p>
     *
     * <p>If the input number does not match any valid month, the method defaults to returning {@code JANUARY}.</p>
     *
     * @param i the zero-based numeric representation of the month (0 for JANUARY, 11 for DECEMBER)
     * @return the {@code MonthOfYear} corresponding to the input number, or {@code JANUARY} if the number is out of bounds
     */
    public static MonthOfYear getFromNumber(int i) {
        switch (i) {
            case 0:
                return JANUARY;
            case 1:
                return FEBRUARY;
            case 2:
                return MARCH;
            case 3:
                return APRIL;
            case 4:
                return MAY;
            case 5:
                return JUNE;
            case 6:
                return JULY;
            case 7:
                return AUGUST;
            case 8:
                return SEPTEMBER;
            case 9:
                return OCTOBER;
            case 10:
                return NOVEMBER;
            case 11:
                return DECEMBER;
        }
        return JANUARY;
    }
}