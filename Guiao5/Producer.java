import java.lang.*;
import java.util.concurrent.locks.*;
import java.util.Random;
import java.lang.InterruptedException;

public class Producer extends Thread {
  private final int id;
  private final BoundedBuffer buffer;

  public Producer( int id, BoundedBuffer buffer ) {
    this.buffer = buffer;
    this.id = id;
  }

  public void run() {
    for( int i = 0; i < 100; i++ ) {
      try{
        this.buffer.put(this.id);
        // Will sleep for 0.5 seconds
        super.sleep(100);
      } catch( Exception E ){
        System.out.println( E.getMessage() );
        return;
      }
    }
  }
}
