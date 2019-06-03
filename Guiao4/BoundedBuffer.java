
import java.lang.*;
import java.util.concurrent.Semaphore;
import java.util.*;
import java.lang.InterruptedException;

class BoundedBuffer {
  private final Semaphore items, slots;
  private final int arraySize;
  private int[] array;
  private int begin;
  private int end;

  public BoundedBuffer ( int N ) {
    this.array = new int[N];
    this.arraySize = N;
    this.begin = this.end = 0;
    this.items = new Semaphore(0);
    this.slots = new Semaphore(this.arraySize);
  }

  int get() throws InterruptedException {
    items.acquire();
      int tmp = 0;
      tmp = this.array[this.begin];
      this.begin = (this.begin+1) % this.arraySize;
    slots.release();
    return tmp;
  }

  void put( int x ) throws InterruptedException {
    slots.acquire();
      this.array[this.end] = x;
      this.end = (this.end+1) % this.arraySize;
    items.release();
  }
}
