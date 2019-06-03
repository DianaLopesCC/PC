import java.lang.*;
import java.util.concurrent.locks.*;
import java.util.Random;

public class Consumer extends Thread {
  private final int id;
  private final BoundedBuffer buffer;

  public Consumer( int id, BoundedBuffer buffer ) {
    this.buffer = buffer;
    this.id = id;
  }

  public void run() {
    for( int i = 0; i < 100; i++ ) {
      try{
        int tmp = this.buffer.get();
        System.out.println("Consumer " + this.id + " got " + tmp);
        // Will sleep for 0.05 seconds
        super.sleep(50);
      } catch( Exception E ){
        System.out.println( E.getMessage() );
        return;
      }
    }
  }
}
