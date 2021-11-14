package emailparser;

import java.util.Arrays;
import java.util.Comparator;

public class Removeme {
    public static void main(String[] args) {
//        var day = 1/0.;
//        Object day = 1.0/0.0;
//        System.out.println(day.getClass());

//        int day = 1/0;
        double day = (double) 1 / 0;

    }
}

class Solution1 {
    public String longestCommonPrefix(String[] strs) {
        Arrays.sort(strs, Comparator.comparingInt(String::length));
        int count = strs[0].length();

        for (String str : strs) {
            if (count < 1)
                break;
            for (int i = 0; i < count; i++) {
                if (str.charAt(i) != strs[0].charAt(i)) {
                    count = i;
                    break;
                }
            }
        }

        {
            int maxCommonIdx = 0;
            idx:
            for (; ; maxCommonIdx++) {
                char char0 = strs[0].charAt(maxCommonIdx);
                for (int s = 1; s < strs.length; s++) {
                    if (strs[s].charAt(maxCommonIdx) != char0) {
                        break idx;
                    }
                }
            }
//            return strs[0].substring(0, maxCommonIdx);
        }

        return strs[0].substring(0, count);
    }
}

class ListNode {
    int val;
    ListNode next;

    ListNode() {
    }

    ListNode(int val) {
        this.val = val;
    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }
}
// 1 2 3     321
//   4 5      54


class Solution {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        int a, b, sum = 0;//
        ListNode temp = new ListNode();
        ListNode head = temp;
        while (l1 != null || l2 != null || sum > 0) {
            a = l1 != null ? l1.val : 0;
            b = l2 != null ? l2.val : 0;
            temp.next = new ListNode((a + b + sum) % 10, null);
            sum = (a + b + sum) / 10;
            l1 = l1 != null ? l1.next : null;
            l2 = l2 != null ? l2.next : null;
            temp = temp.next;
        }

        return head.next;
    }
}

class X {
    public static void main(String[] args) {
        int i = -1;
        System.out.println(i >> 1);
        System.out.println(i >>> 1);

        System.out.println(~-1);//0
        System.out.println(~Integer.MIN_VALUE);
        System.out.println(-1 ^ Integer.MAX_VALUE);

    }
}



