-module(sondagens).
-export([start/0, vota/2, espera/4]).

start() -> spawn(fun() -> loop(#{}) end).

vota(Sondagem, Candidato) -> Sondagem ! {Vota, Candidato}.

espera(Sondagem,C1,C2,C3)->
  Sondagem !{espera,C1,C2,C3,self()},
  recive {ok, Sondagem} -> ok end.

loop(Mapa) ->
  receive
    {vota, Candidato} ->
      {Votos, Esperas} = lookup(Candidato,Mapa),
      NovasEtapas = liberta (Votos+1, Mapa, Esperas),
      loop(Mapa#{Candidato => {Votos+1,NovasEsperas}})
    {espera, C1, C2, C3, From} ->
      {V3, Esperas} = lookup (C3, Mapa),
      NE= liberta(V3, Mapa, [{C1,C2,From}]),
      loop(Mapa# {C3 => {V3, NE ++ Esperas})
    end.

lookup(Candidato, Mapa) -> //testa se existe ou nao existe votos
  case maps:find(Candidato,Mapa) of
    error -> {0,[]};
    {ok , V} -> V
  end.

liberta (_,_,[]) -> []; //liberta a lista que continua a espera ate que a condicao dos votos se verifique
liberta(V3, Mapa,[h={C1,C2,From} | T]) ->
  {V1,_}=lookup(C1,Mapa),
  {V2,_}=lookup(C2,Mapa),
  case V1< V3 andalso V2 <V3 of
    true ->
      From !{ok,self()},
      liberta(V3,Mapa, T);
    false ->
     [H| liberta(V3,Mapa, T)]
    end.
