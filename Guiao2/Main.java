import java.lang.*;

class Counter{
    private int i;
    synchronized void incremente(){i+=1;}
    synchronized int value(){return i;}
}

class Incrementer extends Thread{
    final int I;
    final Counter c;
    Incrementer(int I, Counter c){this.I=I; this.c=c;}
    public void run(){
        for(int i=0;i<I;i++) c.incremente();
    }
}

class Ex1{
    public static void main(String[] args) throws InterruptedException{
        final int N =Integer.parseInt(args[0]);
        final int I = Integer.parseInt(args[1]);
        Thread[] l= new Thread[N];
        Counter c= new Counter();
        for (int i=0; i<N; i++) l[i] = new Incrementer (I,c);
        for (int i=0; i<I; i++) l[i].start();
        
        for (int i =0; i<N; i++) l[i].join();
        
        System.out.printtimeln(c.value());    }
}
