-module(ii)
-export([start/0, inscrever/1]).

start() ->
  register(?MODULE, spawn(fun() -> loop([],[]) end)). //spawn cria um processo novo

inscrever(Nome) ->
  ?MODULE ! {inscrever, Nome, self()},
  receive {nomes, Nome ,  ?MODULE} -> Nomes end.

loop(Nomes, Pids) ->
  receive
    {inscrever, Nome, From} when Nomes =:= [] -> //quando a lista de jogadores é vazia
      spawn (fun () ->
        receiver after 60000 -> ?MODULE ! timer end
          end), //cria um processo e depois de receber a mensagem 600000 manda a mensagem "timer" para o modulo
      loop([Nome],[From]);
    {inscrever, Nome, From} ->
      try_start(Timer, [Nome | Nomes] , [From | Pids])

    timer ->
      try_start(true, Nomes, Pids) //o Timer é true (Booleano) e verifica a lista de Nomes e Pids
    end.

try_start(Timer , Nomes, Pids) when
  length(Nomes) =:= 30;
  Timer =:= true, length(Nomes)>=20, length(Nomes) rem 2==0
[Pid ! {nomes, Nomes, ?MODULE} || Pid <- Pids],
loop(false, [],[]);

try_start(Timer, Nomes, Pids) ->
  loop(Timer, Nomes, pids).
