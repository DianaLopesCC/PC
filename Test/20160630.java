//GRUPO II
import java.util.concurrent.locks.*;

//void vota(String candidato)
//void espera(String c1, String c2, String c3)
class Candidato{
  public String nome;
  public int votos;
}

public class Sistema{
  public candidatos HasMap<String,Candidato>;

  void vota synchronized(String candidato){
    Candidato c;
    if (containsKey(candidato)){
      c=get(candidato);
      c.votos++;
    }
  }

  void espera(String c1, String c2, String c3){
    Candidato a;
    Candidato b;
    Candidato c;
    condition OKordem;
    a.lock();
    b.lock();
    c.lock();
    if (containsKey(c1) && containsKey(c2) && containsKey(c3)){
      a=get(c1);
      b=get(c2);
      c=get(c3);
      if (a.votos > b.votos || a.votos > c.votos || b.votos > c.votos) wait(OKordem);
      signalAll(OKordem);
    }
    a.unlock();
    b.unlock();
    c.unlock();
  }
}
