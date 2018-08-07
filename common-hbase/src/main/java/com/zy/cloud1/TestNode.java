package com.zy.cloud1;

/**
 * Created by Administrator on 2018/4/25.
 */
public class TestNode {
  public static ListNode reverseList(ListNode header) {
    ListNode first = null;
    ListNode curNode = header;
    ListNode next = null;
    while( curNode != null) {
      first = curNode;
      curNode = curNode.next;
      first.next = next;
      next = first;
    }
    return first;
  }

  public static void main(String[] args) {
    ListNode list = buildListNode(new int[] { 1, 3, 4, 5, 7, 8, 9 });
//    do {
//      System.out.println(list.val);
//      list = list.next;
//    } while (list != null);

    ListNode node = reverseList(list);
    while (node != null) {
      System.out.println(node.val);
      node = node.next;
    }
  }

  private static ListNode buildListNode(int[] input) {
    ListNode first = null;
    ListNode last = null;
    ListNode newNode;
    if (input.length > 0) {
      for (int i = 0; i < input.length; i++) {
        newNode = new ListNode(input[i]);
        newNode.next = null;
        if (first == null) {
          first = newNode;
          last = newNode;
        } else {
          last.next = newNode;
          last = newNode;
        }
      }
    }
    return first;
  }
}

class ListNode {
  int val;
  ListNode next;

  public ListNode(int x) {
    val = x;
  }
}
