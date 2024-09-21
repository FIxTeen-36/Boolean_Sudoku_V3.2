public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("\033[1;36m" + "Boolean Sudoku Solver Version 3.2" + "\u001B[0m");

        int[][] xElements = {
                {2, 2},
                {2, 4, 2},
                {1, 3, 2, 1},
                {4, 3},
                {4, 3},
                {3, 4},
                {2, 5},
                {6},
                {4},
                {2, 2}
        };

        int[][] yElements = {
                {2},
                {2, 4},
                {1, 6, 1},
                {5, 3},
                {4, 3},
                {1, 4},
                {9},
                {1, 6, 1},
                {2, 4},
                {2}
        };

        int[][] field = new int[xElements.length][yElements.length];
        boolean fieldChanged;
        do {
            fieldChanged = false;
            for (int i = 0; i < field.length; i++) {
                boolean fieldCheck = fillingRow(field[i], xElements[i]);
                if (fieldCheck) {
                    fieldChanged = true;
                }
            }
            for (int i = 0; i < field[0].length; i++) {
                int[] column = getFieldColumn(field, i);
                boolean fieldCheck = fillingRow(column, yElements[i]);
                placeColumnInField(field, column, i);
                if (fieldCheck) {
                    fieldChanged = true;
                }
            }
        } while (fieldChanged);

        printPattern(xElements, yElements, field);
    }

    // Math

    public static int[] elementsPlacing (int[]element, int[] crossIndex, int[] starIndex, int rowLength) throws Exception {
        int[] elemCoord = new int[2 * element.length + 1];
        elemCoord[2*element.length] = rowLength;
        int ci = 0;
        int si = 0;
        int ei = 0;
        while (ei < element.length) {
            // Placing the end of the element
            elemCoord[2*ei+1] = elemCoord[2*ei] + element[ei] - 1;
            // Cross Actualization
            for (int i = 0; i < crossIndex.length; i++) {
                if (elemCoord[2*ei] <= crossIndex[i]) {
                    ci = i;
                    break;
                }
            }
            // Check a cross in filled area
            if (crossIndex[ci] <= elemCoord[2*ei+1]) {
                while (crossIndex[ci+1] <= elemCoord[2*ei+1]) {
                    ci++;
                }
                elemCoord[2*ei] = crossIndex[ci] + 1;
                if (ei > 0) {
                    ei--;
                    continue;
                } else if (starIndex[si] < elemCoord[0]) {
                    throw new Exception("Critical error - Painted spot before 1st element");
                }
                continue;
            }
            // The next element placing
            if (elemCoord[2*ei+2] < elemCoord[2*ei+1]) {
                elemCoord[2*ei+2] = elemCoord[2*ei+1] + 2;
            }
            // Starr actualization
            for (int i = 0; i < starIndex.length; i++) {
                if (elemCoord[2*ei+1] < starIndex[i]) {
                    si = i;
                    break;
                }
            }
            // Check filled area in space
            if (elemCoord[2*ei+1] < starIndex[si] && starIndex[si] < elemCoord[2*ei+2]) {
                while (starIndex[si+1] < elemCoord[2*ei+2]) {
                    si++;
                }
                elemCoord[2*ei] = starIndex[si] - element[ei] + 1;
                if (ei > 0) {
                    ei--;
                    continue;
                } else if (starIndex[si] < elemCoord[0]) {
                    throw new Exception("Critical error - Painted spot before 1st element");
                }
                continue;
            }
            ei++;
        }
        return elemCoord;
    }

    public static int[] reverseIndex (int[] straight) {
        int[] reverse = new int[straight.length];
        reverse[straight.length-1] = straight[straight.length-1];
        for (int i = 0; i < reverse.length-1; i++) {
            reverse[i] = straight[straight.length-1] - straight[straight.length - 2 - i] - 1;
        }
        return reverse;
    }

    public static boolean fillingRow (int[] row, int[] element) throws Exception {
        boolean rowChanged = false;
        // 1 - cross(x), 2 - painted (*)
        int[] crossIndex = {row.length};
        int[] starIndex = {row.length};
        for (int i = row.length - 1; i >= 0; i--) {
            int[] copy;
            switch (row[i]) {
                case 1:
                    copy = new int[crossIndex.length + 1];
                    System.arraycopy(crossIndex, 0, copy, 1, crossIndex.length);
                    copy[0] = i;
                    crossIndex = copy;
                    break;
                case 2:
                    copy = new int[starIndex.length + 1];
                    System.arraycopy(starIndex, 0, copy, 1, starIndex.length);
                    copy[0] = i;
                    starIndex = copy;
                    break;
            }
        }

        int[] elemCoordStr = elementsPlacing(element, crossIndex, starIndex, row.length);

        int[] elementRev = new int[element.length];
        for (int i = 0; i < element.length; i++) {
            elementRev[i] = element[element.length-1-i];
        }

        int[] elemCoordRev = reverseIndex(elementsPlacing(elementRev, reverseIndex(crossIndex), reverseIndex(starIndex), row.length));

        // Stars placing in the row
        int beg = 0;
        for (int i = 0; i < element.length; i++) {
            if(elemCoordRev[2*i] <= elemCoordStr[2*i+1]) {
                for (int j = elemCoordRev[2*i]; j <= elemCoordStr[2*i+1]; j++) {
                    if (row[j] != 2) {
                        row[j] = 2;
                        rowChanged = true;
                    }
                }
            }
            if (beg < elemCoordStr[2*i]) {
                for (int j = beg; j < elemCoordStr[2*i]; j++) {
                    if (row[j] != 1) {
                        row[j] = 1;
                        rowChanged = true;
                    }
                }
            }
            beg = elemCoordRev[2*i+1] + 1;
        }
        if (elemCoordRev[2*element.length - 1] < elemCoordRev[elemCoordRev.length - 1] - 1) {
            for (int j = beg; j < elemCoordRev[elemCoordRev.length - 1]; j++) {
                row[j] = 1;
            }
        }
        return rowChanged;
    }

    public static int[] getFieldColumn(int[][] field, int index) {
        int[] column = new int[field[0].length];
        for (int i = 0; i < column.length; i++) {
            column[i] = field[i][index];
        }
        return column;
    }

    public static void placeColumnInField (int[][] field, int[] column, int index) {
        for (int i = 0; i < column.length; i++) {
            field[i][index] = column[i];
        }
    }

    // Printing

    public static void printPattern (int[][] xElements, int[][] yElements, int[][] field) {
        for (int i = field[0].length/2 + field[0].length%2 - 1; i >= 0; i--) {
            for (int j = 0; j < field.length/2 + field.length%2; j++) {
                System.out.print("  ");
            }
            for (int j = 0; j < field[0].length; j++) {
                if (i < yElements[j].length) {
                    System.out.print("\033[0;34m" + yElements[j][i] + " ");
                } else {
                    System.out.print("  ");
                }
            }
            System.out.println();
        }
        for (int i = 0; i < field.length; i++) {
            int j = field.length/2 + field.length%2 - 1;
            while (xElements[i].length <= j) {
                System.out.print("  ");
                j--;
            }
            while (0 <= j) {
                System.out.print("\033[0;34m" + xElements[i][j] + " ");
                j--;
            }
            for (int spot: field[i]) {
                if (spot == 2) {
                    System.out.print("\033[1;31m" + "@ ");
                } else {
                    System.out.print("  ");
                }
            }
            System.out.println();
        }
    }

}