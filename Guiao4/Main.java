

import java.lang.InterruptedException;

public class Main {

  public static void main ( String[] args ) throws InterruptedException {
    if( args.length != 2 ) {
      System.out.println("Error: Wrong number of arguments. Must provide number of Producers an number of Consumers");
      return;
    } 

    final int numProducers = Integer.parseInt(args[0]);
    final int numConsumers = Integer.parseInt(args[1]);

    Producer[] producers = new Producer[numProducers];
    Consumer[] consumers = new Consumer[numConsumers];

    BoundedBuffer buffer = new BoundedBuffer( 100 );

    for(int i = 0; i < numProducers; i++ ){
      producers[ i ] = new Producer( i, buffer );
      producers[ i ].start();

    }

    for( int j = 0; j < numConsumers; j++ ){
      consumers[ j ] = new Consumer( j, buffer );
      consumers[ j ].start();
    }

    for( int i = 0; i < numProducers; i++ ){
      producers[ i ].join();
    }
    for( int j = 0; j < numConsumers; j++ ){
      consumers[ j ].join();
    }
  } 
}
