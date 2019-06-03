import java.util.concurrent.locks.*;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

interface BankInterface {
    int createAccount( float initialBalance );
    float closeAccount( int id ) throws InvalidAccount;
    void credit( int id, int val )  throws InvalidAccount;
    void debit( int id, int val ) throws InvalidAccount;
    void transfer( int from, int to, float amount ) throws InvalidAccount, NotEnoughFunds;
    float totalBalance( int accounts[] ) throws InvalidAccount;
}
class InvalidAccount extends Exception {
    InvalidAccount(int i){ super(); } // improve super handler
}

class NotEnoughFunds extends Exception {
    NotEnoughFunds( int i, float requested, float found ){ super(); } // super(i, requested, found);
}

/**
 * bank class, implements BankInterface and its methods
 */
class Bank implements BankInterface {
    private static class Account {
        float balance;
        Lock l = new ReentrantLock();
        boolean closed;

        /**
         * empty constructor for Account
         */
        Account(){
            this.balance = 0;
            this.closed = false;
        }
        Account(  float initialBalance ){
            balance = initialBalance;
            this.closed = false;
        }
    }

    Map <Integer, Account> accounts;
    Lock l = new ReentrantLock();
    private int nextAccountNumber;

    /**
     * increments account counter
     */
    void increment(){
        l.lock();
        nextAccountNumber += 1;
        l.unlock();
    }

    /**
     * constructor
     * @param int N number of accounts to add to create for the  bank
     */
    public Bank( int N ){
        accounts = new HashMap <>();
        for (int i = 0; i < accounts.size(); i++){
            accounts.put(nextAccountNumber++, new Account() );
        }
    }

    public int createAccount( float initialBalance ){
        Account c = new Account(initialBalance); // it is still not available to use so it can be declared here
        // c.balance = initialBalance; // construtor já faz isso
        l.lock();
        try{
            nextAccountNumber += 1;
            accounts.put(nextAccountNumber, c);
            return nextAccountNumber;
        }finally{
            l.unlock();
        }
    }

    /**
     * closes an account inside bank and returns current balance
     * @param  id             account id
     * @return                account balance
     * @throws InvalidAccount if account number does not match an existing and opened account throws InvalidAccount
     */
    public float closeAccount( int i ) throws InvalidAccount{
        Account c;
        float balance;

        l.lock();
        try{
            c = accounts.get(i);
            if( c == null ) throw InvalidAccount(i);
            c.l.lock();
        }finally{
            l.unlock();
        }
        balance = c.balance;
        c.debit(balance);
        c.closed = true;
        accounts.remove(i);
        c.l.unlock();
    }

    /**
     * credits amount m into a account i
     * @param int i account number
     * @param int m amount to put into the account
     */
    public void credit( int i , int m ) throws InvalidAccount {
        //System.out.println("Crédito de " + m + " na conta " + i );
        Account c;

        l.lock(); // locks bank to avoid getting a ref to an account that may have been closed meanwhile
        try{
            c = accounts.get(i);
            if ( c == null ) throw new InvalidAccount(i);
            c.l.lock();
        }finally{
            l.unlock();
        }
        try{
            c.balance += m;
        }finally{
            c.l.unlock();
        }
    }

    /**
     * makes a debit inta an account
     * @param int i account number
     * @param int m ammount to put into the account
     */
    public void debit( int i , int m ){
        Account c;
        l.lock(); // locks bank to avoid getting a ref to an account that may have been closed meanwhile

        try{
            c = accounts.get(i);
            if( c == null ) throw new InvalidAccount(i);
            c.l.lock();
        }finally{
            l.unlock();
        }
        try{
            if( c.balance < m ) throw new NotEnoughFunds(i, m, c.balance());
            c.balance -= m;
        }finally{
            c.l.unlock();
        }
    }

    /**
     * money transfer between banc accounts
     * @param int i origin account
     * @param int j destination accoutn
     * @param int m ammount to transfer between accounts
     */
    // tem de ser sincronizada para evitar que se possa por exemplo consultar a soma dos saldos de ambas as accounts a meio e verifique que o valor total não bate certo
    public void transfer( int i, int j, float m){
        Account ci, cj;
        l.lock();
        try{
            ci = accounts.get(i);
            if( ci == null ) throw new InvalidAccount(i);
            cj = accounts.get(j);
            if( cj == null ) throw new InvalidAccount(j);
            if( i < j ) { ci.l.lock(); cj.l.lock(); }
            else        { cj.l.lock(); ci.l.lock(); }
        }finally{
            l.unlock();
        }
        if ( ci.balance < m ){
            ci.l.unlock();
            cj.l.unlock();
            throw NotEnoughFunds(i, m, c.balance);
        }
        ci.balance -= m;
        ci.l.unlock(); // unlock are not together because since we have a lock on account j them even if anywone sees tha balance from accounti they can't see the balance from acocunt j
        cj.balance += m;
        cj.l.unlock();
    }

    /**
     * checks account's current balance
     * @param  int i             account number
     * @return     the balance of the account
     */
    public synchronized int balance( int i ){
        // System.out.println("O saldo da conta " + i + " é de ");
        return accounts.get(i).balance;
    }

    /**
     * checks the total amount of money present int the banc's accounts
     * @return [description]
     */
    // public synchronized int total(){
    //     int sum = 0;
    //     for (Account c : accounts ){
    //         sum += c.balance;
    //     }
    //     return sum;
    // }


    public float totalBalance( int ids[] ) throws InvalidAccount;{
        ids = Array.sort(ids.clone());
        float sum;
        Account[] ac = new Account[ids.length];
        l.lock();
        try{
            for( int i = 0; i < ids.length; i++){
                ac[i] = contas.get(ids[i]);
                if( ac[i] == null ) throw InvalidAccount(ids[i]);
            }
            for( Account c : ac ) c.l.lock();
        }finally{
            l.unlock();
        }
        for( Account c : ac ){
            sum += c.balance;
            c.l.unlock();
        }



        // Account ci= accounts.get(i);
        // Account cj = accounts.get(j);
        // Account l1,l2;
        // if (i < j) { l1 = ci; l2 = cj; }
        // else { l1 = cj; l2 = ci;}
        // synchronized ( l1 ){
        //     synchronized( l2 ){
        //         return ci.balance + cj.balance;
        //     }
        // }
    }
}

class Client extends Thread {
    Bank b;
    int id;
    Client (Bank b, int i ){ this.b = b; this.id = i; }

    public void run(){
        int n = b.accounts.size();
        for (int i = 0; i < 1000000; i++){
            b.credit ((i*id) % n,1);
        }
    }
}
class Exercicio4 {
    public static void main (String[] args) throws InterruptedException{
        Bank b = new Bank(10000);
        int N = 100000000;

        b.createAccount(100);
        b.createAccount(100);

        Thread t1 = new Thread(() -> {
            Random r = new Random();
            for (int i = 0; i < N; i++ ){
                if (r.nextBoolean())
                    b.transfer(1,2,100);
                else
                    b.transfer(2,1,100);
            }
        });

        Thread t2 = new Thread(() -> {
            Random r = new Random();
            for ( int i = 0 ; i < N ; i++){
                if (i% 100 == 0)
                    System.out.println(b.soma(1,2));
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }
}
