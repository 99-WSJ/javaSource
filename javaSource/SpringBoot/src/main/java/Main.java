/**
 * @program: javaSource
 * @description:
 * @author: wsj
 * @create: 2025-03-05 12:28
 **/
public class Main {
    public static void main(String[] args) {
        int[] arr = {3,5,4,9,6,5,8,3,2,4,5};
        func(arr,0, arr.length-1);
        for(int i : arr) {
            System.out.print(i + " ");
        }
        System.out.println("test.......");
    }

    private static void func(int[] arr, int l, int r) {
        if(l < r) {
            int i = l, j = r, x = arr[i];
            while(i < j) {
                while (x < arr[j] && i < j) {
                    j--;
                }
                if( i < j) {
                    arr[i] = arr[j];
                    i++;
                }
                while (x > arr[i] && i < j) {
                    i++;
                }
                if( i < j) {
                    arr[j] = arr[i];
                    j--;
                }
            }
            arr[i] = x;
            func(arr, l, i -1);
            func(arr, i + 1, r);
        }
    }
}
