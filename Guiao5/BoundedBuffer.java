import java.lang.*;
import java.util.*;
import java.lang.InterruptedException;

class BoundedBuffer {
  private final int arraySize;
  private int[] array;
  private int begin=0;
  private int end;
  private int elems=0;

  public BoundedBuffer ( int N ) {
    this.array = new int[N];
    this.arraySize = N;
    this.begin = this.end = 0;
  }

  int synchronized get() throws InterruptedException {
      int tmp = 0;
      while (elems==0)
        wait()
      elems-=1;
      tmp=a[begin];
      begin=(begin+1)%array.length;
    return tmp;
    notify();
  }

  void synchronized put( int x ) throws InterruptedException {
    while (elems==a.length)
      wait()
    elems+=1;
    a[begin]=x;
    begin=(begin+1)%array.length;
    notify();
  }
}
