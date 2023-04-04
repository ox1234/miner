package org.example.util;

import java.util.ArrayList;
import java.util.List;

public class PrintUtil {
    public static void printTable(List<List<String>> tableData, String title, List<String> description) {
        StringBuilder tableBuilder = new StringBuilder();
        List<Integer> columnWidths = getMaxColumnWidths(tableData);

        String separatorLine = createSeparatorLine(columnWidths);
        String descriptionLines = createDescriptionLines(description, columnWidths);

        tableBuilder.append(separatorLine)
                .append(descriptionLines)
                .append(separatorLine)
                .append(createTitleLine(title, columnWidths))
                .append(separatorLine);

        for (List<String> row : tableData) {
            tableBuilder.append("|");
            for (int i = 0; i < row.size(); i++) {
                tableBuilder.append(String.format(" %-"+ columnWidths.get(i) +"s |", row.get(i)));
            }
            tableBuilder.append("\n");
        }

        tableBuilder.append(separatorLine)
                .append(descriptionLines)
                .append(separatorLine);

        System.out.println(tableBuilder.toString());
    }

    private static List<Integer> getMaxColumnWidths(List<List<String>> tableData) {
        List<Integer> columnWidths = new ArrayList<>();
        for (List<String> row : tableData) {
            for (int i = 0; i < row.size(); i++) {
                if (columnWidths.size() <= i) {
                    columnWidths.add(row.get(i).length());
                } else {
                    columnWidths.set(i, Math.max(columnWidths.get(i), row.get(i).length()));
                }
            }
        }
        return columnWidths;
    }

    private static String createSeparatorLine(List<Integer> columnWidths) {
        StringBuilder separatorLine = new StringBuilder("+");
        for (Integer width : columnWidths) {
            separatorLine.append(repeatString("-", width + 2)).append("+");
        }
        separatorLine.append("\n");
        return separatorLine.toString();
    }

    private static String createTitleLine(String title, List<Integer> columnWidths) {
        int totalWidth = columnWidths.stream().mapToInt(Integer::intValue).sum() + columnWidths.size() * 2 + 1;
        return String.format("|%-"+ (totalWidth - 1) + "s|\n", title);
    }

    private static String createDescriptionLines(List<String> description, List<Integer> columnWidths) {
        StringBuilder descriptionLines = new StringBuilder();
        int totalWidth = columnWidths.stream().mapToInt(Integer::intValue).sum() + columnWidths.size() * 2 + 1;
        for (String line : description) {
            descriptionLines.append(String.format("|%-"+ (totalWidth - 1) + "s|\n", line));
        }
        return descriptionLines.toString();
    }

    // Helper method to repeat a string a specific number of times
    private static String repeatString(String str, int times) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < times; i++) {
            result.append(str);
        }
        return result.toString();
    }
}
