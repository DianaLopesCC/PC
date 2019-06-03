import java.lang.*;

class Conta{
    public int saldo;

    public int getSaldo(){
        return saldo;
    }

    public void setSaldo(int m){
        this.saldo=m;
    }
}

public class Banco{
    Conta contas[];

    public Banco(int N){
        contas= new Conta[N];
        for(int i=0; i<contas.length; i++) contas[i]=new Conta();
    }

    /*public synchronized void credit(int i, int m){
        contas[i].saldo+=m;
    }

    public synchronized void debit(int i, int m){
        contas[i].saldo-=m;
    }*/

    public void credit(int i, int m){
        Conta c= contas[i];
        synchronized (c) {c.saldo+=m;}
    }

    public void debit(int i, int m){
        Conta c= contas[i];
        synchronized (c){ c.saldo-=m;}
    }

    /*public synchronized int balance(int i){
        return contas[i].saldo;
    }*/
    //exercicio 4
    public int balance(int i){
        Conta c = contas[i]
        synchronized (c) {return c.saldo;}
    }

  /*
    public synchronized int soma (int i, int j){
        return contas[i].saldo+ contas[j].saldo;
      }

   public synchronized void transferir(int i, int j, int m){
        //contas[i].saldo-=m;
        //contas[j].saldo+=m;
       debit(i,m);
       credit(j,m);
    }
    */
    public int soma (int i, int j){
      Conta ci = contas[i];
      Conta cj = contas[j];
      synchronized (ci){
        synchronized (cj){
        return ci.saldo+ cj.saldo;
      }}
    }

    public void transferir(int i, int j, int m){
        Conta ci = contas[i];
        Conta cj = contas[j];
        Conta l1,l2;
        //ordena qual a tread que adquire o lock por ordem da mesma
        if (i<j){l1=ci;l2=cj}
        else{l1=cj;l2=ci;}
        synchronized (l1){
          synchronized (l2){
              ci.saldo -=m;
              cj.saldo +=m;
          }
        }
      }

    public int saldoTotal(){
        int sum=0;
        for(Conta c:contas) sum+=c.saldo;
        return sum;
    }

    static class Client extends Thread{
        Banco b;
        int id;
        Client (Banco b, int id){this.b=b; this.id=id;}
        public void run(){
            int n=b.contas.length;
            for (int i=0; i<1000000; i++) b.credit((i*id)%n,1);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Banco b = new Banco(10);
        Client c1= new Client(b,1);
        Client c2= new Client(b,2);
        Client c3= new Client(b,3);
        c1.start();
        c2.start();
        c1.join();
        c2.join();

        System.out.println(b.saldoTotal());
        //teste de Thread
        /*
        Banco b = new Banco (10000);
        int N= 100000000;

        Thread t1 = new Thread(() ->{
         Random r= new Random();
          for (int i=0; i<N; i++){

        }
        }
        */
   }
}
